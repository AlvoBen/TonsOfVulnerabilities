/*******************************************************************************************************************************
Apex Class Name : Public class MTVRemarks_Member_T_HUM
Version         : 1.0
Created On      : 04/05/2019   
Function        : This  class is used to cover parse request for MTV servvice
Test Class      : MTVRemarks_Member_T_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj patil		                                   06/1/2019                  Original version
*******************************************************************************************************************************************************************/
Public class MTVRemarksServiceGetMemberRequest_H_HUM
{
    public GetMTVRemarksRequest GetMTVRemarksRequest;
    public class GetMTVRemarksRequest
    {   
       public String IdentifierId = ''; 
       public String SubIdentifier = ''; 
       public String IdType = ''; 
       public String StartDate = '';
       public String EndDate = '' ;
    }
}