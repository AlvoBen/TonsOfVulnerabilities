/*******************************************************************************************************************************
Apex Class Name : TrackEnrollmentSearch_LC_HUM
Function        : This class is used for showing track enrollment search data table 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Rahul Krishan                                      10/17/2020               Original Version
*    2.0           Nilanjana Sanyal                                   02/17/2023               User Story-4003126: FirstName and Lastname link implementation in Enrollment Search Tab 
																							(for the Track Enrollment Status)
*******************************************************************************************************************************************************************/
public class TrackEnrollmentSearch_LC_HUM 
{    
    public static AsyncEnrollmentTrackResponse.ResponseComplexTypeFuture contstate;
    
    /* Inner class to take callout data to UI */
    public class wrapper
    {
        @AuraEnabled public String fname {get;set;}
        @AuraEnabled public String lname {get;set;}
        @AuraEnabled public String dob {get;set;}
        @AuraEnabled public String platform {get;set;}
        @AuraEnabled public String groupId {get;set;}
        @AuraEnabled public String product {get;set;}
        @AuraEnabled public String lob {get;set;}
        @AuraEnabled public String effectiveDate {get;set;}
        @AuraEnabled public String endDate {get;set;}
        @AuraEnabled public String pend {get;set;}
        @AuraEnabled public String pendKey {get;set;}
        @AuraEnabled public String datetimepend {get;set;}
		@AuraEnabled public String externalId {get;set;} 
        @AuraEnabled public String sLegacyMemberJson{get;set;}
        @AuraEnabled public String sExternalID{get;set;}
        
    }
    
	public static set<String> setofexternalIds = new set<String>();
	
    /*Inner class which is used to store the input search data that comes from UI */
    public class TrackEnroll extends MetavanceEnrollmentSearch_LC_HUM.SearchSSNEnrollment
    {
        public TrackEnroll(){}
    }
    
    /* 
    * Method name : searchTrackEnrollmentSearvice
    * Description : This method will return the list of Track enrollment search Data
    * Parameter   : Track enrollment search input data which comes from UI
    */    
    @AuraEnabled(continuation=true)
    public static object searchTrackEnrollmentSearvice(String TrackEnrollSearch)
    {
        TrackEnroll objTrack = (TrackEnroll) JSON.deserialize(TrackEnrollSearch, TrackEnroll.class);
        HUM_Webservice_Callout__c objWebServiceCalloutTE = HUM_Webservice_Callout__c.getValues('TrackEnrollmentService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebServiceCalloutTE.Timeout__c)/1000;
        Continuation oConTE;
        
        if(objWebServiceCalloutTE != null)
        {
            if(objWebServiceCalloutTE.Active__c)
            {
                oConTE = new Continuation(TIMEOUT_INT_SECS);
                oConTE.continuationMethod = 'TrackEnrollmentSearviceProcessResponse';
                TrackEnrollmentSearch(oConTE,objTrack);
            }
        }
        return oConTE;
    }
    
    /* 
    * Method name : TrackEnrollmentSearch
    * Description : This method will hold the continuation and TrackEnroll class data
    * Parameter   : Continuation and TrackEnroll class
    */    
    public static void TrackEnrollmentSearch(Continuation oCon,TrackEnroll objTrackEnroll)
    { 
        try
        {
            HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues('TrackEnrollmentService');
            AsyncAsyncTrackEnroll.AsyncBasicHttpBinding_ITwoWayAsync  oTrackEnrollmentService = new AsyncAsyncTrackEnroll.AsyncBasicHttpBinding_ITwoWayAsync();
            oTrackEnrollmentService.clientCertName_x =oSvcHeaders.Certificate_Name__c;
            oTrackEnrollmentService.endpoint_x =oSvcHeaders.End_Point_URL__c;
            oTrackEnrollmentService.timeout_x = Integer.valueOf(oSvcHeaders.Timeout__c);
            Datetime todayDate = System.now(); 
            date d= null;
            date startdate= Date.parse(objTrackEnroll.sEffectiveDate);
            date enddate =Date.parse(objTrackEnroll.sEndDate); 
            contstate = oTrackEnrollmentService.beginSearchTrack(oCon, todayDate ,objTrackEnroll.sSSN, startdate, enddate , 'Y', '', '', null, '');
            oCon.state = contstate;
        }
        catch(Exception e)
        {
            throw new HUMCustomException(e.getMessage());
        }
    }
    
