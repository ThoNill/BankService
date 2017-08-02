package ausgang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;

import repositories.AusgangsDateiRepository;
import repositories.UeberweisungRepository;
import fehlerManagement.RollbackActionFabric;

public class DateiRollbackFabric implements RollbackActionFabric {
    private static Logger LOG = LogManager
            .getLogger(DateiRollbackFabric.class);

    private AusgangsDateiRepository ausgangsDateiRepository;
    private UeberweisungRepository ueberweisungRepository;

    public DateiRollbackFabric(UeberweisungRepository ueberweisungRepository,
            AusgangsDateiRepository ausgangsDateiRepository) {
        super();
        this.ueberweisungRepository = ueberweisungRepository;
        this.ausgangsDateiRepository = ausgangsDateiRepository;
    }

    public Runnable createRunnable(Message<?> message) {
        if (message.getHeaders() != null
                && message.getHeaders()
                        .get(OffeneÜberweisungenAbfragen.DATEINR) != null) {
            return new DateiRollback((Long) message.getHeaders().get(
                    OffeneÜberweisungenAbfragen.DATEINR),
                    ueberweisungRepository, ausgangsDateiRepository);
        }
        return new Runnable() {
            public void run() {
                LOG.error("Die Massage " + message + " konnte nicht verarbeitet werden");
            }
        };
    }
}
