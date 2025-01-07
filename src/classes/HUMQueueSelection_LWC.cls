/************************************************************************************************************************
Apex class Name    : HUMQueueSelection_LWC
Version            : 1.0
Function           : Apex Class to poulate/save current queue field on User object.
Created On         : December 29 2021
Test Class         : HUMQueueSelection_T_LWC

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Gowthami Thota                 					12/29/2021          Original Version
***************************************************************************************************************************/

public with sharing class HUMQueueSelection_LWC {

    private static List<Group> lstGroups = null;
    private static Map<String,String> mapServiceCenter = new Map<String,String>();
    private static Map<String,String> mapDepartment = new Map<String,String>();
    private static Integer iLimit= Limits.getLimitQueryRows();
    private static Map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new Map<String, Work_Queue_Setup__c>([Select Name,OwnerId, id,Public_Group_Name__c, Work_Queue_Name__c from Work_Queue_Setup__c where IsActive__c = TRUE LIMIT: iLimit]);
    private static List<String> lstGroupNames = NULL;
    private static String sStandardUrlForTab = '';
    @TestVisible private final Case objCase = NULL;
    private String sObjectNumber = '';
    private String sActionType = '';
    private static String sMF='';
    private static User oUserRec;
    public Boolean IsRapidForceApp {get;set;}
    public Boolean isUnifiedSearchOn {get;set;}
    
    public static String sSelectedQueue { get; set; }
    public static String sAssignedQueue { get; set; }
    public static String numberOfQueue { get; set;}
    public static String sPageTitle{get;set;}
    public static List<String> options{get;set;}
    public static Boolean showSubFunctionPb{get;set;}
    public static String curSubFunction{get;set;}
    public static String sSelectedSub {get;set;}
    
    public static boolean sCompleted {get;set;}
    
    Public Static final String HUMCaseURLInteraction = '&interactionId=';
    Public Static final String HUMSingleSpaceValue = ' ';
    Public Static final String HUMDefaultMentorFunction = 'Contact Center RSO';
    Public Static final String HUMDefaultMentorSubFunction = 'Individual Medicare';
    
    
    
    /*
    * Method Name : populateSF
    * Description : This method is used to populate values in sub function dropdown  
    				if user presents in only one queue.
    * Return type : N/A
    * Paramater   : sMentorFunction
    */
    public static void populateSF(String sMentorFunction)
    {
        
        try
        {
            options = HUMUtilityHelper_LWC.getlstSubFunctions(sMentorFunction,ouserRec,HUMSingleSpaceValue);
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
            HUMExceptionHelper.logErrors(ex, 'HUMQueueSelection_LWC', 'populateSF');
        }
    }
    
    /*
    * Method Name : getGroups
    * Description : This method Is used to get the list of queue names which logged user Is part of.
    * Return type : List<String>
    * Paramater   : N/A
    */
    
    public static List<String> getGroups()
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
                if(mapWorkQueueSetup.containsKey(sWorkQueue) ){
                    If(mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c != NULL){
                if((mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c).trim().equalsIgnoreCase(oAllGrps.Name))
                {
                   lstGroupNames.add(mapWorkQueueSetup.get(sWorkQueue).Work_Queue_Name__c); 
                }
                    }
               }   
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
    * Description : This method is used to display queues of User on LWC in picklist form.
    * Return type : List<String>
    * Paramater   : N/A
    */
    @AuraEnabled
    public static List<string> getQueueList()
    {
      
       
            list<String> lstGrpNames = new list<String>();
            lstGrpNames = HUMQueueSelection_LWC.getGroups(); 
            List<string> lstOptions = new List<string>();  
            List<String> lstGrpValues = new List<String>();
            Set<String> setGrpName = new Set<String>();
            setGrpName.addAll(lstGrpNames);
            lstGrpValues = HUMUtilityHelper_LWC.doSort(setGrpName);
            for(String sGroupName : lstGrpValues)
            {  
                sGroupName = sGroupName.replace('_',' ');          
                lstOptions.add(sGroupName);
            }
            if(lstGrpNames != NULL && 1 == lstGrpNames.size())
            {
                sSelectedQueue = lstOptions[0];
            }
            numberOfQueue = String.valueOf(lstOptions.size());
            
            return lstOptions;
            
    }
    
    /*
    * Method Name : saveQueue
    * Description : This method is used to save the selected picklist queue value to the user field
    * Return type : boolean 
    * Paramater   : SelectedQueueu
    */
    @AuraEnabled
    public static boolean saveQueue(string sSelectedQueue) 
    {
        sCompleted = false;
        
        try
        {
            User oUser =new User();
            oUser = [select id, Current_Queue__c from User where id =: Userinfo.getUserId()];
            if(Schema.sObjectType.User.fields.Current_Queue__c.isUpdateable())
            oUser.Current_Queue__c = sSelectedQueue;
            update oUser;
            sCompleted = true;
            oUser = reassignSF(oUser);
            //return  returnURL(oUser);
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMQueueSelection_LWC','saveQueue');
            sCompleted = false;
        }
        
        return sCompleted;
    }
    
       /*
    * Method Name : reassignSF
    * Description : This method is used to change subfunction based on work queue
    * Return type : User 
    * Paramater   : currUser
    */
    public static User reassignSF(User currUser)
    {
        User oUser = new User(id=UserInfo.getUserId());
        try
        {            
            sMF = HUMUtilityHelper_LWC.fetchUserMentorFunction(currUser);            
            curSubFunction = HumConstants_LWC.EMPTY_STRING;
            if(Schema.sObjectType.User.fields.Mentor_Sub_Function__c.isUpdateable())
            oUser.Mentor_Sub_Function__c = HumConstants_LWC.EMPTY_STRING;
            update oUser;            
            showSubFunctionPb = String.isNotBlank(sMF);               
            options = HUMUtilityHelper_LWC.getlstSubFunctions(sMF,currUser,HumConstants_LWC.HUMSingleSpaceValue);
            if(sMF == HumConstants_LWC.HUMDefaultMentorFunction)
            {
                sSelectedSub = HumConstants_LWC.HUMDefaultMentorSubFunction;
            }
            else
            {
                sSelectedSub = HumConstants_LWC.HUMSingleSpaceValue;
            }              
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMQueueSelection_LWC', 'reassignSF'); 
        }
        return oUser;
    }
    
    /*
    * Method Name : getQueue
    * Description : This method is used to return the work queue selected when page loads.
    * Return type : String 
    * Paramater   : N/A
    */
    @AuraEnabled
    public static string getQueue() 
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
    * Paramater   : NA
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
            HUMExceptionHelper.logErrors(ex,'HUMQueueSelection_LWC','saveSubFunction');
        } 
    }
	

}