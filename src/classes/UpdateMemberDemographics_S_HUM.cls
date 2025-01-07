/**
 * @File Name          : UpdateMemberDemographics_S_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 10-07-2020
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    06/28/2020        Chetan Sindhe           Initial Version
 * 2.0    10/07/2020        Chetan Sindhe           US156992 - added JSON Parser to update effectivedate and timestamp with switch off and on.
**/
public with sharing class UpdateMemberDemographics_S_HUM {
    public static string sClassName = 'EligiblityRequest_S_HUM';
    
    /*
    * Method Name: constructRequest
    * Description: method is used to construct httprequest
    * Parameter: requestInput
    * Return Type: HTTPRequest
    */
    public static HTTPRequest constructRequest(string requestInput) {
        HttpRequest request = new HttpRequest();
        Datetime currDate = Datetime.now();
        String estDate = currDate.format('yyyy-MM-dd', 'America/New_York');
        String estDatetime = currDate.format('yyyy-MM-dd\'T\'HH:mm:ss.SSSSSS', 'America/New_York');
        string requestBody;
        try {
            request = generateHTTPRequestStructure(GLOBAL_CONSTANT_HUM.MM_UPD_SER_NAME_HUM);
                JSONParser parser = JSON.createParser(requestInput);
                JSONGenerator generator = JSON.createGenerator(false);

                while(parser.nextToken() != null) {
                    if(parser.getCurrentToken() == JSONToken.VALUE_STRING) {
                        if(parser.getCurrentName() != null) {
                            if(parser.getCurrentName() == 'timestamp') {
                                generator.writeStringField(parser.getCurrentName(), estDatetime);
                            }
                            else if(parser.getCurrentName() == 'effectivedate') {
                                generator.writeStringField(parser.getCurrentName(), estDate);
                            }
                            else {
                                generator.writeStringField(parser.getCurrentName(), parser.getText());
                            }
                        }
                        else {
                            generator.writeString(parser.getText());
                        }
                    }
                    else if(parser.getCurrentToken() == JSONToken.START_ARRAY) {
                        if(parser.getCurrentName() != null) {
                            generator.writeFieldName(parser.getCurrentName());
                        }
                        generator.writeStartArray();
                    }
                    else if(parser.getCurrentToken() == JSONToken.END_ARRAY) {
                        generator.writeEndArray();
                    }
                    else if(parser.getCurrentToken() == JSONToken.START_OBJECT) {
                        if(parser.getCurrentName() != null) {
                            generator.writeFieldName(parser.getCurrentName());
                        }
                        generator.writeStartObject();
                    }
                    else if(parser.getCurrentToken() == JSONToken.END_OBJECT) {
                        generator.writeEndObject();
                    }
                    else if(parser.getCurrentToken() == JSONToken.VALUE_NULL) {
                        generator.writeNullField(parser.getCurrentName());
                    }
                }
                requestBody = generator.getAsString();
            request.setBody(requestBody);
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'constructRequest');
            return null;
        }
    }

    /*
    * Method Name: generateHTTPRequestStructure
    * Description: method is used to construct httprequest structure
    * Parameter: serviceName
    * Return Type: HTTPRequest
    */
    private static HTTPRequest generateHTTPRequestStructure(string serviceName) {
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c objVEService;
        try {
            objVEService = HUM_Webservice_Callout__c.getValues(serviceName);
            if(objVEService != null) {
                request.setMethod(objVEService.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(objVEService.Certificate_Name__c))
                {
                    request.setClientCertificateName(objVEService.Certificate_Name__c);
                }
                if (String.isNotBlank(objVEService.Content_Type__c))
                {
                    request.setHeader(GLOBAL_CONSTANT_HUM.MM_VE_SER_CONTENTTYPE_HUM, objVEService.Content_Type__c);
                }
                request.setHeader(GLOBAL_CONSTANT_HUM.MM_VE_SER_CONECTION_HUM, GLOBAL_CONSTANT_HUM.MM_VE_SER_KEEPALIVE_HUM);
                request.setEndpoint(objVEService.End_Point_URL__c + objVEService.Service_Name__c);
                request.setHeader(GLOBAL_CONSTANT_HUM.MM_VE_SER_SOAPACTION_HUM, '');
                if(objVEService.Timeout__c != null) {
                    request.setTimeout(Integer.valueOf(objVEService.Timeout__c));
                }
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateHTTPRequestStructure');
            return null;
        }
    }
}