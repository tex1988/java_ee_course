package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsWriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.rmi.RemoteException;
import java.util.List;

@Repository
public class RemoteCqrsWriteRepositoryImpl<T extends BaseEntity<ID>, ID> implements RemoteCqrsWriteRepository<T, ID> {
    private CqrsWriteRepository<T, ID> writeRepository;

    @Autowired
    public RemoteCqrsWriteRepositoryImpl(CqrsWriteRepository<T, ID> writeRepository) {
        this.writeRepository = writeRepository;
    }

    @Override
    public void delete(T entity) throws RemoteException {
        writeRepository.delete(entity);
    }

    @Override
    public void deleteAll() throws RemoteException {
        writeRepository.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) throws RemoteException {
        writeRepository.deleteAll(entities);
    }

    @Override
    public void deleteById(ID id) throws RemoteException {
        writeRepository.deleteById(id);
    }

    @Override
    public <S extends T> S save(S entity) throws RemoteException {
        return writeRepository.save(entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) throws RemoteException {
        return writeRepository.saveAll(entities);
    }

    @Override
    public boolean addObserver(Observer<T, ID> observer) throws RemoteException {
        return writeRepository.addObserver(observer);
    }

    @Override
    public boolean addObservers(List<Observer<T, ID>> observers) throws RemoteException {
        return writeRepository.addObservers(observers);
    }

    @Override
    public List<Observer<T, ID>> getObservers() throws RemoteException {
        return writeRepository.getObservers();
    }

    @Override
    public boolean removeObserver(Observer<T, ID> observer) throws RemoteException {
        return writeRepository.removeObserver(observer);
    }

    @Override
    public boolean removeObservers(List<Observer<T, ID>> observers) throws RemoteException {
        return writeRepository.removeObservers(observers);
    }

    @Override
    public void close() throws Exception {
        writeRepository.close();
    }
}
