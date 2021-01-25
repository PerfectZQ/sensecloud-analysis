package sensecloud.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
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
import sensecloud.web.bean.FlowRunBean;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.vo.DagFileVO;
import sensecloud.web.bean.vo.FlowRunVO;
import sensecloud.web.bean.vo.FlowVO;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.entity.FlowCodeEntity;
import sensecloud.web.entity.FlowEntity;
import sensecloud.web.entity.TaskEntity;
import sensecloud.web.service.UserSupport;
import sensecloud.web.service.IFlowManageService;
import sensecloud.web.service.remote.AirflowRemoteService;

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

    @Autowired
    private AirflowRemoteService airflowRemoteService;

    @Value("${service.flow.env.clickhouse_host}")
    private String clickhouse_host;

    @Value("${service.flow.env.clickhouse_port}")
    private String clickhouse_port;


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
                                                        .eq("create_by", this.getCurrentUserName())
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

        //Todo: invoker restful api to submit code
        DagFileVO dag = new DagFileVO();
        dag.setFileName(vo.getName());
        dag.setGroupName(vo.getSaas());
        dag.setSourceCode(code);
        ResultVO<String> createResult = airflowRemoteService.createOrUpdateDagFile(dag);
        if (createResult.getCode() == 200) {
            log.info("Create DAG successfully: {}", dag);
            return true;
        } else {
            log.error("Failed to create Dag File. Please re-create manually. DAG info: {}, return message: {}", dag, createResult.getMsg());
            return false;
        }
    }

    public boolean update(FlowVO vo) {
        List<TaskEntity> tasksToModify = taskService.query().ge("flow_id", vo.getId()).list();
        if(StringUtils.isNotBlank(vo.getName())){
            vo.setDagId(vo.getName());
        }
        String currentUser = this.getCurrentUserName();
        tasksToModify.forEach(task -> {
            task.setDeleted(true);
            task.setDeleteBy(currentUser);
            task.setDeleteTime(LocalDateTime.now());
        });
        taskService.updateBatchById(tasksToModify, 100);
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
        log.debug(">>> Test vo.getId = {}", vo.getId());
        //Save new version code
        String code = this.generateCode(vo);
        FlowCodeEntity codeEntity = new FlowCodeEntity();
        codeEntity.setCreateBy(currentUser);
        codeEntity.setFlowId(vo.getId());
        codeEntity.setCode(code);
        codeEntity.setVersion(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
        flowCodeService.save(codeEntity);

        DagFileVO dag = new DagFileVO();
        dag.setFileName(vo.getName());
        dag.setGroupName(vo.getSaas());
        dag.setSourceCode(code);

        ResultVO<String> updateResult = airflowRemoteService.createOrUpdateDagFile(dag);
        if (updateResult.getCode() == 200) {
            log.info("Update DAG successfully: {}", dag);
            return true;
        } else {
            log.error("Failed to update Dag File. Please re-update manually. DAG info: {}, return message: {}", dag, updateResult.getMsg());
            //Todo: Rollback and return failure message to caller
            return false;
        }

        //Todo: restart airflow job and restart k8s pod
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

        QueryChainWrapper<FlowCodeEntity> query = flowCodeService.query()
                .eq("deleted", false)
                .eq("flow_id", flowEntity.getId())
                .orderByDesc("version")
                .first("limit 1");
        FlowCodeEntity codeEntity = flowCodeService.getOne(query);
        if(codeEntity != null) {
            codeEntity.setDeleted(true);
            codeEntity.setDeleteBy(currentUser);
            codeEntity.setDeleteTime(LocalDateTime.now());
            flowCodeService.updateById(codeEntity);
        }

        //invoker restful api to delete code
        ResultVO<String> deleteResult = airflowRemoteService.deleteDagFile(flowEntity.getName(), flowEntity.getSaas());
        if (deleteResult.getCode() == 200) {
            log.info("Delete DAG successfully: fileName = {}, groupName = {}", flowEntity.getName(), flowEntity.getSaas());
            //Todo: stop airflow job and kill k8s pod
            return true;
        } else {
            log.error("Failed to delete Dag File. Please re-delete manually. DAG info: fileName = {}, groupName = {}, return message: {}", flowEntity.getName(), flowEntity.getSaas(), deleteResult.getMsg());
            //Todo: Rollback and return failure message to caller
            return false;
        }

    }

    private String generateCode(FlowVO vo) {
        Map<String, Object> env = new HashMap<>();
        try {
            JSONObject clickhouseConf = this.getClickHouseUser(this.getCurrentUserName());

            env.put("ck_host", this.clickhouse_host);
            env.put("ck_port", this.clickhouse_port);
            env.put("ck_password", clickhouseConf.getString("ckPassword"));
            env.put("ck_user", clickhouseConf.getString("ckUser"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String code = dagGenerator.generate(vo, env);
        log.info(">>> Generated code: {}", code);
        return code;
    }



}
