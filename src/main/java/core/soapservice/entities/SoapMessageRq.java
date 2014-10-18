package core.soapservice.entities;

import org.w3c.dom.Document;

public class SoapMessageRq {

    private Document payload;

    public SoapMessageRq(Document payload) {
        this.payload = payload;
    }

    public Document getPayload() {
        return payload;
    }
}
