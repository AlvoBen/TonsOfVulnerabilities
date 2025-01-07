export class providersearchrequest {
    constructor(customerId, zip, zipPlus, networkId, isFacet, distance, SelectLocation, pcpproductmarket, pcpplan, pcpeffectivedate, pcpoption, pcpvalidationtype, memberdob, searchCategory, searchText, offset, limit, providertypeId, searchMatchType) {
        this.baseSearch = new BaseSearch(customerId, zip, zipPlus, networkId, isFacet, distance, SelectLocation, pcpproductmarket, pcpplan, pcpeffectivedate, pcpoption, pcpvalidationtype, memberdob, searchCategory, searchText, offset, limit, providertypeId, searchMatchType);
        this.pagination = new pagination(offset, limit);
    }
}

export class BaseSearch {
    constructor(customerId, zip, zipPlus, networkId, isFacet, distance, SelectLocation, pcpproductmarket, pcpplan, pcpeffectivedate, pcpoption, pcpvalidationtype, memberdob, searchCategory, searchText, providertypeId, searchMatchType) {
        this.customerId = customerId ?? 1;
        this.zip = zip ?? '';
        this.zipPlus = zipPlus ?? '';
        this.coverage = new coverage(networkId);
        this.generalFilter = new Generalfilter(isFacet, distance, SelectLocation, pcpproductmarket, pcpplan, pcpeffectivedate, pcpoption, pcpvalidationtype, memberdob, providertypeId, searchMatchType);
        this.searchCriteria = new searchCriteria(searchCategory, searchText);
    }
}

export class Generalfilter {
    constructor(isFacet, distance, SelectLocation, pcpproductmarket, pcpplan, pcpeffectivedate, pcpoption, pcpvalidationtype, memberdob, providertypeId, searchMatchType) {
        this.isFacet = isFacet ?? false;
        this.distance = distance ?? '';
        this.SelectLocation = SelectLocation ?? 'distance';
        this.searchMatchType = searchMatchType ?? 'Contains';
        this.PrimaryCareRequest = new primarycarerequest(pcpproductmarket, pcpplan, pcpeffectivedate, pcpoption, pcpvalidationtype, memberdob);
        this.providerTypeId = providertypeId ?? 1;
    }
}

export class primarycarerequest {
    constructor(pcpproductmarket, pcpplan, pcpeffectivedate, pcpoption, pcpvalidationtype, memberdob) {
        this.PcpProductMarket = parseInt(pcpproductmarket) ?? 33228;
        this.PcpPlan = pcpplan ?? '076';
        this.PcpEffectiveDate = pcpeffectivedate ?? `${String(new Date().getFullYear())}-${String(new Date().getMonth() + 1).padStart(2, '0')}-${String(new Date().getDate()).padStart(2, '0')}`;
        this.PcpOption = pcpoption ?? '736';
        this.pcpValidationType = pcpvalidationtype ?? 'Full';
        this.MemberBirthDate = memberdob ?? '2000-11-01';
    }
}

export class searchCriteria {
    constructor(searchCategory, searchText) {
        this.searchCategory = searchCategory ?? '2';
        this.searchText = searchText ?? 'All Primary Care Physician Specialties';
    }
}

export class pagination {
    constructor(offset, limit) {
        this.offset = offset ?? 1;
        this.limit = limit ?? 50;
    }
}

export class coverage {
    constructor(networkId) {
        this.networkId = networkId ?? 121;
    }
}