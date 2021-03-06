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

package org.gradle.initialization;

import org.gradle.internal.classloader.ClassLoaderFactory;
import org.gradle.internal.classloader.ClasspathUtil;
import org.gradle.internal.classpath.DefaultClassPath;
import org.gradle.internal.jvm.Jvm;
import org.gradle.internal.os.OperatingSystem;

import java.io.File;
import java.net.URLClassLoader;

public class DefaultJdkToolsInitializer implements JdkToolsInitializer {

    private final ClassLoaderFactory classLoaderFactory;

    public DefaultJdkToolsInitializer(ClassLoaderFactory classLoaderFactory) {
        this.classLoaderFactory = classLoaderFactory;
    }

    @Override
    public void initializeJdkTools() {
        // Add in tools.jar to the systemClassloader parent
        File toolsJar = Jvm.current().getToolsJar();
        //dingyi modify: Only supports running in non-android environments
        if (toolsJar != null && !OperatingSystem.current().isAndroid()) {
            final ClassLoader systemClassLoaderParent = classLoaderFactory.getIsolatedSystemClassLoader();
            ClasspathUtil.addUrl((URLClassLoader) systemClassLoaderParent, DefaultClassPath.of(toolsJar).getAsURLs());
        }
    }
}
