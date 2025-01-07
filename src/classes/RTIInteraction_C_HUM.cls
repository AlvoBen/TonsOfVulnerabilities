/**************************************************************************************************************************************************
Apex class Name    :  RTIInteraction_C_HUM
Version            :  1.0
Function           :  This is the controller class for RTI Interaction  to return list of RTI records
Test Class         :  RTIInteraction_C_HUM

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
* -------------------------------------------------------------------------------------------------------------------------------------------------  
* Pallavi Ravishankar          61268                 01/04/2016            Original Version
* Kritika Popat                86349                 07/04/2016            Phase 2 Service changes
* Kritika Popat                86349                 10/04/2016            Pagination    
* Kritika Popat                                      24/04/2016            IVR and Print 2nd level
* Kritika Popat                106666                06/30/2017            RTI defect fix for servcie down Reset
* Prudhvi Pamarthi             171453                01/22/2018            REQ - 328929 Fix for HUMExceptionHelper: DML 
* Vijaya Koriprolu             234129                05/21/2018            REQ-359055, REQ-359056, and REQ-359057
* Vijaya Koriprolu                                   06/01/2018            Defect fix 360550
* Prathyu Tushar                                     07/30/2018            RTI All categoryCode to support MF9 Switch
* Vandana Chaudhari                                  08/28/2018            NBA MF4-Guidance Alert
* SUmit Pharande                                                           NBA Rollback
* Subhamay Mandal                                    10/25/2018            REQ - 377309 PTO - New Member Indicator (CRM)
* Satyam Malviya                                     12/03/2018            NBA MF-4 GUidance Alert
* Sumit Pharande                                                           REQ-386780 -Removed the 18 to 15 Digit Id conversion
* Santhi Mandava                                     08/06/2019            Implemented REQ - 403609 to display group medicare icon
* Amar Gadewar                                       10/29/2019            REQ - 411303 Quality - 2020 New Member Indicator (CRM)
* Amar Gadewar                                       01/28/2020            USER STORY 729651 And USER STORY 729659 And REQ - 416082 Move Icons from Communication Panel to Under Page Titles on Member Account page / Policy Member Page
* Anjani Vaddadi                                     03/30/2020            REQ-437065-RTI-MF4
* Prasanthi Kandula                                  04/17/2020            US:  MF42 Ability to view up to 24 months of SmartSummaries in CRM Service
* Prasanthi Kandula                                  04/27/2020            US:829867 MF42 Modified as part of latest changes to user story.
* Trina Ghosh                                        02/09/2021            US:1669848 - Low Security Issues Fix
* Rahul Chaurasia                                    03/22/2021             1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service
* Prasanthi Kandula                                  05/18/2021            US - 2082407 Added new card for channel Email
* Anuradha Gajbhe                                    05/18/2021            US - 2082385 Added logic to display new card for channel Text
* Rahul Chaurasia                                    05/19/2021            US - 2082359 Added logic to display new card for channel Web
* Satyanarayana Chenna								 05/31/2021			   Defect fix  3119		
************************************************************************************************************************************************* */


