/**************************************************************************************************************
Apex Class Name : LOGGING_CONSTANT_HUM 
Version         : 1.0
Created On      : 05/05/2021
Function        : Constant class for logging framework 

* Modification Log:
* Developer Name            Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Lakshmi Madduri          05/05/2021                Original version
* Nirmal Garg			   02/15/2022				 REQ_2847599_Lightning Logging Framework
*****************************************************************************************************************/

public class LOGGING_CONSTANT_HUM {
    Public Static final String STRING_CREATEDDATE ='CreatedDate';
    Public Static final String STRING_DESC = 'DESC';
    Public Static final String STRING_ASC = 'ASC';
    Public Static final Integer INTEGER_5 = 5;
    Public Static final List<String> LIST_PHARMACY_CASE_RECORD_TYPES = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};
    Public Static final List<String> LIST_PHARMACY_WORK_QUEUES = New List<String>{'Provider Payment Integrity','Louisville Other Insurance Investigation','Louisville RSO Oral Grievance Oversight','LV Medicare Member Communications'};
    Public Static final List<String> LIST_PHARMACY_WEB_CHAT_WQUEUES = New List<String>{'Humana Pharmacy Web Chat','HP Clinical Services Web Chat'};
    Public Static final String STRING_NO = 'No';
    Public Static final String STRING_CUSTOMER_CARE_SPECIALIST = 'Customer Care Specialist';
    Public static final string STRING_CUSTOMER_CARE_SUPERVISOR = 'Customer Care Supervisor';
    Public static final string STRING_HUMANA_PHARMACY_SPECIALIST = 'Humana Pharmacy Specialist';
    Public Static final String STRING_CRM = 'CRM';
    Public Static final String STRING_SELECT_WRKQUEUE_BEFORE_CREATING_OR_EDITING_CASE = 'Please select a Work Queue before creating or editing a Case';
	public static final string STRING_CRM_LIGHTNING = 'CRM Lightning';
}