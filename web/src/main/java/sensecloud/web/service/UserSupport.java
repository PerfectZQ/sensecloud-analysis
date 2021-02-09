package sensecloud.web.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sensecloud.web.service.remote.ClickHouseRemoteService;

@Slf4j
@Component
public class UserSupport {

    @Autowired
    private ClickHouseRemoteService clickHouseRemoteService;

    @Value("${service.connector.clickhouse.des.key}")
    private String ch_pwd_decrypt_key;

    public JSONObject getClickHouseUser(String username) {
        log.info("Fetch username = {} to call getClickHouseUser", username);
        JSONObject callback = null;
        try {
            callback = this.clickHouseRemoteService.getClickHouseUser(username);

            log.debug("Call getClickHouseUser finished. And callback is {}", callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = new JSONObject();

        if(callback != null) {
            int code = callback.getInteger("code");
            String message = callback.getString("msg");
            if (code == 0) {
                JSONObject data = callback.getJSONObject("data");
                result.putAll(data);
                log.debug("Callback data: {}", data);
            } else {
                log.error("Error occurred while calling getClickHouseUser: {}", message);
                result = null;
            }
        }
        log.debug("Call [getClickHouseUser] and return : {}", result);
        return result;
    }

    public Authentication currentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    public String getCurrentUserName() {
        Authentication authentication = this.currentUser();
        return authentication.getName();
    }

}
