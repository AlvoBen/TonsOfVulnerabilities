/**************************************************************************************************************************************************
Apex class Name    :  RTISummary_S_HUM
Version            :  1.0
Function           :  This is the service class for RTI Summary to hit RTI service and return list of RTI records
Test Class         :  RTISummaryService_T_HUM

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
*  Pallavi Ravishankar          61268                 01/04/2017          Original Version
*  Kritika Popat                86349                 07/04/2017          Phase 2 Service changes
*  Kritika Popat                86349                 10/04/2017          Pagination    
*  Kritika Popat                90451                 24/04/2017          IVR And Print 2nd level
*  Kritika Popat                111075                07/18/2017          Message in Print Card
*  Kritika Popat                180694                01/25/2018          IVR cards to display End Date
*  Shivam Anand                 180694                02/05/2018          REQ-350840,350841,350843- Display End Date/Time for WB, MB And MA
*  Anjani  Vaddadi                                    21/02/2020          REQ- RTI VAT changes
*  Anjani Vaddadi                                     28/02/2020          REQ- RTI Changes to categoryCode variable
*  Anjani Vaddadi                                     03/10/2020          Defect Fix-307 RTI VAT
*  Anjani Vaddadi                                     03/26/2020          REQ-437065 RTI-MF4
*  Imran                                              04/01/2020          REQ-956598 RTI defect
*  Imran                                              04/06/2020          RTI- Defect-464 - Changed the dispositioncd And statuscd to small letters
*  Prasanthi Kandula                                  04/16/2020          US 1003374 - To display send link on RTI Detail page 
*  Anjani Vaddadi                                     7/03/2020           US - 1125628- Pass Message Definition Code (MDC) on Outbound Communications attached to CRM Cases via RTI
*  Muralidhar Kollu                                   20/07/2020          RTI- 836091 View member communications for those members with security profile 
*  Trina Ghosh                                        02/09/2021          US:1669848 - Low Security Issues Fix
*  Rahul Chaurasia                                    03/22/2021          1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service
*  Prasanthi Kandula                                  05/18/2021          US - 2082407 Added new card for channel Email
*  Anuradha Gajbhe                                    05/18/2021          US - 2082385 Added logic to display new card for channel Text
*  Rahul Chaurasia                                    05/19/2021          US - 2082359 Added logic to display new card for channel Web
*  Rahul Chaurasia                                    06/09/2021          US - 2082359 Fixed Category description issue for WEB
*  Anuradha Gajbhe                                    08/19/2021          US - 2496294 Pass Message Definition Code (MDC) on Outbound Communications attached to CRM Cases via RTI
*  Suraj Patil                                        05/26/2022          US - 3290756 T1PRJ0272555 - 2022 MF6 - INCIDENT - INC1690380  :RTI communication panel/cards - cards that are not final/do not have a date are displaying in the panel
************************************************************************************************************************************************* */

