package com.queatz.austinhumans

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.queatz.austinhumans.clubs.ContextClub
import com.queatz.austinhumans.clubs.PermissionClub
import com.queatz.austinhumans.model.MessageModel
import com.queatz.on.On
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.item_message.view.*

class MessagesActivity : AppCompatActivity() {

    private val on = On()

    private lateinit var adapter: Adapter<MessageViewHolder, MessageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        on<ContextClub>().context = this

        sendButton.setOnClickListener {
            on<MessagesActivityClub>().onSendButtonClicked()
        }

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                on<MessagesActivityClub>().onSendButtonClicked()
            }
            false
        }

        reportButton.setOnClickListener {
            on<MessagesActivityClub>().onReportClicked()
        }

        adapter = Adapter(
                { R.layout.item_message },
                { MessageViewHolder(it) },
                { viewHolder, item: MessageModel ->
                    viewHolder.itemView.layout.layoutDirection = if (item.me) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
                    viewHolder.itemView.message.gravity = if (item.me) Gravity.END else Gravity.START
                    viewHolder.itemView.message.textAlignment = if (item.me) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START

                    Picasso.get().load(if (item.me) R.drawable.profile_1 else R.drawable.profile_2)
                            .fit()
                            .transform(RoundedCornersTransformation(2056, 0))
                            .into(viewHolder.itemView.photo)
                    viewHolder.itemView.message.text = item.message
                }
        )

        messagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        messagesRecyclerView.adapter = adapter

        closeButton.setOnClickListener {
            on<MessagesActivityClub>().closePressed()
        }

        on<MessagesActivityClub>().onViewCreated()
    }

    fun showMessages(items: MutableList<MessageModel>) {
        adapter.items = items
    }

    fun setSendText(sendText: String) {
        messageInput.setText(sendText)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        on<PermissionClub>().onPermissionResult(requestCode, permissions, grantResults)
    }
}

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}