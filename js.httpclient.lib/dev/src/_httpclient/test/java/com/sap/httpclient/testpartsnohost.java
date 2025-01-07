package com.sap.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.multipart.FilePart;
import com.sap.httpclient.http.methods.multipart.StringPart;

public class TestPartsNoHost extends TestCase {

  static final String PART_DATA = "This is the part data.";
  static final String NAME = "name";

  public TestPartsNoHost(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(TestPartsNoHost.class);
  }

  public void testFilePartResendsFileData() throws Exception {
    File file = createTempTestFile();
    FilePart part = new FilePart(NAME, file);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    part.send(stream);
    String resp1 = stream.toString();
    stream = new ByteArrayOutputStream();
    part.send(stream);
    String resp2 = stream.toString();
    file.delete();
    assertEquals(resp1, resp2);
  }

  public void testStringPartResendsData() throws Exception {
    StringPart part = new StringPart(NAME, PART_DATA);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    part.send(stream);
    String resp1 = stream.toString();
    stream = new ByteArrayOutputStream();
    part.send(stream);
    String resp2 = stream.toString();
    assertEquals(resp1, resp2);
  }

  public void testFilePartNullFileResendsData() throws Exception {
    FilePart part = new FilePart(NAME, "emptyfile.ext", null);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    part.send(stream);
    String resp1 = stream.toString();
    stream = new ByteArrayOutputStream();
    part.send(stream);
    String resp2 = stream.toString();
    assertEquals(resp1, resp2);
  }

  /**
   * Writes PART_DATA out to a temporary file and returns the file it was written to.
   *
   * @return the File object representing the file the data was written to.
	 * @throws IOException if an IOException occures
   */
  private File createTempTestFile() throws IOException {
    File file = File.createTempFile("FilePartTest", ".txt");
    PrintWriter out = new PrintWriter(new FileWriter(file));
    out.println(PART_DATA);
    out.flush();
    out.close();
    return file;
  }
}