package data;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import betrag.Geld;

public class JaxbMonetaryAmountAdapter extends XmlAdapter<String, MonetaryAmount> {

    @Override
    public String marshal(MonetaryAmount obj) throws Exception {
        double value = obj.getNumber().doubleValue();
        DecimalFormat formatter = new DecimalFormat("########0.00");
        return formatter.format(value).replace(',', '.');
     }
    
    @Override
    public MonetaryAmount unmarshal(String obj) throws Exception {
        BigDecimal betrag = new BigDecimal(obj.replaceAll(",", ""));
        return Geld.createAmount(betrag);
    }

}