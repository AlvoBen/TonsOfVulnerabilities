/***************************************************************************************
Apex Class Name  : HUMWithoutSharingQueryHelper
Version          : 1.0
Created Date     : Sep 09 2014
Function         : This class is used to run Query in without-sharing mode
Test Class       : HUMWithoutSharingQueryHelperTest
 ****************************************************************************************
Modification Log:
 * Developer Name           Code Review                    Date                       Description
 *------------------------------------------------------------------------------------------------------------
 * Manish Kumar              17832                         09/09/2014                    Original Version
 * Manish Kumar              18262                         10/01/2014                    Modified code to fix defect#156234
 * Sagar Tapadia             #####                         01/12/2015                    Modified the code to fix defect#167966.0001
 * Vishal Verma              28495                         05/27/2016                    Defect 212177.0001 : Added order by clause in Policy Member query.
 * Moshitha Gunasekaran                                    05/03/2021                    US#2207661 - Cleanup unused method UpdateHumanaChat of Einstein Chat bot for checkmarx issues
 * Shreya Agrawal/ Sahil Verma                             02/18/2022                    Modified code to fix defect#4469
 * Harshada Kamble/Anil Pavithran                          03/09/2023                    US 2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
 * Harshada Kamble                                         06/22/2023                    User Story 4696940: T1PRJ0036776: Classic - Multi-member transfer call handling
 * Harshada Kamble                                         06/22/2023                    User Story 4707880: T1PRJ0036776: Lightning - Multi-member transfer call handling - Provider (Genesys)
 * Harshada Kamble                                         01/25/2024                    User Story 5602717: T1PRJ0036776: Pass NPI & Tax ID from Watson IVR to Genesys to CRM Salesforce Service
 ***************************************************************************************************/
