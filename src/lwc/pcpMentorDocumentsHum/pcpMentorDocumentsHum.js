/*
LWC Name        : PcpMentorDocumentsHum.html
Function        : LWC to display mentor documents on search screen.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     12/20/2022                  initial version
****************************************************************************************************************************-->
*/
import { LightningElement, track } from 'lwc';
import primaryCarePhysicanChangerMedicareOverview from '@salesforce/label/c.PrimaryCarePhysicanChangerMedicareOverview';
import primaryCarePhysicanChangerMedicaidOverview from '@salesforce/label/c.PrimaryCarePhysicanChangerMedicaidOverview';
import pCPChangesCommercialMemberOverview from '@salesforce/label/c.PCPChangesCommercialMemberOverview';
export default class PcpMentorDocumentsHum extends LightningElement {
    labels = {
        primaryCarePhysicanChangerMedicareOverview,
        primaryCarePhysicanChangerMedicaidOverview,
        pCPChangesCommercialMemberOverview,
    }
    @track urlIndividualAndGroupMedicare = 'https://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092980c99e9c&dl=0&searchID=VI-8d7a0e6a3ad8762&row=0&mode=Mentor&launchId=1579899688670';
    @track urlCommercialGBO = 'https://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=090009298275bc4d&dl=0&searchID=VI-8d7a0e70417223f&row=0&mode=Mentor&launchId=1579899863739';
    @track urlMedicaid = 'https://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=09000929829deb78&dl=0&searchID=VI-8d7a0e6d4a10e3f&row=0&mode=Mentor&launchId=1579899760493';
}