package sensecloud.web.controller.openfeign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sensecloud.web.service.remote.AirflowRemoteAuthService;
import sensecloud.web.service.remote.ClickHouseRemoteAuthService;
import sensecloud.web.service.remote.SupersetRemoteAuthService;


@RestController
@RequestMapping("/openfeign")
public class OpenFeignController {

    @Autowired
    private AirflowRemoteAuthService airflowRemoteService;

    @Autowired
    private SupersetRemoteAuthService supersetRemoteAuthService;

    @Autowired
    private ClickHouseRemoteAuthService clickHouseRemoteAuthService;


}
