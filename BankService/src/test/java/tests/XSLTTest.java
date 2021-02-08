package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = flow.XSLTAnwenden.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// @DataJpaTest
public class XSLTTest extends CopyHelper{
    @Autowired
    @Qualifier("inboundReadDirectory")
    public File inboundReadDirectory;

    @Autowired
    @Qualifier("inboundProcessedDirectory")
    public File inboundProcessedDirectory;

    @Autowired
    @Qualifier("inboundFailedDirectory")
    public File inboundFailedDirectory;

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    @After
    public void tearDown() throws Exception {
    }

    private void createFile(String name, String inhalt) {
        try {
            PrintWriter writer = new PrintWriter(
                    inboundReadDirectory.getAbsolutePath() + "/" + name);
            writer.append(inhalt);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    @Before
    public void createFiles() throws IOException, InterruptedException {
     	copy("./src/test/resources/kontoauszug.xml");
       
    }


    @Autowired
    @Qualifier("fileInputChannel")
    public DirectChannel filePollingChannel;

    
    @Test
    public void normalerAblauf() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        filePollingChannel.addInterceptor(new ChannelInterceptorAdapter() {
            @Override
            public void postSend(Message message, MessageChannel channel,
                    boolean sent) {
                latch.countDown();
                super.postSend(message, channel, sent);
            }
        });

        assertThat(latch.await(10, TimeUnit.SECONDS), is(true));
        TestUtils.assertThatDirectoryIsEmpty(inboundReadDirectory);
        TestUtils.assertThatDirectoryHasFiles(inboundProcessedDirectory, 1);
    }

}
