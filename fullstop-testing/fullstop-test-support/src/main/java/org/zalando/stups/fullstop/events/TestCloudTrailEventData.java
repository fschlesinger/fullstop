/**
 * Copyright (C) 2015 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zalando.stups.fullstop.events;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventData;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.CloudTrailEventField;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.UserIdentity;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.EventSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;
import static org.zalando.stups.fullstop.events.TestCloudTrailEventSerializer.serializerFor;

/**
 * Creates {@link CloudTrailEvent}s with data from classpath-resources.
 */
public class TestCloudTrailEventData extends CloudTrailEventData {

    private Map<String, Object> data = new LinkedHashMap<>();

    private ObjectMapper mapper;

    private String responseElementsResource;

    public TestCloudTrailEventData(final Map<String, Object> data) {
        this.data = data;
    }

    public TestCloudTrailEventData(final String responseElementsResource) {
        this.responseElementsResource = responseElementsResource;
    }

    public TestCloudTrailEventData(final Map<String, Object> data, final String responseElementsResource) {
        this.data = data;
        this.responseElementsResource = responseElementsResource;
    }

    public static CloudTrailEvent createCloudTrailEventFromMap(final Map<String, Object> content) {
        return new CloudTrailEvent(new TestCloudTrailEventData(content), null);
    }

    public static CloudTrailEvent createCloudTrailEvent(final String resource) {
        try (final EventSerializer serializer = serializerFor(resource)){
            // calling hasNextEvent() is essential before getNextEvent()
            checkState(serializer.hasNextEvent(), "Resource %s does not contain cloud trail events");
            return serializer.getNextEvent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserIdentity getUserIdentity() {
        Map<String, Object> value = (Map<String, Object>) this.data.get(CloudTrailEventField.userIdentity.name());
        UserIdentity ui = new UserIdentity();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            ui.add(entry.getKey(), entry.getValue());
        }

        return ui;
    }

    @Override
    public Object get(final String key) {
        return data.get(key);
    }

    @Override
    public UUID getEventId() {
        Object value = data.get(CloudTrailEventField.eventID.name());
        if (value == null) {
            return UUID.randomUUID();
        }
        else {
            if (value instanceof UUID) {
                return (UUID) value;
            }

            if (value instanceof String) {

                return UUID.fromString((String) value);
            }
        }

        throw new RuntimeException("NO-UUID-FOUND");
    }

    @Override
    public void add(final String key, final Object value) {
        this.data.put(key, value);
    }

    @Override
    public String getRequestParameters() {
        if (data.get("requestParameters") != null) {
            Object responseElements = data.get("requestParameters");

            if (mapper == null) {
                mapper = new ObjectMapper();
            }

            StringWriter writer = new StringWriter();
            return writeValue(responseElements, writer);
        }

        return "";
    }

    @Override
    public String getResponseElements() {
        if (data.get("responseElements") != null) {
            Object responseElements = data.get("responseElements");

            if (mapper == null) {
                mapper = new ObjectMapper();
            }

            StringWriter writer = new StringWriter();
            return writeValue(responseElements, writer);
        }
        else if (responseElementsResource != null) {
            return getResponseElementsFromClasspath(responseElementsResource);
        }

        return "";
    }

    private String writeValue(Object responseElements, StringWriter writer) {
        try {
            mapper.writeValue(writer, responseElements);
            writer.flush();
            writer.close();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            try {
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String getResponseElementsFromClasspath(final String resource) {
        try {
            return new String(Files.readAllBytes(java.nio.file.Paths.get(getClass().getResource(resource).toURI())));
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
