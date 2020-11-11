package sensecloud.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sensecloud.auth2.UserContextProvider;
import sensecloud.auth2.model.User;
import sensecloud.web.bean.vo.ConnectorVO;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.entity.ConnectorEntity;
import sensecloud.web.service.IConnectorService;
import sensecloud.web.service.impl.ConnectorServiceImpl;

import java.util.List;

import static sensecloud.web.bean.vo.ResultVO.*;

@Slf4j
@RestController
@RequestMapping("/connector")
public class ConnectorController {

    @Autowired
    private ConnectorServiceImpl connectorService;

    @PostMapping
    public ResultVO<Boolean> add(@RequestBody ConnectorVO params) {
        //Todo: params validation
        //  ...
        ConnectorEntity entity = new ConnectorEntity();
        BeanUtils.copyProperties(params, entity);
        boolean success = connectorService.save(entity);
        if(success) {
            success = connectorService.submit(entity);
        }
        return ok(success);
    }

    @PutMapping
    public ResultVO<Boolean> update(@RequestBody ConnectorVO params) {
        //Todo: params validation
        //  ...
        ConnectorEntity entity = new ConnectorEntity();
        BeanUtils.copyProperties(params, entity);
        boolean updateResult = connectorService.updateById(entity);
        return ok(updateResult);
    }

    @DeleteMapping
    public ResultVO<Boolean> delete(@RequestParam Long id) {
        //Todo: params validation
        //  ...
        ConnectorEntity entity = new ConnectorEntity();
        entity.setId(id);
        entity.setDeleted(true);
        boolean deleteResult = connectorService.updateById(entity);
        return ok(deleteResult);
    }

    @GetMapping
    public ResultVO<ConnectorEntity> get(@RequestBody Long id) {
        ConnectorEntity entity = connectorService.getById(id);
        return ok(entity);
    }

    @GetMapping("/query")
    public ResultVO<List<ConnectorEntity>> query() {
        User user = UserContextProvider.getContext().getCurrentUser();
        if (user != null && StringUtils.isNotBlank(user.getUsername())) {
            String username = user.getUsername();
            List<ConnectorEntity> result = connectorService.query()
                    .select("id", "name", "source_name", "source_type", "sink_name", "sink_type", "create_time")
                    .eq("create_by", username)
                    .and(q -> q.eq("deleted", false))
                    .list();
            return ok(result);
        } else {
            log.warn("Accept a request but current user is null or username is empty.");
            return error("Not a login user.");
        }
    }

}
