#!/bin/sh

# Enables other devices to find the pi
# Sets hostname to Pythonator
# Run as root

hciconfig hci0 name Pythonator
hciconfig hci0 piscan
