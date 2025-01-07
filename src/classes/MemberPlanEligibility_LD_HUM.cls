/**************************************************************************************************************************************************
Apex class Name    :  MemberPlanEligibility_LD_HUM
Version            :  1.0
Function           :  Data Layer for MemberPlanEligibility_LC_HUM
Test Class 		   : MemberPlanEligibility_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev/Ankima                                    06/09/2021                  initial version
* Ankima                                                 07/01/2021                  Method for Cost Share Protected Field added
* Ankima                                                 07/26/2021                  Method for Cost Share Protected Field Rollback
* Ankima                                                 09/01/2021                  Method for Cost Share Protected Field added as per new requirements
* Ankima                                                 09/13/2021                  Method for Cost Share Protected Field Rollback
* Ankima                                                 09/20/2021                  Method for Cost Share Protected Field Re-Deploy
* Ankima                                                 09/30/2021                  Changes related to Eligibility By Product Type
* Ankima                                                 10/07/2021                  Updates related to Eligibility By Product Type
* Ashish Kumar                                           01/10/2021                  Changing constants reference
************************************************************************************************************************************************* */
public with sharing class MemberPlanEligibility_LD_HUM {

    /*
    * This method is used to retrieve member plan records based on memberplan id 
    * @param  String - MemberPlan Id
    * @return List<MemberPlan> 
    */
    public static List<MemberPlan> getMemberPlanRecords(string memberPlanId)
    {
        Set<String> setValidPlatforms = new Set<String>(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.POLICY_PLATFORM_INFO.split(';'));
        return [Select Id, Member_Id_Base__c From MemberPlan Where Id =: memberPlanId AND Plan.Platform__c IN : setValidPlatforms LIMIT 1];
        
    }
    
    /*
    * This method is used to retrieve source key of MemberPlan record
    * @param  String - MemberPlan Id
    * @return Object 
    */
    public static MemberPlan getMemberPlan(string memberPlanId)
    {
        return [Select MemberId, Plan.Source_Cust_Cov_Key__c, Plan.Contract_Number__c, Plan.PBP_Code__c, Plan.Medicare_Segment_ID__c From MemberPlan where Id =: memberPlanId LIMIT 1];
        
    }
    
    /*
    * This method is used to retrieve HUM_Field_Priority__c records
    * @param  String - 
    * @return String - List<String>
    */
    public static List<String> getEligibityFields()
    {
        List<String> lstFieldNames = new List<String>();
        List<HUM_Field_Priority__c> lstFieldPriority = [SELECT Name,Priority__c FROM HUM_Field_Priority__c ORDER By Priority__c ASC NULLS Last];
        for(HUM_Field_Priority__c fieldPriorityInstance : lstFieldPriority)
            {
                lstFieldNames.add(fieldPriorityInstance.Name);
            }
        return lstFieldNames;
        
    }
    
    /*
    * This method is used to retrieve HUM_Section_Priority__c records
    * @param  String - 
    * @return String - List<String>
    */
    public static List<String> getEligibitySections()
    {
        List<String> lstSectionName = new List<String>();
        List<HUM_Section_Priority__c> lstCustomPriority = [SELECT Name,Priority__c FROM HUM_Section_Priority__c ORDER By Priority__c ASC NULLS Last];
        for(HUM_Section_Priority__c sectionPriorityInstance : lstCustomPriority)
        {
            lstSectionName.add(sectionPriorityInstance.Name);
        }
        return lstSectionName;
        
    }
    
    /*
    * This method is used to retrieve Account record
    * @param  String - 
    * @return String - List<String>
    */
    public static Account getAccountRecord(string accountId)
    {
        return [Select Id, Enterprise_Id__c, Birthdate__c from Account Where Id =: accountId Limit 1];
        
    }
    /*
    *This method is used to fetch Network Value for logged in user
	* @param - null
	* @return - User Object
	*/
    public static User userRec(){
        return [Select Network_User_Id__c from User where Id =: UserInfo.getUserId()];
    }
    /*This method is used to decide whether cost share value is to be shown or not
	* @param - Member Plan Id
	* @return - Boolean
	*/
    public static Boolean showCostShare(string memberPlanId){
        Boolean showCostShareFld=false;
        String productTypeCode =  [Select Product_type_Code__c from MemberPlan where Id =: memberPlanId].Product_type_Code__c;
        if(productTypeCode==GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MA || productTypeCode==GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MAPD){
        	showCostShareFld=true;    
        }
        return showCostShareFld; 
    }
     /*This method is used to decide whether Eligibility sections are to be shown or not
	* @param - Member Plan Id
	* @return - Boolean
	*/
    public static Boolean showEligibSections(string memberPlanId){ 
        List<String> lstPTC = new List<String>{GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MER,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGR,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MEP,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGP,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MEF,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MRO,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_PDP,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MPD,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MES,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGS,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MCD};
        Boolean showEligibleSections=false;
        String productTypeElig =  [Select Product_Type__c from MemberPlan where Id =: memberPlanId].Product_Type__c;
        if(lstPTC.Contains(productTypeElig)){
        	showEligibleSections=true;    
        }
        return showEligibleSections; 
    }
     /*This method is used to decide whether Contract information section fields are to be shown or not
	* @param - Member Plan Id
	* @return - Boolean
	*/
   
    /*public static List<String> showContractInfoSection(string memberPlanId,Boolean isDualPlan){ 
    List<String> contractFields = new List<String>();
    if(isDualPlan){
        String productType = [Select Product_Type__c from MemberPlan where Id =: memberPlanId].Product_Type__c;
        if(productType == GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MES || productType == GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MCD){
        	contractFields.add(System.Label.HUMSegmentCode);		    
        }
        if(productType == GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MES ){
            contractFields.add(System.Label.HUMContractId);
            contractFields.add(System.Label.pbpCodeHum);
        }
    }
    return contractFields; 
    }*/
    public static List<String> showContractInfoSection(string memberPlanId){  
    List<String> contractFields = new List<String>();
    List<String> MedicarePT = new List<String>{GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MER,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGR,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MEP,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGP,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MEF,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MRO,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_PDP,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MPD,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MES,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGS}; 
                                        
    List<String> MedicaidPT = new List<String>{GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MCD};
                                        
    List<String> DualPlanPT = new List<String>{GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MER,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGR,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MEP,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGP,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MEF,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MRO,
            						GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MPD,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MES,
       								GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGS,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGF,
                                    GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGO,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MRP,
                                    GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MSL,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MSP,
                                    GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_RSK,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_SUP};
    	String productType = [Select Product_Type__c from MemberPlan where Id =: memberPlanId].Product_Type__c;
        if(MedicarePT.Contains(productType)){
        	contractFields.add(System.Label.HUMSegmentCode);
            contractFields.add(System.Label.HUMContractId);
            contractFields.add(System.Label.pbpCodeHum);
        }
         if(MedicaidPT.Contains(productType)){ 
            contractFields.add(System.Label.HUMSegmentCode);
        }
        if(contractFields.size()==0 && DualPlanPT.Contains(productType)){
        	contractFields.add('checkDualPlan');    
        }
        return contractFields; 
        
    }
   
}