// Coordonnées par défaut : Nancy
let defaultCoords = [48.6881068, 6.1322327];
let defaultZoom = 13;

// La carte affichée sur la page
export const map = L.map('map').setView(defaultCoords, defaultZoom);

let tileLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: 'Nancy Map'
}).addTo(map);

// Demande la position de l'utilisateur pour centrer la carte
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function (position) {
            let userCoords = [position.coords.latitude, position.coords.longitude];
            map.setView(userCoords, defaultZoom);
        },
        function () {
            // Permission refusée ou erreur, reste sur Nancy
        }
    );
}
