/*
 * Copyright (c) 2015 com.haulmont.bpm.gui
 */
package com.haulmont.bpm.gui.form.standard;

import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.bpm.form.ProcFormDefinition;
import com.haulmont.bpm.form.ProcFormParam;
import com.haulmont.bpm.gui.form.AbstractProcForm;
import com.haulmont.bpm.gui.procactor.ProcActorsFrame;
import com.haulmont.bpm.gui.procattachment.ProcAttachmentsFrame;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextArea;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard process form that is used for:
 * <ol>
 *     <li>entering procTask comment</li>
 *     <li>adding process attachment</li>
 *     <li>setting process actors</li>
 * </ol>
 * Visibility of components (comment field, frames for attachments and actors) are defined by
 * parameters of {@code formDefinition}
 * @author gorbunkov
 */
public class StandardProcessForm extends AbstractProcForm {

    @Inject
    protected TextArea comment;

    @Inject
    protected Label procAttachmentsLabel;

    @Inject
    protected ProcAttachmentsFrame procAttachmentsFrame;

    @Inject
    protected  Label procActorsLabel;

    @Inject
    protected ProcActorsFrame procActorsFrame;

    @WindowParam(name = "procTask")
    protected ProcTask procTask;

    @WindowParam(name = "procInstance")
    protected ProcInstance procInstance;

    @WindowParam(name = "formDefinition", required = true)
    protected ProcFormDefinition formDefinition;

    protected static final String COMMENT_REQUIRED_PARAM = "commentRequired";
    protected static final String PROC_ACTORS_VISIBLE_PARAM = "procActorsVisible";
    protected static final String ATTACHMENTS_VISIBLE_PARAM = "attachmentsVisible";

    protected boolean procActorsVisible;
    protected boolean procAttachmentsVisible;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        getDialogParams().setResizable(true);
        getDialogParams().setWidth(700);

        ProcFormParam commentRequiredParam = formDefinition.getParams().get(COMMENT_REQUIRED_PARAM);
        if (commentRequiredParam != null && "true".equals(commentRequiredParam.getValue())) {
            comment.setRequired(true);
        }

        ProcFormParam procActorsVisibleParam = formDefinition.getParams().get(PROC_ACTORS_VISIBLE_PARAM);
        procActorsVisible = procActorsVisibleParam != null && "true".equals(procActorsVisibleParam.getValue());

        ProcFormParam procAttachmentsVisibleParam = formDefinition.getParams().get(ATTACHMENTS_VISIBLE_PARAM);
        procAttachmentsVisible = procAttachmentsVisibleParam != null && "true".equals(procAttachmentsVisibleParam.getValue());

        procActorsLabel.setVisible(procActorsVisible);
        procActorsFrame.setVisible(procActorsVisible);

        procAttachmentsLabel.setVisible(procAttachmentsVisible);
        procAttachmentsFrame.setVisible(procAttachmentsVisible);

        if (procAttachmentsVisible && (procTask != null || procInstance != null)) {
            procAttachmentsFrame.setProcTask(procTask);
            procAttachmentsFrame.setProcInstance(procInstance != null ? procInstance : procTask.getProcInstance());
            procAttachmentsFrame.refresh();
        }

        if (procActorsVisible && procInstance != null) {
            procActorsFrame.setProcInstance(procInstance);
            procActorsFrame.refresh();
        }
    }

    @Override
    public String getComment() {
        return comment.getValue();
    }

    @Override
    public Map<String, Object> getFormResult() {
        return new HashMap<>();
    }

    public void onWindowCommit() {
        if (!validateAll()) {
            return;
        }

        if (procActorsVisible) {
            procActorsFrame.commit();
        }
        if (procAttachmentsVisible) {
            procAttachmentsFrame.commit();
        }
        close(COMMIT_ACTION_ID);
    }

    public void onWindowClose() {
        close(CLOSE_ACTION_ID);
    }
}