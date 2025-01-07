/*************************************************************************************************************************************************************************************************************************************
Apex Class Name : CustomInteractionLog_C_HUM 
Version         : 1.0
Created Date    : 05/21/2014
Function        : 1.This class serves as controller for Interaction log page.
2.This involves saving interaction as well as populating interaction details.
3.This class is saving interaction members and associating them with Interaction object.
As the Interaction member is on detail side of master detail relationship with Account, It will have inherited
permission which may not let users to save the records, hence writting this class in without sharing mode.
Test Class      : CustomInteractionLog_T_HUM

Modification Log:

* Modification Id       Developer Name        Code Reveiw                  Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                  Ninad Patil              15868                        05/21/2014                 Original Version(REQ-00899)
* 1.1                  Ninad Patil              16484                        06/27/2014                 Changed code for multiple Interaction Members REQ-01169, REQ-01458
* 1.2                  Pradeep Raju             16484                        07/03/2014                 Uncommented the code for alerts
* 1.3                  Shreya Choodamani        16484                        07/23/2014                 Commented the code for alerts
* 1.4                  Shreya Choodamani        16484                        07/24/2014                 Un-Commented the code for alerts
* 1.5                  Ninad Patil              16484                        07/31/2014                 Coding standard comments incorporation
* 1.6                  Ninad Patil              17100                        08/05/2014                 Coding standard comments incorporation
* 1.7                  Pradeep Raju             17100                        08/10/2014                 Commented the code for alerts
* 1.8                  Pradeep Raju             17100                        08/11/2014                 Uncommented the code for alerts
* 1.9                  Ninad Patil              17832                        09/10/2014                 Changes for Limited account functionality
* 1.10                 Palak Papneja            18313                        10/06/2014                 Shifted position of declaration of variables according to Humana standards, corrected spelling mistakes
* 1.11                 Ninad Patil              18313                        10/07/2014                 fixed code review comment : sAccId removed.
* 1.12                 Shreya Choodamani        20233                        01/06/2015                 Added the following functions saveInteractionRecord() and saveInteractionAndRequery() as a part of the REQ - 66088
* 1.13                 Shreya Choodamani        21738                        03/20/2015                 Added the code to fix the Command Center Issue 160 (CA 4912880). Added a condition on line# 313 of initializeInteraction().
* 1.14                 Pradeep Raju             24021                        06/12/2015                 Updating the code as part of Interaction Performance latency CA Ticket# 5103328
* 1.15                 Pradeep Raju             24021                        07/21/2015                 Updated the code to make use of HUMConstants class instead of HUMConstants custom settings for hardcoded values
* 1.16                 Sagar Tapadia            25983                        08/21/2015                 Updated code to fix on load showing of interaction when opening record from Global Search.
                                                                                                        Also, on click of save and new if association is already done with case, it was showing error message as Case and interaction are
                                                                                                        already associated. Did a fix to it by adding boolean.Line #41,#255,#260 and #413
* 1.17                 Sagar Tapadia                                         10/29/2015                 Added false condition for Upsert so that validation are not by passed and are shown on page.  REQ - 233779  
* 1.18                 Sharan Shanmugam         29837                        02/10/2016                 Re raising it for review as part of Story REQ - 220870  
* 1.19                 Harshith Mandya                                       04/13/2016                 Defect Fix #210892.0001   
* 1.20                 Harshith Mandya          26131                        04/27/2016                 Defect Fix #223432.0001,210757.0001                                                                                                 
* 1.21                 Pradeepkumar Dani        46100                        10/19/2016                 Added a variable to set dynamic style classes for alerts table REQ - 287463
* 1.22                 Pradeepkumar Dani        47064                        11/14/2016                 Defect Fix #247867.0001 
* 1.23                 Pradeepkumar Dani        61786                        02/07/2017                 GetOffers implementation using JS remoting
* 1.24                 Prudhvi Pamarthi         131678                       11/13/2017                 REQ - 328935: Fix for CA 6972201
* 1.25				   Dinesh Subramaniyan                                   11/20/2017                 REQ - 338378: Ability in quick Start to capture the call back number after the interaction Is created (RF)
* 1.26				   Vinay Lingegowda										 05/06/2020                USER STORY 1083341 - PR00091574 - MF 1 - AHT - Ability to edit interaction details from any CRM page (CRM)
* 1.27                 Sunil Kumar Reddy Y                                   05/22/2020                 User Story 1114855: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 6/5 and 6/19 releases (CRM)
*1.28 				Sahil Verma/Navajit Sarkar						07/12/2021					US#2287945 : Adding Authentication Data in the Interaction Record
* 1.28                 Sahil Verma                                     	     10/14/2021            	Show popup on Case/Plan/Detail page on click of Save button on Interaction Log Panel as part of US - 2564484
* 1.29                Prasuna Pattabhi                                   11/11/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant - Account And Policy Pages
* 1.30               Prasuna Pattabhi                                                      11/12/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant CaseManagement_Constants_HUM reference added
 * 2.15                Sahil/Navajit                                         12/16/2021                 User story -US2704639 : copy the call details like UUID, Genesys Interaction ID in new interaction
* 2.16                 Harshada Kamble                                       01/05/2023                 US-3959734 : Salesforce Genesys CRM Authentication Status should not change.
* 2.17                 Harshada Kamble/Anil Pavithran                        03/09/2023                 US 2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
***************************************************************************************************************************************************************************************************************************************/
public without sharing class CustomInteractionLog_C_HUM 
{
    //Stores Map of interaction Members v/s interacting about.
    private map<id, Interaction_Member__c> mapMemberInteraction = new map<id, Interaction_Member__c>();
    
    //Boolean variable to check association between interaction and case.
    private boolean bCheckAssociation = false;
    
    //String variable to hold the URL Id
    private static String sURLId = CaseManagement_Constants_HUM.ID_HUM;
    
    //List to hold the Interacting With & About UserRecordAccess
    private list<UserRecordAccess> lstUserRecordAccess = new list<UserRecordAccess>();
    
    //List to hold the Interacting About UserRecordAccess
    private list<UserRecordAccess> lstIntAbtRecordAccess = new list<UserRecordAccess>();
    
    // Map to hold the Account Id and the UserRecordAccess records
    private map<Id,UserRecordAccess> mapRecordAccess = New map<Id,UserRecordAccess>();        
    
    Public Static final String OBJECT_ACCOUNT_HUM = 'Account';
    Public Static final String INTERACTIONID_HUM = '&interactionId=';
    Public Static final Integer NUMBER_CONSTANT_MINUSONE_HUM = -1;
    Public Static final Integer NUMBER_CONSTANT_FIFTEEN_HUM = 15;
    Public Static final Integer NUMBER_CONSTANT_PLUSONE_HUM = 1;
    Public Static final Integer NUMBER_CONSTANT_THIRTYTHREE_HUM = 33;
    Public Static final Integer NUMBER_CONSTANT_SIXTEEN_HUM = 16;
    /**
    * Constructor for the class
    * <p>
    * This Method calls a method to initialises member variables of this class.
    *
    * @param    none
    * @return   none
    */
    public CustomInteractionLog_C_HUM ()
    {
        initilializeVariables();
    }
    //Stores URL of the Primary Tab.
    public string sStandardUrl{get; set;}
    
    //Used to store Interaction details.
    public Interaction__c oFetchInteraction{get; set;}
    
    //Id of account record opened.
    public ID sAccountId{get;set;}
    
    //Id of the interaction record. If it already exists.
    public ID sInteractionId{Get; Set;}
   Public Boolean bGuidanceAlerts{Get;Set;}    
      
    //String variable to Store previous values When user toggles value of picklist "Inquired ABout".
    public String sPrevSelectedInqAbt{get; set;}
    
    //String variables holds Name for Naming the Tab being opened in console.
    public String sAccName{get; set;}
    
    //Stores select list for showing Inquired About value on page.
    public list<SelectOption> lstSelectOptionIntAbt{get; set;}
    
    //This is instance of Interaction Member object.
    public Interaction_Member__c oHUMIntMemers{get; set;}
    
    // Boolean for chacking access of the logged in user on the Interacting About record
    public boolean bGetAccess{get;set;}
    
    // Map to hold the Account Id and the logged in user Access
    public map<Id,boolean> mapRecordHaveAccess{get;set;}
    
    // Boolean for checking access of the logged in user on the Interacting with record
    public boolean bAccessInteractingWith{get;set;}
    
    // Boolean value to control the display for "Associate Case" button
    public boolean bAssignCase { get; set; }
    
    // Variable to hold the record Id of the current subtab
    public Id recordId { get; set; }
    
    // Boolean value to capture whether Save and New was donec 
    public Boolean bSavenNew { get; set; }
        
    public string sHomePhone { get; set; }
    //Holds the boolean value to show if apex page has error or not
    public Boolean hasError { get { return ApexPages.hasMessages(); }set; }
    //US#2287945-Boolean value to show the authentication checkbox as enabled or disabled
    public boolean authCheck{get;set;}

    //US#2287945-Boolean value for the switch to show the authentication checkbox as enabled or disabled
    public boolean mf3Switch{get;set;} 

    //US#2230077 - Boolean Value for the switch to show the popup on click of Save button
    Public Boolean SwitchOBInteraction{get;set;}

    //US#2704639
    public Boolean Switch_2704639{get;set;}

    //US#2287945-To check if a user is a genesys user or not
    public Boolean isGenesysUser{get;set;}

    //US#2191493 - Boolean value for the switch to enable or disable caller type population functionality
    public boolean callerTypePopSwitch{get;set;}

    //Holds boolean value whether to show Error message or not.
    public boolean bRenderErrorMsg{get;set;}

    //Id of the interaction record. If it already exists.
    public ID sIntAboutId{get; set;}

    //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
    public Boolean multiMemberAuthSwitch {       
        get{
        return  PhoneBook_H_HUM.isCRMFunctionalityON('2760646');
        }
        set;
    }

    //US#2287945-Label to show based on the authentication checkbox value
    public string authCheckLabel{
        get { 
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            if(multiMemberAuthSwitch)
                return (oHUMIntMemers.Authenticated__c == true) ? 'Authenticated' : 'UnAuthenticated'; 
            else
                return (oFetchInteraction.Authenticated__c == true) ? 'Authenticated' : 'UnAuthenticated'; 
        }
        set;
    }

    /*
     * Method Name   :    updateAuth()
     * Description   :    This method is called to update the authCheck variable.   
     * return Type   :    Void
     * Parameters    :    None
     */
    public void updateAuth()
    {
        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
        if(multiMemberAuthSwitch)
        {
            if(oHUMIntMemers.Authenticated__c == true)authCheck=true;
            else authCheck=false; 
        } 
        else{
            if(oFetchInteraction.Authenticated__c == true)authCheck=true;
            else authCheck=false; 
        }   
    }
    
    
    /**
    * This Method is being called from Constructor.
    * <p>
    * This Method initialises member variables of this class.
    *
    * @param    none
    * @return   none
    */
    private void initilializeVariables()
    {
        oFetchInteraction = new Interaction__C();        
        if(lstSelectOptionIntAbt == null) lstSelectOptionIntAbt = new  list<SelectOption>();
        oHUMIntMemers = new Interaction_Member__c();
        mapMemberInteraction = new map<id, Interaction_Member__c>();
        mapRecordHaveAccess = new map<Id,boolean>();
        bGetAccess = false;
        bAccessInteractingWith = true;
        bAssignCase = False;
        bSavenNew = False;  
        bGuidanceAlerts = True;
        isGenesysUser =  PhoneBook_H_HUM.isGenesysUser();
        mf3Switch=PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.AUTH_SWITCH);
        SwitchOBInteraction = PhoneBook_H_HUM.isCRMFunctionalityON('2230077');
        Switch_2704639 = PhoneBook_H_HUM.isCRMFunctionalityON('2704639');
        callerTypePopSwitch = PhoneBook_H_HUM.isCRMFunctionalityON('2191493');
    }

    /**
    * This method Is called when "Save button is hit on the Interaction log present in the member detail page".
    * <p>
    * This method will save interaction record.
    *
    * @param    none
    * @return   Pagereference
    */
    public Pagereference saveInteractionRecord()
    {
        try
        {
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            Boolean tempAuthenticated; 

            if(multiMemberAuthSwitch)
                tempAuthenticated = oFetchInteraction.Authenticated__c ==true ? oFetchInteraction.Authenticated__c: oHUMIntMemers.Authenticated__c;
            else
                tempAuthenticated= oFetchInteraction.Authenticated__C;

            Interaction__C oInt = new Interaction__c();
            if(String.ISBLANK(oFetchInteraction.name))
            {
                oInt = new Interaction__c(Interacting_with__c = oFetchInteraction.Interacting_with__c,
                                          Interacting_with_type__c = oFetchInteraction.Interacting_with_type__c,
                                          Caller_name__c = oFetchInteraction.Caller_name__c,
                                          Interaction_origin__c = oFetchInteraction.Interaction_origin__c,
                                          uuid__c = oFetchInteraction.uuid__c,
                                          Authenticated__c = tempAuthenticated,//US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)   
                                          Call_Category_Description__c = oFetchInteraction.Call_Category_Description__c,
                                          Survey_ID__c = oFetchInteraction.Survey_ID__c,
                                          Genesys_Interaction_ID__c = oFetchInteraction.Genesys_Interaction_ID__c,
                                          Call_Data__c = oFetchInteraction.Call_Data__c 
                                         );
            }
            else
            {
                oInt = new Interaction__c(id = oFetchInteraction.id,
                                          Interacting_with__c = oFetchInteraction.Interacting_with__c,
                                          Interacting_with_type__c = oFetchInteraction.Interacting_with_type__c,
                                          Caller_name__c = oFetchInteraction.Caller_name__c,
                                          Interaction_origin__c = oFetchInteraction.Interaction_origin__c ,
                                          uuid__c = oFetchInteraction.uuid__c,
                                          Authenticated__c = tempAuthenticated,//US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                                          Call_Category_Description__c = oFetchInteraction.Call_Category_Description__c,
                                          Survey_ID__c = oFetchInteraction.Survey_ID__c,
                                          Genesys_Interaction_ID__c = oFetchInteraction.Genesys_Interaction_ID__c,
                                          Call_Data__c = oFetchInteraction.Call_Data__c 
                                         );
            }
            saveInteractionAndRequery(oInt);
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CustomInteractionLog_C_HUM','saveInteractionRecord');
        }
        return null;
    }

    /**
    * This method upserts Interaction record and requeries it for display on screen.
    * <p>
    * This method will save interaction record.
    *
    * @param    oInt            Interaction__c record object
    * @return   none
    */
    public void saveInteractionAndRequery(Interaction__C oInt)
    {
        try
        {
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            if(multiMemberAuthSwitch)
            {
                    if(oInt != null)
                    {
                        if(oHUMIntMemers.Authenticated__c ==false ||(oHUMIntMemers.Authenticated__c ==true && (oInt.Interacting_with__C != null || oInt.Caller_name__C != null ) && oHUMIntMemers.interacting_about__C != null))
                    {
                        
                        // Upsert the record in without sharing class as account is not accesible here
                        Database.upsertResult oSvr = HUMWithoutSharingQueryHelper.inserInteraction(oInt);
                        list<Database.UpsertResult> listUpsertResults = new list<Database.UpsertResult>{oSvr};
                        list<interaction__C> lstInteractions = new list<interaction__C>{oInt};
                        boolean isError = HUMExceptionHelper.processUpsertResults(listUpsertResults , lstInteractions, 'HUMInteractionLogComponentController', 'saveInteractionAndRequery', 'Interaction__C');
                        //Update the authcheck valriable to update the UI 
                        if(!isError)
                        {
                            updateAuth();
                        }
                        oFetchInteraction = [SELECT id, name, Interacting_with__c,Authenticated__c, Caller_name__c, Interacting_With_type__c, Interaction_origin__c, uuid__c,Call_Category_Description__c,Genesys_Interaction_ID__c,Survey_ID__c,Call_Data__c
                                            from interaction__C where id = :oInt.id];
                        UpdateAuthForInteractionMember(oFetchInteraction);
                    }
                    else {

                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.Label.HUM_INTERACTION_VALIDATION));
                        bRenderErrorMsg =true;
                        }  
                    }
            }
            else 
            {
                if(oInt != null)
                {
                    // Upsert the record in without sharing class as account is not accesible here
                    Database.upsertResult oSvr = HUMWithoutSharingQueryHelper.inserInteraction(oInt);
                    list<Database.UpsertResult> listUpsertResults = new list<Database.UpsertResult>{oSvr};
                    list<interaction__C> lstInteractions = new list<interaction__C>{oInt};
                    boolean isError = HUMExceptionHelper.processUpsertResults(listUpsertResults , lstInteractions, 'HUMInteractionLogComponentController', 'saveInteractionAndRequery', 'Interaction__C');
                    //Update the authcheck valriable to update the UI 
                    if(!isError)
                    {
                        updateAuth();
                    }
                }
                //Re-Querying Interaction record just saved, so as to refresh screen with New values.
                if(oInt.Id!=null)
                {
                    //Added Authenticated__c field in the query to populate the value in the panel
                    oFetchInteraction = [SELECT id, name, Interacting_with__c,Authenticated__c, Caller_name__c, Interacting_With_type__c, Interaction_origin__c, uuid__c,Call_Category_Description__c,Genesys_Interaction_ID__c,Survey_ID__c,Call_Data__c
                                        from interaction__C where id = :oInt.id];
                }
            }
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CustomInteractionLog_C_HUM','saveInteractionAndRequery');
        }
    }

    /**
    * This method is called when SAVE button is clicked on the Interaction Log page.
    * <p>
    * This method is called when SAVE button is clicked on the Interaction Log page.
    *
    * @param    none
    * @return   pageReference
    */
    public Pagereference save()
    {
        try
        {
             if(sAccountId != null)
             {
                //If the subtab opened in console Contains Case record, then while saving Interaction,Associating to Case.
                String strAccId = String.valueOf(sAccountId);        
                String idAccPrefix = strAccId.substring(0,3);
                if(idAccPrefix !='00a')
                {
                    if(sAccountId.getSobjectType().getDescribe().getName() == CaseManagement_Constants_HUM.CASE_CONSTANT_HUM)
                    {
                        list<Case> lstCase = [SELECT ID, accountId From Case Where id = :sAccountId limit 1];
                        if(!lstCase.isEmpty())
                        {
                            String sIntId = saveInteraction();
                            //Creating Interaction Members.
                            if(!String.isBlank(sIntId))createCaseInteraction(sIntId, lstCase[0].id);
                        }
                    }
                    //If the primary Tab opened in the console is of Account type, then just saving the Interaction Record.


                
                    if(sAccountId.getSobjectType().getDescribe().getName() == OBJECT_ACCOUNT_HUM) saveInteractionRecord();
                }               
                
             }
             else
             {
                saveInteractionRecord();
             }
           
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CustomInteractionLog_C_HUM','save');
        }
        return null;
    }

    /**
    * This method creates asociation between Case and Interaction by creating junction object record Case_Interaction__c
    * 
    * @param    sInteractionID
    * @param    sCaseID
    * @return   none
    */
    @TestVisible private void createCaseInteraction(String sInteractionID, String sCaseID)
    {
        list<Case_interaction__c> lstCaseInt = [Select id from Case_interaction__c where interaction__c = :sInteractionID AND Case__c = :sCaseID Limit 1];
        if(lstCaseInt.isEmpty())
        {
            Case_Interaction__c oCaseInt = new Case_Interaction__c(Case__c = sCaseID, Interaction__c = sInteractionID);
            Database.SaveResult oSvr = Database.insert(oCaseInt);
            
            //Calling methods for Error Logging and exception handeling.
            list<Database.SaveResult> listSaveResults = new list<Database.SaveResult>{oSvr};
            list<Case_interaction__C> lstCaseInteractions = new list<Case_interaction__C>{oCaseInt};
            boolean isError = HUMExceptionHelper.processSaveResults(listSaveResults ,lstCaseInteractions ,'CustomInteractionLog_C_HUM','createCaseInteraction','Case_interaction__C');
            if(!isError) 
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Confirm,System.Label.HUMCaseInteractionAssSuccess));
                bCheckAssociation = true;
            }
        }
        else
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error,System.Label.HUMDupCaseInteractionError));
        }
    }

    /**
    * Method saves the Interaction record and returns the 'ID' of newly created record.
    * 
    * @param    none
    * @return   ID - ID of Interaction__C objects record.
    */
    @TestVisible private ID saveInteraction()
    {
        try
        {
            //Added Authenticated__c field in the query to populate the value in the panel
            Interaction__c oInt = new Interaction__C(ID = sInteractionId, Interacting_with_type__c = oFetchInteraction.interacting_with_type__c,
                                                     Interacting_With__c = oFetchInteraction.Interacting_With__c, Caller_name__c = oFetchInteraction.Caller_name__c,
                                                     Interaction_origin__c = oFetchInteraction.Interaction_origin__c,uuid__c=oFetchInteraction.uuid__c, Authenticated__c = oFetchInteraction.Authenticated__c,Call_Category_Description__c = oFetchInteraction.Call_Category_Description__c,
                                                     Survey_ID__c = oFetchInteraction.Survey_ID__c,Genesys_Interaction_ID__c = oFetchInteraction.Genesys_Interaction_ID__c, Call_Data__c = oFetchInteraction.Call_Data__c);
            
            Database.UpsertResult oSvr = Database.upsert(oInt,false);
            list<Database.UpsertResult> listUpsertResults = new list<Database.upsertResult>{oSvr};
            list<Interaction__c> lstInteractions = new list<Interaction__c>{oInt};
            boolean isError = HUMExceptionHelper.processUpsertResults(listUpsertResults,lstInteractions,'CustomInteractionLog_C_HUM','saveInteraction','Interaction__C');
            //Update the authcheck valriable to update the UI 
            if(isError == false && oInt.Id!=null) 
            {
                updateAuth();
                return oInt.id;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CustomInteractionLog_C_HUM','saveInteraction');
        }
        return null;
    }

    /**
* This method is used for initialization of interaction related details on interaction log section when the page loads.
* 
* @param    none
* @return   none
*/
    public void initializeInteraction()
    {
        /*Fetching Interaction Id and Account Id from the URL Using String Parsing.Standard methods of ApexPages object cannot be used. Hence, parsing URL manually.*/
        try
        {    
            if (!bSavenNew)
            {
                set<Id> setAccountsIds = new set<Id>();
                Integer iStartPos = sStandardUrl.indexOf(INTERACTIONID_HUM);
                Integer iStartPos1 = sStandardUrl.lastIndexOf('/');
                Interaction__c oInt;

                /*US2760646- Global search issue fix.
                1. If only one interacting about record found based on interaction id then will take from databases.
                2. Else check current selected interacting about value from sStandardUrl sent from CustomInteractionLog.js file*/
                if(multiMemberAuthSwitch)
                {
                    if (iStartPos != NUMBER_CONSTANT_MINUSONE_HUM && !sStandardUrl.endsWith(INTERACTIONID_HUM))
                    {
                        sInteractionId = sStandardUrl.substring(iStartPos + NUMBER_CONSTANT_FIFTEEN_HUM, iStartPos + NUMBER_CONSTANT_THIRTYTHREE_HUM);
                    }
                    if(sInteractionId != null ){
                        List < Interaction__c > lstInteractionRec = [SELECT Id, Caller_Name__c,Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,Call_data__c,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c,Authenticated__c, Interaction__c FROM Interaction_Members__r order by CreatedDate desc) FROM Interaction__c where ID =: sInteractionId order by Name desc limit 1];
                        if (lstInteractionRec.size() > 0) 
                        {
                           oInt = lstInteractionRec[0];
                        }
                        if (oInt.Interaction_Members__r.size() == 1) 
                        {
                            Interaction_Member__c clonedIntMember = oInt.Interaction_Members__r[0].clone(false,true,false,false);
                            sAccountId = clonedIntMember.interacting_about__C;
                        }
                        else{
                            if (iStartPos1 != NUMBER_CONSTANT_MINUSONE_HUM && !sStandardUrl.endsWith(INTERACTIONID_HUM))
                            {  
                                sIntAboutId = sStandardUrl.substring(iStartPos1 + NUMBER_CONSTANT_PLUSONE_HUM ,iStartPos1 + NUMBER_CONSTANT_SIXTEEN_HUM);
                                if(sIntAboutId != null){
                                    sAccountId = sIntAboutId;  
                                }   
                            }
                            
                        }
                    }

                }
                else{
                    if (ApexPages.currentpage().getParameters().containsKey(sURLId)) sAccountId = ApexPages.currentpage().getParameters().get(sURLId);
                }
                
                if (iStartPos != NUMBER_CONSTANT_MINUSONE_HUM && !sStandardUrl.endsWith(INTERACTIONID_HUM))
                {
                    //Fetching Interaction Details for ID mentioned in the URL.
                    sInteractionId = sStandardUrl.substring(iStartPos + NUMBER_CONSTANT_FIFTEEN_HUM, iStartPos + NUMBER_CONSTANT_THIRTYTHREE_HUM);
                    //Added Authenticated__c field in the query to populate the value in the panel
                    list<Interaction__C> lstInt = [SELECT ID, name, Interacting_With__c,Authenticated__c, Interacting_With__r.Name,Interacting_With__r.PersonHomePhone,Interacting_With__r.Phone,Interacting_With__r.IsPersonAccount, Caller_name__c, Interacting_With_type__c,Call_Category_Description__c,Genesys_Interaction_ID__c,Survey_ID__c,
                                                   Interaction_origin__c, uuid__c, Call_Data__c FROM Interaction__C WHERE ID = :sInteractionId];

                    if (lstInt != null && !lstInt.isEmpty())
                    {
                        //US-3959734 : Salesforce Genesys CRM Authentication Status should not change. Added below condition for fixing Authentication Status change issue on account details page after disconnect or transfer. 
                        if(PhoneBook_H_HUM.isCRMFunctionalityON('3959734')){
                            if(oFetchInteraction.Authenticated__c == True){
                                lstInt[0].Authenticated__c = True;
                            }
                        }
                        oFetchInteraction = lstInt[0];

                        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        if(multiMemberAuthSwitch)
                            authCheck = oHUMIntMemers.Authenticated__c; //assign authcheck variable
                        else
                            authCheck = oFetchInteraction.Authenticated__c; //assign authcheck variable

                        if(lstInt[0].Interacting_With__r.IsPersonAccount)
						{
							sHomePhone = lstInt[0].Interacting_With__r.PersonHomePhone;
						}
						else
						{
							sHomePhone = lstInt[0].Interacting_With__r.Phone;
						}
                        if (oFetchInteraction.Interacting_With__c != null) setAccountsIds.add(oFetchInteraction.Interacting_With__c);

                        //Fetching The Interaction Members pertaining to Interaction being opened.
                        list<Interaction_Member__c> lstSelectedMembers = HUMWithoutSharingQueryHelper.fetchInteractionMembers(sInteractionId);
                        if (lstSelectedMembers != null && !lstSelectedMembers.isEmpty())
                        {
                            initializeInteractionDetails(lstSelectedMembers, setAccountsIds);
                        }
                    }
                    dataSecurity();
                    if (mapRecordHaveAccess.containskey(sAccountId) && sAccountId != null) bGetAccess = mapRecordHaveAccess.get(sAccountId);
                }
            }
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CustomInteractionLog_C_HUM', 'initializeInteraction');
        }
    }

    /**
    * This method is used for initialization of select list by adding all the member names when the page loads.
    * 
    * @param    none
    * @return   none
    */
    public void initializeInteractionDetails(list<Interaction_Member__c> lstSelectedMembers, set<Id> setAccountsIds)
    {
        set<Id> setIntAbtIds = new set<Id>();
        //Select Option List for drop-down(Interacting About) field is populated here.
        lstSelectOptionIntAbt = new list<SelectOption>();
        for (Interaction_Member__c oHUMMember :lstSelectedMembers)
        {
            //Adding each value of Interacton Member in the Select Option List.
            if (lstSelectOptionIntAbt != null)
            {
                lstSelectOptionIntAbt.add(new selectOption(oHUMMember.interacting_about__C, oHUMMember.interacting_about__r.name));
                setAccountsIds.add(oHUMMember.interacting_about__c);
                setIntAbtIds.add(oHUMMember.interacting_about__c);
            }
            Interaction_Member__c oHUMIntMemTemp = oHUMMember;
            if (mapMemberInteraction != null) mapMemberInteraction.put(oHUMMember.interacting_about__C, oHUMIntMemTemp);
        }
        lstUserRecordAccess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN: setAccountsIds];
        for (UserRecordAccess userAccess : lstUserRecordAccess)
        {
            mapRecordAccess.put(userAccess.RECORDID, userAccess);
            if (setIntAbtIds.contains(userAccess.RECORDID)) lstIntAbtRecordAccess.add(userAccess);
        }
        if (mapRecordAccess.containsKey(oFetchInteraction.Interacting_With__c)) bAccessInteractingWith = mapRecordAccess.get(oFetchInteraction.Interacting_With__c).HasReadAccess;
        oHUMIntMemers.interacting_about__C = sAccountId;
        if (mapMemberInteraction != null && mapMemberInteraction.containsKey(sAccountId)) oHUMIntMemers.Interacting_About_Type__c = mapMemberInteraction.get(sAccountId).Interacting_About_Type__c;
        //Saving previously selected Interacting About value so as to revert after tab is changed.
        sPrevSelectedInqAbt = sAccountId;
        if (mapMemberInteraction != null && mapMemberInteraction.containsKey(sAccountId)) sAccName = mapMemberInteraction.get(sAccountId).interacting_about__r.name;

        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
        if(multiMemberAuthSwitch)
        {
            onChangeInqAbt();
        }


    }
    
    /**
    * This method is called when SAVE AND NEW button is clicked on Interaction log section.
    * 
    * @param    none
    * @return   pageReference
    */
    public Pagereference SaveAndNew()
    {
        bCheckAssociation = false;
        save();
        flushInteraction();
        return NULL;
    }

    /**
    * This Method flushes all the data structures being used in the page.So as to start with the new Interaction.
    * 
    * @param    none
    * @return   pageReference
    */
    public void flushInteraction()
    {
        String tempUUID;
        String tempOrigin;
        String tempInteractionId; 
        String tempCallCattDesc;
        String tempSurveyId;
        Boolean tempAuth;
        String tempCallData;
        if(oFetchInteraction.uuid__c != null && oFetchInteraction.Genesys_Interaction_ID__c !=null && Switch_2704639 != false)
        {
            
            tempUUID = oFetchInteraction.uuid__c;
            tempOrigin = oFetchInteraction.Interaction_Origin__c;
            tempCallCattDesc = oFetchInteraction.Call_Category_Description__c;
            tempSurveyId = oFetchInteraction.Survey_ID__c;
            tempInteractionId = oFetchInteraction.Genesys_Interaction_ID__c;
            tempAuth = oFetchInteraction.Authenticated__c;
            tempCallData = oFetchInteraction.Call_Data__c;
            oFetchInteraction = new Interaction__C();
            If(Schema.sObjectType.Interaction__c.fields.uuid__c.isCreateable() && Schema.sObjectType.Interaction__c.fields.uuid__c.isUpdateable()) oFetchInteraction.uuid__c = tempUUID;
            If(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable() && Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isUpdateable()) oFetchInteraction.Interaction_Origin__c = tempOrigin;
            If(Schema.sObjectType.Interaction__c.fields.Call_Category_Description__c.isCreateable() && Schema.sObjectType.Interaction__c.fields.Call_Category_Description__c.isUpdateable()) oFetchInteraction.Call_Category_Description__c = tempCallCattDesc;
            If(Schema.sObjectType.Interaction__c.fields.Survey_ID__c.isCreateable() && Schema.sObjectType.Interaction__c.fields.Survey_ID__c.isUpdateable()) oFetchInteraction.Survey_ID__c = tempSurveyId;
            If(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable() && Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isUpdateable()) oFetchInteraction.Genesys_Interaction_ID__c = tempInteractionId;
            If(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable() && Schema.sObjectType.Interaction__c.fields.Authenticated__c.isUpdateable()) oFetchInteraction.Authenticated__c = tempAuth;
            If(Schema.sObjectType.Interaction__c.fields.Call_Data__c.isCreateable() && Schema.sObjectType.Interaction__c.fields.Call_Data__c.isUpdateable()) oFetchInteraction.Call_Data__c = tempCallData;
        } else {
            oFetchInteraction = new Interaction__C();
        }
        sInteractionId = null;
        sStandardUrl='';
        mapMemberInteraction  = new map<id, Interaction_Member__c>();
        oHUMIntMemers  = new Interaction_Member__c();
        lstSelectOptionIntAbt = new  list<SelectOption>();
        bSavenNew = true;
    }

    /**
    * This method handles on change event of "Inquired About Field".
    * 
    * @param    none
    * @return   pageReference
    */
    public pagereference onChangeInqAbt()
    {
        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
        if(multiMemberAuthSwitch){

            List<Interaction_Member__c> lstInteractionMembers = [SELECT Id,name,Authenticated__C FROM Interaction_Member__c 
                                                                WHERE Interacting_About__c =: oHUMIntMemers.Interacting_About__c 
                                                                AND Interaction__c =: oFetchInteraction.Id];
            
            if(lstInteractionMembers.size() >0){
                oHUMIntMemers.Authenticated__C = lstInteractionMembers[0].Authenticated__C;
                authCheckHandler();
                updateAuth();
            }
        }

        if(!String.isblank(sAccountId))
        {
            list<Account> lstAcc = [Select Id , Name From Account Where ID = :sAccountId];
            if(lstAcc != null && !lstAcc.isEmpty())sAccName = lstAcc[0].name;
            if(mapRecordHaveAccess.containskey(sAccountId) && sAccountId != null) bGetAccess = mapRecordHaveAccess.get(sAccountId);
        }
        return null;
    }

    /**
    * This Method is for Security purpose. Used to display fewer records to External users
    * 
    * @param    none
    * @return   none
    */
    public void dataSecurity()
    {
        List <String> lstAccRecords = new list<String>();
        map<Id, UserRecordAccess> mapUsrRecAccess = new map<Id, UserRecordAccess>();
        mapRecordHaveAccess = new map<Id, Boolean>();
        for(SelectOption s : lstSelectOptionIntAbt)
        {
            lstAccRecords.add(s.getValue());
        }
        for(UserRecordAccess userAccess : lstIntAbtRecordAccess)
        {
            mapUsrRecAccess.put(userAccess.RECORDID, userAccess);
        }
        for(Integer i = 0 ; i <lstAccRecords.size() ; i++)
        {
            mapRecordHaveAccess.put(lstAccRecords.get(i), mapUsrRecAccess.get(lstAccRecords.get(i)).HASREADACCESS);
        }
    }

    /**
    * This Method is to check whether "Assign to Case" button should be enabled on the component or not
    * 
    * @param    none
    * @return   PageReference
    */
    public PageReference validateAssociateCaseButton()
    {
        String strRecordId = String.valueOf(recordId);
        String idRecordPrefix = strRecordId.substring(0,3);
        if(idRecordPrefix != '00a')
        {
            if(recordId.getSobjectType().getDescribe().getName() == CaseManagement_Constants_HUM.CASE_CONSTANT_HUM)
            {
                bAssignCase = true;
            }
            else
            {
                bAssignCase = false;
            }
        }
        else
        {
            bAssignCase = false;
        }
        return null;
    }

    /**
    * This Method is to disalbe the Associate Case button
    * 
    * @param    none
    * @return   PageReference
    */
    public PageReference disableAssociateCaseButton()
    {
        bAssignCase = false;        
        return null;
    }

    /**
    * This Method is to Label to show based on the authentication checkbox value change
    * 
    * @param    none
    * @return   PageReference
    */
    public void authCheckHandler(){
        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
        if(multiMemberAuthSwitch){
            If(oHUMIntMemers.Authenticated__c == true)
                authCheckLabel = 'Authenticated';
            else 
                authCheckLabel = 'UnAuthenticated';
        }
        else{
            If(oFetchInteraction.Authenticated__c == true)
                authCheckLabel = 'Authenticated';
            else 
                authCheckLabel = 'UnAuthenticated';
        }
    }

    /**
    * US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
    * This Method is to update authenticated checkbox into Interaction Member object 
    * 
    * @param    oInt
    * @return   PageReference
    */
    public void updateAuthForInteractionMember(Interaction__C oInt){
        try
        {
            if(oInt.Id != null){
            String Interactingabout = oHUMIntMemers.Interacting_About__c;
            List<Interaction_Member__c> lstSelectedMembers = new List<Interaction_Member__c>();
            
            lstSelectedMembers = [select Id, Name,Authenticated__c
                                    FROM Interaction_Member__c 
                                    Where Interaction__c = : oInt.Id AND Interacting_About__c =:Interactingabout];
            if(lstSelectedMembers.size() >0){
                lstSelectedMembers[0].Authenticated__c = oHUMIntMemers.Authenticated__c;
                update lstSelectedMembers;
            }
            
            }
        }
        Catch(Exception e){
            HUMExceptionHelper.logErrors(e,'CustomInteractionLog_C_HUM','updateAuthForInteractionMember');
        }
        
    }
}