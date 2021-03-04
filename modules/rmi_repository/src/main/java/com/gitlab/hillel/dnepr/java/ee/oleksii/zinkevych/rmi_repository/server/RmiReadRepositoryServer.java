package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.server;

import com.gitlab.hillel.dnepr.java.ee.common.utils.NetUtils;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.config.MainConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsIndexedReadRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RmiReadRepositoryServer implements Runnable {
    private static final int PORT = NetUtils.getFreePort();

    private static void printStartMessage() {
        System.out.println("Read repository server is started on port: " + PORT);
        System.out.print("Enter exit to terminate: ");
    }

    private static void waitForExit() {
        Scanner scanner = new Scanner(System.in);
        String input;
        while (true) {
            input = scanner.nextLine();
            if (input.equals("exit")) {
                System.exit(0);
            }
        }
    }

    private static void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
        RemoteCqrsIndexedReadRepository<User, String> readRepository = context.getBean(RemoteCqrsIndexedReadRepository.class);
        try {
            Registry registry = LocateRegistry.getRegistry(63920);
            Remote remoteReadRepo = UnicastRemoteObject.exportObject((Remote) readRepository, PORT);
            registry.bind("readRepository", remoteReadRepo);
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
        printStartMessage();
        waitForExit();
    }

    @Override
    public void run() {
        main(new String[0]);
    }
}
