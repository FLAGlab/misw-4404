const Motor = require('./motor');
const Dashboard = require('./dashboard');

class Carro {
  constructor(motor, dashboard) {
    this.motor = motor;
    this.dashboard = dashboard;
  }

  acelerar() {
    this.motor.rpm += 100;
    this.motor.velocidad += 10;
    this.motor.nivelAceite -= 0.1;
    this.motor.nivelGasolina -= 0.5;
  }

  frenar() {
    this.motor.rpm -= 0;
    this.motor.velocidad -= 0;
    this.motor.nivelAceite -= 0.1;
    this.motor.nivelGasolina -= 0;
  }
  
}


const motor = new Motor();
const dashboard = new Dashboard(motor);
const carro = new Carro(motor, dashboard);

dashboard.imprimirDashboard();
carro.acelerar();
dashboard.imprimirDashboard();
carro.frenar();
dashboard.imprimirDashboard();