public class HUMTemplateSubmissionDataDTO
{
    @InvocableVariable(label='Submission Data' description='The Submission Data to be encrypted and saved' required=true)
    public List<Template_Submission_Data__c> SubmissionData;
    
    @InvocableVariable(label='Encryption Status' description='The status of the encryption process')
    public boolean Status = false;
    
    @InvocableVariable(label='Encryption Error' description='The error message if the status is false')
    public string ErrorMessage = '';
    
    
}