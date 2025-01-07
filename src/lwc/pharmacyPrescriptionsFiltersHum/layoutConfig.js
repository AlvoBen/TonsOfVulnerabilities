export function getPrescriptionIcons(name)
{
    if(name === "icons"){
        return statusIcon;
    }
}
export const statusIcon = [{
    "iconname" : "INACTIVE",
    "iconvalue" : "utility:sentiment_neutral",
    "iconclass" : "slds-icon_container slds-icon-utility-sentiment_neutral",
    "iconsize" : "small"
},{
    "iconname" : "medication_ingredient",
    "iconvalue" : "standard:product_request",
    "iconclass" : "slds-icon_container slds-icon-standard-product-request",
    "iconsize" : "small"
},{
    "iconname" : "event",
    "iconclass" : "slds-icon_container  slds-icon-utility-event",
    "iconsize" : "medium",
    "customicon" : true,
    "refillvalue" : true,
    "eventicon" : true,
    "icontype" : "black"
},{
    "iconname" : "clock",
    "iconvalue" : "utility:clock",
    "iconclass" : "slds-icon_container  slds-icon-utility-clock",
    "iconsize" : "small",
    "icontype" : "black"
},
{
    "iconname" : "refillsremaining",
    "iconclass" : "slds-icon_container  slds-icon-standard-address",
    "iconsize" : "medium",
    "customicon" : true,
    "refillvalue" : true,
    "iconnamemedication" : true,
    "icontype" : "green"
},
{
    "iconname" : "greenclock",
    "greenclockicon" : true,
    "iconclass" : "slds-icon_container greenclock",
    "iconsize" : "medium",
    "customicon" : true,
    "iconvalue" : '<span class="slds-icon_container greenicon"><svg focusable="false" data-key="today" aria-hidden="true" viewBox="0 0 100 100" class="slds-icon slds-icon_large"><g><path d="M50 20c-16.5 0-30 13.5-30 30s13.5 30 30 30 30-13.5 30-30-13.5-30-30-30zm0 54c-13.2 0-24-10.8-24-24s10.8-24 24-24 24 10.8 24 24-10.8 24-24 24z"></path><path d="M53 48.8V36c0-1.1-.9-2-2-2h-2c-1.1 0-2 .9-2 2v14c0 .8.3 1.6.9 2.1l9.6 9.6c.8.8 2 .8 2.8 0l1.4-1.4c.8-.8.8-2 0-2.8L53 48.8z"></path></g</svg></span>',
    "icontype" : "green"

},{
    "iconname" : "blueclock",
    "blueclockicon" : true,
    "iconclass" : "slds-icon_container blueclock",
    "iconsize" : "medium",
    "customicon" : true,
    "iconvalue" : '<span class="slds-icon_container blueclock"><svg focusable="false" data-key="today" aria-hidden="true" viewBox="0 0 100 100" class="slds-icon slds-icon_large"><g><path d="M50 20c-16.5 0-30 13.5-30 30s13.5 30 30 30 30-13.5 30-30-13.5-30-30-30zm0 54c-13.2 0-24-10.8-24-24s10.8-24 24-24 24 10.8 24 24-10.8 24-24 24z"></path><path d="M53 48.8V36c0-1.1-.9-2-2-2h-2c-1.1 0-2 .9-2 2v14c0 .8.3 1.6.9 2.1l9.6 9.6c.8.8 2 .8 2.8 0l1.4-1.4c.8-.8.8-2 0-2.8L53 48.8z"></path></g</svg></span>',
    "icontype" : "blue"

},{
    "iconname" : "bluecalendar",
    "bluecalendaricon" : true,
    "iconclass" : "slds-icon_container blueclock",
    "iconsize" : "medium",
    "customicon" : true,
    "iconvalue" : '<span class="slds-icon_container blueclock"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" id="event"><path d="M76 42H24c-1.1 0-2 .9-2 2v30c0 3.3 2.7 6 6 6h44c3.3 0 6-2.7 6-6V44c0-1.1-.9-2-2-2zM40 70c0 1.1-.9 2-2 2h-4c-1.1 0-2-.9-2-2v-4c0-1.1.9-2 2-2h4c1.1 0 2 .9 2 2v4zm0-14c0 1.1-.9 2-2 2h-4c-1.1 0-2-.9-2-2v-4c0-1.1.9-2 2-2h4c1.1 0 2 .9 2 2v4zm14 14c0 1.1-.9 2-2 2h-4c-1.1 0-2-.9-2-2v-4c0-1.1.9-2 2-2h4c1.1 0 2 .9 2 2v4zm0-14c0 1.1-.9 2-2 2h-4c-1.1 0-2-.9-2-2v-4c0-1.1.9-2 2-2h4c1.1 0 2 .9 2 2v4zm14 14c0 1.1-.9 2-2 2h-4c-1.1 0-2-.9-2-2v-4c0-1.1.9-2 2-2h4c1.1 0 2 .9 2 2v4zm0-14c0 1.1-.9 2-2 2h-4c-1.1 0-2-.9-2-2v-4c0-1.1.9-2 2-2h4c1.1 0 2 .9 2 2v4zM72 26h-5v-2c0-2.2-1.8-4-4-4s-4 1.8-4 4v2H41v-2c0-2.2-1.8-4-4-4s-4 1.8-4 4v2h-5c-3.3 0-6 2.7-6 6v2c0 1.1.9 2 2 2h52c1.1 0 2-.9 2-2v-2c0-3.3-2.7-6-6-6z"></path></svg><span class="refill">{0}</span></span>',
    "refillvalue" : true,
    "icontype" : "blue"
}]