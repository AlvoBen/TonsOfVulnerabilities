/*****************************************************************************************
Apex class Name    : ClaimsSummary_LD_HUM
Version            : 1.0
Function           : DataLayer Class for the Claims Summary
Created Date       : 06/10/2022
Test Class         : 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Suraj Patil                                      	 06/10/2022         Original Version 
 *   Anuradha Gajbhe                                     11/02/2022         US#3786555 - Claims System Integration: Claims: Medical Claims: Dental Claims: Security Home Office Differentiation
*********************************************************************************************************************************/

public with sharing class ClaimsSummary_LD_HUM
{
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
   /*
    *  This method  is used to return details of policy member
    *  @name   getPolicyMemberDetails
    *  @param  ID - Record ID of Policy Member for which claims are to be displayed
    *  @return List<MemberPlan> -  List of Policy member objects
    */
    @AuraEnabled
    public static List<MemberPlan> getPolicyMemberDetails(ID sRecId)    {
        List<MemberPlan> lstpolmem = new List<MemberPlan>();
        try
        { 
            lstpolmem = [SELECT Id, Name, Plan.Client_Number__c, Member.FirstName,Product__c, Member.Enterprise_Id__c, MemberId, Relationship_Code__c, Member.name,ETL_Record_Deleted__c FROM MemberPlan WHERE ID=:String.escapeSingleQuotes(sRecId)];
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_LD_HUM', 'getMemberName');
        }
        return lstpolmem;    
    }

    /*
    *  This method  is used to return details of memberPlan
    *  @name   getPolicyMemberDetail
    *  @param  ID - Record ID of Policy Member for which claims are to be displayed
    *  @return List<MemberPlan> -  List of Policy member objects
    */
    @AuraEnabled
    public static List<MemberPlan> getPolicyMemberDetail(string sName)    {
        List<MemberPlan> lstpolmem1 = new List<MemberPlan>();
        try
        { 
            lstpolmem1 = [SELECT Id, MemberId, Member.name,ETL_Record_Deleted__c, Member_Coverage_Status__c FROM MemberPlan WHERE (Name=:String.escapeSingleQuotes(sName) AND Member_Coverage_Status__c = 'Active')];
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_LD_HUM', 'getMemberID');
        }
        return lstpolmem1;    
    }
    
   /*
    *  This method  is used to return Member Id details for a policy member
    *  @name   getMemberIDObjDetails
    *  @param  ID - Record ID of Policy Member which has related Member_ID_c records
    *  @return list<MemberPlan> - List of Member_ID_c objects related to given policy member
    *  @throws getMemberIDObjDetails
    */   
    @AuraEnabled
     public static list<MemberPlan> getMemberIDObjDetails(ID sRecId)
     { 
         list<MemberPlan> lstmemid = new list<MemberPlan>();
        try
        {            
           lstmemid = [SELECT Id,Name, subscriberid FROM MemberPlan WHERE MemberId=:String.escapeSingleQuotes(sRecId)];
           string sMemberIdQuery;
           if (lstmemid[0].subscriberid != NULL)
           {
                lstmemid = [SELECT Id FROM MemberPlan WHERE MemberId=:String.escapeSingleQuotes(lstmemid[0].subscriberid)];
                
                for (MemberPlan objmemid : lstmemid)
                {
                    string sMemberId = objmemid.id;
                    sMemberIdQuery = 'SELECT Id, Name, subscriberid FROM MemberPlan WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' LIMIT 1';                    

                }
           } 
           else
           {
                for (MemberPlan objmemid : lstmemid)
                {
                    string sMemberId = objmemid.id; 
                    sMemberIdQuery = 'SELECT Id, Name, subscriberid FROM MemberPlan WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' LIMIT 1';                    
                }
           }  
           if(String.isNotBlank(sMemberIdQuery))
               lstmemid  = database.query(sMemberIdQuery);                  
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_LD_HUM', 'getMemberIDObjDetails');
        }
        return lstmemid;
    }
    
   /*
    *  This method is used to get  CAS Translation Details for a Relation code
    *  @name   getLVCASTranslationDetails
    *  @param  String sRelationCode - Relation code for a policy member
    *  @return list<LVRelationShipCodeToCAS__c> - List of LVRelationShipCodeToCAS__c objects 
    *  @throws getLVCASTranslationDetails
    */    
    @AuraEnabled
     public static list<LVRelationShipCodeToCAS__c> getLVCASTranslationDetails(String sRelationCode)
     { 
        list<LVRelationShipCodeToCAS__c> lstlvcas = new list<LVRelationShipCodeToCAS__c>();
        try
        { 
            lstlvcas = [SELECT Name, CASTranslation__c FROM LVRelationShipCodeToCAS__c  WHERE Name=:sRelationCode LIMIT 1];
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_LD_HUM', 'getLVCASTranslationDetails');           
        }
        return lstlvcas;
    }   
    
    /*
    *  This method is used to get Member paramteres for list of MemberPlan records
    *  @name   getMemberParameters
    *  @param  List<String> memberId -  list of member id's for which respective member parameters are needed
    *  @return List<MemberPlan> - List of MemberPlan records
    *  @throws getMemberParameters
    */ 
	@AuraEnabled    
    public static List<MemberPlan> getMemberParameters(List<String> memberId) 
    {
        List<MemberPlan> lstreturnedresults = new List<MemberPlan>();
        String sKey = '';
        try
        {
            //After converting SOQL to SOSL rows limit will be maximum of 2000
            For(String sVal : memberId)
            {
               If(String.IsNotBlank(sVal)) sKey = sKey + ' OR ' + sVal +'*' ;
            }
            If(String.IsNotBlank(sKey))
            {
                sKey = sKey.substring(4,sKey.length());
                String sMemberQuery = 'FIND \'' + String.escapeSingleQuotes(sKey) + '\' IN NAME FIELDS RETURNING MemberPlan(id,MemberId,EffectiveFrom, EffectiveTo, GroupNumber,Product_Type__c, Name '
                              +   ' limit '+ 2000 + ')';
                List<List<MemberPlan>>searchList = search.query(sMemberQuery);
                List<MemberPlan> tempMemberList = searchList[0];
                If(tempMemberList != Null && !tempMemberList.isEmpty())
                {
                    For(MemberPlan obj : tempMemberList)
                    {
                        If(memberId.contains(obj.Name.substring(0,9)))
                        {
                            lstreturnedresults.Add(obj);
                        }
                    }
                }
            }
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        } 
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_LD_HUM', 'getMemberParameters');
        }
        return lstreturnedresults;
     } 
    
   
    /*
    *  This method is used to get policy member names for a particular Member
    *  @name   getPolicyMembersFromAccount
    *  @param  String sAccId - Account Id
    *  @return Set<MemberPlan> - returns set of policy members for a particular Member
    *  @throws NA
    */ 
    
     public static Set<String> getPolicyMembersFromAccount(String sAccId)
     {
        List<MemberPlan> lstPolicyMember = new List<MemberPlan>();
        Set<String> setPolMem = new Set<String>();
        if(String.isNotBlank(sAccId))
        {
            lstPolicyMember = [select Name from MemberPlan where MemberId=:sAccId and Member_Coverage_Status__c in ('Active', 'Termed') and Policy_platform__c in ('LV', 'EM') and Product__c in ('MED', 'DEN')]; 
        }
        if(!lstPolicyMember.isEmpty())
        {
            for(MemberPlan oPolMem : lstPolicyMember)
            {
                setPolMem.add(oPolMem.Name);    
            }
        }
        return setPolMem;
    }
         
    @AuraEnabled
    public static List<PurchaserPlan> requestPoliciesForGroup(String sAccId)
    {
        List<PurchaserPlan> lstPolicies= new List<PurchaserPlan>();
        
        lstPolicies=[Select Id,Name,Product__c,Product__r.Name ,Product__r.Major_LOB__c,EffectiveTo,EffectiveFrom, Product_Type__c,Platform__c, 
                     PayerId,Issue_State__c from PurchaserPlan where PayerId=:sAccId ORDER BY Product__r.Name ASC LIMIT: limitValue ];  
        
        return lstPolicies;
    }

    @AuraEnabled
    public static planWrapper requestGroupPlan(String sPlanId)
    {
        MemberPlan oMemPlan = new MemberPlan();
        oMemPlan = [Select Id,Name, PlanId From MemberPlan where Id= :sPlanId LIMIT 1]; 
        PurchaserPlan oPlan = new PurchaserPlan();
        String sProductType='';
        oPlan =[Select Id,Name, Product__c,Product__r.Name ,EffectiveTo,EffectiveFrom,Major_LOB__c, Product_Type__c,
                Plan_Status__c,iab_description__c,Payer.Name,Benefit_Coverage__c from PurchaserPlan where Id= :oMemPlan.PlanId LIMIT 1]; 
        return new planWrapper(oPlan);
    }
    
    public class planWrapper{   
        @AuraEnabled public String effectiveTo;
        @AuraEnabled public String effectiveFrom;
        @AuraEnabled public String productType;
        @AuraEnabled public String product;
        @AuraEnabled public String benefitPackage;
        @AuraEnabled public String planStatus;
        @AuraEnabled public String accountName;
        @AuraEnabled public String policyName;
        
        public planWrapper(PurchaserPlan oPlan)
        {	
            effectiveTo = String.isNotBlank(String.valueOf(oPlan.EffectiveTo)) ? String.valueOf(oPlan.EffectiveTo) : '';
            effectiveFrom   = String.isNotBlank(String.valueOf(oPlan.EffectiveFrom)) ? String.valueOf(oPlan.EffectiveFrom) : '';
            product = String.isNotBlank(oPlan?.Product__r?.Name) ? oPlan.Product__r.Name: '';
            benefitPackage    = String.isNotBlank(oPlan.Benefit_Coverage__c) ? oPlan.Benefit_Coverage__c: '';
            planStatus  = String.isNotBlank(oPlan.Plan_Status__c) ? oPlan.Plan_Status__c: '';
            productType = (oPlan?.product__r?.name == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_FIELDVALUE_PRODUCTNAME ) ? (String.isNotBlank(oPlan.Product_Type__c) ? oPlan.Product_Type__c: '') : (String.isNotBlank(oPlan.Major_LOB__c) ? oPlan.Major_LOB__c: '');
            accountName  = String.isNotBlank(oPlan?.Payer?.Name) ? oPlan.Payer.Name: '';
            policyName  = String.isNotBlank(oPlan.Name) ? oPlan.Name: '';
        }
    }
}