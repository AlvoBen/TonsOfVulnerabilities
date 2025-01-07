import { LightningElement ,wire,track} from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';

export default class PharmacyAccountPolicyDetailHum extends  LightningElement 
{
    @track recId ;
	@track fName;
	@track lName;
	@track entId ;
	
    @track currentPageReference = null; 
    @track urlStateParameters = null;
    @track accID;

   @wire(CurrentPageReference)
   getStateParameters(currentPageReference) {
      if (currentPageReference) {
         this.urlStateParameters = currentPageReference.state;
         this.setParametersBasedOnUrl();
      }
   }

   setParametersBasedOnUrl() {
        this.recId = this.urlStateParameters.c__AccountID || null;
		this.fName = this.urlStateParameters.c__fName || null;
        this.lName = this.urlStateParameters.c__lName || null;
		this.entId = this.urlStateParameters.c__enterpriceID || null;
    }

}