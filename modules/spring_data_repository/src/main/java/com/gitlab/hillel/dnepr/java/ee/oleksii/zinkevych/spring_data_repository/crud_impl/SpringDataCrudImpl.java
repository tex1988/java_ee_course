package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.crud_impl;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SpringDataCrudImpl implements com.gitlab.hillel.dnepr.java.ee.common.repository.CrudRepository<Country, Integer> {
    private CrudRepository<Country, Integer> countryCrud;

    @Autowired
    @Qualifier("countrySpringDataCrud")
    public void setCountryCrud(CrudRepository<Country, Integer> countryCrud) {
        this.countryCrud = countryCrud;
    }

    @Override
    public long count() {
        return countryCrud.count();
    }

    @Override
    public boolean existsById(Integer id) {
        return countryCrud.existsById(id);
    }

    @Override
    public Iterable<Country> findAll() {
        return countryCrud.findAll();
    }

    @Override
    public Iterable<Country> findAllById(Iterable<Integer> ids) {
        return countryCrud.findAllById(ids);
    }

    @Override
    public Optional<Country> findById(Integer id) {
        return countryCrud.findById(id);
    }

    @Override
    public void delete(Country entity) {
        countryCrud.delete(entity);
    }

    @Override
    public void deleteAll() {
        countryCrud.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends Country> entities) {
        countryCrud.deleteAll(entities);
    }

    @Override
    public void deleteById(Integer id) {
        countryCrud.deleteById(id);
    }

    @Override
    public <S extends Country> S save(S entity) {
        return countryCrud.save(entity);
    }

    @Override
    public <S extends Country> Iterable<S> saveAll(Iterable<S> entities) {
        return countryCrud.saveAll(entities);
    }

    @Override
    public void close() {

    }
}
