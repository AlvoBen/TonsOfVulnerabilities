/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.lib;

import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.CharArrayUtils;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.server.ServiceContext;

public class CookieParser {
  public static final String jsessionid_cookie = "JSESSIONID";
  public static final byte[] jsessionid_cookie_ = jsessionid_cookie.getBytes();
  public static final String jsessionid_url = "jsessionid";
  public static final byte[] jsessionid_url_ = jsessionid_url.getBytes();
  public static final String jsessionid_url_sep = ";jsessionid=";
  public static final byte[] jsessionid_url_sep_ = jsessionid_url_sep.getBytes();
  public static String app_cookie_prefix = "saplb_";
  public static byte[] app_cookie_prefix_ = app_cookie_prefix.getBytes();
  public static String app_cookie_prefix_old = "sapj2ee_";
  public static byte[] app_cookie_prefix_old_ = app_cookie_prefix_old.getBytes();
  public static final String jsession_mark_cookie = "JSESSIONMARKID";
  public static final byte[] jsession_mark_cookie_ = jsession_mark_cookie.getBytes();

  private static final byte attribute_prefix = '$';
  private static final byte[] comment = "Comment".getBytes();
  private static final byte[] discard = "Discard".getBytes();
  private static final byte[] domain = "Domain".getBytes();
  private static final byte[] expires = "Expires".getBytes();
  private static final byte[] max_age = "Max-Age".getBytes();
  private static final byte[] path = "Path".getBytes();
  private static final byte[] secure = "Secure".getBytes();
  private static final byte[] version = "Version".getBytes();

  private static final byte cookie_separator_old = ';';
  private static final byte cookie_separator_new = ',';

