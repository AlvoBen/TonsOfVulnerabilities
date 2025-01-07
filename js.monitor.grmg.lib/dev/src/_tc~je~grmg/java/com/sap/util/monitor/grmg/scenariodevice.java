/*
 *  last change 2004-03-19
 */

/**
 * @author Bernhard Drabant
 * 
 */

package com.sap.util.monitor.grmg;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.*;

/**
  Abstract class representing a portion of a scenario which will be tested using the 
  parameters provided by the <code>GrmgScenario</code>. 
 * The method <code>runScenario</code> needs to be implemented. It defines the
 * test of this part of the scenario. <code>ScenarioDevice</code> will be tested
 * and its result transmitted by the <code>testScenario</code> methods of class
 * <code>GRMFactory</code>. <code>ScenarioDevice</code> objects are modular, this 
 * means that an array of <code>ScenarioDevice</code> objects or
 * a <code>ScenarioPanel</code> containing several registered <code>ScenarioDevice</code> 
 * objects can be tested sequentially by <code>GRMFactory</code>.
 */

/*
ScenarioDevice provides:

<br>1. GrmgScenario objects from GrmgRequest objects.
<br>2. GrmgComponent objects from GrmgScenario objects.
<br>3. GrmgResponse from GrmgScenario objects.
<br>4. GrmgComponent objects from GrmgScenario objects.
<br>5. GrmgComponent objects from GrmgScenario objects.
<br>6. GrmgComponent objects from GrmgScenario objects.

<p>Furthermore the output streams of responses (GrmgResponse) can be transmitted 
to an HttpResponse.</p>
*/

public abstract class ScenarioDevice {

  private GrmgRequest gRequest;
  private GrmgScenario gScenario;
  private Vector gComponents = new Vector();
  private Vector gComponentNames = new Vector();
  private HttpSession httpses;
  private HttpServletRequest httpreq = null;
  private HttpServletResponse httpres = null;
  private Object requestObject = null;
  private Object responseObject = null;

  // Methods
  // =======

  /**
  Abstract method which defines the (portion of a) GRMG scenario test. Needs 
  to be implemented according to the underlying scenario or a part of it.
  **/
  abstract public void runScenario();

  /**
  Returns the GrmgRequest of the instance.
  * @return GrmgRequest 
  **/
  public GrmgRequest getRequest() {

    return gRequest;
  }

  /**
  Returns the <code>HttpServletRequest</code> underlying the instance, or <code>null</code> 
  if there is none.
  * @return HttpServletRequest 
  **/
  protected HttpServletRequest getHttpRequest() {

    return httpreq;
  }

  /**
  Returns the <code>HttpServletResponse</code> underlying the instance, or <code>null</code> 
  if there is none.
  * @return HttpServletResponse 
  **/
  protected HttpServletResponse getHttpResponse() {

    return httpres;
  }

  /**
  Returns the request object underlying the instance, or <code>null</code> 
  if there is none.
  * @return Object 
  **/
  protected Object getRequestObject() {

    return requestObject;
  }

  /**
  Returns the response object underlying the instance, or <code>null</code> 
  if there is none.
  * @return Object 
  **/
  protected Object getResponseObject() {

    return responseObject;
  }

  /**
  Initializes the request object of the <code>ScenarioDevice</code> instance.
  * The request object represents the web request which may be for instance
  * an <code>HttpServletRequest</code> or an <code>IPortalComponentRequest</code>.
  * The request object will be initialized at most once per session. 
  * @param iniRequest Object 
  **/
  protected void setRequestObject(Object iniRequest) {

    if (requestObject == null)
      requestObject = iniRequest;
  }

  /**
  Initializes the response object of the <code>ScenarioDevice</code> instance.
  * The response object represents the web response which may be for instance
  * an <code>HttpServletResponse</code> or an <code>IPortalComponentResponse</code>.
  * The response object will be initialized at most once per session. 
  * @param iniResponse Object 
  **/
  protected void setResponseObject(Object iniResponse) {

    if (responseObject == null)
      responseObject = iniResponse;
  }

  /**
  Sets the GrmgRequest.
  * @param req GrmgRequest 
  **/
  public void setRequest(GrmgRequest req) {

    gRequest = req;
  }

