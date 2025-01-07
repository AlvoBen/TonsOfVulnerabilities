/*
LWC Name        : loggingUtilityHum.js
Function        : LWC to handle logging functionality.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar              2/16/2022	                   initial version
* Nirmal Garg					  2/16/2022					   initial version
* Abhishek Mangutkar			  6/08/2022					   US - 3305321
* Swapnali Sonawane				  10/12/2022                   US - 3798689 Defect DF-6061 - Logging Framework
* Disha Dole                      12/06/2022                   DF-6169: Person Account - double arrows on Case History section show a component error when clicked
* Nirmal Garg                      01/12/2023                  DF-6952
* Swapnali Sonawane               01/06/2023                   US - 4588633 medical benefits UI logging - Search        
****************************************************************************************************************************/

import pubSubHum from 'c/loggingPubSubHum';
import { LightningElement, track, api, wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { publish, MessageContext, subscribe, unsubscribe, APPLICATION_SCOPE } from 'lightning/messageService';
import loggingLMSChannel from '@salesforce/messageChannel/loggingLMSChannel__c';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';

let collectedLoggedData = [];
let loggedElement;
let columnRef;
let datavalueelement;
let pageref;
let lstLoggingkey=[];
let selectedRowIndexSearch;

export default class LoggingUtilityHum extends LightningElement{

    constructor(){
        super();
        this.wiredPageRef();
        this.connectedCallback();
    }

    loggingSubscription = null;
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        pageref = pageRef;
    }

    connectedCallback(){
        this.subscribeToLoggingMessageChannel();
    }

    subscribeToLoggingMessageChannel() {
        if (!this.loggingSubscription) {
            this.loggingSubscription = subscribe(
                this.messageContext,
                loggingLMSChannel,
                (message) => this.handleLoggingMessage(message));
        }
    }

    handleLoggingMessage(message) {
        console.log(message.MessageName);
    }

    disconnectedCallback() {
        this.unsubscribeToLoggingMessageChannel();
    }

    unsubscribeToLoggingMessageChannel() {
        unsubscribe(this.loggingSubscription);
        this.loggingSubscription = null;
    }
}

const performFilterLogging = (loggingData,relatedInputField,nameofscreen, logId, pageRef,labelName) => {
    if(loggingData){
        if (loggingData && Array.isArray(loggingData) && loggingData?.length > 0) {
            loggingData.forEach(k => {
                let msgLogData = {
                    LabelName: labelName, LabelValue: k?.value ?? '', RelatedField: relatedInputField, Section: nameofscreen, LogId: logId
                };
                sendSearchCriteria(msgLogData, pageRef);
            })
        }
    }
}

const sendSearchCriteria = (payload, pageRef) => {
    try {
        pubSubHum.fireEvent(pageRef, 'loggingSearchCriteriaEvent', payload);
    }
    catch (err) {
        console.log(err);
    }
}

