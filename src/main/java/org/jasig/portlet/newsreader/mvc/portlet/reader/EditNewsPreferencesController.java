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
package org.jasig.portlet.newsreader.mvc.portlet.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jasig.portlet.newsreader.service.RolesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.mvc.AbstractNewsController;
import org.jasig.portlet.newsreader.service.NewsSetResolvingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;


/**
 * EditNewsPreferencesController provides the main edit page for the News Reader
 * portlet.  The page allows users to view, add, delete and edit all available
 * feeds.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 * @since 5.1.1
 */
@Controller
@RequestMapping("EDIT")
public class EditNewsPreferencesController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String,String> predefinedEditActions;

    /**
     * <p>Setter for the field <code>predefinedEditActions</code>.</p>
     *
     * @param predefinedEditActions a {@link java.util.Map} object
     */
    @Resource(name = "predefinedEditActions")
    public void setPredefinedEditActions(Map<String,String> predefinedEditActions) {
        this.predefinedEditActions = predefinedEditActions;
    }

    @Autowired
    private RolesService rolesService;

    private NewsStore newsStore;

    /**
     * <p>Setter for the field <code>newsStore</code>.</p>
     *
     * @param newsStore a {@link org.jasig.portlet.newsreader.dao.NewsStore} object
     */
    @Autowired(required = true)
    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

    private NewsSetResolvingService setCreationService;

    /**
     * <p>Setter for the field <code>setCreationService</code>.</p>
     *
     * @param setCreationService a {@link org.jasig.portlet.newsreader.service.NewsSetResolvingService} object
     */
    @Autowired(required = true)
    public void setSetCreationService(NewsSetResolvingService setCreationService) {
        this.setCreationService = setCreationService;
    }

    /**
     * <p>showPreferencesView.</p>
     *
     * @param request a {@link javax.portlet.RenderRequest} object
     * @param response a {@link javax.portlet.RenderResponse} object
     * @return a {@link org.springframework.web.portlet.ModelAndView} object
     * @throws java.lang.Exception if any.
     */
    @RenderMapping
    public ModelAndView showPreferencesView(RenderRequest request,
            RenderResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();

        PortletSession session = request.getPortletSession();
        String setName = request.getPreferences().getValue("newsSetName", "default");
        NewsSet set = setCreationService.getNewsSet(setName, request);
        final List<NewsConfiguration> configurations = AbstractNewsController.filterNonWhitelistedConfigurations(request, set.getNewsConfigurations());

        // divide the configurations into user-defined and pre-defined
        // configurations for display
        List<UserDefinedNewsConfiguration> myNewsConfigurations = new ArrayList<UserDefinedNewsConfiguration>();
        List<PredefinedNewsConfiguration> predefinedNewsConfigurations = new ArrayList<PredefinedNewsConfiguration>();
        for (NewsConfiguration configuration : configurations) {
            if (configuration instanceof UserDefinedNewsConfiguration) {
                myNewsConfigurations.add((UserDefinedNewsConfiguration) configuration);
            } else if (configuration instanceof PredefinedNewsConfiguration) {
                predefinedNewsConfigurations.add((PredefinedNewsConfiguration) configuration);
            }
        }
        Collections.sort(myNewsConfigurations);
        Collections.sort(predefinedNewsConfigurations);

        model.put("myNewsConfigurations", myNewsConfigurations);
        model.put("predefinedNewsConfigurations", predefinedNewsConfigurations);

        // get the user's role listings
        final Set<String> userRoles = rolesService.getUserRoles(request);

        // get a list of predefined feeds the user doesn't
        // currently have configured
        List<PredefinedNewsDefinition> definitions = newsStore.getHiddenPredefinedNewsDefinitions(set.getId(), userRoles);
        model.put("hiddenFeeds", definitions);

        model.put("predefinedEditActions", predefinedEditActions);

        // return the edit view
        return new ModelAndView("editNews", "model", model);
    }

    /**
     * <p>saveNewsPreference.</p>
     *
     * @param request a {@link javax.portlet.ActionRequest} object
     * @param response a {@link javax.portlet.ActionResponse} object
     * @throws java.lang.Exception if any.
     */
    @ActionMapping
    protected void saveNewsPreference(ActionRequest request,
            ActionResponse response) throws Exception {
        Long id = Long.parseLong(request.getParameter("id"));
        String actionCode = request.getParameter("actionCode");
        PortletSession session = request.getPortletSession();
        Long setId = (Long) session.getAttribute("setId", PortletSession.PORTLET_SCOPE);
        NewsSet set = newsStore.getNewsSet(setId);

        if (actionCode.equals("delete")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            newsStore.deleteNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("show")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            config.setDisplayed(true);
            newsStore.storeNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("hide")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            config.setDisplayed(false);
            newsStore.storeNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("showNew")) {
            // get user information
            PredefinedNewsDefinition definition = (PredefinedNewsDefinition) newsStore.getNewsDefinition(id);
            log.debug("definition to save " + definition.toString());
            PredefinedNewsConfiguration config = new PredefinedNewsConfiguration();
            config.setNewsDefinition(definition);
            config.setNewsSet(set);
            newsStore.storeNewsConfiguration(config);
        }
    }

    /**
     * <p>saveDisplayPreference.</p>
     *
     * @param request a {@link javax.portlet.ResourceRequest} object
     * @param response a {@link javax.portlet.ResourceResponse} object
     * @return a {@link org.springframework.web.portlet.ModelAndView} object
     * @throws java.io.IOException if any.
     */
    @ResourceMapping
    public ModelAndView saveDisplayPreference(ResourceRequest request,
            ResourceResponse response) throws IOException {

        Map<String, ?> model;

        try {
            String prefName = request.getParameter("prefName");
            String prefValue = request.getParameter("prefValue");

            PortletPreferences prefs = request.getPreferences();
            prefs.setValue(prefName, prefValue);
            prefs.store();

            model = Collections.singletonMap("status", "success");

        } catch (Exception e) {
            log.error("There was an error saving the preferences.", e);
            model = Collections.singletonMap("status", "failure");
        }

        return new ModelAndView("json", model);

    }

}
