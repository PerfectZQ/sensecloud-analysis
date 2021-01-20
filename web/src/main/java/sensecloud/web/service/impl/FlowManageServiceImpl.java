package sensecloud.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sensecloud.flow.generator.DAGGenerator;
import sensecloud.web.bean.vo.FlowVO;
import sensecloud.web.entity.FlowCodeEntity;
import sensecloud.web.entity.FlowEntity;
import sensecloud.web.entity.TaskEntity;
import sensecloud.web.service.UserSupport;
import sensecloud.web.service.IFlowManageService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class FlowManageServiceImpl extends UserSupport implements IFlowManageService {

    @Autowired
    private FlowServiceImpl flowService;

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private FlowCodeServiceImpl flowCodeService;

    @Autowired
    private DAGGenerator dagGenerator;

    @Value("${service.flow.env.clickhouse_host}")
    private String clickhouse_host;

    @Value("${service.flow.env.clickhouse_port}")
    private String clickhouse_port;


    public FlowEntity get(Long id) {
        FlowEntity entity = flowService.getWithTasks(id);
        return entity;
    }

    public IPage<FlowEntity> queryFlows(String name, Long page, Long size) {
        QueryChainWrapper<FlowEntity> query = flowService.query()
                                                        .orderByDesc("create_time")
                                                        .eq("deleted", false);
        if (StringUtils.isNotBlank(name)) {
            query.like("name", "%" + name + "%");
        }

        long pageNum = 1;
        long pageSize = 10;
        if(page != null) {
            pageNum = page.longValue();
        }

        if(size != null) {
            pageSize = size.longValue();
        }

        long total = query.count();
        IPage<FlowEntity> result = query.page(new Page<FlowEntity>(pageNum, pageSize, total));
        return result;
    }


    public void save(FlowVO vo) {
        flowService.save(vo);

        List<TaskEntity> entityList = new ArrayList<>();
        vo.getTasks().forEach(task -> {
            TaskEntity entity = new TaskEntity();
            BeanUtils.copyProperties(task, entity);
            entityList.add(entity);
        });
        taskService.saveBatch(entityList);

        String code = this.generateCode(vo);

        FlowCodeEntity codeEntity = new FlowCodeEntity();
        codeEntity.setFlowId(vo.getId());
        codeEntity.setCode(code);
        codeEntity.setVersion(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
        flowCodeService.save(codeEntity);

        //Todo: invoker restful api to submit code

    }

    public void update(FlowVO vo) {
        List<TaskEntity> tasksToModify = taskService.query().ge("flow_id", vo.getId()).list();

        String currentUser = this.getCurrentUserName();
        tasksToModify.forEach(task -> {
            task.setDeleted(true);
            task.setDeleteBy(currentUser);
            task.setDeleteTime(LocalDateTime.now());
        });
        taskService.updateBatchById(tasksToModify, 100);
        flowService.updateById(vo);

        List<TaskEntity> entityList = new ArrayList<>();
        vo.getTasks().forEach(task -> {
            TaskEntity entity = new TaskEntity();
            BeanUtils.copyProperties(task, entity);
            entityList.add(entity);
        });
        taskService.saveBatch(entityList);

        //Save new version code
        String code = this.generateCode(vo);
        FlowCodeEntity codeEntity = new FlowCodeEntity();
        codeEntity.setFlowId(vo.getId());
        codeEntity.setCode(code);
        codeEntity.setVersion(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
        flowCodeService.save(codeEntity);
        //Todo: invoker restful api to update code
        //Todo: restart airflow job and restart k8s pod
    }

    public void delete(Long id) {
        FlowEntity flowEntity = flowService.getById(id);
        String currentUser = this.getCurrentUserName();
        flowEntity.setDeleteBy(currentUser);
        flowEntity.setDeleted(true);
        flowEntity.setDeleteTime(LocalDateTime.now());

        List<TaskEntity> tasksToModify = taskService.query().ge("flow_id", flowEntity.getId()).list();

        tasksToModify.forEach(task -> {
            task.setDeleted(true);
            task.setDeleteBy(currentUser);
            task.setDeleteTime(LocalDateTime.now());
        });
        taskService.updateBatchById(tasksToModify, 100);
        flowService.updateById(flowEntity);

        //Todo: invoker restful api to delete code
        //Todo: stop airflow job and kill k8s pod
    }

    private String generateCode(FlowVO vo) {
        Map<String, Object> env = new HashMap<>();
        JSONObject clickhouseConf = this.getClickHouseUser(this.getCurrentUserName());

        env.put("ck_host", this.clickhouse_host);
        env.put("ck_port", this.clickhouse_port);
        env.put("ck_password", clickhouseConf.getString("ckPassword"));
        env.put("ck_user", clickhouseConf.getString("ckUser"));

        String code = dagGenerator.generate(vo, env);
        log.info(">>> Generated code: {}", code);
        return code;
    }



}
