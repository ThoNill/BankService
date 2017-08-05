package flow;

import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;

public class NeutraleTransformation extends AbstractTransformer {
    
    public NeutraleTransformation() {
        super();
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        return message;
    }

}
