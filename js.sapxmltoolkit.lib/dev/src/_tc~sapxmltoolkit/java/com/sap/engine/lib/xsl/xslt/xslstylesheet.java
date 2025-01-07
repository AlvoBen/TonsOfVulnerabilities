package com.sap.engine.lib.xsl.xslt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sap.engine.lib.jaxp.InstanceManager;
import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.DOMParser;
import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.URLLoader;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.pool.CharArrayPool;
import com.sap.engine.lib.xsl.xpath.BSFLibrary;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xpath.ETBuilder;
import com.sap.engine.lib.xsl.xpath.ETFunction;
import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.JLBLibrary;
import com.sap.engine.lib.xsl.xpath.LibraryManager;
import com.sap.engine.lib.xsl.xpath.StaticDouble;
import com.sap.engine.lib.xsl.xpath.StylesheetFunctionLibrary;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.XPathProcessor;
import com.sap.engine.lib.xsl.xpath.xobjects.IntArrayIterator;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XObjectFactory;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;
import com.sap.engine.lib.xsl.xslt.pool.ObjectPool;
import com.sap.engine.lib.xsl.xslt.pool.XSLVarEvalPool;

/**
 * This class encapulates an XSLT Transformation.
 * <p>First you need to initialize the stylesheet, by using one of the <code>init</code>
 * methods. Then you supply an XML document. The input options through this class are only by
 * specifying a filename, or supplying an input stream. There is also another option but it will
 * be discused in detail later. Then you set the output target which could be a filename, an
 * OutputStream or any class which implements the <code>com.sap.engine.lib.xml.parser.DocHandler</code>
 * interface. Finally you call <code>process(Object output)</code> and the stylesheet is processed.
 * The parameter <code>output</code> could be a filename, an OutputStream or any class which
 * implements the <code>com.sap.engine.lib.xml.parser.DocHandler</code> interface.
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 */
public final class XSLStylesheet {

  public static final String sXSLNamespace = "http://www.w3.org/1999/XSL/Transform";
  protected Vector templates = new Vector();
  protected XPathProcessor xpath = new XPathProcessor();
//  private CharArray hiddenBuffer = null;
  protected XSLOutputNode xslOutputNode = null;
  protected XSLOutputProcessor xslOutputProcessor = new XSLOutputProcessor();
  protected Hashtable decimalFormats = new Hashtable();
  protected Hashtable attributeSets = new Hashtable();
//  protected OutputStream outputStream = System.out; //$JL-SYS_OUT_ERR$
  protected Hashtable variables = new Hashtable();
  protected XSLVarEvalPool xslVarEvalPool = null;
  public boolean debug = false;
  public ETBuilder etBuilder = new ETBuilder();
  private int importLevel = 0;
  private int currentTemplatePosition = 0;
  private XSLTemplateHeader[] vTemplates = null;
  private Vector whiteSpaceElements = new Vector();
  private Vector vTemplateHeaders = new Vector();
  private String baseURI = null;
  private NamespaceManager nsmanager = null;
  private NamespaceManager xslNsmanager = null;
  private NamespaceHandler namespaceHandler = null;
  protected CharArrayPool crPoolXSLElement = new CharArrayPool(10, 15);
  protected StaticDouble staticDouble = new StaticDouble();
  protected URIResolver uriResolver = new URLLoader();
  protected URIResolver defaultResolver = uriResolver;
  private DTMFactory dtmFactory = null;
  private DTM dtm = null;
//  private Element tree = null;
  //  private XObjectFactory xfactMatch = new XObjectFactory(10000);
  private XObjectFactory xfactProcess = new XObjectFactory(5000);
  private int iWhiteSpaceElementsCount = 0;
//  private XMLAnalyzer xmlAnalyzer = null;
  private boolean bIsIndentAllowed = true;
  private ErrorListener errorListener = null;
  private LibraryManager libraryManager = null;
  private boolean bIncludeUse = false;
  public final String SAP_JAVA_XSLT_EXT_CLASSLOADER = "http://sap.com/java/xslt/ext-classloader";
  private ObjectPool hashTablesPool = new ObjectPool(Hashtable.class, 100, 100);
//  public ObjectPool qnamePool = new ObjectPool(QName.class, 100, 100);
  public Hashtable qnameTable = new Hashtable();
  
  /**
   * Stores arrays of xsl keys, every array contains
   * keys with equal names. All keys with equal names
   * are located in one array.
   */
  public Hashtable keyArrays = new Hashtable();
  /**
   * Each of the above keys matches to a NodeSet.
   * Here are stored the corresponding NodeSet arrays for all of the above keys.
   */
  public Hashtable nodesetArrays = new Hashtable();
  public Hashtable keyValuesArrays = new Hashtable();
  /**
   *  Stylesheet functions (XSLT2.0). Each key in the table represents a namespace.
   *  Each object in the table is a vector of the corresponding stylesheet functions.
   */
  public Hashtable stylesheetFunctions = new Hashtable();
  // Used in XSLCopyOf, XSLForEach, XSLApplyTemplates
  protected int[] stack = new int[100];
  protected int pStack = 0;
  protected int startDTMSize = -1;
  protected int startDTMDocumentNodesSize = -1;  
  //this vector holds the Top Variables and Params which are encounted while reading the stylesheet
  //the variables must be processed in document order, so their refernces will be kept
  //e.g. if A uses B then process A before B
  private Vector vTopVariablesToProcess = new Vector();
  private Vector vTopVariablesToProcessSave = new Vector();
  protected Hashtable parameters = null;
  private XSLStylesheet importedStylesheet = null;
  private Hashtable namespaceAliases = new Hashtable();
  private String sourceBaseURI = "";
  private ClassLoader extClassLoader = null;
  private Hashtable xfKeyHash = new Hashtable();

  /**
   * Constructs a new XSLStylesheet object
   *
   * @exception   XSLException  thrown if there was an error while initializing
   */
  public XSLStylesheet() throws XSLException {
    xslVarEvalPool = new XSLVarEvalPool(100, 100);
    libraryManager = new LibraryManager(this);
  }

  /**
   * Returns the pool for <code>XSLVarEval</code> variables
   *
   */
  public XSLVarEvalPool getXSLVarEvalPool() {
    return xslVarEvalPool;
  }

  /**
   * Returns the <code>XPathProcessor</code> assosiated with this stylesheet.
   * Each <code>XSLStylesheet</code> has its own <code>XPathProcessor</code>
   */
  public XPathProcessor getXPathProcessor() {
    return xpath;
  }

  /**
   * Used by the JAXP Framework
   *
   */
  public void reuseTemplate() {
    xslOutputProcessor.reuse();
  }

  /**
   * Prints the stylesheet to <code>System.err</code>
   */
  protected void print() {
    print("");
  }

  /**
   * Prints the stylesheet to <code>System.err</code> and uses the specified indent
   *
   * @param   ind  the indent to use when printing
   */
  protected void print(String ind) {
    for (int i = 0; i < templates.size(); i++) {
      ((XSLTemplate) templates.get(i)).print(ind);
    } 
  }

  /**
   * Returns the requested <code>XSLAttributeSet</code>. All <code>XSLAttributeSet</code>
   * instances are kept in a <code>Hashtable</code> owned by the stylesheet.
   *
   * @param   name  the <code>XSLAttributeSet</code> which is requested
   * @return        the requested <code>XSLAttributeSet</code> or <code>null</code
   *                if it wasn't found
   */
  protected XSLAttributeSet getAttributeSet(String name) {
    return (XSLAttributeSet) attributeSets.get(name);
  }

  /**
   * Determines if the node specified in this context must be filtered out (for text nodes).
   * The definitions for <code>white-space-preserving</code> and <code>white-space-stripping</code>
   * nodes are checked to see, if the content of this node (if it is a white-space node) should
   * be processed.
   *
   * @param   xcont  the <code>XPathContext</code> identifying the node
   * @return         a boolean specifying if the node must be stripped(true) or not(false)
   */
  protected boolean doStripWhiteSpaceNode(XPathContext xcont) {
    return doStripWhiteSpaceNode(xcont, xcont.node);
  }

