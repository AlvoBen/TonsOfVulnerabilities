/**************************************************************************** 
Apex Class Name  : HUMCBISSearchResponse_H_HUM 
Version          : 1.0  
Created Date     : May 10, 2021
Function         : Response helper for CBIS Specialty REST Service
Test Class       : HUMCBISSearchResponse_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Firoja Begam                                 05/10/2021                 US-2229318 - PR00094232/T1PRJ0002736 - 2021 MF6 - Security - SF - CBIS Specialty Service Migration
***************************************************************************************************/
public class HUMCBISSearchResponse_H_HUM
{   
    public GetPendHistoryTermsDetailsResponse GetPendHistoryTermsDetailsResponse;
    
    public class GetPendHistoryTermsDetailsResponse{
       public ResponseItems ResponseItems;
       public Status Status;
    }
    
    public class ResponseItems {
        public List<ResponseItem> ResponseItem;
    }
     public class ResponseItem {
        public String DOB = '';
        public String DependentSSN = '';
        public String FirstName = '';
        public String LastName = '';
        public String MemberId = '';
        public String MemberSSN = '';
        public String MiddleInit = '';
        public String NameSuff = '';
        public String PhoneNumber = '';
        public String Platform = '';
        public String RelationType = '';
        public String SexType = '';
        public String DentalOriginalEffectiveDate = '';
        public String DivisionRecordsTotalCount= '';
        public Address Address;
        public Policy Policy;
    }
       
    public class Address {
        public String Address1 = '';
        public String Address2 = '';
        public String City = '';
        public String State = '';
        public String Zip = '';
        public String ZipPlus = '';   
    }

    public class Policy {
        public String BenId = '';
        public String CoverageEndDate = '';
        public String CoverageStartDate = '';
        public String GroupId = '';
        public String PendDate = '';
        public String PendError = '';
        public String PendKey = '';
    }  
    
    public class Status {
        public Error Error;
        public String IsSuccesful = '';
    }
    
    public class Error{
        public String ErrorCode = '';
        public String Message = '';
    }
}