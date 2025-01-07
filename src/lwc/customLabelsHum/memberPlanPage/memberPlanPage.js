import Hum_NoResultsFound from '@salesforce/label/c.Hum_NoResultsFound';
import crmToastError from '@salesforce/label/c.crmToastError';
import Enroll_Program_HUM from '@salesforce/label/c.Enroll_Program_HUM';
import Available_Program_HUM from '@salesforce/label/c.Available_Program_HUM';
import HUM_FilterByWord from '@salesforce/label/c.HUM_FilterByWord';
import HUM_Member from '@salesforce/label/c.HUM_Member';
import HUM_Purchaser from '@salesforce/label/c.HUM_Purchaser';



export const getmemPlanLabels = ()=>{
    let labels = {
        Hum_NoResultsFound,
        crmToastError,
        Enroll_Program_HUM,
        Available_Program_HUM,
        HUM_FilterByWord,
        HUM_Member,
        HUM_Purchaser 
    }

    return labels;
}