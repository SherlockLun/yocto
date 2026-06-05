# Yocto Crash Kurs: Raspberry Pi

## Kurz-Anleitung

1. Abhängigkeiten installieren.
2. Poky und den Raspberry-Pi-Layer klonen.
3. Build-Umgebung starten: `source oe-init-build-env build-rpi`.
4. In `conf/local.conf` `MACHINE = "raspberrypi4"` setzen.
5. In `conf/bblayers.conf` `meta-raspberrypi` eintragen.
6. Image bauen: `bitbake core-image-minimal`.
7. Image auf SD-Karte schreiben oder mit QEMU testen.

## Was ist Yocto?
Automatisiertes Build-System für embedded Linux. Du beschreibst, was du willst → Yocto generiert komplettes Linux-Image.

## Installation & Setup (15 min)

```bash
# Abhängigkeiten (Ubuntu/Debian)
sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential \
  chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils \
  iputils-ping file

# Yocto-Workspace vorbereiten
cd ~/yocto
git clone git://git.yoctoproject.org/poky.git
cd poky

# Raspi-Layer (Board Support Package)
git clone git://git.github.com/agherzan/meta-raspberrypi.git
```

## Build-Umgebung initialisieren

```bash
# Source-Umgebung. Erzeugt build-Ordner
source oe-init-build-env build-rpi

# conf/local.conf anpassen: diese 2 Zeilen suchen und ändern
MACHINE = "raspberrypi4"          # statt "qemux86-64"
DISTRO = "poky"                   # default okay

# conf/bblayers.conf: meta-raspberrypi Layer hinzufügen
# In BBLAYERS diese Zeile ergänzen:
# /path/to/poky/meta-raspberrypi \
```

## Einfaches Image bauen

```bash
# Raus aus build-rpi, rein ins source
cd ~/yocto/poky

# Image bauen (dauert erste Mal 30-60 min!)
bitbake core-image-minimal

# Fertig! Image liegt hier:
# build-rpi/tmp/deploy/images/raspberrypi4/core-image-minimal-raspberrypi4.wic.bz2
```

## Testen

### Option 1: Auf echte SD-Karte (easiest)
```bash
# Image entpacken & auf SD-Karte schreiben
cd build-rpi/tmp/deploy/images/raspberrypi4
bunzip2 core-image-minimal-raspberrypi4.wic.bz2
sudo dd if=core-image-minimal-raspberrypi4.wic of=/dev/sdX bs=4M
# /dev/sdX = deine SD-Karte (lsblk checken!)
```

### Option 2: QEMU (schneller, keine Hardware nötig)
```bash
# QEMU installieren
sudo apt install qemu-system-arm

# In build-rpi:
runqemu qemuarmv6 core-image-minimal
```

## Erste Anpassung: Package hinzufügen

1. **Layer (Rezeptbuch) erstellen:**
```bash
bitbake-layers create-layer ../meta-custom
bitbake-layers add-layer ../meta-custom
```

2. **Rezept für dein Package** (`meta-custom/recipes-apps/hello/hello_1.0.bb`):
```
SUMMARY = "Hello World"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade40b6dfe2b11f1d8b6f6c09b77"

SRC_URI = "file://hello.c"
S = "${WORKDIR}"

do_compile() {
    ${CC} hello.c -o hello
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 hello ${D}${bindir}
}
```

3. **Image mit deinem Package:**
```bash
# local.conf ergänzen:
IMAGE_INSTALL:append = " hello"

# Neu bauen
bitbake core-image-minimal
```

## Wichtige Begriffe

| Begriff       | Bedeutung                                             |
| ------------- | ----------------------------------------------------- |
| **BitBake**   | Build-Engine (wie Make)                               |
| **.bb Datei** | Rezept (beschreibt Package)                           |
| **Layer**     | Sammlung von Rezepten (meta-foo)                      |
| **BSP**       | Board Support Package (Board-spezifische Anpassungen) |
| **rootfs**    | Root Filesystem (Linux auf der Hardware)              |
| **wic**       | Disk Image Format (direkt auf SD-Karte)               |

## Speicherdebugging-Tools
- `heaptrack` zeigt, wo Speicher allokiert wird, und hilft beim Finden von Leaks und hohen Peaks.
- `massif` ist das Valgrind-Tool für Heap-Wachstum und Speicherverbrauch über die Zeit.

## Nächste Schritte
- `bitbake -k -c build core-image-minimal` (k = weiter bei Fehlern)
- `bitbake-layers show-recipes` (alle verfügbaren Packages)
- `bitbake -c devshell hello` (interaktive Shell zum Debuggen)
- Logs: `build-rpi/tmp/work/` (sehr hilfreich!)

---
**Tipps:**
- Erste Build dauert lange (parallel bauen: `BB_NUMBER_THREADS = 8` in local.conf)
- Build-Cache liegt in `build-rpi/sstate-cache/` (Platz sparen: `rm -rf sstate-cache/*`)
- Raspi mit echtem Image testen ist am schnellsten zum Lernen