public  with sharing class RTIInteraction_C_HUM
{
    Map<String, RTIApplicationChannelValues_H_HUM.Attributes> attributeMap = RTIApplicationChannelValues_H_HUM.getApplicationFieldAttributes('RTI');
    Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
    @TestVisible private String sRequest ='';
    @TestVisible private RTISummary_S_HUM oServiceRTI = new RTISummary_S_HUM();
    @TestVisible private String sId ;
    private Boolean bPagination;
    private final Integer nPage;
    private string sInteractingWithCode = 'All';
    private string sCategoryCode = 'All';
    private string sOldInteractingWithCode = 'All';
    private string sOldCategoryCode = 'All';
    private Integer iSkip;
    private String sOldSelectedChannel;
    private Integer iTotalRec;
    private boolean bIsDataAvailable;
    public String sMinStartDate {get; set;}
    public String sMaxEndDate {get; set;}
    public String sName {get; private set;}
    public Boolean disableSearch{get; private set;}
    public Boolean disableFirstPre {get;private set;}
    public Boolean disableNextLast {get;private set;}
    @TestVisible public String sEnterpriseId {get;private set;}
    public String sSelectedChannel {get;set;}
    public String sSelectedDirection {get;set;}
    public Boolean bIsError{get; private set;}
    @TestVisible public String sStartDate{get;set;}
    @TestVisible public String sEndDate{get;set;}
    public List<RTISummary_DTO_HUM> lstRTIInteractions{get; private set;}
    @TestVisible public String sInteractionWithType {get; private set;}
    public Boolean displayCategories {get; private set;}
    public String sCategoryValue {get;set;}
    @TestVisible public String sSubCategory {get; private set;}
    @TestVisible public List<Selectoption>lstsubCategory {get; private set;}    
    public String iRTICards { get; set; }
    public string sAccountId {get;set;}
    public boolean bDirectionSwitch {get;set;}
    public boolean bIsPageOnLoad {get; set;}
    
    public List<Error_Log__c> lstLogErrors
    {
        get
        {
          if(lstLogErrors == Null){ 
          lstLogErrors = new List<Error_Log__c>();
          if(!oServiceRTI.lstLogErrors.isempty())
          lstLogErrors.addAll(oServiceRTI.lstLogErrors);
          }return lstLogErrors;
        }
        set;
    }
    @TestVisible private List<SelectOption> interactingWithList
    {
        get
        {
            return populateOptions('Interacting With', sSelectedChannel);
        }
    }
    @TestVisible private List<SelectOption> categoryList
    {
        get
        {
            return populateOptions('Category', sSelectedChannel);
        }
    }
    
    /*
    * Method Name   :  RTIInteraction_C_HUM
    * Description   :  This is a constructor for class RTIInteraction_C_HUM
    * Return Type   :  NA
    * Parameters    :  NA
    */
    public RTIInteraction_C_HUM()
    {
        bDirectionSwitch = CRMFunctionalityenabler_H_HUM.Checkflagvalue('RTIDirectionSwitch');
        if(bDirectionSwitch)
        {
            sSelectedDirection = 'O';
            sSelectedChannel = 'All';
        }
        sAccountId = ApexPages.currentPage().getParameters().get('Id');
        sId = ApexPages.currentPage().getParameters().get('Id').escapeHtml4();
        if(Apexpages.currentPage().getParameters().get('polId')!=null) sAccountId = Apexpages.currentPage().getParameters().get('polId');
        sSelectedChannel= 'All';
        sOldSelectedChannel= 'All';
        bIsError = false;
        bIsDataAvailable = true;
        disableSearch = false;
        iSkip = 0;
        iTotalRec = 0;
        bPagination = false;
        disableFirstPre = true;
        disableNextLast = true;
        displayCategories = false;
        sCategoryValue = 'All';
        Account objAcnt = RTISummary_D_HUM.getAccountDetails(sId);
        sEnterpriseId = objAcnt.Enterprise_ID__c;
        sName = objAcnt.Name;
        Datetime dtMinStartDate = Datetime.now().adddays(-90);
        Datetime dtMaxEndDate = Datetime.now();
        sMinStartDate = dtMinStartDate.format('MMddyyyy');
        sMaxEndDate = dtMaxEndDate.format('MMddyyyy');
        try 
        {   
            nPage = Integer.valueOf(HUMConstants__c.getInstance('RTIPagination').StringValue__c);
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(ex,'RTISummary_C_HUM','processResponse');
        }
    }

    
 
    public List<SelectOption> channelList 
    {      
        get 
        {
            if ((channelList == null||channelList.isEmpty())) 
            {
                if(bDirectionSwitch) channelList = populateOptions('Channel', sSelectedDirection);
                else channelList = populateOptions('Channel', null);
            }
            return channelList;
        }   
        private set;       
    }  
    
    
    public List<SelectOption> directionList {
        get {
            if(directionList == null || directionList.isEmpty()){
                 directionList = populateOptions('Direction', null);
            } 
            return directionList;
        }
            private set;
    }
   
    /*
    * Method Name   :  populateOptions
    * Description   :  Populate the select options for the specified Field Name (e.g. 'Channel') and Field Value (e.g. 'IVR')
    * Return Type   :  List<SelectOption> - List of select option values for Channel field
    * Parameters    :  String fieldName - Field for which select options have to be retrieved
    *                  String fieldValue - Field value for which select options have to be retrieved ( if applicable)
    */ 
    private List<SelectOption> populateOptions (String fieldName, String fieldValue) 
    {
        List<SelectOption> retList = new List<SelectOption>();
        if(bDirectionSwitch)
        {
            for (RTIApplicationValues_H_HUM.AttributeValue attrValue : mapRTIattributes.get(fieldName).getAttributeValues(fieldValue)) 
            {           
                retList.add( new SelectOption(attrValue.value, attrValue.label));
            } 
        }       
        else
        {
            for (RTIApplicationChannelValues_H_HUM.AttributeValue attrValue : attributeMap.get(fieldName).getAttributeValues(fieldValue)) 
            {           
                retList.add( new SelectOption(attrValue.value, attrValue.label));
            }        
        }
        
        return retList;    
    }  
  
    /*
    * Method Name   :  resetChannel
    * Description   :  This method is used to reset all filters to default value when 'reset' button is clicked on
    * Return Type   :  NA
    * Parameters    :  NA
    */
    public void resetChannel()
    {
        if(bDirectionSwitch) sSelectedDirection = 'O';
        sSelectedChannel = 'All';
        sCategoryValue = 'All';
        sCategoryCode = 'All';  
        sInteractingWithCode = 'All';
        sOldCategoryCode = 'All';
        sOldInteractingWithCode = 'All';
        displayCategories = false;
		if(bDirectionSwitch) populateChannel();
    }
    
    public void populateChannel(){
            displayCategories = false; 
            sSelectedChannel = 'All';
            channelList = populateOptions('Channel', sSelectedDirection);
   }
   
    /*
    * Method Name   :  populateCategory
    * Description   :  This method is used to populate subcategory
    * Return Type   :  NA
    * Parameters    :  NA
    */
    public void populateCategory()
    {    
        displayCategories = false; 
        sCategoryValue = 'All';
        if(bDirectionSwitch && sSelectedChannel == 'IV')
        {
            displayCategories = true;
            sSubCategory = 'Interacting With';
            lstsubCategory = interactingWithList;  
        }
        if(!bDirectionSwitch && sSelectedChannel == 'IVR')
        {
            displayCategories = true;
            sSubCategory = 'Interacting With';
            lstsubCategory = interactingWithList;  
        }
        if(sSelectedChannel == 'WEB,WB' || sSelectedChannel == 'PRINT')
        {
            displayCategories = true;
            sSubCategory = 'Category';
            lstsubCategory = categoryList;
        }
    }
    
    /*
    * Method Name   :  search
    * Description   :  This method is used to call ther service when user clicks on 'Search' button
    * Return Type   :  Continuation
    * Parameters    :  NA
    */
    public Object search()
    {
        try
        {
            bIsDataAvailable = true;     
            String sStartFormatted = '';
            String sEndFormatted = '';
            if(sStartDate != NULL)    sStartFormatted = sStartDate.remove('/'); 
            if(sEndDate != NULL)    sEndFormatted = sEndDate.remove('/');
            if(sSelectedChannel == 'PRINT' && sCategoryValue == 'SMART SUMMARY,SS' && String.isBlank(sStartFormatted))
            {
                Datetime dtMinStartDate = Datetime.now().addMonths(-24);
                sMinStartDate = dtMinStartDate.format('MMddyyyy');
                sStartFormatted = sMinStartDate;
            }
            if(String.isEmpty(sStartFormatted) && String.isNotEmpty(sEndFormatted))    sStartFormatted = sMinStartDate;
            if(String.isEmpty(sEndFormatted) && String.isNotEmpty(sStartFormatted))    sEndFormatted = sMaxEndDate;
            if(!bDirectionSwitch) sCategoryCode = RTIApplicationChannelValues_H_HUM.getCategoryCode();
            sInteractingWithCode = 'All';
            if((!(sCategoryValue.equalsIgnoreCase('All'))) && (!bPagination))
            {
                if(sSelectedChannel == 'WEB,WB' || sSelectedChannel == 'PRINT')   sCategoryCode = sCategoryValue;
                if(bDirectionSwitch) { if(sSelectedChannel == 'IV')    sInteractingWithCode = sCategoryValue; }
                else {if(sSelectedChannel == 'IVR')    sInteractingWithCode = sCategoryValue;}
            }
            if(String.isNotBlank(sEnterpriseId))
            {
                HUM_Webservice_Callout__c oSvcHeaderRTI = new HUM_Webservice_Callout__c();
                oSvcHeaderRTI = HUM_Webservice_Callout__c.getValues('RTIService_GetInteractions_V2');
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderRTI.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                if(bPagination)    oServiceRTI.setupRequest(request, oSvcHeaderRTI, sEnterpriseId, sStartFormatted, sEndFormatted, sOldSelectedChannel, iSkip, sOldCategoryCode, sOldInteractingWithCode, sSelectedDirection, mapRTIattributes.get('Channel').getAttributeValues(sSelectedDirection), bDirectionSwitch);      
                else    
                {
                    iSkip = 0;
                    oServiceRTI.setupRequest(request, oSvcHeaderRTI, sEnterpriseId, sStartFormatted, sEndFormatted, sSelectedChannel, iSkip, sCategoryCode, sInteractingWithCode, sSelectedDirection, mapRTIattributes.get('Channel').getAttributeValues(sSelectedDirection), bDirectionSwitch);    
                    sOldSelectedChannel = sSelectedChannel;
                    sOldCategoryCode = sCategoryCode;
                    sOldInteractingWithCode = sInteractingWithCode;
                } 
                bPagination = false;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                return con;  
            }
            else    
            {
                disableSearch = true;
                displayErrorMessage(true , System.Label.RTISummary_No_records ,'NoRecords' );
                return Null;
            }
        }     
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTISummary_C_HUM', 'search');
            return NULL;
        }   
    }

   /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processResponse()
    {
        try 
        {   
            HttpResponse response = Continuation.getResponse(sRequest);
            responseparse(response); 
        }
        catch(Exception ex)
        {
            disableSearch = true;
            displayErrorMessage(true, System.Label.RTISummary_Service_Error, 'serviceError');
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(ex,'RTISummary_C_HUM','processResponse');

        }
    }
    
   /*
    * Method Name   :  responseparse
    * Description   :  This method is used to check response
    * Return Type   :  void 
    * Parameters    :  HttpResponse obj
    */
    @testvisible private void  responseParse(HttpResponse sResponse)
    {
        iTotalRec = 0;
        lstRTIInteractions =  new List<RTISummary_DTO_HUM>();
        disableSearch = false;
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == 200 || statusCode == 202)
        {
            lstRTIInteractions = oServiceRTI.processResponse(sResponse.getbody(), bDirectionSwitch);
            iTotalRec = oServiceRTI.totalRec();
            iRTICards = String.valueOf(iTotalRec);
            if((lstRTIInteractions==null||lstRTIInteractions.isEmpty()))
            {
                if(sSelectedChannel.equalsIgnorecase('All'))    disableSearch = true;
                 string sError;
                if(bIsPageOnLoad)
                    sError =  System.Label.RTISummary_NoRecordsOnLoad;
                else        
                    sError = System.Label.RTISummary_No_records ;
                displayErrorMessage(true , sError ,'NoRecords' );
            }
        }
        else
        {
            disableSearch = true;
            displayErrorMessage(true, System.Label.RTISummary_Service_Error, 'serviceError');
        }
        
        if((iTotalRec - iSkip) <= nPage)    disableNextLast= true;
        Else    disableNextLast = False;
        if(iSkip == 0 || iTotalRec <= nPage)    disableFirstPre = true;
        else    disableFirstPre  = false;
    }    
    
    
   /*
    * Method Name : displayErrorMessage
    * Description : This method is used to display error message on UI.
    * Return type : Void
    * Parameter   : Boolean ,String 
    */
    @testVisible private void displayErrorMessage(Boolean bError, String sError, String errorType)
    {
        if(bError) {
            bIsError = bError;
            if(errorType.equalsIgnorecase('NoRecords'))    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,sError,true);
            if(errorType.equalsIgnorecase('serviceError'))    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Error,sError,true);
            bIsDataAvailable = false; 
        }
        else    bIsDataAvailable = true;
    } 
    
    /*  
    * Method Name   :    getFirst
    * Description   :    This Method is used to display the first page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public object getFirst()
    {
        bPagination = true; 
        iSkip = 0;
        return search();   
    }
    
    /*  
    * Method Name   :    getPrevious
    * Description   :    This Method is used to display the previous page record(s).  
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public object getPrevious()
    {
        bPagination = true;
        iSkip = iSkip - nPage;
        return search();               
    }
    
    /*  
    * Method Name   :    getNext
    * Description   :    This Method is used to display the next page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public object getNext()
    {
        bPagination = true;
        iSkip = iSkip + nPage;
        return search();   
    }
    
    /*  
    * Method Name   :    getLast
    * Description   :    This Method is used to display the last page record(s).    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public object getLast()
    {
        bPagination = true;
        Integer divisor = iTotalRec/nPage;
        if (Math.mod(iTotalRec,nPage) == 0)  iSkip = (divisor - 1) * nPage;
        else    iSkip = divisor*nPage;
        return search();    
    }
    
    /*  
    * Method Name   :    getFirstRecordOf_Page
    * Description   :    This method is used to display the first record of the page.
    * Return Type   :    Integer
    * Parameters    :    NA
    */
    public Integer getFirstRecordOf_Page()
    {
        if(iTotalRec == 0) return 0;
        return iSkip+1;
    }
    
    /*  
    * Method Name   :    getLastRecordOf_Page  
    * Description   :    This method is used to display the last record for that page.  
    * Return Type   :    Integer
    * Parameters    :    NA
    */
    public Integer getLastRecordOf_Page()
    {
        return (((iSkip+nPage)<iTotalRec)?(iSkip+nPage):iTotalRec);
    }
    
    /*  
    * Method Name   :    getTotal_SizeRecord
    * Description   :    This method is used to display the total no. of records.
    * Return Type   :    Integer
    * Parameters    :    NA
    */
    public Integer getTotal_SizeRecord()
    {
        return iTotalRec;
    }   
    
  
}