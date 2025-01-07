/*******************************************************************************************************************************
Apex Class Name : HUMInteractionMemberDmlHelper
Version         : 1.0
Created Date    : 06/23/2014
Function        : 1. This class saves Interaction Member records and returns saved list to the calling controller.
                  2. As Interaction Member is child to account object, hence it inherits its Read only access and will not allow 
                   users to save it. Hence executing this class as 'Without sharing'.
Test Class      : HUMInteractionMemberDmlHelperTest
Modification Log:  
 * Modification ID         Developer Name          Code review               Date                       Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * 1.0                      Ninad Patil             16484                    06/23/2014                 Original Version'
 * 1.1                      Ninad Patil             16484                    07/02/2014                 REQ-01524 (Changes added for deleting interacting about from Interaction.)
 * 1.2                      Ninad Patil             16484                    07/27/2014                 REQ-01521 (changes related to deleting interacting about from interaction
                                                                                                        depending upon whether case is created or not)
 * 1.3                      Ninad Patil             17100                    08/06/2014                 Resubmitting after coding guildlines correction.
 * 1.4                      Ninad Patil             17197                    08/11/2014                 Fixed smoke testing issue.
 * 1.5                      Chaitanya Kumar         17532                    08/20/2014                 Created upsertAccountForInteractionID method to upsert Account data irrespective of sharing settings                                                                                
 * 1.6                      Chaitanya Kumar         17722                    09/02/2014                 Created upsertConsumerIDofAccount
 * 1.7                      Asha Deepika Koyyana    20191                    29/31/2014                 REQ-66089 
 * 1.8                      Asha Deepika Koyyana    20191                    1/7/2014                   CC #20191 comments fix 
 * 1.9                      Shreya Choodamani       20336                    1/14/2014                  Defect Fix for 167991.0001
 * 1.10                     Shreya Choodamani       20380                    01/16/2015                 Modified the code to fix the defect#: 167991.0001
 * 1.11                     Pradeep Veeravali       21397                    03/09/2015                 Modified Code to fix CC Issue # 201, added 2 more methods: updateAccountEmailIds, valdiateEmail
 * 1.12                     Pradeep Veeravali       21519                    03/12/2015                 Modified Code to fix CC Issue # 201, removed methods updateAccountEmailIds, valdiateEmail as accouint data Is encrypted before comparison
 * 1.13                     Praveen Kumar Parimi    27872                    11/16/2015                 Added insertAccountForInteractionID  method to insert Group Account Information on Interaction creation.
 * 1.14                     Keerti Durbha                                    01/15/2020                 782508 PR00086652 (Multi Browser Support) - SF - P9 Medium Defects - CRUD Security fixes
 ********************************************************************************************************************************************************************************************************************/
