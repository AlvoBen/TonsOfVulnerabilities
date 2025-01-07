/**************************************************************************** 
Apex Class Name  : PrimaryCare_DTO_HUM  
Version          : 1.0  
Created Date     : March 16, 2016
Function         : Wrapper class for storing PCP/PCD active and Future class details 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Choubey                             03/16/2016                 Original Version
* Rajesh Keswani                              05/10/2016                 adding variables for EPS Service
***************************************************************************************************/

public with sharing class PrimaryCare_DTO_HUM
 {
    public PcpPdpActive_DTO_HUM PcpPdpActiveDTO{get;set;}
    public List<PcpPdpHistory_DTO_HUM>  lstPcpPdpHistoryDTO{get;set;} 
    public string policyMemberObject{get;set;}
    public string refDate{get;set;} 
}