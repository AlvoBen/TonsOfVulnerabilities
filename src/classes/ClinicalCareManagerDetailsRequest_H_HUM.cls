/**************************************************************************** 
Apex Class Name  : ClinicalCareManagerDetailsRequest_H_HUM 
Version          : 1.0  
Created Date     : July 6, 2016
Function         : Request helper for GetCareManagerDetails method in clinical service
Test Class       : ClinicalCareManagerDetailsRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri      34002              7/06/2016                   Original Version
* Shruthi Karanth            34002              7/28/2016                   Restructured as per changes in ICD
***************************************************************************************************/
public with sharing class ClinicalCareManagerDetailsRequest_H_HUM
{   
   public CareManagerRequest CareManagerRequest;
   public class CareManagerRequest 
   {
        public String PersonID = '';
        public String PersonIDType = '';
        public String DateOfBirth = '';
        public String CaseCategory = '';
    }
}