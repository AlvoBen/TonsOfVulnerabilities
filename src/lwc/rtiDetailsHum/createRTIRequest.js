export class RTI_Request {
    constructor(enterpriseId, startDate, endDate, sDirection, sChannel, sCategory, channelList, recordLimit, skipnumber, profile) {
        this.personId = enterpriseId;
        this.interactingWithCode = 'All';
        this.endDate = endDate;
        this.startDate = startDate;
        this.recordLimit = recordLimit;
        this.skip = skipnumber;
        this.SecurityProfile = profile;
        this.channels = new ChannelDTO(sDirection, sChannel, sCategory, channelList);
    }
}

export class ChannelDTO {
    constructor(sDirection, sChannel, sCategory, channelList) {
        let temp = [];
        if (channelList && channelList.length > 0) {
            let tempobj = {};
            channelList.forEach(element => {
                if (element.Attribute_Value__c != 'All') {
                    tempobj.code = element.Attribute_Value__c;
                    tempobj.direction = sDirection;
                    tempobj.isFinal = 'true';
                    if (sChannel == 'WEB,WB' || sChannel == 'PRINT')
                        tempobj.categoryCode = sCategory;
                    else
                        tempobj.categoryCode = element.Category_Code__c;
                    if (((element.Attribute_Label__c == 'PRINT' || element.Label == 'VAT') && sDirection == 'O') || sDirection == 'I')
                        tempobj.isFinal = 'ALL';
                    tempobj.dispositioncd = element.Disposition_Code__c;
                    tempobj.statuscd = element.Status_Code__c;
                }
                temp.push(tempobj);
            });
        }
        this.channels = temp;
    }
}