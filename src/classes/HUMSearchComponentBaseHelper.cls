/*******************************************************************************************************************************
Apex Class Name : HUMSearchComponentBaseHelper
Version         : 1.0
Created On      : 05/21/2014
Function        : This class serves as base class for all the search component class
Test Class      : HUMSearchComponentBaseHelperTest
Modification Log: 
 * Developer Name              Code Review                   Date                      Description
 *-------------------------------------------------------------------------------------------------------------------------------------------------------------------
 *  Chaitanya Kumar            17172                       05/21/2014                  Original  
 *  Shruthi Karanth            17172                       07/07/2014                  Added comments and code for pagination 
 *  Chaitanya Kumar            17172                       07/25/2014                  Added code for unknown search functionality
 *  Chaitanya Kumar            17862                       08/11/2014                  Modified insertDataFromExternalSystem to do upsert in a Without sharing class
 *  Chaitanya Kumar            17862                       08/22/2014                  Modified insertDataFromExternalSystem to encrypt values before save   
 *  Chaitanya Kumar            17862                       09/02/2014                  Created upsertConsumerTaxIds method
 *  Chaitanya Kumar            17862                       09/10/2014                  Moved logic in insertDataFromExternalSystem and upsertConsumerTaxIds to HUMUtilityHeper
 *  Chaitanya Kumar            17862                       09/11/2014                  Modified upsertConsumerTaxIds defination to accomodate normal and encrypted tax id string
 *  Pradeep Veeravali          21519                       03/12/2015                  added 2 methods getEmailForAgentAccount & valdiateEmail as part of fix to CC Issue #201
 *  ChiranjeeviRao Ravuri      21640                       03/17/2015                  REQ - 160866:Removed insertDataFromServiceWithSID() as part of upsert using MemberID from service inplace of salesforceID in account_external_id__c for CBIS unknown account. 
 *  Praveen Kumar Parimi       27872                       11/16/2015                  Modified insertDataFromExternalSystem to return or insert Group Account Information.
 *  Sunil Bukya                                            07/23/2018                  REQ - 362070 - Implemented Group Name platform encryption changes.
 **********************************************************************************************************************************************************************/
