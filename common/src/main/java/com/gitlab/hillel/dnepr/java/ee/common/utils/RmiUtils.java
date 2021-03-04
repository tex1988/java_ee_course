package com.gitlab.hillel.dnepr.java.ee.common.utils;

import lombok.experimental.UtilityClass;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

@UtilityClass
public class RmiUtils {
    public static void startServer(int port, String host, Remote remoteObject) throws Exception {
        startServer(port, host, remoteObject.getClass().getSimpleName(), remoteObject);
    }

    public static void startServer(int port, String host, String remotePath, Remote remoteObject) throws Exception {
        String rmiUri = String.format(
                "rmi://%s:%s/%s",
                host,
                port,
                remotePath);

        LocateRegistry.createRegistry(port);
        Naming.rebind(rmiUri, remoteObject);
    }

    public <T extends Remote> T startClient(int port, String host, Class<T> remoteObject) throws Exception {
        return startClient(port, host, remoteObject.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public <T extends Remote> T startClient(int port, String host, String remoteObject) throws Exception {
        String rmiUri = String.format(
                "rmi://%s:%s/%s",
                host,
                port,
                remoteObject);
        // lookup method to find reference of remote object
        return (T) Naming.lookup(rmiUri);
    }
}
