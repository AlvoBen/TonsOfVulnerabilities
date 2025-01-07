/*******************************************************************************************************************************
Apex Class Name : InquiryDetAuditResponse_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains response Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
 ********************************************************************************************************************************/
public with sharing class InquiryDetAuditResponse_H_HUM 
{
    public List<DRAuditDetails> DRAuditDetails; 
    
    public class DRAuditDetails{
    public String ACTION_TYPE_DESC = '';
    public String Modified_On = '';
    public String CREATED_FIRST_NAME = '';
    public String CREATED_LAST_NAME= '';
    public String PREVIOUS_DESC= ''; 
    public String CURRENT_DESC ='';
    }
}