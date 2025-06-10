// Coordonnées par défaut : Nancy
const defaultCoords = [48.6881068, 6.1322327];
const defaultZoom = 13;

// La carte affichée sur la page
export const map = L.map('map').setView(defaultCoords, defaultZoom);

// base layer Nancy Map
export const osm = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: 'Nancy Map'
}).addTo(map);

export const osmHOT = L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: 'Nancy Map'
});

export const openTopoMap = L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: 'Nancy Map'
});

// Creation des couches pour les stations Vélo et les restaurants
export const veloLayer = L.layerGroup().addTo(map); // Couches pour les stations Vélo
export const restaurantLayer = L.layerGroup(); // Couches pour les restaurants

// Contrôle des couches
const baseMaps = {
    "Nancy Map": osm,
    "Nancy Map HOT": osmHOT,
    "Nancy Topo Map": openTopoMap
};

const overlayMaps = {
    "Stations Vélo": veloLayer,
    "Restaurants": restaurantLayer
};

L.control.layers(baseMaps, overlayMaps).addTo(map);

// Demande la position de l'utilisateur pour centrer la carte
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        position => {
            const userCoords = [position.coords.latitude, position.coords.longitude];
            map.setView(userCoords, defaultZoom);
        },
        () => {
            // Permission refusée ou erreur, reste sur Nancy
            console.log("Geolocation non disponible ou permission refusée, utilisation des coordonnées par défaut.");
        }
    );
}
