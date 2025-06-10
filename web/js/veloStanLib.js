const CYCLOCITY_API = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";

import {map} from "./map.js";

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

async function initMap() {
    const stations = await fetchStations();
    stations.forEach(station => {
        if (station.lat && station.lon) {
            L.marker([station.lat, station.lon])
                .addTo(map)
                .bindPopup(`${station.name} - ${station.capacity} vélos`);
        }
    });
}

// Initialisation de la carte et des stations
initMap().catch(error => {
    console.error("Erreur lors de l'initialisation de la carte :", error);
});