package org.catrobat.paintroid.ui.layer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.List;

public class TouchHelperCallback extends ItemTouchHelper.Callback {
	private static final String TAG = TouchHelperAdapter.class.getSimpleName();

	private TouchHelperAdapter adapter;
	private DragItemHelper dragItemHelper;

	TouchHelperCallback(TouchHelperAdapter adapter) {
		this.adapter = adapter;
		dragItemHelper = new DragItemHelper();
	}

	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
		return makeMovementFlags(dragFlags, swipeFlags);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		dragItemHelper.onMove(recyclerView, viewHolder, target);
		return adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		adapter.onItemDismiss(viewHolder.getAdapterPosition());
	}

	@Override
	public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
		dragItemHelper.chooseDropTarget(selected, dropTargets, curX, curY);
		return super.chooseDropTarget(selected, dropTargets, curX, curY);
	}

	@Override
	public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
		dragItemHelper.onSelectedChanged(viewHolder, actionState);
		super.onSelectedChanged(viewHolder, actionState);
	}

	private void onDrop(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		Log.d(TAG, "mergens...");
	}

	@Override
	public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		dragItemHelper.clearView(recyclerView, viewHolder);
		super.clearView(recyclerView, viewHolder);
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

	private class DragItemHelper {
		private RecyclerView.ViewHolder dropTarget;
		private RecyclerView.ViewHolder viewHolder;

		void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
			Log.d(TAG, "clearView");
			viewHolder.itemView.setAlpha(1);

			this.viewHolder = null;
			this.dropTarget = null;
		}

		void chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
			Log.d(TAG, "chooseDropTarget");
			for (RecyclerView.ViewHolder target : dropTargets) {
				int top = curY - selected.itemView.getTop();
				int bottom = curY + selected.itemView.getHeight();
				int diff;
				if (top < 0) {
					diff = target.itemView.getTop() - curY;
				} else {
					diff = target.itemView.getBottom() - bottom;
				}
				if (Math.abs(diff) < selected.itemView.getHeight() / 2) {
					Log.d(TAG, "in range...");
					if (target != dropTarget) {
						Log.d(TAG, "chooseDropTarget new target...");
						if (dropTarget != null) {
							dropTarget.itemView.setBackgroundColor(Color.TRANSPARENT);
						}
						target.itemView.setBackgroundColor(Color.RED);
						dropTarget = target;
					}
					return;
				}
			}

			if (dropTarget != null) {
				Log.d(TAG, "chooseDropTarget clear target...");
				dropTarget.itemView.setBackgroundColor(Color.TRANSPARENT);
				dropTarget = null;
			}
		}

		void onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
			viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
			target.itemView.setBackgroundColor(Color.TRANSPARENT);
			dropTarget = null;
		}

		void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
			Log.d(TAG, "onSelectedChanged");
			if (viewHolder != null) {
				this.viewHolder = viewHolder;
				if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
					viewHolder.itemView.setAlpha(.5f);
				}
			} else if (this.viewHolder != null && dropTarget != null) {
				onDrop(this.viewHolder, dropTarget);
				this.viewHolder = null;
				dropTarget = null;
			}
		}
	}
}
