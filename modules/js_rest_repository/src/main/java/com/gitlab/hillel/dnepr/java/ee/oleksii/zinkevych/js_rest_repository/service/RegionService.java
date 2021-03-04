package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.dto.RegionDto;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Region;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common.AbstractEntityService;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.exception.ServiceEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class RegionService extends AbstractEntityService<Region, Integer, RegionDto> {
    private final IndexedCrudRepository<Country, Integer> countryRepository;

    @Autowired
    public RegionService(CrudRepository<Region, Integer> repository,
                         ObjectMapper objectMapper,
                         IndexedCrudRepository<Country, Integer> countryRepository) {
        super(repository, objectMapper, Region.class, RegionDto.class);
        this.countryRepository = countryRepository;
    }

    @Override
    protected Region convertDtoToEntity(RegionDto regionDto) {
        Country country = countryRepository.findByIndex("name", regionDto.getCountryName()).
                orElseThrow(() -> new ServiceEntityException("Country with name: " + regionDto.getCountryName() + " not found")).get(0);
        Region result = new Region();
        result.setRegionId(regionDto.getId());
        result.setName(regionDto.getName());
        result.setCountry(country);
        return result;
    }

    @Override
    protected RegionDto convertEntityToDto(Region entity) {
        RegionDto result = new RegionDto();
        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setCountryName(entity.getCountry().getName());
        return result;
    }
}
