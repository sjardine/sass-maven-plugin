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
package nl.geodienstencentrum.maven.plugin.sass.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;

/**
 * Converts the xml report into a maven site html report.
 * @author mprins
 */
public class SCSSLintReportGenerator {

	private final Sink sink;
	private final String description;
	private final File xmlFile;
	private final Log log;

	/**
	 * Construct a configured instance of the report generator.
	 *
	 * @param sink (html) doxia sink to use
	 * @param description description for the report
	 * @param xmlFile input xml file to convert
	 * @param log maven log
	 */
	public SCSSLintReportGenerator(final Sink sink, final String description,
	        final File xmlFile, final Log log) {
		this.sink = sink;
		this.description = description;
		this.xmlFile = xmlFile;
		this.log = log;
	}

	/**
	 * translate the xml report to the format of the sink (html).
	 */
	public void generateReport() {
		sink.head();
		sink.title();
		sink.text(description);
		sink.title_();
		sink.head_();
		sink.body();
		sink.rawText(this.translateXML());
		sink.body_();
		sink.flush();
		sink.close();
	}

	/**
	 * translate the xml using xslt.
	 * @return the transformed xml as string
	 */
	private String translateXML() {
		String translated = null;
		try {
			final TransformerFactory factory = TransformerFactory.newInstance();
			final Templates template = factory.newTemplates(
			    new StreamSource(
			        this.getClass().getClassLoader().getResourceAsStream("scss-report.xsl")));
			final Transformer xformer = template.newTransformer();
			log.info("Transforming scss-lint xml results file: " + xmlFile.getAbsolutePath());
			final Source source = new StreamSource(new FileInputStream(this.xmlFile));
			final StringWriter outWriter = new StringWriter();
			final Result result = new StreamResult(outWriter);
			xformer.transform(source, result);
			translated = outWriter.toString();
		} catch (FileNotFoundException | TransformerConfigurationException e) {
			// error in the XSL file
			log.error("Error during xml conversion of " + this.xmlFile, e);
		} catch (TransformerException e) {
			// error while applying the XSL file
			final SourceLocator locator = e.getLocator();
			final int col = locator.getColumnNumber();
			final int line = locator.getLineNumber();
			log.error("Error during xml transformation of " + this.xmlFile
					+ "line: " + line + ", col: " + col, e);
		}

		log.debug("Transformed scss-lint xml:\n" + translated);
		return translated;
	}
}
