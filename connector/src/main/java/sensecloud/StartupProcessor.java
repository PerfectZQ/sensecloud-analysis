package sensecloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sensecloud.connector.rule.RuleProvider;
import sensecloud.connector.submitter.airflow.AirflowDAGProvider;

@Slf4j
@Order(99)
@Component
public class StartupProcessor implements CommandLineRunner {

    @Autowired
    private AirflowDAGProvider airflowDAGProvider;

    @Autowired
    private RuleProvider ruleProvider;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>> Connector setup start ...");
        airflowDAGProvider.reloadTemplates();
        ruleProvider.loadFromClassPath();
        log.info(">>> Connector setup end!");
    }
}
