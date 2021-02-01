package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.EventEntity;
import sensecloud.web.mapper.EventMapper;
import sensecloud.web.service.IEventService;

@Slf4j
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, EventEntity> implements IEventService {

}
