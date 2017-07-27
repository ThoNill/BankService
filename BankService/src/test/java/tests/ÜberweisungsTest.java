package tests;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import repositories.UeberweisungRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ausgang.ÜberweisungsFlow.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@DataJpaTest
//@EnableTransactionManagement
public class ÜberweisungsTest {
  
  
    @Autowired
    public UeberweisungRepository ueberweisungRepository;
    
    @Autowired
    @Qualifier("erstelleUeberweisungenChannel")
    public DirectChannel erstelleUeberweisungenChannel;

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    
    @Test
    public void pollFindsValidFile() throws Exception {
        
        TestÜberweisungenErzeugen g = new TestÜberweisungenErzeugen(ueberweisungRepository);
        g.generate(10);
        
        Message message =  MessageBuilder.withPayload("Start")
                .setHeader("info", "siia.test@yahoo.ca").build();
        
        erstelleUeberweisungenChannel.send(message);
        
        TestUtils.assertThatDirectoryHasFiles(inboundOutDirectory, 1);
    }

}
