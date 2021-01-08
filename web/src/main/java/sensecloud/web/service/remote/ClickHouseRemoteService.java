package sensecloud.web.service.remote;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "clickHouseRemoteService", url = "clickhouse-access:8080/bigdata-admin/api/v1/access")
public interface ClickHouseRemoteService {

    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET, consumes = "application/json")
    JSONObject getClickHouseUser(@PathVariable(value = "username") String username) throws Exception;

}
