package org.catrobat.paintroid.ui.layer;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class TouchHelperCallback extends ItemTouchHelper.Callback {

	private TouchHelperAdapter adapter;

	public TouchHelperCallback(TouchHelperAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
		return makeMovementFlags(dragFlags, swipeFlags);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		adapter.onItemDismiss(viewHolder.getAdapterPosition());
	}

	@Override
	public boolean isLongPressDragEnabled() {
		return adapter.canMoveItems();
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		float topY = viewHolder.itemView.getTop() + dY;
		float bottomY = topY + viewHolder.itemView.getHeight();
		if (topY < 0) {
			dY = 0;
		} else if (bottomY > recyclerView.getHeight()) {
			dY = recyclerView.getHeight() - viewHolder.itemView.getHeight() - viewHolder.itemView.getTop();
		}
		if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
			float alpha = 1 - (Math.abs(dX) / recyclerView.getWidth());
			viewHolder.itemView.setAlpha(alpha);
		}
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}

	@Override
	public boolean isItemViewSwipeEnabled() {
		return adapter.canDismissItems();
	}

	interface TouchHelperAdapter {
		boolean onItemMove(int fromPosition, int toPosition);

		void onItemDismiss(int position);

		boolean canDismissItems();

		boolean canMoveItems();
	}
}
