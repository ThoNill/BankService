package batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import data.Einzahlung;

public class EinzahlungItemProcessor implements ItemProcessor<Einzahlung, Einzahlung> {

    private static final Logger log = LoggerFactory.getLogger(EinzahlungItemProcessor.class);

   
    public Einzahlung process(final Einzahlung einzahlung) throws Exception {
  
        return einzahlung;
    }

}
