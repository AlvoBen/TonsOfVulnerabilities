/**************************************************************************** 
Apex Class Name  : ClaimsServiceDetailsRequest_H_HUM_V2
Version          : 1.0  
Created Date     : March 6, 2018
Function         : Request helper for GetClaimsDetails method in Claims service
Test Class       : ClaimsServiceDetailsRequest_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat               212620            03/06/2018                REQ - 355817 - Detail page Integration using the ID field
***************************************************************************************************/
public class ClaimsServiceDetailsRequest_H_HUM_V2
{   
   public GetClaimDetail GetClaimDetail;
   
    public class GetClaimDetail 
    {
        public string Id = ''; 
    }
}