/*******************************************************************************************************************************
Apex Class Name : HUMQueueSelectionController 
Version         : 1.0
Created On      : 05/21/2014
Function        : This class is used for select work queue for agents, used to display error messages if agents trying to
do any operation like create/edit cases/tasks.
Test Class      : HUMQueueSelectionControllerTest

Modification Log: 
* Developer Name                      Code Review                            Date                       Description
*-------------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla                 16272                            05/21/2014                 Original Version
* SuryaKumari Medicherla                 17093                            05/21/2014                 Original Version
* SuryaKumari Medicherla                 18032                            09/18/2014                 Added logic to insert a case after selection of Work Queue Defect:154410 
* SuryaKumari Medicherla                 18257                            09/30/2014                 Added line of code to assign value to Interaction About Type field when user trying to create a case from account,Defect:156164
* SuryaKumari Medicherla                 18795                            10/27/2014                 Added line of code to display error message when creating Case Interaction from Interaction detail page
* Mrudula Jaddu                          19591                            11/27/2014                 REQ - 72764. Case Redirection to view page.
* SuryaKumari Medicherla                 19911                            12/11/2014                 Code modified to replace custom settings with custom object (REQ-72809)
* SuryaKumari Medicherla                 20855                            11/02/2015                 Code modified to fix defect:169682.0001 
* SuryaKumari Medicherla                 21614                            18/03/2015                 Alphabetical Order issue fix(Issue Number 41)
* Kritika Popat                          28219                            11/20/2015                 Worked on REQ 161520 for Cases related to Group Policy, new standards 
* Manish Kumar                           24880                            04/11/2016                 Worked on incorporating Logging changes
* Sharan Shanmugam						 47072						      11/07/2016				 Added logic to pick only active queues.
* Pradeepkumar Dani						 98235							  06/06/2017                 Classification_Type_Old__c Clean up
* Vishal Verma                          149549                            11/20/2017                 Defect#327649.0001 : Added a condition when the New case Is clicked while on going chat
* Melkisan Selvaraj 													  08/26/2019				 Launch Compass Functionality
* Sagar MN								  01/31/2020			Checkmarx Error Fix
* Prudhvi Pamarthi                                                        05/05/2021                 US#2212581 Checkmarx error fix
* Lakshmi Madduri                                                         7/7/2021                   US-2446094 - Launch Unified Search
* Prasuna Pattabhi                                                      11/11/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant - Account And Policy Pages
* Prasuna Pattabhi                                                      11/12/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant CaseManagement_Constants_HUM reference added
 ****************************************************************************************************************************/
