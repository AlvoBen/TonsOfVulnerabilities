import { LightningElement, track, api } from 'lwc';

// import server side apex class method 
import findRecords from "@salesforce/apex/InteractionController_LC_HUM.findIntwithLookupRecords";
import findInteractionRecords from "@salesforce/apex/InteractionController_LC_HUM.findInteractionRecords";
import { NavigationMixin } from 'lightning/navigation';
import { getFielddetails } from './humlookupConfig';
// import standard toast event 
import {ShowToastEvent} from 'lightning/platformShowToastEvent';
import {getLabels} from "c/crmUtilityHum";

const intNum = [
    { label: 'Interaction #', fieldName: 'Name',hideDefaultActions:true},
    { label: 'Name', fieldName: 'Caller_Name__c',hideDefaultActions:true},
    { label: 'Interacting With', fieldName: 'Interacting_With__c',hideDefaultActions:true},
    { label: 'Interacting With Type', fieldName: 'Interacting_With_type__c',hideDefaultActions:true},
    { label: 'Created By', fieldName: 'CreatedBy',hideDefaultActions:true}
];

const intWith = [
    { label: 'Account Name', fieldName: 'Name',hideDefaultActions:true},
    { label: 'Birthdate', fieldName: 'Birthdate__c',hideDefaultActions:true},
    { label: 'Account Site', fieldName: 'Site',hideDefaultActions:true},
    { label: 'Account Owner Alias', fieldName: 'Alias',hideDefaultActions:true},
    { label: 'Type', fieldName: 'Type',hideDefaultActions:true}
];


export default class HumlookupSearch extends LightningElement {
    @track interactionRec=[];
    @track labels = getLabels();
    @track searchValue = '';
    @track lookupModal;
    @track columns;

    @track interactingwith;
    @track divisionLayout;
    @track componentHeading;
    @track objectApiName;
    @track selectedObject;

    allClients; //storing all clients data. Not to be modified
    
    paginatedClientData; //storing array of client data based on limit chunks
    dataToDisplay; // storing only the data that need to be displayed
    pageLimit = '5'; //number of record to display per page
    selectedPage='1';//current selected page
    totalPages; //store total number of pages
    isFirst=true;
    isLast=false;
    NameorAllfields='NAME';
    value='NAME';
    
    totalRecords;

   @api handleLookupModal(interactingwith, objectApiName,searchKey) {
        
        this.interactingwith = interactingwith;
      this.lookupModal = true;
      this.objectApiName = objectApiName;
    if(interactingwith){
            this.componentHeading = "Interacting With";
            this.columns = intWith;
           //this.columns = getFielddetails(interactingwith);
         //  if(searchKey){
           this.searchValue = searchKey;
           this.handleSearchKeyword();
        //   }
        }
            else{
                this.componentHeading = "Interaction Number";
                this.columns = intNum;
             //this.columns = getFielddetails(interactingwith);
            // if(searchKey){
                this.searchValue = searchKey;
                this.handleSearchKeyword();
             //   }
            }
      
      
       }

       closeModal() {
        // to close modal set isModalOpen tarck value as false
     
        this.lookupModal = false;
        this.searchValue = '';
        this.interactionRec = [];
        this.paginatedClientData ='';
    }
 
    // update searchValue var when input field value change
    searchKeyword(event) {
        this.searchValue = event.target.value;
    }
 
