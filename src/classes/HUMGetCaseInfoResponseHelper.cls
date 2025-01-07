/**************************************************************************** 
Apex Class Name  : HUMGetCaseInfoResponseHelper
Version          : 1.0  
Created Date     : August 18 2014 
Function         : This class is used as a base class to handle the GetCaseResponse Helper
Test Class       : HUMGetCaseInfoResponseHelperTest
****************************************************************************
Modification Log:

* Developer Name           Code Review #                          Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham             17369                                 08/18/2014                Original Version
* Shruthi Karanth          18313                                 09/29/2014                Updated test class
* Pradeep Veeravali        20179                                 12/25/2014                added extra variables to expose all case fields 
* ChiranjeeviRao           20294                                 01/07/2014                As per confirmation,Removed 'RecommendedProcess' field.
***************************************************************************************************/
global with sharing class HUMGetCaseInfoResponseHelper
{ 
      webservice String AccountName = '';
      webservice Decimal CaseAge = 0.0;      
      webservice String CaseNumber = '';
      webservice String CaseOrigin = '';
      webservice String CaseOwner = '';
      webservice String CaseReason = '';
      webservice String Category = '';
      webservice Boolean Closed = null;
      webservice String Complaint = '';
      webservice String ComplaintReason = '';
      webservice String ContactName = '';
      webservice String CreatedBy = '';
      webservice DateTime DateTimeClosed = null;
      webservice Decimal DaysSinceLastModified = 0.0;
      webservice String Description = '';
      webservice Date DueDate = null;
      webservice Boolean Escalated = null;
      webservice Boolean FollowUpRequired = null;
      webservice String FundingType = '';
      webservice String GAReason = '';
      webservice String GARightsGiven = '';
      webservice Boolean HumanaEmployee= null;
      webservice String InteractingAboutType = '';
      webservice String InteractingWith = '';
      webservice String InteractingWithType = '';
      webservice String InternalComments = '';
      webservice String LastModifiedBy = '';
      webservice String LineofBusiness = '';
      webservice String ParentCase = '';
      webservice String PlanIssueState = '';
      webservice String PolicyMember = '';
      webservice String PolicyPlatform = '';
      webservice String Priority = '';
      webservice String Product = '';
      webservice String ProductType = '';
      webservice String Queue = '';
      webservice String Reason = '';
      webservice Boolean Reopened = null;
      webservice String Status = '';
      webservice String Subject = '';
      webservice String Subtype = '';
      webservice String Type = '';
      webservice String InteractingAbout = '';
      webservice String DateTimeOpened = '';
      webservice String CreatedByQueue = '';
      webservice String lastModifiedByQueue = '';
      webservice String QueueOwner = '';
      webservice String ReOpenCaseAge = '';
      webservice String ReOpenCaseDate = '';
      Webservice List<HUMAttachmentHelper> AttachmentDTO = null;
      Webservice List<HUMAttachmentItemHelper> AttachmentLogDTO = null;
      Webservice HUMInteractionLogHelper InteractionDTO = null; 
      Webservice List<HUMTaskHelper> TaskDTO = null;
      Webservice List<HUMCaseCommentHelper> CaseCommentDTO = null;
      Webservice Boolean Success = null;
      Webservice String ErrorCode = '';
      Webservice String ErrorMessage = '';     

      // newly added fields as per REQ-96619
      webservice Boolean applyToComplaint= null;
      webservice String behavioralHealth= '';      
      webservice String caseTAT= '';
      webservice String classificationType= '';
      webservice Boolean CoKY_Case= null;      
      webservice String Department= '';
      webservice Boolean DSTCase= null;      
      webservice Boolean emailSent= null;
      webservice String employerGroupName= '';      
      webservice String Exchange= '';
      webservice String ExchangeType= '';
      webservice Date FollowUpDueDate= null;    
      webservice Boolean GeneralCase= null;
      webservice Boolean HODentalCase= null;
      webservice Boolean HOMedicalCase= null;            
      webservice String InteractingWithName= '';
      webservice String InteractionID= '';
      webservice Boolean closedWhenCreated= null;
      webservice String LanguagePreference= '';
      webservice String NPIID= '';    
      webservice String PlanSegmentSize= '';  
      webservice String PreviousCaseOwnerRole= '';            
      webservice String SellingMarketNumber= '';
      webservice String ServiceCenter= '';
      webservice String StateMemResidAdd= '';
      webservice String TaskTAT= '';
      webservice String TaxID= '';
      webservice String TenantId= '';
      webservice Boolean TexasComplaintLetterRequested= null;
      webservice String Topic= '';
      webservice String TXComplaintRelatedMessage= '';      
      webservice String WorkQueueViewName= '';      
      webservice String InteractingAboutFullName= '';
      webservice String InteractingWithFullName= '';
      
      
}