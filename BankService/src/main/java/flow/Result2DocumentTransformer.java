package flow;
import javax.xml.transform.dom.DOMResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.transformer.AbstractPayloadTransformer;
import org.w3c.dom.Document;


public class Result2DocumentTransformer extends AbstractPayloadTransformer<DOMResult,Document>  {
    Logger LOG = LogManager.getLogger(Result2DocumentTransformer.class);
    
   
    @Override
    protected Document transformPayload(DOMResult result) throws Exception {
       return  (Document)result.getNode();
    }

    
}

