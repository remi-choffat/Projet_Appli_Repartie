import {veloLayer} from "./map.js";

const CYCLOCITY_API_STATIONS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";
const CYCLOCITY_API_STATUT = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json";

const bikeIcon = L.icon({
    iconUrl: 'img/bike-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});

async function fetchStations() {
    try {
        const response = await fetch(CYCLOCITY_API_STATIONS);
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

async function fetchStationsStatus() {
    try {
        const response = await fetch(CYCLOCITY_API_STATUT);
        if (!response.ok) throw new Error(response.status + " " + response.statusText);
        const data = await response.json();
        return data.data.stations;
    } catch (error) {
        console.error("Erreur lors de la récupération du statut des stations :", error);
        return [];
    }
}

async function initVeloLayer() {
    veloLayer.clearLayers();
    const [stations, statuses] = await Promise.all([fetchStations(), fetchStationsStatus()]);

    // Création d'une map pour un accès rapide au statut par station_id
    const statusMap = new Map();
    statuses.forEach(status => statusMap.set(status.station_id, status));

    stations.forEach(station => {
        if (station.lat && station.lon) {
            const status = statusMap.get(String(station.station_id));
            let popupContent = `<b>${station.name}</b>`;
            if (status) {
                popupContent += `<br/>Vélos disponibles : ${status.num_bikes_available}`;
                popupContent += `<br/>Places libres : ${status.num_docks_available}`;
            } else {
                popupContent += `<br/><i>Statut indisponible</i>`;
            }
            L.marker([station.lat, station.lon], {icon: bikeIcon})
                .addTo(veloLayer)
                .bindPopup(popupContent);
        }
    });
}

// Initialisation de la carte et des stations
initVeloLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des stations de vélo sur la carte :", error);
});