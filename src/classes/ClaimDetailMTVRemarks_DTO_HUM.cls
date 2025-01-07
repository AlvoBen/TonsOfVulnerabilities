/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsMTVRemarks_DTO_HUM
Version          : 1.0
Created Date     : 03/31/2016
Function         : This class is wrapper class to store MTV Remarks data from service
Test Class       : ClaimDetailMTVRemarksDTO_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu                                03/31/2016                 Original Version
************************************************************************************************************************************************/

public with sharing class ClaimDetailMTVRemarks_DTO_HUM
{    
       public String sPagingInd {get; set;}
       public String sStartRemarkSequenceNumber {get; set;}
       public String sCreationDate {get; set;}
       public String sCreationTime {get; set;}
       public String sSurrogateId {get; set;}
       public String sRemarkEntityType {get; set;}
       public String sRemarkIdentifier {get; set;}
       public String sRemarkSequenceNumber {get; set;}
       public String sRemarkType {get; set;}
       public String sRemarkCategory {get; set;}
       public String sRemarkCreationTimestamp {get; set;}
       public String sRemarkText {get; set;}
       public String sRemarkSurrogateId {get; set;}
       public String sRemarkExtendedText {get; set;}        
        
}