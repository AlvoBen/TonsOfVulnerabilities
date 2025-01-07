/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.uri;

import com.sap.httpclient.exception.URIException;
import com.sap.httpclient.exception.URLDecodeException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Imlementation of URI(Uniform Resource Identifiers) version of RFC 2396.
 *
 */
public class URI implements Cloneable, Comparable, Serializable {

  /**
   * Cache the hash code for this URI.
   */
  protected int hash = 0;

  /**
   * This Uniform Resource Identifier (URI).
   */
  protected char[] _uri = null;

  /**
   * The charset of the net used by this URI instance.
   */
  protected String protocolCharset = null;

  /**
   * The default charset of the net.  RFC 2277, 2396
   */
  protected static String defaultProtocolCharset = "UTF-8";

  /**
   * The default charset of the document.  RFC 2277, 2396
   */
  protected static String defaultDocumentCharset = null;
  protected static String defaultDocumentCharsetByLocale = null;
  protected static String defaultDocumentCharsetByPlatform = null;

  // Static initializer for defaultDocumentCharset
  static {
    Locale locale = Locale.getDefault();
    // in order to support backward compatiblity
    if (locale != null) {
      defaultDocumentCharsetByLocale = LocaleToCharsetMap.getCharset(locale);
      // set the default document charset
      defaultDocumentCharset = defaultDocumentCharsetByLocale;
    }
    // in order to support platform encoding
    try {
      defaultDocumentCharsetByPlatform = System.getProperty("file.encoding");
    } catch (SecurityException se) {
      // $JL-EXC$
    }
    if (defaultDocumentCharset == null) {
      // set the default document charset
      defaultDocumentCharset = defaultDocumentCharsetByPlatform;
    }
  }

  /**
   * The scheme.
   */
  protected char[] _scheme = null;

  /**
   * The opaque.
   */
  protected char[] _opaque = null;

  /**
   * The authority.
   */
  protected char[] _authority = null;

  /**
   * The userinfo.
   */
  protected char[] _userinfo = null;

  /**
   * The host.
   */
  protected char[] _host = null;

  /**
   * The port.
   */
  protected int _port = -1;

  /**
   * The path.
   */
  protected char[] _path = null;

  /**
   * The query.
   */
  protected char[] _query = null;

  /**
   * The fragment.
   */
  protected char[] _fragment = null;

  /**
   * The root path.
   */
  protected static char[] rootPath = {'/'};

  /**
   * The percent "%" character always has the reserved purpose of being the escape indicator,
   * it must be escaped as "%25" in order to be used as data within a URI.
   */
  protected static final BitSet percent = new BitSet(256);

  static {
    percent.set('%');
  }

  /**
   * BitSet for digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
   */
  protected static final BitSet digit = new BitSet(256);

  static {
    for (int i = '0'; i <= '9'; i++) {
      digit.set(i);
    }
  }

  /**
   * BitSet for alpha = lowalpha | upalpha
   */
  protected static final BitSet alpha = new BitSet(256);

  static {
    for (int i = 'a'; i <= 'z'; i++) {
      alpha.set(i);
    }
    for (int i = 'A'; i <= 'Z'; i++) {
      alpha.set(i);
    }
  }

  /**
   * BitSet for alphanum = alpha | digit
   */
  protected static final BitSet alphanum = new BitSet(256);

  static {
    alphanum.or(alpha);
    alphanum.or(digit);
  }

  /**
   * BitSet for hex = digit | "A" | "B" | "C" | "D" | "E" | "F" | "a" | "b" | "c" | "d" | "e" | "f"
   */
  protected static final BitSet hex = new BitSet(256);

  static {
    hex.or(digit);
    for (int i = 'a'; i <= 'f'; i++) {
      hex.set(i);
    }
    for (int i = 'A'; i <= 'F'; i++) {
      hex.set(i);
    }
  }

  /**
   * BitSet for escaped = "%" hex hex
   */
  protected static final BitSet escaped = new BitSet(256);

  static {
    escaped.or(percent);
    escaped.or(hex);
  }

  /**
   * BitSet for mark = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
   */
  protected static final BitSet mark = new BitSet(256);

  static {
    mark.set('-');
    mark.set('_');
    mark.set('.');
    mark.set('!');
    mark.set('~');
    mark.set('*');
    mark.set('\'');
    mark.set('(');
    mark.set(')');
  }

  /**
   * Data characters that are allowed in a URI but do not have a reserved purpose are
   * called unreserved = alphanum | mark
   */
  protected static final BitSet unreserved = new BitSet(256);

  static {
    unreserved.or(alphanum);
    unreserved.or(mark);
  }

  /**
   * BitSet for reserved = ";" | "/" | "?" | ":" | "@" | "&amp;" | "=" | "+" | "$" | ","
   */
  protected static final BitSet reserved = new BitSet(256);

  static {
    reserved.set(';');
    reserved.set('/');
    reserved.set('?');
    reserved.set(':');
    reserved.set('@');
    reserved.set('&');
    reserved.set('=');
    reserved.set('+');
    reserved.set('$');
    reserved.set(',');
  }

  /**
   * BitSet for uric = reserved | unreserved | escaped
   * </pre></blockquote><p>
   */
  protected static final BitSet uric = new BitSet(256);

  static {
    uric.or(reserved);
    uric.or(unreserved);
    uric.or(escaped);
  }

  /**
   * BitSet for fragment (alias for uric).
   */
  protected static final BitSet fragment = uric;

  /**
   * BitSet for query (alias for uric).
   */
  protected static final BitSet query = uric;

  /**
   * BitSet for pchar = unreserved | escaped | ":" | "@" | "&amp;" | "=" | "+" | "$" | ","
   */
  protected static final BitSet pchar = new BitSet(256);

  static {
    pchar.or(unreserved);
    pchar.or(escaped);
    pchar.set(':');
    pchar.set('@');
    pchar.set('&');
    pchar.set('=');
    pchar.set('+');
    pchar.set('$');
    pchar.set(',');
  }

  /**
   * BitSet for param (alias for pchar).
   */
  protected static final BitSet param = pchar;

  /**
   * BitSet for segment = *pchar *( ";" param )
   */
  protected static final BitSet segment = new BitSet(256);

  static {
    segment.or(pchar);
    segment.set(';');
    segment.or(param);
  }

  /**
   * BitSet for path segments = segment *( "/" segment )
   */
  protected static final BitSet path_segments = new BitSet(256);

  static {
    path_segments.set('/');
    path_segments.or(segment);
  }

  /**
   * URI absolute path = "/"  path_segments
   */
  protected static final BitSet abs_path = new BitSet(256);

  static {
    abs_path.set('/');
    abs_path.or(path_segments);
  }

  /**
   * URI bitset for encoding typical non-slash characters
   * uric_no_slash = unreserved | escaped | ";" | "?" | ":" | "@" | "&amp;" | "=" | "+" | "$" | ","
   */
  protected static final BitSet uric_no_slash = new BitSet(256);

  static {
    uric_no_slash.or(unreserved);
    uric_no_slash.or(escaped);
    uric_no_slash.set(';');
    uric_no_slash.set('?');
    uric_no_slash.set(';');
    uric_no_slash.set('@');
    uric_no_slash.set('&');
    uric_no_slash.set('=');
    uric_no_slash.set('+');
    uric_no_slash.set('$');
    uric_no_slash.set(',');
  }

  /**
   * URI bitset that combines uric_no_slash and uric.
   * opaque_part   = uric_no_slash *uric
   */
  protected static final BitSet opaque_part = new BitSet(256);

  static {
    // it's generous. because first character must not include a slash
    opaque_part.or(uric_no_slash);
    opaque_part.or(uric);
  }

  /**
   * URI bitset that combines absolute path and opaque part.
   * path          = [ abs_path | opaque_part ]
   */
  protected static final BitSet path = new BitSet(256);

  static {
    path.or(abs_path);
    path.or(opaque_part);
  }

  /**
   * Port, a logical alias for digit.
   */
  protected static final BitSet port = digit;

  /**
   * Bitset that combines digit and dot fo IPv4address = 1*digit "." 1*digit "." 1*digit "." 1*digit
   */
  protected static final BitSet IPv4address = new BitSet(256);

  static {
    IPv4address.or(digit);
    IPv4address.set('.');
  }

  /**
   * RFC 2373. IPv6address = hexpart [ ":" IPv4address ]
   */
  protected static final BitSet IPv6address = new BitSet(256);

  static {
    IPv6address.or(hex); // hexpart
    IPv6address.set(':');
    IPv6address.or(IPv4address);
  }

  /**
   * RFC 2732, 2373. IPv6reference   = "[" IPv6address "]"
   */
  protected static final BitSet IPv6reference = new BitSet(256);

  static {
    IPv6reference.set('[');
    IPv6reference.or(IPv6address);
    IPv6reference.set(']');
  }

  /**
   * BitSet for toplabel = alpha | alpha *( alphanum | "-" ) alphanum
   */
  protected static final BitSet toplabel = new BitSet(256);

  static {
    toplabel.or(alphanum);
    toplabel.set('-');
  }

  /**
   * BitSet for domainlabel = alphanum | alphanum *( alphanum | "-" ) alphanum
   */
  protected static final BitSet domainlabel = toplabel;

   static {
    domainlabel.set('_');  // allow underscore for domain labels
  }

  /**
   * BitSet for hostname = *( domainlabel "." ) toplabel [ "." ]
   */
  protected static final BitSet hostname = new BitSet(256);

  static {
//    hostname.or(toplabel);
    hostname.or(domainlabel);
    hostname.set('.');
  }

  /**
   * BitSet for host = hostname | IPv4address | IPv6reference
   */
  protected static final BitSet host = new BitSet(256);

