package com.tssap.dtr.client.lib.protocol;

import com.tssap.dtr.client.lib.protocol.util.Pair;

/**
 * This class implements a generic HTTP header.
 * HTTP headers are name-value pairs separated by a colon (':').
 */
public class Header extends Pair {

	/**
	 * Collection of strings constants for the predefined headers of HTTP/1.1
	 * (see RFC 2616 for details) and for session menagement headers (cookies, see
	 * RFC 2965 for details).
	 */
	public static class HTTP {
		/** 
		 * The Accept request-header field can be used by a client to indicate 
		 * media types acceptable in the response. 
		 */
		public static final String ACCEPT = "Accept";
		
		/** 
		 * The Accept request-header field can be used by a client to indicate 
		 * character sets acceptable in the response. 
		 */
		public static final String ACCEPT_CHARSET = "Accept-Charset";
		
		/** 
		 * The Accept request-header field can be used by a client to indicate 
		 * content encodings (e.g. compression algorithms) acceptable in the response. 
		 */
		public static final String ACCEPT_ENCODING = "Accept-Encoding";
		
		/** 
		 * The Accept request-header field can be used by a client to indicate 
		 * natural languages acceptable in the response. 
		 */		
		public static final String ACCEPT_LANGUAGES = "Accept-Language";
		
		/** 
		 * The Accept-Charset request-header field can be used by a server 
		 * to indicate its acceptance of range requests for a resource.
		 */
		public static final String ACCEPT_RANGES = "Accept-Ranges";
		
		/** 
		 * The Age response-header field conveys the sender's estimate of 
		 * the amount of time since the response was generated at the origin server.
		 */
		public static final String AGE = "Age";
		
		/** 
		 * The Allow entity-header field lists the set of methods supported by the 
		 * resource identified by the Request-URI.
		 */
		public static final String ALLOW = "Allow";
		
		/** 
		 * A user agent that wishes to authenticate itself with a server - usually, but not 
		 * necessarily, after receiving a 401 response - does so by including an 
		 * Authorization request-header field with the request. 
		 */
		public static final String AUTHORIZATION = "Authorization";
		
		/** 
		 * The Cache-Control general-header field is used to specify directives that 
		 * MUST be obeyed by all caching mechanisms along the request/response chain. 
		 */
		public static final String CACHE_CONTROL = "Cache-Control";
		
		/** 
		 * The Connection general-header field allows the sender to specify options 
		 * that are desired for that particular connection and MUST NOT be communicated 
		 * by proxies over further connections. HTTP/1.1 defines the "close" connection
		 * option for the sender to signal that the connection will be closed after 
		 * completion of the response.
		 */
		public static final String CONNECTION = "Connection";
		
		/** 
		 * The Content-Encoding entity-header field is used as a modifier to the media-type. 
		 * When present, its value indicates what additional content codings have been applied 
		 * to the entity-body, and thus what decoding mechanisms must be applied in order 
		 * to obtain the media-type referenced by the Content-Type header field.
		 */
		public static final String CONTENT_ENCODING = "Content-Encoding";
		
		/** 
		 * The Content-Language entity-header field describes the natural language(s) 
		 * of the intended audience for the enclosed entity. 
		 */
		public static final String CONTENT_LANGUAGE = "Content-Language";
		
		/** 
		 * The Content-Length entity-header field indicates the size of the entity-body,
		 * in decimal number of OCTETs. 
		 */
		public static final String CONTENT_LENGTH = "Content-Length";
		
		/** 
		 * The Content-Location entity-header field MAY be used to supply the 
		 * resource location for the entity enclosed in the message when that entity is 
		 * accessible from a location separate from the requested resource’s URI.
		 */
		public static final String CONTENT_LOCATION = "Content-Location";
		
		/** 
		 * The Content-MD5 entity-header field is an MD5 digest of the entity-body for the
		 * purpose of providing an end-to-end message integrity check of the entity-body.
		 */
		public static final String CONTENT_MD5 = "Content-MD5";
		
		/** 
		 * The Content-Range entity-header is sent with a partial entity-body to specify 
		 * where in the full entity-body the partial body should be applied.
		 */
		public static final String CONTENT_RANGE = "Content-Range";
		
