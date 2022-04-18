/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.artifacts.dsl;


import org.gradle.api.Action;
import org.gradle.api.ActionConfiguration;
import org.gradle.api.artifacts.ComponentMetadataDetails;
import org.gradle.api.artifacts.ComponentMetadataRule;
import org.gradle.internal.HasInternalProtocol;

/**
 * Allows the build to provide rules that modify the metadata of depended-on software components.
 *
 * Component metadata rules are applied in the components section of the dependencies block
 * {@link DependencyHandler} of a build script. The rules can be defined in two different ways:
 * <ol>
 *     <li>As an action directly when they are applied in the components section</li>
 *     <li>As an isolated class implementing the {@link ComponentMetadataRule} interface</li>
 * </ol>
 *
 * <p>Example shows a basic way of removing certain transitive dependencies from one of our dependencies.</p>
 * <pre class='autoTested'>
 * plugins {
 *     id 'java'
 * }
 *
 * repositories {
 *     mavenCentral()
 * }
 *
 * dependencies {
 *     components {
 *         withModule("jaxen:jaxen") {
 *             allVariants {
 *                 withDependencies {
 *                     removeAll { it.group in ["dom4j", "jdom", "xerces", "maven-plugins", "xml-apis", "xom"] }
 *                 }
 *             }
 *
 *         }
 *     }
 *     implementation("jaxen:jaxen:1.1.3")
 * }
 * </pre>
 *
 * @since 1.8
 */
@HasInternalProtocol
public interface ComponentMetadataHandler {
    /**
     * Adds a rule action that may modify the metadata of any resolved software component.
     *
     * @param rule the rule to be added
     * @return this
     */
    ComponentMetadataHandler all(Action<? super ComponentMetadataDetails> rule);



    /**
     * Adds a rule that may modify the metadata of any resolved software component.
     *
     * <p>The ruleSource is an Object that has a single rule method annotated with {}.
     *
     * <p>This rule method:
     * <ul>
     *     <li>must return void.</li>
     *     <li>must have {@link ComponentMetadataDetails} as the first parameter.</li>
     *     <li>may have an additional parameter of type {@link org.gradle.api.artifacts.ivy.IvyModuleDescriptor} or {@link org.gradle.api.artifacts.maven.PomModuleDescriptor}.</li>
     * </ul>
     *
     * @param ruleSource the rule source object to be added
     * @return this
     */
    ComponentMetadataHandler all(Object ruleSource);

    /**
     * Adds a class based rule that may modify the metadata of any resolved software component.
     *
     * @param rule the rule to be added
     * @return this
     *
     * @since 4.9
     */
    ComponentMetadataHandler all(Class<? extends ComponentMetadataRule> rule);

    /**
     * Adds a class based rule that may modify the metadata of any resolved software component.
     * The rule itself is configured by the provided configure action.
     *
     * @param rule the rule to be added
     * @param configureAction the rule configuration
     * @return this
     *
     * @since 4.9
     */
    ComponentMetadataHandler all(Class<? extends ComponentMetadataRule> rule, Action<? super ActionConfiguration> configureAction);

    /**
     * Adds a rule that may modify the metadata of any resolved software component belonging to the specified module.
     *
     * @param id the module to apply this rule to in "group:module" format or as a {@link org.gradle.api.artifacts.ModuleIdentifier}
     * @param rule the rule to be added
     * @return this
     */
    ComponentMetadataHandler withModule(Object id, Action<? super ComponentMetadataDetails> rule);



    /**
     * Adds a rule that may modify the metadata of any resolved software component belonging to the specified module.
     *
     * <p>The rule source parameter is subject to the same requirements as {@link #all(Object)}.
     *
     * @param id the module to apply this rule to in "group:module" format or as a {@link org.gradle.api.artifacts.ModuleIdentifier}
     * @param ruleSource the rule source object to be added
     * @return this
     */
    ComponentMetadataHandler withModule(Object id, Object ruleSource);

    /**
     * Adds a class based rule that may modify the metadata of any resolved software component belonging to the specified module.
     *
     * @param id the module to apply this rule to in "group:module" format or as a {@link org.gradle.api.artifacts.ModuleIdentifier}
     * @param rule the rule to be added
     * @return this
     *
     * @since 4.9
     */
    ComponentMetadataHandler withModule(Object id, Class<? extends ComponentMetadataRule> rule);

    /**
     * Adds a class based rule that may modify the metadata of any resolved software component belonging to the specified module.
     *
     * @param id the module to apply this rule to in "group:module" format or as a {@link org.gradle.api.artifacts.ModuleIdentifier}
     * @param rule the rule to be added
     * @return this
     *
     * @since 4.9
     */
    ComponentMetadataHandler withModule(Object id, Class<? extends ComponentMetadataRule> rule, Action<? super ActionConfiguration> configureAction);
}
