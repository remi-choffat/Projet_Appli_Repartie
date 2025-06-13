export function getApiBaseUrl() {
    return localStorage.getItem('apiBaseUrl') || 'http://127.0.0.1:9090'; // valeur par défaut
}

// Bouton caché pour changer l'IP
document.getElementById('btnChangeApiIp').onclick = () => {
    document.getElementById('inputApiIp').value = getApiBaseUrl();
    document.getElementById('modalChangeIp').style.display = 'flex';
};
document.getElementById('btnCancelApiIp').onclick = () => {
    document.getElementById('modalChangeIp').style.display = 'none';
};
document.getElementById('btnSaveApiIp').onclick = () => {
    const ip = document.getElementById('inputApiIp').value.trim();
    if (ip) {
        localStorage.setItem('apiBaseUrl', ip);
        location.reload();
    }
};