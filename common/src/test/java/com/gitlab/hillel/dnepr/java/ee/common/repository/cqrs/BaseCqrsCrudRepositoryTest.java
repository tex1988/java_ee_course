package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseCqrsCrudRepositoryTest {
    @Mock
    private CqrsReadRepository<TestUserEntity, String> readRepository;
    @Mock
    private CqrsWriteRepository<TestUserEntity, String> writeRepository;

    @BeforeEach
    void init() {
    }

    @Test
    void testCount() {
        final CqrsCrudRepository<TestUserEntity, String> crudRepository =
                new BaseCqrsCrudRepositoryImpl<>(readRepository, writeRepository);

        when(readRepository.count()).thenReturn(42L);
        assertEquals(42L, crudRepository.count());
        verify(readRepository, times(1)).count();
        verify(writeRepository, times(1)).addObserver(readRepository);
    }

    @Test
    void testAddObserver() {
        final CqrsCrudRepository<TestUserEntity, String> crudRepository =
                new BaseCqrsCrudRepositoryImpl<>(readRepository, writeRepository);

        when(writeRepository.addObserver(readRepository)).thenReturn(true);
        assertTrue(crudRepository.addObserver(readRepository));
        assertTrue(crudRepository.addObserver(readRepository));
        verify(writeRepository, times(3)).addObserver(readRepository);
    }

    @Test
    void testAddObserverList() {
        final CqrsCrudRepository<TestUserEntity, String> crudRepository =
                new BaseCqrsCrudRepositoryImpl<>(readRepository, writeRepository);

        verify(writeRepository, times(1)).addObserver(readRepository);

        final List<Observer<TestUserEntity, String>> observerList = new ArrayList<>();
        observerList.add(readRepository);
        observerList.add(readRepository);

        when(writeRepository.addObservers(observerList)).thenReturn(true);
        assertTrue(crudRepository.addObservers(observerList));
        verify(writeRepository, times(1)).addObservers(observerList);
    }

    @Test
    void testGetObservers() {
        final CqrsCrudRepository<TestUserEntity, String> crudRepository =
                new BaseCqrsCrudRepositoryImpl<>(readRepository, writeRepository);

        final List<Observer<TestUserEntity, String>> observerList = List.of(this.readRepository, readRepository);
        when(writeRepository.getObservers()).thenReturn(observerList);
        assertSame(observerList, crudRepository.getObservers());
        verify(writeRepository, times(1)).getObservers();
    }

    @Test
    void testRemoveObserver() {
        final CqrsCrudRepository<TestUserEntity, String> crudRepository =
                new BaseCqrsCrudRepositoryImpl<>(readRepository, writeRepository);

        when(writeRepository.removeObserver(readRepository)).thenReturn(true);
        assertTrue(crudRepository.removeObserver(readRepository));
        verify(writeRepository, times(1)).removeObserver(readRepository);
    }

    @Test
    void testRemoveObservers() {
        final CqrsCrudRepository<TestUserEntity, String> crudRepository =
                new BaseCqrsCrudRepositoryImpl<>(readRepository, writeRepository);

        final List<Observer<TestUserEntity, String>> observerList = List.of(this.readRepository, readRepository);
        when(writeRepository.removeObservers(observerList)).thenReturn(true);
        assertTrue(crudRepository.removeObservers(observerList));
        verify(writeRepository, times(1)).removeObservers(observerList);
    }

    private static class BaseCqrsCrudRepositoryImpl<T extends BaseEntity<ID>, ID> extends BaseCqrsCrudRepository<T, ID> {
        protected BaseCqrsCrudRepositoryImpl(
                CqrsReadRepository<T, ID> readRepository,
                CqrsWriteRepository<T, ID> writeRepository) {
            super(readRepository, writeRepository);
        }
    }
}