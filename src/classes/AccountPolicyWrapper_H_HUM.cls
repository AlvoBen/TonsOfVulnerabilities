/*****************************************************************************************
Apex class Name    : AccountPolicyWrapper_H_HUM 
Version            : 1.0
Function           : Wrapper class used to sort policy member records
Created Date       : 02/10/2020

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *  Vinay Lingegowda			                        02/10/2020		    User story- 791019 PR00091574 - MF 1 - AHT - Display Termed policies in the chronological order on the Member Account page (RF)
 *  Ajay Chakradhar                           		    05/05/2021	        REQ - 2184294 - Replacing constants with Consumer Management Constants
 ****************************************************************************************************************************/
Public class AccountPolicyWrapper_H_HUM implements Comparable 
{       
        Public Policy_Member__c polyMemb = new Policy_Member__c();
        Date dtEffectiveDate{get; set;}
        Date dtEndDate{get; set;}
        String sPolicyMemberStatus{get; set;}
        Private String sVOB_TERMEDDATE_HUM = '12/31/9999';
        
        /**
        * constructor AccountPolicyWrapper_H_HUM
        */
        public AccountPolicyWrapper_H_HUM (Policy_Member__c oPolicyMember) {
            this.polyMemb = oPolicyMember;
            this.sPolicyMemberStatus = oPolicyMember.status__c;
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
        }
        
        /**
        * sorting logic based on sortdate
        * @param Object(AccountPolicyWrapper_H_HUM )
        * @return Integer
        */
        public Integer compareTo(Object compareTo) {
            AccountPolicyWrapper_H_HUM apWrap = (AccountPolicyWrapper_H_HUM)compareTo;
            Integer returnValue = 0;
            if(this.sPolicyMemberStatus == 'Termed'){
                if(this.dtEndDate < apWrap.dtEndDate){
                    returnValue = 1;
                } else if (this.dtEndDate > apWrap.dtEndDate) {
                    returnValue = -1;
                }else if (this.dtEndDate == apWrap.dtEndDate){
                    if(this.dtEffectiveDate < apWrap.dtEffectiveDate){
                        returnValue = 1;
                    }else if(this.dtEffectiveDate > apWrap.dtEffectiveDate){
                        returnValue = -1;
                    }
                }
            }
             return returnValue;
        }

    }