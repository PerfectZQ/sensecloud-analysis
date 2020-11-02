package sensecloud.auth2.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import sensecloud.auth2.config.SSOConfiguration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
public class SSOInterceptor implements HandlerInterceptor {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SSOConfiguration configuration;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("SSOConfiguration: {}", this.configuration);


        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            log.info("Cookie: {} = {}", cookie.getName(), cookie.getValue());
        }

        Enumeration<String> headers = request.getHeaderNames();
        while(headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            log.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }
        return true;
    }

    private String getSSOToken() {
        String tokenUrl = this.configuration.getTokenUrl();
        return "";
    }
}
