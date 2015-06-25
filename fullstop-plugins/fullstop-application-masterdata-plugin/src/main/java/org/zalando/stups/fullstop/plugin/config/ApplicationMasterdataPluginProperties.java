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
package org.zalando.stups.fullstop.plugin.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Lists;

@ConfigurationProperties(prefix = "fullstop.plugins.applicationMasterdata")
public class ApplicationMasterdataPluginProperties {

    private List<String> defaultValidatorsEnabled = Lists.newArrayList("scm_url",
                                                                       "specification_url",
                                                                       "documentation_url");

    private List<String> validatorsEnabled = Lists.newArrayList();

    public List<String> getValidatorsEnabled() {
        if (validatorsEnabled.isEmpty()) {
            return defaultValidatorsEnabled;
        }
        return validatorsEnabled;
    }

    public void setValidatorsEnabled(final List<String> validatorsEnabled) {
        this.validatorsEnabled = validatorsEnabled;
    }

    public List<String> getDefaultValidatorsEnabled() {
        return defaultValidatorsEnabled;
    }

    public void setDefaultValidatorsEnabled(List<String> defaultValidatorsEnabled) {
        this.defaultValidatorsEnabled = defaultValidatorsEnabled;
    }

}
