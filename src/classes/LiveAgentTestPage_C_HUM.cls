/*****************************************************************************************************************************************************************
Apex Class Name : LiveAgentTestPage_C_HUM
Version         : 1.0
Created On      : 10/09/2015
Function        : This class is controler for Live agent Chat functionality
Test Class      : LiveAgentTestPage_T_HUM

Modification Log:
* Developer Name              Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Isaac Lehar/Manish Kumar                             10-09-2015                 Original version
* Gargey Sharma                                        07/19/2016                 REQ - 237067: Dynamically Generate Chat for Testing Home Page
* Harsha N/ Gargey Sharma       37809                  08/31/2016                 Fixed Defects 239329.0001 and 239325.0001
* Lakshmi Madduri                                      04/09/2019                 HP Live Agent changes
* Santhi Mandava                355559                 05/10/2019                 Removed "ENCRYPTIONTYPE" custom setting creation
* Prafull Verma                                        04/07/2021                 REQ- 1915629 Security vulnerability fix
*******************************************************************************************************************************************************************/

public with sharing class LiveAgentTestPage_C_HUM
{
    @testvisible private Boolean bIsNameSearch = false;
    /*
    * This is constructor of the class which will initialize all variabes
    * @param None
    * @return None
    */
    public LiveAgentTestPage_C_HUM(HUMPreChatController ctrl)
    {
        reset();
        for (livechatbutton btn : [SELECT Id, Type, DeveloperName, MasterLabel FROM LiveChatButton WHERE Type = 'Standard' ORDER BY MasterLabel ASC])
        {
            btnValues += btn.DeveloperName + ',';
        }
        if (!string.isBlank(btnValues))btnValues = btnValues.removeEnd(','); 

        string entId = System.currentPagereference().getParameters().get('enterpriseId');

        if (string.isNotBlank(entId))
        {
            encryptedValue = populateEncyptedValue(entId);
        }
    }
    public LiveAgentTestPage_C_HUM()
    {
        reset();
        bIsError = false;
        encryptedValue = '';
        sRecordType = 'Member';
        bShowNextButton = false;
        lstSearcResults = new List<SearchWrapper>();
    }

    public String encryptedValue{ get; set; }
    public Boolean bIsError{ get; set; }
    public string sFname{ get; set; }
    public string sLname{ get; set; }
    public string sDob{ get; set; }
    public string sEnterpriseId{ get; set; }
    public string sMemberId{ get; set; }
    public list<Policy_Member__c> lstResult{ get;set; }
    public String sRecordType{ get;set; }
    public String sAccountId{ get;set; }
    public List<SearchWrapper> lstSearcResults{ get;set; }
    public string btnValues{ get;set; }{ btnValues = '';}
    public Boolean bShowNextButton{ get; set; }
    public string sLegacyDelete{ get; set; }

    /*
    * Wrapper Class
    */
    public class SearchWrapper
    {
        public String sFirstName{ get;set; }
        public String sLastName{ get;set; }
        public String sBirthDate{ get;set; }
        public String sEnterpriseId{ get;set; }
        public Boolean sLegacyDelete{ get; set; }

        public  SearchWrapper(String sFName, String sLName, String sBD, String sEntId, Boolean sLegDel)
        {
            this.sFirstName = sFName;
            this.sLastName = sLName;
            this.sBirthDate = sBD;
            this.sEnterpriseId = sEntId;
            this.sLegacyDelete = sLegDel;
        }
    }

    /*
    * Method to reset parameter on page
    * @param
    * @return
    */
    public pagereference reset()
    {
        sFname = '';
        sLname = '';
        sDob = '';
        sEnterpriseId = '';
        sMemberId = '';
        lstResult = null;
        lstSearcResults = new List<SearchWrapper>();
        sAccountId = '';
        bShowNextButton = false;
        Return null;
    }

    /*
    * Method to search member
    * @param
    * @return list<Account>
    */
    public void searchMember()
    {

        bIsNameSearch = false;
        try
        {
            if (ValidateRequest())
            {
                String sQuery = buildSoqlquery();
                lstSearcResults = new List<SearchWrapper>();
                List<Account> accList = new List<Account>();

                if (!String.isBlank(sQuery))
                {
                    if (bIsNameSearch)
                    {
                        List<List<sObject>> searchList = search.query(sQuery);
                        List<Account> lstTempAccounts = ((List<Account>)searchList[0]);
                        Boolean bIsMatching = true;
                        For(Account obj : lstTempAccounts)
                        {
                            bIsMatching = true;
                            If(String.IsNotBlank(sFname) && (String.IsBlank(obj.Individual_First_Name__c) || !obj.Individual_First_Name__c.startsWithIgnoreCase(sFname))) bIsMatching = false;
                            If(String.IsNotBlank(sLname) && (String.IsBlank(obj.Individual_First_Name__c) || !obj.Individual_Last_Name__c.startsWithIgnoreCase(sLname))) bIsMatching = false;
                            If(bIsMatching)
                            {
                                accList.Add(obj);
                            }
                        }
                    }
                    else
                    {
                        accList = Database.query(sQuery);
                    }

                    if (!accList.isEmpty())
                    {
                        if (accList.size() < 50)
                        {
                            bShowNextButton = true;
                            for (Account acc : accList)
                            {
                                lstSearcResults.add(new SearchWrapper(acc.Individual_First_Name__c, acc.Individual_Last_Name__c, acc.Birthdate__c, acc.Enterprise_Id__c, acc.ETL_Record_Deleted__c));
                            }
                        }
                        else
                        {
                            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.MoreThan50_LiveAgent_HUM));
                        }
                    }
                    else
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.NoResultsFound_LiveAgent_HUM));
                    }
                }
                else
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.NoResultsFound_LiveAgent_HUM));
                }
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'LiveAgentTestPage_C_HUM', 'LiveAgentTestPage_C_HUM');
        }
    }

    /*
    * Method to validate search inputs
    * @param
    * @return boolean
    */
    public boolean ValidateRequest()
    {
        if (string.isBlank(sFname) && string.isBlank(sLname) && string.isBlank(sDob) && string.isBlank(sEnterpriseId) && string.isBlank(sMemberId))
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.InputsRequired_LiveAgent_HUM));
            return false;
        }
        else
        {
            if (!string.isBlank(sFname) && string.isBlank(sLname))
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.RequiredLastName_LiveAgent_HUM));
                return False;
            }
            If(!string.isBlank(sFname) && !string.isBlank(sLname))
            {
                if (sFname.length() < 3 || sLname.length() < 3)
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.CharCount_LiveAgent_HUM));
                    return false;
                }
                else { return true; }
            }
            if (!string.isBlank(sEnterpriseId) && sEnterpriseId.length() >= 12)
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.EntIdValidation_LiveAgent_HUM));
                return false;
            }
            Else
            {
                return true;
            }
        }
    }

    /*
    * Method to build SOQL query
    * @param
    * @return String
    */
    public String buildSoqlquery()
    {
        string sQuery = '';
        String sNameKey = '';
        if (String.isNotBlank(sFname))
        {
            sFname=string.escapeSingleQuotes(sFname);
            sNameKey = sFname.trim() + '*';
        }
        if (string.isNotBlank(sLname))
        {
            sLname=string.escapeSingleQuotes(sLname);            
            If(String.IsNotBlank(sNameKey))
            {
                sNameKey += ' AND ' + sLname.trim() + '*';
            }
        else
        {
            sNameKey = sLname.trim() + '*';
        }
        }
        If(String.IsNotBlank(sNameKey))
        {
            bIsNameSearch = true;
            sQuery = 'Find \'' + sNameKey + '\' in all fields returning Account (Id, Individual_First_Name__c, Individual_Last_Name__c, Birthdate__c, Enterprise_Id__c, ETL_Record_Deleted__c where recordtype.developername =' + '\'' + HUMConstants.HumMemberRecordType + '\'';
        }
        else
        {
            sQuery = 'SELECT Id, Individual_First_Name__c, Individual_Last_Name__c, Birthdate__c, Enterprise_Id__c, ETL_Record_Deleted__c FROM Account where recordtype.developername =' + '\'' + HUMConstants.HumMemberRecordType + '\'';
        }

        if (String.isNotBlank(sDob))
        {
            sQuery += ' AND Birthdate__c =: sDob ';
        }
        if (String.isNotBlank(sEnterpriseId))
        {
            sQuery += ' AND Enterprise_Id__c =: sEnterpriseId ';
        }
        if (String.isNotBlank(sMemberId))
        {
            sMemberId=string.escapeSingleQuotes(sMemberId);
            String sMemberNameKey = sMemberId + '*';
            String SOSLquery = 'FIND \'' + sMemberNameKey + '\' IN Name FIELDS RETURNING Policy_Member__c(Id,Name, Member__c Limit 1)';
            List<List<sObject>> lstPolMemsearch = search.query(SOSLquery);
            List<Policy_Member__c> lstPolMem = ((List<Policy_Member__c>)lstPolMemsearch[0]);
            if (!lstPolMem.isEmpty())
            {
                sAccountId = lstPolMem[0].Member__c;
                sQuery += ' AND Id =: sAccountId';
            }
            else
            {
                sQuery = '';
            }
        }
        If(bIsNameSearch && String.isNotBlank(sQuery)) sQuery += ' )';
        return sQuery;
    }

    /*
    * Method to encrypt enterprise Id
    * @param
    * @return String
    */
    public String populateEncyptedValue(String sEntId)
    {
        blob  cryptoKey = blob.valueOf(HUMConstants__c.getInstance('CRYPTOKEY').StringValue__c);
        Blob encryptedData = Crypto.encryptWithManagedIV(HUMConstants__c.getInstance('CRYPTOALGORITHM').StringValue__c, cryptoKey, Blob.valueOf(sEntId));
        return EncodingUtil.converttoHex(encryptedData);
    }

    /*
    * This method will redirect user to back to test page
    * @param None
    * @return PageReference
    */
    public PageReference liveAgentTestPage()
    {
        try
        {
            encryptedValue = '';
            PageReference pageRef = new PageReference('/apex/LiveAgentTestPage_VF_HUM');
            pageRef.setredirect(false);
            if (Test.isRunningTest())    throw new HUMCustomException('');
            return pageRef;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'LiveAgentTestPage_C_HUM', 'liveAgentTestPage');
            return NULL;
        }
    }
}