		/** 
		 * The Content-Type entity-header field indicates the media type of the 
		 * entity-body sent to the recipient. 
		 */
		public static final String CONTENT_TYPE = "Content-Type";
		
		/** 
		 * The Date general-header field represents the date and time at which 
		 * the message was originated. 
		 */
		public static final String DATE = "Date";
		
		/**
		 * The ETag response-header field provides the current value of the entity 
		 * tag for the requested variant.
		 */
		public static final String ETAG = "ETag";
		
		/**
		 * The Expect request-header field is used to indicate that particular server 
		 * behaviors are required by the client.
		 */
		public static final String EXPECT = "Expect";
		
		/**
		 * The Expires entity-header field gives the date/time after which the 
		 * response is considered stale.
		 */
		public static final String EXPIRES = "Expires";
		
		/**
		 * The From request-header field, if given, SHOULD contain an Internet 
		 * e-mail address for the human user who controls the requesting user agent.
		 */
		public static final String FROM = "From";
		
		/**
		 * The Host request-header field specifies the Internet host and port number 
		 * of the resource being requested, as obtained from the original URI given 
		 * by the user or referring resource.
		 */
		public static final String HOST = "Host";
		
		/**
		 * The If-Match request-header field is used with a method to make it conditional.
		 * A client that has one or more entities previously obtained from the resource 
		 * can verify that one of those entities is current by including a list of
		 * their associated entity tags in the If-Match header field.
		 */
		public static final String IF_MATCH = "If-Match";
		
		/**
		 * The If-None-Match request-header field is used with a method to make it conditional. 
		 * A client that has one or more entities previously obtained from the resource 
		 * can verify that none of those entities is current by including a list
		 * of their associated entity tags in the If-None-Match header field.
		 */
		public static final String IF_NONE_MATCH = "If-None-Match";
		
		/**
		 * The If-Modified-Since request-header field is used with a method 
		 * to make it conditional: if the requested variant has not been modified 
		 * since the time specified in this field, an entity will not be returned 
		 * from the server;
		 */
		public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
		
		/**
		 * The If-Unmodified-Since request-header field is used with a method to 
		 * make it conditional. If the requested resource has not been modified since 
		 * the time specified in this field, the server SHOULD perform the requested
		 * operation as if the If-Unmodified-Since header were not present.
		 */
		public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
		
		/**
		 * If a client has a partial copy of an entity in its cache, and wishes 
		 * to have an up-to-date copy of the entire entity in its cache, it could use the 
		 * Range request-header with a conditional request.
		 */
		public static final String IF_RANGE = "If-Range";
		
		/**
		 * The Last-Modified entity-header field indicates the date and time at 
		 * which the origin server believes the variant was last modified.
		 */
		public static final String LAST_MODIFIED = "Last-Modified";
		
		/**
		 * The Location response-header field is used to redirect the recipient 
		 * to a location other than the Request-URI for completion of the request 
		 * or identification of a new resource.
		 */
		public static final String LOCATION = "Location";
		
		/**
		 * The Max-Forwards request-header field provides a mechanism with 
		 * the TRACE and OPTIONS methods to limit the number of proxies or 
		 * gateways that can forward the request to the next inbound server.
		 */
		public static final String MAX_FORWARDS = "Max-Forwards";
		
		/**
		 * The Pragma general-header field is used to include 
		 * implementation-specific directives that might apply to any recipient 
		 * along the request/response chain.
		 */
		public static final String PRAGMA = "Pragma";
		
		/**
		 * The Proxy-Authenticate response-header field MUST be included as 
		 * part of a 407 (Proxy Authentication Required) response. The field 
		 * value consists of a challenge that indicates the authentication 
		 * scheme and parameters applicable to the proxy for this Request-URI.
		 */
		public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
		
		/**
		 * The Proxy-Authorization request-header field allows the client 
		 * to identify itself (or its user) to a proxy which requires authentication.
		 */
		public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
		
