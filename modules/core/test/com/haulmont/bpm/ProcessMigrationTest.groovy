/*
 * Copyright (c) 2008-2016 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.bpm

import com.haulmont.bpm.core.ExtensionElementsManager
import com.haulmont.bpm.core.ProcessFormManager
import com.haulmont.bpm.core.ProcessRepositoryManager
import com.haulmont.bpm.core.ProcessRuntimeManager
import com.haulmont.bpm.entity.ProcDefinition
import com.haulmont.bpm.entity.ProcInstance
import com.haulmont.bpm.testsupport.BpmTestCase
import com.haulmont.cuba.core.Transaction
import com.haulmont.cuba.core.global.AppBeans

/**
 *
 */
class ProcessMigrationTest extends BpmTestCase{

    static final String PROCESS_PATH_1 = "com/haulmont/bpm/process/migrationTest1.bpmn20.xml"
    static final String PROCESS_PATH_2 = "com/haulmont/bpm/process/migrationTest2.bpmn20.xml"

    ProcessRepositoryManager processRepositoryManager
    ProcessRuntimeManager processRuntimeManager
    ExtensionElementsManager extensionElementsManager
    ProcessFormManager processFormManager

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        processRepositoryManager = AppBeans.get(ProcessRepositoryManager.class)
        processRuntimeManager = AppBeans.get(ProcessRuntimeManager.class)
        extensionElementsManager = AppBeans.get(ExtensionElementsManager.class)
        processFormManager = AppBeans.get(ProcessFormManager.class)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    void testProcessMigration() throws Exception {

        ProcDefinition procDefinition = processRepositoryManager.deployProcessFromPath(PROCESS_PATH_1, null, null)

        ProcInstance procInstance = new ProcInstance(procDefinition: procDefinition)
        persistence.createTransaction().execute( { em ->
            em.persist(procInstance)
        } as Transaction.Runnable)

        procInstance = processRuntimeManager.startProcess(procInstance, '', null)

        processRepositoryManager.deployProcessFromPath(PROCESS_PATH_2, procDefinition, null)

        def task = taskService.createTaskQuery().processInstanceId(procInstance.actProcessInstanceId).singleResult()

        taskService.complete(task.id)

        task = taskService.createTaskQuery().processInstanceId(procInstance.actProcessInstanceId).singleResult()
        assertEquals('newScanning', task.taskDefinitionKey)
    }
}
