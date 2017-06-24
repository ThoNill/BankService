package flow;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.aggregator.AbstractAggregatingMessageGroupProcessor;
import org.springframework.integration.store.MessageGroup;

public class MessagesAmEndeZusammenfassen extends AbstractAggregatingMessageGroupProcessor {
    Logger LOG = LogManager.getLogger(MessagesAmEndeZusammenfassen.class);
    
    public MessagesAmEndeZusammenfassen() {
    }

    @Override
    protected Object aggregatePayloads(MessageGroup group,
            Map<String, Object> defaultHeaders) {
        LOG.debug("Angekommen " + group);
        
        
        return Boolean.TRUE; // group.getSequenceSize() == 2;
    }

}
