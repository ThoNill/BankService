package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import repositories.EinzahlungRepository;

public class FlowTestBasis {

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

    @Autowired
    public EinzahlungRepository einzahlungRepository;

    @Autowired
    @Qualifier("fileInputChannel")
    public DirectChannel filePollingChannel;

    
    public FlowTestBasis() {
        super();
    }

    @After
    public void tearDown() throws Exception {
        cleanDirectories();
    }

    @Before
    public void createFiles() throws IOException, InterruptedException {
        cleanDirectories();
        
        Files.createDirectories(inboundReadDirectory.toPath());
        Files.createDirectories(inboundProcessedDirectory.toPath());
        Files.createDirectories(inboundFailedDirectory.toPath());
        Files.createDirectories(inboundOutDirectory.toPath());
        
        Files.copy(Paths.get("./src/test/resources/kontoauszug.xml"),
                new FileOutputStream(inboundReadDirectory.getAbsolutePath()
                        + "/kontoauszug.xml"));
    }

    protected void cleanDirectories() throws FileNotFoundException {
        TestUtils.deleteRecursive(inboundReadDirectory);
        TestUtils.deleteRecursive(inboundProcessedDirectory);
        TestUtils.deleteRecursive(inboundFailedDirectory);
        TestUtils.deleteRecursive(inboundOutDirectory);
        
    }

    protected void einenCountdownMachen() throws InterruptedException {
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
    }

    protected void überprüfeDieDatenbank() {
        assertThat(einzahlungRepository.anzahlDerEinzahlungen(),is(1));
    }

    protected void überprüfeVerzeichnisse() throws Exception {
        TestUtils.assertThatDirectoryIsEmpty(inboundReadDirectory);
        TestUtils.assertThatDirectoryIsEmpty(inboundFailedDirectory);
        TestUtils.assertThatDirectoryHasFiles(inboundProcessedDirectory, 1);
        TestUtils.assertThatDirectoryHasFiles(inboundOutDirectory, 1);
    }

}