		/**
		 * HTTP/1.1 allows a client to request that only part (a range of) the 
		 * response entity be included within the response. HTTP retrieval 
		 * requests using conditional or unconditional GET methods MAY request 
		 * one or more sub-ranges of the entity, instead of the entire entity, 
		 * using the Range request header, which applies to the entity returned 
		 * as the result of the request.
		 */
		public static final String RANGE = "Range";
		
		/**
		 * The Referer request-header field allows the client to specify, for 
		 * the server’s benefit, the address (URI) of the resource from which the 
		 * Request-URI was obtained.
		 */
		public static final String REFERER = "Referer";
		
		/**
		 * The Retry-After response-header field can be used with a 503 
		 * (Service Unavailable) response to indicate how long the service is expected 
		 * to be unavailable to the requesting client.
		 */
		public static final String RETRY_AFTER = "Retry-After";
		
		/**
		 * The Server response-header field contains information about the software 
		 * used by the origin server to handle the request.
		 */
		public static final String SERVER = "Server";
		
		/**
		 * The TE request-header field indicates what extension transfer-codings it 
		 * is willing to accept in the response and whether or not it is willing to 
		 * accept trailer fields in a chunked transfer-coding.
		 */
		public static final String TE = "TE";
		
		/**
		 * The Trailer general field value indicates that the given set of header 
		 * fields is present in the trailer of a message encoded with chunked 
		 * transfer-coding.
		 */
		public static final String TRAILER = "Trailer";
		
		/**
		 * The Transfer-Encoding general-header field indicates what (if any) type 
		 * of transformation has been applied to the message body in order to safely 
		 * transfer it between the sender and the recipient.
		 */
		public static final String TRANSFER_ENCODING = "Transfer-Encoding";
		
		/**
		 * The Upgrade general-header allows the client to specify what additional 
		 * communication protocols it supports and would like to use if the server 
		 * finds it appropriate to switch protocols.
		 */
		public static final String UPGRADE = "Upgrade";
		
		/**
		 * The User-Agent request-header field contains information about the 
		 * user agent originating the request.
		 */
		public static final String USER_AGENT = "User-Agent";
		
		/**
		 * The Vary field value indicates the set of request-header fields that 
		 * fully determines, while the response is fresh, whether a cache is 
		 * permitted to use the response to reply to a subsequent request without 
		 * revalidation.
		 */
		public static final String VARY = "Vary";
		
		/**
		 * The Via general-header field MUST be used by gateways and proxies 
		 * to indicate the intermediate protocols and recipients between the user 
		 * agent and the server on requests, and between the origin server and 
		 * the client on responses.
		 */
		public static final String VIA = "Via";
		
		/**
		 * The Warning general-header field is used to carry additional information 
		 * about the status or transformation of a message which might not be 
		 * reflected in the message.
		 */
		public static final String WARNING = "Warning";
		
		/**
		 * The WWW-Authenticate response-header field MUST be included in 
		 * 401 (Unauthorized) response messages. The field value consists of at 
		 * least one challenge that indicates the authentication scheme(s) and 
		 * parameters applicable to the Request-URI.
		 */
		public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

		/**
		 * A user agent returns a Cookie request header to the origin server if 
		 * it chooses to continue a session.
		 */
		public static final String COOKIE = "Cookie";
		
		/**
		 * An origin server may initiate a session by returnint a Set-Cookie header
		 * to the client. Note, this header originally was used by Netscape browsers
		 * and has been replaced by the Set-Cookie2 header. However, the Set-Cookie
		 * header still is widely used. 
		 */
		public static final String SET_COOKIE = "Set-Cookie";
		
		/**
		 * An origin server may initiate a session by returnint a Set-Cookie header
		 * to the client (see RFC 2965). In comparision to the old-style Set-Cookie 
		 * header Set-Cookie2 allows a more taylored definition of cookies.
		 */
		public static final String SET_COOKIE2 = "Set-Cookie2";
		
		
		/**
		 * 
		 */
		public static final String CONTENT_ID = "Content-ID";
	}


	/**
	 * Collection of strings constants for the predefined headers of DAV/DeltaV
	 * and DASL protocol extensions (see RFC 2518 and RFC 3253 for details).
	 */
	public static final class DAV {
		
