/**************************************************************************************************************************
Apex class Name    : HUMAVFTemplateService_Plugin 
Version            : 1.0
Function           : Plugin for AVFTemplateService
Created On         : May 21 2015
Test Class         : HUMAVFTemplateService_PluginTest 

Modification Log:
*   Developer                   Code Review         Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Asha Deepika Koyyana           23343            5/21/2015             Original Version
*  Asha Deepika Koyyana           23816            6/1/2015              As service team changed the data types in oAVFTemplateResponseHelper
                                                                         incorporated the same over here.   
*  Asha Deepika Koyyana           23845            6/11/2015             As we are facing deployment issues removed the custom settings   
*  Praveen Kumar Parimi           23971            6/15/2015             Changed the criteria for CMSMAPlanNumber,CMSPDPPlanNumber                                                   
*  Asha Deepika Koyyana                            8/18/2015             Modified the code to match the field length validation with Icd dcoument
                                                                         added response error codes to error logs.         
*  Praveen Kumar Parimi                            8/26/2015             Refactored the Invoke Method. 
* Apoorv Jain                                      6/16/2016             REQ - 237066 : Removed the DPASS service call
* Shreya CHoodamani                                11/6/2017             REQ#: 332366 : Checkmarx security remediation - Added with sharing security keyword
* Asish Behera                                     02/27/2018            REQ - 355832 : Cto accept different Phone Number Format (other than the existing format ) for the Address Verification Form [ AVF] Template Flow 
*********************************************************************************************************************************/

