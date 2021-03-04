package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.remote_wrapper;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsWriteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

@Slf4j
@Repository
public class RemoteCqrsWriteRepositoryWrapper<T extends BaseEntity<ID>, ID> implements CqrsWriteRepository<T, ID>, Serializable {
    RemoteCqrsWriteRepository<T, ID> remoteWriteRepository;

    public RemoteCqrsWriteRepositoryWrapper(RemoteCqrsWriteRepository<T, ID> remoteWriteRepository) {
        this.remoteWriteRepository = remoteWriteRepository;
    }

    @Override
    public void delete(T entity) {
        try {
            remoteWriteRepository.delete(entity);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void deleteAll() {
        try {
            remoteWriteRepository.deleteAll();
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        try {
            remoteWriteRepository.deleteAll(entities);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void deleteById(ID id) {
        try {
            remoteWriteRepository.deleteById(id);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public <S extends T> S save(S entity) {
        try {
            return remoteWriteRepository.save(entity);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        try {
            return remoteWriteRepository.saveAll(entities);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public boolean addObserver(Observer<T, ID> observer) {
        try {
            return remoteWriteRepository.addObserver(observer);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public boolean addObservers(List<Observer<T, ID>> observers) {
        try {
            return remoteWriteRepository.addObservers(observers);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public List<Observer<T, ID>> getObservers() {
        try {
            return remoteWriteRepository.getObservers();
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public boolean removeObserver(Observer<T, ID> observer) {
        try {
            return remoteWriteRepository.removeObserver(observer);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public boolean removeObservers(List<Observer<T, ID>> observers) {
        try {
            return remoteWriteRepository.removeObservers(observers);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void close() throws Exception {
        try {
            remoteWriteRepository.close();
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }
}
