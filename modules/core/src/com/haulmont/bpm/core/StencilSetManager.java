/*
 * Copyright (c) 2008-2016 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.bpm.core;

import com.haulmont.cuba.core.entity.FileDescriptor;

import java.util.List;

/**
 * Class that is used for storing and retrieving stencilSet json
 */
public interface StencilSetManager {

    String NAME = "bpm_StencilSetManager";

    /**
     * Returns a stencilset that is a result of merge of the default stencilset from the 'stencilset.json' file
     * and the custom stencilset that is stored in the database
     * @return stencilset json
     */
    String getStencilSet();

    /**
     * Updates a custom stencilset in the database.
     * @param jsonData custom stencilset json. Note that custom stencilset must contain only custom stencils and propertyPackages
     */
    void setStencilSet(String jsonData);

    /**
     * Removes a custom stencil set from the database
     */
    void resetStencilSet();

    /**
     * Associates a new stencil with the {@link com.haulmont.bpm.core.jsonconverter.CustomServiceTaskJsonConverter}
     */
    void registerServiceTaskStencilBpmnJsonConverter(String stencilId);

    /**
     * Exports a stencilset json file and a set of stencil icon files into the zip archive
     * @param stencilsJson json string with the custom stencils
     * @param iconFiles a list of stencil icons file descriptors
     * @return a byte array of the zip archive
     */
    byte[] exportStencilSet(String stencilsJson, List<FileDescriptor> iconFiles);

    /**
     * Imports a zip archive with the stencilset.json and stencils icons
     * @param zipBytes a byte array of the zip archive
     */
    void importStencilSet(byte[] zipBytes);
}
