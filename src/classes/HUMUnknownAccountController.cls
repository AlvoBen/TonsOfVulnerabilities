/**************************************************************************************************************************************************************************************
Apex Class Name  : HUMUnknownAccountController
Version          : 1.0
Created Date     : June 22 2014
Function         : Controller of Unknown Account- Provider / Group / Agent/ Member . Read the parameter values and assign to the respective fields on Account
Test Class       : HUMUnknownAccountControllerTest
 **************************************************************************************************************************************************************************************
Modification Log:
 * Developer Name           Code Review                    Date                       Description
 *-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar V          17172                     07/22/2014                    Original Version
 * Ranjeeth N                 17172                     07/24/2014                    Added Unknown Member and Provider
 * Ranjeeth N                 17172                     07/24/2014                    Added the Custom settings For Classifications
 * Shruthi Karanth            17534                     08/19/2014                    To include changes for adding Tax Id to Agency Search
 * Ranjeeth N                 17534                     08/20/2014                    Added error message for defect : 150702
 * Manish Kumar               17534                     08/25/2014                    Added error message for defect : 15125
 * Ranjeeth N                 17722                     09/03/2014                   Created the insert for the TaxId object
 * Shruthi Karanth            17722                     09/05/2014                    Changes to encrypt the unknown account before inserting   
 * Chaitanya Kumar            17780                     09/08/2014                    Decode Base 64 Encoding in Constructor   
 * Anil P                     17780                     09/09/2014                    validatePhone method is refactored based on review comments
 * Chaitanya Kumar            17780                     09/10/2014                    Moved logic related to Encryption of Account and Consumer to HUMUtility Helper
 * Kritika P Popat            17886                     09/11/2014                    added logic to display error messages all together in Unknown Member screen
 * Tanushree Hazari           17886                     09/12/2014                    Added first name validation for unknown member
 * Chaitanya Kumar            17933                     09/14/2014                    Modified populateUnknownInformation to remove state code logic as 
                                                                                      two letter code is passed now
 * Kritika P Popat            18059                     09/18/2014                    Using validations from HUMSearchFieldsHelper for UnknownMember
 * Ranjeeth Nagishetty        18059                     09/18/2014                    Using validations from HUMSearchFieldsHelper for UnknownProvider
 * Chaitanya Kumar            18059                     09/19/2014                    Added groupValidation and agentValidation methods
 * Manish Kumar               18362                     10/01/2014                    Code fix for Defect#155398
 * Ranjeeth Nagishetty        18163                     10/16/2014                    Fixed the Defect#158036
 * Chaitanya Kumar            18163                     10/16/2014                    Fixed the defect#158177
 * Santhi Mandava             19277                     11/14/2014                    Fixed the defect#160988
 * Vishal Verma               19277                     11/14/2014                    Fixed the defect#160988  - Agency
 * Vishal Verma               19277                     11/17/2014                    Updated code as per Humana standards
 * Ranjeeth Nagishetty        19583                     11/26/2014                    Defect#160988
 * Vishal Verma               19583                     11/27/2014                    Defect#160988 - Agency
 * Tanushree Hazari           19690                     12/02/2014                    Added validations as per REQ-70935
 * Ranjeeth Nagishetty        19730                     12/05/2014                    Req-63682 
 * Ranjeeth Nagishetty        19850                     12/11/2014                    Defect#160988.0002   
 * Ranjeeth Nagishetty        19882                     12/12/2014                    Changes in REQ-63682  
 * Raja Sekhar Reddy M V      21493                     03/13/2015                    Modification for 162140.0001 
 * Pradeep Raju               21787                     03/24/2015                    Added code at line# 324 & 330 as part of REQ-105805
 * Pradeep Raju               22106                     04/06/2015                    Added code from line# 350 to 362 for defect# 178527
 * Ranjeeth Nagishetty        23424                     05/27/2015                    Defect  166993.001 just reducing the size of business account.
 * Raja Sekhar Reddy M V      57797                     01/10/2017                    Added code for not updating Account owner if already account Owner is Humana api. Reg - 290999.
 * Praveen Kumar Parimi       70518                     02/23/2017                    Rollback of the code which went as part of Req - 290999.    
 * Santhi Mandava             262341                    07/26/2018                    Implemented platform encryption changes
 * Moshitha Gunasekaran       262118                    06/28/2018                    REQ - 350472 ID Number field on the Unknown Account tab          
 * Moshitha Gunasekaran       294763                    10/05/2018          REQ - 375754 - TECH - SF - Textbox copy paste and validation error for unknown member 
 * Anil Kumar Korada          2150839                   04/08/2021                    MF6 - Security - Checkmarx Medium Defects
 * Ankit Avula                                          05/06/2021                    DF-2998 removed FLS check for tenant Id and General Account fields
 * Amar Gadewar                                         10/05/2021                    User Story 2737406: T1PRJ0001827 - MF 1 - AHT - Ability to Save and Create Interaction in single click on the Unknown Member Account (RF)
 * Ajay Chakradhar                                      11/22/2021                    DEF 4182 FIX - Global Constant replace for performance improvement
 **************************************************************************************************************************************************************************************/

