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

import org.apache.maven.plugin.logging.Log;

/**
 * Callback to bind <a
 * href="http://sass-lang.com/docs/yardoc/Sass/Plugin/Compiler.html">Sass::Plugin::Compiler</a>.
 */
public class CompilerCallback {
	/** compiler error indicator. */
	private boolean compileError;
	/** maven logging instance. */
	private final Log log;

	/**
	 * Instantiates a new compiler callback.
	 *
	 * @param log
	 *            the maven logging instance to use for messages
	 */
	public CompilerCallback(final Log log) {
		this.log = log;
	}

	/**
	 * Handle {@code on_compilation_error} event.
	 *
	 * @param error
	 *            the error
	 * @param template
	 *            the template
	 * @param css
	 *            the css
	 * @see <a
	 *      href="http://sass-lang.com/docs/yardoc/Sass/Plugin/Compiler.html#on_compilation_error-instance_method">
	 *            on_compilation_error</a>
	 */
	public void compilationError(final String error, final String template, final String css) {
		this.log.error("Compilation of template " + template + " failed: "
				+ error);
		this.compileError = true;
	}

	/**
	 * Handle {@code on_updated_stylesheet} event.
	 *
	 * @param template
	 *            the template
	 * @param css
	 *            the css
	 * @see <a
	 *      href="http://sass-lang.com/docs/yardoc/Sass/Plugin/Compiler.html#on_updated_stylesheet-instance_method">
	 *            on_updated_stylesheet</a>
	 */
	public void updatedStylesheeet(final String template, final String css) {
		this.log.info("    >> " + template + " => " + css);
	}

	/**
	 * Handle {@code on_template_modified} event.
	 *
	 * @param template
	 *            the template
	 * @see <a
	 *      href="http://sass-lang.com/docs/yardoc/Sass/Plugin/Compiler.html#on_template_modified-instance_method">
	 *            on_template_modified</a>
	 */
	public void templateModified(final String template) {
		this.log.info("File Change detected " + template);
	}

	/**
	 * Handle {@code on_template_created} event.
	 *
	 * @param template
	 *            the template
	 * @see <a
	 *      href="http://sass-lang.com/docs/yardoc/Sass/Plugin/Compiler.html#on_template_created-instance_method">
	 *            on_template_created</a>
	 */
	public void templateCreated(final String template) {
		this.log.info("New File detected " + template);
	}

	/**
	 * Handle {@code on_template_deleted} event.
	 *
	 * @param template
	 *            the template
	 * @see <a
	 *      href="http://sass-lang.com/docs/yardoc/Sass/Plugin/Compiler.html#on_template_deleted-instance_method">
	 *            on_template_deleted</a>
	 */
	public void templateDeleted(final String template) {
		this.log.info("File Delete detected " + template);
	}

	/**
	 * Had error.
	 *
	 * @return {@code true} when there was an error
	 */
	public boolean hadError() {
		return this.compileError;
	}
}
