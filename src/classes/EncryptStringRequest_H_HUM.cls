/*********************************************************************************************************************************************     
Apex Class Name  : EncryptStringRequest_H_HUM 
Created Date     : 11 Nov 2021
Function         : 
**Modification Log
* Developer Name          Code Review          Date                       Description
******************************************************************************************************************************************************
* Suraj Patil								   11/10/2021				  REQ - 2512130 T1PRJ0193862 - MF 01 - Ability to attach PFP email automatically to CRM case (Mbr Policy Page)
*****************************************************************************************************************************************************/
public with sharing class EncryptStringRequest_H_HUM 
    {
        public EncryptStringArray EncryptStringArray;
        public class EncryptStringArray
        {
            public List<string> Value ;
            public String Application = '';
        }
    }