# Test-Setup: Hello World Package für Yocto

## Struktur zum Kopieren

Folgende Struktur in deinem Layer erstellen:

```
meta-custom/
├── recipes-apps/
│   └── hello-world/
│       ├── hello-world_1.0.bb      (BitBake Rezept)
│       └── files/
│           └── hello.c             (Quellcode)
└── conf/
    └── layer.conf
```

## Quick-Start

### 1. Layer erstellen
```bash
cd ~/yocto/poky
bitbake-layers create-layer ../meta-custom
```

### 2. Dateien kopieren
```bash
# Rezept-Verzeichnis erstellen
mkdir -p ../meta-custom/recipes-apps/hello-world/files

# Dateien rein
cp test-example/hello-world_1.0.bb ../meta-custom/recipes-apps/hello-world/
cp test-example/hello.c ../meta-custom/recipes-apps/hello-world/files/
```

### 3. Layer hinzufügen
```bash
bitbake-layers add-layer ../meta-custom
```

### 4. Image konfigurieren
In `build-rpi/conf/local.conf` ergänzen:
```
IMAGE_INSTALL:append = " hello-world"
```

### 5. Testen
```bash
# Nur das Package bauen (schnell!)
bitbake hello-world

# Komplettes Image mit Package
bitbake core-image-minimal
```

## Output checken

```bash
# Wo ist das Binary?
find build-rpi/tmp/work -name "hello" -type f

# Oder Package-Inhalt anschauen:
bitbake hello-world -c do_package_qa
```

## Debugging

```bash
# Detaillierte Build-Logs
bitbake -v hello-world

# Interaktive Shell im Build-Umfeld
bitbake -c devshell hello-world
# → `make` oder `gcc` Commands hier testen

# Task-Liste
bitbake hello-world -c listtasks
```

## Häufige Fehler

| Fehler                     | Lösung                                                 |
| -------------------------- | ------------------------------------------------------ |
| `LayerIndexException`      | Layer mit `bitbake-layers add-layer` einfügen          |
| `file://hello.c not found` | Datei in `files/` Verzeichnis verschieben              |
| `do_compile failed`        | `bitbake -c devshell hello-world` zum Debuggen         |
| Binary nicht im Image      | `IMAGE_INSTALL:append = " hello-world"` in local.conf? |

---
**Tipp:** Nach erfolgreichem Build das Binary testen:
```bash
# Auf Raspi via SSH
ssh root@<raspi-ip>
hello
# → sollte "Hello from Yocto..." ausgeben
```
