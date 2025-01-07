/*****************************************************************************************
Apex class Name     : ImagingService_DTO_HUM 
Version             : 1.0
Function            : DTO class for Imaging service
Created Date        : 30 March 2018

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 * Prasanthi Kandula               211792                03/30/2018          REQ - 301521 DTO class to hold mutliple claims images recevied from service
 * Prasanthi Kandula               216296                04/18/2018          REQ - 301521 Updated logic to display ECN/DCN number based on URL received
 * Vijaya Koriprolu                                      06/25/2018          REQ - 366091 Changed Data Type foe bRepriced field.
*****************************************************************************************************************************/
public class ImagingService_DTO_HUM 
{
    public String sDCN {get; set;} 
    public String sECN {get; set;} 
    public String Receipt_Date {get; set;} 
    public String bRepriced {get; set;} 
    public Decimal dTotal_Billed_Charges {get; set;} 
    public String sBilling_Provider_Last_Facility_Name {get; set;} 
    public String sBilling_provider_id {get; set;} 
    public String sRendering_Provider_Last_Facility_Name {get; set;} 
    public String sRendering_Provider_Id {get; set;} 
    public String sClaim_Frequency_Code {get; set;} 
    public String sClaimURL {get; set;} 
    public String sECNOrDCN {get; set;}
}