package com.github.fridujo.sample;

import com.codahale.metrics.graphite.GraphiteReporter;
import com.github.fridujo.automocker.api.metrics.GraphiteMock;
import com.github.fridujo.automocker.base.Automocker;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Automocker
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GraphiteApplication.class)
class GraphiteApplicationTest {

    private final CommandLineRunner commandLineRunner;
    private final GraphiteMock graphiteMock;
    private final GraphiteReporter graphiteReporter;

    GraphiteApplicationTest(@Autowired CommandLineRunner commandLineRunner, @Autowired GraphiteMock graphiteMock, @Autowired GraphiteReporter graphiteReporter) {
        this.commandLineRunner = commandLineRunner;
        this.graphiteMock = graphiteMock;
        this.graphiteReporter = graphiteReporter;
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 7})
    void metric_is_sent_to_graphite(int valueToIncrementBy) throws Exception {
        commandLineRunner.run(String.valueOf(valueToIncrementBy));
        graphiteReporter.report();

        graphiteMock.assertThatMetric("run.count")
            .derivative()
            .hasLastValue(valueToIncrementBy);
    }
}
