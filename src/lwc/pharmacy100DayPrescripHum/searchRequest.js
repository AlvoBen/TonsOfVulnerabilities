export class searchRequestDTO {
    constructor(memberPlan) {
        this.SearchRequest = new PlanDTO(memberPlan);
    }
}

class PlanDTO {
    constructor(memberPlan) {
        this.PlanID = `${memberPlan?.Plan?.Contract_Number__c ?? ''}-${memberPlan?.Plan?.PBP_Code__c ?? ''}-${memberPlan?.Plan?.Medicare_Segment_ID__c ?? ''}-${this.getYear()}`;
    }

    getYear() {
        let formatOptions = {
            year: "numeric",
            timeZone: "EST"
        }
        return new Intl.DateTimeFormat('en-US', formatOptions).format(new Date());
    }
}