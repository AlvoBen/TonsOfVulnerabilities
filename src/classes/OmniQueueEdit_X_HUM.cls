/***************************************************************************************************************************************************
Apex Class Name  : OmniQueueEdit_X_HUM
Version          : 1.0
Created Date     : 11/01/2017
Function         : Controller to create and Edit OmniQueue Objects and the related Setup objects. (This Class is implemented as Without sharing because)
                   we create/update Group and GroupMember setup objects in the backend.)
Test Class       : OmniQueueEdit_T_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                   Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor             144993                  11/06/2017                Original Version 
* Mohammed Noor             145082                  11/06/2017                Escape single quotes in JSON string.
* Mohammed Noor             149836                  11/20/2017                Fix for Defect# 327624.
* Mohammed Noor             150252                  11/21/2017                Fix for Defect# 327746.
* Mohammed Noor             151361                  11/27/2017                Fix for Defect# 330043 and 327609.
* Mohammmed Noor            156778                  12/11/2017                Include the GroupID in the SOQL used to delete GroupMember records.
* Syed Mubarak T            158900                  12/04/2017                Modified Code for Omni Routing Configuration
* Manoj Chaurasia           209583                  04/04/2018                REQ-356054 Ability for Medicare Enrollment Supervisors to add and remove users from Omni Queue.
* Syed Mubarak T                                    03/01/2019                Added Omni Configuration Item changes and added a UI field to show remaining Limit
* Pooja Kumbhar                                     03/31/2022                User Story - 3252212 - T1PRJ0002736 - 2022 MF6 - Case Management Checkmarx Security violations - Case Service (DF-4676)
****************************************************************************************************************************************************/