public with sharing class HUMQueueSelectionController 
{
    private List<Group> lstGroups = null;
    private Map<String,String> mapServiceCenter = new Map<String,String>();
    private Map<String,String> mapDepartment = new Map<String,String>();
    private Integer iLimit= Limits.getLimitQueryRows();
    private Map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new Map<String, Work_Queue_Setup__c>([Select Name,OwnerId, id,Public_Group_Name__c, Work_Queue_Name__c from Work_Queue_Setup__c where IsActive__c = TRUE LIMIT: iLimit]);
    private List<String> lstGroupNames = NULL;
    private String sStandardUrlForTab = '';
    @TestVisible private final Case objCase = NULL;
    private String sObjectNumber = '';
    private String sActionType = '';
    private String sMF='';
    private User oUserRec;
    public Boolean IsRapidForceApp {get;set;}
    public Boolean isUnifiedSearchOn {get;set;}
    
    Public Static final String HUMTask_Number = 'Task_Number';
    Public Static final String HUMQueueYesForAttachment = 'YesForAttachement';
    Public Static final String HUMsmallId = 'id';
    Public Static final String WHAT_ID = 'what_id';
    Public Static final String HUMChatIdParameter = '&chatId';
    Public Static final String HUMSearchTypeMember = 'Member';
    Public Static final String HUMDotCom = '.com/';
    Public Static final String HUM_Object_Policy = 'Policy__C';
    Public Static final String CASE_STATUS='In Progress';
    Public Static final String CASE_TYPE='Customer Inquiry';
    Public Static final String CASE_ORIGIN='Inbound Call';
    Public Static final String HUMQueueYesForInteraction = 'YesForInteraction';
    Public Static final String HUMParentObjectId = 'ParentObjectId';
    Public Static final String HUMCaseStandardURL = 'sStandardUrlForTab';
    Public Static final String HUMWhatId = 'whatId';    
    Public Static final String HUMCaseURLInteraction = '&interactionId=';
    Public Static final String HUMSingleSpaceValue = ' ';
    Public Static final String HUMDefaultMentorFunction = 'Contact Center RSO';
    Public Static final String HUMDefaultMentorSubFunction = 'Individual Medicare';
    Public Static final String EMPTY_STRING = '';
    Public Static final String QUEUESELECTION_ONGOINGCHAT_HUM = '570/e?ongoing=1';
    
    /*
    * Method Name : HUMQueueSelectionController
    * Description : Constructor for the class , Initialise variables , read page parameter Values and to display error messages based on page parameter values.
    * Return type : N/A
    * Paramater   : N/A
    */
    public HUMQueueSelectionController()
    { 
        try 
        {
            numberOfQueue = '0'; 
	    	isRapidForceApp = HUMUtilityHelper.IsRapidForceAPP();            
            String sObjectType = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectType);
            sObjectNumber = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMobjectNumber);
            sActionType = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMActionType);
            String sPageParam = ApexPages.currentPage().getParameters().get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_SDFCIFRAMEORIGIN);
            if(sPageParam != null && sPageParam.contains(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_HOMEURLPATH) && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible())
            {
                isUnifiedSearchOn = true;
            }
            if(!String.isBlank(sActionType ) && sActionType.equalsIgnoreCase(CaseManagement_Constants_HUM.HUMQueueYesForEdit)) 
            {
                sTabTitle = sObjectNumber;
                if(!String.isBlank(sTabTitle)) 
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueEditCase + sTabTitle));
                } 
                else if(!String.isBlank(ApexPages.currentPage().getParameters().get(HUMTask_Number))) 
                {
                    sTabTitle = ApexPages.currentPage().getParameters().get(HUMTask_Number);
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueEditTask + sTabTitle));
                }
            } 
            else if(!String.isBlank(sActionType) && sActionType.equalsIgnoreCase(HUMQueueYesForInteraction) && !String.isBlank(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectId))) 
            {
                sTabTitle = System.label.HUMNewInteraction;
                if(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectType).equalsIgnoreCase(CaseManagement_Constants_HUM.OBJECT_CASE))
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueCreateCaseInteraction + sObjectNumber));           
                }
                else
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueCreateCIForInteraction + sObjectNumber));           
                }                    
            } 
            else 
            {
                objCase = new Case();
                getPageVariables(sActionType,sObjectType);
            }
            showSubFunctionPb = false;
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMQueueSelectionController','HUMQueueSelectionController');
        }      
    }
    
    public String sTabTitle { get; set; }
    public String sSelectedQueue { get; set; }
    public String sAssignedQueue { get; set; }
    public String numberOfQueue { get; set;}
    public String sPageTitle{get;set;}
    public List<SelectOption> options{get;set;}
    public Boolean showSubFunctionPb{get;set;}
    public String curSubFunction{get;set;}
    public String sSelectedSub {get;set;}
    
    /*
    * Method Name : getPageVariables
    * Description : This method is used to read page variables.
    * Return type : Void
    * Paramater   : string ,string 
    */
    private void getPageVariables(string sActionType,string sObjectType)
    {
        if(!String.isBlank(sActionType) && sActionType.equalsIgnoreCase(HUMQueueYesForAttachment) && !String.isBlank(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectId))) 
        {
            sTabTitle = System.label.HUMNewAttachment;
            if(sObjectType.equalsIgnoreCase(CaseManagement_Constants_HUM.OBJECT_CASE))
            {   
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueCreateCaseAttachment +sObjectNumber));             
            }
            else if(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectType).equalsIgnoreCase(CaseManagement_Constants_HUM.OBJECT_TASK) )
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueCreateTaskAttachment +ApexPages.currentPage().getParameters().get(HUMTask_Number)));             
            }
        }  
        else 
        {
            if(!String.isBlank(sObjectType) && CaseManagement_Constants_HUM.OBJECT_CASE.equalsIgnoreCase(sObjectType)) 
            {
                sTabTitle = System.label.HUMNewCase;
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueCreateCase ));
            } 
            else if(!String.isBlank(sObjectType) && CaseManagement_Constants_HUM.OBJECT_TASK.equalsIgnoreCase(sObjectType)) 
            {
                sTabTitle = System.label.HUMNewTask;
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.HUMQueueCreateTask ));
            }
        }
    }
    
    /*
    * Method Name : init
    * Description : This method is used to update current queue field of user with queue value, 
    if user presents in only one queue.
    * Return type : Pagereference
    * Paramater   : N/A
    */
    public PageReference init()
    {
        PageReference oPage = null;
        try 
        {
            getGroups();
            ouserRec = HUMUtilityHelper.fetchUserDetails();
            curSubFunction = oUserRec.Mentor_Sub_Function__c;
            if(lstGroupNames != NULL && 1 == lstGroupNames.size())
            {
                if(String.isBlank(oUserRec.current_queue__c) || lstGroupNames.get(0) != oUserRec.current_queue__c ) 
                {
                    if(Schema.sObjectType.User.fields.current_queue__c.isUpdateable())
                    oUserRec.current_queue__c = lstGroupNames.get(0).replace('_',' ');
                    update oUserRec;
                }
                oPage = returnURL(oUserRec);    
            }            
            sMF = HUMUtilityHelper.fetchUserMentorFunction(ouserRec);
            showSubFunctionPb = String.isNotBlank(sMF); 
            if(showSubFunctionPb)
            {
                populateSF(sMF);
            }  
            if(lstGroupNames.isEmpty())
            {
                User uRec = new User(id=ouserRec.id);
                if(Schema.sObjectType.User.fields.Mentor_Sub_Function__c.isUpdateable())
                uRec.Mentor_Sub_Function__c ='';
                update uRec;
            }         
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMQueueSelectionController', 'init');
        }
        return oPage;
    }
    
    /*
    * Method Name : populateSF
    * Description : This method is used to populate values in sub function dropdown  
    if user presents in only one queue.
    * Return type : N/A
    * Paramater   : List<Work_Queue_Setup__c>
    */
    public void populateSF(String sMentorFunction)
    {
        try
        {
            options = HUMUtilityHelper.getlstSubFunctions(sMentorFunction,ouserRec,HUMSingleSpaceValue);
            if(sMentorFunction == HUMDefaultMentorFunction)
            {
                sSelectedSub = HUMDefaultMentorSubFunction;
            }
            else
            {
                sSelectedSub = HUMSingleSpaceValue;
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMQueueSelectionController', 'populateSF');
        }
    }
    
    /*
    * Method Name : getGroups
    * Description : This method Is used to get the list of queue names which logged user Is part of.
    * Return type : List<String>
    * Paramater   : N/A
    */
    public List<String> getGroups()
    {
        lstGroupNames = new List<String>();
        List<String> lstGroupNamesFinal = new List<String>();
        List<Id> lstGrpIds = new List<Id>();
        Set<String> setGroupNames = new Set<String>();
        
        Map<Id, GroupMember> mapGrpMem = new Map<Id, GroupMember>([select GroupId from GroupMember Where UserOrGroupId =: Userinfo.getUserId()]);
        for(Id groupMemId : mapGrpMem.Keyset()) 
        {
            lstGrpIds.add(mapGrpMem.get(groupMemId).GroupId);
        }
        lstGroups = [Select Name from Group where Id IN : lstGrpIds];
        
        for (String sWorkQueue : mapWorkQueueSetup.keySet())
        {
            for(Group oAllGrps : lstGroups) 
            {
                if((mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c).trim().equalsIgnoreCase(oAllGrps.Name))
                    lstGroupNames.add(mapWorkQueueSetup.get(sWorkQueue).Work_Queue_Name__c);
            }
        }
        // To remove duplicates from List
        setGroupNames.addAll(lstGroupNames);
        lstGroupNamesFinal.addAll(setGroupNames);
        lstGroupNames = lstGroupNamesFinal;
        return lstGroupNamesFinal;
    }
    
    /*
    * Method Name : getQueueList
    * Description : This method is used to display queues of User on VF Page in picklist form.
    * Return type : List<SelectOption>
    * Paramater   : N/A
    */ 
    public List<SelectOption> getQueueList()
    {
        try 
        {
            getGroups();           
            List<SelectOption> lstOptions = new List<SelectOption>();  
            List<String> lstGrpValues = new List<String>();
            Set<String> setGrpName = new Set<String>();
            setGrpName.addAll(lstGroupNames);
            lstGrpValues = HUMUtilityHelper.doSort(setGrpName);
            for(String sGroupName : lstGrpValues)
            {  
                sGroupName = sGroupName.replace('_',' ');          
                lstOptions.add(new SelectOption(sGroupName, sGroupName));
            }
            if(lstGroupNames != NULL && 1 == lstGroupNames.size())
            {
                sSelectedQueue = lstOptions[0].getValue();
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, System.label.HUMQueueSelectionError));
            }
            numberOfQueue = String.valueOf(lstOptions.size());
            return lstOptions;
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMQueueSelectionController','getQueueList');
        }
        return null;
    }
    
    /*
    * Method Name : saveQueue
    * Description : This method is used to save the selected picklist queue value to the user field and return the next 
    page reference.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference saveQueue() 
    {
        try
        {
            User oUser =new User();
            oUser = [select id, Current_Queue__c from User where id =: Userinfo.getUserId()];
            if(Schema.sObjectType.User.fields.Current_Queue__c.isUpdateable())
            oUser.Current_Queue__c = sSelectedQueue;
            update oUser;
            oUser = reassignSF(oUser);
            return  returnURL(oUser);
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMQueueSelectionController','saveQueue');
        }
        return null;
    }
    
    /*
    * Method Name : reassignSF
    * Description : This method is used to change subfunction based on work queue change 
    page reference.
    * Return type : User 
    * Paramater   : N/A
    */
    public User reassignSF(User currUser)
    {
        User oUser = new User(id=UserInfo.getUserId());
        try
        {            
            sMF = HUMUtilityHelper.fetchUserMentorFunction(currUser);            
            curSubFunction = EMPTY_STRING;
            if(Schema.sObjectType.User.fields.Mentor_Sub_Function__c.isUpdateable())
            oUser.Mentor_Sub_Function__c = EMPTY_STRING;
            update oUser;            
            showSubFunctionPb = String.isNotBlank(sMF);               
            options = HUMUtilityHelper.getlstSubFunctions(sMF,currUser,HUMSingleSpaceValue);
            if(sMF == HUMDefaultMentorFunction)
            {
                sSelectedSub = HUMDefaultMentorSubFunction;
            }
            else
            {
                sSelectedSub = HUMSingleSpaceValue;
            }              
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMQueueSelectionController', 'reassignSF'); 
        }
        return oUser;
    }
    
    /*
    * Method Name : returnURL
    * Description : This method is used get the next page reference based on current URL page parameters.
    * Return type : PageReference 
    * Paramater   : User
    */
    public PageReference returnURL(User oUser)
    {
        if(String.isBlank(oUser.Current_Queue__c))
        {
            return null;
        } 
        else if(String.isNotEmpty(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectType)))
        {  
            if(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectType).equalsIgnoreCase(CaseManagement_Constants_HUM.OBJECT_CASE))
            {           
                return returnURLForCase();
            } 
            else if(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMObjectType).equalsIgnoreCase(CaseManagement_Constants_HUM.OBJECT_TASK))
            {          
                return returnURLForTask();    
            }
            else
            {
                return(new PageReference(ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMNextPage)));
            }
        }
        return null;
    }
    
    /*
    * Method Name : returnURLForCase
    * Description : This method is used to get the next page reference based on current URL page parameters for cases.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference returnURLForCase()
    {
        Boolean bExists = false;
        String sNxtPage = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMNextPage);
        String sParentObjId;
        String sAccountRecTypeName;
        String sObjectName;
        String sParentObjIdPolicy;
        List <Policy__c> lstPolicy = new List<Policy__c>();
        Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
        Map<String, HUMCaseNPI_TAXID__c> mapCaseNPIAndTaxId = HUMCaseNPI_TAXID__c.getAll();
        
        sParentObjId = ApexPages.currentPage().getParameters().get(HUMParentObjectId);
        Id objectId = sParentObjId;
        sObjectName = objectId.getSobjectType().getDescribe().getName();
        if (sObjectName.equalsIgnoreCase(HUM_Object_Policy))
        {
            objCase.Policy__c = sParentObjId;
            lstPolicy = [Select Group_Name__r.Id from Policy__c where ID =: sParentObjId];
            if(!lstPolicy.isEmpty())   sParentObjId = lstPolicy[0].Group_Name__r.Id;
        }
        
        sStandardUrlForTab = ApexPages.currentPage().getParameters().get(HUMCaseStandardURL); 
        if(sActionType.equalsIgnoreCase(CaseManagement_Constants_HUM.HUMQueueYesForEdit) || sActionType.equalsIgnoreCase(HUMQueueYesForAttachment) || sActionType.equalsIgnoreCase(HUMQueueYesForInteraction)) 
        {
            return(new PageReference(sNxtPage));
        } 
        else if(sActionType.equalsIgnoreCase(CaseManagement_Constants_HUM.HUMQueueYesFromAccountRelatedList)) 
        {                        
            objCase.AccountId = sParentObjId;
            Account oAccount = [select NPI_ID__c from Account where Id =: sParentObjId];
            objCase.RecordTypeId = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMcaseRecordType);
            sAccountRecTypeName = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMAccRecordTypeName);
            if(!String.isBlank(sAccountRecTypeName))    objCase.Interacting_About_Type__c = mapCaseInterAboutType.get(sAccountRecTypeName).CaseInteraction_About_Type__c;
            
            for(String sCaseNPITax : mapCaseNPIAndTaxId.keySet())
            {
                if(!String.isBlank(sCaseNPITax) && sCaseNPITax.equalsIgnoreCase(sAccountRecTypeName))
                {
                    bExists = true; 
                    break;               
                }
            }
            if(bExists)
            {
                objCase.NPI_ID__c = oAccount.NPI_ID__c;
                try
                {
                    ConsumerID__c oConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__c =: sParentObjId limit 1];
                    if(oConsumerId != NULL)    objCase.Tax_ID__c = oConsumerId.Consumer_ID__c; 
                }
                catch (Exception ex) 
                {
                    HUMExceptionHelper.logErrors(ex, 'HUMQueueSelectionController', 'returnURLForCase'); 
                }
            }
            return returnSaveURL();
        } 
        else if(sActionType.equalsIgnoreCase(CaseManagement_Constants_HUM.HUMQueueYesFromCaseHistory)) 
        {                         
            objCase.Policy_Member__c = sParentObjId;
            assignValuesToCase();
            return returnSaveURL();
        } 
        else 
        {       
            return new PageReference(CaseManagement_Constants_HUM.HUMApexinURL+sNxtPage);
        }
        return null;
    }
    
    /*
    * Method Name : returnSaveURL
    * Description : This method is used to call other method to assign values and returns return type.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference returnSaveURL()
    {
        Id caseId;
        assignValuesToCase();
        List<Case> lstCase = new List<Case>{objCase};
            List<Database.SaveREsult> lstSaveResults = Database.insert(lstCase, false);
        HUMExceptionHelper.processSaveResults(lstSaveResults, lstCase, 'HUMQueueSelectionController', 'returnURL', 'Case');
        caseId = objCase.Id;
        
        if(caseId != NULL)    
        {
            Pagereference oPageRef =  new ApexPages.Standardcontroller(new Case(Id = caseId)).edit(); 
            oPageRef.setRedirect(true);
            return oPageRef;
        }
        return null;
    }
    
    /*
    * Method Name : returnURLForTask
    * Description : This method is used to get the next page reference based on current URL page parameters for Tasks.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference returnURLForTask()
    {
        String sQuery = '?';
        String sWhatId = '';
        String sNxtPage = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMNextPage);
        String sParentObjId;
        sWhatId = ApexPages.currentPage().getParameters().get(HUMWhatId);
        String sId = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMId);
        if(!String.isBlank(sId))    sQuery += HUMsmallId+'='+ String.escapeSingleQuotes(sId);
        
        if(!String.isBlank(sWhatId))    sQuery += WHAT_ID +'='+ String.escapeSingleQuotes(sWhatId) + '&?';
        sNxtPage = CaseManagement_Constants_HUM.HUMApexinURL+sNxtPage+sQuery;
        
        PageReference oPageRef = new PageReference(sNxtPage);
        if(!String.isBlank(sId))    oPageRef.getParameters().put(HUMsmallId, String.escapeSingleQuotes(sId));
        if(!String.isBlank(sWhatId))    oPageRef.getParameters().put(WHAT_ID, String.escapeSingleQuotes(sWhatId));
        if(!String.isBlank(sActionType))
        {
            if(sActionType.equalsIgnoreCase(HUMQueueYesForAttachment))    
            {
                sNxtPage = ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMNextPage);
                return(new PageReference(sNxtPage));
            }
            else
            {
                return oPageRef;
            } 
        }
        else
        { 
            oPageRef.getParameters().put(CaseManagement_Constants_HUM.HUMRecordType, ApexPages.currentPage().getParameters().get(CaseManagement_Constants_HUM.HUMRecordType));
            oPageRef.setRedirect(true);
            return oPageRef;
        }
    }
    
    /*
    * Method Name : assignValuesToCase
    * Description : This method is used to Assigns the values to case fields before to create a case.
    * Return type : Void
    * Paramater   : N/A
    */
    private Void assignValuesToCase()
    { 
        String sAccountId = '';
        String sObjectName = '';
        Interaction__c ofetchinteraction;
        List <Policy__c>lstPolicy = new List<Policy__c>();
        String sCurrentWorkQueue = [select Current_queue__c from User where id = : Userinfo.getUserId()].Current_queue__c ;
        Interaction_Member__c oHUMIntMember = new Interaction_Member__c();
        
        if (!String.isBlank(sStandardUrlForTab)) 
        {
            if(sStandardUrlForTab.indexOf(HUMChatIdParameter) > -1)
            {
                objCase.Origin = CaseManagement_Constants_HUM.HUMWebChatText;
                objCase.Interacting_With_Type__c = HUMSearchTypeMember;
                objCase.Interacting_About_Type__c = HUMSearchTypeMember;
            }
            else
            {
                Integer iSize = sStandardUrlForTab.indexOf(HUMDotCom);
                if(-1 != iSize)    sAccountId = sStandardUrlForTab.substring(iSize+5, iSize+20);
                if(sAccountId != QUEUESELECTION_ONGOINGCHAT_HUM) // Added this condition to bypass this logic for ongoing chat
                {
                    
                    Id objectId = sAccountId;
                    sObjectName = objectId.getSobjectType().getDescribe().getName();
                    if (sObjectName.equalsIgnoreCase(HUM_Object_Policy))
                    {
                        lstPolicy = [Select Group_Name__r.Id from Policy__c where ID =: sAccountId];
                        if(!lstPolicy.isEmpty())   sAccountId = lstPolicy[0].Group_Name__r.Id;
                    }
                }
                Integer iStartPos = sStandardUrlForTab.indexOf(HUMCaseURLInteraction);
                if (-1 != iStartPos) 
                {
                    String sIntId = sStandardUrlForTab.substring(iStartPos + 15, iStartPos + 33);
                    List < INteraction__C > lstInt = [Select id, name, Interacting_With__c, Interacting_With_type__c, Caller_Name__c,Interaction_Origin__c
                                                      FROM Interaction__C Where id = : sIntId];
                    if (0 < lstInt.size()) 
                    {
                        ofetchinteraction = lstInt[0];
                        List<Interaction_Member__c> lstSelectedIntMembers = [select Interacting_About__c,Interacting_About_Type__c 
                                                                             FROM Interaction_Member__c 
                                                                             Where Interaction__c = : sIntId AND Interacting_About__c = :sAccountId];
                        if(0 < lstSelectedIntMembers.size())
                        {
                            oHUMIntMember.Interacting_About_Type__c = lstSelectedIntMembers[0].Interacting_About_Type__c;
                            oHUMIntMember.Interacting_About__c = lstSelectedIntMembers[0].Interacting_About__c;
                        }
                    }
                }
            }
        }
        assignInteractionValuesToCase(ofetchinteraction,oHUMIntMember,sCurrentWorkQueue);
    }
    
    /*
    * Method Name : assignInteractionValuesToCase
    * Description : This method is used to assign interaction values to case.
    * Return type : Void
    * Paramater   : Interaction__c,Interaction_Member__c,String  
    */
    private Void assignInteractionValuesToCase(Interaction__c ofetchinteraction,Interaction_Member__c oHUMIntMember,String sCurrentWorkQueue) 
    {
        assignToCase(sCurrentWorkQueue);
        if (ofetchinteraction != null) 
        {
            objCase.Interacting_With_Type__c = ofetchinteraction.interacting_with_type__C;
            objCase.Interacting_With__c = ofetchinteraction.Interacting_With__c;
            objCase.Interaction_id__C = ofetchinteraction.id;
            objCase.Origin = ofetchinteraction.Interaction_Origin__c;
            objCase.Interacting_with_name__C = ofetchinteraction.Caller_name__c;
        }
        if(!String.isBlank(oHUMIntMember.Interacting_About_Type__c))
        {
            objCase.AccountID = oHUMIntMember.Interacting_About__c;
            objCase.Interacting_About_Type__c = oHUMIntMember.Interacting_About_Type__c;
        }
    }
    
    /*
    * Method Name : assignToCase
    * Description : This method is used to assign fixed values to Case before creating a case
    * Return type : Void
    * Paramater   : String 
    */
    private Void assignToCase(String sCurrentWorkQueue ) 
    {
        objCase.Interacting_With__c = objCase.AccountId;
        if (objCase.Policy_Member__c != NULL)
        {
            List<Policy_Member__c > lstPolicyMember;
            lstPolicyMember = [Select Member__c From Policy_Member__c where Id =: objCase.Policy_Member__c];
            if(0 < lstPolicyMember.size() && NULL != lstPolicyMember[0].Member__c)  objCase.AccountId = lstPolicyMember[0].Member__c;
            objCase.Interacting_With__c = objCase.AccountId;
        }
        objCase.Status = CASE_STATUS;
        objCase.Type = CASE_TYPE;
        if(objCase.Origin == null)  objCase.Origin = CASE_ORIGIN;
        objCase.Case_Owner__c = UserInfo.getName();
        objCase.Owner_Queue__c = sCurrentWorkQueue ;
        
        if(!String.isBlank(sCurrentWorkQueue))
        {
            sCurrentWorkQueue = sCurrentWorkQueue.replace(' ','_');
            List <Work_Queue_Setup__c> lstWQS = [select Service_Center__c, Department__c, Classification_Type__r.Name from Work_Queue_Setup__c where Work_Queue_Name__c =: sCurrentWorkQueue AND IsActive__c = TRUE];
            if(!lstWQS.isEmpty())    
            {
                Work_Queue_Setup__c oWQS = lstWQS[0];
                objCase.Service_Center__c = oWQS.Service_Center__c; 
                objCase.Department__c =  oWQS.Department__c;           
                objCase.Classification_Type__c = oWQS.Classification_Type__r.Name; 
            }
        } 
    } 
    
    /*
    * Method Name : getQueue
    * Description : This method is used to return the work queue selected when page loads.
    * Return type : String 
    * Paramater   : N/A
    */
    public String getQueue() 
    {
    String queueRetValue ='';
    
     If(Schema.sObjectType.User.fields.Current_Queue__c.isAccessible()){
     User oUSer = [select Current_Queue__c from User where id =: Userinfo.getUserId()];
        queueRetValue = oUSer.Current_Queue__c;
    }
        return queueRetValue;
    } 

    /*
    * Method Name : saveSubFunction
    * Description : Saves sub function of the user
    * Return type : N/A
    * Paramater   : N/A
    */
    public void saveSubFunction()
    {
        try
        {
            User userObj = new User(id=UserInfo.getUserId());
            if(Schema.sObjectType.User.fields.Mentor_Sub_Function__c.isUpdateable())
            userObj.Mentor_Sub_Function__c = sSelectedSub;
            update userObj;
            curSubFunction = sSelectedSub;
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMQueueSelectionController','saveSubFunction');
        } 
    }
}