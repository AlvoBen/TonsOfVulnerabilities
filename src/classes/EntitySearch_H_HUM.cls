/******************************************************************************************************************
Apex class Name    : EntitySearch_H_HUM
Version            : 1.0
Function           : This class is for common utility class to locate Entities for GetCaseInfo and GetInteractionInfo service methods.
Created On         : July 01 2019
Test Class         : GetInteractionInfoRest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Mohammed Noor                                 07/01/2019          Original Version
*   1.1            Mohammed Noor                                 07/26/2019          REQ - 403693 - Correct Unknown Provier TaxID logic
*   1.2            Moshitha Gunasekaran                          08/14/2019          REQ - 404608 - Support TaxID for Known and Unknown Agent
* ------------------------------------------------------------------------------------------------------------------- */
public with sharing class EntitySearch_H_HUM
{	
	private static map<string,set<string>> mapEntitySearchResults = new map<string,set<string>>();
	private static boolean bSearchUnknown = false;	
	private static final string RECORDTYPE_MEMBER = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM;
   	private static final string RECORDTYPE_AGENT = GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER_HUM;
   	private static final string MEMBERIDBASE= GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM;
   	private static final string MemberID =GLOBAL_CONSTANT_HUM.MEMBERID_HUM;   	   	   	   	
   	private static map<string,set<string>> mapIntAboutTaxID = new map<string,set<string>>();
   	private static map<string,set<string>> mapIntWithTaxID = new map<string,set<string>>();
   	private static map<string, map<string, set<string>>> mapMemberDependentCode= new map<string, map<string, set<string>>>();
   	private static map<string,set<string>> mapIntAboutUnknownCriteria = new map<string,set<string>>();
   	private static map<string,set<string>> mapIntWithUnknownCriteria = new map<string,set<string>>();
   	private static map<string,set<string>> mapIntAboutUnknownProviderCriteria = new map<string,set<string>>();
   	private static map<string,set<string>> mapIntWithUnknownProviderCriteria = new map<string,set<string>>();
   	private static map<string,set<string>> mapIntAboutUnknownAgentCriteria = new map<string,set<string>>();
   	private static map<string,set<string>> mapIntWithUnknownAgentCriteria = new map<string,set<string>>();
   	private static map<string,set<string>> mapAcctIDValuesbyType = new map<string,set<string>>();
   	private static map<string, map<string, string>> mapGroupPlatform = new map<string, map<string, string>>();
   	private static map<string,set<ConsumerID__c>> mapAcctWithConsumer = new map<string,set<ConsumerID__c>>();
   	private static map<string,string> mapRecordType = new map<string,string>
                                                {
                                                    RECORDTYPE_MEMBER =>RECORDTYPE_MEMBER,
                                                    MemberID=>MEMBERIDBASE
                                                };
        			
	/*
    * performEntitySearch
    * <p>
    * This method is called to perform the Entity Search by GetCaseInfo and GetInteractionInfo Webservice Methods.
    *
    * @param           lstIntAboutCriteria - list of Interacting About EntityIdentifierDTO from Request
    * @param           lstIntWithCriteria - list of Interacting With EntityIdentifierDTO from Request
    * @param		   bSearchUnknownFlag - Flag for Unknown Entity search.
    * @return          map of located Account Ids for Interacting About and Interacting With.                  
    */
	public static map<string,set<string>> performEntitySearch(list<EntityIdentifierDTO> lstIntAboutCriteria, list<EntityIdentifierDTO> lstIntWithCriteria, boolean bSearchUnknownFlag)
	{			
		bSearchUnknown = bSearchUnknownFlag;
		map<string, map<string,set<string>>> mapIntAboutCriteria = new map<string, map<string,set<string>>>();
        map<string, map<string,set<string>>> mapIntWithCriteria = new map<string, map<string,set<string>>>();
        try
        {
			if(lstIntAboutCriteria!=null && !lstIntAboutCriteria.isEmpty())		
			{
				if(lstIntAboutCriteria.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
	            {
	                list<EntityIdentifierDTO> interactionAboutValues=new list<EntityIdentifierDTO>();
	                for(Integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
	                {
	                    interactionAboutValues.add(lstIntAboutCriteria[i]);
	                }                    
	                mapIntAboutCriteria.putAll(createMapIntCriteria(interactionAboutValues, GLOBAL_CONSTANT_HUM.INTABOUT));                    
	            }
	            else    
	            {
	                mapIntAboutCriteria.putAll(createMapIntCriteria(lstIntAboutCriteria, GLOBAL_CONSTANT_HUM.INTABOUT));
	            }
			}
			if(lstIntWithCriteria!=null && !lstIntWithCriteria.isEmpty())
			{
				if(lstIntWithCriteria.size() > GLOBAL_CONSTANT_HUM.MAXLIMIT)
				{
					list<EntityIdentifierDTO> interactionWithValues=new list<EntityIdentifierDTO>();
					for(Integer i=0; i<GLOBAL_CONSTANT_HUM.MAXLIMIT; i++)
					{
						interactionWithValues.add(lstIntWithCriteria[i]);
					}
					mapIntWithCriteria.putAll(createMapIntCriteria(interactionWithValues, GLOBAL_CONSTANT_HUM.INTWITH));	
				}
				else
				{
					mapIntWithCriteria.putAll(createMapIntCriteria(lstIntWithCriteria, GLOBAL_CONSTANT_HUM.INTWITH));
				}
			}
			
			if((mapIntAboutCriteria != null && !mapIntAboutCriteria.isEmpty()) || (mapIntWithCriteria!= null && !mapIntWithCriteria.isEmpty()))
			{
				mapEntitySearchResults.putAll(locateEntity(mapIntAboutCriteria, mapIntWithCriteria));	
			}			
		}
		catch(Exception ex)
        {
            throw ex;  //exception needs to be bubbled up to the calling method to logged and sent in the service response
        }
		return mapEntitySearchResults;
	}
	
	/*
    * createMapIntCriteria
    * <p>
    * This method is used to populate the EntityIdentifierDTO from input request into a map based on the Entity Types, Id types and Id Values. 
    *
    * @param           lstInteractionValues - list of EntityIdentifierDTO from Request    
    * @param		   sIntType - string value of either Interacting_About or Interacting_With
    * @return          map of Entity type, map of Id type by Id values.                  
    */
	@TestVisible
	private static map<string, map<string,set<string>>> createMapIntCriteria(list<EntityIdentifierDTO> lstInteractionValues, string sIntType)
	{
		map<string, map<string,set<string>>> mapInteractionCriteria = new map<string, map<string,set<string>>>();
		map<string, set<string>> mapMemberIDValues = new map<string, set<string>>();
        map<string,set<string>> mapDependentCode = new map<string, set<string>>();
        map<string, set<string>> mapAgentIDValues = new map<string, set<string>>();
        map<string, set<string>> mapProviderIDValues = new map<string, set<string>>();
        map<string, set<string>> mapGroupIDValues = new map<string, set<string>>();
        map<string, string> mapGrpPlatformCode = new map<string, string>();
                       
        for(EntityIdentifierDTO EntityIDentifier : lstInteractionValues)
        {  
            if(EntityIDentifier != null && String.isNotBlank(EntityIDentifier.EntityType) && String.isNotBlank(EntityIDentifier.IdValue) && String.isNotBlank(EntityIDentifier.IdValueType))                
            { 
                if(EntityIDentifier.EntityType.equalsIgnoreCase(RECORDTYPE_MEMBER)) //For Member
                {                                                                                                                                                                              
                    //Checking for IdValueType as'Member-Id-Base'
                    if(EntityIDentifier.IdValueType.equals(MemberID))
                    {
                        if((mapMemberIDValues.containsKey(mapRecordType.get(EntityIDentifier.IDValueType))))
                        {
                            mapMemberIDValues.get(mapRecordType.get(EntityIDentifier.IDValueType)).add(EntityIDentifier.IdValue.toUppercase());                                                                                                                                                                         
                        }
                        else 
                        {
                            mapMemberIDValues.put(mapRecordType.get(EntityIDentifier.IDValueType),new set<string>{EntityIDentifier.IdValue.toUppercase()});                                            
                        }                        
                        if(String.isNotBlank(EntityIDentifier.MemberIdDependentCode))
                        {
                            if(mapDependentCode.containsKey(EntityIDentifier.IdValue.toUppercase()))
                            {
                                mapDependentCode.get(EntityIDentifier.IdValue.toUppercase()).add(EntityIDentifier.MemberIdDependentCode);
                            }
                            else
                            {
                                mapDependentCode.put(EntityIDentifier.IdValue.toUppercase(),new set<string>{EntityIDentifier.MemberIdDependentCode});
                            }
                        }
                    }
                    else
                    {
                        
                        if((mapMemberIDValues.containsKey(EntityIDentifier.IdValueType)))
                        {                            
                            mapMemberIDValues.get(EntityIDentifier.IdValueType).add(EntityIDentifier.IdValue.toUppercase());                                                                                  
                        }
                        else 
                        {
                            mapMemberIDValues.put(EntityIDentifier.IdValueType,new set<string>{EntityIDentifier.IdValue.toUppercase()});                          
                        }                        
                    }                                                              
               }
               if(EntityIDentifier.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM)) //For Agent 
               {
	               	  if(EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID) || EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_TAXID))
	               	  {                    
	                    if((mapAgentIDValues.containsKey(EntityIDentifier.IdValueType)))
	                    {                            
	                        mapAgentIDValues.get(EntityIDentifier.IdValueType).add(EntityIDentifier.IdValue.toUppercase());                                                                                  
	                    }
	                    else 
	                    {
	                        mapAgentIDValues.put(EntityIDentifier.IdValueType,new set<string>{EntityIDentifier.IdValue.toUppercase()});                                                     
	                    }   
	               	  }                    
               }
               if(EntityIDentifier.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM)) //For Provider
               {                    
                    if(EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID) || EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID) || EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM))
                    {                       
                        if((mapProviderIDValues.containsKey(EntityIDentifier.IdValueType)))
                        {                            
                            mapProviderIDValues.get(EntityIDentifier.IdValueType).add(EntityIDentifier.IdValue.toUppercase());                                                                                  
                        }
                        else 
                        {
                            mapProviderIDValues.put(EntityIDentifier.IdValueType,new set<string>{EntityIDentifier.IdValue.toUppercase()});                                                     
                        }                       
                    }                       
               }
               if(EntityIDentifier.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM)) //For Group
               {                    
                    if(EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM) || EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY))
                    {                       
                        if((mapGroupIDValues.containsKey(EntityIDentifier.IdValueType)))
                        {                            
                            mapGroupIDValues.get(EntityIDentifier.IdValueType).add(EntityIDentifier.IdValue.toUppercase());                                                                                  
                        }
                        else 
                        {
                            mapGroupIDValues.put(EntityIDentifier.IdValueType,new set<string>{EntityIDentifier.IdValue.toUppercase()});                                                     
                        }                       
                    }
                    if(EntityIDentifier.IdValueType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM) && String.isNotBlank(EntityIDentifier.Platform) && mapGrpPlatformCode!=null)
                    {
                        mapGrpPlatformCode.put(EntityIDentifier.IdValue.toUppercase(), EntityIDentifier.Platform);                        
                    }       
               }               
               
            }
        }
        if(mapMemberIDValues!=null && !mapMemberIDValues.isEmpty())
        {
            mapInteractionCriteria.put(RECORDTYPE_MEMBER, mapMemberIDValues);                       
        }
        if(mapDependentCode!=null && !mapDependentCode.isEmpty())
        {
            mapMemberDependentCode.put(sIntType, mapDependentCode);
        }
        if(mapAgentIDValues!=null && !mapAgentIDValues.isEmpty())
        {
            mapInteractionCriteria.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM, mapAgentIDValues);                     
        }
        if(mapProviderIDValues!=null && !mapProviderIDValues.isEmpty())
        {
            mapInteractionCriteria.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM, mapProviderIDValues);                       
        }
        if(mapGroupIDValues!=null && !mapGroupIDValues.isEmpty())
        {
            mapInteractionCriteria.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM, mapGroupIDValues);                     
        }
        if(mapGrpPlatformCode!=null && !mapGrpPlatformCode.isEmpty())
        {
            mapGroupPlatform.put(sIntType, mapGrpPlatformCode); 
        }
		return mapInteractionCriteria;
	}
	
	/*
    * locateEntity
    * <p>
    * This method is used to search the Entities and locate the corresponding Account Ids  
    *
    * @param           mapIntAboutCriteria - map of Interacting About Entity criteria values    
    * @param		   mapIntWithCriteria - map of Interacting WIth Entity criteria values
    * @return          map of Interacting type and set of Account Ids                  
    */
    @TestVisible
	private static map<string,set<string>> locateEntity(map<string, map<string,set<string>>> mapIntAboutCriteria, map<string, map<string,set<string>>> mapIntWithCriteria)
    {                
        try
        {                                                                                                                                             
            if((mapIntAboutCriteria != null && !mapIntAboutCriteria.isEmpty()) || (mapIntWithCriteria!= null && !mapIntWithCriteria.isEmpty()))
            {
                map<string, set<string>> mapMemberIntAbt = new map<string, set<string>>();
                map<string, set<string>> mapMemberIntWith = new map<string, set<string>>();                                
                if(mapIntAboutCriteria != null && !mapIntAboutCriteria.isEmpty())
                {                   
                    if(mapIntAboutCriteria.containsKey(RECORDTYPE_MEMBER))
                    {
                        mapMemberIntAbt.putAll(mapIntAboutCriteria.get(RECORDTYPE_MEMBER));
                        if(mapMemberIntAbt.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID)) mapMemberIntAbt.remove(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID);  //remove EnterpriseID before querying on MemberID object
                    }
                    if(mapIntAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM) && mapIntAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM))
                    {                   
                        mapIntAboutTaxID.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM, mapIntAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).get(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM));                       
                    }
                    if(mapIntAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapIntAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_TAXID))
                    {                   
                        mapIntAboutTaxID.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM, mapIntAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_TAXID));                       
                    }
                }
                if(mapIntWithCriteria!= null && !mapIntWithCriteria.isEmpty())
                {                       
                    if(mapIntWithCriteria.containsKey(RECORDTYPE_MEMBER))
                    {
                        mapMemberIntWith.putAll(mapIntWithCriteria.get(RECORDTYPE_MEMBER));
                        if(mapMemberIntWith.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID)) mapMemberIntWith.remove(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID);   //remove EnterpriseID before querying on MemberID object
                    }
                    if(mapIntWithCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM) && mapIntWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM))
                    {                   
                        mapIntWithTaxID.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM,mapIntWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).get(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM));
                    }
                    if(mapIntWithCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapIntWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_TAXID))
                    {                   
                        mapIntWithTaxID.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM,mapIntWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_TAXID));
                    }           
                }    
                if((mapMemberIntAbt!=null && !mapMemberIntAbt.isEmpty()) || (mapMemberIntWith!=null && !mapMemberIntWith.isEmpty()))
                {                                                               
                    locateMembers(mapMemberIntAbt, mapMemberIntWith);
                }                
                
                if((bSearchUnknown && ((mapIntAboutUnknownCriteria!=null && !mapIntAboutUnknownCriteria.isEmpty())||(mapIntWithUnknownCriteria!=null && !mapIntWithUnknownCriteria.isEmpty())))
                    ||(mapIntAboutTaxID!=null && !mapIntAboutTaxID.isEmpty()) ||(mapIntWithTaxID!=null && !mapIntWithTaxID.isEmpty()))
                {
                    locateConsumerIDs();
                }
                locateAccountRecords(mapIntAboutCriteria,mapIntWithCriteria);                                
            }                                                            
        }
        catch(Exception ex)
        {       
        	HUMExceptionHelper.logErrors(ex,'EntitySearch_H_HUM','performEntirySearch');	                 
        }
        return mapEntitySearchResults;
    }
    
    /*
    * locateMembers
    * <p>
    * This method is used to locate Members based on input Interacting with and Interacting about details 
    *  
    * @param    mapIntAboutCriteria - map of Interacting About Entity criteria values for Entity type Member 
    * @param	mapIntWithCriteria - map of Interacting With Entity criteria values for Entity type Member  
    * @return   Void                  
    */
    @TestVisible
    private static void locateMembers(map<string,set<string>> mapIntAboutCriteria, map<string,set<string>> mapIntWithCriteria)
    {
        list<Member_Id__c> lstMemberRecs = new list<Member_Id__c>();                     
        lstMemberRecs.addAll(getMemberRecords(mapIntAboutCriteria,mapIntWithCriteria));        
        if(lstMemberRecs!=null && !lstMemberRecs.isEmpty())
        {
            getMatchingRecords(lstMemberRecs, mapIntAboutCriteria, mapIntWithCriteria);         
        }
        else if(bSearchUnknown && (lstMemberRecs == null || (lstMemberRecs!=null && lstMemberRecs.isEmpty()))) //If no Known Member found and SearchUnknown is True         
        {                                                                                           
            for(string memType : mapIntAboutCriteria.keySet())
            {
                mapIntAboutUnknownCriteria.put(memType, mapIntAboutCriteria.get(memType).clone());
            }
            for(string memType : mapIntWithCriteria.keySet())
            {
                mapIntWithUnknownCriteria.put(memType, mapIntWithCriteria.get(memType).clone());
            }                
        }                               
    }
    
    /*
    * getMemberRecords
    * <p>
    * This method is used to retrieve the Member_ID__c records 
    *  
    * @param    mapIntAboutCriteria - map of Interacting About Entity criteria values for Entity type Member 
    * @param	mapIntWithCriteria - map of Interacting With Entity criteria values for Entity type Member  
    * @return   list<Member_Id__c> - list of Member ID records.                  
    */
    @TestVisible
    private static list<Member_Id__c> getMemberRecords(map<string,set<string>> mapIntAboutCriteria, map<string,set<string>> mapIntWithCriteria)
    {               
        list<Member_Id__c> lstMemberRecs = new list<Member_Id__c>();        
        set<string> setIdValues = new set<string>();                 
        set<string> setIDTypes = new set<string>();
        list<set<string>> lstIntIDValuesList= new list<set<string>>();                                              
        
        string sSearchKey = '';
        string sMemberQuery ='';         
                        
        if(mapIntAboutCriteria!=null && !mapIntAboutCriteria.isEmpty())
        {            
            for(string sType : mapIntAboutCriteria.keySet())
            {
                if(GLOBAL_CONSTANT_HUM.setMemberIDTypes.contains(sType))    setIDTypes.add(sType);
                lstIntIDValuesList.add(mapIntAboutCriteria.get(sType));  //add all IntAbout ID values.                  
            }
        }
        if(mapIntWithCriteria!=null && !mapIntWithCriteria.isEmpty())
        {            
            for(string sType : mapIntWithCriteria.keySet())
            {
                if(GLOBAL_CONSTANT_HUM.setMemberIDTypes.contains(sType))    setIDTypes.add(sType);
                lstIntIDValuesList.add(mapIntWithCriteria.get(sType));   //add all IntWith ID values                  
            }
        }        
        for(set<string> sIdVal : lstIntIDValuesList)
        {
            setIdValues.addAll(sIdVal);
            for(string sVal : sIdVal)
            {
                sSearchKey = sSearchKey + ' OR ' + sVal +'*' ;
            }
        }
        If(String.isNotBlank(sSearchKey)) sSearchKey = sSearchKey.substring(4,sSearchKey.length());
        
        sMemberQuery = 'FIND \'' + sSearchKey  + '\' IN NAME FIELDS RETURNING Member_Id__c(ID, name, Type__c, Policy_Member__r.Member__c,Member_Dependent_Code__c, Policy_Member__r.Member__r.name ' 
                          +   ' WHERE Type__c IN :  setIDTypes )';
        
        
        if(setIdValues!=null && setIDTypes!= null && !setIdValues.isEmpty() && !setIDTypes.isEmpty())
        {                
            //Retrieve the Member ID records                               
            list<list<Member_Id__c>>searchList = search.query(sMemberQuery);
            list<Member_Id__c> tempMemberList = searchList[0];
            If(tempMemberList != Null && !tempMemberList.isEmpty())
            {
                For(Member_Id__c obj : tempMemberList)
                {
                    If(setIdValues.contains(obj.Name))
                    {
                        lstMemberRecs.Add(obj);
                    }
                }
            }
        }        
        return lstMemberRecs;                   
    }
    
    /*
    * getMatchingRecords
    * <p>
    * This method is used to match the Member ID records retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    lstMemberRecs - list of Member ID Records retrieved by SOQL 
    * @param    mapIntAboutCriteria - map of Interacting About Entity criteria values for Entity type Member 
    * @param	mapIntWithCriteria - map of Interacting With Entity criteria values for Entity type Member
    * @return   Void                  
    */
    @TestVisible
    private static void getMatchingRecords(list<Member_Id__c >lstMemberRecs, map<string,set<string>> mapIntAboutCriteria, map<string,set<string>> mapIntWithCriteria)
    {               
        set<string> setIntAbtAccts = new set<string>();
        set<string> setIntWithAccts = new set<string>();
        if(mapIntAboutCriteria != null & !mapIntAboutCriteria.isEmpty())
        {
            for(string memType:mapIntAboutCriteria.keySet())
            {
                if(GLOBAL_CONSTANT_HUM.setMemberIDTypes.contains(memType))
                    mapIntAboutUnknownCriteria.put(memType, mapIntAboutCriteria.get(memType).clone());
            }
        }
        
        if(mapIntWithCriteria != null & !mapIntWithCriteria.isEmpty())
        {
            for(string memType:mapIntWithCriteria.keySet())
            {
                if(GLOBAL_CONSTANT_HUM.setMemberIDTypes.contains(memType))
                    mapIntWithUnknownCriteria.put(memType, mapIntWithCriteria.get(memType).clone());
            }
        }
        
        if(lstMemberRecs!=NULL && !lstMemberRecs.isEmpty())  
        {
            map<string, set<string>> mapIntAbtDcode = new map<string, set<string>>();
            map<string, set<string>> mapIntWithDcode = new map<string, set<string>>();
            set<string> setIntAbtVal = new set<string>();
            set<string> setIntWithVal = new set<string>();
            if(mapMemberDependentCode!=null && mapMemberDependentCode.containsKey(GLOBAL_CONSTANT_HUM.INTABOUT))   mapIntAbtDcode = mapMemberDependentCode.get(GLOBAL_CONSTANT_HUM.INTABOUT);
            if(mapMemberDependentCode!=null && mapMemberDependentCode.containsKey(GLOBAL_CONSTANT_HUM.INTWITH))    mapIntWithDcode = mapMemberDependentCode.get(GLOBAL_CONSTANT_HUM.INTWITH);
            set<string> setIntAbtDVal = new set<string>();
            set<string> setIntWithDVal = new set<string>();
            
            for(Member_ID__c member:lstMemberRecs)
            {
               setIntAbtVal = new set<string>();
               setIntWithVal = new set<string>();                         
               if(mapIntAboutUnknownCriteria.containsKey(member.Type__c))  setIntAbtVal = mapIntAboutUnknownCriteria.get(member.Type__c);            
               if(mapIntWithUnknownCriteria.containsKey(member.Type__c))   setIntWithVal = mapIntWithUnknownCriteria.get(member.Type__c);            
                           
               if(member.Type__c.equals(mapRecordType.get(MemberID)))  //For Member-Id-Base
               {  
                    setIntAbtDVal = new set<string>();
                    setIntWithDVal = new set<string>();    
                    
                    if(mapIntAbtDcode.containsKey(member.Name))  setIntAbtDVal = mapIntAbtDcode.get(member.Name);            
                    if(mapIntWithDcode.containsKey(member.Name))   setIntWithDVal = mapIntWithDcode.get(member.Name);
                                                      
                    if(setIntAbtVal.contains(member.Name) && mapIntAbtDcode!=null && mapIntAbtDcode.containsKey(member.Name)
                       && mapIntAbtDcode.get(member.Name).contains(member.Member_Dependent_Code__c))
                    {
                        setIntAbtAccts.add(member.Policy_Member__r.Member__c);
                        setIntAbtDVal.remove(member.Member_Dependent_Code__c);
                        if(setIntAbtDVal.isEmpty())
                        {
                            setIntAbtVal.remove(member.Name);
                        }
                    }
                    if(setIntWithVal.contains(member.Name) && mapIntWithDcode!=null && mapIntWithDcode.containsKey(member.Name)
                      && mapIntWithDcode.get(member.Name).contains(member.Member_Dependent_Code__c))
                    {
                        setIntWithAccts.add(member.Policy_Member__r.Member__c);
                        setIntWithDVal.remove(member.Member_Dependent_Code__c);
                        if(setIntWithDVal.isEmpty())
                        {
                            setIntWithVal.remove(member.Name);
                        }
                    }
               }
               else
               {
                   if(setIntAbtVal.contains(member.Name))
                   {
                        setIntAbtAccts.add(member.Policy_Member__r.Member__c);
                        setIntAbtVal.remove(member.Name);
                   }
                   if(setIntWithVal.contains(member.Name))
                   {
                        setIntWithAccts.add(member.Policy_Member__r.Member__c);
                        setIntWithVal.remove(member.Name);
                   }
                }
                if(!setIntAbtVal.isEmpty())
                {   
                    mapIntAboutUnknownCriteria.put(member.Type__c, setIntAbtVal);
                }
                else
                {
                    mapIntAboutUnknownCriteria.remove(member.Type__c);
                }
                if(!setIntWithVal.isEmpty())
                {
                    mapIntWithUnknownCriteria.put(member.Type__c, setIntWithVal);
                }
                else
                {
                    mapIntWithUnknownCriteria.remove(member.Type__c);
                }
           }
           if((setIntAbtAccts!= null && !setIntAbtAccts.isEmpty()) || (setIntWithAccts!= null && !setIntWithAccts.isEmpty()))
           {
                populateResultsInMap(setIntAbtAccts, setIntWithAccts);
           }
       }                                    
    }
    
    /*
    * locateConsumerIDs
    * <p>
    * This method is used to get the Consumer_ID__c records based on input values.
    *  
    * @param    None
    * @return   Void
    */
    @TestVisible
    private static void locateConsumerIDs()
    {
        set<string> setConsumerIDValues = new set<string>();
        set<string> setConsumerIDTypes = new set<string>();
        list<set<string>> lstIntIDValues = new list<set<string>>();
        map<string, set<string>> mapIntAboutValues = new map<string,set<string>>();
        map<string, set<string>> mapIntWithValues = new map<string,set<string>>();
        
        if(mapIntAboutUnknownCriteria!=null && !mapIntAboutUnknownCriteria.isEmpty())
        { 
            lstIntIDValues.addAll(mapIntAboutUnknownCriteria.values());  //add all IntAbout ID values.
            setConsumerIDTypes.addAll(mapIntAboutUnknownCriteria.keySet());        
            mapIntAboutValues.putAll(mapIntAboutUnknownCriteria);   
        }
        if(mapIntWithUnknownCriteria!=null && !mapIntWithUnknownCriteria.isEmpty())
        {
           lstIntIDValues.addAll(mapIntWithUnknownCriteria.values());   //add all IntWith ID values
           setConsumerIDTypes.addAll(mapIntWithUnknownCriteria.keySet());
           mapIntWithValues.putAll(mapIntWithUnknownCriteria);
        }
        if(mapIntAboutTaxID!= null && !mapIntAboutTaxID.isEmpty())
        {   
            setConsumerIDTypes.add(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID);                   
            if(mapIntAboutTaxID.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
            {
            	setConsumerIDValues.addAll(mapIntAboutTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM));
                mapIntAboutValues.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID, mapIntAboutTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM));
            } 
            if(mapIntAboutTaxID.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
            {
            	setConsumerIDValues.addAll(mapIntAboutTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM));
                mapIntAboutValues.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID, mapIntAboutTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM));
            }
        }
        if(mapIntWithTaxID!= null && !mapIntWithTaxID.isEmpty())
        {
            setConsumerIDTypes.add(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID);
            if(mapIntWithTaxID.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
            {
            	setConsumerIDValues.addAll(mapIntWithTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM));
                mapIntWithValues.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID, mapIntWithTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM));
            }
            if(mapIntWithTaxID.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
            {
            	setConsumerIDValues.addAll(mapIntWithTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM));
                mapIntWithValues.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID, mapIntWithTaxID.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM));
            }
        }           
        for(set<string> sIdVal : lstIntIDValues)
        {
            setConsumerIDValues.addAll(sIdVal);
        }
        if(setConsumerIDValues!=null && !setConsumerIDValues.isEmpty() && setConsumerIDTypes!=null && !setConsumerIDTypes.isEmpty())
        {
            if(setConsumerIDTypes.contains(mapRecordType.get(MemberID)))
            {
                setConsumerIDTypes.remove(mapRecordType.get(MemberID));
                setConsumerIDTypes.add(GLOBAL_CONSTANT_HUM.CASESERVICE_HUMANAID_HUM);
            }                       
            list<ConsumerID__c> lstConsumerIDRecs = new list<ConsumerID__c>();
            lstConsumerIDRecs.addAll(getConsumerIDRecs(setConsumerIDValues, setConsumerIDTypes));
            if(lstConsumerIDRecs!=null && !lstConsumerIDRecs.isEmpty())
            {
                getMatchingConsumerIDRecs(lstConsumerIDRecs, mapIntAboutValues, mapIntWithValues);      
            }
        }
    }
    
    /*
    * getConsumerIDRecs
    * <p>
    * This method is used to query the Consumer_ID__c table for locating Unknown Members and Provider by TaxID. 
    *  
    * @param    setUnknwnIDValues - set of Unknown ID values
    * @param    setUnknwnIDTypes - set of Unknown ID types 
    * @return   list<ConsumerID__c>
    */
    @TestVisible
    private static list<ConsumerID__c> getConsumerIDRecs(set<string> setUnknwnIDValues, set<string> setUnknwnIDTypes)
    {
        list<ConsumerID__c> lstConsumerIDRecs = new list<ConsumerID__c>();      
        string sConsumerQuery = 'SELECT Id, Name, Account__c,Account__r.Recordtype.Name, ID_Type__c,Consumer_ID__c FROM ConsumerID__c';
        if(setUnknwnIDValues!=null && !setUnknwnIDValues.isEmpty() && setUnknwnIDTypes!=null && !setUnknwnIDTypes.isEmpty())
        {
            sConsumerQuery += ' WHERE Consumer_ID__c IN :setUnknwnIDValues';
            sConsumerQuery += ' AND ID_Type__c IN :setUnknwnIDTypes'; 
        }                       
        if(String.isNotBlank(sConsumerQuery))
        {
            lstConsumerIDRecs = Database.Query(sConsumerQuery);         
        }       
        return lstConsumerIDRecs;
    }
    
    /*
    * getMatchingConsumerIDRecs
    * <p>
    * This method is used to match the Consumer IDs retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    lstConsumerIDRecs - list of Consumer ID Recs retrieved by SOQL
    * @param    mapIntAboutValues - map of Interacting About Entity criteria values from Input
    * @param    mapIntWithValues - map of Interacting With Entity criteria values from Input
    * @return   Void                  
    */
    @TestVisible    
    private static void getMatchingConsumerIDRecs(list<ConsumerID__c> lstConsumerIDRecs, map<string,set<string>> mapIntAboutValues, map<string,set<string>> mapIntWithValues)
    {                   
        set<string> setIntAbtAccts = new set<string>();
        set<string> setIntWithAccts = new set<string>();
        set<string> setLocatedIntAboutTaxID = new set<string>();
        set<string> setLocatedIntWithTaxID = new set<string>();         
        map<string, set<string>> mapUnknownProvider = new map<string, set<string>>(); 
                                         
        if(lstConsumerIDRecs!=NULL && !lstConsumerIDRecs.isEmpty())  
        {            
            set<string> setIntAbtVal = new set<string>();
            set<string> setIntWithVal = new set<string>();          
            
            for(ConsumerID__c consumerRec : lstConsumerIDRecs)
            {
               setIntAbtVal = new set<string>();
               setIntWithVal = new set<string>();
               string sIdType = consumerRec.ID_Type__c;
               if(sIDType.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_HUMANAID_HUM))    sIdType = mapRecordType.get(MemberID);                        
               if(mapIntAboutValues.containsKey(sIDType)) setIntAbtVal.addAll(mapIntAboutValues.get(sIDType));             
               if(mapIntWithValues.containsKey(sIDType))  setIntWithVal.addAll(mapIntWithValues.get(sIDType));             
                                       
                   if(setIntAbtVal.contains(consumerRec.Consumer_ID__c))
                   {
                        //For Unknown Provider TaxID
                        if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNPROVIDER))
                        {
                        	if(mapIntAboutTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM) && mapIntAboutTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	if(mapUnknownProvider!=null && mapUnknownProvider.containsKey(consumerRec.Consumer_ID__c))
	                        	{
	                        		mapUnknownProvider.get(consumerRec.Consumer_ID__c).add(consumerRec.Account__c);
	                        	}
	                        	else
	                        	{
	                        		mapUnknownProvider.put(consumerRec.Consumer_ID__c, new set<string>{consumerRec.Account__c});
	                        	}
                        	}                        	
                        }
                        //For Unknown Agent TaxID
                        else if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CLOSEDCASE_UNKNOWNAGENTBROKER_HUM))
                        {
                        	if(mapIntAboutTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapIntAboutTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	if(mapUnknownProvider!=null && mapUnknownProvider.containsKey(consumerRec.Consumer_ID__c))
	                        	{
	                        		mapUnknownProvider.get(consumerRec.Consumer_ID__c).add(consumerRec.Account__c);
	                        	}
	                        	else
	                        	{
	                        		mapUnknownProvider.put(consumerRec.Consumer_ID__c, new set<string>{consumerRec.Account__c});
	                        	}
                        	}                        	
                        }
                        //For Known Provider TaxID
                        else if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
                        {
                        	//Both ifs are seperated to check first TaxId is sent only for Provider and set to account. If combined then goes to default else and get assigned
                        	if(mapIntAboutTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM) && mapIntAboutTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	setIntAbtAccts.add(consumerRec.Account__c);
	                        	setLocatedIntAboutTaxID.add(consumerRec.Consumer_ID__c);
                        	}
                        }
                        //For Known Agent TaxID
                        else if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER_HUM))
                        {
                        	//Both ifs are seperated to check first TaxId is sent only for Agent and set to account. If combined then goes to default else and get assigned
                        	if(mapIntAboutTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapIntAboutTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	setIntAbtAccts.add(consumerRec.Account__c);
	                        	setLocatedIntAboutTaxID.add(consumerRec.Consumer_ID__c);
                        	}
                        }
                        else
                        {
                        	setIntAbtAccts.add(consumerRec.Account__c);
                        }
                   }                                     
                   if(setIntWithVal.contains(consumerRec.Consumer_ID__c))
                   {
                        if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNPROVIDER))
                        {
                        	if(mapIntWithTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM) && mapIntWithTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	if(mapUnknownProvider!=null && mapUnknownProvider.containsKey(consumerRec.Consumer_ID__c))
	                        	{
	                        		mapUnknownProvider.get(consumerRec.Consumer_ID__c).add(consumerRec.Account__c);
	                        	}
	                        	else
	                        	{
	                        		mapUnknownProvider.put(consumerRec.Consumer_ID__c, new set<string>{consumerRec.Account__c});
	                        	}
                        	}
                        }
                        else if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CLOSEDCASE_UNKNOWNAGENTBROKER_HUM))
                        {
                        	if(mapIntWithTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapIntWithTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	if(mapUnknownProvider!=null && mapUnknownProvider.containsKey(consumerRec.Consumer_ID__c))
	                        	{
	                        		mapUnknownProvider.get(consumerRec.Consumer_ID__c).add(consumerRec.Account__c);
	                        	}
	                        	else
	                        	{
	                        		mapUnknownProvider.put(consumerRec.Consumer_ID__c, new set<string>{consumerRec.Account__c});
	                        	}
                        	}
                        }
                        else if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
                        {
                        	if( mapIntWithTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM) && mapIntWithTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	setIntWithAccts.add(consumerRec.Account__c);
	                        	setLocatedIntWithTaxID.add(consumerRec.Consumer_ID__c);  
                        	}                      	
                        }
                        else if(sIdType.equals(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && consumerRec.Account__r.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER_HUM))
                        {
                        	if(mapIntWithTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapIntWithTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).contains(consumerRec.Consumer_ID__c))
                        	{
	                        	setIntWithAccts.add(consumerRec.Account__c);
	                        	setLocatedIntWithTaxID.add(consumerRec.Consumer_ID__c);     
                        	}                   	
                        }
                        else
                        {
                        	setIntWithAccts.add(consumerRec.Account__c);
                        }
                   }
                     
                   if(mapAcctWithConsumer.containsKey(consumerRec.Account__c))
                   {
                        mapAcctWithConsumer.get(consumerRec.Account__c).add(consumerRec);
                   }
                   else
                   {
                        mapAcctWithConsumer.put(consumerRec.Account__c, new set<ConsumerID__c>{consumerRec});
                   }             
           }
           if(bSearchUnknown && mapUnknownProvider!=null && !mapUnknownProvider.isEmpty())
           {
	           	set<string> setUnknownTaxID = new set<string>();
	           	set<string> setIntAboutTaxID = new set<string>();
	           	set<string> setIntWithTaxID = new set<string>();
	           	
	           	if(null!= mapIntAboutTaxId && !mapIntAboutTaxId.isEmpty() && mapIntAboutTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
		           	setIntAboutTaxID.addAll(mapIntAboutTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM));
		           	
		        if(null!= mapIntWithTaxId && !mapIntWithTaxId.isEmpty() && mapIntWithTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
		           	setIntWithTaxID.addAll(mapIntWithTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM));
		           	
		        if(null!= mapIntAboutTaxId && !mapIntAboutTaxId.isEmpty() && mapIntAboutTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
		           	setIntAboutTaxID.addAll(mapIntAboutTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM));
		           	
		        if(null!= mapIntWithTaxId && !mapIntWithTaxId.isEmpty() && mapIntWithTaxId.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
		           	setIntWithTaxID.addAll(mapIntWithTaxId.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM));
	           	
				if(setLocatedIntAboutTaxID==null || setLocatedIntAboutTaxID.isEmpty())  // No Known Provider Found
	          	{
	          		setUnknownTaxID.addAll(setIntAboutTaxID);
	          	}
	          	else if(setLocatedIntAboutTaxID.size()< setIntAboutTaxID.size())  //Only a few Known Provider Found
	          	{
	          		for(string sTaxID : setIntAboutTaxID)
	          		{
	          			if(!setLocatedIntAboutTaxID.contains(sTaxID))	setUnknownTaxID.add(sTaxID);
	          		}
	          	}
	           	if(setUnknownTaxID!=null && !setUnknownTaxID.isEmpty())
	           	{
	           		for(string sTaxID : setUnknownTaxID)
	           		{
	           			if(mapUnknownProvider.containsKey(sTaxID))	setIntAbtAccts.addAll(mapUnknownProvider.get(sTaxID));	
	           		}
	           	}
				setUnknownTaxID = new set<string>();
				if(setLocatedIntWithTaxID==null || setLocatedIntWithTaxID.isEmpty())  // No Known Provider Found
	          	{
	          		setUnknownTaxID.addAll(setIntWithTaxID);
	          	}
	          	else if(setLocatedIntWithTaxID.size()< setIntWithTaxID.size())  //Only a few Known Provider Found
	          	{
	          		for(string sTaxID : setIntWithTaxID)
	          		{
	          			if(!setLocatedIntWithTaxID.contains(sTaxID))	setUnknownTaxID.add(sTaxID);
	          		}
	          	}
	           	if(setUnknownTaxID!=null && !setUnknownTaxID.isEmpty())
	           	{
	           		for(string sTaxID : setUnknownTaxID)
	           		{
	           			if(mapUnknownProvider.containsKey(sTaxID))	setIntWithAccts.addAll(mapUnknownProvider.get(sTaxID));	
	           		}
	           	}	
	        }
           if((setIntAbtAccts!= null && !setIntAbtAccts.isEmpty()) || (setIntWithAccts!= null && !setIntWithAccts.isEmpty()))
           {
                populateResultsInMap(setIntAbtAccts, setIntWithAccts);
           }
       } 
    }
    
    /*
    * locateAccountRecords
    * <p>
    * This method is used to get the interacting with and Interacting about Account Records for Agent(AgentID),Group(GroupNumber, CustomerGenKey),   
    * Provider(ProviderID, NPIID), Member(EnterpriseID).
    * @param    mapInquireAboutCriteria - map of Interacting About Input IDs by type from Input request
    * @param    mapInquireWithCriteria  - map of Interacting With Input IDs by type from Input request
    * @return   void
    */
    @TestVisible    
    private static void locateAccountRecords(map<string, map<string,set<string>>> mapInquireAboutCriteria, map<string, map<string,set<string>>> mapInquireWithCriteria)
    {       
        list<Account> lstAccountRecs = new list<Account>();
        if(mapInquireAboutCriteria!=null && !mapInquireAboutCriteria.isEmpty())
        {
            createMapAcctIDValuesByType(mapInquireAboutCriteria);           
        }
        if(mapInquireWithCriteria!=null && !mapInquireWithCriteria.isEmpty())
        {
            createMapAcctIDValuesByType(mapInquireWithCriteria);    
        }
        if(mapAcctIDValuesbyType!=null && !mapAcctIDValuesbyType.isEmpty())
        {           
            lstAccountRecs.addAll(getAccountRecords(mapAcctIDValuesbyType));
        }
        if(lstAccountRecs!=null && !lstAccountRecs.isEmpty())
        {
            if((mapInquireAboutCriteria!=null && !mapInquireAboutCriteria.isEmpty()) || (mapInquireWithCriteria!=null && !mapInquireWithCriteria.isEmpty()))
                getMatchingAccounts(lstAccountRecs, mapInquireAboutCriteria, mapInquireWithCriteria);            
        }
    }
    
    /*
    * createMapAcctIDValuesByType
    * <p>
    * This method is used to populate values in mapAcctIDValuesbyType. mapAcctIDValuesbyType will have the key as API Name   
    * of the Entity IDType and set of Entity IDValues under each IDType.
    * @param    mapInquireCriteria - map of Input IDs by type from Input request
    * @return   void
    */
    @TestVisible    
    private static void createMapAcctIDValuesByType(map<string, map<string,set<string>>> mapInquireCriteria)
    {       
        if(mapInquireCriteria!=null && !mapInquireCriteria.isEmpty())
        {
            //For Member Entity Type
            if(mapInquireCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM))
            {
                map<string, set<string>> mapMemberIDValues = new map<string, set<string>>();
                mapMemberIDValues.putAll(mapInquireCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM));
                if(mapMemberIDValues!= null && mapMemberIDValues.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID))
                {
                    if(mapAcctIDValuesbyType.containsKey(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID)))
                        mapAcctIDValuesbyType.get(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID)).addAll(mapMemberIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID).clone());
                    else
                        mapAcctIDValuesbyType.put(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID), mapMemberIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID).clone());                       
                }
            }
            //For Agent Entity Type
            if(mapInquireCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
            {
                map<string, set<string>> mapAgentIDValues = new map<string, set<string>>();
                mapAgentIDValues.putAll(mapInquireCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM));
                if(mapAgentIDValues!= null && mapAgentIDValues.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID))
                {
                    if(mapAcctIDValuesbyType.containsKey(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID)))
                        mapAcctIDValuesbyType.get(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID)).addAll(mapAgentIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID).clone());
                    else
                        mapAcctIDValuesbyType.put(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID), mapAgentIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID).clone());                      
                }               
            }
            //For Group Entity Type
            if(mapInquireCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))
            {               
                map<string, set<string>> mapGroupIDValues = new map<string, set<string>>();
                mapGroupIDValues.putAll(mapInquireCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM));
                if(mapGroupIDValues!= null && mapGroupIDValues.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM))
                {
                    if(mapAcctIDValuesbyType.containsKey(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM)))
                        mapAcctIDValuesbyType.get(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM)).addAll(mapGroupIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM).clone());
                    else
                        mapAcctIDValuesbyType.put(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM), mapGroupIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM).clone());                        
                }
                if(mapGroupIDValues!= null && mapGroupIDValues.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY))
                {
                    if(mapAcctIDValuesbyType.containsKey(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY)))
                        mapAcctIDValuesbyType.get(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY)).addAll(mapGroupIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY).clone());
                    else
                        mapAcctIDValuesbyType.put(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY), mapGroupIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY).clone());                        
                }               
            }
            //For Provider Entity Type
            if(mapInquireCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
            {
                map<string, set<string>> mapProviderIDValues = new map<string, set<string>>();
                mapProviderIDValues.putAll(mapInquireCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM));
                if(mapProviderIDValues!= null && mapProviderIDValues.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID))
                {
                    if(mapAcctIDValuesbyType.containsKey(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID)))
                        mapAcctIDValuesbyType.get(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID)).addAll(mapProviderIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID).clone());
                    else
                        mapAcctIDValuesbyType.put(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID), mapProviderIDValues.get(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID).clone());                       
                }
                if(mapProviderIDValues!= null && mapProviderIDValues.containsKey(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID))
                {
                    if(mapAcctIDValuesbyType.containsKey(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID)))
                        mapAcctIDValuesbyType.get(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID)).addAll(mapProviderIDValues.get(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID).clone());
                    else
                        mapAcctIDValuesbyType.put(GLOBAL_CONSTANT_HUM.mapAcctIDTypes.get(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID), mapProviderIDValues.get(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID).clone());                     
                }               
            }                                       
        }
    }
    
    /*
    * getAccountRecords
    * <p>
    * This method is used to query the accounts for Input EnterpriseIDs. 
    *  
    * @param    mapAcctIDValuesbyType - map of Input IDs by type from Input request
    * @return   list<Account>
    */
    @TestVisible
    private static list<Account> getAccountRecords(map<string,set<string>> mapAcctIDValuesbyType)
    {
        list<Account> lstAccounts = new list<Account>();        
        string sAccountQuery = 'SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c FROM Account WHERE';        
        string sWhereClause = '';
        map<string, list<string>> mapSearchValues = new map<string, list<string>>();
        
        if(mapAcctIDValuesbyType!=null && !mapAcctIDValuesbyType.isEmpty())
        {
            integer i = 0;
            for(string sIDType : mapAcctIDValuesbyType.keySet())
            {
                list<string> lstStrValues = new list<string>();
                for(string sValue : mapAcctIDValuesbyType.get(sIDType))
                {
                    lstStrValues.add('\''+String.escapeSingleQuotes(sValue)+'\'');
                }
                mapSearchValues.put(sIDType, lstStrValues);
            }   
            if(mapSearchValues!=null && !mapSearchValues.isEmpty())
            {               
                for(string sIDType : mapSearchValues.keySet())
                {
                    
                    if(i==0)
                    {
                        i++;                        
                        sWhereClause += ' ' + sIDType + ' IN ' + mapSearchValues.get(sIDType);
                    }
                    else                        
                        sWhereClause += ' OR ' + sIDType + ' IN ' + mapSearchValues.get(sIDType);   
                }
            }
            if(String.isNotBlank(sWhereClause))
            {
                sAccountQuery += sWhereClause;
                lstAccounts = Database.Query(sAccountQuery); 
            }          
        }        
        return lstAccounts;
    }
    
   /*
    * getMatchingAccounts
    * <p>
    * This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    lstAccountRecs - list of Accounts retrieved by SOQL
    * @param    mapInquireAboutCriteria - map of Input Interacting About values
    * @param    mapInquireWithCriteria - map of Input Interacting About values       
    * @return   Void                  
    */
    @TestVisible
    private static void getMatchingAccounts(list<Account> lstAccountRecs, map<string, map<string,set<string>>> mapInquireAboutCriteria, map<string, map<string,set<string>>> mapInquireWithCriteria)
    {
        set<string> setIntAboutAccts = new set<string>();
        set<string> setIntWithAccts = new set<string>();        
        map<string, map<string, set<string>>> mapKnownMembers = new map<string, map<string, set<string>>>();
        map<string, map<string, set<string>>> mapKnownProviders = new map<string, map<string, set<string>>>();
        map<string, map<string, set<string>>> mapKnownAgents = new map<string, map<string, set<string>>>();                       
		map<string, map<string, set<string>>> mapUnknownMembers = new map<string, map<string, set<string>>>();		
		map<string, map<string, set<string>>> mapUnknownProviders = new map<string, map<string, set<string>>>();
		map<string, map<string, set<string>>> mapUnknownAgents = new map<string, map<string, set<string>>>();
		
		map<string, map<string, list<Account>>> mapKnownGroup = new map<string, map<string, list<Account>>>();
		
		boolean bMatchMembers = false;
        boolean bMatchAgents = false;
        boolean bMatchProviderID = false;
        boolean bMatchProviderNPIID = false;
        boolean bMatchGroupNum = false;
        boolean bMatchGroupGenKey = false;        
                                                                                                                    
        if(lstAccountRecs!=NULL && !lstAccountRecs.isEmpty())            
        {
            if(mapInquireAboutCriteria!=null && !mapInquireAboutCriteria.isEmpty())
            {
	            //Interaction About Criteria
	            if(mapInquireAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) && mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID))
	            {               
	               bMatchMembers = true;              
	            }
	            if(mapInquireAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID))
	            {
	               bMatchAgents = true;               
	            }
	            if(mapInquireAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
	            {
	               if(mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID))		bMatchProviderID = true;
	               if(mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID))    bMatchProviderNPIID = true;            
	            }
	            if(mapInquireAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))
	            {
	               if(mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM))		bMatchGroupNum = true;
	               if(mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY))	bMatchGroupGenKey = true;		                             
	            }
            }
            
            //Interaction With Criteria
            if(mapInquireWithCriteria!=null && !mapInquireWithCriteria.isEmpty())
            {
	            if(mapInquireWithCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) && mapInquireWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID))
	            {               
	               bMatchMembers = true;              
	            }
	            if(mapInquireWithCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && mapInquireWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID))
	            {
	               bMatchAgents = true;               
	            }
	            if(mapInquireWithCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
	            {
	               if(mapInquireWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID))		bMatchProviderID = true;
	               if(mapInquireWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID))    bMatchProviderNPIID = true;            
	            }
	            if(mapInquireWithCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))
	            {
	               if(mapInquireWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM))		bMatchGroupNum = true;
	               if(mapInquireWithCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM).containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY))	bMatchGroupGenKey = true;		                             
	            }
            }
            
            for(Account accountRec : lstAccountRecs) 
            {                                                              
               if(accountRec.recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) && bMatchMembers)
               {
                  map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
                  if(mapKnownMembers.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID))
                  {
                  	mapAccountID.putAll(mapKnownMembers.get(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID));
                  	if(mapAccountID.containsKey(accountRec.Enterprise_ID__c))
                  		mapAccountID.get(accountRec.Enterprise_ID__c).add(accountRec.Id);
                  	else
                  		mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
                  	
                  	mapKnownMembers.put(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID, mapAccountID);
                  }
                  else
                  {
                  	mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
                  	mapKnownMembers.put(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID, mapAccountID);
                  }                  	 
               }
               else if(accountRec.recordtype.Name.equals(GLOBAL_CONSTANT_HUM.RECORD_TYPE_UNKNOWN_MEMBER) && bMatchMembers)
               {
				  map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
                  if(mapUnknownMembers.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID))
                  {
                  	mapAccountID.putAll(mapUnknownMembers.get(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID));
                  	if(mapAccountID.containsKey(accountRec.Enterprise_ID__c))
                  		mapAccountID.get(accountRec.Enterprise_ID__c).add(accountRec.Id);
                  	else
                  		mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
                  	
                  	mapUnknownMembers.put(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID, mapAccountID);
                  }
                  else
                  {
                  	mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
                  	mapUnknownMembers.put(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID, mapAccountID);
                  }               	
               }
               if(accountRec.Recordtype.Name.equals(RECORDTYPE_AGENT) && bMatchAgents)
               {                    
					map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
					if(mapKnownAgents.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID))
					{
						mapAccountID.putAll(mapKnownAgents.get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID));
					  	if(mapAccountID.containsKey(accountRec.Agent_ID__c))
					  		mapAccountID.get(accountRec.Agent_ID__c).add(accountRec.Id);
					  	else
					  		mapAccountID.put(accountRec.Agent_ID__c, new set<string>{accountRec.Id});
					  	
					  	mapKnownAgents.put(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID, mapAccountID);
					}
					else
					{
						mapAccountID.put(accountRec.Agent_ID__c, new set<string>{accountRec.Id});
						mapKnownAgents.put(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID, mapAccountID);
					}
               }
               else if(accountRec.Recordtype.Name.equals(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNAGENTRECORDTYPE_HUM) && bMatchAgents)
               {               		
					map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
					if(mapUnknownAgents.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID))
					{
						mapAccountID.putAll(mapUnknownAgents.get(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID));
						if(mapAccountID.containsKey(accountRec.Agent_ID__c))
							mapAccountID.get(accountRec.Agent_ID__c).add(accountRec.Id);
						else
							mapAccountID.put(accountRec.Agent_ID__c, new set<string>{accountRec.Id});
					
						mapUnknownAgents.put(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID, mapAccountID);
					}
					else
					{
						mapAccountID.put(accountRec.Agent_ID__c, new set<string>{accountRec.Id});
						mapUnknownAgents.put(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID, mapAccountID);
					}               			
               }
               
               if(accountRec.RecordType.Name.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))
               {
               		if(bMatchGroupNum)
               		{
               			map<string, list<Account>> mapAccount = new map<string, list<Account>>(); 
						if(mapKnownGroup.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM))
						{
							mapAccount.putAll(mapKnownGroup.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM));
							if(mapAccount.containsKey(accountRec.Group_Number__c))
								mapAccount.get(accountRec.Group_Number__c).add(accountRec);
							else
								mapAccount.put(accountRec.Group_Number__c, new list<Account>{accountRec});
						
							mapKnownGroup.put(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM, mapAccount);
						}
						else
						{
							mapAccount.put(accountRec.Group_Number__c, new list<Account>{accountRec});
							mapKnownGroup.put(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM, mapAccount);
						}	
               		}
               		if(bMatchGroupGenKey)
               		{
               			map<string, list<Account>> mapAccount = new map<string, list<Account>>(); 
						if(mapKnownGroup.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY))
						{
							mapAccount.putAll(mapKnownGroup.get(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY));
							if(mapAccount.containsKey(accountRec.Enterprise_ID__c))
								mapAccount.get(accountRec.Enterprise_ID__c).add(accountRec);
							else
								mapAccount.put(accountRec.Enterprise_ID__c, new list<Account>{accountRec});
						
							mapKnownGroup.put(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY, mapAccount);
						}
						else
						{
							mapAccount.put(accountRec.Enterprise_ID__c, new list<Account>{accountRec});
							mapKnownGroup.put(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY, mapAccount);
						}
               		}                         
               }
               
               if(accountRec.RecordType.Name.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
               {
                    if(bMatchProviderID)
                    {
                    	map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
						if(mapKnownProviders.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID))
						{
							mapAccountID.putAll(mapKnownProviders.get(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID));
							if(mapAccountID.containsKey(accountRec.Enterprise_ID__c))
								mapAccountID.get(accountRec.Enterprise_ID__c).add(accountRec.Id);
							else
								mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
						
							mapKnownProviders.put(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID, mapAccountID);
						}
						else
						{
							mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
							mapKnownProviders.put(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID, mapAccountID);
						}
                    }
                    if(bMatchProviderNPIID)
                    {
                    	map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
						if(mapKnownProviders.containsKey(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID))
						{
							mapAccountID.putAll(mapKnownProviders.get(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID));
							if(mapAccountID.containsKey(accountRec.NPI_ID__c))
								mapAccountID.get(accountRec.NPI_ID__c).add(accountRec.Id);
							else
								mapAccountID.put(accountRec.NPI_ID__c, new set<string>{accountRec.Id});
						
							mapKnownProviders.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID, mapAccountID);
						}
						else
						{
							mapAccountID.put(accountRec.NPI_ID__c, new set<string>{accountRec.Id});
							mapKnownProviders.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID, mapAccountID);
						}
                    }                    
               }
               else if(accountRec.RecordType.Name.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNPROVIDER))
               {
               		if(bMatchProviderID)
                    {
                    	map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
						if(mapUnknownProviders.containsKey(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID))
						{
							mapAccountID.putAll(mapUnknownProviders.get(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID));
							if(mapAccountID.containsKey(accountRec.Enterprise_ID__c))
								mapAccountID.get(accountRec.Enterprise_ID__c).add(accountRec.Id);
							else
								mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
						
							mapUnknownProviders.put(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID, mapAccountID);
						}
						else
						{
							mapAccountID.put(accountRec.Enterprise_ID__c, new set<string>{accountRec.Id});
							mapUnknownProviders.put(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID, mapAccountID);
						}
                    }
                    if(bMatchProviderNPIID)
                    {
                    	map<string, set<string>> mapAccountID = new map<string, set<string>>(); 
						if(mapUnknownProviders.containsKey(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID))
						{
							mapAccountID.putAll(mapUnknownProviders.get(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID));
							if(mapAccountID.containsKey(accountRec.NPI_ID__c))
								mapAccountID.get(accountRec.NPI_ID__c).add(accountRec.Id);
							else
								mapAccountID.put(accountRec.NPI_ID__c, new set<string>{accountRec.Id});
						
							mapUnknownProviders.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID, mapAccountID);
						}
						else
						{
							mapAccountID.put(accountRec.NPI_ID__c, new set<string>{accountRec.Id});
							mapUnknownProviders.put(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID, mapAccountID);
						}
                    }	
               }                             
            }            
            if(mapInquireAboutCriteria!=null && !mapInquireAboutCriteria.isEmpty())
            {
            	for(string sEntityType : mapInquireAboutCriteria.keySet())
            	{
            		for(string sIDType : mapInquireAboutCriteria.get(sEntityType).keySet())
            		{
            			for(string sIDValue : mapInquireAboutCriteria.get(sEntityType).get(sIDType))
            			{
            				if(sEntityType.equals(RECORDTYPE_MEMBER))
            				{
            					if(mapKnownMembers!=null && mapKnownMembers.containsKey(sIDType) && mapKnownMembers.get(sIDType).containsKey(sIDValue))
            					{
            						setIntAboutAccts.addAll(mapKnownMembers.get(sIDType).get(sIDValue));
            						continue;
            					}
            					else if(mapUnknownMembers!=null && mapUnknownMembers.containsKey(sIDType) && mapUnknownMembers.get(sIDType).containsKey(sIDValue))
            					{
            						setIntAboutAccts.addAll(mapUnknownMembers.get(sIDType).get(sIDValue));            						
            					}
            				}
            				if(sEntityType.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
            				{
            					if(mapKnownAgents!=null && mapKnownAgents.containsKey(sIDType) && mapKnownAgents.get(sIDType).containsKey(sIDValue))
            					{
            						setIntAboutAccts.addAll(mapKnownAgents.get(sIDType).get(sIDValue));
            						continue;
            					}
            					else if(mapUnknownAgents!=null && mapUnknownAgents.containsKey(sIDType) && mapUnknownAgents.get(sIDType).containsKey(sIDValue))
            					{
            						setIntAboutAccts.addAll(mapUnknownAgents.get(sIDType).get(sIDValue));            						
            					}
            				}
            				if(sEntityType.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
            				{
            					if(mapKnownProviders!=null && mapKnownProviders.containsKey(sIDType) && mapKnownProviders.get(sIDType).containsKey(sIDValue))
            					{
            						setIntAboutAccts.addAll(mapKnownProviders.get(sIDType).get(sIDValue));
            						continue;
            					}
            					else if(mapUnknownProviders!=null && mapUnknownProviders.containsKey(sIDType) && mapUnknownProviders.get(sIDType).containsKey(sIDValue))
            					{
            						setIntAboutAccts.addAll(mapUnknownProviders.get(sIDType).get(sIDValue));            						
            					}
            				}
            				if(sEntityType.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM) && mapKnownGroup!=null && mapKnownGroup.containsKey(sIDType) && mapKnownGroup.get(sIDType).containsKey(sIDValue))
            				{
            					if(sIDType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM))
            					{
            						map<string, string> mapGrpPlatform = new map<string, string>();
									mapGrpPlatform = mapGroupPlatform.get(GLOBAL_CONSTANT_HUM.INTABOUT);
									if(mapGrpPlatform!=null && mapGrpPlatform.containsKey(sIDValue))
									{
										string sPlatfrom = mapGrpPlatform.get(sIDValue);
										for(Account accountRec : mapKnownGroup.get(sIDType).get(sIDValue))
										{
											if(sPlatfrom.equals(accountRec.Source_Platform_Code__c))
												setIntAboutAccts.add(accountRec.Id);
										}										
									}										            					
            					}
								else
								{
									for(Account accountRec : mapKnownGroup.get(sIDType).get(sIDValue))
									{									
											setIntAboutAccts.add(accountRec.Id);
									}									            					
								}
            					continue;
            				}								
            			}
            		}
            	}
            }
            if(mapInquireWithCriteria!=null && !mapInquireWithCriteria.isEmpty())
            {
            	for(string sEntityType : mapInquireWithCriteria.keySet())
            	{
            		for(string sIDType : mapInquireWithCriteria.get(sEntityType).keySet())
            		{
            			for(string sIDValue : mapInquireWithCriteria.get(sEntityType).get(sIDType))
            			{
            				if(sEntityType.equals(RECORDTYPE_MEMBER))
            				{
            					if(mapKnownMembers!=null && mapKnownMembers.containsKey(sIDType) && mapKnownMembers.get(sIDType).containsKey(sIDValue))
            					{
            						setIntWithAccts.addAll(mapKnownMembers.get(sIDType).get(sIDValue));            						            					
            					}
            					else if(mapUnknownMembers!=null && mapUnknownMembers.containsKey(sIDType) && mapUnknownMembers.get(sIDType).containsKey(sIDValue))
            					{
            						setIntWithAccts.addAll(mapUnknownMembers.get(sIDType).get(sIDValue));            						
            					}
            					continue;
            				}
            				if(sEntityType.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
            				{
            					if(mapKnownAgents!=null && mapKnownAgents.containsKey(sIDType) && mapKnownAgents.get(sIDType).containsKey(sIDValue))
            					{
            						setIntWithAccts.addAll(mapKnownAgents.get(sIDType).get(sIDValue));            						
            					}
            					else if(mapUnknownAgents!=null && mapUnknownAgents.containsKey(sIDType) && mapUnknownAgents.get(sIDType).containsKey(sIDValue))
            					{
            						setIntWithAccts.addAll(mapUnknownAgents.get(sIDType).get(sIDValue));            						
            					}
            					continue;
            				}
            				if(sEntityType.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
            				{
            					if(mapKnownProviders!=null && mapKnownProviders.containsKey(sIDType) && mapKnownProviders.get(sIDType).containsKey(sIDValue))
            					{
            						setIntWithAccts.addAll(mapKnownProviders.get(sIDType).get(sIDValue));            						
            					}
            					else if(mapUnknownProviders!=null && mapUnknownProviders.containsKey(sIDType) && mapUnknownProviders.get(sIDType).containsKey(sIDValue))
            					{
            						setIntWithAccts.addAll(mapUnknownProviders.get(sIDType).get(sIDValue));            						
            					}
            					continue;
            				}
            				if(sEntityType.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM) && mapKnownGroup!=null && mapKnownGroup.containsKey(sIDType) && mapKnownGroup.get(sIDType).containsKey(sIDValue))
            				{
            					if(sIDType.equals(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM))
            					{
            						map<string, string> mapGrpPlatform = new map<string, string>();
									mapGrpPlatform = mapGroupPlatform.get(GLOBAL_CONSTANT_HUM.INTWITH);
									if(mapGrpPlatform!=null && mapGrpPlatform.containsKey(sIDValue))
									{
										string sPlatfrom = mapGrpPlatform.get(sIDValue);
										for(Account accountRec : mapKnownGroup.get(sIDType).get(sIDValue))
										{
											if(sPlatfrom.equals(accountRec.Source_Platform_Code__c))
												setIntWithAccts.add(accountRec.Id);
										}										
									}										            					
            					}
								else
								{
									for(Account accountRec : mapKnownGroup.get(sIDType).get(sIDValue))
									{									
											setIntWithAccts.add(accountRec.Id);
									}									            					
								}
            					continue;
            				}            				
            			}
            		}
            	}
            }            
            if((setIntAboutAccts!=null && !setIntAboutAccts.isEmpty()) || (setIntWithAccts!=null && !setIntWithAccts.isEmpty()))
            {
            	populateResultsInMap(setIntAboutAccts, setIntWithAccts);
            }             
       }
    }
    
    /*
    * populateResultsInMap
    * <p>
    * This method will be used to populate the Account IDs in the global variable mapEntitySearchResults. This Map stores the 
    * Interacting About AccountIDs and Interacting With Account IDs.
    * @param    setIntAbtAccts -Set of Interacting About Account IDs 
    * @param    setIntWithAccts - Set of Interacting With Account IDs
    * @return   Void                  
    */ 
    @TestVisible
    private static void populateResultsInMap(set<string> setIntAbtAccts, set<string> setIntWithAccts)
    {
        if(setIntAbtAccts!= null && !setIntAbtAccts.isEmpty())
        {
            set<string> setAccountIDs = new set<string>();
            if(mapEntitySearchResults.containsKey(GLOBAL_CONSTANT_HUM.INTABOUT))
            {
                setAccountIDs.addAll(mapEntitySearchResults.get(GLOBAL_CONSTANT_HUM.INTABOUT));
            }
            setAccountIDs.addAll(setIntAbtAccts);
            mapEntitySearchResults.put(GLOBAL_CONSTANT_HUM.INTABOUT, setAccountIDs);                   
        }
        if(setIntWithAccts!= null && !setIntWithAccts.isEmpty())
        {
            set<string> setAccountIDs = new set<string>();
            if(mapEntitySearchResults.containsKey(GLOBAL_CONSTANT_HUM.INTWITH))
            {
                setAccountIDs.addAll(mapEntitySearchResults.get(GLOBAL_CONSTANT_HUM.INTWITH));
            }
            setAccountIDs.addAll(setIntWithAccts);
            mapEntitySearchResults.put(GLOBAL_CONSTANT_HUM.INTWITH, setAccountIDs);                
        }
    }
    
    /**
    * parsestringToDate
    * <p>
    * Method is used to parse date from string
    *
    * @param     string sDate
    * @return    Date                  
    */     
    public static Date parsestringToDate(string sDate)
    {        
        Date dDate = null;        
	    try
	    {
	        if(sDate.length() == 8 && !Test.isRunningTest())
	        {
	            //Added Test.isRunningTest() here because Salesforce shows date as MM/DD/YYYY or M/D/YYYY
	            //While running test class Date is either in / or without / format. Also initial days of month or starting month of year are stored in single digit format
	            //For example 1st January is 1/1 and 29th January is 1/29
	            dDate = date.newInstance(Integer.valueof(sDate.substring(4,8)),
	            Integer.valueof(sDate.substring(2,4)), Integer.valueof(sDate.substring(0,2)));
	        }
	        else
	        {            
	            dDate = date.parse(sDate);
	        }
	    }
        catch(Exception ex)
        {
            throw ex;  //exception needs to be bubbled up to the calling method to logged and sent in the service response
        }
        return dDate;        
    }    
}