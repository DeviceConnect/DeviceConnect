#!/bin/sh

xcodebuild -scheme dConnectDeviceTest -configuration Debug -sdk iphonesimulator clean test OBJROOT=build SYMROOT=build 2>&1 | ocunit2junit
ant -f xctest-report.xml
