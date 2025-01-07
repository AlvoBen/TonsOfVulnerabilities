/**************************************************************************** 
Apex Class Name  : HUMCaseUpdateService
Version          : 1.0  
Created Date     : May 9 2014 
Function         : Here all the CCS Service methods - ICD methods for the integration are defined
                  Used without sharing as the service is exposed to external system
Test class       : HUMCaseUpdateServiceTest              
***************************************************************************/
/*
Modification Log:
* Developer Name       Code Review #                                Date                        Description
*------------------------------------------------------------------------------------------------------------* 
Anil Pilaka              17353                                08/16/2014                   Original Version
Anil Pilaka              17369                                08/18/2014                   Updated changes according to review
Palak Papneja            18313                                10/06/2014                   Made changes according to Humana standards
Vishal Verma                                                  12/19/2014                   Fix for defect 166051.0001 : Made changes in the response message sent in catch block.
ChiranjeeviRao Ravuri    30921                                4/07/2016                    REQ - 262439:Replace Attachment_URL__c field with Attachment_URL_Text__c field. 
***************************************************************************************************/


global with sharing class HUMCaseUpdateService {
    /*  
     * Method name : updateCaseStatus
     * Description : This method will be used to update the case fields.
     * Return Type : HUMStatusMessageHelper 
     * Parameter   : HUMCaseUpdateRequestHelper 
     */
    webService static HUMStatusMessageHelper updateCaseStatus(HUMCaseUpdateRequestHelper CaseUpdateRequestDTO) {
        
        Boolean bResultStatus = false;       
        HUMStatusMessageHelper statMessage;
        try{  
            List<Case> listCase = [SELECT Id, caseNumber, Status, OwnerId FROM Case WHERE CaseNumber = :CaseUpdateRequestDTO.sCaseNumber];
            if (listCase != null && listCase.size() > 0) 
            {
                Case caseRec = listCase[0];
                caseRec =  assignValuesToCase(CaseUpdateRequestDTO,caseRec);
                Database.SaveResult[] lstResultCase = Database.update(new Case[] {caseRec}, false);
                bResultStatus = true;
                for (Database.SaveResult resultCase: lstResultCase) {
                    if (!resultCase.isSuccess())
                    {
                        Database.Error err = resultCase.getErrors()[0];
                        bResultStatus = false;
                        statMessage = new HUMStatusMessageHelper(bResultStatus, err.getStatusCode(), err.getMessage());
                    }
                }
                if (bResultStatus)
                {
                    bResultStatus = doCreateCaseComment(CaseUpdateRequestDTO,caseRec );
                    // By using the comment provided, create the casecomment record for the case.
                    statMessage = new HUMStatusMessageHelper(bResultStatus, null, '');
                    // By using the attachment details provided, create the casecomment record for the case.
                    if(bResultStatus && CaseUpdateRequestDTO.lstAttachmentDTO != null)
                    {                
                        statMessage = new HUMStatusMessageHelper(doCreateAttachment(CaseUpdateRequestDTO,caseRec), null, '');
                    }
                }
            } 
            else
            {
                statMessage = new HUMStatusMessageHelper(false, null, System.label.HUMNoInteractionRecFound);
            }
        }
        catch(Exception e){
            statMessage = new HUMStatusMessageHelper(false, null,e.getMessage());
        }
        return statMessage;
    }
    
    /*  
     * Method name : assignValuesToCase
     * Description : This method is used to assign values to case on the basis of the input.
     * Return Type : Case
     * Parameter   : HUMCaseUpdateRequestHelper , Case
     */
    private Static Case assignValuesToCase(HUMCaseUpdateRequestHelper CaseUpdateRequestDTO , Case caseRec)
    {
        if(caseRec != null)
        {
            if(CaseUpdateRequestDTO.sOwnerId != null && CaseUpdateRequestDTO.sOwnerId != '')
            {
                caseRec.OwnerId = CaseUpdateRequestDTO.sOwnerId;
                List<User> listUser = [Select Name from User where id = :caseRec.OwnerId LIMIT 1];
                if(listUser != null)
                {
                    caseRec.Case_Owner__c = listUser[0].Name;
                }
            }
            caseRec.Owner_Queue__c = CaseUpdateRequestDTO.sQueueName;
            if(CaseUpdateRequestDTO.sCaseStatus != null && CaseUpdateRequestDTO.sCaseStatus != '')
            {
                if (CaseUpdateRequestDTO.sCaseStatus.equalsIgnoreCase(system.label.HUMCaseUpdateOpen))
                {
                    caseRec.Status = system.label.HUMInProgress;
                }
                else if (CaseUpdateRequestDTO.sCaseStatus.equalsIgnoreCase(system.label.HUMCaseUpdateClosed))
                {
                    caseRec.Status = system.label.HUMCanvasCaseStatusClosed;
                }
            }
        }
        return caseRec;
    }
    
     /*  
     * Method name : doCreateCaseComment
     * Description : This method is used to create case comments.
     * Return Type : boolean
     * Parameter   : HUMCaseUpdateRequestHelper , Case
     */
    private static boolean doCreateCaseComment(HUMCaseUpdateRequestHelper CaseUpdateRequestDTO,Case caseRec){
        // By using the comment provided, create the casecomment record for the case.
        Boolean bCaseCommResult = false;         
        CaseComment CaseCommentRec = new CaseComment(ParentId = caseRec.Id, CommentBody = CaseUpdateRequestDTO.sComment);
        Database.SaveResult[] lstResultCaseCmnt = Database.Insert(new CaseComment[] {CaseCommentRec}, false);
        for (Database.SaveResult resultCaseCmnt: lstResultCaseCmnt)
        {
            if (!resultCaseCmnt.isSuccess()) 
            {
                Database.Error err = resultCaseCmnt.getErrors()[0];
            } else
            {
                bCaseCommResult = true;
            }
        }
        return bCaseCommResult;        
    }
    
      /*  
     * Method name : doCreateAttachment
     * Description : This method is used to create case Attachment.
     * Return Type : boolean
     * Parameter   : HUMCaseUpdateRequestHelper , Case
     */
    private static boolean doCreateAttachment(HUMCaseUpdateRequestHelper CaseUpdateRequestDTO,Case caseRec){
        // By using the attachment details provided, create the casecomment record for the case.
        Boolean bAttachmentResult = false;
        List<Attachment__c> lstAttachmentRec = new List<Attachment__c>{};
        if(CaseUpdateRequestDTO.lstAttachmentDTO.size() > 0)
        {
            for(HUMCaseUpdateAttachmentDTO AttachmentDTORec : CaseUpdateRequestDTO.lstAttachmentDTO)
            {
                Attachment__c attachmentRec = new Attachment__c(Related_To_Case__c = caseRec.Id,Attachment_URL_Text__c = AttachmentDTORec.sAttachmentUrl,Name = AttachmentDTORec.sFileName);
                lstAttachmentRec.add(attachmentRec);        
            }
        }
        
        if(lstAttachmentRec.size() > 0)
        {
            Database.SaveResult[] lstResultCaseAttach = Database.Insert(lstAttachmentRec, false);
            for (Database.SaveResult resultCaseAttach: lstResultCaseAttach) 
            {
                if (!resultCaseAttach.isSuccess())
                {
                    Database.Error err = resultCaseAttach.getErrors()[0];
                } else 
                {
                    bAttachmentResult = true;
                }
            }
        }
        return bAttachmentResult;
    }

}