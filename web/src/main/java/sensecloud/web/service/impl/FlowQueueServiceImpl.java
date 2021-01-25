package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.FlowQueueEntity;
import sensecloud.web.mapper.FlowQueueMapper;
import sensecloud.web.service.IFlowQueueService;

@Slf4j
@Service
public class FlowQueueServiceImpl extends ServiceImpl<FlowQueueMapper, FlowQueueEntity> implements IFlowQueueService {
}
