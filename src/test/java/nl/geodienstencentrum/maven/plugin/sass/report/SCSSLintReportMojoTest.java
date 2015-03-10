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

import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
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

	/**
	 * Test method for
	 * {@link nl.geodienstencentrum.maven.plugin.sass.report.SCSSLintMojo#execute()}
	 * .
	 *
	 * @throws Exception if any
	 */
	@Test
	@Ignore("this test seems to be failing as the rule does not execute the forked compile lifecycle, thus no xml file is present")
	public void testLintAndReportWithResources() throws Exception {
		final File projectCopy = this.resources
				.getBasedir("maven-lint-resources-test");
		final File pom = new File(projectCopy, "pom.xml");
		assumeNotNull("POM file should not be null.", pom);
		assumeTrue("POM file should exist as file.",
				pom.exists() && pom.isFile());

		this.rule.executeMojo(projectCopy, "scss-lint-report");
		TestResources.assertDirectoryContents(
				// target directory
				projectCopy.toPath().resolve("target").toFile(),
				new String[]{"scss-lint.xml",
								"target/site/scss-lint.html"
							});
		TestResources.assertDirectoryContents(
				// site directory
				projectCopy.toPath().resolve("target/site").toFile(),
				new String[]{"scss-lint.html"});
	}
}
