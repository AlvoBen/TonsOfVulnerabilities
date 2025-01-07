/*****************************************************************************************************
Apex Class Name  	: CaseCompassExtension_DTO_HUM
Version   			: 1.0 
Created Date  		: 07/23/2017
Function  			: DTO to store compass related to cases
Test Class 			: CompassExtension_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sumit Pharande            149213              09/12/2017                   Original Version
* Pradeepkumar Dani			149213				11/17/2017				     re-factoring
*******************************************************************************************************/
public with sharing class Compass_DTO_HUM 
{
	public String MentorDocumentName = '';
	public String DocumentURL = '';
	public String CreatedDate = '';
	public String CreatedBy = '';
	public String CreatedByQueue = '';
	public String Id = '';
}