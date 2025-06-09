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
package org.jasig.portlet.newsreader.mvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.resourceserver.aggr.om.Included;
import org.jasig.resourceserver.utils.aggr.ResourcesElementsProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Pays attention to the state of skin aggregation and only applies the fitler if it is disabled
 *
 * @author Eric Dalquist
 * @version $Revision$
 * @since 5.1.1
 */
public class AggregationAwareFilterBean implements Filter {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private Filter filter;
    private ResourcesElementsProvider elementsProvider;

    /**
     * The filter to delegate to
     *
     * @param filter a {@link javax.servlet.Filter} object
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * <p>Setter for the field <code>elementsProvider</code>.</p>
     *
     * @param elementsProvider a {@link org.jasig.resourceserver.utils.aggr.ResourcesElementsProvider} object
     */
    @Autowired
    public void setElementsProvider(ResourcesElementsProvider elementsProvider) {
        this.elementsProvider = elementsProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
        this.filter.destroy();
    }

    /** {@inheritDoc} */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filter.init(filterConfig);
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    /** {@inheritDoc} */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.elementsProvider.getIncludedType((HttpServletRequest)request) == Included.AGGREGATED) {
            if (logger.isDebugEnabled()) {
                logger.debug("Aggregation enabled, delegating to filter: " + this.filter);
            }
            this.filter.doFilter(request, response, chain);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("Aggregation disabled, skipping filter: " + this.filter);
            }
            chain.doFilter(request, response);
        }
    }
}
