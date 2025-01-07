/******************************************************************************************************************
Apex class Name    : CRMRetail_Import_DTO_HUM
Version            : 1.0
Function           : This class is used as Wrapper class class for CRMRetail_Task_Import_Helper_HUM
Created On         : Oct 20 ,2021
Test Class         : CRMRetail_Task_Import_T_HUM

Modification Log:
*   Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
* Abhishek Maurya               10/20/2021              User Story - 2578417 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Task Validation
------------------------------------------------------------------------------------------------------------------- */
public class CRMRetail_Import_DTO_HUM {
	@AuraEnabled public String MEMBERID = '';
    @AuraEnabled public String LOCATION = '';
    @AuraEnabled public String ASSIGNEDTO ='';
    @AuraEnabled public String SUBJECT ='';
    @AuraEnabled public String PRIORITY ='';
    @AuraEnabled public String DUEDATE ='';
    @AuraEnabled public string ERROR = '';
}