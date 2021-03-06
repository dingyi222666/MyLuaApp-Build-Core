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

package org.gradle.internal.service.scopes;

import org.gradle.api.internal.ClassPathRegistry;
import org.gradle.api.internal.DefaultClassPathProvider;
import org.gradle.api.internal.DefaultClassPathRegistry;
import org.gradle.api.internal.changedetection.state.DefaultFileAccessTimeJournal;
import org.gradle.api.internal.changedetection.state.GradleUserHomeScopeFileTimeStampInspector;
import org.gradle.api.internal.classpath.ModuleRegistry;
import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.temp.GradleUserHomeTemporaryFileProvider;
import org.gradle.api.internal.initialization.loadercache.ClassLoaderCache;
import org.gradle.api.internal.initialization.loadercache.DefaultClassLoaderCache;
import org.gradle.api.model.ObjectFactory;
import org.gradle.cache.CacheRepository;
import org.gradle.cache.FileLockManager;
import org.gradle.cache.GlobalCache;
import org.gradle.cache.GlobalCacheLocations;
import org.gradle.cache.internal.CacheRepositoryServices;
import org.gradle.cache.internal.CacheScopeMapping;
import org.gradle.cache.internal.CrossBuildInMemoryCacheFactory;
import org.gradle.cache.internal.DefaultFileContentCacheFactory;
import org.gradle.cache.internal.DefaultGeneratedGradleJarCache;
import org.gradle.cache.internal.DefaultGlobalCacheLocations;
import org.gradle.cache.internal.FileContentCacheFactory;
import org.gradle.cache.internal.GradleUserHomeCleanupServices;
import org.gradle.cache.internal.InMemoryCacheDecoratorFactory;
import org.gradle.cache.internal.UsedGradleVersions;
import org.gradle.groovy.scripts.internal.CrossBuildInMemoryCachingScriptClassCache;
import org.gradle.groovy.scripts.internal.DefaultScriptSourceHasher;
import org.gradle.groovy.scripts.internal.RegistryAwareClassLoaderHierarchyHasher;
import org.gradle.groovy.scripts.internal.ScriptSourceHasher;
import org.gradle.initialization.ClassLoaderRegistry;
import org.gradle.initialization.ClassLoaderScopeRegistry;
import org.gradle.initialization.ClassLoaderScopeRegistryListenerManager;
import org.gradle.initialization.DefaultClassLoaderScopeRegistry;
import org.gradle.initialization.GradleUserHomeDirProvider;
import org.gradle.internal.classloader.ClasspathHasher;
import org.gradle.internal.classloader.DefaultHashingClassLoaderFactory;
import org.gradle.internal.classloader.HashingClassLoaderFactory;
import org.gradle.internal.classpath.CachedClasspathTransformer;
import org.gradle.internal.classpath.ClasspathBuilder;
import org.gradle.internal.classpath.ClasspathTransformerCacheFactory;
import org.gradle.internal.classpath.ClasspathWalker;
import org.gradle.internal.classpath.DefaultCachedClasspathTransformer;
import org.gradle.internal.classpath.DefaultClasspathTransformerCacheFactory;
import org.gradle.internal.concurrent.ExecutorFactory;
import org.gradle.internal.event.DefaultListenerManager;
import org.gradle.internal.event.ListenerManager;
import org.gradle.internal.execution.timeout.TimeoutHandler;
import org.gradle.internal.execution.timeout.impl.DefaultTimeoutHandler;
import org.gradle.internal.file.FileAccessTimeJournal;
import org.gradle.internal.hash.ClassLoaderHierarchyHasher;
import org.gradle.internal.jvm.JavaModuleDetector;
import org.gradle.internal.operations.CurrentBuildOperationRef;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistration;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.internal.vfs.FileSystemAccess;
import org.gradle.process.internal.ExecFactory;
import org.gradle.util.GradleVersion;

import java.io.File;
import java.util.List;

/**
 * Defines the shared services scoped to a particular Gradle user home directory. These services are reused across multiple builds and operations.
 */
public class GradleUserHomeScopeServices extends WorkerSharedUserHomeScopeServices {
    private final ServiceRegistry globalServices;

    public GradleUserHomeScopeServices(ServiceRegistry globalServices) {
        this.globalServices = globalServices;
    }

    public void configure(ServiceRegistration registration, GradleUserHomeDirProvider userHomeDirProvider) {
        File userHomeDir = userHomeDirProvider.getGradleUserHomeDirectory();
        registration.addProvider(new CacheRepositoryServices(userHomeDir, null));
        registration.addProvider(new GradleUserHomeCleanupServices());
        registration.add(ClasspathWalker.class);
        registration.add(ClasspathBuilder.class);
        registration.add(GradleUserHomeTemporaryFileProvider.class);
        for (PluginServiceRegistry plugin : globalServices.getAll(PluginServiceRegistry.class)) {
            plugin.registerGradleUserHomeServices(registration);
        }
    }

    DefaultListenerManager createListenerManager(DefaultListenerManager parent) {
        return parent.createChild(Scopes.UserHome.class);
    }

    GradleUserHomeScopeFileTimeStampInspector createFileTimestampInspector(CacheScopeMapping cacheScopeMapping) {
        return new GradleUserHomeScopeFileTimeStampInspector(cacheScopeMapping);
    }

