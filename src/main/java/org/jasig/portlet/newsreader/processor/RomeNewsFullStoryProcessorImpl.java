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
package org.jasig.portlet.newsreader.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.jasig.portlet.newsreader.model.RemoteHttpFullStory;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import com.rometools.rome.feed.synd.SyndEntry;

/**
 * Strategy implementation derived from {@link org.jasig.portlet.newsreader.processor.RomeNewsProcessorImpl} with support for full story.
 *
 * Rather than set the {@code link} in the {@link org.jasig.portlet.newsreader.model.NewsFeedItem}, a {@link org.jasig.portlet.newsreader.model.RemoteHttpFullStory} is created
 * using the link as the URL.
 *
 * @author Benito J. Gonzalez (bgonzalez@unicon.net)
 * @since 3.1.2
 */
public class RomeNewsFullStoryProcessorImpl extends RomeNewsProcessorImpl {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** {@inheritDoc} */
    @Override
    protected NewsFeedItem getNewsFeedItem(SyndEntry entry, String titlePolicy, String descriptionPolicy) throws PolicyException, ScanException {
        log.debug("getNewsFeedItem() in full story method");
        NewsFeedItem item = super.getNewsFeedItem(entry, titlePolicy, descriptionPolicy);
        RemoteHttpFullStory fullStory = new RemoteHttpFullStory(entry.getLink());
        // Here would be a good place to pass along a sequence of filters to
        // RemoteHttpFullStory
        item.setFullStory(fullStory);
        item.setLink(null);
        return item;
    }

}
