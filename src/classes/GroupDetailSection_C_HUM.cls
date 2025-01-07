/*****************************************************************************************
Apex class Name    : GroupDetailSection_C_HUM 
Version            : 1.0
Function           : Controller to VF Page:: GroupDetailSectionHUM.
                     This class is used to display Group Details and Division-Subgroup Related List information from GBE Service .
Created Date       : 01 Oct 2015
Test Class         : GroupDetailSection_C_T_HUM 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   SuryaKumari Medicherla       27320               10/05/2015          Original Version
 *   SuryaKumari Medicherla       27320               10/15/2015          Displaying Divison-SubGroup Information(REQ-198628)
 *   Santhi Mandava               27320               10/29/2015          Fixed CC comments
 *   SuryaKumari Medicherla       27484               11/02/2015          Code modified for REQ-213234(MTVx link in Division-Subgroup related list)
 *   SuryaKumari Medicherla       27921               11/19/2015          Code modified for defect:206965.0002 to show MTVx link only when source plat form code is 'EM' and hide for CAS groups
                                                                          (Access modifier of variable "objGrpAccount" changed from private to public to access in VF page)
 *   Manish Kumar                 27951               11/19/2015          Code modified to fix defect#206767.0001. Code modified Line#257
 *   SuryaKumari Medicherla                           12/03/2015          Code modified to fix defect#208961.0001. Assigning blank value for sMedGrpEnrllCount if service return '0' from service
*********************************************************************************************************************************/

