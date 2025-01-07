package com.sap.jms.interfaces;

import com.sap.exception.BaseException;
import com.sap.exception.BaseExceptionInfo;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Severity;

/**
 * An exception class for the VP Distribution.
 * 
 * JMSProvider in 7.0 has a new feature - 
 * JMS Virtual Provider(VP) Distribution.
 * It means that is made a move of an application JMS Resources
 * from one VP to another VP  
 * When there is no free node, an exception is thrown that 
 * the node should be creted, and after  that create the VP.
 * If there is a free node, the  user should be informed 
 * that the  VP from the moveJMSResource method should be
 * created. 

 * @author rositza.andreeva@sap.com
 * @version 7.0 
 *   
 */
public class NoFreeNodeException extends BaseException {
	  private BaseExceptionInfo info = null;
	  
	  
	  public NoFreeNodeException(DeployExceptionEnum errorCode) {
	  	this(errorCode,null,null);
	  }
	  public NoFreeNodeException() {
	  	
	  }
	  public NoFreeNodeException(Throwable t) {
	  	super(t);
	  } 
	  public NoFreeNodeException(DeployExceptionEnum errorCode, Object[] parameters,Throwable linked_exception) {
		info =
			  new BaseExceptionInfo(
				  JMSDeployResourceAccessor.LOGGER,
				  Severity.ERROR,
				  JMSDeployResourceAccessor.TRACER,
				  new LocalizableTextFormatter(JMSDeployResourceAccessor.getResourceAccessor(), errorCode.getName(), parameters),
				  this,
				  linked_exception);
	  
	  }
}
