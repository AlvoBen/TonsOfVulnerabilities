/*****************************************************************************************
Apex Class Name :  GuidedProcessDetail_D_HUM
Version: 1.0
Function           : DataLayer Class for the Guided Process Detail Page
Created Date       :  05/20/2016
Test Class         : GuidedProcessDetail_T_HUM 

Modification Log: 
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
* Praveen Kumar Parimi             27942               05/20/2016                 Original Version
* Vijaya Koriprolu                                     01/25/2019                 Changed query to get the submission Id specific to Guided Process only
* Jasmeen Shangari                                     04/09/2020                 942030 - Added 'Send Print Item: Member' process in filter criteria  
*********************************************************************************************************************************/
public with sharing Class GuidedProcessDetail_D_HUM
{
     /*
     * Method Name   :  queryDetailValue
     * Description   :  This method fetches the details value
     * Return Type   :  String
     * Parameters    :  String
     */
    public static String queryDetailValue(String sCaseID)
    {
        try
        {
            List<Template_Submission__c> lstTempSubmission = [Select ID from Template_Submission__C where Submission_Owner__r.Object_Owner_ID__c =: sCaseId and template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member')];
            List<Template_Submission_Data__c> lstTempSubmissionData = [select ID,Value__c from Template_Submission_Data__c where Template_Submission__c =: lstTempSubmission[0].id ];
            String sDetailValue = lstTempSubmissionData[0].Value__c;
            return sDetailValue;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'GuidedProcessDetail_D_HUM', 'getDetailValue');
            return NULL;
        }
    }
    
      /*
     * Method Name   :  queryPolicyMemberName
     * Description   :  This method fetches the policy member name value
     * Return Type   :  String
     * Parameters    :  String
     */
    public static String queryPolicyMemberName(String sCaseID)
    {
        try
        {
            List<Case> LstPolMember = [Select ID,Policy_Member__c,Account.Name from Case where ID=:sCaseId];
            String sPolNameValue =  LstPolMember[0].Account.Name;
            return sPolNameValue;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'GuidedProcessDetail_D_HUM', 'getPolicyMemberName');
            return NULL;
        }
    }
    
}