package com.dingyi.terminal.shared;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dingyi.terminal.emulator.Logger;
import com.dingyi.terminal.emulator.TerminalSession;
import com.dingyi.terminal.emulator.TerminalSessionClient;

public class TerminalSessionClientBase implements TerminalSessionClient {
    @Override
    public void onTextChanged(@NonNull TerminalSession changedSession) {

    }

    @Override
    public void onTitleChanged(@NonNull TerminalSession changedSession) {

    }

    @Override
    public void onSessionFinished(@NonNull TerminalSession finishedSession) {

    }

    @Override
    public void onCopyTextToClipboard(@NonNull TerminalSession session, String text) {

    }

    @Override
    public void onPasteTextFromClipboard(@Nullable TerminalSession session) {

    }

    @Override
    public void onBell(@NonNull TerminalSession session) {

    }

    @Override
    public void onColorsChanged(@NonNull TerminalSession session) {

    }

    @Override
    public void onTerminalCursorStateChange(boolean state) {

    }

    @Override
    public void setTerminalShellPid(@NonNull TerminalSession session, int pid) {

    }

    @Override
    public Integer getTerminalCursorStyle() {
        return null;
    }

    @Override
    public void logError(String tag, String message) {
        Logger.logError(null, tag, message);
    }

    @Override
    public void logWarn(String tag, String message) {
        Logger.logWarn(null, tag, message);
    }

    @Override
    public void logInfo(String tag, String message) {
        Logger.logInfo(null, tag, message);
    }

    @Override
    public void logDebug(String tag, String message) {
        Logger.logDebug(null, tag, message);
    }

    @Override
    public void logVerbose(String tag, String message) {
        Logger.logVerbose(null, tag, message);
    }

    @Override
    public void logStackTraceWithMessage(String tag, String message, Exception e) {
        Logger.logStackTraceWithMessage(null, tag, message, e);
    }

    @Override
    public void logStackTrace(String tag, Exception e) {
        logStackTraceWithMessage(tag, e.getMessage(), e);
    }
}
