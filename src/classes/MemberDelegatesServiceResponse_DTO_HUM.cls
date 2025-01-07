/**
   @Author Gargey Sharma
   @name MemberDelegatesServiceResponse_DTO_HUM
   @CreateDate 12/07/2016
   @Description Response helper class for Member Delegates Service
   @Version 1.0
   @reference REQ - 290967: Go365 Kids Delegates section
   @Test Class 
Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma				 70895					12/07/2016						Original Version
****************************************************************************************************************************/
public with sharing class MemberDelegatesServiceResponse_DTO_HUM  {
    public GetDelegateResponseDTO GetDelegateResponseDTO;
    
    public class GetDelegateResponseDTO {
    	public DelegatesCollection DelegatesCollection;
    }
    
    public class DelegatesCollection {
    	public list<Delegate> Delegate;
    }
    
    public class Delegate {
    	public string PersonId = '';
    	public string Relationship = '';
    	public string PolicyType = '';
    	public string StartDate = '';
    	public string EndDate = '';
    	public string ModifiedDate = '';
    	public string AccessType = '';
    	public string Status = '';
    	public string Revoked = '';
    }
}