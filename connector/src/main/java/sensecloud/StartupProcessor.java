package sensecloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sensecloud.connector.rule.RuleProvider;

@Slf4j
@Order(99)
@Component
public class StartupProcessor implements CommandLineRunner {

    @Autowired
    private RuleProvider ruleProvider;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>> Connector setup start ...");
        ruleProvider.loadFromClassPath();
        log.info(">>> Connector setup end!");
    }
}
