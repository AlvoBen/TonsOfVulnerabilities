import { phoneBookConstants } from './pureCloudEventsConstantsHum';
import Hold_Popup from '@salesforce/label/c.Hold_Popup_HUM';
import getSwitchValue from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';


const sStoryNumber = phoneBookConstants.Switch_List;
var watsonProviderCallSwitch;


getSwitchValue({sStoryNumber}).then(result => {
  if(result){ 
    watsonProviderCallSwitch = result['5602717'];
  }
})
.catch(error => {
  console.log('error in retrieving switch',error);
});


export function getLabels() {
    let label = {
        Hold_Popup
    }
    return label;
}
/*
   * Params - {searchModal, searchtype(memberx,provider,group,agent), item(INQR_TYP,INQA_TYP)}
   * Description - this method is used to return the final object formed containing the details about the search page to 
   *               be opened.
   */
export const getSearchMapping = (searchModal, searchType, item, MemberDOBFormatSwitch) => {
    const SearchObject = {};
    const TypeToSearch = {};
    var birthDateVal; 
    //US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)- Added check for Spanish(S) caller type data
    if (searchType === phoneBookConstants.MEMBER_M || searchType === phoneBookConstants.MEMBER_S) {
        SearchObject.sMemberid = '';
        SearchObject.sFirstName = '';
        SearchObject.sLastName = '';
        SearchObject.sSuffix = '';
        SearchObject.sPhone = '';
        SearchObject.sGroupNumber = '';
        SearchObject.sPID = '';
        SearchObject.sState = '';
        SearchObject.sPostalCode = '';
        SearchObject.sBirthdate = '';
        if (item === phoneBookConstants.INQR_TYP) {
            //code to populate the required form fields if inquiring about type is also 'M'
            //US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)- Added check for Spanish(S) caller type
            if (searchModal[phoneBookConstants.INQA_TYP] === phoneBookConstants.MEMBER_M || searchModal[phoneBookConstants.INQA_TYP] === phoneBookConstants.MEMBER_S) {
                if ((searchModal.hasOwnProperty(phoneBookConstants.INQR_ID) && searchModal[phoneBookConstants.INQR_ID] != '' && searchModal[phoneBookConstants.INQR_ID].length <= 9) || (searchModal.hasOwnProperty(phoneBookConstants.INQA_ID) && searchModal[phoneBookConstants.INQA_ID] != '' && searchModal[phoneBookConstants.INQA_ID].length <= 9)) {
                    SearchObject.sMemberid = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : (searchModal[phoneBookConstants.INQA_ID]) ? searchModal[phoneBookConstants.INQA_ID] : '';
                }
                else {
                    SearchObject.sFirstName = searchModal[phoneBookConstants.INQR_FIRST] ? searchModal[phoneBookConstants.INQR_FIRST] : (searchModal[phoneBookConstants.INQA_FIRST] ? searchModal[phoneBookConstants.INQA_FIRST] : '');
                    SearchObject.sLastName = searchModal[phoneBookConstants.INQR_LAST] ? searchModal[phoneBookConstants.INQR_LAST] : (searchModal[phoneBookConstants.INQA_LAST] ? searchModal[phoneBookConstants.INQA_LAST] : '');
                    if(MemberDOBFormatSwitch){
                        birthDateVal = setDateFormatForBirthDate(searchModal[phoneBookConstants.INQA_DOB]);
                        SearchObject.sBirthdate = searchModal[phoneBookConstants.INQA_DOB] ? birthDateVal : '';
                    } else {
                        SearchObject.sBirthdate = (searchModal[phoneBookConstants.INQR_DOB] ? searchModal[phoneBookConstants.INQR_DOB] : (searchModal[phoneBookConstants.INQA_DOB] ? searchModal[phoneBookConstants.INQA_DOB] : '')).replaceAll('-', '/');
                    }
                }
            }
            //code to populate the required form fields if INQA_TYPE is different and INQR_TYPE is member
            else {
                if (searchModal[phoneBookConstants.INQR_ID] != '' && searchModal[phoneBookConstants.INQR_ID].length <= 9) {

                    SearchObject.sMemberid = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : '';
                }
                else {

                    SearchObject.sFirstName = searchModal[phoneBookConstants.INQR_FIRST] ? searchModal[phoneBookConstants.INQR_FIRST] : '';
                    SearchObject.sLastName = searchModal[phoneBookConstants.INQR_LAST] ? searchModal[phoneBookConstants.INQR_LAST] : '';
                    if(MemberDOBFormatSwitch){
                        birthDateVal = setDateFormatForBirthDate(searchModal[phoneBookConstants.INQA_DOB]);
                        SearchObject.sBirthdate = searchModal[phoneBookConstants.INQA_DOB] ? birthDateVal : '';
                    } else {
                        SearchObject.sBirthdate = (searchModal[phoneBookConstants.INQR_DOB] ? searchModal[phoneBookConstants.INQR_DOB] : (searchModal[phoneBookConstants.INQA_DOB] ? searchModal[phoneBookConstants.INQA_DOB] : '')).replaceAll('-', '/');
                    }
                }
            }
        }
        //code to populate the required form fields if INQA_TYPE != INQR_TYPE and INQA_TYPE is member
        else {
            if (searchModal[phoneBookConstants.INQA_ID] != '' && searchModal[phoneBookConstants.INQA_ID].length <= 9) {
                SearchObject.sMemberid = searchModal[phoneBookConstants.INQA_ID] ? searchModal[phoneBookConstants.INQA_ID] : '';
            }
            else {
                SearchObject.sFirstName = searchModal[phoneBookConstants.INQA_FIRST] ? searchModal[phoneBookConstants.INQA_FIRST] : '';
                SearchObject.sLastName = searchModal[phoneBookConstants.INQA_LAST] ? searchModal[phoneBookConstants.INQA_LAST] : '';
                if(MemberDOBFormatSwitch){
                    birthDateVal = setDateFormatForBirthDate(searchModal[phoneBookConstants.INQA_DOB]);
                    SearchObject.sBirthdate = searchModal[phoneBookConstants.INQA_DOB] ? birthDateVal : '';
                } else {
                    SearchObject.sBirthdate = (searchModal[phoneBookConstants.INQR_DOB] ? searchModal[phoneBookConstants.INQR_DOB] : (searchModal[phoneBookConstants.INQA_DOB] ? searchModal[phoneBookConstants.INQA_DOB] : '')).replaceAll('-', '/');
                }
            }
        }
        SearchObject.tabName = phoneBookConstants.MEMBER;
        SearchObject.searchComponent = 'c-member-search-form-hum';
        TypeToSearch.member = SearchObject;
    }
    //US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)- Added check for PPI caller type
    else if (searchType === phoneBookConstants.PROVIDER_P || searchType === phoneBookConstants.PROVIDER_PPI) {
        SearchObject.sTaxID = '';
        SearchObject.sFirstName = '';
        SearchObject.sLastName = '';
        SearchObject.sNPI = '';
        SearchObject.sFacilityName = '';
        SearchObject.sState = '';
        SearchObject.sPostalCode = '';
        SearchObject.sSpeciality = '';
        if (item === phoneBookConstants.INQR_TYP) {
            //US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)- Added check for PPI caller type
            if (searchModal[phoneBookConstants.INQA_TYP] === phoneBookConstants.PROVIDER_P || searchModal[phoneBookConstants.INQA_TYP] === phoneBookConstants.PROVIDER_PPI) {
                if ((searchModal.hasOwnProperty(phoneBookConstants.INQR_ID) && searchModal[phoneBookConstants.INQR_ID] != '') || (searchModal.hasOwnProperty(phoneBookConstants.INQA_ID) && searchModal[phoneBookConstants.INQA_ID] != '')) {
                    SearchObject.sTaxID = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : searchModal[phoneBookConstants.INQA_ID] ? searchModal[phoneBookConstants.INQA_ID] : '';
                    //US-5602717 Added below changes to pass npi id to search page URL if npi id is available in Provider call data. 
                    if(watsonProviderCallSwitch)
                    {
                        SearchObject.sNPI = searchModal[phoneBookConstants.INQR_NPI] ? searchModal[phoneBookConstants.INQR_NPI] :  '';
                    }
                    
                }
                SearchObject.sFirstName = searchModal[phoneBookConstants.INQR_FIRST] ? searchModal[phoneBookConstants.INQR_FIRST] : (searchModal[phoneBookConstants.INQA_FIRST] ? searchModal[phoneBookConstants.INQA_FIRST] : '');
                if(watsonProviderCallSwitch && (searchModal.hasOwnProperty(phoneBookConstants.INQR_ID) && searchModal[phoneBookConstants.INQR_ID] == ''))
                {
                    SearchObject.sLastName = searchModal[phoneBookConstants.INQR_LAST] ? searchModal[phoneBookConstants.INQR_LAST] : (searchModal[phoneBookConstants.INQA_LAST] ? searchModal[phoneBookConstants.INQA_LAST] : '');
                }
                else if(!watsonProviderCallSwitch){
                    SearchObject.sLastName = searchModal[phoneBookConstants.INQR_LAST] ? searchModal[phoneBookConstants.INQR_LAST] : (searchModal[phoneBookConstants.INQA_LAST] ? searchModal[phoneBookConstants.INQA_LAST] : '');
                }
            }
            else {
                SearchObject.sTaxID = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : '';
                //US-5602717 Added below changes to pass npi id to search page URL if npi id is available in Provider call data. 
                if(watsonProviderCallSwitch)
                {
                    SearchObject.sNPI = searchModal[phoneBookConstants.INQR_NPI] ? searchModal[phoneBookConstants.INQR_NPI] :  '';
                }
                SearchObject.sFirstName = searchModal[phoneBookConstants.INQR_FIRST] ? searchModal[phoneBookConstants.INQR_FIRST] : '';
                if(watsonProviderCallSwitch && (searchModal.hasOwnProperty(phoneBookConstants.INQR_ID) && searchModal[phoneBookConstants.INQR_ID] == ''))
                {
                    SearchObject.sLastName = searchModal[phoneBookConstants.INQR_LAST] ? searchModal[phoneBookConstants.INQR_LAST] : (searchModal[phoneBookConstants.INQA_LAST] ? searchModal[phoneBookConstants.INQA_LAST] : '');
                }
                else if(!watsonProviderCallSwitch){
                    SearchObject.sLastName = searchModal[phoneBookConstants.INQR_LAST] ? searchModal[phoneBookConstants.INQR_LAST] : '';
                }
            }
        }
        else {
            SearchObject.sTaxID = searchModal[phoneBookConstants.INQA_ID] ? searchModal[phoneBookConstants.INQA_ID] : '';
            //US-5602717 Added below changes to pass npi id to search page URL if npi id is available in Provider call data. 
            if(watsonProviderCallSwitch )
            {
                SearchObject.sNPI = searchModal[phoneBookConstants.INQR_NPI] ? searchModal[phoneBookConstants.INQR_NPI] :  '';
            }
            SearchObject.sFirstName = searchModal[phoneBookConstants.INQA_FIRST] ? searchModal[phoneBookConstants.INQA_FIRST] : '';
            if(watsonProviderCallSwitch && (searchModal.hasOwnProperty(phoneBookConstants.INQR_ID) && searchModal[phoneBookConstants.INQR_ID] == ''))
            {
                SearchObject.sLastName = searchModal[phoneBookConstants.INQR_LAST] ? searchModal[phoneBookConstants.INQR_LAST] : (searchModal[phoneBookConstants.INQA_LAST] ? searchModal[phoneBookConstants.INQA_LAST] : '');
            }
            else if(!watsonProviderCallSwitch){
                SearchObject.sLastName = searchModal[phoneBookConstants.INQA_LAST] ? searchModal[phoneBookConstants.INQA_LAST] : '';
            }
        }
        SearchObject.tabName = phoneBookConstants.PROVIDER;
        SearchObject.searchComponent = 'c-provider-search-form-hum';
        TypeToSearch.provider = SearchObject;
    }
    else if (searchType === phoneBookConstants.GROUP_G || searchType === phoneBookConstants.EMPLOYER_E) {
        SearchObject.sGroupName = '';
        SearchObject.sState = '';
        SearchObject.sGroupNumber = '';
        if (item === phoneBookConstants.INQR_TYP) {
            if (searchModal[phoneBookConstants.INQA_TYP] === phoneBookConstants.GROUP_G || searchModal[phoneBookConstants.INQA_TYP] === phoneBookConstants.EMPLOYER_E) {
                if ((searchModal.hasOwnProperty(phoneBookConstants.INQR_ID) && searchModal[phoneBookConstants.INQR_ID] != '') || (searchModal.hasOwnProperty(phoneBookConstants.INQA_ID) && searchModal[phoneBookConstants.INQA_ID] != '') || (searchModal.hasOwnProperty(phoneBookConstants.INQA_GRP_ID) && searchModal[phoneBookConstants.INQA_GRP_ID] != '')) {
                    SearchObject.sGroupNumber = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : searchModal[phoneBookConstants.INQA_ID] ? searchModal[phoneBookConstants.INQA_ID] : searchModal[phoneBookConstants.INQA_GRP_ID] ? searchModal[phoneBookConstants.INQA_GRP_ID] : '';
                }
            }
            else {
                SearchObject.sGroupNumber = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : searchModal[phoneBookConstants.INQA_GRP_ID] ? searchModal[phoneBookConstants.INQA_GRP_ID] : '';
            }
        }
        else {
            SearchObject.sGroupNumber = searchModal[phoneBookConstants.INQA_ID] ? searchModal[phoneBookConstants.INQA_ID] : searchModal[phoneBookConstants.INQA_GRP_ID] ? searchModal[phoneBookConstants.INQA_GRP_ID] : '';
        }
        SearchObject.tabName = phoneBookConstants.GROUP;
        SearchObject.searchComponent = 'c-group-search-form-hum';
        TypeToSearch.group = SearchObject;
    }
    else if (searchType === phoneBookConstants.AGENT_A || searchType === phoneBookConstants.BROKER_B) {
        SearchObject.sAgentType = phoneBookConstants.AGENCY;
        SearchObject.sAgentId = '';
        SearchObject.sState = '';
        SearchObject.sFirstName = '';
        SearchObject.sLastName = '';
        SearchObject.sTaxID = '';
        var tempId = '';
        if (item === phoneBookConstants.INQR_TYP) {
            if (searchModal['INQA_TYP'] === phoneBookConstants.AGENT_A || searchModal['INQA_TYP'] === phoneBookConstants.BROKER_B) {
                if ((searchModal.hasOwnProperty(phoneBookConstants.INQR_ID) && searchModal[phoneBookConstants.INQR_ID] != '') || (searchModal.hasOwnProperty(phoneBookConstants.INQA_ID) && searchModal[phoneBookConstants.INQA_ID] != '')) {
                    tempId = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : searchModal[phoneBookConstants.INQA_ID] ? searchModal[phoneBookConstants.INQA_ID] : '';
                }
            }
            else {
                tempId = searchModal[phoneBookConstants.INQR_ID] ? searchModal[phoneBookConstants.INQR_ID] : '';
            }
        }
        else {
            tempId = searchModal[phoneBookConstants.INQA_ID] ? searchModal[phoneBookConstants.INQA_ID] : '';
        }
        if (tempId && tempId.length == 9) {
            SearchObject.sTaxID = tempId;
        }
        else {
            SearchObject.sAgentId = tempId;
        }
        SearchObject.tabName = phoneBookConstants.AGENT;
        SearchObject.searchComponent = 'c-agent-search-form-hum';
        TypeToSearch.agent = SearchObject;
    }
    else {
        SearchObject.sFirstName = '';
        SearchObject.sLastName = '';
        SearchObject.sMemberid = '';
        SearchObject.sSuffix = '';
        SearchObject.sBirthdate = '';
        SearchObject.sPhone = '';
        SearchObject.sGroupNumber = '';
        SearchObject.sPID = '';
        SearchObject.sState = '';
        SearchObject.sPostalCode = '';
        SearchObject.tabName = phoneBookConstants.MEMBER;
        SearchObject.searchComponent = 'c-member-search-form-hum';
        TypeToSearch.member = SearchObject;
    }
    return TypeToSearch;
}

/*
   * Params - {defaultDateVal}
   * Description - this method is used to return the correct dob format for CRM search. 
   */
function setDateFormatForBirthDate(defaultDateVal){
    var birthDateVal ='';
    if(defaultDateVal != '' && defaultDateVal != null){
        const birthDate = new Date(defaultDateVal + phoneBookConstants.UniversalTimezone);
        let day = ("0" + birthDate.getDate()).slice(-2);
        let month = ("0" + (birthDate.getMonth() + 1)).slice(-2);
        let year = birthDate.getFullYear();
        if(birthDate.toString() !== phoneBookConstants.InvalidDateCheck){
            birthDateVal = month + '/' + day + '/' + year;
        }
        else{
            birthDateVal = defaultDateVal;
        }
    }
   return birthDateVal;
}