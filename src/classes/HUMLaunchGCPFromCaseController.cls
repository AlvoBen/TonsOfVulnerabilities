/*******************************************************************************************************************************
Apex Class Name : HUMLaunchGCPFromCaseController 
Version         : 1.8
Created On      : 7/21/2014
Function        : This class sets the parameters for launching GCP CCP
Test Class      : HUMLaunchGCPFromCaseControllerTest
Modification Log: 
 * Developer Name           Code Review                 Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pranali Choudhari         17819                      07/21/2014                 Original Version
 * Pranali Choudhari         17819                      07/24/2014                 Added extra parameters to be set
 * Pranali Choudhari         17916                      09/10/2014                 Moved URL to custom setting and changed some parameters  
 * Pranali Choudhari         17916                      09/14/2014                 Changed TaxID,InteractingPlatform logic
 * Pranali Choudhari         18083                      09/19/2014                 Changed logic for obtaining decrypted values
 * Pranali Choudhari         18083                      09/19/2014                 Changed logic for obtaining policy values
 * Pranali Choudhari         18083                      09/22/2014                 Accomodated SourceProviderID CR 
 * Pranali Choudhari         18083                      09/23/2014                 Added review comments
 * Manish Kumar              18575                      10/15/2014                 Modified code for Defect#158177
 * Pranali Choudhari         20303                      1/12/2015                  Fixed defect 160247
 * Chaitanya Kumar           20303                      1/13/2015                  Updated code as per review comments # 20303
 * Vishal Verma              21405                      03/04/2014                 Defect : 172693.0001: Added try catch in Constructor to catch any 
                                                                                   exception and then show it on HUMLaunchGCPFromCase 
 * Praveen Kumar Parimi      22420                      04/15/2015                 Added logic to send the ContractID for FIMMAS members
 * Vishal Verma              27889                      11/12/2015                 REQ - 237002  : Removing Decryption from Apex. 
 * Prudhvi Pamarthi          112823                     07/21/2017                 REQ- 323449 CRM - CCP : Member Data Issues/Discrepancy
 * Suresh Gottipati          240518                     05/31/2018                 REQ - 366200 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field                                 
 * Santhi Mandava            348723                     04/02/2019                 Cleanup cipher cloud references
 * Santhi Mandava                                       04/15/2019                 Replaced "Member_Id_Value__c" with Name
 ****************************************************************************************************************************/
