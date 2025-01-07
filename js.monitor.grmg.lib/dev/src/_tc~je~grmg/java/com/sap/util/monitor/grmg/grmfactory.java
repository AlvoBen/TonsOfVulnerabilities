/*
 *  last change 2004-03-19
 */

/**
 * @author Bernhard Drabant
 *
 *
 */

package com.sap.util.monitor.grmg;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
<code>GRMFactory</code> fabricates scenarios out of a list of <code>ScenarioDevice</code>s
and the request/response of a servlet or iView.
*/

public class GRMFactory {

  public static final String GRMG_REQUEST = "grmg_request";
  public static String CONTENT_TYPE = "text/html";
  // private static Calendar calendar = new GregorianCalendar();

  private GRMFactory() {}

  // METHODS
  // =======

  /**
  * Creates a GrmgRequest from an InputStream.
  * From a given InputStream a new GrmgRequest will be created.
  * @param  is			InputStream for which a GrmgRequest will be created.
  * @return GrmgRequest
  * @throws ServletException
  * @throws IOException
  * @throws GrmgRequestException
  */
  public static final GrmgRequest createRequestFromInputStream(InputStream is)
    throws ServletException, IOException, GrmgRequestException {

    return new GrmgRequest(is);
  }

  /**
  * Creates a GrmgRequest from an HttpServletRequest.
  * For a given HttpServletRequest a new GrmgRequest will be created.
  * @param  req			HttpServletRequest for which a GrmgRequest will be created.
  * @return GrmgRequest
  * @throws ServletException
  * @throws IOException
  * @throws GrmgRequestException
  * @see com.sap.util.monitor.grmg.GRMFactory#createRequestFromHttpRequest(HttpServletRequest req, String attribName)
  **/
  public static final GrmgRequest createRequestFromHttpRequest(HttpServletRequest req)
    throws ServletException, IOException, GrmgRequestException {

    return createRequestFromHttpRequest(req, GRMG_REQUEST);
  }

  /**
  * Creates a GrmgRequest from an HttpServletRequest.
  * For a given <code>HttpServletRequest</code> a new GrmgRequest will be created
  * and added to the underlying <code>HttpSession</code> if session attribute is
  * @param  req			HttpServletRequest for which a GrmgRequest will be created.
  * @param attribName the name of the attribute in the associated HttpSession
  * @return GrmgRequest
  * @throws ServletException
  * @throws IOException
  * @throws GrmgRequestException
  * @see com.sap.util.monitor.grmg.GRMFactory#createRequestFromInputStream(InputStream is)
  **/
  public static final GrmgRequest createRequestFromHttpRequest(HttpServletRequest req, String attribName) throws ServletException, IOException, GrmgRequestException {
	if (req == null) {
		return null;
	}
	GrmgRequest greq = createRequestFromInputStream(req.getInputStream());
	req.setAttribute(attribName, greq);
	return greq;
  }

  /**
  * Creates a GrmgRequest from an XML file.
  * A well-defined XML file will be parsed and a new GrmgRequest will be returned.
  * @param  filename			the name of the XML file.
  * @return GrmgRequest
  * @throws ServletException
  * @throws IOException
  * @throws GrmgRequestException
  **/
  public static final GrmgRequest createRequestFromFile(String filename)
    throws ServletException, IOException, GrmgRequestException {
    return new GrmgRequest(filename);
  }

  /**
  * Creates a GrmgRequest from an XML file.
  * A well-defined XML file will be parsed and a new GrmgRequest will be returned.
  * @param  file			the XML file
  * @return GrmgRequest
  * @throws ServletException
  * @throws IOException
  * @throws GrmgRequestException
  **/
  public static final GrmgRequest createRequestFromFile(File file) throws ServletException, IOException, GrmgRequestException {

    return new GrmgRequest(file.getName());
  }

  /**
   * Returns a <code>GrmgRequest</code> obtained from the <code>HttpServletRequest</code>.
   * The default attribute name <code>grmg_request</code> will be used.
   * @param  hreq	- the <code>HttpServletRequest</code>
   * @return GrmgRequest
   * @throws GrmgRequestException
   * @throws ServletException
   * @throws IOException
   * @see com.sap.util.monitor.grmg.GRMFactory#getRequestFromHttpRequest(HttpServletRequest hreq, String attribName)
  **/
  public static final GrmgRequest getRequestFromHttpRequest(HttpServletRequest hreq)
    throws GrmgRequestException, IOException, ServletException {

    return getRequestFromHttpRequest(hreq, GRMG_REQUEST);
  }

