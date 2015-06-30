/*
 * Copyright (c) 2015 Haulmont Technology Ltd. All Rights Reserved.
 *   Haulmont Technology proprietary and confidential.
 *   Use is subject to license terms.
 */

package com.haulmont.bpm

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.haulmont.bpm.core.ModelTransformer
import com.haulmont.bpm.service.ModelService
import com.haulmont.bpm.testsupport.BpmTestCase
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Resources
import org.junit.Test

/**
 *
 * @author gorbunkov
 * @version $Id$
 */
class ModelTransformationTest extends BpmTestCase {

    ModelService modelService

    void setUp() {
        super.setUp()
        modelService = AppBeans.get(ModelService.class)
    }

    @Test
    void testSubModelExpanding() {
        def subModelActId = uploadModel("subModel", [:])
        def mainModelActId = uploadModel("mainModel", ["{SUB_MODEL_ACT_ID}": subModelActId])
        def modelTransformer = AppBeans.get(ModelTransformer.class)

        def bytes = repositoryService.getModelEditorSource(mainModelActId)
        def transformedJson = modelTransformer.transformModel(bytes)
        def objectMapper = new ObjectMapper()
        def objectNode = objectMapper.readTree(transformedJson)
        ArrayNode shapes = objectNode.get("childShapes") as ArrayNode
        def mainScript1 = findById(shapes, "mainScript1")
        def mainScript2 = findById(shapes, "mainScript2")
        def subScript1 = findById(shapes, "subScript1")
        def subScript2 = findById(shapes, "subScript2")

        def flow1 = findOutgoing(shapes, mainScript1)
        def afterMainScript1 = findOutgoing(shapes, flow1)
        assertEquals(afterMainScript1.get("resourceId").asText(), subScript1.get("resourceId").asText())

        def flow2 = findOutgoing(shapes, subScript2)
        def afterSubScript2 = findOutgoing(shapes, flow2)
        assertEquals(afterSubScript2.get("resourceId").asText(), mainScript2.get("resourceId").asText())
    }

    protected String uploadModel(String modelName, Map replacements) {
        String modelActId = modelService.createModel(modelName)
        def resources = AppBeans.get(Resources.class)
        def modelJson = resources.getResourceAsString("com/haulmont/bpm/model/${modelName}.json")
        replacements.each {k, v ->
            modelJson = modelJson.replace(k, v)
        }
        modelService.updateModel(modelActId, modelName, "", modelJson, "")
        return modelActId
    }

    protected JsonNode findById(ArrayNode shapes, String id) {
        shapes.find {it.get("properties").get("overrideid").asText() == id};
    }

    protected JsonNode findByResourceId(ArrayNode shapes, String id) {
        shapes.find {it.get("resourceId").asText() == id};
    }

    protected JsonNode findOutgoing(ArrayNode shapes, JsonNode node) {
        def outgoingResourceId = node.get("outgoing")[0].get("resourceId").asText()
        findByResourceId(shapes, outgoingResourceId)
    }
}