const performLogging = (event,relatedInputField,nameofscreen, loggingkey, pageRef) => {
        loggedElement = null;
        getElement(event.target);
        let selectedLabel='';
        let logid=null;
        let selectedValue='';
        let donotlog=false;
        if(loggedElement){
            selectedLabel = loggedElement.hasAttribute('data-id') ? loggedElement.getAttribute('data-id') : null;
            logid = loggedElement.hasAttribute('data-logid') ? loggedElement.getAttribute('data-logid') : null;
            if(loggedElement?.classList?.value?.includes('slds-col') || loggedElement?.outerText?.includes('\n')){
                if(!loggedElement.hasAttribute('data-donotlog')){
                    loggedElement.classList.toggle('logdata');
                    collectLogElements(loggedElement,loggingkey);
                    let node = loggedElement.outerText;
                    if(node && node?.includes('\n')){
                        let columnnode  = node?.includes('\n') ? node.split('\n') : null;
                        if(columnnode && columnnode.length >= 2){
                            selectedLabel = selectedLabel?? columnnode[0];
                            if(loggedElement.hasAttribute('data-value'))
                            {
                                selectedValue= loggedElement.getAttribute('data-value');
                            }
                            else{


                            for(let i=1; i <= columnnode.length -1; i++){
                                selectedValue = `${selectedValue}${columnnode[i]}`
                            }
                        }
                        }
                        else{
                            selectedValue = '';//node;
                        }
                    }else{
                        if(loggedElement && loggedElement?.childNodes
                        && loggedElement.childNodes.length > 0){
                            for(let i=0; i < loggedElement.childNodes.length ; i++){
                                if(i==0){
                                    selectedLabel = loggedElement.childNodes[i].outerText;
                                }else if(i==1){
                                    selectedValue = loggedElement.childNodes[i].outerText.trim();
                                }
                            }
                        }
                    }
                }else{
                    donotlog = true;
                }
            } else if(loggedElement?.parentElement && loggedElement?.parentElement?.outerText?.includes('\n')){
                if(!loggedElement.parentElement.hasAttribute('data-donotlog')){
                    loggedElement.parentElement.classList.toggle('logdata');
                    collectLogElements(loggedElement.parentElement,loggingkey);
                    let node = loggedElement.parentElement.outerText;
                    if(node){
                        let columnnode  = node?.includes('\n') ? node.split('\n') : null;
                        if(columnnode && columnnode.length >= 2){
                            selectedLabel = columnnode[0];
                            for(let i=1; i <= columnnode.length -1; i++){
                                selectedValue = `${selectedValue}${columnnode[i]}`
                            }
                        }
                    }
                }
                else{
                    donotlog = true;
                }
            }else if(loggedElement && (loggedElement?.nodeName.toUpperCase() === 'LIGHTNING-LAYOUT-ITEM'
            || loggedElement?.parentElement?.nodeName.toUpperCase() === 'LIGHTNING-LAYOUT-ITEM')){
                if(!loggedElement.parentElement.hasAttribute('data-donotlog')
                || !loggedElement.hasAttribute('data-donotlog')){
                    loggedElement.parentElement.classList.toggle('logdata');
                    collectLogElements(loggedElement.parentElement,loggingkey);
                    let node = loggedElement.parentElement.outerText;
                    if(node){
                        let columnnode  = node?.includes(':') ? node.split(':') : node?.includes(" ") ? node.split(" ") : node?.includes('\n') ? node.split('\n') : null;
                        if(columnnode && columnnode.length >= 2){
                            selectedLabel = node?.includes(':') ? `${columnnode[0]}:` : columnnode[0];
                            for(let i=1; i <= columnnode.length -1; i++){
                                selectedValue = `${selectedValue}${columnnode[i]}`
                            }
                        }
                    }
                }
            }else if(loggedElement && (loggedElement.hasAttribute("data-value") || loggedElement?.parentElement.hasAttribute("data-value"))){
                if(!loggedElement.hasAttribute('data-donotlog') || !loggedElement.parentElement.hasAttribute('data-donotlog')){
                    loggedElement.parentElement.classList.toggle('logdata');
                    collectLogElements(loggedElement.parentElement,loggingkey);
                    selectedValue = loggedElement.getAttribute("data-value");
                }

            }
            else{
                loggedElement.parentElement.classList.toggle('logdata');

                collectLogElements(loggedElement.parentElement,loggingkey);
                /// code update start
                if(loggedElement.nextElementSibling){
                    getDataValueElement(loggedElement.nextElementSibling);
                }
                else{
                    selectedValue = '';
                    datavalueelement = null;
                }
                /// code update end
                if(datavalueelement === null || datavalueelement === undefined){
                    getDataValueElement(loggedElement);
                    if(datavalueelement){
                        selectedValue = datavalueelement.outerText;
                    }
                }else{
                    selectedValue = datavalueelement.outerText;
                }
            }
            if(!donotlog){
                let msgLogData = { LabelName: selectedLabel, LabelValue: selectedValue != null || selectedValue != undefined
                    ? selectedValue : '', RelatedField: relatedInputField, Section: nameofscreen, LogId : logid };
                    sendData(msgLogData,pageRef);
            }

    }
}

function getDataValueElement(element) {
    if(element){
        if(element.hasAttribute('data-value')){
            datavalueelement = element;
            return;
        }else{
            if(element.childNodes && element.childNodes.length > 0){
                element.childNodes.forEach(k => {
					if(k.outerText){
						getDataValueElement(k);
					}
                });
            }
        }
    }
}

const clearLoggedValues = (key) =>{
    let loggeddata = collectedLoggedData.filter(k => k.loggingkey === key);
    collectedLoggedData = collectedLoggedData.filter(k => k.tabId !== key);
    loggeddata.forEach(k =>{
        k.element.classList.remove('logdata');
    })
}

