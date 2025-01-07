/*******************************************************************************************************************************************************
Apex Class Name  : ClaimStatementsDocument_DTO_HUM 
Version          : 1.0
Created Date     : 05/12/2016
Function         : This class is wrapper class to store data from service
Test Class       : ClaimStatementsDocumentDTO_T_HUM
Modification Log :

* Developer Name           Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Pallavi Ravishankar        27974                 05/12/2016        This is a Wrapper class for ClaimsStatementsDocument Controller
************************************************************************************************************************************************/

public with sharing class ClaimStatementsDocument_DTO_HUM 
{
public getDocumentResponse getDocumentResponse;

public class getDocumentResponse
{
public String getDocumentReturn='';
}

}