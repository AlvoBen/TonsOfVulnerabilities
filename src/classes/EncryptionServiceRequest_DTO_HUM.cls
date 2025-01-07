/******************************************************************************************************************
Apex Class Name :   EncryptionServiceRequest_DTO_HUM 
Version: 1.0
Function           : EncryptionService DTO Class
Created On         : July 10, 2018

Modification Log:  
*   Developer             Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*  Lakshmi Madduri                        	7/10/2018           Original Version -> REQ - 366254
*  Satyam Malviya							5/21/2020			DF 766 Corrected Application key that have been passes to RXConnect Link Fix
------------------------------------------------------------------------------------------------------------------*/
public with sharing class EncryptionServiceRequest_DTO_HUM 
{
    public Encrypt Encrypt;
    
    public class Encrypt 
    {
        public String Value = '';
		public String Application = '';
    }

}