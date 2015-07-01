/*
 * Copyright (c) 2015 Haulmont Technology Ltd. All Rights Reserved.
 *   Haulmont Technology proprietary and confidential.
 *   Use is subject to license terms.
 */

package com.haulmont.bpm.service;

import com.haulmont.bpm.entity.ProcInstance;

import java.util.Map;

/**
 * @author gorbunkov
 * @version $Id$
 */
public interface ProcessVariablesService {
    String NAME = "bpm_ProcessVariablesService";

    Map<String, Object> getVariables(ProcInstance procInstance);

    void setVariables(String executionId, Map<String, Object> variables);

    void setVariables(ProcInstance procInstance, Map<String, Object> variables);

    void setVariable(ProcInstance procInstance, String variableName, Object variableValue);

    Map<String, Object> getVariables(String executionId);

    Object getVariable(ProcInstance procInstance, String variableName);

    void setVariable(String executionId, String variableName, Object variableValue);

    Object getVariable(String executionId, String variableName);
}