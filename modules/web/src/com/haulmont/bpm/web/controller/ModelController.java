/*
 * Copyright (c) 2015 Haulmont Technology Ltd. All Rights Reserved.
 *   Haulmont Technology proprietary and confidential.
 *   Use is subject to license terms.
 */

package com.haulmont.bpm.web.controller;

import com.google.common.base.Strings;
import com.haulmont.bpm.service.ModelService;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for working with model designed in modeler
 * @author gorbunkov
 * @version $Id$
 */

@Controller
@RequestMapping("/modeler/model")
public class ModelController {

    @Inject
    protected ModelService modelService;

    @RequestMapping(value = "/{actModelId}", method = RequestMethod.GET)
    @ResponseBody
    public String getModel(@PathVariable String actModelId,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        if (auth(request, response)) {
            String modelJson = modelService.getModelJson(actModelId);
            if (Strings.isNullOrEmpty(modelJson)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            return modelJson;
        }
        return null;
    }

    @RequestMapping(value = "/{actModelId}", method = RequestMethod.PUT)
    public void saveModel(@PathVariable String actModelId,
                          @RequestBody MultiValueMap<String, String> values,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        if (auth(request, response)) {
            String modelName = values.getFirst("name");
            String modelDescription = values.getFirst("description");
            String modelJsonStr = values.getFirst("json_xml");
            String modelSvgStr = values.getFirst("svg_xml");
            modelService.saveModel(actModelId, modelName, modelDescription, modelJsonStr, modelSvgStr);
        }
    }

    protected boolean auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserSession userSession = ControllerUtils.getUserSession(request);
        if (userSession == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        AppContext.setSecurityContext(new SecurityContext(userSession));
        return true;
    }
}