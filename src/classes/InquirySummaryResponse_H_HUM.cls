/*******************************************************************************************************************************
Apex Class Name : InquirySummaryResponse_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains Response Nodes for Inquiry Summary and Single Reference Service.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 				                     08/27/2020                REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look) 
 ********************************************************************************************************************************/
public  with sharing class InquirySummaryResponse_H_HUM
{
    public List<ContactDetails> ContactDetails;
    public String count = '';
    public String FLAG_24_MONTHS = '';
    
    public class ContactDetails
    {
        public String CONTACT_ID = '';
        public String INQUIRY_ID = '';
        public String INQ_TYPE_DESC = '';
        public String POLICY_NAME = '';
        public String CATEGORY_CD_DESC = '';
        public String REASON_CD_DESC = '';
        public String DISPOSITION_DESC = '';
        public String CREATED_TIMESTAMP = '';
        public String INQUIRYFORTYPE = '';
        public String INQR_ID = '';
        public String INQUIRYFOR_FIRSTNAME = '';
        public String INQUIRYFOR_LASTNAME = '';
        public String INQUIRYABOUTTYPE = '';
        public String INQA_ID = '';
        public String INQUIRYABOUT_FIRSTNAME  = '';
        public String INQUIRYABOUT_LASTNAME = '';
        public String OWNER_FIRST_NAME = '';
        public String OWNER_LAST_NAME = '';
        public String OWNER_TEAM_NAME = '';
        public String OWNER_DEPT_NAME = '';
        public String TASKS = '';
        public String PRIORITY= '';
        public String STATUS = '';
        public String RECEIVED_TS = '';
    }
  }