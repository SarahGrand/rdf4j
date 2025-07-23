/*******************************************************************************
 * Copyright (c) 2020 Eclipse RDF4J contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 *******************************************************************************/
package org.eclipse.rdf4j.rio.ntriples;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModelFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParserFactory;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
import org.eclipse.rdf4j.rio.RDFWriterTest;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.RioSetting;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.rio.helpers.NTriplesWriterSettings;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jeen Broekstra
 */
public abstract class AbstractNTriplesWriterTest extends RDFWriterTest {

	protected AbstractNTriplesWriterTest(RDFWriterFactory writerF, RDFParserFactory parserF) {
		super(writerF, parserF);
	}

	@Override
	protected RioSetting<?>[] getExpectedSupportedSettings() {
		return new RioSetting[] {
				BasicWriterSettings.XSD_STRING_TO_PLAIN_LITERAL,
				NTriplesWriterSettings.ESCAPE_UNICODE
		};
	}

	@Test
	public void testDirLangString() throws Exception {
		dirLangStringTest(RDFFormat.NTRIPLES);
	}

	@Test
	public void testTripleTerm() {
		Model model = new DynamicModelFactory().createEmptyModel();
		String ns = "http://www.example.com/";
		model.setNamespace("", ns);
		model.add(vf.createIRI(ns, "s"), vf.createIRI(ns, "p"),
				vf.createTriple(vf.createIRI(ns, "s2"), vf.createIRI(ns, "p2"), vf.createIRI(ns, "o")));


		StringWriter stringWriter = new StringWriter();
		Rio.write(model, stringWriter, RDFFormat.NTRIPLES, new WriterConfig().set(BasicWriterSettings.ENCODE_RDF_STAR, false));

		assertEquals(
				"<http://www.example.com/s> <http://www.example.com/p> <<( <http://www.example.com/s2> <http://www.example.com/p2> <http://www.example.com/o> )>> .\n",
				stringWriter.toString());
	}

	@Test
	public void testNestedTripleTerm() {
		Model model = new DynamicModelFactory().createEmptyModel();
		String ns = "http://www.example.com/";
		model.setNamespace("", ns);
		model.add(vf.createIRI(ns, "s"), vf.createIRI(ns, "p"),
				vf.createTriple(vf.createIRI(ns, "s2"), vf.createIRI(ns, "p2"),
						vf.createTriple(vf.createIRI(ns, "s3"), vf.createIRI(ns, "p3"), vf.createIRI(ns, "o"))));


		StringWriter stringWriter = new StringWriter();
		Rio.write(model, stringWriter, RDFFormat.NTRIPLES, new WriterConfig().set(BasicWriterSettings.ENCODE_RDF_STAR, false));

		assertEquals("<http://www.example.com/s> <http://www.example.com/p> <<( <http://www.example.com/s2> <http://www.example.com/p2> <<( <http://www.example.com/s3> <http://www.example.com/p3> <http://www.example.com/o> )>> )>> .\n",
				stringWriter.toString());
	}

	@Test
	public void testNestedTripleTerm2() {
		Model model = new DynamicModelFactory().createEmptyModel();
		String ns = "http://www.example.com/";
		model.setNamespace("", ns);
		model.add(vf.createBNode("b"), vf.createIRI(ns, "p"),
				vf.createTriple(vf.createIRI(ns, "s"), vf.createIRI(ns, "p2"),
						vf.createTriple(vf.createBNode("b2"), vf.createIRI(ns, "p3"), vf.createLiteral(9))));

		StringWriter stringWriter = new StringWriter();
		Rio.write(model, stringWriter, RDFFormat.NTRIPLES, new WriterConfig().set(BasicWriterSettings.ENCODE_RDF_STAR, false));

		assertTrue(stringWriter.toString().contains("_:b <http://www.example.com/p> <<( <http://www.example.com/s> <http://www.example.com/p2> <<( _:b2 <http://www.example.com/p3> \"9\"^^<http://www.w3.org/2001/XMLSchema#int> )>> )>> .\n"));
	}
}
