# Roblox-NativeShell-Decomp

This project aims to decompile the Roblox Android App and recompile it with gradle. This could be useful for modding, configuring for custom webservers or just analyzing code.
This might not be 100% accurate but thats not exactly the point, the most important part is whether the app works identically to original APK.

# Useful Info:

Currently Targeted APK version: 1.0

## The APK's are gathered from Yakov's Mobile Archive Website which can be found here:
https://yakov5776.github.io/ultimaterobloxmobilearchive/

## Issues
 - The res folder has folders with v4 appended and some pngs arent identical to what orginal APK has.
 - Crashlytics 1.X hasn't been found and wasnt included but should be
 - Generated APK has bigger compression level than what Original APK has (yet to confirm)

## TODO:
 - Resolve Issues above
 - Figure the build config Roblox Team used in 2014

