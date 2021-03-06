/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.tooling.internal.provider;

import org.gradle.api.internal.StartParameterInternal;
import org.gradle.initialization.BuildRequestContext;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.buildtree.BuildActionRunner;
import org.gradle.internal.invocation.BuildAction;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.internal.service.scopes.GradleUserHomeScopeServiceRegistry;
import org.gradle.internal.session.BuildSessionState;
import org.gradle.internal.session.CrossBuildSessionState;
import org.gradle.launcher.exec.BuildActionExecuter;
import org.gradle.launcher.exec.BuildActionParameters;
import org.gradle.launcher.exec.BuildActionResult;
import org.gradle.launcher.exec.BuildExecuter;


/**
 * A {@link BuildExecuter} responsible for establishing the {@link BuildSessionState} to execute a {@link BuildAction} within.
 */
public class BuildSessionLifecycleBuildActionExecuter implements BuildActionExecuter<BuildActionParameters, BuildRequestContext> {
    private final ServiceRegistry globalServices;
    private final GradleUserHomeScopeServiceRegistry userHomeServiceRegistry;

    public BuildSessionLifecycleBuildActionExecuter(GradleUserHomeScopeServiceRegistry userHomeServiceRegistry, ServiceRegistry globalServices) {
        this.userHomeServiceRegistry = userHomeServiceRegistry;
        this.globalServices = globalServices;
    }

    @Override
    public BuildActionResult execute(BuildAction action, BuildActionParameters actionParameters, BuildRequestContext requestContext) {
        StartParameterInternal startParameter = action.getStartParameter();
        if (action.isCreateModel()) {
            // When creating a model, do not use continuous mode
            startParameter.setContinuous(false);
        }
        try (CrossBuildSessionState crossBuildSessionState = new CrossBuildSessionState(globalServices, startParameter)) {
            try (BuildSessionState buildSessionState = new BuildSessionState(userHomeServiceRegistry, crossBuildSessionState, startParameter, requestContext, actionParameters.getInjectedPluginClasspath(), requestContext.getCancellationToken(), requestContext.getClient(), requestContext.getEventConsumer())) {
                return buildSessionState.run(context -> {
                    //dingyi modify: In this modified gradle, all actions are in the same jvm runtime environment, and there is no need to serialize return values.
                    BuildActionRunner.Result result = context.execute(action);
                    if (result.getBuildFailure() == null) {
                        return BuildActionResult.of(result.getClientResult());
                    }
                    if (requestContext.getCancellationToken().isCancellationRequested()) {
                        // If the build was cancelled, don't return any result
                        // And throw the cancellation exception
                        throw UncheckedException
                                .throwAsUncheckedException(result.getBuildFailure());
                        //return BuildActionResult.cancelled(payloadSerializer.serialize(result.getBuildFailure()));
                    }
                    // Otherwise, throw the failure
                    throw UncheckedException
                            .throwAsUncheckedException(result.getClientFailure() != null ? result.getClientFailure() : result.getBuildFailure());
                });
            }
        }
    }
}
