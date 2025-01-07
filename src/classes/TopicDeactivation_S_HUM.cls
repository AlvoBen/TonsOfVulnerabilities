/****************************************************************************
Apex Class Name :  TopicDeactivation_S_HUM
Version: 1.0 
Created Date     : June 10 2016
Function         : Class for deactivating the active topics which includes validations To be done prior To deactivation
TestClass Name :  TopicDeactivation_T_HUM
Modification Log : 
 *  Modification ID     Developer              Code Review           Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Sharan Shanmugam                          06/10/2016           Original Version     
 *          1.1         Harshith Mandya         50014             11/30/2106           Implemented REQ - 290990 -  removed all lookup related fields from 
 *                                                                                      Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
 *          1.2         Vijaya Koriprolu        268583                  08/13/2018           Implemented REQ - 366118 - ability to deactivate a topic name even when there are open cases associated with the topic.                                                                                                                                                                   
 *          1.3         Prudhvi Pamarthi                           05/05/2021            US#2212581 Checkmarx error fix
 **********************************************************************************************************************************/
Public With sharing Class TopicDeactivation_S_HUM 
{
    Private TopicDeactivation_D_HUM dLayer = New TopicDeactivation_D_HUM();
    
    /**
    * constructor method for TopicDeactivation_C_HUM
    * <p>
    * Method to set the values for TopicDeactivation_C_HUM.
    *
    * @param cls              TopicDeactivation_C_HUM class instance
    */
    Public void initialize(TopicDeactivation_C_HUM cls)
    {
        Try
        {            
            If (ApexPages.currentPage().getParameters().containsKey(GLOBAL_CONSTANT_HUM.QUEUEVIEW_WQID_HUM))
            {
                cls.sWQSId = (ID)ApexPages.currentpage().getParameters().get(GLOBAL_CONSTANT_HUM.QUEUEVIEW_WQID_HUM);
                If (cls.sWQSId!= null)
                {
                    Work_Queue_Setup__c wqs = dLayer.wqInfo(cls.sWQSId);
                    If (wqs!= NULL)
                    cls.workQueueSetup.Name = wqs.name;
                    topics(cls);
                }                               
            }
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.logErrors(e,'TopicDeactivation_S_HUM','initialize');
        }   
    } 
    
    /**
    * Method to display the list of topics related to work queue
    *
    * @param     cls                    TopicDeactivation_C_HUM class instance
    * @return    void
    */ 
    Private void topics(TopicDeactivation_C_HUM cls)        
    {
        List<Work_Queue_Junction__c> lstWqj= dLayer.getWQJ(cls.sWQSId);
        
        //To display the active topics of a work queue setup that can be deactivated
        If (!lstWqj.isEmpty())
        {
            cls.sTopic =  lstWqj[0].Id;
            For (Work_Queue_Junction__c wqj : lstWqj)
            {
                If (wqj.Deactivate_Topic__c)        cls.lstTopic.add(New SelectOption((String)wqj.Work_Queue_Topic_Name__c,(String)wqj.Work_Queue_Topic_Name__r.Name));
            }
        }
        //Displays error message when there Is no active topics to deactivate
        If (cls.lstTopic.isEmpty())
        {
            cls.bActiveTopic = true;
            ApexPages.addMessage(New ApexPages.Message(ApexPages.Severity.ERROR,system.label.ACTIVETOPICERROR_WORKQUEUE_HUM));  
        }
    }  
    
    /**
    * Returns PageReference url when topic Is deactivated
    * <p>
    * Method to deactivate topic.
    *
    * @param    cls                 TopicDeactivation_C_HUM class instance
    * @return   PageReference
    */ 
    Public Pagereference deactivateTopic(TopicDeactivation_C_HUM cls)
    {
        Try
        {
            Work_Queue_Junction__c wqj = New Work_Queue_Junction__c();
            List<Case> lstCase = New List<Case>();
            List<Task> lstTask = New List<Task>();
            If (cls.sTopic!= null)      cls.topic = dLayer.getWQT(cls.sTopic);
            If (cls.topic.Work_Queue_Juntions__r[0].Work_Queue_Setup_Name__c != null && !String.isBlank(cls.topic.Name)) 
            {
                lstCase = dLayer.getCaseTopic(cls.topic.Work_Queue_Juntions__r[0].Work_Queue_Setup_Name__r.Name, cls.topic.Name);
                lstTask = dLayer.getTaskTopic(cls.topic.Work_Queue_Juntions__r[0].Work_Queue_Setup_Name__r.Name, cls.topic.Name);
            }
            //Deactivating the topic And deleting queue view item record whose topic value Is same as the deactivated topic
            
                if(Schema.sObjectType.Work_Queue_Topic__c.fields.IsActive__c.isUpdateable())
                cls.topic.IsActive__c = false;
                dLayer.updateTopic(cls.topic);
                deleteQueueViewItem(cls);
           
            Return redirectToDeatilPage(cls.sWQSId);
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'TopicDeactivation_S_HUM','deactivateTopic');
            Return null;
        }
    } 
    
     /**
    * Invoked from DeactivateTopic method.This method deletes the queue view item
    *
    * @param    cls                 TopicDeactivation_C_HUM class instance
    * @return   void
    */ 
    Private void deleteQueueViewItem(TopicDeactivation_C_HUM cls)
    {
        List<Queue_View__c> lstQv = New List<Queue_View__c>();
        List<Queue_View_Item__c> lstQvi = New List<Queue_View_Item__c>();
        List<Queue_View_Item__c> lstDeleteQvi = New List<Queue_View_Item__c>();
        If (cls.sWQSId!= null)
        {
            lstQv = dLayer.getQVFromWQ(cls.sWQSId);
            If (!lstQv.isEmpty())
            {
                For (Queue_View__c qv : lstQv)
                {
                    lstQvi = New List<Queue_View_Item__c>();
                    lstQvi = qv.Queue_View_Items__r;
                    
                    For (Queue_View_Item__c qvi : lstQvi)
                    {
                        If ((qvi.API_Name__c == GLOBAL_CONSTANT_HUM.QUEUEVIEWITEM_TOPIC_HUM) && (qvi.Field_Operator__c == GLOBAL_CONSTANT_HUM.EQUALS_HUM) && (qvi.Field_Value__c == cls.topic.Name))
                            lstDeleteQvi.add(qvi);
                    }
                }
                If (!lstDeleteQvi.isEmpty())        dLayer.deleteQVI(lstDeleteQvi);
            }       
        }
    }
    
     /**
    * Returns PageReference url when user clicks on cancel button
    * <p>
    * Method Redirects to Work queue setup page
    *
    * @param    cls                 TopicDeactivation_C_HUM class instance
    * @return   PageReference
    */ 
    Public pageReference cancel(TopicDeactivation_C_HUM cls)
    {
        Try
        {
            Return redirectToDeatilPage(cls.sWQSId);
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'TopicDeactivation_S_HUM','cancel');
            Return null;
        }
    }
    
    /**
    * Returns PageReference url which redirects to the respective detail page 
    * <p>
    * Method Redirects to the respective detail page
    *
    * @param    id                  id of the object
    * @return   PageReference
    */ 
    Private pageReference redirectToDeatilPage(id wqId)
    {
        PageReference pg = New PageReference('/'+wqId);
        pg.setRedirect(True);
        Return pg;  
    }
}