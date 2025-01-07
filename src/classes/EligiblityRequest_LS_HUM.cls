/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligiblityRequest_LS_HUM 
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class Name  : EligiblityRequest_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                        06/09/2022                 Original Version
******************************************************************************************************************************************************************************/
public class EligiblityRequest_LS_HUM {
    public static string sClassName = 'EligiblityRequest_LS_HUM';
    public class DataException extends Exception {}
	private static Boolean bIsPolicyPlanSwithON = (CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch') != Null) ? CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch').IsON__c:true;
    
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
        List<MemberPlan> memberPlanList = new List<MemberPlan>();
        List<MemberPlan> subMemberPlanList = new List<MemberPlan>();
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
        Set<Id> memberPlanIds = new Set<Id>();

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
                
                GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
                personId = genericHelper.fetchPersonId(caseRecordId);
                
                blob key = Crypto.generateAesKey(256);
                string hexKey = EncodingUtil.convertToHex(key);
                String requestId = hexKey.SubString(0,8)+ '-' + hexKey.SubString(8,12) + '-' + hexKey.SubString(12,16) + '-' + hexKey.SubString(16,20) + '-' + hexKey.substring(20,32);

                memberPlanList = [
                    Select Name, GroupNumber, Plan.Source_Cust_Cov_Key__c,Policy__r.Source_Cust_Cov_Key__c, Product__c, Product_Type__c, Product_type_Code__c, Policy_Platform__c, 
                    ASO__c, Dual_Status_Indicator__c, Member_Coverage_Status__c, EffectiveTo, SubscriberPlanId__r.Id, SubscriberPlanId__r.Member.Enterprise_Id__c, Id from 
                    MemberPlan where 
                    Member.Enterprise_Id__c =: personId 
                    and Member.ETL_Record_Deleted__c = false
                    and ETL_Record_Deleted__c = false
                    and Policy_Platform__c in :applicablePlatformList
                    LIMIT 100
                ];

