## Voraussetzungen
- Ubuntu 20.04/22.04/24.04
- ~50GB freier Speicherplatz

## 1. Pakete installieren
sudo apt update
sudo apt install -y gawk wget git diffstat unzip texinfo gcc build-essential chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev python3-subunit mesa-common-dev zstd liblz4-tool file locales libacl1

## 2. Verzeichnisse
mkdir -p git/yocto/layers
cd git/yocto

## 3. Quellen klonen
git clone -b kirkstone https://git.yoctoproject.org/git/poky && git clone -b kirkstone https://git.yoctoproject.org/git/meta-raspberrypi && git clone -b kirkstone https://github.com/openembedded/meta-openembedded.git

## 4. Build-Umgebung einrichten
cd git/yocto
source poky/oe-init-build-env build

bitbake-layers add-layer ../meta-raspberrypi
bitbake-layers add-layer ../meta-openembedded/meta-oe
bitbake-layers add-layer ../meta-openembedded/meta-python
bitbake-layers add-layer ../meta-openembedded/meta-networking

## 5. conf/local.conf anpassen
echo 'MACHINE = "raspberrypi4"' >> conf/local.conf
echo 'ENABLE_UART = "1"' >> conf/local.conf
echo 'IMAGE_FEATURES += "ssh-server-openssh"' >> conf/local.conf

## 6. Image bauen (2-4 Stunden)
bitbake core-image-base

## 7. SD-Karte beschreiben
 Nach erfolgreichem Build:
 ls git/yocto/build/tmp/deploy/images/raspberrypi4/*.rpi-sdimg
 sudo dd if=DATEINAME.img of=/dev/sdX bs=4M status=progress

## 8. Eigenes Layer für GitHub
cd git/yocto/layers
bitbake-layers create-layer meta-mein-pi
bitbake-layers add-layer meta-mein-pi

cd meta-mein-pi
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/BENUTZER/meta-mein-pi.git
git push -u origin main

## Nützliche Befehle
 bitbake -c cleanall recipe
 bitbake -c menuconfig linux-raspberrypi
 rm -rf tmp/
 bitbake-layers show-layers

## Login nach Boot: root (kein Passwort)