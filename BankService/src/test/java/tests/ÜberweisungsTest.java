package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ausgang.AusgangsdateiInEinenObjektbaumUmformen;
import ausgang.Ueberweisung;
import repositories.AusgangsDateiRepository;
import repositories.UeberweisungRepository;
import fehlerManagement.Bombe;

@RunWith(SpringJUnit4ClassRunner.class)
// @EnableScheduling
@EnableAsync
@SpringBootTest(classes = ausgang.ÜberweisungsFlow.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// @DataJpaTest
// @EnableTransactionManagement
public class ÜberweisungsTest extends AsyncTest {

    @Autowired
    public UeberweisungRepository ueberweisungRepository;
    
    @Autowired 
    public AusgangsDateiRepository ausgangsDateiRepository;
        
    @Autowired
    @Qualifier("erstelleUeberweisungenChannel")
    public DirectChannel erstelleUeberweisungenChannel;

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    @Before
    public void before() throws FileNotFoundException, IOException {
        TestUtils.deleteRecursive(inboundOutDirectory);
        Files.createDirectories(inboundOutDirectory.toPath());
        ueberweisungRepository.deleteAll();
        ausgangsDateiRepository.deleteAll();
    }

    @Autowired
    SubscribableChannel errorChannel;

    @Autowired
    TaskExecutor executor;

    
    @Test
    public void normalerAblauf() throws Exception {
        before();
        
        Bombe.setAuslöser("");

        errorChannel.subscribe(

        new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message)
                    throws MessagingException {
                System.out.println("Angekommen " + message);
            }

        });

        TestÜberweisungenErzeugen g = new TestÜberweisungenErzeugen(
                ueberweisungRepository);
        g.generate(10);

        CountDownLatch latch = einenCountdownMachen(erstelleUeberweisungenChannel);
        executor.execute(sendeDieStartMessage());
       
        latch.await(5, TimeUnit.SECONDS);
        // assertThat(latch.await(10, TimeUnit.SECONDS), is(true));

        assertEquals(0,ueberweisungRepository.ohneDateinummer().size());
        TestUtils.assertThatDirectoryHasFiles(inboundOutDirectory, 1);
    }

    
@Ignore
    @Test
    public void fehlerFall() throws Exception {
        before();

        Bombe.setAuslöser(AusgangsdateiInEinenObjektbaumUmformen.class);

        TestÜberweisungenErzeugen g = new TestÜberweisungenErzeugen(
                ueberweisungRepository);
        g.generate(10);


        CountDownLatch latch = einenCountdownMachen(erstelleUeberweisungenChannel);
        executor.execute(sendeDieStartMessage());
        latch.await(5, TimeUnit.SECONDS);
  
        TestUtils.assertThatDirectoryHasFiles(inboundOutDirectory, 0);
        assertEquals(10,ueberweisungRepository.ohneDateinummer().size());
       
    }

    
    // @Scheduled(initialDelay = 1000,fixedDelay=5000)
    public void shaduleStartMessageSenden() {
        sendeDieStartMessage().run();
    }

    public Runnable sendeDieStartMessage() {
        Runnable r = new Runnable() {
            public void run() {
                System.out.println("Message versendet");
                Message message = MessageBuilder.withPayload("Start")
                        .setHeader("info", "siia.test@yahoo.ca").build();
                erstelleUeberweisungenChannel.send(message);
            }
        };
        return r;
    }

}
