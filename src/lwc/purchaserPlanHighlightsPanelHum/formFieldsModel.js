const purhaserPlan = {header: { name: "Plan", icon: { size: "large", name: "standard:account" } },
recordDetail: [
    { label: "Benefit Package ID", mapping: "benefitPackage" },
    { label: "Product - Product Type", mapping: "product-productType",  seperator:"-"},
    { label: "Plan Status", mapping: "planStatus" },
    { label: "Effective - End Date", mapping: "effectiveFrom-effectiveTo", bDate: true, seperator:"-"}
],
actions: []
}

export const getModal = () => {
    
    return purhaserPlan;
}