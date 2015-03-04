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
 *
 * @author mprins
 */
public class SCSSLintReportGenerator {

	private final Sink sink;
	private final String description;
	private final File xmlFile;
	private final Log log;

	public SCSSLintReportGenerator(Sink sink, String description, File xmlFile, Log log) {
		this.sink = sink;
		this.description = description;
		this.xmlFile = xmlFile;
		this.log = log;
	}

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

	private String translateXML() {
		String translated = null;
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Templates template = factory.newTemplates(new StreamSource(this.getClass().getClassLoader().getResourceAsStream("scss-report.xsl")));
			Transformer xformer = template.newTransformer();
			log.debug("loading xml file: " + xmlFile.getAbsolutePath());
			Source source = new StreamSource(new FileInputStream(this.xmlFile));
			StringWriter outWriter = new StringWriter();
			Result result = new StreamResult(outWriter);
			xformer.transform(source, result);
			translated = outWriter.toString();
		} catch (FileNotFoundException | TransformerConfigurationException e) {
			// error in the XSL file
			log.error("Error during xml conversion of " + this.xmlFile, e);

		} catch (TransformerException e) {
			// error while applying the XSL file
			SourceLocator locator = e.getLocator();
			int col = locator.getColumnNumber();
			int line = locator.getLineNumber();
			log.error("Error during xml transformation of " + this.xmlFile
					+ "line: " + line + ", col: " + col, e);
		}

		log.debug("translated xml\n" + translated);
		return translated;
	}
}
