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
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.ui.DrawingSurface;

public class LayerFragment extends Fragment implements LayerAdapter.OnLayerClickListener {

	private LayerAdapter adapter;
	private Layer currentLayer;
	private ItemTouchHelper itemTouchHelper;

	private int layerIdCounter = 0;
	private String TAG = LayerFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		adapter = new LayerAdapter(MainActivity.layers);
		adapter.setClickListener(this);

		selectLayer(adapter.get(0));

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layer_dialog, container, false);

		view.findViewById(R.id.layer_button_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//TODO: ADD new Layer
				if (adapter.getItemCount() < 4) {
					Toast.makeText(getContext(), "add", Toast.LENGTH_SHORT).show();
					DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
					Bitmap image = Bitmap.createBitmap(drawingSurface.getBitmapWidth(),
							drawingSurface.getBitmapHeight(), Bitmap.Config.ARGB_8888);
					adapter.add(new Layer(layerIdCounter++, image));
				}
			}
		});

		view.findViewById(R.id.layer_button_delete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
				if (adapter.getItemCount() > 1) {
					adapter.remove(currentLayer);
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
		if (currentLayer != null) {
			currentLayer.setSelected(false);
			currentLayer.setImage(drawingSurface.getBitmapCopy());
			adapter.notifyDataSetChanged();
		}

		currentLayer = layer;
		currentLayer.setSelected(true);

		drawingSurface.setLock(currentLayer.getLocked());
		drawingSurface.setVisible(currentLayer.getVisible());
		drawingSurface.setBitmap(currentLayer.getImage());
	}

	public void refresh() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLayerClick(Layer layer) {
		selectLayer(layer);
		UndoRedoManager.getInstance().update();
	}

	@Override
	public void onLayerLongClick(LayerAdapter.LayerViewHolder holder) {
		itemTouchHelper.startDrag(holder);
	}
}
