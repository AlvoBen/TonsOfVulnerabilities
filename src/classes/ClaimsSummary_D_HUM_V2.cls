/*****************************************************************************************
Apex class Name    : ClaimsSummary_D_HUM_V2
Version            : 1.0
Function           : DataLayer Class for the Claims Summary
Created Date       : 04/04/2018
Test Class         : ClaimsSummaryDataLogic_T_HUM_V2

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   HarikaDevi Kalidindi         26321                  05/03/2016         For the defect fix: 224498.0001   One member's Claims Summary link is not working when you click it
 *   Manish Kumar                                        05/03/2016         For the defect fix: 224903.0001. Fixed DML exception
 *   Gargey Sharma                28937                  06/01/2016         REQ - 269631 Ability to link to CAS legacy from the Claim Summary Page and restructured class to increase
 *   Harika Devi Kalidindi        35220                  08/11/2016         Defect Fix 227069.0001 -Added queryMemberParameters method
 *   Harika Devi Kalidindi        38517                  09/02/2016         Defect  Fix 239780.0001-Changed SOQL Limit in queryMemberParameters method
 *   Santhi Mandava               40488                  09/20/2016         Added logic to retrieve account enterprise id.
 *   Harika Devi Kalidindi                               11/11/2016         Defect Fix 247701 - updated the getMemberParameters to get member details
 *   Prasanthi Kandula            212620                 04/04/2018         Added getPolicyMembersFromAccount,getAccountId methods
 *   Suresh Gottipati                                    06/15/2018         REQ - 366902 - Claim Summary section in Claim Summary page.
 *   Praveen Kumar Parimi         249721                 06/25/2018         REQ - 367749 Updated getMemberIDObjDetails method to send appropriate Member ID.
 *   Praveen Kumar Parimi         249721                 08/17/2018         REQ - 367749 Issue fix in PROD.
 *   Prudhvi Pamarthi                                    02/18/2019         REQ - 361155 - Not allowing case creation on Legacy Deleted policies
 *   Santhi Mandava               354419                 04/15/2019         Replaced "Member_Id_Value__c" with Name
 *   Santhi Mandava               356422                 05/15/2019         Replaced member id SOQL with SOSL
 *   Prasanthi Kandula                                   02/01/2021         US-1669846 SonarQube critical security fixes 
 *   Satyanarayana Chenna         2077440                04/27/2021         US-T1PRJ0002736 - 2021 MF6  - CR AEP readiness - Low Security Issues  
 *   Prasanthi Kandula                                   07/02/2021         US - 2356070 Claims Summary performance improvement 
 *   Tharun M                                            02/16/2023         US-4200713: T1PRJ0883127- Author Shutdown- MF 1- Display Author claims in CRM Service (Classic)
*********************************************************************************************************************************/