    ScriptSourceHasher createScriptSourceHasher() {
        return new DefaultScriptSourceHasher();
    }

    CrossBuildInMemoryCachingScriptClassCache createCachingScriptCompiler(CrossBuildInMemoryCacheFactory cacheFactory) {
        return new CrossBuildInMemoryCachingScriptClassCache(cacheFactory);
    }

    ClassLoaderHierarchyHasher createClassLoaderHierarchyHasher(ClassLoaderRegistry registry, HashingClassLoaderFactory classLoaderFactory) {
        return new RegistryAwareClassLoaderHierarchyHasher(registry, classLoaderFactory);
    }

    HashingClassLoaderFactory createClassLoaderFactory(ClasspathHasher classpathHasher) {
        return new DefaultHashingClassLoaderFactory(classpathHasher);
    }

    ClassLoaderCache createClassLoaderCache(HashingClassLoaderFactory classLoaderFactory, ClasspathHasher classpathHasher, ListenerManager listenerManager) {
        DefaultClassLoaderCache cache = new DefaultClassLoaderCache(classLoaderFactory, classpathHasher);
        listenerManager.addListener(cache);
        return cache;
    }

    protected ClassLoaderScopeRegistryListenerManager createClassLoaderScopeRegistryListenerManager(ListenerManager listenerManager) {
        return new ClassLoaderScopeRegistryListenerManager(listenerManager);
    }

    protected ClassLoaderScopeRegistry createClassLoaderScopeRegistry(
        ClassLoaderRegistry classLoaderRegistry,
        ClassLoaderCache classLoaderCache,
        ClassLoaderScopeRegistryListenerManager listenerManager
    ) {
        return new DefaultClassLoaderScopeRegistry(classLoaderRegistry, classLoaderCache, listenerManager.getBroadcaster());
    }

    ClasspathTransformerCacheFactory createClasspathTransformerCache(
        CacheScopeMapping cacheScopeMapping,
        UsedGradleVersions usedGradleVersions
    ) {
        return new DefaultClasspathTransformerCacheFactory(
            cacheScopeMapping,
            usedGradleVersions
        );
    }

    GlobalCacheLocations createGlobalCacheLocations(List<GlobalCache> globalCaches) {
        return new DefaultGlobalCacheLocations(globalCaches);
    }

    CachedClasspathTransformer createCachedClasspathTransformer(
        CacheRepository cacheRepository,
        ClasspathTransformerCacheFactory classpathTransformerCacheFactory,
        FileAccessTimeJournal fileAccessTimeJournal,
        FileSystemAccess fileSystemAccess,
        ClasspathWalker classpathWalker,
        ClasspathBuilder classpathBuilder,
        ExecutorFactory executorFactory,
        GlobalCacheLocations globalCacheLocations,
        FileLockManager fileLockManager
    ) {
        return new DefaultCachedClasspathTransformer(
            cacheRepository,
            classpathTransformerCacheFactory,
            fileAccessTimeJournal,
            classpathWalker,
            classpathBuilder,
            fileSystemAccess,
            executorFactory,
            globalCacheLocations,
            fileLockManager
        );
    }

    ExecFactory createExecFactory(ExecFactory parent, FileResolver fileResolver, FileCollectionFactory fileCollectionFactory, Instantiator instantiator, ObjectFactory objectFactory, JavaModuleDetector javaModuleDetector) {
        return parent.forContext(fileResolver, fileCollectionFactory, instantiator, objectFactory, javaModuleDetector);
    }



    ClassPathRegistry createClassPathRegistry(ModuleRegistry moduleRegistry) {
        return new DefaultClassPathRegistry(
            new DefaultClassPathProvider(moduleRegistry)
        );
    }


    protected JavaModuleDetector createJavaModuleDetector(FileContentCacheFactory cacheFactory, FileCollectionFactory fileCollectionFactory) {
        return new JavaModuleDetector(cacheFactory, fileCollectionFactory);
    }

    DefaultGeneratedGradleJarCache createGeneratedGradleJarCache(CacheRepository cacheRepository) {
        String gradleVersion = GradleVersion.current().getVersion();
        return new DefaultGeneratedGradleJarCache(cacheRepository, gradleVersion);
    }

    FileContentCacheFactory createFileContentCacheFactory(ListenerManager listenerManager, FileSystemAccess fileSystemAccess, CacheRepository cacheRepository, InMemoryCacheDecoratorFactory inMemoryCacheDecoratorFactory) {
        return new DefaultFileContentCacheFactory(listenerManager, fileSystemAccess, cacheRepository, inMemoryCacheDecoratorFactory, null);
    }

    FileAccessTimeJournal createFileAccessTimeJournal(CacheRepository cacheRepository, InMemoryCacheDecoratorFactory cacheDecoratorFactory) {
        return new DefaultFileAccessTimeJournal(cacheRepository, cacheDecoratorFactory);
    }

    TimeoutHandler createTimeoutHandler(ExecutorFactory executorFactory, CurrentBuildOperationRef currentBuildOperationRef) {
        return new DefaultTimeoutHandler(executorFactory.createScheduled("execution timeouts", 1), currentBuildOperationRef);
    }
}
