package com.gitlab.hillel.dnepr.java.ee.common.repository;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SyncFuture<T> implements Future<T> {
    private final T result;

    public SyncFuture() {
        this(null);
    }

    public SyncFuture(T result) {
        this.result = result;
    }

    public static <T> SyncFuture<T> of(T result) {
        return new SyncFuture<>(result);
    }

    public static <T> SyncFuture<T> empty() {
        return of(null);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return true;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() {
        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        return get();
    }
}
