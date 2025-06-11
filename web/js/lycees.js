import {lyceesLayer, coordonnees} from "./map.js";

// URL de l'API pour les établissements scolaires
const API = "https://data.education.gouv.fr/api/explore/v2.1/catalog/datasets/fr-en-adresse-et-geolocalisation-etablissements-premier-et-second-degre/records?select=appellation_officielle%2C%20adresse_uai%2C%20latitude%2C%20longitude&where=denomination_principale%20LIKE%20%27LYCEE%25%27%20AND%20code_departement%20%3D%20%27054%27&limit=-1";


// Icône pour les lycees
const lyceeIcon = L.icon({
    iconUrl: 'img/map-icons/lycee-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});


/**
 * Récupère la liste des lycées depuis l'API.
 * @returns {Promise<*|*[]>} Liste des lycées.
 */
async function fetchlycees() {
    try {
        const response = await fetch(API);
        if (!response.ok) {
            throw new Error(response.status + " " + response.statusText);
        }
        const data = await response.json();
        return data.results || [];
    } catch (error) {
        console.error("Erreur lors de la récupération des lycées :", error);
        return [];
    }
}


/**
 * Initialise la couche des lycées sur la carte.
 * @returns {Promise<void>}
 */
async function initlyceeLayer() {
    lyceesLayer.clearLayers();
    const lycees = await fetchlycees();
    for (const lycee of lycees) {

        const popupContent = `
                <b>🏫 ${lycee.appellation_officielle.toUpperCase()}</b>
                <br/><br/>
                <span>${lycee.adresse_uai}</span>
        `;

        L.marker([lycee.latitude, lycee.longitude], {icon: lyceeIcon})
            .addTo(lyceesLayer)
            .bindPopup(popupContent);
    }
}


// Initialisation de la carte des lycées
initlyceeLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des lycées sur la carte :", error);
});