package com.vaadin.integration.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.gwt.shell.AbstractGwtShellMojo;
import org.codehaus.mojo.gwt.shell.CompileMojo;

/**
 * Updates Vaadin widgetsets based on other widgetset packages on the classpath.
 * It is assumed that the project does not directly contain other GWT modules.
 * In part adapted from gwt-maven-plugin {@link CompileMojo}.
 *
 * @goal update-widgetset
 * @requiresDependencyResolution compile
 * @phase process-classes
 */
public class UpdateWidgetsetMojo extends AbstractGwtShellMojo {
    public static final String WIDGETSET_BUILDER_CLASS = "com.vaadin.server.widgetsetutils.WidgetSetBuilder";

    public static final String GWT_MODULE_EXTENSION = ".gwt.xml";

    /**
     * {@inheritDoc}
     *
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public final void doExecute() throws MojoExecutionException,
            MojoFailureException {
        if ("pom".equals(getProject().getPackaging())) {
            getLog().info("GWT compilation is skipped");
            return;
        }

        // compile one widgetset at a time
        String[] modules = getModules();
        if (modules.length > 0) {
            for (String module : modules) {
                updateWidgetset(module);
            }
        } else {
            // ask the user to explicitly indicate the widgetset to create
            getLog().info("No widgetsets to update.");
            getLog().info(
                    "To create a widgetset, define a non-existing module in your pom.xml .");
        }

    }

    private void updateWidgetset(String module) throws MojoExecutionException {
        // class path order has "compile" sources first as it should

        getLog().info("Updating widgetset " + module);

        JavaCommand cmd = new JavaCommand(WIDGETSET_BUILDER_CLASS);
        cmd.withinScope( Artifact.SCOPE_COMPILE );
        cmd.withinClasspath(getGwtUserJar()).withinClasspath(getGwtDevJar());

        cmd.arg(module);
        cmd.execute();
    }

}
