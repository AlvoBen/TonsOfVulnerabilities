/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_Response_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This Js Class is to call service and get response from KMDocumentServiceREST DoSearch and to display results on page.
*******************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
import {
    LightningElement,
    track,
    api
} from 'lwc';

import getDataFromService from '@salesforce/apexContinuation/PharmacyRXAccessBenefits_C_HUM.callAccessBenefitMentorService';

export default class PharmacyRxAccessBenefitsLWC extends LightningElement {
    
    resultsVar = false;
    noResultsVar = false;
    isLoaded = false;
    @track pageNumbers = [];
    @api totalFileCount = 0;
    NoResultsLabel = '';
    NoResultsURL = '';
    totalResults = [];
    @api policyMemberIdVar;
	@api PolicyMemberNameVar;
	@api MentorHomeURLVar;
    results = [];
    displayErrorMessage = false;

    connectedCallback() {
        getDataFromService({
                policyMemberId: this.policyMemberIdVar
            })
            .then(data => {
                this.isLoaded = true;                
                if (data != null & data != undefined) {
                    this.results = data.KMDSearchResultsWrapper.Documents;

                    //this.results = null;
                    if (this.results != null && this.results != undefined && this.results.length > 0) {
                        this.resultsVar = true;
                        this.noResultsVar = false;
                        let tempArray = []
                        if (this.results.length == 1) {
                            this.totalFileCount = 1;
                            this.dispatchEvent(new CustomEvent('openfile', {
                                detail: {
                                    data: this.totalFileCount,
                                    url: this.results[0].FileUrl                                   
                                },
                                bubbles: true,
                                composed: true,
                            }))
                        } else {
                            this.totalFileCount = this.results.length;
                            for (let j = 0; j < this.recordsPerPage; j++) {
                                if (this.results[j] != null)
                                    tempArray.push(this.results[j]);
                            }
                            this.totalResults = tempArray;
                            this.ServiceTotalResults = this.results;
                        }
                        this.showPagination(this.recordsPerPage, this.results.length);
                    } else {
                        this.setErrorMessage('InformationMessage');
                    }
                } else {
                    this.setErrorMessage('ErrorMessage');;
                }
            })
            .catch(error => {
                if (error != null && error != undefined) {
                    this.displayErrorMessage = true;
                    this.resultsVar = false;
                    this.noResultsVar = false;
                }
            });
    }

    setErrorMessage(msg) {
        if(msg === 'ErrorMessage'){
            this.noResultsVar = false;
            this.displayErrorMessage = true; 
        }            
        else if(msg === 'InformationMessage') {
            this.noResultsVar = true;  
            this.displayErrorMessage = false; 
        }
               
        this.isLoaded = true;        
        this.resultsVar = false;       
        this.totalFileCount = 0;
        this.NoResultsURL = this.MentorHomeURLVar;
        this.NoResultsLabel = "Take Me to Compass";
    }

    //New Pagination
    //Number of Records to display in table based on the entries Functionality -- START -- //
    @track recordsPerPage;
    @track pageNumber;
    recordsPerPage = '10';

    get comboBoxOption() {
        return [
            {
                label: '10',
                value: '10'
            },
            {
                label: '25',
                value: '25'
            },
            {
                label: '50',
                value: '50'
            },
            {
                label: '75',
                value: '75'
            },
            {
                label: '100',
                value: '100'
            }
        ];
    }
    //handle record display based combobox value change
    handleComboBoxChange(event) {
        const comboBoxValue = event.target.value;
        var allRecords;
        
            allRecords = this.ServiceTotalResults;
        var tempArray = [];
        this.pageNumber = 1;
        this.recordsPerPage = comboBoxValue;
        for (let i = 0; i < this.recordsPerPage; i++) {
            if (allRecords.length > i) {
                tempArray.push(allRecords[i]);
            }
        }
        this.totalResults = tempArray;

        //Pagination -- on load showing data           
        this.showPagination(this.recordsPerPage, allRecords.length);
    }


    //Pagination -- on PAge Load Functionality -- START -- //
    showPagination(recordsPerPageVar, tempData) {
        this.totalRecordCount = tempData;
        this.page = 1;
        this.pageSize = recordsPerPageVar;
        //this.totalPage = 1;
        this.totalPage = Math.ceil(this.totalRecordCount / recordsPerPageVar);
        this.startingRecord = (this.totalRecordCount > 0) ? 1 : 0;
        this.startingRecord1 = this.startingRecord;
        this.endingRecord = recordsPerPageVar; // based on comboBox default option

        this.endingRecord = (this.endingRecord > this.totalRecordCount) ?
            this.totalRecordCount : this.endingRecord;
        //initialize button properties
        this.isFirstButton = false;
        this.isSecondButton = false;
        this.isThirdButton = false;

        this.changeVariantPrevious = 'Neutral';
        this.changeVariantNext = 'Neutral';
        this.changeVariantFirst = 'Neutral';
        this.changeVariantSecond = 'Neutral';
        this.changeVariantThird = 'Neutral';
        // to disable the Previous or Next button on page load based on the records
        if (this.totalPage === 1) {
            this.isFirstPage = true; //previous button disabled
            this.isLastPage = true; //next button disabled
            this.changeVariantPrevious = 'Neutral';
            this.changeVariantNext = 'Neutral';
            this.isFirstButton = true; //1 button visible 
            this.changeVariantFirst = 'brand'; //'1' button showing as brand 
            this.isSecondButton = false; // '2' button is hide
            this.isThirdButton = false; // '3' button is hide            
        } else if (this.totalPage > 1) {
            this.isFirstPage = true; //previous button disabled
            this.changeVariantPrevious = 'Neutral';
            this.isLastPage = false; //next button is enabled            

            this.isFirstButton = true; //1 button visible 
            this.changeVariantFirst = 'brand'; //'1' button showing as brand 
            this.showButtonFirst = '1';

            if (this.totalPage >= 2) {
                this.isSecondButton = true;
                this.showButtonSecond = '2';
            }

            if (this.totalPage >= 3) {
                this.isThirdButton = true;
                this.showButtonThird = '3';
            }
        }
    }
    //New Pagination -- on PAge Load Functionality -- END -- //
    
