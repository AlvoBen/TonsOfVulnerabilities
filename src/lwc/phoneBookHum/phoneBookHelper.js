import { phoneBookConstants } from './phoneBookConstantsHum';
/*
   * Params - {phonebookrules list}
   * Description - this method is used to preapre data to show in data-table.
   */
export const getData = (PhoneruleData, selectedPhonebook,bSwitchPhoneBookRules,alPhabetizeSwitch) => {
    var data = [];
    for (let key in PhoneruleData) {
        var checkDuplicate = false;
        for (var i = 0; i < data.length; i++) {
            if (selectedPhonebook != phoneBookConstants.TRANSFER_PHONE_BOOK) {
                if ((PhoneruleData[key].QueueName == data[i].QueueName && PhoneruleData[key].TransferNumber == data[i].Contact)) {
                    checkDuplicate = true;
                    break;
                }
            }
            else {
                if (bSwitchPhoneBookRules!= null && bSwitchPhoneBookRules == true)
                {
                    if ((PhoneruleData[key].CategoryCd == data[i].CallReason && PhoneruleData[key].TransferNumber == data[i].Contact && PhoneruleData[key].callertype == data[i].callertype)) {
                        checkDuplicate = true;
                        break;
                    }
                }
                else{
                    if ((PhoneruleData[key].CategoryCd == data[i].CategoryCd && PhoneruleData[key].TransferNumber == data[i].Contact)) {
                        checkDuplicate = true;
                        break;
                    }
                }
            }
        }
        if (!checkDuplicate) {
            var fetchData = {
                QueueName: PhoneruleData[key].QueueName,
                CallReason: PhoneruleData[key].CategoryCd,
                Contact: PhoneruleData[key].TransferNumber,
                OperatingHours: PhoneruleData[key].OperatingHours,
                Notes: PhoneruleData[key].Notes,
                ShareableNumber: PhoneruleData[key].ShareableNumber ? PhoneruleData[key].ShareableNumber : PhoneruleData[key].ShareableTollFree,
                CallerType:PhoneruleData[key].CallerType
            }
            data.push(fetchData);
        }
    }
    if(alPhabetizeSwitch==true && data!=null){
        if (selectedPhonebook != phoneBookConstants.TRANSFER_PHONE_BOOK)
        {  
         data=sortDefaultPhonebookRules(data, 'QueueName'); 
        }
        else{
         data=sortDefaultPhonebookRules(data,'CallReason'); 
        }
    }
    
    return data;
}
/*
   * Params - {modal containing policy details}
   * Description - this method is used to create request body for routexml webservice.
   */
