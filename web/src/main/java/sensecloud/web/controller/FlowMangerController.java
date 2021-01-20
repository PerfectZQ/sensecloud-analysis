package sensecloud.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sensecloud.web.bean.FlowBean;
import sensecloud.web.bean.TaskBean;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.entity.ConnectorEntity;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/flow-manager")
public class FlowMangerController {

    @GetMapping
    public ResultVO<IPage<FlowBean>> query(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size
    ) {
        return null;
    }


    @GetMapping("/{id}")
    public ResultVO<FlowBean> get(@PathVariable("id") Long id) {
        return null;
    }

    @PostMapping
    public ResultVO<Boolean> add(@RequestBody FlowBean flowBean) {
        return ResultVO.ok(true);
    }

    @PutMapping
    public ResultVO<Boolean> update(@RequestBody FlowBean flowBean) {
        return ResultVO.ok(true);
    }

    @DeleteMapping
    public ResultVO<Boolean> delete(@RequestParam Long id) {
        return ResultVO.ok(true);
    }

    @PostMapping("/task/check")
    public ResultVO<Boolean> check(@RequestBody List<TaskBean> tasks) {
        return ResultVO.ok(true);
    }
    
}
