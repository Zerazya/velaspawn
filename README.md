# VelaSpawn

Plugin de spawn pour serveur **Paper 26.2** (Java 25).

## Fonctionnalités

- `/spawn` : téléportation au spawn, avec délai, cooldown et annulation si on bouge / subit des dégâts
- `/spawn <joueur>` : envoie un autre joueur au spawn
- `/setspawn` : définit le spawn à votre position (enregistré dans `plugins/VelaSpawn/spawn.yml`)
- `/spawnreload` : recharge `config.yml`
- Téléportation au spawn à la première connexion (ou à chaque connexion), et à la réapparition après la mort
- Messages entièrement configurables (MiniMessage)

## Permissions

| Permission | Défaut | Description |
|---|---|---|
| `spawnplugin.spawn` | tous | `/spawn` |
| `spawnplugin.spawn.others` | op | `/spawn <joueur>` |
| `spawnplugin.setspawn` | op | `/setspawn` |
| `spawnplugin.reload` | op | `/spawnreload` |
| `spawnplugin.bypass.delay` | op | ignore le délai |
| `spawnplugin.bypass.cooldown` | op | ignore le cooldown |

## Compiler

Prérequis : JDK 25 et Gradle.

```
gradle build
```

Le .jar est généré dans `build/libs/`. Copiez-le dans le dossier `plugins/` de votre serveur puis redémarrez.

Pour générer le wrapper Gradle (recommandé, à committer) : `gradle wrapper`.

## GitHub Actions

Le workflow `.github/workflows/build.yml` compile le plugin à chaque push / pull request et publie le .jar comme artefact.
En poussant un tag (`git tag v1.0.0 && git push --tags`), une release GitHub est créée avec le .jar.

## Notes

- `spawn.yml` n'existe qu'après le premier `/setspawn`. Sans lui, le spawn du monde principal est utilisé.
- Spigot/Bukkit seul n'est pas supporté : le plugin cible Paper (téléportation asynchrone, MiniMessage).
