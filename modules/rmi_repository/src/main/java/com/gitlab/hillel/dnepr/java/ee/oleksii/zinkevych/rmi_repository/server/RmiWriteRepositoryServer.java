package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.server;

import com.gitlab.hillel.dnepr.java.ee.common.utils.NetUtils;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.config.MainConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsWriteRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RmiWriteRepositoryServer implements Runnable {
    private static final int PORT = NetUtils.getFreePort();

    private static void printStartMessage() {
        System.out.println("Write repository server is started on port: " + PORT);
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
        RemoteCqrsWriteRepository<User, String> writeRepository = context.getBean(RemoteCqrsWriteRepository.class);
        try {
            Registry registry = LocateRegistry.getRegistry(63920);
            Remote remoteWriteRepo = UnicastRemoteObject.exportObject((Remote) writeRepository, PORT);
            registry.bind("writeRepository", remoteWriteRepo);
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
