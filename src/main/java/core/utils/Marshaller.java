/**
 *
 */
package core.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class Marshaller {
    public static Object unmarshal(String filePath, Class<?>... objectClass) throws JAXBException {
        File file = new File(filePath);
        JAXBContext jaxbContext = JAXBContext.newInstance(objectClass);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object object = jaxbUnmarshaller.unmarshal(file);

        return object;
    }
}
