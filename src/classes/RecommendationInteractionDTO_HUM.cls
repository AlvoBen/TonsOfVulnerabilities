/*******************************************************************************************************************************
Apex Class Name : Public class RecommendationInteractionDTO_HUM 
Version         : 1.0
Created On      : 11/04/2020 
Function        : This class is used to get recommendation for interaction
Test Class      : 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Suraj patil                                        11/04/2020                 Original Version
*******************************************************************************************************************************************************************/
global with sharing class RecommendationInteractionDTO_HUM 
{
    Public String sAlertName {get; set;}
    Public String sActionTaken {get; set;}
    Public String sTermNotDeliveredReason {get; set;}
    Public String sActionDateTime {get; set;}
    Public String sAssociateID {get; set;}
    public list<String> lstQuestion {get; set;}
    public list<String> lstAnswer {get; set;}
	public boolean bQuestions{get;set;}
}