export const getRoutingXMLBody = (searchModal) => {
    var body = {};
    let ASOIndicator = searchModal.ASO_IND ? searchModal.ASO_IND : '';
    let BusinessSegmentIndicator = searchModal.BUS_SEGMENT_IND ? searchModal.BUS_SEGMENT_IND : '';
    let HdhpIndicator = searchModal.HDHP_IND ? searchModal.BUS_SEGMENT_IND : '';
    let SubscriberDOB = searchModal.INQA_DOB ? searchModal.INQA_DOB : '';
    let GroupID = searchModal.INQA_GRP_ID ? searchModal.INQA_GRP_ID : '';
    let PlatFormCD = searchModal.INQA_PLTFRM_CD ? searchModal.INQA_PLTFRM_CD : '';
    let MajorLOB = searchModal.MAJOR_LOB ? searchModal.MAJOR_LOB : '';
    let SubscriberID = searchModal.MEMBER_ID ? searchModal.MEMBER_ID : '';
    let MtvBusinessLevel5 = searchModal.MTV_BL5 ? searchModal.MTV_BL5 : '';
    let MtvBusinessLevel7 = searchModal.MTV_BL7 ? searchModal.MTV_BL7 : '';
    let ProductTypeCode = searchModal.PROD_TYPE_CODE ? searchModal.PROD_TYPE_CODE : '';
    let SellingLedger = searchModal.SELLING_LEDGER ? searchModal.SELLING_LEDGER : '';
    let StateOfIssue = searchModal.STATE_OF_ISSUE ? searchModal.STATE_OF_ISSUE : '';
    let AsOfDate = searchModal.VENDOR_ASOFDATE ? searchModal.VENDOR_ASOFDATE : '';
    let SubGroupID = searchModal.VENDOR_SUB_GROUPID ? searchModal.VENDOR_SUB_GROUPID : '';
    let SellingLedgerForMktName = searchModal.SELLING_LEDGER ? searchModal.SELLING_LEDGER : '';
    let AkaName = '';
    let ActParent = '';
    let CimId = '';
    let IsSoftPhoneIndicator = phoneBookConstants.IsSoftPhoneIndicator;
    let ICMSiteCode = '';
    let NGroupID = '';
    body = '{"RoutingXML":{"PlatFormCD":"' + PlatFormCD + '","SubscriberID":"' + SubscriberID + '","SubscriberDOB":"' + SubscriberDOB + '","SellingLedger":"' + SellingLedger + '","GroupID":"' + GroupID + '","ASOIndicator":"' + ASOIndicator + '","MajorLOB":"' + MajorLOB + '","SellingLedgerForMktName":"' + SellingLedgerForMktName + '","AkaName":"' + AkaName + '","ActParent":"' + ActParent + '","CimId":"' + CimId + '","StateOfIssue":"' + StateOfIssue + '","BusinessSegmentIndicator":"' + BusinessSegmentIndicator + '","HdhpIndicator":"' + HdhpIndicator + '","MtvBusinessLevel5":"' + MtvBusinessLevel5 + '","IsSoftPhoneIndicator":"' + IsSoftPhoneIndicator + '","MtvBusinessLevel7":"' + MtvBusinessLevel7 + '","ProductTypeCode":"' + ProductTypeCode + '","ApplicationSourceRequest":"Softphone","AdditionalContextInformation":{"KeyValueOfstringstring":[{"Key":"NGroupID","Value":"' + NGroupID + '"},{"Key":"Demo","Value":"' + SubGroupID + '"}]}}}';
    return body;
}
/*
   * Params - {curreent time}
   * Description - this method is used to get the time in en-US locale in 24hour format.
   */
export const getUSLocaleTime = (currentTime) => {
    var DateESTFormatted = currentTime.toLocaleString('en-US', { timeZone: 'America/New_York', hour12: true, month: '2-digit', day: '2-digit', year: 'numeric', hour: "2-digit", minute: "2-digit", second: "2-digit" });
    var now = DateESTFormatted.replaceAll(',', '');;
    return now;
}
/*
   * Params - {List of phonebookrule which needs to be filtered , callertype}
   * Description - this method filters the phonebookrules list based uptio the caller type value selected.
   */
export const getFilteredPhoneBookList = (phBookRulesList, callertype) => {
    let callerTypePHList = [];
    phBookRulesList.forEach(function (element) {
        if (callertype === phoneBookConstants.AGENT) {
            if (element.CallerType === callertype || element.CallerType === phoneBookConstants.BROKER) {
                callerTypePHList.push(element);
            }
        }
        else if (callertype === phoneBookConstants.GROUP) {
            if (element.CallerType === callertype || element.CallerType === phoneBookConstants.EMPLOYER) {
                callerTypePHList.push(element);
            }
        }
        else {
            if (element.CallerType === callertype) {
                callerTypePHList.push(element);
            }
        }
    });
    return callerTypePHList;
}

/*
   * Params - {phonebookrules list}
   * Description - this method is used to sort phonebook callreasons.
   */
function sortDefaultPhonebookRules(data, rules){
    let sortedPhonebookrules=[];
    const mapOfPhonebookRules=new Map();
    data.forEach(elem=>{
         for(let key in elem){
             if(key=== rules){
                mapOfPhonebookRules.set(elem,elem[key]);
             }
             else if(key===rules){
                mapOfPhonebookRules.set(elem,elem[key]);
             }
         }
     });
     let sorted=[...mapOfPhonebookRules.entries()].sort();    
    for(let key in sorted){
        sortedPhonebookrules.push(sorted[key][0])
    }
    return sortedPhonebookrules;
    }