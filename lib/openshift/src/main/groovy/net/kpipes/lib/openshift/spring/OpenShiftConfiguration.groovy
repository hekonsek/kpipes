/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kpipes.lib.openshift.spring

import net.kpipes.lib.download.DownloadManager
import net.kpipes.lib.openshift.OpenShift
import net.kpipes.lib.process.ProcessManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Spring configuration for OpenShift PaaS provider
 */
@Configuration
class OpenShiftConfiguration {

    /**
     * OpenShift PaaS instance. Can be overridden.
     */
    @Bean(initMethod = 'init')
    @ConditionalOnMissingBean
    OpenShift openShift(DownloadManager downloadManager, ProcessManager processManager) {
        new OpenShift(downloadManager, processManager)
    }

}