package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public abstract class BaseCqrsCrudRepository<T extends BaseEntity<ID>, ID>
        implements CqrsReadRepository<T, ID>, CqrsWriteRepository<T, ID>, CqrsCrudRepository<T, ID> {
    protected final CqrsReadRepository<T, ID> readRepository;
    protected final CqrsWriteRepository<T, ID> writeRepository;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    protected BaseCqrsCrudRepository(
            CqrsReadRepository<T, ID> readRepository,
            CqrsWriteRepository<T, ID> writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;

        this.writeRepository.addObserver(this.readRepository);
    }

    @Override
    public long count() {
        final Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            return readRepository.count();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean existsById(ID id) {
        final Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            return readRepository.existsById(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterable<T> findAll() {
        final Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            return readRepository.findAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        final Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            return readRepository.findAllById(ids);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        final Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            return readRepository.findById(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(T entity) {
        final Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            writeRepository.delete(entity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteAll() {
        final Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            writeRepository.deleteAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        final Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            writeRepository.deleteAll(entities);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(ID id) {
        final Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            writeRepository.deleteById(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <S extends T> S save(S entity) {
        final Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            return writeRepository.save(entity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        final Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            return writeRepository.saveAll(entities);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addObserver(Observer<T, ID> observer) {
        return writeRepository.addObserver(observer);
    }

    @Override
    public boolean addObservers(List<Observer<T, ID>> observers) {
        return writeRepository.addObservers(observers);
    }

    @Override
    public List<Observer<T, ID>> getObservers() {
        return writeRepository.getObservers();
    }

    @Override
    public boolean removeObserver(Observer<T, ID> observer) {
        return writeRepository.removeObserver(observer);
    }

    @Override
    public boolean removeObservers(List<Observer<T, ID>> observers) {
        return writeRepository.removeObservers(observers);
    }

    @Override
    public Future<?> apply(Action action, Iterable<T> entityList) {
        Lock lock;
        if (action.isMutable()) {
            lock = readWriteLock.writeLock();
        } else {
            lock = readWriteLock.readLock();
        }
        try {
            lock.lock();
            return readRepository.apply(action, entityList);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            readRepository.close();
            writeRepository.close();
        } finally {
            lock.unlock();
        }
    }
}
