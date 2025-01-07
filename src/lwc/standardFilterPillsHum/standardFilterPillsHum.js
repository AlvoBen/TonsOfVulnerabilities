import { LightningElement, api } from 'lwc';

export default class StandardFilterPillsHum extends LightningElement {
    @api filterValues; // Selected Filter Values. Format -> ['Active' , 'MED'];    
    
    @api filterConfigObj; // Original values with available filter options. 
        //Format -> {​​​​​status : ['Active' , 'Closed'] , product : ['MED' , 'DEN']}​​​​​

    handledRemove(event) {

        var indexToRemove = event.currentTarget.dataset.index;
        const objToRemove = this.filterValues[indexToRemove];
        let fltVals = JSON.parse(JSON.stringify(this.filterValues));
        fltVals.splice(indexToRemove, 1);
        this.filterValues = fltVals;
        let newFilterObject = Object.assign({},this.filterConfigObj);
        if( fltVals.length <= 0){
          this.fireEvent('noData'); 
          return;
        }
        else{
        const valueToRemove = newFilterObject[objToRemove.key];
        if(valueToRemove.length){
            newFilterObject[objToRemove.key] = valueToRemove.filter((val)=>{
                return val !== objToRemove.value;
            });
        }
        if(newFilterObject[objToRemove.key].length === 0){
            delete newFilterObject[objToRemove.key];
        }
        this.fireEvent(newFilterObject);       
    }
    }


    fireEvent(dataToFetch){
        const detail = { data: dataToFetch, pillList:this.filterValues};
        const removepill = new CustomEvent('removepill', { detail });
        this.dispatchEvent(removepill);
    }
}