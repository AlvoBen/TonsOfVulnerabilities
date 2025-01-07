package com.sap.security.api.umap;

import java.util.*;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * <p>a generic container used to request and transfer authentication related
 * data needed during login, e.g. username and password. Credentials has two
 * facilities to store data:</p>
 * <h5>Elements</h5>
 * <p>Elements are single pieces of information to login a user, e.g. the username.
 * Which elements are stored in a Credentials object and thus needed for a login
 * procedure is determined at the time of instantiation of the Credentials object
 * by the contents of the Properties object passed to the constructor (more on
 * configuration later). Each Element can provide metadata about itself and can
 * thus indicate how to prompt a user for this Element.</p>
 * <h5>Attributes</h5>
 * <p>Attributes are arbitrary objects. They can be used to share information between
 * different entities which have to handle the Credentials object, e.g. an authentication
 * service and a login module. Clients should NOT have to store nor get any information from the
 * attributes. NOTE that clients have full access to any object stored in the Credential's
 * attributes. Any object stored in the attributes can be read by the client; in case the object
 * is not immutable, it can even be altered or replaced as simply as:
 * <pre>
 * Object sensitive = credentials.getAttribute("very.sensitive.token");
 * sensitive = new OtherToken();
 * </pre>
 * so you must make sure that no data is stored in the attributes which is not to be exposed to
 * the client. Preferrably, you would even work with an adapter which uses Credentials internally, but
 * hides the attributes and ID related methods from the client.</p>
 * <p>After the login procedure has completed, the service which handed out the Credentials object
 * should call {@link #clear()} to clean out the values of all the Credentials Elements and to release
 * all refernces to its attributes.</p>
 * <h3>Configuration</h3>
 * <p>To configure a Credentials object, you can pass in a Properties object which holds information
 * about the Elements to be contained in this Credentials object</p>
 * <p>Example Properties file:
 * <pre>
 * #Configuration of Element "user"
 * MyLoginModule.user.Type      = text
 * MyLoginModule.user.Index     = 1
 * MyLoginModule.user.Default   = guest
 * #
 * #Configuration of Element "password"
 * MyLoginModule.password.Type  = password
 * MyLoginModule.password.Index = 2
 * #
 * #Configuration of Element "client"
 * MyLoginModule.client.Type    = choice
 * MyLoginModule.client.Index   = 3
 * MyLoginModule.client.Choices = 050,100,200
 * </pre>
 * You must at least specify a "Type" for every Element you define. To create a corresponding
 * Credentials object, you would read the file into a Properties object (say <tt>configuration</tt>)
 * and
 * <pre>
 * Credentials credentials = new Credentials(null, configuration, "MyLoginModule");
 * </pre>
 * </p>
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 */
public class Credentials implements Cloneable {

	private static final Location _trace = Location.getLocation("com.sap.security.api");

    /** a single piece of information needed for login */
    public static class Element implements Cloneable {

		/** possible return value of {@link #getType()},
		 *  indicating an Element with a limited choice of allowed
		 *  values
		 */
		public static final String    T_CHOICE    = "choice";

		/** possible return value of {@link #getType()},
		 *  indicating an Element the value of which can be displayed
		 *  to the user in clear text (e.g. user names)
		 */
		public static final String    T_TEXT      = "text";

		/** possible return value of {@link #getType()},
		 *  indicating an Element the value of which must be displayed
		 *  without echoing the actual characters (e.g. passwords)
		 */
		public static final String    T_PASSWORD  = "password";

		private String   name;
		private String   alias;
		private String   type;
		private String   value;
		private String   defaultVal;
		private String   prompt;
		private String[] choices;
		private boolean  isObsolete = false;
		private int      index      = 0;

		private Element() {}

		/**
		 * Clone this Element. Note that the value of an Element won't be cloned.
		 */
		public Object clone() {
			Object o = null;
			try {
				o = super.clone();
				value = null;
			}
			catch(CloneNotSupportedException e) {
				_trace.traceThrowableT(Severity.INFO, "Credentials.Element can't clone", e);
			}
			return o;
		}

		/**
		 * get the Element's name
		 */
		public String getName() {
			return name;
		}

		/**
		 * get the Element's alias (defaults to the Element's
		 * name)
		 */
		public String getAlias() {
			return alias != null ? alias : name;
		}

		/**
		 * get the Element's type, which can be one of
		 * <tt>T_TEXT, T_PASSWORD, T_CHOICE</tt>
		 */
		public String getType() {
			return type;
		}

		/**
		 * get the Element's value, or it's default
		 * if the value is null (note that the default
		 * may also be null)
		 */
		public String getValue() {
			return value != null ? value : defaultVal;
		}

		/**
		 * get the text to display when prompting a user for
		 * this Element
		 */
		public String getPrompt() {
			return prompt;
		}

		/**
		 * get the allowed choices for this Element, in case it
		 * is of type {@link #T_CHOICE}. For other types, this
		 * method will always return null.
		 */
		public String[] getChoices() {
			return choices;
		}

		/**
		 * set this Element's value. If this Element's type is
		 * {@link #T_CHOICE}, the value will only be set if its
		 * one of the allowed values as indicated by
		 * {@link #getChoices()}
		 * @return true if the value has been set, false otherwise
		 */
		public boolean setValue(String value) {
			if(choices != null) {
				for(int i=0; i < choices.length; i++) {
					if(choices[i].equals(value)) {
						this.value = value;
						return true;
					}
				}
				return false;
			}
			else {
				this.value = value;
				return true;
			}
		}
    } // class Element

    /**
     * compare two Elements to be able to sort them after parsing the
     * configuration Properties object
     */
    private static class ElementComparator implements Comparator {
		/**
		 * compare the index of two Elements
		 */
		public int compare(Object o1, Object o2) {
			return (((Element)o1).index - ((Element)o2).index);
		}
    } // class ElementComparator

    /** provides functions for parsing the Properties object used for configuration
     */
    private static class ConfParser {

		/** get all Elemets configured in <pre>p</pre>,
		 *  using all properties in the namespace <pre>ns</pre>.
		 *  @param p  configuration
		 *  @param ns namespace
		 *  @throws IllegalArgumentException if the configuration in
		 *          <pre>p</pre> contains errors
		 */
		static Element[] getElements(Properties p, String ns)
			throws IllegalArgumentException {
			HashMap   elementMap  = new HashMap();
			ArrayList elementList = new ArrayList();
			if(!ns.equals("") && !ns.endsWith(".")) {
				ns = ns + ".";
			}
			int nsLen     = ns.length();
			Enumeration e = p.propertyNames();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				if(!key.startsWith(ns)) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(key.substring(nsLen), ".");
				if(st.countTokens() != 2) {
					continue;
				}
				String name     = st.nextToken();
				String suffix   = st.nextToken();
				Element el      = createOrGetExisting(elementMap, name);
				String val      = p.getProperty(key);
				if(suffix.equals(P_TYPE)) {
					boolean isKnown = false;
					for(int i=0; i < KNOWN_TYPES.length; i++) {
						if(KNOWN_TYPES[i].equalsIgnoreCase(val)) {
							isKnown = true;
							el.type = val;
							break;
						}
					}
					if(!isKnown) {
						throw new IllegalArgumentException("Unknown type: " + val);
					}
				}
				else if(suffix.equals(P_DEFAULT)) {
					el.defaultVal = val;
				}
				else if(suffix.equals(P_PROMPT)) {
					el.prompt = val;
				}
				else if(suffix.equals(P_CHOICES)) {
					el.choices = csListToArray(val);
				}
				else if(suffix.equals(P_OBSOLETE)) {
					el.isObsolete = true;
				}
				else if(suffix.equals(P_ALIAS)) {
					el.alias = val;
				}
				else if(suffix.equals(P_INDEX)) {
					try {
						el.index = Integer.parseInt(val);
					}
					catch(NumberFormatException nfe) {
						throw new IllegalArgumentException(key + ": must be numeric", nfe);
					}
				}
			}
			//post-check
			Set allAliases = new HashSet();
			Set allNames   = elementMap.keySet();
			Iterator iter  = allNames.iterator();
			while(iter.hasNext()) {
				String name = (String)iter.next();
				Element el  = (Element)elementMap.get(name);
				//remove obsolete parameters
				if(el.isObsolete) {
					iter.remove();
					continue;
				}
				//remove untyped parameters
				if(el.type == null) {
					iter.remove();
					continue;
				}
				//disallow invalid defaults
				if(el.defaultVal != null && el.choices != null) {
					boolean isValid = false;
					for(int i=0; i < el.choices.length; i++) {
						if(el.choices[i].equals(el.defaultVal)) {
							isValid = true;
							break;
						}
					}
					if(!isValid) {
						throw new IllegalArgumentException("Element "+name+": Default value ("+el.defaultVal+") is not an allowed choice");
					}
				}
				//type "choice" must have a non-null Choices attribute
				if(el.type.equals(Element.T_CHOICE)) {
					if(el.choices == null) {
						throw new IllegalArgumentException("Element "+name+ ": Type "+Element.T_CHOICE+" must have choices");
					}
				}
				//and vice versa
				if(el.choices != null) {
					if(!el.type.equals(Element.T_CHOICE)) {
						throw new IllegalArgumentException("Element "+name+": must have type "+Element.T_CHOICE+" since it has choices");
					}
				}
				//aliases must be unique if non-null, and no
				//parameter may alias to another's real name
				//since the real name is used as alias if no alias
				//is explicitly provided
				if(el.alias != null) {
					if(allNames.contains(el.alias) || allAliases.contains(el.alias)) {
						throw new IllegalArgumentException("Element "+name+": ambiguous alias ("+el.alias+")");
					}
					else {
						allAliases.add(el.alias);
					}
				}
				//put the Element in a List
				elementList.add(el);
			}
			//sort the List
			Collections.sort(elementList, new ElementComparator());
			return (Element[])elementList.toArray(new Element[0]);
		}

		/**
		 * convenience method to either create a new {@link #Element}
		 * with the given <pre>name</pre> and put in into <tt>elementMap</tt>
		 * or get it from that map if it already exists
		 * @param elementMap map of elements
		 * @param name name of Element
		 * @return Element
		 */
		private static Element createOrGetExisting(Map elementMap,
							   String name) {
			Element el = null;
			if(elementMap.containsKey(name)) {
				el = (Element)elementMap.get(name);
			}
			else {
				el = new Element();
				el.name = name;
				elementMap.put(name, el);
			}
			return el;
		}

		/**
		 * convenience method to split a given comma separated list of
		 * tokens into an array of Strings. Only commas are allowed between
		 * tokens, no whitespace or linebreaks.
		 * @param csList comma separated list
		 * @return tokens of <pre>csList</pre>
		 */
		private static String[] csListToArray(String csList) {
			StringTokenizer st = new StringTokenizer(csList, ",");
			String[] ar        = new String[st.countTokens()];
			for(int i=0; i < ar.length; i++) {
				ar[i] = st.nextToken();
			}
			return ar;
		}
    } // class ConfParser


    /** property which specifies the alias of an Element */
    public static final String    P_ALIAS     = "Alias";
    /** property which specifies the index of an Element */
    public static final String    P_INDEX     = "Index";
    /** property which specifies the type of an Element */
    public static final String    P_TYPE      = "Type";
    /** property which specifies the default value of an Element */
    public static final String    P_DEFAULT   = "Default";
    /** property which specifies the text to display when prompting a user
     *  for this parameter
     */
    public static final String    P_PROMPT    = "Prompt";
    /** property which specifies the allowed choices for
     *  Elements of type {@link Element#T_CHOICE}
     */
    public static final String    P_CHOICES   = "Choices";
    /** property which serves to flag an Element as obsolete */
    public static final String    P_OBSOLETE  = "Obsolete";


    private String    mID;
    private HashMap   mAttributes;
    private Element[] mElements;
    private static final String[] KNOWN_TYPES = new String[] {
	Element.T_CHOICE, Element.T_TEXT, Element.T_PASSWORD };

    /**
     * Construct a new Credentials object. <pre>id</pre> can be
     * used as an identifier for this Credentials object (its use
     * is optional). <pre>configuration</pre> contains the configuration
     * for this Credentials object; every property in this Properties
     * object whose key starts with <pre>namespace</pre> will
     * be evaluated for the Credentials object's configuration,
     * while all others will be ignored.
     *  @param id            an arbitrary, immutable key to identify this
     *                       Credentials object (may be null)
     *  @param configuration contains Credentials configuration
     *  @param namespace     namespace of relevant properties
     *  @throws IllegalArgumentException if the configuration in
     *          <pre>configuration</pre> contains errors
     */
    public Credentials(String id, Properties configuration, String namespace)
	throws IllegalArgumentException {
		mID     = id;
		mAttributes = new HashMap();
		mElements   = ConfParser.getElements(configuration, namespace);
    }

    /**
     * get this Credentials ID
     * @return
     */
    String getID() {
		return mID;
    }

    /**
     * get the attribute which has the given <pre>name</pre>
     * @param key key of the attribute
     * @return value of the attribute, or null if no such attribute
     *         exists
     */
    public Object getAttribute(String key) {
		return mAttributes.get(key);
    }

    /**
     * set the <pre>value</pre> of attribute <pre>name</pre>
     * Credentials doesn't allow attributes to be overwritten,
     * thus the <pre>value</pre> of <pre>key</pre> will remain
     * unaffected if it was non-null and non-empty before.
     * @param key name of the attribute
     * @param value value of the attribute
     * @return true if the value has been set,
     */
    public boolean setAttribute(String key, Object value) {
		if(mAttributes.get(key) == null) {
			mAttributes.put(key, value);
			return true;
		}
		else {
			return false;
		}
    }

    /**
     * get the Elements contained in this
     * Credentials object
     */
    public Element[] getElements() {
		return mElements;
    }

    /**
     * get the Element named <tt>name</tt> from this
     * Credentials object. If no such Element exists,
     * null will be returned.
     */
    public Element getElement(String name) {
		for(int i=0; i < mElements.length; i++) {
			if(mElements[i].name.equals(name)) {
				return mElements[i];
			}
		}
		return null;
    }

    /**
     * clone this Credentials object. Note that this
     * will only clone the public parts of the Credentials,
     * i.e. the Elements accessible via <tt>getElements()</tt>
     */
    public Object clone() {
		Credentials o = null;
		try {
			o = (Credentials)super.clone();
			//clone the Elements in this Credentials object
			o.mElements = new Element[mElements.length];
			for(int i=0; i < mElements.length; i++) {
				o.mElements[i] = (Element)mElements[i].clone();
			}
			//but dismiss its state, since it's not sure if
			//all items in mAttributes are Cloneable and a shallow
			//copy would make sideeffects possible
			o.mAttributes.clear();
		}
		catch(CloneNotSupportedException e) {
			_trace.traceThrowableT(Severity.INFO, "Credentials.Element can't clone", e);
		}
		return o;
    }

    /** clear the values of all Elements
     */
    public void clear() {
		for(int i=0; i < mElements.length; i++) {
			mElements[i].value = null;
		}
		mAttributes.clear();
    }
}