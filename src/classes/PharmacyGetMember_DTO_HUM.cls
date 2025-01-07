/**************************************************************************** 
Apex Class Name :  PharmacyGetMember_DTO_HUM   
Version: 1.0  
Created Date     : Jan 9, 2017
Function         : Wrapper class for GetMember Pharmacys service 
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Kumar Choubey                      01/09/2017                 REQ-296805 DTO class for Getmember Service
* Avinash Kumar Choubey                      01/17/2017                 REQ-291114 
* Shiva Pasumarty                            02/02/2017                 OMS Log Notes Req# REQ - 272225
* Sharan Shanmugam							 02/16/2017					Credit Card information REQ - 296663
* Avinash Kumar Choubey     77115            03/20/2017                 REQ-305004 Added PharmacyPayer DTO
* Pradeep Dani              302363           08/13/2018                 Added response node for duplicate customers.
* Sumit Pharande            345615           03/27/2019                 REQ-362063 Added Pharmacy Order DTO
* Yogesh Gupta								 05/28/2019				    REQ - 400046 Add error Message for member not found
* Lakshmi Madduri							 10/30/2019					Added customerkey node - 410211
***************************************************************************************************/
Public Class PharmacyGetMember_DTO_HUM
{
    Public PharmacyGetMember_Demographic_DTO_HUM  objPharDemographicDetails {Get;Set;}
    Public PharmacyAllergyConditions_DTO_HUM objPharmacyAllergiesAndHConditions {Get;Set;}
    Public List<PharmacyOMSLogNotes_DTO_HUM> objPharOMSDetails {Get;Set;}
    Public PharmacyGetMember_CreditCard_DTO_HUM  objPharCreditCardDetails {Get;Set;}
    Public PharmacyPayer_DTO_HUM  objPharmacyPayer {Get;Set;}
    Public RightSourceGetMemberResponse_DTO_HUM.DuplicateCustomers objDupCusts{ Get;Set; }
    Public PharmacyEditMember_Order_DTO_HUM objPharmacyOrder {Get;Set;}
	//This parameter is used to show/hide error messaga on Pharmacy VF Page if member not found
	public Boolean showErrorMsgs {get;set;}
	public RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail customerInfo{get;set;}
    
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  PharmacyGetMember_Demographic_DTO_HUM  objDemographicDetails DTO class of allergies And Health COnditions
    * @param  PharmacyAllergyConditions_DTO_HUM objPharDemographicDetails  DTO class for Demographics
    * @return  None
    */
	//Adding new argument showErrorMsgs to show member not found message on pharmacy Visualforce page
    Public PharmacyGetMember_DTO_HUM(PharmacyGetMember_Demographic_DTO_HUM  objDemographicDetails, PharmacyAllergyConditions_DTO_HUM objPharmacyAllergiesAndHConditions, List<PharmacyOMSLogNotes_DTO_HUM> objPharmacyOMSDetails, PharmacyGetMember_CreditCard_DTO_HUM  objPharmacyCreditCardDetails,PharmacyPayer_DTO_HUM  objPharmacyPayer,RightSourceGetMemberResponse_DTO_HUM.DuplicateCustomers objDupCusts,PharmacyEditMember_Order_DTO_HUM objPharmacyOrder, Boolean showErrorMsgs)
    {
      this.objPharDemographicDetails =  objDemographicDetails ;
      this.objPharmacyAllergiesAndHConditions = objPharmacyAllergiesAndHConditions;
      this.objPharOMSDetails = objPharmacyOMSDetails;
      this.objPharCreditCardDetails = objPharmacyCreditCardDetails;
      this.objPharmacyPayer = objPharmacyPayer;
      this.objDupCusts = objDupCusts;
      this.objPharmacyOrder =objPharmacyOrder ;
	  this.showErrorMsgs = showErrorMsgs;
    }
	//Used in pharmacy page 
    Public PharmacyGetMember_DTO_HUM(PharmacyGetMember_Demographic_DTO_HUM  objDemographicDetails, PharmacyAllergyConditions_DTO_HUM objPharmacyAllergiesAndHConditions, List<PharmacyOMSLogNotes_DTO_HUM> objPharmacyOMSDetails, PharmacyGetMember_CreditCard_DTO_HUM  objPharmacyCreditCardDetails,PharmacyPayer_DTO_HUM  objPharmacyPayer,RightSourceGetMemberResponse_DTO_HUM.DuplicateCustomers objDupCusts,PharmacyEditMember_Order_DTO_HUM objPharmacyOrder, Boolean showErrorMsgs,RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail customerInfo)
    {
      this.objPharDemographicDetails =  objDemographicDetails ;
      this.objPharmacyAllergiesAndHConditions = objPharmacyAllergiesAndHConditions;
      this.objPharOMSDetails = objPharmacyOMSDetails;
      this.objPharCreditCardDetails = objPharmacyCreditCardDetails;
      this.objPharmacyPayer = objPharmacyPayer;
      this.objDupCusts = objDupCusts;
      this.objPharmacyOrder =objPharmacyOrder ;
	  this.showErrorMsgs = showErrorMsgs;
	  this.customerInfo = customerInfo;
    }  
}