package com.queatz.austinhumans

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.queatz.austinhumans.model.PersonModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_people.view.*
import kotlinx.android.synthetic.main.item_people_detail.view.*
import kotlinx.android.synthetic.main.item_people_detail.view.name as detailName


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var selectedPosition = BehaviorSubject.createDefault(0)
        var compositeDisposable = CompositeDisposable()

        val adapter = Adapter(
                { R.layout.item_people },
                { PeopleViewHolder(it) },
                { viewHolder, item: PersonModel ->
                    viewHolder.itemView.name.text = item.name
                    viewHolder.itemView.newMessagesIndicator.visibility = if (item.name == "Amanda" || item.name == "Esther") View.VISIBLE else View.GONE

                    viewHolder.disposable?.let { compositeDisposable.remove(it) }
                    viewHolder.disposable = selectedPosition.subscribe {
                        viewHolder.itemView.isSelected = it == viewHolder.adapterPosition
                    }
                    compositeDisposable.add(viewHolder.disposable!!)

                    viewHolder.itemView.setOnClickListener {
                        selectedPosition.onNext(viewHolder.adapterPosition)
                        viewHolder.itemView.isSelected = true

                        peopleDetailRecyclerView.isLayoutFrozen = false
                        val smoothScroller = object : LinearSmoothScroller(this) {
                            override fun getHorizontalSnapPreference(): Int {
                                return LinearSmoothScroller.SNAP_TO_START
                            }

                            override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
                                return super.calculateDxToMakeVisible(view, snapPreference) -
                                        resources.getDimensionPixelSize(R.dimen.pad2x) / 2

                            }
                        }

                        smoothScroller.targetPosition = viewHolder.adapterPosition

                        (peopleDetailRecyclerView.layoutManager as LinearLayoutManager).startSmoothScroll(smoothScroller)
                    }
                }
        )

        val items = mutableListOf(
                PersonModel("Alice"),
        PersonModel("Amanda"),
        PersonModel("Esther"),
        PersonModel("Godwin"),
        PersonModel("Marshall"),
        PersonModel("Sosamma"),
        PersonModel("Amanda"),
        PersonModel("Esther"),
        PersonModel("Godwin"),
        PersonModel("Marshall"),
        PersonModel("Sosamma"))

        adapter.items = items
        peopleRecyclerView.adapter = adapter
        peopleRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val detailAdapter = Adapter(
                { R.layout.item_people_detail },
                {
                    val viewHolder = PeopleDetailViewHolder(it)
                    peopleDetailRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                                viewHolder.itemView.contentScrollView.smoothScrollTo(0, 0)
                            }
                        }
                    })

                    return@Adapter viewHolder
                },
                { viewHolder, item: PersonModel ->
                    viewHolder.itemView.detailName.text = item.name
                },
                { peopleDetailRecyclerView.post {
                    peopleDetailRecyclerView.scrollBy(resources.getDimensionPixelSize(R.dimen.pad2x) / 2, 0)
                } })

        detailAdapter.items = items

        val decorator = MarginItemDecorator(resources.getDimensionPixelSize(R.dimen.pad2x))
        peopleDetailRecyclerView.addItemDecoration(decorator)

        peopleDetailRecyclerView.adapter = detailAdapter
        peopleDetailRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        peopleDetailRecyclerView.onInterceptTouchEventListener = { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    peopleDetailRecyclerView.findChildViewUnder(event.x, event.y)!!.let {
                        peopleDetailRecyclerView.isLayoutFrozen = event.y < it.contentScrollView.paddingTop - it.contentScrollView.scrollY
                    }
                }
                MotionEvent.ACTION_UP -> { peopleDetailRecyclerView.isLayoutFrozen = false }
            }
            false
        }

        peopleDetailRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    selectedPosition.onNext((peopleDetailRecyclerView.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition())
                }
            }
        })

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(peopleDetailRecyclerView)
    }
}

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

class MarginItemDecorator(private val padding: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = padding / 2
        outRect.right = padding / 2
        view.layoutParams.width = parent.width
        view.content.minimumHeight = parent.height - view.contentScrollView.paddingTop
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

class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var disposable: Disposable? = null
}
class PeopleDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}