    /*
    * Method Name   :    TrackEnrollmentSearviceProcessResponse()
    * Description   :    Callback Method which will be invoked for Track Enrollment System.                        
    * Return Type   :    Object
    * Parameters    :    NA
    */    
    @AuraEnabled
    public static Object TrackEnrollmentSearviceProcessResponse(List<string> labels, Object state)
    {  
        try
        {
            HttpResponse response = Continuation.getResponse(labels[0]);
            String result = response.getBody();
            AsyncEnrollmentTrackResponse.ResponseComplexTypeFuture respfuture = (AsyncEnrollmentTrackResponse.ResponseComplexTypeFuture)state;
            
            enrollmentTrackResponse.ResponseComplexType oResponseMsg = respfuture.getvalue();
            list<HUMMemberSearchEnrollmentHelper> lstLegacyService = new list<HUMMemberSearchEnrollmentHelper>();
            
            if(oResponseMsg != null && oResponseMsg.TrackList != null && oResponseMsg.TrackList.Track != null)
            {
                for(enrollmentTrackResponse.Track oTrack : oResponseMsg.TrackList.Track)
                {
                    HUMMemberSearchEnrollmentHelper humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oTrack);
                    lstLegacyService.add(humLegMemberObj); 
                }
            }
            else
            {
                return lstLegacyService;
            }
            
            list<wrapper> reslist = new List<wrapper>();
            
            for(HUMMemberSearchEnrollmentHelper h : lstLegacyService )
            {
                wrapper a = new wrapper();
                a.fname = h.sFirstName;
                a.lname = h.sLastName;
                a.dob = h.sDOB;
                if(h.sPlatform == null)
                    a.platform = '';
                else
                    a.platform = h.sPlatform;            
                a.groupId = h.sGroupId;
                
                if(h.sProduct == null)
                    a.product = '';
                else
                    a.product = h.sProduct; 
                
                a.lob = h.sLOB;
                
                if(h.sEffectiveDate == null)
                    a.effectiveDate = '';
                else
                    a.effectiveDate = h.sProduct; 
                
                if(h.sEndDate == null)
                    a.endDate = '';
                else
                    a.endDate = h.sEndDate; 
                
                if(h.sPend == null)
                    a.pend = '';
                else
                    a.pend = h.sPend;
                
                if(h.sPendKey == null)
                    a.pendKey = '';
                else
                    a.pendKey = h.sPendKey;
                
                if(h.sDateTimePend == null)
                    a.datetimepend = '';
                else
                    a.datetimepend = h.sDateTimePend;
                a.sExternalID = h.sExternalID;
                if(String.isNotBlank(a.sExternalID)){
                    String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_TE').StringValue__c + a.sExternalID;
                    a.sExternalID = sAccountExternalID; 
                    a.sLegacyMemberJson = Json.serialize(createAcc(a, sAccountExternalID));  
                }
                reslist.add(a);
            }
            return reslist; 
        }
        catch(CalloutException e)
        {
            throw new HUMCustomException('IO Exception: Time out'+e.getMessage());  
        }
        catch(Exception e)
        {      
            throw new HUMCustomException(e.getMessage()); 
        }   
    }
	
public static Account createAcc( Wrapper objWrapper,String AccExternalId )
    {
        Account accountRecord;
        try{
            string sfirstName=objWrapper.fname;
            Integer ilengthFName = Integer.valueof(System.label.HUMFirstNameLength);
            if(objWrapper.fname != null && objWrapper.fname.length() > ilengthFName )
            {
                sfirstName=objWrapper.fname.substring(0,ilengthFName );
            }
            
            string slastName=objWrapper.lname;
            Integer ilengthLName = Integer.valueof(System.label.HUMLastNamelength);
            if(objWrapper.lname != null && objWrapper.lname.length() > ilengthLName )
            {
                sLastName =objWrapper.lname.substring(0,ilengthLName );
            }
            
            accountRecord = new Account(FirstName = sfirstName, LastName = slastName ,
            Birthdate__c = objWrapper.dob , recordtypeid = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(), HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c),
            Source_Platform_Code__c = objWrapper.platform,
            Group_Number__c = objWrapper.groupId,
            Account_External_ID__c = AccExternalId,
            Tenant_Id__c = system.label.HUMExtMemAccountTenantID,
            General_Account__c = true,
            Individual_First_Name__c = objWrapper.fname,
            Individual_Last_Name__c = objWrapper.lname);
        }catch (Exception e) 
        {   
            HUMExceptionHelper.logErrors(e,'MemberSearchEnrollmentHelper_LC_HUM', 'createAcc');
        }
        return accountRecord;
    }
}