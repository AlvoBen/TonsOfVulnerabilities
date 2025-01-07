/*******************************************************************************************************************************************************
Apex Class Name  : ClassificationTypeIntent_DTO
Version          : 1.0
Created Date     : 05/17/2018
Function         : This class is wrapper class to store data for ClassificationType, Classification and Intent
Test Class       : ClassificationTypeIntent_T_DTO
*****************************************************************************************************************************************************
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Archana                				           05/17/2018                Original Version
* Archana                	237454		           06/30/2018                Added few more variables
************************************************************************************************************************************************/
public with sharing class ClassificationTypeIntent_DTO
{
	public ClassificationTypeIntent_DTO(String sClassificationType, String sClassification , String sIntent)
    {

        this.sClassificationType =sClassificationType;
        this.sClassification = sClassification;
        this.sIntent=sIntent;
      
    }
    public ClassificationTypeIntent_DTO(String sClassificationType, String sClassification , String sIntent, Boolean isTypeToAdd, Boolean isClassificationToAdd, Boolean isIntentToAdd, Boolean isCTCToAdd) 
    {

        this.sClassificationType =sClassificationType;
        this.sClassification = sClassification;
        this.sIntent=sIntent;
		this.isTypeToAdd = isTypeToAdd;
        this.isClassificationToAdd = isClassificationToAdd;
        this.isIntentToAdd = isIntentToAdd;
        this.isCTCToAdd = isCTCToAdd;
    }

    public String sClassificationType ;
    public String sClassification ;
    public String sIntent;
	public boolean isTypeToAdd;
    public boolean isClassificationToAdd;
    public boolean isIntentToAdd;
    public boolean isCTCToAdd;
   

}