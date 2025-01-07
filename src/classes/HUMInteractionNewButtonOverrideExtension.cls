/*******************************************************************************************************************************
Apex Class Name : HUMInteractionNewButtonOverrideExtension
Version         : 1.0
Created On      : 06/27/2014
Function        : This class is used to redirect to queue selection page, if user doesn't select any workqueue and tries
                  to create Interaction for Case.
Test Class      : HUMInteractionNewButtonOverrideExtenTest
                  
Modification Log: 
* Developer Name           Code Review                               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
    SuryaKumari Medicherla          17093                                    06/27/2014                 Original Version
    SuryaKumari Medicherla          17492                                    08/21/2014                 To Fix Defect 149575, removed hardcoding    
    SuryaKumari Medicherla          17717                                    09/04/2014                 Replaced Custom labels with custom settings to fix Defect :153752  
    SuryaKumari Medicherla          18038                                    09/18/2014                 Romved mutiple SQL queries for custom settings
    SuryaKumari Medicherla          18795                                   10/27/2014                 To avoid URL not foung error from interaction detail page.Defect: 158563
************************************************************************************************************************ */
   
public with sharing class HUMInteractionNewButtonOverrideExtension 
{
    private String sCaseId = '';
    private String sCaseNumber = '';
    private Map<String, HUMSalesforceFieldIds__c> mapSalesforceFieldIds = HUMSalesforceFieldIds__c.getAll();
    private String sInteractionId = '';
    private String sInteractionNum = '';
    private String sCurrentUserQueue = '';
    private String sKeyPrefix = '';
    private String sNextPage = '';
    private String sCaseSalesForceId = '';
    private String sInteractionSalesForceId = '';
    
    /*
    * Method Name : HUMInteractionNewButtonOverrideExtension
    * Description : Constructor for the class, Initialise variables, read page parameter Values
    * Return type : N/A
    * Paramater   : StandardController 
    */
    public HUMInteractionNewButtonOverrideExtension(ApexPages.StandardController controller) 
    {
    }
    
    /*
    * Method Name : reDirectPage
    * Description : This method is used to call respective method based on URL parameter values.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference reDirectPage() 
    {
        try
        {
            sKeyPrefix = Case_Interaction__c.sObjectType.getDescribe().getKeyPrefix();
            sCurrentUserQueue = [select Id,Current_Queue__c from User where id= :Userinfo.getUserId()].Current_Queue__c;
            //To Fix Defect 153752, replaced Custom labels with Custom settings
            sCaseSalesForceId = mapSalesforceFieldIds.get(System.label.HUMsmallId).Case_in_Case_Interaction__c; 
            sInteractionSalesForceId = mapSalesforceFieldIds.get(System.label.HUMsmallId).Interaction_in_Case_Interaction__c;
            
            sCaseId = ApexPages.Currentpage().getParameters().get(HUMConstants.CF_VALUE + sCaseSalesForceId + HUMConstants.LKID_VALUE );     
            sCaseNumber = ApexPages.Currentpage().getParameters().get(HUMConstants.CF_VALUE + sCaseSalesForceId );            
            sInteractionId = ApexPages.Currentpage().getParameters().get(HUMConstants.CF_VALUE + sInteractionSalesForceId + HUMConstants.LKID_VALUE );     
            sInteractionNUM = ApexPages.Currentpage().getParameters().get(HUMConstants.CF_VALUE + sInteractionSalesForceId );
                
            if(!String.isEmpty(sCaseId))
            {
                 return reDirectPageForCase();   
            }
            else if(String.isNotEmpty(sInteractionId))
            {
                return reDirectPageForInteraction();
            }
            return null;
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMInteractionNewButtonOverrideExtension', 'reDirectPage');
            return null;
        }
    }
    /*
    * Method Name : reDirectPageForCase
    * Description : This method is used to set page parameters for nextpage based on user workqueue value and redirects to
                     next page when user clicks on New Case Interaction on Cases.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference reDirectPageForCase() 
    {
        //To Fix Defect 149575, removed hardcoding
        sNextPage = '/' + sKeyPrefix +'/e?'+ HUMConstants.CF_VALUE + sCaseSalesForceId  + '='  + sCaseNumber + '&' + HUMConstants.CF_VALUE + 
                           sCaseSalesForceId + HUMConstants.LKID_VALUE +'=' + sCaseId + HUMConstants.SAVEURL  + sCaseId + System.label.HUMNoOverrideEqualsOne;            
                           
        if(String.isEmpty(sCurrentUserQueue))
        {
            PageReference opageRef = new PageReference(System.label.HUMApexinURL + System.label.HUMQueueSelectionPage);
            
            opageRef.getParameters().put(System.label.HUMNextPage, sNextPage);
            opageRef.getParameters().put(System.label.HUMObjectType,  HUMConstants.OBJECT_CASE);             
            opageRef.getParameters().put(System.label.HUMActionType, System.label.HUMQueueYesForInteraction);
            opageRef.getParameters().put(System.label.HUMobjectNumber, sCaseNumber);  
            opageRef.getParameters().put(System.label.HUMObjectId, sCaseId );                                                                                                                                                                                                                                                                               
            return opageRef;
        }
        else 
        {
            return new PageReference(URL.getSalesforceBaseUrl().toExternalForm() +sNextPage);
        }
    }
    
    /*
    * Method Name : reDirectPageForInteraction
    * Description : This method is used to set page parameters for nextpage based on user workqueue value and redirects to
                     next page when user clicks on New Case Interaction on Interaction detail page.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference reDirectPageForInteraction() 
    {
        sNextPage = '/' + sKeyPrefix +'/e?'+ HUMConstants.CF_VALUE + sInteractionSalesForceId + '='  + sInteractionNUM + '&' + HUMConstants.CF_VALUE + 
                                   sInteractionSalesForceId + HUMConstants.LKID_VALUE +'=' + sInteractionId + HUMConstants.SAVEURL  + sInteractionId + System.label.HUMNoOverrideEqualsOne;            
                                   
        if(String.isEmpty(sCurrentUserQueue))
        {
            PageReference opageRef = new PageReference(System.label.HUMApexinURL + System.label.HUMQueueSelectionPage);
            
            opageRef.getParameters().put(System.label.HUMNextPage, sNextPage);
            opageRef.getParameters().put(System.label.HUMObjectType,  System.label.HUMInteraction);             
            opageRef.getParameters().put(System.label.HUMActionType, System.label.HUMQueueYesForInteraction);
            opageRef.getParameters().put(System.label.HUMobjectNumber, sInteractionNUM);  
            opageRef.getParameters().put(System.label.HUMObjectId, sInteractionId );                                                                                                                                                                                                                                                                               
            return opageRef;
        }
        else 
        {
            return new PageReference(URL.getSalesforceBaseUrl().toExternalForm() +sNextPage);
        }
    }
}