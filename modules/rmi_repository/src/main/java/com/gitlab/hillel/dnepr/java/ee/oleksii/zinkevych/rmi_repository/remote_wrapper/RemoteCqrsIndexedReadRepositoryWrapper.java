package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.remote_wrapper;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsIndexedReadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

@Slf4j
@Repository
public class RemoteCqrsIndexedReadRepositoryWrapper<T extends BaseEntity<ID>, ID> implements CqrsIndexedReadRepository<T, ID>, Serializable {
    RemoteCqrsIndexedReadRepository<T, ID> remoteReadRepository;

    public RemoteCqrsIndexedReadRepositoryWrapper(RemoteCqrsIndexedReadRepository<T, ID> remoteReadRepository) {
        this.remoteReadRepository = remoteReadRepository;
    }

    @Override
    public boolean hasIndex(String key) {
        try {
            return remoteReadRepository.hasIndex(key);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void addIndex(String key) {
        try {
            remoteReadRepository.addIndex(key);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void removeIndex(String key) {
        try {
            remoteReadRepository.removeIndex(key);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void addIndexes(Set<String> keySet) {
        try {
            remoteReadRepository.addIndexes(keySet);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void removeIndexes(Set<String> keySet) {
        try {
            remoteReadRepository.removeIndexes(keySet);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public Optional<List<T>> findByIndex(String key, Object value) {
        try {
            List<T> entities = remoteReadRepository.findByIndex(key, value);
            if(entities.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(entities);
            }
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public long count() {
        try {
            return remoteReadRepository.count();
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public boolean existsById(ID id) {
        try {
            return remoteReadRepository.existsById(id);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public Iterable<T> findAll() {
        try {
            return remoteReadRepository.findAll();
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        try {
            return remoteReadRepository.findAllById(ids);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            T entity = remoteReadRepository.findById(id);
            if(entity==null) {
                return Optional.empty();
            } else {
                return Optional.of(entity);
            }
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public Future<?> apply(Action action, Iterable<T> entityList) {
        try {
            return remoteReadRepository.apply(action, entityList);
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    @Override
    public void close() throws Exception {
        try {
            remoteReadRepository.close();
        } catch (RemoteException e) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }
}
