/**************************************************************************** 
Apex Class Name  : HUMCBISSearchRequest_H_HUM 
Version          : 1.0  
Created Date     : May 10, 2021
Function         : Request helper for CBIS Specialty REST Service
Test Class       : HUMCBISSearchRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Firoja Begam                                 05/10/2021                 US-2229318 - PR00094232/T1PRJ0002736 - 2021 MF6 - Security - SF - CBIS Specialty Service Migration
*  Kiran Kumar									06/26/2022				   US 2451298 Buildfix
***************************************************************************************************/
public class HUMCBISSearchRequest_H_HUM
{   
    public GetPendHistoryTermsDetails GetPendHistoryTermsDetails;
    public class GetPendHistoryTermsDetails {
        public String FirstName = '';
        public String LastName = '';
        public String DOB = '';
        public String StateCode = '';
		Public String SSN = '';
    }
}