  /**
   * Parses cookies from the request. The cretaed cookie instances are set into the Cookies object passed as parameter.
   *
   * RFC 2109:
   * cookie          =       "Cookie:" cookie-version
                           1*((";" | ",") cookie-value)
   * cookie-value    =       NAME "=" VALUE [";" path] [";" domain]
   * cookie-version  =       "$Version" "=" value
   * NAME            =       attr
   * VALUE           =       value
   * path            =       "$Path" "=" value
   * domain          =       "$Domain" "=" value
   *
   */
  public static void parseCookies(MimeHeaders headers, boolean urlSessionTracking, Cookies cookies) {
    String[] cookie = headers.getHeaders(HeaderNames.request_header_cookie);
    if (cookie == null) {
      return;
    }
    for (int i = 0; i < cookie.length; i++) {
      if (cookie[i] == null || cookie[i].length() == 0) {
        continue;
      }
      int cookieHeaderVersion = 0;
      byte[] cookieHeader = Ascii.getBytes(cookie[i]);
      int off = 0;
      int len = 0;
      int j = 0;
      while (cookieHeader[j] == ' ') {
        j++;
      }
      if (cookieHeader[j] == attribute_prefix) {
        if (ByteArrayUtils.startsWith(cookieHeader, j + 1, cookieHeader.length - j - 1, version)) {
          if (cookieHeader.length > j + 1 + version.length && cookieHeader[j + 1 + version.length] == '=') {
            j += version.length + 2;
            int versionOffset = j;
            while (j < cookieHeader.length) {
              if (cookieHeader[j] == cookie_separator_old || cookieHeader[j] == cookie_separator_new) {
                if (cookieHeader[versionOffset] == '\"' && cookieHeader[j - 1] == '\"') {
                  cookieHeaderVersion = Ascii.asciiArrToIntNoException(cookieHeader, versionOffset + 1, j - versionOffset - 2);
                } else {
                  cookieHeaderVersion = Ascii.asciiArrToIntNoException(cookieHeader, versionOffset, j - versionOffset);
                }
                break;
              }
              j++;
            }
          }
        }
      }
      for (; j <= cookieHeader.length; j++) {
        if (j == cookieHeader.length || cookieHeader[j] == cookie_separator_new || cookieHeader[j] == cookie_separator_old) {
          int valueOff = ByteArrayUtils.indexOf(cookieHeader, off, len, (byte)'=') + 1;
          if (valueOff != 0) {
            boolean quotes = false;
            if (len - valueOff > 0 && cookieHeader[off + valueOff] == '\"' && cookieHeader[off + len - 1] == '\"' && (len - valueOff) > 1) {
              quotes = true;
            }
            HttpCookie newCookie = null;
            if (!isReserved(cookieHeader, off, valueOff - 1)) {
              if (quotes) {
                newCookie = new HttpCookie(new String(cookieHeader, off, valueOff - 1), new String(cookieHeader, off + valueOff + 1, len - valueOff - 2));
              } else {
                newCookie = new HttpCookie(new String(cookieHeader, off, valueOff - 1), new String(cookieHeader, off + valueOff, len - valueOff));
              }
              j++;
              while (j < cookieHeader.length && cookieHeader[j] == ' ') {
                j++;
              }
              if (j < cookieHeader.length && cookieHeader[j] == attribute_prefix) {
                if (ByteArrayUtils.startsWith(cookieHeader, j + 1, cookieHeader.length - j - 1, path)) {
                  if (cookieHeader.length > j + 1 + path.length && cookieHeader[j + 1 + path.length] == '=') {
                    j += path.length + 2;
                  }
                  int pathOffset = j;
                  while (j < cookieHeader.length) {
                    if (cookieHeader[j] == cookie_separator_old || cookieHeader[j] == cookie_separator_new) {
                      break;
                    }
                    j++;
                  }
                  if (j > 2 && cookieHeader[pathOffset] == '\"' && cookieHeader[j - 1] == '\"') {
										newCookie.setPath(new String(cookieHeader, pathOffset + 1, j - pathOffset - 2));
                  } else {
										newCookie.setPath(new String(cookieHeader, pathOffset, j - pathOffset));
                  }
                  j++;
                } else if (ByteArrayUtils.startsWith(cookieHeader, j + 1, cookieHeader.length - j - 1, domain)) {
									if (cookieHeader.length > j + 1 + domain.length && cookieHeader[j + 1 + domain.length] == '=') {
										j += domain.length + 2;
									}
									int domainOffset = j;
									while (j < cookieHeader.length) {
										if (cookieHeader[j] == cookie_separator_old || cookieHeader[j] == cookie_separator_new) {
											break;
										}
										j++;
									}
									if (j > 2 && cookieHeader[domainOffset] == '\"' && cookieHeader[j - 1] == '\"') {
										newCookie.setDomain(new String(cookieHeader, domainOffset + 1, j - domainOffset - 2));
									} else {
										newCookie.setDomain(new String(cookieHeader, domainOffset, j - domainOffset));
									}
                  j++;
								}
              }
              if (j < cookieHeader.length && cookieHeader[j - 1] == cookie_separator_old) {
                while (j < cookieHeader.length && cookieHeader[j] == ' ') {
                  j++;
                }
              }
              if (j < cookieHeader.length && cookieHeader[j] == attribute_prefix) {
                if (ByteArrayUtils.startsWith(cookieHeader, j + 1, cookieHeader.length - j - 1, domain)) {
                  if (cookieHeader.length > j + 1 + domain.length && cookieHeader[j + 1 + domain.length] == '=') {
                    j += domain.length + 2;
                  }
                  int domainOffset = j;
                  while (j < cookieHeader.length) {
                    if (cookieHeader[j] == cookie_separator_old || cookieHeader[j] == cookie_separator_new) {
                      break;
                    }
                    j++;
                  }
									if (j > 2 && cookieHeader[domainOffset] == '\"' && cookieHeader[j - 1] == '\"') {
										newCookie.setDomain(new String(cookieHeader, domainOffset + 1, j - domainOffset - 2));
									} else {
										newCookie.setDomain(new String(cookieHeader, domainOffset, j - domainOffset));
									}
                } else if (ByteArrayUtils.startsWith(cookieHeader, j + 1, cookieHeader.length - j - 1, path)) {
                  if (cookieHeader.length > j + 1 + path.length && cookieHeader[j + 1 + path.length] == '=') {
                    j += path.length + 2;
                  }
                  int pathOffset = j;
                  while (j < cookieHeader.length) {
                    if (cookieHeader[j] == cookie_separator_old || cookieHeader[j] == cookie_separator_new) {
                      break;
                    }
                    j++;
                  }
                  if (j > 2 && cookieHeader[pathOffset] == '\"' && cookieHeader[j - 1] == '\"') {
                    newCookie.setPath(new String(cookieHeader, pathOffset + 1, j - pathOffset - 2));
                  } else {
                    newCookie.setPath(new String(cookieHeader, pathOffset, j - pathOffset));
                  }
                }
              }
              j--;
              newCookie.setVersion(cookieHeaderVersion);
              cookies.getCookies().add(newCookie);
            }
            if (cookies.getSessionCookie().getName() == null && !urlSessionTracking && ByteArrayUtils.equalsBytes(cookieHeader, off, valueOff - 1, jsessionid_cookie_)) {
              if (quotes) {
                cookies.getSessionCookie().setName(newCookie.getName());
                cookies.getSessionCookie().setValue(newCookie.getValue());
              } else {
                cookies.getSessionCookie().setName(newCookie.getName());
                cookies.getSessionCookie().setValue(newCookie.getValue());
              }
            }
            if (ByteArrayUtils.startsWithIgnoreCase(cookieHeader, off, valueOff - 1, app_cookie_prefix_)) {
              cookies.getApplicationCookies().add(newCookie);
            }
          }
          off = j + 1;
          len = 0;
        } else if (cookieHeader[j] == ' ' && len == 0) {
          off++;
        } else {
          len++;
        }
      }
    }
  }