  /**
  Sets the GrmgRequest with the data provided by an HttpServletRequest. The method is 
  not 'idempotent', i.e. multiple application of method setRequest yields different (new)
  GrmgRequest objects.
  * @param file the File containing the DTD-compliant XML document providing the data of 
  * the GrmgRequest.
  **/
  public void setRequest(HttpServletRequest hreq) {

    try {
      gRequest = GRMFactory.createRequestFromHttpRequest(hreq);
    } catch (IOException ie) {
      debug(ie.getMessage());
    } catch (GrmgRequestException ge) {
      debug(ge.getMessage());
    } catch (ServletException se) {
      debug(se.getMessage());
    }
  }

  /**
  Sets the GrmgRequest with the data provided by an XML file. The method is 
  not 'idempotent', i.e. multiple application of method setRequest yields different (new)
  GrmgRequest objects.
  * @param file the File containing the DTD-compliant XML document providing the data of 
  * the GrmgRequest.
  **/
  public void setRequest(File file) {

    try {
      gRequest = GRMFactory.createRequestFromFile(file);
    } catch (IOException ie) {
      debug(ie.getMessage());
    } catch (GrmgRequestException ge) {
      debug(ge.getMessage());
    } catch (ServletException se) {
      debug(se.getMessage());
    }
  }

  /**
  Sets the GrmgRequest with the data provided by an XML file (name). The method is 
  not 'idempotent', i.e. multiple application of method setRequest yields different (new)
  GrmgRequest objects.
  * @param filename the String representaion of an XML file providing the data of 
  * the GrmgRequest.
  **/
  public void setRequest(String filename) {

    try {
      gRequest = GRMFactory.createRequestFromFile(filename);
    } catch (IOException ie) {
      debug(ie.getMessage());
    } catch (GrmgRequestException ge) {
      debug(ge.getMessage());
    } catch (ServletException se) {
      debug(se.getMessage());
    }
  }

  /**
  Sets the GrmgRequest with the data provided by an InputStream. The method is 
  not 'idempotent', i.e. multiple application of method setRequest yields different (new)
  GrmgRequest objects.
  * @param bis InputStream providing the XML formatted data of the GrmgRequest
  **/
  public void setRequest(InputStream bis) {

    try {
      gRequest = GRMFactory.createRequestFromInputStream(bis);
    } catch (IOException ie) {
      debug(ie.getMessage());
    } catch (GrmgRequestException ge) {
      debug(ge.getMessage());
    } catch (ServletException se) {
      debug(se.getMessage());
    }
  }

  /**
  Sets the GrmgRequest with the data provided by the HttpSession.
  * @param hs HttpSession which provides the data to set the GrmgRequest
  **/
  public void setRequest(HttpSession hs) {

    gRequest = GRMFactory.getRequestFromSession(hs);
  }

  /**
  Returns a GrmgScenario.
  * @return GrmgScenario field of ScenarioDevice
  **/
  public GrmgScenario getScenario() {

    return gScenario;
  }

  /**
  Returns a GrmgScenario from a given GrmgRequest. Caution: In general
  this is not the GrmgScenario of the class ScenarioDevice which will be
  retrieved through method getScenario().
  * @param  grmreq			the GrmgRequest
  * @return GrmgScenario 
  **/
  public GrmgScenario getScenarioFromRequest(GrmgRequest grmreq) {

    return grmreq.getScenario();
  }

  /**
  Sets the internal <code>GrmgScenario</code> of <code>ScenarioDevice</code> 
  to the given <code>GrmgScenario</code>.
  * @param  sc			the GrmgScenario
  **/
  public void setScenario(GrmgScenario sc) {

    gScenario = sc;
  }

  /**
  Sets the internal <code>GrmgScenario</code> of <code>ScenarioDevice</code> 
  to the <code>GrmgScenario</code> underlying the given <code>GrmgRequest</code>.
  * @param  grmreq			the GrmgRequest
  * @return GrmgScenario 
  **/
  public void setScenarioByRequest(GrmgRequest rq) {

    gScenario = rq.getScenario();
  }

  /**
  Returns a GrmgComponent from a GrmgScenario.
  * The current GrmgComponent will be returned from the GrmgScenario.
  * @param  grsc			the GrmgScenario
  * @return the current GrmgComponent
  **/
  public GrmgComponent getCurrentComponent(GrmgScenario grsc) {

    return grsc.getCurrentComponent();
  }

