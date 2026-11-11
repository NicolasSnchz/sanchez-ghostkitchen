/*
 * Mejoras progresivas: la aplicación funciona sin JavaScript; este archivo solo
 * agrega comodidad (cuenta regresiva para deshacer, ubicación y cuentas demo).
 */
(function () {
  "use strict";

  function dosDigitos(numero) {
    return numero < 10 ? "0" + numero : String(numero);
  }

  /* Cuenta regresiva de la ventana para deshacer (patrón Command). */
  function iniciarCuentaRegresiva() {
    var barra = document.querySelector("[data-deshacer]");
    if (!barra) {
      return;
    }
    var restantes = parseInt(barra.getAttribute("data-segundos"), 10);
    var texto = barra.querySelector("[data-deshacer-tiempo]");
    if (isNaN(restantes) || !texto) {
      return;
    }
    function pintar() {
      if (restantes <= 0) {
        var formulario = barra.querySelector("form");
        if (formulario) {
          formulario.remove();
        }
        texto.textContent = "El tiempo para deshacer esta acción terminó.";
        return false;
      }
      var minutos = Math.floor(restantes / 60);
      texto.textContent = "Puedes deshacerla durante " + minutos + ":" + dosDigitos(restantes % 60);
      return true;
    }
    if (pintar()) {
      var temporizador = setInterval(function () {
        restantes -= 1;
        if (!pintar()) {
          clearInterval(temporizador);
        }
      }, 1000);
    }
  }

  /* Botón "Usar mi ubicación" del explorador. Solo aparece si el navegador lo permite. */
  function iniciarUbicacion() {
    var boton = document.querySelector("[data-ubicarme]");
    var formulario = document.querySelector("[data-filtros]");
    if (!boton || !formulario || !("geolocation" in navigator) || !window.isSecureContext) {
      return;
    }
    var estado = formulario.querySelector("[data-ubicacion-estado]");
    boton.hidden = false;
    boton.addEventListener("click", function () {
      boton.disabled = true;
      estado.textContent = "Buscando tu ubicación...";
      navigator.geolocation.getCurrentPosition(function (posicion) {
        formulario.querySelector("[name=lat]").value = posicion.coords.latitude.toFixed(5);
        formulario.querySelector("[name=lng]").value = posicion.coords.longitude.toFixed(5);
        formulario.querySelector("[name=orden]").value = "CERCANIA";
        formulario.submit();
      }, function () {
        boton.disabled = false;
        estado.textContent = "No pudimos obtener tu ubicación. Revisa el permiso del navegador; mientras tanto, las distancias se miden desde el Parque Santander.";
      }, { enableHighAccuracy: false, timeout: 10000, maximumAge: 300000 });
    });
  }

  /* Botones de las cuentas de demostración en la pantalla de ingreso. */
  function iniciarCuentasDemo() {
    var formulario = document.querySelector("[data-ingreso]");
    if (!formulario) {
      return;
    }
    document.querySelectorAll("[data-usar-cuenta]").forEach(function (boton) {
      boton.hidden = false;
      boton.addEventListener("click", function () {
        formulario.querySelector("#correo").value = boton.getAttribute("data-correo");
        formulario.querySelector("#clave").value = boton.getAttribute("data-clave");
        formulario.requestSubmit ? formulario.requestSubmit() : formulario.submit();
      });
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    iniciarCuentaRegresiva();
    iniciarUbicacion();
    iniciarCuentasDemo();
  });
})();
