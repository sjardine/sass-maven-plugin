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
package nl.geodienstencentrum.maven.plugin.sass;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for determining if compass compilation works based on a
 * simple war project.
 *
 * @author Mark C. Prins
 */
public class MavenCompassIntegrationTest {

	/** The Maven verifier. */
	private Verifier verifier;

	/** The test source directory. */
	private File testDir;

	/** The artifactid of the test project. */
	private final String ARTIFACTID = "maven-compass-test";

	/** The packaging of the test project. */
	private final String PACKAGING = "war";

	/**
	 * setUp the Maven project and verifier, execute the 'compile' goal.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testDir = ResourceExtractor.simpleExtractResources(
				this.getClass(), "/" + this.ARTIFACTID);

		this.verifier = new Verifier(this.testDir.getAbsolutePath());
		this.verifier.deleteArtifact(TestConstantsEnum.TEST_GROUPID.toString(), 
			this.ARTIFACTID, TestConstantsEnum.TEST_VERSION.toString(), 
			this.PACKAGING);
		this.verifier.setMavenDebug(true);
		this.verifier.executeGoal("compile");
	}

	/**
	 * test for error free execution.
	 *
	 * @throws Exception
	 *             if any
	 */
	@Test
	public void testErrorFree() throws Exception {
		this.verifier.resetStreams();
		this.verifier.verifyErrorFreeLog();
	}

	/**
	 * test for equal-ness of result end is a result is actualy there.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testCompareResults() throws Exception {
		final File expected = new File(this.testDir.getAbsolutePath()
				+ File.separator + "expected.css");

		final String compiled = this.verifier.getBasedir() + File.separator
				+ "target" + File.separator + this.ARTIFACTID + "-"
				+ TestConstantsEnum.TEST_VERSION + File.separator + "css" + File.separator
				+ "compiled.css";

		this.verifier.assertFilePresent(compiled);

		final File actual = new File(compiled);

		assertTrue("Compiled output should be as expected.",
				FileUtils.contentEqualsIgnoreEOL(expected, actual, "UTF-8"));
	}

	/**
	 * test for sourcemap existence.
	 */
	@Test
	public void testForSourceMap() {
		final String compiledMap = this.verifier.getBasedir() + File.separator
				+ "target" + File.separator + this.ARTIFACTID + "-"
				+ TestConstantsEnum.TEST_VERSION + File.separator + "css" + File.separator
				+ "compiled.css.map";

		this.verifier.assertFilePresent(compiledMap);
	}

	/**
	 * execute the 'clean' goal.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		this.verifier.executeGoal("clean");
	}
}