  /**
  Returns a GrmgComponent from a GrmgScenario.
  * The GrmgComponent at the indicated index will be returned from the GrmgScenario.
  * @param  grsc			the GrmgScenario
  * @param  j				the index of the component to be returned
  * @return the GrmgComponent at index <code>j</code>
  **/
  public GrmgComponent getComponent(GrmgScenario grsc, int j) {

    return grsc.getComponent(j);
  }

  /**
  Returns a GrmgComponent from a GrmgScenario.
  * The GrmgComponent with the indicated name will be returned from the GrmgScenario.
  * @param  grsc			the GrmgScenario
  * @param  name				the name of the component to be returned
  * @return the GrmgComponent with name <code>name</code>
  **/
  public GrmgComponent getComponent(GrmgScenario grsc, String name) {

    return grsc.getComponentByName(name);
  }

  /**
  Returns a GrmgComponent from a GrmgRequest.
  * The GrmgComponent with the indicated name will be returned from the GrmgRequest.
  * @param  greq			the GrmgRequest
  * @param  name				the name of the component to be returned
  * @return the GrmgComponent with name <code>name</code>
  **/
  public GrmgComponent getComponent(GrmgRequest greq, String name) {

    return greq.getScenario().getComponentByName(name);
  }

  /**
  Returns a GrmgComponent from the <code>ScenarioDevice</code>'s underlying 
  <code>GrmgRequest</(code>.
  * The <code>GrmgComponent</code> with the indicated name will be returned.
  * @param  name				the name of the component to be returned
  * @return the <code>GrmgComponent</code> with name <code>name</code> or 
  * <code>null</code> if it is not contained in the GRMG request.
  **/
  public GrmgComponent getComponent(String name) {

    GrmgComponent gcomp = gScenario.getComponentByName(name);
    return gcomp;
  }

  /**
  Returns a filtered array of the <code>GrmgComponent</code>s associated to the 
  <code>ScenarioDevice</code>. 
  * This method yields the <code>GrmgComponent</code>s of the underlying 
  * <code>GrmgScenario</code> of the <code>ScenarioDevice</code> filtered 
  * by the list of GRMG component names obtained from method 
  * <code>getDeviceComponents</code>    
  * @return the filtered <code>GrmgComponent</code> array associated to the 
  * <code>ScenarioDevice</code>
  **/
  public GrmgComponent[] getFilteredDeviceComponents() {

    ArrayList scenComponents = gScenario.getComponents();
    Vector filteredDevComps = new Vector();

    for (int j = 0; j < scenComponents.size(); j += 1) {
      if (gComponentNames.contains(((GrmgComponent)scenComponents.get(j)).getName()))
        filteredDevComps.add(scenComponents.get(j));
    }
    return (GrmgComponent[])filteredDevComps.toArray();
  }

  /**
  Adds a GRMG component name to the <code>ScenarioDevice</code>. 
  * @param  compName	the name of the (virtual) component to be added
  **/
  public void addDeviceComponent(String compName) {

    if (!gComponentNames.contains(compName))
      gComponentNames.add(compName);
  }

  /**
  Adds GRMG component names to the <code>ScenarioDevice</code>. 
  * @param  compNameList	the array of names of the (virtual) components to be added
  **/
  public void addDeviceComponents(String[] compNameList) {

    for (int j = 0; j < compNameList.length; j += 1)
      addDeviceComponent(compNameList[j]);
  }

  /**
  Returns the array of GRMG component names of the <code>ScenarioDevice</code>. 
  * @return  the <code>String</code> array of the names of the (virtual) components 
  * of the <code>ScenarioDevice</code>.
  **/
  public String[] getDeviceComponents() {

    return (String[])gComponentNames.toArray();
  }

  /**
  Adds a message to the current message of a GrmgComponent.
  * @param  comp			the GrmgComponent the message is added to
  * @param  param		the 9-element String array which forms the 
  * content of the message; if <code>null</code> no message will be added.
  **/
  public void addMessage(GrmgComponent comp, String[] param) {

    String[] parameters = { "", "", "", "", "", "", "", "", "" };

    if (param != null) {
      for (int j = 0; j < Math.min(param.length, 9); j += 1)
        parameters[j] = param[j];

      //comp.addMessage().setMessageParameters(parameters);
      comp.getCurrentMessage().setMessageParameters(parameters);
    }
  }

