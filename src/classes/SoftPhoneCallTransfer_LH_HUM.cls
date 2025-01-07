/**************************************************************************** 
Apex Class Name  : SoftPhoneCallTransfer_LH_HUM
Version          : 1.0  
Created Date     : June 4 2014 
Function         : It will used as a Base class for the response structure of HUMSoftPhoneCallTransfer_LS_HUM
Test Class       : SoftPhoneCallTransfer_LT_HUM

 * Developer Name           Code Review                  Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth            17169                    06/04/2014                  Original Version
 * Shruthi Karanth            17169                    07/23/2014                  Updated the mappings as per New design
 * Shruthi Karanth            18133                    09/24/2014                  Updated mappings for Provider And Group
 * Shruthi Karanth            18133                    09/25/2014                  Refactored the code into smaller methods
 * Shruthi Karanth            18959                    11/04/2014                  Fixed defect 159968.0001
 * Shruthi Karanth            19003                    11/05/2014                  Sending Inquiry Id And Reference Id as blank instead of Case # 
                                                                                   to fix issue 156659.0002
 * Ninad Patil                19474                    11/20/2014                  Changes to mapping as per Defect 159968.0001
 * Chaitanya Kumar            19602                    11/30/2014                  Fixed Defect 162894.0001
 * Chaitanya Kumar                                     12/01/2015                  Fixed Defect 168335.0001 , Populated Reference ID with Case Number
 * Praveen Kumar Parimi 	  72347       			   03/03/2017    		       Fixed CA ticket CA 6589406 aka REQ - 303507 NUll pointer Exception.
 * Pradeepkumar Dani		  102268 				   06/16/2017				   Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
 * Pooja Kumbhar									   06/12/2020				   Segment Indicator And LOB Field Mapping softphone
 * Pooja Kumbhar									   07/07/2020				   1201595: PR00091494 - PLT - Remove Temporary On-Off flag - MF WR-04914 - Business Segment Indicator Field And LOB field
 * Keerti Durbha                                       07/28/2020                  1039760- PR00091318 - CR05  - Enabling Call Trasnfer button for OSB policies- Major LOB for OSB  
 * Asish Kumar Behera                                  11/10/2020                  1309260 PR00095366 - FR 01.06A1  Softphone app must have access to system-of-record
 * Asish Kumar Behera                                  11/10/2020                  1309260 Code Review Feedback from Consumer Team
 * Arpit Jain                                          06/18/2021                  Cloned HUMSoftPhoneCallTransferHelper Helper class for lightning 
                                                                                   replacing policy member,policy reference by member Plan,purchaser plan respectively.
* Santhi Mandava                                       01/20/2023                  User Story 3861715: T1PRJ0865978 - MF 20754- Lightning - Consumer/Addition of Health Cloud Object reference changes in Legacy softphone webservice classes
* Mayur Pardeshi                                        04/18/2023                  US4271740 : T1PRJ0036776: Genesys - (Research) Call reasons are updated to dental when clicking on "Call Transfer" from a medical policy on Lightning (Agent about Member Call)

*******************************************************************************************************************************************************************************/ 

