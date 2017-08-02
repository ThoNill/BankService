package fehlerManagement;

import org.springframework.messaging.Message;

public interface RollbackActionFabric {
    Runnable createRunnable(Message<?> message);
}