  /**
  Adds a message to the current message of the GrmgComponent.
  * @param  comp			the GrmgComponent the message is added to
  * @param  msgalert	message alert String, either 'OKAY' or 'ERROR'	 
  * @param  msgseverity message severity String, ranges from 000 - 255
  * @param  msgarea message area String, part of the SAP T100 message identification
  * @param  msgnumber message number String, ranges from 000 - 999. Number of the
  * T100 message. Message number and message area identify T100 messages
  * @param  msgparam1/4 Parameter (String) for a T100 message, if applicable
  * @param  msgtext	message text String. Leave empty if T100 message has to be reported, 
  * otherwise the message text will be reported instead
  **/
  public void addMessage(
    GrmgComponent comp,
    String msgalert,
    String msgseverity,
    String msgarea,
    String msgnumber,
    String msgparam1,
    String msgparam2,
    String msgparam3,
    String msgparam4,
    String msgtext) {

    comp.getCurrentMessage().setMessageParameters(
      msgalert,
      msgseverity,
      msgarea,
      msgnumber,
      msgparam1,
      msgparam2,
      msgparam3,
      msgparam4,
      msgtext);
  }

  /**
  Adds a new message tag to the GrmgComponent.
  * @param  comp			the GrmgComponent the message tag is added to
  * @param  msgalert	message alert String, either 'OKAY' or 'ERROR'	 
  * @param  msgseverity message severity String, ranges from 000 - 255
  * @param  msgarea message area String, part of the SAP T100 message identification
  * @param  msgnumber message number String, ranges from 000 - 999. Number of the
  * T100 message. Message number and message area identify T100 messages
  * @param  msgparam1/4 Parameter (String) for a T100 message, if applicable
  * @param  msgtext	message text String. Leave empty if T100 message has to be reported, 
  * otherwise the message text will be reported instead
  **/
  public void addMessageTag(
    GrmgComponent comp,
    String msgalert,
    String msgseverity,
    String msgarea,
    String msgnumber,
    String msgparam1,
    String msgparam2,
    String msgparam3,
    String msgparam4,
    String msgtext) {

    if (msgtext == null || msgtext.length() <= 0)
      msgtext = " - - - no message text set - - - ";

    if (comp.getCurrentMessage().getMessageText().equals("initial")) {
      addMessage(comp, msgalert, msgseverity, msgarea, msgnumber, msgparam1, msgparam2, msgparam3, msgparam4, msgtext);
    } else {
      GrmgMessage newMessage = new GrmgMessage();
      newMessage.setMessageParameters(
        msgalert,
        msgseverity,
        msgarea,
        msgnumber,
        msgparam1,
        msgparam2,
        msgparam3,
        msgparam4,
        msgtext);
      comp.addMessage(newMessage);
    }
  }

  /**
  Sets the initial data of a GrmgComponent.
  * If the component instance is <code>null</code> it will be set to "001" by default.
  * @param  comp		the GrmgComponent for which the data will be set
  * @param compinst the component instance
  **/
  public void setInitialComponentData(GrmgComponent comp, String compinst) {

    comp.setHost(
      httpreq.getScheme() + "://" + httpreq.getServerName() + ":" + httpreq.getServerPort() + httpreq.getContextPath());

    if (compinst == null)
      compinst = "001";

    comp.setInst(compinst);
  }

  /**
  Like method addMessage but also sets GrmgComponent's host and instance, if not yet set.
  * @param  comp			the GrmgComponent the message is added to
  * @param  param		the 9-element String array which constitute the message parameters
  * @param  compinst the component instance
  * content of the message
  **/
  public void setComponentData(GrmgComponent comp, String[] param, String compinst) {

    if (comp.getHost().equals("") | comp.getInst().equals("") | comp.getHost() == null | comp.getInst() == null)
      setInitialComponentData(comp, compinst);

    addMessage(comp, param);
  }

  /**
  Like method addMessage but also sets GrmgComponent's host and instance to default values,
  if not yet set.
  * @param  comp			the GrmgComponent the message is added to
  * @param  param		the 9-element String array which forms the 
  * content of the message
  **/
  public void setComponentData(GrmgComponent comp, String[] param) {

    setComponentData(comp, param, null);
  }

