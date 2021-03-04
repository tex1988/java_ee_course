package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.server;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.config.SpringRmiServerConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringRmiServer implements Runnable {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringRmiServerConfig.class);
        System.out.println("================ Server Started ========================");
    }

    @Override
    public void run() {
        main(new String[0]);
    }
}