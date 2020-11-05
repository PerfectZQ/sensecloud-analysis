<<<<<<< HEAD:auth2/src/main/java/sensecloud/auth2/controller/SSOController.java
package sensecloud.auth2.controller;
=======
package sensecloud.web.controller;
>>>>>>> master:web/src/main/java/sensecloud/web/controller/SSOController.java

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sso")
public class SSOController {

    @GetMapping("/index")
    public String index(){
        return "Success";
    }

    @GetMapping
    public String sso(){
        return "Success";
    }

}
