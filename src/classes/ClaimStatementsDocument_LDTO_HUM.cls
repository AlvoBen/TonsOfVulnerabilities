/*******************************************************************************************************************************************************
Apex Class Name  : ClaimStatementsDocument_LDTO_HUM 
Version          : 1.0
Created Date     : 06/01/2022
Function         : This class is wrapper class to store data from service
Test Class       : ClaimStatementsDocumentDTO_LT_HUM
Modification Log :

* Developer Name           Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Prashant Moghe        		                   06/01/2022        		  This is a Wrapper class for ClaimsStatementsDocument Controller
************************************************************************************************************************************************/

public with sharing class ClaimStatementsDocument_LDTO_HUM 
{
public getDocumentResponse getDocumentResponse;

public class getDocumentResponse
{
public String getDocumentReturn='';
}

}