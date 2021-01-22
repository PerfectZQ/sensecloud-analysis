package sensecloud.flow;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Flow {

    private String dagId;
    private String name;
    private String scheduleExpr;
    private List<Task> tasks = new LinkedList<>();

    public String checkLoop() {
        Map<String, Task> dict = new HashMap<>();
        Set<String> ids = new HashSet<>();
        Set<String> deps = new HashSet<>();
        tasks.forEach(t -> {
            ids.add(t.getTaskId());
            t.getDependencyIds().forEach(id -> deps.add((String) id));
            dict.put(t.getTaskId(), t);
        });

        // Get the except set
        ids.removeAll(deps);

        Set<String> marked = new HashSet<>();
        String loopedId = null;
        for(String id : ids) {
            if(!"None".equalsIgnoreCase(id)) {
                Task end = dict.get(id);
                if(end != null) {
                    loopedId = hasLoop(end, dict, marked);
                }
            }
        }
        return loopedId;
    }

    private String hasLoop(Task task, Map<String, Task> dict, Set<String> marked) {
        for(int i = 0 ; i < task.getDependencyIds().size(); i++) {
            String id = task.getDependencyIds().get(i);
            if (marked.contains(id)) {
                return id;
            } else {
                Task next = dict.get(id);
                marked.add(id);
                return hasLoop(next, dict, marked);
            }
        }
        return null;
    }

}
