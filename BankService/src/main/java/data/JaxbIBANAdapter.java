package data;

import javax.persistence.AttributeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbIBANAdapter extends XmlAdapter<String, IBAN>  implements AttributeConverter<IBAN, String>{

    @Override
    public String marshal(IBAN obj) throws Exception {
        return obj.toString();
    }

    @Override
    public IBAN unmarshal(String obj) throws Exception {
        return new IBAN(obj);
    }
    
    @Override
    public String convertToDatabaseColumn(IBAN attribute) {
        return attribute.toString();
    }

    @Override
    public IBAN convertToEntityAttribute(String dbData) {
        return new IBAN(dbData);
    }


}