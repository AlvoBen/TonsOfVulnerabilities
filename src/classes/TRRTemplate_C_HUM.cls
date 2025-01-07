/*****************************************************************************************
Apex class Name    : TRRTemplate_C_HUM

Version            : 1.0
Function           : Controller to VF Page:
                     This class is used to display TRR Template Details.
Created Date       : 15 Mar 2019
Test Class         : TRRTemplate_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Moshitha Gunasekaran        348700                  03/15/2019         Original Version
 ******************************************************************************************************************************/
 
public with sharing class TRRTemplate_C_HUM {
	
	public map<string, string> MapTRRTemplateData {get; set;}
	public string ProcessNumber {get;set;} 
	public list<string> TemplateFields {get;set;}
	
	/*
    * Method Name : TRRTemplate_C_HUM
    * Description : Initializes variables and gathers data into query variables.
    * Return type : Void
    * Paramater   : NA
    */
	public TRRTemplate_C_HUM()
	{
		try
        {
			if (Apexpages.currentPage().getParameters().containsKey('ProcessNumber') && Apexpages.currentPage().getParameters().get('ProcessNumber') != null)
	        {
	        	ProcessNumber = Apexpages.currentPage().getParameters().get('ProcessNumber');
	            loadTRRTemplateView();
	        }
        }
        catch(Exception e)
        {
            //As DML is not allowed inside constructor,stop the DML operation by this boolean
            HUMExceptionHelper.bRestrictLog = true;
            //store the list of exceptions captured
            HUMExceptionHelper.logErrors(e,'TRRTemplate_C_HUM','TRRTemplate_C_HUM');
        }
	}
	
	/*
    * Method Name : loadTRRTemplateView
    * Description : Loads the TRR Template view.
    * Return type : Void
    * Paramater   : NA
    */
    public void loadTRRTemplateView()
    {
        List<Template_Submission_Data__c> lstTemplateData = [SELECT Id, Name, Template_Field__c, Template_Submission__c, 
                        Name__c, Value__c,Template_Field__r.Data_Type__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Name = :ProcessNumber AND 
                        Template_Submission__r.Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_NAME];
        
        //Retrieve TemplateFields from constant and display as label in VFPage so that same sequence 
        //can be displayed on detail page. And if any change in field name then only constant need to be changed  
        TemplateFields = GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_FIELDS; 
        MapTRRTemplateData = new map<string, string>();               
        if(lstTemplateData != null && !lstTemplateData.isEmpty())
        {
        	//Retrieve Template data of TRR temaplate to display on detail page
        	for(Template_Submission_Data__c tempData : lstTemplateData)
    		{
    			//Based on datatype format the string to display date and time in MM/DD/YYYY HH:MM:SS.nnnnnn format 
    			if(tempData.Template_Field__r.Data_Type__c.equals(GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_DATETIME))
    			{
    				string dateTimeValue = tempData.Value__c;
    				if(string.isNotBlank(tempData.Value__c) && (tempData.Value__c.length() > 15))
    				{
    					dateTimeValue = tempData.Value__c.substring(5,7) + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + tempData.Value__c.substring(8,10) + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + tempData.Value__c.substring(0,4) + ' ' + tempData.Value__c.substring(11,13) + GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_COLON + tempData.Value__c.substring(14,16) + GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_COLON + tempData.Value__c.substring(17,19);
    				}
    				else if(string.isNotBlank(tempData.Value__c))
    				{
    					dateTimeValue = tempData.Value__c.substring(0,2) + GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_COLON + tempData.Value__c.substring(3,5) + GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_COLON + tempData.Value__c.substring(6,tempData.Value__c.length());
    				}
    				MapTRRTemplateData.put(tempData.Name__c, dateTimeValue);
    			}
    			else if(tempData.Template_Field__r.Data_Type__c.equals(GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_DATE))
    			{
    				string dateValue = tempData.Value__c;
    				if(string.isNotBlank(tempData.Value__c) && tempData.Value__c.length() == 10)
    				{
    					dateValue = tempData.Value__c.substring(5,7) + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + tempData.Value__c.substring(8,10) + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + tempData.Value__c.substring(0,4);
    				}
    				MapTRRTemplateData.put(tempData.Name__c, dateValue);
    			}
    			else
    			{
    				MapTRRTemplateData.put(tempData.Name__c, tempData.Value__c);
    			}
    		}
    	}
    	//Retrieve Template fields of TRR template and add in map for fields those where missed in adding in Submission data
    	//Field Name can be added in Map as we are referencing only Ids in submission data if Fieldname gets changed then 
    	//by default it gets changed in submission data also 
		list<Template_Field__c> lstTemplateFields = [SELECT Name FROM Template_Field__c where Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_NAME];
		if(lstTemplateFields != null && !lstTemplateFields.isEmpty())
		{
			for(Template_Field__c tempField : lstTemplateFields)
			{
				if((!MapTRRTemplateData.containsKey(tempField.Name)) || 
				(MapTRRTemplateData.containsKey(tempField.Name) && string.isBlank(MapTRRTemplateData.get(tempField.Name))))
				{
					MapTRRTemplateData.put(tempField.Name, '');
				}
			}
		}
    }
    
}