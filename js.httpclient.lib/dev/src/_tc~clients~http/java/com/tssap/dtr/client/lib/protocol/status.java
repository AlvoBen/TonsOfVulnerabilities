package com.tssap.dtr.client.lib.protocol;

/**
 * Status codes returned by HTTP and WebDAV servers. 
 */
public interface Status {
	
	/** The lower limit of the range of informational status codes */
	public static final int INFORMATIONAL_MIN = 100;
	/** The upper limit of the range of informational status codes */	
	public static final int INFORMATIONAL_MAX = 199;
	
	/** Initial part of incomplete request was accepted by server. */
	public static final int CONTINUE = 100;
	/** Server wants to switch to a newer version, or real-time protocol, to continue.
	 * Used when an Upgrade header was in the client request.*/
	public static final int SWITCHING_PROTOCOLS = 101;
	/** Indicates an ongoing process, particular used for long-running WebDAV/DeltaV requests.
	 * This status messages is sent before the final response to avoid having the client
	 * time-out with an error.*/
	public static final int PROCESSING = 102;


	/** The lower limit of the range of success status codes */
	public static final int SUCCESS_MIN = 200;
	/** The upper limit of the range of success status codes */		
	public static final int SUCCESS_MAX = 299;	

	/** Success response in cases, where no resource has been created and the response
	 * has a body. Used by methods like GET, OPTIONS, DELETE */
	public static final int OK = 200;
	/** Resource has been sucessfully created.*/
	public static final int CREATED = 201;
	/** Resource will be created or deleted but the action is not yet completed. */
	public static final int ACCEPTED = 202;
	/** Meta-information in the header did not come from the origin server.*/
	public static final int NON_AUTHORITATIVE_INFORMATION = 203;
	/** Success response in cases, where no resource was created and the server does
	 * not need to return a response body.*/
	public static final int NO_CONTENT = 204;
	/** Server has fulfilled the request and the client should reset its document view. */
	public static final int RESET_CONTENT = 205;
	/** Only a part of the resource was returned. The response contains a Range header
	 * to indicate which part has been returned.*/
	public static final int PARTIAL_CONTENT = 206;
	/** Success response for WebDAV/DeltaV methods like PROPFIND and PROPPATCH */
	public static final int MULTI_STATUS = 207;


	/** The lower limit of the range of redirection status codes */
	public static final int REDIRECTION_MIN = 300;
	/** The upper limit of the range of redirection status codes */	
	public static final int REDIRECTION_MAX = 399;
		
	/** The requested resource exists in multiple representations and/or locations.*/
	public static final int MULTIPLE_CHOICES = 300;
	/** A new permant URL has been assigned to the requested resource. The new
	 * location is given in a Location header. */
	public static final int MOVED_PERMANENTLY = 301;
	/** A new temporary URL has been assigned to the requested resource. The new
	 * location is given in a Location header.*/
	public static final int MOVED_TEMPORARILY = 302;
	/** The resource has a new temporary URL, but since this URL will change on occasion,
	 * the client should continue to request the original request URL for future requests.*/
	public static final int FOUND = 302;
	/** */
	public static final int SEE_OTHER = 303;
	/** The client has performed a conditional GET, but the document has not been
	 * modified. The response does not contain a message body.*/
	public static final int NOT_MODIFIED = 304;
	/** The requested resource must be accessed through the proxy given in the
	 * Location header of the response.*/
	public static final int USE_PROXY = 305;
	/** The requested resource resides temporarily under a different URL. */
	public static final int TEMPORARY_REDIRECT = 307;


	/** The lower limit of status codes indicating a client failure*/
	public static final int CLIENT_FAILURE_MIN = 400;
	/** The upper limit of  status codes indicating a client failure*/	
	public static final int CLIENT_FAILURE_MAX = 499;
		
