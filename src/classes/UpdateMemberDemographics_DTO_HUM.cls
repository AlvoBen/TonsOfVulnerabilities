/**
 * @File Name          : UpdateMemberDemographics_DTO_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 6/28/2020, 7:25:43 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
**/
public with sharing class UpdateMemberDemographics_DTO_HUM {
    
    public UpdateMemberDemographicsResponse_DTO_HUM updateResponseDTO { get; set; }
    public string serviceCalloutError { get; set; }
    public boolean calloutErrored { get; set; }
    public FaultCode_DTO_HUM faultResponseDTO { get; set; }
    public string updateRequestDTO { get; set; }

}