public without sharing class OmniQueueEdit_X_HUM {

    public Boolean disableEdit {get; set;}
       
    public OmniQueue__c omniQueueObj {get; set;}  
    
    private OmniQueue__c omniQueueObjCpy;   //copy of Omni Queue Object to compare changes in input for Edit
        
    public Boolean viewUserLookup{get;set;}  //to show or hide the user select popup
    
    public List<User> lstUsers{get; set;}  
    
    public String userJsonString {get; set;} 
    
    public String addUsersJson {get; set;}  //string of Users to be added to Table
    
    private set<Id> queueUserIds;  //set of UserIDs that existing assigned on the Queue --> Used to identify remove and filter existing users.
        
    public Map<Id, User> availableUsersMap = new Map<Id, User>();
    
    public Id userToRemove {get; set;}
   
    private Map<Id, User> userMap = new Map<Id, User>();
        
    private List<User> lstUserToDelete = new List<User>();
    
    public Boolean bError {get; set;}
            
    public Boolean doUpdate {get; set;}
    
   
   
   public Omni_Configuration_Item__c configItem {get;set;}
    
    private static Integer maxNumberOfWorkItems {get;set;} 
    
    public static Integer remainingThreshold {get;set;}
    
    private static String grpType ='Queue';
    
    public Omni_Routing_Configuration__c routingConfig {get;set;}
    
    
    

    public Boolean bNameError {get; set;}   // Error Flag for Invalid Name
     
    public Boolean bMaxCaseError {Get; Set;}  // Error Flag For Invalid Max Cases
    
    Public Boolean bRoutingConfigError {Get;Set;} //Error Flag For Routing Configuration
    
    /*   
    * Constructor
    * @param  NA
    * @return void 
    */
    public OmniQueueEdit_X_HUM(ApexPages.StandardController controller) 
    {               
        if(controller.getId()!= NULL)
        {
            omniQueueObj = [SELECT ID, Name, Maximum_Cases__c, Routing_Configuration_Name__c, Omni_Queue_ID__c, Staging_Queue_ID__c, Staging_Queue_Name__c,Routing_Configuration_Name__r.Configuration_Item_Name__c FROM OmniQueue__c where ID = :controller.getId()];                        
            omniQueueObjCpy = New OmniQueue__c(Name = omniQueueObj.Name, Maximum_Cases__c = omniQueueObj.Maximum_Cases__c, Routing_Configuration_Name__c = omniQueueObj.Routing_Configuration_Name__c,
                                               Staging_Queue_Name__c = omniQueueObj.Staging_Queue_Name__c);                                                 
            getRemainingThreshold();
        }
        else
        {
            omniQueueObj = new OmniQueue__c();
            omniQueueObj.Maximum_Cases__c = 50;
            omniQueueObjCpy = null;
        }
        viewUserLookup = false;
        bError = false;
        doUpdate = false;
        bNameError = false;
        bMaxCaseError = false; 
        bRoutingConfigError = false;
        
        checkDisableEdit(); 
    }
    
    /*
    * This method is called on load of OmniQueue edit page to fetch all the Users assigned to the OmniQueue
    * @param   NA 
    * @return  void
    */
    public void getQueueMembers()
    {
        lstUsers = new List<User>();
        queueUserIds = new set<Id>();        
        if(omniQueueObj.Id != null)
        {
            for(GroupMember groupMem : [SELECT Id, GroupId, UserorGroupId FROM GroupMember where GroupID = :omniQueueObj.Omni_Queue_ID__c] )
            {
                queueUserIds.add(groupMem.UserorGroupId);
            }           
            if(!queueUserIds.isEmpty())
            {
                lstUsers = [SELECT Id, FirstName, LastName, Email from User where Id IN :queueUserIds];
                userMap = new Map<Id, User>();
                userMap.putAll(lstUsers);
            }
        }
        createUserMap();
    }
    
    /*
    * This method is used to Open and Close the Add User Popup
    * @param   NA 
    * @return  void
    */
    public void getopenUserLookup()
    {
        if(viewUserLookup)
        {
            viewUserLookup= false;
        }
        else
        {
            viewUserLookup= true;
        }
    }
    
    /*    
    * This method is used to create the Map of Users with MedicareEdit Permissionset.
    * @param   None
    * @return  Void
    */
    @TestVisible
    Private void createUserMap()
    {
        List<User> lstAvailableUsers = new List<User>();
        
        availableUsersMap = new Map<Id, User>();
                        
        String sQuery = 'SELECT Id, FirstName, LastName, Email, Network_User_Id__c FROM User WHERE IsActive = true AND';
        
        set<String> assignedUserIds = new set<String>();
        
        PermissionSet perm = [SELECT Name, Label, Id FROM PermissionSet where name = :GLOBAL_CONSTANT_HUM.METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM Limit 1];
        
        if(perm != null)
        {
            List<PermissionSetAssignment> permAssigment = [SELECT Id, PermissionSetId, AssigneeId FROM PermissionSetAssignment where PermissionSetId = :perm.Id];
            if(!permAssigment.isEmpty())
            {
                for(PermissionSetAssignment p : permAssigment)
                {
                    assignedUserIds.add(p.AssigneeId);                    
                }
            }
            if(!assignedUserIds.isEmpty())
            {
                sQuery += ' ID IN :assignedUserIds'; 
              
                if(!queueUserIds.isEmpty())
                    sQuery += ' AND ID NOT IN :queueUserIds';
              
                sQuery += ' ORDER BY FirstName, LastName';
                            
                lstAvailableUsers = (List<User>) Database.query(sQuery);              
            }                        
        }
        if(!lstAvailableUsers.isEmpty())
        {
            availableUsersMap.putAll(lstAvailableUsers);
        }
    }
    
    /*
    * This method is used to get the List of Users that are available for being assigned to OmniQueue.
    * @param   NA 
    * @return  void
    */
    public void getAvailUserList()
    {                              
        userJsonString = JSON.serialize((List<User>)availableUsersMap.values());
        userJsonString = '{"data":' + userJsonString + '}';             
        userJsonString = userJsonString.replace('\'', '\\\'');          

        getopenUserLookup();
    }
    
    /*
    * This method is used to add the selected Users to the User's Table on the Page.
    * @param   NA 
    * @return  void
    */
    public void saveSelectedUsers()
    {        
        if(String.isNotBlank(addUsersJson))
        {
            List<User> lstAddUsers = (List<User>) JSON.deserialize(addUsersJson, List<User>.class);            
            if(!lstAddUsers.isEmpty())
            {
                lstUsers.addAll(lstAddUsers);
                userMap.putAll(lstAddUsers);
                for(User addUser : lstAddUsers)
                {
                    availableUsersMap.remove(addUser.Id);
                }               
            }
            addUsersJson = '';
            userJsonString = null;
        }
        getopenUserLookup();
    }
    
    /*
    * This method is used to Remove the Users from User's Table on the Page.
    * @param   NA 
    * @return  void
    */
    public PageReference removeUser()
    {        
        if(String.isNotBlank(userToRemove)&& userMap.get(userToRemove)!=null)
        {
            if(queueUserIds.contains(userToRemove))
            {
                lstUserToDelete.add(userMap.get(userToRemove));
                queueUserIds.remove(userToRemove);              
            }
            availableUsersMap.put(userToRemove,userMap.get(userToRemove));
            userMap.remove(userToRemove);
            lstUsers = userMap.values();                            
        }       
        return null;
    }
    
    /*
    * This method is used to Insert or Update the SetUp Objects. (Groups, GroupMember and QueueSObject)
    * @param   NA 
    * @return  void
    */
    public void saveQueuesAndUsers()
    {
        SavePoint dataSavePoint = Database.setSavepoint();      
        Boolean bSetupError = false;
        List<Group> lstQueues = new List<Group>();
        List<QueueSObject> lstQueueSObject = new list<QueueSObject>();
        set<ID> lstQueueIds = new set<ID>();
        bError = false;
        doUpdate = false;
        
        
        try
        {            
            if(bMaxCaseError || bNameError || validateRoutingConfig())
            {
                bError = true;
                if(bNameError)
                {
                     ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.NAMETEXT_OMNIQUEUE_HUM));
                     bNameError = false;
                }
                if(bMaxCaseError)
                { 
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.MAXCASESTEXT_OMNIQUEUE_HUM));
                    bMaxCaseError = false;
                }
                if(bRoutingConfigError)
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Routing_Config_Name_Error));
                    bRoutingConfigError = false;
                }
                return;
            }
            
            // Adding Routing Config here to validate the maximum work items and maximum limit
            if(omniQueueObj.Routing_Configuration_Name__c != null)
                {                        
                    //Logic to Add QueueRoutingConfigId to OmniQueue Group Object 
                    configItem = Omni_Configuration_Item__c.getValues(routingConfig.Configuration_Item_Name__c);
                    
                }
            
            
            if(omniQueueObjCpy == NULL)   //It is a New Record Creation.
            {
                //Validate the Input values
                bSetupError = validateInput();
                
                if(!bSetupError)
                {
                    //create Omni Queue
                    Group newGroup = new Group(Type=grpType, Name=omniQueueObj.Name.trim(), developerName = omniQueueObj.Name.trim().replace(' ','_'));
                                        
                    //Add Routing Config to the Omni Queue                    
                    if(omniQueueObj.Routing_Configuration_Name__c != null)
                    {                        
                        //Logic to Add QueueRoutingConfigId to OmniQueue Group Object 
                        newGroup.QueueRoutingConfigId = routingConfig.QueueRoutingConfig_Id__c;
                    }
                    
                    lstQueues.add(newGroup);
                                
                    //create Staging Queue
                    newGroup = new Group(Type=grpType, Name=omniQueueObj.Name.trim()+'_Stg', developerName = omniQueueObj.Name.trim().replace(' ','_')+'_Stg');
                    
                    lstQueues.add(newGroup);
                                        
                    Database.SaveResult[] srList = Database.insert(lstQueues);
                
                    for(Database.SaveResult sr : srList)
                    {
                        if(sr.isSuccess())
                        {
                            lstQueueIds.add(sr.getId());
                        }
                        else
                        {
                            lstQueueIds.clear();
                            bSetupError = true;
                            for (Database.Error err : sr.getErrors())
                            {   
                                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, err.getMessage()));
                            } 
                        }
                    }
                    
                    if(!lstQueueIds.isEmpty())     //Insert QueueSobject only if the queue was successfully inserted 
                    {                   
                        for(Group newQueue : [Select Id, Name FROM Group WHERE ID IN :lstQueueIds])
                        {
                            if(newQueue.Name.right(4).equalsIgnoreCase('_Stg'))
                            {
                                if(Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isCreateable() && Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isUpdateable() && Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_ID__c.isCreateable() && Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_ID__c.isUpdateable()) {
                                omniQueueObj.Staging_Queue_ID__c = newQueue.Id;
                                omniQueueObj.Staging_Queue_Name__c = newQueue.Name;
                                }
                            } 
                            else
                                omniQueueObj.Omni_Queue_ID__c = newQueue.Id;                        
                        }                   
                        for(Id newQueueId : lstQueueIds)
                        {
                            QueueSObject newQueueSobj = new QueueSObject(QueueId=newQueueId, SObjectType=configItem.Work_Item__c);
                            lstQueueSObject.add(newQueueSobj);
                        }
                        bSetupError = doUpsert(lstQueueSObject);                        
                    }
                    
                    //Add New Users to the Queue                    
                    if(!userMap.isEmpty() && String.isNotBlank(omniQueueObj.Omni_Queue_ID__c))
                    {
                        bSetupError = addUsersToQueue(userMap.keyset());
                    }                                       
                }                           
            }
            
            else   //It is an Edit of Existing Queue
            {                                                
                Group omniQueue = new Group();
                Group stagingQueue = new Group();
                if(Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isCreateable() && Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isUpdateable())
                {
                    omniQueueObj.Staging_Queue_Name__c = omniQueueObjCpy.Staging_Queue_Name__c;
                }                               
                if(!omniQueueObjCpy.Name.trim().equalsIgnoreCase(omniQueueObj.Name.trim()))
                {                   
                    if(validateQueueName())
                    {
                        bSetupError = true;                        
                    }
                    
                    if(valDuplicateExist(omniQueueObj.Name.trim()))
                    {
                        bSetupError = true;                        
                    }
                    
                    if(!bSetupError)
                    {
                        doUpdate = true;                                        
                        
                        //Update OmniQueue and Stg Queue Name (Group Objects)                       
                        omniQueue.Id = omniQueueObj.Omni_Queue_ID__c;
                        omniQueue.Name = omniQueueObj.Name.trim();
                        omniQueue.developerName = omniQueue.Name.replace(' ','_');                                        
                        stagingQueue.Id = omniQueueObj.Staging_Queue_ID__c;
                        stagingQueue.Name = omniQueueObj.Name.trim() + '_Stg';
                        stagingQueue.developerName = stagingQueue.Name.replace(' ','_');
                        if(Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isCreateable() && Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isUpdateable())
                        {
                            omniQueueObj.Staging_Queue_Name__c = stagingQueue.Name; 
                        }
                    }
                }
                
                //we are just validating the input Maximum_Cases__c field to ensure that we can go ahead with the Setup objects DMLS.                
                if(omniQueueObjCpy.Maximum_Cases__c != omniQueueObj.Maximum_Cases__c)
                {                   
                    if(validateMaxCases()) 
                        bSetupError = true;
                        
                    if(!bSetupError && validateThreshold((Integer)omniQueueObj.Maximum_Cases__c, getExistingThreshold(new set<Id>{omniQueueObj.Id},configItem.Name)))
                        bSetupError = true;                                                                                                         
                }
                
                                               
                if(omniQueueObjCpy.Routing_Configuration_Name__c != omniQueueObj.Routing_Configuration_Name__c && !bSetupError)
                {
                    if(validateThreshold((Integer)omniQueueObj.Maximum_Cases__c, getExistingThreshold(new set<Id>{omniQueueObj.Id},configItem.Name)) || validateMaxCases())
                    {
                        bSetupError = true; 
                    }
                    if(!bSetupError)
                    {
                        doUpdate = true;
                        //Logic to Update Routing Config on the Omni Queue 
                        omniQueue.Id = omniQueueObj.Omni_Queue_ID__c;
                        omniQueue.Name = omniQueueObj.Name.trim();
                        omniQueue.developerName = omniQueue.Name.replace(' ','_');
                        stagingQueue.Id = omniQueueObj.Staging_Queue_ID__c;
                        stagingQueue.Name = omniQueueObj.Name.trim() + '_Stg';
                        stagingQueue.developerName = stagingQueue.Name.replace(' ','_');
                        if(Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isCreateable() && Schema.SObjectType.OmniQueue__c.fields.Staging_Queue_Name__c.isUpdateable())
                        {
                            omniQueueObj.Staging_Queue_Name__c = stagingQueue.Name;
                        }
                        omniQueue.QueueRoutingConfigId = routingConfig.QueueRoutingConfig_Id__c; 
                    }
                }
                                                                
                if(doUpdate && !bSetupError)
                {
                    List<Group> queuesToUpdt = new List<Group>{omniQueue, StagingQueue};                    
                    bSetupError = doUpsert(queuesToUpdt);                   
                }
                 
                //Delete the existing users that were removed from the Queue
                if(!lstUserToDelete.isEmpty()&& !bSetupError) 
                {
                    bSetupError = removeUsersFromQueue(lstUserToDelete);
                }
                
                //Adding new Users to the Queue
                Set<Id> userIdsToAdd = new Set<Id>();
                for(Id userID : userMap.keyset())
                {
                    if(!queueUserIds.contains(userId)) userIdsToAdd.add(userID);
                }
                if(!userIdsToAdd.isEmpty() && String.isNotBlank(omniQueueObj.Omni_Queue_ID__c) && !bSetupError)
                    bSetupError = addUsersToQueue(userIdsToAdd);
            }
            if(bSetupError)
            {               
                bError = true;
                bSetupError = false;            
                Database.rollback(dataSavePoint);
            }           
        }
        catch(exception ex)
        {            
            Database.rollback(dataSavePoint);
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','saveQueuesAndUsers');
            bError = true;
            bSetupError = false;                                   
        }                
    }
    
    /*
    * This method is used to Insert or Update the OmniQueue__c Object.
    * @param   NA 
    * @return  void
    */         
    public void saveCustom()    
    {
        try
        {                            
            if(!bError)
            {
                if(omniQueueObjCpy == NULL) //It is an Insert from New page
                {
                    insert OmniQueueObj;
                }
                else  //It is an Update from Edit Page
                {                   
                    if(!doUpdate && OmniQueueObjCpy.Maximum_Cases__c != OmniQueueObj.Maximum_Cases__c)              
                        doUpdate = true;                                   
                    if(doUpdate)
                        update OmniQueueObj;
                }
            }                         
        }
        catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','saveCustom');
            bError = true;                      
        }                                                    
    }
    
    /*
    * This method is used to redirect to Detail page after save is successfull.
    * @param   NA 
    * @return  PageReference
    */
    public PageReference save()
    {
        if(bError) return null;
        else
        {
            PageReference pageRef = new PageReference('/'+OmniQueueObj.Id);
            pageRef.setRedirect(true);
            return pageRef; 
        }       
    } 
    
    /*
    * This method is used to Insert or Update the SetUp Objects. (Groups, GroupMember and QueueSObject)
    * @param   NA 
    * @return  void
    */
    public void checkDisableEdit()
    {
        try
        {  
            disableEdit = true;
            
            Profile oProfile = [Select Name From Profile Where ID =: UserInfo.getProfileId() limit 1];
            
            If(oProfile.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.SYSTEM_ADMINISTRATOR_PROFILE_HUM))
            {
               disableEdit = false;
            }
                        
            if(disableEdit){
                List<PermissionSetAssignment> lstPermissionSetAssignment= [SELECT Id, PermissionSet.Name FROM PermissionSetAssignment where AssigneeId=:UserInfo.getUserId()]; 
                for(PermissionSetAssignment oPSA: lstPermissionSetAssignment)
                {
                    if(oPSA.PermissionSet.Name == GLOBAL_CONSTANT_HUM.METMEDICAREENROLLMENTOMNIEDIT_PERMISSIONSET_HUM)
                        disableEdit = false;
                }
            }
        }
        catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','checkDisableEdit');
            bError = true;                                   
        }                
    }
    
    /*    
    * This method is used to Upsert the List of SObjects.
    * @param   List of SObjects to Upsert.
    * @return  Boolean (True for Error and False for Success)       
    */
    @TestVisible
    private Boolean doUpsert(List<SObject> lstSobject)
    {
        Boolean bReturnErr = false;
        Database.UpsertResult[] resultList = Database.upsert(lstSobject);
        for(Database.UpsertResult result : resultList)
        {
            if(!result.isSuccess())
            {
                
                for (Database.Error err : result.getErrors())
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, err.getMessage()));
                }
                bReturnErr = true;
            }                        
        }
        return bReturnErr;
    }
        
    
    /*    
    * This method is used to delete the GroupMember records for the users that were remove from OmniQueue.
    * @param   List of Users
    * @return  Boolean (True for Failure and False for Success)       
    */
    @TestVisible
   private Boolean removeUsersFromQueue(List<User> lstUsersDel)
    {    
        Boolean retVal=true;
        try
        {
            List<GroupMember> deleteUsers = [SELECT Id, GroupId, UserorGroupId FROM GroupMember WHERE GroupId =:omniQueueObj.Omni_Queue_ID__c AND UserorGroupId IN :lstUsersDel];         
            if(deleteUsers!=NULL){
                SObjectType myObj = Schema.getGlobalDescribe().get('GroupMember');
                if(myObj.getDescribe().isDeletable())
                    delete deleteUsers;
                retVal= false;  
            }
           return retVal;
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','removeUsersFromQueue');
            return true;
        }
    }
    
    /*    
    * This method is used to insert new GroupMember records for the users that were added to OmniQueue.
    * @param   Set of User Ids
    * @return  Boolean (True for Failure and False for Success)       
    */
    @TestVisible
    private Boolean addUsersToQueue(Set<Id> addUserIds)
    {
        List<GroupMember> addGroupMembers = new List<GroupMember>();
        for(Id userID : addUserIds)
        {                                                                     
            GroupMember newMember = new GroupMember();
            newMember.GroupId = omniQueueObj.Omni_Queue_ID__c;
            newMember.UserorGroupId = userId;
            addGroupMembers.add(newMember);
        }
        
        if(!addGroupMembers.isEmpty())
        {
            return (doUpsert(addGroupMembers));
        }
        return false;
    }
    
    /*    
    * This method does the validation for New OmniQueue Record creation.
    * @param   NA
    * @return  Boolean (True for Error and False for Success)       
    */
    @TestVisible
    private Boolean validateInput()    
    { 
        try
        {
            Boolean bReturnErr = false;
            //validate if any OmniQueue Name is Blank
            if(validateQueueName()) bReturnErr = true;
             
            //validate if any duplicate Queue Exists
            if(String.isNotBlank(omniQueueObj.Name.trim()) && valDuplicateExist(omniQueueObj.Name.trim()))  bReturnErr = true;      
                    
            //validate if Maximum_Cases__c is between 0 and assigned limit
            if(validateMaxCases())  bReturnErr = true; 
                    
            //validate if the Overall Count of Maximum_Cases__c is below assigned limit
            if(validateThreshold((Integer)omniQueueObj.Maximum_Cases__c, getExistingThreshold(NULL,configItem.Name)))   bReturnErr = true;
            
            return bReturnErr; 
        }                              
        catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','validateInput');
            return null;                                   
        }                                  
    }
    
    /*    
    * This method is used the Total Sum of Maximum_Cases__c of all the OmniQueue records.
    * @param   NA
    * @return  Integer       
    */
    @TestVisible
    private Integer getExistingThreshold(set<ID> recsToSkip, String ConfigItemName)
    {
        try
        {
            String sQuery = 'SELECT ID, Maximum_Cases__c FROM OmniQueue__c';
            String sWhereClause = ' WHERE Routing_Configuration_Name__r.Configuration_Item_Name__c =\''+ConfigItemName+'\'';
            Integer sumThreshold = 0;
            List<OmniQueue__c> existingQueues = new List<OmniQueue__c>();
            if(recsToSkip != null)
            {
                sQuery += sWhereClause+' AND ID NOT IN :';
                existingQueues = (List<OmniQueue__c>)HUMWithoutSharingQueryHelper.getListOfsObjects(sQuery, recsToSkip, NULL, NULL);
            }
            else
            {
                sQuery += sWhereClause;
                existingQueues = (List<OmniQueue__c>)HUMWithoutSharingQueryHelper.getListOfsObjects(sQuery, NULL, NULL, NULL);
            }
                    
            if(!existingQueues.isEmpty())
            {
                for(OmniQueue__c queue : existingQueues)
                {
                    sumThreshold += (Integer)queue.Maximum_Cases__c;
                }
            }
            
            return sumThreshold;
        }
        catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','getExistingThreshold');
            return null;                                   
        } 
        
    }
    
    /*    
    * This method is used to validate the Maximum_Cases__c value.
    * @param   NA
    * @return  Boolean (True for Error, False for Success)       
    */
    @TestVisible
    private Boolean validateThreshold(Integer newThreshold, Integer existThresholdSum)
    {  
        try
        {
            if(configItem != null && configItem.Maximum_Limit__c != null && configItem.Maximum_Limit__c > 0)
            {
                Integer thresholdValue = newThreshold + existThresholdSum;
                if(thresholdValue > configItem.Maximum_Limit__c)
                {
                    List<String> parameters = new List<String>{String.valueOf(thresholdValue), String.valueOf(configItem.Maximum_Limit__c).removeEnd('.0'),configItem.Business_Area__c};
                    String labelStr = Label.THRESHOLDERROR_OMNIQUEUE_HUM;
                    String errorStr = String.format(labelStr, parameters);
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, errorStr));            
                    return true;            
                }
                else return false; 
            } else return true;
        }
        catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','validateThreshold');
            return null;                                   
        } 
        
                                 
    }
    
    /*    
    * This method is used to validate if any Queue already exists with the same name.
    * @param   NA
    * @return  Boolean (True for Error, False for Success)       
    */
    @TestVisible
    private Boolean valDuplicateExist(String newQueueName)
    {
        List<Group> lstQueues = [SELECT ID, Name FROM Group Where Name = :newQueueName and Type = 'Queue'];
        if(!lstQueues.isEmpty())
        {           
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.DUPLICATEERROR_OMNIQUEUE_HUM));
            return true;                                
        }
        else    return false;    
    }
    
    /*
    * This method is used to validate if the Queue name is Empty.
    * @param   NA
    * @return  Boolean (True for Error, False for Success)       
    */
    @TestVisible
    private Boolean validateQueueName()
    {
        if(String.isBlank(omniQueueObj.Name.trim()))
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.EMPTYNAME_OMNIQUEUE_HUM));            
            return true;
        }
        else return false;
    }
    
    /*    
    * This method is used to validate the Maximum_Cases__c value is between the range 0 to 1000.
    * @param   NA
    * @return  Boolean (True for Error, False for Success)       
    */
    @TestVisible
    private Boolean validateMaxCases()
    {
        if(configItem != null && configItem.Maximum_No_of_Work_Items__c != null && configItem.Maximum_No_of_Work_Items__c > 0)
        {
            if(omniQueueObj.Maximum_Cases__c == null || omniQueueObj.Maximum_Cases__c < 0 || omniQueueObj.Maximum_Cases__c > configItem.Maximum_No_of_Work_Items__c)
            {
                List<String> parameters = new List<String>{String.valueOf(configItem.Maximum_No_of_Work_Items__c).removeEnd('.0'), configItem.Business_Area__c};
                String labelStr = Label.INVALIDMAXCASES_OMNIQUEUE_HUM;
                String errorStr = String.format(labelStr, parameters);
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, errorStr));            
                return true;            
            }
            
            else return false;
        }else return true;
        
    }  

    /*    
    * This method is used to validate the Routing_Configuration_Name__c value
    * @param   NA
    * @return  Boolean (True for Error, False for Success)       
    */
    @TestVisible
    private Boolean validateRoutingConfig()
    {
        if(omniQueueObj.Routing_Configuration_Name__c == null)
            {
                bRoutingConfigError = true;
                return true;
            }
        else{return false;}
            
    }   
    
    
     /*    
    * This method is used to check the remaining limit from VF page on select of Routing Config
    * @param   NA
    * @return  NA     
    */
    
    public void getRemainingThreshold()
    {
        try
        {
            if(omniQueueObj.Routing_Configuration_Name__c != null)
            {
                routingConfig = [Select ID, QueueRoutingConfig_Id__c,Configuration_Item_Name__c from Omni_Routing_Configuration__c WHERE ID = :omniQueueObj.Routing_Configuration_Name__c LIMIT 1];
                Omni_Configuration_Item__c oldConfigItem = Omni_Configuration_Item__c.getValues(routingConfig.Configuration_Item_Name__c);
                Integer existingThreshold = getExistingThreshold(NULL,oldConfigItem.Name);
                remainingThreshold = Integer.valueOf(oldConfigItem.Maximum_Limit__c) - existingThreshold;
            }
        }
        catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'OmniQueueEdit_X_HUM','getRemainingThreshold');

        } 
            
    } 
   
}