/*******************************************************************************************************************************
Apex Class Name : HUMSearchComponentBaseHelperV2
Version         : 1.0
Created On      : 05/21/2014
Function        : This class serves as base class for all the search component class
                  A detailed analysis was done on date'23/08/2018' by developer "Vijaya Koriprolu" on Sharing without option for this class.
                  During the analysis it was found that Changing the class to with sharing will affect the dependent without sharing classes. They are HUMGroupSearchControllerV2,HUMMemberSearchControllerV2 and HUMMemberSearchEnrollmentControllerV2.
                  This was further discussed with Architects Jeremy And got approval to keep the "Without Sharing" mode of this class as it Is.
Test Class      : HUMSearchComponentBaseHelperV2Test
Modification Log: 
 * Developer Name              Code Review                   Date                      Description
 *-------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Harshith Mandya                29217                   07/31/2015                  Removed abstraction layer - extending HUMSearchFieldHelper  
 * Sharan Shanmugam               29738                   02/05/2016                  Changed logic in last method to fix defect #214831.0001
 * Praveen Kumar Parimi           27872                   11/16/2015                  Modified insertDataFromExternalSystem to return or insert Group Account Information.
 * Vishal Verma                   30238                   03/01/2016                  Merged the code changes of V1 and V2 versions for insertDataFromExternalSystem method. 
 * Sunil Bukya                                            07/23/2018                  REQ - 362070 - Implemented Group Name platform encryption changes. 
 * Ajay Chakradhar                                        05/31/2021                  REQ - 1755482 - Replacing constants to improve performance
 **********************************************************************************************************************************************************************/
 
public virtual class HUMSearchComponentBaseHelperV2 
{
    /*
     * Method Name : HUMSearchComponentBase
     * Description : Constructor for the class
     * Return type : NA
     * Paramater   : NA
     */
    public HUMSearchComponentBaseHelperV2() 
    {
        mapRecordData = new Map<Integer,Account>();
        iRecordMapSize = 0;
        iPagesize = 0;
        iCounter = 0;
    }
    Public String sHUMAgentBrokerRecordTypeName = 'Agent/Broker';
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
    public HUMSearchControllerV2 searchController 
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
            Account newAcc = (Account) JSON.Deserialize(accountJson, Account.Class);
            newAcc = getEmailForAgentAccount(newAcc);
            List<Account> lstexacc = [Select Id, Name from Account WHERE  Account_External_Id__C = :newAcc.Account_External_ID__c Limit 1];         
            //Conditional Logic that checks the Group Record Type and Insert or Just return the ID          
            if (newAcc != null)
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
                        if (InsertAccounts!=null) return insertAccounts[0].id;
                    }
                }            
                else 
                { 
                    List < Account > upsertAccounts = new List < Account >();                
                    upsertAccounts= HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(new List<Account>{newAcc});                
                    if (upsertAccounts!=null) return upsertAccounts[0].id;
                }
            }
        } 
        catch (Exception e) 
        {   
           
            HUMExceptionHelper.logErrors(e,'HUMSearchComponentBaseHelper', 'insertDataFromExternalSystem');
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
        if (mapRecordData != null) 
        {
            if(math.mod(mapRecordData.size(), iPagesize) == 0)  iCounter = mapRecordData.size() - math.mod(mapRecordData.size(), iPagesize) - iPagesize;
            else iCounter = mapRecordData.size() - math.mod(mapRecordData.size(), iPagesize);
        }
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
     public  Account getEmailForAgentAccount(Account newAcc)
     {            
         if(newAcc.recordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get(sHUMAgentBrokerRecordTypeName).getRecordTypeId())
         {
            if(!HUMUtilityHelper.valdiateEmail(newAcc.Work_Email__c.trim()))
            {
                 newAcc.Work_Email__c = null;  
            }
                               
          }
          return newAcc;            
     }
}