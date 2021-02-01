package sensecloud.event.db;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sensecloud.event.EventAction;
import sensecloud.event.EventStatus;
import sensecloud.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EventService extends ServiceImpl<EventMapper, EventEntity> {

    public void raiseEvent(String user, String eventName, EventType type, EventAction action, String dagId) {
        EventEntity queueItem = new EventEntity();
        queueItem.setCreateBy(user);
        queueItem.setCreateTime(LocalDateTime.now());
        queueItem.setDagId(dagId);

        queueItem.setName(eventName);
        queueItem.setType(type.name());
        queueItem.setAction(action.name());
        queueItem.setStatus(EventStatus.PENDING.name());

        this.save(queueItem);
    }

    public List<EventEntity> listUnHandledEvents() {
        List<EventEntity> events = this.query()
                .eq("deleted", 0)
                .eq("status", EventStatus.PENDING.name())
                .list();
        return events;
    }




}
