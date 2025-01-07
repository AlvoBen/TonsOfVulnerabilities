package com.tssap.dtr.client.lib.protocol.templates;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.tssap.dtr.client.lib.protocol.IAuthenticator;
import com.tssap.dtr.client.lib.protocol.IConnectionTemplate;
import com.tssap.dtr.client.lib.protocol.requests.ParserException;
import com.tssap.dtr.client.lib.protocol.session.Certificates;
import com.tssap.dtr.client.lib.protocol.session.Cookies;
import com.tssap.dtr.client.lib.protocol.session.SSO2Authenticator;
import com.tssap.dtr.client.lib.protocol.session.SessionContext;
import com.tssap.dtr.client.lib.protocol.session.Certificates.KeyStoreType;
import com.tssap.dtr.client.lib.protocol.util.XMLWriter;
import com.tssap.dtr.client.lib.protocol.util.XMLWriter.Attribute;

/**
 * Helper class to serialize the content of a template provider
 */
public class TemplateSerializer {

	private ITemplateProvider provider;
	
	/**
	 * Creates a new template serializer for the given provider
	 * @param provider  the provider to handle
	 */
	public TemplateSerializer(ITemplateProvider provider) {
		this.provider = provider;
	}

	/**
	 * Serializes the template to the given stream. The output is written
	 * as XML format
	 * @param out  the stream to write to
	 * @throws IOException  if an i/o error occured
	 */
	public void serialize(OutputStream out) throws IOException {
		List templates = provider.listConnectionTemplates();
		
		XMLWriter writer = new XMLWriter(out);
		writer.wrapAttributeLists(true);
		writer.beginDocument();
		writer.writeRootTag("servers");
				
		for (int i=0; i<templates.size(); ++i) {
			IConnectionTemplate template = (IConnectionTemplate)templates.get(i);
			(new PersistableTemplate(template)).serialize(writer);
		}
		
		writer.endDocument();
		writer.serialize();
		writer.close();		
	}
	
	/**
	 * Initializes the template from the given stream.
	 * @param in  the stream to read from
	 * @throws IOException  if an i/o error occured
	 */
	public void initialize(InputStream in) throws IOException, TemplateException {		
		try {
			XMLReader reader = createXMLReader();			
			if (reader != null) {
				ContentHandler handler = new InitHandler(provider);
				reader.setContentHandler(handler);
				InputSource s = new InputSource(in);
				reader.parse(s);
			}
		} catch (SAXException ex) {
			throw new TemplateException("Invalid templates detected", ex);
		}		
	}


	private XMLReader createXMLReader() {
		XMLReader saxParser = null;		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			saxParser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		} catch (SAXException ex) {
			throw new ParserException("Unable to find or instantiate XML parser", ex);
		} catch (FactoryConfigurationError ex) { //$JL-EXC$
			throw new ParserException("Failed to instantiate XML parser factory", ex);
		} catch (ParserConfigurationException ex) {
			throw new ParserException("Unable to find or instantiate XML parser", ex);
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}

