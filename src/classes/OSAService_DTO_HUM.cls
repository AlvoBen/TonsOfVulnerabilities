/**
 * @File Name          : OSAService_DTO_HUM.cls
 * @Description        : DTO Wrapper class for handling OSA Service response at the GUI side.
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/12/2020, 12:16:42 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class OSAService_DTO_HUM {
    
    public OSAServiceResponse_DTO_HUM osaServiceResponse { get; set; }
    public string serviceCalloutError { get; set; }
    public boolean calloutErrored { get; set; }
    public FaultCode_DTO_HUM faultResponseDTO { get; set; }
    
}