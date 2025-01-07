import { ClaimDetails,denClaimDetails} from './claimsTableModels';

const getValue = (record, mapping) => {
  let value;
     if(mapping.includes('.')){ 
    const mapDepth = mapping.split('.');
    value = record;
    mapDepth.forEach(item => {
      value = value ? value[item] : ''
    });
  }
  else{
    value = record[mapping];
  }
  return value;
}


export const getHelpText = (screenType, labels) => {
  let helpText = '';
  switch(screenType){
    case 'Member Search': 
      helpText = labels.searchResultsMemberHelptext;
    break;
    case 'Group Search': 
      helpText = labels.searchResultsHelptext;
    break;
    case 'providerResutls': 
      helpText = labels.searchResultsProviderHelptext;
    break;
    case 'agencyResults': 
      helpText = labels.searchResultsAgencyHelptext;
    break;
  }
  return helpText;
}

/**
 * Populates data into model and creates records
 * @param {*} model Model Array which needs to be used for data population
 * @param {*} dataArr Data Array
 */
export const populatedDataIntoModel = (model, dataArr) =>{
  let temp1 = [];
  let tempdarry = [];
  dataArr.forEach((dataRecord) => {
    model.forEach((modelRecord) => {
      
      temp1 = [];
      let rowCss = "";
      modelRecord.forEach((v) => {
        if(v.compoundx){
          v.compoundvalue.forEach((compv) => {           
            compv.value = getValue(dataRecord,compv.fieldName);
			if (compv.hasOwnProperty('link')) {
				compv.link = (dataRecord.hasOwnProperty(compv.hasOwnProperty('linkToChange') ? compv.linkToChange : '')) ? dataRecord[compv.linkToChange] : compv.link;
            }
            if(compv.customFunc === 'CASE_CREATION' && dataRecord.isOpenedInTwoWeeks){
              rowCss = 'results-row-highlight';
            } 
          });
        }
        else{
          if (v.fieldName) {
            v.value = getValue(dataRecord,v.fieldName);   
          }
        }
        if(v.radio){
          v.checked = dataRecord.checked;
          v.disabled = dataRecord.disabled;
        }
        else if(v.isViewAll){
          v.filteredList = dataRecord.filteredList;
          v.actualList = dataRecord.actualList;
          v.showViewAll = dataRecord.showViewAll;
        }   
      });
      let record = JSON.parse(JSON.stringify(modelRecord));
      record.rowCss = rowCss;
      
      temp1.push(record); 
    });   
    tempdarry.push(temp1);
  });

  return tempdarry;
}

/**
 * Returns meta data for the given table tpe
 * @param {*} selectedScreen 
 */
export const getTableModal = (selectedScreen) => {
  let model;
  switch(selectedScreen){
   
      case 'ClaimDetails':
      model = ClaimDetails;
      break;
	  case 'denClaimDetails':
      model = denClaimDetails;
      break;
      case 'Division Table':
        model = subGrpDivision;  
    default:''
  }
  return model;
}

export const compute = (selectedScreen, dataArr) => {
 return populatedDataIntoModel(getTableModal(selectedScreen), dataArr); 
};