/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.parser.packrat;

import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.terminalrules.XtextTerminalsTestLanguageStandaloneSetup;
import org.eclipse.xtext.parser.terminalrules.parser.packrat.XtextTerminalsTestLanguagePackratParser;
import org.eclipse.xtext.tests.AbstractGeneratorTest;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.xtext.parser.handwritten.HandwrittenParser;
import org.eclipse.xtext.xtext.parser.handwritten.HandwrittenParserWithMethodCalls;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class PerformanceTest extends AbstractGeneratorTest {

	private HandwrittenParser handwritten;
	
	private HandwrittenParserWithMethodCalls handwrittenWithMethodCalls;
	
	private XtextGrammarTestLanguagePackratParser generated;
	
	private XtextTerminalsTestLanguagePackratParser generatedWithTerminals;

	private String model;
	
	private static int metamodelCount = 200;
	
	private static int lexerRuleCount = metamodelCount;
	
	private long startTime;

	@Override
	protected void setUp() throws Exception {
		with(XtextTerminalsTestLanguageStandaloneSetup.class);
		this.handwritten = new HandwrittenParser();
		setAstFactory(handwritten);
		this.handwrittenWithMethodCalls = new HandwrittenParserWithMethodCalls();
		setAstFactory(handwrittenWithMethodCalls);
		this.generated = new XtextGrammarTestLanguagePackratParser();
		setAstFactory(generated);
		this.generatedWithTerminals = new XtextTerminalsTestLanguagePackratParser();
		setAstFactory(generatedWithTerminals);
		StringBuilder modelBuilder = new StringBuilder("language a.bc.def.ghi extends e.fh.ijk\n");
		for(int i = 0; i < metamodelCount; i++) {
			if (i % 2 == 0)
				modelBuilder.append("import 'http://test' as mm" + i + "\n");
			else
				modelBuilder.append("generate test" + i + " 'http://test' as mm" + i + "\n");
		}
		for(int i = 0; i < lexerRuleCount; i++) {
			if (i % 2 == 0)
				modelBuilder.append("native lexer" + i + ": 'content';\n");
			else
				modelBuilder.append("lexer native" + i + " returns type" + i + ": \"otherContent\";");
		}
		this.model = modelBuilder.toString();
		System.gc();
		if (metamodelCount >= 1000)
			Thread.sleep(2000); // increase chance for the gc to collect unused objects
		System.out.println("===== " + getName() + " =====");
		System.out.println("model.length(): " + model.length() + " chars (ca. " + (metamodelCount * 2)+ " lines)" );
		System.out.println("usage before:   " + (java.lang.Runtime.getRuntime().totalMemory() - java.lang.Runtime.getRuntime().freeMemory()));
		startTime = System.currentTimeMillis();
	}
	
	protected void tearDown() throws Exception {
		long endTime = System.currentTimeMillis();
		System.out.println("usage after:    " + (java.lang.Runtime.getRuntime().totalMemory() - java.lang.Runtime.getRuntime().freeMemory()));
		System.out.println("duration:               " + (endTime - startTime) + " ms");
		this.handwritten = null;
		this.handwrittenWithMethodCalls = null;
		this.model = null;
	}

	private void doTest(AbstractPackratParser parser) {
		IParseResult result = parser.parse(model);
		assertNotNull(result);
		assertNotNull(result.getRootASTElement());
		assertNotNull(result.getRootNode());
	}
	
	public void testFirstHandwrittenPackrat() {
		doTest(handwritten);
	}

	public void testSecondHandwrittenPackrat() {
		doTest(handwritten);
	}
	
	public void testHandwrittenPackratTwice() {
		for (int i = 0; i < 2; i++) {
			doTest(handwritten);
		}
	}
	
//	public void testProfile() {
//		testFirstGeneratedWithTerminalsPackrat();
//		for (int i = 0; i < 10; i++)
//			testGeneratedWithTerminalsPackratTwice();
//	}
	
	public void testFirstGeneratedWithTerminalsPackrat() {
		doTest(generatedWithTerminals);
	}
	
	public void testSecondGeneratedWithTerminalsPackrat() {
		doTest(generatedWithTerminals);
	}
	
	public void testGeneratedWithTerminalsPackratTwice() {
		for (int i = 0; i < 2; i++) {
			doTest(generatedWithTerminals);
		}
	}
	
	public void testFirstGeneratedPackrat() {
		doTest(generated);
	}
	
	public void testSecondGeneratedPackrat() {
		doTest(generated);
	}
	
	public void testGeneratedPackratTwice() {
		for (int i = 0; i < 2; i++) {
			doTest(generated);
		}
	}
	
	public void testFirstHandwrittenWithMethodCallsPackrat() {
		doTest(handwrittenWithMethodCalls);
	}
	
	public void testSecondHandwrittenWithMethodCallsPackrat() {
		doTest(handwrittenWithMethodCalls);
	}
	
	public void testHandwrittenWithMethodCallsPackratTwice() {
		for (int i = 0; i < 2; i++) {
			doTest(handwrittenWithMethodCalls);
		}
	}
	
	public void testFirstAntlr() {
		IParseResult result = getParser().parse(new StringInputStream(model));
		assertNotNull(result);
		assertNotNull(result.getRootASTElement());
		assertNotNull(result.getRootNode());
	}
	
	public void testSecondAntlr() {
		IParseResult result = getParser().parse(new StringInputStream(model));
		assertNotNull(result);
		assertNotNull(result.getRootASTElement());
		assertNotNull(result.getRootNode());
	}
	
	public void testAntlrTwice() {
		for (int i = 0; i < 2; i++) {
			IParseResult result = getParser().parse(new StringInputStream(model));
			assertNotNull(result);
			assertNotNull(result.getRootASTElement());
			assertNotNull(result.getRootNode());
			result = null;
		}
	}
}
