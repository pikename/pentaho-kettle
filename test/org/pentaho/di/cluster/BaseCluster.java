package org.pentaho.di.cluster;

import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.job.JobEntryLoader;
import org.pentaho.di.trans.StepLoader;

public class BaseCluster extends TestCase {
	
	/*
	public void testIgnoreWhiteSpaces() {
		assertEqualsIgnoreWhitespaces("a  b   c", "a b c");
		assertEqualsIgnoreWhitespaces("a, b, c", "a,b,c   ");
		assertEqualsIgnoreWhitespacesAndCase("A  B   C", "a b c");
		assertEqualsIgnoreWhitespacesAndCase("a, b, c", "A,B,C  ");
	}
	*/
	
	protected void init() throws Exception {
        EnvUtil.environmentInit();
        LogWriter.getInstance( LogWriter.LOG_LEVEL_BASIC );

        StepLoader.init();
        JobEntryLoader.init();
	}

	protected String loadFileContent(VariableSpace space, String filename) throws Exception {
		String realFilename = space.environmentSubstitute(filename);
		return KettleVFS.getTextFileContent(realFilename, Charset.defaultCharset().name());
	}
	
	protected void assertEqualsIgnoreWhitespaces(String expected, String two) {
		String oneStripped = stripWhiteSpaces(expected);
		String twoStripped = stripWhiteSpaces(two);
		
		assertEquals(oneStripped, twoStripped);
	}
	
	protected void assertEqualsIgnoreWhitespacesAndCase(String expected, String actual) {
		assertEqualsIgnoreWhitespaces(expected.toUpperCase(), actual.toUpperCase());
	}

	private String stripWhiteSpaces(String one) {
		StringBuilder stripped = new StringBuilder();
		
		boolean previousWhiteSpace = false;
		
		for (char c : one.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (!previousWhiteSpace) {
					stripped.append(' '); // add a single white space, don't add a second
				}
				previousWhiteSpace=true;
			}
			else {
				if (c=='(' || c==')' || c=='|' || c=='-' || c=='+' || c=='/' || c=='*' || c=='{' || c=='}' || c==',' ) {
					int lastIndex = stripped.length()-1;
					if (stripped.charAt(lastIndex)==' ') {
						stripped.deleteCharAt(lastIndex);
					}
					previousWhiteSpace=true;
				} else {
					previousWhiteSpace=false;
				}
				stripped.append(c);
			}
		}
		
		// Trim the whitespace (max 1) at the front and back too...
		if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(0))) stripped.deleteCharAt(0);
		if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(stripped.length()-1))) stripped.deleteCharAt(stripped.length()-1);
		
		return stripped.toString();
	}

}
