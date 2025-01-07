/*******************************************************************************************************************************
Apex Class Name : Pharmacy_CreateEditOrder_DTO_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : DTO for Pharmacy_CreateEditOrder_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------*
* Rajesh Chandgothia        89722                      04/18/2017                  REQ 305689 Original Version
*********************************************************************************************************************************/

public with sharing class Pharmacy_CreateEditOrder_DTO_HUM
{
    public PharmacyGetMember_Demographic_DTO_HUM objPharDemographicDetails {get;set;}
    public  List<PharmacyMemberPrescription_DTO_HUM> prescriptionDetails  {get;set;}
   
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  PharmacyGetMember_Demographic_DTO_HUM  objDemographicDetails
    * @param  List<PharmacyMemberPrescription_DTO_HUM> prescriptionDetails
    * @return  None
    */ 
public Pharmacy_CreateEditOrder_DTO_HUM(PharmacyGetMember_Demographic_DTO_HUM objPharDemographicDetails,List<PharmacyMemberPrescription_DTO_HUM> prescriptionDetails)
   {
        this.objPharDemographicDetails=objPharDemographicDetails;
        this.prescriptionDetails=prescriptionDetails;
   }   
}