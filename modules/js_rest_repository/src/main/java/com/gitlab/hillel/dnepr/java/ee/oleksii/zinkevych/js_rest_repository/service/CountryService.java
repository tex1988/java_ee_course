package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.dto.CountryDto;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common.AbstractEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class CountryService extends AbstractEntityService<Country, Integer, CountryDto> {
    @Autowired
    public CountryService(CrudRepository<Country, Integer> repository,
                          ObjectMapper objectMapper) {
        super(repository, objectMapper, Country.class, CountryDto.class);
    }

    @Override
    protected Country convertDtoToEntity(CountryDto countryDto) {
        Country result = new Country();
        result.setCountryId(countryDto.getId());
        result.setName(countryDto.getName());
        return result;
    }

    @Override
    protected CountryDto convertEntityToDto(Country entity) {
        CountryDto result = new CountryDto();
        result.setId(entity.getId());
        result.setName(entity.getName());
        return result;
    }
}
