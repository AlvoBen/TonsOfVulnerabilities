/*******************************************************************************************************************************
Apex Class Name : InquiryNotesResponse_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains response Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
 ********************************************************************************************************************************/
public with sharing class InquiryNotesResponse_H_HUM
{
    public List<DRNotes> DRNotes;
    
    public class DRNotes
    {
        public String NOTE_DESC = '';
        public String Type = ''; 
    }
  }