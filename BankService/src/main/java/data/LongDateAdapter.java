package data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import betrag.Geld;

public class LongDateAdapter extends XmlAdapter<String, Date> {
    private static String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSS"; // 2010-11-11T09:30:47.000Z
    
    @Override
    public Date unmarshal(String v) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT); 
        return format.parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT); 
        return format.format(v);
    }


}