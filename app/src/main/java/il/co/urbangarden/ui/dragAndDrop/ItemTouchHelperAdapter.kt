package il.co.urbangarden.ui.dragAndDrop

interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int) : Boolean

    fun onItemDismiss(position: Int) : Boolean
}