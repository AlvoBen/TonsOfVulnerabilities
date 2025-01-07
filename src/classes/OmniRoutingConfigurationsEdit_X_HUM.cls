/*
  Apex Class Name  : OmniRoutingConfigurationsEdit_X_HUM
  Version          : 1.0 
  Created Date     : Nov 20 2017
  Function         : Extension class for the OmniRoutingConfigurationsEdit_VF_HUM page
  Test Class       : 
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T                                      11/20/2017                  Original Version
* 1.2                       Sumit Pharande              199560                  03/23/2018              REQ - 357273 Changes push time out to 300
* 1.3                       Syed Mubarak T                                      12/12/2018                  Added Configuration item field for Omni Routing Configuration 
* 1.4						Mohammed Noor										02/22/2021					USER STORY 1990238 - Fix Check Marx FLS issues.
* 1.5                       Krishna Teja S                                      01/07/2022                  US_2990794 Fix Checkmarx issue
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

public with sharing class OmniRoutingConfigurationsEdit_X_HUM
{
    public Omni_Routing_Configuration__c omniRec = NULL; 
    public Omni_Routing_Configuration__c omniRoutingConfig {get;set;}
    public String messageName {get; set;}
    public boolean inputError{get;set;}
    
    public String BusinessAreaName {get;set;}
    public String sConfigItemName {get;set;}
    public Boolean viewLookup{get;set;}
    public String oldConfigurationItem{get;set;}
    
    public OmniRoutingConfigurationsEdit_S_HUM cRoutingConfig = New OmniRoutingConfigurationsEdit_S_HUM();
    public OmniRoutingConfigurationsEdit_D_HUM dRoutingConfig = New OmniRoutingConfigurationsEdit_D_HUM();
    
    /*
    * This is the constructor.
    * @param  NA
    * @return NA
    */
    public OmniRoutingConfigurationsEdit_X_HUM(ApexPages.StandardController controller)
    {
        omniRec = (Omni_Routing_Configuration__c)controller.getRecord();
        inputError = false;

        viewLookup = false;
        
        if(omniRec.Id != null)
        {
            omniRoutingConfig = dRoutingConfig.loadData(omniRec.Id);
            BusinessAreaName = Omni_Configuration_Item__c.getValues(omniRoutingConfig.Configuration_Item_Name__c).Business_Area__c;
            oldConfigurationItem = Omni_Configuration_Item__c.getValues(omniRoutingConfig.Configuration_Item_Name__c).Name;
            sConfigItemName = oldConfigurationItem;            
            /*Temporarily display the Business Area Name on the UI.  This is done as a workaround for removing the inputHidden field
			to fix the check marx FLS errors.*/ 
            omniRoutingConfig.Configuration_Item_Name__c = BusinessAreaName;	            
        }
        else
        {
           omniRoutingConfig  = new Omni_Routing_Configuration__c();
           if(Schema.SObjectType.Omni_Routing_Configuration__c.fields.Push_Time_Out__c.isCreateable() && Schema.SObjectType.Omni_Routing_Configuration__c.fields.Push_Time_Out__c.isUpdateable()) {
               omniRoutingConfig.Push_Time_Out__c = 300;
           }
           omniRoutingConfig.QueueRoutingConfig_Id__c = null;
        }
        
    }
    
    /*
    * This method helps is to save the record
    * @param  NA
    * @return NA
    */
    public PageReference save()
    {
        try
        {
            if(!inputError)
            {
                /*Reassign the value to Name field for saving in the Database. This is done as a workaround for removing the inputHidden field
                to fix the check marx FLS errors.*/
                omniRoutingConfig.Configuration_Item_Name__c = sConfigItemName; 
                boolean saveRoutingConfig = cRoutingConfig.saveQueueRoutingConfig(omniRoutingConfig,oldConfigurationItem);
                
                if(saveRoutingConfig)
                {
                    PageReference pageRef = new PageReference('/'+omniRoutingConfig.Id);
                    pageRef.setRedirect(true);
                    return pageRef;
                }
                else
                {
                 return null;
                }
        
            } else {return null;}
                
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'OmniRoutingConfigurationsEdit_X_HUM','save');
            return null;
        }
        
    }
    
    /*
    * This method helps is override of the standard cancel functionality
    * @param  NA
    * @return NA
    */
    public PageReference cancel()
    {               
        PageReference pageRef = new PageReference(ApexPages.currentPage().getParameters().get('retURL').escapeHtml4());
        pageRef.setRedirect(true);
        return pageRef;
    }
    
    /*
    * This method helps is used to display the error message
    * @param  NA
    * @return NA
    */
    public PageReference showErrorMessage()
    {
         ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, messageName));
         return null;
    }
    
    /*
    * This method helps in assigning the Config Item selected from the popup.
    * @param  NA
    * @return NA
    */
    public PageReference assignConfigItem()
    {    	
    	omniRoutingConfig.Configuration_Item_Name__c =  BusinessAreaName;
    	return null;    	    		
    }
    
	/*
    * This method helps in opening/closing the Modal window on for Config Item Lookup
    * @param  NA
    * @return NA
    */
    public void getOpenConfigLookup()
    {
        if (viewLookup)
        {
            viewLookup = false;
        }
        else
        {
            viewLookup = true;
        }
    }
    
     /*
    * This method returns the list of items for Config Item Lookup
    * @param  NA
    * @return List<Omni_Configuration_Item__c>
    */
    
    public List<Omni_Configuration_Item__c> getLstConfigItems()
    {
        if(!Omni_Configuration_Item__c.getall().values().isEmpty())
        {
            return Omni_Configuration_Item__c.getall().values();
        }
        else
        {
            return null;
        }
        
    }

}