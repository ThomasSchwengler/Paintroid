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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Layer;

public class LayerDialog extends DialogFragment implements LayerAdapter.OnLayerClickListener {

    private RecyclerView mView;
    private LayerAdapter mAdapter;
    private Layer currentLayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        mAdapter = new LayerAdapter();
        mAdapter.setClickListener(this);

        currentLayer = mAdapter.get(0);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (RecyclerView) inflater.inflate(R.layout.layer_dialog, container, false);

        mView.findViewById(R.id.mBtnNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: ADD new Layer
            }
        });

        mView.findViewById(R.id.mBtnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.remove(currentLayer);
            }
        });

        mView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new TouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mView);

        return mView;
    }

    @Override
    public void onLayerClick(Layer layer) {
        //TODO: do something onClick
        // e.g. set layer as current layer
        currentLayer = layer;
    }
}
