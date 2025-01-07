/**************************************************************************** 
Apex Class Name  : PharmacyPayer_DTO_HUM   
Version          : 1.0  
Created Date     : March 20, 2017
Function         : Wrapper class for Payer Infor for Order Detail Section 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Kumar Choubey     77115               03/20/2017                 REQ-305004 
***************************************************************************************************/
public class  PharmacyPayer_DTO_HUM {
	
	public String payer {get;set;}
	
	/**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param String
    * @return  None
    */
	public PharmacyPayer_DTO_HUM(String sPayer)
	{
		this.payer = sPayer;
		
	
	}
    
}