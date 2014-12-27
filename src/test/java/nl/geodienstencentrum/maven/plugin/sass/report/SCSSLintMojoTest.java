/*
 * Copyright 2014 Mark Prins, GeoDienstenCentrum
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for
 * {@link nl.geodienstencentrum.maven.plugin.sass.report.SCSSLintMojo}.
 *
 * @author Mark
 *
 */
public class SCSSLintMojoTest {

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
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link nl.geodienstencentrum.maven.plugin.sass.report.SCSSLintMojo#execute()}
	 * .
	 *
	 * @throws Exception if any
	 */
	@Test
	public void testExecute() throws Exception {

		final File projectCopy = this.resources
				.getBasedir("maven-lint-test");
		final File pom = new File(projectCopy, "pom.xml");
		assertNotNull("POM file should not be null.", pom);
		assertTrue("POM file should exist as file.",
				pom.exists() && pom.isFile());

		final SCSSLintMojo myMojo = (SCSSLintMojo) this.rule
				.lookupConfiguredMojo(projectCopy, "lint");
		assertNotNull(myMojo);

		myMojo.getLog().debug("directory: " + projectCopy);

		// test if execution was succesful, if not fail
		myMojo.execute();

		TestResources.assertDirectoryContents(
				// target directory
				projectCopy.toPath().resolve("target/site").toFile(),
				new String[]{"scss-lint.xml"});

		// TestResources.assertFileContents(projectCopy,
		// 				"scss-lint.expected",
		//				"target/site/scss-lint.xml");
	}

}
