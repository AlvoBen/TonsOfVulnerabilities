/*******************************************************************************************************************************
Apex Class Name : InquirySummaryRequest_H_HUM.cls
Version         : 1.0
Created On      : 08/27/2020
Function        : Class contains Request Nodes for Inquiry Summary Service.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 				                      08/27/2020                REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look) 
* Sayali Nemade 								     09/09/2020                 REQ - 1170241 Not able to see data on UI for Group, Provider and Agent/Broker and categorized data for pharmacy/non-pharmacy User
* Sayali Nemade 					   				 09/12/2020               REQ - 1170241 Added new Parameter Record Type in Legacy Contact History  
 ********************************************************************************************************************************/
public with sharing class InquirySummaryRequest_H_HUM {

    public DataRetention DataRetention;
  
    public class DataRetention {
        public String StartDate = '';
        public String EndDate = '';
        public Integer StartCount;  
        public Integer EndCount;
        public String Memberid = '';
		public String idType= '';
        public String idValue= '';
        public String Phar_NonPhar= '';
		public String MGAPType = '';
    }
}