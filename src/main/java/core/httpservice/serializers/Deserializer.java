package core.httpservice.serializers;


import core.httpservice.exceptions.MediaTypeConversionException;

public interface Deserializer extends InternetMediaTypeHandler {
    Object deserialize(byte[] data) throws MediaTypeConversionException;
}