Global With sharing Class HUMAVFTemplateService_Plugin Implements Process.Plugin
{
    String sCMSMAPlanNumber = '';
    String sCMSPDPPlanNumber = '';
    String strPhone ='';
    String sResidentialPhoneNumber ='';
    String sTempMemberUMID ='';
    String sTempMedicareId = '';
    String sFirstName = '';
    String sLastName = '';
    Id PolicyID = NULL;
    Map<String,String> mapCipherClearText = new Map<String,String>();
        
    /*
    * Method Name   :    invoke
    * Description   :    This method is used to call the AVFTemplate service and the Flow calls this at runtime    
    * Return Type   :    Process.PluginRequest
    * Parameters    :    Process.PluginRequest
    */
    
    global Process.PluginResult invoke(Process.PluginRequest request)
    {
       HUMAVFTemplateResponseHelper oAVFTemplateResponseHelper  = new HUMAVFTemplateResponseHelper();
       try
       {
            //Method calls for various conditional assignments
            populateDecryptValues(request);
            populatePhoneNumber(request);            
            populatePlanNumber(request);                
            HUMAVFTemplateRequestHelper obj = populateAssociateDetails(request, populateAddressesFields(request));
        
            //Additional Assignments to  HUMAVFTemplateRequestHelper Instance                          
            obj.PersonFirstName = sFirstName;
            obj.PersonLastName = sLastName;
            obj.MemberUMID = sTempMemberUMID ;
            obj.MedicareId = sTempMedicareId ;
            obj.CMSMAPlanNumber = sCMSMAPlanNumber;
            obj.CMSPDPPlanNumber = sCMSPDPPlanNumber;            
            
            oAVFTemplateResponseHelper  = HUMAVFTemplateService.getAVFTemplate(obj);
            
            Map<String,Object> result = new Map<String,Object>();
            if(oAVFTemplateResponseHelper  != NULL)
                {
                    result.put('Error', oAVFTemplateResponseHelper.sErrorCode );
                    result.put('ErrorDescription', oAVFTemplateResponseHelper.sErrorDescription );
                }
                
                return new Process.PluginResult(result); 
       }
       catch(Exception ex)
       {
           HUMExceptionHelper.logErrors(ex, 'HUMAVFTemplateService_Plugin', 'invoke');
           Map<String,Object> result = new Map<String,Object>();
           return new Process.PluginResult(result); 
       }
     }

    /*
    * Method Name   :    populatePlanNumber
    * Description   :    This method is used to  populate CMSMAPlanNumber and CMSPDPPlanNumber
    * Return Type   :    NA
    * Parameters    :    Process.PluginRequest
    */
    public void populatePlanNumber(Process.PluginRequest request)
    {
        String PolmemPolType = (String) request.inputParameters.get('PolicyMemberPolicyType');
        
        if(request.inputParameters.get('PolicyContractNumber') != NULL)
        {
            String sTempPolicyContractNumber = (String) request.inputParameters.get('PolicyContractNumber');
            
            if(PolmemPolType == 'MA' || PolmemPolType == 'MAPD')
            {
                sCMSMAPlanNumber = sTempPolicyContractNumber ;
            }
            else if(PolmemPolType == 'PDP')
            {
                sCMSPDPPlanNumber = sTempPolicyContractNumber ;
            }
        
            if(sCMSMAPlanNumber.length() > 5)
            {
                sCMSMAPlanNumber = sCMSMAPlanNumber.Substring(0,5);
            }
            if( sCMSPDPPlanNumber.length() > 5)
            {
                sCMSPDPPlanNumber = sCMSPDPPlanNumber.Substring(0,5) ;
            }
        } 
    }
         
    /*
    * Method Name   :    populateAddressesFields
    * Description   :    This method is used to  populate all the Address related fields.
    * Return Type   :    HUMAVFTemplateRequestHelper 
    * Parameters    :    Process.PluginRequest
    */
    public HUMAVFTemplateRequestHelper populateAddressesFields(Process.PluginRequest request)
    {
        HUMAVFTemplateRequestHelper obj = new HUMAVFTemplateRequestHelper();
            
         if(request.inputParameters.get('PermanentResidentialAddress') != NULL)
            obj.PermanentResidentialAddress = (String) request.inputParameters.get('PermanentResidentialAddress');        
         if(request.inputParameters.get('PermanentResidentialCityName') != NULL)
            obj.PermanentResidentialCityName = (String) request.inputParameters.get('PermanentResidentialCityName');            
         if(request.inputParameters.get('PermanentResidentialCountyName') != NULL)
            obj.PermanentResidentialCountyName = (String) request.inputParameters.get('PermanentResidentialCountyName');            
         if(request.inputParameters.get('PermanentResidentialStateCode') != NULL)
            obj.PermanentResidentialStateCode = (String) request.inputParameters.get('PermanentResidentialStateCode');            
         if(request.inputParameters.get('MailingAddress') != NULL)
            obj.MailingAddress = (String) request.inputParameters.get('MailingAddress');            
         if(request.inputParameters.get('MailingCityName') != NULL)
            obj.MailingCityName = (String) request.inputParameters.get('MailingCityName');            
         if(request.inputParameters.get('MailingStateCode') != NULL)
            obj.MailingStateCode =  (String) request.inputParameters.get('MailingStateCode');            
         if(request.inputParameters.get('TemporaryAddress') != NULL)
            obj.TemporaryAddress = (String) request.inputParameters.get('TemporaryAddress');            
         if(request.inputParameters.get('TemporaryCityName') != NULL)
            obj.TemporaryCityName =  (String) request.inputParameters.get('TemporaryCityName');            
         if(request.inputParameters.get('TemporaryCountyName') != NULL)
            obj.TemporaryCountyName =  (String) request.inputParameters.get('TemporaryCountyName');            
         if(request.inputParameters.get('TemporaryStateCode') != NULL)
            obj.TemporaryStateCode = (String) request.inputParameters.get('TemporaryStateCode');            
         if(request.inputParameters.get('TemporaryZipCode') != NULL)
            obj.TemporaryZipCode = (String) request.inputParameters.get('TemporaryZipCode');
         if(request.inputParameters.get('PermanentResidentialZipCode') != NULL)
            obj.PermanentResidentialZipCode = (String) request.inputParameters.get('PermanentResidentialZipCode');            
         if (sResidentialPhoneNumber != '') obj.PermanentResidentialPhoneNumber = sResidentialPhoneNumber ;            
         if(request.inputParameters.get('PermanentResidentialStartDate') != NULL)
            obj.PermanentResidentialStartDate = (String)( ((Datetime ) request.inputParameters.get('PermanentResidentialStartDate')).format('MMddyyyy'));             
         if(request.inputParameters.get('MailingZipCode') != NULL)
            obj.MailingZipCode = (String) request.inputParameters.get('MailingZipCode');      
        
            return obj;
   } 
   
    /*
    * Method Name   :    populateAssociateDetails
    * Description   :    This method is used to  populate all the Associate related fields
    * Return Type   :    HUMAVFTemplateRequestHelper 
    * Parameters    :    Process.PluginRequest
    */
    public HUMAVFTemplateRequestHelper populateAssociateDetails(Process.PluginRequest request, HUMAVFTemplateRequestHelper obj)
    {
         if((String) request.inputParameters.get('CaseNumber') != NULL)
            obj.CaseNumber = (String) request.inputParameters.get('CaseNumber');            
         if(request.inputParameters.get('EditSequenceNumber') != NULL)
            obj.EditSequenceNumber= string.valueof( (Integer.Valueof(request.inputParameters.get('EditSequenceNumber'))));            
         if(request.inputParameters.get('HumanaAssociateName') != NULL) 
            obj.HumanaAssociateName = (String) request.inputParameters.get('HumanaAssociateName');
         if(request.inputParameters.get('PersonSpeakingWith') != NULL)
            obj.PersonSpeakingWith =  (String) request.inputParameters.get('PersonSpeakingWith');            
         if(request.inputParameters.get('RelationToMember') != NULL)
            obj.RelationToMember =  (String) request.inputParameters.get('RelationToMember');          
         if(request.inputParameters.get('TemporaryOSAStartDate') != NULL)
            obj.TemporaryOSAStartDate = ((String)((DateTime)request.inputParameters.get('TemporaryOSAStartDate')).format('MMddyyyy'));            
         if(request.inputParameters.get('TemporaryOSAEndDate') != NULL)
            obj.TemporaryOSAEndDate =  ((String)((DateTime)request.inputParameters.get('TemporaryOSAEndDate')).format('MMddyyyy'));
            obj.LastSaveDate = (String) system.Now().format('MMddyyyy');            
         if(request.inputParameters.get('EnterprisePersonID') != NULL)
            obj.EnterprisePersonID = (String) request.inputParameters.get('EnterprisePersonID');   
        
        return obj;
    }
        
    /*
    * Method Name   :    populatePhoneNumber
    * Description   :    This method is used to  populate ResidentialPhoneNumber
    * Return Type   :    NA
    * Parameters    :    Process.PluginRequest
    */
    public void  populatePhoneNumber(Process.PluginRequest request)
    {
        if(request.inputParameters.get('PermanentResidentialPhoneNumber') != NULL)
        {
            strPhone= ((String) request.inputParameters.get('PermanentResidentialPhoneNumber'));
            // phone number format change code.
            if(strPhone.length() == 10){
            	sResidentialPhoneNumber = strPhone;
            }
            else{
            	sResidentialPhoneNumber = strPhone.Substring(1,4)+strPhone.Substring(6,9)+strPhone.Substring(10,14);
            }
        }
    }   
  
    /*
    * Method Name   :    populateDecryptValues
    * Description   :    This method is used to decrypt the values
    * Return Type   :    NA
    * Parameters    :    Process.PluginRequest
    */
    public void  populateDecryptValues(Process.PluginRequest request)
    {
        sTempMemberUMID =  (String) request.inputParameters.get('MemberId');
        sTempMedicareId =  (String) request.inputParameters.get('MedicareId');
        sFirstName = (String) request.inputParameters.get('PersonFirstName');
        sLastName = (String) request.inputParameters.get('PersonLastName');
        PolicyID= (String) request.inputParameters.get('PolicyID');
    }
    
    /*
    * Method Name   :    describe
    * Description   :    Returns the describe information for the interface
    * Return Type   :    PluginDescribeResult
    * Parameters    :    NA
    */
    
    global Process.PluginDescribeResult describe()
    {
           
        Process.PluginDescribeResult result = new Process.PluginDescribeResult();
        
        result.Name = 'AVFTemplateplugin';
        result.Tag =  'AVFTemplate';
        
        try
        {
            result.inputParameters = new List<Process.PluginDescribeResult.InputParameter>
            { 
                new Process.PluginDescribeResult.InputParameter('CaseNumber',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('EditSequenceNumber',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('HumanaAssociateName',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('PersonFirstName',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('PersonLastName',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('EnterprisePersonID',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('PersonSpeakingWith',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('RelationToMember',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('PermanentResidentialAddress',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('PermanentResidentialCityName',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('PermanentResidentialCountyName',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('PermanentResidentialStateCode',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('PermanentResidentialZipCode',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('PermanentResidentialPhoneNumber',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('PermanentResidentialStartDate',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('MailingAddress',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('MailingCityName',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('MailingStateCode',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('MailingZipCode',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('TemporaryAddress',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('TemporaryCityName',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('TemporaryCountyName',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('TemporaryStateCode',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('TemporaryZipCode',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('TemporaryOSAStartDate',Process.PluginDescribeResult.ParameterType.STRING, true)  ,
                new Process.PluginDescribeResult.InputParameter('TemporaryOSAEndDate',Process.PluginDescribeResult.ParameterType.STRING, true) ,                          
                new Process.PluginDescribeResult.InputParameter('PolicyID',Process.PluginDescribeResult.ParameterType.STRING, true) ,                           
                new Process.PluginDescribeResult.InputParameter('MemberId',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('MedicareId',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('PolicyContractNumber',Process.PluginDescribeResult.ParameterType.STRING, true),
                new Process.PluginDescribeResult.InputParameter('PolicyMemberPolicyType',Process.PluginDescribeResult.ParameterType.STRING, true)
            }; 
            
            result.outputParameters = new 
            List<Process.PluginDescribeResult.OutputParameter>
            {
                 new Process.PluginDescribeResult.OutputParameter('Error',Process.PluginDescribeResult.ParameterType.String),
                 new Process.PluginDescribeResult.OutputParameter('ErrorDescription',Process.PluginDescribeResult.ParameterType.String)
            }; 
       }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMAVFTemplateService_Plugin', 'describe');
        }
        return result; 
    }
}