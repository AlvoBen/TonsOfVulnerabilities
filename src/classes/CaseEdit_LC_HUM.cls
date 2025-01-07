/*******************************************************************************************************************************
Apex Class Name : CaseEdit_LC_HUM
Version         : 1.0
Created On      : 01/22/2022
Function        : This class is used for handle all edit case scenario

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                    12/06/2021               Original Version
*    1.2           Gowthami Thota                                   06/29/2022               US-3087423, Added additional fields ti the query
*    1.3           Tummala Vijaya Lakshmi                           09/01/2022               US : 3711686 - Lightning- Case Management- Missing Fields Implementation(Incredibles)
*    1.4           Prasuna Pattabhi                                 09/28/2022               US 3707812 - Create Medicare Case
*    1.5           Nirmal Garg                                      09/29/2022               US 2961209 PCP Auto Routing Status
*    1.6           Prasuna Pattabhi                                 09/30/2022               US 3730127 - Edit And Closed Edit Medicare Case
*    1.7		   Jasmeen Shangari									12/05/2022		         US-4016899, Creditable coverage Process changes to launch Case Edit to pre-populate specific values
*    1.8           Disha Dole 										1/10/2023				 US-4095062, DF-6742: Regression - Topic is not displaying on case details page
*    1.9           Vijaya Lakshmi Tummala					        01/19/2023				 US 4128938 -Escalation Indicator New Case & Case Edit Page
*	 2.0    	   Gowthami Thota								    4/5/2023				 US 4461952 Auto Fill "Interacting With" & "Interacting With Name"
*    2.1           Santhi Mandava                                   10/12/2023               US 4884468: Member Search lookup in Related Accounts Section  
*/
public with Sharing class CaseEdit_LC_HUM {
    /*
* Method Name : fetchCaseData
* Description : this method is used to get case details so that we can populate these fields value on case edit as these can not fetch from recordedit form
* Return type : object
* Parameter   : objId
*/
    public static Case fetchCaseData(Id objID, Case objCase){
        Case caseDetails = [Select id, AccountId,Account.Name,Escalation_Indicator__c,Escalation_Indicator_Date__c, Verbal_Consent_Obtained__c,Owner_Queue__c,Priority,Interacting_With_Type__c,Interacting_With__c,Interacting_With__r.Name,Interacting_With_Name__c,NPI_ID__c,Tax_ID__c,G_A_Rights_Given__c,Complaint__c,Origin,Type,Subtype__c, Recordtype.Name, Classification_Type__c,CaseNumber,Response_Status__c,Intent_Id__c,Classification_Id__c,Classification_Id__r.name,Intent_Id__r.name,Topic__c,RecordTypeId,AutoRoute_Status__c,DCN__c,Status, Call_Benefit_Category__c, DCN_Formula__c, Pend_Key__c, Member_Plan_Id__c,Doc_Type__c,Election_Type_Code__c, Guided_Process_Flag__c,Case_Owner__c, OwnerId from Case where id=:objID LIMIT 1];
        objCase.Response_Status__c = caseDetails.Response_Status__c;
        objCase.Classification_Type__c = caseDetails.Classification_Type__c;
        objCase.Priority = caseDetails.Priority;
        objCase.Interacting_With_Type__c =caseDetails.Interacting_With_Type__c;
        objCase.Interacting_With__c =caseDetails.Interacting_With__c;
        objCase.Interacting_With_Name__c =caseDetails.Interacting_With_Name__c;
        objCase.NPI_ID__c =caseDetails.NPI_ID__c;
       	objCase.Tax_ID__c =caseDetails.Tax_ID__c;
        objCase.Escalation_Indicator__c =caseDetails.Escalation_Indicator__c;
        objCase.Escalation_Indicator_Date__c =caseDetails.Escalation_Indicator_Date__c; 
        objCase.G_A_Rights_Given__c =caseDetails.G_A_Rights_Given__c;
        objCase.Complaint__c =caseDetails.Complaint__c;
        objCase.Origin =caseDetails.Origin;
        objCase.Type =caseDetails.Type;
        objCase.Subtype__c =caseDetails.Subtype__c;
        objCase.Classification__c = caseDetails.Classification_Id__r.name;
        objCase.Intent_Id__c = caseDetails.Intent_Id__c;
        objCase.RecordTypeId = caseDetails.RecordTypeId;
        objCase.Verbal_Consent_Obtained__c = caseDetails.Verbal_Consent_Obtained__c;
        objCase.AutoRoute_Status__c = caseDetails.AutoRoute_Status__c;
        objCase.DCN__c = caseDetails.DCN__c;
        objCase.Call_Benefit_Category__c = caseDetails.Call_Benefit_Category__c;
        objCase.Pend_Key__c = caseDetails.Pend_Key__c;
        objCase.Status = caseDetails.Status;
        objCase.Election_Type_Code__c = caseDetails.Election_Type_Code__c;
        objCase.Doc_Type__c = caseDetails.Doc_Type__c;
        objCase.Member_Plan_Id__c = caseDetails.Member_Plan_Id__c;   
		objCase.Guided_Process_Flag__c = caseDetails.Guided_Process_Flag__c; 
        objCase.Case_Owner__c = caseDetails.Case_Owner__c;
		objCase.Topic__c = caseDetails.Topic__c;
		objCase.OwnerId = caseDetails.OwnerId;
        objCase.Owner_Queue__c = caseDetails.Owner_Queue__c;
        return caseDetails;
    }
}