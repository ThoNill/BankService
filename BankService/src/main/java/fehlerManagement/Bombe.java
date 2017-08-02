package fehlerManagement;

import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class Bombe implements Transformer {
    private static String auslöser;

    public String zünder;

    private Transformer transformer;
    private RollbackActionFabric rollbackFabric;

    public Bombe(Transformer transformer, String zünder) {
        super();
        this.transformer = transformer;
        this.zünder = zünder;
    }

    public Bombe(Transformer transformer) {
        this(transformer, transformer.getClass().getName());
    }

    public static void setAuslöser(Class cl) {
        setAuslöser(cl.getName());
    }

    public static void setAuslöser(String a) {
        auslöser = a;
    }


    public void peng() {
        if (zünder.equals(auslöser)) {
            throw new RuntimeException("peng wegen " + auslöser);
        }
    }

    public Message<?> transform(Message<?> message) {
        peng();
        return transformer.transform(message);
    }
}
