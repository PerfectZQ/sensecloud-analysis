package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.FlowCodeEntity;
import sensecloud.web.entity.FlowEntity;
import sensecloud.web.mapper.FlowCodeMapper;
import sensecloud.web.mapper.FlowMapper;
import sensecloud.web.service.IFlowCodeService;
import sensecloud.web.service.IFlowService;

@Slf4j
@Service
public class FlowCodeServiceImpl extends ServiceImpl<FlowCodeMapper, FlowCodeEntity> implements IFlowCodeService {

    public FlowCodeEntity getLatestVersionCode(Long flowId) {
        return this.baseMapper.getLatestVersion(flowId);
    }

}
