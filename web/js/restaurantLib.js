import { restaurantLayer } from "./map.js";

const RMI_API = "";

const restaurantIcon = L.icon({
    iconUrl: 'img/restaurant-icon.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});