const performTableLogging = (event,tableData,relatedInputField,columns,nameofscreen,pageRef, relatedField, loggingkey, selectedRowIndex = null) =>{
        if(selectedRowIndex){
            selectedRowIndexSearch = Number(selectedRowIndex);
        }
        let clickedElement = event.target;
        if(clickedElement && clickedElement?.nodeName && clickedElement?.nodeName === 'TD'){
            columnRef = clickedElement;
        }
        else {
          columnRef = traverseUpTill(event.target, 'TD');
        }
        if (event != null && event.target != null && event.target.parentElement != null) {
          const pElement = event.target.parentElement;

          if (!pElement?.classList?.value?.includes("checkbox-cell") && !pElement?.classList?.value?.includes("results-icon-col") && !clickedElement?.classList?.value?.includes("checkbox-cell") && !clickedElement?.classList?.value?.includes("results-icon-col")) {
            if (pElement?.classList?.value?.includes("logthis") || pElement?.classList?.value?.includes("results-table-cell")) {
              sendSelectedDataToLog(pElement, event, columnRef.cellIndex,tableData,relatedInputField,columns,nameofscreen,pageRef,loggingkey);
            }
            else if (clickedElement?.classList?.value?.includes("logthis")) {
              sendSelectedDataToLog(clickedElement, event, columnRef.cellIndex,tableData,relatedInputField,columns,nameofscreen,pageRef,loggingkey);
            }
          }
     }

}

function collectLogElements(element,key) {
    collectedLoggedData.push({
        loggingkey : key,
        element : element
    });
}

function sendSelectedDataToLog(clickedElement, event, colIndex,tableData,relatedInputField,columns,nameofscreen,pageRef,loggingkey) {
    let labelName = '';
    let elTraverse = traverseUpTill(event.target, "TR");
    let elThead  = traverseUpTill(event.target,'TBODY');
    let cIndex ;
    let columnvalue='';
    let rowdata;
    let compoundvalue;
	let rowRef;
    if (clickedElement.nodeName === 'TD') {
      columnRef = clickedElement;
    }
    else {
      columnRef = traverseUpTill(event.target, 'TD');
    }
    if(columnRef){
        rowRef = traverseUpTill(event.target,'TR');
    }
    if(elThead && elThead.previousSibling && elThead.previousSibling.childNodes[0]){
      if(elThead.previousSibling.childNodes[0].nodeName === 'TR'){
        let thChildNodes = elThead.previousSibling.childNodes[0].childNodes;
        if(thChildNodes && thChildNodes.length > 0){
          cIndex = thChildNodes[colIndex].getAttribute('data-col');
        }
      }
    }
    if (!cIndex){cIndex=colIndex; }

    if(columns && columns[cIndex]?.compoundx){
        labelName = columns[cIndex]?.label ?? '';
        rowdata = rowRef && rowRef?.rowIndex ? tableData[rowRef.rowIndex-1] : '';
        compoundvalue = columns[cIndex]?.compoundvalue;
        if(compoundvalue && Array.isArray(compoundvalue) && compoundvalue.length > 0){
            compoundvalue.forEach(k =>{
                if(k && !k?.hidden){
                    columnvalue += `${k.label}${k.label?': ':''}${rowdata && rowdata.hasOwnProperty(k.fieldName)?rowdata[k.fieldName]:''}\n`
                }
            })
        }
    }
    else{
        labelName = columns[cIndex]?.label ?? '';
	    columnvalue = clickedElement.outerText != null || clickedElement.outerText != undefined ? clickedElement.outerText : '';
    }

    let rowIndex = elTraverse.rowIndex;
    let labelValue = columnvalue;
    if(rowIndex >= 1 && labelName && labelValue){
      let tData = selectedRowIndexSearch ? tableData[selectedRowIndexSearch] : tableData[rowIndex-1];
      selectedRowIndexSearch = null;
      if(tData){
        clickedElement.classList.toggle("logdata");
        collectLogElements(clickedElement,loggingkey);
        let msgLogData = { LabelName: labelName, LabelValue: labelValue, RelatedField: createRelatesfield(tData,relatedInputField), Section: nameofscreen,TableIndex : { colIndex : colIndex, rowIndex : rowIndex} };
        sendData(msgLogData,pageRef);
      }

    }
    tableData=null;
    columns=null;
}

function createRelatesfield(tdata,relatedInputField){
    let temp =[];
    if(relatedInputField && Array.isArray(relatedInputField)){
          relatedInputField.forEach(k =>{
            if (typeof tdata[k.mappingField]=='number')
            {
                temp.push({
                    label : k.label,
                    value : k && k?.mappingField ? tdata[k.mappingField] : k.value
                  });
            }else{
            temp.push({
              label : k.label,
              value : k && k?.mappingField ? tdata[k.mappingField]?.includes("#&;") ? tdata[k.mappingField].split("#&;")[0] : tdata[k.mappingField] : k.value
            });}
          });
    }else{
      temp.push({
        label : relatedInputField,
        value : tdata[relatedInputField] && tdata[relatedInputField]?.includes("#&;") ? tdata[relatedInputField].split("#&;")[0] : tdata[relatedInputField]
      });
    }
    return temp;

  }

