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
package nl.geodienstencentrum.maven.plugin.sass.report;

import com.google.common.primitives.Ints;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import nl.geodienstencentrum.maven.plugin.sass.AbstractSassMojo;
import nl.geodienstencentrum.maven.plugin.sass.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * SCSSLintMojo executes scss-lint goal.
 *
 * @author mprins
 * @since 2.3
 */
@Mojo(name = "scss-lint",
		defaultPhase = LifecyclePhase.COMPILE,
		executionStrategy = "once-per-session",
		threadSafe = true
	)
public class SCSSLintMojo extends AbstractSassMojo {

	/**
	 * Output file for the plugin.
	 *
	 * @since 2.3
	 */
	@Parameter(defaultValue = "${project.build.directory}/scss-lint.xml", readonly = true)
	private File outputFile;

	/**
	 * scss-lint exit codes and messages.
	 */
	public enum ExitCode {

		CODE_0(0, "No lints were found"),
		CODE_1(1, "Lints with a severity of 'warning' were reported (no errors)"),
		CODE_2(2, "One or more errors were reported (and any number of warnings)"),
		CODE_64(64, "Command line usage error (invalid flag, etc.)"),
		CODE_66(66, "Input file did not exist or was not readable"),
		CODE_70(70, "Internal software error"),
		CODE_78(78, "Configuration error");

		private static final HashMap<Integer, ExitCode> lookup = new HashMap<>();

		static {
			for (ExitCode c : EnumSet.allOf(ExitCode.class)) {
				lookup.put(c.code, c);
			}
		}

		private final String msg;
		private final int code;

		ExitCode(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		String msg() {
			return msg;
		}

		int code() {
			return code;
		}

		static ExitCode getExitCode(int code) {
			return lookup.get(code);
		}

		@Override
		public String toString() {
			return code + ": " + msg;
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final Log log = this.getLog();

		// create directory
		this.outputFile.getParentFile().mkdirs();

		log.info("Linting Sass sources in: " + this.getSassSourceDirectory());

		final StringBuilder sassScript = new StringBuilder();
		this.buildBasicSassScript(sassScript);

		log.debug("scss-lint ruby script:\n" + sassScript);

		System.setProperty("org.jruby.embed.localcontext.scope", "threadsafe");
		final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		final ScriptEngine jruby = scriptEngineManager.getEngineByName("jruby");
		ScriptContext context = jruby.getContext();

		ArrayList<String> argv = new ArrayList<>();
		argv.add("--format=Checkstyle");
		argv.add("-o" + this.outputFile);
		// argv.add("--config " + TODO,);
		argv.addAll(this.getSourceDirs());
		// this.getSassSourceDirectory().getPath()
		context.setAttribute(ScriptEngine.ARGV,
				argv.toArray(new String[argv.size()]),
				ScriptContext.GLOBAL_SCOPE);
		try {
			log.info("Reporting scss lint in: " + this.outputFile.getAbsolutePath());
			ExitCode result = ExitCode.getExitCode(Ints.checkedCast((Long) jruby.eval(sassScript.toString(), context)));
			log.debug("scss-lint result: " + result.toString());
			switch (result) {
				case CODE_0:
					log.info(result.msg());
					break;
				case CODE_1:
					log.warn(result.msg());
					break;
				case CODE_2:
					log.error(result.toString());
					if (this.failOnError) {
						throw new MojoFailureException(result.toString());
					}
				case CODE_64:
				// fall through
				case CODE_66:
				// fall through
				case CODE_70:
				// fall through
				case CODE_78:
				// fall through
				default:
					log.error(result.toString());
					throw new MojoExecutionException(result.toString());
			}
		} catch (final ScriptException e) {
			throw new MojoExecutionException(
					"Failed to execute scss-lint Ruby script:\n" + sassScript, e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nl.geodienstencentrum.maven.plugin.sass.AbstractSassMojo#buildBasicSassScript
	 * (java.lang.StringBuilder)
	 */
	@Override
	protected void buildBasicSassScript(final StringBuilder sassScript)
			throws MojoExecutionException {
		final Log log = this.getLog();
		// build up script
		sassScript.append("require 'scss_lint'\n");
		sassScript.append("require 'scss_lint/cli'\n");
		sassScript.append("require 'scss_lint_reporter_checkstyle'\n");

		if (log.isDebugEnabled()) {
			// make ruby give use some debugging info when requested
			sassScript.append("require 'pp'\n");
			sassScript.append("puts 'parameters: '\n");
			sassScript.append("pp ARGV\n");
		}
		sassScript.append("SCSSLint::CLI.new.run(ARGV)\n");
	}

	private Set<String> getSourceDirs() {
		Set<String> dirs = new HashSet<>();
		List<Resource> _resources = this.getResources();
		if (_resources.isEmpty()) {
			dirs.add(getSassSourceDirectory().getPath());
		}
		for (final Resource source : _resources) {
			dirs.addAll(source.getDirectoriesAndDestinations(this.getLog()).keySet());
		}
		return dirs;
	}
}
