package org.catrobat.paintroid.ui.layer;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class TouchHelperCallback extends ItemTouchHelper.Callback {

    interface TouchHelperAdapterInterface {

        boolean onItemMove(int fromPosition, int toPosition);
    }

    private TouchHelperAdapterInterface adapterInterface;

    public TouchHelperCallback(TouchHelperAdapterInterface adapterInterface) {
        this.adapterInterface = adapterInterface;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return adapterInterface.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
