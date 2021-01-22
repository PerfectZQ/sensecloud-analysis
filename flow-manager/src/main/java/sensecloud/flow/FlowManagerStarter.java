package sensecloud.flow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sensecloud.flow.generator.DAGDefinitionProvider;

@Slf4j
@Order(99)
@Component
public class FlowManagerStarter implements CommandLineRunner {

    @Autowired
    private DAGDefinitionProvider provider;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>> Flow manager setup start ...");
        provider.loadFromClassPath();
        log.info(">>> Flow manager setup end!");
    }
}
