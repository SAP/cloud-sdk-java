package com.sap.cloud.sdk.testutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.input.XmlStreamReader;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.internal.MojoDescriptorCreator;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptorBuilder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MavenPluginExtension implements BeforeAllCallback, AfterEachCallback, BeforeEachCallback, AfterAllCallback
{
    private ComponentConfigurator configurator;
    private DefaultPlexusContainer container;
    private Map<String, MojoDescriptor> goals;
    private String goal;
    private File baseDir;
    private Mojo mojo;
    private Class<?> baseClass;

    public MavenPluginExtension withGoal( final String goal )
    {
        this.goal = goal;
        return this;
    }

    public MavenPluginExtension withBasePath( final File basePath )
    {
        this.baseDir = basePath;
        return this;
    }

    public MavenPluginExtension withClass( final Class<?> baseClass )
    {
        this.baseClass = baseClass;
        this.baseDir = new File(baseClass.getClassLoader().getResource(baseClass.getSimpleName()).getFile());
        return this;
    }

    private Map<String, MojoDescriptor> loadGoals()
        throws ComponentLookupException,
            IOException,
            PlexusConfigurationException
    {
        final String pluginDescriptorLocation = "/META-INF/maven/plugin.xml";
        final URL resource = baseClass.getResource(pluginDescriptorLocation);
        final String path = Objects.requireNonNull(resource, "plugin.xml file not found.").getPath();
        final File artifactFile = new File(path.substring(0, path.length() - pluginDescriptorLocation.length()));

        final XmlStreamReader reader = XmlStreamReader.builder().setInputStream(resource.openStream()).get();

        final Map<String, Object> contextData =
            container
                .getContext()
                .getContextData()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Object::toString, Function.identity()));
        try(
            final InterpolationFilterReader interpolationFilterReader =
                new InterpolationFilterReader(new BufferedReader(reader), contextData) ) {

            final PluginDescriptor pluginDescriptor = new PluginDescriptorBuilder().build(interpolationFilterReader);

            final Artifact artifact =
                container
                    .lookup(RepositorySystem.class)
                    .createArtifact(
                        pluginDescriptor.getGroupId(),
                        pluginDescriptor.getArtifactId(),
                        pluginDescriptor.getVersion(),
                        ".jar");
            artifact.setFile(artifactFile);
            pluginDescriptor.setPluginArtifact(artifact);
            pluginDescriptor.setArtifacts(Collections.singletonList(artifact));
            pluginDescriptor.getComponents().forEach(container::addComponentDescriptor);
            return pluginDescriptor
                .getMojos()
                .stream()
                .collect(Collectors.toMap(MojoDescriptor::getGoal, Function.identity()));
        }
    }

    private ContainerConfiguration getContainerConfiguration()
    {
        return new DefaultContainerConfiguration()
            .setClassWorld(new ClassWorld("plexus.core", Thread.currentThread().getContextClassLoader()))
            .setClassPathScanning(PlexusConstants.SCANNING_INDEX)
            .setAutoWiring(true)
            .setName("maven");
    }

    @Override
    public void afterEach( final ExtensionContext extensionContext )
    {
        mojo = null;
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public void beforeEach( final ExtensionContext extensionContext )
        throws Exception
    {
        final String role = "org.apache.maven.plugin.Mojo";
        final MojoDescriptor descriptor = goals.get(goal);
        this.mojo = (Mojo) container.lookup(role, descriptor.getRoleHint());

        // project
        final File pom = new File(baseDir, "pom.xml");
        final MavenExecutionRequest request = new DefaultMavenExecutionRequest().setBaseDirectory(baseDir);
        final DefaultRepositorySystemSession repoSession = new DefaultRepositorySystemSession();
        final ProjectBuildingRequest conf = request.getProjectBuildingRequest().setRepositorySession(repoSession);
        final MavenProject project = container.lookup(ProjectBuilder.class).build(pom, conf).getProject();

        // session
        final MavenExecutionResult res = new DefaultMavenExecutionResult();
        final MavenSession session = new MavenSession(container, MavenRepositorySystemUtils.newSession(), request, res);
        session.setCurrentProject(project);
        session.setProjects(Collections.singletonList(project));

        // execution
        final MojoExecution execution = new MojoExecution(descriptor);
        final Xpp3Dom executionConfiguration = new Xpp3Dom("configuration");
        final Xpp3Dom defaultConfiguration = MojoDescriptorCreator.convert(descriptor);
        final Xpp3Dom finalConfiguration = new Xpp3Dom("configuration");
        if( descriptor.getParameters() != null ) {
            for( final Parameter parameter : descriptor.getParameters() ) {
                Xpp3Dom parameterConfiguration = executionConfiguration.getChild(parameter.getAlias());
                final Xpp3Dom parameterDefaults = defaultConfiguration.getChild(parameter.getName());
                parameterConfiguration = Xpp3Dom.mergeXpp3Dom(parameterConfiguration, parameterDefaults, Boolean.TRUE);
                finalConfiguration.addChild(new Xpp3Dom(parameterConfiguration, parameter.getName()));
            }
        }
        execution.setConfiguration(finalConfiguration);

        // evaluation
        final ExpressionEvaluator evaluator = new PluginParameterExpressionEvaluator(session, execution);
        final Plugin pl = session.getCurrentProject().getPlugin(descriptor.getPluginDescriptor().getPluginLookupKey());
        final Xpp3Dom confXml = Xpp3Dom.mergeXpp3Dom((Xpp3Dom) pl.getConfiguration(), execution.getConfiguration());
        final PlexusConfiguration pluginConfiguration = new XmlPlexusConfiguration(confXml);
        configurator.configureComponent(mojo, pluginConfiguration, evaluator, container.getContainerRealm());
    }

    @SuppressWarnings( "unchecked" )
    public <T extends Mojo> T getMojo()
    {
        return (T) mojo;
    }

    @Override
    public void afterAll( final ExtensionContext extensionContext )
    {
        container.dispose();
        container = null;
        configurator = null;
        goals = null;
    }

    @Override
    public void beforeAll( final ExtensionContext extensionContext )
        throws Exception
    {
        container = new DefaultPlexusContainer(getContainerConfiguration());
        configurator = container.lookup(ComponentConfigurator.class, "basic");
        goals = loadGoals();
    }
}
