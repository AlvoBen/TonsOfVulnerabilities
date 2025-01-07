/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_Request_H_HUM
Version          : 1.0
Created Date     : 03/05/2018 
Function         : Request Helper for getting claims send statement
Test Class       : Claim_Send_Statement_Request_H_T_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  Vijaya Koriprolu       280924               08/28/2018          Original Version
***************************************************************************************************/

public  with sharing class Claim_Send_Statement_Request_H_HUM
{
public StandardizeAddressRequest StandardizeAddressRequest;

/*
 * Value : StandardizeAddressRequest 
*/       

public Class StandardizeAddressRequest 
{

public String AddressLine1='';
public String AddressLine2 ='';
public String City='';
public String StateCode='';
public String ZipCode ='';

}


}