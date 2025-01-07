/************************************************************************************************************************************************
Apex Class Name :  CertificateRedirect_D_HUM
Version: 1.0
Created Date     : 4th May 2016
Function         : Class to redirect To Certificates
Test Class       :  CertificateRedirect_T_HUM
*************************************************************************************************************************************************

Modification Log :

* Developer Name            Review Number            Date                   Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam            27081                 05/04/2016              Original Version
* Sharan Shanmugam            28021                 05/20/2016              Defect 226893 : Added Refdate variable To be passed In MBE Request And added logic To create MBE Request
* Vishal Verma                29581                 06/07/2016              Defect 226893 : Removed the webservice Call As the Gen key Is now stored In Salesforce And updated the Parameters passed In URL.
* Vishal Verma                29779                 06/10/2016              Defect 226893 : Updated the mapping for SEQ.
* Pradeepkumar Dani           30524                 06/17/2016              Updated the logic for ccnbr param - Defect#226893
* Prudhvi Pamarthi            171453                01/22/2018               REQ - 328929 Fix for HUMExceptionHelper: DML
* Melkisan Selvaraj			  266572			    07/23/2018			    REQ - 361165 Certificate of Coverage Link
*************************************************************************************************************************************************/

Public With sharing Class CertificateRedirect_D_HUM
{
	/**
	* Assigns the value to sURL variable
	*<p>
	* @param  CertificateRedirect_C_HUM  C class reference
	* @return void
	*/
	Public void buildURL(CertificateRedirect_C_HUM certificateClass)
{
	Try
	{
		if (ApexPages.currentpage().getParameters().containsKey('Id'))
		{
			String sPolMemId = ApexPages.currentpage().getParameters().get('Id');
			if (sPolMemId != null)
			{
				Policy_Member__c polMemObj = [Select Id, Member__r.Mbr_Gen_Key__c, Policy__r.Platform__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Major_LOB_Frm__c,Policy__r.Product__r.Segment_Type__c,
					Policy__r.Major_LOB__c, Policy__r.Group_Name__r.Enterprise_Id__c,Subscriber__c,Subscriber__r.Member__r.Mbr_Gen_Key__c, Policy__r.Policy_External_ID__c,
					Effective_Date__c, End_Date__c from Policy_Member__c where Id = :sPolMemId];
				certificateClass.sURL = assignParams(polMemObj);
			}
		}
		else if (ApexPages.currentpage().getParameters().containsKey('PolId'))
		{
			String sPolId = ApexPages.currentpage().getParameters().get('PolId');
			if (sPolId != null)
			{
				Policy__c polObj = [Select Id ,Product__r.Name ,Platform__c,Major_LOB__c,Contract_Number__c,PBP_Code__c,Coverage_Plan_Effective_Date__c,Coverage_Plan_End_Date__c,
					Benefit_coverage__c, Group_Number__c,Medicare_Segment_ID__c, Product__r.Sold_Product_Key_Value__c, Major_LOB_Frm__c, Group_Name__r.Enterprise_Id__c, Policy_External_ID__c From Policy__c Where Id = :sPolId];
				certificateClass.sURL = assignGroupParams(polObj);
			}
		}
	}
		Catch(Exception ex)
	{
		//As DML Is Not currently allowed from Constructor boolean flag Is set to true 
		HUMExceptionHelper.bRestrictLog = true;
		certificateClass.listErrorLogs = HUMExceptionHelper.logErrors(ex, 'CertificateRedirect_D_HUM', 'buildURL');
	}

}

/**
* Method is used to assign parameters to URL
*<p>
* @param  Policy_Member__c  Object
* @return String   sURL
*/
private String assignParams(Policy_Member__c polMemObj)
{
	String sURL = HUMCertificateURL__c.getValues('CertificateURL') != NULL ? HUMCertificateURL__c.getValues('CertificateURL').CertificateListURL__c : '';
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PERS_HUM + (String.isNOTBlank(polMemObj.Member__r.Mbr_Gen_Key__c) ? polMemObj.Member__r.Mbr_Gen_Key__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PTFM_HUM + (String.isNOTBlank(polMemObj.Policy__r.Platform__c) ? polMemObj.Policy__r.Platform__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PROD_HUM + (String.isNOTBlank(polMemObj.Policy__r.Product__r.Sold_Product_Key_Value__c) ? polMemObj.Policy__r.Product__r.Sold_Product_Key_Value__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_LOB_HUM + (String.isNOTBlank(polMemObj.Policy__r.Major_LOB_Frm__c) ? polMemObj.Policy__r.Major_LOB_Frm__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_SEQ_HUM + (String.isNOTBlank(polMemObj.Policy__r.Product__r.Segment_Type__c) ? polMemObj.Policy__r.Product__r.Segment_Type__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_CUST_HUM + (String.isNOTBlank(polMemObj.Policy__r.Group_Name__r.Enterprise_Id__c) ? polMemObj.Policy__r.Group_Name__r.Enterprise_Id__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_CCNBR_HUM + (String.isNOTBlank(polMemObj.Policy__r.Policy_External_ID__c) ? polMemObj.Policy__r.Policy_External_ID__c.split('\\|')[3] : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_SUB_HUM + (polMemObj.Subscriber__c != NULL ? polMemObj.Subscriber__r.Member__r.Mbr_Gen_Key__c : String.isNOTBlank(polMemObj.Member__r.Mbr_Gen_Key__c) ? polMemObj.Member__r.Mbr_Gen_Key__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_ASOF_HUM + (String.isNotBlank(polMemObj.Effective_Date__c) ? polMemObj.Effective_Date__c : (String.isNotBlank(polMemObj.End_Date__c) ? polMemObj.End_Date__c : system.today().format()));
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PORT_HUM;
	return sURL;
}

/**
* Method is used to assign parameters to URL
*<p>
* @param  Policy__c  Object
* @return String   sURL
*/
private String assignGroupParams(Policy__c polObj)
{
	String sURL = HUMCertificateURL__c.getValues('CertificateURL') != NULL ? HUMCertificateURL__c.getValues('CertificateURL').CertificateListURL__c : '';
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PERSV2_HUM + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PTFM_HUM + (String.isNOTBlank(polObj.Platform__c) ? polObj.Platform__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PROD_HUM + (String.isNOTBlank(polObj.Product__r.Sold_Product_Key_Value__c) ? polObj.Product__r.Sold_Product_Key_Value__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_LOB_HUM + (String.isNOTBlank(polObj.Major_LOB_Frm__c) ? polObj.Major_LOB_Frm__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_CUST_HUM + (String.isNOTBlank(polObj.Group_Name__r.Enterprise_Id__c) ? polObj.Group_Name__r.Enterprise_Id__c : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_CCNBR_HUM + (String.isNOTBlank(polObj.Policy_External_ID__c) ? polObj.Policy_External_ID__c.split('\\|')[3] : '');
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_SUB_HUM + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_ASOF_HUM + (String.isNotBlank(polObj.Coverage_Plan_Effective_Date__c) ? polObj.Coverage_Plan_Effective_Date__c : (String.isNotBlank(polObj.Coverage_Plan_End_Date__c) ? polObj.Coverage_Plan_End_Date__c : system.today().format()));
	sURL += GLOBAL_CONSTANT_HUM.VOB_CERTIFICATE_PORT_HUM;
	return sURL;
}
}