/*****************************************************************************************
Apex class Name    : DualEligibleDetailsHelper_H_HUM
Version            : 1.0
Function           : This is used as helper class for GetDualInfo Service 
Created Date       : 
Test Class         : ValidOIPolicyMemberID_D_T_HUM

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
* Raja Sekhar Reddy M V                     08/20/2020	                PR00095673_US_1276005_DualstatusIndicator
*********************************************************************************************************************************/    
public with sharing class DualEligibleDetailsHelper_H_HUM{
/**
    * This method generates list of other insurance on policy member page.
    * @Nmae     processOtherInsuranceResponse
    * @param    ValidOIWrapper_DTO_HUM response
    * @return   List of MBEPlusOIResponse_DTO_HUM 
    */
    public Static Boolean processDualResponse(MBEPlusOIResponse_DTO_HUM resvalidOI)
    { 
        MBEPlusOIResponse_DTO_HUM.GetMemberResponse oGetMemberResponse = resvalidOI.GetMemberResponse;
        MBEPlusOIResponse_DTO_HUM.Body oBody = oGetMemberResponse.Body;
        MBEPlusOIResponse_DTO_HUM.Member oMember = oBody.Member;
        boolean isDualEligibleInLastTwelveMonth;
        if (oMember.DualStatusList != null)
        {
            Date currentDate = System.today();
            Date fromDate = currentDate.addMonths(-12);

            for(MBEPlusOIResponse_DTO_HUM.DualStatus oDualStatus : oMember.DualStatusList)
            {
                if(oDualStatus.EffectiveDate != null && oDualStatus.EndDate != null)
                {
                    Date effectiveDate, endDate;
                    effectiveDate = parseDateFormat(oDualStatus.EffectiveDate); 
                    if (effectiveDate != null)
                    {
                        endDate = parseDateFormat(oDualStatus.EndDate);
                        if (endDate != null)
                        {
                            // If the date range is in last twelve months
                            if (effectiveDate <= currentDate && endDate >= fromDate) 
                            {
                                if(oDualStatus.Value != null && !oDualStatus.Value.equalsIgnoreCase('NO STATUS'))
                                {
                                    isDualEligibleInLastTwelveMonth = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
         return isDualEligibleInLastTwelveMonth;
    }


    /** This method parse the date from string
    * @Nmae     parseDateFormat
    * @param    String inputDate
    * @return   Date 
    */
    public static Date parseDateFormat(String inputDate)
    {
        Date dateRes = null;
        try {
                String candDate = inputDate.substring(0,10);
                dateRes = Date.valueOf(candDate);
            }
        catch (Exception e) {}
        
        if (dateRes == null) {
            try {
            String candDate = inputDate.substring(0,Math.min(10,inputDate.length()));
            dateRes = Date.parse(candDate);
            }
            catch (Exception e) {} 
        }        
        return dateRes;
    }
}