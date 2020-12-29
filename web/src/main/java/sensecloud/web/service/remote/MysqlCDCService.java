package sensecloud.web.service.remote;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sensecloud.web.bean.clickhouse.RequestProduct;

import javax.websocket.server.PathParam;

@FeignClient(name = "mysqlCDCService", url = "http://10.10.18.109:49018//mysql-cdc/api/v1")
public interface MysqlCDCService {

    @PostMapping("/syncdb/dbtable")
    JSONObject add(@RequestBody JSONObject params);

    @PutMapping("/syncdb/dbtable")
    JSONObject update(@RequestBody JSONObject params);

    @DeleteMapping("/syncdb/dbtable/{id}")
    JSONObject delete(@PathParam("id") String id);

}
