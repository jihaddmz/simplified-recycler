package com.jihaddmz.simplified_recycler

import android.animation.ObjectAnimator
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class BaseRecyclerAdapter<VB : ViewBinding, T>(
    private val viewBindingProvider: (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB,
    private var list: MutableList<T>,
    private val context: Context,
    private val onBindItem: BaseRecyclerAdapter<VB, T>.ViewHolder.(T, Int) -> Unit,
) : RecyclerView.Adapter<BaseRecyclerAdapter<VB, T>.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            viewBindingProvider.invoke(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.onBindItem(item, position)

    }

    /**
     * Method for updating the list passed to the adapter, and notifying the adapter
     * the current position/layoutPosition
     **/
    fun updateList(newList: MutableList<T>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val itemBinding: VB) : RecyclerView.ViewHolder(itemBinding.root) {

        private var isButtonsVisible = false
        private fun showActionButtons(
            llForeground: ViewGroup, moveXPosBy: Double = 0.5, onSwiped: () -> Unit
        ) {
            if (!isButtonsVisible) {
                val animator = ObjectAnimator.ofFloat(
                    llForeground, "translationX", -(llForeground.width * moveXPosBy).toFloat()
                )
                animator.setDuration(300)
                animator.start()
                animator.doOnEnd {
                    isButtonsVisible = true
                    onSwiped()
                }
            }
        }

        private fun hideActionButtons(llForeground: ViewGroup, onSwipeReset: () -> Unit) {
            if (isButtonsVisible) {
                val animator: ObjectAnimator =
                    ObjectAnimator.ofFloat(llForeground, "translationX", 0f)
                animator.setDuration(300)
                animator.start()
                animator.doOnEnd {
                    isButtonsVisible = false
                    onSwipeReset()
                }
            }
        }

        /**
         * if you want to delete the current item, just call this method and pass
         * the current position/layoutPosition
         **/
        fun clearItem(position: Int) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }

        /**
         * this method is called whenever you have updated
         * the current item
         **/
        fun updateItem(position: Int, llForeground: ViewGroup? = null, onSwipeReset: () -> Unit = {}) {
            notifyItemChanged(position)
            if (isButtonsVisible && llForeground != null) hideActionButtons(llForeground, onSwipeReset)
        }

        /**
         * method responsible for adding an item at specified position
         **/
        fun addItem(position: Int, item: T) {
            list.add(position, item)
            notifyItemInserted(position)
        }

        /**
         * call this method to enable swipe to action on recycler item
         **/
        fun enableSwipe(
            llForeground: ViewGroup,
            moveXPosBy: Double = 0.5,
            onSwiped: () -> Unit = {},
            onSwipeReset: () -> Unit = {}
        ) {
            val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
                ): Boolean {
                    if (e1!!.x - e2.x > 10) { // swipe left
                        showActionButtons(llForeground, moveXPosBy, onSwiped)
                        return true
                    } else if (e2.x - e1.x > 10) { // swipe right
                        hideActionButtons(llForeground, onSwipeReset)
                        return true
                    }
                    return false
                }
            })

            llForeground.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    gestureDetector.onTouchEvent(event!!)
                    return true
                }
            })

        }

    }
}

fun <VB : ViewBinding, D> simplifiedRecycler(
    viewBindingProvider: (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB,
    items: MutableList<D>,
    context: Context,
    onBindItem: BaseRecyclerAdapter<VB, D>.ViewHolder.(D, Int) -> Unit,
): BaseRecyclerAdapter<VB, D> =
    BaseRecyclerAdapter(viewBindingProvider, items, context, onBindItem = { item, position ->
        onBindItem(item, position)
    })