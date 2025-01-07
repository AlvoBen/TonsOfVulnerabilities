/*******************************************************************************************************************************************************
Apex Class Name  : MemberAncillaryRequest_H_HUM
Version          : 1.0
Created Date     : 05/31/2019
Function         : This class is wrapper class to store data from service Request for Ancillary service
Test Class       : MemberAncillaryRequest_H_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
*  Manikanta                  356372               05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
*  Manikanta                                       06/07/2019             REQ - 379006 Made code changes to align with the DP changes
************************************************************************************************************************************************/
Public with sharing class MemberAncillaryRequest_H_HUM
{  
   Public getMemberAncillary getMemberAncillary; 
   
   Public class getMemberAncillary
   { 
       Public includes includes;
       Public List<ancillaryRequest> ancillaryRequest;
   }
  
   Public class includes
   {
      Public IAncillaryEntity IAncillaryEntity;      
   }
    
   Public class IAncillaryEntity
   {
      Public Boolean ICoverageIdentifier;
      public Boolean ICostShareInformation;
      public Boolean ICommunityEngagementExemptCode;
      public Boolean ITypeOfAssistance;
      public Boolean IDeductibleAmount;
      Public List<String> IEntityTypeList;
      Public Boolean IHeadofHousehold;
      public Boolean ILTSS;
   } 
     
   Public class ICoverageIdentifier{}
   Public class ICostShareInformation{}
   Public class ICommunityEngagementExemptCode{}
   Public class ITypeOfAssistance{}
   Public class IDeductibleAmount{}
   Public class IHeadofHousehold{}
   Public class ILTSS{}
     
   Public class ancillaryRequest
   {
      Public Integer SequenceId;
      Public CoverageIdentifier CoverageIdentifier;
   }
    
   Public class CoverageIdentifier
   {
      Public String MemberIdBase = '';
      Public String DependentCode = '';
      Public String PlatformCode = '';
      Public String CoverageStartDate = '';
      Public String GroupId = '';
      Public String DivisionId = '';
      Public String ProductId = '';
   }
 }