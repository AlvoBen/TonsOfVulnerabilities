/*******************************************************************************************************************************
Apex Class Name : CreditableCoverage_S_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Service Class for CreditableCoverage_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sumit Kumar                                        02/19/2018                  Original Version
* Mohammed Noor               357636                 05/28/2019                  REQ - 389570 - Send data elements to CICMS upon Finish of Creditable Coverage Attestation process
* Mohammed Noor               360203                 07/08/2019                  REQ - 389570 - Fix for Defect#427240.0001
* Mohammed Noor                                      09/13/2019                  REQ - 409637 - Fix CA Ticket 8513506
* Jayesh Solanki                                     05/14/2020                  User Story 2271553: T1PRJ0001827 - MF 1 - Quality - Add "Care Manager" as a new Interacting with type value (CRM)
*********************************************************************************************************************************/
public with sharing class CreditableCoverage_S_HUM
{
    /**
    * Method to update calssifiation,intent,type and subtype for CreditableCoverage  
    * Returns responses to flow  
    * @param List<List<string>> this contain two list of values 
    *          1) Case Id as string
    *          2) true or false as string; this indicates to update case Guided Process flag
    * @return List<string>
    */
    @InvocableMethod(label='updateCaseClassificationAndIntent' description='Update Case classificaiton and Intent.')
    public static List<string> updateCaseClassificationAndIntent(List<List<String>> lstcaseId) 
    {
        List<CTCI_Junction__c> lstCTCIObj = new List<CTCI_Junction__c>();
        List<string> lstStr = new List<string>();		
        
        //Check the input param is empty 
        if(!lstcaseId.isempty())
        {

           List<case> lstcaseObj = [SELECT ID,Subtype__c,Guided_Process_Flag__c,Account.RecordTypeId,Type,Status, recordTypeId, Interacting_With_Type__c,Interacting_With__r.RecordType.DeveloperName,CTCI_List__c FROM Case where id = :lstcaseId[0][0]];

           if(lstcaseId[0][1]=='false')
           {
               lstCTCIObj = [SELECT Id FROM CTCI_Junction__c WHERE Classification_Type__r.Name = :GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO AND Classification__r.Name = :GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_COMMUNICATION AND Intent__r.Name = :GLOBAL_CONSTANT_HUM.CASE_INTENT_ELIGIBILITY];
           }
           else
           {
              lstCTCIObj = [SELECT Id FROM CTCI_Junction__c WHERE Classification_Type__r.Name = :GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO AND Classification__r.Name = :GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_ELIGIBILITY AND Intent__r.Name = :GLOBAL_CONSTANT_HUM.CASE_INTENT_LATEENROLLMENTPENALTY];
           }
           
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
                                                                                      
           if(!lstcaseObj.isEmpty() && !lstCTCIObj.isEmpty())
           {               
			   lstcaseObj[0].CTCI_List__c = lstCTCIObj[0].Id;
               lstcaseObj[0].Interacting_With_Type__c = mapInteractingWithRecordTypeWithCasePickList.get(lstcaseObj[0].Interacting_With__r.RecordType.DeveloperName);
               if(lstcaseId[0][1]=='true')
               {                   
				   lstcaseObj[0].Guided_Process_Flag__c = true;                   
                   lstcaseObj[0].Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;                    
                   String  accRecTypeName = (NULL != lstcaseObj[0].Account.RecordTypeId) ? Schema.SObjectType.Account.getRecordTypeInfosById().get(lstcaseObj[0].Account.RecordTypeId).getName() : NULL;
                   lstcaseObj[0].Topic__c = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE;
				   lstcaseObj[0].Type = GLOBAL_CONSTANT_HUM.EINSTEIN_BOT_CASE_TYPE;
				   lstcaseObj[0].subtype__c = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE;				   
                   lstcaseObj[0].RecordTypeId = HUMUtilityHelper.getCaseRecordType(accRecTypeName,lstcaseObj[0].Type , lstcaseObj[0].SubType__c,lstcaseObj[0].recordTypeId);				    				  

               }
               update lstcaseObj;
			   
			   if(lstcaseId[0][1]=='true')
               {   
                   /* Start logic to call Track Service and CI/CMS Service */
			       CaseMilestoneTrackService_C_HUM.CaseMilestoneParams trackServiceParams = new CaseMilestoneTrackService_C_HUM.CaseMilestoneParams();               
           	       trackServiceParams.CaseId = lstcaseObj[0].Id;
           	       trackServiceParams.previousCaseSubType = null;
           	       /*As the Case update always happens within CRM after the Attestation form has been attached by CSR,
	           	   set the isNewCase parameter as false to select the Milestones with Origin as InboundCall to align with existing functionality
	           	   irrespective of the origin of case creation */
	           	   trackServiceParams.isNewCase = false;
           	       CaseMilestoneTrackService_C_HUM.invokeTrackService(new list<CaseMilestoneTrackService_C_HUM.CaseMilestoneParams>{trackServiceParams});
			       /* End logic to call Track Service and CI/CMS Service */
                }

               lstStr.add('true');
           }  
           else
           {
               lstStr.add('false');
           }         
        }
        else
        {
            lstStr.add('false');
        }
        
        return lstStr;
    }

    /**
    * Returns responses of Rest API Call    * <p>
    * Method to get the CreditableCoverage Response
    * @return                 Object
    */
    public static CreditableCoverageTemplateResponse_H_HUM getCreditCoverageResponse(CreditableCoverageTemplateRequest_H_HUM objCreditableCoverageTemplateRequestCls)
    {
        try
        {
            HUM_Webservice_Callout__c ccServiceRequest = HUM_Webservice_Callout__c.getValues('CCTemplate');
            CreditableCoverageResponse_DTO_HUM ccResponseHelper = new CreditableCoverageResponse_DTO_HUM();
            CCResponseFaultCode_DTO_HUM ccResponseFaultDTO = new CCResponseFaultCode_DTO_HUM();
            CreditableCoverageTemplateResponse_H_HUM oResponse = new CreditableCoverageTemplateResponse_H_HUM();
            String sBody = json.serialize(CreditableCoverage_D_HUM.RequestStructureGetResponse(objCreditableCoverageTemplateRequestCls));
            
            if(ccServiceRequest.Active__c && sBody!=null)
             {                    
                    HttpResponse response = HUMCalloutUtilityHelper.callWebservice(ccServiceRequest.End_Point_URL__c, ccServiceRequest.Certificate_Name__c, sBody, ccServiceRequest.Request_Type__c, null, ccServiceRequest.Content_Type__c);                    
                    String strResponse=response.getBody();
                   if(response.getBody() != null)
                   {
                        if(string.isNotBlank(strResponse) && strResponse.contains(GLOBAL_CONSTANT_HUM.FAULTCODE_ALERTS_HUM))
                        {                 
                          strResponse = strResponse.replaceAll('-','_');
                          ccResponseFaultDTO = (CCResponseFaultCode_DTO_HUM)System.JSON.deserialize(strResponse, CCResponseFaultCode_DTO_HUM.class);                  
                          oResponse = CreditableCoverage_D_HUM.buildResponseMessageFaultCode(ccResponseFaultDTO);                          
                        }
                        if(string.isNotBlank(strResponse) && (response.getStatusCode() == 200 || response.getStatusCode() == 202) && response.getBody().contains('SaveCCTemplateResponse'))
                        {
                          ccResponseHelper = (CreditableCoverageResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), CreditableCoverageResponse_DTO_HUM.class);                  
                          oResponse = CreditableCoverage_D_HUM.buildResponseMessage(ccResponseHelper);                          
                        }
                  }
          }            
          return oResponse;
        }
        catch (Exception ex)
        {
           HUMExceptionHelper.logErrors(ex, 'CreditableCoverage_S_HUM', 'processCreditableCoverageResponse');           
           return null;
        }
    }     
}