/**
   @Author Gargey Sharma
   @name MedicareCallsCaseComments_H_HUM
   @CreateDate 05/10/2017
   @Description Helper class to check if case is related to Medicare Call and create case comment records
   @Version 1.0
   @reference REQ - 300142
   @Test Class 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma              95016                   05/10/2017                     Original version
 * Gargey Sharma              96293                   05/26/2017                     Defect 287334.0001
 * Gargey Sharma              97834                   05/31/2017                     Defect 290523.0001 - Length check on case comment fields
 * Gargey Sharma              99543                   06/07/2017                     Changes as per new approach for REQ - 300142
 * Pallavi R                 110756                   07/04/2017                     Changes as per REQ 322573
 * Pallavi R                 114377                   07/27/2017                     Defect Fix 304045.0001 
 * Pallavi R                 116812                   08/04/2017                     REQ - 322573 - Updated code as per requirement.   
 * Praveen Kumar Parimi                               08/22/2017                     Bulkified the  createUpdateCaseCommentRecord and updated  AssigncaseCommentTypeJunction method.                                                                                 
 * Pallavi R                 138826                   10/16/2017                     REQ 329023 - Fix for medicare case comments - updated logic as per CA #7079470
 * Amar Gadewar										  02/21/2019					 REQ - 383787 - User Story - Updating the display of Case Comments (RF)
 * Amar Gadewar										  05/02/2019					 REQ - 396386- Quality - RCC - Update the display of Case Comments (CRM)
 * Vijaykumar								        10/19/2020		       User Story 1442197: PR00091574 - IR 5050 - ability to capture clicks on re-assign to creator/sender (RF)
 * Vijaykumar								        01/18/2021		       User Story 1750377: PR00091574 -IR5050 - hashtags case transfers for no comments/ max char limit comment (RF)
 * Mohammed Noor                              		04/26/2021             US2163321 - Case Management Checkmarx Security violations - Case History & Case Misc
 * Mohammed Noor                              		07/01/2021             US2273201 - Case Management Checkmarx Security violations
********************************************************************************************************************************/
public with sharing class MedicareCallsCaseComments_H_HUM 
{
    public string sHashTagComment ='';
    /*
     * This method is used to check if this is a Medicare case
     * @param  string
     * @param  string
     * @param  string
     * @return boolean 
     */
    public boolean checkMedicareCare(string sOrigin, string sProdCode, string sLOB, string sProdType, string sSegmentType)
    {
        boolean bMedicareCase = false;
        Map<String, Set<String>> medicareLOBType = GLOBAL_CONSTANT_HUM.MEDICARE_LOB_PRODUCTTYPE_LIST(); 
        if(sOrigin == GLOBAL_CONSTANT_HUM.MEDICARECALLS_CASEORIGIN_HUM && sProdCode == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY && medicareLOBType.containsKey(sLOB))
        {
            if(sProdType == GLOBAL_CONSTANT_HUM.MEDICARECALLS_MAPD_HUM || sProdType == GLOBAL_CONSTANT_HUM.MEDICARECALLS_PDP_HUM || sProdType == GLOBAL_CONSTANT_HUM.MEDICARECALLS_MA_HUM)
            bMedicareCase = true;
            else if(string.isBlank(sProdType) && sSegmentType == GLOBAL_CONSTANT_HUM.MEDICARECALLS_SEGMENTMCR_HUM)
            bMedicareCase = true;
        }
        return bMedicareCase;
    }
    
    /*
     * This method is used to create and edit case comment records
     * @param  string
     * @param  string
     * @param  Id
     * @param  boolean
     * @return boolean 
     */
    public boolean createUpdateCaseCommentRecord(string sMedicareCallsIssue, string sMedicareCallsRes, Id CaseId, integer iCountMediIssueCC, integer iCountMediResCC, String caseStatus, boolean bMediCCUpdate, boolean bMediCCIssUpdate, boolean bMediCCResUpdate,boolean bIstransfer)
    {   
       boolean bCheckStatus = true;
       List<CaseCommentType_Junction__c> lstCCTJuninsert = new List<CaseCommentType_Junction__c>();
       
       if((caseStatus.equalsIgnoreCase('Closed')&&(bMediCCUpdate == false))||((bIstransfer==true)&&(bMediCCUpdate == false))) 
       {
            if(String.isBlank(sMedicareCallsIssue) && string.isBlank(sMedicareCallsRes)) 
             { 
               bCheckStatus = false;
               ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, Label.MEDICARECASEREQUIREDERROR_HUM));
             }
        
           else if((String.isBlank(sMedicareCallsIssue)&&string.isNotBlank(sMedicareCallsRes))||(String.isNotBlank(sMedicareCallsIssue)&&string.isBlank(sMedicareCallsRes))) 
              { 
                bCheckStatus = false;
                ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, Label.MEDICARECASEREQUIREDERROR_HUM));
              }
        
           else if(string.isNotBlank(sMedicareCallsIssue) && string.isNotBlank(sMedicareCallsRes))
              {
                   if(!(iCountMediIssueCC >= 10 && iCountMediIssueCC <= 2000 && iCountMediResCC >= 10 && iCountMediResCC <= 2000 ))
                    { 
                      bCheckStatus = false;
                      ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, Label.MEDICARECASELENGTHERROR_HUM));
                    } 
              }
       } 
       
       else if(iCountMediIssueCC>2000)
       {
          bCheckStatus = false;
          ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, Label.MEDICAREISSUELENGTHERROR));
       }
       
       else if(iCountMediResCC>2000)
       {
          bCheckStatus = false;
          ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, Label.MEDICARERESOLENGTHERROR));
       }
       
       if(bCheckStatus)
       {
       	
    	if(string.isNotBlank(sMedicareCallsRes))
	    {
	    	if((bMediCCResUpdate == false)||(bMediCCResUpdate == true&&(bIstransfer==true)))
	        {
	        	CaseComment oMedCallResCC = new CaseComment();
	            if(Schema.SObjectType.CaseComment.fields.CommentBody.isCreateable())	oMedCallResCC.CommentBody = sMedicareCallsRes;
	            if(Schema.SObjectType.CaseComment.fields.ParentId.isCreateable())	oMedCallResCC.ParentId = CaseId;
	            insert oMedCallResCC;
	            lstCCTJuninsert.addall(AssigncaseCommentTypeJunction(oMedCallResCC, GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM));
	        }
	    }
	    
          if((string.isNotBlank(sMedicareCallsIssue)) || (string.isNotBlank(sHashTagComment)))
          {
            if((bMediCCIssUpdate == false)||(bMediCCIssUpdate == true&&(bIstransfer==true)))
            {
              if(string.isNotBlank(sHashTagComment)) {
                String sCaseCommentsHTC = sMedicareCallsIssue + sHashTagComment;
                if(sCaseCommentsHTC.length() <= 2000){
                  sMedicareCallsIssue += sHashTagComment;
                }
                if(sCaseCommentsHTC.length() > 2000) {
                  CaseComment oMedCallIssueCCHTC = new CaseComment();
                  if(Schema.SObjectType.CaseComment.fields.CommentBody.isCreateable())	oMedCallIssueCCHTC.CommentBody = sHashTagComment;
                  if(Schema.SObjectType.CaseComment.fields.ParentId.isCreateable())	oMedCallIssueCCHTC.ParentId = CaseId;
                  insert oMedCallIssueCCHTC;
                  lstCCTJuninsert.addall(AssigncaseCommentTypeJunction(oMedCallIssueCCHTC, GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM));
                  }
              }
              CaseComment oMedCallIssueCC = new CaseComment();
              if(Schema.SObjectType.CaseComment.fields.CommentBody.isCreateable())	oMedCallIssueCC.CommentBody = sMedicareCallsIssue;
              if(Schema.SObjectType.CaseComment.fields.ParentId.isCreateable())	oMedCallIssueCC.ParentId = CaseId;
              insert oMedCallIssueCC;

              lstCCTJuninsert.addall(AssigncaseCommentTypeJunction(oMedCallIssueCC, GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM));
            }
          }
	     
	    
	    bCheckStatus = true;
	    
       }
       
       if(lstCCTJuninsert!=null && lstCCTJuninsert.size()>0)
       insert lstCCTJuninsert;
      return bCheckStatus;
    }
    
    /*
     * This method is used to create CaseCommentType_Junction records
     * @param  CaseComment
     * @param  string
     * @return void 
     */
    public List<CaseCommentType_Junction__c> AssigncaseCommentTypeJunction(CaseComment oMedCall, string sCCType)
    {
        List<CaseCommentType_Junction__c> lstCCTJun = new List<CaseCommentType_Junction__c>();
        list<CaseCommentType__c> lstCCT = [SELECT Id FROM CaseCommentType__c WHERE Name = :sCCType];
        if(lstCCT != null && !lstCCT.isEmpty())
        {  
            CaseCommentType_Junction__c oCCTJunc = new CaseCommentType_Junction__c();
            if(Schema.SObjectType.CaseCommentType_Junction__c.fields.CaseCommentType__c.isCreateable())	oCCTJunc.CaseCommentType__c = lstCCT[0].Id;
            if(Schema.SObjectType.CaseCommentType_Junction__c.fields.CaseComment__c.isCreateable())	oCCTJunc.CaseComment__c = oMedCall.Id;
            lstCCTJun.add(oCCTJunc);
        }
        return lstCCTJun;
    }
}