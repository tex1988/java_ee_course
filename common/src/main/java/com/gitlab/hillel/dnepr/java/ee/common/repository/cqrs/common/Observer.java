package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.util.concurrent.Future;

public interface Observer<T extends BaseEntity<ID>, ID> {
    Future<?> apply(Action action, Iterable<T> entityList);

    enum Action {
        CREATE(true),
        READ(),
        UPDATE(true),
        CREATE_OR_UPDATE(true),
        DELETE(true),
        DELETE_ALL(true),
        UNDEFINED();

        private final boolean isMutable;

        Action() {
            this(false);
        }

        Action(boolean isMutable) {
            this.isMutable = isMutable;
        }

        public boolean isMutable() {
            return isMutable;
        }
    }
}
