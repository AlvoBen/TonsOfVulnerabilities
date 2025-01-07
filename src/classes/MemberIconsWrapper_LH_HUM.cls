/*****************************************************************************************
Apex class Name    : MemberIconsWrapper_LH_HUM 
Version            : 1.0
Function           : Wrapper class used to sort member plan records
Created Date       : 03/12/2021
Test Class         : MemberIconsWrapper_LT_HUM 

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *  Ankima Srivastava                                    04/07/2021        Original Version
 ****************************************************************************************************************************/
Public class MemberIconsWrapper_LH_HUM implements Comparable 
{
        Public Date dtEffectiveDate{get; set;}
        Public Date dtEndDate{get; set;}
        Date dtSortDate{get; set;}
        Public String sPolicyMemberStatus{get; set;}
        Public String sPolicyMemberPlan{get; set;}
        
        /**
        * constructor NewPolicyMemberWrapper
        */
        public MemberIconsWrapper_LH_HUM (MemberPlan oPolicyMember) {
            this.sPolicyMemberStatus = oPolicyMember.Member_Coverage_Status__c ;
            this.sPolicyMemberPlan = oPolicyMember.Product_Type_Code__c;
            if(oPolicyMember.EffectiveFrom!=null){
            	this.dtEffectiveDate =oPolicyMember.EffectiveFrom;
            }else{
            	this.dtEffectiveDate = Date.parse(HUMConstants.VOB_TERMEDDATE_HUM);    
            }
           
            if(oPolicyMember.EffectiveTo!=null){
            	this.dtEndDate =oPolicyMember.EffectiveTo;
            }else{
            	this.dtEndDate = Date.parse(HUMConstants.VOB_TERMEDDATE_HUM);    
            }
           
            // determine date field to sort, Termed policies sort by End Date and Other policies sort by Effective Date
            this.dtSortDate = (this.sPolicyMemberStatus.equals(Constants_C_AHT_HUM.POLICYSTATUS_TERMED) ? this.dtEndDate : this.dtEffectiveDate); 
        }
        
        /**
        * sorting logic based on sortdate
        * @param Object(MemberIconsWrapper_H_HUM )
        * @return Integer
        */
        public Integer compareTo(Object compareTo) {
           Date dtSortDate = compareTo != null ? ((MemberIconsWrapper_LH_HUM)compareTo).dtSortDate : System.today();
           return this.dtSortDate.daysBetween(dtSortDate);
        }
    }