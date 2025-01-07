/**
 * @File Name          : DemographicUpdateMBEPlus_DTO_HUM.cls
 * @Description        : Wrapper DTO class for processing MBEPLus response at the GUI side
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/12/2020, 12:02:40 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class DemographicUpdateMBEPlus_DTO_HUM {
    
    public DemographicMBEPlusResponse_DTO_HUM memberResponseDTO { get; set; }
    public string memberCalloutError { get; set; }
    public Boolean memberCalloutErrored { get; set; }
    public FaultCode_DTO_HUM faultResponseDTO { get; set; }

}