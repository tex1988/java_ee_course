package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.util.List;

public interface Observable<T extends BaseEntity<ID>, ID> {
    boolean addObserver(Observer<T, ID> observer);

    boolean addObservers(List<Observer<T, ID>> observerList);

    List<Observer<T, ID>> getObservers();

    boolean removeObserver(Observer<T, ID> observer);

    boolean removeObservers(List<Observer<T, ID>> observerList);
}
