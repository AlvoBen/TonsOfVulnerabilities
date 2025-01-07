/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-05-2020   Chetan Sindhe                         Initial Version
**/
@istest

public with sharing class SearchMemberMockResponse_T_HUM implements HttpCalloutMock {

    public HTTPResponse respond(HTTPRequest req) 
    {
        // Creating a fake success response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{"SearchMemberResponse":{"MemberList":[{"FirstName":"LINDSYE","LastName":"DADSON","MiddleName":"X","MiddleInitial":"X","DateOfBirth":"02/05/1961","MasterId":"1007485919","Prefix":"Prefix info was not found","Suffix":"Suffix info was not found","Ssn":"565562506"}]}}');
        res.setStatusCode(200);
        return res;
    }

}