public  with sharing class RTISummary_S_HUM
{
    @TestVisible private String sBody ='' ;
    private Integer recordLimit = NULL;
    private List<RTISummary_DTO_HUM > oRTIList = New List<RTISummary_DTO_HUM >();
    @TestVisible private Integer iTotalRec = 0;
    Public List<Error_Log__c> lstLogErrors
    {
        get
        {
          if(lstLogErrors == Null){lstLogErrors = new List<Error_Log__c>();}return lstLogErrors;
        }
        set;
    }
    /* 
      *  This is the Constructor for the class ClaimsSummary_S_HUM
      *  @name   ClaimsSummary_S_HUM
      *  @param  NA
      *  @return NA
      *  @throws NA 
    */
    public RTISummary_S_HUM()
    {
        Try
        {   
            recordLimit = Integer.valueOf(HUMConstants__c.getInstance('RTIPagination').StringValue__c);     
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.bRestrictLog = True;
            lstLogErrors = HUMExceptionHelper.logErrors(ex,'RTISummary_S_HUM','RTISummary_S_HUM');
        }
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
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcHeaderRTI, String sEnterpriseId, String startDate, String endDate, String sChannel, Integer iSkipNmbr, String sCategoryC, String sInteractingC, String sDirection, List<RTIApplicationValues_H_HUM.AttributeValue> attrValue, Boolean bDirectionSwitch)
    {
        HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody = buildRequest(sEnterpriseId,startDate,endDate,sChannel,iSkipNmbr,sCategoryC,sInteractingC,sDirection,attrValue,bDirectionSwitch);
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
            HUMExceptionHelper.logErrors(ex, 'RTISummary_S_HUM', 'setupRequest');
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
    @testvisible private string buildRequest(String sEnterpriseId, String startDate, String endDate, String sChannel, Integer iSkipNmbr, String sCategoryC, String sInteractingC, string sDirection,List<RTIApplicationValues_H_HUM.AttributeValue> attrValue, Boolean bDirectionSwitch)
    {    
        string sJsonbody = '';
        try
        {
            RTIServiceSummaryRequest_H_HUM oRequest = new RTIServiceSummaryRequest_H_HUM(); 
            RTIServiceSummaryRequest_H_HUM.GetInteractionsSummary getRTISummary = new RTIServiceSummaryRequest_H_HUM.GetInteractionsSummary();
            List<RTIServiceSummaryRequest_H_HUM.Channel> channelList = new  List<RTIServiceSummaryRequest_H_HUM.Channel>();
            channelList = getRTIChannel(sChannel,channelList,sCategoryC,sDirection,attrValue,bDirectionSwitch);
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
            HUMExceptionHelper.logErrors(ex, 'RTISummary_S_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
    /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   processResponse
    *  @param  string(Json response) - Body of the response in JSON format
    *  @return List<RTISummaryDTO_H_HUM> - WrapperList containing the response details
    *  @throws NA
    */ 
    public List<RTISummary_DTO_HUM > processResponse(string response, Boolean bDirectionSwitch) 
    {  
        RTIServiceSummaryResponse_H_HUM objRTIresp = new RTIServiceSummaryResponse_H_HUM();
       // List<RTISummary_DTO_HUM > listRTIResp = new List<RTISummary_DTO_HUM>();
        objRTIresp = RTIServiceSummaryResponse_H_HUM.parse(response);
        if(objRTIresp.InteractionSummaryResponse != Null)
        {
            oRTIList = parseServiceResponse(objRTIresp,bDirectionSwitch);
        }
        return oRTIList;
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
    private List<RTISummary_DTO_HUM > parseServiceResponse(RTIServiceSummaryResponse_H_HUM objRTIresp,Boolean bDirectionSwitch)
    {
        List<RTISummary_DTO_HUM > tempListRTISummary = new List<RTISummary_DTO_HUM>();
        RTIServiceSummaryResponse_H_HUM.InteractionSummaryResponse oInteractionSummaryResponse = objRTIresp.InteractionSummaryResponse;
        RTIServiceSummaryResponse_H_HUM.InteractionSummaryResult oInteractionSummaryResult = oInteractionSummaryResponse.InteractionSummaryResult;
        List<RTIServiceSummaryResponse_H_HUM.Interaction> oInteractionsList = oInteractionSummaryResult.Interactions;
        iTotalRec = oInteractionSummaryResult.nbRecords;
        tempListRTISummary = buildWrapperList(oInteractionsList,bDirectionSwitch);
        return tempListRTISummary;
    }
    
    /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperList
    *  @param  List<RTIServiceSummaryResponse_H_HUM.Interaction> -List of RTIServiceSummaryResponse_H_HUM.Interaction objects with the interaction details 
    *  @return List<RTISummary_DTO_HUM > - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */  
    private List<RTISummary_DTO_HUM > buildWrapperList(List<RTIServiceSummaryResponse_H_HUM.Interaction> oInteractionsList,Boolean bDirectionSwitch)
    {
		Boolean bMdcSwitch = CRMFunctionalityenabler_H_HUM.Checkflagvalue('RTI_MDC_Switch');
        Integer Indexcount = 0;
        List<RTISummary_DTO_HUM > listRTISummary = new List<RTISummary_DTO_HUM>();
        Map<string,Id> mapRTIIcons = new Map<string,Id>();
        for(Document oDocument: [SELECT Id, Name FROM Document where Name ='RTI VAT Card Icon' or Name='RTI Email Icon' or Name='RTI Text Icon'])
        {
            mapRTIIcons.put(oDocument.Name,oDocument.Id);
        } 
        for(RTIServiceSummaryResponse_H_HUM.Interaction obj : oInteractionsList)
        {
            RTISummary_DTO_HUM objWrapper = new RTISummary_DTO_HUM();
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
                objWrapper.iconPath = '/img/icon/custom51_100/globe32.png';
                
                if(objWrapper.sChannelCode.equalsIgnoreCase('IV'))
                {
                    objWrapper.title = obj.iv.interactingWithDesc;
                    objWrapper.iconPath = '/img/icon/questions32.png';
                    objWrapper.dDateAndTime = obj.iv.enddatetime;
                }
                if(objWrapper.sChannelCode.equalsIgnoreCase('PRINT'))
                {
                    objWrapper.title = obj.print.categoryDescription;
                    objWrapper.iconPath = '/img/icon/mail32.png';
                    if(obj.print.isFinal)    
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

                    if(bDirectionSwitch)
                    {
                        objWrapper.iconPath = '/img/icon/custom51_100/globe32.png';                        
                        
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
                }
                if( objWrapper.sChannelCode.equalsIgnoreCase('WEB'))  
                {   
                    objWrapper.dDateAndTime =  obj.WEB.EndDateTime;
                    if(bDirectionSwitch)
                    {
                        objWrapper.iconPath = '/img/icon/custom51_100/globe32.png';                       
                        
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
                }
                if(objWrapper.sChannelCode.equalsIgnoreCase('VAT'))
                {                   
                objWrapper.iconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI VAT Card Icon'); 
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
                if(bDirectionSwitch)
                {
                     if(objWrapper.sChannelCode.equalsIgnoreCase('EMAIL'))
                    {
                        objWrapper.iconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI Email Icon'); 
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
                        objWrapper.iconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI Email Icon'); 
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
                        objWrapper.iconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI Text Icon');
                        objWrapper.dDateAndTime = obj.sms.EndDateTime;
                        objWrapper.sMessageName = obj.sms.MessageDefinitionDescription;
                        objWrapper.subtitle = obj.sms.busineesSubArea;
                        objWrapper.statusDescription = obj.sms.statusDescription;
                        if (String.isNotEmpty(obj.sms.recipientPhone))
                        {
                        objwrapper.PhoneNumber = '(' + obj.sms.recipientPhone.substring(0,3) + ') ' +
                                                 obj.sms.recipientPhone.substring(3,6) + '-' + obj.sms.recipientPhone.substring(6); 
                        }
                        objWrapper.viewLabel = 'View';
                        objwrapper.trackingID = obj.sms.TrackingID;
						if(bMdcSwitch){
                           objwrapper.sMsgDefCode = obj.sms.MessageDefinitionIDCD;
                        }
                    }  
                }
            }
             indexcount++;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
    }
    
    
    /*
    *  This method is used to get list of channelDetails based on Selected channel value
    *  @name   getRTIChannel
    *  @param sChannel - Selected channel value, List<RTIServiceSummaryRequest_H_HUM.Channel> - list of Channel objects
    *  @return List<RTIServiceSummaryRequest_H_HUM.Channel> - List of channel object wrappers with the channel details
    *  @throws NA 
    */
    private List<RTIServiceSummaryRequest_H_HUM.Channel> getRTIChannel(String sChannel, List<RTIServiceSummaryRequest_H_HUM.Channel> channelList, String sCategoryC, string sDirection, List<RTIApplicationValues_H_HUM.AttributeValue> attrValue, Boolean bDirectionSwitch)
    {
        Boolean bIsAll = sChannel.equalsIgnoreCase('All');
        if(bDirectionSwitch)
        {
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
                    oRTIChannel.categoryCode = oChannel.channelCategoryCode ;
                    if(oChannel.Label != 'PRINT' && ((oChannel.Label == 'VAT'&& oRTIChannel.direction == 'O') || oRTIChannel.direction=='I'))
                    oRTIChannel.isFinal = 'ALL';                    
                    oRTIChannel.dispositioncd = oChannel.dispositionCode;
                    oRTIChannel.statuscd = oChannel.statusCode;
                    channelList.add(oRTIChannel);
                }
            }
        }
        else
        {
            //IVR
            if((sChannel.equalsIgnoreCase('IVR'))|| bIsAll)
            {        
                RTIServiceSummaryRequest_H_HUM.Channel IVRChannel = new RTIServiceSummaryRequest_H_HUM.Channel();
                IVRChannel.code = 'IV';
                channelList.add(IVRChannel);
            }      
            
            if((sChannel.equalsIgnoreCase('PRINT'))||bIsAll )
            {
                RTIServiceSummaryRequest_H_HUM.Channel printChannel = new RTIServiceSummaryRequest_H_HUM.Channel();
                printChannel.code = 'PRINT';
                printChannel.direction = 'O';
                printChannel.isFinal = 'true';
                printChannel.categoryCode = sCategoryC;
                channelList.add(printChannel);
            }
            if((sChannel.equalsIgnoreCase('WB'))||bIsAll )
            {
                RTIServiceSummaryRequest_H_HUM.Channel webChannel = new RTIServiceSummaryRequest_H_HUM.Channel();
                webChannel.code = 'WB';
                channelList.add(webChannel);
            }
                
            if((sChannel.equalsIgnoreCase('MA'))||bIsAll )
            {
                RTIServiceSummaryRequest_H_HUM.Channel MAChannel = new RTIServiceSummaryRequest_H_HUM.Channel();
                MAChannel.code = 'MA';
                channelList.add(MAChannel);
            }
            
            if((sChannel.equalsIgnoreCase('MB'))||bIsAll )
            { 
                RTIServiceSummaryRequest_H_HUM.Channel MBChannel = new RTIServiceSummaryRequest_H_HUM.Channel();
                MBChannel.code = 'MB';
                channelList.add(MBChannel);
            }
            if((sChannel.equalsIgnoreCase('VAT') || bIsAll))
            {
                RTIServiceSummaryRequest_H_HUM.Channel VATChannel = new RTIServiceSummaryRequest_H_HUM.Channel();
                VATChannel.code='VAT';
                VATChannel.Direction='O';
                VATChannel.isFinal ='All';
                VATChannel.categoryCode = 'All';
                VATChannel.statuscd='D,F,I,U';
                VATChannel.dispositioncd  = '71,72,26,75,77,100,105,50,51,53,102,70,30,49,74,25,73,36,76,52';      
                channelList.add(VATChannel); 
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
    
}