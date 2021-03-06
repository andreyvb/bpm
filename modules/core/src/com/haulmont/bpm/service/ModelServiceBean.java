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

package com.haulmont.bpm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.haulmont.bpm.entity.ProcModel;
import com.haulmont.bpm.exception.BpmException;
import com.haulmont.bpm.rest.RestModel;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service(ModelService.NAME)
public class ModelServiceBean implements ModelService {

    private final Logger log = LoggerFactory.getLogger(ModelServiceBean.class);

    @Inject
    protected RepositoryService repositoryService;

    @Inject
    protected Persistence persistence;

    @Override
    public RestModel getModelJson(String actModelId) {
        Model model = repositoryService.getModel(actModelId);
        if (model != null) {
            try {
                String modelJson = new String(repositoryService.getModelEditorSource(model.getId()), "utf-8");
                return new RestModel(model.getId(), model.getName(), modelJson);
            } catch (Exception e) {
                log.error("Error creating model JSON", e);
                throw new BpmException("Error creating model JSON", e);
            }
        }
        return null;
    }

    @Override
    public void updateModel(String actModelId, String modelName, String modelDescription,
                          String modelJsonStr, String modelSvgStr) {
        Model model = repositoryService.getModel(actModelId);

        JSONObject modelJsonObject = new JSONObject(model.getMetaInfo());

        modelJsonObject.put("name", modelName);
        modelJsonObject.put("description", modelDescription);
        model.setMetaInfo(modelJsonObject.toString());
        model.setName(modelName);

        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(), modelJsonStr.getBytes(StandardCharsets.UTF_8));
        //todo gorbunkov store svg
//            InputStream svgStream = new ByteArrayInputStream(modelSvgStr.getBytes("utf-8"));
//            TranscoderInput input = new TranscoderInput(svgStream);
//
//            PNGTranscoder transcoder = new PNGTranscoder();
//            // Setup output
//            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//            TranscoderOutput output = new TranscoderOutput(outStream);
//
//            // Do the transformation
//            transcoder.transcode(input, output);
//            final byte[] result = outStream.toByteArray();
//            repositoryService.addModelEditorSourceExtra(model.getId(), result);
//            outStream.close();

        Transaction tx = persistence.getTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<ProcModel> query = em.createQuery("select m from bpm$ProcModel m where m.actModelId = :actModelId", ProcModel.class);
            query.setParameter("actModelId", actModelId);
            ProcModel procModel = query.getFirstResult();
            if (procModel != null) {
                procModel.setName(modelName);
                procModel.setDescription(modelDescription);
            }
            tx.commit();
        } finally {
            tx.end();
        }
    }

    @Override
    public void updateModel(String actModelId, String modelName, String modelDescription) {
        Model model = repositoryService.getModel(actModelId);
        if (model == null) return;
        JSONObject modelJsonObject = new JSONObject(model.getMetaInfo());
        modelJsonObject.put("name", modelName);
        modelJsonObject.put("description", modelDescription);
        model.setMetaInfo(modelJsonObject.toString());
        model.setName(modelName);
        repositoryService.saveModel(model);
    }

    @Override
    @Transactional
    public String createModel(String name) {
        Model model = repositoryService.newModel();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put("name", name);
        modelObjectNode.put("revision", 1);
        modelObjectNode.put("description", "");
        model.setMetaInfo(modelObjectNode.toString());
        model.setName(name);

        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);

        ObjectNode propertiesNode = objectMapper.createObjectNode();
        propertiesNode.put("process_id", toCamelCase(name));
        propertiesNode.put("name", name);

        fillEventListeners(objectMapper, propertiesNode);

        editorNode.put("properties", propertiesNode);

        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes(StandardCharsets.UTF_8));

        return model.getId();
    }

    protected void fillEventListeners(ObjectMapper objectMapper, ObjectNode propertiesNode) {
        ObjectNode eventListenerNode = objectMapper.createObjectNode();
        eventListenerNode.put("className", "com.haulmont.bpm.core.engine.listener.BpmActivitiListener");
        eventListenerNode.put("implementation", "com.haulmont.bpm.core.engine.listener.BpmActivitiListener");
        eventListenerNode.put("event", getListenerEventTypesString());
        ArrayNode eventListenersArray = objectMapper.createArrayNode();
        eventListenersArray.add(eventListenerNode);
        ObjectNode eventListenersNode = objectMapper.createObjectNode();
        eventListenersNode.put("eventListeners", eventListenersArray);
        propertiesNode.put("eventlisteners", eventListenersNode);

        ArrayNode eventsArrayNode = objectMapper.createArrayNode();
        for (ActivitiEventType eventType : getActivitiEventTypes()) {
            ObjectNode eventNode = objectMapper.createObjectNode();
            eventNode.put("event", eventType.toString());
            eventsArrayNode.add(eventNode);
        }
        eventListenerNode.put("events", eventsArrayNode);
    }

    protected String toCamelCase(String sentence) {
        StringBuilder sb = new StringBuilder();
        String[] words = sentence.split("\\s+");
        sb.append(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            sb.append(StringUtils.capitalize(words[i].toLowerCase()));
        }
        return sb.toString();
    }

    protected String getListenerEventTypesString() {
        List<ActivitiEventType> activitiEventTypes = getActivitiEventTypes();

        return Joiner.on(", ").join(activitiEventTypes);
    }

    protected List<ActivitiEventType> getActivitiEventTypes() {
        return Arrays.asList(
                ActivitiEventType.TASK_CREATED,
                ActivitiEventType.TASK_ASSIGNED,
                ActivitiEventType.PROCESS_COMPLETED,
                ActivitiEventType.TIMER_FIRED,
                ActivitiEventType.ACTIVITY_CANCELLED,
                ActivitiEventType.ENTITY_DELETED);
    }

    @Override
    public String updateModelNameInJson(String json, String modelName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode objectNode = objectMapper.readTree(json);
            ObjectNode properties = (ObjectNode) objectNode.get("properties");
            properties.put("name", modelName);
            properties.put("process_id", toCamelCase(modelName));
            return objectNode.toString();
        } catch (IOException e) {
            throw new BpmException("Error when update model name in JSON", e);
        }
    }
}