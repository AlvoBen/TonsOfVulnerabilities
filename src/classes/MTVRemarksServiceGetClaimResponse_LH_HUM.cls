/**************************************************************************** 
Apex class Name  : MTVRemarksServiceGetClaimResponse_LH_HUM 
Version          : 1.0  
Created Date     : 06/10/2022  
Function         : Response helper for GetClaimsMTVRemarks method in MTVRemarks Service
Test class       : MTVRemarksServiceGetClaimsResponse_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe                                06/10/2022                    Original Version
************************************************************************************************/

public class MTVRemarksServiceGetClaimResponse_LH_HUM
{  
    public MTVRemarksResponse MTVRemarksResponse;
    public class MTVRemarksResponse {
        public String PagingInd = ''; 
        public String StartRemarkSequenceNumber = '';
        public String CreationDate = '';
        public String CreationTime = '';
        public String SurrogateId = '';
        public RemarksList RemarksList;
    }
    public class RemarksList {
        public List<Remarks> Remarks;
    }
    public class Remarks {
        public String EntityType = '';
        public String Identifier = '';
        public String RemarkSequenceNumber = '';
        public String RemarkType = '';
        public String RemarkCategory = '';
        public String CreationTimestamp = '';
        public String RemarkText = '';
        public String SurrogateId = '';
    }
}