		return saxParser;
	}
	
	
	private static class PersistableTemplate extends ConnectionTemplate 
	{
		private static final String SERVER = "server";
		private static final String URL = "url";
		private static final String PROXY = "proxy";
		private static final String USE_PROXY = "useProxy";
		private static final String TUNNEL_PROXY = "tunnelProxy";
		private static final String BYPASS_PROXY = "bypassProxyFor";
		private static final String READ_TIMEOUT = "readTimeout";
		private static final String CONNECT_TIMEOUT = "connectTimeout";
		private static final String EXPIRE_TIMEOUT = "expireTimeout";
		private static final String REPEAT = "repeat";
		private static final String REPEAT_ON_TIMEOUT = "repeatOnTimeout";
		private static final String FOLLOW_REDIRECTS = "followRedirects";
		private static final String FOLLOW_PERMANENT_REDIRECTS = "redirectPermanent";
		private static final String FOLLOW_FOREIGN_REDIRECTS = "redirectsToForeignDomains";
		private static final String FOLLOW_PROXY_REDIRECTS = "redirectsToProxies";
		private static final String FOLLOW_HTTPS_REDIRECTS = "redirectsToHTTPS";
		private static final String REDIRECT_ALL_METHODS = "redirectAllMethods";
		private static final String REDIRECT_MAX_HOPS = "redirectMaxHops";
		private static final String SENDBUFFER_SIZE = "sendbufferSize";
		private static final String RECEIVEBUFFER_SIZE = "receivebufferSize";
		private static final String COMPRESS = "compress";
		private static final String COMPRESS_REQUESTS = "compressRequests";
		private static final String COMPRESS_RESPONSES = "compressResponses";
		private static final String DIGEST = "digest";
		private static final String CERTIFY_ME = "certifyMe";
		private static final String CERTIFY_THEM = "certifyThem";
		private static final String KEYSTORE = "keystore";
		private static final String TRUSTSTORE = "truststore";
		private static final String AUTHENTICATE = "authenticate";
		private static final String AUTH_SCHEME = "authScheme";
		private static final String COOKIES = "cookies";
		private static final String COOKIE_PRIVACY = "cookiePrivacy";
		private static final String REJECT_COOKIES_FOR = "rejectCookiesFor";
		private static final String PROTOCOL_VERSION = "protocolVersion";
		private static final String USER_AGENT = "userAgent";


		/**
		 * Creates a connection template with default setting.
		 */
		protected PersistableTemplate() {
			super();
		}
		
		/**
		 * Creats a clone of the given template 
		 * @param template  the template to clone
		 */
		public PersistableTemplate(IConnectionTemplate template) {
			super(template);
		}

		/**
		 * Serialize the template to the given writer.
		 * @param writer an XML writer
		 * @throws IOException  if an i/o error occured
		 */
		protected void serialize(XMLWriter writer) throws IOException {

			List attributes = new ArrayList();
			attributes.add(new Attribute(URL, getUrl()));		

			String proxyUrl = (proxy != null) ? proxy + ":" + proxyPort : "none";
			attributes.add(new Attribute(PROXY, proxyUrl));
			attributes.add(new Attribute(USE_PROXY, isUsingProxy() ? "on" : "off"));
			attributes.add(new Attribute(TUNNEL_PROXY, isTunnelingProxy() ? "on" : "off"));
				

			if (context != null) {
				attributes.add(
					new Attribute(AUTHENTICATE, context.getSendAuthentication() ? "on" : "off"));

				IAuthenticator auth = context.getAuthenticator();
				if (auth != null) {
					String authScheme = auth.getAuthenticationScheme();
					if (authScheme == SSO2Authenticator.AUTH_SCHEME) {
						IAuthenticator login = ((SSO2Authenticator)auth).getLoginAuthenticator();
						if (login != null) {
							attributes.add(new Attribute(AUTH_SCHEME,
									authScheme + "," + login.getAuthenticationScheme()));
						} else {
							attributes.add(new Attribute(AUTH_SCHEME, authScheme));
						}
					} else {
						attributes.add(new Attribute(AUTH_SCHEME, authScheme));
					}
				} else {
					attributes.add(new Attribute(AUTH_SCHEME, "default"));
				}

				Certificates certs = context.certificates();
				attributes.add(new Attribute(CERTIFY_ME, certs.authenticateMe() ? "on" : "off"));
				attributes.add(new Attribute(CERTIFY_THEM, certs.authenticateThem() ? "on" : "off"));

				String certStore = certs.getClientCertStore();
				KeyStoreType certStoreType = certs.getClientCertStoreType();
				attributes.add(new Attribute(KEYSTORE,
						(certStore != null) ? certStoreType.toString() + "," + certStore : "none"));

				certStore = certs.getServerCertStore();
				certStoreType = certs.getServerCertStoreType();
				attributes.add(new Attribute(TRUSTSTORE,
						(certStore != null) ? certStoreType.toString() + "," + certStore : "default"));

				attributes.add(new Attribute(COOKIES, context.getSendCookies() ? "on" : "off"));
				attributes.add(new Attribute(COOKIE_PRIVACY, context.cookies().getPrivacy().toString()));

			} else {
				attributes.add(new Attribute(AUTHENTICATE, "off"));
				attributes.add(new Attribute(AUTH_SCHEME, "default"));
				attributes.add(new Attribute(CERTIFY_ME, "off"));
				attributes.add(new Attribute(CERTIFY_THEM, "off"));
				attributes.add(new Attribute(KEYSTORE, "none"));
				attributes.add(new Attribute(TRUSTSTORE, "default"));
				attributes.add(new Attribute(COOKIES, "on"));
				attributes.add(new Attribute(COOKIE_PRIVACY, Cookies.Privacy.FROM_ORIGINAL_HOST_ONLY.toString()));
			}

			boolean compressed = compressedRequests && compressedResponses;
			if (compressed || compressionAlgorithm != null) {
				if (compressionAlgorithm != null) {
					attributes.add(new Attribute(COMPRESS,
							(compressed ? "on" : "off") + "," + compressionAlgorithm ));
				} else {
					attributes.add(new Attribute(COMPRESS, compressed? "on" : "off"));
				}
			} else if (compressedRequests) {
				attributes.add(new Attribute(COMPRESS_REQUESTS, "on"));
			} else if (compressedResponses) {
				attributes.add(new Attribute(COMPRESS_RESPONSES, "on"));
			} else {
				attributes.add(new Attribute(COMPRESS, "off"));
			}

			attributes.add(new Attribute(READ_TIMEOUT, Integer.toString(readTimeout)));
			attributes.add(new Attribute(CONNECT_TIMEOUT, Integer.toString(connectTimeout)));
			attributes.add(new Attribute(EXPIRE_TIMEOUT, Integer.toString(expirationTimeout)));

			attributes.add(new Attribute(REPEAT, Integer.toString(requestRepetitions)));
			attributes.add(new Attribute(REPEAT_ON_TIMEOUT, repeatOnTimeout ? "on" : "off"));

			attributes.add(new Attribute(FOLLOW_REDIRECTS, followRedirects ? "on" : "off"));
			attributes.add(new Attribute(FOLLOW_PERMANENT_REDIRECTS, followPermanentRedirects ? "on" : "off"));
			attributes.add(new Attribute(FOLLOW_FOREIGN_REDIRECTS, followForeignRedirects ? "on" : "off"));
			attributes.add(new Attribute(FOLLOW_PROXY_REDIRECTS, followProxyRedirects ? "on" : "off"));
			attributes.add(new Attribute(FOLLOW_HTTPS_REDIRECTS, "off"));
			attributes.add(new Attribute(REDIRECT_ALL_METHODS, followRedirectAllMethods ? "on" : "off"));
			attributes.add(new Attribute(REDIRECT_MAX_HOPS, Integer.toString(maxRedirects)));

			attributes.add(new Attribute(SENDBUFFER_SIZE, 
				(sendBufferSize<0)? "default" : Integer.toString(sendBufferSize)));
			attributes.add(new Attribute(RECEIVEBUFFER_SIZE,
				(receiveBufferSize<0)? "default" : Integer.toString(receiveBufferSize))); 

			//		if ( isResponseDigestEnabled() || getDigestAlgorithm() != null ) {
			//			if ( getDigestAlgorithm() != null ) {
			//				attributes.add(new Attribute(DIGEST,
			//					getDigestAlgorithm() + "," + (isResponseDigestEnabled()? "on" : "off")));							
			//			} else {
			//				attributes.add(new Attribute(DIGEST,
			//					(isResponseDigestEnabled()? "on" : "off")));				
			//			}
			//		} else {
			//			attributes.add(new Attribute(DIGEST, "off"));				
			//		}

			attributes.add(new Attribute(PROTOCOL_VERSION, version));
			attributes.add(new Attribute(USER_AGENT, userAgent));		

			writer.writeEmptyTag(
				SERVER,
				(Attribute[])attributes.toArray(new Attribute[attributes.size()]));

		}

		/**
		 * Initialize the template from the given attribute list.
		 * The attributes consists of name/value pairs.
		 * @param attributes  a list of attributes defining the parameters
		 * of the template
		 */
		protected void initialize(org.xml.sax.Attributes attributes) throws TemplateException
		{
			String url = attributes.getValue(URL);
			if (url == null) {
				throw new TemplateException("Missing URL");
			}

			try {
				if (!url.endsWith("/")) {
					url = url.concat("/");
				}				
				setUrl(url);				
			} catch (MalformedURLException e) {
				throw new TemplateException("Malformed URL encountered [" + url + "]");
			}

			// Proxy
			String proxy = attributes.getValue(PROXY);
			if (proxy != null  &&  !"none".equalsIgnoreCase(proxy)) {
				try {
					int n = proxy.indexOf("://");
					if (n>0) {
						setProxyUrl(proxy);
					} else {
						n = proxy.indexOf(':');
						if (n > 0) {
							setProxyHost(proxy.substring(0,n).trim());
							setProxyPort(Integer.parseInt(proxy.substring(n+1).trim()));
						} else {
							setProxyHost(proxy);
						}
					}
				} catch (NumberFormatException e) {
					throw new TemplateException("Malformed proxy port [" + proxy + "]");
				} catch (MalformedURLException e) {
					throw new TemplateException("Malformed proxy URL [" + proxy + "]");
				}

				String useProxy = attributes.getValue(USE_PROXY);
				if (useProxy != null) {
					setUseProxy("on".equalsIgnoreCase(useProxy));
				}
			
				String bypassProxy = attributes.getValue(BYPASS_PROXY);
				if (bypassProxy != null) {
					setUseProxy("on".equalsIgnoreCase(useProxy), bypassProxy);
				}			

				String tunnelProxy = attributes.getValue(TUNNEL_PROXY);
				if (tunnelProxy != null) {
					setTunnelProxy("on".equalsIgnoreCase(tunnelProxy));
				}
			}
		
			SessionContext ctx = new SessionContext();
			setSessionContext(ctx);
		
			// Authentication
			String authenticate = attributes.getValue(AUTHENTICATE);
			String authScheme = attributes.getValue(AUTH_SCHEME);
			if (authenticate != null) {		
				ctx.setSendAuthentication("on".equalsIgnoreCase(authenticate));
			}
			if (authScheme != null  &&  !"default".equalsIgnoreCase(authScheme)) {
				try {
					if (authScheme.startsWith(SSO2Authenticator.AUTH_SCHEME)) {
						SSO2Authenticator auth = new SSO2Authenticator(ctx);
						int n = authScheme.indexOf(",");
						if (n > 0) {
							authScheme = authScheme.substring(n+1).trim();
							auth.setLoginAuthenticator(authScheme);					
						}					
						ctx.setAuthenticator(auth);					
					} else {
						ctx.setAuthenticator(authScheme);
					}
				} catch (NoSuchAlgorithmException e) {
					throw new TemplateException("Unknown authentication scheme [" + authScheme + "]");
				}
			}
		
		
			// Certificate stores
			Certificates certs = ctx.certificates();	
		
			String keystore = attributes.getValue(KEYSTORE);
			String clientStoreType = null;
			String clientStoreFile = null;		
		
			if (keystore != null  &&  !"none".equalsIgnoreCase(keystore)) {
				int n = keystore.indexOf(',');
				if (n > 0) {
					clientStoreType = keystore.substring(0,n).trim();
					clientStoreFile = keystore.substring(n+1).trim();
				} else {
					clientStoreFile = keystore.trim();
				}
			}	
		
			String truststore = attributes.getValue(TRUSTSTORE);
			String serverStoreType = null;
			String serverStoreFile = null;

			if (truststore != null  &&  !"default".equalsIgnoreCase(truststore)) {
				int n = truststore.indexOf(',');
				if (n > 0) {
					serverStoreType = truststore.substring(0,n).trim();
					serverStoreFile = truststore.substring(n+1).trim();
				} else {
					serverStoreFile = truststore.trim(); 
				}
			}			
		 
			if ("SERVER".equalsIgnoreCase(clientStoreType) || "SERVER".equalsIgnoreCase(serverStoreType)) {
				if ("SERVER".equalsIgnoreCase(clientStoreType) && "SERVER".equalsIgnoreCase(serverStoreType)) {
					certs.setEngineCertificateStores(serverStoreFile, clientStoreFile);				
				} else {
					throw new TemplateException("Cannot handle mixed keystore types [" 
						+ clientStoreType.toString() + "," + serverStoreType.toString() + "]");
				}
			} else {
				if (clientStoreFile != null) {
					if (clientStoreType == null) {
						try {
							certs.setClientCertificates(clientStoreFile);
						} catch (KeyStoreException e) {
							throw new TemplateException("Unable to determine type of keystore [" +
								clientStoreFile + "]");		
						}	
					} else {
						certs.setClientCertificates(clientStoreFile, Certificates.KeyStoreType.valueOf(clientStoreType));				
					}
				}
				
				if (serverStoreFile != null) {
					if (serverStoreType == null) {
						certs.setServerCertificates(serverStoreFile);	
					} else {
						certs.setServerCertificates(serverStoreFile, Certificates.KeyStoreType.valueOf(serverStoreType));				
					}
				}			
			}
		
			// Certification					
			certs.setAuthenticateMe("on".equalsIgnoreCase(attributes.getValue(CERTIFY_ME)));
			certs.setAuthenticateThem("on".equalsIgnoreCase(attributes.getValue(CERTIFY_THEM)));
		
			// Cookies
			String cookies = attributes.getValue(COOKIES);
			if (cookies != null) {
				ctx.setSendCookies(("on".equalsIgnoreCase(cookies)));
			}
				
			String privacy = attributes.getValue(COOKIE_PRIVACY);
			if (Cookies.Privacy.FROM_ALL_HOSTS.toString().equalsIgnoreCase(privacy)) {
				ctx.cookies().setPrivacy(Cookies.Privacy.FROM_ALL_HOSTS);
			}
		
			String domains = attributes.getValue(REJECT_COOKIES_FOR);
			if (domains != null  &&  !"none".equalsIgnoreCase(domains)) {
				ctx.cookies().rejectCookiesFor(domains);
			}
			
			// Timeouts
			try {
				String timeout = attributes.getValue(READ_TIMEOUT);
				if (timeout != null) {
					setSocketReadTimeout(Integer.parseInt(timeout));
				}
			} catch (NumberFormatException e) {
				throw new TemplateException("Invalid read timeout [not an integer number]");	
			}

			try {
				String connectTimeout = attributes.getValue(CONNECT_TIMEOUT);
				if (connectTimeout != null) {
					setSocketConnectTimeout(Integer.parseInt(connectTimeout));
				}
			} catch (NumberFormatException e) {
				throw new TemplateException("Invalid connect timeout [not an integer number]");
			}

			try {
				String expirationTimeout = attributes.getValue(EXPIRE_TIMEOUT);
				if (expirationTimeout != null) {
					setSocketExpirationTimeout(Integer.parseInt(expirationTimeout));
				}
			} catch (NumberFormatException e) {
				throw new TemplateException("Invalid expire timeout [not an integer number]");
			}

			// Repetition
			try {
				String repeat = attributes.getValue(REPEAT);
				String repeatOnTimeout = attributes.getValue(REPEAT_ON_TIMEOUT);
				if (repeat != null) {
					setRequestRepetitions(
						Integer.parseInt(repeat),
						"on".equalsIgnoreCase(repeatOnTimeout));
				}
			} catch (NumberFormatException e) {
				throw new TemplateException("Invalid repetition count [not an integer number]");
			}

			// Redirects
			String redirect = attributes.getValue(FOLLOW_REDIRECTS);
			if (redirect != null) {
				setFollowRedirects("on".equalsIgnoreCase(redirect));
			}
			redirect = attributes.getValue(FOLLOW_PERMANENT_REDIRECTS);
			boolean redirectPermanent = ("on".equalsIgnoreCase(redirect));
			redirect = attributes.getValue(FOLLOW_FOREIGN_REDIRECTS);
			boolean redirectForeign = ("on".equalsIgnoreCase(redirect));
			redirect = attributes.getValue(FOLLOW_PROXY_REDIRECTS);
			boolean redirectProxy = ("on".equalsIgnoreCase(redirect));
			redirect = attributes.getValue(FOLLOW_HTTPS_REDIRECTS);
			boolean redirectHttps = ("on".equalsIgnoreCase(redirect));
			redirect = attributes.getValue(REDIRECT_ALL_METHODS);
			boolean allMethods = ("on".equalsIgnoreCase(redirect));
			redirect = attributes.getValue(REDIRECT_MAX_HOPS);
			int maxHops = (redirect!=null)? Integer.parseInt(redirect) : 1;		
			//setFollowRedirects(redirectPermanent, redirectProxy, redirectForeign, allMethods, maxHops);

			// Buffers
			try {
				String sendbufferSize = attributes.getValue(SENDBUFFER_SIZE);
				if (sendbufferSize != null  &&  !"default".equalsIgnoreCase(sendbufferSize)) {
					setSendBufferSize(Integer.parseInt(sendbufferSize));
				}
			} catch (NumberFormatException e) {
				throw new TemplateException("Invalid send buffer size [not an integer number]");
			}

			try {
				String receivebufferSize = attributes.getValue(RECEIVEBUFFER_SIZE);
				if (receivebufferSize != null  &&  !"default".equalsIgnoreCase(receivebufferSize)) {
					setReceiveBufferSize(Integer.parseInt(receivebufferSize));
				}
			} catch (NumberFormatException e) {
				throw new TemplateException("Invalid receive buffer size [not an integer number]");
			}

			// Compression
			String compress = attributes.getValue(COMPRESS);
			if (compress != null) {
				int n = compress.indexOf(',');
				if (n < 0) {
					enableCompression("on".equals(compress));
				} else {
					enableCompression("on".equals(compress.substring(0, n)));
					setCompressionAlgorithm(compress.substring(n + 1).toLowerCase());
				}
			}
			String compressRequests = attributes.getValue(COMPRESS_REQUESTS);
			if (compressRequests != null) {
				int n = compressRequests.indexOf(',');
				if (n < 0) {
					enableRequestCompression("on".equals(compressRequests));
				} else {
					this.enableRequestCompression("on".equals(compressRequests.substring(0, n)));
					if (compress == null) {
						setCompressionAlgorithm(compressRequests.substring(n + 1).toLowerCase());
					}
				}
			}
			String compressResponses = attributes.getValue(COMPRESS_RESPONSES);
			if (compressResponses != null) {
				int n = compressResponses.indexOf(',');
				if (n < 0) {
					enableRequestCompression("on".equals(compressResponses));
				} else {
					enableResponseCompression("on".equals(compressResponses.substring(0, n)));
					if (compress == null) {
						setCompressionAlgorithm(compressResponses.substring(n + 1).toLowerCase());
					}
				}
			}

			String digest = attributes.getValue(DIGEST);
			if (digest != null) {
				int n = digest.indexOf(',');
				if (n < 0) {
					enableResponseDigest("on".equals(digest));
				} else {
					enableResponseDigest("on".equals(digest.substring(0, n)));
					setDigestAlgorithm(digest.substring(n + 1).toUpperCase());
				}
			}
		
			String version = attributes.getValue(PROTOCOL_VERSION);
			if (version != null  &&  !"default".equalsIgnoreCase(version)) {
				setHTTPVersion(version);
			}
		
			String userAgent = attributes.getValue(USER_AGENT);
			if (userAgent != null  &&  !"default".equalsIgnoreCase(userAgent)) {
				setUserAgent(userAgent);
			}	
		}	
	}
	
	
	
	private static class InitHandler extends DefaultHandler
	{
		private final int START_STATE = 0;
		private final int SERVERS_STATE = 1;

		private int _state = START_STATE;
		
		private ITemplateProvider provider;
		
		public InitHandler(ITemplateProvider provider) {
			this.provider = provider;
		}		
			
		public void startElement(String namespaceURI, String localName, String rawName, Attributes attributes)
		throws SAXException
		{
			switch ( _state ) {
				case START_STATE:
					if ( rawName.equalsIgnoreCase("servers") ) {
						_state = SERVERS_STATE;
						break;
					} 
				case SERVERS_STATE:
					if ( rawName.equalsIgnoreCase("server") ) {
						PersistableTemplate template = new PersistableTemplate();
						try {
							template.initialize(attributes);
						} catch (TemplateException e) {
							throw new SAXException("Invalid template", e);	
						}
						provider.addConnectionTemplate(template);
						break;
					}
				default: 
					throw new SAXException(
						"internal state error: startTag(" + localName + ") state(" + _state + ")");
												
			}			
		}
	}

//	public static void main(String[] args) throws Exception {
//		TemplateSerializer provider = 
//			new TemplateSerializer(new SimpleTemplateProvider());
//		provider.initialize(new FileInputStream("C:\\DTRdev\\.dtr\\servers.xml"));
//		
//		provider.serialize(new FileOutputStream("C:\\DTRdev\\.dtr\\servers1.xml"));				
//	}

}
