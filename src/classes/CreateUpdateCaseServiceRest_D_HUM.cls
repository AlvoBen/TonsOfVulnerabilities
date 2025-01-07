/***************************************************************************************************************************************************************************
Apex Class Name  : CreateUpdateCaseServiceRest_D_HUM
Version          : 1.0
Created Date     : March 7th 2019
Function         : 
Test Class       : 
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name              Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Asish Kumar Behera                                     07/17/2019                  Original version
* Asish Kumar Behera                                     08/25/2019                  exception handling added
* Asish Kumar Behera                                     09/10/2019                  exception message changed
* Suraj Patil                                   		  10/03/2019        		  REQ - 409544 allow consumer to send Int With type=Internal
* Asish Behera                                   		  05/28/2020        		  Null Pointer Exception Handled.
* Sai Kumar Boga                                         02/24/2021                  US2009196 - PR00094232 - Build Checkmarx - SF - TECH
* Sagar MN						 						10/15/2020		    		 Unknown provider issue fix - updated code to handle one req interaction with/about
* Sagar MN												03/04/2021		     		 US 1999913 Code checkin for MF1
* Jayesh Solanki                                         05/14/2020                  User Story 2271553: T1PRJ0001827 - MF 1 - Quality - Add "Care Manager" as a new Interacting with type value (CRM)      
****************************************************************************************************************************************************************************/
public with sharing class CreateUpdateCaseServiceRest_D_HUM {
    
    private static map<string,string> mapRecordType = new map<string,string>
    {
        GLOBAL_CONSTANT_HUM.MEMBER  => GLOBAL_CONSTANT_HUM.MEMBER,
            GLOBAL_CONSTANT_HUM.PROVIDER_TYPE => GLOBAL_CONSTANT_HUM.PROVIDER_TYPE,
            GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER_HUM => GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM,
            GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNMEMBER_HUM => GLOBAL_CONSTANT_HUM.CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER,
            GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNAGENTRECORDTYPE_HUM => GLOBAL_CONSTANT_HUM.HUMUnknownAgentIntType,
            GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNPROVIDERRECORDTYPE_HUM => GLOBAL_CONSTANT_HUM.HUMUnknownProviderIntType,
            GLOBAL_CONSTANT_HUM.HUMGroupIntType  => GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM ,
            GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE => GLOBAL_CONSTANT_HUM.HUMUnknownGroupIntType 
            };  
                
                /*
* createCase
* <p>
* create case based on the input provided.
*
* @param     CreateCaseRequest_V4_HUM
* @return    CaseDetailResponse_DTO_V4_HUM                    
*/ 
                public static CaseDetailResponse_DTO_V4_HUM createCase(CreateCaseRequest_V4_HUM request){
                    
                    CaseDetailResponse_DTO_V4_HUM response= new CaseDetailResponse_DTO_V4_HUM();
                    if(request == null)
                    {
                        response = getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.HUMCaseCreateMandatoryError,false);
                    }
                    else
                    {  
                        response = createCaseRecord(request);
                    }
                    return response;
                }
    
    /*
* createCaseRecord
* <p>
* create case based on the input provided.
*
* @param     CreateCaseRequest_V4_HUM
* @return    CaseDetailResponse_DTO_V4_HUM                    
*/ 
    private static CaseDetailResponse_DTO_V4_HUM createCaseRecord(CreateCaseRequest_V4_HUM request){
        CaseDetailResponse_DTO_V4_HUm response;
        try{
            
            List<CaseDetailResponse_DTO_V4_HUM> ListOfreponse  = new  List<CaseDetailResponse_DTO_V4_HUM>();            
            list<CreateCaseRequest_DTO_V4_HUM> ListOfcreateCaseRequest = new list<CreateCaseRequest_DTO_V4_HUM>();
            map<String,Account> map_InteractingWith_new = new map<String,Account>();
            map<String,Account> map_InteractingAbout_new = new map<String,Account>();
            map<id,String> mapAccountRecordtype = new map <id,String>();
            set<String> setAccountId = new set<String>(); 
            InteractingAccount_DTO_HUM intAccWith = new InteractingAccount_DTO_HUM();
            InteractingAccount_DTO_HUM intAccAbout = new InteractingAccount_DTO_HUM();
            intAccAbout = request.CreateCaseRequest.InteractingAbout;
            intAccWith = request.CreateCaseRequest.InteractingWith;                                   
            CreateCaseRequest_DTO_V4_HUM caserequestDTO = new CreateCaseRequest_DTO_V4_HUM();            
            Map<String,List<Account>> unknownAccountMapToUpsert = new Map<String,List<Account>>();
            Map<Integer,String> mapUnknownId = new Map<Integer,String>();
            Map<string,Account> accNameMap = new Map<string,Account>();
            Integer iProviderKey=0;
            String sAccRecTypeWith='';
            String sAccRecTypeAbout='';
            Boolean bWithAboutSame =false;
            
            IF((intAccAbout.ID==intAccWith.ID) && (intAccAbout.IDType==intAccWith.IDType) && (intAccAbout.EntityType==intAccWith.EntityType)){
                bWithAboutSame = true;
                map_InteractingAbout_new = CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(intAccAbout,'Interacting_About');
                map_InteractingWith_new.put('Interacting_With',map_InteractingAbout_new.get('Interacting_About'));                   
            }

            
            IF(intAccWith!=null && (request.CreateCaseRequest.InteractingWith.EntityType != GLOBAL_CONSTANT_HUM.sInternalAccountType || request.CreateCaseRequest.InteractingWith.EntityType != 'Care Manager')&& bWithAboutSame==false){
                map_InteractingWith_new = CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(intAccWith,'Interacting_With');
            }
	    IF(intAccAbout!=null && (request.CreateCaseRequest.InteractingAbout.EntityType != GLOBAL_CONSTANT_HUM.sInternalAccountType || request.CreateCaseRequest.InteractingAbout.EntityType != 'Care Manager')&& bWithAboutSame==false){
                map_InteractingAbout_new = CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(intAccAbout,'Interacting_About');
            }
            
            
        IF(map_InteractingWith_new.get('Interacting_With')!=null){
                IF(map_InteractingWith_new.get('Interacting_With').Id!=null){
                    accNameMap.put('Interacting_With',map_InteractingWith_new.get('Interacting_With'));
                    
				}
        ELSE{ 
                    //call  to a new method to query account using entersrise id
                     IF(request.CreateCaseRequest.InteractingWith.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
                    {
                        IF(map_InteractingWith_new.get('Interacting_With').Enterprise_ID__c!=null){
                            accNameMap.put('Interacting_With',getAccountRecord(map_InteractingWith_new.get('Interacting_With').Enterprise_ID__c)); 
                        }ELSE IF(map_InteractingWith_new.get('Interacting_With').NPI_ID__c!=null){
                            accNameMap.put('Interacting_With',getAccountRecord(map_InteractingWith_new.get('Interacting_With').NPI_ID__c));
                        }
                    }ELSE IF(request.CreateCaseRequest.InteractingWith.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM)){
                        IF(map_InteractingWith_new.get('Interacting_With').Enterprise_ID__c!=null){
                            accNameMap.put('Interacting_With',getAccountRecord(map_InteractingWith_new.get('Interacting_With').Enterprise_ID__c)); 
                        }ELSE IF(map_InteractingWith_new.get('Interacting_With').Agent_ID__c!=null){
                            accNameMap.put('Interacting_With',getAccountRecord(map_InteractingWith_new.get('Interacting_With').Agent_ID__c));
                        }
                    }
                }
            }ELSE{
                //WHEN THE VALUE IS NULL              
                CaseServiceRestInteraction_DTO_HUM interactionWrapObjt  = new CaseServiceRestInteraction_DTO_HUM(); 
                Account unknwnAccount = new Account();
                List<Account> lstAccount = new List<Account>();
                
                interactionWrapObjt = CreateCaseRequest_H_HUM.getInteractionWrapObj(intAccWith, 'Interacting_With');
                unknwnAccount = CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObjt);
                if(!string.isNOTBlank(unknwnAccount.RecordTypeId)){
                    if(intAccWith.EntityType=='Agent'){
                       unknwnAccount.RecordTypeId = Schema.getGlobalDescribe().get(GLOBAL_CONSTANT_HUM.CASESERVICE_OBJECT_ACCOUNT).getDescribe().getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CLOSEDCASE_UNKNOWNAGENTBROKER_HUM).getRecordTypeId();       
                    }
                }
                lstAccount.add(unknwnAccount);
                accNameMap.put('Interacting_With',unknwnAccount);                
                unknownAccountMapToUpsert.put('Interacting_With',lstAccount);           	
                mapUnknownId.put(iProviderKey,interactionWrapObjt.sIntID+'-'+'Interacting_With'+'-'+interactionWrapObjt.sIntIDType);
                CaseInteractionSearch_H_HUM.insertUnknownAccounts(mapUnknownId,unknownAccountMapToUpsert);
            }
            
            IF(map_InteractingAbout_new.get('Interacting_About')!=null){
                IF(map_InteractingAbout_new.get('Interacting_About').Id!=null){
                    accNameMap.put('Interacting_About',map_InteractingAbout_new.get('Interacting_About'));
                }ELSE{                    
                     IF(request.CreateCaseRequest.InteractingAbout.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
                    {
                        IF(map_InteractingAbout_new.get('Interacting_About').Enterprise_ID__c!=null){                       
                            accNameMap.put('Interacting_About',getAccountRecord(map_InteractingAbout_new.get('Interacting_About').Enterprise_ID__c)); 
                        }ELSE IF(map_InteractingAbout_new.get('Interacting_About').NPI_ID__c!=null){                       
                            accNameMap.put('Interacting_About',getAccountRecord(map_InteractingAbout_new.get('Interacting_About').NPI_ID__c));
                        }
                    }ELSE IF(request.CreateCaseRequest.InteractingAbout.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
                    {
                        IF(map_InteractingAbout_new.get('Interacting_About').Enterprise_ID__c!='' && map_InteractingAbout_new.get('Interacting_About').Enterprise_ID__c!= null){
                            accNameMap.put('Interacting_About',getAccountRecord(map_InteractingAbout_new.get('Interacting_About').Enterprise_ID__c)); 
                        }ELSE IF(map_InteractingAbout_new.get('Interacting_About').Agent_ID__c!='' && map_InteractingAbout_new.get('Interacting_About').Agent_ID__c!= null){
                            accNameMap.put('Interacting_About',getAccountRecord(map_InteractingAbout_new.get('Interacting_About').Agent_ID__c));
                        }
                    }
                }
            }ELSE{
                //WHEN THE VALUE IS NULL
                CaseServiceRestInteraction_DTO_HUM interactionWrapObj  = new CaseServiceRestInteraction_DTO_HUM(); 
                Account unknwnAcct = new Account();
                List<Account> lstOfUnknownAccount = new List<Account>();                
                
                interactionWrapObj = CreateCaseRequest_H_HUM.getInteractionWrapObj(intAccAbout, 'Interacting_About');
                unknwnAcct = CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj);
                  if(!string.isNOTBlank(unknwnAcct.RecordTypeId)){
                    if(intAccAbout.EntityType=='Agent'){
                       unknwnAcct.RecordTypeId = Schema.getGlobalDescribe().get(GLOBAL_CONSTANT_HUM.CASESERVICE_OBJECT_ACCOUNT).getDescribe().getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CLOSEDCASE_UNKNOWNAGENTBROKER_HUM).getRecordTypeId();       
                    }
                }
                lstOfUnknownAccount.add(unknwnAcct);
                accNameMap.put('Interacting_About',unknwnAcct);
                unknownAccountMapToUpsert.put('Interacting_About',lstOfUnknownAccount);  
                mapUnknownId.put(iProviderKey,interactionWrapObj.sIntID+'-'+'Interacting_About'+'-'+interactionWrapObj.sIntIDType);
                CaseInteractionSearch_H_HUM.insertUnknownAccounts(mapUnknownId,unknownAccountMapToUpsert); 
            }
            
            IF(accNameMap.get('Interacting_With')!=NULL) caserequestDTO.sInteractingWith = accNameMap.get('Interacting_With').Id;
            IF(accNameMap.get('Interacting_About')!=NULL) caserequestDTO.sInteractingAbout = accNameMap.get('Interacting_About').Id;   
            
            String sIntercatingWithkey = String.isBlank(request.CreateCaseRequest.InteractingWith.Id) ? 'Interacting_With': request.CreateCaseRequest.InteractingWith.Id;
            String sIntercatingAboutkey = String.isBlank(request.CreateCaseRequest.InteractingAbout.Id) ? 'Interacting_About': request.CreateCaseRequest.InteractingAbout.Id;
            IF(map_InteractingWith_new.get('Interacting_With')!=null && map_InteractingWith_new.get('Interacting_With').RecordTypeId!=null)
            {
                sAccRecTypeWith = Schema.SObjectType.Account.getRecordTypeInfosById().get(map_InteractingWith_new.get('Interacting_With').recordtypeId).getName();
                caserequestDTO.sInteractingWithType = (mapRecordType.get(sAccRecTypeWith) != null) ? mapRecordType.get(sAccRecTypeWith) : '';  
            }else{
                IF(intAccWith.EntityType=='Agent'){                
                    caserequestDTO.sInteractingWithType =  'Unknown-Agent';               
                }else IF(intAccWith.EntityType=='Provider'){
                    caserequestDTO.sInteractingWithType =  'Unknown-Provider'; 
                } 
            } 
            
            IF(map_InteractingAbout_new.get('Interacting_About')!=null && map_InteractingAbout_new.get('Interacting_About').recordtypeId!=null)
            {
                sAccRecTypeAbout = Schema.SObjectType.Account.getRecordTypeInfosById().get(map_InteractingAbout_new.get('Interacting_About').recordtypeId).getName();
                caserequestDTO.sInteractingAboutType = (mapRecordType.get(sAccRecTypeAbout) != null) ? mapRecordType.get(sAccRecTypeAbout) : '';
            }else{
                IF(intAccAbout.EntityType=='Agent'){                
                    caserequestDTO.sInteractingAboutType =  'Unknown-Agent';               
                }else if(intAccAbout.EntityType=='Provider'){
                    caserequestDTO.sInteractingAboutType =  'Unknown-Provider'; 
                }   
            }
            caserequestDTO.caseCreateRequest = request.CreateCaseRequest.CaseDetails;
            caserequestDTO.sInteractionId = '';  
            If(request.CreateCaseRequest.InteractingWith.EntityType == GLOBAL_CONSTANT_HUM.sInternalAccountType)
            {
                caserequestDTO.sInteractingWithType = GLOBAL_CONSTANT_HUM.sInternalAccountType;
                caserequestDTO.sInteractingWithName = request.CreateCaseRequest.InteractingWith.AccountName;
            }
			else if(request.CreateCaseRequest.InteractingWith.EntityType == 'Care Manager' ){
				caserequestDTO.sInteractingWithType = 'Care Manager';
                caserequestDTO.sInteractingWithName = request.CreateCaseRequest.InteractingWith.AccountName;
			}
			else{
                if(map_InteractingWith_new.get('Interacting_With')!=null)
                {
                    caserequestDTO.sInteractingWithName = map_InteractingWith_new.get('Interacting_With').Name;
                }else{
                    caserequestDTO.sInteractingWithName = '';
                }
            }	
            ListOfcreateCaseRequest.add(caserequestDTO);
            ListOfreponse = CreateCaseRestBulkified_D_V4_HUM.createCases(ListOfcreateCaseRequest, false);
            response = ListOfreponse[0];
            
        }catch(Exception ex){          	
            HUMExceptionHelper.logErrors(ex,'CreateUpdateCaseServiceRest_D_HUM','createCaseRecord');           
            response = getErrorLogInstance(GLOBAL_CONSTANT_HUM.Error_Code_UpdateCasev4,'An Error Occured While Creating the Case',false);
        }
        return response;
    } 
    /*  
* Method name : getAccountRecord
* Description : This method Is used to return Account.
* Return Type :  Account
* Parameter :   String IntWithAboutType - NPIID/AGENTID/ENTERPRISEID  
*/   
    private static Account getAccountRecord(String IntWithAboutType){
        
        ACCOUNT retValueAcnt = new Account();         
        ACCOUNT[] retAccount= [Select Id,recordtypeId,recordtype.name,NPI_ID__c,Account_External_ID__c,Enterprise_ID__c,Name,Agent_ID__c FROM Account where (Enterprise_ID__c=:IntWithAboutType OR NPI_ID__c=:IntWithAboutType OR Agent_ID__c=:IntWithAboutType) AND ETL_Record_Deleted__c = false LIMIT 1];//sagar
        
        IF(retAccount.size()>0){
            retValueAcnt=retAccount[0];
        }else{
            retValueAcnt = null; 
        }
        return retValueAcnt;
    }
    
    /*  
* Method name : getErrorLogInstance
* Description : This method will be used to retuen error response instance.
* Return Type :  CaseDetailResponse_DTO_V4_HUMInteractingAccount_DTO_HUM
* Parameter :   StatusCode , String , Boolean 
*/
    private static CaseDetailResponse_DTO_V4_HUM getErrorLogInstance(String sErrorCode,String sErrorMessage,Boolean bSuccess)
    {
        CaseDetailResponse_DTO_V4_HUM errorRespnse = new CaseDetailResponse_DTO_V4_HUM();
        errorRespnse.ErrorCode = sErrorCode;
        errorRespnse.ErrorMessage = sErrorMessage;
        errorRespnse.Success = bSuccess;        
        return errorRespnse;    
    }
}