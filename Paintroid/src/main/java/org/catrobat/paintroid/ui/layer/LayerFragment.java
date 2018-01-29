/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.layer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.listener.LayerHolder;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.ui.DrawingSurface;

public class LayerFragment extends Fragment implements LayerAdapter.OnLayerClickListener {

	public static final int MAX_LAYER_COUNT = 4;
	private LayerAdapter adapter;
	private Layer selectedLayer;
	private ItemTouchHelper itemTouchHelper;

	private String TAG = LayerFragment.class.getSimpleName();
	private View addButton;
	private View deleteButton;
	private RecyclerView.AdapterDataObserver observer;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		adapter = new LayerAdapter(LayerHolder.getInstance().getLayers());

		selectLayer(adapter.get(0));
		observer = new LayerAdapterDataObserver();
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.setClickListener(this);
		adapter.registerAdapterDataObserver(observer);
	}

	@Override
	public void onPause() {
		super.onPause();
		adapter.setClickListener(null);
		adapter.unregisterAdapterDataObserver(observer);
	}

	private void updateButtonStatus() {
		int itemCount = adapter.getItemCount();
		addButton.setEnabled(itemCount < MAX_LAYER_COUNT);
		deleteButton.setEnabled(itemCount > 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layer_dialog, container, false);

		addButton = view.findViewById(R.id.layer_button_add);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//TODO: ADD new Layer
				if (adapter.getItemCount() < 4) {
					DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
					Bitmap image = Bitmap.createBitmap(drawingSurface.getBitmapWidth(),
							drawingSurface.getBitmapHeight(), Bitmap.Config.ARGB_8888);
					adapter.add(LayerHolder.getInstance().createLayer(image));
				}
			}
		});

		deleteButton = view.findViewById(R.id.layer_button_delete);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (adapter.getItemCount() > 1) {
					adapter.remove(selectedLayer);
				}
			}
		});

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		recyclerView.setAdapter(adapter);

		ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
		itemTouchHelper = new ItemTouchHelper(callback);
		itemTouchHelper.attachToRecyclerView(recyclerView);

		return view;
	}

	public void selectLayer(Layer layer) {
		Log.e(TAG, "selectLayer");
		final DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
		if (selectedLayer != null) {
			selectedLayer.setSelected(false);
			selectedLayer.setImage(drawingSurface.getBitmapCopy());
			adapter.notifyDataSetChanged();
		}

		selectedLayer = layer;
		selectedLayer.setSelected(true);
//		LayerHolder.getInstance().setCurrentLayer(selectedLayer);

		drawingSurface.setLock(selectedLayer.getLocked());
		drawingSurface.setVisible(selectedLayer.getVisible());
		drawingSurface.setBitmap(selectedLayer.getImage());
	}

	public void refresh() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLayerClick(Layer layer) {
		selectLayer(layer);
		UndoRedoManager.getInstance().update();
	}

	private class LayerAdapterDataObserver extends RecyclerView.AdapterDataObserver {
		private void refreshDrawingSurface() {
			PaintroidApplication.drawingSurface.refreshDrawingSurface();
		}

		@Override
		public void onChanged() {
			refreshDrawingSurface();
			updateButtonStatus();
			selectedLayer = adapter.getSelectedLayer();
//			LayerHolder.getInstance().setCurrentLayer(selectedLayer);
		}

		@Override
		public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
			refreshDrawingSurface();
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			refreshDrawingSurface();
			updateButtonStatus();
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			refreshDrawingSurface();
			updateButtonStatus();
			selectedLayer = adapter.getSelectedLayer();
//			LayerHolder.getInstance().setCurrentLayer(selectedLayer);
		}
	}
}
