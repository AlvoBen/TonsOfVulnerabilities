export class Network_DTO_HUM {
    constructor(customerId, ledgerNumber) {
        this.customerId = customerId ?? 1;
        this.ledgerNumber = ledgerNumber ?? '12272';
    }
}