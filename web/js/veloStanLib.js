import { veloLayer, veloDispoLayer, veloPlacesLibresLayer } from "./map.js";

// URL de l'API Cyclocity pour Nancy
const CYCLOCITY_API_STATIONS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";
const CYCLOCITY_API_STATUT = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json";


// Icône pour les stations de vélo
const bikeIcon = L.icon({
    iconUrl: 'img/bike-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});


/**
 * Récupère la liste des stations de vélo depuis l'API Cyclocity.
 * @returns {Promise<*|*[]>} Liste des stations de vélo.
 */
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


/**
 * Récupère le statut des stations de vélo depuis l'API Cyclocity.
 * @returns {Promise<*|*[]>} Statut des stations de vélo (nombre de vélos et de places disponibles).
 */
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


/**
 * Détermine la couleur du statut d'une station de vélo en fonction du nombre de vélos disponibles.
 * @param nb Nombre de vélos disponibles dans la station.
 * @returns {string} La couleur associée au statut de la station.
 */
function getBikeStatusColor(nb) {
    if (nb === 0) return "#dc3545";      // rouge : aucun vélo
    if (nb <= 3) return "#ffc107";       // jaune : peu de vélos
    return "#28a745";                    // vert : ok
}


/**
 * Renvoie le mot au pluriel si le nombre est supérieur ou égal à 2.
 * @param word Le mot à pluraliser.
 * @param count Le nombre à vérifier pour la pluralisation.
 * @returns {*} Le mot au pluriel ou au singulier.
 */
function pluralize(word, count) {
    return word + (count >= 2 ? "s" : "");
}


/**
 * Initialise la couche des stations de vélo sur la carte.
 * @returns {Promise<void>}
 */
async function initVeloLayer() {
    veloLayer.clearLayers();
    veloDispoLayer.clearLayers();
    veloPlacesLibresLayer.clearLayers();
    const [stations, statuses] = await Promise.all([fetchStations(), fetchStationsStatus()]);

    // Création d'une map pour un accès rapide au statut par station_id
    const statusMap = new Map();
    statuses.forEach(status => statusMap.set(status.station_id, status));

    stations.forEach(station => {
        if (station.lat && station.lon) {
            const status = statusMap.get(String(station.station_id));
            const nbVelosDispo = status ? status.num_bikes_available : 0;
            const nbPlacesLibres = status ? status.num_docks_available : 0;
            let popupContent = `<b>🚲 ${station.name.toUpperCase()}</b><br/><br/>`;
            if (status) {
                const couleur = getBikeStatusColor(nbVelosDispo);
                popupContent += `
                <span class="badge-statut" style="background:${couleur};">
                    ${nbVelosDispo > 0 ? nbVelosDispo : "Aucun"} ${pluralize("vélo", nbVelosDispo)} 
                    ${pluralize("disponible", nbVelosDispo)}
                </span>
                <br/>
                <span>
                    ${nbPlacesLibres > 0 ? nbPlacesLibres : "Aucune"} ${pluralize("place", nbPlacesLibres)} 
                    ${pluralize("libre", nbPlacesLibres)}
                </span>
            `;
            } else {
                popupContent += `<br/><i>Statut indisponible</i>`;
            }
            const marker = L.marker([station.lat, station.lon], { icon: bikeIcon })
                .bindPopup(popupContent);
            marker.addTo(veloLayer);
            if (nbVelosDispo > 0) marker.addTo(veloDispoLayer);
            if (nbPlacesLibres > 0) marker.addTo(veloPlacesLibresLayer);
        }
    });
}


// Initialisation de la carte et des stations
initVeloLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des stations de vélo sur la carte :", error);
});