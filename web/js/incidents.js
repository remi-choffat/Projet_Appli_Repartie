import {incidentsLayer, coordonnees} from "./map.js";

// URL de l'API pour les incidents de la circulation
const RMI_API = "http://localhost:9090/incidents"; // TODO: Remplacer par l'URL de l'API RMI


// Icône pour les incidents
const incidentIcon = L.icon({
    iconUrl: 'img/map-icons/incident-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});


/**
 * Récupère la liste des incidents depuis l'API.
 * @returns {Promise<*|*[]>} Liste des incidents.
 */
async function fetchIncidents() {
    try {
        const response = await fetch(RMI_API);
        if (!response.ok) {
            throw new Error(response.status + " " + response.statusText);
        }
        const data = await response.json();
        return data.incidents || [];
    } catch (error) {
        console.error("Erreur lors de la récupération des incidents :", error);
        return [];
    }
}


/**
 * Initialise la couche des incidents sur la carte.
 * @returns {Promise<void>}
 */
async function initIncidentLayer() {
    incidentsLayer.clearLayers();
    const incidents = await fetchIncidents();
    for (const incident of incidents) {

        const now = new Date();
        if ((incident.starttime && new Date(incident.starttime) > now) || (incident.endtime && new Date(incident.endtime) < now) || (!incident.location.polyline)) {
            continue; // Ignore les incidents qui ne sont pas actifs ou qui n'ont pas de coordonnées
        }

        const coordonnees = incident.location.polyline.split(" ").map(Number);

        const popupContent = `
                <b>⚠️ ${incident.short_description.toUpperCase()}</b>
                <br/><br/>
                <span>${incident.location.location_description}</span>
                <br/><br/>
                <span>${incident.description}</span>
                <br/><br/>
                <span>${incident.endtime ? `<i>Jusqu'au ${new Date(incident.endtime).toLocaleDateString("fr-FR")}</i>` : ""}</span>
            `;

        L.marker([coordonnees[0], coordonnees[1]], {icon: incidentIcon})
            .addTo(incidentsLayer)
            .bindPopup(popupContent);
    }
}


// Initialisation de la carte des incidents
initIncidentLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des incidents sur la carte :", error);
});