		/**
		 * This header indicates that the resource supports the DAV schema and 
		 * protocol as specified. All DAV compliant resources MUST return the DAV 
		 * header on all OPTIONS responses.
		 */
		public static final String DAV = "DAV";
		
		/**
		 * The DASL response header indicates server support for a query grammar
		 * in the OPTIONS method. The value is a URI that indicates the type of grammar. 
		 */
		public static final String DASL = "DASL";
		
		/**
		 * The Depth header is used with methods executed on resources which 
		 * could potentially have internal members to indicate whether the method 
		 * is to be applied only to the resource ("Depth: 0"), to the resource 
		 * and its immediate children ("Depth: 1"), or the resource and all its 
		 * progeny ("Depth: infinity").
		 */
		public static final String DEPTH = "Depth";
		
		/**
		 * The Destination header specifies the URI which identifies a destination 
		 * resource for methods such as COPY and MOVE, which take two URIs as parameters.
		 */
		public static final String DESTINATION = "Destination";
		
		/**
		 * The If header's purpose is to describe a series of state lists. 
		 * If the state of the resource to which the header is applied does not 
		 * match any of the specified state lists then the request MUST fail.
		 * In contrast to the various IF headers defined by HTTP/1.1 this header 
		 * is intended for use with any URI which represents state information, 
		 * referred to as a state token, about a resource as well as ETags.
		 */
		public static final String IF = "If";
		
		/**
		 * The Lock-Token request header is used with the LOCK and UNLOCK methods 
		 * to identify the lock that has been created/removed. The lock token in the 
		 * Lock-Token request header MUST identify a lock that contains the resource 
		 * identified by Request-URI as a member.
		 */
		public static final String LOCK_TOKEN = "Lock-Token";
		
		/**
		 * The Overwrite header specifies whether the server should overwrite the 
		 * state of a non-null destination resource during a COPY or MOVE. A value 
		 * of "F" states that the server must not perform the COPY or MOVE operation 
		 * if the state of the destination resource is non-null.
		 */
		public static final String OVERWRITE = "Overwrite";
		
		/**
		 * The Status-URI response header may be used with the 102 (Processing) status 
		 * code to inform the client as to the status of a method.
		 */
		public static final String STATUS_URI = "Status-URI";
		
		/**
		 * Clients may include Timeout headers in their LOCK requests to indicate
		 * when the lock should expire.
		 */
		public static final String TIMEOUT = "Timeout";
		
		/**
		 * For certain DeltaV methods (e.g. PROPFIND) labels can be applied to
		 * identify a target resource.
		 */
		public static final String LABEL = "Label";
	}
	
	
	/**
	 * Collection of strings constants for the additional headers
	 * used by the SAP eXtended Change Management protocol
	 */
	public static final class XCM {
		/**
		 * This header is used by some variants of the ListCollection Report
		 * to return the integration sequence number of a workspace.
		 */
		public static final String SEQUENCE_NUMBER = "Sequence-Number";		
	}	



	/**
	 * Creates a Header instance from the given name and value.
	 * Note, the value of an HTTP header may be empty,
	 * but name must be defined.
	 * @param name   the name part of this header
	 * @param value   the value part of this header
	 * @throws IllegalArgumentException  if name is null or the empty string.
	 */
	public Header(String name, String value) {
		super(name, value, ':');
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("Name of header must not be null or empty.");
		}
		setPartSeparators(",");
	}

	/**
	 * Creates a Header instance from the given string.
	 * Leading and trailing whitespace is removed from both
	 * name and value part of the header. Quotes surrounding the value
	 * are preserved. Note, the value of an HTTP header may be empty,
	 * but a valid name is mandatory.
	 * @param str the string to parse
	 * @return a Header instance
	 * @throws IllegalArgumentException if name is null or the empty string.
	 */
	public Header(String str) {
		super(str, ':');
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("Name of header must not be null or empty.");
		}
		setPartSeparators(",");
	}

	/**
	 * Returns a string representation of this header.
	 * @return The string concatenation of name, separator and value.
	 * Note, the separator is appended also in case of an empty value.
	 */
	public String toString() {
		if (value != null && value.length() > 0) {
			return name + ": " + value;
		} else {
			return name + ":";
		}
	}

}
