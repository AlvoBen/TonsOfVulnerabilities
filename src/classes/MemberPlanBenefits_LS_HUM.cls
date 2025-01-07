/*******************************************************************************************************************************
Apex Class Name : MemberPlanBenefits_LS_HUM
Version         : 1.0
Created On      : 05/01/2021
Function        : This is the service layer class for Medicaid Plan Details section on the Policy Detail page
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											05/01/2021
*     2.0                    Joel George                                            05/18/2021					PBE Service
*     3.0                    Joel George                                            06/03/2021                  Moved PBE Logic to MemberPlanDetail_LS_HUM
* 	  4.0					 Muthukumar 											06/16/2023					CRM "Indigenous Indicator" for FL, LA, SC, and IL Medicaid States
******************************************************************************************************************************************************************************/

public with sharing class MemberPlanBenefits_LS_HUM {
    
    public static MBEResponseV1_DTO_HUM mbeDetails;
    
    /**
* Returns MBEPlusRequest_DTO_HUM object that can then be utilized for sending the webservice request.
* This method is to create the request Structure for MBE service .
*
* @param    oPolMem   a member Plan record object derived from SOQL results.
* @return  MBEPlusRequest_DTO_HUM   request structure of the wrapper class MBEServiceRequest_H_HUM
*/
    
    public static MBEPlusRequest_DTO_HUM raceStructureMBEService(List<MemberPlan> oPolMem)
    {        
        set<id> lstPolId = new set<id>();
        for(MemberPlan obj : oPolMem){
            lstPolId.add(obj.id); 
        }
        
        MBEPlusRequest_DTO_HUM oReq = new MBEPlusRequest_DTO_HUM();
        MBEPlusRequest_DTO_HUM.GetMember getMember = new MBEPlusRequest_DTO_HUM.GetMember();            
        MBEPlusRequest_DTO_HUM.Member raceReq = new MBEPlusRequest_DTO_HUM.Member();
        MBEPlusRequest_DTO_HUM.PersonIdCriteria prsnID = new MBEPlusRequest_DTO_HUM.PersonIdCriteria();            
        MBEPlusRequest_DTO_HUM.GetCriteria getCriteria = new MBEPlusRequest_DTO_HUM.GetCriteria();
        MBEPlusRequest_DTO_HUM.Period per = new MBEPlusRequest_DTO_HUM.Period();
        List<String> lstper = new List<String>{ GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_CURRENT_HUM, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PAST_HUM, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_FUTURE_HUM };
            
            List<MemberPlan> pollist = new List<MemberPlan>([SELECT Id, Name,Member.Enterprise_Id__c FROM MemberPlan WHERE Id IN :lstPolId limit 1]);
        
        per.Period = lstper;
        prsnID.PeriodList = per;
        prsnID.AsOfDate = string.valueof(date.today());
        prsnID.PersonId = pollist[0].member.Enterprise_Id__c;
        getCriteria.PersonIdCriteria = prsnID;
        raceReq.GetCriteria = getCriteria;
        getMember.Member = raceReq;
        oReq.GetMember = getMember;
        Return oReq;              
    }
    
    /**
* Returns HttpRequest request with request body
* <p>
* Method to prepare httpRequest details and certification details
*
* @param  HUM_Webservice_Callout__c record .
* @return HttpRequest
*/
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        try
        {
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_PCP_SER_CONECTION, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_PCP_SER_SOAPACTION, '');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberPlanBenefits_LS_HUM', 'builtHttpRequest');
            return Null;
        }
    }
    
    /*
*  This method is used to parse the MBE service response
*  @name  parseMbeRaceresponse
*  @param response - MBE service response
*  @return MBEResponseV1_DTO_HUM - wrapper class to store data from service
*/
    public static MBEResponseV1_DTO_HUM parseMbeRaceresponse(String  response,String state)
    {
        MBEPlusResponse_DTO_HUM  oResponseMBE = new MBEPlusResponse_DTO_HUM ();
        oResponseMBE = (MBEPlusResponse_DTO_HUM)System.JSON.deserialize(response, MBEPlusResponse_DTO_HUM.class);
        if(oResponseMBE.GetMemberResponse != null)
        {
            MBEPlusResponse_DTO_HUM.GetMemberResponse oMBEResponse = oResponseMBE.GetMemberResponse;
            MBEPlusResponse_DTO_HUM.Body oBody = oMBEResponse.Body;
            MBEPlusResponse_DTO_HUM.Member oMember = oBody.Member;
            MBEPlusResponse_DTO_HUM.RaceList oRaceList = oMember.RaceList;
            
            
            if(oRaceList != null) {
                
                List<MBEPlusResponse_DTO_HUM.Race> oRace  = oRaceList.Race;
                
                
                if(oRace != null && oRace.size() > 0)
                {
                    mbeDetails = buildMbeWrapperList(oRace,state);
                }
            }
        }
        
        return mbeDetails;  
    }
    
    /*
*  This method is used to parse the MBE service reponse
*  @name  buildMbeWrapperList
*  @param MList<MBEPlusResponse_DTO_HUM.Race> oRace

*  @return MBEResponseV1_DTO_HUM - wrapper class to store data from service
*/
    private static MBEResponseV1_DTO_HUM buildMbeWrapperList( List<MBEPlusResponse_DTO_HUM.Race> oRace, String state)
    {
        try
        {
            MBEResponseV1_DTO_HUM objWrapperMediacid = new MBEResponseV1_DTO_HUM ();
            if(oRace[0].Code!= null){
                if((oRace[0].Code.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Race_Code_G) && state!='LA') || 
                oRace[0].Code.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Race_Code_i) || 
                (oRace[0].Code.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Race_Code_AI) && state=='IL'))
                {
                    objWrapperMediacid.Code = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Namerican_Indc_Y; 
                }
                else{
                    objWrapperMediacid.Code = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Namerican_Indc_N; 
                }
                
            } 
            
            return objWrapperMediacid ;
        }
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'MemberPlanBenefits_LS_HUM', 'buildMbeWrapperList');
            return null;
        }
    }
 
}