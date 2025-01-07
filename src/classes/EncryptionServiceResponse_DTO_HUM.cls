/******************************************************************************************************************
Apex Class Name :   EncryptionServiceResponse_DTO_HUM 
Version: 1.0
Function           : Cover EncryptionService DTO Class
Created On         : July 10, 2018

Modification Log:  
*   Developer             Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*  Lakshmi Madduri                        	7/10/2018           Original Version -> REQ - 366254
------------------------------------------------------------------------------------------------------------------*/
public with sharing class EncryptionServiceResponse_DTO_HUM 
{
    public class EncryptResponse
    {
		public EncryptResult  EncryptResult ;
	}

	public EncryptResponse EncryptResponse;

	public class EncryptResult 
    {
		public String EncryptValue = '';
	}

}