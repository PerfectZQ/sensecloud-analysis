package sensecloud.web.controller.openfeign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.superset.DashboardsVO;
import sensecloud.web.service.remote.SupersetRemoteAuthService;

/**
 * @author zhangqiang
 * @since 2021/1/8 14:18
 */
@RestController
@RequestMapping("/api/v1/superset")
public class SupersetRemoteController {

    @Autowired
    private SupersetRemoteAuthService supersetRemoteAuthService;

    @PostMapping("/listDashboardsVO/{pageId}/{pageSize}")
    public PageResult listDashboardsVO(@PathVariable Integer pageId,
                                       @PathVariable Integer pageSize,
                                       @RequestBody DashboardsVO dashboardsVO) {
        return supersetRemoteAuthService.listDashboardsVO(pageId, pageSize, dashboardsVO);
    }
}
