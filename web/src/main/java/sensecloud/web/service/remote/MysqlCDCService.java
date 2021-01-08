package sensecloud.web.service.remote;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "mysqlCDCService", url = "${remote.rest.mysql_cdc.url}")
public interface MysqlCDCService {

    @PostMapping("/syncdb/dbtable")
    JSONObject add(@RequestBody JSONObject params);

    @PutMapping("/syncdb/dbtable")
    JSONObject update(@RequestBody JSONObject params);

    @DeleteMapping("/syncdb/dbtable/{id}")
    JSONObject delete(@PathVariable("id") String id);

}
