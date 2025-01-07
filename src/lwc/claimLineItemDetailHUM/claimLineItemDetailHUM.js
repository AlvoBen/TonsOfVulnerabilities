/*******************************************************************************************************************************
LWC JS Name : claimLineItemDetailHUM.js
Function    : This JS serves as controller to claimLineIteamDetailHum.html
Modification Log: 
  Developer Name           Code Review                      Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Apurva Urkude												04/18/2023				  US-4399890: Claims- Filter Functionality on Claims Detail Page 
* Apurva Urkude                                              06/15/2023				  US-4724857:Switch Clean-up for US 4399890: Claims- Filter Functionality on Claims Detail Pag
* Apurva Urkude                                              07/14/2023				  Defect Fix-7873
*********************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import { getFinalFilterList } from 'c/crmUtilityHum';
import { getClaimDetails } from './layoutConfigClaims';
import json2 from '@salesforce/resourceUrl/json2';
import {
    registerListener,
    unregisterAllListeners
} from 'c/pubsubLinkFramework';
import { CurrentPageReference } from 'lightning/navigation';
import { publish, MessageContext } from 'lightning/messageService';

export default class ClaimDetailsResearchLWCHUM extends LightningElement {
    @track oData = [];
    @track claimDetailResponse;
    @track claimResponse = [];
    @track claimLineItemModel;
    @api showViewAll = false;
    @track bResponse = true;
    @api bInfiniteScroll;
    @api title;
    @track oViewAllParams = {};
    @track claimList = [];
    @track keyword;
    @track resultsTrue = false;
    @track filterObj = {};
	@track sClaimnbr = [];

    @track activityOptions=[];
    @track newarr=[];
    @track newLastProcessedDate=[];
    @track selectValue;
    @track showClaimFilter;
    @api noDataMsg = '';
    @track CurrentlyShowingRecords;
    keyIndex = 0;
	claimPlatformCode;
	bIsLoading = true;
     mapData = new Map();
    @track lastProcessedoptions=[];
    @track diagIndicator;
    @track itemList = [
        {
            id: 0
        }
    ];

    @wire(MessageContext)
    messageContext;
    @wire(CurrentPageReference) pageRef;

    @track filterJsonArray = [];
	@track loggingScreenName = 'Claim Details';
    filterType;

    operatorOptions = [
        { label: 'Equals', value: 'Equals' },
        { label: 'Not Equals', value: 'Not Equals' },
        { label: 'Greater than', value: 'Greater than' },
        { label: 'Less than', value: 'Less than' }
    ];

    fieldOptions = [
        { label: 'Procedure Code', value: 'Procedure Code' },
        { label: 'Cause Code', value: 'Cause Code' }
    ];

    filterTypeOptions = [
        { label: 'All FiLters are true', value: 'AND' },
        { label: 'Any Filter is true', value: 'OR' }
    ];

    addRow() {
        ++this.keyIndex;
        var newItem = [{ id: this.keyIndex }];
        this.itemList = this.itemList.concat(newItem);
    }

    removeRow(event) {
        if (this.itemList.length >= 2) {
            this.itemList = this.itemList.filter(function (element) {
                return (
                    parseInt(element.id) !== parseInt(event.target.accessKey)
                );
            });
        }
    }

    get options()
    {
        return this.activityOptions;
    }


    callbackmethodaname(PublisherMessage) {
		this.bIsLoading = false;
        if (PublisherMessage.ClaimDetailLines != undefined && PublisherMessage.ClaimDetailLines != null && PublisherMessage.ClaimDetailLines.length > 0) 
        {
			this.claimPlatformCode = PublisherMessage.platform;
			this.sClaimnbr = PublisherMessage.sClaimNbr;
            this.diagIndicator = PublisherMessage.sDiagIndicator;
			this.bResponse = true;			
            try{               
            PublisherMessage.ClaimDetailLines.forEach(a=>{
                    a.sClaimNbr?a.sClaimNbr : a.sClaimNumber=PublisherMessage.sClaimNbr; 
					a['ClaimNumber']=a.sClaimNumber;
					a['Claim']=a.sClaimNumber;
					a['Claim ']=a.sClaimNumber;
					a['claimtype']=PublisherMessage.sclaimtype;
             });             
            }
            catch(e)
            {
                console.log('exception' + e);
            }
			var remittarr=[];
 	    for(var key in PublisherMessage.ClaimDetailLines)
            {
                 if(PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence == null ||
		PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence == undefined)
                {
                    this.mapData.set(PublisherMessage.ClaimDetailLines[key].sLastProcessDate,PublisherMessage.ClaimDetailLines[key]);
                }
                if(PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence!=null)
                {
               this.mapData.set(PublisherMessage.ClaimDetailLines[key].sLastProcessDate+'('+PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence+')',PublisherMessage.ClaimDetailLines[key]);
                }
            }
            let lastProcessedoptions1=[];
            for (let [key, value] of this.mapData.entries()) 
            {
	        if(key.length<14)
                {
                lastProcessedoptions1.push({ label: key, value: key});
                }
                if(key.length>=14){
                lastProcessedoptions1.push({ label: key.split(' (')[0], value: key});           
                }
                lastProcessedoptions1.sort(function(a,b){
                     return new Date(a.label.substring(0,10)) - new Date(b.label.substring(0,10))
                })
		 lastProcessedoptions1= lastProcessedoptions1.reverse();
                
            }
            this.value = lastProcessedoptions1[0].value;
            this.lastProcessedoptions = lastProcessedoptions1;
            let temparr = [];
        for(var key in PublisherMessage.ClaimDetailLines)
            {
            if(PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence == null||
	    PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence == undefined)
            {
                let data =this.value;
		               if(PublisherMessage.ClaimDetailLines[key].sLastProcessDate === this.value)
		                {
		                    temparr.push(PublisherMessage.ClaimDetailLines[key]);
		                }
            }
            if(PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence!=null)
            {
                 let data1= this.value.substring(0,10);
                 let data2= this.value.substring(11,14);               
                    if(PublisherMessage.ClaimDetailLines[key].sLastProcessDate == data1 && PublisherMessage.ClaimDetailLines[key].sSrcClaimLineSequence == data2)
                     {
                         temparr.push(PublisherMessage.ClaimDetailLines[key]);
                     }

            }
        }

			PublisherMessage.ClaimDetailLines.forEach(a => {
				if (!remittarr.includes(a.sRemitNumber)){
				remittarr.push(a.sRemitNumber);	
				}
			});
           
			if (remittarr.length > 0) {
                remittarr = remittarr.join('#');
            }
            const selectedEvent = new CustomEvent('remitvalue', { detail: {"remitId":remittarr} });
            this.dispatchEvent(selectedEvent);
			this.oData = temparr;
	    this.activityOptions = this.oData;
            this.claimDetailResponse = PublisherMessage;
			this.claimResponse = PublisherMessage.ClaimDetailLines;
			this.CurrentlyShowingRecords = this.oData.length;
		}
        else {
            this.noDataMsg = 'No Records to Display';
            this.bResponse = false;
        }
        
 


    }
     onChangeLastProcessValue(event)
    {
        this.value = event.detail.value;
            let da= this.value.substring(0,10);
            let da2= this.value.substring(11,14);
            this.oData = [];
            this.claimResponse.forEach(a => {
                if(a.sSrcClaimLineSequence!=null)
                {
                if(a.sLastProcessDate == da && a.sSrcClaimLineSequence == da2)
                {
                    this.oData.push(a);
                }
                }
                else{
                    if(a.sLastProcessDate == this.value)
                {
                    this.oData.push(a);
                }

                }
                
            }); 
        let uniqueChars = this.oData;
	this.activityOptions =this.oData;
        this.CurrentlyShowingRecords = uniqueChars.length;
        this.serveresult = uniqueChars;
        if (this.serveresult.length <= 0) {
            this.template.querySelector(
                'c-claims-table-component-hum'
            ).noDataMessage = 'No Result found';
        }
        this.oData = this.serveresult;
        this.setTotalRecords(this.oData);

       
        //this.CurrentlyShowingRecords = this.oData.length;
    }

    connectedCallback() {
        registerListener('LineItemDataEvent', this.callbackmethodaname, this);
        this.callbackmethodaname(this);
        this.claimLineItemModel = getClaimDetails();
        
    }

    

    getaccordiandata(event) {
        let mpIdvar = '';
        let tempArr = [];
        this.claimResponse.forEach((ele) => {
            if (ele.Id == event.detail.Id) {
                const processedResult = ele;
                this.template
                    .querySelector('c-claims-table-component-hum')
                    .accordiancomputecallback(processedResult);
            }
        });

    }

    findByWord(event) {
        let count = 1;
        const me = this;
        if (event) {
            var element = event.target;
            var value = element.value;
            this.keyword = value;
            if (value.length > 0) {
                this.filterObj['searchByWord'] = [value];
                this.filterObj = {
                    ...this.filterObj,
                    searchByWord: [value]
                };
            } else {
                if (this.filterObj.hasOwnProperty('searchByWord')) {
                    delete this.filterObj['searchByWord'];
                    count = 0;
                }
            }
        } else {
            //if search keyword is pre-entered
            let tempObj = JSON.parse(JSON.stringify(this.filterObj));
            if (tempObj['searchByWord']) {
                me.template.querySelector('.keyword-field').value = tempObj[
                    'searchByWord'
                ].toString(); //sets value of pre-entered keyword on DOM
            }
        }
        if (Object.keys(this.filterObj).length > 0) {
            this.getFilterList(this.activityOptions, this.filterObj);
        } else {
            if (count == 0) {
                this.CurrentlyShowingRecords =this.activityOptions.length;
                this.template
                    .querySelector('c-claims-table-component-hum')
                    .computecallback(this.activityOptions);
            }
        }
    }

    getFilterList(data, filterProperties) {
        const me = this;
        let filterListData = {};
        filterListData = getFinalFilterList(data, filterProperties, this.tmp);
        this.tmp = filterListData.response;
        let uniqueChars = filterListData.uniqueList;
        this.CurrentlyShowingRecords = uniqueChars.length;
        this.serveresult = uniqueChars;
        if (this.serveresult.length <= 0) {
            me.template.querySelector(
                'c-claims-table-component-hum'
            ).noDataMessage = 'No Result found';
        }
        me.oData = me.serveresult;
        me.setTotalRecords(me.oData);
    }

    setTotalRecords(oData) {
        this.bResponse = true;
    }

    handleFieldChange(event) {
        if (this.filterJsonArray.length && this.filterJsonArray.length > 0) {
            let matchfound = false;
            for (let i = 0; i < this.filterJsonArray.length; i++) {
                if (i == event.target.dataset.id) {
                    this.filterJsonArray[i].label = event.detail.value;
                    matchfound = true;
                }
            }
            if (!matchfound) {
                this.filterJsonArray.push({
                    id: event.target.dataset.id,
                    label: event.detail.value
                });
            }
        } else {
            this.filterJsonArray.push({
                id: event.target.dataset.id,
                label: event.detail.value
            });
           
        }
        
    }

    handleOperatorChange(event) {
       
        if (this.filterJsonArray.length && this.filterJsonArray.length > 0) {
            let matchfound = false;
            for (let i = 0; i < this.filterJsonArray.length; i++) {
                if (i == event.target.dataset.id) {
                    this.filterJsonArray[i].operator = event.detail.value;
                    matchfound = true;
                }
            }
            if (!matchfound) {
                this.filterJsonArray.push({
                    id: event.target.dataset.id,
                    operator: event.detail.value
                });
            }
        } else {
            this.filterJsonArray.push({
                id: event.target.dataset.id,
                operator: event.detail.value
            });
        }
    }

    handleValueChange(event) {
        if (this.filterJsonArray.length && this.filterJsonArray.length > 0) {
            let matchfound = false;
            for (let i = 0; i < this.filterJsonArray.length; i++) {
                if (i == event.target.dataset.id) {
                    this.filterJsonArray[i].value = event.detail.value;
                    matchfound = true;
                }
            }
            if (!matchfound) {
                this.filterJsonArray.push({
                    id: event.target.dataset.id,
                    value: event.detail.value
                });
            }
        } else {
            this.filterJsonArray.push({
                id: event.target.dataset.id,
                value: event.detail.value
            });
        }
    }

    handleFilterTypeChange(event) {
        this.filterType = event.target.value;
    }

    handleFilterButton(event) {
        
        let uniqueChars = [];

        for (let j = 0; j < this.filterJsonArray.length; j++) {
            for (let i = 0; i < this.oData.length; i++) {
                if (this.filterJsonArray[j].label == 'Procedure Code') {
                    if (this.filterJsonArray[j].operator == 'Equals') {
                        if (this.oData[i].sServiceCode) {
                            if (
                                this.oData[i].sServiceCode &&
                                this.oData[i].sServiceCode ==
                                    this.filterJsonArray[j].value
                            ) {
                                if (this.filterJsonArray[j].match) {
                                    this.filterJsonArray[j].match.push(
                                        this.oData[i]
                                    );
                                } else {
                                    this.filterJsonArray[j].match = [
                                        this.oData[i]
                                    ];
                                }
                            }
                        }
                    } else if (
                        this.filterJsonArray[j].operator == 'Not Equals'
                    ) {
                        if (
                            this.oData[i].sServiceCode &&
                            this.oData[i].sServiceCode !=
                                this.filterJsonArray[j].value
                        ) {
                            if (this.filterJsonArray[j].match) {
                                this.filterJsonArray[j].match.push(
                                    this.oData[i]
                                );
                            } else {
                                this.filterJsonArray[j].match = [this.oData[i]];
                            }
                        }
                    }
                } else if (this.filterJsonArray[j].label == 'Cause Code') {
                    if (this.filterJsonArray[j].operator == 'Equals') {
                        if (
                            this.oData[i].sCauseCd &&
                            this.oData[i].sCauseCd ==
                                this.filterJsonArray[j].value
                        ) {
                            if (this.filterJsonArray[j].match) {
                                this.filterJsonArray[j].match.push(
                                    this.oData[i]
                                );
                            } else {
                                this.filterJsonArray[j].match = [this.oData[i]];
                            }
                        }
                    } else if (
                        this.filterJsonArray[j].operator == 'Not Equals'
                    ) {
                        if (
                            this.oData[i].sCauseCd &&
                            this.oData[i].sCauseCd !=
                                this.filterJsonArray[j].value
                        ) {
                            if (this.filterJsonArray[j].match) {
                                this.filterJsonArray[j].match.push(
                                    this.oData[i]
                                );
                            } else {
                                this.filterJsonArray[j].match = [this.oData[i]];
                            }
                        }
                    }
                }
            }
        }
        this.filterJsonArray.forEach((element) => {
            uniqueChars = uniqueChars.concat(element.match);
        });
		this.serveresult = uniqueChars;
        if (this.serveresult.length <= 0) {
            this.template.querySelector(
                'c-claims-table-component-hum'
            ).noDataMessage = 'No Result found';
        }
        this.oData = this.serveresult;
        this.setTotalRecords(this.oData);

    }
}