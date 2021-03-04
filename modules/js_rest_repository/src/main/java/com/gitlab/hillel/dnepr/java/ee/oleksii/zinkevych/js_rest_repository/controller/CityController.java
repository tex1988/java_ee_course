package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.controller;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.controller.common.AbstractEntityController;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper.common.Mapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

@Controller
public class CityController extends AbstractEntityController<Integer> {

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        super.entityService = context.getBean(CityService.class);
        super.idMapper = (Mapper<Integer>) context.getBean("intIdMapper");
    }
}
