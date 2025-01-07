/**************************************************************************** 
Apex Class Name  : VeteranGetResponse_DTO_HUM
Version          : 
Created Date     : MAY 20, 2021
Function         : Response DTO Wrapper for VeteransGetService In VeteransAPI
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Giri Yeddula                             20/05/2021                 Original Version
*************************************************************************************************** */

public class VeteranGetResponse_DTO_HUM{
    public VeteranGetResponse_DTO_HUM(){}    
    public String masterPersonId{get; set;}
    public String memberGenKey{get; set;}
    public String veteranStatus{get; set;}
    public String veteranStatusSourceId{get; set;}
    public String isEnrolledInVA{get; set;}
    public List<Audits> latestAudits{get; set;}
    
    public class Audits{
        public String sourceId{get; set;}
        public String timestamp{get; set;}
        public String veteranStatusAction{get; set;}
        public VeteranSourceInput jsonInput{get; set;}
    }
    
    public class VeteranSourceInput{
        public String veteranStatus{get; set;}
        public String sourceId{get; set;}
        public String sourceSystemTimestamp{get; set;}
        public String modifiedBy{get; set;}
    }
}