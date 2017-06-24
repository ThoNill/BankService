package flow;
import javax.xml.transform.dom.DOMResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.transformer.AbstractPayloadTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Result2DocumentTransformer extends AbstractPayloadTransformer<DOMResult,Document>  {
    Logger LOG = LogManager.getLogger(Result2DocumentTransformer.class);
    
   
    @Override
    protected Document transformPayload(DOMResult result) throws Exception {
       Document doc = (Document)result.getNode();
//       ausgabe(doc);
       return doc;
    }

    
    public void ausgabe(Node node) {
        NodeList list = node.getChildNodes();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            LOG.debug("" +node.getLocalName());
            
        }
        if (node.getNodeType() == Node.TEXT_NODE) {
            LOG.debug(""  + node.getTextContent());
            
        }
        for(int i=0;i<list.getLength();i++) {
            Node c = list.item(i);
            ausgabe(c);
        }
    }
}

