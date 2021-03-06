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

package com.haulmont.bpm.entity;

import javax.persistence.*;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.MessageTools;

@NamePattern("#getCaption|name,code,deploymentDate")
@Table(name = "BPM_PROC_DEFINITION")
@Entity(name = "bpm$ProcDefinition")
public class ProcDefinition extends StandardEntity {

    private static final long serialVersionUID = 8978443737036741675L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "CODE")
    protected String code;

    @Column(name = "ACT_ID")
    protected String actId;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "procDefinition")
    @OrderBy("order")
    protected List<ProcRole> procRoles;

    @Column(name = "ACTIVE")
    protected Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODEL_ID")
    protected ProcModel model;

    @Column(name = "DEPLOYMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date deploymentDate;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setModel(ProcModel model) {
        this.model = model;
    }

    public ProcModel getModel() {
        return model;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setProcRoles(List<ProcRole> procRoles) {
        this.procRoles = procRoles;
    }

    public List<ProcRole> getProcRoles() {
        return procRoles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActId() {
        return actId;
    }

    public Date getDeploymentDate() {
        return deploymentDate;
    }

    public void setDeploymentDate(Date deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    @MetaProperty
    public String getCaption() {
        Locale defaultLocale = AppBeans.get(MessageTools.class).getDefaultLocale();
        String formattedDeploymentDate = Datatypes.getNN(Date.class).format(deploymentDate, defaultLocale);
        return this.name + " (" + this.code + " - " + formattedDeploymentDate + ")";
    }
}