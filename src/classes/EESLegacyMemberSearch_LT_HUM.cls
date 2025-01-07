/***************************************************************************************************************************************************************** 
Apex Class Name : EESLegacyMemberSearch_LT_HUM
Version : 1.0 
Created On : 11/17/2020
Function : Test Class to cover EESLegacyMemberSearch_LCH_HUM

* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Rahul Krishan                                       11/17/2020                   Original Version
*  Kajal Namdev                                        03/25/2021                   Added one more function "EESLegacyMemberSearch2"
*  Vardhaman Jain									   04/14/2023					Added EESLegacyMemberSearch3 & EESLegacyMemberSearch4
**********************************************************************************************************************************************************************/

@isTest
public class EESLegacyMemberSearch_LT_HUM
{    
    @testSetup static void setUpTestData()
    {
        HUM_Webservice_Callout__c cs = new HUM_Webservice_Callout__c();
        cs.Name = system.label.HUMEESSearchSvc;
        cs.Certificate_Name__c = 'Humana_GuidanceCare_Mercury';
        cs.Active__c = true;
        cs.End_Point_URL__c ='https://int-crmservicews.humana.com/EPSServiceREST_V1.0/EnterpriseProviderSearch';
        cs.Timeout__c = '12000';
        cs.Request_Type__c = 'POST';
        cs.Content_Type__c ='application/json'; 
        insert cs;
        List<HUMEESLegacySearchPlatforms__c> lstConstants = new List<HUMEESLegacySearchPlatforms__c>();
        lstConstants.add(new HUMEESLegacySearchPlatforms__c(Name='Platform1',Platform_Value__c='LV'));
        lstConstants.add(new HUMEESLegacySearchPlatforms__c(Name='Platform2',Platform_Value__c='EM'));
        insert lstConstants; 
        system.assert(lstConstants !=NULL);
		HUMConstants__c eesinst = new HUMConstants__c();
        eesinst.Name = 'ENROLLMENT_EES';
        eesinst.StringValue__c = '1234';
        insert eesinst;
    }
    
    static testMethod void EESLegacyMemberSearch()
    {
        Test.setMock(WebServiceMock.class, new HUMIntEESLegacySearchServiceTest());
        test.startTest();
        EESLegacyMemberSearch_LCH_HUM eeslegacy = new EESLegacyMemberSearch_LCH_HUM();
        Object result = eeslegacy.submit('111585060');
        system.assert(result!=NULL);
        test.stopTest();        
    }   
    
    static testMethod void EESLegacyMemberSearch2(){
        EESLegacyMemberSearch_LCH_HUM eeslegacy = new EESLegacyMemberSearch_LCH_HUM();
        Continuation con = new Continuation(120);
        test.startTest();
        eeslegacy.submitAsync(con,'dummy');
        string domString = '<?xml version="1.0" encoding="UTF-8"?><products><product><Name>Xbox</Name><code>XBO</code></product><product><Name>Playstation</Name><code>PS</code></product><product><Family>Wii</Family></product><product><Family>Wii</Family></product><product><Family>Wii</Family></product></products>';
        eeslegacy.parseEEESResponse(domString,'dummy');
        Test.setMock(WebServiceMock.class, new HUMIntEESLegacySearchServiceTest());
        object result = eeslegacy.submit('111585060');
        system.assert(result!=NULL);
        test.stopTest();
    }
	