  static {
    host.or(hostname);
    // host.or(IPv4address);
    host.or(IPv6reference); // IPv4address
  }

  /**
   * BitSet for hostport = host [ ":" port ]
   */
  protected static final BitSet hostport = new BitSet(256);

  static {
    hostport.or(host);
    hostport.set(':');
    hostport.or(port);
  }

  /**
   * Bitset for userinfo = *( unreserved | escaped | ";" | ":" | "&amp;" | "=" | "+" | "$" | "," )
   */
  protected static final BitSet userinfo = new BitSet(256);

  static {
    userinfo.or(unreserved);
    userinfo.or(escaped);
    userinfo.set(';');
    userinfo.set(':');
    userinfo.set('&');
    userinfo.set('=');
    userinfo.set('+');
    userinfo.set('$');
    userinfo.set(',');
  }

  /**
   * BitSet for within the userinfo component like user and password.
   */
  public static final BitSet within_userinfo = new BitSet(256);

  static {
    within_userinfo.or(userinfo);
    within_userinfo.clear(';'); // reserved within authority
    within_userinfo.clear(':');
    within_userinfo.clear('@');
    within_userinfo.clear('?');
    within_userinfo.clear('/');
  }

  /**
   * Bitset for server = [ [ userinfo "@" ] hostport ]
   */
  protected static final BitSet server = new BitSet(256);

  static {
    server.or(userinfo);
    server.set('@');
    server.or(hostport);
  }

  /**
   * BitSet for reg_name = 1*( unreserved | escaped | "$" | "," | ";" | ":" | "@" | "&amp;" | "=" | "+" )
   */
  protected static final BitSet reg_name = new BitSet(256);

  static {
    reg_name.or(unreserved);
    reg_name.or(escaped);
    reg_name.set('$');
    reg_name.set(',');
    reg_name.set(';');
    reg_name.set(':');
    reg_name.set('@');
    reg_name.set('&');
    reg_name.set('=');
    reg_name.set('+');
  }

  /**
   * BitSet for authority = server | reg_name
   */
  protected static final BitSet authority = new BitSet(256);

  static {
    authority.or(server);
    authority.or(reg_name);
  }

  /**
   * BitSet for scheme = alpha *( alpha | digit | "+" | "-" | "." )
   */
  protected static final BitSet scheme = new BitSet(256);

  static {
    scheme.or(alpha);
    scheme.or(digit);
    scheme.set('+');
    scheme.set('-');
    scheme.set('.');
  }

  /**
   * BitSet for rel_segment = 1*( unreserved | escaped | ";" | "@" | "&amp;" | "=" | "+" | "$" | "," )
   */
  protected static final BitSet rel_segment = new BitSet(256);

  static {
    rel_segment.or(unreserved);
    rel_segment.or(escaped);
    rel_segment.set(';');
    rel_segment.set('@');
    rel_segment.set('&');
    rel_segment.set('=');
    rel_segment.set('+');
    rel_segment.set('$');
    rel_segment.set(',');
  }

  /**
   * BitSet for rel_path = rel_segment [ abs_path ]
   */
  protected static final BitSet rel_path = new BitSet(256);

  static {
    rel_path.or(rel_segment);
    rel_path.or(abs_path);
  }

  /**
   * BitSet for net_path = "//" authority [ abs_path ]
   */
  protected static final BitSet net_path = new BitSet(256);

  static {
    net_path.set('/');
    net_path.or(authority);
    net_path.or(abs_path);
  }

  /**
   * BitSet for hier_part = ( net_path | abs_path ) [ "?" query ]
   */
  protected static final BitSet hier_part = new BitSet(256);

  static {
    hier_part.or(net_path);
    hier_part.or(abs_path);
    // hier_part.set('?'); aleady included
    hier_part.or(query);
  }

  /**
   * BitSet for relativeURI.
   * <p><blockquote><pre>
   * relativeURI   = ( net_path | abs_path | rel_path ) [ "?" query ]
   * </pre></blockquote><p>
   */
  protected static final BitSet relativeURI = new BitSet(256);

  // Static initializer for relativeURI
  static {
    relativeURI.or(net_path);
    relativeURI.or(abs_path);
    relativeURI.or(rel_path);
    // relativeURI.set('?'); aleady included
    relativeURI.or(query);
  }

  /**
   * BitSet for absoluteURI = scheme ":" ( hier_part | opaque_part )
   */
  protected static final BitSet absoluteURI = new BitSet(256);

  static {
    absoluteURI.or(scheme);
    absoluteURI.set(':');
    absoluteURI.or(hier_part);
    absoluteURI.or(opaque_part);
  }

  /**
   * BitSet for URI-reference = [ absoluteURI | relativeURI ] [ "#" fragment ]
   */
  protected static final BitSet URI_reference = new BitSet(256);

  static {
    URI_reference.or(absoluteURI);
    URI_reference.or(relativeURI);
    URI_reference.set('#');
    URI_reference.or(fragment);
  }

  /**
   * BitSet for control.
   */
  public static final BitSet control = new BitSet(256);

  static {
    for (int i = 0; i <= 0x1F; i++) {
      control.set(i);
    }
    control.set(0x7F);
  }

  /**
   * BitSet for space.
   */
  public static final BitSet space = new BitSet(256);

  static {
    space.set(0x20);
  }

  /**
   * BitSet for delims.
   */
  public static final BitSet delims = new BitSet(256);

  static {
    delims.set('<');
    delims.set('>');
    delims.set('#');
    delims.set('%');
    delims.set('"');
  }

    /**
   * BitSet for unwise.
   */
  public static final BitSet unwise = new BitSet(256);

  static {
    unwise.set('{');
    unwise.set('}');
    unwise.set('|');
    unwise.set('\\');
    unwise.set('^');
    unwise.set('[');
    unwise.set(']');
    unwise.set('`');
  }

    /**
   * Disallowed rel_path before escaping.
   */
  public static final BitSet disallowed_rel_path = new BitSet(256);

  static {
    disallowed_rel_path.or(uric);
    disallowed_rel_path.andNot(rel_path);
  }

  /**
   * Disallowed opaque_part before escaping.
   */
  public static final BitSet disallowed_opaque_part = new BitSet(256);

  static {
    disallowed_opaque_part.or(uric);
    disallowed_opaque_part.andNot(opaque_part);
  }

  /**
   * Those characters that are allowed for the authority component.
   */
  public static final BitSet allowed_authority = new BitSet(256);

  static {
    allowed_authority.or(authority);
    allowed_authority.clear('%');
  }

  /**
   * Those characters that are allowed for the opaque_part.
   */
  public static final BitSet allowed_opaque_part = new BitSet(256);

  static {
    allowed_opaque_part.or(opaque_part);
    allowed_opaque_part.clear('%');
  }

  /**
   * Those characters that are allowed for the reg_name.
   */
  public static final BitSet allowed_reg_name = new BitSet(256);

  static {
    allowed_reg_name.or(reg_name);
    // allowed_reg_name.andNot(percent);
    allowed_reg_name.clear('%');
  }

  /**
   * Those characters that are allowed for the userinfo component.
   */
  public static final BitSet allowed_userinfo = new BitSet(256);

  static {
    allowed_userinfo.or(userinfo);
    // allowed_userinfo.andNot(percent);
    allowed_userinfo.clear('%');
  }

  /**
   * Those characters that are allowed for within the userinfo component.
   */
  public static final BitSet allowed_within_userinfo = new BitSet(256);

  static {
    allowed_within_userinfo.or(within_userinfo);
    allowed_within_userinfo.clear('%');
  }

   /**
   * Those characters that are allowed for the IPv6reference component.
   * The characters '[', ']' in IPv6reference should be excluded.
   */
  public static final BitSet allowed_IPv6reference = new BitSet(256);

  static {
    allowed_IPv6reference.or(IPv6reference);
    // allowed_IPv6reference.andNot(unwise);
    allowed_IPv6reference.clear('[');
    allowed_IPv6reference.clear(']');
  }

   /**
   * Those characters that are allowed for the host component.
   * The characters '[', ']' in IPv6reference should be excluded.
   */
  public static final BitSet allowed_host = new BitSet(256);

  static {
    allowed_host.or(hostname);
    allowed_host.or(allowed_IPv6reference);
  }

   /**
   * Those characters that are allowed for the authority component.
   */
  public static final BitSet allowed_within_authority = new BitSet(256);

  static {
    allowed_within_authority.or(server);
    allowed_within_authority.or(reg_name);
    allowed_within_authority.clear(';');
    allowed_within_authority.clear(':');
    allowed_within_authority.clear('@');
    allowed_within_authority.clear('?');
    allowed_within_authority.clear('/');
  }

   /**
   * Those characters that are allowed for the abs_path.
   */
  public static final BitSet allowed_abs_path = new BitSet(256);

  static {
    allowed_abs_path.or(abs_path);
    // allowed_abs_path.set('/');  // aleady included
    allowed_abs_path.andNot(percent);
  }

   /**
   * Those characters that are allowed for the rel_path.
   */
  public static final BitSet allowed_rel_path = new BitSet(256);

  static {
    allowed_rel_path.or(rel_path);
    allowed_rel_path.clear('%');
  }

   /**
   * Those characters that are allowed within the path.
   */
  public static final BitSet allowed_within_path = new BitSet(256);

  static {
    allowed_within_path.or(abs_path);
    allowed_within_path.clear('/');
    allowed_within_path.clear(';');
    allowed_within_path.clear('=');
    allowed_within_path.clear('?');
  }

   /**
   * Those characters that are allowed for the query component.
   */
  public static final BitSet allowed_query = new BitSet(256);

  static {
    allowed_query.or(uric);
    allowed_query.clear('%');
  }

   /**
   * Those characters that are allowed within the query component.
   */
  public static final BitSet allowed_within_query = new BitSet(256);

