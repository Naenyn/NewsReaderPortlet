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
package org.jasig.portlet.newsreader.model;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jasig.portlet.newsreader.adapter.NewsException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>RemoteHttpFullStory class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public class RemoteHttpFullStory implements FullStory {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final String remoteHttpUrl;

    @JsonCreator
    /**
     * <p>Constructor for RemoteHttpFullStory.</p>
     *
     * @param remoteHttpUrl a {@link java.lang.String} object
     */
    public RemoteHttpFullStory(@JsonProperty("remoteHttpUrl") String remoteHttpUrl) {
        this.remoteHttpUrl = remoteHttpUrl;
    }

    /**
     * <p>Getter for the field <code>remoteHttpUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getRemoteHttpUrl() {
        return remoteHttpUrl;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getFullStoryText() {
        try  {
        return fetchRemoteContent();
        }
        catch (Exception ex) {
            throw new NewsException("Failed to fetch FullStory content from "+remoteHttpUrl,ex);
        }
    }

    private String fetchRemoteContent() throws ClientProtocolException, IOException {
        log.trace("RemoteHttpFullStory.fetchRemoteContent()");
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(remoteHttpUrl);

            log.debug("executing request " + httpget.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            return responseBody;
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

}
