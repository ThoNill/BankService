package data;

import javax.persistence.AttributeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbBICAdapter extends XmlAdapter<String, BIC> implements AttributeConverter<BIC, String> {

    @Override
    public String marshal(BIC obj) throws Exception {
        return obj.toString();
    }

    @Override
    public BIC unmarshal(String obj) throws Exception {
        return new BIC(obj);
    }

    @Override
    public String convertToDatabaseColumn(BIC attribute) {
        return attribute.toString();
    }

    @Override
    public BIC convertToEntityAttribute(String dbData) {
        return new BIC(dbData);
    }

}