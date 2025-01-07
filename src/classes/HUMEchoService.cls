/****************************************************************************
Apex Class Name  : HUMEchoService
Version          : 9.0 
Created Date     : JUNE 06 2014
Function         : Webservice class used for generating custom wsdl for Echo interface
TestClass Name   : HUMEchoServiceTest
Modification Log :
 *  Modification ID     Developer              Code Review           Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Pranali Choudhari       17465                06/06/2014           Original Version
 *          2.0         Pranali Choudhari       17465                06/28/2014           Modified the input and output parameters
 *          3.0         Pranali Choudhari       17465                07/01/2014           Added logic for fetching interaction details
 *          4.0         Pranali Choudhari       17465                07/02/2014           Added error handling
 *          5.0         Pranali Choudhari       17465                07/22/2014           Changed the logic to include the new requirements
 *          6.0         Pranali Choudhari       17465                07/24/2014           Made changes to Group and Member request logic
 *          7.0         Pranali Choudhari       17465                07/31/2014           Made changes to queries
 *          8.0         Pranali Choudhari       17465                08/21/2014           Changed code according to best practices
 *          9.0         Pranali Choudhari       18313                10/07/2014           Incorporated changes according to review comments
 *          10.0        Vishal Verma                                 05/18/2015           CA - 5102605  Added Start Date and End Date filters for echo service in method setEchoOutput(line 115 - line 152) 
                                                                                                                                      and added a new method parseStringToDate
 *          11.0        Vishal Verma            25256                08/04/2015           CA - 5102605 : Changed the date format (method parseStringToDate) to accept date in YYYY-MM-DD format only.                                                                                           
 *          12.0        Prachi Bhasin           33337                07/18/2016           Changes made as part of REQ- 270349     
 *          13.0        Chaitanya Vegendla      82924                04/05/2017           Modified code as per CTCI Data Model- REQ  306329                                                                               
 *          14.0        Santhi Mandava                               12/31/2018           Removed dpaas calls 
 **********************************************************************************************************************************/


