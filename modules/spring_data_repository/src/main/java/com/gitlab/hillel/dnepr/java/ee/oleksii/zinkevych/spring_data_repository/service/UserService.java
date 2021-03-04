package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.service;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Address;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.City;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Region;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository.CitySpringDataCrud;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository.RegionSpringDataCrud;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository.UserSpringDataCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private CitySpringDataCrud cityCrud;
    private RegionSpringDataCrud regionCrud;
    private CrudRepository<Country, Integer> countryCrud;
    private CrudRepository<Address, Integer> addressCrud;
    private UserSpringDataCrud userCrud;

    public List<City> getCities() {
        List<City> cities = new ArrayList<>();
        cityCrud.findAll().forEach(cities::add);
        return cities;
    }

    public List<Address> getAddresses() {
        List<Address> addresses = new ArrayList<>();
        addressCrud.findAll().forEach(addresses::add);
        return addresses;
    }

    public List<Region> getFirstTenRegionsSortedByFieldInDescendingOrder(String firstFieldName, String secondFieldName) {
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(firstFieldName).and(Sort.by(secondFieldName).descending()));
        Page<Region> regionPage = regionCrud.findAll(pageable);
        return regionPage.getContent();
    }

    public List<User> getUsersByCityName(String cityName) {
        return userCrud.findUsersByAddressCityName(cityName);
    }


    public List<City> getCitiesByCountryId(int countryId) {
        return cityCrud.findCitiesByCountryId(countryId);
    }

    @Autowired
    public void setCitySpringDataCrud(CitySpringDataCrud cityCrud) {
        this.cityCrud = cityCrud;
    }

    @Autowired
    public void setRegionSpringDataCrud(RegionSpringDataCrud regionCrud) {
        this.regionCrud = regionCrud;
    }

    @Autowired
    @Qualifier("countrySpringDataCrud")
    public void setCountryCrud(CrudRepository<Country, Integer> countryCrud) {
        this.countryCrud = countryCrud;
    }

    @Autowired
    public void setUserCrud(UserSpringDataCrud userCrud) {
        this.userCrud = userCrud;
    }

    @Autowired
    @Qualifier("addressSpringDataCrud")
    public void setAddressCrud(CrudRepository<Address, Integer> addressCrud) {
        this.addressCrud = addressCrud;
    }
}
