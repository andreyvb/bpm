/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.bpm.web.controller;

import com.haulmont.bpm.form.ProcFormDefinition;
import com.haulmont.bpm.gui.app.ProcessFormRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller("bpm_FormController")
@RequestMapping("/modeler/form")
public class FormController {

    @Inject
    protected ProcessFormRepository processFormRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ProcFormDefinition> getAllForms(HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {
        if (BpmControllerUtils.auth(request, response)) {
            return processFormRepository.getForms();
        }
        return null;
    }
}