/***************************************************************************************************************************************************************************************************
Apex Class Name  : QueueViewItemCreation_S_HUM
Version          : 1.0
Created Date     : May 12 2015
Function         : Class to create the Queue View Items based on the Queue View & Queue View Setup.
Test Class       : QueueViewItemCreation_T_HUM
 ***************************************************************************************************************************************************************************************************
Modification Log:

 * Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Sharan Shanmugam            29395                06/01/2016                   Original Version
 * Sharan Shanmugam                                 11/10/2016                   Modified code to populate PGV id and Group Name
 * Sharan Shanmugam                                 01/23/2017                   Fixed 254402.0001 by modifying cancel method 
 * Sharan Shanmugam                                 02/02/2017                   Modified logic to correct redirect page
 * Saradha Yeduruvada                               08/20/2018                   REQ - 366120 editible  PGV Name field
 * Santhi Mandava                                   12/31/2018                   Removed dpaas call
 * Manikanta                                        07/11/2019                   REQ - 399177 - Added logic to resolve the queue view item issue (collection size exceeds maximum size of 1,000)
 * Mithra Bharadwaj                                                              US#2089546 SonarQube Changes
 * Prudhvi Pamarthi                                 05/05/2021                   US#221581 checkmarx fix
 * Prudhvi Pamarthi                                 07/30/2021                   US#2494738 Fix for INC1197867 Service Center And Department Not visible on View Detail screen
 * Prasuna Pattabhi                                 09/01/2021                   US#2548436 Checkmarx scan issue fix
 * Prasuna Pattabhi                                 09/09/2021                   US#2548436 Checkmarx scan issue changes rollback
 * Prasuna Pattabhi                                 09/20/2021                   US#2548436 Checkmarx scan issue fix
 ***************************************************************************************************************************************************************************************************/