                if(memberPlanList.size() > 0) {
                    for(MemberPlan MemberPlanObj : memberPlanList) {
                        if(MemberPlanObj.Member_Coverage_Status__c == 'Termed') {
                            Date endDate = MemberPlanObj.EffectiveTo;
                            //Date endDate = Date.parse(MemberPlanObj.End_Date__c);
                            Date currDate = Date.today();
                            Integer monthDiff = endDate.monthsBetween(currDate);
                            Integer termedMonths = Integer.valueOf(mmGenericsMonths.Message__c);
                            if(monthDiff < termedMonths) {
                                statusSet.add(MemberPlanObj.Member_Coverage_Status__c);
                                memberPlanIds.add(MemberPlanObj.Id);
                            } 
                        }
                        else {
                            statusSet.add(MemberPlanObj.Member_Coverage_Status__c);
                            memberPlanIds.add(MemberPlanObj.Id);
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
                    for(MemberPlan memberPlanObj : memberPlanList) {
                        Boolean isShortlisted = deduceShortlisted(memberPlanObj.Id, memberPlanObj.Member_Coverage_Status__c, status, memberPlanIds);
                        String sSourceCustCovKey = '';
                        if(bIsPolicyPlanSwithON){
                            sSourceCustCovKey = (memberPlanObj.PlanId != Null) ? memberPlanObj.Plan.Source_Cust_Cov_Key__c : '';
                        }else {
                            sSourceCustCovKey = (memberPlanObj.Policy__c != Null) ? memberPlanObj.Policy__r.Source_Cust_Cov_Key__c : '';
                        }
                        if(String.isNotBlank(sSourceCustCovKey) && isShortlisted) {
                            if(sSourceCustCovKey.length() > 5 && (memberPlanObj.Policy_Platform__c == 'LV' || memberPlanObj.Policy_Platform__c == 'EM')) {
                                groupIdSet.add(sSourceCustCovKey.subString(0, 6));
                            }
                            else if(memberPlanObj.Policy_Platform__c == 'CB') {
                                if(sSourceCustCovKey.length() > 5) {
                                    groupIdSet.add(sSourceCustCovKey.subString(0, 6));
                                }
                                else {
                                    groupIdSet.add(sSourceCustCovKey);
                                }
                            }
                        }
                    }
                }
                if(groupIdSet.size() > 0) {
                    for(String groupId : groupIdSet) {
                        List<EligiblityRequest_DTO_HUM.Products> tempList = new List<EligiblityRequest_DTO_HUM.Products>();
                        for(MemberPlan memberPlanObj : memberPlanList) {
                            Boolean isShortlisted = deduceShortlisted(memberPlanObj.Id, memberPlanObj.Member_Coverage_Status__c, status, memberPlanIds);
                            EligiblityRequest_DTO_HUM.Products dtoProductsTemp = new EligiblityRequest_DTO_HUM.Products();
                           String sSourceCustCovKey = '';
                            if(bIsPolicyPlanSwithON){
                                sSourceCustCovKey = (memberPlanObj.PlanId != null)? memberPlanObj.Plan.Source_Cust_Cov_Key__c :'';
                            }else{
                                sSourceCustCovKey = (memberPlanObj.Policy__c != null)? memberPlanObj.Policy__r.Source_Cust_Cov_Key__c :'';
                            }
                            if(String.isNotBlank(sSourceCustCovKey) && sSourceCustCovKey.length() > 5 && (memberPlanObj.Policy_Platform__c == 'LV' || memberPlanObj.Policy_Platform__c == 'EM')) {
                                if(sSourceCustCovKey.subString(0, 6) == groupId && isShortlisted) {
                                    if(memberPlanObj.SubscriberPlanId__c!=null){
                                      string subPolMemEntId = memberPlanObj.SubscriberPlanId__r.Member.Enterprise_Id__c;
                                    if(subPolMemEntId != null && subPolMemEntId != '') {
                                        subEntIdDataMap.put(groupId, memberPlanObj.SubscriberPlanId__r.Member.Enterprise_Id__c);
                                    }
                                    }
                                    dtoProductsTemp.asoindicator = (memberPlanObj.ASO__c == 'N' || memberPlanObj.ASO__c == '' || memberPlanObj.ASO__c == null) ? 'false' : 'true';
                                    dtoProductsTemp.dualdemoindicator = (memberPlanObj.Dual_Status_Indicator__c == 'N' || memberPlanObj.Dual_Status_Indicator__c == '' || memberPlanObj.Dual_Status_Indicator__c == null) ? 'false' : 'true';
                                    dtoProductsTemp.product = memberPlanObj.Product__c;
                                    platFormValueMap.put(groupId, memberPlanObj.Policy_Platform__c);
                                    dtoProductsTemp.producttype = memberPlanObj.Product_Type__c;
                                    dtoProductsTemp.producttypecode = memberPlanObj.Product_type_Code__c;
                                    dtoProductsTemp.majorlob = '';
                                    tempList.add(dtoProductsTemp);
                                }
                            }
                            if(memberPlanObj.Policy_Platform__c == 'CB') {
                                 if(String.isNotBlank(sSourceCustCovKey) && sSourceCustCovKey.length() > 5) {
                                    if(sSourceCustCovKey.subString(0, 6) == groupId && isShortlisted) {
                                        string subPolMemEntId = memberPlanObj.SubscriberPlanId__r.Member.Enterprise_Id__c;
                                        if(subPolMemEntId != null && subPolMemEntId != '') {
                                            subEntIdDataMap.put(groupId, memberPlanObj.SubscriberPlanId__r.Member.Enterprise_Id__c);
                                        }
                                        dtoProductsTemp.asoindicator = (memberPlanObj.ASO__c == 'N' || memberPlanObj.ASO__c == '' || memberPlanObj.ASO__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.dualdemoindicator = (memberPlanObj.Dual_Status_Indicator__c == 'N' || memberPlanObj.Dual_Status_Indicator__c == '' || memberPlanObj.Dual_Status_Indicator__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.product = memberPlanObj.Product__c;
                                        platFormValueMap.put(groupId, memberPlanObj.Policy_Platform__c);
                                        dtoProductsTemp.producttype = memberPlanObj.Product_Type__c;
                                        dtoProductsTemp.producttypecode = memberPlanObj.Product_type_Code__c;
                                        dtoProductsTemp.majorlob = '';
                                        tempList.add(dtoProductsTemp);
                                    }
                                }
                                else {
                                    if(String.isNotBlank(sSourceCustCovKey) && sSourceCustCovKey == groupId && isShortlisted) {
                                        if(memberPlanObj.SubscriberPlanId__c!=null){
                                            string subPolMemEntId = memberPlanObj.SubscriberPlanId__r.Member.Enterprise_Id__c;
                                        if(subPolMemEntId != null && subPolMemEntId != '') {
                                            subEntIdDataMap.put(groupId, memberPlanObj.SubscriberPlanId__r.Member.Enterprise_Id__c);
                                        }
                                        }
                                        dtoProductsTemp.asoindicator = (memberPlanObj.ASO__c == 'N' || memberPlanObj.ASO__c == '' || memberPlanObj.ASO__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.dualdemoindicator = (memberPlanObj.Dual_Status_Indicator__c == 'N' || memberPlanObj.Dual_Status_Indicator__c == '' || memberPlanObj.Dual_Status_Indicator__c == null) ? 'false' : 'true';
                                        dtoProductsTemp.product = memberPlanObj.Product__c;
                                        platFormValueMap.put(groupId, memberPlanObj.Policy_Platform__c);
                                        dtoProductsTemp.producttype = memberPlanObj.Product_Type__c;
                                        dtoProductsTemp.producttypecode = memberPlanObj.Product_type_Code__c;
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
    private static boolean deduceShortlisted(string policyId, string policyStatus, string status, Set<Id> memberPlanIds) {
        Boolean isShortlisted = false;
        try {
                isShortlisted = memberPlanIds.contains(policyId);
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateHTTPRequestStructure');
        }
        return isShortlisted; 
    }

}