/******************************************************************************************************************************
Apex Class Name  : HUMValidSubmissionHelper
Version          : 1.0 
Created Date     : June 20 2015
Function         : CHelper class to verify Valid Submission                 
Test Class       :
Modification Log :
    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
    Asha Deepika Koyyana                 06/20/2015            Orginal Version    
    Moshitha Gunasekaran 29308           07/26/2019            Made changes to exclude TRR template from cleaning process 
    Riddhi Mittal                        01/15/2020            782503 PR00086652 (Multi Browser Support) - SF - P9 Medium Defects - CRUD Security fixes
  * Jasmeen Shangari                     04/09/2020            942030 - Added 'Send Print Item: Member' process in filter criteria 
  * Mithra Bharadwaj                     4/28/2020             US#1027240
  * Mohammed Noor                      	 02/01/2021			   US1921246-PR94254-MF4-AEP Performance - SF Limits - Sync CPU Time Limit Optimization  
 *********************************************************************************************************************************/  
public with sharing class HUMValidSubmissionHelper
{
     
     /*
    * Method Name   :    deleteInValidSubmission
    * Description   :    Delete the invalid submssion
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public static  void deleteInValidSubmission(String CaseId )
    {
    try
    {
        String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);

            List<Template_Submission__c> lstTemplateSub = new List<Template_Submission__c>();
        List <Template_Submission_Data__c> lstDSubmssionDatatobeDeleted = new List <Template_Submission_Data__c> ();
        
		if(CaseId !=NUll && Template_Submission__c.sObjectType.getDescribe().isDeletable())
        {
            lstTemplateSub = [SELECT ID,Version__c,Submission_Owner__r.Id FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member','Transaction Reply Report (TRR)') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: CaseId AND Object_Owner_Type__c =: sCase )  ];
            if(lstTemplateSub.size() > 0)
            {
                List<Template_Submission_Data__c> lstSubmisssionData = [SELECT ID, Value__c from Template_Submission_Data__c where Template_Submission__c =: lstTemplateSub[0].ID] ;
            
                for(Template_Submission_Data__c oTemplateSubData : lstSubmisssionData ) 
                {
                    if(oTemplateSubData != NULL)
                    {
                        if(String.isBlank(oTemplateSubData.Value__c))	lstDSubmssionDatatobeDeleted .add(oTemplateSubData );
                        else	break;
                    }
                }  
            
                if(lstDSubmssionDatatobeDeleted.size() == lstSubmisssionData .Size() )
                {
					    Database.DeleteResult lstresult = database.delete (lstTemplateSub[0],false ) ;
						Database.DeleteResult result1 =  database.delete (lstTemplateSub[0].Submission_Owner__r.Id,false) ;
					
                }                                                                             
          }  
        }
    }
     catch(Exception ex) 
    {
        HUMExceptionHelper.logErrors(ex, 'HUMValidSubmissionHelper', 'deleteInValidSubmission'); 
    } 
  }
	
	/*
    * Method Name   :    deleteInValidSubmissionAsynch
    * Description   :    Delete the invalid submssion asynchronously
    * Return Type   :    Boolean
    * Parameters    :    String CaseId
    */
	public static Boolean deleteInValidSubmissionAsynch(String CaseId )
    {
   		Boolean bInvalidSubmission = false;
    	try
    	{        
        	String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);
            List<Template_Submission__c> lstTemplateSub = new List<Template_Submission__c>();
        	List <Template_Submission_Data__c> lstDSubmssionDatatobeDeleted = new List <Template_Submission_Data__c> ();
        
			if(CaseId !=NUll && Template_Submission__c.sObjectType.getDescribe().isDeletable())
        	{
            	lstTemplateSub = [SELECT ID,Version__c,Submission_Owner__r.Id FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member','Transaction Reply Report (TRR)') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: CaseId AND Object_Owner_Type__c =: sCase )  ];
            	if(lstTemplateSub.size() > 0)
            	{
                	List<Template_Submission_Data__c> lstSubmisssionData = [SELECT ID, Value__c from Template_Submission_Data__c where Template_Submission__c =: lstTemplateSub[0].ID] ;
            
                	for(Template_Submission_Data__c oTemplateSubData : lstSubmisssionData ) 
                	{                    	
	                    if(oTemplateSubData != NULL)
	                    {
	                        if(String.isBlank(oTemplateSubData.Value__c))	lstDSubmssionDatatobeDeleted .add(oTemplateSubData );
	                        else	break;
	                    }
                	}  
            
	                if(lstDSubmssionDatatobeDeleted.size() == lstSubmisssionData .Size() )
	                {
						    bInvalidSubmission = true;
						    deleteSubmissionRecords(lstTemplateSub[0].Id, lstTemplateSub[0].Submission_Owner__r.Id);	//call the future method to delete the invalid templates						
	                }                                                                             
          		}  
        	}
    	}
     	catch(Exception ex) 
    	{
        	HUMExceptionHelper.logErrors(ex, 'HUMValidSubmissionHelper', 'deleteInValidSubmissionAsynch'); 
    	}
    	return bInvalidSubmission;
  	}
  
	/*
    * Method Name   :    deleteSubmissionRecords
    * Description   :    future method to Delete the invalid submission asynchronously
    * Return Type   :    void
    * Parameters    :    Id, Id
    */
	@future
	public static void  deleteSubmissionRecords(Id templateSubmissionId, Id SubmissionOwnerId)
	{
  		try  		
  		{
  			if(templateSubmissionId!=null && SubmissionOwnerId!=null)
  			{
	  			Template_Submission__c oTsub = new Template_Submission__c(Id=templateSubmissionId);
	  			Template_Submission_Owner__c oTsubOwner = new Template_Submission_Owner__c(Id=SubmissionOwnerId);
	  
	  			Database.DeleteResult lstresult = database.delete (oTsub,false );
				Database.DeleteResult result1 =  database.delete (oTsubOwner,false);				
  			}
  		}
  		catch(Exception ex) 
	    {
	        HUMExceptionHelper.logErrors(ex, 'HUMValidSubmissionHelper', 'deleteSubmissionRecords'); 
	    }
			
	}
}