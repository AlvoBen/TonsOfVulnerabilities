/*****************************************************************************************************
Apex Class Name : CaseInteraction_DTO_HUM
Version           : 1.0 
Created Date      : 10/10/2017
Function          : DTO structure to store Interactions related to cases
Test Class         : CaseInteraction_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sumit Pharande                                10/10/2017                   Original Version
* Pradeepkumar Dani            149126           11/17/2017                     re-factoring
* Srivinas Damera              210924           04/05/2018                   CA Ticket 7363363 
* Ravikanth Samudrala                           05-18-2023                   US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
*******************************************************************************************************/
public with sharing class CaseInteraction_DTO_HUM 
{
    public String InteractionNumber{get; set;}
    public String InteractionOrigin{get; set;}
    public String InteractionWith{get; set;}
    public String InteractionWithType{get; set;}
    public String CreatedDate{get; set;}
    public String CreatedBy{get; set;}
    public String CreatedByQueue{get; set;}
	public String AssociatedtoCaseDate{get; set;}
    public String GenesysInteractionID{get; set;}
    public String CallRecordingLink{get; set;}
}