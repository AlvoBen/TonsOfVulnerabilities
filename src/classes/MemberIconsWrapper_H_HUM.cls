/*****************************************************************************************
Apex class Name    : MemberIconsWrapper_H_HUM 
Version            : 1.0
Function           : Wrapper class used to sort policy member records
Created Date       : 08/28/2019
Test Class         : MemberIconsWrapper_T_HUM 

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                      08/28/2019         Original Version
 *   Raja Sekhar Reddy M V                               11/13/2020         User Story 1660597: PR00096346 - MF 1 - DEV - New Member Indicator
 *   Ajay Chakradhar                           		     05/05/2021	        REQ - 2184294 - Replacing constants with Consumer Management Constants
 ****************************************************************************************************************************/
Public class MemberIconsWrapper_H_HUM implements Comparable 
{
        Public Date dtEffectiveDate{get; set;}
        Public Date dtEndDate{get; set;}
        Date dtSortDate{get; set;}
        Public String sPolicyMemberStatus{get; set;}
        Public String sPolicyMemberPlan{get; set;}
        Private String sVOB_TERMEDDATE_HUM = '12/31/9999';
        /**
        * constructor NewPolicyMemberWrapper
        */
        public MemberIconsWrapper_H_HUM (Policy_Member__c oPolicyMember) {
            this.sPolicyMemberStatus = oPolicyMember.status__c;
            this.sPolicyMemberPlan = oPolicyMember.Product_Type_Code__c;
            String sEffectiveDate = String.isNotBlank(oPolicyMember.Effective_Date__c) ? oPolicyMember.Effective_Date__c : sVOB_TERMEDDATE_HUM;
            try {
                this.dtEffectiveDate = Date.parse(sEffectiveDate);
            } catch(Exception e) {
                List<String> lstStr = sEffectiveDate.split('/');
                this.dtEffectiveDate = Date.newInstance(Integer.valueOf(lstStr.get(2)), Integer.valueOf(lstStr.get(0)), Integer.valueOf(lstStr.get(1)));
            }
            
            String sEndate = String.isNotBlank(oPolicyMember.End_Date__c) ? oPolicyMember.End_Date__c : sVOB_TERMEDDATE_HUM;
            try {
                this.dtEndDate = Date.parse(sEndate);
            } catch(Exception e) {
                List<String> lstStr = sEndate.split('/');
                this.dtEndDate = Date.newInstance(Integer.valueOf(lstStr.get(2)), Integer.valueOf(lstStr.get(0)), Integer.valueOf(lstStr.get(1)));
            }
            
            // determine date field to sort, Termed policies sort by End Date and Other policies sort by Effective Date
            this.dtSortDate = (this.sPolicyMemberStatus.equals(ConsumerManagementConstants_HUM.POLICYSTATUS_TERMED) ? this.dtEndDate : this.dtEffectiveDate); 
        }
        
        /**
        * sorting logic based on sortdate
        * @param Object(MemberIconsWrapper_H_HUM )
        * @return Integer
        */
        public Integer compareTo(Object compareTo) {
           Date dtSortDate = compareTo != null ? ((MemberIconsWrapper_H_HUM)compareTo).dtSortDate : System.today();
           return this.dtSortDate.daysBetween(dtSortDate);
        }
    }