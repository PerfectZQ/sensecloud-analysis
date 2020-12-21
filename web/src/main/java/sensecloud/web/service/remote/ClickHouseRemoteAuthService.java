package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sensecloud.web.bean.clickhouse.RequestAdmin;
import sensecloud.web.bean.clickhouse.RequestBoundUser;
import sensecloud.web.bean.clickhouse.RequestProduct;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "clickHouseRemoteAuthService", url = "clickhouse-access:8080/bigdata-admin/api/v1/access/role")
// @FeignClient(name = "clickHouseRemoteAuthService", url = "10.53.7.100:38080/bigdata-admin/api/v1/access/role")
public interface ClickHouseRemoteAuthService {

    @PostMapping("initproduct")
    void initProduct(@RequestBody RequestProduct requestProduct);

    @PostMapping("initadmin")
    void initAdmin(@RequestBody RequestAdmin requestProduct);

    @PostMapping("bound")
    void boundUserRoleToGroup(@RequestBody RequestBoundUser requestBoundUser);

    @PostMapping("unbound")
    void unboundUserRoleFromGroup(@RequestBody RequestBoundUser requestBoundUser);

}
