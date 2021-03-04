package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.controller;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.controller.common.AbstractEntityController;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper.common.Mapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.SimpleUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;

@Controller
@WebServlet("/simpleuser")
public class SimpleUserController extends AbstractEntityController<String> {

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        super.entityService = context.getBean(SimpleUserService.class);
        super.idMapper = (Mapper<String>) context.getBean("stringIdMapper");
    }
}