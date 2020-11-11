package sensecloud.connector.submitter.remote;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Data
@Slf4j
public class AirflowRestInvoker {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${remote.rest.airflow.url}")
    private String url;
    @Value("${remote.rest.airflow.username}")
    private String loginUser;
    @Value("${remote.rest.airflow.password}")
    private String loginPwd;
    @Value("${remote.rest.airflow.provider}")
    private String provider;

    private String accessToken;
    private String refreshToken;

    private void login() {
        String api = url + "/api/v1/security/login";
        log.debug("Request api: {}", api);

        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        JSONObject params = new JSONObject();
        params.put("username", this.loginUser);
        params.put("password", this.loginPwd);
        params.put("refresh", true);
        params.put("provider", this.provider);

        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(api, method, requestEntity, JSONObject.class);

        JSONObject responseBody = response.getBody();
        log.debug("Request {} with parameters {} and response {}", api, params, responseBody);

        this.accessToken = responseBody.getString("access_token");
        this.refreshToken = responseBody.getString("refresh_token");

    }

    public CommonResponse<JSONObject> triggerDAG (String dagId, String runId, JSONObject conf) {
        if (StringUtils.isBlank(this.accessToken)) {
            this.login();
        }

        String api = url + "/admin/rest_api/api?api=trigger_dag";
        log.debug("Request api: {}", api);
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.GET;
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setCacheControl(CacheControl.noCache());
        headers.set("Authorization", "Bearer " + this.accessToken);
        headers.set("rest_api_plugin_http_token", "changeme");

        api += "&dag_id=" + dagId;

        if (StringUtils.isNotBlank(runId)) {
            api += "&run_id=" + runId;
        }

        if (conf != null) {
            api += "&conf=" + conf.toJSONString();
        }


        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(new JSONObject(), headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(api, method, requestEntity, JSONObject.class);

        JSONObject responseBody = response.getBody();
        log.debug("Request {} with parameters dagId = {}, runId = {}, conf = {} and response {}", api, dagId, runId, conf, responseBody);

        return new CommonResponse<JSONObject>(response.getStatusCode().value(), responseBody);
    }

    @Data
    @AllArgsConstructor
    public static class CommonResponse<T> {
        private int status;
        private T body;
    }

}
