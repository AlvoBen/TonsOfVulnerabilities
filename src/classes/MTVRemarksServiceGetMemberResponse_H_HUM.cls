/*******************************************************************************************************************************
Apex Class Name : Public class MTVRemarksServiceGetMemberResponse_H_HUM
Version         : 1.0
Created On      : 04/05/2019   
Function        : This  class is used to cover parse request for MTV servvice
Test Class      : MTVRemarks_Member_T_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Suraj patil		                                  06/01/2019                  Original version
*  Sagar MN 										  04/21/2020				  User Story 777415 : MF 09 - MTV Member Remarks � Add Entity Remark Type 
*******************************************************************************************************************************************************************/
public class MTVRemarksServiceGetMemberResponse_H_HUM {

    public MTVRemarksResponse MTVRemarksResponse;
    
    public class MTVRemarksResponse {
        public ResponseStatus ResponseStatus;
        public String LastSequenceNumber='';
        public String RemarkCount='';
        public String IdType='';
        public String IdentifierId='';
        public String SubIdentifier='';
        public RemarkList RemarkList;
    }
    
    public class ResponseStatus {
        public String StatusCode='';
        public String StatusDescription='';
        public String ErrorMessage='';
    }
    
    public class RemarkList {
        public List<Remarks> Remarks;
    }
    
    public class Remarks {
        public String IdType='';
        public String IdentifierId='';
        public String SubIdentifier='';
        public String RemarkType='';
        public String RemarkCategory='';
        public String CreatedDate='';
        public String RemarkText='';
    }
}