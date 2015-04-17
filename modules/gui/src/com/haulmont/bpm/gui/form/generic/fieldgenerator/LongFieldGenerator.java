/*
 * Copyright (c) 2015 Haulmont Technology Ltd. All Rights Reserved.
 *   Haulmont Technology proprietary and confidential.
 *   Use is subject to license terms.
 */

package com.haulmont.bpm.gui.form.generic.fieldgenerator;

import com.haulmont.bpm.form.ProcFormParam;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.LongDatatype;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.TextField;

/**
 * @author gorbunkov
 * @version $Id$
 */
public class LongFieldGenerator extends AbstractFormFieldGenerator {

    @Override
    public Field createField(ProcFormParam formParam, String actExecutionId) {
        TextField textField = componentsFactory.createComponent(TextField.class);
        textField.setDatatype(Datatypes.get(LongDatatype.NAME));
        standardFieldInit(textField, formParam);
        setFieldValue(textField, formParam, LongDatatype.NAME, actExecutionId);
        return textField;
    }
}