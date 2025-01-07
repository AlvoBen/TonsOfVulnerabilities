/*************************************************************************************************************************************************************************************************************************************
Apex Class Name : HUMCustomInteractionLogController
Version         : 1.0
Created Date    : 05/21/2014
Function        : 1.This class serves as controller for Interaction log page.
2.This involves saving interaction as well as populating interaction details.
3.This class is saving interaction members and associating them with Interaction object.
As the Interaction member is on detail side of master detail relationship with Account, It will have inherited
permission which may not let users to save the records, hence writting this class in without sharing mode.
Test Class      : HUMCustomInteractionLogControllerTest

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
* 1.21				   Pradeepkumar Dani		46100						 10/19/2016			  		Added a variable to set dynamic style classes for alerts table REQ - 287463
* 1.21				   Pradeepkumar Dani		47064						 11/14/2016			  		Defect Fix #247867.0001 
***************************************************************************************************************************************************************************************************************************************/
public without sharing class HUMCustomInteractionLogController
{
    //Stores Map of interaction Members v/s interacting about.
    private map<id, Interaction_Member__c> mapMemberInteraction = new map<id, Interaction_Member__c>();
    
    //Boolean variable to check association between interaction and case.
    private boolean bCheckAssociation = false;
    
    //String variable to hold the URL Id
    private static String sURLId = GLOBAL_CONSTANT_HUM.ID_HUM;
    
    //List to hold the Interacting With & About UserRecordAccess
    private list<UserRecordAccess> lstUserRecordAccess = new list<UserRecordAccess>();
    
    //List to hold the Interacting About UserRecordAccess
    private list<UserRecordAccess> lstIntAbtRecordAccess = new list<UserRecordAccess>();
    
    // Map to hold the Account Id and the UserRecordAccess records
    private map<Id,UserRecordAccess> mapRecordAccess = new map<Id,UserRecordAccess>();
    
    //Integer variable to do action polling for guidance alerts
    private Integer iAlertsCounter = 0;

    /**
    * Constructor for the class
    * <p>
    * This Method calls a method to initialises member variables of this class.
    *
    * @param 	none
    * @return  	none
    */
    public HUMCustomInteractionLogController ()
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
        
    
    //Boolean to check if guidance alerts are avaialable or not
    public Boolean bGuidanceAlerts{get;set;}
    
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
    
    // String value to store the dynamic style class attribute for UI display
    public string sAlertsClass { get; set; }
    
    /**
    * This Method is being called from Constructor.
    * <p>
    * This Method initialises member variables of this class.
    *
    * @param 	none
    * @return  	none
    */
    private void initilializeVariables()
    {
        oFetchInteraction = new Interaction__C();
        bGuidanceAlerts = true;
        iAlertsCounter = 0;
        if(lstSelectOptionIntAbt == null) lstSelectOptionIntAbt = new  list<SelectOption>();
        oHUMIntMemers = new Interaction_Member__c();
        mapMemberInteraction = new map<id, Interaction_Member__c>();
        mapRecordHaveAccess = new map<Id,boolean>();
        bGetAccess = false;
        bAccessInteractingWith = true;
        bAssignCase = false;
        bSavenNew = false;
        sAlertsClass = GLOBAL_CONSTANT_HUM.HEADERROWEMPTYSTYLE_HUM;
    }

    /**
    * This method is called when "Save button is hit on the Interaction log present in the member detail page".
    * <p>
    * This method will save interaction record.
    *
    * @param 	none
    * @return  	Pagereference
    */
    public Pagereference saveInteractionRecord()
    {
        try
        {
            Interaction__C oInt = new Interaction__c();
            if(String.ISBLANK(oFetchInteraction.name))
            {
                oInt = new Interaction__c(Interacting_with__c = oFetchInteraction.Interacting_with__c,
                                          Interacting_with_type__c = oFetchInteraction.Interacting_with_type__c,
                                          Caller_name__c = oFetchInteraction.Caller_name__c,
                                          Interaction_origin__c = oFetchInteraction.Interaction_origin__c,
                                          uuid__c = oFetchInteraction.uuid__c
                                         );
            }
            else
            {
                oInt = new Interaction__c(id = oFetchInteraction.id,
                                          Interacting_with__c = oFetchInteraction.Interacting_with__c,
                                          Interacting_with_type__c = oFetchInteraction.Interacting_with_type__c,
                                          Caller_name__c = oFetchInteraction.Caller_name__c,
                                          Interaction_origin__c = oFetchInteraction.Interaction_origin__c ,
                                          uuid__c = oFetchInteraction.uuid__c
                                         );
            }
            saveInteractionAndRequery(oInt);
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomInteractionLogController','saveInteractionRecord');
        }
        return null;
    }

    /**
    * This method upserts Interaction record and requeries it for display on screen.
    * <p>
    * This method will save interaction record.
    *
    * @param 	oInt			Interaction__c record object
    * @return  	none
    */
    public void saveInteractionAndRequery(Interaction__C oInt)
    {
        try
        {
            if(oInt != null)
            {
                // Upsert the record in without sharing class as account is not accesible here
                Database.upsertResult oSvr = HUMWithoutSharingQueryHelper.inserInteraction(oInt);
                list<Database.UpsertResult> listUpsertResults = new list<Database.UpsertResult>{oSvr};
                list<interaction__C> lstInteractions = new list<interaction__C>{oInt};
                boolean isError = HUMExceptionHelper.processUpsertResults(listUpsertResults , lstInteractions, 'HUMInteractionLogComponentController', 'saveInteractionAndRequery', 'Interaction__C');
            }
            //Re-Querying Interaction record just saved, so as to refresh screen with New values.
            if(oInt.Id!=null)
            {
                oFetchInteraction = [SELECT id, name, Interacting_with__c, Caller_name__c, Interacting_With_type__c, Interaction_origin__c, uuid__c
                                     from interaction__C where id = :oInt.id];
            }
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomInteractionLogController','saveInteractionAndRequery');
        }
    }

    /**
    * This method is called when SAVE button is clicked on the Interaction Log page.
    * <p>
    * This method is called when SAVE button is clicked on the Interaction Log page.
    *
    * @param 	none
    * @return  	pageReference
    */
    public Pagereference save()
    {
        try
        {
            if(sAccountId != null)
            {
                //If the subtab opened in console Contains Case record, then while saving Interaction,Associating to Case.
                if(sAccountId.getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM)
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
                if(sAccountId.getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM) saveInteractionRecord();
            }
            else
            {
                saveInteractionRecord();
            }
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomInteractionLogController','save');
        }
        return null;
    }

    /**
    * This method creates asociation between Case and Interaction by creating junction object record Case_Interaction__c
    * 
    * @param 	sInteractionID
	* @param 	sCaseID
    * @return  	none
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
            boolean isError = HUMExceptionHelper.processSaveResults(listSaveResults ,lstCaseInteractions ,'HUMCustomInteractionLogController','createCaseInteraction','Case_interaction__C');
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
    * @return  	ID - ID of Interaction__C objects record.
    */
    @TestVisible private ID saveInteraction()
    {
        try
        {
            Interaction__c oInt = new Interaction__C(ID = sInteractionId, Interacting_with_type__c = oFetchInteraction.interacting_with_type__c,
                                                     Interacting_With__c = oFetchInteraction.Interacting_With__c, Caller_name__c = oFetchInteraction.Caller_name__c,
                                                     Interaction_origin__c = oFetchInteraction.Interaction_origin__c,uuid__c=oFetchInteraction.uuid__c);
            
            Database.UpsertResult oSvr = Database.upsert(oInt,false);
            list<Database.UpsertResult> listUpsertResults = new list<Database.upsertResult>{oSvr};
            list<Interaction__c> lstInteractions = new list<Interaction__c>{oInt};
            boolean isError = HUMExceptionHelper.processUpsertResults(listUpsertResults,lstInteractions,'HUMCustomInteractionLogController','saveInteraction','Interaction__C');
            if(isError == false && oInt.Id!=null) return oInt.id;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomInteractionLogController','saveInteraction');
        }
        return null;
    }

    /**
* This method is used for initialization of interaction related details on interaction log section when the page loads.
* 
* @param 	none
* @return  	none
*/
    public void initializeInteraction()
    {
        /*Fetching Interaction Id and Account Id from the URL Using String Parsing.Standard methods of ApexPages object cannot be used. Hence, parsing URL manually.*/
        try
        {
            if (!bSavenNew)
            {
                set<Id> setAccountsIds = new set<Id>();
                if (ApexPages.currentpage().getParameters().containsKey(sURLId)) sAccountId = ApexPages.currentpage().getParameters().get(sURLId);
                Integer iStartPos = sStandardUrl.indexOf(GLOBAL_CONSTANT_HUM.INTERACTIONID_HUM);
                if (iStartPos != GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM && !sStandardUrl.endsWith(GLOBAL_CONSTANT_HUM.INTERACTIONID_HUM))
                {
                    //Fetching Interaction Details for ID mentioned in the URL.
                    sInteractionId = sStandardUrl.substring(iStartPos + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_FIFTEEN_HUM, iStartPos + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THIRTYTHREE_HUM);
                    list<Interaction__C> lstInt = [SELECT ID, name, Interacting_With__c, Interacting_With__r.Name, Caller_name__c, Interacting_With_type__c,
                                                   Interaction_origin__c, uuid__c FROM Interaction__C WHERE ID = :sInteractionId];

                    if (lstInt != null && !lstInt.isEmpty())
                    {
                        oFetchInteraction = lstInt[0];
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
            HUMExceptionHelper.logErrors(e, 'HUMCustomInteractionLogController', 'initializeInteraction');
        }
    }

    /**
    * This method is used for initialization of select list by adding all the member names when the page loads.
    * 
    * @param 	none
    * @return  	none
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
    }
  

    /**
    * This method is called when SAVE AND NEW button is clicked on Interaction log section.
    * 
    * @param 	none
    * @return  	pageReference
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
    * @param 	none
    * @return  	pageReference
    */
    public void flushInteraction()
    {
        oFetchInteraction = new Interaction__C();
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
    * @param 	none
    * @return  	pageReference
    */
    public pagereference onChangeInqAbt()
    {
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
    * @param 	none
    * @return  	none
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
        for(Integer i = 0 ; i < lstAccRecords.size() ; i++)
        {
            mapRecordHaveAccess.put(lstAccRecords.get(i), mapUsrRecAccess.get(lstAccRecords.get(i)).HASREADACCESS);
        }
    }

    /**
    * This Method is to check whether "Assign to Case" button should be enabled on the component or not
    * 
    * @param 	none
    * @return  	PageReference
    */
    public PageReference validateAssociateCaseButton()
    {
        if(recordId.getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM)
        {
            bAssignCase = true;
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
    * @param 	none
    * @return  	PageReference
    */
    public PageReference disableAssociateCaseButton()
    {
        bAssignCase = false;        
        return null;
    }
}