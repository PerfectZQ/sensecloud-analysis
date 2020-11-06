package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "clickHouseRemoteAuthService", url = "10.10.41.59:8082/v1/hadoop/")
public interface ClickHouseRemoteAuthService {
}
