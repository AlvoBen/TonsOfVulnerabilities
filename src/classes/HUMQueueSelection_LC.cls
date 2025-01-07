/************************************************************************************************************************
Apex class Name    : HUMQueueSelection_LC
Version            : 1.0
Function           : Apex Class to poulate/save current queue on QueueSwitchingCmp.
Created On         : November 2 2022
Test Class         : HUMQueueSelection_LT_HUM

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*  Manohar Billa               					11/02/2022          Original Version
***************************************************************************************************************************/
public class HUMQueueSelection_LC {

    public static List<String> lstQueueNames = NULL; 
    public static User oUserRec;

    /*
    * Method Name : init
    * Description : This method returns list of queue selection options and selected queue to LWC comp 
    * Return type : N/A
    * Paramater   : N/A
    */

    @AuraEnabled(cacheable=false)
    public static ResponseWrapper init(){
        ResponseWrapper resObj = new ResponseWrapper();
        List<String> lstGrpValues = new List<String>();
        Set<String> setGrpName = new Set<String>();
        List<string> queueListOptions = new List<string>();
        try 
        {
            getQueues();
            
            ouserRec = [Select Current_Queue__c, Mentor_Sub_Function__c from User where ID=:UserInfo.getUserId()]; 
            
            if(lstQueueNames != NULL && 1 == lstQueueNames.size())
            {
                if(String.isBlank(oUserRec.current_queue__c) || lstQueueNames.get(0) != oUserRec.current_queue__c ) 
                {
                    if(Schema.sObjectType.User.fields.current_queue__c.isUpdateable())
                    oUserRec.current_queue__c = lstQueueNames.get(0).replace('_',' ');
                    update oUserRec;
                } 
            }  
           
            if(lstQueueNames.isEmpty())
            {
                User uRec = new User(id=ouserRec.id);
                if(Schema.sObjectType.User.fields.Mentor_Sub_Function__c.isUpdateable())
                uRec.Mentor_Sub_Function__c ='';
                update uRec;
            }
            lstQueueNames.sort();
            
            for(String sGroupName : lstQueueNames)
            {  
                sGroupName = sGroupName.replace('_',' ');  
                queueListOptions.add(sGroupName);

            }
            resObj.queueOptions = queueListOptions;
            if(queueListOptions != NULL && 1 == queueListOptions.size())
            {
                resObj.selQueue = queueListOptions[0];
            }

            If(Schema.sObjectType.User.fields.Current_Queue__c.isAccessible()){
        
                resObj.currentQueue = ouserRec.Current_Queue__c;
            }         
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMQueueSelection_LC_Ctrl', 'init');
        }
        return resObj;
    }

     /*
    * Method Name : getQueues
    * Description : This method returns list of Queue names based on Work Queue setup and Groups that the user is member of
    * Return type : Void
    * Paramater   : N/A
    */

    private static void getQueues()
    {
        Map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new Map<String, Work_Queue_Setup__c>([Select Name,OwnerId, id,Public_Group_Name__c, Work_Queue_Name__c from Work_Queue_Setup__c where IsActive__c = TRUE ]);
        lstQueueNames = new List<String>();
        Set<String> setQueueNames = new Set<String>();
        Map<Id, GroupMember> mapGrpMem = new Map<Id, GroupMember>([select GroupId, Group.name from GroupMember Where UserOrGroupId =: Userinfo.getUserId()]);
        
        for(Id groupMemId : mapGrpMem.Keyset()) 
        {
            lstQueueNames.add(mapGrpMem.get(groupMemId).Group.name);
        }
       
        for (String sWorkQueue : mapWorkQueueSetup.keySet())
        {
            for(string grpName : lstQueueNames) 
            {
                if((mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c).trim().equalsIgnoreCase(grpName))
                    setQueueNames.add(mapWorkQueueSetup.get(sWorkQueue).Work_Queue_Name__c);
            }
        }

        lstQueueNames = new List<String>();
        lstQueueNames.addAll(setQueueNames);
    }

    /*
    * Method Name : saveQueue
    * Description : This method is used to save the selected picklist queue value to the user field
    * Return type : String
    * Paramater   : string - selected Queue
    */

    @AuraEnabled
    public static string saveQueue(string selQ)
    {
        try
        {
            User oUser =new User(Id = Userinfo.getUserId());

            if(Schema.sObjectType.User.fields.Current_Queue__c.isUpdateable())
            {
                oUser.Current_Queue__c = selQ;
                update oUser;
                return 'Success';
            }else{
                return 'You dont have access to update Queue';
            }    
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMQueueSelection_LC_Ctrl','saveQueue');
             return  ex.getMessage();
        }
    }

    public class ResponseWrapper
    {
        @AuraEnabled
        public string selQueue {get;set;}
        @AuraEnabled
        public string currentQueue {get;set;}
        @AuraEnabled
        public List<string> queueOptions {get;set;}
    }
}