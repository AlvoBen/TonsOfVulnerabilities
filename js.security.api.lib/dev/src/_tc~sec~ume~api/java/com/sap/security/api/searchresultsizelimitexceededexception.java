package com.sap.security.api;

/**
 * Title:        SearchResultSizeLimitExceededException
 * Description:  Exception class used to indicate that a search result has exceeded the size limit
 *               This exception may be thrown from search methods, if the result of the search would be too large
 *               or if a server side size limit was exceeded. Callers should have the possibility to restrict
 *               the search by specifying more restrictive search criterias.
 * Copyright:    Copyright (c) 2001
 * Company:      SAP
 * @author Steffen Huester
 * @version 1.0
 */

public class SearchResultSizeLimitExceededException extends UMException {

	private static final long serialVersionUID = 550989279727265398L;
  /**
   * Constructs a <code>SearchResultSizeLimitExceededException</code> with
   * <code>null</code> as its error detail message.
   */
  public SearchResultSizeLimitExceededException() {
    super();
  }

  public SearchResultSizeLimitExceededException(Throwable reason) {
	super(reason);
  }
  /**
   * Constructs a <code>SearchResultSizeLimitExceededException</code> with the specified string
   * as its error detail message.
   */
  public SearchResultSizeLimitExceededException(String msg) {
    super(msg);
  }
}