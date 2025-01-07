/**
 * @File Name          : AddressStandardizeRequest_DTO_HUM.cls
 * @Description        : DTO class for AddressStandarization request.
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/15/2020, 2:12:47 AM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
 * 2.0    12/28/2020       Firoja Begam           US1755360 - EAS Service side changes - CR1876 (CR1808 Part 2) - SF
**/
public with sharing class AddressStandardizeRequest_DTO_HUM {

    public StandardizeAddressRequest StandardizeAddressRequest;

    public class StandardizeAddressRequest {
        public String AddressLine1;
        public String AddressLine2;
        public String City;
        public String StateCode;
        public String ZipCode;
        public Boolean IncludeValidationDetails = false;
        public Boolean IncludeInputOnError = false;
        public Boolean IncludeDPV = false;
        public Boolean IncludeGeoCode = true;
        public Boolean IncludeEWS = false;
        public String IncludeAddressLine1Split='false';
    }

    public static AddressStandardizeRequest_DTO_HUM parse(String json) {
        return (AddressStandardizeRequest_DTO_HUM) System.JSON.deserialize(json, AddressStandardizeRequest_DTO_HUM.class);
    }

}