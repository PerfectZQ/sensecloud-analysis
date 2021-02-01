package sensecloud.submitter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sensecloud.submitter.airflow.AirflowDAGProvider;

@Slf4j
@Order(100)
@Component
public class SubmitterInitializer implements CommandLineRunner {

    @Autowired
    private AirflowDAGProvider airflowDAGProvider;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>> Submitter setup start ...");
        airflowDAGProvider.reloadTemplates();
        log.info(">>> Submitter setup end!");
    }
}