global with sharing class SoftPhoneCallTransfer_LH_HUM
{
    private String sLVPlatForm =  GLOBAL_CONSTANT_HUM.VOB_PLATFORM_LV_HUM;
    private String sEMPlatForm =  GLOBAL_CONSTANT_HUM.VOB_PLATFORM_EM_HUM;
     /*
     * Method Name   :   SoftPhoneCallTransfer_LH_HUM - constructor
     * Description   :   This is used to instantiate the SoftPhoneCallTransfer_LH_HUM which is returned by the service
     * Return Type   :   N/A
     * Parameters    :   MemberPlan,Account(Inquired By),Account(Inquired About),Case,Interaction__c,String(Market Id),Interaction_Member__c
     */
    global SoftPhoneCallTransfer_LH_HUM(MemberPlan memberPlan,Account inquiringForAcc,Account inquiredAboutAcc,Case caseObj,Interaction__c interaction,String sMarketId, Interaction_Member__c intMember )
    {
        if(inquiringForAcc != null) populateInquiringFor(inquiringForAcc); 
        
        if(inquiredAboutAcc != null) populateInquiringAbout(inquiredAboutAcc);   
        
        if(memberPlan != null){
            populateMemberPlanDetails(memberPlan);
            if(HUMUtilityHelper.isCRMFunctionalityON('1309260') && memberPlan.Plan.Platform__c == sLVPlatForm || memberPlan.Plan.Platform__c == sEMPlatForm)
            populateVendorGroupDetails(memberPlan);
        } 
        
        if(caseObj != null) populateCaseDetails(caseObj);
        
        if(interaction != null) populateInteractionDetails(interaction);
        
        if(intMember != null) InquiredAboutType = intMember.Interacting_About_Type__c; 
        
        if(inquiringForAcc != null && inquiredAboutAcc != null)
        {
	        if(inquiringForAcc.Id == inquiredAboutAcc.Id)
	        {    
	            if(inquiringForAcc.RecordType.Name == System.label.HUMMemberType) InquiringForId = InquiredAboutId;
  
	        }
        }
        InquiredAboutMarketId = sMarketId;
        InquiringForMarketId = sMarketId;
    }
    
    webservice String ASOIndicator{get; set;}  
    webservice String BusinessSegmentIndicator{get; set;}
    webservice String CallerType{get; set;}
    webservice String DentalOIChangeIndicator{get; set;}
    webservice String InquirerFirstName {get; set;}
    webservice String HdphIndicator{get; set;}
    webservice String InquiredAboutDOB{get; set;}
    webservice String InquiredAboutFirstName{get; set;}
    webservice String InquiredAboutGroupId{get; set;}
    webservice String InquiredAboutId{get; set;}
    webservice String InquiredAboutMarketId{get; set;}
    webservice String InquiredAboutPlatformCode{get; set;}
    webservice String InquiredAboutRelation{get; set;}
    webservice String InquiredAboutType{get; set;}
    webservice String InquiredAboutLastName{get; set;}
    webservice String InquiringForDOB{get; set;}
    webservice String InquiringForFirstName{get; set;}
    webservice String InquiringForGenKey{get; set;}
    webservice String InquiringForGroupId{get; set;}
    webservice String InquiringForId{get; set;}
    webservice String InquiringForLastName{get; set;}
    webservice String InquiringForMarketId{get; set;}
    webservice String InquiringForPlatformCode{get; set;}
    webservice String InquiringForRelation{get; set;}
    webservice String InquiringForType{get; set;}
    webservice String InquiryId{get; set;}
    webservice String InquiryReason{get; set;}
    webservice String LastName{get; set;}
    webservice String MajorLineOfBusiness{get; set;}
    webservice String MedicalOIChangeIndicator{get; set;}
    webservice String MemberGenKey{get; set;}
    webservice String MemberId{get; set;}
    webservice String MtvBusinessLevel5{get; set;}
    webservice String MtvBusinessLevel7{get; set;}
    webservice String ProductTypeCode{get; set;}
    webservice String ReferenceId{get; set;}
    webservice String ReqHomeOfficeSecurity{get; set;}
    webservice String SellingLedger{get; set;}
    webservice String StateOfIssue{get; set;}
    webservice String UUID{get; set;}
    webservice String VENDOR_SUB_GROUPID{get; set;}
    webservice String VENDOR_ASOFDATE{get; set;}
    webservice String VENDOR_GROUPID{get; set;}
  
    /*
     * Method Name : populateInquiringFor
     * Description : This Method is used to populate the Inquired By information in the response
     * Return type : void 
     * Paramater   : Account
     */
    private void populateInquiringFor(Account inquiringForAcc)
    {
        InquiringForDOB = inquiringForAcc.Birthdate__c;
        InquiringForFirstName = String.isEmpty(inquiringForAcc.FirstName)?inquiringForAcc.Individual_First_Name__c:inquiringForAcc.FirstName;
        InquiringForGenKey = inquiringForAcc.Enterprise_ID__c;
        if(!String.isEmpty(inquiringForAcc.Group_Number__c)) InquiringForGroupId = inquiringForAcc.Group_Number__c;
        InquiringForLastName = String.isEmpty(inquiringForAcc.LastName)?(String.isEmpty(inquiringForAcc.Individual_Last_Name__c)? inquiringForAcc.Name:inquiringForAcc.Individual_Last_Name__c):inquiringForAcc.LastName;
        InquiringForPlatformCode = inquiringForAcc.Source_Platform_Code__c;
        InquiringForId =  setInquiryId(inquiringForAcc,true);
        InquiringForType = inquiringForAcc.RecordType.Name;
    }
    
    /*
     * Method Name : populateInquiringAbout
     * Description : This Method is used to populate the Inquired About information in the response
     * Return type : void 
     * Paramater   : Account
     */
    private void populateInquiringAbout(Account inquiredAboutAcc)
    {
        InquiredAboutDOB = inquiredAboutAcc.Birthdate__c;
        InquiredAboutFirstName =String.isEmpty(inquiredAboutAcc.FirstName)?inquiredAboutAcc.Individual_First_Name__c:inquiredAboutAcc.FirstName;
        if(!String.isEmpty(inquiredAboutAcc.Group_Number__c)) InquiredAboutGroupId = inquiredAboutAcc.Group_Number__c;
        InquiredAboutPlatformCode = inquiredAboutAcc.Source_Platform_Code__c;
        InquiredAboutLastName = String.isEmpty(inquiredAboutAcc.LastName)?(String.isEmpty(inquiredAboutAcc.Individual_Last_Name__c)? inquiredAboutAcc.Name:inquiredAboutAcc.Individual_Last_Name__c):inquiredAboutAcc.LastName;
        InquiredAboutId =  setInquiryId(inquiredAboutAcc,false);             
        InquiredAboutType = inquiredAboutAcc.RecordType.Name;
    }
    
    /*
     * Method Name : setInquiryId
     * Description : This Method is used to set Interacting With and About Ids.  
     * Return type : Id 
     * Paramater   : String (Account type) and ID (Account)
     */
    private String setInquiryId(Account oAcc, boolean bIsWithType)
    {
        if(oAcc.RecordType.Name == system.label.HumProviderRecordType)
        {
            String sInquiredAboutId = (oAcc.ConsumerIds__r.size()>0)? oAcc.ConsumerIds__r[0].Consumer_Id__c:null;
            return sInquiredAboutId;
        }
        else if(oAcc.RecordType.Name == system.label.HUMAgentBrokerRecordTypeName)
        {               
            String sInquiredAboutId = (oAcc.ConsumerIds__r.size()>0)? oAcc.ConsumerIds__r[0].Consumer_Id__c:null;
            return sInquiredAboutId;
        }
        else if(oAcc.RecordType.Name == system.label.HUM_Group_Account_Record_Type && Schema.sObjectType.Account.fields.Group_Number__c.isAccessible())
        {
            return oAcc.Group_Number__c;
        }
        else
        {
            if(bIsWithType == true)
            {    
            
                String sInquiredAboutId = (oAcc.MemberPlans.size()>0)? oAcc.MemberPlans[0].Name:null;
                return sInquiredAboutId;
            }
            return null;    
        }       
    }
    
    /*
     * Method Name : populateMemberPlanDetails
     * Description : This Method is used to populate the MemberPlan information in the response
     * Return type : void 
     * Paramater   : MemberPlan
     */
    private void populateMemberPlanDetails(MemberPlan memberPlan)
    {
        Boolean callreasonswitch =HUMUtilityHelper.isCRMFunctionalityON('4271740');
        String majorLOB = ''; 
        String sMajorLob = '';
        String sBusSegIndicator;
        List<String> lstSplitBusinessSegIndicatorlist;
           sBusSegIndicator = memberPlan.Segment_Indicator__c;
            
            if(String.isNotBlank(sBusSegIndicator))
            {
            lstSplitBusinessSegIndicatorlist = sBusSegIndicator.split('-');
            BusinessSegmentIndicator = lstSplitBusinessSegIndicatorlist[0];
            }
			else{
			BusinessSegmentIndicator = sBusSegIndicator;
			}
            if(callreasonswitch){
                majorLOB = string.valueof((memberPlan.Policy__r.Original_Major_LOB__c !=null && memberPlan.Policy__r.Original_Major_LOB__c != '')? memberPlan.Policy__r.Original_Major_LOB__c: memberPlan.Plan.Major_LOB__c);
                sMajorLob = string.valueof(majorLOB !=null && majorLOB != '' && majorLOB.startsWith('EHB ') ? majorLOB.replace('EHB ','') : majorLOB);
            }
            else{
                majorLOB = string.valueof(memberPlan.Plan.Major_LOB__c);
                sMajorLob = '';
                if(String.isNotBlank(majorLOB)) sMajorLob = string.valueof(majorLOB !=null && majorLOB != '' && majorLOB.startsWith('EHB ') ? majorLOB.replace('EHB ','') : majorLOB);
                String PlatformCD = string.valueof(memberPlan.Plan.Platform__c);

                if(PlatformCD == 'CB'){
                    if(sMajorLob == 'DHL'){
                        sMajorLob = 'DHLD';
                    }
                    else if(sMajorLob == 'HMO'){
                        sMajorLob = 'HMOD';
                    }
                }

                else{
                    if(sMajorLob == 'PPO'){
                        sMajorLob = 'PPOD';
                    }
                    else if(sMajorLob.Contains('HMO OH')){
                        sMajorLob = 'HMOD';
                    }
                    else if(sMajorLob.Contains('VIS VH')){
                        sMajorLob = 'VIS';
                    }
                    else if(sMajorLob == 'DHL'){
                        sMajorLob = 'DHLD';
                    }
                } 
            }  
        MajorLineOfBusiness = HUMUtilityHelper.OSBMajorLOB(memberPlan.Product__c,sMajorLob);
        ASOIndicator = memberPlan.ASO__c; 
        InquiredAboutRelation = memberPlan.RelationshipToSubscriber;
        InquiringForRelation = memberPlan.RelationshipToSubscriber;
        MemberGenKey = memberPlan.Member.Enterprise_ID__c;
        MemberId = memberPlan.Name;
        MtvBusinessLevel5 = memberPlan.Plan.SourceSystemIdentifier;
        MtvBusinessLevel7 = memberPlan.Plan.Source_Major_LOB__c;
        ProductTypeCode = memberPlan.Plan.Product__r.ProductCode;
        StateOfIssue = memberPlan.Issue_State__c;
        SellingLedger = memberPlan.Plan.Selling_Market_Number__c;
        HdphIndicator = memberPlan.Plan.Product__r.HDHP_Indicator__c;
        if(memberPlan.member.recordType.name ==  System.label.HUMMemberType) InquiredAboutId = memberPlan.Name;
        if(String.isEmpty(InquiringForGroupId)) InquiringForGroupId = memberPlan.Plan.Policy_Group_Number__c;
        if(String.isEmpty(InquiredAboutGroupId)) InquiredAboutGroupId = memberPlan.Plan.Policy_Group_Number__c;
        if(String.isEmpty(InquiringForPlatformCode)) InquiringForPlatformCode = memberPlan.Plan.Platform__c;
        if(String.isEmpty(InquiredAboutPlatformCode)) InquiredAboutPlatformCode = memberPlan.Plan.Platform__c;  
    }
	
    /*
     * Method Name : populateCaseDetails
     * Description : This Method is used to populate the Case information in the response
     * Return type : void 
     * Paramater   : Case
     */
    private void populateCaseDetails(Case caseObj)
    {
        CallerType = caseObj.Origin;
        InquiredAboutType = caseObj.Interacting_About_Type__c;
        InquiryId ='';
        InquiryReason = (caseObj.CTCI_List__c <> null) ? caseObj.CTCI_List__r.Intent__r.Name : caseObj.CTCI_List__c;
        ReferenceId = caseObj.caseNumber;
        ReqHomeOfficeSecurity = String.valueOf(caseObj.Account.Home_Office_Account__c);
    }
    
    /*
     * Method Name : populateInteractionDetails
     * Description : This Method is used to populate the Case information in the response
     * Return type : void 
     * Paramater   : Interaction__c
     */
    private void populateInteractionDetails(Interaction__c interaction)
    {
        InquiringForType = interaction.Interacting_With_type__c;
        UUID = interaction.UUID__c;
        LastName = interaction.Caller_Name__c;
        InquirerFirstName = interaction.Caller_Name__c; 
    }
    
    /*
     * Method Name : populateVendorGroupDetails
     * Description : This Method is used to populate the numeric group information in the response
     * Return type : void 
     * Paramater   : MemberPlan
     */
    private void populateVendorGroupDetails(MemberPlan memberPlan)
    { 
        Boolean bSubgroupOnOffswitch = HUMUtilityHelper.isCRMFunctionalityON('1789465');
        if((memberPlan.Plan.Plan_Status__c).equalsignoreCase(GLOBAL_CONSTANT_HUM.VOB_ACTIVEMEMBERSTATUS_HUM)){
            VENDOR_ASOFDATE = system.today().format();
        }else if((memberPlan.Plan.Plan_Status__c).equalsignoreCase(GLOBAL_CONSTANT_HUM.SAVEOFFERS_TERMED)){
            VENDOR_ASOFDATE = memberPlan.EffectiveTo.format();
        }else if((memberPlan.Plan.Plan_Status__c).equalsignoreCase(GLOBAL_CONSTANT_HUM.VOB_FUTURE_HUM)){
            VENDOR_ASOFDATE = memberPlan.EffectiveFrom.format();
        }
        // As suggsted through Code review from consumer team, use source cust cov key from policy object not from policy plan to avoid loop
        If(string.isNotBlank(memberPlan.Plan.Source_Cust_Cov_Key__c))
        {
            VENDOR_GROUPID =(memberPlan.Plan.Platform__c.equalsignoreCase(sLVPlatForm)? memberPlan.Plan.Source_Cust_Cov_Key__c.subString(0,6):memberPlan.GroupNumber);
            // Switch here added to perform substring operation on Cust Key for first 9 char while switch is ON and for first 8 chars if its OFF
            if(bSubgroupOnOffswitch)
            {
                if(memberPlan.Plan.Platform__c.equalsignoreCase(sEMPlatForm)){
                    VENDOR_SUB_GROUPID = (String.isNotBlank(memberPlan.Plan.Source_Cust_Cov_Key__c) && memberPlan.Plan.Source_Cust_Cov_Key__c.length() >= 9 ) ? memberPlan.Plan.Source_Cust_Cov_Key__c.subString(0,9).trim() : '';
                }else if(memberPlan.Plan.Platform__c.equalsignoreCase(sLVPlatForm)){
                    VENDOR_SUB_GROUPID = (String.isNotBlank(memberPlan.Plan.Source_Cust_Cov_Key__c) && memberPlan.Plan.Source_Cust_Cov_Key__c.length() >= 12 ) ? memberPlan.Plan.Source_Cust_Cov_Key__c.subString(9,12).trim() : '';
                }
            }else
            {
                if(memberPlan.Plan.Platform__c.equalsignoreCase(sEMPlatForm)){
                    VENDOR_SUB_GROUPID = (String.isNotBlank(memberPlan.Plan.Source_Cust_Cov_Key__c) && memberPlan.Plan.Source_Cust_Cov_Key__c.length() >= 8 )? memberPlan.Plan.Source_Cust_Cov_Key__c.subString(0,8).trim() : '';
                }else if(memberPlan.Plan.Platform__c.equalsignoreCase(sLVPlatForm)){
                    VENDOR_SUB_GROUPID = (String.isNotBlank(memberPlan.Plan.Source_Cust_Cov_Key__c) && memberPlan.Plan.Source_Cust_Cov_Key__c.length() >= 12 )? memberPlan.Plan.Source_Cust_Cov_Key__c.subString(9,12).trim() : '';
                }
            }
        }
    }
}