  static {
    allowed_within_query.or(allowed_query);
    allowed_within_query.andNot(reserved); // excluded 'reserved'
  }

   /**
   * Those characters that are allowed for the fragment component.
   */
  public static final BitSet allowed_fragment = new BitSet(256);

  static {
    allowed_fragment.or(uric);
    allowed_fragment.clear('%');
  }

  // Flags for this URI-reference
  // URI-reference = [ absoluteURI | relativeURI ] [ "#" fragment ]
  // absoluteURI   = scheme ":" ( hier_part | opaque_part )
  protected boolean _is_hier_part;
  protected boolean _is_opaque_part;
  // relativeURI   = ( net_path | abs_path | rel_path ) [ "?" query ]
  // hier_part     = ( net_path | abs_path ) [ "?" query ]
  protected boolean _is_net_path;
  protected boolean _is_abs_path;
  protected boolean _is_rel_path;
  // net_path      = "//" authority [ abs_path ]
  // authority     = server | reg_name
  protected boolean _is_reg_name;
  protected boolean _is_server;  // = _has_server
  // server        = [ [ userinfo "@" ] hostport ]
  // host          = hostname | IPv4address | IPv6reference
  protected boolean _is_hostname;
  protected boolean _is_IPv4address;
  protected boolean _is_IPv6reference;

  /**
   * Create an instance as an internal use
   */
  protected URI() {
  }

  /**
   * Construct a URI from a string with the specified charset.
   *
   * @param s       URI character sequence
   * @param escaped <tt>true</tt> if URI character sequence is in escaped form, <tt>false</tt> otherwise.
   * @param charset the charset string to do escape encoding, if required
   * @throws URIException         If the URI cannot be created.
   * @throws NullPointerException if incoming string is <code>null</code>
   */
  public URI(String s, boolean escaped, String charset) throws URIException, NullPointerException {
    protocolCharset = charset;
    parseUriReference(s, escaped);
  }

  /**
   * Construct a URI from a string
   *
   * @param s       URI character sequence
   * @throws URIException         If the URI cannot be created.
   * @throws NullPointerException if incoming string is <code>null</code>
   */
  public URI(String s) throws URIException, NullPointerException {
    parseUriReference(s, false);
  }

  /**
   * Construct a URI from a string.
   *
   * @param s       URI character sequence
   * @param escaped <tt>true</tt> if URI character sequence is in escaped form, <tt>false</tt> otherwise.
   * @throws URIException         If the URI cannot be created.
   * @throws NullPointerException if incoming string is <code>null</code>
   */
  public URI(String s, boolean escaped) throws URIException, NullPointerException {
    parseUriReference(s, escaped);
  }

  /**
   * Construct a general URI from the specified components.
   * <p><blockquote><pre>
   *   URI-reference = [ absoluteURI | relativeURI ] [ "#" fragment ]
   *   absoluteURI   = scheme ":" ( hier_part | opaque_part )
   *   opaque_part   = uric_no_slash *uric
   * </pre></blockquote><p>
   * It's for absolute URI = &lt;scheme&gt;:&lt;scheme-specific-part&gt;#
   * &lt;fragment&gt;.
   *
   * @param scheme             the scheme string
   * @param schemeSpecificPart scheme_specific_part
   * @param fragment           the fragment string
   * @throws URIException If the URI cannot be created.
   */
  public URI(String scheme, String schemeSpecificPart, String fragment) throws URIException {
    // validate and contruct the URI character sequence
    if (scheme == null) {
      throw new URIException(URIException.PARSING, "scheme required");
    }
    char[] s = scheme.toLowerCase().toCharArray();
    if (validate(s, URI.scheme)) {
      _scheme = s; // is_absoluteURI
    } else {
      throw new URIException(URIException.PARSING, "incorrect scheme");
    }
    _opaque = encode(schemeSpecificPart, allowed_opaque_part, getProtocolCharset());
    // Set flag
    _is_opaque_part = true;
    _fragment = fragment.toCharArray();
    setURI();
  }

  /**
   * Construct a general URI from the specified components.
   *
   * @param scheme    the scheme string
   * @param authority the authority string
   * @param path      the path string
   * @param query     the query string
   * @param fragment  the fragment string
   * @throws URIException If the new URI cannot be created.
   */
  public URI(String scheme, String authority, String path, String query, String fragment) throws URIException {
    StringBuilder buff = new StringBuilder();
    if (scheme != null) {
      buff.append(scheme);
      buff.append(':');
    }
    if (authority != null) {
      buff.append("//");
      buff.append(authority);
    }
    if (path != null) {  // accept empty path
      if ((scheme != null || authority != null)
              && !path.startsWith("/")) {
        throw new URIException(URIException.PARSING, "abs_path requested");
      }
      buff.append(path);
    }
    if (query != null) {
      buff.append('?');
      buff.append(query);
    }
    if (fragment != null) {
      buff.append('#');
      buff.append(fragment);
    }
    parseUriReference(buff.toString(), false);
  }

  /**
   * Construct a general URI from the specified components.
   *
   * @param scheme   the scheme string
   * @param userinfo the userinfo string
   * @param host     the host string
   * @param port     the port number
   * @throws URIException If the new URI cannot be created.
   */
  public URI(String scheme, String userinfo, String host, int port) throws URIException {
    this(scheme, userinfo, host, port, null, null, null);
  }

  /**
   * Construct a general URI from the specified components.
   *
   * @param scheme   the scheme string
   * @param userinfo the userinfo string
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @throws URIException If the new URI cannot be created.
   */
  public URI(String scheme, String userinfo, String host, int port, String path) throws URIException {
    this(scheme, userinfo, host, port, path, null, null);
  }

  /**
   * Construct a general URI from the specified components.
   *
   * @param scheme   the scheme string
   * @param userinfo the userinfo string
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    the query string
   * @throws URIException If the new URI cannot be created.
   */
  public URI(String scheme, String userinfo, String host, int port, String path, String query) throws URIException {
    this(scheme, userinfo, host, port, path, query, null);
  }

  /**
   * Construct a general URI from the specified components.
   *
   * @param scheme   the scheme string
   * @param userinfo the userinfo string
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If the new URI cannot be created.
   */
  public URI(String scheme, String userinfo, String host, int port,
             String path, String query, String fragment) throws URIException {

    this(scheme, (host == null) ? null
            : ((userinfo != null) ? userinfo + '@' : "") + host
            + ((port != -1) ? ":" + port : ""), path, query, fragment);
  }

  /**
   * Construct a general URI from the specified components.
   *
   * @param scheme   the scheme string
   * @param host     the host string
   * @param path     the path string
   * @param fragment the fragment string
   * @throws URIException If the new URI cannot be created.
   */
  public URI(String scheme, String host, String path, String fragment) throws URIException {
    this(scheme, host, path, null, fragment);
  }

  /**
   * Construct a general URI with the specified relative URI string.
   *
   * @param base     the base URI
   * @param relative the relative URI string
   * @param escaped  <tt>true</tt> if URI character sequence is in escaped form.
   *                 <tt>false</tt> otherwise.
   * @throws URIException If the new URI cannot be created.
   */
  public URI(URI base, String relative, boolean escaped) throws URIException {
    this(base, new URI(relative, escaped));
  }

  /**
   * Construct a general URI with the specified relative URI.
   *
   * @param base     the base URI
   * @param relative the relative URI
   * @throws URIException If the new URI cannot be created.
   */
  public URI(URI base, URI relative) throws URIException {
    if (base._scheme == null) {
      throw new URIException(URIException.PARSING, "base URI required");
    }
    if (base._scheme != null) {
      this._scheme = base._scheme;
      this._authority = base._authority;
    }
    if (base._is_opaque_part || relative._is_opaque_part) {
      this._scheme = base._scheme;
      this._is_opaque_part = base._is_opaque_part
              || relative._is_opaque_part;
      this._opaque = relative._opaque;
      this._fragment = relative._fragment;
      this.setURI();
      return;
    }
    if (relative._scheme != null) {
      this._scheme = relative._scheme;
      this._is_net_path = relative._is_net_path;
      this._authority = relative._authority;
      if (relative._is_server) {
        this._is_server = relative._is_server;
        this._userinfo = relative._userinfo;
        this._host = relative._host;
        this._port = relative._port;
      } else if (relative._is_reg_name) {
        this._is_reg_name = relative._is_reg_name;
      }
      this._is_abs_path = relative._is_abs_path;
      this._is_rel_path = relative._is_rel_path;
      this._path = relative._path;
    } else if (base._authority != null && relative._scheme == null) {
      this._is_net_path = base._is_net_path;
      this._authority = base._authority;
      if (base._is_server) {
        this._is_server = base._is_server;
        this._userinfo = base._userinfo;
        this._host = base._host;
        this._port = base._port;
      } else if (base._is_reg_name) {
        this._is_reg_name = base._is_reg_name;
      }
    }
    if (relative._authority != null) {
      this._is_net_path = relative._is_net_path;
      this._authority = relative._authority;
      if (relative._is_server) {
        this._is_server = relative._is_server;
        this._userinfo = relative._userinfo;
        this._host = relative._host;
        this._port = relative._port;
      } else if (relative._is_reg_name) {
        this._is_reg_name = relative._is_reg_name;
      }
      this._is_abs_path = relative._is_abs_path;
      this._is_rel_path = relative._is_rel_path;
      this._path = relative._path;
    }
    // resolve the path and query if necessary
    if (relative._scheme == null && relative._authority == null) {
      if ((relative._path == null || relative._path.length == 0)
              && relative._query == null) {
        // handle a reference to the current document, see RFC 2396
        // section 5.2 step 2
        this._path = base._path;
        this._query = base._query;
      } else {
        this._path = resolvePath(base._path, relative._path);
      }
    }
    // base._query removed
    if (relative._query != null) {
      this._query = relative._query;
    }
    // base._fragment removed
    if (relative._fragment != null) {
      this._fragment = relative._fragment;
    }
    this.setURI();
    // reparse the newly built URI to ensure that all flags are set correctly ; TODO - remove maybe
    parseUriReference(new String(_uri), true);
  }

