// initialize the map
const map = L.map('map').setView([48.6921, 6.1844], 13);

// add OpenStreetMap tile layer
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

// event listener for map clicks
map.on('click', function (e) {
    L.popup()
        .setLatLng(e.latlng)
        .setContent(`Vous avez clique a ${e.latlng.toString()}`)
        .openOn(map);
});