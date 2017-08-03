package flow;

import java.io.File;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class DateinameInDenHeader implements Transformer {
    public static final String DATEINAME = "dateiname";

    @Override
    public Message<?> transform(Message<?> message) {
        File file = (File)message.getPayload();
        MessageBuilder<?> builder = MessageBuilder.fromMessage(message);
        builder.setHeader(DATEINAME, file.getPath().toString());
        return builder.build();
    }

}
