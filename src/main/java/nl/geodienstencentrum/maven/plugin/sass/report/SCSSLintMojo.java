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

import com.google.common.primitives.Ints;
import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import nl.geodienstencentrum.maven.plugin.sass.AbstractSassMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * SCSSLintMojo executes scss-lint.
 *
 * @author mprins
 * @since 2.3
 */
@Mojo(name = "lint")
public class SCSSLintMojo extends AbstractSassMojo {

	/**
	 * output file for the plugin.
	 *
	 * @since 2.3
	 */
	@Parameter(defaultValue = "${project.build.directory}/site/scss-lint.xml")
	private File lintOutput;

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
		this.lintOutput.getParentFile().mkdirs();

		log.info("Linting Sass sources in: " + this.getSassSourceDirectory());

		final StringBuilder sassScript = new StringBuilder();
		this.buildBasicSassScript(sassScript);

		System.setProperty("org.jruby.embed.localcontext.scope", "threadsafe");

		log.debug("Execute Sass Ruby script:\n" + sassScript);

		final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		final ScriptEngine jruby = scriptEngineManager.getEngineByName("jruby");
		ScriptContext context = jruby.getContext();

		context.setAttribute(ScriptEngine.ARGV,
				new String[]{"--format=XML",
					"-o" + this.lintOutput,
					//"--config "+TODO,
					this.getSassSourceDirectory().getPath()},
				ScriptContext.GLOBAL_SCOPE);
		try {
			ExitCode result = ExitCode.getExitCode(Ints.checkedCast((Long) jruby.eval(sassScript.toString(), context)));
			log.debug(result.toString());

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
				case CODE_66:
				case CODE_70:
				case CODE_78:
					log.error(result.toString());
					throw new MojoExecutionException(result.toString());
			}
		} catch (final ScriptException e) {
			throw new MojoExecutionException(
					"Failed to execute Sass ruby script:\n" + sassScript, e);
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
	protected void buildBasicSassScript(StringBuilder sassScript)
			throws MojoExecutionException {
		final Log log = this.getLog();
		// build up script
		sassScript.append("require 'scss_lint'").append("\n");
		sassScript.append("require 'scss_lint/cli'").append("\n");
		if (log.isDebugEnabled()) {
			// make ruby give use some debugging info when requested
			sassScript.append("require 'pp'").append("\n");
			sassScript.append("pp ARGV").append("\n");
		}
		sassScript.append("SCSSLint::CLI.new.run(ARGV)").append("\n");
	}

}
