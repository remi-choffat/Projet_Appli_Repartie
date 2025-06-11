import {restaurantLayer, coordonnees} from "./map.js";

// URL de l'API pour les restaurants
const RMI_API = "../resto.json"; // TODO: Remplacer par l'URL de l'API RMI


// Icône pour les restaurants
const restaurantIcon = L.icon({
    iconUrl: 'img/restaurant-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});


/**
 * Récupère la liste des restaurants depuis l'API.
 * @returns {Promise<*|*[]>} Liste des restaurants.
 */
async function fetchRestaurants() {
    try {
        const response = await fetch(RMI_API);
        if (!response.ok) {
            throw new Error(response.status + " " + response.statusText);
        }
        const data = await response.json();
        return data.restaurants || [];
    } catch (error) {
        console.error("Erreur lors de la récupération des restaurants :", error);
        return [];
    }
}


/**
 * Détermine le statut d'un restaurant en fonction de ses heures d'ouverture et de fermeture.
 * @param heureOuverture L'heure d'ouverture du restaurant au format HH:MM
 * @param heureFermeture L'heure de fermeture du restaurant au format HH:MM
 * @returns {{statut: string, couleur: string}} Un objet contenant le statut du restaurant et la couleur associée.
 */
function getRestaurantStatus(heureOuverture, heureFermeture) {

    if (!heureOuverture || !heureFermeture) {
        return {statut: "Horaires non renseignées", couleur: "#6c757d"}; // Gris pour heures non définies
    }

    const now = new Date();
    const [hO, mO] = heureOuverture.split(':').map(Number);
    const [hF, mF] = heureFermeture.split(':').map(Number);

    const ouverture = new Date(now);
    ouverture.setHours(hO, mO, 0, 0);
    const fermeture = new Date(now);
    fermeture.setHours(hF, mF, 0, 0);

    // Cas où fermeture après minuit
    if (fermeture <= ouverture) fermeture.setDate(fermeture.getDate() + 1);

    const minutesAvantOuverture = (ouverture - now) / 60000;
    const minutesAvantFermeture = (fermeture - now) / 60000;

    if (now < ouverture) {
        if (minutesAvantOuverture <= 30) {
            return {statut: "Ouvre bientôt", couleur: "#ffc107"};
        }
        return {statut: "Fermé", couleur: "#dc3545"};
    }
    if (now >= ouverture && now < fermeture) {
        if (minutesAvantFermeture <= 30) {
            return {statut: "Ferme bientôt", couleur: "#fd7e14"};
        }
        return {statut: "Ouvert", couleur: "#28a745"};
    }
    return {statut: "Fermé", couleur: "#dc3545"};
}


/**
 * Initialise la couche des restaurants sur la carte.
 * @returns {Promise<void>}
 */
async function initRestoLayer() {
    restaurantLayer.clearLayers();
    const restaurants = await fetchRestaurants();
    for (const resto of restaurants) {
        if (resto.adresse) {
            const res = await fetch(`https://api-adresse.data.gouv.fr/search/?q=${resto.adresse}&limit=1&lat=${coordonnees[0]}&lon=${coordonnees[1]}`);
            if (!res.ok) throw new Error('Erreur lors de la récupération des coordonnées : ' + res.status + ' ' + res.statusText);
            const data = await res.json();
            if (data.features.length === 0) {
                console.warn(`Aucune coordonnée trouvée pour l'adresse : ${resto.adresse}`);
                continue;
            }
            resto.nom = resto.nom || "Restaurant";
            resto.lat = data.features[0].geometry.coordinates[1]; // Latitude
            resto.lon = data.features[0].geometry.coordinates[0]; // Longitude
            resto.adresse = data.features[0].properties.label; // Adresse formatée
            resto.heureOuverture = resto.heureouverture;
            resto.heureFermeture = resto.heurefermeture;

            const {statut, couleur} = getRestaurantStatus(resto.heureOuverture, resto.heureFermeture);

            const popupContent = `
                <b>🍴 ${resto.nom.toUpperCase()}</b>
                <br/><br/>
                <span>${resto.adresse}</span>
                <br/><br/>
                <span class="badge-statut" style="background:${couleur};">
                    ${statut}
                </span>
                <br/>
                ${resto.heureOuverture && resto.heureFermeture ? `<small>Ouvert de ${resto.heureOuverture} à ${resto.heureFermeture}</small>` : ""}
            `;

            L.marker([resto.lat, resto.lon], {icon: restaurantIcon})
                .addTo(restaurantLayer)
                .bindPopup(popupContent);
        }
    }
}


// Initialisation de la carte et des stations
initRestoLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des restaurants sur la carte :", error);
});