  public static void parseCookiesFromURL(MessageBytes fullRequestURI, Cookies cookies) {
    int begInd = fullRequestURI.indexOf(jsessionid_url_sep_);
    if (begInd == -1) {
      return;
    }
    begInd += 12;
    int endInd = fullRequestURI.indexOf('?');
    if (endInd == -1) {
      endInd = fullRequestURI.length();
    } else if (endInd < begInd) {
      return;
    }
    int nextCookieInd = fullRequestURI.indexOf(';', begInd);
    if (nextCookieInd > 0 && nextCookieInd < endInd) {
      endInd = nextCookieInd;
    }

    if (begInd >= fullRequestURI.length() || endInd - 1 >= fullRequestURI.length()) {
        return;
    }

    if (fullRequestURI.charAt(begInd) == '\"' && fullRequestURI.charAt(endInd - 1) == '\"') {
      begInd++;
      endInd--;
    }
    if (fullRequestURI.length() == begInd) {
      return;
    }
    byte[] value = fullRequestURI.getBytes(begInd, endInd - begInd);
    if (cookies.getSessionCookie().getName() == null) {
      cookies.getSessionCookie().setName(jsessionid_url);
      cookies.getSessionCookie().setValue(new String(value));
    }
    endInd++;
    if (endInd < fullRequestURI.length()) {
      parseApplicationCookies(fullRequestURI.getBytes(endInd), cookies);
    }
  }

  public static HttpCookie createSessionCookie(String val, String domainName, WebCookieConfig cookieConfig) {
    return createCookie(jsessionid_cookie, val, domainName, cookieConfig);
  }

  public static HttpCookie createMarkIDCookie(String val, String domainName, WebCookieConfig cookieConfig) {
      //return createCookie(jsession_mark_cookie, val, domainName, cookieConfig);
	  HttpCookie cok = null;
	  cok = new HttpCookie(jsession_mark_cookie, val);
	  cok.setMaxAge(cookieConfig.getMaxAge());
	  cok.setVersion(1);
	  setCookieMarkIdDomain(cok, domainName, cookieConfig);
	  setCookiePath(cok, cookieConfig);
	  return cok;
	  
  }


  public static HttpCookie createCookie(String name, String val, String hostName, WebCookieConfig cookieConfig) {
    HttpCookie cok = null;
    byte[] nameBytes = Ascii.getBytes(name);
    if (isReserved(nameBytes, 0, nameBytes.length)) {
      return null;
    }
    cok = new HttpCookie(name, val);
    cok.setMaxAge(cookieConfig.getMaxAge());
    cok.setVersion(1);
    setCookieDomain(cok, hostName, cookieConfig);
    setCookiePath(cok, cookieConfig);
    return cok;
  }

