/*******************************************************************************************************************************
Apex Class Name : RequestIdCard_DTO_HUM.cls
Version         : 1.0
Created On      : 18th March 2018
Function        : Wrapper Class for data displayed in Order ID card table for Member.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni               207348             	 03/18/2018                	 Original Version 

*****************************************************************************************************************************************/

public with sharing class RequestIdCard_DTO_HUM
{
    public class PolicyMembersListDTO 
    {
        public List<ColumnDetailsDTO> columnDetailList;
        public List<PolicyMemberDTO> policyMembersDTOList;
        public PolicyMembersListDTO(List<Policy_Member__c> policyMembers) 
        {
            this.policyMembersDTOList = getPolicyMembersDTO(policyMembers);
            this.columnDetailList = getColumnDetails();
        }
        
        public List<PolicyMemberDTO> getPolicyMembersDTO(List<Policy_Member__c> policyMembers) 
        {
            List<PolicyMemberDTO> policyMembersDTOList = new PolicyMemberDTO[2];
            List<PolicyMemberDTO> policyMembersDTOListToSort = new List<PolicyMemberDTO>();
            for(Policy_Member__c policyMember: policyMembers) 
            {
				if (String.isBlank(policyMember.Subscriber__c))
                {
                        policyMembersDTOList.set(0,new PolicyMemberDTO(policyMember));
                }
                else if(policyMember.Relationship__c.containsIgnoreCase('Spouse')) 
                {
                    policyMembersDTOList.set(1,new PolicyMemberDTO(policyMember));
                }
                else 
                {
                    policyMembersDTOListToSort.add(new PolicyMemberDTO(policyMember));
                }
            }
            if(!policyMembersDTOListToSort.isEmpty()) 
            {
                policyMembersDTOList.addAll(policyMembersDTOListToSort);
            }
            return policyMembersDTOList;
        }
        
        public List<ColumnDetailsDTO> getColumnDetails() 
        {
            List<ColumnDetailsDTO> columnDetailsList = new List<ColumnDetailsDTO>();
            columnDetailsList.add(new ColumnDetailsDTO('Select','sSelectRow'));
            columnDetailsList.add(new ColumnDetailsDTO('Name','sName'));
            columnDetailsList.add(new ColumnDetailsDTO('Address','sAddress'));
            columnDetailsList.add(new ColumnDetailsDTO('Date of Birth','sBirthDate'));
            columnDetailsList.add(new ColumnDetailsDTO('Relationship','sRelationship'));
            return columnDetailsList;
        }
    }
    
    public class PolicyMemberDTO 
    {
        public String sName;
        public String sSubscriber;
        public String sAddress;
        public String sBirthDate;
        public String sRelationship;
        public String sSelectRow;
        public String sPolciyMemberId;
        public PolicyMemberDTO(Policy_Member__c policyMember) 
        {
            this.sName = policyMember.Member__r.Name;
            this.sSubscriber = policyMember.Subscriber__c;
            this.sAddress = getAddress(policyMember);
            this.sBirthDate = policyMember.Member__r.Birthdate__c;
            this.sRelationship = policyMember.Relationship__c;
            this.sPolciyMemberId = policyMember.Id;
            this.sSelectRow = '';
        }        
        public string getAddress(Policy_Member__c policyMem){
            String memberAddressStr = '';
            memberAddressStr+=policyMem.Member__r.PersonMailingStreet != null ? policyMem.Member__r.PersonMailingStreet+'  ' : '';
            memberAddressStr+=policyMem.Member__r.PersonMailingCity != null ? policyMem.Member__r.PersonMailingCity+', ' : '';
            memberAddressStr+=policyMem.Member__r.PersonMailingStateCode != null ? policyMem.Member__r.PersonMailingStateCode+'  ' : '';
            memberAddressStr+=policyMem.Member__r.PersonMailingPostalCode != null ? policyMem.Member__r.PersonMailingPostalCode : '';
            return memberAddressStr;
        }
    }    
    public class ColumnDetailsDTO 
    {
        public String columnTitle;
        public String columnData;
        public ColumnDetailsDTO(String columnTitle,String columnData) 
        {
            this.columnTitle = columnTitle;
            this.columnData = columnData;
        }
    }
}