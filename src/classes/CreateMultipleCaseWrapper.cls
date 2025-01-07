/******************************************************************************************************************
Apex class Name    : CreateMultipleCaseWrapper
Version            : 1.0
Function           : This class is for wrapper class to hold case identefier and case details
Created On         : July 7th 2020
Test Class         : CreateMultipleCaseWrapperTest 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Asish Behera                                   07/08/2020          Original Version      
------------------------------------------------------------------------------------------------------------------- */
public class CreateMultipleCaseWrapper {
	
    public String sCaseIdentifier {set; get;}
    public Case caseRecord;
}