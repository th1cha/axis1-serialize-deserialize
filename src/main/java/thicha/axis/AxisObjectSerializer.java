package thicha.axis;

import org.apache.axis.MessageContext;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.apache.axis.server.AxisServer;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class AxisObjectSerializer {

    public static String serializeAxisObject(final Object obj) throws AxisObjectException {
        if (obj == null) {
            return null;
        }
        StringWriter outStr = new StringWriter();
        TypeDesc typeDesc = getAxisTypeDesc(obj);
        QName qname = typeDesc.getXmlType();
        String lname = qname.getLocalPart();
        if (lname.startsWith(">") && lname.length() > 1)
            lname = lname.substring(1);

        qname = new QName(qname.getNamespaceURI(), lname);
        AxisServer server = new AxisServer();
        BeanSerializer ser = new BeanSerializer(obj.getClass(), qname, typeDesc);
        SerializationContext ctx = new SerializationContext(outStr,
                new MessageContext(server));
        ctx.setSendDecl(false);
        ctx.setDoMultiRefs(false);
        ctx.setPretty(true);
        try {
            ser.serialize(qname, new AttributesImpl(), obj, ctx);
        } catch (final Exception e) {
            throw new AxisObjectException("Unable to serialize object " + obj.getClass().getName(), e);
        }

        return outStr.toString();
    }

    private static TypeDesc getAxisTypeDesc(final Object obj) throws AxisObjectException {
        final Class objClass = obj.getClass();
        try {
            final Method methodGetTypeDesc = objClass.getMethod("getTypeDesc", new Class[]{});
            final TypeDesc typeDesc = (TypeDesc) methodGetTypeDesc.invoke(obj, new Object[]{});
            return (typeDesc);
        } catch (final Exception e) {
            throw new AxisObjectException("Unable to get Axis TypeDesc for "
                    + objClass.getName(), e);
        }
    }
}