public without sharing class HUMWithoutSharingQueryHelper
{
    /*
     * Method Name   :    getListOfRecords 
     * Description   :    This method accepts String parameters related to SOQL query.
                          It aslo accepts set of Accoun ids, member Ids, Group number.
                          It Queries and returns result in the form of standard set controller.
     * Return Type   :    ApexPages.StandardSetController
     * Parameters    :    String sSoqlQuery (Query string) 
                          Set <ID> setAccIds 
                          Set <ID> setAccIdForMemberId
                          set <ID> setAccIdForGrpNumber
     */
    public static ApexPages.StandardSetController getListOfRecords (string sSoqlQuery, 
            Set <ID> setAccIds, 
            Set <ID> setAccIdForMemberId, 
            set <ID> setAccIdForGrpNumber)
    {   
        return  new ApexPages.StandardSetController(Database.getQueryLocator(sSoqlQuery));
    }
    
    /*
     * Method Name   :    getListOfsObjects 
     * Description   :    This method accepts String parameters related to SOQL query.
                          It aslo accepts set of Accoun ids, member Ids, Group number.
                          It Queries and returns result in the form of sObject.
     * Return Type   :    List<sObject>
     * Parameters    :    String sSoqlQuery (Query string) 
                          Set <ID> setAccIds 
                          Set <ID> setAccIdForMemberId
                          set <ID> setAccIdForGrpNumber
     */
    public static List<sObject> getListOfsObjects(string sSoqlQuery, 
            Set <ID> setAccId, 
            Set <ID> setAccIdForMemberId, 
            set <ID> setAccIdForGrpNumber)
    {   
        if(setAccId != NULL)
        {
            sSoqlQuery +=' setAccId';
            if(sSoqlQuery.containsIgnoreCase('Policy_member__c'))    sSoqlQuery +=  ' order by Member__c desc NULLS LAST Limit ' +HUMCOnstants.HumListsizeLimit;
        }
        return Database.query(String.escapeSingleQuotes(sSoqlQuery));
    }
    
    /*
     * Method Name   :    fetchAccounts 
     * Description   :    This member fetches the Account for a given ID in without sharing mode
     * Return Type   :    List<Account> 
     * Parameters    :    Id
     */
    public Static List<Account> fetchAccounts(Id accId)
    {
        List<Account> lstAcc = new List<Account>();
        lstAcc = [SELECT Id, Name, Recordtype.Name from Account Where Id =: accId];
        return lstAcc;
    }
    
    /*
     * Method Name   :    fetchInteractionMembers
     * Description   :    This function fetches Interaction members for Intraction id passed.
     * Return Type   :    List<Interaction_member__c>
     * Parameters    :    String
     */
    public Static List<Interaction_member__c> fetchInteractionMembers(String sInteractionId)
    {
        List<Interaction_Member__c> lstSelectedMembers = new List<Interaction_Member__c>();
        lstSelectedMembers = [select Id, Name, Interacting_about__C,
                                      Interacting_about__r.name,
                                      Interacting_About_Type__c,Interaction__c, Authenticated__c 
                                      FROM Interaction_Member__c 
                                      Where Interaction__c = : sInteractionId];
        return  lstSelectedMembers;
    }
    
     /*
     * Method Name   :    insertInteraction 
     * Description   :    This member is used to upsert interaction record in without sharing mode.
                          We are doing this in without sharing because we are not able to access the account record in sharing mode
     * return Type   :    Database.upsertResult
     * Parameters    :    Interaction__c
     */
    public Static Database.upsertResult inserInteraction(Interaction__c oInt)
    {
        Database.upsertResult oSvr = Database.upsert(oInt,false);
        return oSvr;
    }

    /*
     * Method Name   :    fetchInteractionwithItsMembers
     * Description   :    This function fetches Interaction along with its Interaction members for UUID passed.
     * 					  We are doing this in without sharing because we are not able to access the account record in sharing mode
     * Return Type   :    List<Interaction__c>
     * Parameters    :    String
     */
    public Static List<Interaction__c> fetchInteractionwithItsMembers(String uuidataValue)
    {
        List<Interaction__c> lstInteractionRec = new List<Interaction__c>();
        if(uuidataValue != null && uuidataValue != ''){
            lstInteractionRec = [SELECT Id, Caller_Name__c, Authenticated__c, Interacting_With__c,Interacting_With__r.Name,Interacting_With__r.FirstName,Interacting_With__r.LastName, Interacting_With__r.Birthdate__c, Interacting_With__r.Group_Number__c,Interacting_With__r.agent_ID__c,Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c, CreatedDate, Interacting_With__r.NPI_ID__c, (SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About__r.Name,Interacting_About__r.Group_Number__c,Interacting_About__r.agent_ID__c, Interacting_About_Type__c, Interaction__c,  Authenticated__c FROM Interaction_Members__r order by CreatedDate desc) FROM Interaction__c where UUID__c =: uuidataValue order by Name desc limit 1];
        }
        return  lstInteractionRec;
    }

     /*
     * Method Name   :    fetchCurrSelectedIntAbt
     * Description   :    This function fetches Interaction along with its Interaction members for UUID passed.
     * 					  We are doing this in without sharing because we are not able to access the account record in sharing mode
     * Return Type   :    List<Interaction__c>
     * Parameters    :    String
     */
    public Static List<Interaction__c>  fetchCurrSelectedIntAbt(String uuidataValue,string currSelectedIntAboutValue)
    {
        List<Interaction__c>  lstInteractionRec = new List<Interaction__c>  ();

        if(HUMUtilityHelper.isCRMFunctionalityON('4696940')){
            lstInteractionRec = [SELECT Id, Caller_Name__c, Authenticated__c, Interacting_With__c,Interacting_With__r.Name,Interacting_With__r.FirstName,Interacting_With__r.LastName, Interacting_With__r.Birthdate__c, Interacting_With__r.Group_Number__c,Interacting_With__r.agent_ID__c,Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c, CreatedDate, Interacting_With__r.NPI_ID__c, (SELECT Id, Authenticated__c, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About__r.Name,Interacting_About__r.Group_Number__c,Interacting_About__r.agent_ID__c, Interacting_About_Type__c, Interaction__c FROM Interaction_Members__r where Interacting_About__c=: currSelectedIntAboutValue order by name ASC) FROM Interaction__c where UUID__c =: uuidataValue order by Name desc limit 1];
        }

        return  lstInteractionRec;
    }

    /*  
    * Method name : getAccount
    * Description : This method will return account object for the Id passed as Input.
    * Return Type : Account
    * Parameter : Requires 1 argument, Account Id
    */
    public static Account getAccount(String sAccId)
    {
        try
        {
            List<Account> lstAcc = new List<Account>();
            String sQry = 'SELECT Id,Mbr_Gen_Key__c,Birthdate__c,FirstName,LastName,Individual_First_Name__c,Individual_Last_Name__c,Name,RecordType.Name,Enterprise_ID__c,Group_Number__c,'
                         +'Source_Platform_Code__c,(Select Consumer_Id__c from ConsumerIds__r order by lastmodifieddate desc limit 1)'
                         +',(Select Id,Name from policy_members__r order by lastmodifiedDate desc limit 1) from Account WHERE id = :sAccId LIMIT 1';
            lstAcc = Database.Query(sQry);
            if(!lstAcc.isEmpty()) 
            { 
                return lstAcc[0]; 
            }
            else
            {
                return null;
            }
         }
         catch(Exception e)
         {
            HUMExceptionHelper.logErrors(e, 'HUMWithoutSharingQueryHelper', 'getAccount');
            return null; 
         }
     }

    /*  
    * Method name : getMapping
    * Description : This method will return account object for the Id passed as Input.
    * Return Type : Map<String,String>
    * Parameter : Requires 1 argument, UUID
    */
     public static Map<String,String> getMapping(string UUID)
     {
        Map<String,String> mapSoftPhone = new Map<String,String>();
        try{
             if (!String.isEmpty(UUID)) 
             {
                 List < Integration_Mapping__c > lstsoftphoneCallRouting = new List < Integration_Mapping__c > ();
                 String sQry = 'SELECT Mapping__c FROM Integration_Mapping__c WHERE Record_ID__c = :UUID ORDER BY CreatedDate DESC Limit 1';
                 lstsoftphoneCallRouting = Database.query(sQry);
                 if (!lstsoftphoneCallRouting.isEmpty()) 
                 {
                    if (lstsoftphoneCallRouting[0] != null) 
                    {
                        if (lstsoftphoneCallRouting[0].Mapping__c != null) 
                        {
                            if (lstsoftphoneCallRouting[0].Mapping__c.contains(system.label.HUMSPhoneMappingObj_semicolon) && lstsoftphoneCallRouting[0].Mapping__c.contains(system.label.HUMSPhoneMappingObj_colon)) 
                            {
                                for (String s: lstsoftphoneCallRouting[0].Mapping__c.split(system.label.HUMSPhoneMappingObj_semicolon)) 
                                {
                                    if (s.contains(system.label.HUMSPhoneMappingObj_colon)) 
                                    {
                                        List < String > lstTemp = s.split(system.label.HUMSPhoneMappingObj_colon);
                                        if (lstTemp.size() == 1) 
                                        {
                                            mapSoftPhone.put(lstTemp[0], '');
                                        }
                                        else if(lstTemp.size() > 1)
                                        {
                                            mapSoftPhone.put(lstTemp[0], lstTemp[1]); 
                                        }
                                    }
                                }
                            }
                        }
                       
                    }
                 }
             }
             return mapSoftPhone;
         }
         catch(Exception e)
         {
            HUMExceptionHelper.logErrors(e, 'HUMWithoutSharingQueryHelper', 'getMapping');
            return null; 
         }
     }
}