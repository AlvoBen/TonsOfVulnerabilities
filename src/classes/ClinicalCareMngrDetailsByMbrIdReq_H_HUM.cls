/**************************************************************************** 
Apex Class Name  : ClinicalCareMngrDetailsByMbrIdReq_H_HUM 
Version          : 1.0  
Created Date     : July 26, 2018
Function         : Request helper for retrieveCareManagerDetailById method in clinical service
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Amol Patil		                         7/26/2018                    Original Version

***************************************************************************************************/
public with sharing class ClinicalCareMngrDetailsByMbrIdReq_H_HUM
{   
   public CareManagerbyMemberIdRequest CareManagerbyMemberIdRequest;
   public class CareManagerbyMemberIdRequest 
   {
        public String PersonID ='';
        public String PersonIDType = '';
        public String DateOfBirth = '';
     }
}