  /**
   * Encodes URI string.
   *
   * @param original the original character sequence
   * @param allowed  those characters that are allowed within a component
   * @param charset  the net charset
   * @return URI character sequence
   */

  protected static char[] encode(String original, BitSet allowed, String charset) {
    if (original == null) {
      throw new IllegalArgumentException("Original string is null");
    }
    if (allowed == null) {
      throw new IllegalArgumentException("Allowed bitset is null");
    }
    byte[] rawdata = URLCodec.encodeUrl(allowed, EncodingUtil.getBytes(original, charset));
    return EncodingUtil.getASCIIString(rawdata).toCharArray();
  }

  /**
   * Decodes URI encoded string.
   *
   * @param component the URI character sequence
   * @param charset   the net charset
   * @return original character sequence
   * @throws URIException incomplete trailing escape pattern or unsupported
   *                      character encoding
   */
  protected static String decode(char[] component, String charset) throws URIException {
    if (component == null) {
      throw new IllegalArgumentException("Component array of chars is null");
    }
    return decode(new String(component), charset);
  }

  /**
   * Decodes URI encoded string.
   *
   * @param component the URI character sequence
   * @param charset   the net charset
   * @return original character sequence
   * @throws URIException incomplete trailing escape pattern or unsupported
   *                      character encoding
   */
  protected static String decode(String component, String charset) throws URIException {
    if (component == null) {
      throw new IllegalArgumentException("Component array of chars is null");
    }
    byte[] rawdata;
    try {
      rawdata = URLCodec.decodeUrl(EncodingUtil.getASCIIBytes(component));
    } catch (URLDecodeException e) {
      throw new URIException(e.getMessage());
    }
    return EncodingUtil.getString(rawdata, charset);
  }

