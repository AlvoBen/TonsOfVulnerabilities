/************************************************************************************************************************************************
  Apex Class Name  : FloatingLinkConstants_HUM
  Version          : 1.0 
  Created Date     : May 24 2018
  Function         : Class to hold the Constants used in the various detail page for FloatingLinks_CMP_HUM.component
  Test Class       : 
  * Developer Name            Code Review                 Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  * Darshan Sharma									  05/24/2018				  Original Version
  * Binod Katwal              294960                  10/12/2018                  REQ - 359002 - Add Associated Forms & Information Section to Floating Navigation Link
  * Sumanth Naredla                                   04/13/2019                  REQ - 396258 - User Story - Add Jump Links to Benefits Page (GBORF) (ID# 55)    
  * Manikanta                 356372                  05/31/2019                  REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
  * Suraj patil		                                    06/1/2019                  REQ - 366241 - MTV Member Remarks - Policy Member Page
  * Yogesh Gupta                                      11/25/2019                 REQ - 410405 - Relocate Tooth History - Dental
  * Deepankar Binjola         62335                   11/11/2019                 REQ-410411 Relocate Reimbursement Limit - Dental
  * Harish Gowda                                      02/24/2020                 USER STORY 889485 PR00091574 - MF 1 - AHT - Add jump links on Billing Profile Detail page (RF)
  * Santhi Mandava                                    03/06/2010                 US - 903330 Display jump links on Auth/Referral Detail page 
  * Riddhi Mittal                                      07/27/2020               755539-PR00093155 - MF 21 - Other Insurance (OI) Status in CRM
  * Anjani Vaddadi                                     03/17/2021               User Story#-1260081 -T1PRJ0003372 - MF 2, MF 3, MF 4 - Veteran Update Functionlity
  * Rahul Chaurasia								      10/13/2021  				User Story 2376253: T1PRJ0003805 - Billing- Ability to see if an agent/broker is web registered
  * Vamshi Krishna Pemberthi						  11/03/2021				Rollback User Story 2376253: T1PRJ0003805 - Billing- Ability to see if an agent/broker is web registered 
  * Vamshi Krishna Pemberthi 						  02/10/2022				DF - 4425 Adding floating links to claims detail page  *****************************************************************************************************************************************************************************/

public with sharing class FloatingLinkConstants_HUM
{
    
    public static final string sfloatLinkAccount0 = 'Power of Attorney';
    public static final string sfloatLinkAccount1 = 'Associated Forms & Information';
    public static final string sfloatLinkAccount2 = 'Person Account Detail';
    public static final string sfloatLinkAccount3 = 'Business Account Detail';
    public static final string sfloatLinkAccount4 = 'Group Details';
    public static final string sfloatLinkAccount5 = 'Verify Demographics';    
    public static final string sfloatLinkAccount7 = 'Policies';
    public static final string sfloatLinkAccount8 = 'Case History';
    public static final string sfloatLinkAccount9 = 'Legacy Contact History';
    public static final string sfloatLinkAccount10 = 'Consumer ID';
    public static final string sfloatLinkAccount11 = 'Policies';
    public static final string sfloatLinkAccount12 = 'Interactions (Interacting With)';
    public static final string sfloatLinkAccount13 = 'Live Chat Transcripts';
    public static final string sfloatLinkAccount14 = ' Veteran Information';
        
    public static final string sfloatLinkPolicy1 = 'Policy Member Detail';
    public static final string sfloatLinkPolicy2 = 'Policy Details';
    public static final string sfloatLinkPolicy3 = 'Other Insurance';
    public static final string sfloatLinkPolicy4 = 'Case History';
    public static final string sfloatLinkPolicy5 = 'Eligibility Information';
    public static final string sfloatLinkPolicy6 = 'MyHumana Details';
    public static final string sfloatLinkPolicy7 = 'Legacy Contact History';
    public static final string sfloatLinkPolicy8 = 'Member Ids';
    public static final string sfloatLinkPolicy9 = 'Policy Members';    	
    public static final string sfloatLinkPolicy10 = 'Medicaid Policy Details';
    public static final string sfloatLinkPolicy11 = 'MTV Member Remarks';
	   
    public static final string sfloatLinkBenefits1 = 'Current Benefit Period';
    public static final string sfloatLinkBenefits2 = 'Benefit Snapshot';
    public static final string sfloatLinkBenefits3 = 'Pharmacy Mail Order Benefit Snapshot';
    public static final string sfloatLinkBenefits4 = 'Benefits Summary';
    public static final string sfloatLinkBenefits5 = 'Benefits Search';
    public static final string sfloatLinkBenefits6 = 'Accumulator Information';
    public static final string sfloatLinkBenefits7 = 'Dental Waiting Periods';
    public static final string sfloatLinkBenefits8 = 'Dental Policy Information';
	  public static final string sfloatLinkBenefits9 = 'Tooth History';
	  public static final string sfloatLinkBenefits10 = 'Reimbursement Limit';

    //Member Billing Profile/Detail Page.
    public static final string sfloatLinkMemberBillingprofile1 = 'Profile Account Information';
    public static final string sfloatLinkMemberBillingprofile2 = 'Payer Information';
    public static final string sfloatLinkMemberBillingprofile3 = 'Balance Information';
    public static final string sfloatLinkMemberBillingprofile4 = 'Last Payment Information';
    public static final string sfloatLinkMemberBillingprofile5 = 'Collection Information';
    public static final string sfloatLinkMemberBillingprofile6 = 'Rate Information';
    public static final string sfloatLinkMemberBillingprofile7 = 'Last Invoice Summary';

    //Auth detail page constants
     public static final string sfloatLinkAuthDetail1 = 'Contact Information';
     public static final string sfloatLinkAuthDetail2 = 'Provider Information';
     public static final string sfloatLinkAuthDetail3 = 'Authorization Information';
     public static final string sfloatLinkAuthDetail4 = 'Authorization/Referral Information';
     public static final string sfloatLinkAuthDetail5 = 'Diagnosis Codes';
     public static final string sfloatLinkAuthDetail6 = 'Procedure Codes';
     public static final string sfloatLinkAuthDetail7 = 'Letters';
     public static final string sfloatLinkAuthDetail8 = 'Communication Records';
	 
	 //Claim Details page constants
	public static final string sfloatLinkClaimDetail1 = 'Claim Details';
    public static final string sfloatLinkClaimDetail2 = 'Diagnosis Codes';
    public static final string sfloatLinkClaimDetail3 = 'Texas Deficiency';
    public static final string sfloatLinkClaimDetail4 = 'Provider Details';
    public static final string sfloatLinkClaimDetail5 = 'Line Items';
    public static final string sfloatLinkClaimDetail6 = 'Claim Payment Details';
    public static final string sfloatLinkClaimDetail7 = 'MTV Remarks';
    public static final string sfloatLinkClaimDetail8 = 'Member Claim Statements';
    public static final string sfloatLinkClaimDetail9 = 'Provider Claim Statements';
}