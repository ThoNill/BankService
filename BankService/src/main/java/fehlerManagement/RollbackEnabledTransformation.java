package fehlerManagement;

import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class RollbackEnabledTransformation implements Transformer {
    private Transformer transformer;
    private RollbackActionFabric rollbackFabric;

    public RollbackEnabledTransformation(Transformer transformer,
            RollbackActionFabric rollbackFabric) {
        super();
        this.transformer = transformer;
        this.rollbackFabric = rollbackFabric;
    }

    public Message<?> transform(Message<?> message) {
        try {
            return transformer.transform(message);
        } catch (Exception ex) {
            throw new RollbackException(rollbackFabric.createRunnable(message),
                    ex);
        }
    }

}
