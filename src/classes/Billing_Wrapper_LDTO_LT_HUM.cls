/******************************************************************************************************************
Apex class Name    : Billing_Wrapper_LDTO_LT_HUM
Version            : 1.0
Function           : Parameter transfer for Billing Service
Created On         : 05/23/2022
Test Class For     : Billing_Wrapper_LDTO_HUM

Modification Log:
* Developer                   Code Review            Date                       Description
* -----------------------------------------------------------------------------------------------------------------
* Prashant Moghe                                     05/23/2022                 Original Version
------------------------------------------------------------------------------------------------------------------*/
@isTest
public class Billing_Wrapper_LDTO_LT_HUM {
    static testMethod void testWrapperDTO(){
        Billing_Wrapper_LDTO_HUM test1 = new Billing_Wrapper_LDTO_HUM();
        system.assertEquals(test1.sRecId, null);
         Billing_Wrapper_LDTO_HUM test2 = new Billing_Wrapper_LDTO_HUM('123', 'sGroupName', 'sObjectName','sPlatformCode', 'PEM1795');
        system.assertEquals(test2.sRecId, '123');
        system.assertEquals(test2.sGroupName, 'sGroupName');
    }
}