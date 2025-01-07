/****************************************************************************************************************************************************
* Class Name       : CloseFlow_C_HUM 
* Version          : 1.0 
* Created Date     : Apr 22 2018
* Function         : Controller to handle event on close Pages.                   
* Test Class       : CloseFlow_T_HUM
*****************************************************************************************************************************************************
* Modification Log :
* Developer                    Code Review      Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Moshitha Gunasekaran          218172          04/22/2018           Original version.
* Prathyu Tushar                324032          01/02/2019           REQ - 380419 Auto route cases for Expedited and Part - D redetermination
* Prathyu Tushar                                01/27/2019           Defect#399537.0001 fix when claim process is attached
* Moshitha Gunasekaran                          01/29/2020           Story - 725129 - DEAA Medicare Opt Out process display in CRM for DEAA (Dual Eligible Auto Assigned) cases
* Vinay lingegowda                              03/04/2020           USER STORY 856241 PR00091574 - MF 1 - Quality - PCP Template Updates (CRM)
* Vinay Lingegowda                              03/19/2020           US-857398 AHT/Quality - Medicare Good Cause Reinstatement Template Auto Routing and AHT Updates (CRM)
* Jasmeen Shangari                              04/21/2021           Checkmarx fixes Azure ID - 2163309
* Y Sunil Kumar Reddy                           04/06/2021           User Story 1874589: T1PRJ0001827 - MF 1 - AHT - "Medicaid PAR Provider Not Accepting" template auto routing (CRM)
****************************************************************************************************************************************************/

public with sharing class CloseFlow_C_HUM {

     /*
     * This method is constructor for this class and used to initialize variables.
     * @name   CloseFlow_C_HUM
     * @param  ApexPages.StandardController - Object which holds instance of current loaded record
     * @return NA
     * @throws NA
     */
    public CloseFlow_C_HUM(ApexPages.StandardController controller) {
         initLoadEdit();
    }

    public String sProcessType { get; set; }
    public String sSelectedCaseId { get; set; }
       
   
    /*
    * Method Name : initLoadEdit
    * Description : assigning variables based on page paramenters and generating basic queries needed.
    * Return type : Void
    * Paramater   : NA
    */
    public void initLoadEdit()
    {
         Id SubmissionId  = null;
         if (Apexpages.currentPage().getParameters().containsKey('ATTVFCaseId') &&  Apexpages.currentPage().getParameters().get('ATTVFCaseId') != null )
         {
            sSelectedCaseId = Apexpages.currentPage().getParameters().get('ATTVFCaseId');

            try
            {           
                Template_Submission__c oTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: 'Case') ];                 
                SubmissionId = oTemplateSubmssion.Id;                
            }
            catch(Exception ex) 
            {
                SubmissionId = null;  
            } 
    
         } 
         else if ( Apexpages.currentPage().getParameters().containsKey('ExpCaseId') || Apexpages.currentPage().getParameters().containsKey('PartD')
	 	|| Apexpages.currentPage().getParameters().containsKey('PCP') || Apexpages.currentPage().getParameters().containsKey('GoodCause') || Apexpages.currentPage().getParameters().containsKey('MedicaidPARCaseId'))
         {
             
             if(Apexpages.currentPage().getParameters().containsKey('ExpCaseId') && Apexpages.currentPage().getParameters().get('ExpCaseId') != null)
             	sSelectedCaseId = Apexpages.currentPage().getParameters().get('ExpCaseId');
             else if(Apexpages.currentPage().getParameters().containsKey('PartD') && Apexpages.currentPage().getParameters().get('PartD') != null)
             	sSelectedCaseId = Apexpages.currentPage().getParameters().get('PartD');
		else if(Apexpages.currentPage().getParameters().containsKey('PCP') && Apexpages.currentPage().getParameters().get('PCP') != null)
             	sSelectedCaseId = Apexpages.currentPage().getParameters().get('PCP');
             else if(Apexpages.currentPage().getParameters().containsKey('GoodCause') && Apexpages.currentPage().getParameters().get('GoodCause') != null)
                 sSelectedCaseId = Apexpages.currentPage().getParameters().get('GoodCause');
             else if(Apexpages.currentPage().getParameters().containsKey('MedicaidPARCaseId') && Apexpages.currentPage().getParameters().get('MedicaidPARCaseId') != null)
             	sSelectedCaseId = Apexpages.currentPage().getParameters().get('MedicaidPARCaseId');
             try
             {           
                 Template_Submission__c oTemplateSubmssion = [SELECT ID,Version__c, Template__r.Unique_Template_Name__c FROM Template_Submission__c where Template__r.Unique_Template_Name__c in ('95C90830-3133','BC929CD6-E06A','E0A13562-89EE-48B8-8EBA-E485C6583FF1','d884d160-77c0-4d1b-95aa-edf8c81f3a59','deeef952-9f63-49ce-97d0-c7eb3204b519') and  Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: 'Case') ];                 
                 if(oTemplateSubmssion.Template__r.Unique_Template_Name__c != GLOBAL_CONSTANT_HUM.PCPTEMPLATEUNIQUEID){
                 SubmissionId = oTemplateSubmssion.Id;  
                 }  
                 else if(oTemplateSubmssion.Template__r.Unique_Template_Name__c == GLOBAL_CONSTANT_HUM.PCPTEMPLATEUNIQUEID ){
                        Template_Submission_Data__c PCPCheck = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =: GLOBAL_CONSTANT_HUM.PCPTEMPLATEUNIQUEID AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:sSelectedCaseId AND Template_Field__r.Name=:GLOBAL_CONSTANT_HUM.SERVICEFUNDCHECK LIMIT 1];
                        if(PCPCheck.Value__c == 'Yes'){
                            SubmissionId = oTemplateSubmssion.Id;
                        }
                 } 
             }
             catch(Exception ex) 
             {
                 SubmissionId = null;   
             } 
         }
         else if (Apexpages.currentPage().getParameters().containsKey('DEAACaseId') &&  Apexpages.currentPage().getParameters().get('DEAACaseId') != null )
         {
            sSelectedCaseId = Apexpages.currentPage().getParameters().get('DEAACaseId');
            try
            {           
                list<Template_Submission__c> lstTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.DEAA_Template and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: 'Case') ];
                if(lstTemplateSubmssion != null && !lstTemplateSubmssion.isEmpty())
		        {                 
                	SubmissionId = lstTemplateSubmssion[0].Id;   
		        }             
            }
            catch(Exception ex) 
            {
                SubmissionId = null;  
            } 
         }
         if (SubmissionId != null && Apexpages.currentPage().getParameters().containsKey(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM) && Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM) != null)
         { 
			 sProcessType = Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM).escapehtml4();
         }
         else
         { 
             sProcessType = '';
         }
    }
}