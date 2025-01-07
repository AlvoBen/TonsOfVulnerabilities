/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCreateCaseRequestHelper
Version          : 1.0
Created Date     : May 23 2016
Function         : This class is used as request for create case  CreateCase_D_HUM.
Test Class       : HUMCreateCaseRequestHelperTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name        Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Adil Mohammed                                    05/24/2016                           Original Version
 * Prasad Jandhyala      103536                     02/28/2017                           Req 301390
 * Prafull Verma         112270                     07/14/2017	            		   	 REQ - 315928 - Creditable Coverage Intake - Case Service
 * Mohammed Noor 		 114118 					07/26/2017							 REQ - 315928 - Creditable Coverage Intake - Case Service - Added three new Variables
 *                                                                                       For Encrypted Error messages. 
 ****************************************************************************************************************************************************************************/

global with sharing class HUMCreateCaseRequestHelper
{
  webservice boolean bAsynchronousProcess = null;
  webservice string sSourceApp ='';
  webservice string sSourceKey = '';
  webservice string sInteractingAboutId = '';
  webservice string sInteractingAboutIdType = '';
  webservice string sInteractingAboutType = '';
  webservice string sInteractingAboutFirstName = '';
  webservice string sInteractingAboutLastName = '';
  webservice string sInteractingAboutDOB = '';
  webservice string sInteractingWithId = '';
  webservice string sInteractingWithIdType ='';
  webservice string sInteractingWithType = '';
  webservice string sInteractingWithFirstName = '';
  webservice string sInteractingWithLastName = '';
  webservice string sInteractingWithDOB = '';
  webservice string sOrigin = '';
  webservice string sType = '';
  webservice string sCaseSubType = '';
  webservice string sStatus = '';
  webservice string sCreatedQueue = '';
  webservice string sOwnerQueue = '';
  webservice string sRecordTypeID = '';

  webservice string sServiceCenter = '';
  webservice string sDepartment = '';
  webservice string sClassificationType = '';
  webservice string sPriority = '';
  webservice string sComplaint = '';
  webservice string sComplaintReason = '';
  webservice string sComplaintType = '';
  
  webservice string sGARightsGiven = '';
  webservice string sGAReason = '';
  webservice string sFollowupDueDate = '';
  webservice string sClassification = '';
  webservice string sIntent = '';  
  webservice List<HUMCaseCommentRequestDTO> CaseCommentDTO = null;
  
  webservice string sDocType= '';
  webservice string sDCN= '';
     
 // webservice List<HUMLogAttachmentRequestDTO> attachmentDTO = null;  
    
    webservice string  sClosedDate = '';
    webservice string sContract = '';
    webservice string sExternalTrackId = '';
    webservice string sInteractingAboutAddressLine1 = '';
    
    webservice string sInteractingAboutCity = '';
    webservice string sInteractingAboutEmailID = '';
    webservice string sInteractingAboutHomePhone = '';
    webservice string sInteractingAboutState = '';
    webservice string sInteractingAboutZipCode = '';
  
    webservice string sInteractingWithAddressLine1 = '';
        
    webservice string sInteractingWithCity = '';
    webservice string sInteractingWithEmailID = '';
    webservice string sInteractingWithHomePhone = '';
    webservice string sInteractingWithState = '';
    webservice string sInteractingWithZipCode = '';
    
    webservice string sMbrSignDate = '';
    webservice string sMemberSSN = '';
    webservice string sMilestoneId = '';
    webservice string sPBP = '';
    webservice string sPendKey = '';
    webservice string sSegment = '';
    webservice string sSourceIdTypeCd = '';  
    webservice string sTopicName = '';
    webservice string sWorkQueueViewName = '';

	webservice string sAccountErrMsg = '';
    webservice string sPolicyErrMsg  = '';
    webservice string sCaseCommentErrMsg = '';
	
}