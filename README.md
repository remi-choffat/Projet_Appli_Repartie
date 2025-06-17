# Nancy Map - Projet d'Application Répartie

## ✍️ Auteurs

> - [Kateryna BABACHANAKH](https://github.com/babachanakh-kateryna)
> - [Rémi CHOFFAT](https://github.com/remi-choffat)
> - [Mathieu GRAFF](https://github.com/Cesareuh)
> - [Maxime VAULTRIN](https://github.com/vmaxime03)

## 📖 Présentation

L'objectif est de proposer une application répartie qui permet d'afficher, dans un navigateur, sur une carte Leaflet
des informations hétérogènes sur Nancy, transitant parfois par un proxy, qui fait la passerelle entre le navigateur et
des services.

## 🚀 Lancer les services
### Compiler
- Aller dans le répertoire java/Nancy-Map
- Compiler `mvn clean compile assembly:single`
- Envoyer le projet sur les machines qui exécuteront les services
- Pour lancer les scripts, il faut être dans le répertoire `java/Nancy-Map`
### À distance
Ce script va lancer les services sur les machines mises en paramètres. Si les paramètres optionnels ne sont pas remplis, les services seront lancés sur la première machine.
Sur une machine, lancer le script `runDistant.sh` de `java/Nancy-Map` : 
```bash
./runDistant.sh <user ssh> <ip serveur> [port serveur (1099)] [ip serviceproxy] [port serviceproxy] [ip servicedb] [port servicedb] [port serveur web (9090)]
```
### Localement
Ce script va lancer tous les services sur la même machine :
```bash
./runApp.sh [port du registry (1099 par defaut)] [port du serveur web (9090 par defaut)]
```

## 🖥️ Utiliser le site web
L'application est disponible à l'adresse [https://webetu.iutnc.univ-lorraine.fr/www/choffat2u/S4/NancyMap](https://webetu.iutnc.univ-lorraine.fr/www/choffat2u/S4/NancyMap).  
Utiliser la fonction de paramétrage (en bas à gauche de l'écran) pour définir l'URL du serveur HTTP. Il est nécessaire de renseigner le port sur lequel le service est actif (par défaut 9090).
