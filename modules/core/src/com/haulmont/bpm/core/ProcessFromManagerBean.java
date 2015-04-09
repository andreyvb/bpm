/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.bpm.core;

import com.haulmont.bpm.entity.ProcDefinition;
import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.bpm.form.ProcFormDefinition;
import com.haulmont.bpm.form.ProcFormParam;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

/**
 * @author gorbunkov
 * @version $Id$
 */
@ManagedBean(ProcessFormManager.NAME)
public class ProcessFromManagerBean implements ProcessFormManager {

    @Inject
    protected ExtensionElementsManager extensionElementsManager;

    @Inject
    protected TaskService taskService;

    @Override
    public Map<String, ProcFormDefinition> getOutcomesWithForms(ProcTask procTask) {
        Map<String, ProcFormDefinition> result = new HashMap<>();

        Task task = taskService.createTaskQuery().taskId(procTask.getActTaskId()).singleResult();

        Map<String, List<ExtensionElement>> extensionElements = extensionElementsManager.getTaskExtensionElements(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        List<ExtensionElement> outcomesElements = extensionElements.get("outcomes");
        if (outcomesElements != null) {
            ExtensionElement outcomesElement = outcomesElements.get(0);
            List<ExtensionElement> outcomeElementsList = outcomesElement.getChildElements().get("outcome");
            for (ExtensionElement outcomeElement : outcomeElementsList) {
                ProcFormDefinition formDefinition = null;
                List<ExtensionElement> formElements = outcomeElement.getChildElements().get("form");
                if (formElements != null && !formElements.isEmpty()) {
                    formDefinition = extractProcFormDefinition(formElements.get(0), procTask.getActProcessDefinitionId());
                }
                result.put(outcomeElement.getAttributeValue(null, "name"), formDefinition);
            }
        }
        return result;
    }

    @Override
    @Nullable
    public ProcFormDefinition getStartForm(ProcDefinition procDefinition) {
        Map<String, List<ExtensionElement>> extensionElements = extensionElementsManager.getStartExtensionElements(procDefinition.getActId());
        List<ExtensionElement> formElements = extensionElements.get("form");
        if (formElements != null && !formElements.isEmpty()) {
            return extractProcFormDefinition(formElements.get(0), procDefinition.getActId());
        }
        return null;
    }

    protected ProcFormDefinition extractProcFormDefinition(ExtensionElement formElement, String actProcessDefinitionId) {
        ProcFormDefinition procFormDefinition = new ProcFormDefinition();
        procFormDefinition.setName(formElement.getAttributeValue(null, "name"));
        procFormDefinition.setCaption(formElement.getAttributeValue(null, "caption"));
        procFormDefinition.setActProcessDefinitionId(actProcessDefinitionId);

        Map<String, ProcFormParam> params = new LinkedHashMap<>();
        List<ExtensionElement> paramElements = formElement.getChildElements().get("param");
        if (paramElements != null) {
            for (ExtensionElement paramElement : paramElements) {
                ProcFormParam param = extractProcFormParam(paramElement, procFormDefinition);
                params.put(param.getName(), param);
            }
        }
        procFormDefinition.setParams(params);

        return procFormDefinition;
    }

    protected ProcFormParam extractProcFormParam(ExtensionElement paramElement, ProcFormDefinition formDefinition) {
        ProcFormParam procFormParam = new ProcFormParam();
        procFormParam.setName(paramElement.getAttributeValue(null, "name"));
        procFormParam.setCaption(paramElement.getAttributeValue(null, "caption"));
        procFormParam.setTypeName(paramElement.getAttributeValue(null, "type"));
        String editable = paramElement.getAttributeValue(null, "editable");
        procFormParam.setEditable(editable == null || Boolean.parseBoolean(editable));
        procFormParam.setRequired(Boolean.parseBoolean(paramElement.getAttributeValue(null, "required")));
        procFormParam.setEntityName(paramElement.getAttributeValue(null, "entityName"));
        procFormParam.setEntityLookupScreen(paramElement.getAttributeValue(null, "entityLookupScreen"));
        if ("enum".equals(procFormParam.getTypeName())) {
            procFormParam.setEnumItems(extractEnumItems(paramElement));
        }
        String value = paramElement.getAttributeValue(null, "value");
        if (value == null)
            value = paramElement.getElementText();
        procFormParam.setValue(value);
        procFormParam.setFormDefinition(formDefinition);
        return procFormParam;
    }

    protected Map<String, Object> extractEnumItems(ExtensionElement paramElement) {
        Map<String, Object> enumItems = new LinkedHashMap<>();
        List<ExtensionElement> enumItemElements = paramElement.getChildElements().get("enumItem");
        if (enumItemElements != null) {
            for (ExtensionElement enumItemElement : enumItemElements) {
                String value = enumItemElement.getAttributeValue(null, "value");
                String caption = enumItemElement.getAttributeValue(null, "caption");
                enumItems.put(caption, value);
            }
        }
        return enumItems;
    }
}
