/*
 * Copyright 2014-2015 Mark Prins, GeoDienstenCentrum
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
package nl.geodienstencentrum.maven.plugin.sass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.util.List;

import nl.geodienstencentrum.maven.plugin.sass.compiler.UpdateStylesheetsMojo;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

/**
 * Testcase for {@link nl.geodienstencentrum.maven.plugin.sass.Resource }.
 *
 * @author Mark C. Prins
 *
 */
public class ResourceTest {
	/** Test resources. */
	@Rule
	public TestResources resources = new TestResources();

	/** test rule. */
	@Rule
	public MojoRule rule = new MojoRule();

	/**
	 * Test method for
	 * {@link nl.geodienstencentrum.maven.plugin.sass.Resource#getDirectoriesAndDestinations(org.apache.maven.plugin.logging.Log) }
	 * .
	 *
	 * @throws Exception
	 *             if any
	 * @see nl.geodienstencentrum.maven.plugin.sass.Resource#getDirectoriesAndDestinations(org.apache.maven.plugin.logging.Log)
	 */
	@Test
	public void testGetDirectoriesAndDestinations() throws Exception {
		final File projectCopy = this.resources
				.getBasedir("maven-resources-test");
		final File pom = new File(projectCopy, "pom.xml");

		assumeNotNull("POM file should not be null.", pom);
		assumeTrue("POM file should exist as file.",
				pom.exists() && pom.isFile());

		final UpdateStylesheetsMojo myMojo = (UpdateStylesheetsMojo) this.rule
				.lookupConfiguredMojo(projectCopy, "update-stylesheets");
		assumeNotNull(myMojo);

		@SuppressWarnings("unchecked")
		final List<Resource> reslist = (List<Resource>) this.rule
		.getVariableValueFromObject(myMojo, "resources");
		final Resource res = reslist.get(0);
		assertEquals(projectCopy.getAbsolutePath() + "/src/main/scss",
				res.source.getDirectory());
		// directory should return ${basedir}/src/main/scss
		// includes should return *.scss
	}

}