global with sharing class HUMEchoService
{

    /**
     * The method is exposed to the external system for getting the interaction details.
     *
     * @param     inputEchoRequest                HUMEchoRequestDTO
     * @return    NA                                            
     */ 
    webservice static HUMEchoOutputDTO getInteractionDetails(HUMEchoRequestDTO inputEchoRequest)
    {
        return fetchInteractionDetails(inputEchoRequest);
    }

    /**
     * Fetches interaction details for the given Enterprise ID,Type and InteractingWithType
     *
     * @param     inputEchoRequest               HUMEchoRequestDTO
     * @return    NA                                            
     */ 
    private static HUMEchoOutputDTO fetchInteractionDetails(HUMEchoRequestDTO inputEchoRequest)
    {

        HUMEchoOutputDTO echoOutput;
        try
        {
            if(inputEchoRequest!=null 
                    && !(String.isBlank(inputEchoRequest.sType)) && !(String.isBlank(inputEchoRequest.sInteractingWithType))
                    && (!String.isBlank(inputEchoRequest.sEnterpriseID) || !String.isBlank(inputEchoRequest.sInquiredAbtID)))
            {
                id iAcctId = fetchAccId(inputEchoRequest);
                if(iAcctId != null) // If Account exists
                {
                    echoOutput=  setEchoOutput(iAcctId,inputEchoRequest);
                }
                else // If Account doesn't exist return error
                {
                    echoOutput = getErrorDetails(null,false,System.Label.ECHOSERVICE_NORECORDFOUND_HUM);
                }
            }
            else //If all input paramters are blank, send error
            {
                echoOutput =  getErrorDetails(null,false,System.Label.ECHOSERVICE_MISSINGINPUT_HUM);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMEchoService','fetchInteractionDetails');
            echoOutput = getErrorDetails(null,false,System.Label.ECHOSERVICE_GENERICERROR_HUM+':'+e.getMessage());
        }   
        return echoOutput;
    }

    /**
     * Fetches the Account details
     *
     * @param     inputEchoRequest               HUMEchoRequestDTO
     * @return    Id                                            
     */ 
    private static Id fetchAccId(HUMEchoRequestDTO inputEchoRequest)
    {
        ID accID;

        List<Account> lstAccount = new List<Account>();
        if(!String.isBlank(inputEchoRequest.sEnterpriseID)) //Check for scenario where enterpriseID is provided but InquiredAboutID is blank
        {
            lstAccount = [Select Id, Enterprise_ID__c,Account_External_ID__c from Account where  Enterprise_ID__c = :inputEchoRequest.sEnterpriseID AND RecordType.Name = :inputEchoRequest.sType limit 1];
            if(!lstAccount.isEmpty()) { accID =  lstAccount[0].Id; }                        
        }
        else if(String.isBlank(inputEchoRequest.sEnterpriseID) && !String.isBlank(inputEchoRequest.sInquiredAbtID)) //Check if enterpriseID is blank but InquiredAboutID is not
        {
            if(inputEchoRequest.sType == System.Label.LAUNCHGCPCCP_MEMBER_HUM)
            {
                List<Policy_Member__c> lstPolMem = new List<Policy_Member__c>();               
                String sMemberNameKey = inputEchoRequest.sInquiredAbtID;
                sMemberNameKey = sMemberNameKey +'*';
                String varsType= inputEchoRequest.sType; 
                String SOSLquery = 'FIND \'' + sMemberNameKey +'\' IN Name FIELDS RETURNING Policy_Member__c(Id, Member__c Where Member__r.RecordType.Name = :varsType Limit 1)';
                List<List<sObject>> lstPolMemsearch = search.query(SOSLquery);
                lstPolMem = ((List<Policy_Member__c>)lstPolMemsearch[0]);
                if(!lstPolMem.isEmpty()) { accID =  lstPolMem[0].Member__c; }                                           
            }
            else if(inputEchoRequest.sType == System.Label.LAUNCHGCPCCP_GROUP_HUM)
            {
                lstAccount = [Select Id, Account_External_ID__c from Account where Group_Number__c =:inputEchoRequest.sInquiredAbtID AND RecordType.Name = :inputEchoRequest.sType limit 1];
                if(!lstAccount.isEmpty()) { accID =  lstAccount[0].Id; }                                    
            }
        }
        return accID;
    }

    /**
     * Method which gets called from getInteractionDetails method to fetch the interaction and case details
     *
     * @param     iAccountId                Id
     * @param     inputEchoRequest          HUMEchoRequestDTO
     * @return    HUMEchoOutputDTO                                          
     */ 
    private static HUMEchoOutputDTO setEchoOutput(Id iAccountId,HUMEchoRequestDTO inputEchoRequest )
    {
        HUMEchoOutputDTO echoOut = new HUMEchoOutputDTO();

        Date dtFromDate = null;
        Date dtToDate = null;

        if(!String.isBlank(inputEchoRequest.sStartDate))  dtFromDate = parseStringToDate(inputEchoRequest.sStartDate); 
        if(!String.isBlank(inputEchoRequest.sEndDate))  dtToDate = parseStringToDate(inputEchoRequest.sEndDate).addDays(1); 

        Interaction_Member__c oIntMem = new Interaction_Member__c();
        List<Interaction_Member__c> lstInteractionMem = getInteractionMemberDetails(inputEchoRequest,dtFromDate,dtToDate,iAccountId);

        if(!lstInteractionMem.isEmpty())
        {
            oIntMem = lstInteractionMem[0];
            Id intId = oIntMem.Interaction__c;  

            List<Case_Interaction__c> lstCaseInteraction = getCaseInteractionRecords(intID,dtFromDate,dtToDate);

            if(lstCaseInteraction.isEmpty()) //If no case associated with latest interaction, return the interaction details only
            {
                echoOut.sInteractionID = oIntMem.Interaction__r.Name;
                echoOut.sLastmodifiedby = oIntMem.Interaction__r.LastModifiedBy.Network_User_Id__c;
                echoOut.sLastModifiedDateInteraction = String.valueOf(oIntMem.Interaction__r.LastModifiedDate);
                echoOut.bIsSuccess = true;
                echoOut.sReferenceType = System.Label.ECHOSERVICE_REFERENCETYPE_HUM;

            }   
            else //If case assoaciated, return case & interaction details 
            {       

                Case_Interaction__c ocaseInt = lstCaseInteraction[0];// Fetch the first Case Interaction record
                echoOut = assignEchoOutPut(ocaseInt,oIntMem,echoOut);

            }

        }   
        else //else return failure message to the service
        {
            echoOut = getErrorDetails(null,false,System.Label.ECHOSERVICE_NORECORDFOUND_HUM);
        }

        return echoOut;
    }

    /**
     * Assign Response parameters 
     *
     * @param     ocaseInt              Case_Interaction__c
     * @param     oIntMem               Interaction_Member__c
     * @param     echoOut               HUMEchoOutputDTO
     * @return    HUMEchoOutputDTO                                          
     */ 
    private static HUMEchoOutputDTO assignEchoOutPut(Case_Interaction__c ocaseInt,Interaction_Member__c oIntMem ,HUMEchoOutputDTO echoOut )
    {

        echoOut.sCaseId = ocaseInt.Case__r.CaseNumber;
        echoOut.sCreatedBy = ocaseInt.Case__r.CreatedBy.Network_User_Id__c;     
        if(ocaseInt.Case__r.CTCI_List__c!=null) 
        {
            if(ocaseInt.Case__r.CTCI_List__r.Intent__c!=null)
                echoOut.sReason = ocaseInt.Case__r.CTCI_List__r.Intent__r.Name;

            echoOut.sCategory = ocaseInt.Case__r.CTCI_List__r.Classification__r.Name;               
        }

        echoOut.sInteractionID = oIntMem.Interaction__r.Name;
        echoOut.sLastmodifiedby = ocaseInt.Case__r.LastModifiedBy.Network_User_Id__c;
        echoOut.sLastModifiedDateCase = String.ValueOf(ocaseInt.Case__r.LastModifiedDate);
        echoOut.sLastModifiedDateInteraction = String.ValueOf(oIntMem.LastModifiedDate);
        echoOut.sSite = ocaseInt.Case__r.Service_Center__c;
        echoOut.sCreatedTeamID = null; 
        echoOut.sDepartment =ocaseInt.Case__r.Department__c;
        echoOut.sLastUpdatedTeamName = ocaseInt.Case__r.LastModifiedby_Queue__c;
        echoOut.sDisposition = System.Label.ECHOSERVICE_INTERFACEDISPOSITION_HUM;
        echoOut.sReferenceType = System.Label.ECHOSERVICE_REFERENCETYPE_HUM;
        echoOut.bIsSuccess = true;
        return echoOut;

    }



    /**
     * Return Case Interaction  Records
     *
     * @param     intId                 Interaction ID
     * @param     dtFromDate            Last Modified From Date
     * @param     dtToDate              Last Modified To Date
     * @return    List<Case_Interaction__c>                                         
     */ 
    private static List<Case_Interaction__c> getCaseInteractionRecords(Id intId,Date dtFromDate, Date dtToDate)
    {
        List<Case_Interaction__c> lstCaseInteraction;

        //Fetch the latest case & interaction details from junction object between case and interaction                  
        String sCaseIntQuery = 'Select Id,Case__c,LastModifiedById, LastModifiedBy.Name,Case__r.LastModifiedDate,Case__r.CaseNumber,Case__r.CTCI_List__c,Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name,'+
                'Case__r.Service_Center__c, Case__r.Owner_Queue__c, Case__r.LastModifiedById,Case__r.CreatedById, Case__r.LastModifiedBy.Network_User_Id__c,'+
                'Case__r.LastModifiedBy.Name, Case__r.CreatedBy.Name, Case__r.CreatedBy.Network_User_Id__c, Case__r.Department__c, Case__r.LastModifiedby_Queue__c '+
                'from Case_Interaction__c where Interaction__c = :  intId ';


        if(dtFromDate != null)  sCaseIntQuery = sCaseIntQuery + ' And  Case__r.LastModifiedDate >= : dtFromDate ';  // If Start date is provided 

        if(dtToDate != null)  sCaseIntQuery = sCaseIntQuery + ' And  Case__r.LastModifiedDate < : dtToDate ';  // If End Date is provided 

        sCaseIntQuery =  sCaseIntQuery + ' order by Case__r.LastModifiedDate DESC NULLS LAST ';         
        lstCaseInteraction = database.query(sCaseIntQuery);         
        return lstCaseInteraction;

    }


    /**
     * Return Interaction Member Records
     *
     * @param     inputEchoRequest           HUMEchoRequestDTO
     * @return    HUMEchoOutputDTO                                          
     */ 
    private static List<Interaction_Member__C> getInteractionMemberDetails(HUMEchoRequestDTO inputEchoRequest,Date dtFromDate, Date dtToDate,ID iAccountId)
    {

        List<Interaction_Member__c> lstInteractionMem;

        String sIntWithtype = inputEchoRequest.sInteractingWithType ;
        String sIntMember = 'Select Id,Name, Interaction__r.Interacting_With_type__c,LastModifiedDate, Interacting_About__c,Interaction__r.LastModifiedDate,'+
                'Interaction__c,Interaction__r.LastModifiedBy.Name, Interaction__r.Name, Interaction__r.LastModifiedBy.Network_User_Id__c '+
                ' from Interaction_Member__c where Interaction__r.Interacting_With_type__c = :sIntWithtype '+                                      
                ' AND Interacting_About__c =:iAccountId ';

        if(dtFromDate != null) sIntMember = sIntMember + ' And  Interaction__r.LastModifiedDate >= : dtFromDate ';  // If Start date is provided 

        if(dtToDate != null) sIntMember = sIntMember + ' And  Interaction__r.LastModifiedDate < : dtToDate ';   // If End Date is provided

        sIntMember =  sIntMember + 'order by Interaction__r.LastModifiedDate DESC NULLS LAST ';
        lstInteractionMem = database.query(sIntMember);
        return lstInteractionMem;

    }


    /**
     * Generates error messages for any failure
     *
     * @param     sErrorCode                String
     * @param     bIsSuccess                Boolean
     * @param     sErrorMessage             String
     * @return    HUMEchoOutputDTO                                          
     */ 
    private static HUMEchoOutputDTO getErrorDetails(String sErrorCode, Boolean bIsSuccess, String sErrorMessage)
    {
        HUMEchoOutputDTO echoOutput = new HUMEchoOutputDTO();
        echoOutput.bIsSuccess = bIsSuccess;
        echoOutput.sErrorCode = sErrorCode;
        echoOutput.sErrorMessage = sErrorMessage;
        return echoOutput;
    }

    /**
     * Method is used to parse date from String
     *
     * @param     sDate                String
     * @return    Date                                          
     */ 
    private Static Date parseStringToDate(String sDate)
    {
        List<String> lstDateString = sDate.split('-');
        return (lstDateString != null && lstDateString.size() == 3) ? date.parse(lstDateString[1] +  '/' +lstDateString[2] + '/' + lstDateString[0]) : null;        
    }   
}