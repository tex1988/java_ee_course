package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public abstract class BaseCqrsWriteRepository<T extends BaseEntity<ID>, ID>
        implements CqrsWriteRepository<T, ID> {
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private final ExecutorService executorService;
    private final List<Observer<T, ID>> observerList = Collections.synchronizedList(new ArrayList<>());

    public BaseCqrsWriteRepository() {
        executorService = Executors.newFixedThreadPool(
                THREAD_POOL_SIZE,
                runnable -> {
                    final Thread thread = new Thread(runnable);
                    thread.setDaemon(true);
                    thread.setName("BaseCqrsWriteRepository-thread");
                    LOGGER.info("New thread is created.");
                    return thread;
                });
        LOGGER.info("Thread-pool is created. Thread count: {}", THREAD_POOL_SIZE);
    }

    @Override
    public boolean addObserver(Observer<T, ID> observer) {
        return observerList.add(observer);
    }

    @Override
    public boolean addObservers(List<Observer<T, ID>> observerList) {
        return this.observerList.addAll(observerList);
    }

    @Override
    public List<Observer<T, ID>> getObservers() {
        return Collections.unmodifiableList(observerList);
    }

    @Override
    public boolean removeObserver(Observer<T, ID> observer) {
        return this.observerList.remove(observer);
    }

    @Override
    public boolean removeObservers(List<Observer<T, ID>> observerList) {
        return this.observerList.removeAll(observerList);
    }

    @Override
    public void close() {
        executorService.shutdown();
        LOGGER.info("Write repository executor-service is stopped.");
    }

    protected final Future<?> onChange(Observer.Action action, Iterable<T> entityList) {
        return executorService.submit(() -> {
            for (Observer<T, ID> observer : observerList) {
                try {
                    observer.apply(action, entityList).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new UncheckedRepositoryException("Failed to wait action process");
                }
            }
        });
    }
}
