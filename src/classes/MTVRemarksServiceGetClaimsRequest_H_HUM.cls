/**************************************************************************** 
Apex Class Name  : MTVRemarksServiceGetClaimsRequest_H_HUM 
Version          : 1.0  
Created Date     : February 17, 2016
Function         : Request helper for GetClaimsMTVRemarks method in MTVRemarks Service
Test Class       : MTVRemarksServiceGetClaimsRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    30111                02/17/2016                    Original Version
* ChiranjeeviRao Ravuri    30351                03/02/2016                    Added extra fields as per new request structure
***************************************************************************************************/
public class MTVRemarksServiceGetClaimsRequest_H_HUM
{   
   public String ClaimNumber = ''; 
   public String CreationDate = ''; 
   public String CreationTime = ''; 
   public String SurrogateId = '';
   public String StartRemarkSequenceNumber = '' ;
}