/* 
    This class has been made without sharing because throughout this class we are sending case and account parameters to GCP. 
    Account and Case both have private sharing setting, hence, if a user without access to account, tries to launch GCP, parameters would not be sent.
    Without sharing class will solve the issue. 
*/
public without sharing class HUMLaunchGCPFromCaseController 
{
    private List<Policy_Member__c> lstPolicyMem = new List<Policy_Member__c>();

    /*
     * Method Name : HUMLaunchGCPFromCaseController
     * Description : Constructor of the class
     * Return type : None
     * Paramater   : Standard controller
     */ 
    public HUMLaunchGCPFromCaseController (ApexPages.StandardController controller) 
    {
        try
        { 
            HUMExceptionHelper.bRestrictLog = true;
            oCase = (Case)controller.getRecord();
            getMandatoryParams();
        }
        catch(Exception e)
        {            
            listErrorLogs = HUMExceptionHelper.logErrors(e,'HUMLaunchGCPFromCaseController','HUMLaunchGCPFromCaseController');
        }
    }

    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs == null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
    
    public String sGCPUrl{get;set;}

    public Id sCaseId{get;set;}

    public Case oCase{get;set;}

    public boolean bIsPolicyMemberPresent {get;set;}

    public String sCaseOrigin{get;set;}

    public String sInteractingAboutMemberIdBase{get;set;}

    public String sInteractingWithMemberIdBase{get;set;}

    public String sInteractingAboutCovergeEffDate{get;set;}

    public String sInteractingWithCovergeEffDate{get;set;}

    public String sCallingApplication{get;set;}

    public String sMode{get;set;}

    public String sInteractingAboutId{get;set;}

    public String sInteractingWithId{get;set;}

    public String sInteractingAboutType{get;set;}

    public String sInteractingWithType{get;set;}

    public String sInteractingAboutGroupId{get;set;}

    public String sInteractingWithGroupId{get;set;}

    public String sInteractingAboutPlatform{get;set;}

    public String sInteractingWithPlatform{get;set;}

    public String sInteractingAboutSubType{get;set;}

    public String sInteractingWithSubType{get;set;}

    public String sInteractingAboutAgentId{get;set;}

    public String sInteractingWithAgentId{get;set;}

    public String sInteractingAboutProviderId{get;set;}

    public String sInteractingAboutSourcePlatformCd{get;set;}

    public String sInteractingWithSourcePlatformCd   {get;set;}

    public String sInteractingWithProviderId{get;set;}  

    public String sInteractingWithFirstName{get;set;}

    public String sInteractingWithLastName{get;set;}

    public String sInteractingWithDateOfBirth{get;set;}

    public String sInteractingWithGroupName{get;set;}

    public String sInteractingAboutProvFirstName{get;set;}

    public String sInteractingAboutProvLastName{get;set;}

    public String sInteractingAboutProvFullName{get;set;}

    public String sInteractingWithProvFirstName{get;set;}

    public String sInteractingWithProvLastName{get;set;}

    public String sInteractingWithProvFullName{get;set;}

    public String sInteractingAboutMemberContractId{get;set;}
    
    /*
     * Method Name : getMandatoryParams
     * Description : Method to assign parameters to launch GCP/CCP - Origin and Interacting Ids
     * Return type : void
     * Paramater   : NA
     */ 

    public void getMandatoryParams()
    {
        //Set Mandatory parameters
        oCase = [Select Id,Origin,Interacting_With__r.isPersonAccount,Account.Enterprise_ID__c,Interacting_With__c,Interacting_With__r.Name,Interacting_With__r.Enterprise_ID__c,Interaction_ID__c,AccountId, Account.RecordType.Name,
                Interacting_With__r.RecordType.Name,Account.Source_Customer_Key__c,Interacting_With__r.Source_Customer_Key__c,Policy_Member__r.Policy__r.Platform__c,
                Account.Name, Interacting_With__r.Individual_First_Name__c,Interacting_With__r.Individual_Last_Name__c,Account.Individual_First_Name__c,Account.Individual_Last_Name__c,Account.Agent_Type__c,Interacting_With__r.Agent_Type__c,
                Account.Source_Platform_Code__c,Interacting_With__r.Source_Platform_Code__c,Account.Source_Provider_Id__c,Interacting_With__r.Source_Provider_Id__c,Interacting_With__r.FirstName,
                Interacting_With__r.LastName,Interacting_With__r.BirthDate__c,Interacting_With__r.Group_Number__c from Case where Id = : oCase.Id];

        HUM_Webservice_Callout__c cstGCP =  HUM_Webservice_Callout__c.getInstance('HUMLaunchGCP');
        sGCPUrl = cstGCP.End_Point_URL__c;
        sCallingApplication = HUMConstants.HUM_LaunchGCPCCP_CALLINGAPP;
        sMode = HUMConstants.HUM_LaunchGCPCCP_MODE;
            
        if(oCase.Origin == HUMConstants.HUM_LaunchGCPCCP_INBOUNDCALL)
        {
            sCaseOrigin = HUMConstants.HUM_LaunchGCPCCP_INBOUNDCODE;
        }
        else if(oCase.Origin == HUMConstants.HUM_LaunchGCPCCP_OUTBOUNDCALL)
        {
            sCaseOrigin = HUMConstants.HUM_LaunchGCPCCP_OUTBOUNDCODE;
        }

        //Set InteractingAboutID and InteractingWithID
        if(oCase.Account.RecordType.Name != HUMConstants.HUM_LaunchGCPCCP_GROUP) sInteractingAboutId = oCase.Account.Enterprise_ID__c;

        if(oCase.Interacting_With__r.RecordType.Name !=  HUMConstants.HUM_LaunchGCPCCP_GROUP) sInteractingWithId = oCase.Interacting_With__r.Enterprise_ID__c;
            
        populateMandatoryParams();
    }
    
    
    /*
     * Method Name : populateMandatoryParams
     * Description : Method to populate parameters to launch GCP/CCP 
     * Return type : void
     * Paramater   : NA
     */ 
    private void populateMandatoryParams()
    {
        getInteractingWithAndAbout();
        getPolicyValues();
        getProviderAndGroupDetails();
        getAgentDetails();
        getInputDetailsAdditional();
    }
    

    /*
     * Method Name : getInteractingWithAndAbout
     * Description : Method to assign parameters to launch GCP/CCP - Interacting TYpes
     * Return type : void
     * Paramater   : NA
     */ 

    public void getInteractingWithAndAbout()
    {
        Map<String,String> mapRecTypeToValue = new Map<String, String>{HUMConstants.HUM_LaunchGCPCCP_MEMBER => HUMConstants.HUM_LaunchGCPCCP_KM, 
                                                                       HUMConstants.HUM_LaunchGCPCCP_GROUP => HUMConstants.HUM_LaunchGCPCCP_KE,
                                                                       HUMConstants.HUM_LaunchGCPCCP_PROVIDER => HUMConstants.HUM_LaunchGCPCCP_KP,
                                                                       HUMConstants.HUM_LaunchGCPCCP_AGENTBROKER => HUMConstants.HUM_LaunchGCPCCP_KA,
                                                                       HUMConstants.HUM_LaunchGCPCCP_UNKNOWNMEMBER => HUMConstants.HUM_LaunchGCPCCP_UM,
                                                                       HUMConstants.HUM_LaunchGCPCCP_UNKNOWNGROUP => HUMConstants.HUM_LaunchGCPCCP_UE, 
                                                                       HUMConstants.HUM_LaunchGCPCCP_UNKNOWAGENTBROKER => HUMConstants.HUM_LaunchGCPCCP_UA, 
                                                                       HUMConstants.HUM_LaunchGCPCCP_UNKNOWNPROVIDER => HUMConstants.HUM_LaunchGCPCCP_UP};

        if(mapRecTypeToValue.containsKey(oCase.Account.RecordType.Name) && !oCase.Account.RecordType.Name.contains(HUMConstants.HUM_LaunchGCPCCP_UNKNOWN)) 
        {
            sInteractingAboutType = mapRecTypeToValue.get(oCase.Account.RecordType.Name);
        }
            
        if(mapRecTypeToValue.containsKey(oCase.Interacting_With__r.RecordType.Name)) sInteractingWithType = mapRecTypeToValue.get(oCase.Interacting_With__r.RecordType.Name);       
    }

    /*
     * Method Name : getPolicyValues
     * Description : Split Method to assign policy parameters to launch GCP/CCP
     * Return type : void
     * Paramater   : NA
     */    
    public void getPolicyValues()
    {       
        getMemberDetails();
        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_GROUP)
        {
            List<Policy__c> lstPolicy = [Select Id, Platform__c from Policy__c where Group_Name__c = :oCase.Interacting_With__c ORDER BY CreatedDate DESC NULLS LAST];
            if(!(lstPolicy.isEmpty())) sInteractingWithPlatform = lstPolicy[0].Platform__c; 
        }
        if(oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_GROUP)
        {
            List<Policy__c> lstPolicy = [Select Id, Platform__c from Policy__c where Group_Name__c = :oCase.Account.Id ORDER BY CreatedDate DESC NULLS LAST];
            if(!(lstPolicy.isEmpty())) sInteractingAboutPlatform = lstPolicy[0].Platform__c; 
        }   
    }    
    
    
     /*
     * Method Name : getMemberDetails
     * Description : Method to assign parameters to launch GCP/CCP - for Member type
     * Return type : void
     * Paramater   : NA
     */ 
    private void getMemberDetails()
    {
        //Set CovergaeEffectivedate,MemberIdbase and PolicyMemberFlag and platform values
        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_MEMBER || oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_MEMBER)
        {
            if(oCase.Policy_Member__c == null)
            {
                bIsPolicyMemberPresent = false;
                if(oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_MEMBER)
                {
                    lstPolicyMem = [Select Id,CreatedDate,Name,Effective_Date__c from Policy_Member__c where Member__c =:oCase.Account.Id AND ETL_RECORD_DELETED__C=false ORDER BY CreatedDate DESC NULLS LAST ];
                    if(!(lstPolicyMem.isEmpty()))
                    {
                        sInteractingAboutMemberIdBase = lstPolicyMem[0].Name;
                        sInteractingAboutCovergeEffDate = lstPolicyMem[0].Effective_Date__c;                    
                    }  
                }
                if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_MEMBER)
                {
                    lstPolicyMem = [Select Id,CreatedDate,Name,Effective_Date__c from Policy_Member__c where Member__c =:oCase.Interacting_With__r.Id ORDER BY CreatedDate DESC NULLS LAST ];
                    if(!(lstPolicyMem.isEmpty()))
                    {
                        sInteractingWithMemberIdBase = lstPolicyMem[0].Name;
                        sInteractingWithCovergeEffDate = lstPolicyMem[0].Effective_Date__c;                    
                    }  
                }
            }
            else
            {
                bIsPolicyMemberPresent = true;
                String sVarcontractIDFM = '';
                Policy_Member__c objPolicyMem = [Select Id,Name,Effective_Date__c,Policy_Platform__c from Policy_Member__c where Id=:oCase.Policy_Member__c limit 1];
                
                //Added the logic for FIMMAS members to pass their ContractID instead of Client ID.   This Contract ID is being fectched from Member_ID__C object.
                if (objPolicyMem.Policy_Platform__c == HUMConstants.HUM_LaunchGCPCCP_FIMMASPLATFORM)
                {
                                     
                    for(Member_ID__c mi : [Select Id,Type__c, name from Member_ID__c where Policy_Member__c =: objPolicyMem.ID])          
                        {
                            if(mi.Type__c == HUMConstants.HUM_LaunchGCPCCP_CONTRACTIDTYPE) 
                                {
                                    sVarcontractIDFM = mi.Name;
                                    break; 
                                }
                        }
                   } 
                    
                
                if(oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_MEMBER)
                {
                    if(objPolicyMem.Policy_Platform__c != HUMConstants.HUM_LaunchGCPCCP_FIMMASPLATFORM)
                    { 
                        sInteractingAboutMemberIdBase = objPolicyMem.Name;
                        sInteractingAboutCovergeEffDate = objPolicyMem.Effective_Date__c;
                    }
                    else if (objPolicyMem.Policy_Platform__c == HUMConstants.HUM_LaunchGCPCCP_FIMMASPLATFORM)
                    {
                         sInteractingAboutMemberContractId = sVarcontractIDFM;
                         sInteractingAboutMemberIdBase = sInteractingAboutMemberContractId;  
                         sInteractingAboutCovergeEffDate = objPolicyMem.Effective_Date__c;   
                    }
                }           
                if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_MEMBER)
                {
                    sInteractingWithMemberIdBase = objPolicyMem.Name;
                    sInteractingWithCovergeEffDate = objPolicyMem.Effective_Date__c; 
                }         
            }     
        }
    }

    /*
     * Method Name : getProviderAndGroupDetails
     * Description : Method to assign parameters to launch GCP/CCP - Provider, Group and Unknown Types
     * Return type : void
     * Paramater   : NA
     */ 
    public void getProviderAndGroupDetails()
    {
        //Set InteractingAboutSourceProviderID and InteractingWithSourceProviderId
        if(oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_PROVIDER)
        {            
            sInteractingAboutProvFirstName = oCase.Account.Individual_First_Name__c;
            sInteractingAboutProvLastName = oCase.Account.Individual_Last_Name__c;
            sInteractingAboutProvFullName = oCase.Account.Name;
        }

        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_PROVIDER)
        {
            sInteractingWithProvFirstName = oCase.Interacting_With__r.Individual_First_Name__c;
            sInteractingWithProvLastName = oCase.Interacting_With__r.Individual_Last_Name__c;
            sInteractingWithProvFullName = oCase.Interacting_With__r.Name;  
        }          
    }
    
    /*
     * Method Name : getUnknownAccountDetails
     * Description : Method to assign parameters to launch GCP/CCP - for Unknown Accounts
     * Return type : void
     * Paramater   : NA
     */ 
    public void getUnknownAccountDetails()
    {
        //Set InteractingWithFirstName and InteractingWithLastName
        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_UNKNOWNMEMBER || oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_UNKNOWAGENTBROKER || 
                oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_UNKNOWNPROVIDER)
        {           
            sInteractingWithFirstName = oCase.Interacting_With__r.FirstName;
            sInteractingWithLastName = String.isBlank(oCase.Interacting_With__r.LastName) ? oCase.Interacting_With__r.Name : oCase.Interacting_With__r.LastName;             
        }

        //Set InteractingWithDateofBirth
        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_UNKNOWNMEMBER) sInteractingWithDateOfBirth = oCase.Interacting_With__r.Birthdate__c;

        //Set InteractingWithGroupName
        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_UNKNOWNGROUP) sInteractingWithGroupName = oCase.Interacting_With__r.Group_Number__c;
        
    }

    /*
     * Method Name : getAgentDetails
     * Description : Method to assign agent parameters to launch GCP/CCP
     * Return type : void
     * Paramater   : NA
     */ 
    public void getAgentDetails()
    {
        //Set InteractingAboutSubType and InteractingWithSubType
        if(oCase.Account.Agent_Type__c == HUMConstants.HUM_LaunchGCPCCP_AGENCY)
        {
            sInteractingAboutSubType = HUMConstants.HUM_LaunchGCPCCP_A;
        }
        else if(oCase.Account.Agent_Type__c == HUMConstants.HUM_LaunchGCPCCP_BROKER)
        {
            sInteractingAboutSubType = HUMConstants.HUM_LaunchGCPCCP_B;
        }

        if(oCase.Interacting_With__r.Agent_Type__c == HUMConstants.HUM_LaunchGCPCCP_AGENCY)
        {
            sInteractingWithSubType = HUMConstants.HUM_LaunchGCPCCP_A;
        }
        else if(oCase.Interacting_With__r.Agent_Type__c == HUMConstants.HUM_LaunchGCPCCP_BROKER)
        {
            sInteractingWithSubType = HUMConstants.HUM_LaunchGCPCCP_B;       
        }

        //Set InteractingAboutAgentId && InteractingWithAgentId
        if(oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_AGENTBROKER) sInteractingAboutAgentId = getTaxIdValues(oCase.AccountId);

        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_AGENTBROKER) sInteractingWithAgentId = getTaxIdValues(oCase.Interacting_With__c);     
    }

    /*
     * Method Name : getInputDetailsAdditional
     * Description : Split Method to assign additional parameters to launch GCP/CCP
     * Return type : void
     * Paramater   : NA
     */ 
    public void getInputDetailsAdditional()
    {
        //Set InteractingABoutGroupId and InteractingAboutPlatform
        if(oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_GROUP) sInteractingAboutGroupId = oCase.Account.Source_Customer_Key__c;

        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_GROUP) sInteractingWithGroupId = oCase.Interacting_With__r.Source_Customer_Key__c;             

        //Set InteractingAboutProviderId and InteractingWithProviderId
        if(oCase.Account.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_PROVIDER)
        {
            sInteractingAboutProviderId = getTaxIdValues(oCase.AccountId);            
            sInteractingAboutSourcePlatformCd = oCase.Account.Source_Platform_Code__c;     
        }       

        if(oCase.Interacting_With__r.RecordType.Name == HUMConstants.HUM_LaunchGCPCCP_PROVIDER)
        {
            sInteractingWithProviderId  = getTaxIdValues(oCase.Interacting_With__c);
            sInteractingWithSourcePlatformCd = oCase.Interacting_With__r.Source_Platform_Code__c;      
        }            
    }   

    /*
     * Method Name : getTaxIdValues
     * Description : Method to get TaxID values to set for InteractingAgentId and InteractingProviderID
     * Return type : String
     * Paramater   : Id
     */ 
    public String getTaxIdValues(Id accId)
    {           
        String sTaxId = '';
        List<ConsumerID__c> lstConsumerId = [Select  Consumer_ID__c, Consumer_External_ID__c, Account__c From ConsumerID__c where Account__c =: accId limit 1];
        if(!lstConsumerId.isEmpty())
        {            
            sTaxId = lstConsumerId[0].Consumer_ID__c;
        }
        return sTaxId;
    }
}