package sensecloud.web.service.remote;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@FeignClient(name = "mysqlCDCService", url = "http://10.10.18.109:49018/mysql-cdc/api/v1")
public interface MysqlCDCService {

    @PostMapping("/syncdb/dbtable")
    JSONObject add(@RequestBody JSONObject params);

    @PutMapping("/syncdb/dbtable")
    JSONObject update(@RequestBody JSONObject params);

    @DeleteMapping("/syncdb/dbtable/{id}")
    JSONObject delete(@PathVariable("id") String id);

}
