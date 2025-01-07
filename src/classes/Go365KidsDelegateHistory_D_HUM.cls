/**
   @Author Gargey Sharma
   @name Go365KidsDelegateHistory_D_HUM 
   @CreateDate 02/06/2016
   @Description Data Layer class for Go365 Member Delegate History Section
   @Version 1.0
   @reference REQ - 290969
   @Test Class Go365KidsDelegateHistory_T_HUM 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         	   70895                  02/06/2016                     Original version
 * Gargey Sharma         	   70895                  02/20/2017                     Changes as per REQ - 301564
 * Gargey Sharma         	   73511                  03/07/2017                     Defect 262203.0001 - Effective Date format
********************************************************************************************************************************/
public with sharing class Go365KidsDelegateHistory_D_HUM 
{
	
	/**
    * This method is used to process Member Delegate Response
    * 
    * @param    
    * @return   Object
    */
    public static object processMemDelHisResponse(Object state)
	{
		HttpResponse response = Continuation.getResponse((string)state);
        MemberDelegatesServiceResponse_DTO_HUM oResponse = new MemberDelegatesServiceResponse_DTO_HUM();
        if(response != null && (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) && !response.getbody().contains(GLOBAL_CONSTANT_HUM.FAULTCODE_ALERTS_HUM))
		{
	        oResponse = (MemberDelegatesServiceResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), MemberDelegatesServiceResponse_DTO_HUM.class);
	        return parseResponseHis(oResponse);
		}
		return oResponse;
	}  
	
	/**
    * This method is used to parse member delegate history response 
    * 
    * @param    
    * @return   Object
    */
	public static object parseResponseHis(MemberDelegatesServiceResponse_DTO_HUM oResponse)
	{
		map<string, list<string>> serResp = new map<string, list<string>>();
		list<Account> lstAccMemDel = new list<Account>();
		if(oResponse.GetDelegateResponseDTO!=null) 
		{
			MemberDelegatesServiceResponse_DTO_HUM.GetDelegateResponseDTO getDelSer = new MemberDelegatesServiceResponse_DTO_HUM.GetDelegateResponseDTO();
	   		getDelSer = oResponse.GetDelegateResponseDTO;
	   		if(getDelSer.DelegatesCollection!=null)
	   		{
		   		MemberDelegatesServiceResponse_DTO_HUM.DelegatesCollection delCol = new MemberDelegatesServiceResponse_DTO_HUM.DelegatesCollection();
		   		delCol = getDelSer.DelegatesCollection;
		   		if(delCol.Delegate != null && !delCol.Delegate.isEmpty())
		   		{
			   		list<MemberDelegatesServiceResponse_DTO_HUM.Delegate> lstDel = new list<MemberDelegatesServiceResponse_DTO_HUM.Delegate>();
			   		lstDel = delCol.Delegate;
			   		for(MemberDelegatesServiceResponse_DTO_HUM.Delegate dele: lstDel)
			   		{
			   			if(serResp.Keyset().contains(dele.PersonId))
			   			{
			   				list<string> lstVal = new list<string>{dele.StartDate, dele.Status};
			   				serResp.get(dele.PersonId).addAll(lstVal);
			  			}
			  			else
						{
			   				serResp.put(dele.PersonId, new list<string>{dele.StartDate, dele.Status});
			   			}
			   		}
			   		lstAccMemDel = [Select Id, Individual_First_Name__c, Individual_Last_Name__c, BirthDate__c, Enterprise_Id__c From Account Where Enterprise_Id__c IN : serResp.Keyset()];
		   		}
	   		}
		}
		return createFinalDTOHis(serResp, lstAccMemDel);
	}
	
	/**
    * This method create final list to be displayed for Kids delegate History section
    * 
    * @param    map<string, list<string>>
    * @param    list<Account>
    * @return   list<Go365KidsDelegateHistory_DTO_HUM>
    */
	public static list<Go365KidsDelegateHistory_DTO_HUM> createFinalDTOHis(map<string, list<string>> serResp, list<Account> lstAccMemDel)
	{
		list<Go365KidsDelegateHistory_DTO_HUM> lstkidsdeldto = new list<Go365KidsDelegateHistory_DTO_HUM>();
		if(lstAccMemDel!=null && !lstAccMemDel.isEmpty())
		{
			for(Account accDel: lstAccMemDel)
		   	{
		   		if(serResp.containsKey(accDel.Enterprise_Id__c))
		   		{
			   		for(Integer i = 0; i < serResp.get(accDel.Enterprise_Id__c).size(); i = i + 2)
					{
						Go365KidsDelegateHistory_DTO_HUM kidsDele = new Go365KidsDelegateHistory_DTO_HUM();
					   	kidsDele.Delegate = accDel.Individual_First_Name__c + ' ' + accDel.Individual_Last_Name__c;
					   	if(!String.isBlank(serResp.get(accDel.Enterprise_Id__c)[i])) 
					   	{
					   		DateTime dEffectiveDate = Date.valueOf(serResp.get(accDel.Enterprise_Id__c)[i]);
							kidsDele.EffectiveDate = dEffectiveDate.formatGMT(GLOBAL_CONSTANT_HUM.POA_DATEFORMAT);
					   	}
					   	else kidsDele.EffectiveDate = ' ';
					   	if(!String.isBlank(serResp.get(accDel.Enterprise_Id__c)[i + 1])) kidsDele.Action = serResp.get(accDel.Enterprise_Id__c)[i + 1];
					   	else kidsDele.Action = ' ';
				   		lstkidsdeldto.add(kidsDele);
					}
		   		}
		  	}
		}
		return lstkidsdeldto.isEmpty() ? null : lstkidsdeldto;
	}  
}