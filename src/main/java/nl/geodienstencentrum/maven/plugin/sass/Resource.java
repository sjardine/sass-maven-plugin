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
package nl.geodienstencentrum.maven.plugin.sass;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

/**
 * A resource describes a set of sass files to compile and a target.
 */
public class Resource {

	/** Directories containing Sass files. */
	protected FileSet source;

	/**
	 * Defines an additional path section when calculating the destination for
	 * the SCSS file. Allows, for example
	 * "/media/skins/universality/coal/scss/portal.scss" to end up at
	 * "/media/skins/universality/coal/portal.css" by specifying ".."
	 */
	protected String relativeOutputDirectory;

	/** Where to put the compiled CSS files. */
	protected File destination;

	/**
	 * Gets the source directories and target destinations.
	 *
	 * @param log
	 *            the maven logging instance to use for messages
	 * @return the directories and destinations, may be an empty map if the
	 *            source does not exist.
	 */
	public Map<String, String> getDirectoriesAndDestinations(Log log) {
		final File sourceDirectory = new File(this.source.getDirectory());
		final Map<String, String> result = new LinkedHashMap<>();

		if (!sourceDirectory.exists()) {
			log.error("Specified sourcedirectory (" + sourceDirectory + ") does not exist.");
			return result;
		}

		// Scan for directories
		final DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(sourceDirectory);
		scanner.setIncludes(this.source.getIncludes().toArray(
		        new String[this.source.getIncludes().size()]));
		scanner.setExcludes(this.source.getExcludes().toArray(
		        new String[this.source.getExcludes().size()]));

		// Add default excludes to the list of excludes, see
		// http://plexus.codehaus.org/plexus-utils/apidocs/org/codehaus/plexus/util/AbstractScanner.html#DEFAULTEXCLUDES
		// or
		// http://plexus.codehaus.org/plexus-utils/apidocs/org/codehaus/plexus/util/AbstractScanner.html#addDefaultExcludes()
		scanner.addDefaultExcludes();
		scanner.scan();

		result.put(FilenameUtils.separatorsToUnix(sourceDirectory.toString()),
		        FilenameUtils.separatorsToUnix(this.destination.toString()));

		for (final String included : scanner.getIncludedDirectories()) {
			if (!included.isEmpty()) {
				final String subdir = StringUtils.difference(
				        sourceDirectory.toString(), included);

				final File sourceDir = new File(sourceDirectory, included);

				File destDir = new File(this.destination, subdir);
				if (this.relativeOutputDirectory != null
				        && !this.relativeOutputDirectory.isEmpty()) {
					destDir = new File(destDir, this.relativeOutputDirectory);
				}

				result.put(
				        FilenameUtils.separatorsToUnix(sourceDir.toString()),
				        FilenameUtils.separatorsToUnix(destDir.toString()));
			}
		}

		return result;
	}
}
