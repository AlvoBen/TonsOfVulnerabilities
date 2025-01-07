/******************************************************************************************************************
Apex class Name    : AspectResponse_DTO_HUM
Version            : 1.0
Function           : Wrapper for sending response for Aspect service
Created On         : November 9 2016
Test Class         : Aspect_T_HUM
 
Modification Log:
*   Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*   Shruthi Karanth              48462             11/09/2016       Response Wrapper for Aspect service 
*   Shruthi Karanth                                12/06/2016       Defect 251848.0001 : Logic to send ATT,ACWT,ASA,%SL,APS as Decimals with 2 decimal points
******************************************************************************************************************/

global with sharing class AspectResponse_DTO_HUM 
{
    webservice String StartDate ;
    webservice String EndTime ;     
    webservice String Identifier ;
    webservice String DataGroup ;
    webservice Integer ContactsOffered_NCO = 0;
    webservice Integer ContactsHandles_NCH = 0;
    webservice Decimal AverageTalkTime_ATT = 0;
    webservice Decimal AverageAfterContactWorkTime_ACWT = 0;
    webservice Decimal AverageDelay_ASA = 0;
    webservice Decimal PercentServiceLevel = 0;
    webservice Decimal AveragePositionsStaffed_APS = 0;
    webservice Integer ActualAbandons_ABD = 0;
}