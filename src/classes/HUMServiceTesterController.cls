/**********************************************************************************************************************************************
Apex Class Name  : HUMServiceTesterController
Version          : 1.0
Created Date     : February 04 2015
Function         : This class is used as a controller class for HUMServiceTesterPage to test the webservice.
Test Class       : HUMServiceTesterControllerTest
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma             21651                      02/04/2015                   Original Version
 * Vishal Verma             21651                      03/20/2015                   Added a map to store the size of columns.Changed line to check if the Service is not blank 63
 * Vishal Verma             21937                      03/27/2015                   Added !Test.isRunningTest() at line 182 before setting the certificate for the callout. 
                                                                                    As certificate cannot be inserted in Test class we need to by pass it in the class.
 * Gargey Sharma            22901                      05/05/2015                   REQ - 178697 : Added code to method fetchServiceDetails() to auto populate Request Type and Content Type
 * Gargey Sharma            23473                      05/22/2015                   REQ - 186152 : Added code to implement Virtual Switch in Service Tester                        
 * Vishal Verma             23473                      06/02/2015                   Fixing Code Review Comments for Review # 23473   
 * Gargey Sharma            26155                      09/04/2015                   REQ - 220235 : Service Tester Enhancement - Updated Error conditions when the Methods are not available for a service and user selects the Virtual Flag.
 **********************************************************************************************************************************************/
