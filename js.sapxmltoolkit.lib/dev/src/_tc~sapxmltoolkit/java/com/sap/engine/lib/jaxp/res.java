package com.sap.engine.lib.jaxp;

import java.util.*;

public final class Res extends java.util.ListResourceBundle {

  static final Object[][] contents = new String[][] {{"XML_Source_is_null_", "XML Source is null."}, {"XSL_Source_is_null_", "XSL Source is null."}, {"Result_is_null_", "Result is null."}, {"SAXSource_whose", "SAXSource whose InputSource is null."}, {"DOMSource_whose_Node", "DOMSource whose Node is null."}, {"StreamSource_whose", "StreamSource whose InputStream, Reader, and systemId are all null."}, {"StreamResult_whose", "StreamResult whose OutputStream, Writer, and systemId are all null."}, {"Only_input_from", "Unsupported Source object."}, {"Only_output_to", "Unsupported Result object."}, {"Unable_to_create_SAX", "Unable to create SAX handler for source."}, {"XSLT_currently", "XSLT currently supports only input through systemId."}, {"SAXResult_whose", "SAXResult whose ContentHandler and LexicalHandler are both null."}, {"Exception_occurred", "Exception occurred while creating SAXParser: "}, {"Transformer_warning_", "Transformer warning: "}, {"Transformer_error_", "Transformer error: "}, {"Transformer_fatal", "Transformer fatal error: "}};

  public Object[][] getContents() {
    return contents;
  }

}

