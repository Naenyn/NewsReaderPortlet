/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.newsreader;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * NewsDefinition represents the base class for news registrations.
 * Information required to retrieve the news, such as the news's URL
 * or important default system-wide configuration information may be stored
 * in the parameters map.  In order to add this news for a specific user,
 * a NewsConfiguration referencing this news definition must be
 * created.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 * @since 5.1.1
 */
public class NewsDefinition implements Comparable<NewsDefinition> {

    private Long id = new Long(-1);
    private String className;
    private String name;
    private Map<String, String> parameters = new HashMap<>();

    /**
     * Default constructor.
     */
    public NewsDefinition() {}

    /**
     * <p>Constructor for NewsDefinition.</p>
     *
     * @param id a {@link java.lang.Long} object
     * @param className a {@link java.lang.String} object
     * @param name a {@link java.lang.String} object
     */
    public NewsDefinition(Long id, String className, String name) {
        this.id = id;
        this.className = className;
        this.name = name;
    }

    /**
     * Return the unique id of this news.
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the unique id for this news.
     *
     * @param id a {@link java.lang.Long} object
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the adapter class for this news which will
     * determine how the news is retrieved.  This id must match a
     * news adapter registered in the spring context files.
     *
     * @return a {@link java.lang.String} object
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the name of the adapter class for this news which will
     * determine how the news is retrieved.  This id must match a
     * news adapter registered in the spring context files.
     *
     * @param className a {@link java.lang.String} object
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Return the display name for this news.
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * Set the display name for this news.
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>isPredefined.</p>
     *
     * @return a boolean
     */
    public boolean isPredefined() {
        return this instanceof PredefinedNewsDefinition;
    }

    /**
     * Return the map of news parameters.  These parameters can hold
     * any extra information needed by the particular adapter used by
     * this news, such as a URL.
     *
     * @return parameter map
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Set the map of news parameters.  These parameters can hold
     * any extra information needed by the particular adapter used by
     * this news, such as a URL.
     *
     * @param parameters parameter map
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Add an individual news parameter.  These parameters can hold
     * any extra information needed by the particular adapter used by
     * this news, such as a URL.
     *
     * @param name  parameter name (key)
     * @param value value to be stored
     */
    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
	    return new EqualsBuilder()
	        .append("id", this.id)
            .append("name", this.name)
	        .append("class", this.className)
	        .toString();
	}

    /** {@inheritDoc} */
    @Override
    public int compareTo(NewsDefinition def) {
        return new CompareToBuilder()
            .append(this.name, def.getName())
            .append(this.id, def.getId())
            .toComparison();
    }
	
	
}