	/** The request is malformed. */
	public static final int BAD_REQUEST = 400;
	/** Accessing the resource requires authorization. */
	public static final int UNAUTHORIZED = 401;
	/** Accessing the resource requires payment. */
	public static final int PAYMENT_REQUIRED = 402;
	/** The server understood the request, but will not do it.
	 * Auhtorization or repeating the request will not help.*/
	public static final int FORBIDDEN = 403;
	/** Resource was not found. */	
	public static final int NOT_FOUND = 404;
	/** The method was not allowed for the resource. The response must include
	 * an Allow header*/
	public static final int METHOD_NOT_ALLOWED = 405;
	/** The resource does not match the content characteristics acceptable
	 * according to the Accept header sent in the request.*/
	public static final int NOT_ACCEPTABLE = 406;
	/** Accesing the resource requires authentication to a proxy first.*/
	public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
	/** The client did not complete the request within the time the server
	 * was prepared to wait.*/
	public static final int REQUEST_TIMEOUT = 408;
	/** The server understood the request, but cannot perform it at the moment,
	 * because some necessary precondition cannot be met. However, the request
	 * probably would succeed if this precondition would be fullfilled. */
	public static final int CONFLICT = 409;
	/** The resource is no longer available on the server.*/
	public static final int GONE = 410;
	/** The server refuses to accept the request without Content-Length header.*/
	public static final int LENGTH_REQUIRED = 411;
	/** One of the preconditions given in the header evaluated to false on the server.*/
	public static final int PRECONDITION_FAILED = 412;
	/** The request entity is larger than the server is able or willing to accept.*/
	public static final int REQUEST_TOO_LONG = 413;
	/** The request URL is longer than the server is able or willing to accept.*/
	public static final int REQUEST_URI_TOO_LONG = 414;
	/** The format of the request entity is not supported by the server.*/
	public static final int UNSUPPORTED_MEDIA_TYPE = 415;
	/** The request contains a Range header that cannot be satisfied. For example,
	 * the given range may have no overlap with the current extent of the requested
	 * resource.*/
	public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
	/** The exceptation given in the Expect header field of the request cannot be
	 * fulfilled by the server.*/
	public static final int EXPECTATION_FAILED = 417;
	/** The server understands the content type of the request entity, but was unable
	 * to process it.*/
	public static final int UNPROCESSABLE_ENTITY = 422;
	/** The resource is locked by the WebDAV LOCK command. */
	public static final int LOCKED = 423;
	/** The execution of the method was aborted because some part of the execution failed.
	 * Used by the WebDAV PROPPATCH and UNLOCK commands. */
	public static final int METHOD_FAILURE = 424;
	/** The query produces more results than the server was able or willing to transmit.*/
	public static final int INSUFFICIENT_SPACE_ON_RESOURCE = 425;
	/** The server wants to switch the current protocol */
	public static final int UPGRADE_REQUIRED = 426;


	/** The lower limit of status codes indicating a server failure*/
	public static final int SERVER_FAILURE_MIN = 500;
	/** The upper limit of status codes indicating a server failure*/	
	public static final int SERVER_FAILURE_MAX = 599;
	
	/** The server encountered an unexpected condition that prevented it from
	 * fulfilling the request.*/
	public static final int INTERNAL_SERVER_ERROR = 500;
	/** The server does not provide the requested functionality.*/
	public static final int NOT_IMPLEMENTED = 501;
	/** A gateway or proxy received an invalid response from an upstream server.*/
	public static final int BAD_GATEWAY = 502;
	/** The server is currently unable to handle the request due to temporary reasons.*/
	public static final int SERVICE_UNAVAILABLE = 503;
	/** A gateway or proxy did not receive a response from an upstream server in time.*/
	public static final int GATEWAY_TIMEOUT = 504;
	/** The server does not support, or refuses to support, the HTTP protocol version of
	 * the request.*/
	public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
	/** The destination resource does not have sufficient space to execute the request.*/
	public static final int INSUFFICIENT_STORAGE = 506;
}