const sendData = (payload,pageRef) =>{
    try{
        pubSubHum.fireEvent(pageRef, 'loggingDataEvent', payload);
    }
    catch(err){
        console.log(err);
    }

}

function traverseUpTill(clickedElement, nodeName) {
    let prtNode = clickedElement;
	if(prtNode && prtNode?.parentNode){
		while (prtNode.parentNode) {
			prtNode = prtNode.parentNode;
		  if (prtNode.nodeName == nodeName)
			return prtNode;
		}
	}
    return null;
}

function getElement(element) {
    try{
        if(element != null && element?.classList && element?.classList?.value
            && element?.classList?.value?.includes("logthis")){
            loggedElement = element;
            return;
        }else if(element && element?.hasAttribute('data-donotlog')){
            loggedElement = null;
            return;
        }
        else{
            if(element && element?.parentNode){
                getElement(element.parentNode)
            }
        }
    }
    catch(e){
        console.log('Error: ' + e);
    }

}

const getLoggingKey = (pageref) => {
    return new Promise((resolve,reject)=>{
        try {
            getTabDetails().then(result =>{
                if(pageref && pageref?.attributes && pageref?.attributes?.recordId){
                    resolve(`${result}-${pageref.attributes.recordId}`)
                }else if(pageref && pageref?.attributes && pageref?.attributes?.attributes &&
                    pageref?.attributes?.attributes?.Id){
                        resolve(`${result}-${pageref.attributes.attributes.Id}`);
                }else if(pageref && pageref?.state && pageref?.state?.C__Id){
                    resolve(`${result}-${pageref.state.C__Id}`);
                }
				else if(pageref && pageref?.state && (pageref?.attributes?.attributes?.C__Id || pageref?.attributes?.url)){
                    if (pageref?.attributes?.attributes?.C__Id){
                        resolve(`${result}-${pageref.attributes.attributes.C__Id}`);
                    }else if (pageref?.attributes?.url){
                        let navData = pageref?.attributes?.url.split('&');
                        let newObj = {};
                        navData.map(item => {
                            let splittedData = item.split('=');
                            newObj[splittedData[0]] = splittedData[1];
                        });
                        resolve(`${result}-${newObj.Id}`);
                    }
                }
            });
        } catch (error) {
            console.log(error);
            reject(error);
        }
    });
}

const setEventListener = (loggingkey, msg) =>{
    let loggignele = lstLoggingkey.find(k => k.loggingkey === loggingkey);
    if(loggignele){
        lstLoggingkey.forEach(t =>{
            if(t.loggingkey === loggingkey){
                t.msg = msg;
            }
        })
    }else{
        lstLoggingkey.push({
            loggingkey : loggingkey,
            msg : msg
        });
    }
    if(msg ===  'Start Logging'){
        sessionStorage.setItem(loggingkey,msg);
    }
    if(msg === 'Stop Logging'){
        sessionStorage.removeItem(loggingkey);
        clearLoggedValues(loggingkey);
    }
}

const checkloggingstatus = (key) =>{

    let loggingele = lstLoggingkey.find(k => k.loggingkey === key);
    if(loggingele && loggingele?.msg && loggingele.msg === 'Start Logging'){
        return true;
    }else{
        return false;
    }
}

function getTabDetails() {
    return new Promise((resolve, reject) => {
        let key = '';
        try {
            setTimeout(() => {
                if (invokeWorkspaceAPI('isConsoleNavigation')) {
                    invokeWorkspaceAPI('getFocusedTabInfo').then(result => {
                        let focusedTab = result;
                        key = focusedTab && focusedTab.isSubtab ? `${focusedTab.parentTabId}-${focusedTab.tabId}` : focusedTab.tabId;
                        resolve(key);
                    }).catch(error => {
                        console.log(error);
                        resolve(undefined);
                    })
                }
            }, "100");
        } catch (error) {
            reject(error);
        }
    })
}

const clearSearchCriteriaLog = (nameofscreen, pageRef) => {
    let clearDearchCriteriaMsg = {
        LabelName: 'Clear Search Screen Log Data', Section: nameofscreen
    };
    sendClearSearchCriteria(clearDearchCriteriaMsg, pageRef);
}

const sendClearSearchCriteria = (payload, pageRef) => {
    try {
        pubSubHum.fireEvent(pageRef, 'clearLoggingSearchCriteriaEvent', payload);
    }
    catch (err) {
        console.log(err);
    }
}

export {performLogging,performTableLogging,performFilterLogging,setEventListener,checkloggingstatus,clearLoggedValues,getLoggingKey, clearSearchCriteriaLog, LoggingUtilityHum}