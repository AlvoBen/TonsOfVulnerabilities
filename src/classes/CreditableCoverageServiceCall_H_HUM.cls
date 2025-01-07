/**************************************************************************************************************************
Apex class Name    : CreditableCoverageServiceCall_H_HUM 
Version            : 1.0
Function           : Service Helper for CreditableCoverage_S_HUM
Created On         : Feb 21 2018
Test Class         : CreditableCoverage_T_HUM

Modification Log:
*   Developer                   Code Review         Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Sumit Kumar                                      02/21/2018          Original Version
*  Bharath Rajappan                                 02/21/2018          Updated to Fix the Return code blank error
*  Satyanarayana chenna                             04/09/2021          PR00094226 - CR AEP readiness - Low Security Issues  
*   Vaishnavi Manda                                 05/12/2021          US2163327 - MF6 Checkmarx scan issue fix
*********************************************************************************************************************************/

public with sharing class CreditableCoverageServiceCall_H_HUM implements Queueable, Database.AllowsCallouts 
{
    private String sCaseID{get;Set;}    
    private static final String  RESPONSE_INFORMATION= 'CICMSIN';
    private static final String  RESPONSE_ERRORINFO= 'CICMSER';
     
    /**
    * Constructor of the class to Instantiate the variables.
    *
    * @param    lstTaskIds   List of Task Record Ids
    * @param    sCaseID      Case Record Id
    * @return   na
    */
    public CreditableCoverageServiceCall_H_HUM(String sCaseID)
    {       
        this.sCaseID = sCaseID;
    }
        
    /*
    * Method Name   :    invoke
    * Description   :    This method is used to call the CreditableCoverage_S_HUM service     
    * Return Type   :    Void
    * Parameters    :    String CaseId
    */
    
    public void  invoke(String strCaseId)
    {
      CreditableCoverageTemplateResponse_H_HUM oCCTemplateResponseHelper  = new CreditableCoverageTemplateResponse_H_HUM();
       try
       {
            List<Case> lstCase = [SELECT CaseNumber,Classification_Type__c,Subtype__c,Medicare_Track_ID__c,createdDate,Account.FirstName,Account.LastName,Guided_Process_Flag__c FROM Case where Id = :strCaseId limit 1];
            //call only when case process attached and its elligible to call CICMS
            if(lstCase[0].Classification_Type__c == GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM && isCreditableCoverageSubtypeElligible(lstCase[0].Id) && lstCase[0].Subtype__c == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE)
            {
                 //Method calls for various conditional assignments
                CreditableCoverageTemplateRequest_H_HUM obj = populateTemplateDetails(lstCase); 
                If(obj != null && lstCase[0].Guided_Process_Flag__c)                
                {
                    oCCTemplateResponseHelper  = CreditableCoverage_S_HUM.getCreditCoverageResponse(obj);                     
                    
					if(string.isBlank(oCCTemplateResponseHelper.ReturnCode))
                    {
                        throw new HUMCustomException('CC:CASE:'+lstCase[0].CaseNumber+'OTHERERROR:CALLOUTREAD_OR_SERVICE_DOWN: RETURNCODE:NULL:'+oCCTemplateResponseHelper);                        
                    }                    
                    else if(string.isNotBlank(oCCTemplateResponseHelper.ReturnCode))
					{
						if(oCCTemplateResponseHelper.ReturnCode.startsWith(RESPONSE_INFORMATION))
						{
							//Update Template service check as complete only when the Data power return the success code
							List<Template_Submission_Data__c > submissionList = new  List<Template_Submission_Data__c >();
								   
							for(Template_Submission_Data__c submissionDataObj : [SELECT Template_Field__r.Name,Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.ATTESTATION_TEMPLATE_NAME AND Template_Field__r.Name = :GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_CICMSCALLFIELD  AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :lstCase[0].Id])
							{
                                 if(Schema.sobjectType.Template_Submission_Data__c.fields.Value__c.isUpdateable())
								    submissionDataObj.Value__c  = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_YES;
								submissionList.add(submissionDataObj );
							}
							update submissionList;
						}						
						else if(oCCTemplateResponseHelper.ReturnCode.startsWith(RESPONSE_ERRORINFO))
						{
							throw new HUMCustomException('CC:CASE:'+lstCase[0].CaseNumber+'CODE:'+oCCTemplateResponseHelper.ReturnCode+':TYPE:'+oCCTemplateResponseHelper.Type+':DESC:'+oCCTemplateResponseHelper.Description);                      
						}   
						else
						{
							throw new HUMCustomException('CC:CASE:'+lstCase[0].CaseNumber+'CODE:'+oCCTemplateResponseHelper.ReturnCode+':TYPE:'+oCCTemplateResponseHelper.Type+':DESC:'+oCCTemplateResponseHelper.Description);                      
						}						
					}
					else
					{
						throw new HUMCustomException('CC:CASE:'+lstCase[0].CaseNumber+'OTHERERROR:CALLOUTREAD_OR_SERVICE_DOWN'+oCCTemplateResponseHelper);                        
					}
                }
            }                
            
       }
       catch(Exception ex)
       {   
            List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CreditableCoverageServiceCall_H_HUM','SERVICE_FAILURE');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            }   
       }
     }

    /*
    * Method Name   :    populateTemplateDetails
    * Description   :    This method is used to  populate all the Attestation Template related fields.
    * Return Type   :    CreditableCoverageTemplateRequest_H_HUM 
    * Parameters    :    Process.PluginRequest
    */
   public CreditableCoverageTemplateRequest_H_HUM populateTemplateDetails(list<case> caselst)
    {
        String strCaseId = caselst[0].Id;
        CreditableCoverageTemplateRequest_H_HUM obj = new CreditableCoverageTemplateRequest_H_HUM();
       
        //This will remove the unsaved template data And helps rep to launch template form again.
        HUMValidSubmissionHelper.deleteInValidSubmission(strCaseId); 
        
        //Get Template Submission for the creditable coverage where submission owner is case Id 
        List<Template_Submission__c> lstTemplateSubmission = [SELECT ID FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c = :strCaseId and Submission_Owner__r.Object_Owner_Type__c = 'Case'];
        
        if(!lstTemplateSubmission.isEmpty())
        {           
            List<Template_Submission_Data__c> templateSubmissionListData = [SELECT Name__c,Template_Field__r.Name,Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c = :lstTemplateSubmission[0].Id];
            if(!templateSubmissionListData.isEmpty())
            {
                for(Template_Submission_Data__c submissionDataObj : templateSubmissionListData)
                {
                    if(submissionDataObj.Name__c == 'ATVF_IsCICMSCalled')
                    {
                        if(string.isNotBlank(submissionDataObj.Value__c) && submissionDataObj.Value__c == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_YES)
                        {
                            return null;
                        }
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_RecieptDate')
                    {
                        dateTime strReciepDateTime = caselst[0].createdDate;
                        obj.ReceiptDate = (String) strReciepDateTime.format('MM/dd/yyyy');
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_EnrolleeFirstName')
                    {
                        obj.EnrolleeFirstName = caselst[0].Account.FirstName;
                    }
                    
                     if(submissionDataObj.Name__c == 'ATVF_EnrolleeLastName')
                    {
                        obj.EnrolleeLastName = caselst[0].Account.LastName;
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_MemberId')
                    {
                        obj.MemberID =  string.isnotblank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'';
                    }
                                    
                    if(submissionDataObj.Name__c == 'ATVF_CaseNumber')
                    {
                        obj.CRMCaseNumber = string.isnotblank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'';
                    }
                
                    if(submissionDataObj.Name__c == 'ATVF_CRMCaseCreateDate')
                    {
                        dateTime strCreatedDateTime = caselst[0].createdDate;
                        obj.CRMCaseCreateDate = (String) strCreatedDateTime.format('MM/dd/yyyy');           
                    }     
                    
                    if(submissionDataObj.Name__c == 'ATVF_MedicareTrackId')
                    {
                        obj.TrackID =  string.isNotBlank(caselst[0].Medicare_Track_ID__c)? caselst[0].Medicare_Track_ID__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_FEHBPCoveragePCheck')
                    {
                        obj.EmpUnionCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_FEHBPCoverageFromDate')
                    {
                        obj.EmpUnionCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'';  
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_FEHBPCoverageToDate')
                    {
                        obj.EmpUnionCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_SPAPCoverageCheck')
                    {
                        obj.StateCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_SPAPCoverageFromDate')
                    {
                        obj.StateCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_SPAPCoverageToDate')
                    {
                        obj.StateCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_VACoverageCheck')
                    {
                        obj.VABenefits = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_VACoverageFromDate')
                    {
                        obj.VABenefitsStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_VACoverageToDate')
                    {
                        obj.VABenefitsEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_TRICARECoverageCheck')
                    {
                        obj.TricareMilitaryCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_TRICARECoverageFromDate')
                    {
                        obj.TricareMilitaryCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_TRICARECoverageToDate')
                    {
                        obj.TricareMilitaryCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_MediGapCoverageCheck')
                    {
                        obj.MedigapCreditableCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_MediGapCoverageFromDate')
                    {
                        obj.MedigapCreditableCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_MediGapCoverageToDate')
                    {
                        obj.MedigapCreditableCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_ITUCoverageCheck')
                    {
                        obj.IndianHealthService = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_ITUCoverageFromDate')
                    {
                        obj.IndianHealthServiceStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_ITUToCoverageDate')
                    {
                        obj.IndianHealthServiceEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }               

                    if(submissionDataObj.Name__c == 'ATVF_PACECoverageCheck')
                    {
                        obj.PaceCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }               
                    
                    if(submissionDataObj.Name__c == 'ATVF_PACECoverageFromDate')
                    {
                        obj.PaceCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }   
                    
                    if(submissionDataObj.Name__c == 'ATVF_PACECoverageToDate')
                    {
                        obj.PaceCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }               
                    
                    if(submissionDataObj.Name__c == 'ATVF_DiffSourceOfCoverageCheck')
                    {
                        obj.DiffSourceCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_DiffSourceOfCoverageFromDate')
                    {
                        obj.DiffSourceCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_DiffSourceOfCoverageToDate')
                    {
                        obj.DiffSourceCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_HumanaCoverageCheck')
                    {
                        obj.HumanaCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_HumanaCoverageFromDate')
                    {
                        obj.HumanaCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_HumanaCoverageToDate')
                    {
                        obj.HumanaCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_ExtraHelpCoverageCheck')
                    {
                        obj.ExtraMedicareCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_ExtraHelpCoverageFromDate')
                    {
                        obj.ExtraMedicareCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_ExtraHelpCoverageToDate')
                    {
                        obj.ExtraMedicareCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_KatrinaCoverageCheck')
                    {
                        obj.HurricaneKatrinaCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_KatrinaCoverageFromDate')
                    {
                        obj.HurricaneKatrinaCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_KatrinaCoverageToDate')
                    {
                        obj.HurricaneKatrinaCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_PuertoRicoCoverageCheck')
                    {
                        obj.PuertoRicoReformaCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_PuertoRicoCoverageFromDate')
                    {
                        obj.PuertoRicoReformaCoverageStartDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_PuertoRicoCoverageToDate')
                    {
                        obj.PuertoRicoReformaCoverageEndDate = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:''; 
                    }
                    
                    if(submissionDataObj.Name__c == 'ATVF_NeverHadCrditableCoverage')
                    {
                        obj.NeverHadCreditibleCoverage = string.isNotBlank(submissionDataObj.Value__c)? submissionDataObj.Value__c:'false';
                    }                
                }
            
                return obj;
            }
            else
            {
                return null;
            }
        } 
        else        
        {
            return null;
        }
            
   } 
   
    /**
    * This method will be called when this class will be called using System.enqueueJob method
    * 
    * @param    context          QueueableContext instance
    * @return   na
    */
    public void execute(QueueableContext context) 
    {       
        if(sCaseID <> null)
        {
           invoke(sCaseID);
        }   
       
    }
    
      /*
    * This method is to check the case subtype is eligible for creditable Coverage.    
    * @param case Id
    * @return Boolean
    * 
    */
    public Boolean isCreditableCoverageSubtypeElligible(Id caseRecId)
    {       
        Boolean isCreditableCoverageSubtypeElligible = false;
        List<Template__c> lstTemplateObj = [Select Id from Template__c where Unique_Template_Name__c= :GLOBAL_CONSTANT_HUM.ATTESTATION_TEMPLATE_NAME];  
        //Check Attestation Template exist
        if(lstTemplateObj.size()>0)
        {   
            List<Template_Submission_Owner__c> lstSubmissionOwner = [SELECT Id FROM Template_Submission_Owner__c WHERE Object_Owner_ID__c = :caseRecId];
            //Check Attestation Template submission exist
            if(lstSubmissionOwner.size()>0)
            {
                List<Template_Submission__c> lstTemplateSubmission = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__c = :lstSubmissionOwner[0].Id AND Template__c = :lstTemplateObj[0].Id];
                //Check Attestation Template submission owner exist
                if(lstTemplateSubmission.size()>0)
                {
                    //Check Attestation Template Submission data exist
                    for(Template_Submission_Data__c subDataObj : [SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c = :lstTemplateSubmission[0].Id])    
                    {
                        //Check Attestation Disclaimer is selected (i.e.) Process attached and its ready to send CICMS 
                        if(subDataObj.Template_Field__r.Name  == GLOBAL_CONSTANT_HUM.ATTESTATION_DISCLAIMERNAME && subDataObj.Value__c == GLOBAL_CONSTANT_HUM.ATTESTATION_DISCLAIMERYESVALUE )
                        {
                            isCreditableCoverageSubtypeElligible = true;
                            break;
                        }
                    } 
                }
            }
        }
        
        return isCreditableCoverageSubtypeElligible;
    }
   
}