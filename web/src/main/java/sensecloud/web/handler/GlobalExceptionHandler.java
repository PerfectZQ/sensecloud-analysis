package sensecloud.web.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import sensecloud.auth2.config.SSOConfiguration;
import sensecloud.web.bean.vo.ResultVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注意
 */
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @Autowired
    private SSOConfiguration configuration;

    /**
     * 未授权异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseBody
    // @ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "AuthenticationException")
    public ResultVO<Object> exceptionHandler(HttpServletRequest req, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.error("AuthenticationException: ", e);
        response.sendRedirect(this.configuration.getLogout_redirect_url());
        return ResultVO.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    /**
     * 处理其他异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public ResultVO<Object> exceptionHandler(HttpServletRequest req, HttpServletResponse response, Throwable e) throws IOException {
        log.error("ServerInternalException: ", e);
        return ResultVO.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
