package sensecloud.event;

import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sensecloud.event.db.EventEntity;
import sensecloud.event.db.EventService;
import sensecloud.submitter.remote.KubernetesClient;
import sensecloud.submitter.remote.bean.ResultVO;
import sensecloud.submitter.remote.feign.AirflowSidecarService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "service.submitter")
public class EventMonitor {

    @Autowired
    private EventService eventService;

    @Autowired
    private AirflowSidecarService airflowSidecarService;

    @Setter
    private Map<String, String> env;

    @Autowired
    private KubernetesClient kubernetesClient;

    @Scheduled(cron = "0 */5 * * * ?")
    public void work() {
        List<EventEntity> events = eventService.listUnHandledEvents();
        if (events != null && !events.isEmpty()) {
            for (EventEntity event : events) {
                String type = event.getType();
                String action = event.getAction();
                String dagId = event.getDagId();

                if (StringUtils.isNotBlank(action)) {
                    EventAction act = EventAction.valueOf(action);

                    event.setStatus(EventStatus.ACCEPTED.name());
                    event.setUpdateBy("system");
                    event.setUpdateTime(LocalDateTime.now());
                    eventService.updateById(event);

                    switch(act) {
                        case CREATE_DAG: {
                            //Because of connectors schedule_interval = None, trigger DAG automatically.
                            if (type.equalsIgnoreCase(EventType.CONNECTOR.name())) {
                                ResultVO<String> triggerResult = this.airflowSidecarService.dagTrigger(dagId);
                                if (triggerResult.getCode() != 200) {
                                    log.error("EventMonitor - Trigger DAG {} failed: {}", dagId, triggerResult.getMsg());
                                    //rollback
                                    event.setStatus(EventStatus.PENDING.name());
                                    event.setUpdateBy("system");
                                    event.setUpdateTime(LocalDateTime.now());
                                    eventService.updateById(event);
                                    break;
                                }
                            }

                            ResultVO<String> pauseResult = this.airflowSidecarService.dagPause(dagId, false);
                            if (pauseResult.getCode() == 200) {
                                log.info("EventMonitor - Pause DAG {} success: {}", dagId, pauseResult.getMsg());
                                event.setStatus(EventStatus.SUCCESS.name());
                            } else {
                                log.error("EventMonitor - Pause DAG {} failed: {}", dagId, pauseResult.getMsg());
                                //rollback
                                event.setStatus(EventStatus.PENDING.name());
                            }
                            event.setUpdateBy("system");
                            event.setUpdateTime(LocalDateTime.now());
                            eventService.updateById(event);

                            break;
                        }
                        case UPDATE_DAG: {
                            if (type.equalsIgnoreCase(EventType.CONNECTOR.name())) {
                                ResultVO<String> pauseResult = this.airflowSidecarService.dagPause(dagId, true);
                                if (pauseResult.getCode() != 200) {
                                    log.error("EventMonitor - Un-paused DAG {} failed: {}", dagId, pauseResult.getMsg());
                                    //rollback
                                    event.setStatus(EventStatus.PENDING.name());
                                    event.setUpdateBy("system");
                                    event.setUpdateTime(LocalDateTime.now());
                                    eventService.updateById(event);
                                    break;
                                }
                                String namespace = env.get("kubernetes_namespace");
                                String podName = "connector-" + dagId;
                                kubernetesClient.stopPodAsync(namespace, podName, new ApiCallback<V1Pod>() {
                                    @Override
                                    public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
                                        log.info("Failed to stop pod {} in namespace {} with exception {}.", podName, namespace, e.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(V1Pod v1Pod, int i, Map<String, List<String>> map) {
                                        log.info("Stop pod {} in namespace {} successfully.", podName, namespace);
                                        ResultVO<String> triggerResult = airflowSidecarService.dagTrigger(dagId);

                                        if (triggerResult.getCode() == 200) {
                                            ResultVO<String> pauseResult = airflowSidecarService.dagPause(dagId, false);
                                            if (pauseResult.getCode() == 200) {
                                                log.info("DAG {} is ready to rerun", dagId);
                                            } else {
                                                log.error("Failed to un-pause DAG {}, message is {}", dagId, pauseResult.getMsg());
                                            }
                                        }  else {
                                            log.error("Failed to trigger DAG {}, message is {}", dagId, triggerResult.getMsg());
                                        }
                                    }

                                    @Override
                                    public void onUploadProgress(long l, long l1, boolean b) {

                                    }

                                    @Override
                                    public void onDownloadProgress(long l, long l1, boolean b) {

                                    }
                                });

                            } else {
                                ResultVO<String> pauseResult = airflowSidecarService.dagPause(dagId, false);
                                if (pauseResult.getCode() == 200) {
                                    log.info("DAG {} is ready to run", dagId);
                                } else {
                                    log.error("Failed to un-pause DAG {}, message is {}", dagId, pauseResult.getMsg());
                                }
                            }
                            break;
                        }
                        case DELETE_DAG: {
                            if (type.equalsIgnoreCase(EventType.CONNECTOR.name())) {
                                ResultVO<String> pauseResult = this.airflowSidecarService.dagPause(dagId, true);
                                if (pauseResult.getCode() != 200) {
                                    log.error("EventMonitor - Un-paused DAG {} failed: {}", dagId, pauseResult.getMsg());
                                    //rollback
                                    event.setStatus(EventStatus.PENDING.name());
                                    event.setUpdateBy("system");
                                    event.setUpdateTime(LocalDateTime.now());
                                    eventService.updateById(event);
                                    break;
                                }
                                String namespace = env.get("kubernetes_namespace");
                                String podName = "connector-" + dagId;
                                kubernetesClient.stopPodAsync(namespace, podName, new ApiCallback<V1Pod>() {
                                    @Override
                                    public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
                                        log.info("Failed to stop pod {} in namespace {} with exception {}.", podName, namespace, e.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(V1Pod v1Pod, int i, Map<String, List<String>> map) {
                                        log.info("Stop pod {} in namespace {} successfully.", podName, namespace);
                                    }

                                    @Override
                                    public void onUploadProgress(long l, long l1, boolean b) {

                                    }

                                    @Override
                                    public void onDownloadProgress(long l, long l1, boolean b) {

                                    }
                                });

                            }
                            break;
                        }
                    }

                    event.setStatus(EventStatus.SUCCESS.name());
                    event.setUpdateBy("system");
                    event.setUpdateTime(LocalDateTime.now());
                    eventService.updateById(event);

                }
            }
        }
    }

}
