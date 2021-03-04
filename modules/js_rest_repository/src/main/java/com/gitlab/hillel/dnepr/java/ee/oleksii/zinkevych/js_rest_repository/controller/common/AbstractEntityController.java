package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.controller.common;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper.common.Mapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.exception.ServiceEntityException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common.EntityService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractEntityController<ID> extends HttpServlet {
    private final Map<String, Function<String, ID>> idTypeMappers = new HashMap<>();
    protected EntityService<ID> entityService;
    protected Mapper<ID> idMapper;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        String respString;
        String stringId = req.getParameter("id");
        int statusCode;
        try {
            if (stringId == null || stringId.equals("")) {
                resp.setContentType("text/html");
                statusCode = 400;
                respString = "Invalid request";
            } else {
                ID id = idMapper.map(stringId);
                resp.setContentType("application/json");
                respString = entityService.getEntity(id);
                statusCode = 200;
            }
        } catch (ServiceEntityException e) {
            resp.setContentType("text/html");
            statusCode = 404;
            respString = e.getMessage();
        } catch (Throwable e) {
            resp.setContentType("text/html");
            statusCode = 400;
            LOGGER.error("Exception: ", e);
            respString = "Server error";
        }
        resp.setStatus(statusCode);
        writer.println(respString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        String respString;
        int statusCode;
        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            entityService.saveEntity(body);
            statusCode = 201;
            respString = "Entity saved successfully";
        } catch (ServiceEntityException e) {
            statusCode = 406;
            respString = e.getMessage();
        } catch (Throwable e) {
            statusCode = 500;
            LOGGER.error("Exception: ", e);
            respString = "Server error";
        }
        resp.setStatus(statusCode);
        writer.println(respString);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        String respString;
        int statusCode;
        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            entityService.updateEntity(body);
            statusCode = 201;
            respString = "User updated successfully";
        } catch (ServiceEntityException e) {
            statusCode = 404;
            respString = e.getMessage();
        } catch (Throwable e) {
            statusCode = 500;
            LOGGER.error("Exception: ", e);
            respString = "Server error";
        }
        resp.setStatus(statusCode);
        writer.println(respString);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        String respString;
        String stringId = req.getParameter("id");
        int statusCode;
        try {
            if (stringId == null || stringId.equals("")) {
                statusCode = 400;
                respString = "Invalid request";
            } else {
                ID id = idMapper.map(stringId);
                respString = "Entity deleted successfully";
                entityService.deleteEntity(id);
                statusCode = 200;
            }
        } catch (ServiceEntityException e) {
            statusCode = 404;
            respString = e.getMessage();
        } catch (Throwable e) {
            statusCode = 500;
            LOGGER.error("Exception: ", e);
            respString = "Server error";
        }
        resp.setStatus(statusCode);
        writer.println(respString);
    }
}
