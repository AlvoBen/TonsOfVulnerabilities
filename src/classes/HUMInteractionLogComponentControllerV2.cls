/*******************************************************************************************************************************
Apex Class Name : HUMInteractionLogComponentControllerV2 
Version         : 2.0
Created On      : 08/01/2015
Function        : 1. This class serves as Controller of the HUMCustomInteractionComponent on the Search Tab.
                  2. This Class Interacts with Search pages and gets the data from Searches to create interaction records.
Test Class      : HUMInteractionLogCompControllerV2Test
Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 2.0              Chaitanya Vegendla        25261                 08/01/2015                 Original Version(REQ-01189)
 * 2.1              Harshith Mandya/          25695                                            Missed code from TFS and updated
 * 2.2              Manish Kuamr                                    09/10/2015                 Removed usage of HUMSearchController instead using HUMSearchControllerV2
 * 2.3              Vishal Verma                                    11/09/2015                 REQ - 237001 : Removed Interaction Encrytion
 * 2.4				Amar Gadewar									07/13/2017				   REQ - 323374 - Populating the interaction origin as inbound call by default (RF)
 * 2.5              Mohammed Noor            126055                 09/06/2017                 Fix for Defect # 313759.
 * 2.6              Piyusha Babar                                   02/02/2018                 REQ -350779-Populating the interaction origin as inbound call by default (CRM)
 * 2.7              Asish Behera             314777                 11/08/2018                 REQ - 378095 PCC - Set Interaction Origin to Inbound Call for PCC Users (RF)
 * 2.8				Amar Gadewar									01/18/2019				   REQ - 383509 - Remove 655 PCC QS Permission Set dependency.
 * 2.9              Moshitha Gunasekaran                            04/20/2021                 US#2163329 - Removed PopulateUnknownMemberDetails method for checkmarx issues
 * 2.10             Ankit Avula                                     04/29/2021                 US2082644 - OGO -  CRM Display 'GNA' call category in the form of alert on member search screen
 *2.11 			Sahil Verma/Navajit Sarkar			07/12/2021					US#2287945 : Adding Authentication Data in the Interaction Record
 * 2.12             Ankit Avula                                     08/10/2021                 User story -2153743 : Adding Get Interaction Button in the Interaction Log Section and Fetching the Latest Interactions
 * 2.13             Anil Kumar                                      08/10/2021                 User story -2153743 : Fetching the Latest Interactions and Implementing Switch Logic
 * 2.14             Surendra Vemulapalli                            11/17/2021                 User story -US2793086 : Removed Member id 9 digits limit
 * 2.15             Sahil/Navajit                                   12/16/2021                 User story -US2704639 : copy the call details like UUID, Genesys Interaction ID in new interaction
 * 2.16             Harshada Kamble                                 01/05/2023                 US-3959734 : Salesforce Genesys CRM Authentication Status should not change.
 * 2.17             Harshada Kamble                                 02/03/2023                 US 3979946: T1PRJ0036776: PCC VOC Survey Transfer Identification 
 * * 2.18             Harshada Kamble/Anil Pavithran                03/09/2023                 US 2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
 ***************************************************************************************************************************/

