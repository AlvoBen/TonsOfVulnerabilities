package com.sap.engine.services.httpserver.exceptions;

import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;

public class HttpException extends BaseException {
	public static String HTTP_PROCESSING_ERROR = "http_0080";
	public static String HTTP_OPENFILE_ERROR = "http_0081";
	public static String HTTP_READFILE_ERROR = "http_0082";
	public static String HTTP_LOG_ID = "http_0083";
	public static String HTTP_LOG_ID_NULL = "servlet_jsp_0084";
	
	public HttpException(String s, Object[] args, Throwable t) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s, args), t);
    }

	public HttpException(String s, Throwable t) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s), t);
    }

	public HttpException(String s, Object[] args) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s, args));
	}


	public HttpException(String s) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s));
	}
}
