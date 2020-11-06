package sensecloud.web.service.remote.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class RemoteAuthFallback {

    public Map<String,Object> getHDFSDirPermissionByUser(String cluster, String username, String filePath) {
        log.info("getHDFSDirPermissionByUser invoked fallback");
        return Collections.emptyMap();
    }

}
