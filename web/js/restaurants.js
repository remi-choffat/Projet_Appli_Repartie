import {restaurantLayer, coordonnees} from "./map.js";

// URL de l'API pour les restaurants
const RMI_API = "http://localhost:9090/restos"; // TODO: Remplacer par l'URL de l'API RMI


// Icône pour les restaurants
const restaurantIcon = L.icon({
    iconUrl: 'img/map-icons/restaurant-icon.png',
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
        return {statut: "Horaires non renseignées", couleur: "#6c757d"};
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
 * Affiche une modale de réservation avec le contenu HTML fourni.
 * @param html Le contenu HTML à afficher dans la modale.
 */
function showReservationModal(html) {
    const modal = document.getElementById('reservationModal');
    const content = document.getElementById('reservationContent');
    content.innerHTML = html;
    modal.style.display = 'flex';
    modal.onclick = e => {
        if (e.target === modal) modal.style.display = 'none';
    };
}


/**
 * Ouvre le formulaire de réservation pour un restaurant.
 * @param resto L'objet restaurant contenant les informations nécessaires.
 */
function openReservationForm(resto) {
    showReservationModal(`
        <h3 class="subtitle">Réserver chez ${resto.nom}</h3>
        <label>Jour : <input class="input" type="date" id="resDate" min="${new Date().toISOString().split('T')[0]}" required></label>
        <br/><br/>
        <label>Heure : <input class="input" type="time" id="resTime" required></label>
        <br/><br/>
        <button class="button" id="checkTablesBtn">Voir les tables disponibles</button>
        <div id="reservationError" style="color:#dc3545;margin-top:10px;"></div>
    `);
    document.getElementById('checkTablesBtn').onclick = async () => {
        const date = document.getElementById('resDate').value;
        const time = document.getElementById('resTime').value;
        const errorDiv = document.getElementById('reservationError');
        errorDiv.textContent = "";

        if (!date || !time) {
            errorDiv.textContent = "Veuillez saisir une date et une heure.";
            return;
        }

        // Vérifie que la date/heure n'est pas dans le passé
        const now = new Date();
        const selected = new Date(date + "T" + time);
        if (selected < now) {
            errorDiv.textContent = "Veuillez sélectionner une date et une heure dans le futur.";
            return;
        }

        // Vérifie les horaires d'ouverture/fermeture si définis
        if (resto.heureOuverture && resto.heureFermeture) {
            const [hO, mO] = resto.heureOuverture.split(':').map(Number);
            const [hF, mF] = resto.heureFermeture.split(':').map(Number);
            const ouverture = new Date(selected);
            ouverture.setHours(hO, mO, 0, 0);
            const fermeture = new Date(selected);
            fermeture.setHours(hF, mF, 0, 0);
            if (fermeture <= ouverture) fermeture.setDate(fermeture.getDate() + 1);

            if (selected < ouverture || selected >= fermeture) {
                errorDiv.textContent = `Réservation possible uniquement entre ${resto.heureOuverture} et ${resto.heureFermeture}.`;
                return;
            }
        }

        await fetchAvailableTables(resto, date, time);
    };
}


/**
 * Récupère les tables disponibles pour un restaurant à une date et une heure données.
 * @param resto L'objet restaurant contenant les informations nécessaires.
 * @param date La date de la réservation au format YYYY-MM-DD.
 * @param time L'heure de la réservation au format HH:MM.
 * @returns {Promise<void>} La liste des tables disponibles.
 */
async function fetchAvailableTables(resto, date, time) {
    const url = `${RMI_API}/${resto.id}/tables?date=${date}&heure=${time}`;
    const res = await fetch(url);
    if (!res.ok) return alert('Erreur lors de la récupération des tables');
    const reponse = await res.json();
    const tables = reponse.tables || [];
    if (!tables.length > 0) return showReservationModal('<p>Aucune table disponible à ce créneau.</p>');
    showReservationModal(`
        <h3 class="subtitle">Tables disponibles</h3>
        <ul>
            ${tables.map(t => `<li>
                <button class="selectTableBtn button" data-idtable="${t.numTable}" style="margin-bottom: 5px;">${t.nom}</button>
            </li>`).join('')}
        </ul>
    `);
    document.querySelectorAll('.selectTableBtn').forEach(btn => {
        btn.onclick = () => openVisitorForm(resto, date, time, btn.dataset.idtable);
    });
}


/**
 * Ouvre le formulaire de réservation pour un visiteur.
 * @param resto L'objet restaurant contenant les informations nécessaires.
 * @param date La date de la réservation au format YYYY-MM-DD.
 * @param time L'heure de la réservation au format HH:MM.
 * @param tableId L'ID de la table sélectionnée.
 */
function openVisitorForm(resto, date, time, tableId) {
    showReservationModal(`
        <h3 class="subtitle">Vos informations</h3>
        <form id="visitorForm">
            <input class="input" type="text" name="nom" placeholder="Nom" required>
            <br/><br/>
            <input class="input" type="text" name="prenom" placeholder="Prénom" required>
            <br/><br/>
            <input class="input" type="number" name="convives" placeholder="Nombre de convives" min="1" required>
            <br/><br/>
            <input class="input" type="tel" name="tel" placeholder="Téléphone" required>
            <br/><br/>
            <button id="boutonReserver" class="button" type="submit">Réserver</button>
        </form>
    `);
    document.getElementById('visitorForm').onsubmit = async e => {
        e.preventDefault();
        const bouton = document.getElementById('boutonReserver');
        bouton.disabled = true;
        bouton.classList.add("is-loading");
        const data = Object.fromEntries(new FormData(e.target));
        data.date = new Date(date + 'T' + time).toISOString();
        data.tableId = tableId;
        await sendReservation(data);
    };
}


/**
 * Envoie les données de réservation à l'API.
 * @param data Les données de réservation à envoyer.
 * @returns {Promise<void>}
 */
async function sendReservation(data) {
    try {
        const res = await fetch(RMI_API + "/reserver", {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            throw new Error(await res.text());
        }
        const result = await res.text();
        showReservationModal(`<p>${result || 'Réservation effectuée !'}</p>`);
    } catch (error) {
        console.error('Erreur lors de l\'envoi de la réservation :', error);
        showReservationModal(`<p style="color:#dc3545;margin-top:10px;">Une erreur est survenue lors de la réservation...</p>`);
    }
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
                <br/><br/>
                <button class="btn-reserver button" data-resto='${JSON.stringify({id: resto.id, nom: resto.nom})}'>Réserver une table</button>
            `;

            L.marker([resto.lat, resto.lon], {icon: restaurantIcon})
                .addTo(restaurantLayer)
                .bindPopup(popupContent)
                .on('popupopen', function (e) {
                    const btn = document.querySelector('.btn-reserver');
                    if (btn) {
                        btn.onclick = () => openReservationForm(resto);
                    }
                });
        }
    }
}


// Initialisation de la carte des restaurants
initRestoLayer().catch(error => {
    console.error("Erreur lors de l'initialisation des restaurants sur la carte :", error);
});
