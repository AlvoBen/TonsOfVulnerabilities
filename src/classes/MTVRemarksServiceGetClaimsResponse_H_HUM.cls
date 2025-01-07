/**************************************************************************** 
Apex class Name  : MTVRemarksServiceGetClaimsResponse_H_HUM 
Version          : 1.0  
Created Date     : February 17, 2016
Function         : Response helper for GetClaimsMTVRemarks method in MTVRemarks Service
Test class       : MTVRemarksServiceGetClaimsResponse_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    30111                02/17/2016                    Original Version
* ChiranjeeviRao Ravuri    30351                03/02/2016                    Added extra fields as per new response structure
***************************************************************************************************/
public class MTVRemarksServiceGetClaimsResponse_H_HUM
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