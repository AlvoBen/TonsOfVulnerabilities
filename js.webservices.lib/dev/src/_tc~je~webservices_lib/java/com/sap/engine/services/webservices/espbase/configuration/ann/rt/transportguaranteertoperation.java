package com.sap.engine.services.webservices.espbase.configuration.ann.rt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Run-Time Operation level security settings of data transport. 
 * NameSpace of the coresponding feature is: <tt>http://www.sap.com/webas/630/soap/features/transportguarantee/</tt>.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransportGuaranteeRTOperation
{
  /**
   * Attribute, which defines expected signed elements of message.
   * Attribute is optional and if the value is array with <tt>"wssp:Body()"</tt> and <tt>"wssp:Header(*)"</tt>, coresponding properties are not generated.
   * Name of the coresponding property name is <tt>IncomingSignature.ExpectedSignedElement.Operation.MessagePart</tt>.
   */
  String[] IncomingSignatureExpectedSignedElementOperationMessagePart() default {"wssp:Body()", "wssp:Header(*)"};
  
  /**
   * Attribute, which defines whether encrypted elements of message are expected.
   * Attribute is optional and if the value is array with <tt>"wssp:Body()"</tt>, coresponding properties are not generated.
   * Name of the coresponding property name is <tt>IncomingEncryption.ExpectedEncryptedElement.Operation.MessagePart</tt>.
   */
  String[] IncomingEncryptionExpectedEncryptedElementOperationMessagePart() default {"wssp:Body()"};
  
  /**
   * Attribute, which defines whether encrypted elements of message are expected.
   * Attribute is optional and if the value is array with <tt>"wssp:Body()"</tt> and <tt>"wssp:Header(*)</tt>, coresponding properties are not generated.
   * Name of the coresponding property name is <tt>OutgoingSignature.SignedElement.Operation.MessagePart</tt>.
   */
  String[] OutgoingSignatureSignedElementOperationMessagePart() default {"wssp:Body()", "wssp:Header(*)"};
  
  /**
   * Attribute, which defines elements of message to encrypt.
   * Attribute is optional and if the value is array with one <tt>"wssp:Body()"</tt>, coresponding properties are not generated.
   * Name of the coresponding property name is <tt>OutgoingEncryption.EncryptedElement.Operation.MessagePart</tt>.
   */
  String[] OutgoingEncryptionEncryptedElementOperationMessagePart() default {"wssp:Body()"};
}
