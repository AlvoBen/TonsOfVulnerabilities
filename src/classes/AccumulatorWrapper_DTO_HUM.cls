/**************************************************************************** 
Apex Class Name  : AccumulatorWrapper_DTO_HUM   Version  : 1.0  
Created Date     : December 10, 2015
Function         : Wrapper class for Accumulator Table's of Benefit Page 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Choubey                        04/20/2016                 Original Version
***************************************************************************************************/
public class AccumulatorWrapper_DTO_HUM {
    public List<VobBenefitsCurrent_DTO_HUM> lstAccumsData = new List<VobBenefitsCurrent_DTO_HUM>();
    public Boolean bHasMoreThanTwoYearPolicies = false;  
  
   
   /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  List<VobBenefitsCurrent_DTO_HUM>  Accums Data 
    * @param  Boolean flag for having beyond two year policies
    * @return  None
    */
    
	public AccumulatorWrapper_DTO_HUM(List<VobBenefitsCurrent_DTO_HUM> tlstAccumsData ,Boolean tbHasMoreThanTwoYearPolicies)
	{
		lstAccumsData = tlstAccumsData;
		tbHasMoreThanTwoYearPolicies = bHasMoreThanTwoYearPolicies;
		
	}
	
	 }