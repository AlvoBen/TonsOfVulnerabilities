/**************************************************************************** 
Apex Class Name  : ClinicalCareManagerDetailsResponse_H_HUM 
Version          : 1.0  
Created Date     : July 6, 2016
Function         : Response helper for GetCareManagerDetails method in clinical service
Test Class       : ClinicalCareManagerDetailsResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri      34002            7/06/2016                   Original Version
* Shruthi Karanth            34002            7/28/2016                   Restructured as per changes in ICD
***************************************************************************************************/
public with sharing class ClinicalCareManagerDetailsResponse_H_HUM
{   
    public CareManagerResponse CareManagerResponse;
    public class CareManagerResponse 
    {
        public CareManagerDetails CareManagerDetails;
        public String Message = '';
    }
    public class CareManagerDetails 
    {
        public list<CareManagerDetail> CareManagerDetail;
    }
    public class CareManagerDetail 
    {
        public String CaseId = '';   
        public QueueOwnerDetails QueueOwnerDetails;
    }
    public class QueueOwnerDetails 
    {
        public list<QueueOwnerDetail> QueueOwnerDetail;
    }
    public class QueueOwnerDetail 
    {
        public String Type = '';
        public String Name = '';
        public String UserId = '';
        public String DateAssigned = '';
    }
}