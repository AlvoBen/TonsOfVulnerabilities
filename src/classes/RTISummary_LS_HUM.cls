/**************************************************************************** 
Apex Class Name  : RTIInteraction_LS_HUM
Version          : 1.0  
Created Date     : 07/12/2022
Function         : Controller class RTIDetailHum
Test class       : RTIInteraction_LT_HUM
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane 							  7/12/2022				     US-3298002 RTI Implementation
* Divya Bhamre                                    01/19/2023                 US -T1PRJ0170850 - MF 21566 - TECH - SF- Fix RTI Print Issue
* Divya Bhamre                                    2/23/2022                  US - 4286513
* Suraj Patil                                     06/02/2023                 US - 4656698
***************************************************************************************************/
public with sharing class RTISummary_LS_HUM {
    private String sBody ='' ;
    private Integer recordLimit = 200;
    private List<RTISummary_DTO_HUM > oRTIList = New List<RTISummary_DTO_HUM >();
    @TestVisible private Integer iTotalRec = 0;
    public static List<RTISummary_DTO_HUM> lstRTIInteractions{get; private set;}
    public static List<RTIPrintSummary_DTO_HUM> lstRTIInteractionsDetails {get; private set;}
 	public static  String iRTICards { get; set; }
    public static String sSelectedChannel {get;set;}
    public static boolean bIsPageOnLoad {get; set;}
    private static RTISummary_LS_HUM oServiceRTI = new RTISummary_LS_HUM();
    private static List<RTIPrintSummary_DTO_HUM> lstRTIDto = New List<RTIPrintSummary_DTO_HUM>(); 
    private static Integer iTotalRec;
    
    Public List<Error_Log__c> lstLogErrors
    {
        get
        {
          if(lstLogErrors == Null){lstLogErrors = new List<Error_Log__c>();}return lstLogErrors;
        }
        set;
    }
     
    
    /*
    *  This method is used to form http request for RTI summary service
    *  @name   setupRequest
    *  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
                           sEnterpriseId - enterprise Id of the account, 
                           Startdate - Selected startdate, enddate - Selected endDate
                           sChannel - selected channel
    *  @return HttpRequest - request for RTI summary service
    *  @throws setupRequest exception
    */
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcHeaderRTI, String sEnterpriseId, String startDate, String endDate, String sChannel, Integer iSkipNmbr, String sCategoryC, String sInteractingC, String sDirection, List<RTIApplicationValues_H_HUM.AttributeValue> attrValue)
    {
        
        HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody = buildRequest(sEnterpriseId,startDate,endDate,sChannel,iSkipNmbr,sCategoryC,sInteractingC,sDirection,attrValue);
           
            if(String.isNotBlank(sBody))
            {
                request.setMethod(oSvcHeaderRTI.Request_Type__c); 
                request.setHeader('content-type', oSvcHeaderRTI.Content_Type__c);  
                request.setHeader('Connection','keep-alive');
                if(!Test.isRunningTest())    
                {
                    request.setClientCertificateName(oSvcHeaderRTI.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcHeaderRTI.Timeout__c));
                request.setEndpoint(oSvcHeaderRTI.End_Point_URL__c);
                request.setBody(sBody);
                orequest = request;
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTISummary_LS_HUM', 'setupRequest');
            orequest = null;
        }
        return orequest; 
    }
    /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   buildRequest
    *  @param  String sEnterpriseId - Person id of the member
               String startDate, String endDate - selected start and end date
               String sChannel - Selected Channel
    *  @return string - Returns the serialized body of the request
    *  @throws NA
    */ 
    @testvisible private string buildRequest(String sEnterpriseId, String startDate, String endDate, String sChannel, Integer iSkipNmbr, String sCategoryC, String sInteractingC, string sDirection,List<RTIApplicationValues_H_HUM.AttributeValue> attrValue)
    {    
        
        string sJsonbody = '';
        try
        {
            RTIServiceSummaryRequest_H_HUM oRequest = new RTIServiceSummaryRequest_H_HUM(); 
            RTIServiceSummaryRequest_H_HUM.GetInteractionsSummary getRTISummary = new RTIServiceSummaryRequest_H_HUM.GetInteractionsSummary();
            List<RTIServiceSummaryRequest_H_HUM.Channel> channelList = new  List<RTIServiceSummaryRequest_H_HUM.Channel>();
            channelList = getRTIChannel(sChannel,channelList,sCategoryC,sDirection,attrValue);
            
            if(!channelList.isEmpty())
            {
                getRTISummary.channels = channelList;
            }
            getRTISummary.personId = sEnterpriseId;
            getRTISummary.interactingWithCode = sInteractingC;
            getRTISummary.endDate = endDate; 
            getRTISummary.startDate = startDate;
            getRTISummary.recordLimit = recordLimit;
            getRTISummary.skip = iSkipNmbr;
            
            if(isNonHOUser())
            {            
                getRTISummary.SecurityProfile = 'GEN';            
            }
            else
            {

                getRTISummary.SecurityProfile = 'HO';
            }
            
            oRequest.GetInteractionsSummary = getRTISummary;
            sJsonbody = JSON.serialize(oRequest); 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTISummary_LS_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
     /*
    *  This method is used to get list of channelDetails based on Selected channel value
    *  @name   getRTIChannel
    *  @param sChannel - Selected channel value, List<RTIServiceSummaryRequest_H_HUM.Channel> - list of Channel objects
    *  @return List<RTIServiceSummaryRequest_H_HUM.Channel> - List of channel object wrappers with the channel details
    *  @throws NA 
    */
    private List<RTIServiceSummaryRequest_H_HUM.Channel> getRTIChannel(String sChannel, List<RTIServiceSummaryRequest_H_HUM.Channel> channelList, String sCategoryC, string sDirection, List<RTIApplicationValues_H_HUM.AttributeValue> attrValue)
    {
        
        Boolean bIsAll = sChannel.equalsIgnoreCase('All');
        
        for(RTIApplicationValues_H_HUM.AttributeValue oChannel: attrValue)
        {
            if((oChannel.value.equalsIgnoreCase(sChannel) || bIsAll) && oChannel.value!='All' )
            {
                RTIServiceSummaryRequest_H_HUM.Channel oRTIChannel = new RTIServiceSummaryRequest_H_HUM.Channel();
                oRTIChannel.code = oChannel.Value;
                oRTIChannel.direction = sDirection;
                oRTIChannel.isFinal = 'true';
                if(sChannel == 'WEB,WB' || sChannel== 'PRINT')   
                    oRTIChannel.categoryCode = sCategoryC;
                else
                    oRTIChannel.categoryCode = oChannel.channelCategoryCode;
                
				if(oChannel.Label != 'PRINT' && ((oChannel.Label == 'VAT'&& oRTIChannel.direction == 'O') || oRTIChannel.direction=='I'))
                    oRTIChannel.isFinal = 'ALL';                    
                oRTIChannel.dispositioncd = oChannel.dispositionCode;
                oRTIChannel.statuscd = oChannel.statusCode;
                channelList.add(oRTIChannel);
            }
        }
       return channelList;
    }
    /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   isNonHOUser
    *  @param  NA
    *  @return Boolean
    *  @throws NA
    */  
    private Boolean isNonHOUser()
    {
        ClaimsSummaryHOCheck_C_HUM_V2 ObjClaimsSummaryHOCheck_C_HUM_V2 = New ClaimsSummaryHOCheck_C_HUM_V2();
        String sUserId = UserInfo.getUserId(); 
        return ObjClaimsSummaryHOCheck_C_HUM_V2.isNonHoUser(sUserId);        
    }
    
    /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   processResponse
    *  @param  string(Json response) - Body of the response in JSON format
    *  @return List<RTISummaryDTO_H_HUM> - WrapperList containing the response details
    *  @throws NA
    */ 
    public List<RTISummary_DTO_HUM > processResponse(string response) 
    {  
        
        RTIServiceSummaryResponse_H_HUM objRTIresp = new RTIServiceSummaryResponse_H_HUM();
        objRTIresp = RTIServiceSummaryResponse_H_HUM.parse(response);
        if(objRTIresp.InteractionSummaryResponse != Null)
        {
            oRTIList = parseServiceResponse(objRTIresp);
        }
        
        return oRTIList;
    }
    
    /*
    * Method Name   :  responseparse
    * Description   :  This method is used to check response
    * Return Type   :  void 
    * Parameters    :  HttpResponse obj
    */
    public Object responseParse(List<string> labels,Object State)
    {
        HttpResponse sResponse = Continuation.getResponse(labels[0]);
        iTotalRec = 0;
        lstRTIInteractions =  new List<RTISummary_DTO_HUM>();

        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == 200 || statusCode == 202)
        {
            lstRTIInteractions = processResponse(sResponse.getbody());
            iTotalRec = oServiceRTI.totalRec();
            iRTICards = String.valueOf(iTotalRec);
            if((lstRTIInteractions==null||lstRTIInteractions.isEmpty()))
            {
                string sError;
                return null;
            }
            else{return lstRTIInteractions;}
                
        }
        else
        {
           throw new HUMCustomException('Service Error');
        }
        
    }    
    /*
    *  This method is for display the pagination total record
    *  @name   totalRec
    *  @param sChannel NA
    *  @return NA
    */
    public Integer totalRec()
    {
        return iTotalRec;
    }
    /*
    *  This method is used to parse the reponse and assign it to the wrapper class objects
    *  @name   parseServiceResponse
    *  @param  RTIServiceSummaryResponse_H_HUM - The Response object
    *  @return List<RTISummary_DTO_HUM > - List of Wrapper class objects
    *  @throws NA
    */  
    private List<RTISummary_DTO_HUM > parseServiceResponse(RTIServiceSummaryResponse_H_HUM objRTIresp)
    {
        List<RTISummary_DTO_HUM > tempListRTISummary = new List<RTISummary_DTO_HUM>();
        RTIServiceSummaryResponse_H_HUM.InteractionSummaryResponse oInteractionSummaryResponse = objRTIresp.InteractionSummaryResponse;
        RTIServiceSummaryResponse_H_HUM.InteractionSummaryResult oInteractionSummaryResult = oInteractionSummaryResponse.InteractionSummaryResult;
        List<RTIServiceSummaryResponse_H_HUM.Interaction> oInteractionsList = oInteractionSummaryResult.Interactions;
        iTotalRec = oInteractionSummaryResult.nbRecords;
        tempListRTISummary = buildWrapperList(oInteractionsList,iTotalRec);
        return tempListRTISummary;
    }
    
    /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperList
    *  @param  List<RTIServiceSummaryResponse_H_HUM.Interaction> -List of RTIServiceSummaryResponse_H_HUM.Interaction objects with the interaction details 
    *  @return List<RTISummary_DTO_HUM > - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */  
    private List<RTISummary_DTO_HUM > buildWrapperList(List<RTIServiceSummaryResponse_H_HUM.Interaction> oInteractionsList, integer iRecTotal)
    {
		Boolean bMdcSwitch = CRMFunctionalityenabler_H_HUM.Checkflagvalue('RTI_MDC_Switch');
        Integer Indexcount = 0;
        List<RTISummary_DTO_HUM > listRTISummary = new List<RTISummary_DTO_HUM>();
        
        for(RTIServiceSummaryResponse_H_HUM.Interaction obj : oInteractionsList)
        {
            RTISummary_DTO_HUM objWrapper = new RTISummary_DTO_HUM();
            objWrapper.sNbRecords = iRecTotal;
            objWrapper.index = String.valueOf(Indexcount);
            objWrapper.sInteractionkey =  obj.interactionkey;
            objWrapper.sInteractionsrckey = obj.interactionsrckey;
            objWrapper.sSourceCode = obj.sourceCode;
            objWrapper.sChannelCode = obj.channelCode;
            objWrapper.sChannelDescription = obj.channelDescription;
            objWrapper.sSourceDescription = obj.sourceDescription;
            objwrapper.sdirectionCode=obj.directionCode;
            if(String.isNotEmpty(objWrapper.sChannelCode))
            {   
                objWrapper.title = obj.channelDescription;
                
                
                if(objWrapper.sChannelCode.equalsIgnoreCase('IV'))
                {
                    objWrapper.title = obj.iv.interactingWithDesc;
                    objWrapper.dDateAndTime = obj.iv.enddatetime;
                }
                if(objWrapper.sChannelCode.equalsIgnoreCase('PRINT'))
                {
                    objWrapper.title = obj.print.categoryDescription;
                    
                    objWrapper.dDateAndTime = obj.print.EndDateTime;
                    objWrapper.subtitle = obj.print.busineesSubArea;
                    objWrapper.viewLabel = 'View';
                    objWrapper.sMessageName = obj.print.MessageDefinitionDescription;
                    objwrapper.trackingID = obj.print.TrackingID;
                    objwrapper.sMemberCardId = obj.print.MemberCardId;
                    objwrapper.sDependentCode = obj.print.DependentCode;
                    objwrapper.sPlatformCode = obj.print.PlatformCode;
                    objwrapper.sSDRPersonId = obj.print.SDRPersonId;
                    objwrapper.sPersonGenKey = obj.print.PersonGenKey;
                    objwrapper.sMessageFulfillmentId = obj.print.MessageFulfillmentId;
                    objwrapper.bPrintable = obj.print.Printable;
                    objwrapper.sMsgDefCode = obj.print.MessageDefinitionIDCD;
                }
                     
                if(objWrapper.sChannelCode.equalsIgnoreCase('MB'))
                {
                   objWrapper.dDateAndTime =  obj.MB.EndDateTime; 
                }
                
                if(objWrapper.sChannelCode.equalsIgnoreCase('MA'))
                {
                   objWrapper.dDateAndTime =  obj.MA.EndDateTime;
                }
                
                if(objWrapper.sChannelCode.equalsIgnoreCase('WB'))
                {
                    objWrapper.dDateAndTime =  obj.WB.EndDateTime;
                                         
                    if(objWrapper.sDirectionCode == 'O'){
                        objWrapper.title = obj.WB.categoryDescription;
                        objWrapper.sMessageName = obj.WB.MessageDefinitionDescription;
                        objWrapper.subtitle = obj.WB.BusineesSubArea;
                        objWrapper.viewLabel ='View'; 
                        objwrapper.trackingID = obj.WB.TrackingID;
                        if(bMdcSwitch){
                            objwrapper.sMsgDefCode = obj.WB.MessageDefinitionIDCD;
                        }
                    }
                }
                if( objWrapper.sChannelCode.equalsIgnoreCase('WEB'))  
                {   
                    objWrapper.dDateAndTime =  obj.WEB.EndDateTime;
                                          
                    if(objWrapper.sDirectionCode == 'O'){
                        objWrapper.title = obj.WEB.categoryDescription;
                        objWrapper.sMessageName = obj.WEB.MessageDefinitionDescription;
                        objWrapper.subtitle = obj.WEB.BusineesSubArea; 
                        objWrapper.viewLabel ='View';
                        objwrapper.trackingID = obj.WEB.TrackingID;
                        if(bMdcSwitch){
                            objwrapper.sMsgDefCode = obj.WEB.MessageDefinitionIDCD;
                        }
                    }          
                }
                if(objWrapper.sChannelCode.equalsIgnoreCase('VAT'))
                {                   
                
                objWrapper.dDateAndTime =  obj.Actiontime;  
                objWrapper.campaignName = obj.vat.campaignName; 
                objWrapper.statusDescription = obj.vat.statusDescription;
                objwrapper.statusCode = obj.vat.statusCode;
                objwrapper.PhoneNumber = obj.vat.phoneNumber;
                objWrapper.title = obj.channelDescription;
                objWrapper.viewLabel ='View'; 
                objWrapper.subStatusDesc = obj.vat.SubStatusDesc;
                objwrapper.trackingID = obj.vat.TrackingID;
                objwrapper.sMsgDefCode = obj.vat.MessageDefinitionIDCD;
                }
                
                if(objWrapper.sChannelCode.equalsIgnoreCase('EMAIL'))
                {
                   
                    objWrapper.dDateAndTime = obj.email.EndDateTime;
                    objWrapper.subtitle = obj.email.busineesSubArea;
                    objWrapper.viewLabel = 'View';
                    objWrapper.sMessageName = obj.email.MessageDefinitionDescription;
                    objWrapper.BusineesSubArea = obj.email.BusineesSubArea;
                    objWrapper.statusDescription = obj.email.statusDescription;
                    objWrapper.sRecipientName = obj.email.RecipientEmail;
                    objwrapper.trackingID = obj.email.TrackingID;
                    if(bMdcSwitch){
                        objwrapper.sMsgDefCode = obj.email.MessageDefinitionIDCD;
                    }
                }
                if(objWrapper.sChannelCode.equalsIgnoreCase('EM'))
                {
                    
                    objWrapper.dDateAndTime = obj.em.EndDateTime;
                    objWrapper.subtitle = obj.em.busineesSubArea;
                    objWrapper.viewLabel = 'View';
                    objWrapper.sMessageName = obj.em.MessageDefinitionDescription;
                    objWrapper.BusineesSubArea = obj.em.BusineesSubArea;
                    objWrapper.statusDescription = obj.em.statusDescription;
                    objWrapper.sRecipientName = obj.em.RecipientEmail;
                    objwrapper.trackingID = obj.em.TrackingID;
                    if(bMdcSwitch){ 
                        objwrapper.sMsgDefCode = obj.em.MessageDefinitionIDCD;
                    }
                }
                
                if(objWrapper.sChannelCode.equalsIgnoreCase('SMS'))
                {
                    objWrapper.title = 'Text';
                    
                    objWrapper.dDateAndTime = obj.sms.EndDateTime;
                    objWrapper.sMessageName = obj.sms.MessageDefinitionDescription;
                    objWrapper.subtitle = obj.sms.busineesSubArea;
                    objWrapper.statusDescription = obj.sms.statusDescription;
                    if (String.isNotEmpty(obj.sms.recipientPhone) && obj.sms.recipientPhone.isNumeric() && obj.sms.recipientPhone.length() >= 6)
                    {
                        objwrapper.PhoneNumber = '(' + obj.sms.recipientPhone.substring(0,3) + ') ' +
                            obj.sms.recipientPhone.substring(3,6) + '-' + obj.sms.recipientPhone.substring(6); 
                    }
                    else{
                        objwrapper.PhoneNumber = obj.sms.recipientPhone;
                    }
                    objWrapper.viewLabel = 'View';
                    objwrapper.trackingID = obj.sms.TrackingID;
                    if(bMdcSwitch){
                        objwrapper.sMsgDefCode = obj.sms.MessageDefinitionIDCD;
                    }
                }  
                
            }
             indexcount++;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
    }
    
   /*
    *  This method Is used to form http request for RTI print service
    *  @name   setupRequest
    *  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
                             sInteractionKey, channelCode, iSkipNmbr, recordLimit                        
    *  @return HttpRequest - request for RTI print service
    *  @throws setupRequest exception
    */
    Public HttpRequest setupInteractionDetailsRequest(HttpRequest oRequest, HUM_Webservice_Callout__c oSvcHeaderRTI, String sInteractionKey, String sChannelCode, Integer iSkipNmbr, Integer iRecordLimit)
    { 
        HttpRequest oHttpRequest = New HttpRequest(); 
        try
        { 
            sBody = buildInteractionDetailsRequest(sInteractionKey, sChannelCode, iSkipNmbr, iRecordLimit);
            If (String.isNotBlank(sBody))   
            {
                oRequest.setMethod(oSvcHeaderRTI.Request_Type__c);
                oRequest.setHeader('content-type', oSvcHeaderRTI.Content_Type__c);  
                oRequest.setHeader('Connection', 'keep-alive');
                If (!Test.isRunningTest())  
                {
                    oRequest.setClientCertificateName(oSvcHeaderRTI.Certificate_Name__c);
                }
                oRequest.setTimeout(Integer.valueof(oSvcHeaderRTI.Timeout__c));
                oRequest.setEndpoint(oSvcHeaderRTI.End_Point_URL__c);
                oRequest.setBody(sBody);  
                oHttpRequest = oRequest;
            }  
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'RTIPrintSummary_S_HUM' , 'setupRequest');
        }
        return oHttpRequest; 
    }
   
    /*
    *  This method Is used to build the required inputs needed to hit the RTI print service
    *  @name   buildInteractionDetailsRequest
    *  @param  String sInteractionKey
               String channelCode, String iSkipNmbr, Integer recordLimit            
    *  @return string - Returns the serialized body of the request
    *  @throws NA
    */ 
    private string buildInteractionDetailsRequest(String sInteractionKey, String sChannelCode, Integer iSkipNmbr, Integer iRecordLimit)
    {   
        String sJsonbody = '';
        try
        { 
            RTIServiceDetailRequest_H_HUM  oRequest = New RTIServiceDetailRequest_H_HUM(); 
            RTIServiceDetailRequest_H_HUM.GetInteractionDetails getRTISummary = New RTIServiceDetailRequest_H_HUM.GetInteractionDetails();
            getRTISummary.Interactionkey = sInteractionKey;
            if (sChannelCode == 'Text' ){
               sChannelCode = 'SMS'; 
            }
            getRTISummary.channelCode = sChannelCode;
            getRTISummary.RecordLimit = iRecordLimit;
            getRTISummary.skip = iSkipNmbr;
            oRequest.GetInteractionDetails = getRTISummary;
            sJsonbody = JSON.serialize(oRequest);
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTIPrintSummary_S_HUM' , 'buildRequest');
        }
        Return sJsonbody;
    }
    
    /*
    * Method Name   :  responseParse
    * Description   :  This method Is used to get response from service.
    * Return Type   :  void
    * Parameters    :  NA
    */
    public RTIPrintSummary_DTO_HUM responseParseDetails(List<string> labels,Object state)
    {   
        HttpResponse sResponse = Continuation.getResponse(labels[0]);
        string stateValues = string.valueOf(state);
	    string[] stateValue = stateValues.split('_');
	    string channelCode = stateValue[0];
	    string categoryDesc = stateValue[1];
        lstRTIInteractionsDetails = New List < RTIPrintSummary_DTO_HUM > ();
        RTIPrintSummary_DTO_HUM RTIInteraction = New RTIPrintSummary_DTO_HUM();
        Integer iStatusCode = sResponse.getStatusCode();                     
        If (iStatusCode == 200 || iStatusCode == 202)
        {
            lstRTIInteractionsDetails = processDetailsResponse(sResponse.getbody(),channelCode,categoryDesc);                        
            If (lstRTIInteractionsDetails == null || lstRTIInteractionsDetails.isEmpty() )
            {
                return null;
            }
            Else
            {            
                For (RTIPrintSummary_DTO_HUM obj : lstRTIInteractionsDetails)   
                { 
                  RTIInteraction.sdocumentViewUrl = obj.sdocumentViewUrl;  
                  RTIInteraction.sdocumentType = obj.sdocumentType;
                }
                return RTIInteraction;
            }
        }
        Else
        {
            return null;
        }
        
    }
  /*
    *  This method Is used to Process the Json response from service to Wrapper List
    *  @name   processDetailsResponse
    *  @param  string(Json response) - Body of the response in JSON format
    *  @return List<RTIPrintSummary_DTO_HUM> - WrapperList containing the response details
    *  @throws NA
    */ 
    Public List<RTIPrintSummary_DTO_HUM> processDetailsResponse(String response, string channelCode, string categoryDesc) 
    {          
        RTIServiceDetailResponse_H_HUM objRTIresp = New RTIServiceDetailResponse_H_HUM();
        List<RTIPrintSummary_DTO_HUM> listRTIResp = new List<RTIPrintSummary_DTO_HUM>();
        objRTIresp = RTIServiceDetailResponse_H_HUM.parse(response);     
        if(objRTIresp.InteractionDetailResponse != Null)
        {
            lstRTIDto = parseDetailsServiceResponse(objRTIresp,channelCode,categoryDesc); 
        }     
        return lstRTIDto;
    }
      
   /*
    *  This method is used to parse the reponse and assign it to the wrapper class objects
    *  @name   parseDetailsServiceResponse
    *  @param  RTIServiceDetailResponse_H_HUM - The Response object
    *  @return List<RTIPrintSummary_DTO_HUM> - List of Wrapper class objects
    *  @throws NA
    */
    private List<RTIPrintSummary_DTO_HUM> parseDetailsServiceResponse(RTIServiceDetailResponse_H_HUM objRTIresp,string channelCode,string categoryDesc)
    {        
        List<RTIPrintSummary_DTO_HUM> tempListRTISummary = new List<RTIPrintSummary_DTO_HUM>();            
        RTIServiceDetailResponse_H_HUM.InteractionDetailResponse oInteractionSummaryResponse = objRTIresp.InteractionDetailResponse;        
        RTIServiceDetailResponse_H_HUM.InteractionDetailResult oInteractionSummaryResult = oInteractionSummaryResponse.InteractionDetailResult;                
  
        if(categoryDesc =='VAT' )        
        {
            List<RTIServiceDetailResponse_H_HUM.VAT> oInteractionsListVT = oInteractionSummaryResult.vaTs;            
            tempListRTISummary = buildWrapperListVT(oInteractionsListVT);           
        }        
        if(channelCode =='PRINT' )         
        { 
            List<RTIServiceDetailResponse_H_HUM.PRINT> oInteractionsList = oInteractionSummaryResult.prints;
            tempListRTISummary = buildWrapperListPrint(oInteractionsList);
        }  
        if(categoryDesc =='EMAIL' )
        { 
            List<RTIServiceDetailResponse_H_HUM.EMAILs> oInteractionsList = oInteractionSummaryResult.emails;
            tempListRTISummary = buildWrapperListEmail(oInteractionsList);
        }  
        if(categoryDesc =='EM')
        { 
            List<RTIServiceDetailResponse_H_HUM.eMs> oInteractionsList = oInteractionSummaryResult.eMs;
            tempListRTISummary = buildWrapperListEM(oInteractionsList);
        }               
        if(categoryDesc == 'Text' )
        { 
            List<RTIServiceDetailResponse_H_HUM.SMS> oInteractionsList = oInteractionSummaryResult.smss;
            tempListRTISummary = buildWrapperListSms(oInteractionsList);
        }
        if(channelCode =='WEB')        
        { 
            List<RTIServiceDetailResponse_H_HUM.WEB> oInteractionsList = oInteractionSummaryResult.webs;
            tempListRTISummary = buildWrapperListWeb(oInteractionsList);

        }
        if(channelCode =='WB' ) 
        { 
            List<RTIServiceDetailResponse_H_HUM.WB> oInteractionsList = oInteractionSummaryResult.wbs;
            tempListRTISummary = buildWrapperListWb(oInteractionsList);
        }   
        return tempListRTISummary; 
    }
   
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildPrintWrapperList
    *  @param  List<RTIServiceDetailResponse_H_HUM.Interaction> -List of RTIServiceSummaryResponse_H_HUM.Interaction objects with the interaction details 
    *  @return List<RTIPrintSummary_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListPrint(List<RTIServiceDetailResponse_H_HUM.PRINT> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.PRINT obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     } 
     
     
     /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperListVT
    *  @param  List<RTIServiceDetailResponse_H_HUM.Interaction> -List of RTIServiceSummaryResponse_H_HUM.Interaction objects with the interaction details 
    *  @return List<RTIPrintSummary_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */  
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListVT(List<RTIServiceDetailResponse_H_HUM.VAT> oInteractionsListVT)
    {        
        List<RTIPrintSummary_DTO_HUM> listRTISummaryVT = new List<RTIPrintSummary_DTO_HUM>();  
        for(RTIServiceDetailResponse_H_HUM.VAT obj : oInteractionsListVT)
        {   
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;            
            listRTISummaryVT.add(objWrapper);
        }
        return listRTISummaryVT;   
     } 
     
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListEmail(List<RTIServiceDetailResponse_H_HUM.Emails> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.Emails obj : oInteractionsList)
        {
           RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }  
     
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListEM(List<RTIServiceDetailResponse_H_HUM.eMs> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.eMs obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }    
    
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListSms(List<RTIServiceDetailResponse_H_HUM.SMS> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.SMS obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     } 
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListWb(List<RTIServiceDetailResponse_H_HUM.WB> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.WB obj : oInteractionsList) 
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }     
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListWeb(List<RTIServiceDetailResponse_H_HUM.WEB> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.WEB obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }  
        
}