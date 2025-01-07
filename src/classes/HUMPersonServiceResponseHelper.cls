/**************************************************************************** 
Apex Class Name  : HUMPersonServiceResponseHelper
Version          : 1.0  
Created Date     : August 18 2014 
Function         : This class is used as a base class to handle the Member Info in Person Service
Test Class       : HUMPersonServiceResponseHelperTest
****************************************************************************
Modification Log:

* Developer Name                 Code Review                     Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham                   17353                          08/18/2014                   Original Version
* Shruthi Karanth                                               09/29/2014                   Updated test class
* Pradeep Veeravali              20976                          02/13/2015                   Added new field memberRelationship for COB change
* Mohammed Noor                                                 07/24/2020                   US825176 PR00093155 - MF 18 - Reusing this DTO for																							 mapping the MBE+ response to COB Template.
***************************************************************************************************/
public with sharing class HUMPersonServiceResponseHelper
{
    public String Member{get; set;}
    public String Relationship{get; set;}
    public String OICarriername{get; set;}
    public String OIEffectiveDate{get; set;}
    public String OIEndDate{get; set;}
    public String OIPrimary{get; set;}
	public String OIDentalLastVerifieddate{get; set;}
    public String OIMedicalLastVerifieddate{get; set;}
	public String OILastVerifieddate{get; set;}
    public String memberRelationship{get; set;}
	public String memberFirstName{get; set;}
    public String memberLastName{get; set;}
}