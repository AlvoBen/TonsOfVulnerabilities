/*****************************************************************************************************************************************************************************
    @Apex Class Name  : HighestEligibleHeirarchy_LH_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Claa        : EligibilityResponse_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                 		  06/15/2022                 Original Version
******************************************************************************************************************************************************************************/
public class HighestEligibleHeirarchy_LH_HUM {
	public static string sClassName = 'HighestEligibleHeirarchy_LH_HUM';
    string templateName; 
    EligiblityResponse_DTO_HUM responseDTO;
    List<EligiblityResponse_DTO_HUM.Members> membersList;
    List<EligiblityResponse_DTO_HUM.Eligibilities> memberEligibleList = new List<EligiblityResponse_DTO_HUM.Eligibilities>();
    List<Integer> heirarchyList = new List<Integer>();
    List<Integer> selHeirList = new List<Integer>();
    Set<String> platformList = new Set<String>();
    Integer highestHierarchy;
    List<EligiblityResponse_DTO_HUM.Eligibilities> highestEligibility = new List<EligiblityResponse_DTO_HUM.Eligibilities>();
    List<EligibilePlatProdModel> eligilbityPlatProdModelList = new List<EligibilePlatProdModel>();
    Map<String, Object> returnMap = new Map<String, Object>();
    List<MemberPlan> memberPlanList = new List<MemberPlan>();
    Member_Maintenance_Generics__c mmGenerics;
    List<MemberPlan> shortlistedMemberList = new List<MemberPlan>();
    Map<String, MemberPlan> selMemListMap = new Map<String, MemberPlan>();
    List<String> applicablePlatformList;
    string personId;
    Map<Integer, EligiblityResponse_DTO_HUM.Eligibilities>  heirEligibilityMap = new Map<Integer, EligiblityResponse_DTO_HUM.Eligibilities>();
    Map<Integer, MemberPlan>  heirPolicyMemMap = new Map<Integer, MemberPlan>();
    private static Boolean bIsPolicyPlanSwithON = (CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch') != Null) ? CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch').IsON__c:true;

    /**
    * @description this class will be used to raise exception when there is logic or data issues
    */
    public class DataException extends Exception {}

    /**
    * @description this class will be used as template for Eligibility Product Model
    */
    private class EligibilePlatProdModel {
        public string platform { get; set; }
        public EligiblityResponse_DTO_HUM.GroupRecord product { get; set; }
        public string policyMemberId { get; set; }
        public Boolean primary { get; set; }
    }

    /**
    * @description this method is used to retrieve the highest eligible product
    * @param EligiblityResponse_DTO_HUM, String, String
    */
    public HighestEligibleHeirarchy_LH_HUM(EligiblityResponse_DTO_HUM responseDTO, string templateName, string personId)
    {
        this.templateName = templateName;
        this.responseDTO = responseDTO;
        this.personId = personId;
    }

