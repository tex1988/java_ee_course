package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.dto.UserDto;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Address;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository.AddressRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository.UserRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common.AbstractEntityService;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.exception.ServiceEntityException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.validator.AddressValidationException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService extends AbstractEntityService<User, String, UserDto> {
    private final AddressRepository addressRepository;
    private final Validator addressValidator;

    @Autowired
    public UserService(UserRepository repository,
                       AddressRepository addressRepository,
                       ObjectMapper objectMapper,
                       Validator addressValidator) {
        super(repository, objectMapper, User.class, UserDto.class);
        this.addressRepository = addressRepository;
        this.addressValidator = addressValidator;
    }

    @Override
    protected User convertDtoToEntity(UserDto userDto) {
        User result = new User();
        Address address = getAddress(userDto.getAddress());
        result.setId(userDto.getId());
        result.setFName(userDto.getFName());
        result.setLName(userDto.getLName());
        result.setAge(userDto.getAge());
        result.setAddress(address);
        return result;
    }

    @Override
    protected UserDto convertEntityToDto(User user) {
        UserDto result = new UserDto();
        String country = user.getAddress().getCity().getCountry().getName();
        String region = user.getAddress().getCity().getRegion().getName();
        String city = user.getAddress().getCity().getName();
        String street = user.getAddress().getStreet();
        String address = country + ", " + region + ", " + city + ", " + street;
        result.setId(user.getId());
        result.setFName(user.getFName());
        result.setLName(user.getLName());
        result.setAge(user.getAge());
        result.setAddress(address);
        return result;
    }

    private Address getAddress(String stringAddress) {
        try {
            addressValidator.validate(stringAddress);
        } catch (AddressValidationException e) {
            throw new ServiceEntityException(e.getMessage());
        }
        Address result;
        String[] addressArr = stringAddress.split(", ");
        String city = addressArr[2];
        String street = "";
        for (int i = 3; i < addressArr.length; i++) {
            street += addressArr[i] + ", ";
        }
        street = street.substring(0, street.length() - 2);
        List<Address> addressList = addressRepository.findAddressByCityNameAndStreet(city, street);
        if (addressList.isEmpty()) {
            throw new ServiceEntityException("Address: " + stringAddress + " not found");
        } else {
            result = addressList.get(0);
        }
        return result;
    }
}