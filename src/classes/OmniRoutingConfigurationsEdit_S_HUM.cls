/*
  Apex Class Name  : OmniRoutingConfigurationsEdit_S_HUM
  Version          : 1.0 
  Created Date     : Nov 11 2017
  Function         : service layer class for the OmniRoutingConfigurationsEdit_VF_HUM page
  Test Class       : 
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T                                      11/20/2017                      Original Version
* 1.1                       Syed Mubarak T                                      01/03/2019                      Added Validation for Capacity fields as part of Omni Configuration Item changes
* 1.2						Jasmeen Shangari                                    02/22/2021                      CheckMarx fix -update class with sharing																				 
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

public with sharing class OmniRoutingConfigurationsEdit_S_HUM
{
    public Omni_Routing_Configuration__c omniRoutingConfig {get;set;}
    public OmniRoutingConfigurationsEdit_D_HUM dQueueRoutingConfig = new OmniRoutingConfigurationsEdit_D_HUM();
    
     /*
    * This method Is to check validations & call save/update method on success
    * @param  Omni_Routing_Configuration__c
    * @return Boolean
    */
    
    public boolean saveQueueRoutingConfig(Omni_Routing_Configuration__c queueRoutingConfigData,String OldConfigItem)
    {
        Boolean saveOmniData = false;
        if(queueRoutingConfigData != null)
        {
            omniRoutingConfig = queueRoutingConfigData;
            
            if(validateRoutingConfigName(omniRoutingConfig) && validateRoutingPriority(omniRoutingConfig) && validatePushTimeOut(omniRoutingConfig) && validateCapacity(omniRoutingConfig) && validateConfigItem(omniRoutingConfig))
            {
                if(omniRoutingConfig.Id == null && omniRoutingConfig.Is_Active__c == true)
                {   
                    saveOmniData = dQueueRoutingConfig.createQueueRoutingConfig(omniRoutingConfig);
                }
                else if(omniRoutingConfig.Id != null && omniRoutingConfig.Is_Active__c == true && checkOmniQueues(omniRoutingConfig,OldConfigItem))
                {
                    if(omniRoutingConfig.QueueRoutingConfig_Id__c != null && omniRoutingConfig.QueueRoutingConfig_Id__c !='')
                    {
                        saveOmniData = dQueueRoutingConfig.updateQueueRoutingConfig(omniRoutingConfig);
                    }
                    else
                    {
                        saveOmniData = dQueueRoutingConfig.createQueueRoutingConfig(omniRoutingConfig);
                    }
                    
                }
                else if(omniRoutingConfig.Id != null && omniRoutingConfig.Is_Active__c == false)
                {
                    if(omniRoutingConfig.QueueRoutingConfig_Id__c != null && omniRoutingConfig.QueueRoutingConfig_Id__c != '')
                    {
                        if(checkOmniQueues(omniRoutingConfig,null))
                        {
                            saveOmniData = dQueueRoutingConfig.deleteQueueRoutingConfig(omniRoutingConfig);
                        }
                        else
                        {
                            saveOmniData = false;
                        }
                    }
                    else if(checkOmniQueues(omniRoutingConfig,OldConfigItem))
                    {
                        saveOmniData = dQueueRoutingConfig.createLocalRoutingConfig(omniRoutingConfig);
                    }
                    
                    
                }
                else if(omniRoutingConfig.Id == null && omniRoutingConfig.Is_Active__c == false)
                {
                    saveOmniData = dQueueRoutingConfig.createLocalRoutingConfig(omniRoutingConfig);
                }
                
            }
        }
        return saveOmniData;
    }
    
         /*
    * This method Is to validate routing configuration name
    * @param  Omni_Routing_Configuration__c
    * @return Boolean
    */
    
    public Boolean validateRoutingConfigName(Omni_Routing_Configuration__c queueRoutingConfigData)
    {
        Boolean validData = false;
        if(queueRoutingConfigData.Id == null)
        {
            if(queueRoutingConfigData.Name != null && queueRoutingConfigData.Name != '')
            {
            Omni_Routing_Configuration__c[] chechDuplicateName = [SELECT Name FROM Omni_Routing_Configuration__c WHERE Name =:queueRoutingConfigData.Name LIMIT 1]; 
                if(chechDuplicateName.size() > 0)
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Duplicate_RoutingConfig_Name_Error));
                    validData = false;
                    
                }
                else
                {
                    validData = true;
                }
            }
            else
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Routing_Config_Name_Error));
                validData = false;
            }
            
        }
        else
        {
            if(queueRoutingConfigData.Name != null && queueRoutingConfigData.Name != '')
            {
            Omni_Routing_Configuration__c[] chechDuplicateName = [SELECT Id,Name FROM Omni_Routing_Configuration__c WHERE Id =:queueRoutingConfigData.Id];  
                if(chechDuplicateName.size() > 0)
                {
                    if(chechDuplicateName[0].Name == queueRoutingConfigData.Name)
                    {
                    validData = true;
                    }
                    else
                    {
                    Omni_Routing_Configuration__c[] renameRoutingConfig = [SELECT Name FROM Omni_Routing_Configuration__c WHERE Name =:queueRoutingConfigData.Name LIMIT 1];
                        if(renameRoutingConfig.size() > 0)
                        {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Duplicate_RoutingConfig_Name_Error));
                        validData = false;
                        }
                        else
                        {
                        validData = true;   
                        }
                    }
                }
                else
                {
                validData = false;
                }
            }
            else
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Routing_Config_Name_Error));
                validData = false;
            }
        }
    return validData;
        
    }
    
         /*
    * This method Is to validate routing priority
    * @param  Omni_Routing_Configuration__c
    * @return Boolean
    */
    
    public Boolean validateRoutingPriority(Omni_Routing_Configuration__c queueRoutingConfigData)
    {
        Boolean validData = false;
        if(queueRoutingConfigData.Routing_Priority__c != null)
        {
            if(queueRoutingConfigData.Routing_Priority__c > 0)
            {
                validData = true;
            }
            else
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error,System.Label.Invalid_Routing_Priority_Error));
                validData = false;
            }
            
        }
        else
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Routing_Priority_Blank_Error));
            validData = false;
        }
        
    return validData;
        
    }
    
         /*
    * This method Is to validate push time out
    * @param  Omni_Routing_Configuration__c
    * @return Boolean
    */
    public Boolean validatePushTimeOut(Omni_Routing_Configuration__c queueRoutingConfigData)
    {
        Boolean validData = false;
        if(queueRoutingConfigData.Push_Time_Out__c != null)
        {
                if(queueRoutingConfigData.Push_Time_Out__c >=100 && queueRoutingConfigData.Push_Time_Out__c <= 500)
                {
                    validData = true;
                }
                else
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Push_Time_Out_Range_Error));
                    validData = false;
                }
            
        }
        else
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Push_Time_Out_Blank_Error));
            validData = false;
        }
        return validData;
    
    }
    
     /*
    * This method Is to validate Omni Queues Assignment
    * @param  Omni_Routing_Configuration__c
    * @return Boolean
    */
    public Boolean checkOmniQueues(Omni_Routing_Configuration__c queueRoutingConfigData,String OldConfigItem)
    {
    	boolean success;
    	if(!queueRoutingConfigData.Configuration_Item_Name__c.equalsIgnoreCase(OldConfigItem) || !queueRoutingConfigData.Is_Active__c)
    	{
    		OmniQueue__c[] omniQueuesAssigned = [SELECT Id FROM OmniQueue__c WHERE Routing_Configuration_Name__c =: queueRoutingConfigData.Id];
    	
	        if(omniQueuesAssigned.size() > 0 && OldConfigItem == null)
	        {
	            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Routing_Configuration_Disable_Error));
	            success = false;
	        }
	        else if(omniQueuesAssigned.size() > 0 && OldConfigItem != null && !queueRoutingConfigData.Configuration_Item_Name__c.equalsIgnoreCase(OldConfigItem))
	        {
	        	ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.label.Configuration_Item_Modify_Error));
	            success = false;
	        }
	        else
	        {
	            success = true;
	        }
    	}
    	else
    	{
    		success = true;
    	}
        return success;
        
    }
    
     /*
    * This method Is to validate Capacity of Work items
    * @param  Omni_Routing_Configuration__c
    * @return boolean
    */
    
    public boolean validateCapacity(Omni_Routing_Configuration__c queueRoutingConfigData)
    {
        boolean validData = false;
        if(String.isBlank(queueRoutingConfigData.Unit_of_Capacity__c) && String.isBlank(queueRoutingConfigData.Percentage_of_Capacity__c))
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Capacity_Fields_Blank_Error));
            validData = false;
        }
        else if(!String.isBlank(queueRoutingConfigData.Unit_of_Capacity__c) && !String.isBlank(queueRoutingConfigData.Percentage_of_Capacity__c))
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Capacity_Fields_Data_Error));
            validData = false;
        }
        else if(!String.isBlank(queueRoutingConfigData.Unit_of_Capacity__c) && String.isBlank(queueRoutingConfigData.Percentage_of_Capacity__c))
        {
            if(Decimal.valueOf(queueRoutingConfigData.Unit_of_Capacity__c) > 0 && Decimal.valueOf(queueRoutingConfigData.Unit_of_Capacity__c) <= 1)
            {
                validData = true;
                if(!String.isBlank(queueRoutingConfigData.QueueRoutingConfig_Id__c))
                {
                queueRoutingConfigData.Percentage_of_Capacity__c = '0';
                }
            }
            else
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Unit_Of_Capacity_Error));
                validData = false;
            }
        }
        else if(String.isBlank(queueRoutingConfigData.Unit_of_Capacity__c) && !String.isBlank(queueRoutingConfigData.Percentage_of_Capacity__c))
        {
            if(Integer.valueOf(queueRoutingConfigData.Percentage_of_Capacity__c) > 0 && Integer.valueOf(queueRoutingConfigData.Percentage_of_Capacity__c) <= 100)
            {
                validData = true;
                if(!String.isBlank(queueRoutingConfigData.QueueRoutingConfig_Id__c))
                {
                   queueRoutingConfigData.Unit_of_Capacity__c = '0'; 
                }
            }
            else
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Percentage_Of_Capacity_Error));
                validData = false;
            }
        }
        
        return validData;
    }
    
    /*
    * This method Is to validate Configuration_Item_Name__c
    * @param  Omni_Routing_Configuration__c
    * @return Boolean
    */
    
    public boolean validateConfigItem(Omni_Routing_Configuration__c queueRoutingConfigData)
    {
        Boolean validData = false;
        if(String.isNotBlank(queueRoutingConfigData.Configuration_Item_Name__c))
        {
            validData = true;
        }
        else
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, System.Label.Config_Item_Error));
            validData = false;
        }
        return validData;
    }
    
    

}