package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteCqrsWriteRepository<T extends BaseEntity<ID>, ID> extends Remote, Serializable {
    void delete(T entity) throws RemoteException;

    void deleteAll() throws RemoteException;

    void deleteAll(Iterable<? extends T> entities) throws RemoteException;

    void deleteById(ID id) throws RemoteException;

    <S extends T> S save(S entity) throws RemoteException;

    <S extends T> Iterable<S> saveAll(Iterable<S> entities) throws RemoteException;

    boolean addObserver(Observer<T, ID> observer) throws RemoteException;

    boolean addObservers(List<Observer<T, ID>> observerList) throws RemoteException;

    List<Observer<T, ID>> getObservers() throws RemoteException;

    boolean removeObserver(Observer<T, ID> observer) throws RemoteException;

    boolean removeObservers(List<Observer<T, ID>> observerList) throws RemoteException;

    void close() throws Exception;
}
