/**
 * @File Name          : AddressStandardizeList_D_HUM.cls
 * @Description        : DTO class for processing the HttpResponse from AddressStandardization call.
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 11-19-2020
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class AddressStandardizeResponse_DTO_HUM {

    public StandardizeAddressResponse StandardizeAddressResponse;

    public class StandardizeAddressResponse {
        public String SuccessFlag;
        public String AddressLine1;
        public String AddressLine2;
        public String City;
        public String StateCode;
        public String ZipCode;
        public String StateID;
        public String ZipCodePlus;
        public String CountyName;
        public String CountyID;
        public String Longitude;
        public String Latitude;
        public String Deliverable;
        public String ReturnCode;
        public String Valid;
        public String ChangeIndicator;
    }

    public static AddressStandardizeResponse_DTO_HUM parse(String json) {
        return (AddressStandardizeResponse_DTO_HUM) System.JSON.deserialize(json, AddressStandardizeResponse_DTO_HUM.class);
    }
}