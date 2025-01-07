/**
   @Author      : Pradeepkumar Dani
   @name        : AttachFiles_C_HUM
   @CreateDate  : 10/30/2017
   @Description : This class serves as controller for AttachFiles_CMP_HUM component
   @Version     : 1.0
   @reference   : AttachFiles_CMP_HUM
   
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pradeepkumar Dani          146713                   11/10/2017                     Original version 
 * Pradeepkumar Dani		  160881				   12/22/2017                     Enhancements  
 * Mohammed Noor              268720                   08/08/2018                     REQ - 362124 - Added Logic to create Milestone 903 for attachments
 *                                                                                    added for Incomplete DE SNP Cases. 
********************************************************************************************************************************/
public with sharing class AttachFiles_C_HUM 
{
    @RemoteAction
    public static string createAttRecord(String sCaseId, string fName) 
    {
        Attachment__c attachment = new Attachment__c();
        try 
        {    
            if(string.isNotBlank(sCaseId) && string.isNotBlank(fName))
            {
            	attachment.RecordtypeID 		= HUMUtilityHelper.getRecordTypeID(Attachment__c.sObjectType ,'Case');
	            attachment.Name 				= fName;
	            attachment.Related_To_Case__c 	= sCaseId; 
	            insert attachment; 
            }
        }
        catch(Exception e) 
        {    
            /*Possible Exceptions 
            DML Exception - will be handled by DatabaseUpsertResult
            Any other exception - Exception logger will show a generic error message 
             */
            HUMExceptionHelper.logErrors(e,'AttachFiles_C_HUM','createAttRecord');           
        }
          
        return attachment.Id;
    }

	@RemoteAction
    public static boolean deleteAttRecords(list<String> attIds) 
    {
        boolean res = false;
        list<Attachment__c> attRecords = null;
        if(attIds != null && !attIds.isEmpty())
        {
        	attRecords = [SELECT ID FROM Attachment__c WHERE Id=:attIds];
        } 
        
        try 
        {    
            if(attRecords != null && !attRecords.isEmpty())
            {
				List<Database.deleteResult> delResults = Database.delete(attRecords, false);
            	res = true;
            }
        }
        catch(Exception e) 
        {    
            /*Possible Exceptions 
            DML Exception - will be handled by DatabaseUpsertResult
            Any other exception - Exception logger will show a generic error message 
             */
            HUMExceptionHelper.logErrors(e,'AttachFiles_C_HUM','deleteAttRecords');           
        }
          
        return res;
    }
	
	/*
    * This is the method which loads data for attachments added for Incomplete DE SNP cases.
    * @param     List of Attachment IDs and Case ID.
    * @return    Boolean.
    */
	@RemoteAction    
    public static boolean createAttachmentMstone(List<String> attachmentIDs, string sCaseID)
    {    	
    	try
    	{
	    	List<Case> parentCase = [Select id, subtype__c, recordtype.name from Case where ID =:sCaseID];
	    	
	    	if(parentCase!=null && !parentCase.isEmpty()&& parentCase[0].subtype__c == GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_INCOMPLETE_DE_SNP && parentCase[0].recordtype.name == GLOBAL_CONSTANT_HUM.MEDICARE_CASE)
	    	{	    		    	
		    	if(attachmentIDs!=null && !attachmentIDs.isEmpty())
		    	{
		    		CaseAttachmentTrackService_C_HUM.invokeTrackServiceForAttachments(attachmentIDs);
		    	}
	    	}
    		return true;
    	}
    	catch(exception ex)
    	{
    		return false;
    	}
    }
}