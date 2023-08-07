function calc_promedio(clases) {
  let suma = 0;
  let creditos = 0;

  for (let i = 0; i < clases.length; i++) {
    suma += clases[i].nota * clases[i].creditos;
    creditos += clases[i].creditos;
  }

  const promedio = suma / creditos;
  let result = ""
  //todo cambiar los ifs
  if (promedio < 3) {
    result = "No aprobado";
    if (promedio == 1.5) {
      result += ", nota minima";
    }
    else {
      result += ", insuficiente";
    }
  }
  else {
    result = "Aprobado";
    if (promedio < 3.5) {
      result += ", suficiente";
    }
    else if (promedio < 4) {
      result += ", satisfactorio";
    }
    else if (promedio < 4.5) {
      result += ", bueno";
    }
    else {
      result += ", excelente";
    }
  }

  return result;
}

const clases = [
  { nombre: "Matematicas", creditos: 3, nota: 5.0 },
  { nombre: "Fisica", creditos: 3, nota: 4.5 },
  { nombre: "Quimica", creditos: 2, nota: 3.0 },
  { nombre: "Programacion", creditos: 3, nota: 5.0 },
]

console.log(calc_promedio(clases));
