/**
   @Author Gargey Sharma
   @name Go365KidsDelegate_D_HUM 
   @CreateDate 12/21/2016
   @Description Data Layer class for Go365 Kids Delegate Section
   @Version 1.0
   @reference REQ - 290967
   @Test Class Go365KidsDelegate_T_HUM 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         		70895                 12/21/2016                     Original version
 * Gargey Sharma         		70895                 02/17/2017                     Changes as per REQ - 301565
 * Gargey Sharma         		73511                 03/06/2017                     Defect 262154.0001 - Start Date format 
********************************************************************************************************************************/
public with sharing class Go365KidsDelegate_D_HUM 
{
    /**
    * This method is used to process Member Delegate Response
    * 
    * @param    
    * @return   Object
    */
    public static object processMemDelResponse(Object state)
    {
        HttpResponse response = Continuation.getResponse((string)state);
        MemberDelegatesServiceResponse_DTO_HUM oResponse = new MemberDelegatesServiceResponse_DTO_HUM();
        if(response != null && (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) && !response.getbody().contains(GLOBAL_CONSTANT_HUM.FAULTCODE_ALERTS_HUM))
		{
	        oResponse = (MemberDelegatesServiceResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), MemberDelegatesServiceResponse_DTO_HUM.class);
	        return parseResponse(oResponse);
		}
		return oResponse;
    }
    
    /**
    * This method is used to parse response 
    * 
    * @param    MemberDelegatesServiceResponse_DTO_HUM
    * @return   Object
    */
    public static object parseResponse(MemberDelegatesServiceResponse_DTO_HUM oResponse)
    {
        map<string, string> serResp = new map<string, string>();
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
		                if(!serResp.containskey(dele.PersonId))
		                {
		                    serResp.put(dele.PersonId, dele.StartDate);
		                }
		            }
		            lstAccMemDel = [Select Id, Individual_First_Name__c, Individual_Last_Name__c, BirthDate__c, Enterprise_Id__c From Account Where Enterprise_Id__c IN : serResp.Keyset()];
		   		}
	   		}
        }
       return createFinalDTO(serResp, lstAccMemDel);
    }
    
    /**
    * This method create final list to be displayed.
    * 
    * @param    map<string, string>
    * @param    list<Account>
    * @return   list<Go356KidsDelegates_DTO_HUM>
    */
    public static list<Go356KidsDelegates_DTO_HUM> createFinalDTO(map<string, string> serResp, list<Account> lstAccMemDel)
    {
        list<Go356KidsDelegates_DTO_HUM> lstkidsdeldto = new list<Go356KidsDelegates_DTO_HUM>();
        if(lstAccMemDel!=null && !lstAccMemDel.isEmpty())
		{
	        for(Account accDel: lstAccMemDel)
	        {
	            Go356KidsDelegates_DTO_HUM kidsDele = new Go356KidsDelegates_DTO_HUM();
	            kidsDele.Name = accDel.Individual_First_Name__c + ' ' + accDel.Individual_Last_Name__c;
	            kidsDele.DateOfBirth = accDel.Birthdate__c;
	            if(serResp.containsKey(accDel.Enterprise_Id__c))
                   {
                       if(!String.isBlank(serResp.get(accDel.Enterprise_Id__c)))
                      {
                        DateTime dStartDate = Date.valueOf(serResp.get(accDel.Enterprise_Id__c));
                                      kidsDele.StartDate = dStartDate.formatGMT(GLOBAL_CONSTANT_HUM.POA_DATEFORMAT);
                      }
                   }

	            lstkidsdeldto.add(kidsDele);
	        }
		}
        return lstkidsdeldto.isEmpty() ? null : lstkidsdeldto;
    }  
}