  /**
	* Returns a <code>GrmgRequest</code> obtained from the <code>HttpServletRequest</code>.
	* If no <code>GrmgRequest</code> exists, 
	* a new instance of <code>GrmgRequest</code> will be created and added to the attributes
	* of the <code>HttpServletRequest</code>.
	* @param hreq - the <code>HttpServletRequest</code>
	* @param attribName - the name of the attribute linked to the <code>GrmgRequest</code>
	* @return GrmgRequest
	* @throws GrmgRequestException
	* @throws ServletException
	* @throws IOException
	* @see com.sap.util.monitor.grmg.GRMFactory#createRequestFromHttpRequest(HttpServletRequest req, String attribName)
   **/
  public static final GrmgRequest getRequestFromHttpRequest(HttpServletRequest hreq, String attribName) throws GrmgRequestException, IOException, ServletException {
	if (hreq == null) {
		return null;
	}
	GrmgRequest greq = null;
	if (hreq.getAttribute(attribName) != null) {
		greq = (GrmgRequest) hreq.getAttribute(attribName);
	}

	if(greq == null) {
		greq = createRequestFromHttpRequest(hreq, attribName);
	}		
	return greq;
  }

  /**
   * Returns the <code>GrmgRequest</code> obtained from the <code>HttpSession</code>.
   * The default attribute name <code>grmg_request</code> will be used.
   * @param  hss - the <code>HttpSession</code>
   * @return GrmgRequest
   **/
  public static final GrmgRequest getRequestFromSession(HttpSession hss) {
    return getRequestFromSession(hss, GRMG_REQUEST);
  }

  /**
	* Returns a <code>GrmgRequest</code> obtained from an <code>HttpSession</code>.
	* @param  hss - the <code>HttpSession</code>
	* @param attribName - the name of the attribute linked to the <code>GrmgRequest</code>
	* in the <code>HttpSession</code>.
	* @return GrmgRequest
	**/
  public static final GrmgRequest getRequestFromSession(HttpSession hss, String attribName) {
	if (hss == null || hss.getAttribute(attribName) == null) {
		return null;
	}
	return (GrmgRequest) hss.getAttribute(attribName);
  }

  /**
  * Returns a GrmgScenario derived from a GrmgRequest of an HttpSession.
  * @param  hss			the HttpSession
  * @return GrmgScenario
  **/
  public static final GrmgScenario getScenarioFromSession(HttpSession hss) {
	GrmgRequest grmgRequest = getRequestFromSession(hss);
	if (grmgRequest != null) {
		return grmgRequest.getScenario();
	}
    return null;
  }

  /**
  * Returns a GrmgScenario derived from a newly created GrmgRequest of an underlying
  * HttpServletRequest
  * @param  hreq			the HttpServletRequest
  * @return GrmgScenario
  **/
  public static final GrmgScenario createScenarioFromHttpRequest(HttpServletRequest hreq)
    throws GrmgRequestException, IOException, ServletException {

    return createRequestFromHttpRequest(hreq).getScenario();
  }

  /**
  * Returns an array of <code>GrmgComponent</code>s contained in the <code>GrmgScenario</code>.
  * @param gscen the <code>GrmgScenario</code>
  * @return array of <code>GrmgComponent</code>
  **/
  public static final GrmgComponent[] getComponentsFromScenario(GrmgScenario gscen) {

    if (gscen == null) {
			return null;
	}
		
	GrmgComponent[] allComps = new GrmgComponent[gscen.getComponents().size()];
	gscen.getComponents().toArray(allComps);
		
	return allComps;
  }

