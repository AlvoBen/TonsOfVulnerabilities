/*******************************************************************************************************************************
File Name : unifiedSearch_LWC_HUM.JS 
Version : 1.0
Created On : 01/18/2021
Function : This file contains javascripts for unifiedSearch_LWC_HUM component

Modification Log: 
* Modification ID		Date				Developer Name           	Description                                         
*------------------------------------------------------------------------------------------------------------------------------
* 1.0       			01/18/2021			Akshay Pai					Orginal Version	
* 1.1       			09/2/2021			Lakshmi Madduri				US-2560943 Case/Interaction details dynamic
* 1.2                   11/09/2021  		Akshay Pai           	    REQ - 2773030. Matrix Defect Fix
* 1.3                   10/05/2021          Lakshmi Madduri             US-2512908, US-2513247 
* 1.4					10/18/2021			Lakshmi Madduri				Fixing the scroll bar issue
* 1.5                   10/19/2021          Lakshmi Madduri             Remove execute query to make search & analytics call if tab is available
* 1.6                   10/21/2021          Lakshmi Madduri             Changes on post message event
* 1.7                   11/2/2021           Lakshmi Madduri             DF-4008 Retain scroll bar position after facet selection
****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { loadScript, loadStyle } from 'lightning/platformResourceLoader';

import coveoStaticResource from '@salesforce/resourceUrl/UnifiedSearchToolKit_SR_HUM';
import coveoLightningBundle from '@salesforce/resourceUrl/UnifiedSearchLightningBundle_SR_HUM';
import searchAPI from '@salesforce/apexContinuation/UnifiedSearch_LC_HUM.searchQuery';
import crmjquery from '@salesforce/resourceUrl/CRM_JQUERY';
import logError from '@salesforce/apex/HUMExceptionHelper.logError';
import generateTokenAPI from '@salesforce/apexContinuation/UnifiedSearch_LC_HUM.generateToken';

import resultTemplate from './unifiedSearchResult_H_HUM';
import searchTemplate from './unifiedSearch_H_HUM';

export default class CoveoSample extends LightningElement {

    @api pageIdentifier;
    @api hideIntDetails = false;
    @api contextInput;
    @api intWithType;
    @api intWith;
    @api CaseNumber;
    @api lQValue;
    contextObj;
    setContext;
    windowContext;
    /**
     * Whether or not the component is loading.
     */
    @track isLoading = true;

    /**
     * The coveo-search-ui object.
     * @type {Coveo}
     */
    Coveo;

    /**
     * The root HTMLElement of the Search Interface.
     * @type {HTMLElement}
     */
    root = undefined;
    connectedCallback() {
        if (this.pageIdentifier && this.pageIdentifier == "default-Tab")
        {
            this.hideIntDetails = true;
            this.lQValue = "";
        }
        if (!this.CaseNumber && !this.intWithType && !this.intWith)
        {
            this.hideIntDetails = true;
        }
        this.allScriptsLoaded = this.loadScripts();   
        this.token = generateTokenAPI()
            .then(result => {
                var resEl = JSON.parse(JSON.stringify(result));
                this.token = resEl['token']; 
                var doTokenDecryption = resEl['decryptToken'];
				if(doTokenDecryption === 'true')
				{
					var decryptedObj;
					try 
					{     
						var base64Url = this.token.split('.')[1];
						var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
						var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
							return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
						}).join(''));
						decryptedObj = JSON.parse(jsonPayload);
					}
					catch(err)
					{
						if(err.name) logError({sMessage:'Issue in token decryption',sClass:'unifiedSearch_LWC_HUM',sMethod:'generateToken',sExceptionType:'UIException',sErrorType:err.name});
					} 
					try 
					{
						var obj={};
						var userGroups = decryptedObj.userGroups;
						for(let groupVal of userGroups){
							let arr = groupVal.split(':');
							let val = (arr[1]==='int') ? Number(arr[2]) : ((arr[1]==='boolean') ? Boolean(arr[2]) : arr[2]);
							obj[arr[0]] = val;
						} 
						this.contextObj=obj;
					}
					catch(err) 
					{
						if(err.name) logError({sMessage:'Issue while parsing the object',sClass:'unifiedSearch_LWC_HUM',sMethod:'generateToken',sExceptionType:'UIException',sErrorType:err.name});
					}					
				}                                                                    
            })
            .catch(error => {
                if(error.message && error.name)    
                {
                    logError({sMessage:error.message,sClass:'unifiedSearch_LWC_HUM',sMethod:'generateToken',sExceptionType:'UIException',sErrorType:error.name});
                } 
            });
        var lwcContext = this;
        window.addEventListener('message', function(event){
            if(event.data && event.data.type == 'UnifiedSearchWindow' && event.data.message){
                lwcContext.inputListener(event.data.message);
            }
        });
    }

    renderedCallback() {
        const container = this.template.querySelector('.container');
        container.innerHTML = searchTemplate;
        if (this.isRendered) {
            return;
        }
        this.isRendered = true;
        Promise.all([this.allScriptsLoaded, this.token]).then(([_, token]) => {
            this.Coveo = window.Coveo;
            this.initializeSearchInterface(this.token);
        });    
    }

    disconnectedCallback() {
        if (window.Coveo && this.root) {
            window.Coveo.nuke(this.root);
        }
    }

    /**
     * Initialize and render the Search Interface.
     */
    initializeSearchInterface(token) {
        this.windowContext = {
            resultList : this.template.querySelector('div.resultSection')       
        }
        // Get the "root" SearchInterface
        this.root = this.template.querySelector('div.CoveoSearchInterface');
        if (!this.hideIntDetails)
        {           
            if (this.CaseNumber)
            {
                var caseNumInnerHTML = this.template.querySelector('div.caseNumber'); 
                caseNumInnerHTML.innerHTML = '<strong>Case # :</strong>&nbsp;'+this.CaseNumber;
            }
            if (this.intWith || this.intWithType )
            {
                var intDetailsInnerHTML = this.template.querySelector('div.intDetails');
                var intWithValue = (this.intWith) ? this.intWith :"" ;
                var intWithTypeValue = (this.intWithType) ? this.intWithType :"" ;
                intDetailsInnerHTML.innerHTML = '<p><strong>Interacting with :</strong>&nbsp;'+intWithValue+'<br><strong>Interacting with type :</strong>&nbsp;'+intWithTypeValue+' </br></p>'                       
            }
        }
        
        this.Coveo.$$(this.root).on('afterComponentsInitialization', () => this.hideLoadingAnimation());

        // Inject the ResultTemplates (this is a limitation in LWC)
        const template = document.createElement('div');
        template.innerHTML = resultTemplate;
        this.resultTemplate = new this.Coveo.HtmlTemplate(template);

        // Configure the "special" endpoint with our token
        let endpoint = new this.Coveo.SearchEndpoint({
            restUri: 'fakeuri/rest/search'//this will be taken care at APEX level
            , accessToken: this.token,//token will be handled at APEX level
            storeInfo: this.windowContext
        });

        // Change the endpoint so that calls are proxied in Salesforce
        endpoint.caller.call = function (params) {          
            if(this.options.accessToken && this.options.accessToken != 'Error')     
            { 
               return searchAPI({ params: JSON.stringify({ ...params, ...this.options })}).then(result => {               
                if (result)
                {      
                    this.options.storeInfo.resultList.scrollTop=0;                     
                    var i,millisecDate;
                    var retResult = JSON.parse(result);                    
                    var searchUid;
                    if (retResult.data.searchUid){
                        this.searchUid = retResult.data.searchUid;
                    }                    
                    if(retResult.data.results) 
                    {
                        for(i in retResult.data.results)
                        {   
                            if(retResult.data.results[i].raw)
                            {
                               if (retResult.data.results[i].raw.lastpublisheddate)
                                {   
                                    millisecDate= new Date(retResult.data.results[i].raw.lastpublisheddate);
                                    if(!isNaN(millisecDate.valueOf()))
                                    var vMonth = millisecDate.getMonth();
                                    var vDate = millisecDate.getDate();
                                    retResult.data.results[i].raw.lastpublisheddate = ((vMonth > 8) ? (vMonth + 1) : ('0' + (vMonth + 1))) + '/' + ((vDate > 9) ? vDate : ('0' + vDate)) + '/' + millisecDate.getFullYear();
                                }                                                             
                                var isMentorSource = retResult.data.results[i].raw.source.toLowerCase().includes("mentor");
                                if(retResult.data.results[i].raw.source && retResult.data.results[i].clickUri && isMentorSource)  
                                {
                                    var modifyUrl = new URL(retResult.data.results[i].clickUri);
                                    modifyUrl.searchParams.set('searchEventUid', this.searchUid );
                                    retResult.data.results[i].clickUri = decodeURI(modifyUrl.href);
                                }
                            }
                        }   
                    }
                    return retResult;                  
                }                    
            })
            .catch(error => { 
                if(error.message && error.name)    
                {
                    logError({sMessage:error.message,sClass:'unifiedSearch_LWC_HUM',sMethod:'searchAPI',sExceptionType:'UIException',sErrorType:error.name});
                }          
            });
        }
        return null;
    }
        
        this.Coveo.$$(this.root).on('afterComponentsInitialization', (e, args) => {
            let searchInterface = this.Coveo.get(this.root, this.Coveo.SearchInterface);
             searchInterface.usageAnalytics.endpoint.endpointCaller.call = function (params) {
                if(this.options.accessToken && this.options.accessToken != 'Error' && params.url != 'fakeuri/rest/ua/v15/analytics/custom')     
                {
                    return searchAPI({ params: JSON.stringify({ ...params, ...this.options })}).then(result => {                         
                        return JSON.parse(result);
                    })
                    .catch(error => {
                        if(error.message && error.name)    
                        {
                            logError({sMessage:error.message,sClass:'unifiedSearch_LWC_HUM',sMethod:'analyticsAPI',sExceptionType:'UIException',sErrorType:error.name});
                        } 
                    });
                }
                return null;                
            };
        });
		
		this.Coveo.$$(this.root).on('changeAnalyticsCustomData', (e, args) => {
            if(args.type === 'ClickEvent'){
                for(let key of Object.keys(this.setContext)){
                    let appendKey = 'context_'+key;
                    args.metaObject[appendKey] = this.setContext[key];
                }
            }
        });
        
        const options = {
            SearchInterface: {
                endpoint: endpoint,
                enableHistory: false,
                pipeline: 'CRMUI_pipeline',
                searchHub: 'CRMUnifiedSearch',
            },
            Analytics: {
                endpoint: 'fakeuri/rest/ua'
            },
            ResultList: {
                resultTemplate: this.resultTemplate
            },
            ResultLink: {
                onClick: (e, result) => {
                    e.preventDefault();
                    e.currentTarget['CoveoResultLink'].openLinkInNewWindow();
                }
            }
        };

        //building query params
        this.Coveo.$$(this.root).on('buildingQuery', (e, args) => {
            if(this.lQValue){
                args.queryBuilder.longQueryExpression.add(this.lQValue); 
            }          
            if(this.contextInput){                
                var contextValue = JSON.parse(this.contextInput);
                //combine the context 
                 this.setContext = Object.assign(contextValue,this.contextObj);
                args.queryBuilder.addContext(this.setContext)
            }
        });

        this.Coveo.init(this.root, options);
    }

    /**
     * Load all Search-UI scripts.
     */
    loadScripts() {
        return Promise.all([
            loadStyle(this, `${coveoStaticResource}/css/CoveoFullSearchNewDesign.css`),
            loadScript(this, `${crmjquery}/jquery.min.js`)
                .then(() => loadScript(this, `${coveoStaticResource}/js/CoveoJsSearch.min.js`))
                .then(() => loadScript(this, `${coveoStaticResource}/js/templatesNew.js`))
                .then(() => loadScript(this, `${coveoLightningBundle}/UnifiedSearchLightningBundle_SR_HUM.js`))
        ]);
    }

    showLoadingAnimation() {
        this.isLoading = true;
    }

    hideLoadingAnimation() {
        this.isLoading = false;
        this.template.querySelector('div.coveo-spinner-container').classList.add('slds-hide');
    }

    inputListener(message){       
        if(message){
            let res = JSON.parse(message) ;
            if(res.identifier === this.pageIdentifier){
                if(res.lQValue){
                    this.lQValue = res.lQValue;
                }
                if(res.context){
                    this.contextInput = JSON.stringify(res.context);
                }             
                // check for autorunning the query search
                if(res.refreshResult && res.refreshResult.toUpperCase() == "TRUE") {
                    if(res.intWithType || res.intWith){
                        this.intWithType = res.intWithType;
                        this.intWith = res.intWith;
                        var intDetailsInnerHTML = this.template.querySelector('div.intDetails');
                        var intWithValue = (this.intWith) ? this.intWith :"" ;
                        var intWithTypeValue = (this.intWithType) ? this.intWithType :"" ;
                        intDetailsInnerHTML.innerHTML = '<p><strong>Interacting with :</strong>&nbsp;'+intWithValue+'<br><strong>Interacting with type :</strong>&nbsp;'+intWithTypeValue+' </br></p>'     
                        intDetailsInnerHTML.style.display='block';
                    }              
                    else{
                        var intInnerHTML = this.template.querySelector('div.intDetails');
                        intInnerHTML.style.display='none';
                        this.intWithType='';
                        this.intWith='';
                    }

                    if(res.CaseNumber && res.CaseNumber != this.CaseNumber)
                    {
                        this.CaseNumber = res.CaseNumber;                    
                        var caseNumInnerHTML = this.template.querySelector('div.caseNumber'); 
                        caseNumInnerHTML.innerHTML = '<strong>Case # :</strong>&nbsp;'+this.CaseNumber;
                        caseNumInnerHTML.style.display='block';
                    }  
                    else if(!res.CaseNumber){
                        var caseInnerHTML = this.template.querySelector('div.caseNumber');
                        caseInnerHTML.style.display='none';
                        this.CaseNumber='';
                    }
                    var searchIcon = this.template.querySelector('a.CoveoSearchButton');
                    searchIcon.click();
                }
                
            } 
        }
    }
    
}