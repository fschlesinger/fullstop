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
package org.zalando.stups.fullstop.plugin;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;
import com.amazonaws.services.ec2.model.Image;
import org.zalando.stups.clients.kio.Application;
import org.zalando.stups.fullstop.violation.ViolationBuilder;

import java.util.Map;
import java.util.Optional;

public interface EC2InstanceContext {

    CloudTrailEvent getEvent();

    String getInstanceJson();

    String getInstanceId();

    Optional<String> getAmiId();

    Optional<Image> getAmi();

    <T extends AmazonWebServiceClient> T getClient(Class<T> type);

    ViolationBuilder violation();

    String getEventName();

    String getAccountId();

    Region getRegion();

    String getRegionAsString();

    Optional<String> getApplicationId();

    Optional<String> getVersionId();

    Optional<Application> getKioApplication();

    Optional<Boolean> isTaupageAmi();

    Optional<Map> getTaupageYaml();
}