public with sharing class HUMInteractionLogComponentControllerV2
{
    //Set for storing Interacting About member ids for deletion.
    private set<Id> setInqAbtIds= new set<Id>();

    //Creating map for holding "Inquired About" vs its interaction Member type.
    public map<id, String> mapMemberInteractionType = new map<id, String>();
 
    //Creating map for holding "Inquired About" v/s its interaction Member detail object.
    public map<id, Interaction_Member__c> mapMemberInteraction =  new map<id, Interaction_Member__c>();
    
    protected string uniqueVar = '';


    /*
     * Method Name   :    HUMInteractionLogComponentControllerV2()
     * Description   :    This is a constructor for the class.   
     * return Type   :    None
     * Parameters    :    None
     */
    public HUMInteractionLogComponentControllerV2()
    {
        //Calling function to initialize variables.
        initializeVariables();
    }
    // Boolean variable for switch
    public boolean validateCRMSwitch{get;set;}

    public boolean Switch_2793086{get;set;}
    
    //Boolean variable for Storing status of display of Update button pop up.
    public boolean bDisplayPopUp{get;set;}

    //Storing Interaction Id created on the Interaction Log Component.
    public String sInteractionId{get; set;}
   
    //Storing Account Id in case of unknown Member/Provider account is selected.
    public String sAccName {get; set;}  

    //Storing Interaction Member record.  
    public Interaction_Member__c oHUMIntMembers {get; set;}

    //Storing Account object Id when Unknown Member/Provider/Group button is clicked
    public String sAccId{get; set;} 

    //Storing true/false Value. All Interaction Members are saved: true else: false
    public boolean bSavedOrUnsavesMember {get; set;}

    //Holds Selected value of "inquired About" field so as to open Member detail page.  
    public String sSelMember {get; set;} 

    //Stores Interction object, so as to show Interaction Details on screen.
    public interaction__c oFetchInteraction {get; set;} 

    //US-3959734 :Stores temporary details to Interction object, so as to show Interaction Details on screen.
    public interaction__c oFetchInteraction1 {get; set;} 

    //Storing Id for Case opened.
    public String sNewCaseId {get; set;}

    //Holds status whether interaction created or not.
    public boolean bIntCreationStatus{get;set;}

    //Creating select list for showing Inquired About values.
    public List<SelectOption> lstSelectOptionIntAbt{get;set;}

    //Holds boolean value whether to show Error message or not.
    public boolean bRenderErrorMsg{get;set;}
    
    //Hols boolean value to show Update button on the screen.
    public boolean bShowUpdate{get;set;}

    public boolean bGetAccess{get;set;}

    public map<Id,boolean> mapRecordHaveAccess {get;set;}
    
    //Holds a boolean value to show whether the Interaction has associated case or not.
    public boolean bCaseInteraction {get;set;}
    
    //Holds the value of the selected interaction about from the update pop-up
    public string sSelectedValue{get; set;}

    //Holds the boolean value to show if apex page has error or not
    public Boolean hasError { get { return ApexPages.hasMessages(); }set; }
        
    //Holds Controller instance passed as Attribute to the Component.
    public HUMSearchControllerV2 oHUMSearchController 
    { 
        get; 
        set 
        {
            if (value != null) 
            {
                oHUMSearchController = value;
                oHUMSearchController.interactionLogController = this;                
            }
        }
    } 
    //US#2287945 - Boolean value to show the authentication checkbox as enabled or disabled 
    public boolean authCheck{get;set;}
    //US#2287945 - To check if a user is a genesys user or not
    public boolean isGenesysUser{get;set;}
    //US#2287945  -Boolean value for the switch to show the authentication checkbox as enabled or disabled
    public boolean mf3Switch{get;set;} 
    //US#2191493 - Boolean value for the switch to enable or disable caller type population functionality
    public boolean callerTypePopSwitch{get;set;}
      
    public list<Interaction__c> lstIntsave;
     
    public Boolean Switch_2230000 {
        get{
           return  PhoneBook_H_HUM.isCRMFunctionalityON('2230000');
        }set;
    }
    //US#2704639-Switch for saving different attributes to interaction record 
     public Boolean Switch_2704639 {get;set;}

    //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
    public Boolean multiMemberAuthSwitch {       
        get{
        return  PhoneBook_H_HUM.isCRMFunctionalityON('2760646');
        }
        set;
    }
    
    //US#2287945 - Label to show based on the authentication checkbox value
    public string authCheckLabel{
        get { 
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            if(multiMemberAuthSwitch)
                return (oHUMIntMembers.Authenticated__c == true) ? 'Authenticated' : 'UnAuthenticated'; 
            else
                return (oFetchInteraction.Authenticated__c == true) ? 'Authenticated' : 'UnAuthenticated'; 
        }
        set;
    }
    /*
     * Method Name   :    getThis() 
     * Description   :    This method retunrs the Controller's instance to the calling page.   
     * return Type   :    HUMInteractionLogComponentController
     * Parameters    :    None
     */
    public HUMInteractionLogComponentControllerV2 getThis()
    {
        return this;
    }


/*
     * Method Name   :    updateAuth()
     * Description   :    This method is called to update the authCheck variable.   
     * return Type   :    Void
     * Parameters    :    None
     */
    public void updateAuth()
    {	
        if(multiMemberAuthSwitch){
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            if(oHUMIntMembers.Authenticated__c == true)authCheck=true;
            else authCheck=false;   
        }
        else{	
            if(oFetchInteraction.Authenticated__c == true)authCheck=true;
            else authCheck=false;  
        }            
    }
    /*
     * Method Name   :    initializeVariables()
     * Description   :    This method is called from constructor to initialize Member variables being used in the class.   
     * return Type   :    Void
     * Parameters    :    None
     */
    private void initializeVariables()
    {
 validateCRMSwitch = HUMUtilityHelper.isCRMFunctionalityON('2153743');
  Switch_2793086 = HUMUtilityHelper.isCRMFunctionalityON('2793086');
  

        HUMSearchControllerV2 oHUMSearchController = new HUMSearchControllerV2();
        oFetchInteraction = new Interaction__C();
        //default Origin of 'Inbound Call'  
        if(UtilityHelper_H_AHT_HUM.isRSOUserHavingQuickStartAccess()) {
        	if(Schema.SObjectType.Interaction__C.fields.Interaction_Origin__c.isCreateable() && Schema.SObjectType.Interaction__C.fields.Interaction_Origin__c.isUpdateable()) oFetchInteraction.Interaction_Origin__c = HUMConstants__c.getInstance('INBCALL').StringValue__c;
        }
        
        
        if(lstSelectOptionIntAbt ==null)lstSelectOptionIntAbt = new  List<SelectOption>();
        oHUMIntMembers = new Interaction_Member__c();
        mapMemberInteraction = new map<id, Interaction_Member__c>();
        mapMemberInteractionType = new map<id, String>(); 
        bSavedOrUnsavesMember = true;
        bIntCreationStatus = false;
        bDisplayPopUp = false;
        bRenderErrorMsg = false;
        bShowUpdate = false;
        mapRecordHaveAccess = new map<Id,boolean>();
        bGetAccess = false;
        bCaseInteraction  = false;
        sSelectedValue = '';
        isGenesysUser = PhoneBook_H_HUM.isGenesysUser();
        mf3Switch=PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.AUTH_SWITCH);
        Switch_2704639 = PhoneBook_H_HUM.isCRMFunctionalityON('2704639');
        callerTypePopSwitch = PhoneBook_H_HUM.isCRMFunctionalityON('2191493');
           // Getting the User record and check if the User has Callcenter enabled
        List<User> userList = [SELECT CallCenterId FROM User WHERE id = :UserInfo.getUserId()];
        if(!userList.isEmpty() && userList[0].CallCenterId != null){
        	isGenesysUser = true;
        }
   
   
    }
     /*
     * Method Name   :    clear() 
     * Description   :    This method is called when "Clear button is hit on the VF page".
     *                    This method will clear interaction/Interaction Members.
     * return Type   :    Pagereference
     * Parameters    :    None
     */
    public Pagereference clear(){
        oFetchInteraction = new Interaction__C();
        if(UtilityHelper_H_AHT_HUM.isRSOUserHavingQuickStartAccess()) {
        	oFetchInteraction.Interaction_Origin__c = HUMConstants__c.getInstance('INBCALL').StringValue__c;
        }
        if(lstIntsave != NULL) lstIntsave.clear();
        mapMemberInteraction  = new map<id, Interaction_Member__c>();
        oHUMIntMembers  = new Interaction_Member__c();
        lstSelectOptionIntAbt =new  List<SelectOption>();
        oHUMSearchController.showCallCatergoryAlert = false;
        authCheck =false;//User Story 4725390: T1PRJ0036776- MMA - include interacting name OR interacting with AND about in the authentication logic
        //flushInteraction();
        return null;
    }

    /*
     * Method Name   :    save() 
     * Description   :    This method is called when "Save button is hit on the VF page".
     *                    This method will save interaction/Interaction Members.
     * return Type   :    Pagereference
     * Parameters    :    None
     */   
    public Pagereference save()
    {
        try
        {
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            Boolean tempAuthenticated; 

            if(multiMemberAuthSwitch)
                tempAuthenticated = oFetchInteraction.Authenticated__c ==true ? oFetchInteraction.Authenticated__c: oHUMIntMembers.Authenticated__c;
            else
                tempAuthenticated= oFetchInteraction.Authenticated__C;

            //List Created to store all Interaction Members pertaining to Interaction being saved.
            List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
            Interaction__C oInt;
            
            if(String.ISBLANK(oFetchInteraction.name))
            {
                oInt = new Interaction__C(Interacting_with__C = oFetchInteraction.Interacting_with__C,                                                    
                        Interacting_with_type__C = oFetchInteraction.Interacting_with_type__C,
                        Caller_name__C = oFetchInteraction.Caller_name__C,
                        Interaction_origin__C = oFetchInteraction.Interaction_origin__C,
                        Call_Category__c = oFetchInteraction.Call_Category__c,//US 2082644 Added new field Call Category on interaction__c object and saving the value
                        uuid__c = oFetchInteraction.uuid__c,
                        Authenticated__c = tempAuthenticated,//US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        Call_Category_Description__c = oFetchInteraction.Call_Category_Description__c,//US#2704639-to store the value of Call Cat Description in the Interaction record
                        Survey_ID__c = oFetchInteraction.Survey_ID__c,//US#2704639 to store the value of Survey Id in the Interaction record
                        Genesys_Interaction_ID__c = oFetchInteraction.Genesys_Interaction_ID__c, //US#2704639 to store the value of Genesys Interaction Id in the Interaction record
                        Call_Data__c = oFetchInteraction.Call_Data__c //US#2704639 to store the value of Call Data in the Interaction record
                );
            }
            else
            {
                //US-3959734 : Salesforce Genesys CRM Authentication Status should not change 
                if(PhoneBook_H_HUM.isCRMFunctionalityON('3959734')){
                    string setRecordIdAccCase = system.CurrentPageReference().getParameters().get(HUMConstants.setRecordIdAccCase);
                    if((setRecordIdAccCase != null && String.isNotBlank(setRecordIdAccCase)) && setRecordIdAccCase.equalsIgnoreCase(HUMConstants.setRecordIdAccCase)){
                        //Re-Querying Interaction record saved,for fixing Authentication Status change issue on account details page after disconnect or transfer. 
                        if(oFetchInteraction.Id!=null)
                        {
                            //Added Authenticated__c field in the query to populate the value in the panel
                            oFetchInteraction1 = [SELECT id, name, Interacting_with__c,Authenticated__c, Caller_name__c, Interacting_With_type__c, Interaction_origin__c, uuid__c,Call_Category_Description__c,Genesys_Interaction_ID__c,Survey_ID__c,Call_Data__c
                                                from interaction__C where id = :oFetchInteraction.id];

                            if(oFetchInteraction1.Authenticated__c == True){
                                oFetchInteraction.Authenticated__c = oFetchInteraction1.Authenticated__c;

                                //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                                if(multiMemberAuthSwitch){
                                    onChangeInqAbtw();
                                    if(oHUMIntMembers.Authenticated__C == true)
                                        oHUMIntMembers.Authenticated__c = oHUMIntMembers.Authenticated__c;
                                }
                            }
                        }
                    }
                }
                oInt = new Interaction__C(id = oFetchInteraction.id,
                        Interacting_with__C = oFetchInteraction.Interacting_with__C,                                                   
                        Interacting_with_type__C = oFetchInteraction.Interacting_with_type__C,
                        Caller_name__C = oFetchInteraction.Caller_name__C,
                        Interaction_origin__C = oFetchInteraction.Interaction_origin__C ,
                        Call_Category__c = oFetchInteraction.Call_Category__c,//US 2082644 Added new field Call Category on interaction__c object and saving the value
                        uuid__c = oFetchInteraction.uuid__c,                                        
                        Authenticated__c = tempAuthenticated,//US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        Call_Category_Description__c = oFetchInteraction.Call_Category_Description__c,//US#2704639
                        Survey_ID__c = oFetchInteraction.Survey_ID__c,//US#2704639
                        Genesys_Interaction_ID__c = oFetchInteraction.Genesys_Interaction_ID__c, //US#2704639
                        Call_Data__c = oFetchInteraction.Call_Data__c //US#2704639                                    
                );
            }
            
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            if(multiMemberAuthSwitch)
            {   
                //User Story 4725390: T1PRJ0036776- MMA - include interacting name OR interacting with AND about in the authentication logic 
                if(oHUMIntMembers.Authenticated__c ==false ||(oHUMIntMembers.Authenticated__c ==true && (oInt.Interacting_with__C != null || oInt.Caller_name__C != null ) && oHUMIntMembers.interacting_about__C != null))
                {
                    saveInteractionAndRequery(oInt);

                    //Calling function for Upserting Interaction Member records.
                    if(bIntCreationStatus == true){
                        saveIntMembers();
                        updateAuth(); //US#2287945-updating authcheck variable to populate in the UI
                    }
                    else 
                    {
                        authCheck=false; //US#2287945-updating authcheck variable to populate in the UI
                    }   
                }   
                else {

                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.Label.HUM_INTERACTION_VALIDATION));
                    bRenderErrorMsg =true;
                    oHUMIntMembers.Authenticated__c = false;
                    authCheckHandler();
                    updateAuth();
                }
            }
            else{
                
                saveInteractionAndRequery(oInt);
                
                //Calling function for Upserting Interaction Member records.
                if(bIntCreationStatus == true){
                    saveIntMembers();
                    updateAuth(); //US#2287945-updating authcheck variable to populate in the UI
                }
                else 
                {
                    authCheck=false; //US#2287945-updating authcheck variable to populate in the UI
                }   
            }
             
        }
        Catch(Exception e)
        {             
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','save');
        }
        return null;      
    }
     /*
     * Method Name   :    getLatestInteraction() 
     * Description   :    This method is called when "Get Interction button is hit on the VF page".
     *                    This method will Fetch Latest interaction/Interaction Members for the Logged In User.
     * return Type   :    Pagereference
     * Parameters    :    None
     */ 
    
    public void getLatestInteraction(){
        try{
            List<Interaction_Member__c> lstMbr = new List<Interaction_Member__c> ();
            List<Interaction__c> lstInt = [Select Id,name,Interacting_with__C ,
                                             Caller_name__C,Interacting_With_type__c,
                                             Interaction_origin__C,uuid__c,call_category__c,Authenticated__c,
                                             Call_Category_Description__c,Genesys_Interaction_ID__c,Survey_ID__c, Call_Data__c
                                             FROM Interaction__c where createdbyId = :UserInfo.getUserId() order by CreatedDate desc Limit 1];
            if(lstInt != null && !lstInt.isEmpty()){
                oFetchInteraction = lstInt[0];

                //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                if(multiMemberAuthSwitch){
                    lstMbr = [Select Id,Interacting_About__r.Name,Interacting_About_Type__c,Interaction__c,Authenticated__c from Interaction_Member__c 
                                                    where Interaction__c =:lstInt[0].Id and createdby.Name =: system.label.SoftPhoneIntMemberOwner order by CreatedDate desc];
                }
                else
                {
                    lstMbr = [Select Id,Interacting_About__r.Name,Interacting_About_Type__c,Interaction__c from Interaction_Member__c 
                                                      where Interaction__c =:lstInt[0].Id and createdby.Name =: system.label.SoftPhoneIntMemberOwner order by CreatedDate desc];
                }

                if(lstMbr != null && !lstMbr.isEmpty()){
                    //Creating select option list for "Interacting About" drop down on Interaction panel.                                                         
                    lstSelectOptionIntAbt = new List<SelectOption>(); 
                    for(Interaction_Member__c oHUMMember :lstMbr)
                    {
                        lstSelectOptionIntAbt.add(new selectOption(oHUMMember.interacting_about__C,oHUMMember.interacting_about__r.name));
                        if(mapMemberInteraction != null) mapMemberInteraction.put(oHUMMember.interacting_about__C, oHUMMember); 
                        if(mapMemberInteractionType != null) mapMemberInteractionType.put(oHUMMember.interacting_about__c,oHUMMember.Interacting_About_Type__c);
                    }
                    //Setting selected Member in drop down for interacting about
                    if(lstMbr != null && lstMbr.size()>0)
                    {
                        sSelMember = lstMbr[0].interacting_about__C;
                        oHUMIntMembers.interacting_about__C = lstMbr[0].interacting_about__C;

                        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        if(multiMemberAuthSwitch){
                            oHUMIntMembers.Authenticated__c = lstMbr[0].Authenticated__c;
                        }
                    }
                    changeInteractionMemberDetail(); 
                }
                bShowUpdate = true; 
            }
           
            //US2793086 added logic to check interation record exists for current user
            if(Switch_2793086){
                if(!lstInt.isEmpty() && string.isNotBlank(lstInt[0].Call_Data__c)){
                map<String,Object> mapCallData = (map<String,Object>) JSON.deserializeUntyped(lstInt[0].Call_Data__c);
                oHUMSearchController.transferJsonObject = buildDataJsonString(mapCallData);
                oHUMSearchController.stopPopulateInt = true;  
                oHUMSearchController.softPhoneRoutingSearch();
            }
            
            }else{
            if(string.isNotBlank(lstInt[0].Call_Data__c)){
                map<String,Object> mapCallData = (map<String,Object>) JSON.deserializeUntyped(lstInt[0].Call_Data__c);
                oHUMSearchController.transferJsonObject = buildDataJsonString(mapCallData);
                oHUMSearchController.stopPopulateInt = true;  
                oHUMSearchController.softPhoneRoutingSearch();
            }
            } 
            
            
        }
        catch(Exception e){
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','getLatestInteraction()');
        }
    }
    
    public string buildDataJsonString(map<String,Object> mapCallData){
        String callDataCode = '';
        String seperator = '';
      
        if(mapCallData.containsKey('InteractingWithType') && String.isNotBlank((String)(mapCallData.get('InteractingWithType')))){
            String INTWITHTYPE = ((String)(mapCallData.get('InteractingWithType'))).substring(0,1);
            callDataCode += seperator + HUMConstants.INTWITHTYPE+'='+ INTWITHTYPE;
            seperator = '&';
            if(INTWITHTYPE == 'A'){
    	        if(String.isNotBlank((String)(mapCallData.get('InteractingWithID')))){
                	//callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('AccountName2').Value__c +'='+(String)(mapCallData.get('InteractingWithID'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithLastName')))){
                	callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c +'='+(String)(mapCallData.get('InteractingWithLastName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithID')))){
                	callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__c +'='+(String)(mapCallData.get('InteractingWithID'));
                }
            }
            if(INTWITHTYPE == 'P'){
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithID')))){
                	callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__c +'='+(String)(mapCallData.get('InteractingWithID'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithNPIId')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c +'='+(String)(mapCallData.get('InteractingWithNPIId'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithFirstName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c +'='+(String)(mapCallData.get('InteractingWithFirstName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithLastName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c +'='+(String)(mapCallData.get('InteractingWithLastName'));
                }
            }
            if(INTWITHTYPE == 'G'){
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithID')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber2').Value__c +'='+(String)(mapCallData.get('InteractingWithID'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithFirstName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c +'='+(String)(mapCallData.get('InteractingWithFirstName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithLastName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c +'='+(String)(mapCallData.get('InteractingWithLastName'));
                }
            }
            if(INTWITHTYPE == 'M'){
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithID')))){
                      //US2793086 removed 9 digits limit on member id in member search tab
                        if(Switch_2793086){
                             callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('MemberId2').Value__c +'='+(String)(mapCallData.get('InteractingWithID'));
                   		 
                 		}else{
                        callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('MemberId2').Value__c +'='+((String)(mapCallData.get('InteractingWithID'))).substring(0,9);
                		}
                	}                
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithFirstName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('FirstName2').Value__c +'='+(String)(mapCallData.get('InteractingWithFirstName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithLastName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('LastName2').Value__c +'='+(String)(mapCallData.get('InteractingWithLastName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingWithDOB')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('DOB2').Value__c +'='+(String)(mapCallData.get('InteractingWithDOB'));
                }
                //US2793086 added suffix\dependent code from intergation record
                if(Switch_2793086){
                 if(String.isNotBlank((String)(mapCallData.get('InteractingWithDepCode')))){
                    callDataCode += seperator + 'sIntWithSuffixId' +'='+(String)(mapCallData.get('InteractingWithDepCode'));
                }
                }
            }
        }
        if(mapCallData.containsKey('InteractingAboutType') && String.isNotBlank((String)(mapCallData.get('InteractingAboutType')))){
            String INTABOUTTYPE = ((String)(mapCallData.get('InteractingAboutType'))).substring(0,1);
            callDataCode += seperator + HUMConstants.INTABOUTTYPE+'='+ INTABOUTTYPE;
            seperator = '&';
            if(INTABOUTTYPE == 'A'){
    	        if(String.isNotBlank((String)(mapCallData.get('InteractingAboutID')))){
                	//callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('AccountName').Value__c +'='+(String)(mapCallData.get('InteractingWithID'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutLastName')))){
                	callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c +'='+(String)(mapCallData.get('InteractingAboutLastName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutID')))){
                	callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__c +'='+(String)(mapCallData.get('InteractingAboutID'));
                }
            }
            if(INTABOUTTYPE == 'P'){
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutID')))){
                	callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__c +'='+(String)(mapCallData.get('InteractingAboutID'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutNPIId')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('NpiId').Value__c +'='+(String)(mapCallData.get('InteractingAboutNPIId'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutFirstName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c +'='+(String)(mapCallData.get('InteractingAboutFirstName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutLastName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c +'='+(String)(mapCallData.get('InteractingAboutLastName'));
                }
            }
            if(INTABOUTTYPE == 'G'){
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutID')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber').Value__c +'='+(String)(mapCallData.get('InteractingAboutID'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutFirstName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c +'='+(String)(mapCallData.get('InteractingAboutFirstName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutLastName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c +'='+(String)(mapCallData.get('InteractingAboutLastName'));
                }
            }
            if(INTABOUTTYPE == 'M'){
                
               if(String.isNotBlank((String)(mapCallData.get('InteractingAboutID'))))
               {
                    //US2793086 removed 9 digits limit on member id in member search tab
                  if(Switch_2793086){
                      callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('MemberId').Value__c +'='+(String)(mapCallData.get('InteractingAboutID'));
                  
                  }else{
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('MemberId').Value__c +'='+((String)(mapCallData.get('InteractingAboutID'))).substring(0,9);
                 } 
               }
                                
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutFirstName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('FirstName').Value__c +'='+(String)(mapCallData.get('InteractingAboutFirstName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutLastName')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('LastName').Value__c +'='+(String)(mapCallData.get('InteractingAboutLastName'));
                }
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutDOB')))){
                    callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance('DOB').Value__c +'='+(String)(mapCallData.get('InteractingAboutDOB'));
                }
                //US2793086 added suffix\dependent code from intergation record
                if(Switch_2793086){
                if(String.isNotBlank((String)(mapCallData.get('InteractingAboutDepCode')))){
                    callDataCode += seperator + 'sIntAboutSuffixId' +'='+(String)(mapCallData.get('InteractingAboutDepCode'));
                }
                }
            }
            if(String.isNotBlank((String)(mapCallData.get('CallCategory')))){
                callDataCode += seperator + SOFTPHONE_PRPID_HUM__c.getInstance( 'CallCategory').Value__c +'='+(String)(mapCallData.get('CallCategory'));
            }
               
        }
        return callDataCode;
    }
    /*
     * Method Name   :    getInteractionDetails()  
     * Description   :    This method retrieves the Interaction record for the Interaction name 
                          provided in cutsom look up field.    
     * return Type   :    void
     * Parameters    :    None
     */ 
    public void getInteractionDetails()
    {
        try
        { 
            //US#2287945-Fetching Interaction Details so as to populate the details on the screen added Authenticated__c field
            List<Interaction__C> lstInt =   [SELECT Id,name,Interacting_with__C , Authenticated__c,Call_Category_Description__c,
                                            Genesys_Interaction_ID__c,Survey_ID__c,Caller_name__C,Interacting_With_type__c,
                                             Interaction_origin__C,uuid__c,Call_Category__c, Call_Data__c
                                             FROM Interaction__C 
                                             WHERE id = :sInteractionId];
            if(lstInt != null && !lstInt.isEmpty())
            {
                oFetchInteraction = lstInt[0];
                //Fetching Interaction Member details for corresponding Interaction.                                            
                List<Interaction_Member__c> lstSelectedMembers = HUMWithoutSharingQueryHelper.fetchInteractionMembers(sInteractionId);
                if(lstSelectedMembers != null && !lstSelectedMembers.isEmpty())
                {                   
                    //Creating select option list for "Interacting About" drop down on Interaction panel.                                                         
                    lstSelectOptionIntAbt = new List<SelectOption>(); 
                    for(Interaction_Member__c oHUMMember :lstSelectedMembers )
                    {
                        lstSelectOptionIntAbt.add(new selectOption(oHUMMember.interacting_about__C,oHUMMember.interacting_about__r.name));
                        if(mapMemberInteraction != null) mapMemberInteraction.put(oHUMMember.interacting_about__C, oHUMMember); 
                        if(mapMemberInteractionType != null) mapMemberInteractionType.put(oHUMMember.interacting_about__C,oHUMMember.Interacting_About_Type__c);
                    }
                    //Setting selected Member in drop down for interacting about
                    if(lstSelectedMembers != null && lstSelectedMembers.size()>0)
                    {

                        sSelMember = lstSelectedMembers[0].interacting_about__C;
                        oHUMIntMembers.interacting_about__C = lstSelectedMembers[0].interacting_about__C;

                        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        if(multiMemberAuthSwitch){
                            oHUMIntMembers.Authenticated__c = lstSelectedMembers[0].Authenticated__c;
                        }
                    }
                    else
                    {
                        oHUMIntMembers = new Interaction_member__C();
                    } 
                    //As Interaction is saved already, setting both boolean variables to true.
                    bSavedOrUnsavesMember = true;
                    bIntCreationStatus = true;

                    //Calling This function to adjust selected Interacting About as per member detail page.
                    changeInteractionMemberDetail(); 
                    
                    updateAuth();  //US#2287945-update authcheck variable to update the UI                  
                    
                }  
                else
                {
                    bSavedOrUnsavesMember = true;
                    bIntCreationStatus = true;
                    lstSelectOptionIntAbt = new List<SelectOption>();
                    changeInteractionMemberDetail();
                      
                    updateAuth(); //US#2287945-update authcheck variable to update the UI
                    
                }
                bShowUpdate = true;   
            }
            else
            {
                lstSelectOptionIntAbt = new  List<SelectOption>();
                mapMemberInteraction = new map<id, Interaction_Member__c>();
                mapMemberInteractionType = new map<id, String>();
                oHUMIntMembers.interacting_about_type__C = system.label.HUMDefaultDropDownVal;

            }
            dataSecurity();
            if(mapRecordHaveAccess.containskey(sSelMember) && sSelMember != null)bGetAccess = mapRecordHaveAccess.get(sSelMember);
        }
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','getInteractionDetails');
        }                                             
    }

    /*
     * Method Name   :    SaveAndNew()  
     * Description   :    This method flushes the Interaction details from the Interaction log panel.  
     * return Type   :    Pagereference
     * Parameters    :    None
     */ 
    public Pagereference SaveAndNew()
    {    
        save();
        if(bIntCreationStatus == true) flushInteraction();
        return null;
    }

    /*
     * Method Name   :    flushInteraction()  
     * Description   :    This method flushes the Interaction inputs fields from the Interaction log panel.  
     *                    This method is called as reply to event fired from the page. i.e. if save and new button
     *                    is clicked from Interaction log pages opened.
     * return Type   :    Pagereference
     * Parameters    :    None
     */
    public Pagereference flushInteraction()
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
            oFetchInteraction.uuid__c = tempUUID;
            oFetchInteraction.Interaction_Origin__c = tempOrigin;
            oFetchInteraction.Call_Category_Description__c = tempCallCattDesc;
            oFetchInteraction.Survey_ID__c = tempSurveyId;
            oFetchInteraction.Genesys_Interaction_ID__c = tempInteractionId;
            oFetchInteraction.Authenticated__c = tempAuth;
            oFetchInteraction.Call_Data__c = tempCallData;
        }
        else{
            oFetchInteraction = new Interaction__C();            
        }      
        //default Origin of 'Inbound Call'  
        if(UtilityHelper_H_AHT_HUM.isRSOUserHavingQuickStartAccess()) {
        	oFetchInteraction.Interaction_Origin__c = HUMConstants__c.getInstance('INBCALL').StringValue__c;
        }
        //US 2082644 setting the call category value to GNA on save and new button click.
        if(oHUMSearchController.showCallCatergoryAlert != null && oHUMSearchController.showCallCatergoryAlert){
            oFetchInteraction.Call_Category__c = System.Label.US2082644GNACallCategoryLabel;
        }
        if(lstIntsave != NULL) lstIntsave.clear();
        mapMemberInteraction  = new map<id, Interaction_Member__c>();
        oHUMIntMembers  = new Interaction_Member__c();
        lstSelectOptionIntAbt =new  List<SelectOption>(); 
        sSelMember = '';        
        return null;
    }

    /*
     * Method Name   :    onChangeInput()  
     * Description   :    This method is called as action method to the apex action function
     *                    'ON change' of Name field which is a free text field on the interaction log, so that
     *                    Interaction record gets updated when Name is added/changed in the interaction log panel.
     * return Type   :    Pagereference
     * Parameters    :    None
     */ 
    public Pagereference onChangeInput()
    {
        if(oFetchInteraction != null)
        {
            if(oFetchInteraction.id == null)
            {
                saveInteractionAndRequery(oFetchInteraction);
            }
        }
        return null;
    }

    /* 
     * Method Name   :    populateInteractionDetails() 
     * Description   :    This Method populates following values in the interaction log page.
     *                    1. Interacting About 
     *                    2. Interacting About Type
     * return Type   :    PageReference  
     * Parameters    :    Id, String
     */
    public pageReference populateInteractionDetails(Id sInqAbtId, String sTabType) 
    {
        try
        {
            //As new Interaction Member is added, update button is shown on screen.
            bShowUpdate = true; 
            //Fetching details of the Account using a without sharing class and  selected as "Inquired About" in search results.
            List<Account> lstAcc = HUMWithoutSharingQueryHelper.fetchAccounts(sInqAbtId);
            if(lstAcc != null && !lstAcc.isEmpty())
            {
                //Setting Selected value in drop down and account name for Tab to be opened.
                sSelMember = lstAcc[0].id;  
                sAccName =  lstAcc[0].Name; 
                //Logic to open Detail record page of Account if Interacting with and About are also not provided.
                if(oFetchInteraction.Interacting_With__c==null && String.isEmpty(oFetchInteraction.Caller_Name__c) && lstSelectOptionIntAbt.size()==0)
                {
                   if(String.isEmpty(oFetchInteraction.Interaction_Origin__c))
                   {
                        bGetAccess = checkAccessWithoutInteraction(sInqAbtId);
                        bIntCreationStatus=true;
                        HUMSearchControllerV2.bPopup = false;
                        return null;
                   }
                   else
                   {
                        bGetAccess = checkAccessWithoutInteraction(sInqAbtId);
                        HUMSearchControllerV2.bPopup = true;
                        bIntCreationStatus=true;
                        return null;
                   }
                }
                else if(oFetchInteraction.Interacting_With__c==null && String.isEmpty(oFetchInteraction.Caller_Name__c) && lstSelectOptionIntAbt.size()>0)
                {
                    dataSecurity();
                    if(mapRecordHaveAccess.containskey(sSelMember) && sSelMember != null)bGetAccess = mapRecordHaveAccess.get(sSelMember);
                    HUMSearchControllerV2.bPopup = true;
                    bIntCreationStatus=true;
                    return null;
                }
                else if(oFetchInteraction != null)
                {           
                    HUMSearchControllerV2.bPopup = false;            
                    saveInteractionAndRequery(oFetchInteraction);
                }
                //End of Logic to open Detail record page of Account if Interacting with and About are also not provided.
                
                if(mapMemberInteraction != null && mapMemberInteractionType != null &&
                        bIntCreationStatus == true)
                {
                    if(mapMemberInteraction != null && !mapMemberInteraction.containskey(lstAcc[0].id))
                    {
                        List<SelectOption> lstSelectOptionIntAbtTemp = lstSelectOptionIntAbt;
                        lstSelectOptionIntAbt = new List<SelectOption>();
                        lstSelectOptionIntAbt.add(new Selectoption(lstAcc[0].id,lstAcc[0].name));  
                        for(SelectOption oSelOp :lstSelectOptionIntAbtTemp)
                        {
                            lstSelectOptionIntAbt.add(oSelOp);
                        }
                        dataSecurity();
                        if(mapRecordHaveAccess.containskey(sSelMember) && sSelMember != null)bGetAccess = mapRecordHaveAccess.get(sSelMember);
                        //Assigning selected values to the inquired about field.     
                        oHUMIntMembers.interacting_about__C = lstAcc[0].id;
                        oHUMIntMembers.Interacting_About_Type__c = fnGetInqAbtType(lstAcc[0].recordtype.Name,sTabType);

                        //US2760646: changing auth on first or last name click
                        if(multiMemberAuthSwitch){
                            if(sInqAbtId != null && sTabType != null){
                               oHUMIntMembers.Authenticated__C =false;
                            }
                        }

                        /*Populating maps for storing :
                         1. Interacting ABout id-Interaction Member object.
                         2. Interacting About Id- Interacting About type.*/
                        Interaction_Member__c oIntMemberTemp = new Interaction_Member__c ();
                        oIntMemberTemp.Interacting_About_Type__c = oHUMIntMembers.Interacting_About_Type__c;
                        oIntMemberTemp.interacting_about__C= oHUMIntMembers.interacting_about__C;
                        mapMemberInteraction.put(lstAcc[0].id,  oIntMemberTemp); 
                        mapMemberInteractionType.put(lstAcc[0].id,oIntMemberTemp.Interacting_About_Type__c);
                    }
                    else if(mapMemberInteraction.containskey(lstAcc[0].id))
                    {
                        //US4723377 - T1PRJ0036776: MMA - Ability To Save Authentication Status by Clicking on Account Name (Genesys)
                        if(multiMemberAuthSwitch){
                            if(oHUMIntMembers.interacting_about__C != sInqAbtId)
                            {
                                oHUMIntMembers.interacting_about__C = lstAcc[0].id;
                                onChangeInqAbtw();
                            }
                        }
                    }
                }
                //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                if(multiMemberAuthSwitch)
                    oHUMIntMembers.interacting_about__C = lstAcc[0].id;

                //Calling Function to save Interaction Members.
                if(bIntCreationStatus)saveIntMembers();                         
            }
        }
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','populateInteractionDetails');
        }    
        return null;
    }

    /*
     * Method Name   :   populateInteractionWithDetails()
     * Description   :   This Method  populates details of 'Interacting With' in Interaction Log panel.
     *                   it stores 2 types of values:
     *                    1. Interacting With
     *                    2. Interacting With Type
     * return Type   :    pageReference
     * Parameters    :    Id, String
     */
    public pageReference populateInteractionWithDetails(Id sInteractingWithId, String sTabType) 
    {
        try
        {
            //Query on accounts in without sharing class
            List<Account> lstAcc = HUMWithoutSharingQueryHelper.fetchAccounts(sInteractingWithId);
            if(lstAcc != null && !lstAcc.IsEmpty())
            {
                oFetchInteraction.Interacting_With_type__c = fnGetInqAbtType(lstAcc[0].recordtype.Name,sTabType);
                oFetchInteraction.Interacting_with__C =  lstAcc[0].id;   
            }
        }
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','populateInteractionWithDetails');
        }   
        return null;
    }

    /*
     * Method Name   :    populateInteractionAboutDetails() 
     * Description   :    This method populates Interacting About details on the Interaction log panel,
     *                      on click of Record link in the search results section.
     *                    it stores 2 types of values:
     *                    1. Inquired About
     *                    2. Inquired About Type
     * return Type   :    Pagereference
     * Parameters    :    Id, String
     */
    public pageReference populateInteractionAboutDetails(Id sInqAbtId, String sTabType) 
    {
        try
        {
            //Query on accountin without sharing class
            List<Account> lstAcc =  HUMWithoutSharingQueryHelper.fetchAccounts(sInqAbtId);
            if(lstAcc != null && !lstAcc.isEmpty())
            {
                //If Interacting About is not present in the drop-down earlier, then Adding it here.
                if(mapMemberInteraction != null && mapMemberInteractionType != null)
                {
                    if(!mapMemberInteraction.containskey(lstAcc[0].id))
                    {
                        List<SelectOption> lstSelectOptionIntAbtTemp = lstSelectOptionIntAbt;
                        lstSelectOptionIntAbt = new List<SelectOption>();
                        lstSelectOptionIntAbt.add(new Selectoption(lstAcc[0].id,lstAcc[0].name));

                        //Making variables false as new interaction member added and yet to be saved.
                        bSavedOrUnsavesMember = false;  
                        for(SelectOption oSelOp :lstSelectOptionIntAbtTemp)
                        {
                            lstSelectOptionIntAbt.add(oSelOp);
                        }

                        //Assigning selected values to the inquired about field.     
                        oHUMIntMembers.interacting_about__C = lstAcc[0].id;
                        oHUMIntMembers.Interacting_About_Type__c = fnGetInqAbtType(lstAcc[0].recordtype.Name,sTabType);

                        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        if(multiMemberAuthSwitch){
                            oHUMIntMembers.Authenticated__c =false;
                        }

                        //Populating maps for newly added Interacting About details.
                        Interaction_Member__c oIntMemberTemp = new Interaction_Member__c ();
                        oIntMemberTemp.Interacting_About_Type__c = oHUMIntMembers.Interacting_About_Type__c;
                        oIntMemberTemp.interacting_about__C= oHUMIntMembers.interacting_about__C;

                        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        if(multiMemberAuthSwitch){
                            oIntMemberTemp.Authenticated__c = oHUMIntMembers.Authenticated__c;
                        }

                        mapMemberInteraction.put(lstAcc[0].id,  oIntMemberTemp);
                        mapMemberInteractionType.put(lstAcc[0].id,oIntMemberTemp.Interacting_About_Type__c); 
                    }
                    else if(mapMemberInteraction.containskey(lstAcc[0].id))
                    {
                        //US4723377 - T1PRJ0036776: MMA - Ability To Save Authentication Status by Clicking on Interacting with and about button (Genesys)
                        if(multiMemberAuthSwitch){
                            oHUMIntMembers.interacting_about__C = lstAcc[0].id;
                            onChangeInqAbtw();
                        }
                    }
                }
                //Assigning selected Member in the drop down to latest selected member.
                datasecurity();
                sSelMember = lstAcc[0].id;
                if(mapRecordHaveAccess.containskey(sSelMember) && sSelMember != null)bGetAccess = mapRecordHaveAccess.get(sSelMember); 
                bShowUpdate = true;                        
            }
        }
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','populateInteractionDetails');
        } 
        return null;
    }

    /*
     * Method Name   :    changeInteractionMemberDetail() 
     * Description   :    This Method adjusts "Interacting About Type" as per selected "Interacting About value".
     * return Type   :    void
     * Parameters    :    None
     */
    public void changeInteractionMemberDetail()
    {
        try
        {
            if(mapMemberInteractionType != null)
            {
                if(mapMemberInteractionType.containsKey(oHUMIntMembers.interacting_about__C))
                {
                    oHUMIntMembers.Interacting_About_Type__c = mapMemberInteractionType.get(oHUMIntMembers.interacting_about__C);
                }
                else
                {
                    oHUMIntMembers.Interacting_About_Type__c = system.label.HUMDefaultDropDownVal;
                }
                sSelMember = oHUMIntMembers.Interacting_about__C;
                if(mapRecordHaveAccess.containskey(sSelMember) && sSelMember != null)
                {
                    bGetAccess = mapRecordHaveAccess.get(sSelMember);
                    sAccName = [Select Name from Account where id=:sSelMember].Name;
                }
            }
        }
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','changeInteractionMemberDetail');
        }
    }

    /*
     * Method Name   :    saveIntMembers() 
     * Description   :    This method saves the Interaction Members records pertaining to Interaction being saved.
     * return Type   :    void
     * Parameters    :    None
     */
    private void saveIntMembers()
    {
        try
        {
            List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
            if(mapMemberInteraction != null)
            {
                for(Id sIntMemberId : mapMemberInteraction.keyset())
                {
                    //Associating Interaction Member record to the Interaction being saved on the page.
                    if(mapMemberInteraction.get(sIntMemberId).Interaction__c == null)
                        mapMemberInteraction.get(sIntMemberId).Interaction__c = oFetchInteraction.id;
                        //Populating list of Interaction Members to upsert.
                    if(mapMemberInteraction.get(sIntMemberId).Interacting_About__c!= null && mapMemberInteraction.get(sIntMemberId).Interacting_About__c==oHUMIntMembers.Interacting_About__c)
                        mapMemberInteraction.get(sIntMemberId).Interacting_About_Type__c=oHUMIntMembers.Interacting_About_Type__c;
                        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                        if(multiMemberAuthSwitch){
                            if(mapMemberInteraction.get(sIntMemberId).Interacting_About__c!= null && mapMemberInteraction.get(sIntMemberId).Interacting_About__c==oHUMIntMembers.Interacting_About__c){
                                mapMemberInteraction.get(sIntMemberId).Authenticated__c = oHUMIntMembers.Authenticated__C;//@test
                                lstInteractionMembers.add(mapMemberInteraction.get(sIntMemberId));
                            }
                        }
                        else{ 
                            lstInteractionMembers.add(mapMemberInteraction.get(sIntMemberId));
                        }
                }
            }
            //Upserting Interaction Members.
            if(lstInteractionMembers != null && !lstInteractionMembers.isEmpty())
            {
                HUMInteractionMemberDmlHelper oHUMSave = new HUMInteractionMemberDmlHelper();
                lstInteractionMembers = oHUMSave.createInteractionMembers(lstInteractionMembers);
                if(lstInteractionMembers == null) 
                {
                    bRenderErrorMsg = true;
                    bSavedOrUnsavesMember = false;
                }
                else
                {
                    bSavedOrUnsavesMember = true;
                }   
            }
        }
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','saveIntMembers');
        }
    }

    /*
     * Method Name   :    showError() 
     * Description   :    Shows Error on the screen if there any unsaved Interaction Members are present.
     *                    Still user tries to move on their detail page. 
     *                    This method is called from javascript, on change of "Interacting about".
     * return Type   :    pagereference
     * Parameters    :    None
     */ 
    public pagereference showError()
    {
        bRenderErrorMsg = true;
        ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, System.Label.HUMErrorONInteratioMemberSave ));
        return null;
    }

    /*
     * Method Name   :    closePopup() 
     * Description   :    This method closes pop up opened for Update of Interaction Log. 
     * return Type   :    void
     * Parameters    :    None
     */
    public void closePopup() 
    { 
        bDisplayPopUp = false;    
    }
    
    /*
     * Method Name   :    updateDisp() 
     * Description   :    This method openes pop up for Updatating Interaction Log. 
     * return Type   :    void
     * Parameters    :    None
     */     
    public void updateDisp() 
    {
        bDisplayPopUp = true;    
    }
    
     /*     
      * Method Name   :    getbCaseInteraction()      
      * Description   :    gets bCaseInteraction value     
      * return Type   :    void     
      * Parameters    :    None     
     */     
     public PageReference getbCaseInteraction()     
     {         
        try         
        {       
            bCaseInteraction = false;
            setInqAbtIds = new Set<Id>();
            for(SelectOption oselOp : lstSelectOptionIntAbt)
            {
                if(oselOp.getValue() == sSelectedValue)
                {
                    setInqAbtIds.add(oselOp.getValue());
                }
            }                              
            HUMInteractionMemberDmlHelper oHUMInteractionMember = new HUMInteractionMemberDmlHelper();  
            bCaseInteraction = oHUMInteractionMember.setbCaseInteraction(setInqAbtIds,ofetchInteraction.id); 
                 
        }          
        catch(Exception e)          
        {               
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','getbCaseInteraction');          
        }
        return null;       
     }

    /*
     * Method Name   :    deleteSelectedVal() 
     * Description   :    This method deletes set of interacting about selected by user. 
     * return Type   :    void
     * Parameters    :    None
     */ 
    public void deleteSelectedVal()     
    {        
        try        
        {            
            //Collecting all the interaction members whose selected status flag is true.            
            set<SelectOption>lstTempSelectOp = new Set<SelectOption>();            
            set<SelectOption>lstTempDelSelectOp = new Set<SelectOption>();            
            lstTempDelSelectOp.addall(lstSelectOptionIntAbt);                        
            for(SelectOption oselectOp : lstSelectOptionIntAbt)
            {
                if(oselectOp.getValue() == sSelectedValue)
                {
                    lstTempSelectOp.add(oselectOp);
                    setInqAbtIds.add(oselectOp.getValue());
                }
            }                          
            HUMInteractionMemberDmlHelper oHUMSave = new HUMInteractionMemberDmlHelper();            
            //Sending the selected ids for deletion.            
            if(oHUMSave.deleteInqAbt(setInqAbtIds,ofetchInteraction.id))            
            {                               
                //Removing the deleted ids from datastructures usec on the page.                
                for(SelectOption oSelOp : lstTempSelectOp)                
                {                    
                    if(setInqAbtIds.contains(oSelOp.getValue()))                    
                    {                        
                        if(mapMemberInteraction != null)mapMemberInteraction.remove(oSelOp.getValue());                        
                        if(mapMemberInteractionType != null)mapMemberInteractionType.remove(oSelOp.getValue());                        
                        lstTempDelSelectOp.remove(oSelOp);                    
                    }               
                }                
                lstSelectOptionIntAbt = new List<SelectOption>();                
                lstSelectOptionIntAbt.addall(lstTempDelSelectOp);                
                if(!lstSelectOptionIntAbt.isEMpty())                    
                oHUMIntMembers.interacting_about__C = lstSelectOptionIntAbt[0].getValue();                
                changeInteractionMemberDetail();                    
                bDisplayPopUp = false;             
            }  

            //US2760646-updated authentication status based on selected value after delete
            if(multiMemberAuthSwitch)
                onChangeInqAbtw();   

            dataSecurity();        
        }       
        catch(Exception e)        
        {            
            bDisplayPopUp = false;            
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','deleteSelectedVal');        
        }    
    }

    /*
     * Method Name   :    saveInteractionAndRequery
     * Description   :    This method upserts Interaction record and requeries it for display on screen.
     * return Type   :    boolean
     * Parameters    :    Interaction__C
     */
    private boolean saveInteractionAndRequery(Interaction__C oInt)
    {
        try
        {
            List<Database.UpsertResult> listUpsertResults;
            list<interaction__c> lstnewInteraction = new list<interaction__c>();
            if(oInt != null)
            {
                
                    if(lstIntsave!= null && lstIntsave.size()>0) 
                    { 
                        lstnewInteraction = lstIntSave; 
                    }   
                else 
                { 
                    lstnewInteraction.add(oInt); 
                } 
            } 
                
                // Upsert the record in without sharing class as account is not accesible here
                Database.upsertResult oSvr = HUMWithoutSharingQueryHelper.inserInteraction(oInt);
                listUpsertResults = new List<Database.UpsertResult>{oSvr};
                List<interaction__C> lstInteractions = new List<interaction__C>{oInt};
                boolean isError =HUMExceptionHelper.processUpsertResults(listUpsertResults ,lstInteractions ,
                        'HUMInteractionLogComponentController','saveInteractionAndRequery','Interaction__C');
                if(isError == false)
                {
                    //As Interaction is created successfully, setting boolean variable to true.
                    bIntCreationStatus = true;
                    bRenderErrorMsg = true;
                }
                                                       
            //Re-Querying Interaction record just saved, so as to refresh screen with New values.
            //US2082644 As per the requirement added new field Call_Category__c field in the below query
            //US#2287945 as per the requirement added new field interaction__C in the query
            if(bIntCreationStatus == true && oInt.Id!=null && listUpsertResults[0].isSuccess())                
                oFetchInteraction = [SELECT id,name,Interacting_with__C ,Authenticated__c,Call_Category_Description__c,Genesys_Interaction_ID__c,
                                    Survey_ID__c,Caller_name__C,Interacting_With_type__c,Interaction_origin__C,Call_Category__c,uuid__C, Call_Data__c
                                     from interaction__C where id = :oInt.id];
            else bIntCreationStatus=false;

            return bIntCreationStatus; 
        }
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            bIntCreationStatus = false;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','saveInteractionAndRequery');
            return bIntCreationStatus;
        }
    }
    
    /*
     * Method Name   :    reQueryInteraction
     * Description   :    This method upserts Interaction record and requeries it for display on screen.
     * return Type   :    Void
     * Parameters    :    NA
     */
    public void reQueryInteraction()
    {
        try
        {
            if(oFetchInteraction.Id != null)
            {   
                //US#2287945 as per the requirement added new field interaction__C in the query
                oFetchInteraction = [SELECT id,name,Interacting_with__C ,
                                     Caller_name__C,Authenticated__c,Interaction_origin__C,uuid__C,Call_Category_Description__c,Genesys_Interaction_ID__c,
                                     Survey_ID__c,Interacting_With_type__c, Call_Data__c,Call_Category__c
                                     from interaction__C where id = :oFetchInteraction.Id];
            }
       } 
        Catch(Exception e)
        {
            bRenderErrorMsg = true;
            HUMExceptionHelper.logErrors(e,'HUMInteractionLogComponentController','reQueryInteraction');
        }
    }
    
    
    /*
     * Method Name   :    manageCustomSetting
     * Description   :    This method fetches Record from Custom setting and sends its string value back to calling function.
     * return Type   :    String
     * Parameters    :    String
     */

    private String manageCustomSetting(String sTobefetched)
    {
        HUMConstants__c recTypeUnknownAgent = HUMConstants__c.getInstance(sTobefetched);
        if(recTypeUnknownAgent != null) return recTypeUnknownAgent.StringValue__C;
        return null;
    }    
    /*
     * Method Name   :    dataSecurity
     * Description   :    This Method is for Security purpose. Used to display fewer records to External users
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void dataSecurity()
    {
        List <String> lstAccRecords = new List<String>();
        map<Id, UserRecordAccess> mapUsrRecAccess = new map<Id, UserRecordAccess>();
        mapRecordHaveAccess = new map<Id, Boolean>();
        for(SelectOption s : lstSelectOptionIntAbt){
            lstAccRecords.add(s.getValue());
        }
        List <UserRecordAccess> lstUserRecordAcess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : lstAccRecords];
        for(UserRecordAccess userAccess : lstUserRecordAcess)
        {
            mapUsrRecAccess.put(userAccess.RECORDID, userAccess);
        }

        for(Integer i = 0 ; i < lstAccRecords.size() ; i++)
        {
            mapRecordHaveAccess.put(lstAccRecords.get(i), mapUsrRecAccess.get(lstAccRecords.get(i)).HASREADACCESS);
        }
    }
    /*
     * Method Name   :    fnGetInqAbtType
     * Description   :    This method accepts record type name and returns type of account.
     * Return Type   :    String
     * Parameters    :    String , String 
     */  
    private  String fnGetInqAbtType(String sRecTypeName, String sTabType)
    {
        if(!String.isBlank(sRecTypeName))
        {
            if(sRecTypeName == manageCustomSetting('Unknown Agent/Broker')) 
            {
                return(manageCustomSetting('Unknown-Agent'));
            }   
            else if(sRecTypeName == manageCustomSetting('Unknown Group'))
            {
                return(manageCustomSetting('Unknown-Group'));
            }
            else if(sRecTypeName == manageCustomSetting('Unknown Provider')) 
            {
                return(manageCustomSetting('Unknown-Provider'));
            }
            else if(sRecTypeName == manageCustomSetting('Unknown Member')) 
            {
                return(manageCustomSetting('Unknown-Member'));
            }
            else 
            {
                return(sTabType);
            }
        }
        else
        {
            return(sTabType);
        }
    }
    
     /*
     * Method Name   :    checkAccessWithoutInteraction
     * Description   :    It checks for Access to Account record when Interaction is not present.
                          Based on Accessibility relevant Page layout will be opened when Account is openend without an interaction.
     * Return Type   :    Boolean
     * Parameters    :    ID 
     */
     private Boolean checkAccessWithoutInteraction(ID sAccID)
     {
        map<Id, UserRecordAccess> mapUsrRecAccess = new map<Id, UserRecordAccess>();
        
        List <UserRecordAccess> lstUserRecordAcess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID= :sAccID];
        for(UserRecordAccess userAccess : lstUserRecordAcess)
        {
            mapUsrRecAccess.put(userAccess.RECORDID, userAccess);
        }
        
        if(!mapUsrRecAccess.isEmpty())
        {
            return mapUsrRecAccess.get(sAccID).HASREADACCESS;
        }
        else
        {
            return null;
        }
     }
     
    /*
     * Method Name   :    authCheckHandler
     * Description   :    US#2287945 - Label to show based on the authentication checkbox value change
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void authCheckHandler(){
        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
        if(multiMemberAuthSwitch){
            if(oHUMIntMembers.Authenticated__c == true)
                authCheckLabel = 'Authenticated';
            else 
                authCheckLabel = 'UnAuthenticated';
        }
        else{
            If(oFetchInteraction.Authenticated__c == true){
                authCheckLabel = 'Authenticated';
            }
            else {
                authCheckLabel = 'UnAuthenticated';
            }
        }
    }  

    
     /*
     * Method Name   :    updateInteractionAttribute()  
     * Description   :    This method updates the Interaction details after "VOC Survey Transfer click ".  
     * return Type   :    Pagereference
     * Parameters    :    None
     */ 
    public Pagereference updateInteractionAttribute()
    {   
        string uuidata = system.CurrentPageReference().getParameters().get(HUMConstants.uuidata);
        if(uuidata != null && string.isNotBlank(uuidata) && string.isNotEmpty(uuidata))
        {
            PhoneBook_LC_HUM.updateInteractionAttribute(uuidata);
        }

        return null;
    }  

    /**
     * This method handles on change event of "Interaction About Field".
    * US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
    * @param    none
    * @return   pageReference
    */
    public pagereference onChangeInqAbtw()
    {
        if(multiMemberAuthSwitch)
        {
            List<Interaction_Member__c> lstInteractionMembers = [SELECT Id,name,Authenticated__C FROM Interaction_Member__c 
                                                                WHERE Interacting_About__c =: oHUMIntMembers.Interacting_About__c 
                                                                AND Interaction__c =: oFetchInteraction.Id];
            if(lstInteractionMembers.size() > 0) 
            {
                oHUMIntMembers.Authenticated__C = lstInteractionMembers[0].Authenticated__C;
                sSelMember = oHUMIntMembers.Interacting_About__c;
            }
            else{
                oHUMIntMembers.Authenticated__C =false;  
            }
            
            authCheckHandler();
            updateAuth();
        }
        return null;
    } 
 }