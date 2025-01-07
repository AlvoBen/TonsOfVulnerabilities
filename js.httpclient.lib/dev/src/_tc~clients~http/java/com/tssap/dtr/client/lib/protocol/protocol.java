package com.tssap.dtr.client.lib.protocol;

/**
 * Enumerator defining the protocol to use for requests
 * (either "HTTP" or "HTTPS").
 */
public final class Protocol {
	
	private final String name;
	private int defaultPort;
	
	private Protocol(String name, int defaultPort) {
		this.name = name;
		this.defaultPort = defaultPort;
	}

	/**
	 * Returns a new String object representing the selected protocol.
	 * @return either "http" or "https".
	 */
	public String toString() {
		return name;
	}

	/**
	 * Checks whether the given protocol string equals
	 * this protocol.
	 * @param protocol  either "http" or "https" (case-insensitive)
	 * @return true, if the given protocol matches this one
	 */
	public boolean equals(String protocol) {
		return name.equalsIgnoreCase(protocol);
	}
	
	/**
	 * Returns the default port number for this protocol
	 * @return a port number
	 */
	public int getDefaultPort() {
		return defaultPort;
	}	

	/**
	 * Returns a Ptotocol value matching the given string. Expected either
	 * "http" or "https". The method is not case-sensitive.
	 * @param s  the string to be evaluated.
	 */
	public static Protocol valueOf(String s) {
		Protocol protocol;
		if (s.equalsIgnoreCase("http")) {
			protocol = Protocol.HTTP;
		} else if (s.equalsIgnoreCase("https")) {
			protocol = Protocol.HTTPS;
		} else {
			throw new IllegalArgumentException("parameter is not recoginized as legal enumeration value");
		}
		return protocol;
	}

	/**
	 * Checks whether the given string matches a known protocol
	 * @param s  a protocol identifier
	 * @return true, if the protocol is known
	 */
	public static boolean isValidProtocol(String s) {
		if (s.equalsIgnoreCase("http")) {
			return true;
		} else if (s.equalsIgnoreCase("https")) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the default port number for the given protocol
	 * @param protocol  a protocol identifier
	 * @return the default port number for the protocol, or -1 if
	 * the protocol is not known
	 */
	public static int getDefaultPort(String protocol) {
		if (protocol.equalsIgnoreCase("http")) {
			return IConnectionTemplate.DEFAULT_PORT;
		} else if (protocol.equalsIgnoreCase("https")) {
			return IConnectionTemplate.DEFAULT_SSL_PORT;
		}
		return -1;
	}

	/** Indicates that the selected protocol is HTTP */
	public static final Protocol HTTP = new Protocol("http", 80);
	/** Indicates that the selected protocol is Secure HTTP (HTTPS) */
	public static final Protocol HTTPS = new Protocol("https", 443);
}
