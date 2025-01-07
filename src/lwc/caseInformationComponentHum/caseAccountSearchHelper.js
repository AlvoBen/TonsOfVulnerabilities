export function showAccSearchPopUpModal(me){
        me.accSearchHistory['sField'] = 'IntWith';
        me.selectedField = 'IntWith';
        if(me.showWithClear){
            me.showAccSearch = false;
        }else{
            me.showAccSearch = true;
        }
    }

export function showAccAboutSearchPopUpModal(me){
    me.accSearchHistory['sField'] = 'IntAbout'
    me.selectedField = 'IntAbout';
    if(me.showAboutClear){
        me.showAccAboutSearch = false;
    }else{
        me.showAccAboutSearch = true;
    }
}

export function handleAccountSelection(event,me){
    me.accSearchHistory = {};
    if(event.detail.formData?.sField == 'IntAbout'){
        me.interactingAboutObj.sInteractingAboutName = event.detail.accName;
        me.interactingAboutObj.sInteractingAboutId = event.detail.accId;
        
        me.case.AccountId = event.detail.accId;
        me.case.NPI_ID__c = event.detail.sNPIId;
        me.case.Tax_ID__c = event.detail.sTaxId;
        me.showAboutClear = true;
        const objAboutEle = me.template.querySelector('.accNameLkp');
        objAboutEle.value = event.detail.accName;
        objAboutEle.setCustomValidity("");
        objAboutEle?.reportValidity();
    }else if(event.detail.formData?.sField == 'IntWith'){
        me.interactingWithObj.sInteractingWithName = event.detail.accName;
        me.interactingWithObj.sInteractingWithId = event.detail.accId;
        me.case.Interacting_With__c = event.detail.accId;
        me.showWithClear = true;
    }
    me.accSearchHistory = event.detail.formData;
    me.showAccSearch = false;
    me.showAccAboutSearch = false;
}

export function handleAccSearchPopupClose(me){
    if(me.bInteractingWithMember){
        if(me.template.querySelector("c-member-search-case-lookup-hum") !=
                undefined &&
                me.template.querySelector("c-member-search-case-lookup-hum") != null
            ) {
            me.accSearchHistory = me.template
                .querySelector("c-member-search-case-lookup-hum")
                .accountSearchHistory;
        }
    }
    me.showAccSearch = false;
}

export function handleAccAboutSearchPopupClose(me){
    if(me.bInteractingAboutMember){
        if(me.template.querySelector("c-member-search-case-lookup-hum") !=
                undefined &&
                me.template.querySelector("c-member-search-case-lookup-hum") != null
            ) {
            me.accSearchHistory = me.template
                .querySelector("c-member-search-case-lookup-hum")
                .accountSearchHistory;
        }
    }     
    me.showAccAboutSearch = false;
}

export function handleInteractingWithClear(me){
    me.interactingWithObj.sInteractingWithId = ''
    me.interactingWithObj.sInteractingWithName = '';
    me.case.Interacting_With__c = null;
    me.showWithClear = false;
}

export function handleInteractingAboutClear(me){
    me.interactingAboutObj.sInteractingAboutId = ''
    me.interactingAboutObj.sInteractingAboutName = '';
    me.showAboutClear = false;
}

export function handleInteractingWithTypeChange(event,me){
    if(me.switch_4884468){
        me.bInteractingWithMember = false;
        me.bInteractingWithProvider = false;
        me.bNonMemberOrProvider = true;
        if(event.target.value == 'Member' || event.target.value == 'Unknown-Member'){
            me.bInteractingWithMember = true;
            me.bInteractingWithProvider = false;
            me.bNonMemberOrProvider = false
        }
        if(event.target.value == 'Provider' || event.target.value == 'Unknown-Provider'){
            me.bInteractingWithProvider = true;
            me.bInteractingWithMember = false;
            me.bNonMemberOrProvider = false
        }
    }else me.bNonMemberOrProvider = true;
}

export function handleInteractingAboutTypeChange(event,me){
    me.bAccountNameCustomLookUp = false;
    if(me.switch_4884468){
        if(event.target.value == 'Member' || event.target.value == 'Unknown-Member'){ 
            me.bAccountNameCustomLookUp = true;
            me.bInteractingAboutMember = true;
            me.bInteractingAboutProvider = false;
        }
        if(event.target.value == 'Provider' || event.target.value == 'Unknown-Provider'){
            me.bAccountNameCustomLookUp = true;
            me.bInteractingAboutProvider = true;
            me.bInteractingAboutMember = false;
        }
    }
}

export function doInitialSetup(result,me){
    if(me.switch_4884468){
        me.bInteractingWithMember = (result?.objCase.Interacting_With_Type__c == 'Member' || result?.objCase.Interacting_With_Type__c == 'Unknown-Member') ? true :false;
        me.bInteractingWithProvider = (result?.objCase.Interacting_With_Type__c == 'Provider' || result?.objCase.Interacting_With_Type__c == 'Unknown-Provider') ? true :false;
        me.bInteractingAboutMember = (result?.objCase.Interacting_About_Type__c == 'Member' || result?.objCase.Interacting_About_Type__c == 'Unknown-Member') ? true :false;
        me.bInteractingAboutProvider = (result?.objCase.Interacting_About_Type__c == 'Provider' || result?.objCase.Interacting_About_Type__c == 'Unknown-Provider') ? true :false;
        me.interactingWithObj.sInteractingWithName = result?.additionalInfo?.interactingWithAccName ?? null;
        me.interactingWithObj.sInteractingWithId = result?.objCase?.Interacting_With__c;
        me.interactingAboutObj.sInteractingAboutName = result?.additionalInfo?.interactingAboveAccName ?? null;
        me.interactingAboutObj.sInteractingAboutId = result?.objCase?.AccountId ?? null;
    
        if(me.bInteractingWithMember === false && me.bInteractingWithProvider === false){
            me.bNonMemberOrProvider = true
        }

        if(me.bInteractingAboutMember === true || me.bInteractingAboutProvider === true){
            me.bAccountNameCustomLookUp = true
        }
    
        if(me.interactingWithObj.sInteractingWithName) me.showWithClear = true;
        if(me.interactingAboutObj.sInteractingAboutName) me.showAboutClear = true;
    }else{
        me.bNonMemberOrProvider = true;
        me.bAccountNameCustomLookUp = false;
    }
}

export function handleAboutChange(event,me){
    const closeEle = me.template.querySelector('.closeIcon');
    const searchEle = me.template.querySelector('.searchIcon');
    if(event.target.checkValidity()){
        if(closeEle && !closeEle.classList.contains('align-icons')) closeEle.classList.add('align-icons');
        if(searchEle && !searchEle.classList.contains('align-icons')) searchEle.classList.add('align-icons');
    } 
    else {
        if(closeEle) closeEle.classList.remove('align-icons');
        if(searchEle) searchEle.classList.remove('align-icons');
    }
}