/*******************************************************************************************************************************************************
Apex Class Name  : MTV_Member_Provider_LC_HUM 
Version          : 1.0
Created Date     : 06/01/2022 
Function         : This is used to return response from service and format the response.
*****************************************************************************************************************************************************

Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
* Anuradha Gajbhe 	   	                   		08/10/2022                 US#3587844 - Claims--Lightning--Additional Capabilities on Claims Line Items: error Messages Member Claim Statements
* Sagar Gulleve 	   	                   		09/22/2022                 US#3587846 - Claims--Lightning--Additional Capabilities on Claims Line Items: error Messages Provider Claim Statements 
* Apurva Urkude 	   	                   		09/22/2022                 US#3587842 - Claims--Lightning--Additional Capabilities on Claims Line Items: error Messages MTV Remarks 
*************************************************************************************************/
public with sharing class MTV_Member_Provider_LC_HUM {
//Member Claim Statement and Provider Claim statement
    public enum EnumStatementType{Provider, Member}
    public static String sRemittenceIds {Get; Set;}
    public static string sClaimNumber = '';
    public static ClaimDetailMTVRemarks_LS_HUM oclaimMTVRemarks = new ClaimDetailMTVRemarks_LS_HUM(); 
    public static String sRequest  = '';
    private static List<ClaimDetailMTVRemarks_LDTO_HUM> lstMTVRemarks = new List<ClaimDetailMTVRemarks_LDTO_HUM>();
    private static List<ClaimDetailMTVRemarks_LDTO_HUM> lstMTV = new List<ClaimDetailMTVRemarks_LDTO_HUM>();
    public static Map<String, ClaimDetailMTVRemarks_LDTO_HUM> mapMTV = new Map<String, ClaimDetailMTVRemarks_LDTO_HUM>();
    public static Boolean bMTVSecondCall = false;
    private static String sCreationDate;
    private static String sCreationTime;
    private static String sSurrogateId;
    private static String sStartRemarkSequenceNumber;
    public static String sMTVJsonData; 
    public static Boolean bShowMTVTable; 
    public static Boolean bShowMTVServiceError=false;
    public static boolean setPagindID = false;
    public static Boolean bSwitch_2132792;
    public static boolean bPrimarySpecSwitch;
    public static boolean bCPRSwitch;
    Public Static final String ChromeBrowser = 'Chrome/';
    Public Static final String EdgeBrowser = 'Edg/';
    public static boolean bCoveoUser;
    
    @auraEnabled(continuation=true cacheable=true)
    public static object initiateMTVRequest(String claimNumber)
	{
        
	String sBrowserContext = '';
        bPrimarySpecSwitch = ([SELECT Id,  Primary_Specialty_Switch__c FROM Provider_functionality_switch__c where Primary_Specialty_Switch__c = true limit 1]).size()>0?true:false;
        bCPRSwitch = HUMUtilityHelper.isCRMFunctionalityON('2132792');//2201523 Original 2132792
  
        if(string.isNotBlank(sBrowserContext)){
            if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
                bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
           }
       }
        bSwitch_2132792 = HUMUtilityHelper.isCRMFunctionalityON('2132792'); 
        
        
        try
        {	
           if(bSwitch_2132792 == true) {  
              
                MTVRemarksGetClaimsRequest_LH_HUM_V1.ClaimNumbersList oClaimnumber= new MTVRemarksGetClaimsRequest_LH_HUM_V1.ClaimNumbersList();
                oClaimnumber.ClaimNumber = claimNumber;  
                MTVRemarksGetClaimsRequest_LH_HUM_V1 oMTVRemarksGetClaimsRequest = new MTVRemarksGetClaimsRequest_LH_HUM_V1();
                oMTVRemarksGetClaimsRequest.GetClaimsRemarksRequest = new MTVRemarksGetClaimsRequest_LH_HUM_V1.GetClaimsRemarksRequest();
                oMTVRemarksGetClaimsRequest.GetClaimsRemarksRequest.ClaimNumbersList = new List<MTVRemarksGetClaimsRequest_LH_HUM_V1.ClaimNumbersList>();
                oMTVRemarksGetClaimsRequest.GetClaimsRemarksRequest.ClaimNumbersList.add(oClaimnumber);
                HUM_Webservice_Callout__c oSvcHeaderClaimMTVRemarks = HUM_Webservice_Callout__c.getValues('MTVRemarkService_GetClaimsMTVRemarkV2'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimMTVRemarks.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processMTVResponse1';
                 
                string sJsonRequest = JSON.Serialize(oMTVRemarksGetClaimsRequest);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oclaimMTVRemarks.setupRequest(oRequest,oSvcHeaderClaimMTVRemarks,sJsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                return con;  
             
            }
            else {
            MTVRemarksServiceGetClaimsRequest_LH_HUM oMTVRemarksReq = new MTVRemarksServiceGetClaimsRequest_LH_HUM();            
            oMTVRemarksReq = oclaimMTVRemarks.assignValuesToHelper(bMTVSecondCall, claimNumber, sCreationDate, sCreationTime, sSurrogateId, sStartRemarkSequenceNumber);            
            if(oMTVRemarksReq != null)
            {	
                HUM_Webservice_Callout__c oSvcHeaderClaimMTVRemarks = HUM_Webservice_Callout__c.getValues('MTVRemarksService_GetClaimsMTVRemarks'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimMTVRemarks .Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processMTVResponse';
                string sJsonRequest = JSON.Serialize(oMTVRemarksReq,true);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oclaimMTVRemarks.setupRequest(oRequest,oSvcHeaderClaimMTVRemarks,sJsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                return con;            
            }
            else
            {
                return null;
            }
                
          }
        }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTV_Member_Provider_LC_HUM', 'initiateMTVRequest');
            return NULL;
        }
    }
     /*
    * Method Name   :  processMTVResponse1
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
     @AuraEnabled(cacheable=true)
    public static Object processMTVResponse1(List<String> labels, Object status)
    {
       
        try 
        {	
           
            HttpResponse response = Continuation.getResponse(labels[0]);
            if (response.getStatusCode() == 200 || response.getStatusCode() == 202) 
            {
                lstMTVRemarks = oclaimMTVRemarks.processResponseNew(response.getbody());

                 if(lstMTVRemarks!=null && !lstMTVRemarks.isempty())
                {
                sMTVJsonData = JSON.serialize(lstMTVRemarks, true); 
                sMTVJsonData = '{"data":'+sMTVJsonData+'}';
				
                bShowMTVTable = true;
                }
                else
                {
                     bShowMTVTable = false;
                    sWarningMsg = Label.ClaimDetails_NoRecords_Msg;
                        return sWarningMsg;
                }     
            }
            else
            {
                bShowMTVServiceError = true;               
                lstMTVRemarks=null;
                bShowClaimStatementsServiceError = true;
				sWarningMsg = Label.ClaimsSummary_Service_Error;
                               
            }     
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTV_Member_Provider_LC_HUM', 'processMTVResponse');
        }
        return sMTVJsonData;
    } 
    
   
    
    /*
    * Method Name   :  processMTVResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled(cacheable=true)
    public static Object processMTVResponse(List<String> labels, Object status)
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(labels[0]);
            if (response.getStatusCode() == 200 || response.getStatusCode() == 202) 
            {               
                lstMTVRemarks = oclaimMTVRemarks.processResponse(response.getbody());
                lstMTVRemarks = prepareMTVRemarkslist(lstMTVRemarks);
            }
            else
            {
                bShowMTVServiceError = true;
                lstMTVRemarks=null;
            }

        }
        catch(Exception ex)
      
	  {
            HUMExceptionHelper.logErrors(ex, 'MTV_Member_Provider_LC_HUM', 'processMTVResponse');
        }

         return lstMTVRemarks;
    } 
    
            /*
    * Method Name   :  prepareMTVRemarkslist
    * Description   :  This method is used to form the wrapper list that needs to be display on UI. 
    * Return Type   :  void 
    * Parameters    :  List<ClaimDetailMTVRemarks_LDTO_HUM>
    */
    @AuraEnabled
    public static List<ClaimDetailMTVRemarks_LDTO_HUM> prepareMTVRemarkslist(List<ClaimDetailMTVRemarks_LDTO_HUM> lstMTVRemarks)
    {
        try
        {
            setPagindID = false;
            if(lstMTVRemarks != null)
            {
                if(!lstMTVRemarks.isEmpty())
                {
                    lstMTV = new List<ClaimDetailMTVRemarks_LDTO_HUM>();
                    string sRTxt;
                    mtvConcatenate(setPagindID,lstMTVRemarks);
                    for(string varS : mapMTV.keyset())
                    {
                        ClaimDetailMTVRemarks_LDTO_HUM objMTV = mapMTV.get(varS);
                        lstMTV.add(objMTV);
                    }
                    sMTVJsonData = JSON.serialize(lstMTV,true); 
		    sMTVJsonData = '{"data":'+sMTVJsonData+'}';
                    bShowMTVTable = true;
                }
                else
                {
                    bShowMTVServiceError = true;                    
                }  
            }
            else
            {
                bShowMTVServiceError = true;                
            }
           
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTV_Member_Provider_LC_HUM', 'prepareMTVRemarkslist');
        } 
         return (List<ClaimDetailMTVRemarks_LDTO_HUM>)JSON.deserialize(sMTVJsonData, ClaimDetailMTVRemarks_LDTO_HUM.class);
    }
    /*
    * Method Name   :  mtvConcatenate
    * Description   :  This method is used for Concatenation of MTV Remarks text based on SurrogateId. 
    * Return Type   :  void 
    * Parameters    :  Boolean, List<ClaimDetailMTVRemarks_LDTO_HUM>
    */
    @AuraEnabled
    public static void mtvConcatenate(boolean setPagindID, List<ClaimDetailMTVRemarks_LDTO_HUM> lstMTVRemarks)
    {
        for(ClaimDetailMTVRemarks_LDTO_HUM obj:lstMTVRemarks)
        {
            if(!setPagindID )
            {
                setPagindID = true;                              
                if(obj.sPagingInd == 'Y')
                {
                    bMTVSecondCall = true;
                    sCreationDate = obj.sCreationDate;
                    sCreationTime = obj.sCreationTime;
                    sSurrogateId = obj.sSurrogateId;
                    sStartRemarkSequenceNumber = obj.sStartRemarkSequenceNumber;
                }
                else
                {
                    bMTVSecondCall = false;                     
                }                
             }
            if(!(mapMTV.containskey(obj.sRemarkSurrogateId)))
            {
                mapMTV.put(obj.sRemarkSurrogateId, obj);
                obj.sRemarkExtendedText = obj.sRemarkText.trim();
            }
            else
            {
                ClaimDetailMTVRemarks_LDTO_HUM objRepeat =  mapMTV.get(obj.sRemarkSurrogateId);
                                         
                if(Integer.Valueof(obj.sRemarkSequenceNumber) <= Integer.valueOf(objRepeat.sRemarkSequenceNumber) )
                {
                    objRepeat.sRemarkExtendedText =  obj.sRemarkText.trim() + objRepeat.sRemarkExtendedText.trim();
                } 
                else
                {
                    objRepeat.sRemarkExtendedText = objRepeat.sRemarkExtendedText.trim() + obj.sRemarkText.trim();
                }
                 mapMTV.remove(objRepeat.sRemarkSurrogateId);
                 mapMTV.put(objRepeat.sRemarkSurrogateId, objRepeat);
             }
         }
    }
//==========================================================================================================
// Member Claim Statenment and Provider Claim Statement
  
    public static Set<String> lstRemitNumber = New Set<String>();
    public enum EnumDocumentType{SS,EOB,EOR}
    @TestVisible private static ClaimMobiusDetails_LS_HUM oclaimStatements = new ClaimMobiusDetails_LS_HUM();    
    @TestVisible private static ClaimMobiusDetails_LS_HUM oPrvdrStatements = new ClaimMobiusDetails_LS_HUM();
    @TestVisible private static String sPrvdrClaimStatementsRequest  = ''; 
    @TestVisible private static String sClaimStatementsRequest  = ''; 
    @TestVisible Public Static List<MemberPlan> lstPolMember = new List<MemberPlan>(); 
    public static Id sPolMemID {get; set;}
    Public Enum EnumPlatform{EM,LV,Undefined}
    public static EnumPlatform  ePlatform{get;set;}
    public static string sMemberId {get; set;}
    public static string sMemberIdDepCd {get; set;}
    public static string RecId {get; set;}
    public static string PersonId {get; set;}
    public static string MemberGenKey {get; set;}
    public static string sPlatformCode {get; set;}
    public Static String sWarningMsg{get;set;}
    public static String sjsonClaimStatementsdata { get; set; }               
    public static Boolean bShowClaimStatementsTable{get; set;}          
    public static Boolean bShowPrvdrClaimStatementsTable{get; set;} 
    public static Boolean bShowClaimStatementsServiceError{get;set;}          
    public static Boolean bShowPrvdrClaimStatementsServiceError{get;set;}
    Public static String sjsonPrvdrClaimStatementsdata { Get; Set; }                    
   
    /*
    * Method Name   :  initClaimStatementsRequest
    * Description   :  This method is used to Call Member Claims Statements Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    @AuraEnabled(continuation=true)
    public static object initClaimStatementsRequest(String sMemName, string sClmNbr)
    {    
        return initStatementsRequest(EnumStatementType.Member, sMemName , sClmNbr); 
    }
    
    /*
    * Method Name   :  initPrvdrClaimStatementsRequest
    * Description   :  This method is used to Call Provider Claims Statements Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    @AuraEnabled(continuation=true)
     public static object initPrvdrClaimStatementsRequest(String sMemName, string sClmNbr,string sRemitIds)
    {
		sRemittenceIds = sRemitIds;
        if(sRemittenceIds != null && sRemittenceIds != '')
        {
            String[] arrRemitId = sRemittenceIds.split('#');
            for(integer i=0; i< arrRemitId.size(); i++)
            {                    
                if(String.isNotBlank(arrRemitId[i])) lstRemitNumber.add(arrRemitId[i]);
            }
        }
        return initStatementsRequest(EnumStatementtype.Provider, sMemName, sClmNbr);
    }
    
       /*
    * Method Name   :  initStatementsRequest
    * Description   :  This method is used to invoke Member and Provider Claims Statements Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    @AuraEnabled(Continuation=true cacheable=true)
    public static object initStatementsRequest(EnumStatementType eStatementType , String sMemName, String sClmNbr)
    {
    try
        {
            String sServiceName;
            String sContinuationName;
            MobiusServiceGetDocListRequest_LH_HUM oDocListReq = new MobiusServiceGetDocListRequest_LH_HUM();
            
            list<MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch> lstDocSearch = new list<MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch>();            
            if(eStatementType == EnumStatementType.Provider)
            {
                lstDocSearch.add(prepareDocSearchReq(EnumDocumentType.EOR, sMemName, sClmNbr));
                sServiceName = 'MobiusService_GetDocumentList';
                sContinuationName = 'processPrvdrClaimStatementsResponse';
            }
            else if(eStatementType == EnumStatementType.Member)
            {
                lstDocSearch.add(prepareDocSearchReq(EnumDocumentType.SS, sMemName, sClmNbr));
                lstDocSearch.add(prepareDocSearchReq(EnumDocumentType.EOB, sMemName, sClmNbr));
                sServiceName = 'MobiusService_GetDocumentList';  
                sContinuationName = 'processClaimStatementsResponse';
            }
            MobiusServiceGetDocListRequest_LH_HUM.getDocument ogetDoc = new MobiusServiceGetDocListRequest_LH_HUM.getDocument();                                         
            ogetDoc.DocumentSearch = lstDocSearch;

            MobiusServiceGetDocListRequest_LH_HUM.getDocumentList ogetDocLst = new MobiusServiceGetDocListRequest_LH_HUM.getDocumentList();                                         
            ogetDocLst.getDocument = ogetDoc;            

            oDocListReq.getDocumentList= ogetDocLst;
            if(oDocListReq != null)
            {
                HUM_Webservice_Callout__c oSvcHeaderClaimStatements = HUM_Webservice_Callout__c.getValues(sServiceName); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimStatements.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.state = sPolMemID;
                con.continuationMethod = sContinuationName;
                string sJsonRequest = JSON.Serialize(oDocListReq);
                HttpRequest oRequest = new HttpRequest();                
                if(eStatementType == EnumStatementType.Provider) 
                {
                    oRequest = oPrvdrStatements.setupRequest(oRequest,oSvcHeaderClaimStatements,sJsonRequest);
                    sPrvdrClaimStatementsRequest = con.addHttpRequest(oRequest);
                }
                else if(eStatementType == EnumStatementType.Member) 
                {    
                    
                    oRequest = oclaimStatements.setupRequest(oRequest,oSvcHeaderClaimStatements,sJsonRequest);
                    sClaimStatementsRequest = con.addHttpRequest(oRequest);                
                }   
                return con;            
            }
            else
            {
                return null;
            }
        }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTV_Member_Provider_LC_HUM', 'initPrvdrClaimStatementsRequest');
            return NULL;
        }
    }

        /*
    * Method Name   :  prepareDocSearchReq
    * Description   :  This method is used to prepare DocumentSearch ojbect of service request
    * Return Type   :  MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch
    * Parameters    :  NA
    */
    @AuraEnabled(cacheable=true)
    public Static MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch prepareDocSearchReq(EnumDocumentType docType, String sMemName, String sClmNbr)
    {    
        lstPolMember = getMemberId(sMemName);
        sClaimNumber = sClmNbr;
        MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch oDoc = new MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch();
        List<MobiusServiceGetDocListRequest_LH_HUM.TopicSearch> lstTS = new List<MobiusServiceGetDocListRequest_LH_HUM.TopicSearch>();
        MobiusServiceGetDocListRequest_LH_HUM.TopicSearch oTS = new MobiusServiceGetDocListRequest_LH_HUM.TopicSearch();        
        MobiusServiceGetDocListRequest_LH_HUM.TopicSearchList oTSL = new MobiusServiceGetDocListRequest_LH_HUM.TopicSearchList();                                                
        
         if(docType == EnumDocumentType.EOR)
        {
            oDoc.DocumentServer = DocumentServer__c.getInstance('DocumentServerEOR').Value__c;
            oDoc.ReportName = 'EORSTMT';
            oDoc.Creator = 'EOR'; 
            Boolean isHO = ClaimSecurityDataLogic_LD_HUM.isHomeOfficeMember(ClaimSecurityDataLogic_LD_HUM.getMemberAccountID(lstPolMember[0].id)); 
            
            for(string sRN: lstRemitNumber)
            {
                oTS = new MobiusServiceGetDocListRequest_LH_HUM.TopicSearch();
               if(isHO)
                {
                   oTS.TopicName = 'eorremitho';
                }
                else
                {
                    oTS.TopicName = 'eorremit'; 
                }
                oTS.RemitID = sRN;   
                oTS.DocumentType = 'EOR';
                oTS.PlatformCode = lstPolMember[0].Policy_Platform__c.escapeHtml4();                            
                oTS.Comparator = 'LIKE';
                oTS.SortOrder = 'Descending';                                
                lstTS.add(oTS);
            }
                oTSL.JoinType = 'OR';
                oTSL.TopicSearch = lstTS;                            
                oDoc.MaxRows = '24'; 
                oDoc.TopicSearchList = oTSL;                      
                return oDoc;
            
        }
        if(docType ==  EnumDocumentType.SS)
        {
            oDoc.DocumentServer = DocumentServer__c.getInstance('DocumentServerSS').Value__c;
            oDoc.ReportName = 'AFPTEST';
            oDoc.Creator = 'HUM';
            oTS.TopicName = 'PersonId';                     
            oTS.PersonId = lstPolMember[0].Member.Enterprise_Id__c;   
            oTS.MemberGenKey = lstPolMember[0].Member.Mbr_Gen_Key__c;   
        }
        if(docType ==  EnumDocumentType.EOB)
        {
            oDoc.DocumentServer = DocumentServer__c.getInstance('DocumentServerEOB').Value__c;
            oDoc.ReportName = 'EOBSTMT';
            oDoc.Creator = 'EOB';
            if(sPlatformCode == 'LV')
            {
                oTS.TopicName = 'eobmember';  
                oTS.MemberId = sMemName.substringBefore('-');     
            }
            else
            {
                oTS.TopicName = 'eobclaim';                                          
            }
        }
        oTS.PlatformCode = string.valueOf(lstPolMember[0].Policy_Platform__c);                                       
        oTS.ClaimNumber = sClaimNumber;        
        oDoc.MaxRows = '24';                   
        oTS.Comparator = 'LIKE';
        oTS.SortOrder = 'Descending';                
        lstTS.add(oTS);
        oTSL.JoinType = 'AND';
        oTSL.TopicSearch = lstTS;            
        oDoc.TopicSearchList = oTSL;         
        return oDoc;
    }   
    
    /*
    * Method Name   :  processClaimStatementsResponse
    * Description   :  This method is used to get response from member claim statements service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled
    public static String processClaimStatementsResponse(List<String> labels, Object state)
    {
        try 
        {
            sPolMemID = String.valueof(state);
            HttpResponse response = Continuation.getResponse(labels[0]);
            if(response != Null)
            {
                List<ClaimsDocumentsList_LDTO_HUM> lstClaimsStatementsResp = new List<ClaimsDocumentsList_LDTO_HUM>();
                lstClaimsStatementsResp = ClaimMobiusDetails_LS_HUM.processResponse(response.getbody(), 'Member', sPolMemID);
                if(lstClaimsStatementsResp != null)
                {
                    if(!lstClaimsStatementsResp.isEmpty())
                    {
                        sjsonClaimStatementsdata  = JSON.serialize(lstClaimsStatementsResp,true); 
                        sjsonClaimStatementsdata  = '{"data":'+sjsonClaimStatementsdata+'}';
                        bShowClaimStatementsTable = true;
                    }  
                    else
                    {
                        bShowClaimStatementsServiceError = true;                                         
			sWarningMsg = Label.ClaimDetails_NoRecords_Msg;
                        return sWarningMsg;
                    }
                 }
                 else
                 {
                    bShowClaimStatementsServiceError = true;
		    sWarningMsg = Label.ClaimsSummary_Service_Error;
                    return sWarningMsg;
                 }
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTV_Member_Provider_LC_HUM', 'processClaimStatementsResponse');
        }
        return sjsonClaimStatementsdata;
    }
    
      /*
    * Method Name   :  processPrvdrClaimStatementsResponse
    * Description   :  This method is used to get response from Claims provider statements call.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled
    public static String processPrvdrClaimStatementsResponse(List<String> labels, Object state)
    {
        try 
        {
            sPolMemID = String.valueof(state);
            HttpResponse response = Continuation.getResponse(labels[0]);
            if(response != Null)
            {
               List<ClaimsDocumentsList_LDTO_HUM> lstClaimsStatementsResp = new List<ClaimsDocumentsList_LDTO_HUM>();
               lstClaimsStatementsResp = ClaimMobiusDetails_LS_HUM.processResponse(response.getBody(), 'Provider', sPolMemID);
                if(lstClaimsStatementsResp != null)
                {
                    if(!lstClaimsStatementsResp.isEmpty())
                    {
                        sjsonPrvdrClaimStatementsdata  = JSON.serialize(lstClaimsStatementsResp,true); 
                        sjsonPrvdrClaimStatementsdata  = '{"data":'+sjsonPrvdrClaimStatementsdata +'}';
                        bShowPrvdrClaimStatementsTable = true;
                    }  
                    else
                    {
                        bShowPrvdrClaimStatementsServiceError = true; 
						sWarningMsg = Label.ClaimDetails_NoRecords_Msg;
                        return sWarningMsg;
                    }
                 }
                 else
                 {
                    bShowPrvdrClaimStatementsServiceError = true;
					bShowClaimStatementsServiceError = true;
					sWarningMsg = Label.ClaimsSummary_Service_Error;
					return sWarningMsg;
                 }
                 
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTV_Member_Provider_LC_HUM', 'processPrvdrClaimStatementsResponse');
        }
        return sjsonPrvdrClaimStatementsdata;
    }
     
/*
     * Method Name   :  getEnterpriseID
     * Description   :  This method is return EnterpriseID
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    @testvisible
    private static String getEnterpriseID(ID sRecId )
    {
       List<MemberPlan> lstPolicyMem = ClaimDetails_LD_HUM_V2.getEnterpriseID(sRecId);
       if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())    return lstPolicyMem[0].Member.Enterprise_ID__c ;
       return NULL;
    }
    
     /*
     * Method Name   :  getMemberGenKey
     * Description   :  This method returns MemberGenKey
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    @testvisible
    private static String getMemberGenKey(ID sRecId )
    {
       List<MemberPlan> lstPolicyMem = ClaimDetails_LD_HUM_V2.getMemberGenKey(sRecId);
       if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())    return lstPolicyMem[0].Member.Mbr_Gen_Key__c ;
       return NULL;
    }       

    /*
     * Method Name   :  getMemberId
     * Description   :  This method is return memberid
     * Return Type   :  ID
     * Parameters    :  String sMemName
     */
    @AuraEnabled
    public Static List<MemberPlan> getMemberId(string sMemName)
    {
       string sMemBaseplusDepCd = '';
       sMemberId = sMemName.substringBefore('-'); 
       sMemberIdDepCd = sMemName.substringAfter('-'); 
       sMemBaseplusDepCd =  sMemberId + sMemberIdDepCd;
       List<MemberPlan> lstPolMem = ClaimDetails_LD_HUM_V2.getMemberId(sMemBaseplusDepCd);
       if(NULL != lstPolMem && !lstPolMem.isEmpty())    
       {
           sPolMemID = lstPolMem[0].id;
           sPlatformCode = lstPolMem[0].Policy_Platform__c;
           PersonId = lstPolMem[0].Member.Mbr_Gen_Key__c; 
           MemberGenKey = lstPolMem[0].Member.Enterprise_Id__c;
           return lstPolMem;
       }
       return NULL;
    }
}