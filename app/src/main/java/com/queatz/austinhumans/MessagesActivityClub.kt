package com.queatz.austinhumans

import com.queatz.austinhumans.clubs.AlertClub
import com.queatz.austinhumans.clubs.ContextClub
import com.queatz.austinhumans.model.MessageModel
import com.queatz.on.On
import com.queatz.on.OnLifecycle

class MessagesActivityClub(private val on: On) : OnLifecycle {

    private lateinit var activity: MessagesActivity

    override fun on() {
        activity = on<ContextClub>().context as MessagesActivity
    }

    fun onViewCreated() {
        activity.showMessages(mutableListOf(
                MessageModel("Hey thanks!"),
                MessageModel("Hey thanks!", me = true),
                MessageModel("Hey thanks!")
        ))
    }

    fun onSendButtonClicked() {
        activity.setSendText("")
    }

    fun closePressed() {
        activity.finish()
    }

    fun onReportClicked() {
        on<AlertClub>().show(R.string.report_user)
    }

}