public virtual class HUMSearchComponentBaseHelper extends HUMSearchFieldsHelper 
{
    /*
     * Method Name : HUMSearchComponentBase
     * Description : Constructor for the class
     * Return type : NA
     * Paramater   : NA
     */
    public HUMSearchComponentBaseHelper() 
    {
        mapRecordData = new Map<Integer,Account>();
        iRecordMapSize = 0;
        iPagesize = 0;
        iCounter = 0;
    }

    //Used for pagination
    public Integer iCounter { get;set;}    
    public Integer iPagesize{ get;set;}
    //Store the Json Vesion of mapRecordSet
    public transient Map < Integer, String > mapRecordSetJson {get;private set;}    
    //Store Serialized version of Tax Ids in Consumer ID object
    public  Map<Integer ,String> mapConsumerTaxIds 
    {
        get
        {
            if(mapConsumerTaxIds==null) { return new Map<Integer,String>(); }
            return mapConsumerTaxIds;

        }
        set;
    }    
    //This is used for setting the data in the search controller classes
    public Map < Integer, Account > mapRecordData { get;set;}       
    //This is used for displaying in the search VF components
    public ApexPages.StandardSetController setAccount {get;set;}
    public Integer iRecordMapSize {get;set;}
    //Store the type of Component
    public String key 
    {
        get;
        set 
        {
            if (value != null) 
            {
                key = value;
                //If Search controller is being set before Key is set
                if (searchController != null){searchController.setComponentControllerMap(key, this);}
            }
        }
    }
    // This holds instance of search controller class
    public HUMSearchController searchController 
    {
        get;
        set 
        {
            if (NULL != value) 
            {
                searchController = value;
                //If Key is set before Search Controller is being Set
                if (!String.ISBLANK(key)) {searchController.setComponentControllerMap(key, this);}
            }
        }
    }
    public Map < Integer, Account > mapRecordset 
    {
        get 
        {
            Map < Integer, Account > mapAccList = new Map < Integer, Account > ();
            if (NULL != mapRecordData) 
            {
                mapRecordSetJson = new Map < Integer, String > ();
                for (integer i = iCounter; i < iCounter + iPagesize; i++) 
                {
                    if (mapRecordData.get(i) != null) 
                    {
                        mapAccList.put(i, mapRecordData.get(i));
                        mapRecordSetJson.put(i, JSON.Serialize(mapRecordData.get(i)));
                    }
                }
            }
            if (mapRecordData != null) {iRecordMapSize = mapRecordData.size();}
            return mapAccList;
        }
        set;
    }
    public Map<Id, Boolean> mapRecordHaveAccess {get;set;}
    /*
     * Method Name : insertDataFromExternalSystem
     * Description : This method will be used when data is coming from external system.
     * Return type : ID
     * Parameter   : String  -Account Json version
     */
    public virtual ID insertDataFromExternalSystem(String accountJson) 
    {
        try 
        {   
            Map<String,String> mapEncryptFields = new Map<String,String>();
            Account newAcc = (Account) JSON.Deserialize(accountJson, Account.Class);
            newAcc = getEmailForAgentAccount(newAcc);            
            newAcc=HUMUtilityHelper.getEncryptedAccount(newAcc);        
            List<Account> lstexacc = [Select Id, Name from Account WHERE  Account_External_Id__C = :newAcc.Account_External_ID__c];         
            //Conditional Logic that checks the Group Record Type and Insert or Just return the ID          
            if (newAcc!=null)
            {            
            if (newAcc.recordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.Label.HUM_Group_Account_Record_Type).getRecordTypeId())
            {
            if (lstexacc.size() > 0)
            {
            return lstexacc[0].id;
            }            
            else if(lstexacc.size() == 0)
            {
            List <Account> InsertAccounts = new List <Account>();  
            InsertAccounts= HUMInteractionMemberDMLHelper.InsertAccountForInteractionID(new List<Account>{newAcc});
            if (InsertAccounts!=null)
            return insertAccounts[0].id;
            }
            }            
            else 
            { 
            List < Account > upsertAccounts = new List < Account >();                
            upsertAccounts= HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(new List<Account>{newAcc});                
            if (upsertAccounts!=null)               
            return upsertAccounts[0].id;
            }
            }
        } 
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchComponentBaseHelper', 'insertDataFromExternalSystem');
        }
        return null;
    }
    
    /*
     * Method Name : upsertConsumerTaxIds
     * Description : Method to upsert Consumer Tax Id records for an Account.
     * Return type : boolean
     * Parameter   : ID,Map<String,String>
     */
    public boolean upsertConsumerTaxIds(ID accID,Map<String,String> mapNormalAndEncryptedIds ) 
    {
        try
        {           
            if(accID !=null && mapNormalAndEncryptedIds !=null && mapNormalAndEncryptedIds.keyset().size()>0)
            {   
                List<ConsumerId__c> lstConsumerRecords = HUMUtilityHelper.getEncryptedConsumerIds(accID,mapNormalAndEncryptedIds);
                if(lstConsumerRecords!=null)
                {
                    lstConsumerRecords= HUMInteractionMemberDMLHelper.upsertConsumerIDofAccount(lstConsumerRecords);
                    if (lstConsumerRecords!=null) return true; 
                }                                           
            }
        }

        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchComponentBaseHelper', 'upsertConsumerTaxIds');
        }
        return false;  
    }

    /*
     * Method Name : next
     * Description : Method for Pagination - Next button functionality in all searches
     * Return type : PageReference
     * Parameter   : NA
     */
    public virtual PageReference next() 
    {
        iCounter = iCounter + iPagesize;
        return null;
    }

    /*
     * Method Name : last
     * Description : Method for Pagination - Last button functionality in all searches
     * Return type : PageReference
     * Parameter   : NA
     */     
    public virtual PageReference last() 
    {
        if (mapRecordData != null) {iCounter = mapRecordData.size() - math.mod(mapRecordData.size(), iPagesize);}
        return null;
    }

    /*
     * Method Name : first
     * Description : Method for Pagination - First button functionality in all searches
     * Return type : PageReference
     * Parameter   : NA
     */
    public virtual PageReference first() 
    {
        iCounter = 0;
        return null;
    }

    /*
     * Method Name : previous
     * Description : Method for Pagination - Previous button functionality in all searches
     * Return type : PageReference
     * Parameter   : NA
     */
    public virtual PageReference previous() 
    {
        iCounter = iCounter - iPagesize;
        return null;
    }

    /*
     * Method Name : gethasNext
     * Description : Method for enabling or disabling Pagination - Next and Last button functionality in all searches
     * Return type : boolean
     * Parameter   : NA
     */
    public virtual boolean gethasNext() 
    {
        if (NULL != mapRecordData) 
        {
            if (iCounter + iPagesize < mapRecordData.size()) {return true;} 
            else {return false;}
        } 
        else {return false;}
    }

    /*
     * Method Name : gethasPrevious
     * Description : Method for enabling or disabling Pagination - Previous and First button functionality in all searches
     * Return type : boolean
     * Parameter   : NA
     */
    public virtual boolean gethasPrevious() 
    {
        if (iCounter <> 0) {return true;} 
        else {return false;}
    }

    /*
     * Method Name : getEmailForAgentAccount
     * Description : Checks if Account is Agent, if so changes Email to null if invalid
     * Return type : Account
     * Paramater   : Account
     */
     public static Account getEmailForAgentAccount(Account newAcc)
     {            
         if(newAcc.recordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.Label.HUMAgentBrokerRecordTypeName).getRecordTypeId())
         {
                if(newAcc.Work_Email__c != null)
                {
                    if(!valdiateEmail(newAcc.Work_Email__c.trim()))
                    {
                         newAcc.Work_Email__c = null;  
                    }
                }                
          }
          return newAcc;            
     }
    
    /*
     * Method Name : valdiateEmail
     * Description : Valdiates the Email values of each Account & returns true if valid email 
     * Return type : Boolean
     * Paramater   : String emailId
     */
     public static boolean valdiateEmail(String emailId)
     {         
         boolean match = false;         
         Matcher emailMatcher = Pattern.compile(System.Label.HUMValidAccountEmailRegExp).matcher(string.valueof(emailId));         
         if(emailMatcher.matches())
         {
            match = true;
         }                 
         return match;
     }
}