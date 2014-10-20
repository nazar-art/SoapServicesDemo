/**
 *
 */
package core.example.poena;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import core.httpservice.HttpService;
import core.httpservice.entities.HttpMethod;
import core.httpservice.entities.ServiceResponseMessage;
import core.httpservice.exceptions.HttpServiceException;
import core.utils.IOUtils;
import core.utils.StringUtils;
import core.utils.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PoenaRequestService {

    private static final String TEMPLATE_PATH = "resources/xml_messages/bp12/message01.xml";
    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PoenaRequestService.class);

    @Inject
    @Named("poena_service")
    private HttpService poenaService;

    public String sendRequest(/*TaxPayer taxPayer,*/ String kbk) throws PoenaServiceException {
        LOG.info(String.format("Generating poena message request for string: %s", kbk));

        Map<String, String> replaceValues = new HashMap<>();
        replaceValues.put("guid", "guid");
        replaceValues.put("iinbin", "iinbin");
        replaceValues.put("rnn", "rnn");
        replaceValues.put("taxOrgCode", "taxOrgCode");
        replaceValues.put("kbk", "kbk");
        replaceValues.put("dateMessage", "dateMessage");
        replaceValues.put("applyDate", "applyDate");
        /*Logger.operation("Generating poena message request for tax payer: {0}", taxPayer);
        OperDay operday = OperDayUtils.getCurrentOperDay();

        Map<String, String> replaceValues = new HashMap<String, String>();
        String guid = UUID.randomUUID().toString();
        replaceValues.put("guid", guid);
        replaceValues.put("iinBin", taxPayer.getIinBin());
        replaceValues.put("rnn", taxPayer.getRnn());
        replaceValues.put("taxOrgCode", taxPayer.getTaxOrgCode());
        replaceValues.put("kbk", kbk);
        replaceValues.put("dateMessage", operday.getDate().minusDays(1).toString("yyyyMMdd HH:mm:ss"));
        replaceValues.put("applyDate", operday.getDate().minusDays(1).toString("yyyyMMdd"));*/

        ServiceResponseMessage result;
        try {
            String template = IOUtils.readFileIntoString(TEMPLATE_PATH);
            Document rq = XmlUtil.parseDocument(StringUtils.replaceValues(template, replaceValues));
            result = poenaService.execute(HttpMethod.POST, null, rq);
        } catch (IOException e) {
            throw new PoenaServiceException("Unable to read template file: " + TEMPLATE_PATH, e);
        } catch (SAXException e) {
            throw new PoenaServiceException("Unable to parse result document, please check template file: " + TEMPLATE_PATH, e);
        } catch (HttpServiceException e) {
            throw new PoenaServiceException(e);
        }

        if (result.isSuccess()) {
            return (String) result.getResult();
        }

        throw new PoenaServiceException("HTTP service error code '" + result.getStatusCode() + "', message: " + result.getStatusMessage());
    }
}
