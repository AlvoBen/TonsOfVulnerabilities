/**
 * @File Name          : AddressStandardizeList_D_HUM.cls
 * @Description        : Wrapper DTO Class to process the httpresponse of AddressStandardization call
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/11/2020, 7:22:17 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/

public with sharing class AddressStandardizeList_DTO_HUM {
    
    public AddressStandardizeResponse_DTO_HUM addressStandardizedResponse { get; set; }
    public string serviceCalloutError { get; set; }
    public boolean calloutErrored { get; set; }
    public FaultCode_DTO_HUM faultResponseDTO { get; set; }
    
}