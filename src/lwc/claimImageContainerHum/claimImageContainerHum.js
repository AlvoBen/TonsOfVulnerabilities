import { LightningElement,wire,api,track } from 'lwc';
import initiateRequest from '@salesforce/apexContinuation/ClaimImage_LC_HUM.initiateRequest';
import { CurrentPageReference, NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import ClaimsSummary_Service_Error from '@salesforce/label/c.ClaimsSummary_Service_Error';



export default class ClaimImageContainerHum extends LightningElement {
    @track pageRef;
    @track resp;
    @track recordId;
    @track ClaimNumber;
    @track noResult=false;
	@track singleImage=false;
    @track noResponse;
    @track isLoading=true;
    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference?currentPageReference.attributes?currentPageReference.attributes.attributes:'':'';
        this.ClaimNumber = this.pageRef.ClaimNumber;
        this.recordId=this.pageRef.Id;
    }    
    connectedCallback(){
        this.init();
    }
    
    init(){
        initiateRequest({ClaimNumber:this.pageRef.ClaimNumber,Platform:this.pageRef.Platform,StartDate:this.pageRef.ServiceStartDate,SrcLvCASPrefix:this.pageRef.SrcLvCASPrefix})
            .then((result) => {
                if(result=='No Claim Image to Display'){
                   this.noResult=true;
                   this.isLoading=false;
                }else if(result==ClaimsSummary_Service_Error){
                    this.showToast(
                        '',
                        result,
                        'warning',
                        'sticky'
                    );
                    this.noResult=true;
                   this.isLoading=false;
                }else{
                    this.resp=JSON.parse(JSON.stringify(result));
                    if(this.resp.length ==1){
                        window.open(this.resp[0].sClaimURL, '_blank').focus();
						this.singleImage=true;
                    }
                    this.isLoading=false;
                }
                
                
            })
            .catch((error) => {
                console.log('error==>',error);
            });
    }

    showToast(strTitle, strMessage, strStyle, strMode) {
        this.dispatchEvent(
            new ShowToastEvent({
                title: strTitle,
                message: strMessage,
                variant: strStyle,
                mode: strMode
            })
        );
    }

}