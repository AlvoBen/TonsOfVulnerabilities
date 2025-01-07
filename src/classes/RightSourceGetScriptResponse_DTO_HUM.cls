/******************************************************************************************************************
Apex class Name    : RightSourceGetScriptResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for RightSource GetScript
Created On         : December 9 2016
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Vishal Verma                                 12/09/2016             Original Version -> REQ - 291073
*    1.1                Rajesh Keswani               66068           02/17/2017             Added NextFillMinDate and AutoRefillEligible fields  -> REQ - 301418
*    1.2                Melkisan Selvaraj							 03/04/2019             REQ - 387544 : Splunk error logging Issues
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceGetScriptResponse_DTO_HUM 
{
    public ScriptsResponse GetScriptsResponse;
    
    public class ScriptsResponse 
    {
        public String EnterprisePersonID = '';  
        public ScriptsDetail Scripts;
        public string StaleData = '';
		public string ErrorDescription = '';
    }
    
    public class ScriptsDetail 
    {
        public List<ScriptDetail> Script;
    }
    
    public class ScriptDetail 
    {
        public String ConsentStatus = '';   
        public String ConsentDateTime = ''; 
        public String ScriptKey = '';   
        public String ScriptStatus = '';
        public String IsAutoRefill = ''; 
        public String DrugType = '';    
        public ItemsDetail Items;
        public String Quantity = '';    
        public String RefillsRemaining = '';
        public String DaysSupply = '';  
        public String LastFillDate = '';    
        public String NextFillDate = ''; 
        public String NextFillMinDate = '';
        public String AutoRefillEligible = '';   
        public String ScriptExpirationDate = '';
        public PhysicianDetail physician;
    }
    
    public class ItemsDetail 
    {
        public List<ItemDetail> Item;
    }
    
    public class ItemDetail 
    {
        public String ItemType = '';    
        public String ItemLabel = '';   
    }
    
    public class PhysicianDetail 
    {
        public String FirstName = '';   
        public String LastName = '';    
        public PhonesDetail Phones;
    }
    
    public class PhonesDetail 
    {
        public List<PhoneDetail> Phone;
    }
    
    public class PhoneDetail 
    {
        public String PhoneType = '';   
        public String PhoneValue = '';
    }
}