/****************************************************************************
Apex Class Name  : Claim_Send_Statement_LC_HUM
Version          : 1.0
Created Date     : 07/12/2022
Function         : Controller class RTIResenHum
Test class       : Claim_Send_Statement_LT_HUM
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane                               7/12/2022                  US-3406771 Lightning- RTI - Resend functionality
* Swapnali Sonawane                               8/08/2022                  DF#5529 Error message(Failure to send) is displayed after selecting 'Yes' to send the card Addressverification popover.
* Anuradha Gajbhe								  9/08/2022					 US-3731625	Claims System Integration Claims--Enabling Member/Provider Claim Statements Tab Capabilities-Backend Code Conversionion.
* Abhishek Mangutkar							  10/28/2022				 US-3868532 - attach to case upon resend  functionality
* Nirmal Garg							          01/04/2023				 DF-6893 fix.
* Apurva Urkude							          03/06/2023				 US-4302431 Case Management: Systematically associate interaction to existing case during Process Logging: RTI - Resending Print Letters - Existing Case
* Sagar Gulleve							          03/06/2023				 US-4302417  Case Management - Systematically associate interaction to existing case during Process Logging: Resending Claim Statements (Surge).
* Anuradha Gajbhe								  03/01/2024				 US-5480525: DF- 8386: Address is not updating on Resend for Provider/Member Claim Statements on Case Pop-up.
***************************************************************************************************/
public with sharing class Claim_Send_Statement_LC_HUM {

    public static Claim_Send_Statement_LS_HUM oServiceClaimSend = new Claim_Send_Statement_LS_HUM();
    private static string sRequest ='';
    public static transient Claim_Send_Statement_DTO_HUM oClaimSendWrap {get; private set;}
    public static Boolean bProviderInt{get; private set;}
    public static String bError{get; private set;}
    public static String bWarning{get; private set;}
    public static final Integer HTTP_ACCEPTED_HUM = 202;
    public static final Integer HTTP_OK_HUM = 200;
    public static final String MOBIUS_GETDOCUMENT_APPLICATIONKEY ='238F6F83-B8B4-11CF-8771-00A024541EE3';

    public static string sRefAddressLine1,sRefAddressLine2, sRefCity ,sRefStatecode,sRefZipcode,sMailrepName,repName,sPolMemID;
    public static string sServiceRequest ='' ;
    public static string sProvID = '';
    public static string sProvIDType = '';
    public static Boolean bIsPageFromRTI=true;
    public static string sStatementTypeMobValue ='rtiMember';
    public static string sRTIPageId;
    public static string sMemberName ;
    public static string sCategoryDesc;
    public static string sMessageName ;
    public static string sSourceCode ;
	public static string sPolicyMemberId;
    public static string sStatementType='rtiMember';
    public static string sDateSent;
    public static string sStatementPeriod;
    public static string sEndStatementPeriod = '';
    public static string sApplicationKey;
    public static string selectedValue = 'Mail';
    public static string sRecipientType ;
    public static string sViewLink;
    public static string sFaxNumber {get; set;}
    public static string sRepName {get; set;}
    public static string sClaimNumber {get; set;}
    public static string sRemitId {get; set;}
    public static String sDocumentKey{get;set;}

    public static string faxNumber {
        get;
        set {
            faxNumber = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }

    @TestVisible
    @AuraEnabled(continuation=true)
    public static List<RTI_Delivery_Method__mdt> getRTIDeliveryMethod()
    {
        List<RTI_Delivery_Method__mdt> RTIDeliveryMethodList = [select Method_Name__c,Method_Name_Value__c from RTI_Delivery_Method__mdt Order by Method_Name_Value__c DESC limit 100 ];  
        return RTIDeliveryMethodList;
    }

    @TestVisible
    @AuraEnabled (continuation=true)
    public static Account getAccountDetails(String sRecId)
    {
        Account objAcc = new Account();
        try
        {
            if(String.valueof(sRecId).startswith(Schema.SObjectType.Account.keyprefix))
            {

                List<Account> lstAccount = [Select id,name,Fax, FirstName,LastName,IsPersonAccount,PersonMailingStreet,PersonMailingState,PersonMailingCity,PersonMailingStateCode,PersonMailingPostalCode, PersonMobilePhone, PersonHomePhone, PersonOtherPhone from account where id = :sRecId];
                if(!lstAccount.isEmpty())
                {
                    objAcc = lstAccount[0];
                }
            }
            else
            {

                List<MemberPlan> lstPolMember = [Select Member.Id from MemberPlan where Id =:sRecId];
                if(!lstPolMember.isEmpty())
                {
                    objAcc  = [Select id,name,Fax, FirstName,LastName,IsPersonAccount,PersonMailingStreet,PersonMailingState,PersonMailingCity,PersonMailingStateCode,PersonMailingPostalCode, PersonMobilePhone, PersonHomePhone, PersonOtherPhone from account where id IN (Select MemberId from MemberPlan where id=:sRecId)];

                }
            }
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Claim_Send_Statement_LC_HUM', 'getAccountDetails');
            objAcc = new Account();
        }
        return objAcc;
    }

    @TestVisible
    public static list<MemberPlan> getMemberPlanDetails(String sRecId)
    {
        MemberPlan objMemberPlan = new MemberPlan();
        try
        {
            if(String.valueof(sRecId).startswith(Schema.SObjectType.Account.keyprefix))
            {
                List<MemberPlan> lstPolMember = [Select Name,Member.Id,Member.Mbr_Gen_Key__C,Member_Dependent_Code__c,Policy_Platform__c,Member.Enterprise_ID__c from MemberPlan where MemberId =:sRecId order by lastModifiedDate desc];
                return lstPolMember;

            }else{
                List<MemberPlan> lstPolMember = [Select Name,Member.Id,Member.Mbr_Gen_Key__C,Member_Dependent_Code__c,Policy_Platform__c,Member.Enterprise_ID__c from MemberPlan where Id =:sRecId order by lastModifiedDate desc];
                return lstPolMember;
            }           
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Claim_Send_Statement_LC_HUM', 'getMemberPlanDetails');
           return null;
        }
    }

    @AuraEnabled(continuation=true)
    public static Object initiateRequest(string sRefAddressLine1,string sRefAddressLine2, string sRefCity ,string sRefStatecode,string sRefZipcode)
    {
        try {
            HUM_Webservice_Callout__c oSvcAddress = HUM_Webservice_Callout__c.getValues('Claims_AddressStandardization');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcAddress.Timeout__c)/1000;
            HttpRequest request = new HttpRequest();


            oServiceClaimSend.setupRequest(request, oSvcAddress,sRefAddressLine1,sRefAddressLine2,sRefCity,sRefStatecode,sRefZipcode);
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            sRequest = con.addHttpRequest(request);
            con.continuationMethod='processResponse';
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_LC_HUM', 'initiateRequest');
            return NULL;
        }
    }

    /*
    * Method Name   :  processResponse
    * Description   :  This process response method is used to call the Service when clicked on Address verification
    * Return Type   :  Void
    * Parameters    :  NA
    */
    @AuraEnabled (continuation=true)
    public Static Object processResponse(List<string> labels,Object State)
    {
        try
        {
            HttpResponse response = Continuation.getResponse(labels[0]);
            if(response != null)
                   return Json.serialize(responseparse(response));
            else{
                   return null;
                }
        }
        catch(Exception ex)
        {

            HUMExceptionHelper.logErrors(ex,'Claim_Send_Statement_LC_HUM','processResponse');
            return null;

        }
    }

    /*
    * Method Name   :  responseParse
    * Description   :  This method is used to check response
    * Return Type   :  void
    * Parameters    :  HttpResponse obj
    */
    @testvisible public static object responseParse(HttpResponse sResponse)
    {
        Integer iStatusCode = sResponse.getStatusCode();

        if (iStatusCode == HTTP_OK_HUM || iStatusCode == HTTP_ACCEPTED_HUM)
        {
            oServiceClaimSend.processResponse(sResponse.getbody());
            oClaimSendWrap = oServiceClaimSend.clmSendWrapObj;
            if(oClaimSendWrap.sValid != null)
            {
                return oClaimSendWrap;

            }
        }
        return oClaimSendWrap;
    }

    /*
    * Method Name   :  initiateSendStmtRequest
    * Description   :  This method is used to call the Service on click of Submit Request
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    @AuraEnabled(continuation=true)
    public static object initiateSendStmtRequest(Map<Object, String> sAddressData,Map<Object, String> sRtiData)
    {
        try
        {
            Map<String, String> rtiDataTemplate = new Map<String, String>();

            String sMemberGenKey = '';
            String sRFN = '';
            String sRLN = '';
            String sSDRPersonId = '';
            String sMbrId = '';
            String sDptCode = '';
            String sName = '';
            String sType = '';
            String sMsgFulfillmentId = '';
            String sROrgName = '';
            String sPlatformCode = '';
            String sAttachmentCategory = '';
            String sReprintSystemOfRecord ='';
            selectedValue = sRtiData.get('selectedValue');
            String sDistributionChnl = ((selectedValue != null || String.isNotEmpty(selectedValue)) ? selectedValue : '');
            String sRAdrLine1 = sAddressData.get('AddressLine1');
            String sRAdrLine2 = sAddressData.get('AddressLine2');
            String sRCity = sAddressData.get('City');
            String sRState = sAddressData.get('StateCode');
            String sRZip = sAddressData.get('ZipCode');
            String sRFaxNbr = sAddressData.get('FaxNo');
            sRecipientType = sRtiData.get('recipientType');
            sMailrepName = sAddressData.get('RecipientName');
            sRFN = sAddressData.get('FirstName');
            sRLN = sAddressData.get('LastName');
            sName =sAddressData.get('Name');
            //Initialise value
            sMemberName = sAddressData.get('Name');
            sCategoryDesc = sRtiData.get('CatDesc');
            sMessageName = sRtiData.get('MsgName');
            sRTIPageId = sRtiData.get('rtiPageId');
            sDateSent = sRtiData.get('DateSent');
            sStatementPeriod = '';
            sSourceCode = sRtiData.get('SourceCode');
            sViewLink = sRtiData.get('Viewlink');
            sStatementType = sRtiData.get('sStatementType');

            //Set data for template
            rtiDataTemplate.put('RefCity', sRCity);
            rtiDataTemplate.put('RefStatecode', sRState);
            rtiDataTemplate.put('RTIPageId',sRTIPageId);
            rtiDataTemplate.put('MailrepName',sMailrepName);
            rtiDataTemplate.put('RecipientType',sRecipientType);
            rtiDataTemplate.put('RefAddressLine1',sRAdrLine1);
            rtiDataTemplate.put('RefAddressLine2',sRAdrLine2);
            rtiDataTemplate.put('RefZipcode',sRZip);
            rtiDataTemplate.put('CategoryDesc',sCategoryDesc);
            rtiDataTemplate.put('MessageName', sMessageName);
            rtiDataTemplate.put('DateSent', sDateSent);
            rtiDataTemplate.put('ViewLink', sViewLink);

	    if (sRTIPageId == null || sRTIPageId == ''){
			sDocumentKey = sRtiData.get('DocKey');
			sStatementTypeMobValue = sRtiData.get('StatementTypeSerValue');
			sClaimNumber = sRtiData.get('ClaimNumber');
			sStatementPeriod = sRtiData.get('StatementPeriod');
			sEndStatementPeriod = sRtiData.get('EndStatementPeriod');
			sRemitId = sRtiData.get('RemitId');
			sRepName = sAddressData.get('RecipientName');
            sFaxNumber = sAddressData.get('FaxNo');
			selectedValue = sRtiData.get('selectedValue');
			string sClmPolMemID = sRtiData.get('sPolMemID');
			string bIsPageFromClm = 'true';
			bIsPageFromRTI = false;
			rtiDataTemplate.put('bIsPageFromClm', bIsPageFromClm);
			rtiDataTemplate.put('sClmPolMemID', sClmPolMemID);
			rtiDataTemplate.put('sStatementType', sStatementType);
			rtiDataTemplate.put('selectedValue', selectedValue);
			rtiDataTemplate.put('DocumentKey', sDocumentKey);
			rtiDataTemplate.put('StatementTypeMobValue', sStatementTypeMobValue);
			rtiDataTemplate.put('ClaimNumber', sClaimNumber);
			rtiDataTemplate.put('StatementPeriod', sStatementPeriod);
			rtiDataTemplate.put('EndStatementPeriod', sEndStatementPeriod);
			rtiDataTemplate.put('RemitId', sRemitId);
			rtiDataTemplate.put('RepName', sRepName);
			rtiDataTemplate.put('FaxNumber', sFaxNumber);

            }else {
                string bIsPageFromClm = 'false';
                bIsPageFromRTI = true;
                rtiDataTemplate.put('bIsPageFromClm', bIsPageFromClm);
            }

            if(sStatementPeriod == null || sStatementPeriod=='')
            {
                sStatementPeriod = '';
            }

            if(sEndStatementPeriod == null || sEndStatementPeriod=='')
            {
                sEndStatementPeriod = '';
            }

            sApplicationKey = MOBIUS_GETDOCUMENT_APPLICATIONKEY;
            sStatementTypeMobValue = sRtiData.get('StatementTypeSerValue');
            if(sStatementTypeMobValue == null || sStatementTypeMobValue=='')
            {
                sStatementTypeMobValue = 'Traditional EOB';
            }

            if(selectedValue != null || String.isNotEmpty(selectedValue))
            {
                if(selectedValue == 'Mail')
                {
                    sDistributionChnl = 'Print';
                    if(sMailrepName != null || String.isNotEmpty(sMailrepName))    sROrgName = sMailrepName;
                }
                else
                {
                    sDistributionChnl = selectedValue;
                    if(repName != null || String.isNotEmpty(repName))    sROrgName = repName;
                }
            }

            if(sStatementType == 'rtiMember')
            {
                List<MemberPlan> lstPolicyMem = getMemberPlanDetails(sRTIPageId);
                if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())
                {
                    string memId = lstPolicyMem[0].Name;
                    sMbrId = memId.left(9);
                    sDptCode =  lstPolicyMem[0].Member_Dependent_Code__c;
                    sMemberGenKey = lstPolicyMem[0].Member.Mbr_Gen_Key__C;
                    sSDRPersonId = lstPolicyMem[0].Member.Enterprise_ID__c ;
                    sPlatformCode = lstPolicyMem[0].Policy_Platform__c;
                }
                sMsgFulfillmentId = sRtiData.get('fulfilId');
                if(sSourceCode.equalsIgnoreCase('EMCON'))
                    sReprintSystemOfRecord = 'History';
                else if(sSourceCode.equalsIgnoreCase('CP') || sSourceCode.equalsIgnoreCase('Control Point') )
                    sReprintSystemOfRecord = 'CP';
                else if(sSourceCode.equalsIgnoreCase('EMME'))
                    sReprintSystemOfRecord = sSourceCode;
            }
            else
            {
                String sMsgFulfillment = sRtiData.get('DocKey');
                if(sMsgFulfillment != null || String.isNotEmpty(sMsgFulfillment))    sMsgFulfillmentId = sMsgFulfillment.escapeHtml4().substringAfter('DocumentKey=');
                sPolicyMemberId = sRtiData.get('sPolMemID');
                List<MemberPlan> lstPolicyMem = getAccDetails(sPolicyMemberId);
                String sMemberId = lstPolicyMem[0].Member_Id_Base__c;

                if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())
                {
                    sMemberGenKey = lstPolicyMem[0].Member.Mbr_Gen_Key__C;
                    sSDRPersonId = lstPolicyMem[0].Member.Enterprise_ID__c;
                    sPlatformCode = lstPolicyMem[0].Policy_Platform__c;
                }

                if(sMemberId.escapeHtml4() != null || String.isNotEmpty(sMemberId.escapeHtml4()))
                {
                    sMbrId = sMemberId;
                    sDptCode = lstPolicyMem[0].Member_Dependent_Code__c;
                }
            }


            if(sStatementType.escapeHtml4() != null || String.isNotEmpty(sStatementType.escapeHtml4()))
            {
                if(sStatementType == 'Member' || sStatementType == 'rtiMember')
                {
                    if(sRecipientType == 'Member')    sType = 'Member';
                    if(sRecipientType == 'Member Representative or Caregiver')    sType = 'Member';
                    if(sRecipientType == 'Other')    sType = 'Member';

                    if(sStatementType == 'rtiMember'){
                        if(sCategoryDesc.equalsIgnoreCase('EOB') || sCategoryDesc.equalsIgnoreCase('Id card')||sCategoryDesc.equalsIgnoreCase('Letter') ||sCategoryDesc.equalsIgnoreCase('Renewal Packet' )|| sCategoryDesc.equalsIgnoreCase('Smart Summary'))
                            sAttachmentCategory = sCategoryDesc;
                        else if(sCategoryDesc.equalsIgnoreCase('EXPLANATION OF BENEFITS'))
                            sAttachmentCategory = 'EOB' ;
                        else
                            sAttachmentCategory = 'Other';
                    }else if(sStatementType == 'Member'){
                        List<String> smartSummaryCodeValues = new List<String> {'COM','PDP','SSA','FAM','H1F','H1I','MAPD','XBSI','YBSI','ZBSI'};
                            List<String> eobCodeValues = new List<String>{'EBSF','EBSI','XBSF','EOBSTMT'};
                                if(eobCodeValues.contains(sStatementTypeMobValue))
                                sAttachmentCategory = 'EOB';
                        else if(smartSummaryCodeValues.contains(sStatementTypeMobValue))
                            sAttachmentCategory = 'Smart Summary';
                        else if(sStatementTypeMobValue.equalsIgnoreCase('Traditional EOB'))
                            sAttachmentCategory = 'EOB';
                        else
                            sAttachmentCategory = 'Other';

                        sReprintSystemOfRecord = 'Mobius';
                    }
                }
                if(sStatementType == 'Provider')
                {
                    sRFN = '';
                    sRLN = sName;
                    if(sRecipientType == 'Provider')    sType = 'Provider';
                    if(sRecipientType == 'Other')    sType = 'Provider';
                    sAttachmentCategory = 'Other';
                    sReprintSystemOfRecord = 'Mobius';
					providerAddress(sPolicyMemberId);
                }

            }

            sServiceRequest = '';
            HUM_Webservice_Callout__c oSvcHeaderSendStmt = new HUM_Webservice_Callout__c ();
            oSvcHeaderSendStmt = HUM_Webservice_Callout__c.getValues('CommunicationReprint_V3');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderSendStmt.Timeout__c)/1000;
            HttpRequest request = new HttpRequest();
            oServiceClaimSend.setupPrintSendRequest(request, oSvcHeaderSendStmt, sROrgName, sDistributionChnl, sMsgFulfillmentId, sRFN, sRLN, sRAdrLine1, sRAdrLine2, sRCity, sRState, sRZip, sRFaxNbr, sMbrId, sDptCode, sPlatformCode, sSDRPersonId, sMemberGenKey, sProvID, sProvIDType, sType,sAttachmentCategory,sReprintSystemOfRecord);
            Continuation con = new Continuation(iTIMEOUT_INT_SECS );
            sServiceRequest = con.addHttpRequest(request);
            con.state = rtiDataTemplate;
            con.continuationMethod = 'processSendStmtResponse';
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
            return con;
        }
        catch(Exception ex)
        {
            showErrorMsg(bIsPageFromRTI);HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_LC_HUM', 'initiateSendStmtRequest');
            return NULL;
        }
    }
    @AuraEnabled (continuation=true)
    public static Object processSendStmtResponse(List<string> labels,Object state)
    {
        try
        {
            HttpResponse response = Continuation.getResponse(labels[0]);
            if(response != null)
                return JSON.serialize(responseParseSendStmt(response,state));
            else{
                return null;
                }
        }
        catch(Exception ex)
        {

            HUMExceptionHelper.logErrors(ex,'Claim_Send_Statement_LC_HUM','processResponse');
            return null;

        }
    }

    /*
    * Method Name   :  responseParseSendStmt
    * Description   :  This method is used to check response
    * Return Type   :  void
    * Parameters    :  HttpResponse obj
    */
    @testvisible
    private static object responseParseSendStmt(HttpResponse sResponse,Object state)
    {
        Integer statusCode = sResponse.getStatusCode();

        string myMsg;
		object clmDataSwitch =  state;
        Map<String, Object> clmDataSwitchObj = (Map<String, Object>) JSON.deserializeUntyped(JSON.serialize(clmDataSwitch));
        string sClmPage = string.valueOf(clmDataSwitchObj.get('bIsPageFromClm'));
        if (sClmPage == 'true'){
            bIsPageFromRTI = false;
        }else{
            bIsPageFromRTI = true;
        }

        if (statusCode == HTTP_OK_HUM || statusCode == HTTP_ACCEPTED_HUM)
        {
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM oResponse = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM();
            oResponse = (ClaimSendStmt_Mbr_ServiceResponse_H_HUM)JSON.deserialize(sResponse.getBody(), ClaimSendStmt_Mbr_ServiceResponse_H_HUM.class);

            if(oResponse.ReprintResponse != null)
            {
                ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResponse oDetailsResponse = oResponse.ReprintResponse;
                ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResult oDetailsResult = oDetailsResponse.ReprintResult;
                if(oDetailsResult != null)
                {
                    if(oDetailsResult.Status != null && (oDetailsResult.Status == 'Printed' || oDetailsResult.Status == 'Success') && bIsPageFromRTI)
                    {
                        return myMsg='Send Print Item Request successfully submitted.';
                    }
                    else if(oDetailsResult.Status != null && (oDetailsResult.Status == 'Printed' || oDetailsResult.Status == 'Success') && !bIsPageFromRTI)
                    {
                        return myMsg= 'Send Claim Statement Request successfully submitted.';
                    }
                    else   return showErrorMsg(bIsPageFromRTI);
                }
                else   return showErrorMsg(bIsPageFromRTI);
            }
            else return   showErrorMsg(bIsPageFromRTI);
        }
        else  return   showErrorMsg(bIsPageFromRTI);
    }
    /*
    * Method Name   :  getAccDetails
    * Description   :  This method is used for getting details from Account associated to Policy Member
    * Return Type   :  List<POLICY_Member__C>
    * Parameters    :  ID sRecId
    */

    public static List<MemberPlan> getAccDetails(ID sRecId)
    {
        try
        {
            return (List<MemberPlan>)[SELECT Id, name, Member_Dependent_Code__c, Member_Id_Base__c,Policy_Platform__c, Member.Name, Member.FirstName, Member.LastName, Member.Enterprise_ID__c, Member.Mbr_Gen_Key__c,Member.Source_Platform_Code__c FROM MemberPlan WHERE ID =: sRecId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_LC_HUM', 'getAccDetails');
            return null;
        }
    }


/*
* Method Name   :  providerAddress
* Description   :  This method is used get provider details
* Return Type   :  void
* Parameters    :  NA
*/

	 @auraEnabled()
    public Static string providerAddress(ID PolicyMemberId)
    {
        MemberPlan objPolMember = [Select MemberId from MemberPlan where Id =:PolicyMemberId];
        DateTime currentLogin = [SELECT LastLoginDate FROM User WHERE Id =:UserInfo.getUserId()].LastLoginDate;
        if(objPolMember!= null && currentLogin != null)
        {
            List<Interaction__c> lstInteraction  = [Select Id, CreatedbyID, CreatedDate,interacting_with__c,LastModifiedDate,Interacting_With_type__c from Interaction__c where CreatedbyId =:String.escapeSingleQuotes( UserInfo.getUserId()) and LastModifiedDate >= :currentLogin order by LastModifiedDate desc limit 1];
            if(!lstInteraction.isEmpty() && lstInteraction[0] != null && lstInteraction[0].Interacting_With_type__c != null)
            {
                if(lstInteraction[0].Interacting_With_type__c.equalsIgnoreCase('Provider'))
                {
                    List<Interaction_Member__c> lstIntrcMember = [Select Id, Interacting_About__r.ShippingPostalCode, Interacting_About__r.ShippingCity, Interacting_About__r.ShippingStateCode, Interacting_About__r.ShippingStreet, CreatedbyID, CreatedDate, Interaction__c, Interacting_About__c,LastModifiedDate from Interaction_Member__c  where CreatedbyId =: UserInfo.getUserId() and Interaction__c =: lstInteraction[0].Id and Interacting_About__c =: objPolMember.MemberId order by LastModifiedDate desc limit 1];
                    if(lstIntrcMember != null && !lstIntrcMember.isEmpty())
                    {
                        bProviderInt = true;
                        List<Account> accID = [SELECT Id,Name,Enterprise_ID__c,Source_Platform_Code__c,ShippingStreet, ShippingCity,ShippingStateCode,ShippingPostalCode, ShippingState, ShippingCountry , ShippingCountryCode FROM Account where id =: lstInteraction[0].interacting_with__c limit 1];
                        sRefAddressLine1 =  accId[0].ShippingStreet;
                        sRefAddressline2 = sRefAddressline2;
                        sRefStatecode = accID[0].ShippingStateCode;
                        sRefCity = accID[0].ShippingCity;
                        sRefZipcode = accID[0].ShippingPostalCode;
			 			string sPrvName = accID[0].Name;
                        sProvIDType = accID[0].Source_Platform_Code__c;
                        sProvID = accID[0].Enterprise_ID__c;
			 			return '{"sRefAddressLine1": "' + '' + '","sRefAddressLine2": "' + '' + '","sRefStatecode": "' + '' + '","sRefCity": "' + '' + '","sRefZipcode": "' + '' + '","sPrvName": "' + sPrvName + '"}';
                    }
                    else
                    {

						bWarning = System.Label.ClaimSendStatementWarning;
                        return bWarning;
                    }
                }
                else
                {

					bWarning = System.Label.ClaimSendStatementWarning;
                    return bWarning;
                }
            }
            else
            {

				bError=System.Label.ClaimSendStatementError;
                return bError;
            }
        }
		 return null;
    }

 /*
* Method Name   :  showErrorMsg
* Description   :  This method is used for showing error
* Return Type   :  NA
* Parameters    :  NA
*/
public static object showErrorMsg(boolean bIsPageFromRTIval)
{
    string myMsg;
    if(bIsPageFromRTIval)

		myMsg = System.Label.SEND_PRINT_ITEM_ERROR_MSG;

    else

		myMsg = System.Label.SEND_CLAIM_ITEM_ERROR_MSG;
    return myMsg;
}

    /*
    * Method Name : getUserQueue
    * Description : This method is used to get User's current queue
    * Return type : string
    * Parameter   : Id
    */
    public static String getUserQueue(Id userId)
    {
        if(String.isNotBlank(userId))    return [Select Current_Queue__c From User Where Id =: userId].Current_Queue__c;
        return '';
    }

	@AuraEnabled 
    public static boolean createCaseInteraction(String sCaseID, String sInteractionID)
       {
        try{
        list<Case_interaction__c> lstCaseInt = [Select id from Case_interaction__c where interaction__c = :sInteractionID AND Case__c = :sCaseID Limit 1];
		if(lstCaseInt.isEmpty())
        {
            Case_Interaction__c oCaseInt = new Case_Interaction__c(Case__c = sCaseID, Interaction__c = sInteractionID);
            Insert oCaseInt;
            return true;
        }
        else
        {      
           return false;
        }
        }
        catch(exception e){
            system.debug('exception'+e);
            return false;
           }
       }
	   
    @AuraEnabled 
    public static boolean updateInteractionOnCase(String sCaseID, String sInteractionID)
    {
        case objCase = new case();
        Interaction__c objInteraction = Logging_LC_HUM.getInteractionDetails(sInteractionID);
                if(objInteraction != null){
                    objCase.Origin = objInteraction.Interaction_Origin__c;
                    objCase.Interacting_With_type__c =objInteraction.Interacting_With_type__c;
                    objCase.Interacting_With__c = objInteraction.Interacting_With__c;
                    objCase.Interacting_With_Name__c = objInteraction.Caller_Name__c;
                    objCase.Id = sCaseID;
                }
        List<Case> lstCase = new List<Case>{objCase};
        List<Database.SaveREsult> lstSaveResults = Database.update(lstCase, false);
        boolean bIsError = HUMExceptionHelper.processSaveResults(lstSaveResults, lstCase, 'Claim_Send_Statement_LC_HUM', 'updateCase', 'Case');
        return bIsError;
        
    }
}