    get options() {
        return [
            { label: 'Name', value: 'NAME' },
            { label: 'All Fields', value: 'ALL' },
        ];
    }
    // call apex method on button click 
    handleSearchKeyword() {
       //  if (this.searchValue !== '') {
          if(this.objectApiName == "Interaction__c"){
              
              this.selectedObject="intNum";
            findInteractionRecords
            ({
                    searchKey: this.searchValue,
                    objectName: this.objectApiName,
                    scrName :'lookup'
            })
                .then(result => {
                    
            if(result!=''){

            this.isLast=false;
            this.isFirst=true;
            var obj=JSON.parse(JSON.stringify(result));
           
           obj.forEach(function(el) {
          
          if (el.Interacting_With__c) {
             
            if(el.hasOwnProperty('Interacting_With__r')){
             
                el.Interacting_With__c = el.Interacting_With__r.Name;
               }else{
                   el.Interacting_With__c ='';
               }
            }
            
        
        
        el.CreatedBy = el.CreatedBy.Name;
        
        });
                   
            this.totalRecords=obj.length;
            this.allClients=obj;
            this.selectedPage = '1';
            this.handlePagination(); //invoking the pagination logic
            this.validatePagination(); 
            

                    }else{
                        this.interactionRec = '';
                        
                        const event = new ShowToastEvent({
                            variant: 'error',
                            message: 'No Records Found',
                        });
                        this.dispatchEvent(event);
                    }
                })
                .catch(error => {
                   const event = new ShowToastEvent({
                        title: 'Error',
                        variant: 'error',
                        message: error.body,
                    });
                    this.dispatchEvent(event);
                    // reset contacts var with null   
                    this.interactionRec = null;
                });
            }else{
                
                this.selectedObject="intWith";
                 findRecords({                    
                    searchKey: this.searchValue,
                    objectName: this.objectApiName,
                    filter : this.NameorAllfields
                })
                .then(result => {
                    if(result!=''){
                    this.isLast=false;
                    this.isFirst=true;
                  var obj=JSON.parse(JSON.stringify(result));

                    obj.forEach(function(el){
                        if (el.Owner) {
                        el.Alias = el.Owner.Alias;
                        }
                        });

                    
                    this.totalRecords=obj.length;
                    this.allClients=obj;
                    this.selectedPage = '1';
                    this.handlePagination(); 
                    }else{
                        this.interactionRec = '';
                        
                        const event = new ShowToastEvent({
                            variant: 'error',
                            message: 'No Records Found',
                        });
                        this.dispatchEvent(event);
                    }

                })
                .catch(error => {
                   
                    const event = new ShowToastEvent({
                        title: 'Error',
                        variant: 'error',
                        message: error.body.message,
                    });
                    this.dispatchEvent(event);
                    // reset contacts var with null   
                    this.interactionRec = null;
                });
            }
          }
    handleRowAction(event){

        var selectedRecords =  this.template.querySelector("lightning-datatable").getSelectedRows();
                   
            const passEventr = new CustomEvent("selectedrecord", {
                detail: {
                  selectedRecordId: selectedRecords[0].Id,
                  selectedValue: selectedRecords[0].Name,
                  selectedObject : this.selectedObject
                }
              });
              this.dispatchEvent(passEventr);
              this.closeModal();
    }

    handleRadiobuttonchange(event)
    {
                this.NameorAllfields=event.detail.value;
    }


    handleRecordSelection(event) {
         const recUniqueId = event.detail.Id;
         const getSelectedRow = this.interactionRec.filter(item => 
                                item.Id === recUniqueId);
         const selectedrow = getSelectedRow[0];
         const passEventr = new CustomEvent("selectedrecord", {
                detail: {
                  selectedRecordId: selectedrow.Id,
                  selectedValue: selectedrow.Name,
                  selectedObject : this.selectedObject
                }
              });
              this.dispatchEvent(passEventr);
              this.closeModal();
    }
handlePagination()
{
   
    this.totalPages = Math.ceil(this.allClients.length / parseInt(this.pageLimit));
    var perChunk = parseInt(this.pageLimit);
                   
    var inputArray = this.allClients;
    var result = inputArray.reduce((resultArray, item, index) =>  {
    const chunkIndex = Math.floor(index/perChunk)
        if(!resultArray[chunkIndex]){
            resultArray[chunkIndex] = []
        }
        resultArray[chunkIndex].push(item)
        return resultArray
    },[])
    this.paginatedClientData=result;
    this.interactionRec = this.paginatedClientData[parseInt(this.selectedPage)-1];
}
    handleNext(){
        if(!this.isLast)
        this.selectedPage=(parseInt(this.selectedPage)+1).toString();
        this.interactionRec=this.paginatedClientData[parseInt(this.selectedPage)-1];
        this.validatePagination();
    }

    handlePrev()
    {
        if(!this.isFirst)
            this.selectedPage=(parseInt(this.selectedPage)-1).toString();
        this.interactionRec=this.paginatedClientData[parseInt(this.selectedPage)-1];
        this.validatePagination();
    }

    handleFirst()
    {
        this.selectedPage='1';
        this.isFirst=true;
        this.isLast=false;
        this.interactionRec=this.paginatedClientData[parseInt(this.selectedPage)-1];
        this.validatePagination();
    }

    handleLast()
    {
        this.selectedPage=this.totalPages.toString();
        this.isFirst=false;
        this.isLast=true;
        this.interactionRec=this.paginatedClientData[parseInt(this.selectedPage)-1];
        this.validatePagination();
    }

    validatePagination()
    {
        if(parseInt(this.totalPages) == 1){
            this.isFirst=true;
            this.isLast=true;
        }
        else if(parseInt(this.selectedPage) == 1)
        {
            this.isFirst=true;
            this.isLast=false;
        }
        else if(parseInt(this.selectedPage) == parseInt(this.totalPages))
        {
            this.isFirst=false;
            this.isLast=true;
        }
        else
        {
            this.isFirst=false;
            this.isLast=false;
        }
        }
}