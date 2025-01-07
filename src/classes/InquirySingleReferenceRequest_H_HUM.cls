/*******************************************************************************************************************************
Apex Class Name : InquirySingleReferenceRequest_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains Request Nodes for Inquiry Single Reference Service.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 				                      08/27/2020                REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look) 
 ********************************************************************************************************************************/
public with sharing class InquirySingleReferenceRequest_H_HUM {

    public CCPGCPDataRetention CCPGCPDataRetention;
  
    public class CCPGCPDataRetention {
        public String ContactId = '';
        public String CriteriaFor = '';
		public String idType = '';
        public String idValue = '';
        public String Phar_NonPhar = '';
		public String MGAPType = '';
    }
}