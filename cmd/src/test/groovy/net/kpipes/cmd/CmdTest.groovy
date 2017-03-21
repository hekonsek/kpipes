/**
 * Licensed to the KPipes under one or more
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
package net.kpipes.cmd

import net.kpipes.core.KPipesApplication
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static net.kpipes.cmd.Cmd.parseArguments
import static net.kpipes.lib.commons.Mavens.kpipesVersion
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static org.assertj.core.api.Assertions.assertThat

class CmdTest {

    @Test
    void shouldGetKpipesVersion() {
        System.setProperty('zooKeeper.dataDirectory', createTempDir().absolutePath)
        System.setProperty('kafka.dataDirectory', createTempDir().absolutePath)

        KPipesApplication.main()
        def versionResponse = new Cmd().executeCommand('kpipes', 'version')
        assertThat(versionResponse).isEqualTo(kpipesVersion())
    }

    @Test
    void shouldHandleStoppedServer() {
        def response = new Cmd(availableTcpPort()).executeCommand('kpipes', 'version') as String
        assertThat(response).startsWith('Cannot connect to KPipes server')
    }

    @Test
    void shouldExtractArgumentsFromCommand() {
        def arguments = parseArguments('service', 'operation', 'argument1', 'argument2')
        assertThat(arguments).isEqualTo(['argument1', 'argument2'])
    }

    @Test
    void shouldParseMapArgument() {
        def arguments = parseArguments('service', 'operation', 'argument1', "[foo: 'bar']")
        assertThat(arguments).isEqualTo(['argument1', [foo: 'bar']])
    }

}
