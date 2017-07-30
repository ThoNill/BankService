package ausgang;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;

public class ErgebnisInEineDatei extends AbstractTransformer {
    File inboundOutDirectory;
    private AtomicInteger counter = new AtomicInteger();

    public ErgebnisInEineDatei(File inboundOutDirectory) {
        super();
        this.inboundOutDirectory = inboundOutDirectory;
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        String text = (String)message.getPayload();
        File directory = inboundOutDirectory;
        long zeit = new Date().getTime();
        int nummer = counter.addAndGet(1);
        String fileName = directory.toString() + File.separator + "zurBank"
                + zeit + "_" + nummer + ".xml";
        try (FileWriter writer = new FileWriter(new File(fileName))) {
            writer.write(text);
        } catch (Exception ex) {
            throw ex;
        }
        return fileName;
    }
    
}
