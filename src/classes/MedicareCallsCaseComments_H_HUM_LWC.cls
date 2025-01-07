/************************************************************************************************************************
Apex class Name    : MedicareCallsCaseComments_H_HUM_LWC
Version            : 1.0
Function           : Helper class to check if case is related to Medicare Call and create case comment records
Created On         : March 21 2022

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala/
	Shailesh Bagade/Gowthami Thota                  03/21/2022          Original Version(Feature 2963843 - Cases/Tasks Tab)
    Prasuna Pattabhi                                 10/27/2022               US 3934433 - Medicare Calls CAse Comments
***************************************************************************************************************************/
public class MedicareCallsCaseComments_H_HUM_LWC {
    
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
        Map<String, Set<String>> medicareLOBType = HumConstants_LWC.MEDICARE_LOB_PRODUCTTYPE_LIST(); 
        if(sOrigin == HumConstants_LWC.MEDICARECALLS_CASEORIGIN_HUM && sProdCode == HumConstants_LWC.MEDICARE_POLICY && medicareLOBType.containsKey(sLOB))
        {
            if(sProdType == HumConstants_LWC.MEDICARECALLS_MAPD_HUM || sProdType == HumConstants_LWC.MEDICARECALLS_PDP_HUM || sProdType == HumConstants_LWC.MEDICARECALLS_MA_HUM)
            bMedicareCase = true;
            else if(string.isBlank(sProdType) && sSegmentType == HumConstants_LWC.MEDICARECALLS_SEGMENTMCR_HUM)
            bMedicareCase = true;
        }
        return bMedicareCase;
    }
    /*
     * This method Is used to create And edit case comment records
     * @param  string
     * @param  string
     * @param  Id
     * @return boolean 
     */
    @AuraEnabled
    public static boolean createUpdateCaseCommentRecord(string sMedicareCallsIssue, string sMedicareCallsRes, Id CaseId){   
       boolean bCheckStatus = True;
       List<CaseCommentType_Junction__c> lstCCTJuninsert = new List<CaseCommentType_Junction__c>();
        if(string.isNotBlank(sMedicareCallsRes)){
            CaseComment oMedCallResCC = new CaseComment();
	        oMedCallResCC.CommentBody = sMedicareCallsRes;
	        oMedCallResCC.ParentId = CaseId;
	        insert oMedCallResCC;
	        lstCCTJuninsert.addall(AssigncaseCommentTypeJunction(oMedCallResCC, GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM));
        }
        if (string.isNotBlank(sMedicareCallsIssue)){
            CaseComment oMedCallIssueCC = new CaseComment();
            oMedCallIssueCC.CommentBody = sMedicareCallsIssue;
            oMedCallIssueCC.ParentId = CaseId;
            insert oMedCallIssueCC;
            lstCCTJuninsert.addall(AssigncaseCommentTypeJunction(oMedCallIssueCC, GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM));
        }
	    if (lstCCTJuninsert != null && lstCCTJuninsert.size() > 0){
            insert lstCCTJuninsert;
        }
        return bCheckStatus;
    }    
    /*
     * This method Is used to create CaseCommentType_Junction records
     * @param  CaseComment
     * @param  string
     * @return void 
     */
    public static List<CaseCommentType_Junction__c> AssigncaseCommentTypeJunction(CaseComment oMedCall, string sCCType){
        List<CaseCommentType_Junction__c> lstCCTJun = new List<CaseCommentType_Junction__c>();
        list<CaseCommentType__c> lstCCT = [SELECT Id FROM CaseCommentType__c WHERE Name = :sCCType];
        if (lstCCT != null && !lstCCT.isEmpty()){  
            CaseCommentType_Junction__c oCCTJunc = new CaseCommentType_Junction__c();
            oCCTJunc.CaseCommentType__c = lstCCT[0].Id;
            oCCTJunc.CaseComment__c = oMedCall.Id;
            lstCCTJun.add(oCCTJunc);
        }
        return lstCCTJun;
    }
}