  public boolean doStripWhiteSpaceNode(XPathContext xcont, int node) {
    //    LogWriter.getSystemLogWriter().println("XSLStylesheer.doStripWhiyeSpaceNode:, count=" + iWhiteSpaceElementsCount +", id="+ hashCode());
    iWhiteSpaceElementsCount = whiteSpaceElements.size() - 1;
    if (iWhiteSpaceElementsCount == -1) {
      return false;
    }
    if (xcont.dtm.nodeType[node] != org.w3c.dom.Node.TEXT_NODE) {
      return false;
    }
    int p = xcont.dtm.parent[node];
    CharArray parentname = xcont.dtm.name[p].rawname;
    WhiteSpaceElement ws = null;

    for (int i = iWhiteSpaceElementsCount; i >= 0; i--) {
      ws = (WhiteSpaceElement) whiteSpaceElements.get(i);

      if (ws.match(parentname)) {
        if (!ws.isPreserve()) {
          if (xcont.dtm.getStringValue(node).isWhitespace()) {
            return true;
          } else {
            return false;
          }
        } else {
          return false;
        }
      }
    } 

    return false;
  }

  /**
   * Processes the stylesheet to the specified output target.
   * Call this method to invoke the transformation.
   *
   * @param   output  specifies the output target. Can be one of the follwing:
   *                  <ul><li>a filename specified as a string</li>
   *                      <li>an <code>OutputStream</code>. The stream isn't closed at the and of the process</li>
   *                      <li>any implemenation of the <code>com.sap.engine.lib.xml.parser.DocHandler</code></li>
   *                  </ul>
   *                  Many other output options are supported through the JAXP Framework
   * @exception   XSLException  if the transformation could not be completed
   */
  public void process(Object output) throws XSLException {
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process(object) >>>>>>>>>>>>>>>>>>>>>>>>>>>> :" + output);
    int nodes[] = new int[1];
    nodes[0] = 0;
    xfKeyHash.clear();
    xpath.initializeProcess();
    //xfactProcess = new XObjectFactory(5000);
    xfactProcess.releaseAllPools();
    crPoolXSLElement.releaseAllObjects();
    getDTM().setFactories(xfactProcess, null);
    xslVarEvalPool = new XSLVarEvalPool(100, 100);
    hashTablesPool.releaseAllObjects();
    //initializeKeysOldDocNodes = -1;
    initializeKeysDocumentNode = -1;
    xpath.reuse(dtm);
    startDTMSize = dtm.size;
    startDTMDocumentNodesSize = dtm.documentNodesCount;
    
    XPathContext xpc = dtm.getInitialContext(this);
    xpc.owner = this;
    libraryManager = new LibraryManager(this);
    //-------- register stylesheet functions within the library manager
    try {
      Enumeration functionURIs = stylesheetFunctions.keys();

      while (functionURIs.hasMoreElements()) {
        String nextURI = (String) functionURIs.nextElement();
        //LogWriter.getSystemLogWriter().println("XSLStylesheet.process Registring stylesheet library: " + nextURI);
        libraryManager.registerLibrary(new StylesheetFunctionLibrary(nextURI, (Vector) stylesheetFunctions.get(nextURI)));
      }
    } catch (ClassNotFoundException e) {
      throw new XSLException("problem registring stylesheet function library ", e);
    }
    xpc.library = libraryManager;
    boolean throwExc = true;

    try {
      initializeKeys(xpc);  
    } catch (Exception e) {
      //$JL-EXC$
      // the key initialization is attempted again after the variable initializations
    }
    
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process:  >>>>>>>> vTopVariablesToProcessSize=" + vTopVariablesToProcess.size() );
    for (int k = 0; vTopVariablesToProcess.size() > 0;) {
      if (k == vTopVariablesToProcess.size()) {
        throwExc = true;
      } else {
        throwExc = false;
      }

      k = vTopVariablesToProcess.size();
      //      LogWriter.getSystemLogWriter().println("XSLStylesheet.process:  >>>>>>>> k =" + k );
      try {
        for (int i = 0; i < vTopVariablesToProcess.size(); i++) {
          XSLVariable xv = (XSLVariable) vTopVariablesToProcess.get(i);

          //LogWriter.getSystemLogWriter().println("XSLStyleshet.include: process TOP var: " + xv.getName() + ". " + vTopVariablesToProcess.size());
          //          LogWriter.getSystemLogWriter().println("XSLStyleshet.process: processing var: " + xv.getName() + ", dochanmdler= "+ getOutputProcessor().getDocHandler());
          if (parameters != null && parameters.get(xv.getName()) != null) {
            String key = xv.getName();
            Object value = parameters.get(key);

            //        LogWriter.getSystemLogWriter().println("-----------------------Param:" + key + " = " + value.getClass());
            //XSLVarEval xv = null;
            XObject varValue;
            if (value instanceof String) {
              //          LogWriter.getSystemLogWriter().println("-----------------------Param:" + key + " = " + value);
              varValue = xfactProcess.getXString((String) value);
            } else {
              //LogWriter.getSystemLogWriter().println("----------------------------Setting parameter to stylesheet:" + key + " of class:" + value.hashCode());
              //LogWriter.getSystemLogWriter().println("XSLStylesheet.process: Searchin library for class:" + value.getClass().getName());
              JLBLibrary lb = libraryManager.getByClassName(value.getClass().getName());

              if (lb != null) {
                //            LogWriter.getSystemLogWriter().println("XSLStylesheet.process: lb is NOT NULL");
                lb.setUserClass(value.getClass());
              }

              varValue = xfactProcess.getXJavaObject(value);
            }
            varValue.setTopVariableValue(true);
            xv = new XSLVarEval(key, varValue);
          } else {
            xv.processStart();
            xv.process(xpc, 0);
            xv.processEnd();
          }

          vTopVariablesToProcessSave.add(vTopVariablesToProcess.remove(i));
          i--;
          setVariable(xv.getName(), xv);
        } 
      } catch (Exception e) {
        //$JL-EXC$
        // The logic of this code is to throw exceptions, until all variables are initialized
        // if in a run - nothing gets initialized - throw an exception
        Object x = vTopVariablesToProcess.remove(0);
        vTopVariablesToProcess.add(x);
        getOutputProcessor().clearAttributeValueProcessing();

        if (throwExc) {
          throw new XSLException("", e);
        }
      }
    } 
    
    initializeKeys(xpc);  

    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process: 3");

    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process: 4");
    setOutput(output);
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process: 5");
    //
    //    iWhiteSpaceElementsCount = whiteSpaceElements.size() -1;
    xslOutputProcessor.startDocument();
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process: 6");
    try {
      process(xpc, nodes, 0, 1, new Vector(), "");
      xslOutputProcessor.endDocument();
    } finally {
      /*
       * CSN:  0000956395 2007  :  OutOfMemoryError after large message 
       * After transformation of a big message, the DTM remains intialized and cannot be garbage collected. The solution is to delete the
       * DTM instance after each transformation. 
       */
      dtm.size = startDTMSize;
      dtm.documentNodesCount = startDTMDocumentNodesSize;
      dtm.xfDocumentCache.clear();
      dtm.trim();
      Object o = vTopVariablesToProcess;
      vTopVariablesToProcess = vTopVariablesToProcessSave;
      vTopVariablesToProcessSave = (Vector) o;
      vTopVariablesToProcessSave.clear();
      xpath.finalizeLocal();
      xfactProcess.releaseAllPools();
    }
    
  }

