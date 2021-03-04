package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsIndexedReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Repository
public class RemoteCqrsIndexedReadRepositoryImpl<T extends BaseEntity<ID>, ID> implements RemoteCqrsIndexedReadRepository<T, ID> {

    private CqrsIndexedReadRepository<T, ID> readRepository;

    @Autowired
    public RemoteCqrsIndexedReadRepositoryImpl(CqrsIndexedReadRepository<T, ID> readRepository) {
        this.readRepository = readRepository;
    }

    @Override
    public boolean hasIndex(String key) throws RemoteException {
        return readRepository.hasIndex(key);
    }

    @Override
    public void addIndex(String key) throws RemoteException {
        readRepository.addIndex(key);
    }

    @Override
    public void removeIndex(String key) throws RemoteException {
        readRepository.removeIndex(key);
    }

    @Override
    public void addIndexes(Set<String> keySet) throws RemoteException {
        readRepository.addIndexes(keySet);
    }

    @Override
    public void removeIndexes(Set<String> keySet) throws RemoteException {
        readRepository.removeIndexes(keySet);
    }

    @Override
    public List<T> findByIndex(String key, Object value) throws RemoteException {
        return readRepository.findByIndex(key, value).get();
    }

    @Override
    public long count() throws RemoteException {
        return readRepository.count();
    }

    @Override
    public boolean existsById(ID id) throws RemoteException {
        return readRepository.existsById(id);
    }

    @Override
    public Iterable<T> findAll() throws RemoteException {
        return readRepository.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) throws RemoteException {
        return readRepository.findAllById(ids);
    }

    @Override
    public T findById(ID id) throws RemoteException {
        return readRepository.findById(id).get();
    }

    @Override
    public Future<?> apply(Observer.Action action, Iterable<T> entityList) throws RemoteException {
        return readRepository.apply(action, entityList);
    }

    @Override
    public void close() throws Exception {
        readRepository.close();
    }
}
