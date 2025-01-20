/**
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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.jasig.portlet.newsreader.UserDefinedNewsDefinition;
import org.jasig.portlet.newsreader.adapter.RomeAdapter;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.mvc.AbstractNewsController;
import org.jasig.portlet.newsreader.mvc.NewsListingCommand;
import org.jasig.portlet.newsreader.service.NewsSetResolvingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;


/**
 * EditNewsDefinitionController allows a user to add or edit a user-defined
 * news definition.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
@Controller
@RequestMapping("EDIT")
public class EditUserRomeController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private NewsStore newsStore;

    @Autowired(required = true)
    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

    private NewsSetResolvingService setCreationService;

    @Autowired(required = true)
    public void setSetCreationService(NewsSetResolvingService setCreationService) {
        this.setCreationService = setCreationService;
    }

    @ModelAttribute("newsListingCommand")
    public NewsListingCommand getNewsForm(PortletRequest request) throws Exception {

        // if we're editing a news, retrieve the news definition from
        // the database and add the information to the form
        String id = request.getParameter("id");
        if (id != null && !id.equals("")) {
            Long configurationId = Long.parseLong(id);
            if (configurationId > -1) {
                NewsConfiguration listing = (NewsConfiguration) newsStore.getNewsConfiguration(configurationId);
                log.debug("retrieved " + listing.toString());
                NewsListingCommand command = new NewsListingCommand();
                command.setId(listing.getId());
                command.setName(listing.getNewsDefinition().getName());
                command.setUrl(listing.getNewsDefinition().getParameters().get("url"));
                command.setDisplayed(listing.isDisplayed());

                return command;
            } else {
                // otherwise, construct a brand new form
                return new NewsListingCommand();
            }

        } else {
            // otherwise, construct a brand new form
            return new NewsListingCommand();
        }
    }

    @RenderMapping(params = "action=editUrl")
    public String getUserEditView(PortletRequest request, RenderResponse response) {
        log.debug("Returning editNewsUrl view");

        // get the to-be-edited news configuration id
        String[] formIdValues = request.getParameterMap().get("id");
        String formId = null;
        if (formIdValues != null && formIdValues.length > 0) {
            formId = formIdValues[0];
        }

        // if user doesn't have permissions, redirect
        if (StringUtils.isNotBlank(formId)) {
            long lFormId = Long.parseLong(formId);
            if (lFormId > -1) {
                if (!canEditNewsConfiguration(request, lFormId)) {
                    log.warn("User [ {} ] with IP [ {} ] tried to edit news configuration [ {} ] without permission!",
                            request.getRemoteUser(),
                            request.getProperty("REMOTE_ADDR"),
                            lFormId);
                    PortletURL redirectUrl = response.createRenderURL();
                    redirectUrl.setParameter("action", "editPreferences");
                    request.setAttribute("redirectUrl", redirectUrl.toString());
                }
            }
        }

        return "editNewsUrl";
    }
    
    @ActionMapping(params = "action=editUrl")
    public void onSubmitAction(ActionRequest request, ActionResponse response,
            @ModelAttribute("newsListingCommand") NewsListingCommand form) throws Exception {

        // construct a news definition from the form data
        UserDefinedNewsConfiguration config = null;
        UserDefinedNewsDefinition definition = null;

        if (form.getId() > -1) {

            if (canEditNewsConfiguration(request, form.getId())) {
                config = (UserDefinedNewsConfiguration) newsStore.getNewsConfiguration(form.getId());
                log.debug("User [ {} ] is updating news", request.getRemoteUser());
                definition = (UserDefinedNewsDefinition) config.getNewsDefinition();
                definition.addParameter("url", form.getUrl());
                definition.setName(form.getName());
            } else {
                log.warn("User [ {} ] with IP [ {} ] tried to edit news configuration [ {} ] without permission!",
                        request.getRemoteUser(),
                        request.getProperty("REMOTE_ADDR"),
                        form.getId());
                return;
            }

        } else {

            definition = new UserDefinedNewsDefinition();
            definition.setClassName(RomeAdapter.class.getName());
            definition.addParameter("url", form.getUrl());
            definition.setName(form.getName());
            newsStore.storeNewsDefinition(definition);

            config = new UserDefinedNewsConfiguration();
            config.setNewsDefinition(definition);
            config.setDisplayed(form.isDisplayed());

            String setName = request.getPreferences().getValue("newsSetName", "default");
            config.setNewsSet(setCreationService.getNewsSet(setName, request));
            log.debug("Insert new");
        }

        log.debug("User defined News definition is " + config.getNewsDefinition().getName());

        // save the news
        newsStore.storeNewsConfiguration(config);

        // send the user back to the main edit page
        response.setRenderParameter("action", "editPreferences");

    }

    private boolean isPredefinedNewsConfiguration(NewsConfiguration newsConfiguration) {
        return newsConfiguration.getNewsDefinition() instanceof PredefinedNewsDefinition;
    }

    private boolean canEditNewsConfiguration(PortletRequest request, long configurationId) {
        boolean isAdmin = request.isUserInRole(AbstractNewsController.NEWS_ADMIN_ROLE);
        NewsConfiguration configuration = newsStore.getNewsConfiguration(configurationId);
        if (isPredefinedNewsConfiguration(configuration)) {
            return isAdmin;
        } else {
            UserDefinedNewsConfiguration userConfiguration = (UserDefinedNewsConfiguration) configuration;
            return isAdmin || userConfiguration.getNewsSet().getUserId().equals(request.getRemoteUser());
        }
    }

}
