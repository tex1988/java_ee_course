package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RmiRegistryServer implements Runnable {
    private static final int PORT = 63920;

    private static void printStartMessage() {
        System.out.println("Registry server is started on port: " + PORT);
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
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
        } catch (RemoteException e) {
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
