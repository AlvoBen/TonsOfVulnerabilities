/*****************************************************************************************************************************************************************************
Apex Class Name : HUMTemplateSubmissionDataDecrypt
Version         : 1.0
Created On      : 06/12/2015
Function        : This class has methods to pass the value to Decryption service.
Test Class      : HUMTemplateSubmissionDataEncryptTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Jeremy Thiede                                     06/12/2015                  Original Version
* Kalyan Lanka                                      06/12/2015                  Original Version
********************************************************************************************************************************************************************************/
public with sharing class HUMTemplateSubmissionDataDecrypt 
{
    private static string VALUE_FIELD = 'Value__c';
    
    /* 
      * Method Name   :    decryptValues 
      * Description   :    This method is used for decrypting field values. 
      * Return Type   :    Returns list of HUMTemplateSubmissionDataDTO 
      * Parameters    :    List of HUMTemplateSubmissionDataDTO field values  
      */ 
    @InvocableMethod(label='Decrypt Template Submission Values' description='Decrypts Template Submission values and saves them. Only supports the Decryption of the Template_Submission_Data__c.Value__c field.')
    public static List<HUMTemplateSubmissionDataDTO> decryptValues(List<HUMTemplateSubmissionDataDTO> Data)
    {
        List<String> Values = new List<String>();
        List<HUMTemplateSubmissionDataDTO> ToReturn = new List<HUMTemplateSubmissionDataDTO>();
        
        for (Integer i = 0; i < Data.size(); i++) 
        {
            for (Integer x = 0; x < Data[i].SubmissionData.size(); x++) 
            {
                string s = string.isNotEmpty(Data[i].SubmissionData[x].Value__c) ? String.escapeSingleQuotes(Data[i].SubmissionData[x].Value__c) : '';
                Values.add(s);
            }
            
            if(!Test.isRunningTest()){
               ToReturn.add(LoadSubmissionData(Data[i]));
            } else {
                ToReturn.add(LoadSubmissionData(Data[i]));
            }
        }
        return ToReturn;
    }
    
    /* 
     * Method Name   :    LoadSubmissionData 
     * Description   :    This method is used for creating a Map of the results from decryption. 
     * Return Type   :    Returns HUMTemplateSubmissionDataDTO 
     * Parameters    :    DpaaSSoapWebService.DetokenizationResult[] and List of HUMTemplateSubmissionDataDTO field values  
   */ 
    private static HUMTemplateSubmissionDataDTO LoadSubmissionData(HUMTemplateSubmissionDataDTO Data)
    {
        HUMTemplateSubmissionDataDTO dto = new HUMTemplateSubmissionDataDTO();
        dto.SubmissionData = new List<Template_Submission_Data__c>();
        String Double_Backslash = HUMConstants__c.getInstance('Double_Backslash').StringValue__c;
        String Single_Backslash = HUMConstants__c.getInstance('Single_Backslash').StringValue__c;
        
        for(Template_Submission_Data__c d : Data.SubmissionData)
        {   
            //TODO: Cannot currently update value on referenced object, hence the reload. Tried to clone but cannot set reference ids and need to get the formula value
            Template_Submission_Data__c NewObject = GetInstanceOf(d);
            
            if (String.isNotBlank(NewObject.Value__c)) 
            {
                NewObject.Value__c = NewObject.Value__c.unescapeEcmaScript();
            } 
            dto.SubmissionData.add(NewObject);            
        }
        return dto;
    }
    
    //TODO: OPEN Case with Salesforce as to why the internal salesforce error is received when trying to update the referenced value
    private static Template_Submission_Data__c GetInstanceOf(Template_Submission_Data__c OldObject)
    {
        return [SELECT ID, Name__c, Name, Value__c, Template_Field__c, Template_Submission__c FROM Template_Submission_Data__c WHERE ID =: OldObject.Id];
    }
}