  /**
  Like method addMessageTag but also sets GrmgComponent's host and instance, if not yet set.
  * @param  comp			the GrmgComponent the message is added to
  * @param  msgalert	message alert String, either 'OKAY' or 'ERROR'	 
  * @param  msgseverity message severity String, ranges from 000 - 255
  * @param  msgarea message area String, part of the SAP T100 message identification
  * @param  msgnumber message number String, ranges from 000 - 999. Number of the
  * T100 message. Message number and message area identify T100 messages
  * @param  msgparam1/4 Parameter (String) for a T100 message, if applicable
  * @param  msgtext	message text String. Leave empty if T100 message has to be reported, 
  * otherwise the message text will be reported instead
  * @param compinst the component instance
  **/
  public void setComponentData(
    GrmgComponent comp,
    String msgalert,
    String msgseverity,
    String msgarea,
    String msgnumber,
    String msgparam1,
    String msgparam2,
    String msgparam3,
    String msgparam4,
    String msgtext,
    String compinst) {

    if (comp.getHost().equals("") | comp.getInst().equals("") | comp.getHost() == null | comp.getInst() == null)
      setInitialComponentData(comp, compinst);

    addMessageTag(comp, msgalert, msgseverity, msgarea, msgnumber, msgparam1, msgparam2, msgparam3, msgparam4, msgtext);
  }

  /**
  Like method addMessageTag but also sets GrmgComponent's host and instance to default values,
  if not yet set.
  * @param  comp			the GrmgComponent the message is added to
  * @param  msgalert	message alert String, either 'OKAY' or 'ERROR'	 
  * @param  msgseverity message severity String, ranges from 000 - 255
  * @param  msgarea message area String, part of the SAP T100 message identification
  * @param  msgnumber message number String, ranges from 000 - 999. Number of the
  * T100 message. Message number and message area identify T100 messages
  * @param  msgparam1/4 Parameter (String) for a T100 message, if applicable
  * @param  msgtext	message text String. Leave empty if T100 message has to be reported, 
  * otherwise the message text will be reported instead
  **/
  public void setComponentData(
    GrmgComponent comp,
    String msgalert,
    String msgseverity,
    String msgarea,
    String msgnumber,
    String msgparam1,
    String msgparam2,
    String msgparam3,
    String msgparam4,
    String msgtext) {

    setComponentData(comp, msgalert, msgseverity, msgarea, msgnumber, msgparam1, msgparam2, msgparam3, msgparam4, msgtext, null);
  }

  /**
  Sets the HttpServletRequest and HttpServletResponse of the given ScenarioDevice instance
  **/
  public void setHttpData(HttpServletRequest req, HttpServletResponse res) {

    httpreq = req;
    httpres = res;
    // httpses = req.getSession();
  }

  /**
  Sets the HttpServletRequest and HttpServletResponse of the given ScenarioDevice instance
  **/
  public void setDeviceData(HttpServletRequest req, HttpServletResponse res) {

    setHttpData(req, res);
    setRequest(req);
    setScenarioByRequest(getRequest());
  }

  /**
  Sets HttpSession attributes of the HTTP session underlying the given ScenarioDevice instance
  * @param  comp			the GrmgComponent 
  * @param  propName the name of the GrmgProperty 
  * @param  sessAttName the name of the attribute of the HttpSession which will be assigned 
  * the value of the GrmgProperty
  **/
  public void setSessionData(String compName, String propName, String sessAttName) {

    GrmgComponent gcomp = gScenario.getComponentByName(compName);
    GrmgProperty prop = gcomp.getPropertyByName(propName);

    if (prop != null)
      httpses.setAttribute(sessAttName, prop.getValue());
  }

  /**
  Sets HttpServletRequest attributes of the HTTP request underlying the given 
  * ScenarioDevice instance
  * @param  comp			the GrmgComponent 
  * @param  propName the name of the GrmgProperty 
  * @param  reqAttName the name of the attribute of the HttpServletRequest 
  * which will be assigned the value of the GrmgProperty
  **/
  public void setRequestData(String compName, String propName, String reqAttName) {

    GrmgComponent gcomp = gScenario.getComponentByName(compName);
    GrmgProperty prop = gcomp.getPropertyByName(propName);

    if (prop != null)
      httpreq.setAttribute(reqAttName, prop.getValue());
  }

  /**
  Adds a GrmgProperty to a GrmgComponent.
  * @param  comp			the GrmgComponent the property is added to
  * @param  name     the String representing the name of the property
  * @param  value    the String representing the value of the property
  **/
  public void addProperty(GrmgComponent comp, String name, String value) {

    comp.addProperty().setName(name);
    comp.addProperty().setValue(value);
  }

  private void debug(String s) {
    //add logging here
    System.out.println(s);
  }

  private void log(String s) {
    //add logging here
  }

  private void log(String s, Exception e) {
    //add logging here
  }
}