public with sharing class GroupDetailSection_C_HUM 
{
    private AsyncHUMGBEService.GetGroupInfoResponse_elementFuture oGetDetailInfo = NULL;
    @testVisible private HUMGBEService.ResponseGroupDetail_element oGBEResponse = NULL;
    @testVisible private Integer iTotalRec = (Integer)HUMConstants__c.getInstance('GBES_DivisionSubGroup_Records_HUM').IntegerValue__c;
    @testVisible private Integer iMaxRecordsToDisplay = (Integer)HUMConstants__c.getInstance('GBES_DivisionSubGroup_Records_HUM').IntegerValue__c;
    @testVisible private HUMGBEService.VitalityIndicatorDTO[] arrVitalityIndicatorDTO = NULL;
    @testVisible private HUMGBEService.ResponseGroupBenefitDTO oResponseGroupBenefitDTO = NULL;
    @testVisible private GroupDetailSection_S_HUM objServiceInstance = NULL;
    
    
    /*
     * Method Name : GroupDetailSection_C_HUM 
     * Description : Constructor for the class , initialize variable
     * Return type : N/A
     * Parameter   : ApexPages.StandardController
     */
    public GroupDetailSection_C_HUM(ApexPages.StandardController oStdController)
    {
       try
       {
           iCount = 1;
           iTotalCount = 0;
           bShowDivisionDetails = true;
           bShowGroupDetails = true;
           if(!Test.isRunningTest()) oStdController.addFields(new List<String> {'Source_Customer_Key__c','Source_Platform_Code__c'});
           objGrpAccount = (Account)oStdController.getrecord();
           objServiceInstance = new GroupDetailSection_S_HUM();
       }
       catch(Exception ex)
       {
           HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM', 'GroupDetailSection_C_HUM');
       }  
    }
    
    public String sMedicalEffDate {get;set;}
    public String sDentalEffDate {get;set;}
    public String sNxtRenDt {get;set;}
    public String sMedGrpEnrllCount {get;set;}
    public String sDentalGrpEnrllCount {get;set;}
    public Boolean bPrevious{get;set;}
    public Boolean bNext {get;set;}
    public List<GroupDetailSection_H_HUM> lstVitalityRecords{get;set;}
    public Boolean bShowGroupDetails{get;set;}
    public Boolean bShowDivisionDetails{get;set;}
    public Integer iCount{get;set;}
    public Integer iTotalCount{get;set;}
    public Integer iSize{get;set;}
    public Integer iCountPerPage{get;set;}
    public Account objGrpAccount{get;set;}
    
    /*
     * Method Name   :  startRequest
     * Description   :  This method is used to Call the Service while loading the page
     * Return Type   :  Object
     * Parameters    :  NA
     */
    public Object startRequest() 
    {
        if(!String.isblank(objGrpAccount.Source_Platform_Code__c) && !String.isblank(objGrpAccount.Source_Customer_Key__c))
        {
            Continuation con = new Continuation(HUMConstants.iGBESTimeOut);   
            con.continuationMethod = HUMConstants.sProcessMethodName;
            oGetDetailInfo = objServiceInstance.requestToServer(objGrpAccount,con,True,String.valueOf(iCount),String.valueOf(iMaxRecordsToDisplay));
            return con;
        }
        else
        {
           bShowGroupDetails =  true;
           bShowDivisionDetails = false;
           return NULL;
        }
    }
    
    /*
     * Method Name   :  getNext
     * Description   :  This method will be called whenever we click on Next button to show more records
     * Return Type   :  Object
     * Parameters    :  NA
     */
    public Object getNext()
    {
        if(iTotalCount > iMaxRecordsToDisplay)
        {
            iCount += iMaxRecordsToDisplay; 
            if(iCount+iMaxRecordsToDisplay > iTotalCount)
            {
                iTotalRec = (iTotalCount - iCount) + 1;
            }
            Continuation con = new Continuation(HUMConstants.iGBESTimeOut);
            con.continuationMethod=HUMConstants.sProcessMethodName;
            oGetDetailInfo = objServiceInstance.requestToServer(objGrpAccount,con,false,String.valueOf(iCount),String.valueOf(iTotalRec));
            return con;
        }
        else
        {
            return null;
        }   
    }
    
    /*
     * Method Name   :  getPrevious
     * Description   :  This method is will be called whenever user clicks on "Previous" button
     * Return Type   :  Object
     * Parameters    :  NA
    */
    public Object getPrevious()
    {
        if(iTotalCount > iMaxRecordsToDisplay && iCount > 1)
        {
        
            iCount -= iMaxRecordsToDisplay; 
            iTotalRec = iMaxRecordsToDisplay;
            Continuation con = new Continuation(HUMConstants.iGBESTimeOut);
            con.continuationMethod= HUMConstants.sProcessMethodName;

            oGetDetailInfo = objServiceInstance.requestToServer(objGrpAccount,con,false,String.valueOf(iCount),String.valueOf(iMaxRecordsToDisplay));
            return con;
        }
        else
        {
            return null;
        }   
    }
   
    /*
     * Method Name   :  processResponse
     * Description   :  This is the Callback method for Continous Object to process the Response data
     * Return Type   :  Void
     * Parameters    :  NA
    */
    public Void processResponse()
    {
         oResponseGroupBenefitDTO = new HUMGBEService.ResponseGroupBenefitDTO();
         try
         {
             if(!Test.isRunningTest()) oGBEResponse = oGetDetailInfo.getvalue();
             
             if(oGBEResponse != NULL)
             {
                 bShowGroupDetails = true;
                 
                 if(oGBEResponse.Platform != NULL && (oGBEResponse.Platform.equals(HUMConstants.sMTV_Value) || oGBEResponse.Platform.equals(HUMConstants.sLV_Value)  || oGBEResponse.Platform.equals(HUMConstants.sCI_Value ) || oGBEResponse.Platform.equals(HUMConstants.sCAS_Value) || oGBEResponse.Platform.equals(HUMConstants.sEM_Value)))    
                 {
                     groupBenefitAndVitalityData();  
                 }
                 else    
                 {
                     bShowDivisionDetails = false;
                 }
             }
             else
             {    
                  bShowGroupDetails = false;    
             }
         }
         catch(Exception ex)
         {
             bShowGroupDetails =  false;
             HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM', 'processResponse');
         }
    }
    
    /*
     * Method Name   :  groupBenefitAndVitalityData
     * Description   :  This is the used to process Vitality and Benefit Data
     * Return Type   :  Void
     * Parameters    :  NA
    */
    private Void groupBenefitAndVitalityData()
    {
        try
        {
            bShowDivisionDetails = true;  
            HUMGBEService.VitalityIndicatorListDTO oVitalityIndicatorListDTO = new HUMGBEService.VitalityIndicatorListDTO();
            oResponseGroupBenefitDTO = oGBEResponse.ResponseGroupBenefitDTO;
            if(oResponseGroupBenefitDTO != NULL)    groupBenefitData();  
            
            oVitalityIndicatorListDTO = oGBEResponse.VitalityIndicatorListDTO;
            if(oVitalityIndicatorListDTO != NULL)    arrVitalityIndicatorDTO = oVitalityIndicatorListDTO.VitalityIndicatorDTO;
            bNext = false;
            bPrevious = (iCount == 1 ? true : false);
            iSize = Integer.valueOf(oGBEResponse.VitalityRecordsTotalCount);
            
            
            if(iSize <= iMaxRecordsToDisplay)
            {
                bNext = true;
                bPrevious = true;
                iCountPerPage = iSize;
            }
            else if(iTotalRec < iMaxRecordsToDisplay && Integer.valueOf(oGBEResponse.VitalityRecordsTotalCount) > iMaxRecordsToDisplay)
            {
                iSize = iTotalRec;
                bNext = true;
                iCountPerPage  = iTotalRec+iCount -1;
            }
            else
            {
                iSize = iMaxRecordsToDisplay;
                iCountPerPage = iCount+iSize-1;
            }
            
            lstVitalityRecords = new List<GroupDetailSection_H_HUM>();
            for(Integer i = 0; i < iSize; i++)
            {
                if(arrVitalityIndicatorDTO[i] != NULL)
                {
                    lstVitalityRecords.add(new GroupDetailSection_H_HUM(objGrpAccount.Source_Customer_Key__c, arrVitalityIndicatorDTO[i].VitalityIndicator,arrVitalityIndicatorDTO[i].SubGroupID,arrVitalityIndicatorDTO[i].SubGroupName ,
                             arrVitalityIndicatorDTO[i].UnitCount,arrVitalityIndicatorDTO[i].Unit,arrVitalityIndicatorDTO[i].EffectiveProvision));        
                }             
            }  
        }
        catch(Exception ex)
        {
            bShowGroupDetails = false;
            HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM', 'groupBenefitAndVitalityData');
        }   
    }
    
    /*
     * Method Name   :  groupBenefitData
     * Description   :  This is the used to get GroupBenefit Data
     * Return Type   :  Void
     * Parameters    :  NA
    */
    private Void groupBenefitData()
    {
         if(!String.ISBLANK(oResponseGroupBenefitDTO.EffectiveDate))    sMedicalEffDate = dateParse(oResponseGroupBenefitDTO.EffectiveDate);
         
         if(!String.ISBLANK(oResponseGroupBenefitDTO.DentalEffectiveDate))    sDentalEffDate = dateParse(oResponseGroupBenefitDTO.DentalEffectiveDate);
         
         if(!String.ISBLANK(oResponseGroupBenefitDTO.NextRenewalDate))    sNxtRenDt = dateParse(oResponseGroupBenefitDTO.NextRenewalDate);
         
         sMedGrpEnrllCount = oResponseGroupBenefitDTO.EnrolledSubscriberCountMedical;
         sDentalGrpEnrllCount = oResponseGroupBenefitDTO.EnrolledSubscriberCountDental;
         if(!String.isBlank(sDentalGrpEnrllCount) && sDentalGrpEnrllCount.trim().equals('0'))    sDentalGrpEnrllCount = '';
         if(!String.isBlank(sMedGrpEnrllCount) && sMedGrpEnrllCount.trim().equals('0'))    sMedGrpEnrllCount = '';
         iTotalCount = Integer.Valueof(oGBEResponse.VitalityRecordsTotalCount);
    }
    
    /*
     * Method Name   :  dateParse
     * Description   :  This is the used to parse date to MM/DD/YYYY format
     * Return Type   :  String
     * Parameters    :  String
    */  
    private String dateParse(String strDate)
    {
        String[] arrSplittedDate = strDate.split('-');
        String sParsedDate = '';
        if(arrSplittedDate.size() >= 3)    sParsedDate = arrSplittedDate[1]+ '/' +arrSplittedDate[2]+ '/' +arrSplittedDate[0];
        return sParsedDate;
    }
}