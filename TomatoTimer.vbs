' ============================================================
'  TomatoTimer.vbs  --  Silent Windows launcher
'
'  Double-click this file to start TomatoTimer without any
'  visible console window.  It delegates to TomatoTimer.bat
'  and shows a dialog if the batch exits with an error.
'
'  Requirements: TomatoTimer.bat must be in the same folder.
' ============================================================
Option Explicit

Dim wshShell
Dim scriptDir
Dim batPath
Dim exitCode

Set wshShell = CreateObject("WScript.Shell")

' Build absolute path to the batch file next to this script
scriptDir = Left(WScript.ScriptFullName, InStrRev(WScript.ScriptFullName, "\"))
batPath    = scriptDir & "TomatoTimer.bat"

' Run the batch hidden (window style 0) and wait for it to finish
exitCode = wshShell.Run("""" & batPath & """", 0, True)

If exitCode <> 0 Then
    wshShell.Popup "TomatoTimer could not be started." & vbCrLf & vbCrLf & _
                   "Exit code: " & exitCode & vbCrLf & vbCrLf & _
                   "Possible causes:" & vbCrLf & _
                   "  - No fat JAR found in target\  (run: mvn package)" & vbCrLf & _
                   "  - javaw not found on PATH  (install Java 21+)", _
                   0, "TomatoTimer Launch Error", 16
End If

Set wshShell = Nothing

