/**************************************************************************** 
Apex Class Name  : AccumsType_LD_HUM
Version          : 1.0  
Created Date     : 5/27/2022
Function         : Controller class benefit Accums tab component 
Test class       : AccumsType_LT_HUM
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane 							  5/27/2022				     US- 3143662 Benefit Accums
* Nirmal Garg									  7/13/2023					 US-4785080
***************************************************************************************************/
public with sharing class AccumsType_LD_HUM {
    public static MemberPlan currentMemberPlan = null;
    public static Date dateOfService=null;
    public static List<MemberPlan> lstMemberPlans = new List<MemberPlan>();
    public static final String VOB_ACCUMS_SERVICE_HUM = 'AccumsServiceREST';
    public static final String VOB_ACCUMS_PREPARESERVICEDATA_HUM = 'prepareServiceDataREST';
    public static final String VOB_TIME_FORMAT_HUM = 'T23:59:59';
    public static final String VOB_ACCUMS_ZERODOTZERO_HUM = '0.00';
    public static final String VOB_ACCUMS_NOLIMIT_HUM = 'No Limit';
    public static final String VOB_ACCUMS_DOLLAR_SIGN_HUM = '$';
    public static final String VOB_ACCUMS_DOLLAR_ZERO_SIGN_HUM = '$0.00';
    public static final string PRODUCT_DENTAL = 'Dental';
    public static final string PRODUCT_DENTAL_ORTHODONTIC = 'Orthodontic';
    public static final string ProductDental = 'Dental';
    public static final string ProductDentalOrthodontic = 'Orthodontic';
    public static string sBenefitType = '';
    public static List<string> lstReqLabels = new List<string>();
    public static List<integer> lstYears = new List<integer>();
    public static integer startCounter = -1;
    public static set<Id> lstRemainingPlanMembersIdsSet = new set<Id>();
    public static integer accumRequestCounter = 0;


    @AuraEnabled
    public static object getAccumsData(string personId, string benefitType, string refDate){
        try {
            if(string.isNotBlank(personId)){
                sBenefitType = benefitType;
                switch on sBenefitType {
                    when 'Current' {
                        return getCurrentBenefitRequest(personId,refDate);
                    }
                    when else {
                        return getPreviousBenefitRequest(personId,refDate);
                    }
                }
            }
            return null;         
        } catch (Exception e) {
			HUMExceptionHelper.logErrors(e,'AccumsType_LD_HUM','getAccumsData');
            throw new AuraHandledException(e.getMessage());
        }
    }
    
    private static object getPreviousBenefitRequest(string personId, string refDate){
        currentMemberPlan = getCurrentMemberPlan(personId);
        if(currentMemberPlan != null){
            dateOfService = getDateOfService(currentMemberPlan,refDate);
            dateOfService = dateOfService > Date.today() ? Date.Today() : dateOfService;
            if(checkEndDate(dateOfService)){
                Date dLastDate = dateOfService.addMonths(-18);
                lstMemberPlans = getAllPoliciesOfMember(currentMemberPlan,dLastDate);
            }
            return createAccumsRequests();
        }
        return null;
    }
    
    private static List<MemberPlan> getAllPoliciesOfMember(MemberPlan planMemberObj, Date lastDate){
        List<MemberPlan> tempPolList = new  List<MemberPlan>();
        List<String> lstCurrentPolEndDate = planMemberObj.EffectiveTo.format().Split('/');
        List<String> lstCurrentPolEffectiveDate = planMemberObj.EffectiveFrom.format().Split('/');
        Date currPolEndDate = date.newinstance(Integer.valueof(lstCurrentPolEndDate[2]), Integer.valueof(lstCurrentPolEndDate[0]), Integer.valueof(lstCurrentPolEndDate[1]));
        Date currPolEffectiveDate = date.newinstance(Integer.valueof(lstCurrentPolEffectiveDate[2]), Integer.valueof(lstCurrentPolEffectiveDate[0]), Integer.valueof(lstCurrentPolEffectiveDate[1]));
        for(MemberPlan objPol :[select id ,name,EffectiveTo,EffectiveFrom,Status,Member_Dependent_Code__c,
                                Plan.Payer.enterprise_id__c,Plan.Purchaser_Plan_External_ID__c,
                                Member.Enterprise_ID__c,MemberId,SubscriberPlanId__c,
                                SubscriberPlanId__r.MemberId ,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C 
                                from MemberPlan 
                                where Product__c = :planMemberObj.Product__c 
                                AND  EffectiveTo != null  
                                AND EffectiveFrom != null
                                AND MemberId = :planMemberObj.MemberId with USER_MODE
                                order by EffectiveTo DESC])
        {
            
            List<String> lstPolEndDate = objPol.EffectiveTo.format().Split('/');
            List<String> lstPolEffectiveDate = objPol.EffectiveFrom.format().Split('/');
            if( null != lstPolEndDate && lstPolEndDate.size() > 2 && null != lstPolEffectiveDate  && lstPolEffectiveDate.size() > 2)
            {
                Date polEndDate = date.newinstance(Integer.valueof(lstPolEndDate[2]), Integer.valueof(lstPolEndDate[0]), Integer.valueof(lstPolEndDate[1]));
                Date polEffectiveDate = date.newinstance(Integer.valueof(lstPolEffectiveDate[2]), Integer.valueof(lstPolEffectiveDate[0]), Integer.valueof(lstPolEffectiveDate[1]));
                if( null != currPolEffectiveDate && null != currPolEndDate && null != polEndDate && null !=  polEffectiveDate  && polEndDate >= lastDate &&   polEffectiveDate <= dateOfService)
                {
                    tempPolList.add(objPol);
                }
            }
        }
        return tempPolList;
    }
    
    private static object getCurrentBenefitRequest(string personId, string refDate){
        currentMemberPlan = getCurrentMemberPlan(personId);
        if(currentMemberPlan != null){
            dateOfService = getDateOfService(currentMemberPlan,refDate);
            dateOfService = dateOfService > Date.today() ? Date.Today() : dateOfService;
            if(checkEndDate(dateOfService)){
                lstMemberPlans.add(currentMemberPlan);
            }
            return createAccumsRequests();
        }
        return null;
    }
    
    public static object createAccumsRequests(){
        if(lstMemberPlans != null && lstMemberPlans.size() > 0){
            for(MemberPlan mp : lstMemberPlans){
                lstRemainingPlanMembersIdsSet.add(mp.id);
            }
            return prepareAccumService(sBenefitType,dateOfService);
        }
        return null;
    }
    
    public static object prepareAccumService(string accumtype, date dateOfService){
        HUM_Webservice_Callout__c accumServiceObj = HUM_Webservice_Callout__c.getValues(VOB_ACCUMS_SERVICE_HUM);
        Continuation con = new Continuation(Integer.valueof(accumServiceObj.Timeout__c)/1000);
        con.continuationMethod = VOB_ACCUMS_PREPARESERVICEDATA_HUM;
        switch on accumtype {
            when 'Current' {
                return getCurrentBenefitObject(con,accumServiceObj);
            }
            when else {
                return getPreviousBenefitObject(con,accumServiceObj);
            }
        }
    }    
    
    private static string checkForPreviousYear(integer year){
        return lstYears.contains(year) ? 'true' : 'false';
    }
    
    private static string checkDateCondition(Date polEffectiveDate, Date polEndDate, integer asOfDatePrevYear){
        return PolEffectiveDate.Year() <= AsOfDatePrevYear && PolEndDate.Year() >= asOfDatePrevYear ? 'true' : 'false';
    }
    
    private static object getPreviousBenefitObject(Continuation con, HUM_Webservice_Callout__c accumServiceObj)
    {        
        lstYears = null;
        string asOfDate = null;        
        for(MemberPlan objMemberPlan : lstMemberPlans){
            if(objMemberPlan != null && lstRemainingPlanMembersIdsSet.contains(objMemberPlan.Id) && accumRequestCounter < 3){
                processMemberPlanObject(objMemberPlan,con,accumServiceObj);                
            }
        }
        lstYears = null;
        con.state = new  MemberPlanInfoWrapper_DTO_HUM_V2(currentMemberPlan, lstMemberPlans,  lstRemainingPlanMembersIdsSet,  lstReqLabels,null,sBenefitType,-1, dateOfService );
        return lstReqLabels != null && lstReqLabels.Size() > 0 ? con : null;
    }
    
    private static void processMemberPlanObject(MemberPlan objMemberPlan,Continuation con, HUM_Webservice_Callout__c accumServiceObj){
        startCounter = startCounter == -1 ? getYearCounter(sBenefitType, dateOfService) : startCounter;
        string asOfDate = null;
		lstYears = lstYears == null ? new List<Integer>() : lstYears;
        for(integer counter = startCounter; counter > 0; counter--){
            asOfDate = null;
            switch on counter {
                when 1 {
                    lstRemainingPlanMembersIdsSet.remove(objMemberPlan.id);
                    startCounter = counter - 2;
                }
            }
            integer asOfDatePrevYear = dateOfService.year() - counter;
            List<String> lstEndDate = objMemberPlan.EffectiveTo.format().split('/');
            List<String> lstEffectiveDate = objMemberPlan.EffectiveFrom.format().split('/');
            Date polEndDate = date.newinstance(Integer.valueof(lstEndDate[2]), Integer.valueof(lstEndDate[0]), Integer.valueof(lstEndDate[1]));
            Date polEffectiveDate = date.newinstance(Integer.valueof(lstEffectiveDate[2]), Integer.valueof(lstEffectiveDate[0]), Integer.valueof(lstEffectiveDate[1]));
            switch on checkDateCondition(polEffectiveDate,polEndDate,asOfDatePrevYear) {
                when 'true' {
                    asOfDate = String.valueof(date.newinstance(asOfDatePrevYear, dateOfService.Month(), dateOfService.Day())) + VOB_TIME_FORMAT_HUM;
                    switch on checkForPreviousYear(asOfDatePrevYear) {
                        when 'true' {
                            asOfDate=null;
                        }
                        when else {
                            lstYears.add(asOfDatePrevYear);
                        }
                    }
                }
            }
            if(asOfDate != null)
            {
                AccumsServiceRequest_H_HUM_V2 objAccumsReq = prepareAccumsRequest( currentMemberPlan, sBenefitType,asOfDate);
                HttpRequest request = Benefits_LS_HUM.builtAccumsRequest(new HttpRequest(),accumServiceObj,JSON.serialize(objAccumsReq));
                lstReqLabels.add(con.addHttpRequest(request));
                if( ++accumRequestCounter == 3)
                {
                    startCounter = counter-2;
                    break;
                }
            }
        }
    }
    
    private static object getCurrentBenefitObject(Continuation con, HUM_Webservice_Callout__c accumServiceObj){
        string asOfDate = dateOfService != null ? String.valueof(dateOfService) : null;
        List<string> lstReqLabels = new List<string>();
        if(asOfDate != null){
            AccumsServiceRequest_H_HUM_V2 objAccumsReq = prepareAccumsRequest( currentMemberPlan, sBenefitType,asOfDate);
            HttpRequest request = Benefits_LS_HUM.builtAccumsRequest(new HttpRequest(),accumServiceObj,JSON.serialize(objAccumsReq));
            lstReqLabels.add(con.addHttpRequest(request));
            con.state = new  MemberPlanInfoWrapper_DTO_HUM_V2(currentMemberPlan, lstMemberPlans,  lstRemainingPlanMembersIdsSet,  lstReqLabels,null,sBenefitType,-1, dateOfService );
            return con;
        }
        return null;
    }
    
    private static AccumsServiceRequest_H_HUM_V2 prepareAccumsRequest( MemberPlan objPolicyMember,String accumtype, String asOfDate)
    {
        List<String> lstPolicyExtrnID = null;
        String memId;
        AccumsServiceRequest_H_HUM_V2 requestDataObj = new AccumsServiceRequest_H_HUM_V2();
        AccumsServiceRequest_H_HUM_V2.request requestObj = new AccumsServiceRequest_H_HUM_V2.request();
        AccumsServiceRequest_H_HUM_V2.BenefitAccumsRequest benefitAccumReq;
        AccumsServiceRequest_H_HUM_V2.GetBenefitAccums mainDataObj = new AccumsServiceRequest_H_HUM_V2.GetBenefitAccums();
        AccumsServiceRequest_H_HUM_V2.BenefitAccumsRequests benefitAccumReqs = new AccumsServiceRequest_H_HUM_V2.BenefitAccumsRequests(); 
        benefitAccumReq = new AccumsServiceRequest_H_HUM_V2.BenefitAccumsRequest();          
        benefitAccumReq.MemberDependentCode = objPolicyMember.Member_Dependent_Code__c;
        memId = objPolicyMember.name;
        benefitAccumReq.MemberId = memId.left(9);         
        benefitAccumReqs.BenefitAccumsRequest = new List<AccumsServiceRequest_H_HUM_V2.BenefitAccumsRequest>();
        benefitAccumReqs.BenefitAccumsRequest.add(benefitAccumReq);
        requestObj.asOfdate = asOfDate;
        requestObj.BenefitAccumsRequests = benefitAccumReqs; 
        mainDataObj.request = requestObj;
        RequestDataObj.GetBenefitAccums = mainDataObj;
        return RequestDataObj;
    }
    
    public static object getContinutionObject(){
        HUM_Webservice_Callout__c accumServiceObj = HUM_Webservice_Callout__c.getValues(VOB_ACCUMS_SERVICE_HUM);
        Continuation con = new Continuation(Integer.valueof(accumServiceObj.Timeout__c)/1000);
        con.continuationMethod = VOB_ACCUMS_PREPARESERVICEDATA_HUM;
        return con;
    }
    
    private static integer getYearCounter(string accumtype, date dtDOS){
        return accumtype != 'Current' ? getPreviousYearCounter(dtDOS) : 1;
    }
    
    private static integer getPreviousYearCounter(date dtDOS){
        return dtDOS.Year() - Date.today().addMonths(-18).Year();
    }
    
    private static Boolean checkEndDate(Date policyEndDate )
    {   
        return policyEndDate != null && policyEndDate >= Date.today().addMonths(-18) ? true : false;
    }
    
    private static List<MemberPlan> getMemberPlans(string memberPlanId){
        List<MemberPlan> lstAllMemberPlans = new List<MemberPlan>();
        return null;
    }
    
    private static date getDateOfService(MemberPlan policyMemObj,string dos){
        if(string.isNotBlank(dos)){
            return date.newinstance(Integer.valueof(dos.substring(4, 8)), Integer.valueof(dos.substring(0, 2) ), Integer.valueof(dos.substring(2, 4)));
        }else{
            String effectiveTo = policyMemObj.EffectiveTo.format();
            List<String> lstPolEndDate = effectiveTo.split('/');
            return date.newinstance(Integer.valueof(lstPolEndDate[2]), Integer.valueof(lstPolEndDate[0]), Integer.valueof(lstPolEndDate[1]));
        }        
    }
    
    private static MemberPlan getCurrentMemberPlan(string planMemberId){
        List<MemberPlan> lstMemberPlans  = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,planId,Status,MemberId,EffectiveTo,Plan.Payer.enterprise_id__c,
                                            Plan.Purchaser_Plan_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where Id= :planMemberId WITH USER_MODE];
        return lstMemberPlans != null && lstMemberPlans.size() > 0 ? lstMemberPlans[0] : null;                                       
    }
    
    
    
    public static Object prepareServiceDataREST(object state)
    {       
        List<AccumsServiceResponse_H_HUM_V2> listOfServiceData = new List<AccumsServiceResponse_H_HUM_V2>();
        List<VobBenefitsCurrent_DTO_HUM> listOfDTOData =  new List<VobBenefitsCurrent_DTO_HUM>();
        MemberPlanInfoWrapper_DTO_HUM_V2 objPoliMemberWrapper = null;
        try
        {
            AccumsServiceResponse_H_HUM_V2 oResponse = NULL;            
            if(state != null)
            {       
                objPoliMemberWrapper = (MemberPlanInfoWrapper_DTO_HUM_V2)state;                
                listOfDTOData = null != objPoliMemberWrapper.listOfDTOData && objPoliMemberWrapper.listOfDTOData.size() > 0
                    ? objPoliMemberWrapper.listOfDTOData : new List<VobBenefitsCurrent_DTO_HUM>();
                for( String sLabel :objPoliMemberWrapper.lstLabels)
                {
                    HttpResponse httpRes = continuation.getResponse(sLabel);
                    oResponse = (AccumsServiceResponse_H_HUM_V2)System.JSON.deserialize(httpRes.getbody(),AccumsServiceResponse_H_HUM_V2.class);    
                    System.debug(Json.serialize(oResponse));
                    listOfDTOData.addAll(prepareDTOClass(oResponse));
                }
                /*if(null != objPoliMemberWrapper.setRemainingPolicyMember && objPoliMemberWrapper.setRemainingPolicyMember.size() > 0 )
                {
                    currentMemberPlan = objPoliMemberWrapper.objCurrentPolicyMember;
                    lstMemberPlans = objPoliMemberWrapper.lstPolicyMember;
                    lstRemainingPlanMembersIdsSet = objPoliMemberWrapper.setRemainingPolicyMember;
                    startCounter = -1;
                    return prepareAccumService( objPoliMemberWrapper.accumsType, objPoliMemberWrapper.dtOfService);
                }*/
            }
            return new AccumulatorWrapper_DTO_HUM(listOfDTOData, false);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AccumsType_LD_HUM','prepareServiceDataREST');
            throw new AuraHandledException(ex.getMessage());
        }       
    }
    
    private static List<VobBenefitsCurrent_DTO_HUM> prepareDTOClass(AccumsServiceResponse_H_HUM_V2 accumDataObj)
    {   
        List<VobBenefitsCurrent_DTO_HUM> listOfDTOData = new List<VobBenefitsCurrent_DTO_HUM>();
        if(null != accumDataObj && null != accumDataObj.GetBenefitAccumsResponse  && null != accumDataObj.GetBenefitAccumsResponse.GetBenefitAccumsResult) 
        {   
            listOfDTOData = prepareAccumsData(accumDataObj.GetBenefitAccumsResponse.GetBenefitAccumsResult.BenefitAccumsResponses);
        }
        return listOfDTOData;
    }
    
    private static  List<VobBenefitsCurrent_DTO_HUM> prepareAccumsData(AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponses benefitAccumres) 
    {       
        List<VobBenefitsCurrent_DTO_HUM> lstBenefitsDTO = new List<VobBenefitsCurrent_DTO_HUM>();
        if(benefitAccumres != null && benefitAccumres.BenefitAccumsResponse != null && benefitAccumres.BenefitAccumsResponse.size() > 0)
        {
            for(AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponse benefitAccumsResponse : benefitAccumres.BenefitAccumsResponse)
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
    
    private  static List<VobBenefitsCurrent_DTO_HUM> prepareDataFromResponse(AccumsServiceResponse_H_HUM_V2.BenefitAccumsDataList benefitAccumsDataList)   
    {
        VobBenefitsCurrent_DTO_HUM objVobBenefit = null;
        List<VobBenefitsCurrent_DTO_HUM> lstBenefitsData = new List<VobBenefitsCurrent_DTO_HUM>();
        AccumsServiceResponse_H_HUM_V2.DentalAccumsResponse dentalAccResponse = new AccumsServiceResponse_H_HUM_V2.DentalAccumsResponse();
        if(benefitAccumsDataList != null && benefitAccumsDataList.BenefitAccumsData != null && benefitAccumsDataList.BenefitAccumsData.size()>0 )
        {                                       
            for(AccumsServiceResponse_H_HUM_V2.BenefitAccumsData benefitAccumsData : benefitAccumsDataList.BenefitAccumsData)
            {   
                objVobBenefit = new VobBenefitsCurrent_DTO_HUM();
                if(benefitAccumsData != null && benefitAccumsData.AccumLimit != VOB_ACCUMS_ZERODOTZERO_HUM && benefitAccumsData.AccumLimit !='0' && benefitAccumsData.AccumLimit != VOB_ACCUMS_NOLIMIT_HUM && benefitAccumsData.AccumAmt != VOB_ACCUMS_ZERODOTZERO_HUM && benefitAccumsData.AccumAmt !='0' && benefitAccumsData.AccumAmt != VOB_ACCUMS_NOLIMIT_HUM)
                {
                    objVobBenefit.sAccumulator = getAccumulator(benefitAccumsData);
                    objVobBenefit.sFrom = benefitAccumsData.AccumBegDate;
                    objVobBenefit.sTo = benefitAccumsData.AccumEndDate;
                    objVobBenefit.sLimit = VOB_ACCUMS_DOLLAR_SIGN_HUM + benefitAccumsData.AccumLimit;
                    objVobBenefit.sUsed = VOB_ACCUMS_DOLLAR_SIGN_HUM + benefitAccumsData.AccumAmt;
                    objVobBenefit.sAvailable = benefitAccumsData.AccumRemaining == '0' ? VOB_ACCUMS_DOLLAR_ZERO_SIGN_HUM : VOB_ACCUMS_DOLLAR_SIGN_HUM + benefitAccumsData.AccumRemaining;
                    lstBenefitsData.add(objVobBenefit);
                }                
                else if( benefitAccumsData != null && benefitAccumsData.VisitLimit != VOB_ACCUMS_ZERODOTZERO_HUM && benefitAccumsData.VisitLimit !='0' && benefitAccumsData.VisitLimit != VOB_ACCUMS_NOLIMIT_HUM && benefitAccumsData.VisitUsed != VOB_ACCUMS_ZERODOTZERO_HUM && benefitAccumsData.VisitUsed !='0' && benefitAccumsData.VisitUsed != VOB_ACCUMS_NOLIMIT_HUM )
                {                    
                    objVobBenefit.sAccumulator = getAccumulator(benefitAccumsData);                   
                    objVobBenefit.sFrom = benefitAccumsData.AccumBegDate;
                    objVobBenefit.sTo = benefitAccumsData.AccumEndDate;
                    objVobBenefit.sLimit = benefitAccumsData.VisitLimit;
                    objVobBenefit.sUsed =  benefitAccumsData.VisitUsed;
                    objVobBenefit.sAvailable = benefitAccumsData.VisitRemaining;                    
                    lstBenefitsData.add(objVobBenefit);
                }
            }
        }
        return lstBenefitsData;
    }
    
    private static string getAccumulator(AccumsServiceResponse_H_HUM_V2.BenefitAccumsData benefitAccumsData){
        string accums = null;
        AccumsServiceResponse_H_HUM_V2.DentalAccumsResponse dentalAccResponse = new AccumsServiceResponse_H_HUM_V2.DentalAccumsResponse();
        if(benefitAccumsData.BenTypeCdDesc == PRODUCT_DENTAL || benefitAccumsData.BenTypeCdDesc == PRODUCT_DENTAL_ORTHODONTIC)
        {
            dentalAccResponse = benefitAccumsData.DentalAccumsResponse;
            accums = dentalAccResponse != null ? dentalAccResponse.BenefitDescription : accums;
        }
        else
        {
            accums =  benefitAccumsData.FullDescription;
        }
        return accums;
    }
    
    private static string getAvailable(AccumsServiceResponse_H_HUM_V2.BenefitAccumsData benefitAccumsData){
        string availData = '';
        availData = benefitAccumsData.AccumRemaining == '0' ? VOB_ACCUMS_DOLLAR_ZERO_SIGN_HUM : VOB_ACCUMS_DOLLAR_SIGN_HUM + benefitAccumsData.AccumRemaining;
        return availData;
    }
}