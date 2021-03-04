package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper.common.Mapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class IdMapper<ID> implements Mapper<ID> {
    private final Class<ID> idClass;
    private final Map<String, Function<String, ID>> idTypeMappers = new HashMap<>();

    public IdMapper(Class<ID> idClass) {
        this.idClass = idClass;
        addIdTypeMappers();
    }

    @SuppressWarnings("unchecked")
    private void addIdTypeMappers() {
        idTypeMappers.put("String", (id) -> (ID) id);
        idTypeMappers.put("int", (id) -> (ID) Integer.valueOf(id));
        idTypeMappers.put("Integer", (id) -> (ID) Integer.valueOf(id));
        idTypeMappers.put("Long", (id) -> (ID) Long.valueOf(id));
        idTypeMappers.put("long", (id) -> (ID) Long.valueOf(id));
        idTypeMappers.put("Double", (id) -> (ID) Double.valueOf(id));
        idTypeMappers.put("double", (id) -> (ID) Double.valueOf(id));
        idTypeMappers.put("Float", (id) -> (ID) Float.valueOf(id));
        idTypeMappers.put("float", (id) -> (ID) Float.valueOf(id));
    }

    public ID map(String id) throws MapperException {
        String typeName = idClass.getSimpleName();
        if (idTypeMappers.containsKey(typeName)) {
            return idTypeMappers.get(typeName).apply(id);
        } else {
            MapperException exception = new MapperException(new IllegalStateException("Unsupported id type:" + typeName));
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }
}