public without sharing class HUMInteractionMemberDmlHelper
{    
    public boolean bCaseInteractionPres =  false;
    /*
     * Method Name : createInteractionMembers
     * Description : This Function accepts the list of Interaction member records for upserting.
     * Return type : Void
     * Paramater   : List<Interaction_Member__c> lstInteractionMembers 
                    (List of Interaction members object being modified) 
     */
    public List<Interaction_Member__c> createInteractionMembers(List<Interaction_Member__c> lstInteractionMembers)
    {        
        savepoint sp = database.setSavepoint();

        if(!lstInteractionMembers.isEmpty())
        {
            List<Database.upsertResult> lstSvr = Database.upsert(lstInteractionMembers,false);      
            boolean isError =HUMExceptionHelper.processUpsertResults(lstSvr ,lstInteractionMembers ,
                    'HUMInteractionMemberDmlHelper','createInteractionMembers','Interaction_Member__c');  
            if(isError == true)
            {
                Database.Rollback(sp);
            } 
        }  
        return lstInteractionMembers; 
    } 
    
    /*
     * Method Name : createMapIDvsObject
     * Description : This Function caretes and returns map for Interaction ids bs its members./
     * Return type : MAP<ID, Interaction_member__c>
     * Paramater   : Set<ID> setIdsTobeDel -- Ids to be deleted from interaction member
                     String sInteractionId -- Interaction Id from which Interactions to be deleted. 
     */
    private MAP<ID, Interaction_member__c> createMapIDvsObject(Set<ID> setIdsTobeDel, String sInteractionId)
    {
        Map<id, Interaction_member__c> mapInqAbtIdAndName = new Map<id,Interaction_member__c>();
        if(setIdsTobeDel != null && !setIdsTobeDel.isEmpty())
        {
            for(Interaction_member__c oIntMember :[SELECT id, Interacting_About__r.Id, 
                                                   Interacting_About__r.name,createdbyId,interaction__c
                                                   FROM Interaction_member__c 
                                                   WHERE Interacting_About__r.Id IN : setIdsTobeDel
                                                   AND Interaction__c = :sInteractionId] )
            {
                mapInqAbtIdAndName.put(oIntMember.Interacting_About__r.Id, oIntMember);
            }
        }
        return mapInqAbtIdAndName;
    }
    
    /*
     * Method Name : careateInqAbtCase
     * Description : This Function creates map of interaction ids vs its case ids.
     * Return type : MAP<Id, List<Id>>
     * Paramater   : Set<ID> setIdsTobeDel -- Ids to be deleted from interaction member
                     String sInteractionId -- Interaction Id from which Interactions to be deleted. 
     */
    private MAP<Id, List<Id>> careateInqAbtCase(Set<ID> setIdsTobeDel, String sInteractionId)
    {
        List<Id> lstCaseIds = new List<Id>();
        for(case_interaction__C oCaseInt: [Select Id,case__c, interaction__C from case_interaction__C WHere Interaction__C =: sInteractionId])
        {
            lstCaseIds.add(oCaseInt.case__c);
        }

        Map<Id, List<Id>> mapInqAbtCases = new map<Id, List<Id>>();
        for (id sInqAbtId :setIdsTobeDel )
        {
            mapInqAbtCases.put(sInqAbtId , null);
        }

        for(case oCase : [Select id,accountId from case where accountId in :mapInqAbtCases.keyset() AND ID IN :lstCaseIds])
        {
            List<ID> lstCsId = mapInqAbtCases.get(oCase.accountId);
            if(lstCsId == NULL) lstCsId = new List<ID>();
            lstCsId.add(oCase.ID);
            mapInqAbtCases.put(oCase.AccountID,lstCsId );
        }
        return mapInqAbtCases;
    }
    
    /*
     * Method Name : setbCaseInteraction
     * Description : Set bCaseInteractionPres value
     * Return type : boolean
     * Paramater   : Set<ID> setIdsTobeDel -- Ids to be deleted from interaction member
                     String sInteractionId -- Interaction Id from which Interactions to be deleted. 
     */
    public boolean setbCaseInteraction(Set<ID> setIdsTobeDel,String sInteractionId)
    {
        Map<id, Interaction_member__c> mapInqAbtIdAndName = new Map<id,Interaction_member__c>();
        Map<Id, List<Id>> mapInqAbtCases = careateInqAbtCase(setIdsTobeDel,sInteractionId);
        
        if(setIdsTobeDel != null && !setIdsTobeDel.isEmpty())
        {            
            for(Id sMemberId :setIdsTobeDel )
            {
                if(mapInqAbtCases.containskey(sMemberId) && mapInqAbtCases.get(sMemberId) != null && !mapInqAbtCases.get(sMemberId).isEmpty())
                {                    
                   bCaseInteractionPres = true;
                }
                else
                {
                  bCaseInteractionPres = false;
                }
                
            } 
         }      
           return bCaseInteractionPres ; 
    }  
    /*
     * Method Name : deleteInqAbt
     * Description : This Function accepts the list of Interaction member records for deleting them.
     * Return type : boolean
     * Paramater   : Set<ID> setIdsTobeDel -- Ids to be deleted from interaction member
                     String sInteractionId -- Interaction Id from which Interactions to be deleted. 
     */
    public boolean deleteInqAbt(Set<ID> setIdsTobeDel,String sInteractionId)
    {
        Map<id, Interaction_member__c> mapInqAbtIdAndName = new Map<id,Interaction_member__c>();
        
        mapInqAbtIdAndName = createMapIDvsObject(setIdsTobeDel,sInteractionId);

        List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
        
        List<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>();
        
        Map<Id, List<Id>> mapInqAbtCases = careateInqAbtCase(setIdsTobeDel,sInteractionId);
        
        savepoint sp = database.setSavepoint();

        List <ID> lstIdsTobeDel = new List<ID>();
        if(setIdsTobeDel != null && !setIdsTobeDel.isEmpty())
        {            
            for(Id sMemberId :setIdsTobeDel )
            {
                lstIdsTobeDel.add(sMemberId);
            }
            if(deleteCase(lstIdsTobeDel, sInteractionId)) Database.Rollback(sp); 
            if(deleteInteractionMembers(lstIdsTobeDel, sInteractionId)) Database.Rollback(sp);      
        }
        return true; 
    } 
    
    /*
     * Method Name : deleteCase
     * Description : Deleting the case
     * Return type : boolean 
     * Paramater   : List <ID>, String  
     */ 
    private boolean deleteCase(List <ID> lstIdsTobeDel, String sInteractionId)
    {
       boolean isbError = false;
       List<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>();
       lstCaseInteraction = [Select Id from Case_Interaction__c where Interaction__c = :sInteractionId AND Case__r.AccountId IN : lstIdsTobeDel];
       if(!lstCaseInteraction.isEmpty())
       {            
           if(Case_Interaction__c.sobjecttype.getdescribe().isdeletable())
        {            
            List<Database.deleteResult> lstSvrRes = Database.delete(lstCaseInteraction,false);    
            isbError = HUMExceptionHelper.processDeleteResults(lstSvrRes ,lstCaseInteraction ,
                    'HUMInteractionMemberDmlHelper','deleteInqAbt','Case_Interaction__c');     
        }
        }
        return isbError;
    } 
     
    /*
     * Method Name : deleteInteractionMembers
     * Description : Deleting the interaction members
     * Return type : boolean 
     * Paramater   : List <ID>, String  
     */ 
    private boolean deleteInteractionMembers(List <ID> lstIdsTobeDel, String sInteractionId)
    {
        boolean isError = false;
        List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
        //Deleting the Interacting about members which passed all the conditions.
        lstInteractionMembers = [Select Id from Interaction_Member__c 
                                     WHERE  Interaction__C = :sInteractionId AND Interacting_about__C IN : lstIdsTobeDel];

        if(!lstInteractionMembers.isEmpty())
        {
           if(Interaction_Member__c.sobjecttype.getdescribe().isdeletable())
        {
            List<Database.deleteResult> lstSvr = Database.delete(lstInteractionMembers,true);    
            isError = HUMExceptionHelper.processDeleteResults(lstSvr ,lstInteractionMembers ,
                    'HUMInteractionMemberDmlHelper','deleteInqAbt','Interaction_Member__c');     
         }   
        }
         return isError;
    } 
     
    /*
     * Method Name : upsertAccountForInteractionID
     * Description : Upsert the Accounts and return the result 
     * Return type : List<Account>
     * Paramater   : List<Account>
     */
     public static List<Account> upsertAccountForInteractionID (List<Account> lstAccounts)
     {  
         if(lstAccounts!=null)
         { 
             Database.upsertResult[] upsertResults = Database.upsert(lstAccounts, Account.Fields.Account_External_ID__c, false);
             boolean isError = HUMExceptionHelper.processUpsertResults(upsertResults, lstAccounts, 
                        'HUMInteractionMemberDMLHelper', 'upsertAccountForInteractionID', 'Account');
             if (!isError) return lstAccounts;
         }
         return null;
     }
     
     /*
     * Method Name : insertAccountForInteractionID
     * Description : insert the Accounts and return the result 
     * Return type : List<Account>
     * Paramater   : List<Account>
     */
     public static List<Account> insertAccountForInteractionID (List<Account> lstAccounts)
     {  
         if(lstAccounts!=null)
         { 
             Database.SaveResult[] insertResults = Database.insert(lstAccounts, false);
             boolean isError = HUMExceptionHelper.processSaveResults(insertResults, lstAccounts, 
                        'HUMInteractionMemberDMLHelper', 'insertAccountForInteractionID', 'Account');
             if (!isError) return lstAccounts;
         }
         return null;
     }
     
     /*
     * Method Name : upsertConsumerIDOfAccount
     * Description : Upsert the Consumer ID records and return the result 
     * Return type : List<ConsumerId__c>
     * Paramater   : List<ConsumerId__c>
     */
     public static List<ConsumerId__c> upsertConsumerIDofAccount (List<ConsumerId__c> lstConsumers)
     {  
         if(lstConsumers!=null)
         {
             Database.upsertResult[] upsertResults = Database.upsert(lstConsumers,ConsumerId__c.Fields.Consumer_External_ID__c, false);
             boolean isError = HUMExceptionHelper.processUpsertResults(upsertResults, lstConsumers, 
                        'HUMInteractionMemberDMLHelper', 'upsertConsumerIDOfAccount', 'ConsumerId__c');  
                         if (!isError) return lstConsumers;
         }
         return null;
     }
                   
}