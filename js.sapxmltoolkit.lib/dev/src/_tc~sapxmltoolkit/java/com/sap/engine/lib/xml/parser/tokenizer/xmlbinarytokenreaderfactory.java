package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.io.InputStream;

public class XMLBinaryTokenReaderFactory {
  
  /**
   * Creates new default XMLTokenReader instance.
   * @return
   */
  public static XMLTokenReader newInstance() {
    return new XMLBinaryTokenReader();
  }
  
  public static XMLTokenReader newInstance(final InputStream input) throws IOException {
    return new XMLBinaryTokenReader(input);
  }
    
}