  /**
   * Processes a specific <code>XPathContext</code>. This method does the actual transformation.
   * It processes the supplied list of nodes, identifies which template matches the current context
   * and then instantiates the template. If there are any parameter they are passed to the template.
   *
   * @param   xcont    the context which identifies the current node, which should be matched
   *                   against the declared templates
   * @param   nodes    an array specifying the nodes which should be processed. It is not nessessary
   *                   that the first node is at position 0, since the begin and count of the nodes
   *                   in the array is specified by the <code>beg</code> and
   *                   <code>len</code> parameters
   * @param   beg      the index in the <code>nodes</code> array where the first node
   *                   is located
   * @param   len      the count of nodes
   * @param   params   a <code>Vector</code> of <code>XSLVariable</code> objects, which
   *                   represent the parameters, which should be supplied to the template
   *                   to be instantiated
   * @param   mode     the current processing mode
   * @exception   XSLException  if the transformation could not be completed
   */
  protected void process(XPathContext xcont, int[] nodes, int beg, int len, Vector params, String mode) throws XSLException {
    process(xcont, nodes, beg, len, params, mode, null);
  }
  protected void process(XPathContext xcont, int[] nodes, int beg, int len, Vector params, String mode, ETObject select) throws XSLException {
    XPathContext x2 = new XPathContext();
//    int node;
//    int lastmatch = -1;
//    double lastprior = Double.MIN_VALUE;
    if (mode == null) {
      mode = "";
    }

    for (int k = 0; k < len; k++) {
      x2.reuse(xcont, nodes[k + beg], k + 1, len);

      for (int i = vTemplates.length - 1; i >= 0; i--) {
        XSLTemplateHeader tmpl = (XSLTemplateHeader) vTemplates[i];
        //        LogWriter.getSystemLogWriter().println("Matching header: " + tmpl.match.squery + "   template: " +
        //            tmpl.template.match.squery + "  for node: " + x2.dtm.name[x2.node] + "  # " + x2.node + ", i=" + i);//        if (x2.node == 2 && tmpl.match.squery.equals("NavigationEntry[@status=\'active\']")) {
        //          LogWriter.getSystemLogWriter().println("---------------------------------------------------------------here");
        //        }
        if (doStripWhiteSpaceNode(x2)) {
          continue;
        }

        if (!tmpl.bMatchable) {
          continue;
        } else if ((tmpl.sMode.equals(mode) || tmpl.bIsDefault) && xpath.match(tmpl.match, x2, mode, select)) {
//                    LogWriter.getSystemLogWriter().println(">>>>>>>>>>>>>>>>>. Processing header: " + tmpl.match.squery + "   template: " +
          //             tmpl.template.match.squery + "  for node: " + x2.dtm.name[x2.node] + "  # " + x2.node);
          
          XSLTemplate template = tmpl.template;
          template.processStart();
          template.process(x2, x2.node, params);
          template.processEnd();
          break;
        }
      } 
    } 
  }

  /**
   * Replaces every '/' or '\' in a path string to <code>File.separator</code>
   *
   * @param   path  the string to be transformed
   * @return        the resulting string
   */
  public synchronized static String mkPath(String path) {
    return path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
  }

  /**
   * Initializes the stylesheet from a file, containig the stylesheet definition.
   * The import level is used when importing from inside a stylesheed and
   * is incremented in each next level stylesheet in order to keep the
   * import precedence
   *
   * @param   xsldoc        a String specifying the xsl file
   * @param   importLevel   the import level. This is 0 for the initial stylesheet
   * @exception   XSLException
   */
  public void init(String xsldoc, URIResolver uriResolver, int importLevel) throws XSLException {
    //    LogWriter.getSystemLogWriter().println("init(string): " + xsldoc);
//    URL url;
    Source src = null;
    try {
      if (uriResolver == null) {
        uriResolver = new URLLoader();
      }

      src = uriResolver.resolve(xsldoc, baseURI);
      
      if (src != null && src.getSystemId() == null && src != null) {
        src.setSystemId(xsldoc);
      }
    } catch (Exception e) {
      throw new XSLException("Could not load stylesheet from: " + xsldoc, e);
    }
    //    LogWriter.getSystemLogWriter().println("init(string): src=" + src);
    //URL url = urlLoader.load(xsldoc);
    String a = xsldoc;
    int index = 0;

    if (a.toLowerCase(Locale.ENGLISH).startsWith("file:")) {
      index = 5;
    }

    while (a.charAt(index) == '/') {
      index++;
    }

    if (a.length() > index + 1) {
      if (a.charAt(index + 1) != ':') {
        index--;
      }
    }

    a = a.substring(index);
    //
    a = mkPath(a);
    baseURI = a.substring(0, a.lastIndexOf(File.separator) + 1);
    init(src, uriResolver, importLevel);
  }

  /**
   * Initializes the stylesheet form a <code>javax.transform.Source</code> Object
   *
   * @param   src          the stylesheet source
   * @param   importLevel  @see init(String, int)
   * @exception   XSLException  if there was some error while reading the stylesheet
   */
  public void init(Source src, URIResolver uriResolver, int importLevel) throws XSLException {
    //    LogWriter.getSystemLogWriter().println("init(source): " +  src);
    if (src.getSystemId() != null) {
      //      baseURI = new File(src.getSystemId()).getPath();
      // required by CRM the other method cannot resolve \\xxxx\xx.xml uris
      baseURI = src.getSystemId();
    }

    if (src instanceof StreamSource) {
      Reader r = ((StreamSource) src).getReader();
      InputStream in = ((StreamSource) src).getInputStream();
      if (r != null) {
        init(r, uriResolver, importLevel);
      } else if (in != null) {
        init(in, src.getSystemId(), uriResolver, importLevel);
      } else {
        init(((StreamSource) src).getSystemId(), uriResolver, importLevel);
      }
    } else if (src instanceof DOMSource) {
      init(((DOMSource) src).getNode(), uriResolver, importLevel);
    } else if (src instanceof SAXSource) {
      throw new XSLException("SAXSource is not supported for loading stylesheets");
    } else {
      throw new XSLException("This type of source is not supported for loading stylesheets: " + src);
    }
  }

  /**
   * Initializes the stylesheet form an <code>InputStream</code>
   *
   * @param   in            the <code>InputStream</code> with the stylesheet
   * @param   importLevel  @see init(String, int)
   * @exception   XSLException  if there was some error while reading the stylesheet
   */
  public void init(InputStream in, String systemID, URIResolver uriResolver, int importLevel) throws XSLException {
    // ! Copy-pasted from above
    Document doc = null;
    try {
      DOMParser parser = new DOMParser();
      parser.setFeature("http://inqmy.org/dom/features/trim-white-spaces", false);
      parser.setFeature("http://xml.org/sax/features/namespaces", true);
      parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      InputSource source = new InputSource(in);
      if (systemID != null) {
        source.setSystemId(systemID);
      }
      doc = parser.parse(source);
    } catch (Exception e) {
      throw new XSLException("Exception while parsing stylesheet", e);
    }
    init(doc.getDocumentElement(), uriResolver, importLevel);
  }
  
  public void init(Reader r, URIResolver uriResolver, int importLevel) throws XSLException {
    // ! Copy-pasted from above
    Document doc = null;
    try {
      DOMParser parser = new DOMParser();
      parser.setFeature("http://inqmy.org/dom/features/trim-white-spaces", false);
      parser.setFeature("http://xml.org/sax/features/namespaces", true);
      parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      doc = parser.parse(new InputSource(r));
    } catch (Exception e) {
      throw new XSLException("Exception while parsing stylesheet", e);
    }
    init(doc.getDocumentElement(), uriResolver, importLevel);
  }  

  /**
   * Initializes the stylesheet form a <code>org.w3c.dom.Node</code>. If this is a
   * <code>org.w3c.dom.Document</code>, then the root-element is got and processed, and if it is
   * a <code>org.w3c.dom.Element</code>, then it is assumed that this element is holding the
   * stylesheet and is processed. (It is not nessessary that its node name is 'xsl:stylesheet'
   * or 'xsl:transform', because the stylesheet could be inside the document.
   *
   * @param   nd           the input node
   * @param   importLevel  @see init(String, int)
   * @exception   XSLException  if there was some error while reading the stylesheet
   */
  public void init(Node nd, URIResolver uriResolver, int importLevel) throws XSLException {
    if (nd.getNodeType() == Node.DOCUMENT_NODE) {
      init((Document) nd, uriResolver, importLevel);
    } else if (nd.getNodeType() == Node.ELEMENT_NODE) {
      init((Element) nd, uriResolver, importLevel);
    } else {
      throw new XSLException("Cannot initialize  stylesheet with a not Document or Element node");
    }
  }

  /**
   * @see init(Node, int)
   */
  public void init(Document doc, URIResolver uriResolver, int importLevel) throws XSLException {
    init(doc.getDocumentElement(), uriResolver, importLevel);
  }