	static testMethod void EESLegacyMemberSearch3(){
        EESLegacyMemberSearch_LCH_HUM eeslegacy = new EESLegacyMemberSearch_LCH_HUM();
        test.startTest();
        String sResp = '<Return SessionID="5561184479" Version="2.23.0.0" Duration="1328" TimeStamp="05/15/2014 09:05:35:570" Environment="QA">';
        sResp += '<PlatformMembers><PlatformMember><FirstName>JOSEPH</FirstName><MiddleInitial>U</MiddleInitial><LastName>ADAMOWICZ</LastName>';
        sResp += '<DateOfBirth>07/18/1962</DateOfBirth><Gender>M</Gender><SSN>947225537</SSN><Age>60</Age><Address><Line1>1234 GREEN Park</Line1>';
        sResp += '<Line2/><Line3/><City>OOSTBURG</City><County>10</County><State>WI</State></Address><PID>0338730300212608</PID>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><Prefix>MR</Prefix><Suffix/><MiddleName>U</MiddleName><MaritalStatus>M</MaritalStatus>';
        sResp += '<Smoker>N</Smoker><Language>E</Language><DeceasedDate>NONE</DeceasedDate><Family><PlatformMember><FirstName>JOSEPH</FirstName>';
        sResp += '<SSN>947225537</SSN><Address><Line1>1234 GREEN Park</Line1><City>OOSTBURG</City><County>10</County><State>WI</State>';
        sResp += '</Address><MemberContracts><MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate>';
        sResp += '<EndDate>NONE</EndDate><Group><GroupID>553121</GroupID><GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages>';
        sResp += '<PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category><SrcPlatformCode>EM</SrcPlatformCode>';
        sResp += '<CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier><MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate>';
        sResp += '<MemberCoverageEndDate>NONE</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate>';
        sResp += '<MemberEnrollmentEndDate>NONE</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode>';
        sResp += '<TermedReason/><Student>N</Student><Disabled>N</Disabled><MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode>';
        sResp += '<SubscriberIDDependentCode>04</SubscriberIDDependentCode> </PlatformMemberCoverage></Coverages></MemberContract><MemberContract>';
        sResp += '<ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate><Group><GroupID>553121</GroupID>';
        sResp += '<GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier>';
        sResp += '<MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2013</MemberCoverageEndDate>';
        sResp += '<MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2022</MemberEnrollmentEndDate><PrePostTax/>';
        sResp += '<CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode><TermedReason/><Student>N</Student><Disabled>N</Disabled>';
        sResp += '<MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode><SubscriberIDDependentCode>04</SubscriberIDDependentCode>'; 
        sResp += '</PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses><EMailAddress><Address>NOFLTBXZCD5@Tdmtest.com</Address>';
        sResp += '<Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>9201123456</Number><Extension>0</Extension>';
        sResp += '<Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember><PlatformMember><FirstName>JOSEPH1</FirstName>';
        sResp += '<SSN>947225111</SSN><Address><Line1>1234 GREEN Park</Line1><City>OOSTBURG</City><County>10</County><State>WI</State></Address><MemberContracts>';
        sResp += '<MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate><Group><GroupID>553121</GroupID>';
        sResp += '<GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier><MemberCoverageEffectiveDate>';
        sResp += '02/01/2013</MemberCoverageEffectiveDate><MemberCoverageEndDate>NONE</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate>';
        sResp += '<MemberEnrollmentEndDate>NONE</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode>';
        sResp += '<TermedReason/><Student>N</Student><Disabled>N</Disabled><MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode>';
        sResp += '<SubscriberIDDependentCode>04</SubscriberIDDependentCode> </PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses>';
        sResp += '<EMailAddress><Address>NOFLTBXZCD5@Tdmtest.com</Address><Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers>';
        sResp += '<PhoneNumber><Number>9201123456</Number><Extension>0</Extension><Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember>';
        sResp += '</Family><EMailAddresses><EMailAddress></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber></PhoneNumber></PhoneNumbers></PlatformMember></PlatformMembers></Return>';

        eeslegacy.parseEEESResponseNew(sResp,'947225537');
        test.stopTest();
    }
            static testMethod void EESLegacyMemberSearch4(){
        EESLegacyMemberSearch_LCH_HUM eeslegacy = new EESLegacyMemberSearch_LCH_HUM();
        test.startTest();
        String sResp = '<Return SessionID="5561184479" Version="2.23.0.0" Duration="1328" TimeStamp="05/15/2014 09:05:35:570" Environment="QA">';
        sResp += '<PlatformMembers><PlatformMember><FirstName>JOSEPH</FirstName><MiddleInitial>U</MiddleInitial><LastName>ADAMOWICZ</LastName>';
        sResp += '<DateOfBirth>07/18/1962</DateOfBirth><Gender>M</Gender><SSN>947225537</SSN><Age>60</Age><Address><Line1>1234 GREEN Park</Line1>';
        sResp += '<Line2/><Line3/><City>OOSTBURG</City><County>10</County><State>WI</State></Address><PID>0338730300212608</PID>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><Prefix>MR</Prefix><Suffix/><MiddleName>U</MiddleName><MaritalStatus>M</MaritalStatus>';
        sResp += '<Smoker>N</Smoker><Language>E</Language><DeceasedDate>NONE</DeceasedDate><Family><PlatformMember><FirstName>JOSEPH</FirstName>';
        sResp += '<SSN>947225537</SSN><Address><Line1>1234 GREEN Park</Line1><City>OOSTBURG</City><County>10</County><State>WI</State>';
        sResp += '</Address><MemberContracts><MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate>';
        sResp += '<EndDate>NONE</EndDate><Group><GroupID>553121</GroupID><GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages>';
        sResp += '<PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category><SrcPlatformCode>EM</SrcPlatformCode>';
        sResp += '<CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier><MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate>';
        sResp += '<MemberCoverageEndDate>NONE</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate>';
        sResp += '<MemberEnrollmentEndDate>NONE</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode>';
        sResp += '<TermedReason/><Student>N</Student><Disabled>N</Disabled><MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode>';
        sResp += '<SubscriberIDDependentCode>04</SubscriberIDDependentCode> </PlatformMemberCoverage></Coverages></MemberContract><MemberContract>';
        sResp += '<ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate><Group><GroupID>553121</GroupID>';
        sResp += '<GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier>';
        sResp += '<MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2013</MemberCoverageEndDate>';
        sResp += '<MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2022</MemberEnrollmentEndDate><PrePostTax/>';
        sResp += '<CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode><TermedReason/><Student>N</Student><Disabled>N</Disabled>';
        sResp += '<MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode><SubscriberIDDependentCode>04</SubscriberIDDependentCode>'; 
        sResp += '</PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses><EMailAddress><Address>NOFLTBXZCD5@Tdmtest.com</Address>';
        sResp += '<Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>9201123456</Number><Extension>0</Extension>';
        sResp += '<Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember><PlatformMember><FirstName>JOSEPH1</FirstName>';
        sResp += '<SSN>947225111</SSN><Address><Line1>1234 GREEN Park</Line1><City>OOSTBURG</City><County>10</County><State>WI</State></Address><MemberContracts>';
        sResp += '<MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate><Group><GroupID>553121</GroupID>';
        sResp += '<GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier><MemberCoverageEffectiveDate>';
        sResp += '02/01/2013</MemberCoverageEffectiveDate><MemberCoverageEndDate>NONE</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate>';
        sResp += '<MemberEnrollmentEndDate>NONE</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode>';
        sResp += '<TermedReason/><Student>N</Student><Disabled>N</Disabled><MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode>';
        sResp += '<SubscriberIDDependentCode>04</SubscriberIDDependentCode> </PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses>';
        sResp += '<EMailAddress><Address>NOFLTBXZCD5@Tdmtest.com</Address><Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers>';
        sResp += '<PhoneNumber><Number>9201123456</Number><Extension>0</Extension><Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember>';
        sResp += '</Family><EMailAddresses><EMailAddress></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber></PhoneNumber></PhoneNumbers></PlatformMember></PlatformMembers></Return>';

        eeslegacy.parseEEESXMLResponse(sResp,'947225537');
        test.stopTest();
    } 
}