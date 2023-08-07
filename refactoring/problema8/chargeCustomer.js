class Customer {
  constructor(name, type) {
    this.name = name;
    this.type = type;
  }
}

function chargeCustomer(customer, usage) {
  cost = calculateCost(usage, customer.type==='premium');
  console.log(`Customer: ${customer.name}, Cost: ${cost}`);
}

function calculateCost(usage, isPremium) {
  let baseCost = baseCharge(usage);
  if (isPremium) {
    return baseCost*0.95;
  }
  else {
    return baseCost;
  }
}

function baseCharge(usage) {
  if (usage < 0) return 0;
  const amount =
        bottomBand(usage) * 0.03
        + middleBand(usage) * 0.05
        + topBand(usage) * 0.07;
  return amount;
}

function bottomBand(usage) {
  return Math.min(usage, 100);
}

function middleBand(usage) {
  return usage > 100 ? Math.min(usage, 200) - 100 : 0;
}

function topBand(usage) {
  return usage > 200 ? usage - 200 : 0;
}




const customer1 = new Customer('customer1', 'premium');
const customer2 = new Customer('customer2', 'regular');

chargeCustomer(customer1, 50);
chargeCustomer(customer1, 150);
chargeCustomer(customer2, 250);
chargeCustomer(customer2, 300);