package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public interface RemoteCqrsIndexedReadRepository<T extends BaseEntity<ID>, ID> extends Remote, Serializable {

    boolean hasIndex(String key) throws RemoteException;

    void addIndex(String key) throws RemoteException;

    void removeIndex(String key) throws RemoteException;

    void addIndexes(Set<String> keySet) throws RemoteException;

    void removeIndexes(Set<String> keySet) throws RemoteException;

    List<T> findByIndex(String key, Object value) throws RemoteException;

    long count() throws RemoteException;

    boolean existsById(ID id) throws RemoteException;

    Iterable<T> findAll() throws RemoteException;

    Iterable<T> findAllById(Iterable<ID> ids) throws RemoteException;

    T findById(ID id) throws RemoteException;

    Future<?> apply(Observer.Action action, Iterable<T> entityList) throws RemoteException;

    void close() throws Exception;
}
