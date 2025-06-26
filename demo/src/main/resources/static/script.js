console.log("java esta funcionando");



const eventosGratis = [
  {
    titulo: "Festival de Música en el Parque",
    fecha: "20 de junio",
    lugar: "Parque Central",
    imagen: "https://picsum.photos/id/1003/300/200"
  },
  {
    titulo: "Clases de Zumba Gratis",
    fecha: "25 de junio",
    lugar: "Gimnasio del barrio",
    imagen: "https://picsum.photos/id/1011/300/200"
  },
  {
    titulo: "Obra de teatro comunitaria",
    fecha: "30 de junio",
    lugar: "Centro Cultural",
    imagen: "https://picsum.photos/id/1015/300/200"
  }
];

const botonGratis = document.getElementById("btnEventosGratis");
const contenedor = document.getElementById("contenedorEventosGratis");

botonGratis.addEventListener("click", () => {
  contenedor.innerHTML = ""; // Limpiamos el contenedor por si se clicó antes

  eventosGratis.forEach(evento => {
    const card = document.createElement("div");
    card.className = "evento-card";
    card.innerHTML = `
      <img src="${evento.imagen}" alt="${evento.titulo}">
      <h4>${evento.titulo}</h4>
      <p>${evento.fecha}</p>
      <p>${evento.lugar}</p>
    `;
    contenedor.appendChild(card);
  });
});