public with sharing class HUMServiceTesterController
{
    map<String,HUM_Webservice_Callout__c> mapWebserviceCallout = new map<String,HUM_Webservice_Callout__c>();
  
    /*
     * Method Name : HUMServiceTesterController
     * Description : Constructor for the class
     * Return type : None
     * Paramater   : None 
     */
    public HUMServiceTesterController()
    {
        sContent = '';
        sMethodsAvailable = '';
        sSelectedMethod = '';
        sService = '';
        init();
        initVirtual();
        mapWebserviceCallout = HUM_Webservice_Callout__c.getAll();
        mapSize = new Map<String,Integer>();
        mapSize.put(label.HumServiceUpdateCnt,1);
    }

    public map<String,Integer> mapSize {get;set;}
    public String sContent { get; set; }
    public String sStatus { get; set; }
    public Integer iStatusCode {get;set;}
    public String sbody { get; set; }
    public String sEndpoint {get; set;}
    public String sSOAPAction{get; set;}
    public Double dTotalTime {get; set;}
    public String sSelectedMethod { get; set; }    
    public list<String> lstXmlResponse {get;set;}
    public String sService {get;set;}
    public String sMethodsAvailable{get;set;}
    public Boolean bVFlag{get;set;} 
    public String sVirtualURL{get;set;}
    public Boolean bVswitchRender{get;set;}
    public Boolean bpagerender{get;set;}
    
    
    /*
     * Method Name : executeRequest
     * Description : Method to execute the webservice request and dispaly response on the page
     * Return type : void
     * Paramater   : None 
     */
    public void executeRequest() 
    {
        try
        {
            bVswitchRender = false;
            if(String.isBlank(sService))
            {  
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, Label.HUMSelectService));
            }
            else
            {
                if(mapWebserviceCallout.containskey(sService))
                { 
                    DateTime requestTime = DateTime.now();
                    HTTPResponse res = HUMCalloutUtilityHelper.callWebservice(sEndpoint,mapWebserviceCallout.get(sService).Certificate_Name__c,sbody,sSelectedMethod,sSOAPAction,sContent);
                    DateTime responseTime = DateTime.now(); 
                    if(res!=null) { lstXmlResponse.add(res.getBody());}
                    if(res.getBody().split('><').size()<1000)
                    {
                        lstXmlResponse = res.getBody().split('><');
                    }
                    dTotalTime = Math.Floor(Decimal.valueOf((responseTime.getTime() - requestTime.getTime()))) ;
                    sStatus = res.getStatus();
                    iStatusCode = res.getStatusCode(); 
                } 
            }
        }
        catch(Exception e)
        {    
            HUMExceptionHelper.logErrors(e,'HUMServiceTesterController','executeRequest');
        }     
    }
           
    /*
     * Method Name : getServices
     * Description : Method to display all the available webservices from the custom setting
     * Return type : List<SelectOption>
     * Paramater   : None 
     */ 
    public List<SelectOption> getServices()
    {
        List<SelectOption> lstOptions = new List<SelectOption>();
        lstOptions.add(new SelectOption('',System.Label.HUMSelectService));
        for(String calloutService : mapWebserviceCallout.keyset())
        {
            if(mapWebserviceCallout.get(calloutService).Enable_Service_Tester__c == true)
            {
                lstOptions.add(new SelectOption(calloutService, calloutService));
            } 
        }
        lstOptions.sort();
        return lstOptions;
    }
    
    /*
     * Method Name : getMethodsAvailable
     * Description : Method to display all the available methods for a webservice from the custom setting
     * Return type : List<SelectOption>
     * Paramater   : None 
     */ 
    public List<SelectOption> getMethodsAvailable()
    {
        List<SelectOption> lstOptions = new List<SelectOption>();
        if(mapWebserviceCallout.containskey(sService) && mapWebserviceCallout.get(sService).Methods_Available__c!=null)
        {
            sMethodsAvailable = mapWebserviceCallout.get(sService).Methods_Available__c;
            List<String> methods = sMethodsAvailable.split(';');
            lstOptions.add(new SelectOption('','Please Select a Method'));
            for(String availablemethods : methods)
            {
                lstOptions.add(new SelectOption(availablemethods, availablemethods)); 
            }
            lstOptions.sort();
        }
        return lstOptions;
    } 
    
    /*
     * Method Name : getRequestType
     * Description : Method to display all the available request type e.g.(GET,PUT,POST,DELETE)
     * Return type : List<SelectOption>
     * Paramater   : None 
     */
    public List<SelectOption> getRequestType()
    {
        List<SelectOption> lstOptions = new List<SelectOption>();
        lstOptions.add(new SelectOption(HUMConstants.GET, HUMConstants.GET));
        lstOptions.add(new SelectOption(HUMConstants.PUT, HUMConstants.PUT));
        lstOptions.add(new SelectOption(HUMConstants.POST, HUMConstants.POST));
        lstOptions.add(new SelectOption(HUMConstants.DEL, HUMConstants.DEL));
        return lstOptions;
    }
    
    /*
     * Method Name : getContentType
     * Description : Method to display all the available content type 
     * Return type : List<SelectOption>
     * Paramater   : None 
     */
    public List<SelectOption> getContentType()
    {
        List<SelectOption> lstOptions = new List<SelectOption>();        
        lstOptions.add(new SelectOption(HUMConstants.Application_json,HUMConstants.Application_json));
        lstOptions.add(new SelectOption(HUMConstants.Application_xml,HUMConstants.Application_xml));
        lstOptions.add(new SelectOption(HUMConstants.Text_xml,HUMConstants.Text_xml));
        lstOptions.add(new SelectOption(HUMConstants.Application_x_www_form_urlencoded,HUMConstants.Application_x_www_form_urlencoded));
        lstOptions.add(new SelectOption(HUMConstants.text_plain,HUMConstants.text_plain));
        return lstOptions;
    }
    
    /*
     * Method Name : fetchServiceDetails
     * Description : Method to auto populate Endpoint, Response Type, Content Type and body on the basis of the selected Service 
     * Return type : Void
     * Paramater   : None 
     */
    public void fetchServiceDetails()
    {
        init();
        initVirtual();
        bpagerender = mapWebserviceCallout.containskey(sService)? true: false;
        if(mapWebserviceCallout.containskey(sService)) 
        { 
            sSOAPAction = mapWebserviceCallout.get(sService).SOAP_Action__c;
            sEndpoint = mapWebserviceCallout.get(sService).End_Point_URL__c;
            sSelectedMethod = mapWebserviceCallout.get(sService).Request_Type__c;
            sContent = mapWebserviceCallout.get(sService).Content_Type__c;
            if(mapWebserviceCallout.get(sService).SampleParameter__c != null) 
            { 
                sEndpoint = sEndpoint + '?' + mapWebserviceCallout.get(sService).SampleParameter__c;
            }
        }
        
        List<StaticResource> lstStaticResource = [Select id ,body from StaticResource where name = :sService limit :1];
        if(lstStaticResource != null &&!lstStaticResource.isEmpty()) { sbody = lstStaticResource[0].body.toString(); }
        
    }
    
    /*
     * Method Name : getURL
     * Description : Method to Get Virtual URL from webservice
     * Return type : void
     * Paramater   : None 
     */
    public void getURL() 
    {
        try
        {
            bVswitchRender = true;
            if(String.isBlank(sService))
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, Label.HUMSelectServiceAvailableMethods));
            }
            else
            {
                initVirtual();
                String str = getServiceURI();
                string sErrorMsg = '';
                HTTPResponse res = HUMCalloutUtilityHelper.callWebservice(mapWebserviceCallout.get('VirtualSwitch_Get').End_Point_URL__c,mapWebserviceCallout.get(sService).Certificate_Name__c,str,HUMConstants.POST,'', '');
                if(res.getStatusCode() == 200 || res.getStatusCode() == 202)
                {
                    Boolean bCheck = false;
                    List<Dom.XMLNode> lstChildElements = HUMCalloutUtilityHelper.fetchChildElements(res.getBodyDocument().getRootElement());
                    if(!lstChildElements.isEmpty())
                    {
                        for(Dom.XmlNode virConfig:lstChildElements)
                        {
                            if(virConfig.getname() == 'Status' && virConfig.gettext() == 'Error')
                            {
                                sErrorMsg = HUMCalloutUtilityHelper.fetchChild(res.getBodyDocument().getRootElement(),'Message');
                                break;
                            }
                            else if(virConfig.getName() == 'VirtualConfigDetails')
                            {
                                if(sMethodsAvailable!=null)
                                {
                                    if(HUMCalloutUtilityHelper.fetchChild(virConfig,'Operations').contains(sMethodsAvailable))
                                    {
                                        if(HUMCalloutUtilityHelper.fetchChild(virConfig,'VirtualFlag').equals('Y')){ bVFlag = true;}
                                        sVirtualURL = HUMCalloutUtilityHelper.fetchChild(virConfig,'VirtualURL');
                                        bCheck = true;  
                                        break;
                                    }
                                }
                                else
                                {
                                    sErrorMsg = Label.HUMSelectServiceAvailableMethods;
                                }
                            }
                        }
                    }
                    
                    if(bCheck == true)
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, Label.HUMVSwitchGetSuccess));
                    }
                    else
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, sErrorMsg));
                    }
                }
                else
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, Label.HUMInquiryHistoryGenericError));
                }
            }   
        }
        catch(Exception e)
        {    
            HUMExceptionHelper.logErrors(e,'HUMServiceTesterController','getURL');
        }     
    }
    
    /*
     * Method Name : setURL
     * Description : Method to Set Virtual URL
     * Return type : void
     * Paramater   : None 
     */
    public void setURL() 
    {
        try
        {
            bVswitchRender = true;
            if(String.isBlank(sService) || String.isBlank(sMethodsAvailable))
            {  
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, 'Virtual Flag cannot be set for this service.'));
            }   
            else
            {
                String VirtualFlag = 'N';
                if(bVFlag == true){VirtualFlag = 'Y';}
                String body = getServiceURI() + '&'+'OperationName='+sMethodsAvailable+'&'+'VirtualFlag='+VirtualFlag+'&'+'VirtualURL='+sVirtualURL;
                HTTPResponse res = HUMCalloutUtilityHelper.callWebservice(mapWebserviceCallout.get('VirtualSwitch_Update').End_Point_URL__c,mapWebserviceCallout.get(sService).Certificate_Name__c,body,HUMConstants.POST,'', '');
                if(res.getStatusCode() == 200 || res.getStatusCode() == 202)
                {
                    String Status = HUMCalloutUtilityHelper.fetchChild(res.getBodyDocument().getRootElement() , 'Status');
                    if(!String.isBlank(Status))
                    {
                        if(Status.equals('Error'))
                        { 
                            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, HUMCalloutUtilityHelper.fetchChild(res.getBodyDocument().getRootElement() , 'Message')));
                        }
                        else
                        {
                            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, Label.HUMVSwitchUpdateSuccess));
                        }
                    }
                    else
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, Label.HUMVSwitchError));
                    }
                }
                else
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, Label.HUMInquiryHistoryGenericError));
                }
            }
        }
        catch(Exception e)
        {    
            HUMExceptionHelper.logErrors(e,'HUMServiceTesterController','setURL');
        } 
    }
         
     /*
     * Method Name : getServiceURI
     * Description : Method to get ServiceURI from Endpoint
     * Return type : String
     * Paramater   : None 
     */
     public String getServiceURI()
     {
        List<String> URI = new List<String>();
        URI = mapWebserviceCallout.get(sService).End_Point_URL__c.split('crmservicews.humana.com');
        return !URI.isEmpty() ?  ('ServiceURI='+ URI[1]) : null;
     }
     
     /*
     * Method Name : initVirtual
     * Description : Method to initialize Virtual Switch Varialbles
     * Return type : void
     * Paramater   : None 
     */
     public void initVirtual()
     {
        sVirtualURL = '';
        bVFlag = false;
     }
     
     /*
     * Method Name : init
     * Description : Method to initialize Varialbles
     * Return type : void
     * Paramater   : None 
     */
     public void init()
     {
        sEndpoint = '';
        sbody = '';
        sSOAPAction = '';
        dTotalTime = null;
        sStatus = '';
        iStatusCode = null;
        lstXmlResponse = new List<String>();
        bpagerender = false;
     }
}