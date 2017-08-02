package tests;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class AsyncTest {

    public AsyncTest() {
        super();
    }

    protected CountDownLatch einenCountdownMachen(AbstractMessageChannel channel) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        channel.addInterceptor(new ChannelInterceptorAdapter() {
            @Override
            public void postSend(Message message, MessageChannel channel,
                    boolean sent) {
                latch.countDown();
                super.postSend(message, channel, sent);
            }
        });
        return latch;
    }

}