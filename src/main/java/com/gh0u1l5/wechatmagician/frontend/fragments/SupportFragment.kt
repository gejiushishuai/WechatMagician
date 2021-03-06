package com.gh0u1l5.wechatmagician.frontend.fragments

import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.FileProvider.getUriForFile
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gh0u1l5.wechatmagician.Global.ACTION_REQUIRE_REPORTS
import com.gh0u1l5.wechatmagician.Global.LOG_TAG
import com.gh0u1l5.wechatmagician.Global.MAGICIAN_REPORT_PROVIDER
import com.gh0u1l5.wechatmagician.R
import com.gh0u1l5.wechatmagician.util.ViewUtil.openURL
import kotlinx.android.synthetic.main.fragment_support.*
import java.io.File

class SupportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_support, container, false)

    override fun onStart() {
        super.onStart()
        support_github_card.setOnClickListener { view ->
            openURL(activity, "${view?.context?.getString(R.string.view_about_project_github_url)}/issues")
        }
        support_email_card.setOnClickListener {
            generateReport()
        }
    }

    private fun generateReport() {
        val promptWait = getString(R.string.prompt_wait)
        Toast.makeText(activity, promptWait, Toast.LENGTH_SHORT).show()
        activity?.sendOrderedBroadcast(Intent(ACTION_REQUIRE_REPORTS), null, object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                val greetings = getString(R.string.support_report_greetings)
                sendReport(context, greetings, resultData)
            }
        }, null, RESULT_OK, null, null)
    }

    private fun sendReport(context: Context, greetings: String, reportPath: String) {
        try {
            // TODO: Fix the Xposed Log here.
//            val xposedLog = getUriForFile(context, XPOSED_FILE_PROVIDER, File("$XPOSED_BASE_DIR/log/error.log"))
            val magicianLog = getUriForFile(context, MAGICIAN_REPORT_PROVIDER, File(reportPath))
            context.startActivity(Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "text/plain"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(magicianLog))
                putExtra(Intent.EXTRA_EMAIL, arrayOf("WechatMagician@yahoo.com"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_report_subject))
                putExtra(Intent.EXTRA_TEXT, greetings)
            })
        } catch (t: Throwable) {
            Log.e(LOG_TAG, "Cannot send email: $t")
            Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(): SupportFragment = SupportFragment()
    }
}
