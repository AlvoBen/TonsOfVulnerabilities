/*Component        : genericYearCmpHum.html
Version          : 1.0
Created On       : 08/11/2022
Function         : Component to display to benefit Snapshot information
* Developer Name                       Date                         Description
* Nirmal Garg                      08/11/2022                     Initial Version        
* Aishwarya Pawar                  02/13/2022                     DF- 7136 fix
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api } from 'lwc';

export default class GenericYearCmpHum extends LightningElement {
    @api year='';
    
    handleInputChange(event){
        this.year = event.detail.value;
        let reg = new RegExp('^[0-9]*$');
        if(reg.test(this.year) === false){
            this.year = this.year.length > 0 ? this.year.substring(0,this.year.length-1) : '';
            if(this.template.querySelector('lightning-input') != null){
                this.template.querySelector('lightning-input').value = this.year
            }
        }
    }
	
	
	@api
    validate(){
        if(this.year && this.year.length ==4){
            return {
                isValid:true
            }
        }else{
            return {
                isValid:false,
                errorMessage : 'You must enter year in correct YYYY format'
            }
        }
    }
}