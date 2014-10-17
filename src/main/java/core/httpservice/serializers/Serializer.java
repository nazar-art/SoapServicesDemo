package core.httpservice.serializers;

import core.httpservice.exceptions.MediaTypeConversionException;

public interface Serializer extends InternetMediaTypeHandler {
    byte[] serialize(Object rq) throws MediaTypeConversionException;
}