public with sharing class QueueViewItemCreation_S_HUM
{
    private QueueViewItemCreation_D_HUM dLayer = new QueueViewItemCreation_D_HUM();
    private Group grp = new Group();
          
     /**
    * Method to get work queue and queue view ids from URL.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    public void getWQQVIds(QueueViewItemCreation_C_HUM cls)   
    {
        if(ApexPages.currentPage().getParameters().containsKey(GLOBAL_CONSTANT_HUM.QUEUEVIEW_QID_HUM)) // Create Queue View Item from Work Queue Setup
        {
            createQueueViewItemFromWorkQueueSetup(cls);                               
        }            
        else if(ApexPages.currentPage().getParameters().containskey(GLOBAL_CONSTANT_HUM.ID_HUM))  // Edit Queue View
        {        
            editQueueView(cls);        
        }            
        else if(ApexPages.currentPage().getParameters().containskey(GLOBAL_CONSTANT_HUM.QUEUEVIEW_RETURL_HUM)) // New Queue View from Work Queue related list
        {       
            createQueueViewFromWorkQueueRelatedList(cls);           
        }
        if(! (ApexPages.currentPage().getParameters().containskey(GLOBAL_CONSTANT_HUM.ID_HUM) && 
        ApexPages.currentPage().getParameters().containskey(GLOBAL_CONSTANT_HUM.QUEUEVIEW_QID_HUM) && 
        ApexPages.currentPage().getParameters().containskey(GLOBAL_CONSTANT_HUM.QUEUEVIEW_RETURL_HUM))) 
        {
            cls.bShowQueueView = true;
        }       
    }
    
     /**
    * Method to populate the Queue Views for selection once user clicks on Save on WQS creation page.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    private void queueViews(QueueViewItemCreation_C_HUM cls)   
    {
        List<Queue_View__c> lstWQ = new List<Queue_View__c>();
        if(cls.sWQSId != null) lstWQ = dLayer.queueWQ(cls.sWQSId);                            
        if(!lstWQ.isEmpty())
        {
            cls.sQVType =  lstWQ[0].Id;
            for(Queue_View__c qv:lstWQ)
            {
                cls.lstWQI.add(new SelectOption((String)qv.id,(String)qv.name));
                cls.mapQV.put((String)qv.id,(String)qv.name);
            }            
            cls.lstGroups = dLayer.groupName(lstWQ[0].Public_Group_View_Id__c);
            cls.publicGroupRecordName = cls.lstGroups[0].Name;
        }
    }
    
     /**
    * Method to get the Queue Views.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    private void getQueueViews(QueueViewItemCreation_C_HUM cls)
    {
        List<Queue_View_Item__c> lstQueueViewItems = new List<Queue_View_Item__c>();
        if(cls.sQVId != null) lstQueueViewItems = dLayer.getQVI(cls.sQVId);        
        if(!lstQueueViewItems.isEmpty()) cls.bShowTable=true;       
        //Get Queue View Items 
        for(Queue_view_Item__c qvi : lstQueueViewItems)
        {
            cls.lstQViewItemWrapper.add(new HUMQueueViewCriteriaWrapper(qvi.Name, '', qvi, cls.counter));
            cls.counter++;
        }
    }
    
      /**
    * Returns pageReference url when QVI is added
    * <p>
    * Method to add the Queue View Items to the wrapper list once user clicks on Add button.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              pageReference
    */ 
    public pageReference addQVI(QueueViewItemCreation_C_HUM cls)
    {
        try
        {
            String sName = cls.mapQV.get(cls.sQVType);
            Boolean bMutlipleTopics = false;
            if(cls.queueViewItem.API_Name__c == null || cls.queueViewItem.Field_Operator__c == null || cls.queueViewItem.Field_Value__c == null)
            {
                ApexPages.Message errorMsg = new ApexPages.Message(ApexPages.Severity.Warning, Label.QUEUEVIEWITEMERROR_WORKQUEUE_HUM);
                ApexPages.addMessage(errorMsg);
                return null;
            }       
            if(cls.queueViewItem.API_Name__c == 'Topic')
            {
                String sTopic = cls.queueViewItem.Field_Value__c;
                if(sTopic.contains(','))
                {
                    bMutlipleTopics = true;
                    String fieldOperator = cls.queueViewItem.Field_Operator__c;
                    List<String> lstTopics = sTopic.split(',');
                    for(String topicInstance : lstTopics)
                    {
                        cls.queueViewItem = new Queue_View_Item__c();
                        cls.queueViewItem.API_Name__c = 'Topic';
                        cls.queueViewItem.Field_Operator__c = fieldOperator;
                        cls.queueViewItem.Field_Value__c = topicInstance;
                        HUMQueueViewCriteriaWrapper queueViewCriteriaInstance  = new HUMQueueViewCriteriaWrapper(sName, cls.sQVType, cls.queueViewItem, cls.counter);
                        cls.counter++;  
                        if(cls.mapWrapper.containskey(cls.sQVType))
                        {
                            cls.mapWrapper.get(cls.sQVType).add(queueViewCriteriaInstance);
                        }
                        else
                        {
                           cls.mapWrapper.put(cls.sQVType, new List<HUMQueueViewCriteriaWrapper>{queueViewCriteriaInstance});              
                        }
                        if(cls.sWQSId != null) 
                        {
                           filterQVI(cls);
                        }
                        else
                        {
                           cls.lstQViewItemWrapper.add(queueViewCriteriaInstance);
                        }
                    }
                }
            }
            cls.bShowTable = true;
            if(cls.queueViewItem.API_Name__c != 'Topic' || (!bMutlipleTopics && cls.queueViewItem.API_Name__c == 'Topic'))
            {
                HUMQueueViewCriteriaWrapper queueViewCriteriaInstane  = new HUMQueueViewCriteriaWrapper(sName, cls.sQVType, cls.queueViewItem, cls.counter);
                cls.counter++;     
                 
                if(cls.mapWrapper.containskey(cls.sQVType))
                {
                    cls.mapWrapper.get(cls.sQVType).add(queueViewCriteriaInstane);
                }
                else
                {
                   cls.mapWrapper.put(cls.sQVType, new List<HUMQueueViewCriteriaWrapper>{queueViewCriteriaInstane});              
                }
                if(cls.sWQSId != null) 
                {
                   filterQVI(cls);
                }
                else
                {
                   cls.lstQViewItemWrapper.add(queueViewCriteriaInstane);
                }
            }
            cls.queueViewItem = new Queue_View_Item__c();
            cls.lstQViewItemWrapper.sort();
            cls.sQueueViewItems = JSON.serialize(cls.lstQViewItemWrapper);
            cls.sQueueViewItems = '{"data":'+cls.sQueueViewItems+'}';
            return null;
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'QueueViewItemCreation_S_HUM','addQVI');
            return null;
        }
    }
    
     /**
    * Returns pageReference url when QVI is filtered
    * <p>
    * Method to filtering the Queue View Items displayed on the screen.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              PageReference
    */ 
    private PageReference filterQVI(QueueViewItemCreation_C_HUM cls)
    {
        if(cls.mapWrapper.containsKey(cls.sQVType))
        {
            cls.lstQViewItemWrapper = cls.mapWrapper.get(cls.sQVType);  
        }
        else
        {
            cls.bShowTable = false;
        }
        return null;
    }
    
     /**
    * Returns pageReference url when QVI is removed
    * <p>
    * Method to remove the Queue View Items from the wrapper list once user clicks on Remove link.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              pageReference
    */ 
    public pageReference removeQVI(QueueViewItemCreation_C_HUM cls)
    {
        Map<Id, List<HUMQueueViewCriteriaWrapper>> removeMap = new Map<Id, List<HUMQueueViewCriteriaWrapper>>();
        Set<HUMQueueViewCriteriaWrapper> setWrapper = new Set<HUMQueueViewCriteriaWrapper>();
        try
        { 
            if(cls.sWQSId != null)
            {
                removeQVIWQS(cls);               
            }            
            else
            {
                removeQVIWithoutWQS(cls);
            }   
             if (cls.lstQViewItemWrapper!= null && !cls.lstQViewItemWrapper.isEmpty())
            {
                cls.sQueueViewItems = JSON.serialize(cls.lstQViewItemWrapper);
                cls.sQueueViewItems = '{"data":'+cls.sQueueViewItems+'}';
            }
            else
            {
                cls.sQueueViewItems = '';
            }
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'QueueViewItemCreation_S_HUM','removeQVI');
            return null;
        }
    }
    
     /**
    * Returns list of QVI formed under a Queue View
    * <p>
    * Method to create a list of Queue View Items
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              List<Queue_View_Item__c>
    */ 
    private List<Queue_View_Item__c> constructQVI(QueueViewItemCreation_C_HUM cls)
    {
        List<Queue_View_Item__c>  lstQWIRecords = New List<Queue_View_Item__c>();
        for(HUMQueueViewCriteriaWrapper humQVCW : cls.lstQVCWrapperInsert)
        {
            Queue_View_Item__c qvi = New Queue_View_Item__c();
            if(Schema.sObjectType.Queue_View_Item__c.fields.Queue_View__c.isCreateable())
            qvi.Queue_View__c = humQVCW.iQVId;
            if(Schema.sObjectType.Queue_View_Item__c.fields.API_Name__c.isCreateable())
            qvi.API_Name__c = humQVCW.objQViewItem.API_Name__c;
            if(Schema.sObjectType.Queue_View_Item__c.fields.Field_Operator__c.isCreateable())
            qvi.Field_Operator__c = humQVCW.objQViewItem.Field_Operator__c;
            if (humQVCW.objQViewItem.API_Name__c!= null && (humQVCW.objQViewItem.API_Name__c == GLOBAL_CONSTANT_HUM.QUEUEVIEW_EMPLOYERGROUPNAME_HUM || 
            humQVCW.objQViewItem.API_Name__c == GLOBAL_CONSTANT_HUM.QUEUEVIEW_COMPLAINTTYPE_HUM ))
            {
                qvi.Field_Value__c = cls.mapEncryptedValues.containskey(humQVCW.objQViewItem.Field_Value__c) ? 
                cls.mapEncryptedValues.get(humQVCW.objQViewItem.Field_Value__c): humQVCW.objQViewItem.Field_Value__c;
                If (!String.isBlank(qvi.Field_Value__c)) qvi.Field_Value__c = qvi.Field_Value__c.substring(9,qvi.Field_Value__c.length()-3);                                                     
            }
            else
            {
                if(Schema.sObjectType.Queue_View_Item__c.fields.Field_Value__c.isCreateable())
                qvi.Field_Value__c = humQVCW.objQViewItem.Field_Value__c;   
            }
            lstQWIRecords.add(qvi);
        }
        return lstQWIRecords;
    }
    
     /**
    * Returns pageReference url which redirects to respective detail page
    * <p>
    * Method to save the Queue View Items & redirect the user to the QWS detail page
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              pageReference
    */ 
    public pageReference save(QueueViewItemCreation_C_HUM cls)
    {
        try
        {
            if(cls.bRenderQWI)
            {
                if(!upsertQueueViewItemRecords(cls)) return null;                                                       
            }
            else if(!cls.bRenderQWI)
            {
                if(String.isBlank(cls.queueView.Name))
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Warning, Label.QUEUEVIEWERROR_WORKQUEUE_HUM));
                    return null;
                }
                else
                {
                    upsertQV(cls);    
                }
                if(cls.queueView.id != null)
                {
                    if (cls.lstDeleteQueueViewItem == null || cls.lstDeleteQueueViewItem.isEmpty())
                    {
                        for(HUMQueueViewCriteriaWrapper humQVCW : cls.lstQVCWrapperInsert)
                        {
                            conditionallyEncrypt(humQVCW,cls);
                        }
                     }   
                    
                    deleteQueueViewItems(cls);
                    if(checkGroup(cls))
                    {
                        return null;
                    }
                    else
                    {
                        cls.workQueueSetup = dLayer.getWorkQueue(cls.queueView.Work_Queue_Name__c);
                        if(Schema.sObjectType.Queue_View__c.fields.Queue_View_Name__c.isUpdateable())
                        cls.queueView.Queue_View_Name__c = cls.queueView.Name;  
                        if(!cls.bIsEdit)
                        {
                            if(Schema.sObjectType.Queue_View__c.fields.Public_Group_View_Id__c.isUpdateable())
                            cls.queueView.Public_Group_View_Id__c = grp.Id;
                            if(Schema.sObjectType.Queue_View__c.fields.Group_Name__c.isUpdateable())
                            cls.queueView.Group_Name__c = grp.Name;
                            if(Schema.sObjectType.Queue_View__c.fields.Queue_Name__c.isUpdateable())
                            cls.queueView.Queue_Name__c = cls.workQueueSetup.Name;
                            if(Schema.sObjectType.Queue_View__c.fields.Service_Center__c.isUpdateable())
                            cls.queueView.Service_Center__c = cls.workQueueSetup.Service_Center__c;
                            if(Schema.sObjectType.Queue_View__c.fields.Department__c.isUpdateable())
                            cls.queueView.Department__c = cls.workQueueSetup.Department__c;
                        }
                        
                         else
                        { 
                           boolean flag = UpdateGroup(cls.queueView.Public_Group_View_Id__c,cls.publicGroupRecordName);
                           cls.queueView.Group_Name__c = cls.publicGroupRecordName;
                           cls.queueView.Queue_Name__c = cls.workQueueSetup.Name;
                           cls.queueView.Service_Center__c = cls.workQueueSetup.Service_Center__c;
                           cls.queueView.Department__c = cls.workQueueSetup.Department__c;
                        }
                        update cls.queueView;
                    }
                }
            }            
            Id redirectId = (cls.queueView.Id == NULL? cls.sWQSId : cls.queueView.Id);
            return redirectPage('/' + String.valueOf(redirectId));
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'QueueViewItemCreation_S_HUM','save');
            return null;
        }
    }
    
     /**
    * Returns boolean to check the status of QVI upsert
    * <p>
    * Method to upsert queue view item records and return the status of DML action.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              Boolean
    */ 
    private Boolean upsertQueueViewItemRecords(QueueViewItemCreation_C_HUM cls)
    {
        if(cls.mapWrapper != null &&  cls.mapWrapper.values() != null)
        {
            for(List<HUMQueueViewCriteriaWrapper> lstHumQVCW : cls.mapWrapper.values())
            {
                cls.lstQVCWrapperInsert.addAll(lstHumQVCW); 
            }
        }
        getEncrytedValue(cls);        
        cls.lstQWIRecords = constructQVI(cls);
        if(!cls.lstQWIRecords.isEmpty())
        {
            System.debug('cls.lstQWIRecords--------'+cls.lstQWIRecords);
            dLayer.upsertQVI(cls.lstQWIRecords);
            return true;
        }
        else
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Warning,Label.QUEUEVIEWITEMERROR_WORKQUEUE_HUM));
            return false;
        }    
    }
    
     /**
    * Method to get the encrypted value for complaint type field
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    private void getEncrytedValue(QueueViewItemCreation_C_HUM cls)
    {
        List<String> lstCLearTextValue = new List<String>();           
        cls.mapEncryptedValues = new Map<String,String>();
        for(HUMQueueViewCriteriaWrapper humQVCW : cls.lstQVCWrapperInsert)
        {
            if(humQVCW.objQViewItem.API_Name__c != null && (humQVCW.objQViewItem.API_Name__c == GLOBAL_CONSTANT_HUM.QUEUEVIEW_EMPLOYERGROUPNAME_HUM || 
            humQVCW.objQViewItem.API_Name__c == GLOBAL_CONSTANT_HUM.QUEUEVIEW_COMPLAINTTYPE_HUM )) cls.mapEncryptedValues.put(humQVCW.objQViewItem.Field_Value__c,humQVCW.objQViewItem.Field_Value__c);                                        
        }
    }
    
     /**
    * Method to encrypt complaint type field value in the queue view item
    *
    * @param HUMQueueViewCriteriaWrapper           humQVCW
    * @param QueueViewItemCreation_C_HUM           cls 
    * @return                                      void
    */ 
    private void conditionallyEncrypt(HUMQueueViewCriteriaWrapper humQVCW,QueueViewItemCreation_C_HUM cls)
    {
        Queue_View_Item__c qvi = New Queue_View_Item__c();
        if (humQVCW.objQViewItem != null && humQVCW.objQViewItem.id != null)
        {     
            qvi= humQVCW.objQViewItem;
        }
        else
        {
            qvi.Queue_View__c = cls.queueView.Id;
        }         
        qvi.API_Name__c = humQVCW.objQViewItem.API_Name__c;
        qvi.Field_Operator__c = humQVCW.objQViewItem.Field_Operator__c;
        if (humQVCW.objQViewItem.API_Name__c!= null && (humQVCW.objQViewItem.API_Name__c == GLOBAL_CONSTANT_HUM.QUEUEVIEW_EMPLOYERGROUPNAME_HUM || 
        humQVCW.objQViewItem.API_Name__c == GLOBAL_CONSTANT_HUM.QUEUEVIEW_COMPLAINTTYPE_HUM ))
        {
            qvi.Field_Value__c = cls.mapEncryptedValues.containskey(humQVCW.objQViewItem.Field_Value__c) ? 
            cls.mapEncryptedValues.get(humQVCW.objQViewItem.Field_Value__c): humQVCW.objQViewItem.Field_Value__c;
            if (!String.isBlank(qvi.Field_Value__c)) qvi.Field_Value__c = qvi.Field_Value__c;
        }
        else
        {
            qvi.Field_Value__c = humQVCW.objQViewItem.Field_Value__c; 
        }
        cls.lstQWIRecords.add(qvi);    
    }
    
     /**
    * Returns pageReference url which redirects the page to WQ detail page
    * <p>
    * Method to redirect the user to the QWS detail page when he clicks on Cancel button
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              pageReference
    */ 
    public pageReference cancel(QueueViewItemCreation_C_HUM cls)
    {
        try
        {
            return (cls.bRenderQWI?redirectPage('/' + string.escapeSingleQuotes(cls.sWQSId)): redirectPage(ApexPages.currentpage().getParameters().get(GLOBAL_CONSTANT_HUM.QUEUEVIEW_RETURL_HUM)));
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'QueueViewItemCreation_S_HUM','cancel');
            return null;
        }
    }
    
    /**
    * Returns pageReference url which redirects the page to WQ detail page
    * <p>
    * Method to redirect the user to the QWS detail page when he clicks on Cancel button
    *
    * @param sURL         String 
    * @return              pageReference
    */ 
    private PageReference redirectPage(String sURL)
    {
        PageReference pageRef = new PageReference(sURL.escapeHtml4());
        pageRef.setRedirect(true);
        return pageRef;
    }
    
    /**
    * This method used to create relation between the PGV and the PGQ.
    *
    * @param    id           queueViewID 
    * @return                void
    */ 
    @future
    public static void createGroupMembers(id queueViewID)
    {
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Queue_View__c oQV = new Queue_View__c();
        if(queueViewID != null)
        {
            oQV = QueueViewItemCreation_D_HUM.getPGVQV(queueViewID);
            oWQS = QueueViewItemCreation_D_HUM.getPGQWQ(oQV.Work_Queue_Name__c);
        }
        GroupMember newGroupMember;
        If(Schema.sObjectType.GroupMember.fields.GroupId.isCreateable() && Schema.sobjectType.GroupMember.fields.UserOrGroupId.isCreateable()){
            newGroupMember = new GroupMember(GroupId = oWQS.Public_Group_Queue_Id__c, UserOrGroupId = oQV.Public_Group_View_Id__c);
            QueueViewItemCreation_D_HUM.insertGroupMember(newGroupMember);  
        }
    }
    
    /**
    * Returns pageReference to populate the PGV name based on QV name
    * <p>
    * Method to populate the PGV name.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              PageReference
    */ 
    public PageReference populatePGV(QueueViewItemCreation_C_HUM cls)
    {
        try
        {
            if(cls.sWQSId != null)
            {
                cls.bShowTable = true;
                Queue_View__c sQV = new Queue_View__c();            
                if(cls.sQVType != null) sQV = dLayer.queueInfo(cls.sQVType);                
                cls.lstGroups = new List<Group>();              
                if(!String.isBlank(sQV.Public_Group_View_Id__c)) cls.lstGroups = dlayer.groupName(sQV.Public_Group_View_Id__c);                                 
                if(!cls.lstGroups.isEmpty()) cls.publicGroupRecordName = cls.lstGroups[0].Name;                                 
                filterQVI(cls);    
                return null;
            }
            else
            {
                String tempStr = '';
                cls.workQueueSetup = [Select Name, Service_Center__c, Department__c from Work_Queue_Setup__c where id =: cls.queueView.Work_Queue_Name__c];
                tempStr = cls.workQueueSetup.Name + ' '+ cls.queueView.Name;
                // Public group name is trimmed 36 to concatenate it with PGV so that Public group developer name won't exceed 40 characters.
                if (tempStr.length() > 36)  
                {
                    cls.publicGroupRecordName = tempStr.substring(0,36) + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGV_HUM;
                }
                else 
                {
                    cls.publicGroupRecordName = tempStr + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGV_HUM;
                }
                return null;    
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'QueueViewItemCreation_S_HUM','populatePGV');
            return null;
        }
    }
    
     /**
    * Method to update the PGV name.
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    public void updatePGV(QueueViewItemCreation_C_HUM cls)
    {
        try
        {
            if(!String.isBlank(cls.publicGroupRecordName))
            {
                List<Group> lstQueryGrp = new List<Group>();        
                Queue_View__c sQV = new Queue_View__c();                
                if(cls.sQVType != null) sQV = dLayer.queueInfo(cls.sQVType);                    
                if(!String.isBlank(sQV.Public_Group_View_Id__c)) lstQueryGrp = dlayer.groupName(sQV.Public_Group_View_Id__c);                   
                if(cls.publicGroupRecordName.length() > 40) cls.publicGroupRecordName = cls.publicGroupRecordName.substring(0,40);                  
                Group grp = new Group(name = cls.publicGroupRecordName , developerName = lstQueryGrp[0].Name.replace(' ','_'), Id = lstQueryGrp[0].Id);
                dLayer.updateGroup(grp);
                dLayer.updateQV(new List<Queue_View__c>{sQV});
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'QueueViewItemCreation_S_HUM','updatePGV');
        }
    }
    
    /**
    * Method to create Queue View Item from Work Queue Setup
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    private void createQueueViewItemFromWorkQueueSetup(QueueViewItemCreation_C_HUM cls)
    {
       cls.sWQSId = (ID)ApexPages.currentpage().getParameters().get(GLOBAL_CONSTANT_HUM.QUEUEVIEW_QID_HUM);
       cls.bRenderQWI = true;
       cls.bShowTable = false;
       cls.bShowQueueView = false;
       cls.bIsEdit = false;
       if(cls.sWQSId != null)
       {
            cls.workQueueSetup = dLayer.getWorkQueue(cls.sWQSId);
            queueViews(cls);
       }
    }
    
    /**
    * Method to initialize variables for editing queue View
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    private void editQueueView(QueueViewItemCreation_C_HUM cls)
    {
        cls.sQVID = (ID) ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.ID_HUM);
        if(cls.sQVID != null)
        {
            cls.queueView = dLayer.queueInfo(cls.sQVId);
            cls.sQVType=(String)cls.queueView.id;
            cls.workQueueSetup.Name = cls.queueView.Work_Queue_Name__r.Name;                
            cls.bShowQueueView = true;
            cls.bIsEdit = true;
            cls.mapQV.put((String)cls.queueView.id,cls.queueView.Name);
            getQueueViews(cls);       
        }
    }   
    
     /**
    * Method to initialize variables for editing queue View
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    private void createQueueViewFromWorkQueueRelatedList(QueueViewItemCreation_C_HUM cls)
    {
        if((String.valueOf(ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.QUEUEVIEW_RETURL_HUM).escapeHtml4()).remove('/')).length() >= 15)
        {
            cls.workQueueSetup = dLayer.getWorkQueue((ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.QUEUEVIEW_RETURL_HUM)).remove('/'));
            cls.sQVCreationID =  (ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.QUEUEVIEW_RETURL_HUM)).remove('/');
            cls.queueView.Work_Queue_Name__c = cls.sQVCreationID; 
        }
        cls.bShowQueueView = true;
        cls.bIsEdit = false;  
    }       
    
    /**
    * Method to remove queue view items from the map associated with WQS
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
    private void removeQVIWQS(QueueViewItemCreation_C_HUM cls)
    {
       List<HUMQueueViewCriteriaWrapper> lstTemp = new List<HUMQueueViewCriteriaWrapper>();
       Map<Id, List<HUMQueueViewCriteriaWrapper>> removeMap = new Map<Id, List<HUMQueueViewCriteriaWrapper>>();
       cls.counter = 0;
       for(List<HUMQueueViewCriteriaWrapper> lstHumQVCW : cls.mapWrapper.values())
       {
            for(HUMQueueViewCriteriaWrapper humQVCW : lstHumQVCW)
            {
                if(humQVCW.iCounter != cls.iCurrentCounter)
                {
                    humQVCW.icounter = cls.counter;
                    if(removeMap.containskey(humQVCW.iQVId))
                    {
                        removeMap.get(humQVCW.iQVId).add(humQVCW);
                    }
                    else
                    {
                        removeMap.put(humQVCW.iQVId, new List<HUMQueueViewCriteriaWrapper>{humQVCW});
                    }
                    cls.counter++;
                }
                else if(humQVCW.objQViewItem != null && humQVCW.objQViewItem.id != null) 
                {
                    cls.lstDeleteQueueViewItem.add(humQVCW.objQViewItem);
                }
            }
        }
        removeMapQVIWQS(cls,removeMap,lstTemp);
    }
    
    /**
    * Method to remove queue view items from the map associated with WQS
    *
    * @param QueueViewItemCreation_C_HUM                   cls 
    * @param Map of Id,List<HUMQueueViewCriteriaWrapper>   removeMap
    * @param List<HUMQueueViewCriteriaWrapper>             lstTemp
    * @return                                              null
    */ 
    private void removeMapQVIWQS(QueueViewItemCreation_C_HUM cls,Map<Id, List<HUMQueueViewCriteriaWrapper>> removeMap,List<HUMQueueViewCriteriaWrapper> lstTemp)
    {
        if(removeMap.size() > 0)
        {
            cls.mapWrapper = removeMap;
            filterQVI(cls);
        }
        else
        {
            cls.bShowTable = false;
            cls.mapWrapper = new Map<Id, List<HUMQueueViewCriteriaWrapper>>();
            cls.lstQviewItemWrapper = new List<HUMQueueViewCriteriaWrapper>();
            cls.counter = 0;
            cls.iCurrentCounter = 0;
        }
    }
    
    /**
    * Method to remove queue view items from the map which are not assicoated with WQS
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
      private void removeQVIWithoutWQS(QueueViewItemCreation_C_HUM cls)
      {
            List<HUMQueueViewCriteriaWrapper> lstTempWrap = new List<HUMQueueViewCriteriaWrapper>();
            Set<HUMQueueViewCriteriaWrapper> setWrapper = new Set<HUMQueueViewCriteriaWrapper>();
            Map<Id, List<HUMQueueViewCriteriaWrapper>> removeMap = new Map<Id, List<HUMQueueViewCriteriaWrapper>>();
            lstTempWrap.addAll(cls.lstQViewItemWrapper);
            removeQVIWrapper(cls,removeMap,lstTempWrap);
      }
      
    /**
    * Method to remove queue view items from the map which are not assicoated with WQS
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
      private void removeQVIWrapper(QueueViewItemCreation_C_HUM cls,Map<Id, List<HUMQueueViewCriteriaWrapper>> removeMap,List<HUMQueueViewCriteriaWrapper> lstTempWrap)
      {
            List<HUMQueueViewCriteriaWrapper> lstTemp = new List<HUMQueueViewCriteriaWrapper>();
            cls.counter=0;
            for(HUMQueueViewCriteriaWrapper humQVCW : lstTempWrap)
            {
                if(humQVCW.iCounter != cls.iCurrentCounter)
                {
                    humQVCW.icounter = cls.counter;
                    lstTemp.add(humQVCW);
                    if(removeMap.containskey(cls.sQVType))
                    {
                        removeMap.get(cls.sQVType).add(humQVCW);
                    }
                    else
                    {
                        removeMap.put(cls.sQVType, new List<HUMQueueViewCriteriaWrapper>{humQVCW});
                    }
                    cls.counter++;
                }
                else if(humQVCW.objQViewItem!=null && humQVCW.objQViewItem.id!=null)
                {
                    cls.lstDeleteQueueViewItem.add(humQVCW.objQViewItem);
                }
            }
            if(lstTemp.size()>0)
            {
                cls.mapWrapper = removeMap;
                cls.lstQViewItemWrapper = lstTemp;
            }
            else
            {
                cls.lstQviewItemWrapper = new List<HUMQueueViewCriteriaWrapper>();
                cls.counter = 0;
                cls.iCurrentCounter = 0;
            }
      }
            
    /**
    * Method to upsert queue view
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
      private void upsertQV(QueueViewItemCreation_C_HUM cls)
      {
          if(cls.queueView.Work_Queue_Name__c != null && cls.queueView.Name != null)
          {
              if(cls.mapWrapper != null &&  cls.mapWrapper.values() != null)
              {
                  for(List<HUMQueueViewCriteriaWrapper> lstHumQVCW : cls.mapWrapper.values())
                  {
                      cls.lstQVCWrapperInsert.addAll(lstHumQVCW); 
                  }
              }
              getEncrytedValue(cls);
              if(cls.queueView.Id == NULL) cls.queueView.IsActive__c = False;               
              List<Queue_View__c> lstQV = new List<Queue_View__c>{cls.queueView};
              dLayer.upsertQV(lstQV);
              if(!cls.bIsEdit) createGroupMembers(cls.queueView.id);
          }
      }
            
   /**
    * Method to upsert/delete Queue View Item
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              void
    */ 
      private void deleteQueueViewItems(QueueViewItemCreation_C_HUM cls)
      {
            if(!cls.lstQWIRecords.isEmpty()) dLayer.upsertQVI(cls.lstQWIRecords);                                   
            if(cls.lstDeleteQueueViewItem != null && !cls.lstDeleteQueueViewItem.isEmpty()) dLayer.deleteQVI(cls.lstDeleteQueueViewItem);                           
      }
                 
     /**
    * Returns boolean to check the status of QV update 
    * <p>
    * Method to validate and create group
    *
    * @param QueueViewItemCreation_C_HUM   cls 
    * @return                              Boolean
    */ 
      private Boolean checkGroup(QueueViewItemCreation_C_HUM cls)
      {
            ID grpID = NULL;               
            Boolean bError = false;
            if(!cls.bIsEdit)
            {   
                if(!dLayer.checkGrp(cls.publicGroupRecordName))
                {
                    if(Schema.sObjectType.Group.fields.Name.isCreateable()){
                    	grp.Name = cls.publicGroupRecordName;  
                    }
                    if(Schema.sObjectType.Group.fields.type.isCreateable()){
                        grp.type = GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM;
                    }
                    if(schema.sobjecttype.Group.fields.DoesSendEmailToMembers.isCreateable()){
                        grp.DoesSendEmailToMembers = false ;
                    }
                    if(schema.sobjecttype.Group.fields.DoesIncludeBosses.isCreateable()){
                        grp.DoesIncludeBosses = false;
                    }
                    dLayer.insertGroup(grp);
                }
                else
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, Label.PGVERROR_WORKQUEUE_HUM));
                    bError = true;
                }
            }        
            return bError;
      }          
/**
     * Method Name : UpdateGroup
     * Discription : Updates the Group Object on Name, DeveloperName
     * Inputs      : sOldGroupName, sNewDeveloperName, sNewGroupName        
     * OutPut      : Boolean Values true or false based on successfull execution.                   
    */
    public static boolean UpdateGroup(Id sOldGroupName, String sNewGroupName){
        boolean bSuccess = true;
        try{
            List<Group> lstGroup = [SELECT Id, Name, DeveloperName FROM Group where Id =:sOldGroupName];
            if(!lstGroup.IsEmpty()){
                for( Group oGroup : lstGroup ){
                    oGroup.Name = sNewGroupName;
                    oGroup.DeveloperName = sNewGroupName.replace(' - ',' ').replace('-','_').replace(' & ',' ').replace('&','_').replace('/','_').replace('','').replace(' ','_').replace('__','_');
                    
                }
                update lstGroup;
            }
        }catch(exception e){
            HUMExceptionHelper.logErrors(e, 'QueueViewItemCreation_S_HUM', 'UpdateGroup');
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_ERROROCCURED_HUM, true);
            bSuccess = false;
        }
        return bSuccess;
    }
}