  /**
  * Sets the initial data of a GrmgComponent for an underlying HttpServletRequest.
  * If component instance is <code>null</code> it will be set to "001" by default.
  * @param  comp			the GrmgComponent for which the data will be set
  * @param httpreq the HttpServletRequest
  * @param compinst the component instance
  **/
  public static final void setInitialComponentData(GrmgComponent comp, HttpServletRequest httpreq, String compinst) {

    comp.setHost(
      httpreq.getScheme() + "://" + httpreq.getServerName() + ":" + httpreq.getServerPort() + httpreq.getContextPath());

    if (compinst == null)
      compinst = "001";

    comp.setInst(compinst);
    // here default message text will be added due to CCMS insufficiency
    comp.addMessage().setMessageParameters("", "", "", "", "", "", "", "", "initial");
  }

  /**
  * Runs the test method <code>runScenario</code> of a list of ScenarioDevice objects
  * <code>scdevarr</code>, with parameters <code>hreq</code>, <code>hres</code>.
  * @param  scdevarr			the array of ScenarioDevice objects to be tested
  * @param hreq the HttpServletRequest
  **/
  public static final void testScenario(ScenarioDevice[] scdevarr, HttpServletRequest hreq) {

    String doMethodType = hreq.getMethod();

    if (!doMethodType.equals("POST")) {
      return;
    }

    GrmgRequest greq = null;
    GrmgScenario gscen = null;
    HttpServletRequest ghreq = hreq;

    try {
      greq = getRequestFromHttpRequest(hreq);
      gscen = greq.getScenario();

      GrmgComponent[] allComps = getComponentsFromScenario(gscen);
      for (int i = 0; i < allComps.length; i += 1)
        setInitialComponentData(allComps[i], hreq, null);
    } catch (GrmgRequestException e) {
      debug(e.getMessage());
    } catch (IOException e) {
      debug(e.getMessage());
    } catch (ServletException e) {
      debug(e.getMessage());
    }

    if (scdevarr == null | scdevarr.length == 0)
      return;

    System.out.println("\n[INFO] Heartbeat Test of Scenario " + gscen.getName());
    System.out.println("[INFO] Time: " + new GregorianCalendar().getTime());
    System.out.println("[INFO] Remote host: " + hreq.getRemoteHost());
    System.out.println("[INFO] URL: " + hreq.getRequestURL());

    for (int i = 0; i < scdevarr.length; i += 1) {
      scdevarr[i].setHttpData(ghreq, null);
      scdevarr[i].setRequest(greq);
      scdevarr[i].setScenario(gscen);
      scdevarr[i].setRequestObject(hreq);

      GrmgComponent scenGenComp = gscen.getComponentByName("ScenSelf");

      try {
        testScenario(scdevarr[i], "runScenario", null);
      } catch (IllegalAccessException e) {
        if (scenGenComp != null)
          scdevarr[i].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + i + ". scenario. " + e.getMessage());
      } catch (IllegalArgumentException e) {
        if (scenGenComp != null)
          scdevarr[i].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + i + ". scenario. " + e.getMessage());
      } catch (InvocationTargetException e) {
        if (scenGenComp != null)
          scdevarr[i].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + i + ". scenario. " + e.getMessage());
      }
    }
  }

  /**
  * Runs the test method <code>runScenario</code> of a list of ScenarioDevice objects
  * <code>scdevarr</code>, with parameters <code>hreq</code>, <code>hres</code>.  After
  * testing the result will be transmitted to the <code>HttpServletResponse</code>.
  * @param  scdevarr			the array of ScenarioDevice objects to be tested
  * @param hreq the HttpServletRequest
  * @param hres the HttpServletResponse
  **/
  public static final void testScenario(ScenarioDevice[] scdevarr, HttpServletRequest hreq, HttpServletResponse hres) {

    hres.setContentType(CONTENT_TYPE);
    String doMethodType = hreq.getMethod();

    if (!doMethodType.equalsIgnoreCase("POST"))
      return;

    GrmgRequest greq = null;
    GrmgScenario gscen = null;
    HttpServletRequest ghreq = hreq;
    HttpServletResponse ghres = hres;

    try {
      greq = getRequestFromHttpRequest(hreq);
      gscen = greq.getScenario();
      GrmgComponent[] allComps = getComponentsFromScenario(gscen);
      for (int i = 0; i < allComps.length; i += 1)
        setInitialComponentData(allComps[i], hreq, null);
    } catch (GrmgRequestException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServletException e) {
      e.printStackTrace();
    }

    if (scdevarr == null | scdevarr.length == 0)
      return;

    System.out.println("\n[INFO] Heartbeat Test of Scenario " + gscen.getName());
    System.out.println("[INFO] Time: " + new GregorianCalendar().getTime());
    System.out.println("[INFO] Remote host: " + hreq.getRemoteHost());
    System.out.println("[INFO] URL: " + hreq.getRequestURL());

    for (int i = 0; i < scdevarr.length; i += 1) {
      scdevarr[i].setHttpData(ghreq, ghres);
      scdevarr[i].setRequest(greq);
      scdevarr[i].setScenario(gscen);
      scdevarr[i].setRequestObject(hreq);
      scdevarr[i].setResponseObject(hres);

      GrmgComponent scenGenComp = gscen.getComponentByName("ScenSelf");

      try {
        testScenario(scdevarr[i], "runScenario", null);
      } catch (IllegalAccessException e) {
        if (scenGenComp != null)
          scdevarr[i].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + i + ". scenario. " + e.getMessage());
      } catch (IllegalArgumentException e) {
        if (scenGenComp != null)
          scdevarr[i].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + i + ". scenario. " + e.getMessage());
      } catch (InvocationTargetException e) {
        if (scenGenComp != null)
          scdevarr[i].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + i + ". scenario. " + e.getMessage());
      }
    }
    try {
      transmitResponse(createResponse(gscen), hres);
    } catch (Exception e) {
      log("Could not send response.", e);
    }
  }

  /**
  * Runs the test method <code>runScenario</code> of a list of ScenarioDevice objects
  * <code>scdevarr</code>, with parameter <code>hreqobj</code>.
  * The parameter <code>hreqobj</code> is the request
  * object. This means it is an object of type <code>HttpServletRequest</code>,
  * or it has methods <code>getServletRequest()</code> and
  * <code>getServletResponse(boolean)</code> with return type
  * <code>HttpServletRequest</code> and <code>HttpServletResponse</code>
  * resp. An example of the second case is an object of type
  * <code>IPortalComponentRequest</code> of the PortalRuntime API.
  * @param  scdevarr			the array of ScenarioDevice objects to be tested
  * @param hreqobj the request object
  **/
  public static final void testScenario(ScenarioDevice[] scdevarr, Object hreqobj) {

    Class hreqcls = hreqobj.getClass();
    HttpServletRequest hreq;

    for (int j = 0; j < scdevarr.length; j += 1)
      scdevarr[j].setRequestObject(hreqobj);

    if (HttpServletRequest.class.isAssignableFrom(hreqcls) || (isRequestObject(hreqobj))) {
      hreq = getHttpRequest(hreqobj);
      testScenario(scdevarr, hreq);
    } else
      System.out.println("No match of request or response object.");
  }

  /**
  * Runs the test method <code>runScenario</code> of a list of ScenarioDevice objects
  * <code>scdevarr</code>, with parameters <code>hreqobj</code>, <code>hresobj</code>.
  * After testing the result will be transmitted to the response object <code>hresobj</code>.
  * The parameters <code>hreqobj</code>, <code>hresobj</code> are request and response
  * objects respectively. This means they are objects of type <code>HttpServletRequest</code>
  * and <code>HttpServletResponse</code> respectively, or their request object has a method
  * <code>getServletRequest()</code> and <code>getServletResponse(boolean)</code>
  * with return type <code>HttpServletRequest</code> and <code>HttpServletResponse</code>
  * resp. An example of the second case are objects of type <code>IPortalComponentRequest</code>
  * and <code>IPortalComponentResponse</code> of the PortalRuntime API.
  * @param  scdevarr			the array of ScenarioDevice objects to be tested
  * @param hreqobj the request object
  * @param hresobj the response object
  **/
  public static final void testScenario(ScenarioDevice[] scdevarr, Object hreqobj, Object hresobj) {

    Class hreqcls = hreqobj.getClass();
    Class hrescls = hresobj.getClass();
    HttpServletRequest hreq;
    HttpServletResponse hres;

    for (int j = 0; j < scdevarr.length; j += 1) {
      scdevarr[j].setRequestObject(hreqobj);
      scdevarr[j].setResponseObject(hresobj);
    }

    if ((HttpServletRequest.class.isAssignableFrom(hreqcls) && HttpServletResponse.class.isAssignableFrom(hrescls))
      || (isRequestObject(hreqobj))) {

      hreq = getHttpRequest(hreqobj);
      hres = getHttpResponse(hreqobj, hresobj);

      testScenario(scdevarr, hreq, hres);
    } else
      System.out.println("No match of request or response object.");
  }

  /**
  * Runs the test method <code>runScenario</code> of the ScenarioDevice <code>scdef</code>,
  * with parameters <code>hreq</code>, <code>hres</code>. After
  * testing the result will be transmitted to the <code>HttpServletResponse</code>.
  * This is the standard invocation method. Alternatively, <code>runScenario</code>
  * can be called directly on <code>scdef</code>.
  * @param  scdev			the ScenarioDevice
  * @param hreq the HttpServletRequest
  * @param hres the HttpServletResponse
  **/
  public static final void testScenario(ScenarioDevice scdev, HttpServletRequest hreq, HttpServletResponse hres) {

    ScenarioDevice[] scdevarray = { scdev };
    testScenario(scdevarray, hreq, hres);
  }

  /**
  * Runs the test method <code>runScenario</code> of the ScenarioDevice <code>scdef</code>,
  * with parameters <code>hreq</code>, <code>hres</code>.
  * This is the standard invocation method. Alternatively, <code>runScenario</code>
  * can be called directly on <code>scdef</code>.
  * @param  scdev			the ScenarioDevice
  * @param hreq the HttpServletRequest
  **/
  public static final void testScenario(ScenarioDevice scdev, HttpServletRequest hreq) {

    ScenarioDevice[] scdevarray = { scdev };
    testScenario(scdevarray, hreq);
  }

  /**
  * Runs the test method <code>runScenario</code> of a ScenarioDevice object
  * <code>scdev</code>, with parameter <code>hreqobj</code>.
  * The parameter <code>hreqobj</code> is a request
  * object. This means it is an object of type <code>HttpServletRequest</code>
  * or it has methods <code>getServletRequest()</code> and
  * <code>getServletResponse(boolean)</code> with return type
  * <code>HttpServletRequest</code> and <code>HttpServletResponse</code>
  * resp. An example of the second case is an object of type
  * <code>IPortalComponentRequest</code> of the PortalRuntime API.
  * @param  scdev			the ScenarioDevice object to be tested
  * @param hreqobj the request object
  **/
  public static final void testScenario(ScenarioDevice scdev, Object hreqobj) {

    ScenarioDevice[] scdevarr = new ScenarioDevice[] { scdev };

    for (int j = 0; j < scdevarr.length; j += 1) {
      scdevarr[j].setRequestObject(hreqobj);
    }

    testScenario(scdevarr, hreqobj);
  }

  /**
  * Runs the test method <code>runScenario</code> of a ScenarioDevice object
  * <code>scdev</code>, with parameters <code>hreqobj</code>, <code>hresobj</code>.
  * After testing the result will be transmitted to the response object <code>hresobj</code>.
  * The parameters <code>hreqobj</code>, <code>hresobj</code> are request and response
  * objects respectively. This means they are objects of type <code>HttpServletRequest</code>
  * and <code>HttpServletResponse</code> respectively, or their request object has a method
  * <code>getServletRequest()</code> and <code>getServletResponse(boolean)</code>
  * with return type <code>HttpServletRequest</code> and <code>HttpServletResponse</code>
  * resp. An example of the second case are objects of type <code>IPortalComponentRequest</code>
  * and <code>IPortalComponentResponse</code> of the PortalRuntime API.
  * @param  scdev			the ScenarioDevice object to be tested
  * @param hreqobj the request object
  * @param hresobj the response object
  **/
  public static final void testScenario(ScenarioDevice scdev, Object hreqobj, Object hresobj) {

    ScenarioDevice[] scdevarr = new ScenarioDevice[] { scdev };

    for (int j = 0; j < scdevarr.length; j += 1) {
      scdevarr[j].setRequestObject(hreqobj);
      scdevarr[j].setResponseObject(hresobj);
    }

    testScenario(scdevarr, hreqobj, hresobj);
  }

  /**
  Calls a method called <code>methodname</code> of the
  ScenarioDevice <code>scdef</code>, with parameters <code>params</code>. This
  is the "mother" of all testScenario methods.
  * @param  scdev			the ScenarioDevice
  * @param methodname  the name of the method to be invoked in scdev
  * @param params the parameters for the method <code>methodname</code>
  **/
  private static final void testScenario(ScenarioDevice scdev, String methodname, Object[] params)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    Class[] parms;
    Method method = null;
    int arrayLength;
    boolean match = false;

    Class scdevClass = scdev.getClass();
    Method[] methods = scdevClass.getMethods();

    one : for (int j = 0; j < methods.length; j += 1) {

      parms = methods[j].getParameterTypes();

      if (params == null)
        arrayLength = 0;
      else
        arrayLength = params.length;

      if (methods[j].getName().equals(methodname) & parms.length == arrayLength) {
        for (int k = 0; k < parms.length; k += 1) {
          if (!parms[k].isAssignableFrom(params[k].getClass()))
            continue one;
          if (k == parms.length - 1) {
            match = true;
          }
        }
        if (match | arrayLength == 0) {
          method = methods[j];
          break one;
        }
      }
    }

    if (method != null) {
      // prints to console of Web Server
      System.out.println("[INFO] Method invoked: " + method);
      method.invoke(scdev, params);
    }
  }

  /**
  Calls the method <code>runScenario</code> of the
  ScenarioDevice <code>scdef</code>, with parameters <code>params</code>.
  * @param  scdev			the ScenarioDevice
  * @param params the parameters for the method <code>methodname</code>
  **/
  public static final void testScenario(ScenarioDevice scdev, Object[] params) {

    try {
      testScenario(scdev, "runScenario", params);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
  Runs a list of scenarion devices contained in a <code>ScenarioPanel</code>
  with parameters <code>hreqobj</code>, <code>hresobj</code>.
  * The parameters <code>hreqobj</code>, <code>hresobj</code> are request and response
  * objects respectively. This means they are objects of type <code>HttpServletRequest</code>
  * and <code>HttpServletResponse</code> respectively, or their request object has a method
  * <code>getServletRequest()</code> and <code>getServletResponse(boolean)</code>
  * with return type <code>HttpServletRequest</code> and <code>HttpServletResponse</code>
  * resp. An example of the second case are objects of type <code>IPortalComponentRequest</code>
  * and <code>IPortalComponentResponse</code> of the PortalRuntime API.
  * @param  scpan		ScenarioPanel object
  * @param hreqobj the request object
  **/
  public static final void testScenario(ScenarioPanel scpan, Object hreqobj) {

    testScenario(scpan.getScenarioDeviceList(), hreqobj);
  }

  /**
  Runs a list of scenarion devices contained in a <code>ScenarioPanel</code>
  with parameters <code>hreqobj</code>, <code>hresobj</code>.  After
  testing the result will be transmitted to the response object <code>hresobj</code>.
  * The parameters <code>hreqobj</code>, <code>hresobj</code> are request and response
  * objects respectively. This means they are objects of type <code>HttpServletRequest</code>
  * and <code>HttpServletResponse</code> respectively, or their request object has a method
  * <code>getServletRequest()</code> and <code>getServletResponse(boolean)</code>
  * with return type <code>HttpServletRequest</code> and <code>HttpServletResponse</code>
  * resp. An example of the second case are objects of type <code>IPortalComponentRequest</code>
  * and <code>IPortalComponentResponse</code> of the PortalRuntime API.
  * @param  scpan			the array of ScenarioDevice objects to be tested
  * @param hreqobj the request object
  * @param hresobj the response object
  **/
  public static final void testScenario(ScenarioPanel scpan, Object hreqobj, Object hresobj) {

    testScenario(scpan.getScenarioDeviceList(), hreqobj, hresobj);
  }

  /**
  Runs a list of scenarion devices contained in a <code>ScenarioDevice</code> array,
  with parameter <code>grmgCust</code> which is of type <code>org.w3c.dom.Document</code>.
  * The grmg customizing XML document <code>grmgCust</code> contains the relevant
  * information in order to run the GRMG scenario test on the given array of
  * <code>ScenarioDevice</code> objects. The GRMG response will be returned by
  * the method. Currently only single scenario settings are admitted.
  * @param  scdevarray			the array of <code>ScenarioDevice</code> objects to be tested
  * @param grmgCust the <code>Document</code> containing the GRMG request
  * @return the <code>Document</code> containing the GRMG response
  **/
  public static final Document testScenario(ScenarioDevice[] scdevarray, Document grmgCust) throws GrmgRequestException {

    NodeList allScenarios = grmgCust.getElementsByTagName("scenario");

    // for convenience: only a single scenario allowed for adapter mechanism

    if (allScenarios.getLength() > 1 & allScenarios.getLength() == 0)
      return null;

    Document document;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.newDocument();
    } catch (ParserConfigurationException pce) {
      throw new GrmgRequestException("Parser Configuration Exception while creating Output");
    }

    document.importNode(allScenarios.item(0), true);

    InputStream docStream = new GrmgXMLFile(document).getDocumentAsStream();

    GrmgRequest greq = new GrmgRequest(docStream);

    for (int j = 0; j < scdevarray.length; j += 1) {

      scdevarray[j].setRequest(greq);
      scdevarray[j].setScenarioByRequest(greq);

      GrmgComponent scenGenComp = scdevarray[j].getScenario().getComponentByName("ScenSelf");

      try {
        testScenario(scdevarray[j], "runScenario", null);
      } catch (IllegalAccessException e) {
        if (scenGenComp != null)
          scdevarray[j].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + j + ". scenario. " + e.getMessage());
      } catch (IllegalArgumentException e) {
        if (scenGenComp != null)
          scdevarray[j].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + j + ". scenario. " + e.getMessage());
      } catch (InvocationTargetException e) {
        if (scenGenComp != null)
          scdevarray[j].setComponentData(
            scenGenComp,
            "ERROR",
            "010",
            "RT",
            "555",
            "",
            "",
            "",
            "",
            "General scenario error in " + j + ". scenario. " + e.getMessage());
      }
    }
    return createResponse(greq.getScenario()).getResponseAsDom();
  }

  /**
  Runs a list of scenarion devices contained in a <code>ScenarioPanel</code>,
  with parameter <code>grmgCust</code> which is of type <code>org.w3c.dom.Document</code>.
  * The grmg customizing XML document <code>grmgCust</code> contains the relevant
  * information in order to run the GRMG scenario test on the given
  * <code>ScenarioPanel</code>. The GRMG response will be returned by
  * the method. Currently only single scenario settings are admitted.
  * @param  scpan			the <code>ScenarioPanel</code> to be tested
  * @param grmgCust the <code>Document</code> containing the GRMG request
  * @return the <code>Document</code> containing the GRMG response
  **/
  public static final Document testScenario(ScenarioPanel scpan, Document grmgCust) throws GrmgRequestException {

    ScenarioDevice[] scdevarray = scpan.getScenarioDeviceList();
    return testScenario(scdevarray, grmgCust);
  }

  public static final void testScenario(ScenarioDataCollector scdatacoll, Object hreqobj, Object hresobj)
    throws GrmgRequestException {

    Class hreqcls = hreqobj.getClass();
    HttpServletRequest hreq;
    String scenname = "";

    if (HttpServletRequest.class.isAssignableFrom(hreqcls) || (isRequestObject(hreqobj))) {
      hreq = getHttpRequest(hreqobj);
    } else
      return;

    GrmgRequest greq = null;
    GrmgScenario gscen = null;

    try {
      greq = getRequestFromHttpRequest(hreq);
      gscen = greq.getScenario();
      scenname = gscen.getName();
      GrmgComponent[] allComps = getComponentsFromScenario(gscen);
      for (int i = 0; i < allComps.length; i += 1)
        setInitialComponentData(allComps[i], hreq, null);
    } catch (GrmgRequestException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServletException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < scdatacoll.getAllDataContainers().size(); i += 1) {
      if (((ScenarioDataContainer) (scdatacoll.getAllDataContainers().get(i)))
        .getGrmgXMLFile()
        .getScenarioNames()[0]
        .equals(scenname)) {
        testScenario(((ScenarioDataContainer)scdatacoll.getAllDataContainers().get(i)).getScenarioPanel(), hreqobj, hresobj);
        return;
      }
    }
  }

  /**
  Returns a new GrmgResponse from a GrmgScenario.
  * @param  grsc			the GrmgScenario
  * @return the GrmgResponse
  **/
  public static final GrmgResponse createResponse(GrmgScenario grsc) {

    return new GrmgResponse(grsc);
  }

  /**
  Transmits the output stream of a GrmgResponse to the output stream of an HttpServletResponse.
  * @param  gresp			the GrmgResponse from which the output will be written to the
  * HttpServletResponse
  * @param  hresp 			the HttpServletResponse to which the output of the GrmgReponse
  * will be written.
  **/
  public static final void transmitResponse(GrmgResponse gresp, HttpServletResponse hresp) throws IOException {

    try {
      gresp.getOutput().writeTo(hresp.getOutputStream());
    } catch (IllegalStateException e) {
      // use method getWriter() instead of getOutputStream()
      transmitResponse(gresp, hresp.getWriter());
    }
  }

  /**
  Transmits the output stream of a GrmgResponse to a PrintWriter.
  * @param  gresp			the GrmgResponse from which the output will be written to the
  * HttpServletResponse
  * @param  wrt 			the PrintWriter to which the output of the GrmgReponse
  * will be written.
  **/
  public static final void transmitResponse(GrmgResponse gresp, PrintWriter wrt) throws IOException {

    byte[] barray = gresp.getOutput().toByteArray();

    for (int k = 0; k < barray.length; k += 1) {
      wrt.write(barray[k]);
    }
  }

  /**
  Tests if an object is a request object - either <code>HttpServletRequest</code> or
  <code>IPortalComponentRequest</code> or any other object with method
  <code>getServletRequest</code> with return type <code>HttpServletRequest</code>.
  * @param  reqobj			the potential request object
  **/
  private static final boolean isRequestObject(Object reqobj) {

    try {
      Method reqmeth = reqobj.getClass().getMethod("getServletRequest", null);

      if (((reqmeth != null) && HttpServletRequest.class.isAssignableFrom(reqmeth.getReturnType()))
        || HttpServletRequest.class.isAssignableFrom(reqobj.getClass())) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return false;
  }

  /**
  Returns the HttpServletRequest for a request object (see method <code>isRequestObject</code>),
  otherwise returns <code>null</code>.
  * @param  reqobj			the request object
  **/
  private static final HttpServletRequest getHttpRequest(Object reqobj) {

    try {
      if (isRequestObject(reqobj))
        return (HttpServletRequest) (reqobj.getClass().getMethod("getServletRequest", null).invoke(reqobj, null));

      if (HttpServletRequest.class.isAssignableFrom(reqobj.getClass()))
        return (HttpServletRequest)reqobj;
    } catch (Exception e) {
      debug(e.getMessage());
    }

    return null;
  }

  /**
  Returns the <code>HttpServletResponse</code> for a (request object/response object)
  pair - for instance <code>HttpServletRequest</code>/<code>HttpServletResponse</code>
  or <code>IPortalComponentRequest</code>/<code>IPortalComponentResponse</code>.
  Otherwise the method returns <code>null</code>.
  * @param reqobj		the request object
  * @param resobj    the acompanying response object
  **/
  private static final HttpServletResponse getHttpResponse(Object reqobj, Object resobj) {

    try {
      if (isRequestObject(reqobj))
        return (HttpServletResponse)
          (reqobj
            .getClass()
            .getMethod("getServletResponse", new Class[] { boolean.class })
            .invoke(reqobj, new Object[] { new Boolean(true)}));

      if (HttpServletResponse.class.isAssignableFrom(resobj.getClass()))
        return (HttpServletResponse)resobj;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private static void debug(String s) {
    //add logging here
    System.out.println(s);
  }

  private static void log(String s) {
    //add logging here
  }

  private static void log(String s, Exception e) {
    //add logging here
    System.out.println(s + e.getMessage());
  }

}