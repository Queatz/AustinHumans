package com.queatz.austinhumans

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.queatz.austinhumans.clubs.AlertClub
import com.queatz.austinhumans.clubs.ContextClub
import com.queatz.austinhumans.clubs.PermissionClub
import com.queatz.austinhumans.clubs.SnackbarClub
import com.queatz.austinhumans.model.HumanModel
import com.queatz.on.On
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_goal.view.*
import kotlinx.android.synthetic.main.item_people.view.*
import kotlinx.android.synthetic.main.item_people_detail.view.*
import kotlinx.android.synthetic.main.item_people_detail.view.name as detailName


class MainActivity : AppCompatActivity() {

    private val on = On()

    private lateinit var adapter: Adapter<PeopleViewHolder, HumanModel>
    private lateinit var detailAdapter: Adapter<PeopleDetailViewHolder, HumanModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        on<ContextClub>().context = this

        val selectedPosition = BehaviorSubject.createDefault(0)
        val compositeDisposable = CompositeDisposable()

        adapter = Adapter(
                { R.layout.item_people },
                { PeopleViewHolder(it) },
                { viewHolder, human: HumanModel ->
                    viewHolder.itemView.name.text = human.name
                    viewHolder.itemView.newMessagesIndicator.visibility = if (human.name == "Amanda" || human.name == "Esther") View.VISIBLE else View.GONE

                    viewHolder.disposable?.let { compositeDisposable.remove(it) }
                    viewHolder.disposable = selectedPosition.subscribe {
                        viewHolder.itemView.photo.isSelected = it == viewHolder.adapterPosition
                        peopleRecyclerView.smoothScrollToPosition(it)
                    }
                    compositeDisposable.add(viewHolder.disposable!!)

                    Picasso.get().load(listOf(
                            R.drawable.profile_1,
                            R.drawable.profile_2,
                            R.drawable.profile_3,
                            R.drawable.profile_4,
                            R.drawable.profile_5
                    )[viewHolder.adapterPosition % 5])
                            .fit()
                            .transform(RoundedCornersTransformation(2056, 0))
                            .into(viewHolder.itemView.photo)

                    viewHolder.itemView.setOnClickListener {
                        if (selectedPosition.value == viewHolder.adapterPosition) {
                            if (human.me) {
                                on<AlertClub>().show(R.string.change_photo)
                            } else {
                                startActivity(Intent(this, MessagesActivity::class.java))
                            }
                        } else {
                            selectedPosition.onNext(viewHolder.adapterPosition)
                        }

                        viewHolder.itemView.photo.isSelected = true

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

        peopleRecyclerView.adapter = adapter
        peopleRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        detailAdapter = Adapter(
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

                    viewHolder.itemView.goalsRecyclerView.layoutManager = LinearLayoutManager(this)

                    return@Adapter viewHolder
                },
                { viewHolder, human: HumanModel ->
                    viewHolder.itemView.detailName.text = human.name

                    if (human.me) {
                        viewHolder.itemView.detailName.setOnClickListener {
                            AlertDialog.Builder(this)
                                    .setPositiveButton(R.string.change_name) { dialog, which ->  }
                                    .show()
                        }
                    } else {
                        viewHolder.itemView.detailName.setOnClickListener {  }
                    }

                    val adapter = Adapter(
                            { R.layout.item_goal },
                            { GoalViewHolder(it) },
                            { viewHolder, item: String ->
                                viewHolder.itemView.goalName.text = item

                                if (human.me) {
                                    viewHolder.itemView.cheerButton.text = getString(R.string.change_goal)
                                    viewHolder.itemView.setOnClickListener {
                                        AlertDialog.Builder(this)
                                                .setPositiveButton(R.string.change_goal) { dialog, which ->  }
                                                .show()
                                    }

                                } else {
                                    viewHolder.itemView.cheerButton.text = getString(R.string.cheer_person, human.name)
                                    viewHolder.itemView.setOnClickListener {
                                        startActivity(Intent(this, MessagesActivity::class.java))
                                    }
                                }
                            }
                    )

                    viewHolder.itemView.goalsRecyclerView.adapter = adapter

                    // TODO("Move these out")
                    adapter.items = mutableListOf(
                            "\uD83C\uDF32 Spend a week in Upstate NY",
                            "Learn how to dance",
                            "Start doing yoga regularly \uD83E\uDDD8"
                    )
                },
                { peopleDetailRecyclerView.post { peopleDetailRecyclerView.scrollBy(resources.getDimensionPixelSize(R.dimen.pad2x) / 2, 0) } })

        val decorator = MarginItemDecorator(resources.getDimensionPixelSize(R.dimen.pad2x))
        peopleDetailRecyclerView.addItemDecoration(decorator)

        peopleDetailRecyclerView.adapter = detailAdapter
        peopleDetailRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        peopleDetailRecyclerView.onInterceptTouchEventListener = { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    peopleDetailRecyclerView.findChildViewUnder(event.x, event.y)?.let {
                        peopleDetailRecyclerView.isLayoutFrozen = event.y < it.contentScrollView.paddingTop - it.contentScrollView.scrollY
                    } ?: run {
                        peopleDetailRecyclerView.isLayoutFrozen = false
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

        useLocationButton.setOnClickListener {
            on<MainActivityClub>().onUseLocationClicked()
        }

        on<MainActivityClub>().onViewCreated()
    }

    fun showHumans(items: MutableList<HumanModel>) {
        adapter.items = items
        detailAdapter.items = items
    }

    fun sortByDistance(sortByDistance: Boolean) {
        useLocationButton.setImageResource(if (sortByDistance) R.drawable.ic_my_location_black_24dp else R.drawable.ic_location_disabled_black_24dp)
        on<SnackbarClub>().show(if (sortByDistance) R.string.sort_by_distance_enabled else R.string.sort_by_distance_disabled)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        on<PermissionClub>().onPermissionResult(requestCode, permissions, grantResults)
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

class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var disposable: Disposable? = null
}
class PeopleDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}