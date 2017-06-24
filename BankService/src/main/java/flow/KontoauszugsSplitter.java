package flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import data.Datei;
import data.Einzahlung;

public class KontoauszugsSplitter extends AbstractMessageSplitter {
    AtomicInteger correlationId = new AtomicInteger();

    @Override
    protected List<Message> splitMessage(Message<?> message) {
        Datei d = (Datei) message.getPayload();
        List<Einzahlung> einzahlungen = d.getKontoauszug().getEinzahlungen();
        List<Einzahlung> inDieDatenbank = new ArrayList<Einzahlung>();
        List<Einzahlung> inDieDatei = new ArrayList<Einzahlung>();
        for (Einzahlung z : einzahlungen) {
            if (z.sollExportiertWerden()) {
                inDieDatei.add(z);
            } else {
                inDieDatenbank.add(z);
            }
        }

        int id = correlationId.addAndGet(1);

        List<Message> messages = new ArrayList<>();

        MessageBuilder<List<Einzahlung>> builder = MessageBuilder
                .withPayload(inDieDatenbank);
        builder.setHeaderIfAbsent("inDatenbank", Boolean.TRUE);
        builder.setCorrelationId(id);
        builder.setSequenceNumber(1);
        builder.setSequenceSize(2);
        Message<List<Einzahlung>> messageInDieDatenbank = builder.build();

        builder = MessageBuilder.withPayload(inDieDatei);
        builder.setHeaderIfAbsent("inDatenbank", Boolean.FALSE);
        builder.setCorrelationId(id);
        builder.setSequenceNumber(2);
        builder.setSequenceSize(2);
        Message<List<Einzahlung>> messageInDieDatei = builder.build();

        messages.add(messageInDieDatei);
        messages.add(messageInDieDatenbank);
        return messages;

    }

}
