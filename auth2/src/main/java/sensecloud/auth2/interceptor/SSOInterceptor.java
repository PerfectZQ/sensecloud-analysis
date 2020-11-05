package sensecloud.auth2.interceptor;


import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import sensecloud.auth2.UserContext;
import sensecloud.auth2.UserContextProvider;
import sensecloud.auth2.config.SSOConfiguration;
import sensecloud.auth2.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SSOInterceptor implements HandlerInterceptor {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SSOConfiguration configuration;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("SSOConfiguration: {}", this.configuration);

        boolean isValid = false;
        String xIdToken = request.getHeader(configuration.getId_token_header());

        String msg = "";
        if(StringUtils.isNotBlank(xIdToken)) {
            if (this.checkToken(xIdToken)) {
                UserContext context = UserContextProvider.getContext();
                //Todo: get domain and username
                // .......
                String username = "";
                String domain = "";
                User user = context.lookup(domain, username);
                if (user == null) {
                    user = new User();
                    //Todo: get user info
                    // .......
                    // user = user info

                    context.login(domain, username, user);
                }
                isValid = true;
            } else {
                msg = "Not a valid request: Failed to verify ID token.";
                log.warn(msg);
            }
        } else {
            msg = "Not a valid request: Header for ID token is missing.";
            log.warn(msg);
        }

        if (!isValid) {
            response.setContentType("application/json;charset=utf-8");
            JSONObject responseBody = new JSONObject();
            responseBody.put("code", 401);
            responseBody.put("message", msg);

            response.getWriter().write(responseBody.toJSONString());
        }
        return isValid;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();

        if (uri.equalsIgnoreCase(this.configuration.getLogout_uri())) {
            UserContext context = UserContextProvider.getContext();
            String xIdToken = request.getHeader(configuration.getId_token_header());
            //Todo: get domain and username
            // .......
            String username = "";
            String domain = "";
            User user = context.lookup(domain, username);
            if (user != null) {
                context.logout(domain, username);
            } else {
                log.error("Logout with an invalid user: domain = {}, username = {}", domain, username);
            }

            response.sendRedirect(this.configuration.getLogout_redirect_url());
        }
    }

    private boolean checkToken (String xIdToken) {
        //Todo: check token
        return true;
    }

    public static void main(String[] args) {
        String token = "eyJpYXQiOjE2MDQyOTczNDQsImlzcyI6Imh0dHBzOlwvXC9zc28tc2MtZGV2LnNlbnNldGltZS5jb21cLyIsImF1ZCI6WyJiaWdkYXRhIl0sIm5vbmNlIjoiNDIxZTNhYmE1OWRhMGZjMGE5MDZjZGE3YTc1OTIwZTEiLCJzaWQiOiJiNjkxOGU1Ny0xNmNiLTQxNDctOTM2Yy0xYWVhMTAxMzNlYzgiLCJhdF9oYXNoIjoiLWVHTURaQTVNdGs5bG53eGR3elpXZyIsImF1dGhfdGltZSI6MTYwNDI5NzM0NCwiZXh0Ijp7InJvbGVzIjpudWxsLCJwZXJtaXNzaW9ucyI6bnVsbCwiaWRlbnRpdHkiOnsiZW1haWwiOiJsaWppbmd5dTJAc2Vuc2V0aW1lLmNvbSIsImNyZWF0ZWRfYXQiOiIyMDIwLTEwLTI3VDA0OjI0OjQ0KzA4OjAwIiwidXBkYXRlZF9hdCI6IjIwMjAtMTAtMjdUMDQ6MjQ6NDQrMDg6MDAiLCJpZCI6Ijc5MmJjODEyLTRkYjYtNGZmYi1iNWU4LWUxOWViZTViZWJjNSIsImlkZW50aXR5X3R5cGUiOiJJbmRpdmlkdWFsIiwibmFtZSI6ImxpamluZ3l1MiIsInN0YXR1cyI6ImFjdGl2ZSIsImJpbGxpbmdfZGF0ZSI6NX0sInJzIjp7IklEIjoiYjNkMmQ3ZWQtMjBkMi00NmI0LWIzNDAtZjUzY2EzYWQ1MjBmIn19LCJleHAiOjE2MDQzMDA5NDQsImp0aSI6IjM0ZDg2NzM0LTMyOTktNGQ2YS04ZTlhLWE3NjA1YTg4ZjZiMiIsInN1YiI6Ijc5MmJjODEyLTRkYjYtNGZmYi1iNWU4LWUxOWViZTViZWJjNSIsInJhdCI6MTYwNDI5NzMyOX0=";
        Claims claims = Jwts.parser().parseClaimsJws(token).getBody();
//        System.out.println(claims.);
    }

}
