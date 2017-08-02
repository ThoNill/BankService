package tests;

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
@SpringBootTest(classes = flow.BankEingangMitLambdas.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BankEingangMitLambdasTest extends FlowTestBasis{


    @Autowired
    @Qualifier("fileInputChannel")
    public DirectChannel filePollingChannel;

    @Autowired
    @Qualifier("vorDemSplittenChannel")
    public DirectChannel vorDemSplittenChannel;

    @Autowired
    @Qualifier("nachDemSplittenChannel")
    public DirectChannel nachDemSplittenChannel;

    @Autowired
    @Qualifier("sollInDieDatenbank")
    public DirectChannel sollInDieDatenbankChannel;

    @Autowired
    @Qualifier("sollInDieDatei")
    public DirectChannel sollInDieDateiChannel;

    @Autowired
    @Qualifier("wiederZusammen")
    public DirectChannel wiederZusammenChannel;

    @Test
    public void normalerAblauf() throws Exception {
        einenCountdownMachen();

        vorDemSplittenChannel.addInterceptor(new ChannelInterceptorAdapter() {
            @Override
            public void postSend(Message message, MessageChannel channel,
                    boolean sent) {
                System.out.println("vor dem Splitten: " + message);
                super.postSend(message, channel, sent);
            }
        });

        nachDemSplittenChannel.addInterceptor(new ChannelInterceptorAdapter() {
            @Override
            public void postSend(Message message, MessageChannel channel,
                    boolean sent) {
                System.out.println("nach dem Splitten: " + message);
                super.postSend(message, channel, sent);
            }
        });

        sollInDieDatenbankChannel
                .addInterceptor(new ChannelInterceptorAdapter() {
                    @Override
                    public void postSend(Message message,
                            MessageChannel channel, boolean sent) {
                        System.out.println("soll in die Datenbank: " + message);
                        super.postSend(message, channel, sent);
                    }
                });

        sollInDieDateiChannel.addInterceptor(new ChannelInterceptorAdapter() {
            @Override
            public void postSend(Message message, MessageChannel channel,
                    boolean sent) {
                System.out.println("in die Datei: " + message);
                super.postSend(message, channel, sent);
            }
        });

        wiederZusammenChannel.addInterceptor(new ChannelInterceptorAdapter() {
            @Override
            public void postSend(Message message, MessageChannel channel,
                    boolean sent) {
                System.out.println("wieder zusammen: " + message);
                super.postSend(message, channel, sent);
            }
        });
        überprüfeVerzeichnisse();
        überprüfeDieDatenbank();

    }

}
