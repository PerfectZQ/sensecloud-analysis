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
import sensecloud.submitter.airflow.RestfulApiSubmitter;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.vo.FlowVO;
import sensecloud.web.entity.FlowCodeEntity;
import sensecloud.web.entity.FlowEntity;
import sensecloud.web.entity.TaskEntity;
import sensecloud.web.service.UserSupport;
import sensecloud.web.service.IFlowManageService;
import sensecloud.web.service.remote.AirflowRemoteService;
import sensecloud.web.utils.DesUtil;

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
    private RestfulApiSubmitter submitter;

    @Autowired
    private DAGGenerator dagGenerator;

    @Autowired
    private AirflowRemoteService airflowRemoteService;

    @Value("${service.flow.env.clickhouse_host}")
    private String clickhouse_host;

    @Value("${service.flow.env.clickhouse_port}")
    private String clickhouse_port;

    @Value("${service.connector.clickhouse.des.key}")
    private String ch_pwd_decrypt_key;


    public FlowEntity get(Long id) {
        FlowEntity entity = flowService.getById(id);
        if(entity != null) {
            List<TaskEntity> tasks = taskService.query().eq("deleted", false).eq("flow_id", entity.getId()).list();
            entity.getTasks().addAll(tasks);
        }
        return entity;
    }

    public IPage<FlowEntity> queryFlows(String name, Long page, Long size) {
        QueryChainWrapper<FlowEntity> query = flowService.query()
//                                                        .eq("create_by", this.getCurrentUserName())
                                                        .eq("deleted", false)
                                                        .orderByDesc("create_time");
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

    public PageResult queryFlowRuns(String dagId, Long page, Long size) {
        return this.airflowRemoteService.listDagRuns(this.getCurrentUserName(), dagId, page.intValue(), size.intValue());
    }


    public boolean save(FlowVO vo) {
        String currentUser = this.getCurrentUserName();
        vo.setDagId(vo.getName());
        vo.setCreateBy(currentUser);
        flowService.save(vo);

        List<TaskEntity> entityList = new ArrayList<>();
        vo.getTasks().forEach(task -> {
            TaskEntity entity = new TaskEntity();
            BeanUtils.copyProperties(task, entity);
            entity.setFlowId(vo.getId());
            entityList.add(entity);
        });
        taskService.saveBatch(entityList);

        String code = this.generateCode(vo);

        FlowCodeEntity codeEntity = new FlowCodeEntity();
        codeEntity.setCreateBy(currentUser);
        codeEntity.setFlowId(vo.getId());
        codeEntity.setCode(code);
        codeEntity.setVersion(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
        flowCodeService.save(codeEntity);

        //invoker restful api to submit code
        boolean success = this.submitter.submitFlowJob(vo.getSaas(), currentUser, vo.getName(), code);
        if (success) {
            return true;
        } else {
            log.error("Failed to create Dag File. Please re-create manually.");
            return false;
        }
    }

    public boolean update(FlowVO vo) {
        List<TaskEntity> tasksToModify = taskService.query().ge("flow_id", vo.getId()).list();
        if(StringUtils.isNotBlank(vo.getName())){
            vo.setDagId(vo.getName());
        }
        String currentUser = this.getCurrentUserName();
        List<Long> ids = new ArrayList<>();
        tasksToModify.forEach(task -> {
            ids.add(task.getId());
        });
        taskService.removeByIds(ids);

        vo.setUpdateBy(currentUser);
        flowService.updateById(vo);

        List<TaskEntity> entityList = new ArrayList<>();
        vo.getTasks().forEach(task -> {
            TaskEntity entity = new TaskEntity();
            BeanUtils.copyProperties(task, entity);
            entity.setFlowId(vo.getId());
            entityList.add(entity);
        });
        taskService.saveBatch(entityList);
        log.info(">>> Test vo.getId = {}", vo.getId());
        //Save new version code
        String code = this.generateCode(vo);
        FlowCodeEntity codeEntity = new FlowCodeEntity();
        codeEntity.setCreateBy(currentUser);
        codeEntity.setFlowId(vo.getId());
        codeEntity.setCode(code);
        codeEntity.setVersion(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
        flowCodeService.save(codeEntity);

        boolean success = this.submitter.updateFlowJob(vo.getSaas(), currentUser, vo.getName(), code);

        if (success) {
            log.info("Update DAG successfully: {}", vo.getName());
            return true;
        } else {
            log.error("Failed to update Dag File. Please re-update manually. DAG info: {}", vo);
            //Todo: Rollback and return failure message to caller
            return false;
        }
    }

    public boolean delete(Long id) {
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

        FlowCodeEntity codeEntity = flowCodeService.getLatestVersionCode(flowEntity.getId());
        if(codeEntity != null) {
            codeEntity.setDeleted(true);
            codeEntity.setDeleteBy(currentUser);
            codeEntity.setDeleteTime(LocalDateTime.now());
            flowCodeService.updateById(codeEntity);
        }

        //invoker restful api to delete code
        boolean success = this.submitter.removeFlowJob(flowEntity.getSaas(), currentUser, flowEntity.getName());
        if (success) {
            log.info("Delete DAG successfully: fileName = {}, groupName = {}", flowEntity.getName(), flowEntity.getSaas());
            return true;
        } else {
            log.error("Failed to delete Dag File. Please re-delete manually. DAG info: fileName = {}, groupName = {}", flowEntity.getName(), flowEntity.getSaas());
            return false;
        }

    }

    private String generateCode(FlowVO vo) {
        Map<String, Object> env = new HashMap<>();
        try {
            JSONObject clickhouseConf = this.getClickHouseUser(this.getCurrentUserName());

            env.put("ck_host", this.clickhouse_host);
            env.put("ck_port", this.clickhouse_port);

            String encryptedPwd = clickhouseConf.getString("ckPassword");

            env.put("ck_password", DesUtil.decrypt(this.ch_pwd_decrypt_key, encryptedPwd));
            env.put("ck_user", clickhouseConf.getString("ckUser"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String code = dagGenerator.generate(vo, env);
        log.info(">>> Generated code: {}", code);
        return code;
    }



}
