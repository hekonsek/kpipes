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
package net.kpipes.core.starter.service

import groovy.transform.CompileStatic
import net.kpipes.core.starter.spi.KPipesOperations
import net.kpipes.core.starter.spi.Service
import net.kpipes.core.starter.spi.ServiceRegistry
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ConfigurationBuilder

import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.reflections.util.ClasspathHelper.forJavaClassPath

@CompileStatic
class InMemoryServiceRegistry implements ServiceRegistry {

    private final List<ServiceDefinition> serviceDefinitions = new CopyOnWriteArrayList<>()

    @Override
    def start(KPipesOperations kpipes) {
        def reflections = new Reflections(new ConfigurationBuilder().
                setUrls(forJavaClassPath()).setScanners(new MethodAnnotationsScanner()))
        reflections.getMethodsAnnotatedWith(Service).forEach {
            Method factoryMethod = it
            def serviceFactory = factoryMethod.declaringClass.newInstance()
            def service = factoryMethod.invoke(serviceFactory, kpipes)
            def serviceAnnotation = factoryMethod.getAnnotation(Service)
            serviceDefinitions << new ServiceDefinition(factoryMethod.returnType, service, serviceAnnotation.onStart(), serviceAnnotation.overridable())
        }

        serviceDefinitions.each { definition ->
            if(isNotBlank(definition.onStartCallback())) {
                definition.instance().class.declaredMethods.find{ it.name == definition.onStartCallback() }.invoke(definition.instance())
            }
        }
        serviceDefinitions.findAll(){ it.overrideable() }.each { overrideCandidate ->
            if(serviceDefinitions.find{ overrideCandidate.type().isAssignableFrom(it.type()) && !it.is(overrideCandidate) } != null) {
                serviceDefinitions.remove(overrideCandidate)
            }
        }

        serviceDefinitions.each {
            registerService(it.instance())
        }
    }

    def registerService(Object instance) {
        serviceDefinitions << new ServiceDefinition(instance.class, instance, null, false)
    }

    def <T> Optional<T> service(Class<T> serviceType) {
        def definitionFound = serviceDefinitions.find{ serviceType.isAssignableFrom(it.instance().class) }
        definitionFound == null ? Optional.empty() : Optional.of(definitionFound.instance() as T)
    }

}