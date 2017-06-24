package tests;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
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
@SpringBootTest(classes = flow.BankEingangsApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GesamtTest {

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

   // @After
    public void tearDown() throws Exception {
        TestUtils.deleteRecursive(inboundReadDirectory);
        TestUtils.deleteRecursive(inboundProcessedDirectory);
        TestUtils.deleteRecursive(inboundFailedDirectory);
        TestUtils.deleteRecursive(inboundOutDirectory);
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
          System.out.println(Paths.get("./src/main/resources/kontoauszug.xml")
                .toAbsolutePath().toString());
        Files.copy(Paths.get("./src/test/resources/kontoauszug.xml"),
                new FileOutputStream(inboundReadDirectory.getAbsolutePath()
                        + "/kontoauszug.xml"));
        // createFile("file1.txt", "content");
        // createFile("file2.txt", "another file");
    }

    @Autowired
    @Qualifier("fileInputChannel")
    public DirectChannel filePollingChannel;

    @Test
    public void pollFindsValidFile() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        filePollingChannel.addInterceptor(new ChannelInterceptorAdapter() {
            @Override
            public void postSend(Message message, MessageChannel channel,
                    boolean sent) {
                System.out.println(" Latch ");
                latch.countDown();
                super.postSend(message, channel, sent);
            }
        });

        assertThat(latch.await(10, TimeUnit.SECONDS), is(true));
        TestUtils.assertThatDirectoryIsEmpty(inboundReadDirectory);
        TestUtils.assertThatDirectoryIsEmpty(inboundFailedDirectory);
        TestUtils.assertThatDirectoryHasFiles(inboundProcessedDirectory, 1);
        TestUtils.assertThatDirectoryHasFiles(inboundOutDirectory, 1);
    }

}
