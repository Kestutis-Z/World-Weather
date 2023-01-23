package com.haringeymobile.ukweather.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A decoration that adds vertical margins to the items view (these margins act as a recycler
 * view list divider).
 */
public class ItemDecorationListDivider extends RecyclerView.ItemDecoration {

    private final int divider;

    public ItemDecorationListDivider(int divider) {
        this.divider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.bottom = divider;
        if (parent.getChildLayoutPosition(view) == 0)
            outRect.top = divider;
    }

}
