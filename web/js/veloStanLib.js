import { veloLayer } from "./map.js";

const CYCLOCITY_API = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";

const bikeIcon = L.icon({
    iconUrl: 'img/bike-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});

async function fetchStations() {
    try {
        const response = await fetch(CYCLOCITY_API);
        if (!response.ok) {
            throw new Error(response.status + " " + response.statusText);
        }
        const data = await response.json();
        return data.data.stations;
    } catch (error) {
        console.error("Erreur lors de la récupération des stations de vélo :", error);
        return [];
    }
}

async function initVeloLayer() {
    veloLayer.clearLayers();
    const stations = await fetchStations();
    stations.forEach(station => {
        if (station.lat && station.lon) {
            L.marker([station.lat, station.lon], { icon: bikeIcon })
                .addTo(veloLayer)
                .bindPopup(`
                    <b>${station.name}</b><br>
                    Capacité : ${station.capacity} vélos
                `);
        }
    });
}

// Initialisation de la carte et des stations
initVeloLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des stations de vélo sur la carte :", error);
});