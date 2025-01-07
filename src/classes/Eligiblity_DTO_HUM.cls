/**
 * @File Name          : Eligiblity_DTO_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 01-28-2021
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
 * 2.0    1/28/2021        Chetan Sindhe           US1900555-PR00093155 - INC0877125 - Active, Future, Termed  Vs. Hierachy Rules
**/
public with sharing class Eligiblity_DTO_HUM {
    
    public EligiblityResponse_DTO_HUM eligibilityResponseDTO { get; set; }
    public string serviceCalloutError { get; set; }
    public boolean calloutErrored { get; set; }
    public FaultCode_DTO_HUM faultResponseDTO { get; set; }
    public string eligibilityRequestDTO { get; set; }
    public string metadataDTO { get; set; }

}