package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.FlowEntity;
import sensecloud.web.mapper.FlowMapper;
import sensecloud.web.service.IFlowService;

@Slf4j
@Service
public class FlowServiceImpl extends ServiceImpl<FlowMapper, FlowEntity> implements IFlowService {

    public FlowEntity getWithTasks(Long id){
        return this.baseMapper.getWithTasks(id);
    }
}
