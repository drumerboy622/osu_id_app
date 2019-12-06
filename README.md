# How to Download and Use OSU ID Application

There are many different ways to download and use Android Applications that are not on the App Store. This report will demonstrate only one, but other methods may be used. To get started, download Android Studio @ https://developer.android.com/studio. If you will be testing without an actual Android Device 8.0 or greater, make sure your device has a usable camera and include virtual machine's as a download option in Android Studio.


# Download the Source Code

The source code for this project is currently hosted on GitHub @ https://github.com/drumerboy622/osu_id_app. To download the code, go to the URL for the repository and select Download ZIP.  Once you have downloaded the zip, extract it, and you should get an osu_id_app folder.  Alternatively, if you have git installed on your machine, you can download the source code using the following git command:

git clone https://github.com/drumerboy622/osu_id_app

Once downloaded, start the Android Studio program.  At the "Welcome to Android Studio" page, click "Open an Existing Android Studio project" and enter the path to the downloaded source code mentioned above and click "OK". Allow up to 2 minutes for the Gradle Scripts to download. 

## Download Virtual Device and Deploy

If testing on a virtual device, go to the top menu and select "Tools" - "AVD Manager". Select " + Create Virtual Device...". On the left select "Tablet", in the middle select "Nexus 7(2012)" and finally select "Next" on the bottom right. Select "Download" next to "Pie". Accept Terms and select "Next". Allow time for it to download. Select "Finish". Select "Next". Select "Finish". On the far right under "Actions" a pencil "edit" button should be present. Press this button. Select "Show Advanced Settings". There should be a "Camera" Section. In the drop down for the "Front" Camera select an available camera on your device. Do the same for the "Back" Camera. Select "Finish".   Exit the "Android Virtual Device Manager" window. In the middle of the top menu there should be a pull-down menu that "Unknown Device" is selected. Change that to the "Nexus 7 (2012) API 29..." device that was just downloaded. Select the "Play" button next to this drop-down. It may take up to 2 minutes, but the emulator should deploy the application in a new window. 

## Download Onto Device and Deploy

If testing on an actual android device, make sure that developer mode is enabled @ https://developer.android.com/studio/debug/dev-options. Plug device into the computer. From the top menu select the drop down menu and select device that was plugged in. Once selected, select the "play" button next to the drop-down. It may take up to two minutes for the application to download and deploy. 

