DEPlugin_HM_Multicode for Honeywell Android Devices

Install APK
    1. Use adb
        a. adb install /path/to/apk/DEPlugin_HM_Multicode.apk
    2. Use AutoInstall
        a. Copy apk to /Internal Storage/honeywell/autoinstall
        b. Reboot device

Enable DataEditing plugin
    1. Go to Android Settings -> Scanning -> Internal Scanner -> Default profile -> Data Processing Settings -> Data Editing Plugin
    2. Click on Data Editing Plugin and select the plugin.
    3. The plugin is now active

Remap a key to toggle between Normal and Continuous scan mode
    1. Go to Android Settings -> Key remap -> Select a key
    2. Click on the APPS tab
    3. Select "H&M Scanning Plugin"
    4. Press the remapped key to toggle the scan mode

List of Changes:
20170927_DEPlugin_HM_Multicode
- Version 1.01
- Added ability to toggle between Normal and Continuous scan mode. This can be activated by launching the app using the Honeywell setting
    Key remap, or by directly launching the "H&M Scanning Plugin" app from the launcher. This assumes there is a DEFAULT profile in the
    Scanning settings for the Internal scanner.

20170508_DEPlugin_HM_Multicode
- Version 1.00
- Strips whitespace from barcode data while processing for H&M multicode. When the H&M multicode is complete, this will add a '\n' newline
    to the concatenated barcode data.

20170504_DEPlugin_HM_Multicode
- Implemented H&M multicode. This concatenates three I2of5 barcodes for wedging where the first barcode starts with '3' with 12 length,
    second barcode starts with '4' with 12 length, and third barcode starts with '5' or '7' with length 8. If a barcode is read that
    does not match this criteria, the data will be wedge as is.