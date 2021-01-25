package sensecloud.web.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sensecloud.flow.Flow;
import sensecloud.web.bean.FlowBean;
import sensecloud.web.bean.TaskBean;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.vo.FlowVO;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.entity.ConnectorEntity;
import sensecloud.web.entity.FlowEntity;
import sensecloud.web.service.impl.FlowManageServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/flow-manager")
public class FlowMangerController {

    @Autowired
    private FlowManageServiceImpl flowManageService;

    @GetMapping
    public ResultVO<IPage<FlowEntity>> query(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size
    ) {
        IPage<FlowEntity> flows = flowManageService.queryFlows(name, page, size);
        return ResultVO.ok(flows);
    }

    @GetMapping("/runs")
    public PageResult queryFlowRuns(
            @RequestParam String dagId,
            @RequestParam Long page,
            @RequestParam Long size
    ) {
        return flowManageService.queryFlowRuns(dagId, page, size);
    }

    @GetMapping("/{id}")
    public ResultVO<FlowEntity> get(@PathVariable("id") Long id) {
        FlowEntity entity = flowManageService.get(id);
        return ResultVO.ok(entity);
    }

    @PostMapping
    public ResultVO<Boolean> add(@RequestBody FlowVO vo) {
        //Input validation
        if(flowManageService.save(vo)) {
            return ResultVO.ok(true);
        } else {
            return ResultVO.error("Failed to save.");
        }
    }

    @PutMapping
    public ResultVO<Boolean> update(@RequestBody FlowVO vo) {
        //Input validation
        if(flowManageService.update(vo)){
            return ResultVO.ok(true);
        } else {
            return ResultVO.error("Failed to update.");
        }
    }

    @DeleteMapping
    public ResultVO<Boolean> delete(@RequestParam Long id) {
        if(flowManageService.delete(id)) {
            return ResultVO.ok(true);
        } else {
            return ResultVO.error("Failed to delete.");
        }
    }

    @PostMapping("/task/check")
    public ResultVO<Boolean> check(@RequestBody List<TaskBean> tasks) {
        log.info("Calling URL [POST] /task/check with parameters {}", JSON.toJSONString(tasks));

        Flow flow = new Flow();
        flow.getTasks().addAll(tasks);
        String loopedId = flow.checkLoop();
        if (StringUtils.isBlank(loopedId)) {
            return ResultVO.ok(true);
        } else {
            return ResultVO.error("Looped id is " + loopedId);
        }
    }

}
