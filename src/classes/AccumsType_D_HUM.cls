/************************************************************************************************************************************************
Apex Class Name  : AccumsType_D_HUM
Version          : 1.0
Created Date     : FEB 25 2015
Function         : Class to return Accums data for Benefits History table
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani                                   02/25/2016                    Original Version
* MadhuSudhan Chandra                              03/25/2016                    Added Changes for (REQ-255112)Benefit – Previous Beyond Two Years
* Samridh Manucha            30878                 04/11/2016                    REQ - 237144
* Avinash Choubey            31077                 04/18/2016                    Design Change in All Accums Req ( 237142, 246888 etc)
* Avinash Choubey            25938(VS)             04/25/2016                    logic updated to calculate end date for getting previous year policies in getAllPoliciesOfMember method
* Avinash Choubey            28388(VS)             05/25/2016                    Defect# 227506 fix and Code refactoring as per Review comments
* Avinash Choubey            28561(VS)             05/28/2016                    Changes to fix Defect# ( 225237, 226287)
* Avinash Choubey            29988                 06/09/2016                    Changes to fix Defect# 228609
* Avinash Choubey            32583                 07/12/2016                    Removed MBE Dependency for Gen Key REQ - 269975
* Muralidhar Kollu           102572                15/06/2017                    REQ - 304936 VOB – Accumulator section of the Benefits and Accumulator page (MF5)
* Muralidhar Kollu           110015                13/07/2017                    REQ - 304936 - defect fix 301405.0001
* Muralidhar Kollu           121053                18/08/2017                    REQ - 326297 - change of AsOfDate Previous section logic
* Muralidhar Kollu                                 21/08/2017                    REQ - 326297 - logic to check 18 months data for current section as well
**************************************************************************************************************************************************/
public with sharing class AccumsType_D_HUM
 {          
 
    private static String sDateOfService;
    private static Date dateOfService = NULL;  
    public static Boolean differentsSubPolicyMemberFound= false;
    public static boolean memberServiceFlag = false;
    public static set<id> setOfAccountIds = NULL;
    public static List<policy_member__c> lstPolicyMembersList = NULL;
    public Static Set<id> setSubscriberPoliciesid = NULL;
      
   
    /**
    * Returns an object
    * <p>
    * This method calls the accums web service
    *
    * @pram  id of the policy member in string format
    * @param  type in string format i.e either history or current
    * @return  object
    * @see
    */
    public static Object getAccumsData(String personID, String accumtype, String refDate)
    {
        Policy_Member__c policyMemObj = NULL;
        lstPolicyMembersList = new List<policy_member__c> ();
        set<id> lstRemainingPolicyMembersIdsSet = new set<id>();
        sDateOfService = refDate;  
        try
        {
            if( NULL != personID)
            {
                policyMemObj = getCurrentPolicyMemberRecord(personID);               

                getAllSubscriberPolicyMember(policyMemObj,accumtype);
                if(!lstPolicyMembersList.isEmpty() && lstPolicyMembersList.size() > 0) 
                {  
                    for(Policy_Member__c objpolicyMem :lstPolicyMembersList)
                    {
                        lstRemainingPolicyMembersIdsSet.add(objpolicyMem.id);
                    }
                    return PrepareAccumService(policyMemObj,lstPolicyMembersList,lstRemainingPolicyMembersIdsSet,null,accumtype,-1, dateOfService);
                }
            }   
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AccumsType_D_HUM','getAccumsData');
            return NULL;
        }
    }  
    
        /**
    * Returns record of Policy_Member__c object
    * <p>
    * Method to get policy member this would be the currect policy member record.
    *
    * @param  Id of currect policy member.
    * @return Policy_Member__c record
    * @see
    */
    private static Policy_Member__c getCurrentPolicyMemberRecord(String PolmemId)
    {  
        List<policy_member__c> lstSelectedPolMem  = [select id,name,Product__c ,Effective_Date__C,policy__c,Status__c,Member__c,
                       end_date__c ,policy__r.Group_Name__r.enterprise_ID__c,
                       policy__r.Policy_External_ID__c,member__r.Enterprise_ID__c,Subscriber__c,
                       Subscriber__r.member__c,Subscriber__r.member__r.Mbr_Gen_Key__C,member__r.Mbr_Gen_Key__C
                       from Policy_Member__c 
                       where id = :PolmemId];
        if(!String.isBlank(sDateOfService))
        {   
            Policy_Member__c  polMemOfSelDate = null;
            Date dtOfService = date.newinstance(Integer.valueof(sdateOfService.substring(4, 8)), Integer.valueof(sdateOfService.substring(0, 2) ), Integer.valueof(sdateOfService.substring(2, 4)));        
            
            if(dtOfService > Date.today()) 
            {
                dtOfService = Date.Today();
            }     
            if(lstSelectedPolMem[0] != null)
            {
                    List<String> lstPolEndDate = lstSelectedPolMem[0].end_Date__c.split('/');
                    Date polEndDate = date.newinstance(Integer.valueof(lstPolEndDate[2]), Integer.valueof(lstPolEndDate[0]), Integer.valueof(lstPolEndDate[1]));

                    List<String> lstPolEffectiveDate = lstSelectedPolMem[0] .Effective_Date__c.split('/');
                    Date polEffectiveDate = date.newinstance(Integer.valueof(lstPolEffectiveDate[2]), Integer.valueof(lstPolEffectiveDate[0]), Integer.valueof(lstPolEffectiveDate[1]));
                    
                    if(polEndDate >= dtOfService && polEffectiveDate <= dtOfService )
                    {
                        return lstSelectedPolMem[0];
                    }
            }
            return polMemOfSelDate;
        }  
        return lstSelectedPolMem[0];
    }
    
    /**
    * Returns list of policy_Member__c object
    * <p>
    * Method to getAll  policy members
    *
    * @param  currect policy member.
    * @param  Date lastDate    
    * @return List
    * @see
    */ 
    private static List<Policy_Member__c> getAllPoliciesOfMember(Policy_Member__c policyMemObj, Date lastDate)
    { 
        List< Policy_Member__c> tempPolList = new  List< Policy_Member__c>();
        List<String> lstPolEndDate  = null;
        List<String> lstCurrentPolEndDate = String.valueOf(policyMemObj.end_date__c).Split('/');
        List<String> lstCurrentPolEffectiveDate = String.valueOf(policyMemObj.Effective_Date__C).Split('/');
        List<String> lstPolEffectiveDate  =null;
        Date polEndDate =null;
        Date polEffectiveDate = null;
        Date currPolEndDate = date.newinstance(Integer.valueof(lstCurrentPolEndDate[2]), Integer.valueof(lstCurrentPolEndDate[0]), Integer.valueof(lstCurrentPolEndDate[1]));
        Date currPolEffectiveDate = date.newinstance(Integer.valueof(lstCurrentPolEffectiveDate[2]), Integer.valueof(lstCurrentPolEffectiveDate[0]), Integer.valueof(lstCurrentPolEffectiveDate[1]));
        for(Policy_Member__c objPol :[select id ,name,end_date__c,Effective_Date__C,Status__c,
                                            policy__r.Group_Name__r.enterprise_ID__c,policy__r.Policy_External_ID__c,
                                            member__r.Enterprise_ID__c,member__c,Subscriber__c,
                                            Subscriber__r.member__c ,Subscriber__r.member__r.Mbr_Gen_Key__C,member__r.Mbr_Gen_Key__C 
                                            from Policy_Member__c 
                                            where Product__c = :policyMemObj.Product__c 
                                            AND  end_date__c != null  
                                            AND Effective_Date__C != null
                                            AND member__c = :policyMemObj.member__c 
                                            order by end_date__c DESC])
        {
            lstPolEndDate = objPol.end_date__c.Split('/');
            lstPolEffectiveDate = objPol.Effective_Date__C.Split('/');
            if( null != lstPolEndDate && lstPolEndDate.size() > 2 && null != lstPolEffectiveDate  && lstPolEffectiveDate.size() > 2)
            {
                polEndDate = date.newinstance(Integer.valueof(lstPolEndDate[2]), Integer.valueof(lstPolEndDate[0]), Integer.valueof(lstPolEndDate[1]));
                polEffectiveDate = date.newinstance(Integer.valueof(lstPolEffectiveDate[2]), Integer.valueof(lstPolEffectiveDate[0]), Integer.valueof(lstPolEffectiveDate[1]));
                if( null != currPolEffectiveDate && null != currPolEndDate && null != polEndDate && null !=  polEffectiveDate  && polEndDate >= lastDate &&   polEffectiveDate <= dateOfService)
                {
                    tempPolList.add(objPol);
                }
            }
        }
        return tempPolList;
    } 
      
    /**
    * Returns list of  String
    * <p>
    * Method to get From and To Dates
    *
    * @param  end date of current policy member.
    * @return List
    * @see
    */
    private static Boolean checkEndDate(Date policyEndDate )
    {   
        if(policyEndDate != null )
        {   
        
            if(policyEndDate >= Date.today().addMonths(-18)) 
            {
                return true;
            }   
        }
        return false;
    }   
    
    /*
    * Returns void
    * <p>
    * Method to get all Subscriber Account Id of policy member
    *
    * @param  policy member record.
    * @param  accumstype in string format.
    * @return void
    * @see
    */
    private static void getAllSubscriberPolicyMember(Policy_Member__c policyMemObj,string accumtype)
    {  
        if(policyMemObj != null)
        {        
            if(!String.isBlank(sDateOfService))
            {
                dateOfService = date.newinstance(Integer.valueof(sdateOfService.substring(4, 8)), Integer.valueof(sdateOfService.substring(0, 2) ), Integer.valueof(sdateOfService.substring(2, 4)));        
    
            }
            else
            {
                List<String> lstPolEndDate = policyMemObj.end_Date__c.split('/');
                dateOfService = date.newinstance(Integer.valueof(lstPolEndDate[2]), Integer.valueof(lstPolEndDate[0]), Integer.valueof(lstPolEndDate[1]));        
            }
            
            if(dateOfService > Date.today()) 
            {
                dateOfService = Date.Today();
            }       
                 
            if(checkEndDate(dateOfService))
            { 
                if(accumtype =='Current')
                {
                    lstPolicyMembersList.add(policyMemObj);
                }
                else
                {
                    Date dLastDate = dateOfService.addMonths(-18);
                    lstPolicyMembersList = getAllPoliciesOfMember(policyMemObj,dLastDate);
                }
            }
        }
    }
     
    /**
    * Returns yearCounter as Integer
    * <p>
    * Method to check calculate no of previous years based on current policy enda date 
    *
    * @param  current Policy_Member__c record
    * @param  string accumtype
    * @param  date dtDOS - date of service
    * @return  Integer
    * @see
    */
    private static Integer getYearCounter(Policy_Member__c objCurrentPolicyMember,string accumtype, date dtDOS)
    {
        integer yearCounter = 1;
        if (accumtype !='Current')
        {
            Date dCurrentDate = Date.Today();
            yearCounter = dtDOS.Year() - (dCurrentDate.addMonths(-18)).Year();
        }
        return yearCounter;
    }
    
    /**
    * Returns an object
    * <p>
    * Method to check requests and if no request added process remaining policies else return Continuation object
    *
    * @param  current Policy_Member__c record
    * @param  List<PolicyMemberInfoWrapper_DTO_HUM> which containing all data required to pass in accums service.
    * @param  Map<string,string> containing all account ids and their memberGenKey.
    * @param  Map<string,string> for upserting the memberGenKey in member_cache__c
    * @param  date dtDateOfService
    * @return  object
    * @see
    */
    private static Object checkRequests(Policy_Member__c objCurrentPolicyMember,List<policy_member__c> lsPolicyMembersList, set<ID> lstSetremainingPolicyID,List<VobBenefitsCurrent_DTO_HUM> tlistOfDTOData,String accumtype, Integer iStartCounter, date dtDateOfService, Continuation con, List<String> lstReqLabels)
    {
        if(lstReqLabels != null && lstReqLabels.Size() > 0)
        {
            con.state = new  PolicyMemberInfoWrapper_DTO_HUM( objCurrentPolicyMember, lsPolicyMembersList,  lstSetremainingPolicyID,  lstReqLabels,tlistOfDTOData,accumtype,iStartCounter, dtDateOfService );
            return con;
        }
        else if(tlistOfDTOData !=null && tlistOfDTOData.size() > 0) 
        {
            return prepareServiceData(new  PolicyMemberInfoWrapper_DTO_HUM( objCurrentPolicyMember, lsPolicyMembersList, lstSetremainingPolicyID,  lstReqLabels,tlistOfDTOData,accumtype,iStartCounter, dtDateOfService ));
        }
        return null;
    }
    
    /**
    * Returns an object
    * <p>
    * Method to prepare request for Accums web service
    *
    * @param  current Policy_Member__c record
    * @param  List<PolicyMemberInfoWrapper_DTO_HUM> which containing all data required to pass in accums service.
    * @param  Map<string,string> containing all account ids and their memberGenKey.
    * @param  Map<string,string> for upserting the memberGenKey in member_cache__c
    * @param  date dtDateOfService
    * @return  object
    * @see
    */
    public Static Object PrepareAccumService(Policy_Member__c objCurrentPolicyMember,List<policy_member__c> lsPolicyMembersList, set<ID> lstSetremainingPolicyID,List<VobBenefitsCurrent_DTO_HUM> tlistOfDTOData,String accumtype, Integer iStartCounter, date dtDateOfService)
    {
        try
        {
            List<String> lstReqLabels = new List<String>();
            String asOfDate = null;
            HUM_Webservice_Callout__c accumServiceObj = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.VOB_ACCUMS_SERVICE_HUM);
            Continuation con = new Continuation(Integer.valueof(accumServiceObj.Timeout__c)/1000);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.VOB_ACCUMS_PREPARESERVICEDATA_HUM;
            integer iAccumRequestCounter = 0;
            Integer yearCounter = getYearCounter(objCurrentPolicyMember,accumtype, dtDateOfService);
            Integer AsOfDatePrevYear = null;
            
            for(Policy_Member__c objPolicyMember :lsPolicyMembersList)
            {   
                if(null != objPolicyMember && lstSetremainingPolicyID.contains(objPolicyMember.id) && iAccumRequestCounter < 3 )
                {
                    if(iStartCounter == -1)
                    {
                        iStartCounter = yearCounter;
                    }
                    for(integer counter = iStartCounter ;  counter > 0 ; counter-- )
                    { 
                       if( counter == 1)
                        {
                            lstSetremainingPolicyID.remove(objPolicyMember.id);
                            iStartCounter = counter - 2;
                        }
                        
                        if(accumtype !='Current')
                        {
                            
                            AsOfDatePrevYear =  dateOfService.Year() - counter;
                            List<String> lstEndDate = objPolicyMember.end_Date__C.split('/');
                            List<String> lstEffectiveDate = objPolicyMember.Effective_Date__C.split('/');
                            Date PolEndDate = date.newinstance(Integer.valueof(lstEndDate[2]), Integer.valueof(lstEndDate[0]), Integer.valueof(lstEndDate[1]));
                            Date PolEffectiveDate = date.newinstance(Integer.valueof(lstEffectiveDate[2]), Integer.valueof(lstEffectiveDate[0]), Integer.valueof(lstEffectiveDate[1]));       
                            if(PolEffectiveDate.Year() <= AsOfDatePrevYear && PolEndDate.Year() >= AsOfDatePrevYear)
                            {
                                asOfDate = String.valueof(date.newinstance(AsOfDatePrevYear, dateOfService.Month(), dateOfService.Day())) + GLOBAL_CONSTANT_HUM.VOB_TIME_FORMAT_HUM;
                            }
                        }
                        else
                        {
                            asOfDate = String.valueof(dateOfService) + GLOBAL_CONSTANT_HUM.VOB_TIME_FORMAT_HUM;
                        }
                        

                        if(asOfDate != null)
                        {
                            AccumsServiceRequest_H_HUM objAccumsReq =prepareAccumsRequest( objPolicyMember,  objCurrentPolicyMember,   accumtype,asOfDate);
                            HttpRequest request = Accums_S_HUM.builtAccumsRequest(new HttpRequest(),accumServiceObj,JSON.serialize(objAccumsReq));
                            lstReqLabels.add(con.addHttpRequest(request));
                            if( ++iAccumRequestCounter == 3)
                            {
                                iStartCounter = counter-2;
                                break;
                            }
                        }   
                    }
                }
            }
            return checkRequests( objCurrentPolicyMember, lsPolicyMembersList, lstSetremainingPolicyID, tlistOfDTOData, accumtype,  iStartCounter,dtDateOfService, con,lstReqLabels);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AccumsType_D_HUM','prepareServiceData');
            return null;
        }
    }
    
    /**
    * Returns an AccumsServiceRequest_H_HUM
    * <p>
    * Method to prepare Accums Request
    *
    * @param  objPolicyMember Policy_Member__c
    * @param  sMemGenKey String
    * @param  objCurrentPolicyMember Policy_Member__c
    * @param  mapOfAccIdMemKey Map<id,string>
    * @return  AccumsServiceRequest_H_HUM
    * @see
    */
    private static AccumsServiceRequest_H_HUM prepareAccumsRequest( Policy_Member__c objPolicyMember,Policy_Member__c objCurrentPolicyMember,String accumtype, String asOfDate)
    {
        List<String> lstPolicyExtrnID = null;
        AccumsServiceRequest_H_HUM RequestDataObj = new AccumsServiceRequest_H_HUM();
        AccumsServiceRequest_H_HUM.request requestObj = new AccumsServiceRequest_H_HUM.request();
        AccumsServiceRequest_H_HUM.BenefitAccumsRequest benefitAccumReq;
        AccumsServiceRequest_H_HUM.GetBenefitAccums mainDataObj = new AccumsServiceRequest_H_HUM.GetBenefitAccums();
        AccumsServiceRequest_H_HUM.BenefitAccumsRequests benefitAccumReqs = new AccumsServiceRequest_H_HUM.BenefitAccumsRequests(); 
        benefitAccumReq = new AccumsServiceRequest_H_HUM.BenefitAccumsRequest();
        benefitAccumReq.CustomerGenKey = objPolicyMember.policy__r.Group_Name__r.enterprise_ID__c;
        benefitAccumReq.MemberPersonGenKey = objPolicyMember.member__r.Mbr_Gen_Key__c;
        if( objPolicyMember.subscriber__c == null || objPolicyMember.subscriber__r.member__C == objCurrentPolicyMember.member__c)
        { 
            benefitAccumReq.SubscriberPersonGenKey = objPolicyMember.member__r.Mbr_Gen_Key__c;
        }
        else
        {
            benefitAccumReq.SubscriberPersonGenKey = objPolicyMember.subscriber__r.member__r.Mbr_Gen_Key__c;
        } 
        if(objPolicyMember.policy__r != null && String.isNotBlank(objPolicyMember.policy__r.Policy_External_ID__c))
        { 
            lstPolicyExtrnID = objPolicyMember.policy__r.Policy_External_ID__c.split('\\|');
            if(lstPolicyExtrnID != null && lstPolicyExtrnID.size() > 3)
            {
                benefitAccumReq.CustomerCoverageSequenceNumber = lstPolicyExtrnID[3];
            }
        } 
        
        benefitAccumReqs.BenefitAccumsRequest = new List<AccumsServiceRequest_H_HUM.BenefitAccumsRequest>();
        benefitAccumReqs.BenefitAccumsRequest.add(benefitAccumReq);
        requestObj.asOfdate = asOfDate;
        requestObj.BenefitAccumsRequests = benefitAccumReqs; 
        mainDataObj.request = requestObj;
        RequestDataObj.GetBenefitAccums = mainDataObj;
        return RequestDataObj;
    }
    
    /**
    * Returns an object
    * <p>
    * Method to prepare DTO from the response Coming from accums service and upsert the member_gen_key in member_cache__c
    *
    * @param  state object
    * @return  object
    * @see
    */
    public static Object prepareServiceData(object state)
    {
        List<AccumsServiceResponse_H_HUM> listOfServiceData = new List<AccumsServiceResponse_H_HUM>();
        List<VobBenefitsCurrent_DTO_HUM> listOfDTOData =  new List<VobBenefitsCurrent_DTO_HUM>();
        PolicyMemberInfoWrapper_DTO_HUM objPoliMemberWrapper = null;
        try
        {
            AccumsServiceResponse_H_HUM oResponse = NULL;            
            if(state != null)
            {       
                objPoliMemberWrapper = (PolicyMemberInfoWrapper_DTO_HUM)state;
                if( null != objPoliMemberWrapper.listOfDTOData && objPoliMemberWrapper.listOfDTOData.size() > 0)
                {
                    listOfDTOData = objPoliMemberWrapper.listOfDTOData;
                }
                for( String sLabel :objPoliMemberWrapper.lstLabels)
                {
                    HttpResponse httpRes = continuation.getResponse(sLabel);
                    oResponse = (AccumsServiceResponse_H_HUM)System.JSON.deserialize(httpRes.getbody(),AccumsServiceResponse_H_HUM.class); 
                    listOfDTOData.addAll(prepareDTOClass(oResponse));
                }
                if(null != objPoliMemberWrapper.setRemainingPolicyMember && objPoliMemberWrapper.setRemainingPolicyMember.size() > 0 )
                {
                 return PrepareAccumService(objPoliMemberWrapper.objCurrentPolicyMember,objPoliMemberWrapper.lstPolicyMember, objPoliMemberWrapper.setRemainingPolicyMember,listOfDTOData,objPoliMemberWrapper.accumsType,objPoliMemberWrapper.StartCounter, objPoliMemberWrapper.dtOfService);
                }
            }
            return new AccumulatorWrapper_DTO_HUM(listOfDTOData, false);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AccumsType_D_HUM','prepareServiceData');
            return NULL;
        }       
    }
    
    /**
    * Returns list of DTO class to send to UI
    * <p>
    * Method to prepare DTO from the response Coming from accums service
    *
    * @param  object of AccumsServiceResponse_H_HUM service response
    * @return  List
    * @see
    */
    private static List<VobBenefitsCurrent_DTO_HUM> prepareDTOClass(AccumsServiceResponse_H_HUM accumDataObj)
    {   
        List<VobBenefitsCurrent_DTO_HUM> listOfDTOData = new List<VobBenefitsCurrent_DTO_HUM>();
        if(null != accumDataObj && null != accumDataObj.GetBenefitAccumsResponse  && null != accumDataObj.GetBenefitAccumsResponse.GetBenefitAccumsResult) 
        {   
            listOfDTOData = prepareAccumsData(accumDataObj.GetBenefitAccumsResponse.GetBenefitAccumsResult.BenefitAccumsResponses);
        }
        return listOfDTOData;
    }
    
     /**
    * Returns list of DTO class 
    * <p>
    * Method to prepare DTO from the response Coming from accums service
    *
    * @param  object of AccumsServiceResponse_H_HUM.BenefitAccumsResponses service response
    * @return  List
    * @see
    */
    private static  List<VobBenefitsCurrent_DTO_HUM> prepareAccumsData(AccumsServiceResponse_H_HUM.BenefitAccumsResponses benefitAccumres) 
    {       
        List<VobBenefitsCurrent_DTO_HUM> lstBenefitsDTO = new List<VobBenefitsCurrent_DTO_HUM>();
        if(benefitAccumres != null && benefitAccumres.BenefitAccumsResponse != null && benefitAccumres.BenefitAccumsResponse.size() > 0)
        {
            for(AccumsServiceResponse_H_HUM.BenefitAccumsResponse benefitAccumsResponse : benefitAccumres.BenefitAccumsResponse)
            {
                if(benefitAccumsResponse != null && string.isNotBlank(benefitAccumsResponse.BenefitAccumsDataFound) &&'true'.equalsIgnoreCase(benefitAccumsResponse.BenefitAccumsDataFound))
                {
                    if(benefitAccumsResponse.BenefitAccumsDataList != null)
                    {  
                      lstBenefitsDTO.addAll(prepareDataFromResponse( benefitAccumsResponse.BenefitAccumsDataList));
                    }
                }
            }
        }
        return lstBenefitsDTO;
    }
    
    /**
    * Returns list of DTO class 
    * <p>
    * Method to prepare DTO from the response Coming from accums service
    *
    * @param  object of AccumsServiceResponse_H_HUM.BenefitAccumsDataList service response
    * @return  List
    * @see
    */
    private  static List<VobBenefitsCurrent_DTO_HUM> prepareDataFromResponse(AccumsServiceResponse_H_HUM.BenefitAccumsDataList benefitAccumsDataList)   
    {
        VobBenefitsCurrent_DTO_HUM DTOObj = NULL;
        List<VobBenefitsCurrent_DTO_HUM> lstBenefitsData = new List<VobBenefitsCurrent_DTO_HUM>();
        if(benefitAccumsDataList != null && benefitAccumsDataList.BenefitAccumsData != null && benefitAccumsDataList.BenefitAccumsData.size()>0 )
        {                                       
            for(AccumsServiceResponse_H_HUM.BenefitAccumsData benefitAccumsData : benefitAccumsDataList.BenefitAccumsData)
            {   
                if(benefitAccumsData != null && benefitAccumsData.AccumLimit != GLOBAL_CONSTANT_HUM.VOB_ACCUMS_ZERODOTZERO_HUM && benefitAccumsData.AccumLimit !='0' && benefitAccumsData.AccumLimit != GLOBAL_CONSTANT_HUM.VOB_ACCUMS_NOLIMIT_HUM )
                {
                    DTOObj = new VobBenefitsCurrent_DTO_HUM();
                    DTOObj.sAccumulator = benefitAccumsData.FullDescription;
                    DTOObj.sFrom = benefitAccumsData.AccumBegDate;
                    DTOObj.sTo = benefitAccumsData.AccumEndDate;
                    DTOObj.sLimit = GLOBAL_CONSTANT_HUM.VOB_ACCUMS_DOLLAR_SIGN_HUM + benefitAccumsData.AccumLimit;
                    DTOObj.sUsed = GLOBAL_CONSTANT_HUM.VOB_ACCUMS_DOLLAR_SIGN_HUM + benefitAccumsData.AccumAmt;
                   if(benefitAccumsData.AccumRemaining == '0')
                    { 
                        DTOObj.sAvailable = GLOBAL_CONSTANT_HUM.VOB_ACCUMS_DOLLAR_ZERO_SIGN_HUM;
                    }
                    else 
                    {
                        DTOObj.sAvailable = GLOBAL_CONSTANT_HUM.VOB_ACCUMS_DOLLAR_SIGN_HUM + benefitAccumsData.AccumRemaining;
                    }
                    lstBenefitsData.add(DTOObj);
                }
            }
        }
        return lstBenefitsData;
    }
    
}