    //New Pagination Functionality -- START -- //
    @track page = 1; //this is initialize for 1st page
    @track pages = [];
    @track startingRecord = 1; //start record position per page
    @track startingRecord1 = 1;
    @track endingRecord = 0; //end record position per page
    @track pageSize = this.recordsPerPage; //default value we are assigning
    @track totalRecordCount = 0; //total record count received from all retrieved records
    @track totalPage = 0; //total number of page is needed to display all records
    @track items = []; //it contains all the records.
    showButtonFirst = '1';
    showButtonSecond = '2';
    showButtonThird = '3';
    firstButtonClick = false;
    secondButtonClick = false;
    thirdButtonClick = false;
    next = false;
    previous = false;

    i = 1;
    isFirstButton = false;
    isSecondButton = false;
    isThirdButton = false;

    changeVariantFirst = 'Neutral';
    changeVariantSecond = 'Neutral';
    changeVariantThird = 'Neutral';

    handleNextPage() {
        if ((this.page < this.totalPage) && this.page !== this.totalPage) {
            this.next = true;
            this.page = this.page + 1; //increase page by 1
            this.isLastPage = false;
            this.isFirstPage = false;
            this.setButtonProperties('next');
            this.displayRecordPerPage(this.page);
        }
    }

    handlePreviousPage() {
        if (this.page > 1) {
            this.previous = true;
            this.page = this.page - 1;
            this.isLastPage = false;
            this.isFirstPage = false;

            this.setButtonProperties('previous');
            this.displayRecordPerPage(this.page);
        }
    }

