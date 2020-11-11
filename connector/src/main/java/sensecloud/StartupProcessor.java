package sensecloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sensecloud.connector.rule.RuleProvider;
import sensecloud.connector.submitter.airflow.AirflowDAGProvider;

@Order(99)
@Component
public class StartupProcessor implements CommandLineRunner {

    @Autowired
    private AirflowDAGProvider airflowDAGProvider;

    @Autowired
    private RuleProvider ruleProvider;

    @Override
    public void run(String... args) throws Exception {
        airflowDAGProvider.reloadTemplates();
        ruleProvider.loadFromClassPath();
    }
}