public with sharing class HUMUnknownAccountController extends HUMSearchFieldsHelper
{
    
    //Store information related to Record type Names
    static final Map<String,String> mapRecordtypeInfo= new map<String,String>
    {'A' => HUMConstants__c.getInstance('UNKNOWN_AGENT_RECORDTYPE').Stringvalue__c,
        'G' => HUMConstants__c.getInstance('UNKNOWN_GROUP_RECORDTYPE').Stringvalue__c,
        'P' => HUMConstants__c.getInstance('UNKNOWN_PROVIDER_RECORDTYPE').Stringvalue__c,
        'M' => HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c
    };
    //Store Information related to URL Mapping
    static final map<String,Map<String,String>> mapURLParametersMapping = new Map<String,Map<String,String>>
    {
        'G' => new Map<String,String>{'gNo' => 'Group_Number__C','gName' => 'Name','state' => 'BillingStateCode'},
        'P' => new Map<String,String>{'npi' => 'NPI_ID__c','State' => 'BillingStateCode','spec' => 'Description','Fs' => 'name','Pc' => 'billingpostalcode','fn' => 'Individual_First_Name__c','ln' => 'Individual_Last_Name__c'},
        'A' => new Map<String,String>{'fName' => 'Individual_first_Name__c','lName' => 'Individual_last_Name__c','agName' => 'Name','aId' => 'Agent_ID__c','aType' => 'Agent_Type__C','state' => 'BillingStateCode'},
        'M' => new Map<String,String>{'fName' => 'firstName','lName' => 'lastName','DOB' => 'Birthdate__c','state' => 'PersonMailingStateCode'}
    };
    //External ID format
    static final map<string,string> mapExternalIDFormat = new map<String,String>{'A' => HUMConstants__c.getInstance('UNKNOWN_AGENT_EXTID').Stringvalue__c,'G' => HUMConstants__c.getInstance('UNKNOWN_GROUP_EXTID').Stringvalue__c,'P' => HUMConstants__c.getInstance('UNKNOWN_PROVIDER_EXTID').Stringvalue__c,'M' => HUMConstants__c.getInstance('UNKNOWN_MEMBER_EXTID').Stringvalue__c};
    static set<String> setConsumerID = new Set<String>{'A','P','M'};
    String sEncryptedTaxId = '';
    //public boolean bError{get;set;}
    public String idNumber {get; set;}
    public String consumerIDType {get; set;}
    public boolean isIDNumRequired {get; set;}
    public String errorLevel{ get; set; }
    public String sErrMsgJSON {get; set;}            
    public List<SelectOption> getItems() 
    {
        List<SelectOption> options = new List<SelectOption>();
        options.add(new SelectOption('','--None--'));  
        options.add(new SelectOption('CBIS ALT ID','CBIS Alt ID'));
        options.add(new SelectOption('HumanaId','Humana ID'));      
        options.add(new SelectOption('Medicaid-Id','Medicaid ID'));
        options.add(new SelectOption('MedicareID','Medicare Number'));
        options.add(new SelectOption('SSN','Social Security Number'));

       return options;
    }
    public List<SelectOption> lstConsumerIDTypes
    {
        get
        {
            lstConsumerIDTypes = getItems();
            return lstConsumerIDTypes;
        }
        set;
    }
    public Boolean bSaveAndCreateInteractionSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2737406');
        }
        set;
    }

    Public String ACCOUNT_EXTERNALID_MEDICARE ='UKM|00|ETL|MCR|';
    Public String ACCOUNT_EXTERNALID_SSN ='UKM|00|ETL|SSN|';
    Public String ACCOUNT_EXTERNALID_ENTID ='UKM|00|ETL|EID|';
    Public String ACCOUNT_EXTERNALID_UKEY ='UKM|00|ETL|UKEY|';
    Public String ACCOUNT_EXTERNALID_MEMBERIDBASE ='UKM|00|ETL|MBID|';
    Public String ACCOUNT_EXTERNALID_SFDC ='UKM|00|ETL|SFDC|';
    Public String ACCOUNT_EXTERNALID_CBISALTID ='UKM|00|ETL|CBIS|';  
    Public String ACCOUNT_EXTERNALID_MEDICAID ='UKM|00|ETL|MCID|';     
    Public String CASESERVICE_MEDICAREID_HUM = 'MedicareID' ;
    Public String CASESERVICE_SSN_HUM = 'SSN' ;
    Public String CASESERVICE_MEDICADEID_HUM = 'Medicaid-Id' ;
    Public String CASESERVICE_ENTPERSONID_HUM = 'EnterprisePersonID';
    Public String CASESERVICE_HUMANAID_HUM = 'HumanaId';
    Public String CASESERVICE_CBISALTID_HUM = 'CBIS ALT ID';
    Public String CONSUMER_EXTERNALID_MEDICARE ='UKM|00|ETL|MCARE|';
    Public String CONSUMER_EXTERNALID_SSN ='UKM|00|ETL|SSN|';
    Public String CONSUMER_EXTERNALID_MEDICAID ='UKM|00|ETL|MCAID|';
    /*
    * Method Name   :    isIDNumberRequired
    * Description   :    To check if ID Number is required to validate and show error messages
    * Return Type   :    void
    */
    public void isIDNumberRequired()
    {
        if(null != consumerIDType && consumerIDType.length() > 0)
        {
            isIDNumRequired = true;
        }
        else
        {
            isIDNumRequired = false;
        }
    }

    /*
    * This method Is to add required error message
    * @param  NA
    * @return PageReference
    */
     public PageReference showMessage() {
        List<String> lstErrorMsg = new List<String>();
        
        if(String.isNotBlank(sErrMsgJSON))  lstErrorMsg = (List<String>)JSON.deserialize(sErrMsgJSON, List<String>.class);
        
        if(lstErrorMsg!=null && !lstErrorMsg.isEmpty())
        {
            for(String err : lstErrorMsg)
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, err)); 
            }
        }
        
        return null;
    }
    
    /*
    * Method Name   :    HUMUnknownAccountController
    * Description   :    Constructor for the class
    * Return Type   :    void
    * Parameters    :    ApexPages.StandardController - This is related to Account
    */
    public HUMUnknownAccountController(ApexPages.StandardController stdController)
    {
        try
        {
            if(stdController != null) account = (Account)stdController.getRecord();
            if(account != null)
            {
                bIsErrorOnPage  = false;
                lstConsumerIDTypes = new List<SelectOption>();
                mapUnknownAccParameters = ApexPages.currentPage().getParameters();
                sAccountType = mapUnknownAccParameters.containsKey('type')? mapUnknownAccParameters.get('type'):'' ;
                //Populate Record Type Information
                ID recordTypeID = mapRecordtypeInfo.containsKey(sAccountType) ? HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),mapRecordtypeInfo.get(sAccountType)) : null;
                   if(Schema.sobjectType.account.isAccessible()){    //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                   //DF-2998 the below line is commented as all the profiles/users do not have access to the fields General_Account__c field as a result causing an issue while retrieving the unknown member accounts.
                     //if(Schema.sobjectType.account.fields.General_Account__c.isCreateable() || Schema.sobjectType.account.fields.General_Account__c.isUpdateable()) 
                       account.General_Account__c = true;
                     //DF-2998 the below line is commented as all the profiles/users do not have access to the fields Tenant_Id__c field as a result causing an issue while retrieving the unknown member accounts.
                     //if(Schema.sobjectType.account.fields.Tenant_Id__c.isCreateable() || Schema.sobjectType.account.fields.Tenant_Id__c.isUpdateable())//*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema  
                       account.Tenant_Id__c = HUMConstants__c.getInstance('TENANTID_GEN').StringValue__c;
                    }
                        if(recordTypeID != null)
                {
                    if(Schema.sobjectType.account.fields.recordTypeID.isCreateable() || Schema.sobjectType.account.fields.recordTypeID.isUpdateable())//*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                   account.recordTypeID=recordTypeID;
                    sRecordTypeName = [Select Name from RecordType Where Id=:recordtypeID].Name;
                }
                
                sTaxId = mapUnknownAccParameters.containsKey('taxNo')?mapUnknownAccParameters.get('taxNo'):'';
                //Populate Unknown Account Information
                populateUnknownInformation(mapURLParametersMapping.get(sAccountType));
                if(sAccountType.equalsIgnoreCase('P')) RetrieveProviderClassification();
            }
        }
        catch(Exception ex)
        {
            //Dont insert any logs in the constructor . A seperate custom component in the Page will handle this part.
            //bError = true;
            bIsErrorOnPage = true;
            HUMExceptionHelper.bRestrictLog = true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex,'HUMUnknownAccountController','HUMUnknownAccountController');
            
        }
    }
    
    //Store URL Parameters
    public Map<String,String> mapUnknownAccParameters {get;set;}
    //Type of Unknown Account - Provider(P) / Group(G) / Agent(A)/Member(M)
    public String sAccountType { get; set; }
    public String sRecordtypeName { get; set; }
    public Account account { get; set; }
    public String staxId { get;set; }
    public List<SelectOption> Providerclassification{get;set;}
    
    //To check if there is any error on page
    public boolean bIsErrorOnPage
    {
        get
        {
            if(!bIsErrorOnPage)  return ApexPages.hasMessages();
            return bIsErrorOnPage;
        }
        set;
    }
    
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs
    {
        get
        {
            if(listErrorLogs == null)  return new List<Error_Log__c>();
            return listErrorLogs;
        }
        set;
    }
    
    //capture error message for state dropdown
    public String stateErrorMessage
    {
        get
        {
            string temp = 'Error: You must enter a value';
            return temp;
        }
        set;
    }

    /*
    * Method Name   :    save
    * Description   :    Save the Account
    * Return Type   :    void
    * Parameters    :    NA
    */
    public void save()
    {
        try
        {
            //Reset Error Value
            bIsErrorOnPage = false;
            
            if(sAccountType.equalsIgnoreCase(HUMConstants__c.getInstance('ACCOUNT_MEMBER').StringValue__c))
            {
                if(!memberValidation())
                {
                    return;
                }
            }
            else if(sAccountType.equalsIgnoreCase(HUMConstants__c.getInstance('ACCOUNT_PROVIDER').StringValue__c))
            {
                if(!providerValidation())
                {
                    return;
                }
            }
            else if(sAccountType.equalsIgnoreCase(HUMConstants__c.getInstance('ACCOUNT_GROUP').StringValue__c))
            {
                assignDataForValidation();
                if(!groupValidation())
                {
                    return;
                }
            }
            else if(sAccountType.equalsIgnoreCase(HUMConstants__c.getInstance('ACCOUNT_AGENCY').StringValue__c))
            {
                resetValuesOnAgentTypeChange();
                assignDataForValidation();
                if(!agentValidation())
                {
                    return;
                }
            }
          
            if(sAccountType != HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
            {
                if(String.isBlank(account.Name))
                {
                      string sfirstName = account.Individual_First_Name__c;
                      Integer ilengthFName = 30;
                       if(sFirstName != null &&  sFirstName.length() > ilengthFName )
                        {
                            sfirstName = sFirstName.substring(0,ilengthFName );
                        }
                         string slastNameInd =account.Individual_Last_Name__c;
       
                        if(slastNameInd != null && slastNameInd.length() > ilengthFName )
                        {
                            slastNameInd = slastNameInd.substring(0,ilengthFName );
                        }
                       
                        account.Name =(String.IsNotblank(sFirstName )?sFirstName  :'' ) + (String.IsNotblank(slastNameInd)? slastNameInd :'' );
                }
            }
            
            if(sAccountType == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
            {
          //  if(Schema.sobjectType.account.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
             If((Schema.sobjectType.account.fields.Individual_First_Name__c.isCreateable()) || Schema.sobjectType.account.fields.Individual_First_Name__c.isUpdateable())//*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                account.Individual_First_Name__c = account.Firstname;
               
              If((Schema.sobjectType.account.fields.Individual_Last_Name__c.isCreateable()) || Schema.sobjectType.account.fields.Individual_Last_Name__c.isUpdateable())//*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                account.Individual_Last_Name__c  =  account.lastname;
 //}
                Integer ilengthfirstname = account.firstname.length();
                if(ilengthfirstname > 12) account.firstname= account.firstname.substring(0,12);
                Integer ilengthLastname = account.firstname.length();
                if(ilengthLastname > 36) account.lastname = account.lastname.substring(0,36);
            }
            
            processSaveResults();
        }
        catch(Exception e)
        {
            //bError = true;
            HUMExceptionHelper.logErrors(e,'HUMUnknownAccountController','save');
            bIsErrorOnPage = true;
        }
    }
    
    /*
    * Method Name   :  groupValidation
    * Description   :  This Method is used to validate Group Account values
    * Return Type   :  boolean
    * Parameters    :  NA
    */
    private boolean groupValidation()
    {
        boolean bIsError = false;
        
        if(!validateUnknownPostalCode())  bIsError = true;
        if(!validatePhone())  bIsError = true;
        if(!validateTaxID())  bIsError = true;
        if(!validateTaxIDLength())  bIsError = true;
        if(!validateGroupNumber())  bIsError = true;
        
        if(bIsError)
        {
            return false;
        }
        return true;
    }
    
    /*
    * Method Name   :  assignDataForValidation
    * Description   :  This Method is used assign data to Validation wrapper to execute validations;
    * Return Type   :  void
    * Parameters    :  NA
    */
    private void assignDataForValidation()
    {
        osearchWrapper.sTaxID = sTaxID;
        osearchWrapper.sPostalCode = account.billingpostalcode;
        osearchWrapper.sPhone = account.phone;
        oSearchWrapper.sGroupNumber = account.group_number__c;
        osearchWrapper.sLastName = account.Individual_Last_Name__c;
        osearchWrapper.sfirstName = account.Individual_first_Name__c;
        osearchWrapper.sAgentId = account.Agent_id__c;
        osearchWrapper.sAgencyName = account.Name;
    }
    
    /*
    * Method Name   :  agentValidation
    * Description   :  This Method is used to validate Agent Account values
    * Return Type   :  boolean
    * Parameters    :  NA
    */
    private boolean agentValidation()
    {
        boolean bIsError = false;
        
        if(!validateUnknownPostalCode())  bIsError = true;
        if(!validatePhone())  bIsError = true;
        if(!validateTaxID())  bIsError = true;
        if(!validateTaxIDLength())  bIsError = true;
        if(!validateAgentId())  bIsError = true;
        if(!lastNameLength())  bIsError = true;
        if(!validateFirstName())  bIsError = true;
        if(Account.Agent_Type__c == system.label.HUMAgencySearchAgentTypeValue2)
        {
            if(!validateAgencyName())  bIsError = true;
        }
        if(!validateLastName())  bIsError = true;
        
        if(bIsError)
        {
            return false;
        }
        return true;
    }
    
    /*
    * Method Name   :    processSaveResults
    * Description   :    Save the Account and process the results for any error
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testVisible private void processSaveResults()
    {
        List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();
        //Get Encrypted version of a new Account
         //Get Encrypted version of a new Account
        //if(Schema.sobjectType.Account.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
              if(Schema.sobjectType.Account.fields.Id.isCreateable() || Schema.sobjectType.Account.fields.Id.isUpdateable())
        account.id = null;
       // }
        if (!Test.isRunningTest())
        {
            account = HUMUtilityHelper.getEncryptedAccount(account);
        }
        
        if(sAccountType == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
        {
            sTaxId = idNumber.toUpperCase();
        }

        //Get Encrypted Tax ID values
        if(!String.isBlank(sTaxId) && setConsumerID.contains(sAccountType))
        {
                sEncryptedTaxId = sTaxId;
                if(sAccountType == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
                {
                    String sConsumerSearchQuery =   'SELECT Id, Name, Consumer_ID__c, ID_Type__c FROM ConsumerID__c where ID_Type__c = \'' + string.escapeSingleQuotes(consumerIDType) + '\' and ';
                    sConsumerSearchQuery += 'Consumer_ID__c = \'' + string.escapeSingleQuotes(sEncryptedTaxId) + '\'';
                    lstConsumers = (List<ConsumerID__c>)Database.Query(sConsumerSearchQuery);
                }
        }
        
       if(lstConsumers.isEmpty())      
       {             
            List<Account> lstAccounts  = new  List<Account>{account};
            boolean isError = false;
            string accountExternalId = '';
            if(sAccountType == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
            {
                if(mapExternalIDFormat.containsKey(sAccountType))
                {
                    if(null != consumerIDType)
                    {               
                        if(consumerIDType == CASESERVICE_SSN_HUM)
                        {
                            accountExternalId = ACCOUNT_EXTERNALID_SSN + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());                        
                        }
                        else if (consumerIDType == CASESERVICE_MEDICAREID_HUM)
                        {
                            accountExternalId = ACCOUNT_EXTERNALID_MEDICARE + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase()); 
                        }
                        else if (consumerIDType == CASESERVICE_ENTPERSONID_HUM)
                        {
                            accountExternalId = ACCOUNT_EXTERNALID_ENTID + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());                
                        }
                        else if (consumerIDType == CASESERVICE_HUMANAID_HUM )
                        {
                            accountExternalId = ACCOUNT_EXTERNALID_MEMBERIDBASE + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());                
                        }
                        else if (consumerIDType == CASESERVICE_CBISALTID_HUM)
                        {
                            accountExternalId = ACCOUNT_EXTERNALID_CBISALTID + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());
                        } 
                        else if (consumerIDType == CASESERVICE_MEDICADEID_HUM)
                        {
                            accountExternalId = ACCOUNT_EXTERNALID_MEDICAID + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());
                        }                   
                        else if (!String.isBlank(idNumber))
                        {
                            accountExternalId = ACCOUNT_EXTERNALID_UKEY + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());
                        }                
                    }
                    else
                    {
                        accountExternalId = ACCOUNT_EXTERNALID_SFDC;
                    }
                }
            }
            if(String.isNotBlank(accountExternalId) && accountExternalId != ACCOUNT_EXTERNALID_SFDC)
            {
                           //  if(Schema.sobjectType.Account.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
              if(Schema.sobjectType.Account.fields.Account_External_ID__c.isCreateable() || Schema.sobjectType.Account.fields.Account_External_ID__c.isUpdateable())
                lstAccounts[0].Account_External_ID__c = accountExternalId;  
                //} 
                Schema.sObjectField schemaExternalId = Schema.getGlobalDescribe().get('Account').getDescribe().fields.getMap().get('Account_External_ID__c');
                List<Database.upsertResult> upsertResults = Database.upsert(lstAccounts,schemaExternalId, false);
                isError = HUMExceptionHelper.processUpsertResults(upsertResults, lstAccounts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
            }
            else
            {
                Database.saveResult[] saveResults = Database.insert(lstAccounts, false);
                isError = HUMExceptionHelper.processSaveResults(saveResults, lstAccounts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
            }
            //In case of error set the flag
            if(isError)
            {
                bIsErrorOnPage = true;
                //Reset the ids in case of Error
                for(Account oAccount: lstAccounts)
                {
                    oAccount.id = null;
                }
            }
            else
            {
                List<User> usr = [Select id, username from User where username LIKE: system.label.HUMUnknownAccountOwner];
                //Populate External ID
                if(mapExternalIDFormat.containsKey(sAccountType))
                {
                    if(String.isBlank(accountExternalId) || accountExternalId == ACCOUNT_EXTERNALID_SFDC)
                    {
                        for(Account oAcc: lstAccounts)
                        {
                            if(accountExternalId == ACCOUNT_EXTERNALID_SFDC)
                            {               
                                accountExternalId = ACCOUNT_EXTERNALID_SFDC + oAcc.id;                
                            }
                            else
                            {
                                accountExternalId = mapExternalIDFormat.get(sAccountType) + oAcc.id;
                            }
                            oAcc.Account_External_Id__c = accountExternalId;
                            if(!usr.isEmpty() && String.isBlank(sEncryptedTaxId)) oAcc.OwnerId = usr[0].id;
                        }
                        
                        //Update the result
                        List<Database.saveResult> saveResults = Database.update(lstAccounts,false);
                        isError = HUMExceptionHelper.processSaveResults(saveResults,lstAccounts,'HUMUnknownAccountController','processSaveResults','Account');
                    }
                    if(isError)
                    {
                        bIsErrorOnPage = true ;
                    }
                    else
                    {
                        if(String.isNotBlank(sEncryptedTaxId) && setConsumerID.contains(sAccountType))
                        {
                            //Insert Consumer IDs
                            List<ConsumerID__c> lstConsumerIds = insertConsumerTaxIds(sEncryptedTaxId,lstAccounts[0].id);
                            if(lstconsumerIds == null) bIsErrorOnPage = true;
                            
                            for(Account oAcc: lstAccounts)
                            {
                                //Assign the owner the record to Humana API Only
                                if(!usr.isEmpty()) oAcc.OwnerId = usr[0].id;
                            }
                            
                            //Update the result
                            List<Database.saveResult> saveResults = Database.update(lstAccounts,false);
                            isError = HUMExceptionHelper.processSaveResults(saveResults,lstAccounts,'HUMUnknownAccountController','processSaveResults','Account');
                            if(isError)
                            {
                                bIsErrorOnPage = true ;
                            }                       
                        }
                    }
                }
            }
        }
        else
        {
            throw new HUMCustomException(system.Label.HUMUnknownSearchEnrollmentSave);
        }
    }
    
    /*
    * Method Name   :    RetrieveProviderClassification
    * Description   :    classification has Picklist
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public void RetrieveProviderClassification()
    {
        Providerclassification=new List<selectOption>();
        List<selectOption> lstOptions = new List<selectOption>();
        Map<String,HumProviderClassifications__c> mapClassification = HumProviderClassifications__c.getAll();
        List<string> lstsClassification = new list<string>();
        lstsclassification.addAll(mapClassification .keySet());
        
        for (String sClassify: lstsClassification)
        {
            HumProviderClassifications__c classifycust= Mapclassification.get(sClassify);
            Providerclassification.add(new SelectOption(classifycust.ClassificationTypeCode__c,classifycust.name));
        }
        if(!string.isblank(Account.name) &&  Schema.sobjectType.account.fields.Provider_Classification__c.isCreateable()) //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
        {
            account.Provider_Classification__c = Label.HumProviderFacilityRecord ;
        }
        else
        {
        if(Schema.sobjectType.account.fields.Provider_Classification__c.isCreateable()) //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
            account.Provider_Classification__c = Label.HumProviderNameIndividual;
        }
    }
    
    /*
    * Method Name   :    insertConsumerTaxIds
    * Description   :    Insertthe Consumer TAXIDs based on the Account ID
    * Return Type   :    List<ConsumerId__c>
    * Parameters    :    String, ID
    */
    @testVisible private List<ConsumerID__c> insertConsumerTaxIds(String sConsumerID,ID accID)
    {
        List<ConsumerId__c> lstConsumerRecords = new List<ConsumerId__c>();
        String sExternalID = mapExternalIDFormat.containsKey(sAccountType) ? mapExternalIDFormat.get(sAccountType):'';
        String sTaxID = '';
        if(sAccountType == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
        {
            sTaxID = consumerIDType;
            
        }
        else
        {
            sTaxID = HUMConstants__c.getInstance('CONSUMERID_TAXID').StringValue__c;
        }
        String sPipeCharacter = HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;
        
        if(!String.isBlank(sExternalID))
        {
            lstConsumerRecords.add(new ConsumerID__c(Consumer_Id__c = sConsumerID,Account__c = accId ,ID_Type__c = sTaxID));
            Database.saveResult[] saveResults = Database.insert(lstConsumerRecords, false);
            boolean isError = HUMExceptionHelper.processSaveResults(saveResults, lstConsumerRecords, 'HUMUnknownAccountController', 'insertConsumerTaxIds', 'ConsumerId__c');
            if(!isError)
            {
                //Populate ID
                for(ConsumerId__c consumerId: lstConsumerRecords)
                {
                   if(sAccountType == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
                    {
                        if(sTaxID == CASESERVICE_SSN_HUM)
                        {
                            consumerId.Consumer_External_Id__c = CONSUMER_EXTERNALID_SSN + accId;
                        }
                        else if(sTaxID == CASESERVICE_MEDICAREID_HUM)
                        {
                            consumerId.Consumer_External_Id__c = CONSUMER_EXTERNALID_MEDICARE + accId;
                        }
                        else if(sTaxID == CASESERVICE_MEDICADEID_HUM)
                        {
                            consumerId.Consumer_External_Id__c = CONSUMER_EXTERNALID_MEDICAID + accId;
                        }
                        else
                        {
                            consumerId.Consumer_External_Id__c = sExternalID + sTaxID + sPipeCharacter +accId;  
                        }
                    }
                    else
                    {
                        consumerId.Consumer_External_Id__c = sExternalID + sTaxID + sPipeCharacter + consumerId.id;
                    }
                }
                saveResults = Database.update(lstConsumerRecords, false);
                isError = HUMExceptionHelper.processSaveResults(saveResults, lstConsumerRecords, 'HUMUnknownAccountController', 'insertConsumerTaxIds', 'ConsumerId__c');
                if(!isError) return lstConsumerRecords;
            }
        }
        return null;
    }
    
    /*
    * Method Name   :    populateUnknownInformation
    * Description   :    Populate the Unknown Account Information passed in URL
    * Return Type   :    void
    * Parameters    :    Map<String,String>
    */
    private void populateUnknownInformation(Map<String,String>  unknownMapping)
    {
        if(unknownMapping != null)
        {
            for(String urlKey:unknownMapping.keyset())
            {
                if(mapUnknownAccParameters.containsKey(urlKey))
                {
                    String sParValue = mapUnknownAccParameters.get(urlKey);
                    account.put(unknownMapping.get(urlKey),sParValue) ;
                }
            }
        }
    }
    
    /*
    * Method Name   :  providerValidation
    * Description   :  This Method is used to validate Provider Account
    * Return Type   :  boolean
    * Parameters    :  NA
    */
    private boolean providerValidation()
    {
        assignProviderField();
        boolean bIsError = false;
        
        if(!validateFirstName())  bIsError = true;
        if(!validateLastName())  bIsError = true;
        
        if(!validateUnknownPostalCode())  bIsError = true;
        if(!validatePhone())  bIsError = true;
        if(!validateTaxID())  bIsError = true;
        if(!validateTaxIDLength())  bIsError = true;
        if(!lastNamelength())  bIsError = true;
        if(!validateNPI())  bIsError = true;
        if(!validateNPILength())  bIsError = true;
        
        if(bIsError)
        {
            bIsErrorOnPage = true;
            return false;
        }
        return true;
    }
    
    /*
    * Method Name   :  assignProviderField
    * Description   :  This Method is used for assigning for validations for Provider Account
    * Return Type   :  boolean
    * Parameters    :  NA
    */
    private boolean assignProviderField()
    {
        osearchWrapper.sTaxID = sTaxID;
        osearchWrapper.sPostalCode = account.billingpostalcode;
        osearchWrapper.sPhone = account.phone;
        oSearchWrapper.sGroupNumber = account.group_number__c;
        osearchWrapper.sLastName = account.Individual_Last_Name__c;
        osearchWrapper.sfirstName = account.Individual_first_Name__c;
        osearchWrapper.sNPI = Account.NPI_ID__c;
        return true;
    }
    
    /*
    * Method Name   :  memberValidation
    * Description   :  This Method is used to validate Member Account
    * Return Type   :  boolean
    * Parameters    :  NA
    */
    private boolean memberValidation()
    {
        assignMemberField();
        boolean bIsError = false;
        
        if(!validateFirstName())  bIsError = true;
        if(!validateLastName())  bIsError = true;
        
        if(!lastNameLength())  bIsError = true;
        if(!validateBirthdate())  bIsError = true;
        if(!validateUnknownPostalCode())  bIsError = true;
        if(!validatePhone())  bIsError = true;
        if(!validateLastName())  bIsError = true;
        if(bIsError)
        {
            return false;
        }
        return true;
    }
    
    /*
    * Method Name   :  assignMemberField
    * Description   :  This Method is used for assigning for validations for Member Account
    * Return Type   :  boolean
    * Parameters    :  NA
    */
    private boolean assignMemberField()
    {
        oSearchWrapper.sFirstName = Account.firstName;
        oSearchWrapper.sLastName = Account.lastName;
        oSearchWrapper.sBirthdate = Account.Birthdate__c;
        oSearchWrapper.sPostalCode = Account.PersonMailingPostalCode;
        oSearchWrapper.sPhone = Account.PersonHomePhone;
        return true;
    }
    
    /*
    * Method Name   :  resetValuesOnAgentTypeChange
    * Description   :  This Method is used for reseting values when the Agent Tye is changed
    * Return Type   :  void
    * Parameters    :  NA
    */
    public void resetValuesOnAgentTypeChange()
    {
        if(Account.Agent_Type__c == system.label.HUMAgencySearchAgentTypeValue3 && (Schema.sobjectType.account.fields.Name.isCreateable()))
        {
            Account.Name = '';
        }
        else if(Account.Agent_Type__c == system.label.HUMAgencySearchAgentTypeValue2)
        {
          if(Schema.sobjectType.account.fields.Individual_First_Name__C.isCreateable() || Schema.sobjectType.account.fields.Individual_First_Name__c.isUpdateable())//*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                        Account.Individual_First_Name__c = '';
           if(Schema.sobjectType.account.fields.Individual_Last_name__c.isCreateable() || Schema.sobjectType.account.fields.Individual_Last_name__c.isUpdateable())//*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                  Account.Individual_Last_Name__c = '';
        }
    }
    
    /*
    * Method Name   :  resetValuesOnProviderTypeChange
    * Description   :  This Method is used for reseting values when the Agent Tye is changed
    * Return Type   :  void
    * Parameters    :  NA
    */
    public void resetValuesOnProviderTypeChange()
    {
        if(Account.Provider_Classification__c!= system.label.HumserviceIndividualtag)
        {
            Account.Individual_First_Name__c = '';
            Account.Individual_Last_Name__c = '';
        }
        else if(Account.Provider_Classification__c== system.label.HumserviceIndividualtag)
        {
            Account.Name = '';
        }
    }
}