  // ------------------------ PRIVATE ------------------------
  
  
  /**
   * If session cookie's domain is configured to:
   * 			NONE - no domain will be set to the MARKID cookie
   * 			SERVER/Custom string - the value of the servlet_jsp service property JSessionMarkIdDomain will be set.
   * The possible values of this property for now are:
   * 			sessionCookie - the domain configuration of the sessionCookie is copied to the MarkId cookie 
   * 			NONE - the domain of the MarkID cookie is not set
   *   
   */
  private static void setCookieMarkIdDomain(HttpCookie cookie,  String hostName, WebCookieConfig cookieConfig ){
		if (cookieConfig==null || cookieConfig.getCookieType() == WebCookieConfig.COOKIE_TYPE_APPLICATION) {
			//the given cookie configuration is not correct
	        return;
		}
		if (cookieConfig.getDomainType() == WebCookieConfig.NONE) {
			return;
		}else{
			if (ServiceContext.getServiceContext().getHttpProvider().getWebContainer() != null) {
				String custom_domain = ServiceContext.getServiceContext().getHttpProvider().getWebContainer().getSecurtiySessionIdDomain();
				if (custom_domain != null && custom_domain.equalsIgnoreCase("NONE")){
					//no domain is set
					return;
				}//in all other cases apply the default behavior - copy the session cookie's configuration
				if (cookieConfig.getDomainType() == WebCookieConfig.OTHER){
					//copy the custom value directly
					cookie.setDomain(cookieConfig.getDomain());
					return;
				}
				if (cookieConfig.getDomainType() == WebCookieConfig.SERVER){
					//calculate the domain of the given host and put is as domain attribute of the markId
					setDomainFromHost(cookie, hostName);
				}
			}
		}
  }


/**
 * Extracts the correct domain from the given host name and sets it to the given cookie.
 * Does not set anything if the given host name is not correct or null. 
 */
private static void setDomainFromHost(HttpCookie cookie,  String hostName){
      if (hostName == null) {
        return;
      }
      boolean isIP = true;
      int points = 0;
      boolean domainHasPort = false;
      char[] domain = hostName.toCharArray();
      int ind = CharArrayUtils.indexOf(domain, ':');
      if (ind > 0) {
        domainHasPort = true;
        char[] tmpDomain = new char[ind];
        System.arraycopy(domain, 0, tmpDomain, 0, ind);
        domain = tmpDomain;
      }
     // check given host - ip and number of dots in it
      for (int i = 0; i < domain.length; i++) {
        if (domain[i] == '.') {
          points++;
        } else if (!Character.isDigit(domain[i])) {
          isIP = false;
          break;
        }
      }
      if (isIP && points == 3) {
        if (domainHasPort) {
          cookie.setDomain(new String(domain));
        } else {
          cookie.setDomain(hostName);
        }
      } else {
        int dotInd = CharArrayUtils.indexOf(domain, '.');
        if (dotInd >= 0 && CharArrayUtils.indexOf(domain, '.', dotInd) >= 0) {
          cookie.setDomain(new String(domain, dotInd, domain.length - dotInd));
        }
      }
}
  private static void parseApplicationCookies(byte[] url, Cookies cookies) {
    int nameOff = 0;
    int valueOff = -1;
    int nameLen = -1;
    int off = 0;
    while (off < url.length) {
      if (url[off] == ';') {
        if (nameOff > -1 && nameLen > -1 && valueOff > -1) {
          if (isApplicationCookie(url, valueOff, off - valueOff)) {
            if (!isReserved(url, nameOff, nameLen)) {
              String key = new String(url, nameOff, nameLen);
              String value = new String(url, valueOff, off - valueOff);
              cookies.getApplicationCookies().add(new HttpCookie(key, value));
            }
          }
        }
        nameOff = off + 1;
        nameLen = -1;
        valueOff = -1;
      } else if (url[off] == ' ') {
        if (nameOff > -1 && nameLen > -1 && valueOff > -1) {
          if (isApplicationCookie(url, valueOff, off - valueOff)) {
            if (!isReserved(url, nameOff, nameLen)) {
              String key = new String(url, nameOff, nameLen);
              String value = new String(url, valueOff, off - valueOff);
              cookies.getApplicationCookies().add(new HttpCookie(key, value));
            }
          }
        }
        break;
      } else if (url[off] == '=') {
        nameLen = off - nameOff;
        valueOff = off + 1;
      }
      off++;
    }
    if (nameOff > -1 && nameLen > -1 && valueOff > -1) {
      if (isApplicationCookie(url, valueOff, off - valueOff)) {
        if (!isReserved(url, nameOff, nameLen)) {
          String key = new String(url, nameOff, nameLen);
          String value = new String(url, valueOff, off - valueOff);
          cookies.getApplicationCookies().add(new HttpCookie(key, value));
        }
      }
    }
  }

  private static boolean isApplicationCookie(byte[] value, int off, int len) {
    return Ascii.asciiArrToIntNoException(value, off, len) != -1;
  }

  private static boolean isReserved(byte[] cookieName, int off, int len) {
    return ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, comment) || ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, discard)
            || ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, domain) || ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, expires)
            || ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, max_age) || ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, path)
            || ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, secure) || ByteArrayUtils.equalsIgnoreCase(cookieName, off, len, version);
  }

  private static void setCookieDomain(HttpCookie cok, String hostName, WebCookieConfig cookieConfig) {
	  if (cookieConfig.getDomainType() == WebCookieConfig.NONE) {
	      return;
	    }
	    if (cookieConfig.getDomainType() == WebCookieConfig.OTHER) {
	      cok.setDomain(cookieConfig.getDomain());
	      return;
	    }

	    if (cookieConfig.getCookieType() == WebCookieConfig.COOKIE_TYPE_APPLICATION && cookieConfig.getDomain() == null) {
	      return;
	    }
    setDomainFromHost(cok, hostName);
  }

  private static void setCookiePath(HttpCookie cok, WebCookieConfig cookieConfig) {
    if (cookieConfig.getPathType() == WebCookieConfig.NONE) {
      return;
    }
	cok.setPath(cookieConfig.getPath());
  }
}
