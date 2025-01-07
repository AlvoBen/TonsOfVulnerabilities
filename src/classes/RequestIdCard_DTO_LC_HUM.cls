/*******************************************************************************************************************************
Apex Class Name : RequestIdCard_DTO_LC_HUM.cls
Version         : 1.0
Created On      : 11th Febraury 2022
Function        : Wrapper Class for data displayed in Order ID card table for Member.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi Patni                       02/11/2022                  Original Version 

*****************************************************************************************************************************************/

public with sharing class RequestIdCard_DTO_LC_HUM
{
    //@AuraEnabled
    //public List<ColumnDetailsDTO> columnDetailList;
    @AuraEnabled
    public List<PolicyMemberDTO_LC_HUM> policyMembersDTOList;
    public RequestIdCard_DTO_LC_HUM(List<MemberPlan> policyMembers){
        this.policyMembersDTOList =  getPolicyMembersDTO(policyMembers);
        //this.columnDetailList =  getColumnDetails();
    }

    @AuraEnabled
    public static List<PolicyMemberDTO_LC_HUM> getPolicyMembersDTO(List<MemberPlan> policyMembers) 
    {
        List<PolicyMemberDTO_LC_HUM> policyMembersDTOList = new PolicyMemberDTO_LC_HUM[2];
        List<PolicyMemberDTO_LC_HUM> policyMembersDTOListToSort = new List<PolicyMemberDTO_LC_HUM>();
        for(MemberPlan policyMember: policyMembers) 
        {
            if (String.isBlank(policyMember.Subscriberid))
            {
                    policyMembersDTOList.set(0,new PolicyMemberDTO_LC_HUM(policyMember));
            }
            else if(policyMember.RelationshipToSubscriber.containsIgnoreCase('Spouse')) 
            {
                policyMembersDTOList.set(1,new PolicyMemberDTO_LC_HUM(policyMember));
            }
            else 
            {
                policyMembersDTOListToSort.add(new PolicyMemberDTO_LC_HUM(policyMember));
            }
        }
        if(!policyMembersDTOListToSort.isEmpty()) 
        {
            policyMembersDTOList.addAll(policyMembersDTOListToSort);
        }
        return policyMembersDTOList;
    }

    /*
    public List<ColumnDetailsDTO> getColumnDetails() 
    {
        List<ColumnDetailsDTO> columnDetailsList = new List<ColumnDetailsDTO>();
        columnDetailsList.add(new ColumnDetailsDTO('Select','sSelectRow'));
        columnDetailsList.add(new ColumnDetailsDTO('Name','sName'));
        columnDetailsList.add(new ColumnDetailsDTO('Address','sAddress'));
        columnDetailsList.add(new ColumnDetailsDTO('Date of Birth','sBirthDate'));
        columnDetailsList.add(new ColumnDetailsDTO('Relationship','sRelationship'));
        return columnDetailsList;
    }*/
  
    
}