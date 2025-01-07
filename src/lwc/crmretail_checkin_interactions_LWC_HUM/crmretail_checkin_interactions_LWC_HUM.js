import { LightningElement,api } from 'lwc';
import NameLabel from '@salesforce/label/c.CRMRetail_Name';
import PlaceholderLabel from '@salesforce/label/c.CRMRetail_Placeholder';
import fetchSearchConfigMetaData from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.fetchSearchConfigMetaData';

const className='overflowclass';
export default class Crmretail_checkin_interactions_LWC_HUM extends LightningElement {
columns;
@api searchText;
showSearchBox = true;
showSearchType = true;
@api switchmap;
@api placeholderText = PlaceholderLabel;
@api value = NameLabel;
options = [];

connectedCallback(){
    this.processinitial();    

    let searchTypeOptions= [];
    fetchSearchConfigMetaData()
    .then(response=>{
        if(response)
        {
            response.forEach(entry=>{
                searchTypeOptions.push({ label: entry.Label, value: entry.MasterLabel })
            })
            this.options = searchTypeOptions;
        }
    })
    
}

processinitial()
{
   this.columns = [
		{label: 'Alert', fieldName: 'alert', type: 'button',sortable: false, initialWidth:12,
		typeAttributes:{iconName: { fieldName: 'alertIcon' }, name: 'showNotification', variant:'base',disabled: false,class:'bellIcon'}}, 
	   {label: 'Birthday', fieldName: 'birthday', type: 'button',sortable: false,initialWidth:65,
		typeAttributes:{iconName: { fieldName: 'birthdayIconName' }, variant:'base',title: { fieldName: 'birthdayIconLabel' },disabled: false,class: { fieldName: 'birthdayIconClass'}}}, 
	   { label: 'Name', fieldName: 'accountURL', type: 'url', sortable: true,
		typeAttributes: {label: { fieldName: 'accountName' }, target: '_blank', tooltip: { fieldName: 'notificationToolTip'}}, cellAttributes:{class:className}},
	   {label: 'Category', fieldName: 'category', type: 'button',sortable: true, initialWidth:85,
		typeAttributes:{iconName: { fieldName: 'categoryIconName' }, variant:'base',title: { fieldName: 'categoryIconLabel' },disabled: false,class: { fieldName: 'categoryIconClass'}},
		cellAttributes:{iconName: { fieldName: 'categoryScheduledIconName'},iconPosition: 'right',class:'btnIcon2'}},
	   {label: 'Member', fieldName: 'isMember', type: 'button', sortable: true, initialWidth:70,
		typeAttributes:{iconName: { fieldName: 'memberIconName' }, variant:'base',title: { fieldName: 'memberIconLabel' },disabled: false,class: 'memberIcon'}},
	   { label: 'Time', fieldName: 'Date', type: 'date', sortable: true, initialWidth:90,cellAttributes:{class:className},
		typeAttributes:{hour:'2-digit',minute:'2-digit',second:'2-digit',hour12:true}},
	   { label: 'Interaction Reason', fieldName: 'interactionReasonURL', type: 'url',
		sortable: true, typeAttributes: {label: { fieldName: 'reasonName' }, target: '_blank', tooltip: { fieldName: 'reasonToolTip' }}, cellAttributes:{class:className}} 
	];	
	if(this.switchmap && this.switchmap['Switch_3782843'])
	{                   
		this.columns.push({ label: 'Created By', fieldName: 'createdByName', type: 'text', sortable: true, cellAttributes:{class:className} });
	}
	else
	{
		this.columns.push({ label: 'Last Modified By', fieldName: 'lastModifiedByName', type: 'text', sortable: true, cellAttributes:{class:className} });   
	}
	this.columns.push({ label: '', fieldName: 'isMissingEvent', type: 'button',sortable: true,initialWidth:30,
	typeAttributes:{iconName: { fieldName: 'missingEventIconName' }, variant:'base',title: 'Missing Event',disabled: false,class: 'priorityIcon'}});
	if(this.switchmap && this.switchmap['Switch_3510484'])
	{
		this.columns.push({ label: '', fieldName: 'eventRecommendation', type: 'button', sortable: false,initialWidth:35,
		typeAttributes:{iconName: { fieldName: 'eventRecommendationIconName' }, name: 'showRecommendations',variant:'base',title: 'Event Recommendations',disabled: false,class: 'eventRecommendationIconClass'}} );
	}
	if(this.switchmap && this.switchmap['Switch_3551183']== false){
		this.showSearchBox = false;
	}
	if(this.switchmap && this.switchmap['Switch_2792916']== false){
		this.showSearchType = false;
	}
}																																															

reloadInteractions(event){
    if(event.detail){
        this.dispatchEvent(new CustomEvent('reloadpage',{detail:event.detail}));
    }
    else{
        this.dispatchEvent(new CustomEvent('reloadpage'));
    }
}

handleSearchFilterChange(event){
    this.placeholderText = 'Search for '+event.target.value;
    this.value = event.target.value;
	if(this.searchText)
    {
        this.searchText = '';
        let evtVar = {target:{value:''}};
        this.handleSearchBox(evtVar);
    }
}

handleSearchBox(event){
	this.searchText = event.target.value;
	if(this.searchText != null){
		if (this.searchText.length > 2 || this.searchText.length == 0) {
			this.dispatchEvent(new CustomEvent('searchresults',{
				detail:{
					'numberOfRowsToSkip':0,
					'table':null,
					'sortedBy':null,
					'sortDirection':null,
					'searchString': this.searchText,
                    'searchType': this.value
				}
			}));
		}
	}
}
initiateDatatable(checkin,onsite,virtual,isSearch){
    if(this.columns){
        this.template.querySelector('[data-id="checkin"]').intermediateMethod(checkin,onsite,virtual,isSearch,this.switchmap);
    }
}
@api loadInteractions(checkin,onsite,virtual,isSearch){
	if(this.columns){
        this.template.querySelector('[data-id="checkin"]').intermediateMethod(checkin,onsite,virtual,isSearch,this.switchmap);
    }
}
}