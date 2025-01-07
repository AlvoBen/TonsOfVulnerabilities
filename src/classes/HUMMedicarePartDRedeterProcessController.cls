/******************************************************************************************************************************
Apex Class Name  : HUMMedicarePartDRedeterProcessController 
Version          : 1.0 
Created Date     : May 19 2015
Function         : Controller to handle display medicare part D form.                   
Test Class       : HUMTemplateBaseHelperTest
Modification Log :
    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Santhi Mandava                         05/19/2015           Original version.
 *  Vijaya Koriprolu                       11/20/2018           Added param to allow multiple claims process
 *  Prathyu Tushar     324032              01/02/2019           REQ - 380419: Auto route cases for Part - D Redetermination
 *  Pavan Karnam       331169              01/24/2019           Defect Fix #399240.0001 of REQ - 380419: Case Auto Route 
 *  Prudhvi Pamarthi   354356              05/07/2019           REQ - 396328 Fix for CA Ticket - 8175553 
 *  Pavan Karnam                           08/20/2019           REQ - 406912 Regression Defect Fix 431444.0001
 *  Prudhvi Pamarthi                       03/06/2019           US#909076 Fix for CA Ticket - 8654667 - Cases with Part D Redetermination processes attached are changing to Critical priority even if No is selected 
 *  Jasmeen Shangari                       04/09/2020           942030 - Added 'Send Print Item: Member' process in filter criteria
 *  Mohammed Noor                          06/04/2020           Changes done for CR674 - US1034977 and US1035031
 * Mithra Bharadwaj                        09/21/2020           US#1393270 CRM Process - Initiating queue/views for transfers to MHK
 * Mithra Bharadwaj                        10/05/2020           Defect fix
 * Jasmeen Shangari                        04/21/2021           Checkmarx fixes Azure ID - 2163309
 * Jayesh Solanki                          05/14/2020           User Story 2271553: T1PRJ0001827 - MF 1 - Quality - Add "Care Manager" as a new Interacting with type value (CRM)
*********************************************************************************************************************************/
public with sharing class HUMMedicarePartDRedeterProcessController extends HUMTemplateBaseHelper 
{
    public Id sSubmissionOwnerId {get;set;}
    public Boolean bSubmissionPres {get;set;}
    String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);
    Template_Submission__c oTemplateSubmssion = new Template_Submission__c();
    
    public HUMMedicarePartDRedeterProcessController(ApexPages.StandardController controller) 
    {
        try
        {
			bSubmissionPres = true;
            oTemplateSubmssion = [SELECT ID,Version__c,Submission_Owner__r.Id FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId AND Object_Owner_Type__c = : sCase)];
            sSubmissionOwnerId = oTemplateSubmssion.Submission_Owner__r.Id;
        }
        catch(Exception ex) 
        {
            sSubmissionOwnerId = null;
            bSubmissionPres  = false; 
        }
    }
    
    /*
    * Method Name   :    getFinishPage
    * Description   :    sets the finish page and pass parameter 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public PageReference getFinishPage()
    {
        PageReference prRef = new PageReference('/apex/HUMCloseFlow');
        prRef.getParameters().put('PartD',sSelectedCaseId); 
      	prRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTDESCR_HUM);
     	prRef.setRedirect(true);
     	return prRef;
   }
   
   /*
    * Method Name   :    checkAutoRouteCriteria
    * Description   :    sets the finish page and pass parameter 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    @InvocableMethod(label='updateCaseAutoClassificationAndIntentPartD' description='Update Case classificaiton and Intent for Auto Route')
    public static void checkAutoRouteCriteria(List<Case> sSelectedCaseId)
    {

        List<String> lstWQNames = GLOBAL_CONSTANT_HUM.AutoRouteWQs;
        List<case> lstCases = [SELECT ID,Subtype__c, Owner_Queue__c, Work_Queue_View_Name__c, Classification_Type__c, Account.RecordTypeId,Type,Status,Interacting_With_Type__c,Interacting_With__r.RecordType.DeveloperName,CTCI_List__c FROM Case where id = :sSelectedCaseId[0].id];
        Map<String,String> mapInteractingWithRecordTypeWithCasePickList = new Map<String,String>{'Member' => 'Member',
                                                                                                        'Provider' => 'Provider',
                                                                                                        'Agent_Broker' => 'Agent',
                                                                                                        'Group' => 'Group',
                                                                                                        'Unknown_Agent_Broker' => 'Unknown-Agent',
                                                                                                        'Unknown_Member' => 'Unknown-Member',
                                                                                                        'Unknown_Provider' => 'Unknown-Provider',
                                                                                                        'Unknown_Group' => 'Unknown-Group',
                                                                                                        'Pharmacy' => 'Pharmacy',
                                                                                                        'Member Representative or Caregiver' => 'Member Representative or Caregiver',
                                                                                                        'Internal' => 'Internal',
            												'Care Manager' => 'Care Manager',
                                                                                                        'Prospective Customer' => 'Prospective Customer',
                                                                                                        'Government' => 'Government',
                                                                                                        'Other' => 'Other'};
                                                                                                        
        if(HUMUtilityHelper.isCRMFunctionalityON('1393270'))
            {
                List<CTCI_Junction__c> lstClassiObj = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=:GLOBAL_CONSTANT_HUM.sPartDCalssification and Intent__r.Name=:GLOBAL_CONSTANT_HUM.sPartDIntent and Classification_Type__r.Name =: lstCases[0].Classification_Type__c and IsActive__c=TRUE];
                 if(!lstCases.IsEmpty())
                    {              
                        if(lstClassiObj != NUll && !lstClassiObj.IsEmpty())               
                            {               
                                lstCases[0].CTCI_List__c = lstClassiObj[0].Id;                   
                            }               
                String interactingWithTypeVal = mapInteractingWithRecordTypeWithCasePickList.get(lstCases[0].Interacting_With__r.RecordType.DeveloperName);
                if(String.isNotBlank(interactingWithTypeVal))
                lstCases[0].Interacting_With_Type__c = interactingWithTypeVal;
                lstCases[0].Topic__c =GLOBAL_CONSTANT_HUM.sPartDTopic;
                update lstCases;
            }           
          }
        else
        {
        if(lstWQNames.Contains(lstCases[0].Owner_Queue__c) && lstCases[0].Work_Queue_View_Name__c == GLOBAL_CONSTANT_HUM.HUM_ALLOTHERVIEW)
        {           
            List<CTCI_Junction__c> lstClassiObj = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=:GLOBAL_CONSTANT_HUM.sPartDCalssification and Intent__r.Name=:GLOBAL_CONSTANT_HUM.sPartDIntent and Classification_Type__r.Name=: GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO and IsActive__c=TRUE];   
            if(!lstCases.IsEmpty() && !lstClassiObj.IsEmpty()){          
                if(lstCases[0].Classification_Type__c ==  GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO)
                {                   
                    lstCases[0].CTCI_List__c = lstClassiObj[0].Id;
                }
                String interactingWithTypeVal = mapInteractingWithRecordTypeWithCasePickList.get(lstCases[0].Interacting_With__r.RecordType.DeveloperName);
                if(String.isNotBlank(interactingWithTypeVal))
                lstCases[0].Interacting_With_Type__c = interactingWithTypeVal;
                lstCases[0].Topic__c = GLOBAL_CONSTANT_HUM.sPartDTopic;
                update lstCases;}}
                }
            } 
    
}