/*
  Apex Class Name  : OmniRoutingConfigurationsEdit_D_HUM
  Version          : 1.0 
  Created Date     : Nov 20 2017
  Function         : Data Layer class for the OmniRoutingConfigurationsEdit_VF_HUM page
  Test Class       : 
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T                                      11/20/2017                      Original Version
* 1.1                       Syed Mubarak T                171237                01/18/2018                      Defect 338122 fix
* 1.2                       Sumit Pharande                199560                03/23/2018                      REQ - 357273 Changes push time out to 300
* 1.3						Syed Mubarak T										03/01/2019						Added New capacity fields for Routing Configuration as part of Omni Configuration Item changes
* 1.4						Jasmeen Shangari                                    02/22/2021                      CheckMarx fix -update class with sharing																				 
* 1.5 	                   Mithra Bharadwaj                                                                     US#1522825 SonarQube Changes
* 1.6                       Pooja Kumbhar										05/10/2021						FLS Create and Update Checkmarx fix
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

public with sharing class OmniRoutingConfigurationsEdit_D_HUM
{
    public Omni_Routing_Configuration__c omniRoutingConfig {get;set;}   
    public HUM_Webservice_Callout__c qRtgConfigCreate = HUM_Webservice_Callout__c.getValues('QueueRoutingConfigService_Create');
    public HUM_Webservice_Callout__c qRtgConfigUpdate = HUM_Webservice_Callout__c.getValues('QueueRoutingConfigService_Update');
    public HUM_Webservice_Callout__c qRtgConfigDelete = HUM_Webservice_Callout__c.getValues('QueueRoutingConfigService_Delete');
    
     /*
    * This method Is to validate push time out
    * @param Id
    * @return Omni_Routing_Configuration__c
    */
     public Omni_Routing_Configuration__c loadData(Id omniRoutingId)
    {
        if(omniRoutingId != null)
        {
            omniRoutingConfig = [SELECT Name,Is_Active__c, Routing_Priority__c,Push_Time_Out__c,QueueRoutingConfig_Id__c,Configuration_Item_Name__c,Unit_of_Capacity__c,Percentage_of_Capacity__c FROM Omni_Routing_Configuration__c WHERE Id =: omniRoutingId LIMIT 1 ];            
        }
        return omniRoutingConfig;
    }
    
     /*
    * This method Is to create routing Configuration on Omni Channel
    * @param Omni_Routing_Configuration__c
    * @return Boolean
    */
    public boolean createQueueRoutingConfig(Omni_Routing_Configuration__c omniQueueRoutingConfigData)
    {
        boolean Success = false;
        try
        {
            HttpRequest request = new HttpRequest();
            request.setMethod(qRtgConfigCreate.Request_Type__c);
            request.setHeader(Label.HUMServiceTesterController_content_type,qRtgConfigCreate.Content_Type__c);
            request.setTimeout(Integer.valueOf(qRtgConfigCreate.Timeout__c));
            request.setEndpoint(qRtgConfigCreate.End_Point_URL__c);
            if (!Test.isRunningTest()) request.setClientCertificateName(qRtgConfigCreate.Certificate_Name__c);
            list<Omni_Routing_Configuration__c> lstORC = new list<Omni_Routing_Configuration__c>();
            QueueRoutingConfigCreateRequest_DTO_HUM.RecordDataList createRecord = new QueueRoutingConfigCreateRequest_DTO_HUM.RecordDataList();
            createRecord.Type = HUMConstants.HUMQueueRoutingConfig;
            String formattedDeveloperName = omniQueueRoutingConfigData.Name.replace(' ','_');
            createRecord.DeveloperName = formattedDeveloperName;
            createRecord.CapacityWeight = omniQueueRoutingConfigData.Unit_of_Capacity__c;
            createRecord.CapacityPercentage = omniQueueRoutingConfigData.Percentage_of_Capacity__c;
            createRecord.MasterLabel = omniQueueRoutingConfigData.Name;
            createRecord.PushTimeout = String.valueOf(omniQueueRoutingConfigData.Push_Time_Out__c);
            createRecord.RoutingModel = HUMConstants.HUMQueueRoutingConfigRoutingModel;
            createRecord.RoutingPriority =  String.valueOf(omniQueueRoutingConfigData.Routing_Priority__c);
            createRecord.OverflowAssigneeId =  getOverflowAssigneeID(omniQueueRoutingConfigData);
            
            List<QueueRoutingConfigCreateRequest_DTO_HUM.RecordDataList> createRecordList = new List<QueueRoutingConfigCreateRequest_DTO_HUM.RecordDataList>();
            createRecordList.add(createRecord);
            
            QueueRoutingConfigCreateRequest_DTO_HUM.RecordData oCreateRecord = new QueueRoutingConfigCreateRequest_DTO_HUM.RecordData();
            oCreateRecord.Record = createRecordList;
            
            QueueRoutingConfigCreateRequest_DTO_HUM.CreateRequestDTO dtoCreateRecord = new QueueRoutingConfigCreateRequest_DTO_HUM.CreateRequestDTO();
            dtoCreateRecord.Records = oCreateRecord;
            
            QueueRoutingConfigCreateRequest_DTO_HUM requestBody = new QueueRoutingConfigCreateRequest_DTO_HUM();
            requestBody.CreateRequest = dtoCreateRecord;
            
            String sBody = JSON.serialize(requestBody);
            request.setBody(sBody);
            
            Http http = new Http();
            Httpresponse res = http.send(request);
            QueueRoutingConfigCreateResponse_DTO_HUM responseBody = null;
            Boolean successResponse = false;
            
            if(res.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM && res.getbody() != null && res.getbody().contains('CreateResponse'))
            {
                responseBody = (QueueRoutingConfigCreateResponse_DTO_HUM)System.JSON.deserialize(res.getbody(), QueueRoutingConfigCreateResponse_DTO_HUM.class);
                successResponse = true;
            }
            else
            {
                HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',res.getbody(),qRtgConfigCreate.End_Point_URL__c,'Connection Error');
                throw new HUMCustomException(h);
                successResponse = false;
            }
            if(successResponse)
            {
                QueueRoutingConfigCreateResponse_DTO_HUM.CreateResponseDTO dtoCreate = new QueueRoutingConfigCreateResponse_DTO_HUM.CreateResponseDTO();
                dtoCreate = responseBody.CreateResponse;
                
                QueueRoutingConfigCreateResponse_DTO_HUM.RecordData responseRecordList = new QueueRoutingConfigCreateResponse_DTO_HUM.RecordData();
                responseRecordList = dtoCreate.Records;
                
                for(QueueRoutingConfigCreateResponse_DTO_HUM.RecordDataList r :responseRecordList.Result)
                {
                 
                    if(r.success == 'true')
                    {
                        if(Schema.SObjectType.Omni_Routing_Configuration__c.fields.QueueRoutingConfig_Id__c.isCreateable() && Schema.SObjectType.Omni_Routing_Configuration__c.fields.QueueRoutingConfig_Id__c.isUpdateable()) omniQueueRoutingConfigData.QueueRoutingConfig_Id__c = r.Id;
                        lstORC.add(omniQueueRoutingConfigData);
                        Success = true;
                    }
                    else if(r.success == 'false')
                    {
                        QueueRoutingConfigCreateResponse_DTO_HUM.ErrorResponse e = new QueueRoutingConfigCreateResponse_DTO_HUM.ErrorResponse();
                        e = r.Errors;
                        HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',String.valueOf(e),qRtgConfigCreate.End_Point_URL__c,'DP Service Error');
                        throw new HUMCustomException(h);
                    }
                }
                if(lstORC!=null && !lstORC.isEmpty() && lstORC.size() > 0 )
                {
                    upsert lstORC;
                }
            }
        }
        catch(Exception e)
        {
                HUMExceptionHelper.logErrors(e,'OmniRoutingConfigurationsEdit_D_HUM','createQueueRoutingConfig');
                Success = false;
        }
    return Success;
    }
    
        
     /*
    * This method Is to update routing Configuration on Omni Channel
    * @param Omni_Routing_Configuration__c
    * @return Boolean
    */
    
    public boolean updateQueueRoutingConfig(Omni_Routing_Configuration__c omniQueueRoutingConfigData)
    {
        boolean Success = false;
        try
        {
            HttpRequest request = new HttpRequest();
            request.setMethod(qRtgConfigUpdate.Request_Type__c);
            request.setHeader(Label.HUMServiceTesterController_content_type,qRtgConfigUpdate.Content_Type__c);
            request.setTimeout(Integer.valueOf(qRtgConfigUpdate.Timeout__c));
            request.setEndpoint(qRtgConfigUpdate.End_Point_URL__c);
            if (!Test.isRunningTest()) request.setClientCertificateName(qRtgConfigUpdate.Certificate_Name__c);
            list<Omni_Routing_Configuration__c> lstomniRConfig = new list<Omni_Routing_Configuration__c>();
            QueueRoutingConfigUpdateRequest_DTO_HUM.RecordDataList updateRecord = new QueueRoutingConfigUpdateRequest_DTO_HUM.RecordDataList();
            updateRecord.Id = omniQueueRoutingConfigData.QueueRoutingConfig_Id__c;
            updateRecord.Type = HUMConstants.HUMQueueRoutingConfig;
            String formattedDeveloperName = omniQueueRoutingConfigData.Name.replace(' ','_');
            updateRecord.DeveloperName = formattedDeveloperName;
            updateRecord.CapacityWeight = omniQueueRoutingConfigData.Unit_of_Capacity__c;
            updateRecord.CapacityPercentage = omniQueueRoutingConfigData.Percentage_of_Capacity__c;
            updateRecord.MasterLabel = omniQueueRoutingConfigData.Name;
            updateRecord.PushTimeout = String.valueOf(omniQueueRoutingConfigData.Push_Time_Out__c);
            updateRecord.RoutingModel = HUMConstants.HUMQueueRoutingConfigRoutingModel;
            updateRecord.RoutingPriority =  String.valueOf(omniQueueRoutingConfigData.Routing_Priority__c);
            updateRecord.OverflowAssigneeId =  getOverflowAssigneeID(omniQueueRoutingConfigData);
            
            List<QueueRoutingConfigUpdateRequest_DTO_HUM.RecordDataList> updateRecordList = new List<QueueRoutingConfigUpdateRequest_DTO_HUM.RecordDataList>();
            updateRecordList.add(updateRecord);
            
            QueueRoutingConfigUpdateRequest_DTO_HUM.RecordData oUpdateRecord = new QueueRoutingConfigUpdateRequest_DTO_HUM.RecordData();
            oUpdateRecord.Record = updateRecordList;
            
            QueueRoutingConfigUpdateRequest_DTO_HUM.UpdateRequestDTO dtoUpdateRecord = new QueueRoutingConfigUpdateRequest_DTO_HUM.UpdateRequestDTO();
            dtoUpdateRecord.Records = oUpdateRecord;
            
            QueueRoutingConfigUpdateRequest_DTO_HUM requestBody = new QueueRoutingConfigUpdateRequest_DTO_HUM();
            requestBody.UpdateRequest = dtoUpdateRecord;
            
            String sBody = JSON.serialize(requestBody);
            request.setBody(sBody);
            
            Http http = new Http();
            Httpresponse res = http.send(request);
            QueueRoutingConfigUpdateResponse_DTO_HUM responseBody = null;
            Boolean successResponse = false;
            
            if(res.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM && res.getbody() != null && res.getbody().contains('UpdateResponse'))
            {
                responseBody = (QueueRoutingConfigUpdateResponse_DTO_HUM)System.JSON.deserialize(res.getbody(), QueueRoutingConfigUpdateResponse_DTO_HUM.class);
                successResponse = true;
            }
            else
            {
                    HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',res.getbody(),qRtgConfigUpdate.End_Point_URL__c,'Connection Error');
                    throw new HUMCustomException(h);
                    successResponse = false;
            }
            if(successResponse)
            {
                QueueRoutingConfigUpdateResponse_DTO_HUM.UpdateResponseDTO dtoUpdate = new QueueRoutingConfigUpdateResponse_DTO_HUM.UpdateResponseDTO();
                dtoUpdate = responseBody.UpdateResponse;
                
                QueueRoutingConfigUpdateResponse_DTO_HUM.RecordData responseRecordList = new QueueRoutingConfigUpdateResponse_DTO_HUM.RecordData();
                responseRecordList = dtoUpdate.Records;
                
                for(QueueRoutingConfigUpdateResponse_DTO_HUM.RecordDataList r :responseRecordList.Result)
                {
                 
                    if(r.success == 'true')
                    {
                         if(Schema.SObjectType.Omni_Routing_Configuration__c.fields.QueueRoutingConfig_Id__c.isUpdateable()) omniQueueRoutingConfigData.QueueRoutingConfig_Id__c = r.Id;
                        if(omniQueueRoutingConfigData.Unit_of_Capacity__c == '0')
                        {
                            omniQueueRoutingConfigData.Unit_of_Capacity__c = null;                            
                        }
                        else if(omniQueueRoutingConfigData.Percentage_of_Capacity__c == '0')
                        {
                            omniQueueRoutingConfigData.Percentage_of_Capacity__c = null;
                        }
                        lstomniRConfig.add(omniQueueRoutingConfigData);
                        Success = true;
                    }
                    else if(r.success == 'false')
                    {
                        QueueRoutingConfigUpdateResponse_DTO_HUM.ErrorResponse e = new QueueRoutingConfigUpdateResponse_DTO_HUM.ErrorResponse();
                        e = r.Errors;
                        HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',String.valueOf(e),qRtgConfigUpdate.End_Point_URL__c,'DP Service Error');
                        throw new HUMCustomException(h);
                    }
                }
                if(lstomniRConfig != NULL && !lstomniRConfig.isEmpty() && lstomniRConfig.size() > 0 )
                {
                    update lstomniRConfig;
                }    
            }
        }
        catch(Exception e)
        {
                HUMExceptionHelper.logErrors(e,'OmniRoutingConfigurationsEdit_D_HUM','updateQueueRoutingConfig');
                Success = false;
        }
        
        
        return Success;
    }
    
            
     /*
    * This method Is to remove routing Configuration on Omni Channel
    * @param Omni_Routing_Configuration__c
    * @return Boolean
    */
    
    public boolean deleteQueueRoutingConfig(Omni_Routing_Configuration__c omniQueueRoutingConfigData)
    {
        boolean Success = false;
        try
        {
            HttpRequest request = new HttpRequest();
            request.setMethod(qRtgConfigDelete.Request_Type__c);
            request.setHeader(Label.HUMServiceTesterController_content_type,qRtgConfigDelete.Content_Type__c);
            request.setTimeout(Integer.valueOf(qRtgConfigDelete.Timeout__c));
            request.setEndpoint(qRtgConfigDelete.End_Point_URL__c);
            if (!Test.isRunningTest()) request.setClientCertificateName(qRtgConfigDelete.Certificate_Name__c);
            list<Omni_Routing_Configuration__c> lstoRoutingConfig = new list<Omni_Routing_Configuration__c>();
            QueueRoutingConfigDeleteRequest_DTO_HUM.RecordDataList deleteRecord = new QueueRoutingConfigDeleteRequest_DTO_HUM.RecordDataList();
            deleteRecord.Id = omniQueueRoutingConfigData.QueueRoutingConfig_Id__c;
            
            List<QueueRoutingConfigDeleteRequest_DTO_HUM.RecordDataList> deleteRecordList = new List<QueueRoutingConfigDeleteRequest_DTO_HUM.RecordDataList>();
            deleteRecordList.add(deleteRecord);
            
            QueueRoutingConfigDeleteRequest_DTO_HUM.RecordData oDeleteRecord = new QueueRoutingConfigDeleteRequest_DTO_HUM.RecordData();
            oDeleteRecord.Record = deleteRecordList;
            
            QueueRoutingConfigDeleteRequest_DTO_HUM.DeleteRequestDTO dtoDeleteRecord = new QueueRoutingConfigDeleteRequest_DTO_HUM.DeleteRequestDTO();
            dtoDeleteRecord.Records = oDeleteRecord;
            
            QueueRoutingConfigDeleteRequest_DTO_HUM requestBody = new QueueRoutingConfigDeleteRequest_DTO_HUM();
            requestBody.DeleteRequest = dtoDeleteRecord;
            
            String sBody = JSON.serialize(requestBody);
            request.setBody(sBody);
            
            Http http = new Http();
            Httpresponse res = http.send(request);
            QueueRoutingConfigDeleteResponse_DTO_HUM responseBody = null;
            Boolean successResponse = false;
            
            if(res.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM && res.getbody() != null && res.getbody().contains('DeleteResponse'))
            {
                responseBody = (QueueRoutingConfigDeleteResponse_DTO_HUM)System.JSON.deserialize(res.getbody(), QueueRoutingConfigDeleteResponse_DTO_HUM.class);
                successResponse = true;
            }
            else
            {
                HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',res.getbody(),qRtgConfigDelete.End_Point_URL__c,'Connection Error');
                throw new HUMCustomException(h);
                successResponse = false;
            }
            
            if(successResponse)
            {
                QueueRoutingConfigDeleteResponse_DTO_HUM.DeleteResponseDTO dtoDelete = new QueueRoutingConfigDeleteResponse_DTO_HUM.DeleteResponseDTO();
                dtoDelete = responseBody.DeleteResponse;
                QueueRoutingConfigDeleteResponse_DTO_HUM.RecordData responseRecordList = new QueueRoutingConfigDeleteResponse_DTO_HUM.RecordData();
                responseRecordList = dtoDelete.Records;
                

                for(QueueRoutingConfigDeleteResponse_DTO_HUM.RecordDataList r :responseRecordList.Result)
                {
                  if(r.success == 'true')
                    {
                        if(Schema.SObjectType.Omni_Routing_Configuration__c.fields.QueueRoutingConfig_Id__c.isUpdateable()) omniQueueRoutingConfigData.QueueRoutingConfig_Id__c = null;
                        if(omniQueueRoutingConfigData.Unit_of_Capacity__c == '0')
                        {
                            omniQueueRoutingConfigData.Unit_of_Capacity__c = null;                            
                        }
                        else if(omniQueueRoutingConfigData.Percentage_of_Capacity__c == '0')
                        {
                            omniQueueRoutingConfigData.Percentage_of_Capacity__c = null;
                        }
                        lstoRoutingConfig.add(omniQueueRoutingConfigData);
                        Success = true;
                    }
                    else if(r.success == 'false')
                    {
                        QueueRoutingConfigDeleteResponse_DTO_HUM.ErrorResponse e = new QueueRoutingConfigDeleteResponse_DTO_HUM.ErrorResponse();
                        e = r.Errors;
                        HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',String.valueOf(e),qRtgConfigDelete.End_Point_URL__c,'DP Service Error');
                        throw new HUMCustomException(h);
                    }
                }
                if(lstoRoutingConfig != NULL && !lstoRoutingConfig.isEmpty() && lstoRoutingConfig.size() > 0 )
                {
                    update lstoRoutingConfig;
                }
            }
        }
         catch(Exception e)
        {
                HUMExceptionHelper.logErrors(e,'OmniRoutingConfigurationsEdit_D_HUM','deleteQueueRoutingConfig');
                Success = false;
        }
        
        
        return Success;
    }
    
            
     /*
    * This method Is to create routing configuration only on Omni_Routing_Configuration__c object
    * @param Omni_Routing_Configuration__c
    * @return boolean
    */
    
    public boolean createLocalRoutingConfig(Omni_Routing_Configuration__c omniQueueRoutingConfigData)
    {
        boolean Success = false;
        upsert omniQueueRoutingConfigData;
        success = true;
        return success;
    }
    
    /*
    * This method Is to return Overflow Assignee Queue Id
    * @param Omni_Routing_Configuration__c
    * @return boolean
    */
     public string getOverflowAssigneeID(Omni_Routing_Configuration__c omniQueueRoutingConfigData)
    {
       Omni_Configuration_Item__c oc = Omni_Configuration_Item__c.getValues(omniQueueRoutingConfigData.Configuration_Item_Name__c); 
       return  oc.Overflow_Assignee_Queue_Id__c;
    }
}