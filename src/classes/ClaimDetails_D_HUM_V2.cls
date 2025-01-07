/*****************************************************************************************
Apex class Name    : ClaimDetails_D_HUM_V2
Version            : 1.0
Function           : DataLayer Class for the Claims Details
Created Date       : 04/05/2018
Test Class         : ClaimDetailsDataLogic_T_HUM_V2

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Muralidhar Kollu              27974               05/17/2016           Added getEnterpriseID() method to return Enterprised id for the Account
                                                                            associated to Policy Member
 *   Prasanthi Kandula             27974               05/18/2016           Added getLoggedinUserQueue() method to return the Loggedin
                                                                            user current queue  
 *   Pallavi Ravishankar           29226               06/02/2016           Added getPolicyMember, getLVCASTranslationDetails,getMemberIDObjDetails
                                                                            For REQ  263142 
 *   Pallavi Ravishankar           33054               07/14/2016           Added queryMemberParameters, queryPolicyMembers for defect fix 231974.0001                                                                                                                   
 *   Prasanthi Kandula             33598               07/25/2016           Changed getLoggedinUserQueue() method to static. 
 *   Saurabh Kaple                 157136              01/10/2018           REQ - 348181 Mobius Service- Use Genkey from platform instead of MBE genkey conversion  
 *   Prasanthi Kandula             212620              04/05/2018           REQ - 355817 - Changed as part of ClaimsV3 service change
 *   Sunil Bukya                   239093              05/25/2018           REQ - 362049 Implemented Member_Id__c platform encryption changes.
 *   Praveen Kumar Parimi          249721              06/25/2018           REQ - 367749 Updated getMemberIDObjDetails method to send appropriate Member ID.
 *   Praveen Kumar Parimi          249721              08/17/2018           REQ - 367749 Issue fix in PROD.
 *  Santhi Mandava                354419              04/15/2019           Replaced "Member_Id_Value__c" with Name
 *  Santhi Mandava                356422              05/15/2019           Replaced member id SOQL with SOSL
 *  Anjani Vaddadi                                    02/25/2020           REQ - 764250
 *  Ramakrishna Gundeti                               07/01/2020           US #1210909 - PCP Not showing correctly under Claims PCP_PCD
 *  Prasanthi Kandula                                 02/01/2021           US-1669846 SonarQube critical security fixes 
 *  Satyanarayana Chenna         2077440              04/27/2021           US-T1PRJ0002736 - 2021 MF6  - CR AEP readiness - Low Security Issues  
 *  Pinky Vijur                                       02/22/2023         User Story 4200769: T1PRJ0883127- Author Shutdown- MF 2- Display and Resend Author generated tEOB�s in CRM Service (Classic)
*********************************************************************************************************************************/
Public with sharing Class ClaimDetails_D_HUM_V2
{      
     /*
     * Method Name   :  getEnterpriseID
     * Description   :  This method Is used for getting Enterprise_ID__c from Account associated to Policy Member
     * Return Type   :  List<POLICY_Member__C>
     * Parameters    :  ID sRecId
     */
    public static List<POLICY_Member__C> getEnterpriseID(ID sRecId)
    {
        try
        {
        
            return (List<POLICY_Member__C>)[SELECT Id, Member__r.Enterprise_ID__c FROM POLICY_Member__C WHERE ID=:sRecId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_D_HUM_V2', 'getEnterpriseID');
            return null;
        } 
    } 
    
     /*
     * Method Name   :  getMemberGenKey
     * Description   :  This method is used for getting Mbr_Gen_Key__c from Account associated to Policy Member
     * Return Type   :  List<POLICY_Member__C>
     * Parameters    :  ID sRecId
     */
    public static List<POLICY_Member__C> getMemberGenKey(ID sRecId)
    {
        try
        {
        
            return (List<POLICY_Member__C>)[SELECT Id, Member__r.Mbr_Gen_Key__c, Subscriber__c FROM POLICY_Member__C WHERE ID=:sRecId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_D_HUM_V2', 'getEnterpriseID');
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
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_D_HUM_V2', 'getLoggedinUserQueue');
            return null;
        }          
       
    }  
    
    /*
     * Method Name   :  getPolicyMember
     * Description   :  This method is used for getting Policy Member Details
     * Return Type   :  List<POLICY_Member__C>
     * Parameters    :  ID sRecId
     */
    
   public static List<POLICY_Member__C> getPolicyMember(ID sRecId)
   {
          List<POLICY_Member__C> lstpolmem = null;
          lstpolmem = new List<POLICY_Member__C>();
          lstpolmem = [SELECT Id, Policy__r.Client_Number__c, Member__c, Member__r.FirstName, Relationship_Code__c, Product_Type__c,Product__c FROM POLICY_Member__C WHERE ID=:String.escapeSingleQuotes(sRecId)];
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
     * Method Name   :  getMemberIDObjDetails
     * Description   :  This method returns Member ID object
     * Return Type   :  list<Member_ID__c>
     * Parameters    :  ID sRecId
     */ 
         
     public static list<Member_ID__c> getMemberIDObjDetails(ID sRecId)
     { 
         list<Member_ID__C> lstmemid = new list<Member_ID__C>();
           lstmemid = [SELECT Id,Name,policy_Member__r.subscriber__c,Type__c FROM Member_ID__c WHERE Policy_Member__c=: String.escapeSingleQuotes(sRecId)];
           string sMemberIdQuery;
           if (lstmemid[0].policy_Member__r.subscriber__c != NULL)
           {
                lstmemid = [SELECT Id, Type__c FROM Member_ID__c WHERE Policy_Member__c=: String.escapeSingleQuotes(lstmemid[0].policy_Member__r.subscriber__c)];
                
                for (Member_ID__c objmemid : lstmemid)
                {  
                  string sMemID =  objmemid.id;  
                  if(objmemid.Type__c == 'SSN')
                  {
                    sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemID) + '\'  AND Type__c = \'SSN\' LIMIT 1';                    
                    break;
                  }
                  else if (objmemid.Type__c == 'Member-Id-Base')
                  {                  
                    sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemID) + '\'  AND Type__c = \'Member-Id-Base\' LIMIT 1';                     
                  }                      
                }
           } 
           else
           {
                for (Member_ID__c objmemid : lstmemid)
                {
                  string sMemID =  objmemid.id;
                  if(objmemid.Type__c == 'SSN')
                  {
                    sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemID) + '\' AND Type__c = \'SSN\' LIMIT 1';                        
                    break;
                  }
                  else if(objmemid.Type__c == 'Member-Id-Base')
                  {
                    sMemberIdQuery = 'SELECT Id, Name, Policy_Member__c, policy_Member__r.subscriber__c, Type__c FROM Member_ID__c WHERE id =\'' +string.escapeSingleQuotes(sMemID) + '\' AND Type__c = \'Member-Id-Base\' LIMIT 1';                      
                  }                       
                }
           }
          lstmemid  = database.query(sMemberIdQuery);
          return lstmemid;
     } 
    
     /*
     * Method Name : queryMemberParameters
     * Description : This method will fetch the List of Member_ID__c records 
     * Return type : List<Member_ID__c>
     * Paramater   : String , String 
     */
     
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
    */
    
   public static List<policy_member__c> queryPolicyMembers(set<id> PID) 
   {
    List<policy_member__c> selctedpolciymember=new List<policy_member__c>();
    selctedpolciymember =[select id,name,Effective_Date__c,Status__c from policy_member__c where id  in : PID]; 
    return selctedpolciymember;
   }  
       
}