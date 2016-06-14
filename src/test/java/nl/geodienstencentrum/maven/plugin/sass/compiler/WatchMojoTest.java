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
package nl.geodienstencentrum.maven.plugin.sass.compiler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Testcase for
 * {@link nl.geodienstencentrum.maven.plugin.sass.compiler.WatchMojo }.
 *
 * @author Mark C. Prins
 */
public class WatchMojoTest {

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

	/** sleep time for the watcher. */
	private static long SLEEP_TIME;

	/**
	 * check if running on windows.
	 */
	private static boolean IS_WINDOWS;

	@BeforeClass
	public static void readEnvironment() {
		try {
			SLEEP_TIME = Long.parseLong(System.getProperty("WatchMojoTestSleeptime"));
			if (SLEEP_TIME < 15000) {
				SLEEP_TIME = 15000;
			}
		} catch (NumberFormatException e) {
			SLEEP_TIME = 15000;
		}
		System.out.println("[TEST] SLEEP_TIME set to " + SLEEP_TIME);
		IS_WINDOWS = (System.getProperty("os.name").toLowerCase().contains("win"));
	}

	/**
	 * Test method for
	 * {@link nl.geodienstencentrum.maven.plugin.sass.compiler.WatchMojo#execute() }.
	 *
	 * @throws Exception if any
	 * @see nl.geodienstencentrum.maven.plugin.sass.compiler.WatchMojo#execute()
	 */
	@Test
	public void testExecute() throws Exception {
		// setup mojo and start execution
		final File projectCopy = this.resources
                .getBasedir("maven-sass-test");
		final File pom = new File(projectCopy, "pom.xml");
		assertNotNull("POM file should not be null.", pom);
		assertTrue("POM file should exist as file.",
				pom.exists() && pom.isFile());

		final WatchMojo myMojo = (WatchMojo) this.rule
				.lookupConfiguredMojo(projectCopy, "watch");
		assertNotNull("the 'watch' mojo should exist", myMojo);
		// start 'watch'ing
		synchronized (this) {
			new Thread("sassWatcher") {
				@Override
				public void run() {
					try {
						System.out.println("Start Sass watcher thread.");
						myMojo.execute();
					} catch (MojoExecutionException | MojoFailureException e) {
						org.junit.Assert.fail("Sass watcher thread execution failed: " + e);
						this.interrupt();
					}
				}
			}.start();
			// wait for watcher to start up...
			System.out.println("[TEST] Waiting " + SLEEP_TIME / 1000 + " sec.");
			this.wait(SLEEP_TIME);

			// modify a file in the project
			System.out.println("[TEST] Modify (touch) '_colours.scss'.");
			TestResources.touch(new File(projectCopy.getAbsolutePath() + "/src/main/sass/"),
					"_colours.scss");

			// wait for watcher to catch up...
			System.out.println("[TEST] Waiting " + SLEEP_TIME * 2 / 1000 + " sec.");
			this.wait(SLEEP_TIME * 2);

			// modify another file in the project
			System.out.println("[TEST] Create 'print.scss'.");
			TestResources.cp(new File(projectCopy.getAbsolutePath() + "/src/main/sass/"),
					"compiled.scss", "print.scss");

			// wait for watcher to catch up...
			System.out.println("[TEST] Waiting " + SLEEP_TIME * 2 / 1000 + " sec.");
			this.wait(SLEEP_TIME * 2);
			this.notifyAll();
		}

		// done; lets check compilation results
		TestResources.assertFileContents(projectCopy, "expected.css",
                "target/maven-sass-test-1.0-SNAPSHOT/css/compiled.css");

		// skip for now because the jruby watcher fails to see the changes on windows
		// this would be better with org.junit.Assume.assumeThat.assumeThat
		// but since TestResources assertions are void...
		TestResources.assertDirectoryContents(
				new File(projectCopy.getAbsolutePath()
                        + "/target/maven-sass-test-1.0-SNAPSHOT/css/"),
            				"compiled.css.map", "compiled.css",
				"print.css.map", "print.css");
		// this may fail when line endings differ, eg. on Windows
		// set up git to check out with native file endings
		TestResources.assertFileContents(projectCopy,
                "expected-print.css",
                "target/maven-sass-test-1.0-SNAPSHOT/css/print.css");
	}
}
