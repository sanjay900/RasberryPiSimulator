#!/bin/bash
sudo apt-get install qemu qemu-user-static binfmt-support
if [ ! -d "rpi_image" ]; then
    mkdir rpi_image
    cd rpi_image
    wget "https://downloads.raspberrypi.org/raspbian/images/raspbian-2016-03-18/2016-03-18-raspbian-jessie.zip"
    unzip *
    cd ..
fi
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
mkdir rpi_mnt
sudo mount "${DIR}/rpi_image/2016-03-18-raspbian-jessie.img" -o loop,offset=$((131072*512)),rw rpi_mnt
cd rpi_mnt
sudo \cp ../ld.so.preload etc/ld.so.preload
sudo \cp /usr/bin/qemu-arm-static usr/bin
sudo cp "../RasberryPi Home/.bashrc" bashrc.sh
sudo mkdir home/pi/code
sudo mount --bind "${DIR}/RasberryPi Home" "${DIR}/rpi_mnt/home/pi/code"
sudo mount --bind /dev dev/
sudo mount --bind /sys sys/
sudo mount --bind /proc proc/
sudo mount --bind /dev/pts dev/pts
sudo chroot . bin/bash -c /bashrc.sh
sudo umount -l dev/pts
sudo umount -l home/pi/code
sudo umount -l dev
sudo umount -l sys
sudo umount -l proc
cd ..
sleep 1
sudo umount rpi_mnt
