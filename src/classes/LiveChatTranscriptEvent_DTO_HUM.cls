/*****************************************************************************************************
Apex Class Name :  LiveChatTranscriptEvent_DTO_HUM
Version: 1.0
Created Date 		: 04/27/2023
Function            : DTO to store LiveChatTranscriptEvent related to cases
Test Class  		: LiveChatTranscriptEvent_T_HUM

        Modification Log : 
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------
* Ravikanth Samudrala 					       05-18-2023          US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
*******************************************************************************************************/
public with sharing class LiveChatTranscriptEvent_DTO_HUM 
{
	public String StartTime{get; set;}
	public String Agent{get; set;}
	public String Type{get; set;}
	public String Detail{get; set;}
    public String HumanaChatTranscriptName{get; set;}
}