  /**
   * @see init(Node, int)
   */
  public void init(Element root, URIResolver userURIResolver, int il) throws XSLException {
//    tree = root;
    xpath.initializeProcess();
    decimalFormats.clear();
    attributeSets.clear();
    variables.clear();
    importLevel = il;
    currentTemplatePosition = 0;
    whiteSpaceElements.clear();
    vTemplateHeaders.clear();
    nsmanager = new NamespaceManager();
    //xslNsmanager = new NamespaceManager();
    namespaceHandler = new NamespaceHandler(null);
    bIsIndentAllowed = true;
    libraryManager.reuse();
    etBuilder.setNamespaceStuff(nsmanager, namespaceHandler);
    etBuilder.setParentStylesheet(this);
    templates.clear();
    xslOutputProcessor.reuse();
//    int importPrecedence = 0;
    xfactProcess.releaseAllPools();
    namespaceAliases.clear();
    //qnamePool.releaseAllObjects();
    qnameTable.clear();

    //    xfactMatch.releaseAllPools();
    if (userURIResolver != null) {
      uriResolver = userURIResolver;
    } else {
      uriResolver = new URLLoader();
    }

    namespaceHandler.findNamespaceNodes(root, nsmanager);
    namespaceHandler.levelUp();
    vTopVariablesToProcess.clear();
    if (parameters != null) {
      parameters.clear();
    }
    keyValuesArrays.clear();
    nodesetArrays.clear();
    keyArrays.clear();
    NodeList nl = root.getChildNodes();

    if (root.getNamespaceURI() == null || !root.getNamespaceURI().equals(sXSLNamespace)) {
      //if (!root.getNodeName().equals("xsl:stylesheet") && !root.getNodeName().equals("xsl:transform") ) {
      processLiteralResultElement(root);
    } else if (!root.getLocalName().equals("stylesheet") && !root.getLocalName().equals("transform")) {
      throw new XSLException("Root element of stylesheet must be either 'transform' or 'stylesheet' in the xsl namespace - " + sXSLNamespace);
    } else {
      for (int i = 0; i < nl.getLength(); i++) {
        if (nl.item(i).getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
          continue;
        }

        Element node = (Element) nl.item(i);
        //the namespace nodes of the template element are scanned inside the
        //XSLContentNode construction for compability with other nodes
        namespaceHandler.findNamespaceNodes(node, nsmanager);
        namespaceHandler.levelUp();

        if (node.getNamespaceURI() == null) {
          throw new XSLException("All top level elements in a stylesheet must be namespace qualified. Bad Element: " + node.getNodeName());
        }

        if (node.getNamespaceURI().equals(sXSLNamespace)) {
          String localName = node.getLocalName();

          if (localName.equals("import")) {
            processTopLevelImport(node);
          } else if (localName.equals("include")) {
            processTopLevelInclude(node);
          } else if (localName.equals("strip-space")) {
            processTopLevelStripSpace(node);
          } else if (localName.equals("preserve-space")) {
            processTopLevelPreserveSpace(node);
          } else if (localName.equals("output")) {
            processTopLevelOutput(node);
          } else if (localName.equals("key")) {
            processTopLevelKey(node);
          } else if (localName.equals("decimal-format")) {
            processTopLevelDecimalFormat(node);
          } else if (localName.equals("namespace-alias")) {
            processTopLevelNamespaceAlias(node);
          } else if (localName.equals("attribute-set")) {
            processTopLevelAttributeSet(node);
          } else if (localName.equals("variable")) {
            processTopLevelVariable(node);
          } else if (localName.equals("param")) {
            processTopLevelParam(node);
          } else if (localName.equals("template")) {
            //LogWriter.getSystemLogWriter().println("XSLStylesheet, found template:" + node);
            processTopLevelTemplate(node);
          } else if (localName.equals("script")) {
            processTopLevelScript(node);
          } else if (localName.equals("message")) {
            if (node.getAttribute("terminate").equalsIgnoreCase("yes")) {
              LogWriter.getSystemLogWriter().println("Received xsl:message with terminate = yes"); //$JL-SYS_OUT_ERR$
              System.exit(0);
            }
          } else if (localName.equals("function")) {
            processTopLevelFunction(node);
          } else {
            throw new XSLException("Error: bad toplevel element int the XSLT Namespace: " + node.getNodeName());
          }
        } else {
//          throw new XSLException("Error: Top level is in an unrecognized namespace: " + node.getNodeName() + ", namespace: " + node.getNamespaceURI());
          XSLTemplate xsltemp = new XSLTemplate(this, node, Integer.MIN_VALUE, Integer.MIN_VALUE);
          templates.add(xsltemp);
        }

        try {
          namespaceHandler.levelDown();
        } catch (Exception e) {
          throw new XSLException("Exception while processing namespaces", e);
        }
      } 
    }

    try {
      namespaceHandler.levelDown();
    } catch (Exception e) {
      throw new XSLException("Exception while processing namespaces for the root node of the stylesheet.", e);
    }
    //    try {
    //      libraryManager.registerLibrary(new StylesheetFunctionLibrary(stylesheetFunctions));        
    //    } catch(ClassNotFoundException e) {
    //      throw new XSLException("problem registring stylesheet function library ", e );
    //    }
    //skip non-relevan initializations in the case of styleshhet used for including
    if (bIncludeUse == true) {
      return;
    }
    XSLTemplate xsltemp = new XSLTemplate(this, "*|/", "", Double.NEGATIVE_INFINITY + 1, "", true, Integer.MIN_VALUE, Integer.MIN_VALUE);
    xsltemp.append(new XSLApplyTemplates(this, xsltemp, "", ""));
    templates.add(xsltemp);
    xsltemp = new XSLTemplate(this, "text()", "", Double.NEGATIVE_INFINITY + 1, "", true, Integer.MIN_VALUE, Integer.MIN_VALUE);
    xsltemp.append(new XSLValueOf(this, xsltemp, "."));
    templates.add(xsltemp);
    xsltemp = new XSLTemplate(this, "@*", "", Double.NEGATIVE_INFINITY + 1, "", true, Integer.MIN_VALUE, Integer.MIN_VALUE);
    xsltemp.append(new XSLValueOf(this, xsltemp, "."));
    templates.add(xsltemp);
    xsltemp = new XSLTemplate(this, "processing-instruction()", "", Double.NEGATIVE_INFINITY + 1, "", true, Integer.MIN_VALUE, Integer.MIN_VALUE);
    templates.add(xsltemp);
    xsltemp = new XSLTemplate(this, "comment()", "", Double.NEGATIVE_INFINITY + 1, "", true, Integer.MIN_VALUE, Integer.MIN_VALUE);
    templates.add(xsltemp);
    makeTemplateHeaders(templates, vTemplateHeaders);
    vTemplates = new XSLTemplateHeader[vTemplateHeaders.size()];
    vTemplateHeaders.toArray(vTemplates);
    Arrays.sort(vTemplates);
    //    for (int g=0;g<vTemplates.length;g++) {
    //      LogWriter.getSystemLogWriter().println(vTemplates[g].sName);
    //    }
    //    LogWriter.getSystemLogWriter().println("Dumping Headers:");
    //    for (int k = 0; k<vTemplates.length; k++) {
    //      LogWriter.getSystemLogWriter().println(vTemplates[k]);
    //    }
    //print();
  }

  /**
   * Takes a <code>Vector</code> of <code>XSLTemplate</code> objects, and checks their
   * <code>match</code> attributes, to see if there are expresstions separated by '|'
   * and for every expression which does not contain a '|' a new <code>XSLTemplateHeader</code>
   * wrapper is created, and then when indentifying the priority of the templates, these wrappers
   * are used
   *
   * @param   templates
   * @param   vTemplateHeaders
   * @exception   XSLException
   */
  private void makeTemplateHeaders(Vector templates, Vector vTemplateHeaders) throws XSLException {
    String s = null;
    ETItem et = null;
    Vector vETItem = new Vector();
    vTemplateHeaders.clear();

    for (int i = 0; i < templates.size(); i++) {
      XSLTemplate templ = (XSLTemplate) templates.get(i);
      s = templ.match.squery;
      et = templ.match.et;
      vETItem.clear();

      //      if (et instanceof ETFunction) {
      //        LogWriter.getSystemLogWriter().println("XSLStylesheet: funcrion name:" + ((ETFunction)et).getName() + "  " + ((ETFunction)et).getName().localname.equals("|"));
      //      }
      while (et instanceof ETFunction && ((ETFunction) et).getName().localname.equals("|")) {
        vETItem.add(0, ((ETFunction) et).getArg(1));
        et = ((ETFunction) et).getArg(0);
      }

      if (vETItem.size() > 0) {
        vETItem.add(0, et);
      }

      char ch;
      boolean bInQuotes = false;
      int openBrackets = 0;
      int tokens = 0;
      int laststroke = 0;
      int r = 0;

      for (r = 0; r < s.length(); r++) {
        ch = s.charAt(r);

        if (ch == '(' && !bInQuotes) {
          openBrackets++;
        } else if (ch == ')' && !bInQuotes) {
          openBrackets--;
        } else if (ch == '\'' || ch == '\"') {
          bInQuotes = !bInQuotes;
        } else if (ch == '|' && !bInQuotes && openBrackets == 0) {
          ETObject etobj = new ETObject(s.substring(laststroke, r), (ETItem) vETItem.get(tokens));
          vTemplateHeaders.add(new XSLTemplateHeader(etobj, templ, vTemplateHeaders.size()));
          laststroke = r + 1;
          tokens++;
        }
      } 

      String laststr = s.substring(laststroke, r);
      ETObject etobj = null;

      if (vETItem.size() > 0) {
        etobj = new ETObject(laststr, (ETItem) vETItem.get(tokens));
      } else {
        etobj = new ETObject(laststr, templ.match.et);
      }

      vTemplateHeaders.add(new XSLTemplateHeader(etobj, templ, vTemplateHeaders.size()));
    } 
  }

