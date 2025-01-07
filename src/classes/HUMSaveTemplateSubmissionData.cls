/*****************************************************************************************************************************************************************************
Apex Class Name : HUMSaveTemplateSubmissionData
Version         : 1.0
Created On      : 06/11/2015
Function        : This class has methods to save the values passed.
Test Class      : HUMSaveTemplateSubmissionDataTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Jeremy                                            06/11/2015                  Original Version
* Kalyan Lanka                                      06/11/2015                  Original Version
* Praveen Kumar Parimi       27066                  10/12/2015                  Updated the isFieldEncrypted method's check
* Suresh Gottipati                                  04/20/2018                  Removed dpaas callout as part of platform migration and Renamed the class to 
	                                                                            HUmSaveTemplateSubmissionData (Old Name - HUMTemplateSubmissiondataencrypt).  
********************************************************************************************************************************************************************************/

public with sharing class HUMSaveTemplateSubmissionData
{
    
     /* 
      * Method Name   :    saveTemplateValues 
      * Description   :    This method is used for Saving field values. 
      * Return Type   :    Returns list of HUMTemplateSubmissionDataDTO 
      * Parameters    :    List of HUMTemplateSubmissionDataDTO field values  
      */ 
    
    @InvocableMethod(label='Template Submission Values' description='Template Submission values Saving. Only supports to save the Template_Submission_Data__c.Value__c field.')
    Public static List<HUMTemplateSubmissionDataDTO> saveTemplateValues(List<HUMTemplateSubmissionDataDTO> Data)
    {
        List<HUMTemplateSubmissionDataDTO> Results = new List<HUMTemplateSubmissionDataDTO>();      
     
        
        for(HUMTemplateSubmissionDataDTO d : Data)
        {
         
            try
            {
                SaveSubmissionData(d.SubmissionData);
                d.Status = true;
                
            }
            catch(Exception e)
            {   
                d.Status = false;
                d.ErrorMessage = e.getMessage();
            }
            
            Results.add(d);
        }
        return Results;        
        
    }
    
    /* 
      * Method Name   :    SaveSubmissionData 
      * Description   :    This method is used for saving the submitted field values. 
      * Return Type   :    Returns Boolean value True 
      * Parameters    :    List of HUMTemplateSubmissionDataDTO field values  
      */ 
    
    private static boolean SaveSubmissionData(List<Template_Submission_Data__c> SubmissionData)
    {
       
        List<Template_Submission_Data__c> ToUpdate = new List<Template_Submission_Data__c>();
        
        for(Template_Submission_Data__c d : SubmissionData)
        {
            ToUpdate.add(d);
        }        
        Update ToUpdate;        
        return true;        
    }

    
 
}