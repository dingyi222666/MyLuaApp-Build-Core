package org.gradle.launcher;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.internal.StartParameterInternal;
import org.gradle.api.logging.configuration.LoggingConfiguration;
import org.gradle.configuration.GradleLauncherMetaData;
import org.gradle.initialization.BuildLayoutParameters;
import org.gradle.initialization.BuildRequestContext;
import org.gradle.initialization.ReportedException;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.buildevents.BuildExceptionReporter;
import org.gradle.internal.classpath.ClassPath;
import org.gradle.internal.concurrent.CompositeStoppable;
import org.gradle.internal.concurrent.Stoppable;
import org.gradle.internal.logging.DefaultLoggingConfiguration;
import org.gradle.internal.logging.LoggingManagerInternal;
import org.gradle.internal.logging.services.LoggingServiceRegistry;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.internal.nativeintegration.console.ConsoleDetector;
import org.gradle.internal.nativeintegration.console.ConsoleMetaData;
import org.gradle.internal.nativeintegration.services.NativeServices;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.internal.service.ServiceRegistryBuilder;
import org.gradle.internal.service.scopes.GlobalScopeServices;
import org.gradle.internal.service.scopes.GradleUserHomeScopeServiceRegistry;
import org.gradle.internal.vfs.FileSystemAccess;
import org.gradle.internal.vfs.VirtualFileSystem;
import org.gradle.launcher.bootstrap.ExecutionListener;
import org.gradle.launcher.cli.DebugLoggerWarningAction;
import org.gradle.launcher.cli.ExceptionReportingAction;
import org.gradle.launcher.cli.NativeServicesInitializingAction;
import org.gradle.launcher.cli.RunBuildAction;
import org.gradle.launcher.cli.WelcomeMessageAction;
import org.gradle.launcher.configuration.BuildLayoutResult;
import org.gradle.launcher.exec.BuildActionExecuter;
import org.gradle.launcher.exec.BuildActionParameters;
import org.gradle.launcher.exec.BuildExecuter;
import org.gradle.launcher.exec.DefaultBuildActionParameters;

