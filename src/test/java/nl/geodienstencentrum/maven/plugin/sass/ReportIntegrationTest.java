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
package nl.geodienstencentrum.maven.plugin.sass;

import java.io.File;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for determining 'site' works.
 *
 * @author mprins
 */
public class ReportIntegrationTest {

	/** The Maven verifier. */
	private Verifier verifier;

	/** The test source directory. */
	private File testDir;

	/** The artifactId of the test project. */
	private final String ARTIFACTID = "maven-lint-test";

	/** The packaging of the test project. */
	private final String PACKAGING = "pom";

	/**
	 * Delete any of this artifact in the local repository, setup the Maven
	 * project and verifier and execute the 'compile' goal.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testDir = ResourceExtractor.simpleExtractResources(this.getClass(),
				"/" + this.ARTIFACTID);
		this.verifier = new Verifier(this.testDir.getAbsolutePath());
		this.verifier.deleteArtifact(TestConstantsEnum.TEST_GROUPID.toString(),
				this.ARTIFACTID, TestConstantsEnum.TEST_VERSION.toString(),
				this.PACKAGING);
		//boolean debug = new Boolean(System.getProperty("debug"));
		//this.verifier.setMavenDebug(debug);
		this.verifier.setMavenDebug(true);
		this.verifier.executeGoal("site");
	}

	/**
	 * test for error free execution.
	 *
	 * @throws Exception
	 *             if any
	 */
	@Test
	public void testErrorFree() throws Exception {
		this.verifier.verifyErrorFreeLog();
	}

	/**
	 * test if results are actually there.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testForResults() throws Exception {
		final String lintXML = this.verifier.getBasedir() + File.separator
				+ "target" + File.separator + "scss-lint.xml";

		final String lintHTMLReport = this.verifier.getBasedir() + File.separator
				+ "target" + File.separator + "site" + File.separator + "scss-lint.html";

		this.verifier.assertFilePresent(lintXML);
		this.verifier.assertFilePresent(lintHTMLReport);
	}

	/**
	 *  reset after test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		this.verifier.setMavenDebug(false);
		this.verifier.executeGoal("clean");
		this.verifier.resetStreams();
	}
}
