package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.FlowCodeEntity;
import sensecloud.web.entity.TaskEntity;
import sensecloud.web.mapper.FlowCodeMapper;
import sensecloud.web.mapper.TaskMapper;
import sensecloud.web.service.IFlowCodeService;
import sensecloud.web.service.ITaskService;

@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskEntity> implements ITaskService {
}
