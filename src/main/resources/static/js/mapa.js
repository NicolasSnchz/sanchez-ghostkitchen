/*
 * Mapa de cocinas con Leaflet. Lee las cocinas de los atributos data-* que ya
 * están en la página, así el mapa y la lista siempre muestran lo mismo.
 */
(function () {
  "use strict";

  var TESELAS = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png";
  var ATRIBUCION = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> " +
      "&copy; <a href=\"https://carto.com/attributions\">CARTO</a>";

  function icono(clases) {
    return L.divIcon({
      className: "marcador-envoltura",
      html: "<span class=\"" + clases + "\"></span>",
      iconSize: [26, 26],
      iconAnchor: [13, 30],
      popupAnchor: [0, -28]
    });
  }

  function clasesDe(elemento) {
    var clases = "marcador";
    if (elemento.getAttribute("data-abierta") !== "true") {
      clases += " marcador--cerrada";
    } else if (elemento.getAttribute("data-rareza") === "LEGENDARIA") {
      clases += " marcador--legendaria";
    }
    return clases;
  }

  function contenidoPopup(elemento) {
    var caja = document.createElement("div");
    var nombre = elemento.getAttribute("data-nombre");
    var url = elemento.getAttribute("data-url");
    var titulo;
    if (url) {
      titulo = document.createElement("a");
      titulo.href = url;
    } else {
      titulo = document.createElement("strong");
    }
    titulo.textContent = nombre;
    caja.appendChild(titulo);
    var estado = document.createElement("div");
    estado.textContent = elemento.getAttribute("data-abierta") === "true" ? "Abierta ahora" : "Cerrada ahora";
    caja.appendChild(estado);
    return caja;
  }

  function iniciar() {
    var contenedor = document.querySelector("[data-mapa]");
    if (!contenedor || typeof L === "undefined") {
      return;
    }
    var referencia = [parseFloat(contenedor.getAttribute("data-ref-lat")),
      parseFloat(contenedor.getAttribute("data-ref-lng"))];
    var mapa = L.map(contenedor, { scrollWheelZoom: false }).setView(referencia, 14);
    L.tileLayer(TESELAS, { subdomains: "abcd", maxZoom: 19, attribution: ATRIBUCION }).addTo(mapa);

    var puntos = [];
    var cocinas = document.querySelectorAll("[data-cocina]");
    cocinas.forEach(function (elemento) {
      var posicion = [parseFloat(elemento.getAttribute("data-lat")), parseFloat(elemento.getAttribute("data-lng"))];
      var clases = clasesDe(elemento);
      var marcador = L.marker(posicion, { icon: icono(clases), title: elemento.getAttribute("data-nombre"), keyboard: true })
          .addTo(mapa)
          .bindPopup(contenidoPopup(elemento));
      puntos.push(posicion);
      if (elemento.tagName === "LI") {
        var resaltar = function (activo) {
          elemento.classList.toggle("es-activa", activo);
          marcador.setIcon(icono(activo ? clases + " marcador--activo" : clases));
        };
        elemento.addEventListener("mouseenter", function () { resaltar(true); });
        elemento.addEventListener("mouseleave", function () { resaltar(false); });
        elemento.addEventListener("focusin", function () { resaltar(true); });
        elemento.addEventListener("focusout", function () { resaltar(false); });
        marcador.on("click", function () {
          cocinas.forEach(function (otra) { otra.classList.remove("es-activa"); });
          elemento.classList.add("es-activa");
        });
      }
    });

    if (contenedor.getAttribute("data-con-ubicacion") === "true") {
      L.marker(referencia, {
        icon: L.divIcon({ className: "marcador-envoltura", html: "<span class=\"marcador-yo\"></span>", iconSize: [18, 18] }),
        title: "Tu ubicación"
      }).addTo(mapa).bindPopup("Estás aquí");
      puntos.push(referencia);
    }

    if (puntos.length > 1) {
      mapa.fitBounds(puntos, { padding: [36, 36], maxZoom: 15 });
    } else if (puntos.length === 1) {
      mapa.setView(puntos[0], 15);
    }
  }

  document.addEventListener("DOMContentLoaded", iniciar);
})();
