/*
 * Copyright 2015 Mark Prins, GeoDienstenCentrum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.geodienstencentrum.maven.plugin.sass.report;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;

/**
 * SCSSLintMojo executes scss-lint-report goal.
 *
 * @author mprins
 * @since 2.3
 */
@Mojo(name = "scss-lint-report",
		defaultPhase = LifecyclePhase.SITE,
		threadSafe = true
)
@Execute(goal = "scss-lint")
public class SCSSLintReportMojo extends AbstractMavenReport {

	/**
	 * Specifies if the build should fail upon a violation.
	 */
	@Parameter(defaultValue = "false")
	protected boolean failOnError;

	/**
	 * output file for the plugin.
	 *
	 * @since 2.3
	 */
	@Parameter(defaultValue = "scss-lint", property = "outputName", required = true)
	private String outputName;

	@Parameter(defaultValue = "${project.build.directory}/site/", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Component
	private Renderer siteRenderer;

	/**
	 * Build the report, for now ignoring the locale.
	 * @param locale ignored
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void executeReport(Locale locale) {
		SCSSLintReportGenerator generator = new SCSSLintReportGenerator(
				getSink(), this.getDescription(locale),
				new File(getProject().getBasedir() + "/target", "scss-lint.xml"),
				getLog());
		generator.generateReport();
	}

	@Override
	protected Renderer getSiteRenderer() {
		return siteRenderer;
	}

	@Override
	protected String getOutputDirectory() {
		return outputDirectory.getAbsolutePath();
	}

	@Override
	protected MavenProject getProject() {
		return project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOutputName() {
		return this.outputName;
	}

	@Override
	public String getName(Locale locale) {
		return "scss-lint report";
	}

	@Override
	public String getDescription(Locale locale) {
		return "A scss-lint report.";
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("scss-lint-report", locale, this.getClass().getClassLoader());
	}
}
