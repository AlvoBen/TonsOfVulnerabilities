package com.sap.httpclient;

import junit.framework.*;

import java.lang.reflect.*;

import com.sap.httpclient.http.HttpStatus;

/**
 * Unit tests for {@link HttpStatus}
 */
public class TestHttpStatus extends TestCase {

  public TestHttpStatus(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpStatus.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttpStatus.class);
  }

  public void testStatusText() throws IllegalAccessException {
    Field[] publicFields = HttpStatus.class.getFields();
    assertTrue(publicFields != null);
    assertTrue(publicFields.length > 0);
		for (final Field f : publicFields) {
			final int modifiers = f.getModifiers();
			if ((f.getType() == int.class)
							&& Modifier.isPublic(modifiers)
							&& Modifier.isFinal(modifiers)
							&& Modifier.isStatic(modifiers)) {
				final int iValue = f.getInt(null);
				final String text = HttpStatus.getReasonPhrase(iValue);
				assertTrue("text is null for HttpStatus." + f.getName(), (text != null));
				assertTrue(text.length() > 0);
			}
		}

	}

  public void testStatusTextNegative() throws Exception {
    try {
      HttpStatus.getReasonPhrase(-1);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testStatusTextAll() throws Exception {
    for (int i = 0; i < 600; i++) {
      HttpStatus.getReasonPhrase(i);
    }
  }
}