  public void processLiteralResultElement(Element el) throws XSLException {
    XSLTemplate xsltemp = new XSLTemplate(this, "/", "", 0, "", true, Integer.MIN_VALUE, Integer.MIN_VALUE);
    xsltemp.scanNode(el);
    templates.add(xsltemp);
    makeTemplateHeaders(templates, vTemplateHeaders);
    vTemplates = new XSLTemplateHeader[vTemplateHeaders.size()];
    vTemplateHeaders.toArray(vTemplates);
    Arrays.sort(vTemplates);
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelTemplate(Element el) throws XSLException {
    templates.add(new XSLTemplate(this, el, importLevel, currentTemplatePosition++));
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelImport(Element el) throws XSLException {
    String uri = el.getAttribute("href");
    importedStylesheet = InstanceManager.getXSLStylesheet();
    try {
      Source source = uriResolver.resolve(uri, baseURI);
      if (source != null && source.getSystemId() == null && uri != null) {
        source.setSystemId(uri);
      }

      importedStylesheet.init(source, uriResolver, importLevel - 1);
    } catch (TransformerException exc) {
      throw new XSLException("Could not resolve: '" + uri + "', base: '" + baseURI + "'", exc);
    }
    Vector v = importedStylesheet.getTemplates();
    int tempstart = templates.size() - 1;

    for (int i = tempstart; i >= 0; i--) {
      XSLTemplate told = (XSLTemplate) templates.get(i);

      for (int j = 0; j < v.size(); j++) {
        XSLTemplate timp = (XSLTemplate) v.get(j);

        if (timp.isSameMatch(told)) {
          timp.setOverloadByMatch(told);
        }

        if (timp.isSameName(told)) {
          timp.setOverloadByName(told);
        }
      } 
    } 

    for (int i = 0; i < v.size(); i++) {
      templates.add(((XSLNode) v.get(i)).changeOwner(this));
    } 

    Enumeration importedVars = importedStylesheet.getVariables().elements();

    for (int i = 0; importedVars.hasMoreElements(); i++) {
      XSLVariable xv = (XSLVariable) importedVars.nextElement();
      setVariable(xv.getName(), (XSLVariable) ((XSLNode) xv).changeOwner(this));
    } 

    Vector importedTopVars = importedStylesheet.getTopVariablesToProcess();

    for (int i = 0; i < importedTopVars.size(); i++) {
      XSLVariable xv = (XSLVariable) importedTopVars.get(i);
      vTopVariablesToProcess.add((XSLVariable) ((XSLNode) xv).changeOwner(this));
    } 

    Enumeration imortedDecimalFormats = importedStylesheet.decimalFormats.keys();

    while (imortedDecimalFormats.hasMoreElements()) {
      String key = (String) imortedDecimalFormats.nextElement();

      if (!decimalFormats.containsKey(key)) {
        decimalFormats.put(key, importedStylesheet.decimalFormats.get(key));
      }
    }

    Enumeration importedAttributeSets = importedStylesheet.attributeSets.elements();

    while (importedAttributeSets.hasMoreElements()) {
      XSLAttributeSet set = (XSLAttributeSet) importedAttributeSets.nextElement();
      attributeSets.put(set.getName(), (XSLAttributeSet) set.changeOwner(this));
    }

    Enumeration importedKeys = importedStylesheet.keyArrays.keys();

    while (importedKeys.hasMoreElements()) {
      CharArray keyname = (CharArray) importedKeys.nextElement();
      XSLKey[] imported = (XSLKey[]) importedStylesheet.keyArrays.get(keyname);

      if (keyArrays.containsKey(keyname)) {
        XSLKey[] current = (XSLKey[]) keyArrays.get(keyname);
        XSLKey[] newKeys = new XSLKey[current.length + imported.length];
        System.arraycopy(current, 0, newKeys, 0, current.length);
        System.arraycopy(imported, 0, newKeys, current.length, imported.length);
      } else {
        keyArrays.put(new CharArray(keyname), imported);
      }
    }

    Enumeration importedFunctionNames = importedStylesheet.stylesheetFunctions.keys();

    while (importedFunctionNames.hasMoreElements()) {
      String funcUri = (String) importedFunctionNames.nextElement();
      Vector next = (Vector) importedStylesheet.stylesheetFunctions.get(funcUri);

      if (stylesheetFunctions.containsKey(funcUri)) {
        //Vector current = stylesheetFunctions.get(funcUri);
        ((Vector) stylesheetFunctions.get(funcUri)).addAll(next);
      } else {
        stylesheetFunctions.put(funcUri, next);
      }
    }
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelInclude(Element el) throws XSLException {
    String uri = el.getAttribute("href");

    //    LogWriter.getSystemLogWriter().println("XSLStyleshet.include: including: " + uri + ", base=" + baseURI + ", id=" + hashCode());
    if (importedStylesheet == null) {
      importedStylesheet = InstanceManager.getXSLStylesheet();
    }

    try {
      Source src = uriResolver.resolve(uri, baseURI);
      if (src != null && src.getSystemId() == null && uri != null) {
         src.setSystemId(uri);
      }

      if (src instanceof StreamSource) {
        StreamSource ss = (StreamSource) src;

        if (ss.getInputStream() == null && ss.getReader() == null && ss.getSystemId() != null) {
          ss.setInputStream(new FileInputStream(ss.getSystemId()));
        }
      }

      importedStylesheet.setIncludeUse(true);
      importedStylesheet.init(src, uriResolver, importLevel);
      importedStylesheet.setIncludeUse(false);
    } catch (TransformerException exc) {
      throw new XSLException("Could not resolve: '" + uri + "' base: '" + baseURI + "'", exc);
    } catch (IOException e) {
      throw new XSLException("Could not resolve: '" + uri + "' base: '" + baseURI + "'", e);
    }
    Vector v = importedStylesheet.getTemplates();

    for (int i = 0; i < v.size(); i++) {
      if (((XSLTemplate) v.get(i)).isDefault()) {
        continue;
      }
      templates.add(((XSLNode) v.get(i)).changeOwner(this));
    } 

    Enumeration importedVars = importedStylesheet.getVariables().elements();

    for (int i = 0; importedVars.hasMoreElements(); i++) {
      XSLVariable xv = (XSLVariable) importedVars.nextElement();
      setVariable(xv.getName(), (XSLVariable) ((XSLNode) xv).changeOwner(this));
    } 

    Vector importedTopVars = importedStylesheet.getTopVariablesToProcess();

    for (int i = 0; i < importedTopVars.size(); i++) {
      XSLVariable xv = (XSLVariable) importedTopVars.get(i);
      vTopVariablesToProcess.add((XSLVariable) ((XSLNode) xv).changeOwner(this));
    } 

    Enumeration imortedDecimalFormats = importedStylesheet.decimalFormats.keys();

    while (imortedDecimalFormats.hasMoreElements()) {
      String key = (String) imortedDecimalFormats.nextElement();

      if (!decimalFormats.containsKey(key)) {
        decimalFormats.put(key, importedStylesheet.decimalFormats.get(key));
      }
    }

    Enumeration importedKeys = importedStylesheet.keyArrays.keys();

    while (importedKeys.hasMoreElements()) {
      CharArray keyname = (CharArray) importedKeys.nextElement();
      XSLKey[] imported = (XSLKey[]) importedStylesheet.keyArrays.get(keyname);

      if (keyArrays.containsKey(keyname)) {
        XSLKey[] current = (XSLKey[]) keyArrays.get(keyname);
        XSLKey[] newKeys = new XSLKey[current.length + imported.length];
        System.arraycopy(current, 0, newKeys, 0, current.length);
        System.arraycopy(imported, 0, newKeys, current.length, imported.length);
      } else {
        keyArrays.put(new CharArray(keyname), imported);
      }
    }

    Enumeration importedAttributeSets = importedStylesheet.attributeSets.elements();

    while (importedAttributeSets.hasMoreElements()) {
      XSLAttributeSet set = (XSLAttributeSet) importedAttributeSets.nextElement();
      attributeSets.put(set.getName(), (XSLAttributeSet) set.changeOwner(this));
    }

    Enumeration importedFunctionNames = importedStylesheet.stylesheetFunctions.keys();

    while (importedFunctionNames.hasMoreElements()) {
      String funcUri = (String) importedFunctionNames.nextElement();
      Vector next = (Vector) importedStylesheet.stylesheetFunctions.get(funcUri);

      if (stylesheetFunctions.containsKey(funcUri)) {
        //Vector current = stylesheetFunctions.get(funcUri);
        ((Vector) stylesheetFunctions.get(funcUri)).addAll(next);
      } else {
        stylesheetFunctions.put(funcUri, next);
      }
    }
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelStripSpace(Element el) throws XSLException {
    processWhiteSpaceElement(el.getAttribute("elements"), false);
  }

  /**
   *
   *
   * @param   elements
   * @param   preserve
   */
  public void processWhiteSpaceElement(String elements, boolean preserve) {
    StringTokenizer tok = new StringTokenizer(elements, "\040\012\015\011");

    while (tok.hasMoreElements()) {
      whiteSpaceElements.add(new WhiteSpaceElement(tok.nextToken(), preserve));
    }
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelPreserveSpace(Element el) throws XSLException {
    processWhiteSpaceElement(el.getAttribute("elements"), true);
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelOutput(Element el) throws XSLException {
    xslOutputNode = new XSLOutputNode(this, el);
    getOutputProcessor().setOutputMode(xslOutputNode);
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelKey(Element el) throws XSLException {
    XSLKey key = new XSLKey(this, el);
    CharArray keyName = new CharArray(key.getName());
    
    if (!keyArrays.containsKey(keyName)) {
      keyArrays.put(keyName, new XSLKey[] {key});
    } else {
      XSLKey[] oldKeys = (XSLKey[]) keyArrays.get(keyName);
      XSLKey[] newKeys = new XSLKey[oldKeys.length + 1];
      System.arraycopy(oldKeys, 0, newKeys, 0, oldKeys.length);
      keyArrays.put(keyName, newKeys);
    }
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelFunction(Element el) throws XSLException {
    //LogWriter.getSystemLogWriter().println("XSLSrylesheet: Processing top level function.");
    //String uri = el.getNamespaceURI();
    String name = el.getAttribute("name");
    int colonIndex = name.indexOf(':');

    if (colonIndex < 0) {
      throw new XSLException("Stylesheet functions must have namespace-qualified names.");
    }

    String prefix = name.substring(0, name.indexOf(':'));
    String uri = namespaceHandler.get(new CharArray(prefix)).toString();
    //String uri = com.sap.engine.lib.xml.dom.DOM.prefixToURI(name, el);
    //    String uri = (String)(com.sap.engine.lib.xml.dom.DOM.getNamespaceMappingsInScopeSpecial(el)).get(name);
    //
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.processTopLevelFunction: prefix = " + prefix);     
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.processTopLevelFunction: uri = " + uri);     
    Vector related = (Vector) stylesheetFunctions.get(uri);

    if (related == null) {
      related = new Vector();
      stylesheetFunctions.put(uri, related);
    }

    XSLFunction func = new XSLFunction(this, el);
    related.add(func);
    //stylesheetFunctions.add(func);
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process stylesheet functions were: " + stylesheetFunctions);
    //    stylesheetFunctions.put(uri, related);
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.process stylesheet functions are: " + stylesheetFunctions);
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelDecimalFormat(Element el) throws XSLException {
    DecimalFormat dform = new DecimalFormat();
    DecimalFormatSymbols df = new DecimalFormatSymbols();
    //if (el.getAttribute("name").length() > 0) df.setName(el.getAttribute("name"));
    if (el.getAttribute("infinity").length() > 0) {
      df.setInfinity(el.getAttribute("infinity"));
    }
    if (el.getAttribute("NaN").length() > 0) {
      df.setNaN(el.getAttribute("NaN"));
    }
    if (el.getAttribute("decimal-separator").length() > 0) {
      df.setDecimalSeparator(el.getAttribute("decimal-separator").charAt(0));
    }
    if (el.getAttribute("grouping-separator").length() > 0) {
      df.setGroupingSeparator(el.getAttribute("grouping-separator").charAt(0));
    }
    if (el.getAttribute("minus-sign").length() > 0) {
      df.setMinusSign(el.getAttribute("minus-sign").charAt(0));
    }
    if (el.getAttribute("precent").length() > 0) {
      df.setPercent(el.getAttribute("precent").charAt(0));
    }
    if (el.getAttribute("per-mille").length() > 0) {
      df.setPerMill(el.getAttribute("per-mille").charAt(0));
    }
    if (el.getAttribute("zero-digit").length() > 0) {
      df.setZeroDigit(el.getAttribute("zero-digit").charAt(0));
    }
    if (el.getAttribute("digit").length() > 0) {
      df.setDigit(el.getAttribute("digit").charAt(0));
    }
    if (el.getAttribute("pattern-separator").length() > 0) {
      df.setPatternSeparator(el.getAttribute("pattern-separator").charAt(0));
    }
    dform.setDecimalFormatSymbols(df);
    decimalFormats.put(el.getAttribute("name"), dform);
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelNamespaceAlias(Element el) throws XSLException {
    String stylesheetPrefix = el.getAttribute("stylesheet-prefix");
    //String stylesheetUri = namespaceHandler.get(stylesheetPrefix);
    String resultPrefix = el.getAttribute("result-prefix");
    //String resultUri = namespaceHandler.get(resultPrefix);
    namespaceAliases.put(stylesheetPrefix, resultPrefix);
    //throw new XSLException("not implemented");
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelAttributeSet(Element el) throws XSLException {
    XSLAttributeSet set = new XSLAttributeSet(this, el);
    attributeSets.put(set.getName(), set);
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelVariable(Element el) throws XSLException {
    vTopVariablesToProcess.add(new XSLTopVariable(this, el));
  }

  /**
   *
   *
   * @param   el
   * @exception   XSLException
   */
  public void processTopLevelParam(Element el) throws XSLException {
    vTopVariablesToProcess.add(new XSLTopVariable(this, el));
  }

  public void processTopLevelScript(Element el) throws XSLException {
    XSLScript xs = new XSLScript(el);
    try {
      if (xs.getLanguage().equals("java")) {
        //        LogWriter.getSystemLogWriter().println("XSLStylesheet.processTopLevelScript: prefix=" + xs.getPrefix() + ", ns = " + namespaceHandler.get(xs.getPrefix()) + ", src=" + xs.getSrc() +", archive="+ xs.getArchive());
        libraryManager.registerLibrary(new JLBLibrary(namespaceHandler.get(new CharArray(xs.getPrefix())).toString(), xs.getSrc(), xs.getArchive(), getExtClassLoader()));
      } else {
        libraryManager.registerLibrary(new BSFLibrary(namespaceHandler.get(new CharArray(xs.getPrefix())).toString(), xs.getLanguage(), xs.getData()));
      }
    } catch (ClassNotFoundException e) {
      throw new XSLException("Could not load class: " + xs.getSrc() + " required for extension library.", e);
    }
  }

  /**
   *
   *
   * @param   name
   * @return
   * @exception   XSLException
   */
  public XSLVariable getVariable(String name) throws XSLException {
    //    LogWriter.getSystemLogWriter().println("XSLStylesheet.getVariable() requesting: " + name + ", other is: " + variables.get("containerAtt"));
    XSLVariable xv = (XSLVariable) variables.get(name);
    if (xv == null) {
      throw new XSLException("Variable <" + name + "> undefined");
    }
    return xv;
  }

  /**
   *
   *
   * @param   name
   * @param   var
   */
  public void setVariable(String name, XSLVariable var) {
    variables.put(name, var);
  }

  /**
   *
   *
   * @param   name
   * @param   xcont
   * @param   params
   * @exception   XSLException
   * @exception   XPathException
   */
  public void callTemplate(String name, XPathContext xcont, Vector params) throws XSLException, XPathException {
    for (int i = vTemplates.length - 1; i >= 0; i--) {
      if (name.equals(vTemplates[i].sName)) {
        XSLTemplate template = vTemplates[i].template;
        template.processStart(); 
        template.process(xcont, xcont.node, params);
        template.processEnd(); 
        return;
      }
    } 

    throw new XSLException("Transformer: Could not find specified template in xsl:call-template \'" + name + "\'");
  }

  /**
   *
   *
   * @return
   */
  public XSLOutputProcessor getOutputProcessor() {
    return xslOutputProcessor;
  }

  /**
   *
   *
   * @param   o
   * @exception   XSLException
   */
  public void setOPByteStream(OutputStream o) throws XSLException {
    xslOutputProcessor.reuse();
    getOutputProcessor().setByteStream(o);
  }

  /**
   *
   *
   * @return
   */
  public XSLOutputNode getOutputProperties() {
    return xslOutputNode;
  }

  /**
   *
   *
   * @return
   */
  public String getBaseURI() {
    return baseURI;
  }

  public String getSourceBaseURI() {
    return sourceBaseURI;
  }

  public void setSourceBaseURI(String uri) {
    sourceBaseURI = uri;
  }

  /**
   *
   *
   * @return
   */
  public NamespaceManager getNamespaceManager() {
    return nsmanager;
  }

  /**
   *
   *
   * @param   output
   * @exception   XSLException
   */
  public void setOutput(Object output) throws XSLException {
    if (output == null) {
      throw new IllegalArgumentException("Output cannot be null.");
    }

    xslOutputProcessor.reuse();

    if (output instanceof String) {
      try {
        setOPByteStream(new FileOutputStream((String) output));
      } catch (FileNotFoundException e) {
        throw new XSLException("Exception while configuring output", e);
      }
    } else if (output instanceof OutputStream) {
      setOPByteStream((OutputStream) output);
    } else if (output instanceof DocHandler) {
      xslOutputProcessor.setDocHandler((DocHandler) output);
    } else {
      throw new IllegalArgumentException("Output through " + output.getClass().getName() + " not supported.");
    }
  }

  /**
   *
   *
   * @return
   */
  private Vector getTemplates() {
    return templates;
  }

  public LibraryManager getLibraryManager() {
    return libraryManager;
  }

  /**
   *
   *
   * @return
   * @exception   XSLException
   */
  public DocHandler getInputHandler() throws XSLException {
    getDTMFactory().initialize(getDTM(), nsmanager);
    return dtmFactory;
  }

  /**
   *
   *
   * @param   s
   * @return
   * @exception   XSLException
   */
  public DocHandler getInputHandler(String s) throws XSLException {
    getDTMFactory().approximateAndInitialize(getDTM(), nsmanager, s);
    return dtmFactory;
  }

  /**
   *
   *
   * @return
   * @exception   XSLException
   */
//  public XSLStylesheet cloneSheet() throws XSLException {
//    XSLStylesheet sheet = InstanceManager.getXSLStylesheet();
//    sheet.init(tree, uriResolver, 0);
//    return sheet;
//  }

  public void init(Node node, URIResolver uriResolver, String systemId) throws XSLException {
    //    try {
    if (systemId != null) {
      //baseURI = new File(systemId).getPath();
      // required by CRM the other method cannot resolve \\xxxx\xx.xml uris
      baseURI = systemId;
    }

    //    } catch (Exception e) {
    //      throw new XSLException("Could not resolve base uri: " + systemId);
    //    }
    //LogWriter.getSystemLogWriter().println(baseURI);
    init(node, uriResolver, 0);
  }

  /**
   *
   *
   * @param   node
   * @exception   XSLException
   */
  public void init(Node node, URIResolver uriResolver) throws XSLException {
    init(node, uriResolver, 0);
  }

  /**
   *
   *
   * @return
   */
  public DTM getDTM() {
    if (dtm == null) {
      dtm = new DTM();
    }
    return dtm;
  }

  public DTMFactory getDTMFactory() {
    if (dtmFactory == null) {
      dtmFactory = new DTMFactory();
    }
    return dtmFactory;
  }

  /**
   *
   *
   * @param   resolver
   */
  public void setURIResolver(URIResolver resolver) {
    this.uriResolver = resolver;
  }

  /**
   *
   *
   * @return
   */
  public URIResolver getURIResolver() {
    return uriResolver;
  }

  /**
   *
   *
   * @return
   */
  public URIResolver getDefaultResolver() {
    return defaultResolver;
  }

  /**
   *
   *
   * @param   uri
   * @exception   XSLException
   */
  public void setInputXML(String uri) throws XSLException {
    try {
      //Source src = uriResolver.resolve(uri, "");
      Source src = uriResolver.resolve(uri, null);

      if (!(src instanceof StreamSource)) {
        throw new XSLException("Reslolved uri must be a StreamSource");
      }

      setInputXML(((StreamSource) src).getInputStream(), getInputHandler(uri));
    } catch (Exception e) {
      throw new XSLException("Could not load input XML file", e);
    }
  }

  private XMLParser xmlParser = null;

  /**
   *
   *
   * @param   in
   * @exception   XSLException
   */
  public void setInputXML(InputStream in, DocHandler fact) throws XSLException {
    try {
      if (xmlParser == null) {
        xmlParser = new XMLParser();
      }

      xmlParser.setValidation(false);
      xmlParser.setNamespaces(true);
      xmlParser.setNamespacePrefixes(true);
      xmlParser.parse(in, fact);
    } catch (Exception e) {
      throw new XSLException("Error while parsing input xml", e);
    }
  }

  /**
   *
   *
   * @return
   */
  public NamespaceManager getXSLNamespaceManager() {
    return xslNsmanager;
  }

  /**
   *
   *
   * @return
   */
  public NamespaceHandler getNamespaceHandler() {
    return namespaceHandler;
  }

  public Hashtable getVariables() {
    return variables;
  }

  public Vector getTopVariablesToProcess() {
    return vTopVariablesToProcess;
  }

  public boolean isIndentAllowed() {
    return bIsIndentAllowed;
  }

  public void setIsIndentAllowed(boolean value) {
    bIsIndentAllowed = value;
  }

  public void setParameters(Hashtable parameters) {
    //    LogWriter.getSystemLogWriter().println("Setting parameters:" + parameters);
    if (parameters.get(SAP_JAVA_XSLT_EXT_CLASSLOADER) != null) {
      //      LogWriter.getSystemLogWriter().println("XSLStylesheet. yooooooooooo");
      setExtClassLoader((ClassLoader) parameters.get(SAP_JAVA_XSLT_EXT_CLASSLOADER));
      parameters.remove(SAP_JAVA_XSLT_EXT_CLASSLOADER);
    }

    this.parameters = parameters;
  }

  public void clearParameters() {
    parameters = null;
  }

  public DecimalFormat getDecimalFormat(String name) {
    return (DecimalFormat) decimalFormats.get(name);
  }

//  private int initializeKeysOldDocNodes = -1;
//  public void initializeKeysOld(XPathContext context) throws XPathException {
//    if (initializeKeysOldDocNodes >= context.dtm.documentNodesCount) {
//      return;
//    }
//    context = context.dtm.getInitialContext(context.owner);
//     
//    Enumeration e = keyArrays.keys();
//
//    while (e.hasMoreElements()) {
//      CharArray sc = (CharArray) e.nextElement();
//      //CharArrat sc = new CharArray(s);
//      //      LogWriter.getSystemLogWriter().println("XSLStylehseet.initKeys s=" + sc);
//      //              LogWriter.getSystemLogWriter().println(" 1 , free mem: " + Runtime.getRuntime().freeMemory());
//      XSLKey[] keys = (XSLKey[]) keyArrays.get(sc);
//      //      LogWriter.getSystemLogWriter().println(" 2 , free mem: " + Runtime.getRuntime().freeMemory());
//      XNodeSet[] nodeSets = new XNodeSet[keys.length];
//      //      LogWriter.getSystemLogWriter().println(" 3 , free mem: " + Runtime.getRuntime().freeMemory());
//      CharArray[][] keyValues = new CharArray[keys.length][];
//
//      //      LogWriter.getSystemLogWriter().println(" 4 , free mem: " + Runtime.getRuntime().freeMemory());
//      for (int i = 0; i < keys.length; i++) {
//        XSLKey key = keys[i];
//        //        LogWriter.getSystemLogWriter().println("XSLStylesheet.initKeyus key,match=" + key.getMatch());
//        //    XNodeSet xo = (xpath.process(etMatch, context)).toXNodeSet();
//        //        XPathContext xc = new XPathContext(context);
//        //        XNodeSet xo = context.getXFactCurrent().getXNodeSet(dtm);
//        //        for (int k=0; k<dtm.size; k++) {
//        //          xc.node = k;
//        //          if (key.getETMatch().et.match(xc)) {
//        //            xo.add(k);
//        //            //LogWriter.getSystemLogWriter().println("Matched For: " + k);
//        //          }
//        //          if ((k % 2000) == 0) {
//        //            LogWriter.getSystemLogWriter().println("k=" + k);
//        //          }
//        //        }
//        //        LogWriter.getSystemLogWriter().println(" 5 , free mem: " + Runtime.getRuntime().freeMemory());
//  
//        
//        //Vector docNodes = new Vector();
//        //for (int j = 0; j <  context.dtm.nodeType.length; j ++) {
//          //if (context.dtm.nodeType[j] == DTM.DOCUMENT_NODE) {
//            //docNodes.add(new Integer(j));
//          //}
//        //}
//        
//        int[] docNodes = context.dtm.documentNodes;
//        XNodeSet xo = null;
//        for (int j = 0; j < context.dtm.documentNodesCount; j ++) {
//          context.node = docNodes[j];
//          XNodeSet next = (XNodeSet) xpath.process(key.getETMatch(), context);
//          if (xo == null) {
//            xo = next;
//          } else {
//            xo.uniteWith(next);
//          }
//
//        }
//
////        DTM dtm = context.dtm;
////        XNodeSet xo = new XNodeSet();   
////        xo.dtm = context.dtm;
//        
//        
//        
//        //XNodeSet xo = (XNodeSet) xpath.process(key.getETMatch(), context);
//     //           LogWriter.getSystemLogWriter().println("XSLStylesheet. initializeKeys 1 :" + xo.size());
//        //        LogWriter.getSystemLogWriter().println(" 6 , free mem: " + Runtime.getRuntime().freeMemory());
//        keyValues[i] = new CharArray[xo.size()];
//        //        LogWriter.getSystemLogWriter().println(" 7 , free mem: " + Runtime.getRuntime().freeMemory());
//        XPathContext c = new XPathContext(context);
//        CharArray x = new CharArray(10000);
//        int pos = 0;
//        int m = 0;
//
//        for (IntArrayIterator k = xo.iterator(); k.hasNext(); m++) {
//          pos++;
//          c.position = pos;
//          c.node = k.next();
//          XObject pr = getXPathProcessor().process(key.getETUse(), c);
//          XString xstr = pr.toXString();
//          //          if ((m%5) == 0 && m > 35500) {
//          //            LogWriter.getSystemLogWriter().println("XSLStylesheet. initializeKeys 2:" + m + ", c.node=" + c.node + ", value=" + xstr.toString() + ", free mem: " + Runtime.getRuntime().freeMemory());
//          //          }
//          freeMem();
//          //          try {
//          //            while (Runtime.getRuntime().freeMemory() < 2000) {
//          //              System.gc();
//          //              Thread.sleep(1000);
//          //              LogWriter.getSystemLogWriter().println(", free mem: " + Runtime.getRuntime().freeMemory());
//          //            }
//          //          } catch (Exception ex) {
//          //          }
//          //          keyValues[i][m] = xstr.getValue().copy();
//          int sz = x.length();
//          int l = xstr.getValue().length();
//          x.append(xstr.getValue());
//          CharArray ca = new CharArray();
//          ca.substring(x, sz, sz + l);
//          //LogWriter.getSystemLogWriter().println("c=" + ca);
//          keyValues[i][m] = ca;
//          pr.close();
//          xstr.close();
//        } 
//
//        //        LogWriter.getSystemLogWriter().println("XSLStylesheet. key ebluated: = " + xo.size());
//        nodeSets[i] = xo;
//      } 
//
//      nodesetArrays.put(sc, nodeSets);
//      keyValuesArrays.put(sc, keyValues);
//    }
//    
//    initializeKeysOldDocNodes = context.dtm.documentNodesCount;
//
//    /*ETObject etMatch = etBuilder.process(key.getMatch());
//     XObject xo = xpath.process(etMatch, context);
//     ETObject etUse = etBuilder.process(key.getUse());*/
//  }
     
  private int initializeKeysDocumentNode = -1;
  public void initializeKeys(XPathContext context) throws XPathException {
    int currentNode = context.node;
    int documentNode = context.dtm.getDocumentElement(currentNode);
    if (initializeKeysDocumentNode == documentNode) {
      return; //Keys are already processed. Do not process them again due to performance reasons
    }
    context = context.dtm.getInitialContext(context.owner);
    context.node = documentNode;
       
    Enumeration e = keyArrays.keys();
    while (e.hasMoreElements()) {
      CharArray sc = (CharArray) e.nextElement();
      XSLKey[] keys = (XSLKey[]) keyArrays.get(sc);
      XNodeSet[] nodeSets = new XNodeSet[keys.length];
      CharArray[][] keyValues = new CharArray[keys.length][];
  
      for (int i = 0; i < keys.length; i++) {
        XSLKey key = keys[i];
          
        XNodeSet xo = (XNodeSet) xpath.process(key.getETMatch(), context);
  
        keyValues[i] = new CharArray[xo.size()];
        XPathContext c = new XPathContext(context);
        CharArray x = new CharArray(10000);
        int pos = 0;
        int m = 0;
  
        for (IntArrayIterator k = xo.iterator(); k.hasNext(); m++) {
          pos++;
          c.position = pos;
          c.node = k.next();
          XObject pr = getXPathProcessor().process(key.getETUse(), c);
          XString xstr = pr.toXString();
          freeMem();
          int sz = x.length();
          int l = xstr.getValue().length();
          x.append(xstr.getValue());
          CharArray ca = new CharArray();
          ca.substring(x, sz, sz + l);
          keyValues[i][m] = ca;
          pr.close();
          xstr.close();
        } 
  
        nodeSets[i] = xo;
      } 
  
      nodesetArrays.put(sc, nodeSets);
      keyValuesArrays.put(sc, keyValues);
    }
      
    initializeKeysDocumentNode = documentNode;
  }

  public void setErrorListener(ErrorListener e) {
    errorListener = e;
  }

  public void sendWarning(String msg) throws XSLException {
    try {
      if (errorListener != null) {
        errorListener.warning(new TransformerException(msg));
      } else {
        LogWriter.getSystemLogWriter().println(msg);
      }
    } catch (TransformerException e) {
      throw new XSLException("Exception while sending warning", e);
    }
  }

  public Hashtable getNamespaceAliases() {
    return namespaceAliases;
  }

  public boolean getIncludeUse() {
    return bIncludeUse;
  }

  public void setIncludeUse(boolean value) {
    bIncludeUse = value;
  }

  public void reuseLastSource() {

  }

  public ClassLoader getExtClassLoader() {
    return extClassLoader;
  }

  public void setExtClassLoader(ClassLoader cl) {
    extClassLoader = cl;
  }

  public Runtime rt = Runtime.getRuntime();

  public void freeMem() {
    try {
      if (rt.freeMemory() < 20000) {
        while (rt.freeMemory() < 20000) {
          System.gc();
          Thread.sleep(500);
          //LogWriter.getSystemLogWriter().println("XSLStylesheet. OutOfMemory - freeing: " + rt.freeMemory());
        }

        //LogWriter.getSystemLogWriter().println("XSLStylesheet. Free memory: " + rt.freeMemory());
      }
    } catch (Exception e) {
      //$JL-EXC$
      //freeing of memory is only an optional step, if memory isn't freed, then OutOfMemoryError will occur later
    }
  }

  public Hashtable getXFKeyHash() {
    return xfKeyHash;
  }
  

  public StaticDouble getStaticDouble() {
    return staticDouble;
  }

  public ObjectPool getHashTablesPool() {
    return hashTablesPool;
  }
}

