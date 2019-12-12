# Pythonator

## Compiling

### App
The app has been developped for Android, in Android Studio, on a Windows machine.
You can use Android Studio for other operating systems, too, but the Windows one handles dependencies automatically.

In order to work with the source code of the app, please follow the following instructions:
 1. Go to [https://developer.android.com/studio](https://developer.android.com/studio) and download Android Studio for Windows
 2. Once Downloaded, run the installer
 3. When booting up for the first time, Android Studio will ask you what project you want to make/open. Choose to open a project. Navigate to where you placed the source code of the app. Select the folder named 'app' with a distinctive Android Studio-icon instead of a folder-icon, and hit OK.
 4. Android Studio opens the project, and may tell you that it needs some libraries (in the right-bottom corner of the screen).
   Just click the blue link to start downloading and installing
 5. Now, Android Studio is ready: compile the source code by clicking Build > Make Project ('Build' is found in top menu). This project contains no errors, so making the project should be succesfull.
Now you can look around the source code.

In order to generate an APK (Android PacKage, app install package for Android phones):
 1. Go to Build > Generate Signed APK ('Build' is found in top menu)
 2. Check V2 Full APK Signature, pick a directory to store build output (including APK) and click Next
 3. Create a signing key (fill out a password, fabricate a package) and click next
 4. Apk is generated as 'release.apk' in the folder you specified in step 2.

### Controller software
Compilation instructions for the controller:

Note:
This program requires a bluetooth adapter and a usb port to be availlable on the host device. For the bluetooth adapter, libbluetooth must be available, which should be availlable on any Linux system with a bluetooth adapter.

First, install a modern C++ compiler, gcc version 7 was used for our project.

Next install OpenCV dependencies, the packages required are:
    -cmake
    -git
    -libgtk2.0-dev
    -pkg-config
    -libavcodec-dev
    -libavformat-dev
    -libswscale-dev
    -libtbb2
    -libtbb-dev
    -libjpeg-dev
    -libpng-dev

These packages can simply be installed with the package manager, which is apt-get on raspberry pi.

Next, download the OpenCV version 4.1.2 source code from https://opencv.org

Run the following commands in the source directory to compile and build:

mkdir build
cd build
cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/usr/local -DOPENCV_GENERATE_PKGCONFIG=ON
make
sudo make install

Note that the make can take a while to build OpenCV

After this, we can build our project by running make in the controller directory. This results in the pythonator-pi program.

### Arduino software

The arduino part of the project requires a few dependencies:
- avr-gcc
- avr-libc
- [meson](https://mesonbuild.com/) (can be installed via pip)
- [ninja](https://ninja-build.org/)
Meson cross compilation definitions for the specific Arduino chip used in the project
(an Arduino Uno compatible device) are included in the project. When `avr-gcc` is in the path,
compilation can be done as follows:
```
$ cd bot
$ mkdir build
$ cd build
$ meson .. --cross avr-atmega328p-cross.ini
$ ninja
```

When an arduino is connected, it can be programmed by using the utility target `ninja flash`. The 
interface of the arduino can be customized in meson_options.txt, and defaults to `/dev/ttyACM0`.

### Dxf2ptn
Dxf2ptn is a simple python 3.7 script. The only non-standard dependency is [ezdxf](https://ezdxf.readthedocs.io/en/master/), which can be installed via pip.

### Simulator

The simulator requires the following dependencies:
- meson
- ninja
- [GLFW 3](https://www.glfw.org/)

Compilation can be done as follows:
```
$ cd simulator
$ mkdir build
$ meson ..
$ ninja
```

Once built, the `ptnsim` executable accepts drawing commands via stdin. The `-m` flag can be used to also show non-drawing moves.

### Ptncom

Ptncom requires no dependencies except a Linux system. The executable can be built as follows:
```
$ cd ptncom
$ make
```

The executable accept commands via stdin, and accepts a serial device file as its first parameter. Together with dxf2ptn
and ptncom, one can make a flexible setup for testing and drawing images.

### Controller-Test

The controller test requires a Windows system with a bluetooth interface to run. To compile it:

g++ -o controller-test main.cpp -lwsa2_32