    /**
    * @description this method is used to retrieve the highest eligible product
    * @param none
    * @return Map<String, String>
    */
    public Map<String, Object> deduceHighestHeirarchy()
    {
        Map<String, Object> returnMap = new Map<String, Object>();
        try {
            //1. procure initial values
            this.procureIntitialValues();

            //2. validate Initial Values
            this.validateInitialValues();

            //3. deduce the Active vs Future vs Termed List
            this.procureActiveFutureTermed();

            //4. Set the shortlisted Policy Member Id
            if(this.heirPolicyMemMap.size() > 0) {
                returnMap.put('shortlistedPolicyMemberId', this.heirPolicyMemMap.get(this.highestHierarchy).Id);
            }

            //5. Set the Eligibility Data Model and Platform List
            if(this.highestEligibility.size() > 0) {
                returnMap.put('eligibilityDataModel', this.highestEligibility);
                returnMap.put('eligiblePlatformList', this.platformList);
            }
            
            //6. Set the Eligibile Platform Product Model
            if(this.eligilbityPlatProdModelList.size() > 0) {
                returnMap.put('eligiblePlatProdModel', this.eligilbityPlatProdModelList);
            }
        } 
        catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'deduceHighestHeirarchy');
        }

        return returnMap;
    }

    /**
    * @description this method is used to retrieve the intitial values
    * @param none
    * @return none
    */
    private void procureIntitialValues()
    {
        //1. Get the members from the Validate Eligibility Response
        this.membersList = this.responseDTO.ValidateEligibilityResponse.members;

        //2. Procure the Validate Eligibility Custom Settings
        this.mmGenerics = Member_Maintenance_Generics__c.getValues('Validate Eligibility');
        if(String.isNotEmpty(this.mmGenerics.Applicable_Platform_List__c)) {
            this.applicablePlatformList = this.mmGenerics.Applicable_Platform_List__c.split(',');
            if(this.applicablePlatformList.size() == 0) {
                throw new DataException(this.mmGenerics.Message__c);
            }
        }

        //3. Procure the Member Eligible List from Member List from Step 1
        if(this.membersList.size() > 0) {
            for(EligiblityResponse_DTO_HUM.Members member : this.membersList) {
                this.memberEligibleList = member.eligibilities;
                break;
            }
        }

        //4. Procure Eligibile Platform Products from Eligible Member List from Step 3
        if(this.memberEligibleList.size() > 0) {
            for(EligiblityResponse_DTO_HUM.Eligibilities eligibility : this.memberEligibleList) {
                if(eligibility.template == this.templateName) {
                    this.heirarchyList.add(Integer.valueof(eligibility.heirarchy));
                }
                this.platformList.add(eligibility.groupRecord.platformCode);
                EligibilePlatProdModel platProdModelTemp = new EligibilePlatProdModel();
                platProdModelTemp.platform = eligibility.groupRecord.platformCode;
                platProdModelTemp.policyMemberId = null;
                platProdModelTemp.product = (EligiblityResponse_DTO_HUM.GroupRecord)eligibility.groupRecord;
                platProdModelTemp.primary = false;
                this.eligilbityPlatProdModelList.add(platProdModelTemp);
            }
        }

    }

    /**
    * @description this method is used to validate the initial values
    * @param none
    * @return none
    */
    private void validateInitialValues() 
    {
        if(this.membersList.isEmpty()) {
            throw new DataException(Label.VE_MemberListEmpty);
        }

        if(this.memberEligibleList.isEmpty()) {
            throw new DataException(Label.VE_EligibleMemberListEmpty);
        }
    }
    
    /**
    * @description this method is used to procure Active vs Future vs Termed
    * @param none
    * @return none
    */
    private void procureActiveFutureTermed()
    {
        //1. Fetch all the shortlisted policy member records that were sent in the request
        this.fetchAllPolicyMembers();

        //2. Construct heirarchy Eligibility and heirarchy Policy Member Map
        if(this.shortlistedMemberList.size() > 0) {
            this.constructHeirarchyMap();
        }
    }

    /**
    * @description this method is used to Fetch all the shortlisted policy member records that were sent in the request
    * @param none
    * @return none
    */
    private void fetchAllPolicyMembers()
    {
        Member_Maintenance_Generics__c mmGenericsMonths = Member_Maintenance_Generics__c.getValues('Termed Months');
        if (Schema.sObjectType.Policy_Member__c.fields.Name.isAccessible()) {
            this.memberPlanList = [
                Select  Name, GroupNumber, Plan.Source_Cust_Cov_Key__c,Policy__r.Source_Cust_Cov_Key__c, Product__c, Product_Type__c, Product_type_Code__c, Policy_Platform__c, 
                ASO__c, Dual_Status_Indicator__c, Member_Coverage_Status__c, EffectiveTo, SubscriberPlanId__r.Id, SubscriberPlanId__r.Member.Enterprise_Id__c, Id
                from 
                MemberPlan where 
                Member.Enterprise_Id__c = :this.personId 
                and Member.ETL_Record_Deleted__c = false
                and ETL_Record_Deleted__c = false
                and Policy_Platform__c in :this.applicablePlatformList
                LIMIT 100
            ];
        }

        if(this.memberPlanList.size() > 0) {
            for(MemberPlan memberPlanObj : this.memberPlanList) {
                if(memberPlanObj.Member_Coverage_Status__c == 'Termed') {
                    Date endDate = memberPlanObj.EffectiveTo;
                    Date currDate = Date.today();
                    Integer monthDiff = endDate.monthsBetween(currDate);
                    Integer termedMonths = Integer.valueOf(mmGenericsMonths.Message__c);
                    if(monthDiff < termedMonths) {
                        this.selMemListMap.put(memberPlanObj.Member_Coverage_Status__c + '_' + memberPlanObj.Id, memberPlanObj);
                        this.shortlistedMemberList.add(memberPlanObj);
                    }
                }
                else {
                    this.selMemListMap.put(memberPlanObj.Member_Coverage_Status__c + '_' + memberPlanObj.Id, memberPlanObj);
                    this.shortlistedMemberList.add(memberPlanObj);
                }
            }
        }
    }

    /**
    * @description this method is used to construct heirarchy maps
    * @param none
    * @return none
    */
    private void constructHeirarchyMap()
    {
        //1. Construct Heirarchy List, Heirarchy Eligibility and Heirarchy Policy Member Map
        this.constructMap();

        //2. Construct Eligilble Platform Product Model List
        this.constructModel();
    }

    /**
    * @description this method is used to Construct Heirarchy List, Heirarchy Eligibility and Heirarchy Policy Member Map
    * @param none
    * @return none
    */
    private void constructMap()
    {
        List<MemberPlan> activeList = new List<MemberPlan>();
        List<MemberPlan> futureList = new List<MemberPlan>();
        List<MemberPlan> termedList = new List<MemberPlan>();
        List<MemberPlan> resultList = new List<MemberPlan>();
        
        List<EligiblityResponse_DTO_HUM.Eligibilities> activeEList = new List<EligiblityResponse_DTO_HUM.Eligibilities>();
        List<EligiblityResponse_DTO_HUM.Eligibilities> futureEList = new List<EligiblityResponse_DTO_HUM.Eligibilities>();
        List<EligiblityResponse_DTO_HUM.Eligibilities> termedEList = new List<EligiblityResponse_DTO_HUM.Eligibilities>();
        List<EligiblityResponse_DTO_HUM.Eligibilities> resultEList = new List<EligiblityResponse_DTO_HUM.Eligibilities>();
        
        for(MemberPlan policyMem : this.shortlistedMemberList) {
            String sSourceCustCovKey = '';
            if(bIsPolicyPlanSwithON){
                sSourceCustCovKey = (policyMem.PlanId != null) ? policyMem.Plan.Source_Cust_Cov_Key__c : '';
            }else {
                sSourceCustCovKey = (policyMem.Policy__c != null) ? policyMem.Policy__r.Source_Cust_Cov_Key__c : '';
            }
            for(EligiblityResponse_DTO_HUM.Eligibilities eligibility : this.memberEligibleList) {
                eligibility.groupRecord.productTypeCode = (eligibility.groupRecord.productTypeCode == '') ? 
                    null : eligibility.groupRecord.productTypeCode;
                eligibility.groupRecord.productType = (eligibility.groupRecord.productType == '') ? 
                    null : eligibility.groupRecord.productType;
               
                if(eligibility.template == this.templateName && 
                    String.isNotBlank(sSourceCustCovKey) &&
                    sSourceCustCovKey.startsWith(eligibility.groupRecord.groupId) && 
                    policyMem.Product__c == eligibility.groupRecord.product && 
                    policyMem.Product_Type__c == eligibility.groupRecord.productType && 
                    policymem.Product_type_Code__c == eligibility.groupRecord.productTypeCode && 
                    policyMem.Policy_Platform__c == eligibility.groupRecord.platformCode) {
                        if(policyMem.Member_Coverage_Status__c == 'Active') {
                            activeList.add(policyMem);
                            activeEList.add(eligibility);
                        }
                        if(policyMem.Member_Coverage_Status__c == 'Future') {
                            futureList.add(policyMem);
                            futureEList.add(eligibility);
                        }
                        if(policyMem.Member_Coverage_Status__c == 'Termed') {
                            termedList.add(policyMem);
                            termedEList.add(eligibility);
                        }
                }
            }
        }
        
        resultList = (activeList.size() > 0) ? activeList : (futureList.size() > 0) ? futureList : (termedList.size() > 0) ? termedList : resultList;
        resultEList = (activeList.size() > 0) ? activeEList : (futureList.size() > 0) ? futureEList : (termedList.size() > 0) ? termedEList : resultEList;
        
        for(MemberPlan policyMem : resultList) {
            String sSourceCustCovKey = '';
            if(bIsPolicyPlanSwithON){
                sSourceCustCovKey = (policyMem.PlanId != null) ? policyMem.Plan.Source_Cust_Cov_Key__c : '';
            }else {
                sSourceCustCovKey = (policyMem.Policy__c != null) ? policyMem.Policy__r.Source_Cust_Cov_Key__c : '';
            }
            for(EligiblityResponse_DTO_HUM.Eligibilities eligibility : resultEList) {
                if(eligibility.template == this.templateName && 
                    String.isNotBlank(sSourceCustCovKey) &&
                    sSourceCustCovKey.startsWith(eligibility.groupRecord.groupId) && 
                    policyMem.Product__c == eligibility.groupRecord.product && 
                    policyMem.Product_Type__c == eligibility.groupRecord.productType && 
                    policymem.Product_type_Code__c == eligibility.groupRecord.productTypeCode && 
                    policyMem.Policy_Platform__c == eligibility.groupRecord.platformCode) {
                    
                        this.selHeirList.add(Integer.valueof(eligibility.heirarchy));
                        this.heirEligibilityMap.put(Integer.valueof(eligibility.heirarchy), eligibility);
                        this.heirPolicyMemMap.put(Integer.valueOf(eligibility.heirarchy), policyMem);
                }
            }
        }
    }

    /**
    * @description this method is used to Construct Eligilble Platform Product Model List
    * @param none
    * @return none
    */
    private void constructModel()
    {
        if(this.selHeirList.size() > 0) {
            this.selHeirList.sort();
            this.highestHierarchy = this.selHeirList.get(0);
            this.highestEligibility.add(this.heirEligibilityMap.get(this.highestHierarchy));
            if(this.eligilbityPlatProdModelList.size() > 0) {
                for(EligibilePlatProdModel platformProduct : this.eligilbityPlatProdModelList) {
                    MemberPlan policyMem = this.heirPolicyMemMap.get(this.highestHierarchy);
                    if(platformProduct.platform == policyMem.Policy_Platform__c) {
                        platformProduct.primary = true;
                        break;
                    }
                }
            }
        }
    }

}