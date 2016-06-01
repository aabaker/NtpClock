# NtpClock
A clock that displays the time obtained from an NTP server.

Licensed under the Apache 2.0 license http://www.apache.org/licenses/LICENSE-2.0

This program is intended to provide a simple but accurate clock. If using the automatic
option on a phone to set the time it is, in most cases, uses the time sent by the
basestation which for some operators isn't always reliable. It also provides no
indication of when it was last corrected.

Most NTP apps for Android provide an option to set the system clock and hence expect
root access. That is explicitly not a goal for this app.

The SntpClient code is taken from the AOSP project, it exists in the standard android libraries
but that copy is hidden so normal apps can't use it.
