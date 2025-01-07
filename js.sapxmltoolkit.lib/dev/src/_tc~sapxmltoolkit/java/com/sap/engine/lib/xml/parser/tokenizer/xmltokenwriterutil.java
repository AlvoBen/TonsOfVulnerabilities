package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.util.Set;
import org.w3c.dom.Node;

/**
 * Used to expose the method through a facade.
 * @author I056242
 *
 */
public class XMLTokenWriterUtil {
  
  public static void outputDomToWriter(XMLTokenWriter writer, Node node) throws IOException {
    XMLTokenWriterImpl.outputDomToWriter(writer, node);
  }
  
  
  public static void registerNamespaces(XMLTokenWriter writer, Set<String> nss) throws IOException {
    XMLTokenWriterImpl.registerNamespaces(writer, nss);
  }

}
