package org.tilkynna.report.generate.worker;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartWorkers implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private GenerateReportQueueRunnable generateReportQueueRunnable;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Random randomGenerator = new Random();
        //
        // for (int i = 0; i < 100; i++) {
        // Thread thread1 = new Thread(generateReportQueueRunnable, "GenRepWorker-" + i);
        // try {
        // int randomInt = randomGenerator.nextInt(5) + 1;
        // Thread.sleep(randomInt * 1000);
        // thread1.start();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        //
        // }
    }
}