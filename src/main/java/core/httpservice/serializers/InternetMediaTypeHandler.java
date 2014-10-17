package core.httpservice.serializers;

import com.google.common.net.MediaType;

public interface InternetMediaTypeHandler {
    boolean handles(MediaType type);
}
