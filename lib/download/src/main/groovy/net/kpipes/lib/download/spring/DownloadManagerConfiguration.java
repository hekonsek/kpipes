/**
 * Licensed to the KPipes under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kpipes.lib.download.spring;

import net.kpipes.lib.process.ProcessManager;
import net.kpipes.lib.download.DownloadManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static org.apache.commons.lang3.SystemUtils.getUserHome;

@Configuration
public class DownloadManagerConfiguration {

    @Bean
    DownloadManager downloadManager(ProcessManager processManager, @Value("${download.directory:default}") File downloadDirectory) {
        if(downloadDirectory.getName().equals("default")) {
            downloadDirectory = new File(new File(getUserHome(), ".kpipes"), "downloads");
        }
        return new DownloadManager(processManager, downloadDirectory);
    }

}
