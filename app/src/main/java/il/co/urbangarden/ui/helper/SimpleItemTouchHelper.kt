package il.co.urbangarden.ui.helper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*

import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.ui.location.LocationAdapter


class SimpleItemTouchHelperCallback(adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    private var itemAdapter: ItemTouchHelperAdapter = adapter


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = UP or DOWN or LEFT or RIGHT
        val swipeFlags = START or END
        return makeMovementFlags(dragFlags, swipeFlags)
    }


    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemAdapter.onItemDismiss(viewHolder.bindingAdapterPosition);
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        itemAdapter.onItemMove(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition);
        return true;
    }


}