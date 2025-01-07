/*****************************************************************************************************************************************************************************
    @Apex Class Name  : GenericHelper_LightningStrides_LH_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class Name  : GenericHelper_LightningStrides_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                 		  06/09/2022                 Original Version
* Nirmal Garg										  03/02/2022				 DF-7300 - Added logic for person id on account Id. fetchPersonId method.
******************************************************************************************************************************************************************************/

public with sharing class GenericHelper_LightningStrides_LH_HUM {
    public static string sClassName = 'GenericHelper_LightningStrides_LH_HUM';
    public class DataException extends Exception {}
    private static Boolean bIsPolicyPlanSwithON = (CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch') != Null) ? CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch').IsON__c:true;

    /*
    * Method Name: getStateDropdown
    * Description: method is used to retrieve state dropdown
    * Parameter: Nothing
    * Return Type: Map<string, string>
    */
    public Map<String, String> getStateDropdown() {
        List<SelectOption> optionList = new List<SelectOption>();
        Map<String, String> options = new Map<String, String>();
        
        try {
            optionList = HUMUtilityHelper.getStateFullNameValueWithCode();
            if(optionList.size() > 0) {
                for(selectOption option: optionList) {
                    options.put(option.getLabel(), option.getValue());
                }
            }
            
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getStateDropdown');
        }
        return options;
    }

    /*
    * Method Name: fetchAccountDetails
    * Description: method is used to fetch Account Details
    * Parameter: Case Record Id
    * Return Type: List<Account>
    */
    public List<Account> fetchAccountDetails(string caseRecordId) {
        List<Account> accountList = new List<Account>();
        List<Case> caseList = new List<Case>();
        string accountId = '';
        try {
            caseList = [Select Id, AccountId from Case where Id = :caseRecordId LIMIT 1];
            
            if(caseList.size() > 0) {
                for(Case c : caseList) {
                    accountId = c.AccountId;
                    break;
                }
                accountList = [Select Id, FirstName, Middle_Initial__c, LastName, Gender__c, 
                                Birthdate__c, 
                                ShippingAddress, ShippingStreet, ShippingCity, 
                                ShippingState, ShippingPostalCode, 
                                PersonMailingAddress, PersonMailingStreet, PersonMailingCity, 
                                PersonMailingState, PersonMailingPostalCode, 
                                PersonOtherAddress, PersonOtherStreet,
                                PersonOtherCity, PersonOtherState, PersonOtherPostalCode,
                                PersonEmail, Work_Email__c,
                                PersonHomePhone, PersonMobilePhone, PersonOtherPhone, 
                                Work_Phone_Ext__c, SSNLast4__c 
                                from Account  
                                where 
                                Id = :accountId LIMIT 1];
            }
            
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'fetchAccountDetails');
        }
        return accountList;
    }

    /*
    * Method Name: fetchPersonId
    * Description: method is used to fetch Enterprise Id
    * Parameter: Case Record Id
    * Return Type: String
    */
    public String fetchPersonId(string caseRecordId) {
        String personId = ''; 
        String personEnterpriseId = '';
        List<Case> caseList = new List<Case>();
        List<Account> accList = new List<account>();
        string accountId = '';
        try {
		
			if(string.isNotBlank(caseRecordId) && caseRecordId.startswith(Schema.SObjectType.Account.keyprefix)){
				accList = [Select Id, Name, Enterprise_Id__c from Account where id = :caseRecordId LIMIT 1];
                if(accList.size() > 0) {
                    for(Account acc : accList) {
                        personEnterpriseId = acc.Enterprise_Id__c;
                        break;
                    }
                }
			}else{
            caseList = [Select Id, AccountId from Case where Id = :caseRecordId LIMIT 1];
            
            for(Case c : caseList) {
                personId = c.AccountId;
                break;
            }

            if(caseList.size() > 0) {
                accList = [Select Id, Name, Enterprise_Id__c from Account where id = :personId LIMIT 1];
                if(accList.size() > 0) {
                    for(Account acc : accList) {
                        personEnterpriseId = acc.Enterprise_Id__c;
                        break;
                    }
                }
            }
            else {
                accList = [Select Id, Name, Enterprise_Id__c from Account where id = :caseRecordId LIMIT 1];
                if(accList.size() > 0) {
                    for(Account acc : accList) {
                        personEnterpriseId = acc.Enterprise_Id__c;
                        break;
                    }
                }
            }
			}
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'fetchAccountDetails');
        }
        return personEnterpriseId;
    }

    /*
    * Method Name: fetchPersonId
    * Description: method is used to fetch Plan Id
    * Parameter: MemberId
    * Return Type: Map<String, String>
    */
    public Map<String, String> fetchDetailsForOSA(string recordId) {
        List<MemberPlan> policyList = new List<MemberPlan>();
        Map<String, String> osaRequestMap = new Map<String, String>();
        String accountId = '';
        Boolean isProcessed = false;
        List<Case> caseList = new List<Case>();
        try {
            String objectName = String.valueOf(Id.valueOf(recordId).getSobjectType());
            if(objectName.toLowerCase() == 'case') {
                caseList = [Select Account.Id from Case where Id = :recordId LIMIT 1];
                if(caseList.size() > 0) {
                   for(Case c : caseList)  {
                       accountId = c.Account.Id;
                   }
                }
            }
            if(objectName.toLowerCase() == 'account') {
                accountId = recordId;
            }

            policyList = [Select Name, Plan.Contract_Number__c, 
                            Plan.PBP_Code__c, Plan.Medicare_Segment_ID__c, 
                            Plan.EffectiveFrom,Member_Coverage_Status__c,
                            Policy__r.Contract_Number__c, 
                            Policy__r.PBP_Code__c, Policy__r.Medicare_Segment_ID__c, 
                            Policy__r.Coverage_Plan_Effective_Date__c,
                            Member.personMailingPostalCode, EffectiveTo from MemberPlan 
                            where MemberId = :accountId and ETL_Record_Deleted__c = false and Member.ETL_Record_Deleted__c = false];
            
            if(policyList.size() > 0) {
                for(MemberPlan p : policyList) {
                    if(p.Member_Coverage_Status__c == 'Active') {
                        if(bIsPolicyPlanSwithON){
                            //adding null check
                            String EffDate= ( String.isNotBlank(String.valueOf(p.Plan.EffectiveFrom))) ?  String.valueOf(p.Plan.EffectiveFrom).split('-')[0] : '';
                            osaRequestMap.put('PlanID', 
                            p.Plan.Contract_Number__c + '-' + 
                            p.Plan.PBP_Code__c + '-' + 
                            p.Plan.Medicare_Segment_ID__c + '-' + 
                            EffDate);
                            isProcessed = true;
                            break;
                        }else {
                            String EffDate= ( String.isNotBlank(String.valueOf(p.Policy__r.Coverage_Plan_Effective_Date__c))) ?  String.valueOf(p.Policy__r.Coverage_Plan_Effective_Date__c).split('/')[2] : '';
                            osaRequestMap.put('PlanID', 
                            p.Policy__r.Contract_Number__c + '-' + 
                            p.Policy__r.PBP_Code__c + '-' + 
                            p.Policy__r.Medicare_Segment_ID__c + '-' + 
                            EffDate);
                            isProcessed = true;
                            break;
                        }
                    }
                }

                if(!isProcessed) {
                    for(MemberPlan p : policyList) {
                        if(p.Member_Coverage_Status__c == 'Future') {
                            if(bIsPolicyPlanSwithON){
                                String EffDate= (String.isNotBlank(String.valueOf(p.Plan.EffectiveFrom))) ?  String.valueOf(p.Plan.EffectiveFrom).split('-')[0] : '';
                                osaRequestMap.put('PlanID', 
                                p.Plan.Contract_Number__c + '-' + 
                                p.Plan.PBP_Code__c + '-' + 
                                p.Plan.Medicare_Segment_ID__c + '-' +
                                EffDate);
                                isProcessed = true;
                                break;
                            }else {
                                String EffDate= (String.isNotBlank(String.valueOf(p.Policy__r.Coverage_Plan_Effective_Date__c))) ?  String.valueOf(p.Policy__r.Coverage_Plan_Effective_Date__c).split('/')[2] : '';
                                osaRequestMap.put('PlanID', 
                                p.Policy__r.Contract_Number__c + '-' + 
                                p.Policy__r.PBP_Code__c + '-' + 
                                p.Policy__r.Medicare_Segment_ID__c + '-' +
                                EffDate);
                                isProcessed = true;
                                break;
                            }
                            
                        }
                    }
                }

                if(!isProcessed) {
                    for(MemberPlan p : policyList) {
 					//no parsing required here in lightning and End_Date__c(text) has been replaced with EffectiveTO(Date)
                        Date endDate = p.EffectiveTO;
                        Date currDate = Date.today();
                        Integer monthDiff = endDate.monthsBetween(currDate);
                        if(monthDiff < 19 && p.Member_Coverage_Status__c == 'Termed') {
                            if(bIsPolicyPlanSwithON){
                                String EffDate= (String.isNotBlank(String.valueOf(p.Plan.EffectiveFrom))) ?  String.valueOf(p.Plan.EffectiveFrom).split('-')[0] : '';
                                osaRequestMap.put('PlanID', 
                                p.Plan.Contract_Number__c + '-' + 
                                p.Plan.PBP_Code__c + '-' + 
                                p.Plan.Medicare_Segment_ID__c + '-' +
                                EffDate);
                                isProcessed = true;
                                break;
                            }else{
                                String EffDate= (String.isNotBlank(String.valueOf(p.Policy__r.Coverage_Plan_Effective_Date__c))) ?  String.valueOf(p.Policy__r.Coverage_Plan_Effective_Date__c).split('/')[2] : '';
                                osaRequestMap.put('PlanID', 
                                p.Policy__r.Contract_Number__c + '-' + 
                                p.Policy__r.PBP_Code__c + '-' + 
                                p.Policy__r.Medicare_Segment_ID__c + '-' +
                                EffDate);
                                isProcessed = true;
                                break;
                            } 
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
            system.debug('ex'+ex.getMessage()+'###'+ex.getLineNumber());
            HUMExceptionHelper.logErrors(ex, sClassName, 'fetchDetailsForOSA');
        }
        return osaRequestMap;
    }

    /*
    * Method Name: retrievePolicyMemberIds
    * Description: method is used to retrieve PolicyMemeberId
    * Parameter: groupRecordObject and personId
    * Return Type: String
    */
    public string retrievePolicyMemberIds(string groupOfRecordsObject, string personId) {
        string returnValue = '';
        GenericHelper_LightningStrides_LH_HUM helperClass = new GenericHelper_LightningStrides_LH_HUM();
        try {
            GroupOfRecords_DTO_HUM groupOfRecords = (GroupOfRecords_DTO_HUM)System.JSON.deserialize(groupOfRecordsObject, GroupOfRecords_DTO_HUM.class);
            for(GroupOfRecords_DTO_HUM.Details gor : groupOfRecords.details) {
                returnValue = helperClass.retrievePolicyMemberId(JSON.serialize(gor.product), personId);
                gor.policyMemberId = returnvalue;
            }
            returnValue = JSON.serialize(groupOfRecords);
        }
        catch(Exception ex) {
            system.debug('Exception in retrievePolicyMemberIds ---> '+ex.getMessage()+' at line no. --> '+ex.getLineNumber());
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrievePolicyMemberIds');
        }
        return returnValue;
    }

     /*
    * Method Name: retrievePolicyMemberId
    * Description: method is used to retrieve PolicyMemeberId
    * Parameter: groupRecordObject and personId
    * Return Type: String
    */
    public String retrievePolicyMemberId(string groupRecordObject, string personId) {
        string returnValue = 'No Policies Available';
        List<MemberPlan> policyMemberList = new List<MemberPlan>();
        Set<String> statusSet = new Set<String>();
        Set<Id> shortlistedIds = new Set<Id>();
        Member_Maintenance_Generics__c mmGenericsMonths;
        try {
            mmGenericsMonths = Member_Maintenance_Generics__c.getValues('Termed Months');
            EligiblityResponse_DTO_HUM.GroupRecord groupRecordDTO = 
            (EligiblityResponse_DTO_HUM.GroupRecord)System.JSON.deserialize(
                groupRecordObject, 
                EligiblityResponse_DTO_HUM.GroupRecord.class
            );
            string groupIdSearchString = groupRecordDTO.groupId + '%';
            string dualStatusString = (groupRecordDTO.dualDemoIndicator == 'true') ? 'Y' : 'N';
            string asoString = (groupRecordDTO.asoIndicator == 'true') ? 'Y' : 'N';
            boolean exhangeIndicatorString = (groupRecordDTO.exchangeIndicator == 'true') ? true : false;
			if(bIsPolicyPlanSwithON){
                policyMemberList = [
                    Select Id, Member_Coverage_Status__c, EffectiveTo from MemberPlan 
                    where 
                    Plan.Source_Cust_Cov_Key__c like :groupIdSearchString AND 
                    Product__c = :groupRecordDTO.product AND 
                    Product_Type__c = :groupRecordDTO.productType AND 
                    Product_type_Code__c = :groupRecordDTO.productTypeCode AND 
                    Policy_Platform__c = :groupRecordDTO.platformCode AND 
                    Member.Enterprise_Id__c = :personId AND 
                    Member.ETL_Record_Deleted__c = false AND
                    ETL_Record_Deleted__c = false
                ];
            }else{
                policyMemberList = [
                    Select Id, Member_Coverage_Status__c, EffectiveTo from MemberPlan 
                    where 
                    Policy__r.Source_Cust_Cov_Key__c like :groupIdSearchString AND 
                    Product__c = :groupRecordDTO.product AND 
                    Product_Type__c = :groupRecordDTO.productType AND 
                    Product_type_Code__c = :groupRecordDTO.productTypeCode AND 
                    Policy_Platform__c = :groupRecordDTO.platformCode AND 
                    Member.Enterprise_Id__c = :personId AND 
                    Member.ETL_Record_Deleted__c = false AND
                    ETL_Record_Deleted__c = false
                ];
            }
           
            if(policyMemberList.size() > 0) {
                for(MemberPlan policyMember : policyMemberList) {
                    if(policyMember.Member_Coverage_Status__c == 'Termed') {
                        Date endDate = policyMember.EffectiveTo;
                        Date currDate = Date.today();
                        Integer monthDiff = endDate.monthsBetween(currDate);
                        Integer termedMonths = Integer.valueOf(mmGenericsMonths.Message__c);
                        if(monthDiff < termedMonths) {
                            statusSet.add(policyMember.Member_Coverage_Status__c);
                            shortlistedIds.add(policyMember.Id);
                        }
                    }
                    else {
                        statusSet.add(policyMember.Member_Coverage_Status__c);
                        shortlistedIds.add(policyMember.Id);
                    }
                }
            }
            if(statusSet.size() > 0) {
                string status = '';
                if(statusSet.contains('Active')) {
                    status = 'Active';
                }
                else if(statusSet.contains('Future')) {
                    status = 'Future';
                }
                else if(statusSet.contains('Termed')) {
                    status = 'Termed';
                }
                for(MemberPlan policyMember : policyMemberList) {
                    if(HUMUtilityHelper.isCRMFunctionalityON('1645972')) {
                        if(policyMember.Member_Coverage_Status__c.toLowerCase() == status.toLowerCase() && shortlistedIds.contains(policyMember.Id)) {
                            returnValue = policyMember.Id;
                            break;
                        }
                    }
                    else {
                        if(policyMember.Member_Coverage_Status__c == status) {
                            system.debug('policyMember.Status__c ===> '+status);
                            if(status == 'Termed') {
                                Date endDate = policyMember.EffectiveTo;
                                Date currDate = Date.today();
                                Integer monthDiff = endDate.monthsBetween(currDate);
                                returnValue = (monthDiff < 19) ? policyMember.Id : 'No Policies Available';
                                break;
                            }
                            else {
                                returnValue = policyMember.Id;
                                break;
                            }
                        } 
                    }
                }
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrievePolicyMemberId');
        }
        return returnValue;
    }
    /*
    * Method Name: generateGenericsForRequest
    * Description: method is used to retrieve PolicyMemeberId
    * Parameter: count of number of depenedents
    * Return Type: List<map<string, string>>
    */
    public List<map<string, string>> generateGenericsForRequest(integer loopCount) {
        List<map<string, string>> genericsMapList = new List<map<string, string>>();
        try {
            for(integer i = 0; i < loopCount; i++) {
                map<string, string> genericsMap = new map<string, string>();
                blob key = Crypto.generateAesKey(256);
                string hexKey = EncodingUtil.convertToHex(key);
                String requestId = hexKey.SubString(0,8)+ '-' + hexKey.SubString(8,12) + '-' + hexKey.SubString(12,16) + '-' + hexKey.SubString(16,20) + '-' + hexKey.substring(20,32);

                DateTime dDateTime = DateTime.now();
                string formattedDateTime = dDateTime.formatGmt('yyyy-MM-dd\'T\'HH:mm:ss.SSSSSS');

                string consumer = GLOBAL_CONSTANT_HUM.MM_UPD_SER_CONSUMER_NAME;

                genericsMap.put('requestId', requestId);
                genericsMap.put('timestamp', formattedDateTime);
                genericsMap.put('consumer', consumer);
                genericsMapList.add(genericsMap);
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateGenericsForRequest');
        }
        return genericsMapList;
    }

    /*
    * Method Name: procureEligibleDetails
    * Description: method is used to retrieve Policy Elgibile Details
    * Parameter: policyMemberId, isCommercial of boolean type
    * Return Type: map<string, string>
    */
    public map<string, string> procureEligibleDetails(string policyMemberId, boolean isGBO) {
        map<string, List<MemberPlan>> eligibleMap = new map<string, List<MemberPlan>>();
        List<MemberPlan> selfPolicyMemberList = new List<MemberPlan>();
        List<MemberPlan> depPolicyMemberList = new List<MemberPlan>();
        map<string, string> genericsMap = new map<string, string>();
        map<string, string> returnMap = new map<string, string>();
        boolean isDependentRequired = false;
        try {
            selfPolicyMemberList = procureSelfEligibleDetails(policyMemberId);
            if(selfPolicyMemberList.size() > 0) {
                eligibleMap.put('self', selfPolicyMemberList);
                for(MemberPlan policyMember : selfPolicyMemberList) {
                    isDependentRequired = (policyMember.RelationshipToSubscriber == 'SUBSCRIBER' || policyMember.RelationshipToSubscriber == 'EMPLOYEE') ? true : false;
                    break;
                }
                if((isGBO && isDependentRequired) || Test.isRunningTest()) {
                    depPolicyMemberList = procureDepEligibleDetails(policyMemberId);
                    if(depPolicyMemberList.size() > 0) {
                        eligibleMap.put('dependents', depPolicyMemberList);
                    }
                }
            }

            blob key = Crypto.generateAesKey(256);
            string hexKey = EncodingUtil.convertToHex(key);
            String requestId = hexKey.SubString(0,8)+ '-' + hexKey.SubString(8,12) + '-' + hexKey.SubString(12,16) + '-' + hexKey.SubString(16,20) + '-' + hexKey.substring(20,32);

            DateTime dDateTime = DateTime.now();
            string formattedDateTime = dDateTime.formatGmt('yyyy-MM-dd\'T\'HH:mm:ss.SSSSSS');

            string consumer = GLOBAL_CONSTANT_HUM.MM_UPD_SER_CONSUMER_NAME;

            genericsMap.put('requestId', requestId);
            genericsMap.put('timestamp', formattedDateTime);
            genericsMap.put('consumer', consumer);

            returnMap.put('data', System.JSON.serialize(eligibleMap));
            returnMap.put('generics', System.JSON.serialize(genericsMap));

        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrievePolicyMemberId');
        }
        return returnMap;
    }

    /*
    * Method Name: procureSelfEligibleDetailsList
    * Description: method is used to retrieve Policy Elgibile Details for self
    * Parameter: policyMemberId
    * Return Type: List<Policy_Member__c>
    */
    public map<string, string> procureEligibleDetailsList(List<string> policyMemberIds, boolean isGBO) {
        map<string, List<MemberPlan>> eligibleMap = new map<string, List<MemberPlan>>();
        List<MemberPlan> selfPolicyMemberList = new List<MemberPlan>();
        List<MemberPlan> selfPolicyMemberLis1 = new List<MemberPlan>();
        List<MemberPlan> depPolicyMemberList = new List<MemberPlan>();
        map<string, string> genericsMap = new map<string, string>();
        map<string, string> returnMap = new map<string, string>();
        boolean isDependentRequired = false;
        try {
            selfPolicyMemberList = procureSelfEligibleDetailsList(policyMemberIds);
            if(selfPolicyMemberList.size() > 0) {
                for(MemberPlan policy : selfPolicyMemberList) {
                    selfPolicyMemberLis1.add(policy);
                }
                eligibleMap.put('self', selfPolicyMemberLis1);
                for(MemberPlan policyMember : selfPolicyMemberList) {
                    isDependentRequired = (policyMember.RelationshipToSubscriber == 'SUBSCRIBER' || policyMember.RelationshipToSubscriber == 'EMPLOYEE') ? true : false;
                    break;
                }
                if((isGBO && isDependentRequired) || Test.isRunningTest()) {
                    depPolicyMemberList = procureDepEligibleDetailsList(policyMemberIds);
                    if(depPolicyMemberList.size() > 0) {
                        eligibleMap.put('dependents', depPolicyMemberList);
                    }
                }
            }
            blob key = Crypto.generateAesKey(256);
            string hexKey = EncodingUtil.convertToHex(key);
            String requestId = hexKey.SubString(0,8)+ '-' + hexKey.SubString(8,12) + '-' + hexKey.SubString(12,16) + '-' + hexKey.SubString(16,20) + '-' + hexKey.substring(20,32);
            DateTime dDateTime = DateTime.now();
            string formattedDateTime = dDateTime.formatGmt('yyyy-MM-dd\'T\'HH:mm:ss.SSSSSS');
            string consumer = GLOBAL_CONSTANT_HUM.MM_UPD_SER_CONSUMER_NAME;
            genericsMap.put('requestId', requestId);
            genericsMap.put('timestamp', formattedDateTime);
            genericsMap.put('consumer', consumer);
            
            returnMap.put('data', System.JSON.serialize(eligibleMap));
            returnMap.put('generics', System.JSON.serialize(genericsMap));
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrievePolicyMemberId');
        }
        return returnMap;
    }

    /*
    * Method Name: procureSelfEligibleDetails
    * Description: method is used to retrieve Policy Elgibile Details for self
    * Parameter: policyMemberId
    * Return Type: List<Policy_Member__c>
    */
    private static List<MemberPlan> procureSelfEligibleDetails(string policyMemberId) {
        List<MemberPlan> policyMemberList = new List<MemberPlan>();
        try {
            // relationship__c has been changed to RelationshipToSubscriber in memberPlan, effective_Date__C to EffectiveFrom,   end_date__c has been replaced with effectiveTO and 
            // //policy__R.Coverage_Plan_Effective_Date__c has been replaced with Plan.EffectiveFrom
            policyMemberList = [
                select id, name, member.Name, member.FirstName, member.LastName, member.birthdate__c, member.gender__c, 
                RelationshipToSubscriber,EffectiveFrom,EffectiveTo,Member_Coverage_Status__c, ehb_term_date__c, member.Enterprise_Id__c,
                Plan.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c,
                Plan.Contract_Number__c, Plan.PBP_Code__c, Plan.Medicare_Segment_ID__c, Plan.EffectiveFrom,
                member.salutation, member.suffix,Policy__r.Source_Cust_Cov_Key__c,
                Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Policy__r.Medicare_Segment_ID__c, Policy__r.Coverage_Plan_Effective_Date__c
                from MemberPlan 
                where 
                id = :policyMemberId LIMIT 1
            ];
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrievePolicyMemberId');
        }
		return policyMemberList;
    }
    
    /*
    * Method Name: procureSelfEligibleDetailsList
    * Description: method is used to retrieve Policy Elgibile Details for dependents
    * Parameter: policyMemberId of Self
    * Return Type: List<Policy_Member__c>
    */	
    @TestVisible
    private static List<MemberPlan> procureSelfEligibleDetailsList(List<String> policyMemberIds) {
        List<MemberPlan> policyMemberList = new List<MemberPlan>();
        Member_Maintenance_Generics__c mmGenerics;
        List<String> applicablePlatformList = new List<String>();
        try {
            mmGenerics = Member_Maintenance_Generics__c.getValues('Update Member');
            if(String.isNotEmpty(mmGenerics.Applicable_Platform_List__c)) {
                applicablePlatformList = mmGenerics.Applicable_Platform_List__c.split(',');
            }
            if(applicablePlatformList.size() == 0) {
                throw new DataException('Cannot proceed as the Platform List is empty. Please contact your System Administrator.');
            }
            policyMemberList = [Select id, name, member.Name, member.FirstName, member.LastName, member.birthdate__c, member.gender__c, 
                RelationshipToSubscriber, EffectiveFrom, EffectiveTo,Member_Coverage_Status__c, ehb_term_date__c, member.Enterprise_Id__c,
                plan.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c, 
                plan.Contract_Number__c, plan.PBP_Code__c, plan.Medicare_Segment_ID__c, plan.EffectiveFrom,
                member.salutation, member.suffix,Policy__r.Source_Cust_Cov_Key__c,
                Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Policy__r.Medicare_Segment_ID__c, Policy__r.Coverage_Plan_Effective_Date__c
                from MemberPlan 
                where 
                id in :policyMemberIds
                and Policy_Platform__c in :applicablePlatformList
            ];
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'procureSelfEligibleDetails');
        }
        return policyMemberList;
    }

    /*
    * Method Name: procureDepEligibleDetails
    * Description: method is used to retrieve Policy Elgibile Details for dependents
    * Parameter: policyMemberId of Self
    * Return Type: List<Policy_Member__c>
    */
    private static List<MemberPlan> procureDepEligibleDetails(string subscriberId) {
        List<MemberPlan> policyMemberList = new List<MemberPlan>();
        //subscriber--->changed to SubscriberPlanId__c
        try {
            policyMemberList = [
                select id, name, member.Name, member.FirstName, member.LastName, member.birthdate__c, member.gender__c, 
                RelationshipToSubscriber, EffectiveFrom, EffectiveTo,Member_Coverage_Status__c, ehb_term_date__c, member.Enterprise_Id__c,
                Plan.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c, 
                member.salutation, member.suffix,Plan.Contract_Number__c,Plan.PBP_Code__c,Plan.Medicare_Segment_ID__c,Plan.EffectiveFrom,
                Policy__r.Source_Cust_Cov_Key__c,Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, 
                Policy__r.Medicare_Segment_ID__c, Policy__r.Coverage_Plan_Effective_Date__c
                from MemberPlan 
                where 
                SubscriberPlanId__c = :subscriberId
                and ETL_Record_Deleted__c = false
                and member.ETL_Record_Deleted__c = false
            ];
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrievePolicyMemberId');
        }

        return policyMemberList;
    }

    /*
    * Method Name: procureDepEligibleDetailsList
    * Description: method is used to retrieve Policy Elgibile Details for dependents
    * Parameter: policyMemberId of Self
    * Return Type: List<Policy_Member__c>
    */
    @TestVisible
    private static List<MemberPlan> procureDepEligibleDetailsList(List<String> subscriberIds) {
        List<MemberPlan> policyMemberList = new List<MemberPlan>();
        Member_Maintenance_Generics__c mmGenerics;
        List<String> applicablePlatformList = new List<String>();
        try {
            mmGenerics = Member_Maintenance_Generics__c.getValues('Update Member');
            if(String.isNotEmpty(mmGenerics.Applicable_Platform_List__c)) {
                applicablePlatformList = mmGenerics.Applicable_Platform_List__c.split(',');
            }
            if(applicablePlatformList.size() == 0) {
                throw new DataException('Cannot proceed as the Platform List is empty. Please contact your System Administrator.');
            }
            policyMemberList = [
               select id, name,member.Name, member.FirstName, member.LastName, member.birthdate__c, member.gender__c, 
                RelationshipToSubscriber,  EffectiveFrom, EffectiveTo,Member_Coverage_Status__c, ehb_term_date__c, member.Enterprise_Id__c,
                Plan.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c, 
                member.salutation, member.suffix,Policy__r.Source_Cust_Cov_Key__c
                from MemberPlan 
                where 
                SubscriberPlanId__c in :subscriberIds
                and ETL_Record_Deleted__c = false
                and member.ETL_Record_Deleted__c = false
                and Policy_Platform__c in :applicablePlatformList
            ];
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'procureDepEligibleDetails');
        }
        return policyMemberList;
    }

    /*
    * Method Name: checkIfDateInFuture
    * Description: method is used to compare the entered date with todays date in EST as per US-US-1559373
    * Parameter: datevalue in string
    * Return Type: boolean
    */

    @AuraEnabled
    public static Boolean checkIfDateInFuture(String dateInput) {
        boolean returnValue = false;
        try {
            if(String.isBlank(dateInput)) {
                throw new DataException('Date Input is blank');
            }
            Datetime currDate = Datetime.now();
            String estDate = currDate.format('yyyy-MM-dd', 'America/New_York');
            String estDatetime = currDate.format('yyyy-MM-dd\'T\'HH:mm:ss.SSSSSS', 'America/New_York');

            DateTime inputDate = Date.valueOf(dateInput);

            returnValue = (inputDate > Date.valueOf(estDatetime)) ? true : false;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'checkIfDateInFuture');
        }
        return returnValue;
    }
}