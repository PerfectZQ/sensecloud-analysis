package sensecloud.web.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import sensecloud.web.bean.vo.ResultVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangqiang
 * @since 2020/12/16 21:55
 */
@Component
public class TokenAuthenticationFailureHandler implements AuthenticationFailureHandler {
    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (!(exception instanceof UsernameNotFoundException)) {
            ResultVO<Object> resultVO = ResultVO.error(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(JSON.toJSONString(resultVO));
        } else {
            ResultVO<Object> resultVO = ResultVO.error(HttpStatus.FORBIDDEN.value(), exception.getMessage());
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(JSON.toJSONString(resultVO));
        }
    }
}
