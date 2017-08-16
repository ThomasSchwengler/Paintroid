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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.LayerViewHolder> implements
        TouchHelperCallback.TouchHelperAdapterInterface {

    List<Layer> mLayers = new ArrayList<>();
    OnLayerClickListener mListener;

    class LayerViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        LayerViewHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.layer_view);
        }
    }

    public void setClickListener(OnLayerClickListener listener) {
        mListener = listener;
    }

    @Override
    public LayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layer_view_holder, parent, false);
        return new LayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LayerViewHolder holder, int position) {
        final Layer layer = mLayers.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLayerClick(layer);
            }
        });

        holder.mImageView.setImageBitmap(layer.getImage());
    }

    public Layer get(int position) {
        return mLayers.get(position);
    }

    public void add(Layer layer) {
        mLayers.add(layer);
    }

    public void remove(Layer layer) {
        mLayers.remove(layer);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mLayers, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mLayers.size();
    }

    public interface OnLayerClickListener {

        void onLayerClick(Layer layer);
    }
}
