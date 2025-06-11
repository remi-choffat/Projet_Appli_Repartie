import {restaurantLayer, coordonnees} from "./map.js";

const RMI_API = "../resto.json"; // TODO: Remplacer par l'URL de l'API RMI

const restaurantIcon = L.icon({
    iconUrl: 'img/restaurant-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});

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

async function initRestoLayer() {
    restaurantLayer.clearLayers();
    const restaurants = await fetchRestaurants();
    for (const resto of restaurants) {
        if (resto.adresse) {
            // Récupère les coordonnées GPS à partir de l'adresse
            const res = await fetch(`https://api-adresse.data.gouv.fr/search/?q=${resto.adresse}&limit=1&lat=${coordonnees[0]}&lon=${coordonnees[1]}`);
            if (!res.ok) throw new Error('Erreur de chargement');
            const data = await res.json();
            if (data.features.length === 0) {
                console.warn(`Aucune coordonnée trouvée pour l'adresse : ${resto.adresse}`);
                continue;
            }
            resto.lat = data.features[0].geometry.coordinates[1];
            resto.lon = data.features[0].geometry.coordinates[0];

            L.marker([resto.lat, resto.lon], { icon: restaurantIcon })
                .addTo(restaurantLayer)
                .bindPopup(`
                <b>${resto.nom}</b><br>
            `);
        }
    }
}

// Initialisation de la carte et des stations
initRestoLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des restaurants sur la carte :", error);
});