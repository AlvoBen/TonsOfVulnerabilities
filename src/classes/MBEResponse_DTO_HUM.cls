/**************************************************************************** 
Apex Class Name :  MBEResponse_DTO_HUM   
Version: 1.0  
Created Date     : April 7, 2016
Function         : Wrapper class for MBE Service 
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh keswani          26120              04/07/2016                 Original Version
* Pradeepkumar Dani       26125              04/25/2016                 Added variables for Benefit summary section
* Pradeepkumar Dani       30140              05/15/2016                 Added variable for PolicyIndicator - Defect#225214
* Raja Sekhar Reddy M V   132082             09/22/2017                 REQ - 304970 - Adding SuperNetwork codes to Benefit Summary section
***************************************************************************************************/

Public Class MBEResponse_DTO_HUM
{ 
    Public String sOriginalEffectiveDate ='';
    Public String memberGenKey = '';
    Public MBEServiceResponse_H_HUM.EnrollmentInformation EnrollmentInformation = NULL;
    Public MBEServiceResponse_H_HUM.WaitingPeriodList WaitingPeriodList = NULL;
    Public MBEServiceResponse_H_HUM.IndicatorList IndicatorList = NULL;
    Public MBEServiceResponse_H_HUM.PolicyIndicatorList PolicyIndicatorList = NULL;
   
    //Benefit Summary Variables
    Public MBEServiceResponse_H_HUM.OutOfAreaList OutOfAreaList = NULL;
    Public MBEServiceResponse_H_HUM.NetworkList NetworkList= NULL;
    Public MBEServiceResponse_H_HUM.MemberPolicyList MemberPolicyList = NULL;
    Public String sCoverageType = '';
    

    //Cost share level variable
    Public MBEServiceResponse_H_HUM.CSRLevelHistory CSRLevelHistory = NULL;
}