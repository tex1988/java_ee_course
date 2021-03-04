package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.dto.CityDto;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.City;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Region;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common.AbstractEntityService;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.exception.ServiceEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class CityService extends AbstractEntityService<City, Integer, CityDto> {
    private final IndexedCrudRepository<Country, Integer> countryRepository;
    private final IndexedCrudRepository<Region, Integer> regionRepository;

    @Autowired
    public CityService(CrudRepository<City, Integer> repository,
                       ObjectMapper objectMapper,
                       IndexedCrudRepository<Country, Integer> countryRepository,
                       IndexedCrudRepository<Region, Integer> regionRepository) {
        super(repository, objectMapper, City.class, CityDto.class);
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    protected City convertDtoToEntity(CityDto cityDto) {
        Region region = regionRepository.findByIndex("name", cityDto.getRegionName()).
                orElseThrow(() -> new ServiceEntityException("Region with name: " + cityDto.getRegionName() + " not found")).get(0);
        Country country = countryRepository.findByIndex("name", cityDto.getCountryName()).
                orElseThrow(() -> new ServiceEntityException("Country with name: " + cityDto.getRegionName() + " not found")).get(0);
        City result = new City();
        result.setCityId(cityDto.getId());
        result.setName(cityDto.getName());
        result.setCountry(country);
        result.setRegion(region);
        return result;
    }

    @Override
    protected CityDto convertEntityToDto(City entity) {
        CityDto result = new CityDto();
        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setRegionName(entity.getRegion().getName());
        result.setCountryName(entity.getCountry().getName());
        return result;
    }
}