  /**
   * Pre-validate the unescaped URI string within a specific component.
   *
   * @param component  the component string within the component
   * @param disallowed those characters disallowed within the component
   * @return if true, it doesn't have the disallowed characters
   *         if false, the component is undefined or an incorrect one
   */
  protected boolean prevalidate(String component, BitSet disallowed) {
    // prevalidate the specified component by disallowed characters
    if (component == null) {
      return false; // undefined
    }
    char[] target = component.toCharArray();
    for (char t : target) {
      if (disallowed.get(t)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Validate the URI characters within a specific component.
   *
   * @param component the characters sequence within the component
   * @param generous  those characters that are allowed within a component
   * @return if true, it's the correct URI character sequence
   */
  protected boolean validate(char[] component, BitSet generous) {
    return validate(component, 0, -1, generous);
  }

  /**
   * Validate the URI characters within a specific component.
   *
   * @param component the characters sequence within the component
   * @param soffset   the starting offset of the specified component
   * @param eoffset   the ending offset of the specified component
   *                  if -1, it means the length of the component
   * @param generous  those characters that are allowed within a component
   * @return if true, it's the correct URI character sequence
   */
  protected boolean validate(char[] component, int soffset, int eoffset, BitSet generous) {
    if (eoffset == -1) {
      eoffset = component.length - 1;
    }
    for (int i = soffset; i <= eoffset; i++) {
      if (!generous.get(component[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Parses a URI reference from a specified <code>String</code>
   *
   * @param original the original character sequence
   * @param escaped  <code>true</code> if <code>original</code> is escaped
   * @throws URIException If an error occurs.
   */
  protected void parseUriReference(String original, boolean escaped) throws URIException {
    // validate and contruct the URI character sequence
    if (original == null) {
      throw new URIException("URI-Reference required");
    }
    /* @
     *  ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
     */
    String tmp = original.trim();
    /*
     * The length of the string sequence of characters.
     * It may not be equal to the length of the byte array.
     */
    int length = tmp.length();
    /*
     * Remove the delimiters like angle brackets around an URI.
     */
    if (length > 0) {
      char[] firstDelimiter = {tmp.charAt(0)};
      if (validate(firstDelimiter, delims)) {
        if (length >= 2) {
          char[] lastDelimiter = {tmp.charAt(length - 1)};
          if (validate(lastDelimiter, delims)) {
            tmp = tmp.substring(1, length - 1);
            length = length - 2;
          }
        }
      }
    }
    /*
     * The starting index
     */
    int from = 0;
    /*
     * The test flag whether the URI is started from the path component.
     */
    boolean isStartedFromPath = false;
    int atColon = tmp.indexOf(':');
    int atSlash = tmp.indexOf('/');
    if (atColon <= 0 || (atSlash >= 0 && atSlash < atColon)) {
      isStartedFromPath = true;
    }
    /*
     * <p><blockquote><pre>
     *     @@@@@@@@
     *  ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
     * </pre></blockquote><p>
     */
    int at = indexFirstOf(tmp, isStartedFromPath ? "/?#" : ":/?#", from);
    if (at == -1) {
      at = 0;
    }
    /*
     * Parse the scheme.
     * <p><blockquote><pre>
     *  scheme    =  $2 = http
     *              @
     *  ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
     * </pre></blockquote><p>
     */
    if (at > 0 && at < length && tmp.charAt(at) == ':') {
      char[] target = tmp.substring(0, at).toLowerCase().toCharArray();
      if (validate(target, scheme)) {
        _scheme = target;
      } else {
        throw new URIException("incorrect scheme");
      }
      from = ++at;
    }
    /*
     * Parse the authority component.
     * <p><blockquote><pre>
     *  authority =  $4 = www.sap.com
     *                  @@
     *  ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
     * </pre></blockquote><p>
     */
    // Reset flags
    _is_net_path = _is_abs_path = _is_rel_path = _is_hier_part = false;
    if (0 <= at && at < length && tmp.charAt(at) == '/') {
      // Set flag
      _is_hier_part = true;
      if (at + 2 < length && tmp.charAt(at + 1) == '/') {
        // the temporary index to start the search from
        int next = indexFirstOf(tmp, "/?#", at + 2);
        if (next == -1) {
          next = (tmp.substring(at + 2).length() == 0) ? at + 2 : tmp.length();
        }
        parseAuthority(tmp.substring(at + 2, next), escaped);
        from = at = next;
        // Set flag
        _is_net_path = true;
      }
      if (from == at) {
        // Set flag
        _is_abs_path = true;
      }
    }
    /*
     * Parse the path component.
     * <p><blockquote><pre>
     *  path      =  $5 = /ietf/uri/
     *                                @@@@@@
     *  ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
     * </pre></blockquote><p>
     */
    if (from < length) {
      // rel_path = rel_segment [ abs_path ]
      int next = indexFirstOf(tmp, "?#", from);
      if (next == -1) {
        next = tmp.length();
      }
      if (!_is_abs_path) {
        if (!escaped
                && prevalidate(tmp.substring(from, next), disallowed_rel_path)
                || escaped
                && validate(tmp.substring(from, next).toCharArray(), rel_path)) {
          // Set flag
          _is_rel_path = true;
        } else if (!escaped
                && prevalidate(tmp.substring(from, next), disallowed_opaque_part)
                || escaped
                && validate(tmp.substring(from, next).toCharArray(), opaque_part)) {
          // Set flag
          _is_opaque_part = true;
        } else {
          // the path component may be empty
          _path = null;
        }
      }
      if (escaped) {
        setRawPath(tmp.substring(from, next).toCharArray());
      } else {
        setPath(tmp.substring(from, next));
      }
      at = next;
    }
    // set the charset to do escape encoding
    String charset = getProtocolCharset();
    /*
     * Parse the query component.
     * <p><blockquote><pre>
     *  query     =  $7 = <undefined>
     *                                        @@@@@@@@@
     *  ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
     * </pre></blockquote><p>
     */
    if (0 <= at && at + 1 < length && tmp.charAt(at) == '?') {
      int next = tmp.indexOf('#', at + 1);
      if (next == -1) {
        next = tmp.length();
      }
      _query = (escaped) ? tmp.substring(at + 1, next).toCharArray()
              : encode(tmp.substring(at + 1, next), allowed_query, charset);
      at = next;
    }
    /*
     * Parse the fragment component.
     * <p><blockquote><pre>
     *  fragment  =  $9 = Related
     *                                                   @@@@@@@@
     *  ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
     * </pre></blockquote><p>
     */
    if (0 <= at && at + 1 <= length && tmp.charAt(at) == '#') {
      if (at + 1 == length) { // empty fragment
        _fragment = "".toCharArray();
      } else {
        _fragment = (escaped) ? tmp.substring(at + 1).toCharArray()
                : encode(tmp.substring(at + 1), allowed_fragment, charset);
      }
    }
    // set this URI.
    setURI();
  }

  /**
   * Get the earlier index that to be searched for the first occurrance in
   * one of any of the specified string.
   *
   * @param s      the string to be indexed
   * @param delims the delimiters used to index
   * @return the earlier index if there are delimiters
   */
  protected int indexFirstOf(String s, String delims) {
    return indexFirstOf(s, delims, -1);
  }

  /**
   * Get the earlier index that to be searched for the first occurrance in one of any of the specified string.
   *
   * @param s      the string to be indexed
   * @param delims the delimiters used to index
   * @param offset the from index
   * @return the earlier index if there are delimiters
   */
  protected int indexFirstOf(String s, String delims, int offset) {
    if (s == null || s.length() == 0) {
      return -1;
    }
    if (delims == null || delims.length() == 0) {
      return -1;
    }
    // check boundaries
    if (offset < 0) {
      offset = 0;
    } else if (offset > s.length()) {
      return -1;
    }
    // s is never null
    int min = s.length();
    char[] delim = delims.toCharArray();
    for (char d : delim) {
      int at = s.indexOf(d, offset);
      if (at >= 0 && at < min) {
        min = at;
      }
    }
    return (min == s.length()) ? -1 : min;
  }

  /**
   * Get the earlier index that to be searched for the first occurrance in one of any of the specified array.
   *
   * @param s     the character array to be indexed
   * @param delim the delimiter used to index
   * @return the ealier index if there are a delimiter
   */
  protected int indexFirstOf(char[] s, char delim) {
    return indexFirstOf(s, delim, 0);
  }

  /**
   * Get the earlier index that to be searched for the first occurrance in one of any of the specified array.
   *
   * @param s      the character array to be indexed
   * @param delim  the delimiter used to index
   * @param offset The offset.
   * @return the ealier index if there is a delimiter
   */
  protected int indexFirstOf(char[] s, char delim, int offset) {
    if (s == null || s.length == 0) {
      return -1;
    }
    // check boundaries
    if (offset < 0) {
      offset = 0;
    } else if (offset > s.length) {
      return -1;
    }
    for (int i = offset; i < s.length; i++) {
      if (s[i] == delim) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Parse the authority component.
   *
   * @param original the original character sequence of authority component
   * @param escaped  <code>true</code> if <code>original</code> is escaped
   * @throws URIException If an error occurs.
   */
  protected void parseAuthority(String original, boolean escaped) throws URIException {
    // Reset flags
    _is_reg_name = _is_server = _is_hostname = _is_IPv4address = _is_IPv6reference = false;
    // set the charset to do escape encoding
    String charset = getProtocolCharset();
    boolean hasPort = true;
    int from = 0;
    int next = original.indexOf('@');
    if (next != -1) { // neither -1 and 0
      // each net extented from URI supports the specific userinfo
      _userinfo = (escaped) ? original.substring(0, next).toCharArray()
              : encode(original.substring(0, next), allowed_userinfo,
                      charset);
      from = next + 1;
    }
    next = original.indexOf('[', from);
    if (next >= from) {
      next = original.indexOf(']', from);
      if (next == -1) {
        throw new URIException(URIException.PARSING, "IPv6reference");
      } else {
        next++;
      }
      // In IPv6reference, '[', ']' should be excluded
      _host = (escaped) ? original.substring(from, next).toCharArray()
              : encode(original.substring(from, next), allowed_IPv6reference, charset);
      // Set flag
      _is_IPv6reference = true;
    } else { // only for !_is_IPv6reference
      next = original.indexOf(':', from);
      if (next == -1) {
        next = original.length();
        hasPort = false;
      }
      // doesn't need the pre-validation
      _host = original.substring(from, next).toCharArray();
      if (validate(_host, IPv4address)) {
        // Set flag
        _is_IPv4address = true;
      } else if (validate(_host, hostname)) {
        // Set flag
        _is_hostname = true;
      } else {
        // Set flag
        _is_reg_name = true;
      }
    }
    if (_is_reg_name) {
      // Reset flags for a server-based naming authority
      _is_server = _is_hostname = _is_IPv4address = _is_IPv6reference = false;
      // set a registry-based naming authority
      _authority = (escaped) ? original.toCharArray()
              : encode(original, allowed_reg_name, charset);
    } else {
      if (original.length() - 1 > next && hasPort
              && original.charAt(next) == ':') { // not empty
        from = next + 1;
        try {
          _port = Integer.parseInt(original.substring(from));
        } catch (NumberFormatException error) {
          throw new URIException(URIException.PARSING, "invalid port number");
        }
      }
      // set a server-based naming authority
      StringBuilder buf = new StringBuilder();
      if (_userinfo != null) { // has_userinfo
        buf.append(_userinfo);
        buf.append('@');
      }
      if (_host != null) {
        buf.append(_host);
        if (_port != -1) {
          buf.append(':');
          buf.append(_port);
        }
      }
      _authority = buf.toString().toCharArray();
      // Set flag
      _is_server = true;
    }
  }

  /**
   * Once it's parsed successfully, set this URI.
   */
  protected void setURI() {
    // set _uri
    StringBuilder buf = new StringBuilder(64);
    // ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
    if (_scheme != null) {
      buf.append(_scheme);
      buf.append(':');
    }
    if (_is_net_path) {
      buf.append("//");
      if (_authority != null) { // has_authority
        if (_userinfo != null) { // by default, remove userinfo part
          if (_host != null) {
            buf.append(_host);
            if (_port != -1) {
              buf.append(':');
              buf.append(_port);
            }
          }
        } else {
          buf.append(_authority);
        }
      }
    }
    if (_opaque != null && _is_opaque_part) {
      buf.append(_opaque);
    } else if (_path != null) {
      // _is_hier_part or _is_relativeURI
      if (_path.length != 0) {
        buf.append(_path);
      }
    }
    if (_query != null) { // has_query
      buf.append('?');
      buf.append(_query);
    }
    // ignore the fragment identifier
    _uri = buf.toString().toCharArray();
    hash = 0;
  }

  /**
   * Tell whether or not this URI is absolute.
   *
   * @return true iif this URI is absoluteURI
   */
  public boolean isAbsoluteURI() {
    return (_scheme != null);
  }

  /**
   * Tell whether or not this URI is relative.
   *
   * @return true iif this URI is relativeURI
   */
  public boolean isRelativeURI() {
    return (_scheme == null);
  }

  /**
   * Tell whether or not the absoluteURI of this URI is hier_part.
   *
   * @return true iif the absoluteURI is hier_part
   */
  public boolean isHierPart() {
    return _is_hier_part;
  }

  /**
   * Tell whether or not the absoluteURI of this URI is opaque_part.
   *
   * @return true iif the absoluteURI is opaque_part
   */
  public boolean isOpaquePart() {
    return _is_opaque_part;
  }

  /**
   * Tell whether or not the relativeURI or heir_part of this URI is net_path.
   * It's the same function as the has_authority() method.
   *
   * @return true iif the relativeURI or heir_part is net_path
   */
  public boolean isNetPath() {
    return _is_net_path || (_authority != null);
  }

  /**
   * Tell whether or not the relativeURI or hier_part of this URI is abs_path.
   *
   * @return true iif the relativeURI or hier_part is abs_path
   */
  public boolean isAbsPath() {
    return _is_abs_path;
  }

  /**
   * Tell whether or not the relativeURI of this URI is rel_path.
   *
   * @return true iif the relativeURI is rel_path
   */
  public boolean isRelPath() {
    return _is_rel_path;
  }

  /**
   * Tell whether or not this URI has authority.
   * It's the same function as the is_net_path() method.
   *
   * @return true iif this URI has authority
   */
  public boolean hasAuthority() {
    return (_authority != null) || _is_net_path;
  }

  /**
   * Tell whether or not the authority component of this URI is reg_name.
   *
   * @return true iif the authority component is reg_name
   */
  public boolean isRegName() {
    return _is_reg_name;
  }

  /**
   * Tell whether or not the authority component of this URI is server.
   *
   * @return true iif the authority component is server
   */
  public boolean isServer() {
    return _is_server;
  }

  /**
   * Tell whether or not this URI has userinfo.
   *
   * @return true iif this URI has userinfo
   */
  public boolean hasUserinfo() {
    return (_userinfo != null);
  }

  /**
   * Tell whether or not the host part of this URI is hostname.
   *
   * @return true iif the host part is hostname
   */
  public boolean isHostname() {
    return _is_hostname;
  }

  /**
   * Tell whether or not the host part of this URI is IPv4address.
   *
   * @return true iif the host part is IPv4address
   */
  public boolean isIPv4address() {
    return _is_IPv4address;
  }

  /**
   * Tell whether or not the host part of this URI is IPv6reference.
   *
   * @return true iif the host part is IPv6reference
   */
  public boolean isIPv6reference() {
    return _is_IPv6reference;
  }

  /**
   * Tell whether or not this URI has query.
   *
   * @return true iif this URI has query
   */
  public boolean hasQuery() {
    return (_query != null);
  }

  /**
   * Tell whether or not this URI has fragment.
   *
   * @return true iif this URI has fragment
   */
  public boolean hasFragment() {
    return (_fragment != null);
  }

  /**
   * Set the default charset of the net.
   *
   * @param charset the default charset for each net
   * @throws DefaultCharsetChanged default charset changed
   */
  public static void setDefaultProtocolCharset(String charset) throws DefaultCharsetChanged {
    defaultProtocolCharset = charset;
    throw new DefaultCharsetChanged(DefaultCharsetChanged.PROTOCOL_CHARSET,
            "the default net charset changed");
  }

  /**
   * Get the default charset of the net.
   *
   * @return the default charset string
   */
  public static String getDefaultProtocolCharset() {
    return defaultProtocolCharset;
  }

  /**
   * Get the net charset used by this current URI instance.
   *
   * @return the net charset string
   */
  public String getProtocolCharset() {
    return (protocolCharset != null) ? protocolCharset : defaultProtocolCharset;
  }

  /**
   * Set the default charset of the document.
   *
   * @param charset the default charset for the document
   * @throws DefaultCharsetChanged default charset changed
   */
  public static void setDefaultDocumentCharset(String charset) throws DefaultCharsetChanged {
    defaultDocumentCharset = charset;
    throw new DefaultCharsetChanged(DefaultCharsetChanged.DOCUMENT_CHARSET,
            "the default document charset changed");
  }

  /**
   * Get the recommended default charset of the document.
   *
   * @return the default charset string
   */
  public static String getDefaultDocumentCharset() {
    return defaultDocumentCharset;
  }

  /**
   * Get the default charset of the document by locale.
   *
   * @return the default charset string by locale
   */
  public static String getDefaultDocumentCharsetByLocale() {
    return defaultDocumentCharsetByLocale;
  }

  /**
   * Get the default charset of the document by platform.
   *
   * @return the default charset string by platform
   */
  public static String getDefaultDocumentCharsetByPlatform() {
    return defaultDocumentCharsetByPlatform;
  }

  /**
   * Get the scheme.
   *
   * @return the scheme
   */
  public char[] getRawScheme() {
    return _scheme;
  }

  /**
   * Get the scheme.
   *
   * @return the scheme
   *         null if undefined scheme
   */
  public String getScheme() {
    return (_scheme == null) ? null : new String(_scheme);
  }

  /**
   * Set the raw authority = server | reg_name
   *
   * @param escapedAuthority the raw escaped authority
   * @throws URIException         If {@link #parseAuthority(java.lang.String,boolean)} fails
   * @throws NullPointerException null authority
   */
  public void setRawAuthority(char[] escapedAuthority) throws URIException, NullPointerException {
    parseAuthority(new String(escapedAuthority), true);
    setURI();
  }

  /**
   * Set the authority.
   *
   * @param escapedAuthority the escaped authority string
   * @throws URIException If {@link #parseAuthority(java.lang.String,boolean)} fails
   */
  public void setEscapedAuthority(String escapedAuthority) throws URIException {
    parseAuthority(escapedAuthority, true);
    setURI();
  }

  /**
   * Get the raw-escaped authority.
   *
   * @return the raw-escaped authority
   */
  public char[] getRawAuthority() {
    return _authority;
  }

  /**
   * Get the escaped authority.
   *
   * @return the escaped authority
   */
  public String getEscapedAuthority() {
    return (_authority == null) ? null : new String(_authority);
  }

  /**
   * Get the authority.
   *
   * @return the authority
   * @throws URIException If {@link #decode} fails
   */
  public String getAuthority() throws URIException {
    return (_authority == null) ? null : decode(_authority, getProtocolCharset());
  }

  /**
   * Get the raw-escaped userinfo.
   *
   * @return the raw-escaped userinfo
   */
  public char[] getRawUserinfo() {
    return _userinfo;
  }

  /**
   * Get the escaped userinfo.
   *
   * @return the escaped userinfo
   */
  public String getEscapedUserinfo() {
    return (_userinfo == null) ? null : new String(_userinfo);
  }

  /**
   * Get the userinfo.
   *
   * @return the userinfo
   * @throws URIException If {@link #decode} fails
   */
  public String getUserinfo() throws URIException {
    return (_userinfo == null) ? null : decode(_userinfo, getProtocolCharset());
  }

  /**
   * Get the host = hostname | IPv4address | IPv6reference
   *
   * @return the host
   */
  public char[] getRawHost() {
    return _host;
  }

  /**
   * Get the host = hostname | IPv4address | IPv6reference
   *
   * @return the host
   * @throws URIException If {@link #decode} fails
   */
  public String getHost() throws URIException {
    if (_host != null) {
      return decode(_host, getProtocolCharset());
    } else {
      return null;
    }
  }

  /**
   * Get the host = hostname | IPv4address | IPv6reference
   *
   * @return the host
   * @throws URIException If {@link #decode} fails
   */
  public String getHost_notStrict() throws URIException {
    if (_host != null) {
      return new String(_host);
    } else {
      return null;
    }
  }

  /**
   * Get the port.
   *
   * @return the port
   */
  public int getPort() {
    return _port;
  }

  /**
   * Set the raw-escaped path.
   *
   * @param escapedPath the path character sequence
   * @throws URIException encoding error or not proper for initial instance
   */
  public void setRawPath(char[] escapedPath) throws URIException {
    if (escapedPath == null || escapedPath.length == 0) {
      _path = _opaque = escapedPath;
      setURI();
      return;
    }
    // remove the fragment identifier
    escapedPath = removeFragmentIdentifier(escapedPath);
    if (_is_net_path || _is_abs_path) {
      if (escapedPath[0] != '/') {
        throw new URIException(URIException.PARSING, "not absolute path");
      }
      if (!validate(escapedPath, abs_path)) {
        throw new URIException(URIException.ESCAPING, "escaped absolute path not valid");
      }
      _path = escapedPath;
    } else if (_is_rel_path) {
      int at = indexFirstOf(escapedPath, '/');
      if (at == 0) {
        throw new URIException(URIException.PARSING, "incorrect path");
      }
      if (at > 0 && !validate(escapedPath, 0, at - 1, rel_segment)
              && !validate(escapedPath, at, -1, abs_path)
              || at < 0 && !validate(escapedPath, 0, -1, rel_segment)) {
        throw new URIException(URIException.ESCAPING, "escaped relative path not valid");
      }
      _path = escapedPath;
    } else if (_is_opaque_part) {
      if (!uric_no_slash.get(escapedPath[0])
              && !validate(escapedPath, 1, -1, uric)) {
        throw new URIException(URIException.ESCAPING, "escaped opaque part not valid");
      }
      _opaque = escapedPath;
    } else {
      throw new URIException(URIException.PARSING, "incorrect path");
    }
    setURI();
  }

  /**
   * Set the escaped path.
   *
   * @param escapedPath the escaped path string
   * @throws URIException encoding error or not proper for initial instance
   */
  public void setEscapedPath(String escapedPath) throws URIException {
    if (escapedPath == null) {
      _path = _opaque = null;
      setURI();
      return;
    }
    setRawPath(escapedPath.toCharArray());
  }

  /**
   * Set the path.
   *
   * @param path the path string
   * @throws URIException set incorrectly or fragment only
   */
  public void setPath(String path) throws URIException {
    if (path == null || path.length() == 0) {
      _path = _opaque = (path == null) ? null : path.toCharArray();
      setURI();
      return;
    }
    // set the charset to do escape encoding
    String charset = getProtocolCharset();
    if (_is_net_path || _is_abs_path) {
      _path = encode(path, allowed_abs_path, charset);
    } else if (_is_rel_path) {
      StringBuilder buff = new StringBuilder(path.length());
      int at = path.indexOf('/');
      if (at == 0) { // never 0
        throw new URIException(URIException.PARSING, "incorrect relative path");
      }
      if (at > 0) {
        buff.append(encode(path.substring(0, at), allowed_rel_path, charset));
        buff.append(encode(path.substring(at), allowed_abs_path, charset));
      } else {
        buff.append(encode(path, allowed_rel_path, charset));
      }
      _path = buff.toString().toCharArray();
    } else if (_is_opaque_part) {
      StringBuilder buf = new StringBuilder();
      buf.insert(0, encode(path.substring(0, 1), uric_no_slash, charset));
      buf.insert(1, encode(path.substring(1), uric, charset));
      _opaque = buf.toString().toCharArray();
    } else {
      throw new URIException(URIException.PARSING, "incorrect path");
    }
    setURI();
  }

  /**
   * Resolve the base and relative path.
   *
   * @param basePath a character array of the basePath
   * @param relPath  a character array of the relPath
   * @return the resolved path
   * @throws URIException no more higher path level to be resolved
   */
  protected char[] resolvePath(char[] basePath, char[] relPath) throws URIException {
    // paths are never null
    String base = (basePath == null) ? "" : new String(basePath);
    int at = base.lastIndexOf('/');
    if (at != -1) {
      basePath = base.substring(0, at + 1).toCharArray();
    }
    // _path could be empty
    if (relPath == null || relPath.length == 0) {
      return normalize(basePath);
    } else if (relPath[0] == '/') {
      return normalize(relPath);
    } else {
      StringBuilder buff = new StringBuilder(base.length() + relPath.length);
      buff.append((at != -1) ? base.substring(0, at + 1) : "/");
      buff.append(relPath);
      return normalize(buff.toString().toCharArray());
    }
  }

  /**
   * Get the raw-escaped current hierarchy level in the specified path.
   *
   * @param path the path
   * @return the current hierarchy level
   * @throws URIException no hierarchy level
   */
  protected char[] getRawCurrentHierPath(char[] path) throws URIException {
    if (_is_opaque_part) {
      throw new URIException(URIException.PARSING, "no hierarchy level");
    }
    if (path == null) {
      throw new URIException(URIException.PARSING, "empty path");
    }
    String buff = new String(path);
    int first = buff.indexOf('/');
    int last = buff.lastIndexOf('/');
    if (last == 0) {
      return rootPath;
    } else if (first != last && last != -1) {
      return buff.substring(0, last).toCharArray();
    }
    // could be a document on the server side
    return path;
  }

  /**
   * Get the raw-escaped current hierarchy level.
   *
   * @return the raw-escaped current hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public char[] getRawCurrentHierPath() throws URIException {
    return (_path == null) ? null : getRawCurrentHierPath(_path);
  }

  /**
   * Get the escaped current hierarchy level.
   *
   * @return the escaped current hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public String getEscapedCurrentHierPath() throws URIException {
    char[] path = getRawCurrentHierPath();
    return (path == null) ? null : new String(path);
  }

  /**
   * Get the current hierarchy level.
   *
   * @return the current hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public String getCurrentHierPath() throws URIException {
    char[] path = getRawCurrentHierPath();
    return (path == null) ? null : decode(path, getProtocolCharset());
  }

  /**
   * Get the level above the this hierarchy level.
   *
   * @return the raw above hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public char[] getRawAboveHierPath() throws URIException {
    char[] path = getRawCurrentHierPath();
    return (path == null) ? null : getRawCurrentHierPath(path);
  }

  /**
   * Get the level above the this hierarchy level.
   *
   * @return the raw above hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public String getEscapedAboveHierPath() throws URIException {
    char[] path = getRawAboveHierPath();
    return (path == null) ? null : new String(path);
  }

  /**
   * Get the level above the this hierarchy level.
   *
   * @return the above hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public String getAboveHierPath() throws URIException {
    char[] path = getRawAboveHierPath();
    return (path == null) ? null : decode(path, getProtocolCharset());
  }

  /**
   * Get the raw-escaped path = [ abs_path | opaque_part ]
   *
   * @return the raw-escaped path
   */
  public char[] getRawPath() {
    return _is_opaque_part ? _opaque : _path;
  }

  /**
   * Get the escaped path = [ abs_path | opaque_part ]
   *   abs_path      = "/"  path_segments
   *   opaque_part   = uric_no_slash *uric
   *
   * @return the escaped path string
   */
  public String getEscapedPath() {
    char[] path = getRawPath();
    return (path == null) ? null : new String(path);
  }

  /**
   * Get the path = [ abs_path | opaque_part ]
   *
   * @return the path string
   * @throws URIException If {@link #decode} fails.
   */
  public String getPath() throws URIException {
    char[] path = getRawPath();
    return (path == null) ? null : decode(path, getProtocolCharset());
  }

  /**
   * Get the raw-escaped basename of the path.
   *
   * @return the raw-escaped basename
   */
  public char[] getRawName() {
    if (_path == null) {
      return null;
    }
    int at = 0;
    for (int i = _path.length - 1; i >= 0; i--) {
      if (_path[i] == '/') {
        at = i + 1;
        break;
      }
    }
    int len = _path.length - at;
    char[] basename = new char[len];
    System.arraycopy(_path, at, basename, 0, len);
    return basename;
  }

  /**
   * Get the escaped basename of the path.
   *
   * @return the escaped basename string
   */
  public String getEscapedName() {
    char[] basename = getRawName();
    return (basename == null) ? null : new String(basename);
  }

  /**
   * Get the basename of the path.
   *
   * @return the basename string
   * @throws URIException incomplete trailing escape pattern or unsupported character encoding
   */
  public String getName() throws URIException {
    char[] basename = getRawName();
    return (basename == null) ? null : decode(getRawName(), getProtocolCharset());
  }

  /**
   * Get the raw-escaped path and query.
   *
   * @return the raw-escaped path and query
   */
  public char[] getRawPathQuery() {
    if (_path == null && _query == null) {
      return null;
    }
    StringBuilder buff = new StringBuilder();
    if (_path != null) {
      buff.append(_path);
    }
    if (_query != null) {
      buff.append('?');
      buff.append(_query);
    }
    return buff.toString().toCharArray();
  }

  /**
   * Get the escaped query.
   *
   * @return the escaped path and query string
   */
  public String getEscapedPathQuery() {
    char[] rawPathQuery = getRawPathQuery();
    return (rawPathQuery == null) ? null : new String(rawPathQuery);
  }

  /**
   * Get the path and query.
   *
   * @return the path and query string.
   * @throws URIException incomplete trailing escape pattern or unsupported character encoding
   */
  public String getPathQuery() throws URIException {
    char[] rawPathQuery = getRawPathQuery();
    return (rawPathQuery == null) ? null : decode(rawPathQuery, getProtocolCharset());
  }

  /**
   * Set the raw-escaped query.
   *
   * @param escapedQuery the raw-escaped query
   * @throws URIException escaped query not valid
   */
  public void setRawQuery(char[] escapedQuery) throws URIException {
    if (escapedQuery == null || escapedQuery.length == 0) {
      _query = escapedQuery;
      setURI();
      return;
    }
    // remove the fragment identifier
    escapedQuery = removeFragmentIdentifier(escapedQuery);
    if (!validate(escapedQuery, query)) {
      throw new URIException(URIException.ESCAPING, "escaped query not valid");
    }
    _query = escapedQuery;
    setURI();
  }

  /**
   * Set the escaped query string.
   *
   * @param escapedQuery the escaped query string
   * @throws URIException escaped query not valid
   */
  public void setEscapedQuery(String escapedQuery) throws URIException {
    if (escapedQuery == null) {
      _query = null;
      setURI();
      return;
    }
    setRawQuery(escapedQuery.toCharArray());
  }

  /**
   * Set the query.
   *
   * @param query the query string.
   * @throws URIException incomplete trailing escape pattern or unsupported character encoding
   */
  public void setQuery(String query) throws URIException {
    if (query == null || query.length() == 0) {
      _query = (query == null) ? null : query.toCharArray();
      setURI();
      return;
    }
    setRawQuery(encode(query, allowed_query, getProtocolCharset()));
  }

  /**
   * Get the raw-escaped query.
   *
   * @return the raw-escaped query
   */
  public char[] getRawQuery() {
    return _query;
  }

  /**
   * Get the escaped query.
   *
   * @return the escaped query string
   */
  public String getEscapedQuery() {
    return (_query == null) ? null : new String(_query);
  }

  /**
   * Get the query.
   *
   * @return the query string.
   * @throws URIException incomplete trailing escape pattern or unsupported character encoding
   */
  public String getQuery() throws URIException {
    return (_query == null) ? null : decode(_query, getProtocolCharset());
  }

  /**
   * Set the raw-escaped fragment.
   *
   * @param escapedFragment the raw-escaped fragment
   * @throws URIException escaped fragment not valid
   */
  public void setRawFragment(char[] escapedFragment) throws URIException {
    if (escapedFragment == null || escapedFragment.length == 0) {
      _fragment = escapedFragment;
      hash = 0;
      return;
    }
    if (!validate(escapedFragment, fragment)) {
      throw new URIException(URIException.ESCAPING, "escaped fragment not valid");
    }
    _fragment = escapedFragment;
    hash = 0;
  }

  /**
   * Set the escaped fragment string.
   *
   * @param escapedFragment the escaped fragment string
   * @throws URIException escaped fragment not valid
   */
  public void setEscapedFragment(String escapedFragment) throws URIException {
    if (escapedFragment == null) {
      _fragment = null;
      hash = 0;
      return;
    }
    setRawFragment(escapedFragment.toCharArray());
  }

  /**
   * Set the fragment.
   *
   * @param fragment the fragment string.
   * @throws URIException If an error occurs.
   */
  public void setFragment(String fragment) throws URIException {
    if (fragment == null || fragment.length() == 0) {
      _fragment = (fragment == null) ? null : fragment.toCharArray();
      hash = 0;
      return;
    }
    _fragment = encode(fragment, allowed_fragment, getProtocolCharset());
    hash = 0;
  }

  /**
   * Get the raw-escaped fragment.
   *
   * @return the raw-escaped fragment
   */
  public char[] getRawFragment() {
    return _fragment;
  }

  /**
   * Get the escaped fragment.
   *
   * @return the escaped fragment string
   */
  public String getEscapedFragment() {
    return (_fragment == null) ? null : new String(_fragment);
  }

  /**
   * Get the fragment.
   *
   * @return the fragment string
   * @throws URIException incomplete trailing escape pattern or unsupported character encoding
   */
  public String getFragment() throws URIException {
    return (_fragment == null) ? null : decode(_fragment, getProtocolCharset());
  }

  /**
   * Remove the fragment identifier of the specified component.
   *
   * @param component the component that a fragment may be included
   * @return the component that the fragment identifier is removed
   */
  protected char[] removeFragmentIdentifier(char[] component) {
    if (component == null) {
      return null;
    }
    int lastIndex = new String(component).indexOf('#');
    if (lastIndex != -1) {
      component = new String(component).substring(0, lastIndex).toCharArray();
    }
    return component;
  }

  /**
   * Normalize the specified hier path part.
   *
   * @param path the path to normalize
   * @return the normalized path
   * @throws URIException no more higher path level to be normalized
   */
  protected char[] normalize(char[] path) throws URIException {
    if (path == null) {
      return null;
    }
    String normalized = new String(path);
    // If the buffer begins with "./" or "../", the "." or ".." is removed.
    if (normalized.startsWith("./")) {
      normalized = normalized.substring(1);
    } else if (normalized.startsWith("../")) {
      normalized = normalized.substring(2);
    } else if (normalized.startsWith("..")) {
      normalized = normalized.substring(2);
    }
    // All occurrences of "/./" in the buffer are replaced with "/"
    int index;
    while ((index = normalized.indexOf("/./")) != -1) {
      normalized = normalized.substring(0, index) + normalized.substring(index + 2);
    }
    // If the buffer ends with "/.", the "." is removed.
    if (normalized.endsWith("/.")) {
      normalized = normalized.substring(0, normalized.length() - 1);
    }
    int startIndex = 0;
    // All occurrences of "/<segment>/../" in the buffer, where ".."
    // and <segment> are complete path segments, are iteratively replaced
    // with "/" in order from left to right until no matching pattern remains.
    // If the buffer ends with "/<segment>/..", that is also replaced
    // with "/".  Note that <segment> may be empty.
    while ((index = normalized.indexOf("/../", startIndex)) != -1) {
      int slashIndex = normalized.lastIndexOf('/', index - 1);
      if (slashIndex >= 0) {
        normalized = normalized.substring(0, slashIndex) + normalized.substring(index + 3);
      } else {
        startIndex = index + 3;
      }
    }
    if (normalized.endsWith("/..")) {
      int slashIndex = normalized.lastIndexOf('/', normalized.length() - 4);
      if (slashIndex >= 0) {
        normalized = normalized.substring(0, slashIndex + 1);
      }
    }
    // All prefixes of "<segment>/../" in the buffer, where ".."
    // and <segment> are complete path segments, are iteratively replaced
    // with "/" in order from left to right until no matching pattern remains.
    // If the buffer ends with "<segment>/..", that is also replaced
    // with "/".  Note that <segment> may be empty.
    while ((index = normalized.indexOf("/../")) != -1) {
      int slashIndex = normalized.lastIndexOf('/', index - 1);
      if (slashIndex >= 0) {
        break;
      } else {
        normalized = normalized.substring(index + 3);
      }
    }
    if (normalized.endsWith("/..")) {
      int slashIndex = normalized.lastIndexOf('/', normalized.length() - 4);
      if (slashIndex < 0) {
        normalized = "/";
      }
    }
    return normalized.toCharArray();
  }

  /**
   * Normalizes the path part of this URI.
   *
   * @throws URIException no more higher path level to be normalized
   */
  public void normalize() throws URIException {
    if (isAbsPath()) {
      _path = normalize(_path);
      setURI();
    }
  }

  /**
   * Test if the first array is equal to the second array.
   *
   * @param first  the first character array
   * @param second the second character array
   * @return true if they're equal
   */
  protected boolean equals(char[] first, char[] second) {
    if (first == null && second == null) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    if (first.length != second.length) {
      return false;
    }
    for (int i = 0; i < first.length; i++) {
      if (first[i] != second[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Test an object if this URI is equal to another.
   *
   * @param obj an object to compare
   * @return true if two URI objects are equal
   */
  public boolean equals(Object obj) {
    // normalize and test each components
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof URI)) {
      return false;
    }
    URI another = (URI) obj;
    // scheme
    if (!equals(_scheme, another._scheme)) {
      return false;
    }
    // is_opaque_part or is_hier_part?  and opaque
    if (!equals(_opaque, another._opaque)) {
      return false;
    }
    // is_hier_part
    // has_authority
    if (!equals(_authority, another._authority)) {
      return false;
    }
    // path
    if (!equals(_path, another._path)) {
      return false;
    }
    // has_query
    if (!equals(_query, another._query)) {
      return false;
    }
    // has_fragment?  should be careful of the only fragment case.
		return equals(_fragment, another._fragment);
	}

  /**
   * Write the content of this URI.
   *
   * @param oos the object-outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }

  /**
   * Read a URI.
   *
   * @param ois the object-incoming stream
   * @throws ClassNotFoundException If one of the classes specified in the incoming stream cannot be found.
   * @throws IOException            If an IO problem occurs.
   */
  protected void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }

  /**
   * Return a hash code for this URI.
   *
   * @return a has code value for this URI
   */
  public int hashCode() {
    if (hash == 0) {
      char[] c = _scheme;
      if (c != null) {
        for (int i = 0, len = c.length; i < len; i++) {
          hash = 31 * hash + c[i];
        }
      }
      c = _authority;
      if (c != null) {
        for (int i = 0, len = c.length; i < len; i++) {
          hash = 31 * hash + c[i];
        }
      }
      c = _path;
      if (c != null) {
        for (int i = 0, len = c.length; i < len; i++) {
          hash = 31 * hash + c[i];
        }
      }

      c = _fragment;
      if (c != null) {
        for (int i = 0, len = c.length; i < len; i++) {
          hash = 31 * hash + c[i];
        }
      }
    }
    return hash;
  }

  /**
   * Compare this URI to another object.
   *
   * @param obj the object to be compared.
   * @return 0, if it's same,
   *         -1, if failed, first being compared with in the authority component
   * @throws ClassCastException not URI argument
   */
  public int compareTo(Object obj) throws ClassCastException {
    URI another = (URI) obj;
    if (!equals(_authority, another.getRawAuthority())) {
      return -1;
    }
    return toString().compareTo(another.toString());
  }

  /**
   * Create and return a copy of this object
   *
   * @return a clone of this instance
   */
	@SuppressWarnings({"CloneDoesntCallSuperClone"})
	public synchronized Object clone() throws CloneNotSupportedException {
    URI instance = new URI();
    instance._uri = _uri;
    instance._scheme = _scheme;
    instance._opaque = _opaque;
    instance._authority = _authority;
    instance._userinfo = _userinfo;
    instance._host = _host;
    instance._port = _port;
    instance._path = _path;
    instance._query = _query;
    instance._fragment = _fragment;
    // the charset to do escape encoding for this instance
    instance.protocolCharset = protocolCharset;
    // flags
    instance._is_hier_part = _is_hier_part;
    instance._is_opaque_part = _is_opaque_part;
    instance._is_net_path = _is_net_path;
    instance._is_abs_path = _is_abs_path;
    instance._is_rel_path = _is_rel_path;
    instance._is_reg_name = _is_reg_name;
    instance._is_server = _is_server;
    instance._is_hostname = _is_hostname;
    instance._is_IPv4address = _is_IPv4address;
    instance._is_IPv6reference = _is_IPv6reference;
    return instance;
  }

  /**
   * Gets raw uri as char[]
   *
   * @return the URI character sequence
   */
  public char[] getRawURI() {
    return _uri;
  }

  /**
   * Gets the escaped uri
   *
   * @return the escaped URI string
   */
  public String getEscapedURI() {
    return (_uri == null) ? null : new String(_uri);
  }

  /**
   * It can be gotten the URI character sequence.
   *
   * @return the original URI string
   * @throws URIException incomplete trailing escape pattern or unsupported character encoding
   */
  public String getURI() throws URIException {
    return (_uri == null) ? null : decode(_uri, getProtocolCharset());
  }

  /**
   * Get the URI reference character sequence.
   *
   * @return the URI reference character sequence
   */
  public char[] getRawURIReference() {
    if (_fragment == null) {
      return _uri;
    }
    if (_uri == null) {
      return _fragment;
    }
    // if _uri != null &&  _fragment != null
    String uriReference = new String(_uri) + "#" + new String(_fragment);
    return uriReference.toCharArray();
  }

  /**
   * Get the original URI reference string.
   *
   * @return the original URI reference string
   * @throws URIException If {@link #decode} fails.
   */
  public String getURIReference() throws URIException {
    char[] uriReference = getRawURIReference();
    return (uriReference == null) ? null : decode(uriReference, getProtocolCharset());
  }

  /**
   * Get the escaped URI string.
   *
   * @return the escaped URI string
   */
  public String toString() {
    return getEscapedURI();
  }

  /**
   * The charset-changed normal operation to represent to be required to
   * alert to user the fact the default charset is changed.
   */
  public static class DefaultCharsetChanged extends RuntimeException {

    /**
     * The constructor with a reason string and its code arguments.
     *
     * @param reasonCode the reason code
     * @param reason     the reason
     */
    public DefaultCharsetChanged(int reasonCode, String reason) {
      super(reason);
      this.reasonCode = reasonCode;
    }

    /**
     * No specified reason code.
     */
    public static final int UNKNOWN = 0;

    /**
     * Protocol charset changed.
     */
    public static final int PROTOCOL_CHARSET = 1;

    /**
     * Document charset changed.
     */
    public static final int DOCUMENT_CHARSET = 2;

    /**
     * The reason code.
     */
    private int reasonCode;

    /**
     * Get the reason code.
     *
     * @return the reason code
     */
    public int getReasonCode() {
      return reasonCode;
    }

  }

  /**
   * A mapping to determine the (somewhat arbitrarily) preferred charset for a
   * specified locale.  Supports all locales recognized in JDK 1.1.
   */
  public static class LocaleToCharsetMap {

    /**
     * A mapping of language code to charset
     */
    private static final Hashtable<String, String> LOCALE_TO_CHARSET_MAP;

    static {
      LOCALE_TO_CHARSET_MAP = new Hashtable<String, String>();
      LOCALE_TO_CHARSET_MAP.put("ar", "ISO-8859-6");
      LOCALE_TO_CHARSET_MAP.put("be", "ISO-8859-5");
      LOCALE_TO_CHARSET_MAP.put("bg", "ISO-8859-5");
      LOCALE_TO_CHARSET_MAP.put("ca", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("cs", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("da", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("de", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("el", "ISO-8859-7");
      LOCALE_TO_CHARSET_MAP.put("en", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("es", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("et", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("fi", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("fr", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("hr", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("hu", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("is", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("it", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("iw", "ISO-8859-8");
      LOCALE_TO_CHARSET_MAP.put("ja", "Shift_JIS");
      LOCALE_TO_CHARSET_MAP.put("ko", "EUC-KR");
      LOCALE_TO_CHARSET_MAP.put("lt", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("lv", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("mk", "ISO-8859-5");
      LOCALE_TO_CHARSET_MAP.put("nl", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("no", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("pl", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("pt", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("ro", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("ru", "ISO-8859-5");
      LOCALE_TO_CHARSET_MAP.put("sh", "ISO-8859-5");
      LOCALE_TO_CHARSET_MAP.put("sk", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("sl", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("sq", "ISO-8859-2");
      LOCALE_TO_CHARSET_MAP.put("sr", "ISO-8859-5");
      LOCALE_TO_CHARSET_MAP.put("sv", "ISO-8859-1");
      LOCALE_TO_CHARSET_MAP.put("tr", "ISO-8859-9");
      LOCALE_TO_CHARSET_MAP.put("uk", "ISO-8859-5");
      LOCALE_TO_CHARSET_MAP.put("zh", "GB2312");
      LOCALE_TO_CHARSET_MAP.put("zh_TW", "Big5");
    }

    /**
     * Get the preferred charset for the specified locale.
     *
     * @param locale the locale
     * @return the preferred charset or null if the locale is not
     *         recognized.
     */
    public static String getCharset(Locale locale) {
      // try for an full name match (may include country)
      String charset = LOCALE_TO_CHARSET_MAP.get(locale.toString());
      if (charset != null) {
        return charset;
      }
      // if a full name didn't match, try just the language
      charset = LOCALE_TO_CHARSET_MAP.get(locale.getLanguage());
      return charset;  // may be null
    }

  }


  public static void main(String[] args) {
    new URI();
  }

}