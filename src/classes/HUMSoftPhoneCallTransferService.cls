/**************************************************************************** 
Apex Class Name  : HUMSoftPhoneCallTransferService
Version          : 1.0  
Created Date     : June 3 2014 
Function         : This is called from softphone to get data required for call transfer
Test Class       : HUMSoftPhoneCallTransferServiceTest

Modification Log: 
 * Developer Name           Code Review                  Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth            17169                     06/03/2014                  Original Version
 * Shruthi Karanth            17169                     07/23/2014                  Updating the mappings as per new design
 * Shruthi Karanth            17369                     08/08/2014                  Code clean up
 * Shruthi Karanth            17477                     08/19/2014                  Refactored createResponse as per review comments
 * Shruthi Karanth            17532                     08/21/2014                  Changes for Data model update - Policy__c.Segment_Indicator__c
 * Shruthi Karanth            18178                     09/25/2014                  Updated query to fetch tax id which will be mapped for Providers
 * Shruthi Karanth            19474                     11/20/2014                  Changes to mapping as per Defect 159968.0001
 * Shruthi Karanth            21469                     03/12/2015                  Added Segment Indicator to Policy Member SOQL to change the mapping of Business Segment indicator
 * Gargey Sharma              23879                     06/11/2015                  Removed decryption for InquiredAboutId, InquiringForId, InquiredAboutDOB, InquiredAboutFirstName, 
 																																										InquiredAboutLastName, InquiringForDOB, InquiringForFirstName, InquiringForLastName, MemberId, 
 																																										InquiredAboutGroupId, InquiringForGroupId fields as per REQ - 191230
 * Chaitanya																						1/25/2017										Fixed Null Pointer exception - REQ - 296917 . UUID from Softphone is sent as null
 * Pradeepkumar Dani		 102268 					06/16/2017					Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
 * Pooja Kumbhar									    06/12/2020				    Segment Indicator and LOB Field Mapping softphone
 * Riddhi Mittal									    07/30/2020				 CCP functionality is shutting down so removing its functionality 
 * Keerti Durbha                                        08/03/2020                 1039760- PR00091318 - CR05  - Enabling Call Trasnfer button for OSB policies- Assing Product__c to policy Member query
 * Asish Kumar Behera                                   11/10/2020                  1309260 PR00095366 - FR 01.06A1  Softphone app must have access to system-of-record
 * Asish Kumar Behera                                   11/17/2020                  1309260 Code Review Feedback from Consumer Team
 * Shreya Agrawal										02/03/2022					US3029788 - Updated to without Sharing
 * Santhi Mandava                                       01/20/2023                  User Story 3861715: T1PRJ0865978 - MF 20754- Lightning - Consumer/Addition of Health Cloud Object reference changes in Legacy softphone webservice classes
************************************************************************************************************************************************************************************/   
global without sharing class HUMSoftPhoneCallTransferService 
{ Public static Boolean ccpOnOff {get; set;}  
    /*  
    * Method name : getCallTransferData
    * Description : This method is exposed as a webservice method which is called by Softphone during call transfer
                    to get the required details about the caller.
    * Return Type : HUMSoftPhoneCallTransferData 
    * Parameter   : Requires 1 argument ,UUID of type string
    */ 
     webService static HUMSoftPhoneCallTransferHelper getCallTransferData(String UUID) 
     {
        try
        {
            Boolean isLightingSwitchON = HUMUtilityHelper.isCRMFunctionalityON('3861715');
            if(!String.isEmpty(UUID))
            {
	            List<Integration_Mapping__c> lstsoftphoneCallRouting = new List<Integration_Mapping__c>();
	            Map<String,String> mapSoftPhone = new Map<String,String>();
	            String sQry = 'SELECT Mapping__c FROM Integration_Mapping__c WHERE Record_ID__c = :UUID ORDER BY CreatedDate DESC Limit 1';
	            lstsoftphoneCallRouting = Database.query(sQry);    
	            if(!lstsoftphoneCallRouting.isEmpty()) { mapSoftPhone = HUMUtilityHelper.getSoftPhoneIntegrationMapping(lstsoftphoneCallRouting[Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c)]); }
                if(isLightingSwitchON){
                    if(mapSoftPhone != Null && !mapSoftPhone.isEmpty()){
                        if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjMemberPlan) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjMemberPlan).containsIgnoreCase(Label.HUMNullValue))
                        {
                            String sMemPlanId = mapSoftPhone.get(Label.HUMSPhoneMappingObjMemberPlan);
                            if(String.isNotBlank(sMemPlanId)){
                                return createResponseLightning(mapSoftPhone);
                            }else{
                                return createResponse(mapSoftPhone);
                            }
                        }else{
                            return createResponse(mapSoftPhone);  
                        }
                    }
                }
                else{
                    return createResponse(mapSoftPhone);  
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'getCallTransferData');
        }    
       return null;  
    }

    /*  
    * Method name : createResponseLightning
    * Description : This method will be used to build the response structure using lightning objects which is returned to softphone.
    * Return Type : HUMSoftPhoneCallTransferHelper
    * Parameter : Requires 1 argument, of type map
    */
    public static HUMSoftPhoneCallTransferHelper createResponseLightning(Map<String,String> mapSoftPhone)
    {
        HUMSoftPhoneCallTransferHelper responseMsg = null;
        SoftPhoneCallTransfer_LH_HUM objResponse = SoftPhoneCallTransfer_LS_HUM.createResponse(mapSoftPhone);
        responseMsg = mapFieldResponse(objResponse);
        return responseMsg;
    }
    
    /*  
    * Method name : mapFieldResponse
    * Description : This method will be used to prepare response object(HUMSoftPhoneCallTransferHelper) which is returned to softphone.
    * Return Type : HUMSoftPhoneCallTransferHelper
    * Parameter : Requires 1 argument, of type SoftPhoneCallTransfer_LH_HUM
    */
    public static HUMSoftPhoneCallTransferHelper mapFieldResponse(SoftPhoneCallTransfer_LH_HUM objResponse){
        HUMSoftPhoneCallTransferHelper responseMsg = new HUMSoftPhoneCallTransferHelper();
        if(objResponse !=null){
        responseMsg.ASOIndicator = objResponse.ASOIndicator;
        responseMsg.BusinessSegmentIndicator = objResponse.BusinessSegmentIndicator;
        responseMsg.CallerType = objResponse.CallerType;
        responseMsg.DentalOIChangeIndicator = objResponse.DentalOIChangeIndicator;
        responseMsg.InquirerFirstName  = objResponse.InquirerFirstName;
        responseMsg.HdphIndicator = objResponse.HdphIndicator;
        responseMsg.InquiredAboutDOB = objResponse.InquiredAboutDOB;
        responseMsg.InquiredAboutFirstName = objResponse.InquiredAboutFirstName;
        responseMsg.InquiredAboutGroupId = objResponse.InquiredAboutGroupId;
        responseMsg.InquiredAboutId = objResponse.InquiredAboutId;
        responseMsg.InquiredAboutMarketId = objResponse.InquiredAboutMarketId;
        responseMsg.InquiredAboutPlatformCode = objResponse.InquiredAboutPlatformCode;
        responseMsg.InquiredAboutRelation = objResponse.InquiredAboutRelation;
        responseMsg.InquiredAboutType = objResponse.InquiredAboutType;
        responseMsg.InquiredAboutLastName = objResponse.InquiredAboutLastName;
        responseMsg.InquiringForDOB = objResponse.InquiringForDOB;
        responseMsg.InquiringForFirstName = objResponse.InquiringForFirstName;
        responseMsg.InquiringForGenKey = objResponse.InquiringForGenKey;
        responseMsg.InquiringForGroupId = objResponse.InquiringForGroupId;
        responseMsg.InquiringForId = objResponse.InquiringForId;
        responseMsg.InquiringForLastName = objResponse.InquiringForLastName;
        responseMsg.InquiringForMarketId = objResponse.InquiringForMarketId;
        responseMsg.InquiringForPlatformCode = objResponse.InquiringForPlatformCode;
        responseMsg.InquiringForRelation = objResponse.InquiringForRelation;
        responseMsg.InquiringForType = objResponse.InquiringForType;
        responseMsg.InquiryId = objResponse.InquiryId;
        responseMsg.InquiryReason = objResponse.InquiryReason;
        responseMsg.LastName = objResponse.LastName;
        responseMsg.MajorLineOfBusiness = objResponse.MajorLineOfBusiness;
        responseMsg.MedicalOIChangeIndicator = objResponse.MedicalOIChangeIndicator;
        responseMsg.MemberGenKey = objResponse.MemberGenKey;
        responseMsg.MemberId = objResponse.MemberId;
        responseMsg.MtvBusinessLevel5 = objResponse.MtvBusinessLevel5;
        responseMsg.MtvBusinessLevel7 = objResponse.MtvBusinessLevel7;
        responseMsg.ProductTypeCode = objResponse.ProductTypeCode;
        responseMsg.ReferenceId = objResponse.ReferenceId;
        responseMsg.ReqHomeOfficeSecurity = objResponse.ReqHomeOfficeSecurity;
        responseMsg.SellingLedger = objResponse.SellingLedger;
        responseMsg.StateOfIssue = objResponse.StateOfIssue;
        responseMsg.UUID = objResponse.UUID;
        responseMsg.VENDOR_SUB_GROUPID = objResponse.VENDOR_SUB_GROUPID;
        responseMsg.VENDOR_ASOFDATE = objResponse.VENDOR_ASOFDATE;
        responseMsg.VENDOR_GROUPID = objResponse.VENDOR_GROUPID;
        }
        return responseMsg;
    } 

    /*  
    * Method name : createResponse
    * Description : This method will be used to build the response structure which is returned to softphone.
    * Return Type : HUMSoftPhoneCallTransferData
    * Parameter : Requires 1 argument, of type map
    */
    public static HUMSoftPhoneCallTransferHelper createResponse(Map<String,String> mapSoftPhone)
    {
        try
        {
            String sQry = '';
            Policy_Member__c policyMember = null;
            Account intWithAcc = null;
            Account intAboutAcc = null;
            Case caseObj = null;
            Interaction__c interaction = null;
            Interaction_Member__c intMember = null;
            HUMSoftPhoneCallTransferHelper responseMsg = null;
            String sMarketId = '';
            if(mapSoftPhone != null)
            {
                if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjInteraction) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjInteraction).containsIgnoreCase(Label.HUMNullValue))
                {
                    interaction = getInteraction(mapSoftPhone.get(Label.HUMSPhoneMappingObjInteraction));
                }
                if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjInqFor) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjInqFor).containsIgnoreCase(Label.HUMNullValue))
                {
                    intWithAcc = getAccount(mapSoftPhone.get(Label.HUMSPhoneMappingObjInqFor));
                }
                if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjInqAb) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb).containsIgnoreCase(Label.HUMNullValue))
                {
                    intAboutAcc = getAccount(mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb));
                }
                if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjCase) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjCase).containsIgnoreCase(Label.HUMNullValue))
                {
                    caseObj = getCase(mapSoftPhone.get(Label.HUMSPhoneMappingObjCase));
                }
                if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjPolicyMember) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjPolicyMember).containsIgnoreCase(Label.HUMNullValue))
                {
                    policyMember = getPolicyMember(mapSoftPhone.get(Label.HUMSPhoneMappingObjPolicyMember));
                    sMarketId = deriveMarketId(policyMember);
                }
                if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjInqAb) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb).containsIgnoreCase(Label.HUMNullValue)
                    && mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjInteraction) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjInteraction).containsIgnoreCase(Label.HUMNullValue))
                {
                    intMember = getInteractionMember(mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb),mapSoftPhone.get(Label.HUMSPhoneMappingObjInteraction));
                }
            responseMsg = new HUMSoftPhoneCallTransferHelper(policyMember,intWithAcc,intAboutAcc,caseObj,interaction,sMarketId,intMember);
            return responseMsg;
            
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'createResponse');
            return null; 
        }   
    }
    
    /*  
    * Method name : deriveMarketId
    * Description : This method will be used to derive the Market Id based on the product of the policy.
    * Return Type : String
    * Parameter   : Requires 1 argument, Policy_Member__c object
    */
    public static String deriveMarketId(Policy_Member__c policyMember)
    {
        try
        {
            String sProdLine =  policyMember.Policy__r.Product__r.Name;
            HUMSoftPhoneDeriveSellingLedger__c medicareLOB =  HUMSoftPhoneDeriveSellingLedger__c.getValues('Medicare LOB');
            HUMSoftPhoneDeriveSellingLedger__c medicaidLOB =  HUMSoftPhoneDeriveSellingLedger__c.getValues('Medicaid LOB');
            HUMSoftPhoneDeriveSellingLedger__c comLOB1 =  HUMSoftPhoneDeriveSellingLedger__c.getValues('LOBCombination1');
            HUMSoftPhoneDeriveSellingLedger__c comLOB2 =  HUMSoftPhoneDeriveSellingLedger__c.getValues('LOBCombination2');
            HUMSoftPhoneDeriveSellingLedger__c dentalProductLine =  HUMSoftPhoneDeriveSellingLedger__c.getValues('DentalProductLine');
            HUMSoftPhoneDeriveSellingLedger__c humanaOneSellingLedge =  HUMSoftPhoneDeriveSellingLedger__c.getValues('IsHumanaOneSellingLegderNum');
            
            if(sProdLine == dentalProductLine.LOB__c)
            {
                return dentalProductLine.StringValue__c;
            }
            else if(medicareLOB.LOB__c.contains(sProdLine) || medicaidLOB.LOB__c.contains(sProdLine))
            {
                if(comLOB1.LOB__c.contains(sProdLine))
                {
                    return comLOB1.StringValue__c;
                }
                else if(comLOB2.LOB__c.contains(sProdLine))
                {
                    return comLOB2.StringValue__c; 
                }
                else
                {
                    return medicareLOB.StringValue__c;
                }
            }
            else
            {
                 return policyMember.Policy__r.Selling_Market_Number__c;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'deriveMarketId');
            return null; 
        }
    }
    
    /*  
    * Method name : fetchIsHumanaOne
    * Description : This method will be used to get the IsHumanOne value for the Policy member's group gy calling 
                    the GetGBEInfo method of GroupSearchService.
    * Return Type : Boolean
    * Parameter   : Requires 1 argument, GroupId
    */
    public static Boolean fetchIsHumanaOne(String sGroupId)
    {  
	  ccpOnOff = false;
        //On/Off switch functionality for CCP 
         ccpOnOff= HUMUtilityHelper.isCRMFunctionalityON('1185084');
         
        if(ccpOnOff)
         {
         return false;
         }
         else
         { 
       try
       {
           HUMIntGroupSearchServiceHelper.RequestGroupExchangeDTO  gbeData = new HUMIntGroupSearchServiceHelper.RequestGroupExchangeDTO();
           HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO gbeRespData = new HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO();
           HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO finalGBEData = new HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO();
           HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO groupData = new HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO();
           HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO exchangeData = new HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO(); 
           HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO[] GroupExchangeDetailDTO = new HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO[]{};
           groupData.GroupId = sGroupId ;
           groupData.Platform = HUMConstants__c.getInstance('GRPSVC_PLATFORM').Stringvalue__c ; 
           groupData.StartDate = (Datetime.now()).format('MM/dd/yyyy');
           groupData.EndDate = 'NONE';
           groupData.BenefitSequenceNumber = '';
           groupData.BenefitPackageId = '';
           groupData.SubGroupId = '';
           
           GroupExchangeDetailDTO.add(groupData);        
           exchangeData.GroupExchangeDetailDTO = GroupExchangeDetailDTO;
           gbeData.GroupExchangeDetailList = exchangeData ;
    
           HUMIntGroupSearchService.soap requestMsg = (HUMIntGroupSearchService.soap)HUMSearchHelper.getServiceRequestInstance('GroupSearchService',
                                                                        'HUMIntGroupSearchService.soap');       
           gbeRespData  = requestMsg.GetGBEInfo(gbeData);
           HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO respvar=  gbeRespData.GroupExchangeInfoDetailList;
           finalGBEData = respvar.GroupExchangeInfoDetailDTO[0];
           
           return finalGBEData.IsHumanaOne;
       }
       catch(Exception e)
       {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'fetchIsHumanaOne');
            return null; 
       }
	   }
    }
       
    /*  
    * Method name : getAccount
    * Description : This method will return account object for the Id passed as Input.
    * Return Type : Account
    * Parameter : Requires 1 argument, Account Id
    */
    public static Account getAccount(String sAccId)
    {
        try
        {
            List<Account> lstAcc = new List<Account>();
            String sQry = 'SELECT Id,Birthdate__c,FirstName,LastName,Individual_First_Name__c,Individual_Last_Name__c,Name,RecordType.Name,Enterprise_ID__c,Group_Number__c,'
                         +'Source_Platform_Code__c,(Select Consumer_Id__c from ConsumerIds__r order by lastmodifieddate desc limit 1)'
                         +',(Select Id,Name from policy_members__r order by lastmodifiedDate desc limit 1) from Account WHERE id = :sAccId LIMIT 1';
            lstAcc = Database.Query(sQry);
            if(!lstAcc.isEmpty()) 
            { 
                return lstAcc[0]; 
            }
            else
            {
                return null;
            }
         }
         catch(Exception e)
         {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'getAccount');
            return null; 
         }
     }
     
    /*  
    * Method name : getCase
    * Description : This method will return case object for the Id passed as Input.
    * Return Type : Case
    * Parameter : Requires 1 argument, Case Id
    */
    public static Case getCase(String sCaseId)
    {
        try
        {
            List<Case> lstCase = new List<Case>();
            String sQry = 'SELECT Origin,Interacting_About_Type__c,CaseNumber,CTCI_List__c, CTCI_List__r.Intent__r.Name,Account.Enterprise_ID__c,Account.Home_Office_Account__c'+
                          +' from Case WHERE id = :sCaseId LIMIT 1';
            lstCase = Database.Query(sQry);
            if(!lstCase.isEmpty()) 
            { 
                return lstCase[0]; 
            }
            else
            {
                return null;
            }
         }
         catch(Exception e)
         {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'getCase');
            return null; 
         }
     }
    
    /*  
    * Method name : getInteraction
    * Description : This method will return interaction object for the Id passed as Input.
    * Return Type : Interaction__c
    * Parameter : Requires 1 argument, Interaction Id
    */
    public static Interaction__c getInteraction(String sInteractionId)
    {
        try
        {
            List<Interaction__c> lstInteraction = new List<Interaction__c>();
            String sQry = 'SELECT Caller_Name__c,Interacting_With_type__c,UUID__c from Interaction__c WHERE id = :sInteractionId LIMIT 1';
            lstInteraction = Database.Query(sQry);
            if(!lstInteraction.isEmpty()) 
            { 
                return lstInteraction[0]; 
            }
            else
            {
                return null;
            }
         }
         catch(Exception e)
         {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'getInteraction');
            return null; 
         }
     }
     
    /*  
    * Method name : getPolicyMember
    * Description : This method will return policy member object for the Id passed as Input.
    * Return Type : Policy_Member__c
    * Parameter : Requires 1 argument, Policy Member Id
    */
    public static Policy_Member__c getPolicyMember(String sPolicyMemberId)
    {
        try
        {
            List<Policy_Member__c> lstPolicyMember = new List<Policy_Member__c>();
			String sQry;
			if(HUMUtilityHelper.isCRMFunctionalityON('1309260')){
				sQry = 'SELECT Name,Segment_Indicator__c,Member__r.Enterprise_ID__c,Member__r.recordType.name,ASO__c,Relationship__c,Issue_State__c,Policy__r.Product__r.Segment_Type__c,'+
                        + 'Policy__r.Major_LOB__c,Policy__r.Original_Major_LOB__c,Policy__r.Source_Legal_Entity__c,Policy__r.Source_Major_LOB__c,Policy__r.Product__r.ProductCode,'+
                        + 'Policy__r.Selling_Market_Number__c,Policy__r.Platform__c,Policy__r.Product__r.Name,Policy__r.Group_Number__c,Product__c,Policy__r.Product__r.HDHP_Indicator__c,'+
						+'Status__c,Policy__c,End_Date__c,Effective_Date__c,Group_Number__c,Policy__r.Source_Cust_Cov_Key__c'+
                        + ' from Policy_Member__c WHERE id = :sPolicyMemberId LIMIT 1';
			}
			else{
			
				sQry = 'SELECT Name,Segment_Indicator__c,Member__r.Enterprise_ID__c,Member__r.recordType.name,ASO__c,Relationship__c,Issue_State__c,Policy__r.Product__r.Segment_Type__c,'+
                        + 'Policy__r.Major_LOB__c,Policy__r.Original_Major_LOB__c,Policy__r.Source_Legal_Entity__c,Policy__r.Source_Major_LOB__c,Policy__r.Product__r.ProductCode,'+
                        + 'Policy__r.Selling_Market_Number__c,Policy__r.Platform__c,Policy__r.Product__r.Name,Policy__r.Group_Number__c,Product__c,Policy__r.Product__r.HDHP_Indicator__c'+
                        + ' from Policy_Member__c WHERE id = :sPolicyMemberId LIMIT 1';
			}
            lstPolicyMember = Database.Query(sQry);
            if(!lstPolicyMember.isEmpty()) 
            { 
                return lstPolicyMember[0]; 
            }
            else
            {
                return null;
            }
         }
         catch(Exception e)
         {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'getPolicyMember');
            return null; 
         }
     }
     
    /*  
    * Method name : getInteractionMember
    * Description : This method will return interaction member object for the Id passed as Input.
    * Return Type : Interaction_Member__c
    * Parameter : Requires 2 argument2, Interacting About Id and Interaction Id
    */
    public static Interaction_Member__c getInteractionMember(String sIntAboutId, String sInteractId)
    {
        try
        {
            List<Interaction_Member__c> lstInteractMember = new List<Interaction_Member__c>();
            String sQry = 'SELECT Interacting_About_Type__c from Interaction_Member__c WHERE Interacting_About__c = :sIntAboutId AND Interaction__c = :sInteractId LIMIT 1';
                        
            lstInteractMember = Database.Query(sQry);
            if(!lstInteractMember.isEmpty()) 
            { 
                return lstInteractMember[0]; 
            }
            else
            {
                return null;
            }
         }
         catch(Exception e)
         {
            HUMExceptionHelper.logErrors(e, 'HUMSoftPhoneCallTransferService', 'getInteractionMember');
            return null; 
         }
     }
}