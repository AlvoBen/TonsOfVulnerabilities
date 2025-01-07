/*****************************************************************************************
Apex class Name    : ClaimDetails_LD_HUM_V2
Version            : 1.0
Function           : DataLayer Class for the Claims Details
Created Date       : 06/01/2022
Test Class         : ClaimDetailsDataLogic_LT_HUM_V2

Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
* Sagar Gulleve	                   		        10/11/2022                 DF-6312 Fix
* Suraj Patil									05/22/2023				   US-4600146: Remove references of Member_ID__c in Lightning C
***************************************************************************************************/
Public with sharing Class ClaimDetails_LD_HUM_V2
{      
     /*
     * Method Name   :  getEnterpriseID
     * Description   :  This method Is used for getting Enterprise_ID__c from Account associated to Policy Member
     * Return Type   :  List<MemberPlan>
     * Parameters    :  ID sRecId
     */
    public static List<MemberPlan> getEnterpriseID(ID sRecId)
    {
        try
        {
        
            return (List<MemberPlan>)[SELECT Id, Member.Enterprise_ID__c FROM MemberPlan WHERE ID=:sRecId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_LD_HUM_V2', 'getEnterpriseID');
            return null;
        } 
    } 
    
     /*
     * Method Name   :  getMemberGenKey
     * Description   :  This method is used for getting Mbr_Gen_Key__c from Account associated to Policy Member
     * Return Type   :  List<MemberPlan>
     * Parameters    :  ID sRecId
     */
    public static List<MemberPlan> getMemberGenKey(ID sRecId)
    {
        try
        {
        
            return (List<MemberPlan>)[SELECT Id, Member.Mbr_Gen_Key__c, SubscriberId,SubscriberPlanId__c FROM MemberPlan WHERE ID=:sRecId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_LD_HUM_V2', 'getEnterpriseID');
            return null;
        } 
    } 
    
    /*
     * Method Name   :  getLoggedinUserQueue
     * Description   :  This method will return Current_Queue for Loggedin User
     * Return Type   :  User 
     * Parameters    :  Id sUserId
     */
    public static User getLoggedinUserQueue(Id sUserId)
    {
        try
        {
            return [select Id, Current_Queue__c from User where id=:sUserId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_LD_HUM_V2', 'getLoggedinUserQueue');
            return null;
        }          
       
    }  
    
    /*
     * Method Name   :  getPolicyMember
     * Description   :  This method is used for getting Policy Member Details
     * Return Type   :  List<POLICY_Member__C>
     * Parameters    :  ID sRecId
     */
    
   public static List<MemberPlan> getPolicyMember(ID sRecId)
   {
          List<MemberPlan> lstpolmem = null;
          lstpolmem = new List<MemberPlan>();
          lstpolmem = [SELECT Id, Policy__r.Client_Number__c, MemberId, Member.FirstName, Relationship_Code__c, Product_Type__c FROM MemberPlan WHERE ID=:String.escapeSingleQuotes(sRecId)];
          return lstpolmem;
   
   }
   
   /*
     * Method Name   :  getMemberId
     * Description   :  This method is used for getting Policy Member Details
     * Return Type   :  ID sRecId
     * Parameters    :  Member Name  
     */
    @AuraEnabled
    public Static List<MemberPlan> getMemberId(String sMemName)    {
        List<MemberPlan> lstpolmem = new List<MemberPlan>();
        try
        { 
			lstpolmem = [SELECT Id, Plan.Client_Number__c, Product__c, Name, Policy_Platform__c, Member.Mbr_Gen_Key__c, Member.Enterprise_Id__c, Member.name, MemberId, Relationship_Code__c, ETL_Record_Deleted__c FROM MemberPlan WHERE Name=:String.escapeSingleQuotes(sMemName)];      
		}  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimDetails_LD_HUM', 'getMemberId');
        }
        return lstpolmem;    
    }
	
    /*
     * Method Name   :  getLVCASTranslationDetails
     * Description   :  This method returns LVRelationShipCodeToCAS__c custom setting
     * Return Type   :  list<LVRelationShipCodeToCAS__c>
     * Parameters    :  String
     */    
      
     public static list<LVRelationShipCodeToCAS__c> getLVCASTranslationDetails(String sRelationCode)
     {    
            list<LVRelationShipCodeToCAS__c>    lstlvcas = new list<LVRelationShipCodeToCAS__c>();
            lstlvcas = [SELECT Name, CASTranslation__c FROM LVRelationShipCodeToCAS__c  WHERE Name=:sRelationCode LIMIT 1];
        
            return lstlvcas;
    }       
    
    /*
     * Method Name   :  getMemberPlanObjDetails
     * Description   :  This method returns Member ID object
     * Return Type   :  list<MemberPlan>
     * Parameters    :  ID sRecId
     *
    */     
    @AuraEnabled
     public static list<MemberPlan> getMemberPlanObjDetails(ID sRecId)
     { 
         list<MemberPlan> lstmemid = new list<MemberPlan>();
        try
        {            
           lstmemid = [SELECT Id,Name, subscriberid FROM MemberPlan WHERE id=:String.escapeSingleQuotes(sRecId)];
           string sMemberIdQuery;
           if (lstmemid[0].subscriberid != NULL)
           {
                lstmemid = [SELECT Id FROM MemberPlan WHERE MemberId=:String.escapeSingleQuotes(lstmemid[0].subscriberid)];
                
                for (MemberPlan objmemid : lstmemid)
                {
                    string sMemberId = objmemid.id;
                    sMemberIdQuery = 'SELECT Id, Name,Member_Id_Base__c, subscriberid FROM MemberPlan WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' LIMIT 1';                    
                }
           } 
           else
           {
                for (MemberPlan objmemid : lstmemid)
                {
                    string sMemberId = objmemid.id; 
                    sMemberIdQuery = 'SELECT Id, Name,Member_Id_Base__c, subscriberid FROM MemberPlan WHERE id =\'' +string.escapeSingleQuotes(sMemberId) + '\' LIMIT 1';                    
                }
           }  
           if(String.isNotBlank(sMemberIdQuery))
               lstmemid  = database.query(sMemberIdQuery);                  
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ClaimDetails_LD_HUM_V2', 'getMemberPlanObjDetails');
        }
        return lstmemid;
    }
    
     /*
     * Method Name : queryMemberParameters
     * Description : This method will fetch the List of Member_ID__c records 
     * Return type : List<Member_ID__c>
     * Paramater   : String , String 
     *
     
   public static List<Member_ID__c> queryMemberParameters(String MemberID, String sDependentCode) 
   {
        List<Member_ID__c> lstreturnedresults =new List<Member_ID__c>();
        String sMemberQuery = '';
        String sSearchKey = '';
        sSearchKey = MemberID+'*';
        sMemberQuery = 'FIND \'' + String.escapeSingleQuotes(sSearchKey)  + '\' IN NAME FIELDS RETURNING Member_Id__c(Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name ';
        if(String.IsNotBlank(sDependentCode))
        {
            sMemberQuery = sMemberQuery + ' where Member_Dependent_Code__c = \'' +  String.escapeSingleQuotes(sDependentCode) + '\')';      
        }
        else
        {
            sMemberQuery = sMemberQuery + ')';
        }

        List<List<Member_Id__c>>searchList = search.query(sMemberQuery);
        List<Member_Id__c> tempMemberList = searchList[0];
        
        If(tempMemberList != Null && !tempMemberList.isEmpty())
        {
            For(Member_Id__c obj : tempMemberList)
            {
                If(obj.Name == MemberID)
                {
                    lstreturnedresults.Add(obj);
                }
            }
        }
       return lstreturnedresults;
   }
   
   /*
    * Method Name : queryPolicyMembers
    * Description : This method will fetch the required Policy Member details from the set of ID
    * Return type : List<policy_member__c>
    * Paramater   : set<id> PID
    *
    
   public static List<policy_member__c> queryPolicyMembers(set<id> PID) 
   {
    List<policy_member__c> selctedpolciymember=new List<policy_member__c>();
    selctedpolciymember =[select id,name,Effective_Date__c,Status__c from policy_member__c where id  in : PID]; 
    return selctedpolciymember;
   }  
*/
       
}