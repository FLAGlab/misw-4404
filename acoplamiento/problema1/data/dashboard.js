class Dashboard {
  constructor(motor) {
    this.motor = motor;
  }

  imprimirDashboard() {
    console.log('--------------------------------');
    console.log('DASHBOARD:');
    console.log(`RPM: ${this.motor.rpm}`);
    console.log(`Velocidad: ${this.motor.velocidad}`);
    console.log(`Nivel de aceite: ${this.motor.nivelAceite}`);
    console.log(`Nivel de gasolina: ${this.motor.nivelGasolina}`);
  }
}

module.exports = Dashboard;