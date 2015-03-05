/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.geodienstencentrum.maven.plugin.sass.report;

import java.io.File;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author mprins
 */
public class SCSSLintReportMojoTest {

	/**
	 * Test resources.
	 */
	@Rule
	public TestResources resources = new TestResources();

	/**
	 * test rule.
	 */
	@Rule
	public MojoRule rule = new MojoRule();

	/**
	 * Test method for
	 * {@link nl.geodienstencentrum.maven.plugin.sass.report.SCSSLintMojo#execute()}
	 * .
	 *
	 * @throws Exception if any
	 */
	@Test
	public void testLintAndReport() throws Exception {
		final File projectCopy = this.resources
				.getBasedir("maven-lint-test");
		final File pom = new File(projectCopy, "pom.xml");
		assumeNotNull("POM file should not be null.", pom);
		assumeTrue("POM file should exist as file.",
				pom.exists() && pom.isFile());

		this.rule.executeMojo(projectCopy, "scss-lint");
		TestResources.assertDirectoryContents(
				// target directory
				projectCopy.toPath().resolve("target").toFile(),
				new String[]{"scss-lint.xml"});

		this.rule.executeMojo(projectCopy, "scss-lint-report");
		TestResources.assertDirectoryContents(
				// site directory
				projectCopy.toPath().resolve("target/site").toFile(),
				new String[]{"scss-lint.html"});
	}
}
