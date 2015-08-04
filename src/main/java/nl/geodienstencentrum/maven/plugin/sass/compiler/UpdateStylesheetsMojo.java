/*
 * Copyright 2014-2015 Mark Prins, GeoDienstenCentrum.
 * Copyright 2010-2014 Jasig.
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;
import nl.geodienstencentrum.maven.plugin.sass.AbstractSassMojo;
import org.apache.commons.io.DirectoryWalker;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Mojo that compiles Sass sources into CSS files using
 * {@code update_stylesheets}.
 */
@Mojo(name = "update-stylesheets", defaultPhase = PROCESS_SOURCES)
public class UpdateStylesheetsMojo extends AbstractSassMojo {

	/**
	 * Execute the compiler script.
	 *
	 * @see org.apache.maven.plugin.Mojo#execute()
	 * @throws MojoExecutionException when the execution of the plugin
	 *         errored
	 * @throws MojoFailureException when the Sass compilation fails
	 *
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (this.isSkip()) {
			this.getLog().info("Skip compiling Sass templates");
			return;
		}
		boolean buildRequired = true;
		try {
			buildRequired = buildRequired();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not check file timestamps", e);
		}
		if (!buildRequired) {
			this.getLog().info("Skip compiling Sass templates, no changes.");
			return;
		}

		this.getLog().info("Compiling Sass templates");

		// build sass script
		final StringBuilder sassBuilder = new StringBuilder();
		this.buildBasicSassScript(sassBuilder);
		sassBuilder.append("Sass::Plugin.update_stylesheets");
		final String sassScript = sassBuilder.toString();

		// ...and execute
		this.executeSassScript(sassScript);
	}

	/**
	 * Returns true if a build is required.
	 *
	 * @return true if a build is required
	 * @throws IOException if one occurs checking the files and directories
	 */
	private boolean buildRequired() throws IOException {
		// If the target directory does not exist we need a build
		if (!buildDirectory.exists()) {
			return true;
		}

		final LastModifiedWalker sourceWalker = new LastModifiedWalker(getSassSourceDirectory());
		final LastModifiedWalker targetWalker = new LastModifiedWalker(destination);
		// If either directory is empty, we do a build to make sure
		if (sourceWalker.getCount() == 0 || targetWalker.getCount() == 0) {
			return true;
		}

		return sourceWalker.getYoungest() > targetWalker.getYoungest();
	}

	/**
	 * Directorywalker that looks at the lastModified timestamp of files.
	 *
	 * @see File#lastModified()
	 */
	private class LastModifiedWalker extends DirectoryWalker<Void> {

		private Long youngest;
		private Long oldest;
		private int count = 0;

		public LastModifiedWalker(final File startDirectory) throws IOException {
			walk(startDirectory, null);
			getLog().info("Checked " + count + " files for " + startDirectory);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void handleFile(final File file, final int depth,
		        final Collection<Void> results) throws IOException {
			long lastMod = file.lastModified();
			youngest = youngest == null ? lastMod : Math.max(youngest, lastMod);
			oldest = oldest == null ? lastMod : Math.min(oldest, lastMod);
			count++;
			super.handleFile(file, depth, results);
		}

		/**
		 * Get timestamp of the youngest file in the directory.
		 *
		 * @return timestamp of youngest file
		 * @see File#lastModified()
		 */
		public Long getYoungest() {
			return youngest;
		}

		/**
		 * Get timestamp of the oldest file in the directory.
		 *
		 * @return timestamp of oldest file
		 * @see File#lastModified()
		 */
		public Long getOldest() {
			return oldest;
		}

		/**
		 * get number of files in the directory.
		 *
		 * @return number of files
		 */
		public int getCount() {
			return count;
		}
	}
}
