/*****************************************************************************************
Apex class Name    : MemberIconsConfiguration_C_HUM
Version            : 1.0
Function           : Controller to VF page: MemberIconsConfiguration_VF_HUM
                     This class is used to view/edit the list of member icons
Created Date       : 11/01/2019
Test Class         : MemberIconsConfiguration_CT_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                      11/01/2019             Original Version
 *   Amit Agarwal                                        06/25/2020             User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page And Repeat Caller Information on the Member Account page (RF)
 *  Giri Yeddula                                     06/07/2021         User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
 ****************************************************************************************************************************/
public without sharing class MemberIconsConfiguration_C_HUM 
{ 
    public Map<String,MemberIcon_DTO_HUM> mapIconsDto{get;set;}
    public MemberIcon_DTO_HUM objSelectedIconDto{get;set;}

    public Boolean bIsDetailsSectionVisible{get;set;}
    public MultiSelectPicklistWithAddRemove_H_HUM objPageWrapperHelper{get;set;}
    public MultiSelectPicklistWithAddRemove_H_HUM objPermissionSetWrapperHelper{get;set;}
    public MultiSelectPicklistWithAddRemove_H_HUM objProfileWrapperHelper{get;set;} 
    public MultiSelectPicklistWithAddRemove_H_HUM objIOWrapperHelper{get;set;} 
    public MultiSelectPicklistWithAddRemove_H_HUM objIWTWrapperHelper{get;set;} 
    private boolean bESAPILibSecurityfixSwitch {get;set;}
    public Map<String,List<String>> mapIconImages{get;set;}
    public Boolean bShowError{get;set;}
    public String sSelectedConfigurationType{get;set;}
    public List<SelectOption> lstConfigurationType{get;set;}
    
    public Map<String,RepeatCallerConfiguration_DTO_HUM> mapRepeatCallerDTO{get;set;}
    public RepeatCallerConfiguration_DTO_HUM objSelectedRCDTO{get;set;}

    public Boolean bIsRCDetailsSectionVisible{get;set;}
    public Boolean bIsRCTableSectionVisible{get;set;}
    public String sSelectedInteractingWithType{get;set;}
    public String sSelectedInteractionOrigin{get;set;}
   
    /*
    * Constuctor
    * @param  N/A
    * @return N/A
    */
    public MemberIconsConfiguration_C_HUM() 
    {
        bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
        bIsDetailsSectionVisible = false;
        bIsRCDetailsSectionVisible = false;
        bIsRCTableSectionVisible = true;
        mapIconsDto = MemberIconsConfiguration_S_HUM.getAllIconsData();
        mapIconImages = MemberIconsConfiguration_S_HUM.getDocumentDetails(mapIconsDto);
        getConfigurationTypes();
        mapRepeatCallerDTO = MemberIconsConfiguration_S_HUM.getRepeatCallerSettings();
    }
    /*
    * This method is used to fetch the Configuration types
    * @param  N/A
    * @return Void
    */
    private void getConfigurationTypes()
    {
        lstConfigurationType = new List<SelectOption>();
        lstConfigurationType.add(new SelectOption(HUMConstants.HUMNone, HUMConstants.HUMNoneDisplay));
        lstConfigurationType.Add(new SelectOption('Member Icons', 'Member Icons'));
        lstConfigurationType.Add(new SelectOption('Repeat Caller', 'Repeat Caller'));
    }
    /*
    * This method is used to display selected icon details on UI
    * @param  N/A
    * @return Void
    */
    public void displayIconDetails()
    {
        String sSelectedIcon = ApexPages.CurrentPage().getParameters().get('selectedIcon');
        sSelectedIcon = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sSelectedIcon) : sSelectedIcon;
        mapIconsDto = MemberIconsConfiguration_S_HUM.getAllIconsData();
        objSelectedIconDto = mapIconsDto.get(sSelectedIcon);
        objPageWrapperHelper = new MultiSelectPicklistWithAddRemove_H_HUM();
        objPermissionSetWrapperHelper = new MultiSelectPicklistWithAddRemove_H_HUM();
        objProfileWrapperHelper = new MultiSelectPicklistWithAddRemove_H_HUM();
        objPageWrapperHelper.sAvailableOptions = JSON.serialize(GLOBAL_CONSTANT_HUM.lstAvailablePages) ;
        objPermissionSetWrapperHelper.sAvailableOptions = JSON.serialize(GLOBAL_CONSTANT_HUM.lstAvailablePermissionSets) ;
        objProfileWrapperHelper.sAvailableOptions = JSON.serialize(GLOBAL_CONSTANT_HUM.lstAvailableProfiles) ;
        If(String.IsNotBlank(objSelectedIconDto.sPageName))
        {
            objPageWrapperHelper.sSelectedOptions =  JSON.serialize(objSelectedIconDto.sPageName.split(','));
        }
        if(String.IsNotBlank(objSelectedIconDto.sPermissionSet))
        {
            objPermissionSetWrapperHelper.sSelectedOptions = JSON.serialize(objSelectedIconDto.sPermissionSet.split(','));
        }
        If(String.IsNotBlank(objSelectedIconDto.sProfile))
        {
            objProfileWrapperHelper.sSelectedOptions = JSON.serialize(objSelectedIconDto.sProfile.split(','));
        }
        bIsDetailsSectionVisible = true;
    }
     /*
    * This method is used to display Repeat Caller icon details on UI
    * @param  N/A
    * @return Void
    */
     public void displayRepeatCallerDetails()
    {
        String sSelectedRC = ApexPages.CurrentPage().getParameters().get('selectedRepeatCaller');  
        sSelectedRC = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sSelectedRC) : sSelectedRC;
        objSelectedRCDTO = mapRepeatCallerDTO.get(sSelectedRC);
        objIOWrapperHelper = new MultiSelectPicklistWithAddRemove_H_HUM();
        objIOWrapperHelper.sAvailableOptions = JSON.serialize(MemberIconsConfiguration_S_HUM.getInteractionOriginValues()) ;
        If(String.IsNotBlank(objSelectedRCDTO.sInteractionOrigin))
        {
            objIOWrapperHelper.sSelectedOptions =  JSON.serialize(objSelectedRCDTO.sInteractionOrigin.split(','));
        }
        objIWTWrapperHelper = new MultiSelectPicklistWithAddRemove_H_HUM();
        objIWTWrapperHelper.sAvailableOptions = JSON.serialize(MemberIconsConfiguration_S_HUM.getInteractingWithTypeValues()) ;
        If(String.IsNotBlank(objSelectedRCDTO.sInteractingWithType))
        {
            objIWTWrapperHelper.sSelectedOptions =  JSON.serialize(objSelectedRCDTO.sInteractingWithType.split(','));
        }
        bIsRCDetailsSectionVisible = true;
        bIsRCTableSectionVisible = false;
    }
    
    /*
    * This method is used to save updated icon details
    * @param  N/A
    * @return Void
    */
    public void saveIconData()
    {
        String sPages = ApexPages.currentPage().getParameters().get('selectedPages');
        sPages = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sPages) : sPages;
        String sPermissions = ApexPages.currentPage().getParameters().get('selectedPermissions');
        sPermissions = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sPermissions) : sPermissions;
        String sProfiles = ApexPages.currentPage().getParameters().get('selectedProfiles');
        sProfiles = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sProfiles) : sProfiles;
        bShowError = false;

        MemberIconsConfiguration_S_HUM.Save(objSelectedIconDto,sPages,sPermissions ,sProfiles);
        mapIconsDto = MemberIconsConfiguration_S_HUM.getAllIconsData();
        bIsDetailsSectionVisible = false;
    }
     /*
    * This method is used to save Repeat Caller icon details
    * @param  N/A
    * @return Void
    */
    public void saveRC()
    {
        MemberIconsConfiguration_S_HUM.sveRCSettings(objSelectedRCDTO ,sSelectedInteractionOrigin,sSelectedInteractingWithType);
        mapRepeatCallerDTO = MemberIconsConfiguration_S_HUM.getRepeatCallerSettings();
        bIsRCDetailsSectionVisible = false;
        bIsRCTableSectionVisible = true;
    }

    /*
    * This method is used to cancel modification process
    * @param  N/A
    * @return Void
    */
    public void cancel()
    {
        bIsDetailsSectionVisible = false;
    }
    
    /*
    * This method is used to cancel modification process
    * @param  N/A
    * @return Void
    */
    public void cancelRC()
    {
        bIsRCDetailsSectionVisible = false;
        bIsRCTableSectionVisible = true;
    }
    
    /*
    * This is a wrapper class user to display multiselect picklist values
    * @param  N/A
    * @return N/A
    */
    public Class MultiSelectPicklistWithAddRemove_H_HUM 
    {
        public String sAvailableOptions{get;set;}
        public String sSelectedOptions{get;set;}
    }

    
}