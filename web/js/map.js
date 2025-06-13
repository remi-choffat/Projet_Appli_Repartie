// Coordonnées par défaut : Nancy
export let coordonnees = [48.6881068, 6.1322327];
const defaultZoom = 13;

// La carte affichée sur la page
export const map = L.map('map').setView(coordonnees, defaultZoom);

// Création des cartes de base
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
export const restaurantLayer = L.layerGroup().addTo(map); // Couches pour les restaurants
export const veloDispoLayer = L.layerGroup(); // Couches pour les stations Vélo avec vélos disponibles
export const veloPlacesLibresLayer = L.layerGroup(); // Couches pour les stations Vélo avec places libres
export const incidentsLayer = L.layerGroup().addTo(map); // Couches pour les incidents de la circulation
export const lyceesLayer = L.layerGroup().addTo(map); // Couches pour les établissements scolaires

// Contrôle des couches
const baseMaps = {
    "Nancy Map": osm,
    "Nancy Map HOT": osmHOT,
    "Nancy Topo Map": openTopoMap
};

const overlayMaps = {
    "Stations VeloStanLib": veloLayer,
    "Vélos disponibles": veloDispoLayer,
    "Places libres": veloPlacesLibresLayer,
    "Restaurants": restaurantLayer,
    "Incidents de la circulation": incidentsLayer,
    "Lycées": lyceesLayer
};

// Ajout du contrôle des couches à la carte
L.control.layers(baseMaps, overlayMaps).addTo(map);

// Désactivation des couches de vélo si elles sont ajoutées sans la couche principale
const layerNamesToDisable = {
    "Vélos disponibles": veloDispoLayer,
    "Places libres": veloPlacesLibresLayer
};

// Événement pour mettre à jour les couches de vélo lorsque des couches sont ajoutées ou supprimées
map.on('overlayadd overlayremove', function () {
    const isVeloLayerActive = map.hasLayer(veloLayer);

    const controlContainer = document.querySelector('.leaflet-control-layers-overlays');
    if (!controlContainer) return;

    const labels = controlContainer.querySelectorAll('label');

    labels.forEach(label => {
        const labelText = label.textContent.trim();
        if (layerNamesToDisable[labelText]) {
            const checkbox = label.querySelector('input[type="checkbox"]');
            if (checkbox) {
                if (!isVeloLayerActive) {
                    checkbox.disabled = true;
                    checkbox.checked = false;
                    map.removeLayer(layerNamesToDisable[labelText]); // remove marker group
                } else {
                    checkbox.disabled = false;
                }
            }
        }
    });

    if (typeof window.updateVeloLayer === 'function') {
        window.updateVeloLayer();
    }
});

// Demande la position de l'utilisateur pour centrer la carte
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        position => {
            coordonnees = [position.coords.latitude, position.coords.longitude];
            map.setView(coordonnees, defaultZoom);
        },
        () => {
            // Permission refusée ou erreur, reste sur Nancy
            console.warn("Géolocalisation non disponible ou permission refusée, utilisation des coordonnées par défaut.");
        }
    );
}
