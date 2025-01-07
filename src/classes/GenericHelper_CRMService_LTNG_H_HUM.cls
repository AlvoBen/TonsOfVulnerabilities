/**
 * @File Name          : GenericHelper_CRMService_LTNG_H_HUM.cls
 * @Description        : Helper Class for CRM Service LWC Aura Enabled classes
 * @Author             : ChangeMeIn@UserSettingsUnder.SFDoc
 * @Group              : Team Thunder
 * @Last Modified On   : 01-15-2021
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
 * 2.0    12/31/2020       Chetan Sindhe           To test copado
 * 3.0    1/15/2020                                US-1559373
**/
public with sharing class GenericHelper_CRMService_LTNG_H_HUM {
    public static string sClassName = 'GenericHelper_CRMService_LTNG_H_HUM';
    public class DataException extends Exception {}

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
        List<Policy_Member__c> policyList = new List<Policy_Member__c>();
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

            policyList = [Select Name, Policy__r.Contract_Number__c, 
                            Policy__r.PBP_Code__c, Policy__r.Medicare_Segment_ID__c, 
                            Policy__r.Coverage_Plan_Effective_Date__c, Status__c, 
                            Member__r.personMailingPostalCode, End_Date__c from Policy_Member__c 
                            where Member__r.Id = :accountId and ETL_Record_Deleted__c = false and Member__r.ETL_Record_Deleted__c = false];
            
            if(policyList.size() > 0) {
                for(Policy_Member__c p : policyList) {
                    if(p.Status__c == 'Active') {
                        osaRequestMap.put('PlanID', 
                        p.Policy__r.Contract_Number__c + '-' + 
                        p.Policy__r.PBP_Code__c + '-' + 
                        p.Policy__r.Medicare_Segment_ID__c + '-' + 
                        p.Policy__r.Coverage_Plan_Effective_Date__c.split('/')[2]);
                        isProcessed = true;
                        break;
                    }
                }

                for(Policy_Member__c p : policyList) {
                    if(p.Status__c == 'Active') {
                        osaRequestMap.put('PlanID', 
                        p.Policy__r.Contract_Number__c + '-' + 
                        p.Policy__r.PBP_Code__c + '-' + 
                        p.Policy__r.Medicare_Segment_ID__c + '-' +
                        p.Policy__r.Coverage_Plan_Effective_Date__c.split('/')[2]);
                        isProcessed = true;
                        break;
                    }
                }

                if(!isProcessed) {
                    for(Policy_Member__c p : policyList) {
                        if(p.Status__c == 'Future') {
                            osaRequestMap.put('PlanID', 
                            p.Policy__r.Contract_Number__c + '-' + 
                            p.Policy__r.PBP_Code__c + '-' + 
                            p.Policy__r.Medicare_Segment_ID__c + '-' +
                            p.Policy__r.Coverage_Plan_Effective_Date__c.split('/')[2]);
                            isProcessed = true;
                            break;
                        }
                    }
                }

                if(!isProcessed) {
                    for(Policy_Member__c p : policyList) {
                        Date endDate = Date.parse(p.End_Date__c);
                        Date currDate = Date.today();
                        Integer monthDiff = endDate.monthsBetween(currDate);
                        if(monthDiff < 19 && p.Status__c == 'Termed') {
                            osaRequestMap.put('PlanID', 
                            p.Policy__r.Contract_Number__c + '-' + 
                            p.Policy__r.PBP_Code__c + '-' + 
                            p.Policy__r.Medicare_Segment_ID__c + '-' +
                            p.Policy__r.Coverage_Plan_Effective_Date__c.split('/')[2]);
                            isProcessed = true;
                            break;
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
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
        GenericHelper_CRMService_LTNG_H_HUM helperClass = new GenericHelper_CRMService_LTNG_H_HUM();
        try {
            GroupOfRecords_DTO_HUM groupOfRecords = (GroupOfRecords_DTO_HUM)System.JSON.deserialize(groupOfRecordsObject, GroupOfRecords_DTO_HUM.class);
            for(GroupOfRecords_DTO_HUM.Details gor : groupOfRecords.details) {
                returnValue = helperClass.retrievePolicyMemberId(JSON.serialize(gor.product), personId);
                gor.policyMemberId = returnvalue;
            }
            returnValue = JSON.serialize(groupOfRecords);
        }
        catch(Exception ex) {
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
        List<Policy_Member__c> policyMemberList = new List<Policy_Member__c>();
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

            policyMemberList = [
                Select Id, Status__c, End_Date__c from Policy_Member__c 
                where 
                Policy__r.Source_Cust_Cov_Key__c like :groupIdSearchString AND 
                Product__c = :groupRecordDTO.product AND 
                Product_Type__c = :groupRecordDTO.productType AND 
                Product_type_Code__c = :groupRecordDTO.productTypeCode AND 
                Policy_Platform__c = :groupRecordDTO.platformCode AND 
                Member__r.Enterprise_Id__c = :personId AND 
                Member__r.ETL_Record_Deleted__c = false AND
                ETL_Record_Deleted__c = false
            ];
            if(policyMemberList.size() > 0) {
                for(Policy_Member__c policyMember : policyMemberList) {
                    if(policyMember.Status__c == 'Termed') {
                        Date endDate = Date.parse(policyMember.End_Date__c);
                        Date currDate = Date.today();
                        Integer monthDiff = endDate.monthsBetween(currDate);
                        Integer termedMonths = Integer.valueOf(mmGenericsMonths.Message__c);
                        if(monthDiff < termedMonths) {
                            statusSet.add(policyMember.Status__c);
                            shortlistedIds.add(policyMember.Id);
                        }
                    }
                    else {
                        statusSet.add(policyMember.Status__c);
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
                for(Policy_Member__c policyMember : policyMemberList) {
                    if(HUMUtilityHelper.isCRMFunctionalityON('1645972')) {
                        if(policyMember.Status__c.toLowerCase() == status.toLowerCase() && shortlistedIds.contains(policyMember.Id)) {
                            returnValue = policyMember.Id;
                            break;
                        }
                    }
                    else {
                        if(policyMember.Status__c == status) {
                            if(status == 'Termed') {
                                Date endDate = Date.parse(policyMember.End_Date__c);
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
        map<string, List<Policy_Member__c>> eligibleMap = new map<string, List<Policy_Member__c>>();
        List<Policy_Member__c> selfPolicyMemberList = new List<Policy_Member__c>();
        List<Policy_Member__c> depPolicyMemberList = new List<Policy_Member__c>();
        map<string, string> genericsMap = new map<string, string>();
        map<string, string> returnMap = new map<string, string>();
        boolean isDependentRequired = false;
        try {
            selfPolicyMemberList = procureSelfEligibleDetails(policyMemberId);
            if(selfPolicyMemberList.size() > 0) {
                eligibleMap.put('self', selfPolicyMemberList);
                for(Policy_Member__c policyMember : selfPolicyMemberList) {
                    isDependentRequired = (policyMember.relationship__c == 'SUBSCRIBER' || policyMember.relationship__c == 'EMPLOYEE') ? true : false;
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
        map<string, List<Policy_Member__c>> eligibleMap = new map<string, List<Policy_Member__c>>();
        List<Policy_Member__c> selfPolicyMemberList = new List<Policy_Member__c>();
        List<Policy_Member__c> selfPolicyMemberLis1 = new List<Policy_Member__c>();
        List<Policy_Member__c> depPolicyMemberList = new List<Policy_Member__c>();
        map<string, string> genericsMap = new map<string, string>();
        map<string, string> returnMap = new map<string, string>();
        boolean isDependentRequired = false;
        try {
            selfPolicyMemberList = procureSelfEligibleDetailsList(policyMemberIds);
            if(selfPolicyMemberList.size() > 0) {
                for(Policy_Member__c policy : selfPolicyMemberList) {
                    selfPolicyMemberLis1.add(policy);
                }
                eligibleMap.put('self', selfPolicyMemberLis1);
                for(Policy_Member__c policyMember : selfPolicyMemberList) {
                    isDependentRequired = (policyMember.relationship__c == 'SUBSCRIBER' || policyMember.relationship__c == 'EMPLOYEE') ? true : false;
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
    private static List<Policy_Member__c> procureSelfEligibleDetails(string policyMemberId) {
        List<Policy_Member__c> policyMemberList = new List<Policy_Member__c>();
        try {
            policyMemberList = [
                select id, name, member__c, member__r.Name, member__r.FirstName, member__r.LastName, member__r.birthdate__c, member__r.gender__c, 
                relationship__c, effective_date__c, end_date__c, status__c, ehb_term_date__c, member__r.Enterprise_Id__c,
                Policy__r.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c,
                Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Policy__r.Medicare_Segment_ID__c, Policy__r.Coverage_Plan_Effective_Date__c,
                member__r.salutation, member__r.suffix
                from Policy_Member__c 
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
    private static List<Policy_Member__c> procureSelfEligibleDetailsList(List<String> policyMemberIds) {
        List<Policy_Member__c> policyMemberList = new List<Policy_Member__c>();
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
                select id, name, member__c, member__r.Name, member__r.FirstName, member__r.LastName, member__r.birthdate__c, member__r.gender__c, 
                relationship__c, effective_date__c, end_date__c, status__c, ehb_term_date__c, member__r.Enterprise_Id__c,
                Policy__r.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c, 
                Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Policy__r.Medicare_Segment_ID__c, Policy__r.Coverage_Plan_Effective_Date__c,
                member__r.salutation, member__r.suffix
                from Policy_Member__c 
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
    private static List<Policy_Member__c> procureDepEligibleDetails(string subscriberId) {
        List<Policy_Member__c> policyMemberList = new List<Policy_Member__c>();
        try {
            policyMemberList = [
                select id, name, member__c,member__r.Name, member__r.FirstName, member__r.LastName, member__r.birthdate__c, member__r.gender__c, 
                relationship__c, effective_date__c, end_date__c, status__c, ehb_term_date__c, member__r.Enterprise_Id__c,
                Policy__r.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c, 
                member__r.salutation, member__r.suffix
                from Policy_Member__c 
                where 
                subscriber__c = :subscriberId
                and ETL_Record_Deleted__c = false
                and member__r.ETL_Record_Deleted__c = false
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
    private static List<Policy_Member__c> procureDepEligibleDetailsList(List<String> subscriberIds) {
        List<Policy_Member__c> policyMemberList = new List<Policy_Member__c>();
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
                select id, name, member__c,member__r.Name, member__r.FirstName, member__r.LastName, member__r.birthdate__c, member__r.gender__c, 
                relationship__c, effective_date__c, end_date__c, status__c, ehb_term_date__c, member__r.Enterprise_Id__c,
                Policy__r.Source_Cust_Cov_Key__c, Policy_Platform__c, Product__c, Product_Type__c, Product_type_Code__c, 
                member__r.salutation, member__r.suffix
                from Policy_Member__c 
                where 
                subscriber__c in :subscriberIds
                and ETL_Record_Deleted__c = false
                and member__r.ETL_Record_Deleted__c = false
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