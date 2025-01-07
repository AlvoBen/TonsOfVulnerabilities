/**
   @Author Gargey Sharma
   @name MemberDelegatesServiceRequest_DTO_HUM
   @CreateDate 12/07/2016
   @Description Request helper class for Member Delegates Service
   @Version 1.0
   @reference REQ - 290967: Go365 Kids Delegates section
   @Test Class  
Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma				  70895					02/10/2017						Added new field DelegateType in request
****************************************************************************************************************************/
public with sharing class MemberDelegatesServiceRequest_DTO_HUM 
{
    public GetDelegateRequest GetDelegateRequest = null;
    
    public class GetDelegateRequest 
    {
        public string EnterpriseID = '';
        public string History = '';
        public string DelegateType = '';
    }
}