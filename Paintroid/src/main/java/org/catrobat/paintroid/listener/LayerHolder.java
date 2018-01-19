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

package org.catrobat.paintroid.listener;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;
import java.util.List;

public final class LayerHolder {
	private static final String TAG = LayerHolder.class.getSimpleName();
	private static LayerHolder instance;
	private Layer currentLayer;
	private List<Layer> layers;
	private int layerIdCounter = 0;

	private LayerHolder() {
		Bitmap bitmap = PaintroidApplication.drawingSurface.getBitmapCopy();
		layers = new ArrayList<>();
		currentLayer = createLayer(bitmap);
		layers.add(currentLayer);
	}

	public static LayerHolder getInstance() {
		if (instance == null) {
			instance = new LayerHolder();
		}
		return instance;
	}

	public Layer getCurrentLayer() {
		return currentLayer;
	}

	public void setCurrentLayer(Layer currentLayer) {
		this.currentLayer = currentLayer;
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public Bitmap getBitmapOfAllLayersToSave() {
		Bitmap firstBitmap = layers.get(layers.size() - 1).getImage();
		Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(), firstBitmap.getConfig());
		Canvas canvas = new Canvas(bitmap);
		Paint overlayPaint = new Paint();
		overlayPaint.setAlpha(layers.get(layers.size() - 1).getScaledOpacity());
		canvas.drawBitmap(firstBitmap, new Matrix(), overlayPaint);

		if (layers.size() > 1) {
			for (int i = layers.size() - 2; i >= 0; i--) {
				overlayPaint.setAlpha(layers.get(i).getScaledOpacity());
				canvas.drawBitmap(layers.get(i).getImage(), 0, 0, overlayPaint);
			}
		}

		return bitmap;
	}

	public int getPosition(int layerID) {
		int i;
		for (i = 0; i < layers.size(); i++) {
			if (layers.get(i).getLayerID() == layerID) {
				break;
			}
		}
		return i;
	}

	public void resetLayer() {
		Layer layer = clearLayer();
		setCurrentLayer(layer);
	}

	private Layer clearLayer() {
		layers.clear();
		layerIdCounter = 0;
		Bitmap bitmap = Bitmap.createBitmap(PaintroidApplication.drawingSurface.getBitmapWidth(),
				PaintroidApplication.drawingSurface.getBitmapHeight(), Bitmap.Config.ARGB_8888);
		Layer layer = createLayer(bitmap);
		layers.add(layer);
		return layer;
	}

	public Layer createLayer(Bitmap bitmap) {
		return new Layer(layerIdCounter++, bitmap);
	}
}
