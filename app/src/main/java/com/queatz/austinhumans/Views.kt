package com.queatz.austinhumans

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class SheetRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var onInterceptTouchEventListener: ((view: View, event: MotionEvent) -> Boolean)? = null

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (onInterceptTouchEventListener?.invoke(this, event) == true) {
            true
        } else {
            super.onInterceptTouchEvent(event)
        }
    }
}

class ScrollSheet : ScrollView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        overScrollMode = ScrollView.OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (event.y > paddingTop - scrollY) super.onTouchEvent(event) else false
    }

}

class Adapter<VH : RecyclerView.ViewHolder, T> constructor(
        @LayoutRes private val layoutResId: (viewType: Int) -> Int,
        private val viewHolder: (itemView: View) -> VH,
        private val onBind: (viewHolder: VH, item: T) -> Unit,
        private val onDataChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<VH>() {

        var items = mutableListOf<T>()
            set(value) {
                field.clear()
                field.addAll(value)
                notifyDataSetChanged()
                onDataChanged?.invoke()
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            viewHolder.invoke(LayoutInflater.from(parent.context)
                    .inflate(layoutResId.invoke(viewType), parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) = onBind.invoke(holder, items[position])

        override fun getItemCount() = items.size
}