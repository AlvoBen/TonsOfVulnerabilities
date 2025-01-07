/****************************************************************************************
Apex Class Name  : PowerOfAttorney_C_HUM
Version          : 1.0 
Created Date     : AUGUST 14 2014
Function         : To populate Power of attorney in Account detail page
Modification Log :
 *  Developer                   Code Review            Date                 Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------           
 *  Gargey Sharma                  17703            08/14/2014              Original Version  
 *  Suresh Gottipati               240518           05/31/2018              REQ - 366200 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field
 *  Suraj Patil		               306028           10/30/2018              REQ - 350626 - Power of attorney display from designee service.
 *  Santhi Mandava                                       04/15/2019                 Replaced "Member_Id_Value__c" with Name
 *  Ajay Chakradhar                           	    05/05/2021	            REQ- 2184294 - Replacing constants with Consumer Management Constants
 *****************************************************************************************************************************************************************/

public with sharing class PowerOfAttorney_C_HUM 
{
    public Account oAcct = null;
    public list<Policy_Member__c> lstPM = new list<Policy_Member__c>();
    public string sCIMedReq = '';
    public string lstPOAData {get;set;}    
    Private String sPOA_FIELDLIST = 'First Name;Last Name;Phone;POA Type';
    Private String sPOA_VALIDMEMBERTYPES = 'Member-Id-Base;';
    Private String sPOA_VALIDPLATFORMS = 'LV;'; 
    Private String sPOA_CONSTRECORD = 'Record';
	 
    /**
    * This is the constructor for class
    * 
    * @param    
    * @return  	Object
    */
    public PowerOfAttorney_C_HUM(ApexPages.StandardController oStdController)
    {
        try
        {
            lstFieldSet = new list<String>(sPOA_FIELDLIST.split(';'));          
            oAcct = (Account)oStdController.getRecord();          
        }
        catch(Exception ex)
        {
             //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'PowerOfAttorney_C_HUM', 'PowerOfAttorney_C_HUM');
        }
    }
    
    
    public map<string, list<string>> mapServiceResponse{get; set;}
    public list<string> lstFieldSet{get; set;}
    public list<string> lstKeyTOCIMember{get; set;}
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
    /**
    * This method checks whether policy member is eligible for POA
    * 
    * @param    
    * @return  	Object
    */   
    public object checkConditionForCallout()
    {
        try
        {
            Set<string> setValidMemTypes = new set<string>(sPOA_VALIDMEMBERTYPES.split(';'));            
            Set<string> setValidPlatforms = new Set<string>(sPOA_VALIDPLATFORMS.split(';'));
                        
              
               	lstPM = [Select Id, Name, Policy__r.Source_Cust_Cov_Key__c, 
                   (Select  Name From Member_Ids__r Where Type__c IN : setValidMemTypes) 
                   From Policy_Member__c
                    Where Member__c =: oAcct.Id AND Policy__r.Platform__c IN : setValidPlatforms];
                   
              
                       
            for(Integer i = 0; i < lstPM.size() ; i++)
            {
               
                 if(lstPM.get(i).Member_Ids__r.isEmpty() || string.isBlank(lstPM.get(i).Member_Ids__r[0].Name) )
                {
                    lstPM.remove(i);
                    i--;
                }
                	
                            
            }
            if(lstPM != null && !(lstPM.isEmpty())) return callCIMedicare();
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PowerOfAttorney_C_HUM', 'checkConditionForCallout');
        }
        return null;
    }
    
    /**
    * This is a service callout method for CI Medicare
    * 
    * @param    
    * @return  	Object
    */  
    public Continuation callCIMedicare()
    {
        try
        {
            Continuation oCon = new Continuation(GlobalCommonConstants_HUM.SERVICETIMEOUT);
            PowerOfAttorney_H_HUM oPOAHelper = new PowerOfAttorney_H_HUM();
            sCIMedReq = oCon.addHttpRequest(oPOAHelper.createRequestCIMed(lstPM));
            oCon.continuationMethod = 'processCIResponse';
            return oCon;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PowerOfAttorney_C_HUM', 'callCIMedicare');
            return null;
        }
    }
    
    /**
    * This is a a callback method CI medicare service call
    * 
    * @param    
    * @return  	Object
    */ 
    public object processCIResponse()
    {
        try
        {
            HttpResponse response = Continuation.getResponse(sCIMedReq);
            if(response != null && response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)
            {
                PowerOfAttorney_H_HUM oPOAHelper = new PowerOfAttorney_H_HUM();
                mapServiceResponse = oPOAHelper.parseCIResponse(response.getBody());
                if(!mapServiceResponse.isEmpty())
                {
                    lstKeyTOCIMember = new list<string>();
                    lstKeyTOCIMember.addAll(mapServiceResponse.keySet());
                }
            }
            else
            {
                ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, Label.ClinicalServiceError_HUM));
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PowerOfAttorney_C_HUM', 'processCIResponse');
        }
        return null;
    }
	
	public void processPOA()
    {
        try
        {
			POA_DTO_HUM lstPOA_DTO = new POA_DTO_HUM();
			lstPOA_DTO = (POA_DTO_HUM)System.JSON.deserialize(lstPOAData, POA_DTO_HUM.class);
			mapServiceResponse = new map<string, list<string>>();
			Integer iCount = 0;
			list<POA_DTO_HUM.POAData> lstPOADatainside = lstPOA_DTO.POAData;
			if(lstPOADatainside.size() > 0)
            { 
				for(POA_DTO_HUM.POAData objPOAInfo: lstPOADatainside)
				{
					mapServiceResponse.put(sPOA_CONSTRECORD + iCount, new list<string>{objPOAInfo.FirstName,objPOAInfo.LastName,objPOAInfo.PhoneNumber,objPOAInfo.Type});
					iCount = iCount + 1;
				}
			}
			if(!mapServiceResponse.isEmpty())
			{
				lstKeyTOCIMember = new list<string>();
				lstKeyTOCIMember.addAll(mapServiceResponse.keySet());
			}
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PowerOfAttorney_C_HUM', 'processPOA');
        }
    }
}