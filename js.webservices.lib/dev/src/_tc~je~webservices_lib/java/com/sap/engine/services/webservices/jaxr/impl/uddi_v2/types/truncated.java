﻿/**
 * Generated by SAP Schema-to-Java Generator
 * Tue Sep 30 15:47:40 EEST 2003
 * Chavdar Baikov (chavdar.baikov@sap.com)
 */

package com.sap.engine.services.webservices.jaxr.impl.uddi_v2.types;

import java.rmi.MarshalException;
import java.rmi.UnmarshalException;
import org.w3c.dom.Node;
import com.sap.engine.services.webservices.jaxrpc.encoding.SOAPDeserializationContext;
import com.sap.engine.services.webservices.jaxrpc.encoding.SOAPSerializationContext;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenWriter;

public class Truncated extends com.sap.engine.services.webservices.jaxrpc.encoding.primitive.StringSD {
  public java.lang.String _d_originalLocalName() {
    return "truncated";
  }

  public java.lang.String _d_originalUri() {
    return "urn:uddi-org:api_v2";
  }

  public Object deserialize(String content, SOAPDeserializationContext context, Class resultClass) throws UnmarshalException {
    Object result = super.deserialize(content,context,resultClass);
    return result;
  }

  public Object deserialize(XMLTokenReader reader, SOAPDeserializationContext context, Class resultClass) throws UnmarshalException {
    Object result = super.deserialize(reader,context,resultClass);
    return result;
  }

  public Object deserialize(Node node, SOAPDeserializationContext context, Class resultClass) throws UnmarshalException {
    Object result = super.deserialize(node,context,resultClass);
    return result;
  }

  public String serialize(Object obj, SOAPSerializationContext context) throws MarshalException {
    return super.serialize(obj, context);
  }

  public void serialize(Object obj, XMLTokenWriter writer, SOAPSerializationContext context) throws MarshalException, java.io.IOException {
    super.serialize(obj, writer, context);
  }

  public void serialize(Object obj, Node node, SOAPSerializationContext context) throws MarshalException {
    super.serialize(obj, node, context);
  }
}
