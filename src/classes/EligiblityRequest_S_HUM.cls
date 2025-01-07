/**
 * @File Name          : EligiblityRequest_S_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : ChangeMeIn@UserSettingsUnder.SFDoc
 * @Group              : Team Thunder
 * @Last Modified On   : 12-08-2020
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
 * 2.0    9/24/2020        Ajay Chakradhar         DEF-1707 fix 
**/
public with sharing class EligiblityRequest_S_HUM {
    public static string sClassName = 'EligiblityRequest_S_HUM';
    public class DataException extends Exception {}

    /*
    * Method Name: getValidateEligiblityRequest
    * Description: method is responsible to process template data insertion
    * Parameter: caseRecordId
    * Return Type: HTTPRequest
    */
    public static HTTPRequest getValidateEligiblityRequest(string caseRecordId, string platformValue) {
        HttpRequest request = new HttpRequest();
        String personId = '';
        string status = '';
        Map<String, String> platFormValueMap = new Map<String, String>();
        List<Policy_Member__c> policyMemberList = new List<Policy_Member__c>();
        List<Policy_Member__c> subPolMemberList = new List<Policy_Member__c>();
        Set<String> groupIdSet = new Set<String>();
        Set<String> subscriberIdSet = new Set<String>();
        Set<String> statusSet = new Set<String>();
        List<EligiblityRequest_DTO_HUM.Products> prodListTemp = new List<EligiblityRequest_DTO_HUM.Products>();
        Map<String, List<EligiblityRequest_DTO_HUM.Products>> eligibleProdMap = new Map<String, List<EligiblityRequest_DTO_HUM.Products>>();
        Map<String, String> subEntIdDataMap = new Map<String, String>();
        List<EligiblityRequest_DTO_HUM.Membercriterion> memberCriterionList = new List<EligiblityRequest_DTO_HUM.Membercriterion>();
        List<EligiblityRequest_DTO_HUM.Members> memberList = new List<EligiblityRequest_DTO_HUM.Members>();
        Member_Maintenance_Generics__c mmGenerics;
        Member_Maintenance_Generics__c mmGenericsMonths;
        List<String> applicablePlatformList = new List<String>();
        Set<Id> policyMemberIdList = new Set<Id>();

        try {
            request = generateHTTPRequestStructure(GLOBAL_CONSTANT_HUM.MM_VE_SER_NAME_HUM);
            mmGenerics = Member_Maintenance_Generics__c.getValues('Validate Eligibility');
            mmGenericsMonths = Member_Maintenance_Generics__c.getValues('Termed Months');
            if(String.isNotEmpty(mmGenerics.Applicable_Platform_List__c)) {
                applicablePlatformList = mmGenerics.Applicable_Platform_List__c.split(',');
            }
            if(applicablePlatformList.size() == 0) {
                throw new DataException(mmGenerics.Message__c);
            }
            if(request != null) {
                EligiblityRequest_DTO_HUM dtoRequest = new EligiblityRequest_DTO_HUM();
                EligiblityRequest_DTO_HUM.ValidateEligibilityRequest dtoValidateEligibilityRequest = new EligiblityRequest_DTO_HUM.ValidateEligibilityRequest();
                EligiblityRequest_DTO_HUM.Members dtoMembers = new EligiblityRequest_DTO_HUM.Members();
                EligiblityRequest_DTO_HUM.Membercriterion dtoMemberCriteria = new EligiblityRequest_DTO_HUM.Membercriterion();
                EligiblityRequest_DTO_HUM.Products dtoProducts = new EligiblityRequest_DTO_HUM.Products();

                GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
                personId = genericHelper.fetchPersonId(caseRecordId);
                
                blob key = Crypto.generateAesKey(256);
                string hexKey = EncodingUtil.convertToHex(key);
                String requestId = hexKey.SubString(0,8)+ '-' + hexKey.SubString(8,12) + '-' + hexKey.SubString(12,16) + '-' + hexKey.SubString(16,20) + '-' + hexKey.substring(20,32);

                policyMemberList = [
                    Select 
                    Name, Group_Number__c, Policy__r.Source_Cust_Cov_Key__c, Product__c, Product_Type__c, Product_type_Code__c, Policy_Platform__c, 
                    ASO__c, Dual_Status_Indicator__c, Status__c, End_Date__c, subscriber__r.Id, subscriber__r.Member__r.Enterprise_Id__c, Id
                    from 
                    Policy_Member__c where 
                    Member__r.Enterprise_Id__c = :personId 
                    and Member__r.ETL_Record_Deleted__c = false
                    and ETL_Record_Deleted__c = false
                    and Policy_Platform__c in :applicablePlatformList
                    LIMIT 100
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
                                policyMemberIdList.add(policyMember.Id);
                            }
                        }
                        else {
                            statusSet.add(policyMember.Status__c);
                            policyMemberIdList.add(policyMember.Id);
                        }
                    }
                    
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
                        Boolean isShortlisted = deduceShortlisted(policyMember.Id, policyMember.Status__c, status, policyMemberIdList);
                        if(policyMember.Policy__r.Source_Cust_Cov_Key__c != null && policyMember.Policy__r.Source_Cust_Cov_Key__c != '' && isShortlisted) {
                            if(policyMember.Policy__r.Source_Cust_Cov_Key__c.length() > 5 && (policyMember.Policy_Platform__c == 'LV' || policyMember.Policy_Platform__c == 'EM')) {
                                groupIdSet.add(policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(0, 6));
                            }
                            else if(policyMember.Policy_Platform__c == 'CB') {
                                if(policyMember.Policy__r.Source_Cust_Cov_Key__c.length() > 5) {
                                    groupIdSet.add(policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(0, 6));
                                }
                                else {
                                    groupIdSet.add(policyMember.Policy__r.Source_Cust_Cov_Key__c);
                                }
                            }
                        }
                    }
                }

                if(groupIdSet.size() > 0) {
                    for(String groupId : groupIdSet) {
                        List<EligiblityRequest_DTO_HUM.Products> tempList = new List<EligiblityRequest_DTO_HUM.Products>();
                        for(Policy_Member__c policyMember : policyMemberList) {
                            Boolean isShortlisted = deduceShortlisted(policyMember.Id, policyMember.Status__c, status, policyMemberIdList);
                            EligiblityRequest_DTO_HUM.Products dtoProductsTemp = new EligiblityRequest_DTO_HUM.Products();
                            if(policyMember.Policy__r.Source_Cust_Cov_Key__c.length() > 5 && (policyMember.Policy_Platform__c == 'LV' || policyMember.Policy_Platform__c == 'EM')) {
                                if(policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(0, 6) == groupId && isShortlisted) {
                                    string subPolMemEntId = policyMember.Subscriber__r.Member__r.Enterprise_Id__c;
                                    if(subPolMemEntId != null && subPolMemEntId != '') {
                                        subEntIdDataMap.put(groupId, policyMember.Subscriber__r.Member__r.Enterprise_Id__c);
                                    }
                                    dtoProductsTemp.asoindicator = (policyMember.ASO__c == 'N' || policyMember.ASO__c == '' || policyMember.ASO__c == null) ? 'false' : 'true';
                                    dtoProductsTemp.dualdemoindicator = (policyMember.Dual_Status_Indicator__c == 'N' || policyMember.Dual_Status_Indicator__c == '' || policyMember.Dual_Status_Indicator__c == null) ? 'false' : 'true';
                                    dtoProductsTemp.product = policyMember.Product__c;
                                    platFormValueMap.put(groupId, policyMember.Policy_Platform__c);
                                    dtoProductsTemp.producttype = policyMember.Product_Type__c;
                                    dtoProductsTemp.producttypecode = policyMember.Product_type_Code__c;
                                    dtoProductsTemp.majorlob = '';
                                    tempList.add(dtoProductsTemp);
                                }
                            }
                            if(policyMember.Policy_Platform__c == 'CB') {
                                if(policyMember.Policy__r.Source_Cust_Cov_Key__c.length() > 5) {
                                    if(policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(0, 6) == groupId && isShortlisted) {
                                        string subPolMemEntId = policyMember.Subscriber__r.Member__r.Enterprise_Id__c;
                                        if(subPolMemEntId != null && subPolMemEntId != '') {
                                            subEntIdDataMap.put(groupId, policyMember.Subscriber__r.Member__r.Enterprise_Id__c);
                                        }
                                        dtoProductsTemp.asoindicator = (policyMember.ASO__c == 'N' || policyMember.ASO__c == '' || policyMember.ASO__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.dualdemoindicator = (policyMember.Dual_Status_Indicator__c == 'N' || policyMember.Dual_Status_Indicator__c == '' || policyMember.Dual_Status_Indicator__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.product = policyMember.Product__c;
                                        platFormValueMap.put(groupId, policyMember.Policy_Platform__c);
                                        dtoProductsTemp.producttype = policyMember.Product_Type__c;
                                        dtoProductsTemp.producttypecode = policyMember.Product_type_Code__c;
                                        dtoProductsTemp.majorlob = '';
                                        tempList.add(dtoProductsTemp);
                                    }
                                }
                                else {
                                    if(policyMember.Policy__r.Source_Cust_Cov_Key__c == groupId && isShortlisted) {
                                        string subPolMemEntId = policyMember.Subscriber__r.Member__r.Enterprise_Id__c;
                                        if(subPolMemEntId != null && subPolMemEntId != '') {
                                            subEntIdDataMap.put(groupId, policyMember.Subscriber__r.Member__r.Enterprise_Id__c);
                                        }
                                        dtoProductsTemp.asoindicator = (policyMember.ASO__c == 'N' || policyMember.ASO__c == '' || policyMember.ASO__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.dualdemoindicator = (policyMember.Dual_Status_Indicator__c == 'N' || policyMember.Dual_Status_Indicator__c == '' || policyMember.Dual_Status_Indicator__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.product = policyMember.Product__c;
                                        platFormValueMap.put(groupId, policyMember.Policy_Platform__c);
                                        dtoProductsTemp.producttype = policyMember.Product_Type__c;
                                        dtoProductsTemp.producttypecode = policyMember.Product_type_Code__c;
                                        dtoProductsTemp.majorlob = '';
                                        tempList.add(dtoProductsTemp);
                                    }
                                }
                            }
                        }
                        if(tempList.size() > 0) {
                            eligibleProdMap.put(groupId, tempList);
                        }
                    }   
                }

                DateTime dDateTime = DateTime.now();
                string formattedDateTime = dDateTime.formatGmt('yyyy-MM-dd\'T\'HH:mm:ss.SSSSSS');
                dtoValidateEligibilityRequest.timestamp = formattedDateTime;
                dtoValidateEligibilityRequest.consumer = GLOBAL_CONSTANT_HUM.MM_VE_SER_CONSUMER_NAME;
                dtoValidateEligibilityRequest.requestid = requestId;

                if(groupIdSet.size() > 0) {
                    for(String groupIdKey : groupIdSet) {
                        EligiblityRequest_DTO_HUM.Membercriterion dtoMemberCriteriaTemp = new EligiblityRequest_DTO_HUM.Membercriterion();
                        dtoMemberCriteriaTemp.source = '';
                        dtoMemberCriteriaTemp.groupid = groupIdKey;
                        dtoMemberCriteriaTemp.platform = platFormValueMap.get(groupIdKey);
                        dtoMemberCriteriaTemp.membersourcepersonid = personId;
                        dtoMemberCriteriaTemp.subscribersourcepersonid = (subEntIdDataMap.size() > 0) ? ((subEntIdDataMap.get(groupIdKey) == null) ? personId : subEntIdDataMap.get(groupIdKey)) : personId;
                        dtoMemberCriteriaTemp.products = (eligibleProdMap.size() > 0) ? eligibleProdMap.get(groupIdKey) : null;
                        memberCriterionList.add(dtoMemberCriteriaTemp);
                        // break;
                    }
                }

                dtoMembers.membercriterion = memberCriterionList;
                memberList.add(dtoMembers);
                dtoValidateEligibilityRequest.members = memberList;
                dtoRequest.ValidateEligibilityRequest = dtoValidateEligibilityRequest;

                if(memberCriterionList.size() == 0) {
                    return null;
                }

                String requestBody = JSON.serialize(dtoRequest);
                request.setBody(requestBody);
            }
            
            return request;
            }
        
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getValidateEligiblityRequest');
            return null;
        }
    }

    /*
    * Method Name: generateHTTPRequestStructure
    * Description: method is responsible to generate httpstructure
    * Parameter: serviceName
    * Return Type: HTTPRequest
    */
    private static HTTPRequest generateHTTPRequestStructure(string serviceName) {
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c objVEService;
        try {
            objVEService = HUM_Webservice_Callout__c.getValues(serviceName);
            if(objVEService != null) {
                request.setMethod(objVEService.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(objVEService.Certificate_Name__c))
                {
                    request.setClientCertificateName(objVEService.Certificate_Name__c);
                }
                if (String.isNotBlank(objVEService.Content_Type__c))
                {
                    request.setHeader(GLOBAL_CONSTANT_HUM.MM_VE_SER_CONTENTTYPE_HUM, objVEService.Content_Type__c);
                }
                request.setHeader(GLOBAL_CONSTANT_HUM.MM_VE_SER_CONECTION_HUM, GLOBAL_CONSTANT_HUM.MM_VE_SER_KEEPALIVE_HUM);
                request.setEndpoint(objVEService.End_Point_URL__c + objVEService.Service_Name__c);
                request.setHeader(GLOBAL_CONSTANT_HUM.MM_VE_SER_SOAPACTION_HUM, '');
                if(objVEService.Timeout__c != null) {
                    request.setTimeout(Integer.valueOf(objVEService.Timeout__c));
                }
                 
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateHTTPRequestStructure');
            return null;
        }
    }

    /*
    * Method Name: deduceShortlisted
    * Description: method is responsible to deduce whether policy member record is shortlited for Eligiblity
    * Parameter: policy member id, policy member status
    * Return Type: boolean
    */
    private static boolean deduceShortlisted(string policyId, string policyStatus, string status, Set<Id> policyMemberIdList) {
        Boolean isShortlisted = false;
        try {
                isShortlisted = policyMemberIdList.contains(policyId);
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateHTTPRequestStructure');
        }
        return isShortlisted;
    }
}