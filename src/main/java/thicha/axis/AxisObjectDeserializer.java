package thicha.axis;

import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBodyElement;

public class AxisObjectDeserializer {

    private static final String SOAP_START = "";
    private static final String SOAP_START_XSI = "";
    private static final String SOAP_END = "";

    public static Object deserializeObject(String xml, Class clazz) throws AxisObjectException {
        assert xml != null : "xml != null";
        assert clazz != null : "clazz != null";

        Object result = null;
        try {
            Message message = new Message(SOAP_START + xml + SOAP_END);
            result = message.getSOAPEnvelope().getFirstBody().getObjectValue(clazz);
        } catch (Exception e) {
            try {
                Message message = new Message(SOAP_START_XSI + xml + SOAP_END);
                SOAPBodyElement firstSoapBody = message.getSOAPEnvelope().getFirstBody();
                MessageElement childOfSoapBody = (MessageElement) firstSoapBody.getChildElements().next();
                result = childOfSoapBody.getObjectValue(clazz);
            } catch (Exception e1) {
                throw new AxisObjectException(e1);
            }
        }
        return result;
    }
}
