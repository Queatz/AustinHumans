package com.queatz.austinhumans

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.queatz.austinhumans.model.MessageModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.item_message.view.*

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        sendButton.setOnClickListener {
            messageInput.setText("")
        }

        messageInput.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    messageInput.setText("")
                }
            }

            false
        }

        val adapter = Adapter(
                { R.layout.item_message },
                { MessageViewHolder(it) },
                { viewHolder, item: MessageModel ->
                    viewHolder.itemView.layout.layoutDirection = if (item.me) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
                    viewHolder.itemView.message.gravity = if (item.me) Gravity.END else Gravity.START

                    Picasso.get().load(if (item.me) R.drawable.profile_1 else R.drawable.profile_2)
                            .fit()
                            .transform(RoundedCornersTransformation(2056, 0))
                            .into(viewHolder.itemView.photo)
                    viewHolder.itemView.message.text = item.message
                }
        )

        adapter.items = mutableListOf(
            MessageModel("Hey thanks!"),
            MessageModel("Hey thanks!", me = true),
            MessageModel("Hey thanks!")
        )

        messagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        messagesRecyclerView.adapter = adapter

        closeButton.setOnClickListener { finish() }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}