    setButtonProperties(previousORnext) {
        this.changeVariantPrevious = 'Neutral';
        this.changeVariantNext = 'Neutral';
        let first = parseInt(this.showButtonFirst, 10);
        let second = parseInt(this.showButtonSecond, 10);
        let third = parseInt(this.showButtonThird, 10);

        this.isFirstButton = true;
        this.isSecondButton = true;
        this.isThirdButton = true;

        if (previousORnext === 'previous') {
            this.changeVariantPrevious = 'brand';
            if (this.totalPage >= 1 && this.totalPage <= 3) {
                if (this.page === 1) {
                    this.changeVariantFirst = 'brand';
                    this.changeVariantSecond = 'Neutral';
                    this.changeVariantThird = 'Neutral';
                } else if (this.page === 2) {
                    this.changeVariantFirst = 'Neutral';
                    this.changeVariantSecond = 'brand';
                    this.changeVariantThird = 'Neutral';
                }
                //For Total pages == 2
                if (this.totalPage === 2) // for 2 page
                {
                    this.isThirdButton = false;
                }
            } else if (this.totalPage > 3) // for equal to or more than 3 pages
            {
                if (this.page === 1) {
                    if (this.changeVariantSecond === 'brand')
                        this.i = 0;
                    this.changeVariantFirst = 'brand';
                    this.changeVariantSecond = 'Neutral';
                    this.changeVariantThird = 'Neutral';
                } else if (this.page === 2) {
                    if (this.changeVariantSecond === 'brand') this.i = 1;
                    else if (this.changeVariantThird === 'brand') this.i = 0;
                    if (this.changeVariantFirst === 'brand')
                        this.changeVariantFirst = 'brand';
                    else {
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantSecond = 'brand';
                        this.changeVariantThird = 'Neutral';
                    }
                } else {
                    this.i = 1;
                    if (this.changeVariantFirst === 'brand') {
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if (this.changeVariantSecond === 'brand') {
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else {
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'brand';
                    }
                }
                this.showButtonFirst = first - this.i;
                this.showButtonSecond = second - this.i;
                this.showButtonThird = third - this.i;
            }
        }

        if (previousORnext === 'next') {
            this.changeVariantNext = 'brand';
            if (this.totalPage >= 1) {
                if (this.totalPage === 2) // for 2 page
                {
                    this.isThirdButton = false;

                    if (this.page === 1) {
                        this.changeVariantFirst = 'brand';
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if (this.page === 2) {
                        this.changeVariantSecond = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    }
                } else if (this.totalPage === 3) // for 3 page
                {
                    if (this.page === 1) {
                        this.changeVariantFirst = 'brand';
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if (this.page === 2) {
                        this.changeVariantSecond = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else {
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'brand';
                    }
                } else if (this.totalPage > 3) // for more than 3 pages
                {
                    if ((this.totalPage - this.page) === 1 && this.changeVariantFirst === 'brand') {
                        this.i = 0;
                        this.changeVariantSecond = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if ((this.totalPage - this.page) === 1 && this.changeVariantSecond === 'brand') {
                        this.i = 1;
                    } else if ((this.totalPage - this.page) === 0 && this.changeVariantSecond === 'brand') {
                        this.i = 0;
                        this.changeVariantThird = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantSecond = 'Neutral';
                    } else {
                        this.i = 1;
                        if (this.changeVariantFirst === 'brand') {
                            this.changeVariantSecond = 'Neutral';
                            this.changeVariantThird = 'Neutral';
                        } else if (this.changeVariantSecond === 'brand') {
                            this.changeVariantFirst = 'Neutral';
                            this.changeVariantThird = 'Neutral';
                        } else if (this.changeVariantThird === 'brand') {
                            this.changeVariantFirst = 'Neutral';
                            this.changeVariantSecond = 'Neutral';
                        }
                    }

                    this.showButtonFirst = first + this.i;
                    this.showButtonSecond = second + this.i;
                    this.showButtonThird = third + this.i;
                }
            }
        }
    }

    showCurrentPageData(event) {
        if (this.totalPage === 1 && event.target.title === 'first') {
            this.page = 1;
            this.isLastPage = true;
            this.isFirstPage = true;
            this.changeVariantFirst = 'brand';
            this.changeVariantSecond = 'Neutral';
            this.changeVariantThird = 'Neutral';
        } else if (this.totalPage === 2) {
            if (event.target.title === 'first') {
                this.page = 1;
                this.changeVariantFirst = 'brand';
                this.changeVariantSecond = 'Neutral';
            } else if (event.target.title === 'second') {
                if (this.changeVariantFirst === 'brand' || this.changeVariantPrevious === 'brand')
                    this.page = this.page + 1;
                this.changeVariantFirst = 'Neutral';
                this.changeVariantSecond = 'brand';
            }
            this.changeVariantPrevious = 'Neutral';
            this.changeVariantNext = 'Neutral';
        } else if (this.totalPage >= 3) {
            if (event.target.title === 'first') {
                if (this.totalPage === 3)
                    this.page = 1;
                else if (this.changeVariantThird === 'brand' || this.changeVariantNext === 'brand')
                    this.page = this.page - 2;
                else if (this.changeVariantSecond === 'brand')
                    this.page = this.page - 1;

                this.changeVariantFirst = 'brand';
                this.changeVariantSecond = 'Neutral';
                this.changeVariantThird = 'Neutral';
            } else if (event.target.title === 'second') {
                if (this.changeVariantFirst === 'brand')
                    this.page = this.page + 1;
                else if (this.changeVariantThird === 'brand' || this.changeVariantNext === 'brand')
                    this.page = this.page - 1;
                else if (this.changeVariantPrevious === 'brand')
                    this.page = this.page + 2;

                this.isFirstPage = false;
                this.isLastPage = false;
                this.changeVariantFirst = 'Neutral';
                this.changeVariantSecond = 'brand';
                this.changeVariantThird = 'Neutral';
            } else if (event.target.title === 'third') {
                this.thirdButtonClick = true;
                if (this.changeVariantFirst === 'brand' || this.changeVariantPrevious === 'brand')
                    this.page = this.page + 2;
                else if (this.changeVariantSecond === 'brand')
                    this.page = this.page + 1;
                this.changeVariantFirst = 'Neutral';
                this.changeVariantSecond = 'Neutral';
                this.changeVariantThird = 'brand';
            }
            this.changeVariantPrevious = 'Neutral';
            this.changeVariantNext = 'Neutral';
        }
        this.displayRecordPerPage(this.page);
    }

    //this method displays records page by page
    displayRecordPerPage(page) {
        var allRecords;
        
            allRecords = this.ServiceTotalResults
        this.items = allRecords;
        this.totalRecordCount = allRecords.length;
        
        this.startingRecord1 = ((page - 1) * this.pageSize) + 1;
        this.startingRecord = ((page - 1) * this.pageSize);      
        this.endingRecord = (this.pageSize * page);

        this.endingRecord = (this.endingRecord > this.totalRecordCount) ?
            this.totalRecordCount : this.endingRecord;

        if (this.page === this.totalPage) {
            this.isLastPage = true;
            this.changeVariantNext = 'Neutral';
            this.isFirstPage = false;
        }
        if (this.page === 1) {
            this.showButtonFirst = '1';
            this.showButtonSecond = '2';
            this.showButtonThird = '3';
            this.isFirstPage = true;
            this.changeVariantPrevious = 'Neutral';
            this.isLastPage = false;
        }
        this.totalResults = this.items.slice(this.startingRecord, this.endingRecord);
    }
    //New Pagination Functionality -- END -- // 
}