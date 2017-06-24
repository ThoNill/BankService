package data;

import java.math.BigDecimal;

import javax.money.MonetaryAmount;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import betrag.Geld;

public class JaxbMonetaryAmountAdapter extends XmlAdapter<String, MonetaryAmount> {

    @Override
    public String marshal(MonetaryAmount obj) throws Exception {
        return obj.toString();
    }

    @Override
    public MonetaryAmount unmarshal(String obj) throws Exception {
        BigDecimal betrag = new BigDecimal(obj.replaceAll(",", ""));
        return Geld.createAmount(betrag);
    }

}