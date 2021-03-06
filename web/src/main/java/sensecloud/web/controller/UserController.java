package sensecloud.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sensecloud.auth2.model.UserInfo;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.service.UserSupport;

import javax.servlet.http.HttpServletRequest;

import static sensecloud.web.bean.vo.ResultVO.*;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/current")
    public ResponseEntity<ResultVO<String>> current(HttpServletRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        if (auth == null) {
            return new ResponseEntity<ResultVO<String>>(error("No authenticated user."), HttpStatus.UNAUTHORIZED);
        }
        log.info("Current User's details: {}", auth.getDetails());
        UserInfo userInfo = userSupport.currentUserInfo();
        log.info("Current User's info: {}", userInfo);
        String username = auth.getName();
        if (StringUtils.isBlank(username)) {
            return new ResponseEntity<ResultVO<String>>(error("Username in auth is empty."), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<ResultVO<String>>(ok(auth.getName()), HttpStatus.OK);
    }

}
