/**************************************************************************** 
Apex Class Name  : PharmacyAllergyConditions_DTO_HUM   
Version          : 1.0  
Created Date     : Jan 17, 2017
Function         : Wrapper class for Allergies and Health Conditions 
****************************************************************************
Modification Log:
* Developer Name          		Code Review #         	Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Kumar Choubey    		59057                  01/17/2017                 REQ-291114 
***************************************************************************************************/
public class  PharmacyAllergyConditions_DTO_HUM {
	
	public List<String> lstAllergies {get;set;}
	public List<String> lstHealthConditions {get;set;}
	
	/**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  List<String>  lstAllergies list of Allergies
    * @param  List<String>
    * @return  None
    */
	public PharmacyAllergyConditions_DTO_HUM(List<String> lstAllergies, List<String> lstHealthConditions)
	{
		this.lstAllergies = lstAllergies;
		this.lstHealthConditions = lstHealthConditions;
	
	}
    
}