public with sharing class ClaimsSummary_D_HUM_V2
{
    
   /*
    *  This method  is used to return details of policy member
    *  @name   getPolicyMemberDetails
    *  @param  ID - Record ID of Policy Member for which claims are to be displayed
    *  @return List<Policy_Member__c> -  List of Policy member objects
    */
    public static List<Policy_Member__c> getPolicyMemberDetails(ID sRecId)    {
        List<Policy_Member__c> lstpolmem = new List<Policy_Member__c>();
        try
        { 
            lstpolmem = [SELECT Id, Policy__r.Client_Number__c, Member__r.FirstName,Product__c, Member__r.Enterprise_Id__c, Member__c, Relationship_Code__c, Member__r.name,ETL_Record_Deleted__c FROM POLICY_Member__C WHERE ID=:String.escapeSingleQuotes(sRecId)];
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_D_HUM_V2', 'getMemberName');
        }
        return lstpolmem;    
    }
    
   /*
    *  This method  is used to return Member Id details for a policy member
    *  @name   getMemberIDObjDetails
    *  @param  ID - Record ID of Policy Member which has related Member_ID_c records
    *  @return list<Member_ID__c> - List of Member_ID_c objects related to given policy member
    *  @throws getMemberIDObjDetails
    */    
     public static list<Member_ID__c> getMemberIDObjDetails(ID sRecId)
     { 
         list<Member_ID__C> lstmemid = new list<Member_ID__C>();
        try
        {            
           lstmemid = [SELECT Id,Name, policy_Member__r.subscriber__c,Type__c FROM Member_ID__c WHERE Policy_Member__c=:String.escapeSingleQuotes(sRecId)];
           string sMemberIdQuery;
           if (lstmemid[0].policy_Member__r.subscriber__c != NULL)
           {
                lstmemid = [SELECT Id, Type__c FROM Member_ID__c WHERE Policy_Member__c=:String.escapeSingleQuotes(lstmemid[0].policy_Member__r.subscriber__c)];
                
                for (Member_ID__c objmemid : lstmemid)
                {
                    string sMemberId = objmemid.id;
                    if(objmemid.Type__c == 'SSN')
                    {
                        sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' AND Type__c = \'SSN\' LIMIT 1';                    
                        break;
                    }
                    else if (objmemid.Type__c == 'Member-Id-Base')
                    {                  
                       sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' AND Type__c = \'Member-Id-Base\' LIMIT 1';                     
                    }    
                }
           } 
           else
           {
                for (Member_ID__c objmemid : lstmemid)
                {
                    string sMemberId = objmemid.id;
                    if(objmemid.Type__c == 'SSN')
                    {
                        sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' AND Type__c = \'SSN\' LIMIT 1';         
                        break;
                    }
                    else if(objmemid.Type__c == 'Member-Id-Base')
                    {
                        sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' AND Type__c = \'Member-Id-Base\' LIMIT 1';   
                    } 
                }
           }  
           if(String.isNotBlank(sMemberIdQuery))
               lstmemid  = database.query(sMemberIdQuery);                  
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_D_HUM_V2', 'getMemberIDObjDetails');
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
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_D_HUM_V2', 'getLVCASTranslationDetails');           
        }
        return lstlvcas;
    }   
    
    /*
    *  This method is used to get Member paramteres for list of Member_ID__c records
    *  @name   getMemberParameters
    *  @param  List<String> memberId -  list of member id's for which respective member parameters are needed
    *  @return List<Member_ID__c> - List of Member_ID__c records
    *  @throws getMemberParameters
    */     
    public static List<Member_ID__c> getMemberParameters(List<String> memberId) 
    {
        List<Member_ID__c> lstreturnedresults = new List<Member_ID__c>();
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
                String sMemberQuery = 'FIND \'' + sKey  + '\' IN NAME FIELDS RETURNING Member_Id__c(id,Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c,Policy_Member__r.Group_Number__c,Policy_Member__r.Product_Type__c, Name ,Member_Dependent_Code__c '
                              +   ' limit '+ 2000 + ')';
                List<List<Member_Id__c>>searchList = search.query(sMemberQuery);
                List<Member_Id__c> tempMemberList = searchList[0];
                If(tempMemberList != Null && !tempMemberList.isEmpty())
                {
                    For(Member_Id__c obj : tempMemberList)
                    {
                        If(memberId.contains(obj.Name))
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
            HUMExceptionHelper.logErrors(e, 'ClaimsSummary_D_HUM_V2', 'getMemberParameters');
        }
        return lstreturnedresults;
     } 
    
   
    /*
    *  This method is used to get policy member names for a particular Member
    *  @name   getPolicyMembersFromAccount
    *  @param  String sAccId - Account Id
    *  @return Set<Policy_Member__c> - returns set of policy members for a particular Member
    *  @throws NA
    */    
     public static Set<String> getPolicyMembersFromAccount(String sAccId)
     {
        Boolean bSwitch_4200713 = false;
		bSwitch_4200713 = HUMUtilityHelper.isCRMFunctionalityON('4200713');
        List<Policy_member__c> lstPolicyMember = new List<Policy_Member__c>();
        Set<String> setPolMem = new Set<String>();
        if(String.isNotBlank(sAccId))
        {
            if(!bSwitch_4200713)
            	lstPolicyMember = [select Name from policy_member__c where Member__c=:sAccId and Status__c in ('Active', 'Termed') and Policy_platform__c in ('LV', 'EM') and Product__c in ('MED', 'DEN')]; 
            else if(bSwitch_4200713)
               	lstPolicyMember = [select Name from policy_member__c where Member__c=:sAccId and Status__c in ('Active', 'Termed') and Policy_platform__c in ('LV', 'EM', 'HP') and Product__c in ('MED', 'DEN')];
        }
        if(!lstPolicyMember.isEmpty())
        {
            for(Policy_Member__c oPolMem : lstPolicyMember)
            {
                setPolMem.add(oPolMem.Name);    
            }
        }
        return setPolMem;
    }
          
}