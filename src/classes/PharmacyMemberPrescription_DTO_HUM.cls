/**************************************************************************** 
Apex Class Name  : PharmacyMemberPrescription_DTO_HUM
Version          : 1.0  
Created Date     : Jan 9, 2017
Function         : Wrapper class for GetScript service to store Member Prescription details
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh Chandgothia        70352                02/14/2017                 Member prescription Req# REQ - 296752
* Rajesh Keswani            82973                03/24/2017               	REQ - 302791: financial offline service mode
* Rajesh Chandgothia        82476                04/06/2017                 REQ - 301571 : Member Prescription section-- color indicator
***************************************************************************************************/
public class PharmacyMemberPrescription_DTO_HUM{
        public String Consent = '';
        public String RXNumber = '';
        public String Status = '';
        public String AutoRefillEnrolled = '';
        public String AutoRefillEligible = '';
        public String ControlledRX = '';
        public String WrittenDrug = '';
        public String DispensedDrug = '';
        public String Quantity = '';
        public String DaysSupply = '';
        public String RefillsRemaining = '';
        public String LastFillDate = '';   
        public String NextFillDate = '';
        Public String ExpirationDate = '';
        Public String PrescriberInformation = '';
        Public String NextFillMinDate =''; 
        Public String Phone= '';
        public String Fax= ''; 
        public String staleData= ''; 
}