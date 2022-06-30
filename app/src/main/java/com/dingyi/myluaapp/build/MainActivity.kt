package com.dingyi.myluaapp.build

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.dingyi.myluaapp.build.api.databinding.MainBinding
import com.dingyi.terminal.emulator.TerminalSession
import com.dingyi.terminal.shared.TerminalSessionClientBase
import com.dingyi.terminal.shared.TerminalViewClientBase
import java.io.File
import java.util.zip.ZipFile
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {


    private lateinit var viewBinding: MainBinding


    private lateinit var terminalFontSize: FontSize


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewBinding = MainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        terminalFontSize = FontSize(this.getDefaultFontSizes())

        viewBinding.terminalView.setBackgroundColor(0xff000000.toInt())


        thread {
            extractProjectFromApk()
            /* runGradle()*/
            runOnUiThread {
                createTerminal()
            }
        }
    }

    private fun createTerminal() {

        val session = TerminalSession(
            "gradle",
            File(getDefaultProjectDir(), "TestProject").path,
            arrayOf(":app:assemble"),
            arrayOfNulls(0),
            500,
            TestTerminalSessionClient()
        )


        viewBinding.terminalView.setTextSize(terminalFontSize.getFontSize())

        viewBinding.terminalView.setTerminalViewClient(TestTerminalViewClient())

        viewBinding.terminalView.attachSession(session)


    }

    private fun extractProjectFromApk() {
        val apkFile = File(packageCodePath)
        val extractDir = getDefaultProjectDir()
        extractDir.mkdirs()
        val zipFile = ZipFile(apkFile)
        zipFile.entries().asSequence()
            .filter {
                it.name.startsWith("assets/")
            }.forEach {
                val file = File(extractDir, it.name.replace("assets/",""))
                println(file)
                file.parentFile?.mkdirs()
                file.createNewFile()
                zipFile.getInputStream(it).use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

        zipFile.close()
    }

    private fun getDefaultProjectDir(): File {
        return checkNotNull(getExternalFilesDir(null)) {
            "External files dir is null"
        }
    }


    inner class TestTerminalViewClient : TerminalViewClientBase() {

        private val LOG_TAG = "TestTerminalViewClient"

        private var mTerminalCursorBlinkerStateAlreadySet = false

        override fun onEmulatorSet() {
            if (!mTerminalCursorBlinkerStateAlreadySet) {
                // Start terminal cursor blinking if enabled
                // We need to wait for the first session to be attached that's set in
                // TermuxActivity.onServiceConnected() and then the multiple calls to TerminalView.updateSize()
                // where the final one eventually sets the mEmulator when width/height is not 0. Otherwise
                // blinker will not start again if TermuxActivity is started again after exiting it with
                // double back press. Check TerminalView.setTerminalCursorBlinkerState().
                setTerminalCursorBlinkerState(true);
                mTerminalCursorBlinkerStateAlreadySet = true;
            }
        }

        private fun setTerminalCursorBlinkerState(start: Boolean) {
            if (start) {
                // If set/update the cursor blinking rate is successful, then enable cursor blinker
                if (viewBinding.terminalView.setTerminalCursorBlinkerRate(500)) {
                    viewBinding.terminalView
                        .setTerminalCursorBlinkerState(true, true)
                } else {
                    logError(LOG_TAG, "Failed to start cursor blinker")
                }
            } else {
                // Disable cursor blinker
                viewBinding.terminalView.setTerminalCursorBlinkerState(false, true)
            }
        }

        override fun onScale(scale: Float): Float {
            if (scale < 0.9f || scale > 1.1f) {
                val increase = scale > 1f
                changeFontSize(increase)
                return 1.0f
            }
            return scale
        }

        override fun onSingleTapUp(e: MotionEvent?) {

        }


        private fun changeFontSize(increase: Boolean) {
            terminalFontSize.changeFontSize(increase)
            viewBinding.terminalView.setTextSize(terminalFontSize.getFontSize())
        }

    }

    inner class TestTerminalSessionClient : TerminalSessionClientBase() {
        override fun onTextChanged(changedSession: TerminalSession) {
            viewBinding.terminalView.onScreenUpdated()
        }

        override fun onCopyTextToClipboard(session: TerminalSession, text: String?) {

            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text))
        }

        override fun onPasteTextFromClipboard(session: TerminalSession?) {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val text = clipboardManager.primaryClip
                ?.getItemAt(0)
                ?.coerceToText(this@MainActivity)
                .toString()

            session?.write(text)

        }


        override fun onSessionFinished(finishedSession: TerminalSession) {
            viewBinding.terminalView.setTerminalCursorBlinkerState(false, true)
        }

        override fun onColorsChanged(session: TerminalSession) {
            /* val colors = session.emulator.mColors
             viewBinding
                 .terminalView
                 .setBackgroundColor(colors.mCurrentColors[TextStyle.COLOR_INDEX_BACKGROUND])
       */
        }


        override fun onTerminalCursorStateChange(state: Boolean) {
            viewBinding
                .terminalView
                .setTerminalCursorBlinkerState(state, false)
        }
    }

    override fun onStop() {
        super.onStop()
        viewBinding.terminalView.setTerminalCursorBlinkerState(false, true)
    }

}