/*****************************************************************************************************
Apex Class Name :  LiveChatTranscript_DTO_HUM
Version: 1.0
Created Date 		: 10/12/2017
Function            : DTO to store compass related to cases
Test Class  		: LiveChatTranscript_T_HUM

        Modification Log : 
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------
* Sumit Pharande              148798             10/12/2017                   Original Version
* Pradeepkumar Dani		      148798			 11/17/2017				      re-factoring
* Ravikanth Samudrala 						     05-18-2023                   US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
*******************************************************************************************************/
public with sharing class LiveChatTranscript_DTO_HUM 
{
	public String Name{get; set;}
	public String StartTime{get; set;}
	public String EndTime{get; set;}
	public String Owner{get; set;}
	public String Status{get; set;}
    public String HumanaChatTranscript{get; set;}
    public String URL{get; set;}
}