import java.io.File;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class TestGradleLauncher {

    private final StartParameterInternal startParameter;
    private ServiceRegistry globalServices;
    private ServiceRegistry loggingServices;

    private OutputStream gradleOutputStream = System.out;
    private OutputStream gradleErrorStream = System.err;

    TestGradleLauncher(StartParameterInternal startParameter) {
        this.startParameter = startParameter;
    }

    private static GradleLauncherMetaData clientMetaData() {
        return new GradleLauncherMetaData();
    }

    private ServiceRegistry createLoggingServices() {
        return LoggingServiceRegistry.newNestedLogging();
    }


    private ServiceRegistry createGlobalServices(ServiceRegistry loggingServices) {
        return ServiceRegistryBuilder
                .builder()
                .parent(loggingServices)
                .parent(NativeServices.getInstance())
                .displayName("global services")
                .provider(new GlobalScopeServices(true, ClassPath.EMPTY))
                .build();
    }

    @Nullable
    public ServiceRegistry getGradleUserHomeServices() {
        return globalServices
                .get(GradleUserHomeScopeServiceRegistry.class)
                .getServicesFor(startParameter.getGradleUserHomeDir());
    }


    private void prepareServices() {
        NativeServices.initializeOnDaemon(startParameter.getGradleUserHomeDir());
        loggingServices = createLoggingServices();
        globalServices = createGlobalServices(loggingServices);

        ServiceRegistry gradleUserHomeScopeServices = getGradleUserHomeServices();
        if (gradleUserHomeScopeServices == null) {
            return;
        }
        gradleUserHomeScopeServices.get(VirtualFileSystem.class);
        gradleUserHomeScopeServices.get(FileSystemAccess.class);

    }

    public void execute(String... taskNames) {

        prepareServices();

        GradleLauncherMetaData launcherMetaData = clientMetaData();

        long buildStartedTime = System.currentTimeMillis();

        startParameter.setTaskNames(List.of(taskNames));
        Action<ExecutionListener> executionAction = (listener) -> {
            runBuildAndCloseServices(
                    startParameter,
                    launcherMetaData,
                    buildStartedTime,
                    globalServices.get(BuildExecuter.class),
                    globalServices,
                    globalServices.get(GradleUserHomeScopeServiceRegistry.class)
            ).run();
        };

        LoggingConfiguration loggingConfiguration = new DefaultLoggingConfiguration();

        loggingConfiguration
                .setLogLevel(startParameter.getLogLevel());
        loggingConfiguration
                .setShowStacktrace(startParameter.getShowStacktrace());
        loggingConfiguration
                .setConsoleOutput(startParameter.getConsoleOutput());

        loggingConfiguration
                .setWarningMode(startParameter.getWarningMode());

        Action<Throwable> reporter = new BuildExceptionReporter(loggingServices.get(StyledTextOutputFactory.class), loggingConfiguration, launcherMetaData)
        ;

        ExecutionListener executionListener = failure -> {
            if (!(failure instanceof ReportedException)) {
                throw UncheckedException.throwAsUncheckedException(failure);
            }
        };

        BuildLayoutParameters layoutParameters = new BuildLayoutParameters();
        layoutParameters.setCurrentDir(startParameter.getCurrentDir());
        layoutParameters.setProjectDir(startParameter.getProjectDir());
        layoutParameters.setGradleUserHomeDir(startParameter.getGradleUserHomeDir());

        BuildLayoutResult buildLayout = new TestGradleLauncher.Result(layoutParameters);

        LoggingManagerInternal loggingManager = loggingServices.getFactory(LoggingManagerInternal.class).create();
        loggingManager.setLevelInternal(loggingConfiguration.getLogLevel());

        loggingManager.attachConsole(gradleOutputStream, gradleErrorStream, loggingConfiguration.getConsoleOutput(),
                NativeServices.getInstance().get(ConsoleDetector.class).getConsole());

        loggingManager.start();
        try {
            Action<ExecutionListener> exceptionReportingAction =
                    new ExceptionReportingAction(reporter, loggingManager,
                            new NativeServicesInitializingAction(buildLayout, loggingConfiguration, loggingManager,
                                    new WelcomeMessageAction(buildLayout,
                                            new DebugLoggerWarningAction(loggingConfiguration, executionAction))));
            exceptionReportingAction.execute(executionListener);
        } finally {
            loggingManager.stop();
        }
    }


    public TestGradleLauncher redirectOutputStream(OutputStream stream) {
        gradleOutputStream = stream;
        return this;
    }


    public TestGradleLauncher redirectErrorStream(OutputStream errorStream) {
        gradleErrorStream = errorStream;
        return this;
    }


    private static class Result implements BuildLayoutResult {
        private final BuildLayoutParameters buildLayout;

        public Result(BuildLayoutParameters buildLayout) {
            this.buildLayout = buildLayout;
        }

        @Override
        public void applyTo(BuildLayoutParameters buildLayout) {
            buildLayout.setCurrentDir(this.buildLayout.getCurrentDir());
            buildLayout.setProjectDir(this.buildLayout.getProjectDir());
            buildLayout.setGradleUserHomeDir(this.buildLayout.getGradleUserHomeDir());
            buildLayout.setGradleInstallationHomeDir(this.buildLayout.getGradleInstallationHomeDir());
        }

        @Override
        public void applyTo(StartParameterInternal startParameter) {
            startParameter.setProjectDir(buildLayout.getProjectDir());
            startParameter.setCurrentDir(buildLayout.getCurrentDir());
            startParameter.setGradleUserHomeDir(buildLayout.getGradleUserHomeDir());
        }

        @Override
        public File getGradleUserHomeDir() {
            return buildLayout.getGradleUserHomeDir();
        }
    }

    private Runnable runBuildAndCloseServices(
            StartParameterInternal startParameter,
            GradleLauncherMetaData metaData,
            long buildStartTime,
            BuildActionExecuter<BuildActionParameters, BuildRequestContext> executer,
            ServiceRegistry sharedServices,
            Object... stopBeforeSharedServices) {
        BuildActionParameters
                parameters = createBuildActionParameters(startParameter);

        Stoppable stoppable = new CompositeStoppable();
                /*.add(stopBeforeSharedServices).add(sharedServices);*/
        return new RunBuildAction(executer, startParameter, metaData, buildStartTime, parameters, sharedServices, stoppable);
    }

    private BuildActionParameters createBuildActionParameters(StartParameter startParameter) {
        return new DefaultBuildActionParameters(
//                daemonParameters.getEffectiveSystemProperties(),
                Collections.emptyMap(),
//                daemonParameters.getEnvironmentVariables(),
                Collections.emptyMap(),
                startParameter.getCurrentDir(),
                startParameter.getLogLevel(),
//                daemonParameters.isEnabled(),
                false,
                ClassPath.EMPTY
        );
    }

    public synchronized static TestGradleLauncher createLauncher(Action<StartParameter> configAction) {
        StartParameterInternal startParameterInternal = new StartParameterInternal();

        configAction.execute(startParameterInternal);
        return new TestGradleLauncher(startParameterInternal);
    }
}
