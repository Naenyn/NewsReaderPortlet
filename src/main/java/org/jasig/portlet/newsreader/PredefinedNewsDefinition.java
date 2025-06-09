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

import java.util.HashSet;
import java.util.Set;

/**
 * PredefinedNewsDefinition represents a built-in news definition.  These
 * definitions are defined in the database by an administrator, and can be
 * automatically added to user's news registration lists based on user
 * role.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 * @since 5.1.1
 */
public class PredefinedNewsDefinition extends NewsDefinition {

    private String fname;
    private Set<PredefinedNewsConfiguration> userConfigurations = new HashSet<>();
    private Set<String> defaultRoles;

    /**
     * Default constructor
     */
    public PredefinedNewsDefinition() {
        super();
    }

    /**
     * Construct a new predefined news definition
     *
     * @param id a {@link java.lang.Long} object
     * @param className a {@link java.lang.String} object
     * @param name a {@link java.lang.String} object
     */
    public PredefinedNewsDefinition(Long id, String className, String name) {
        super(id, className, name);
    }

    /**
     * Get all the user configurations for this news definition
     *
     * @return a {@link java.util.Set} object
     */
    public Set<PredefinedNewsConfiguration> getUserConfigurations() {
        return userConfigurations;
    }

    /**
     * Set the user configurations for this news definition
     *
     * @param configurations a {@link java.util.Set} object
     */
    public void setUserConfigurations(Set<PredefinedNewsConfiguration> configurations) {
        this.userConfigurations = configurations;
    }

    /**
     * Get the set of user roles who should get this news by default.
     *
     * @return set of default user roles
     */
    public Set<String> getDefaultRoles() {
        return defaultRoles;
    }

    /**
     * Set the user roles should should get this news by default.
     *
     * @param defaultRoles set of default user roles
     */
    public void setDefaultRoles(Set<String> defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    /**
     * Add a user configuration for this news definition
     *
     * @param config a {@link org.jasig.portlet.newsreader.PredefinedNewsConfiguration} object
     */
    public void addUserConfiguration(PredefinedNewsConfiguration config) {
        this.userConfigurations.add(config);
    }

	/**
	 * Add a user role to the set of roles that should get this news
	 * by default.
	 *
	 * @param role user role to be added
	 */
	public void addDefaultRole(String role) {
		this.defaultRoles.add(role);
	}

    /**
     * <p>Getter for the field <code>fname</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getFname() {
        return fname;
    }

    /**
     * <p>Setter for the field <code>fname</code>.</p>
     *
     * @param fname a {@link java.lang.String} object
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

}
