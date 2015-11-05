package com.haringeymobile.ukweather;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.haringeymobile.ukweather.database.CityTable;

/**
 * An adapter to map the cities stored in the database to the city list.
 */
public abstract class BaseCityCursorAdapter extends SimpleCursorAdapter {

    /**
     * The resource ID for the view corresponding to an even cursor position.
     */
    static final int BACKGROUND_RESOURCE_EVEN = R.drawable.clickable_dark;
    /**
     * The resource ID for the view corresponding to an odd cursor position. It depends on the app
     * theme, and is resolved at runtime.
     */
    static int BACKGROUND_RESOURCE_ODD;

    /**
     * The factor by which a button drawable is to be scaled (reduced)
     */
    private static final double BUTTON_DRAWABLE_FIT_FACTOR = 0.45;

    /**
     * A listener for button clicks.
     */
    protected OnClickListener onClickListener;

    BaseCityCursorAdapter(Context context, int layout, Cursor c, String[] from,
                          int[] to, int flags, OnClickListener onClickListener) {
        super(context, layout, c, from, to, flags);
        this.onClickListener = onClickListener;

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.themed_clickable, outValue, true);
        BACKGROUND_RESOURCE_ODD = outValue.resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position % 2 == 1) {
            view.setBackgroundResource(BACKGROUND_RESOURCE_ODD);
        } else {
            view.setBackgroundResource(BACKGROUND_RESOURCE_EVEN);
        }
        return view;
    }

    /**
     * Obtains the Open Weather Map city ID for the specified list position.
     *
     * @param position the city list position
     * @return Open Weather Map city ID, or -1 if the city list does not contain the
     * specified position
     */
    int getCityId(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return cursor.getInt(cursor.getColumnIndex(CityTable.COLUMN_CITY_ID));
        }
        return CityTable.CITY_ID_DOES_NOT_EXIST;
    }

    /**
     * Obtains the city name stored in the database for the specified list position.
     *
     * @param position city list position
     * @return city name, or null if city list does not contain the specified position
     */
    String getCityName(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return cursor.getString(cursor.getColumnIndex(CityTable.COLUMN_NAME));
        }
        return null;
    }

    /**
     * Scales (reduces) button drawable height, to make the button look better
     * Code taken from
     * http://stackoverflow.com/questions/7538021/how-can-i-shrink-the-drawable-on-a-button/32479741#32479741
     *
     * @param button button with the drawable to be scaled
     */
    protected void makeButtonLookBetter(final Button button) {
        ViewTreeObserver vto = button.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                button.getViewTreeObserver().removeOnPreDrawListener(this);
                scaleButtonDrawables(button, BUTTON_DRAWABLE_FIT_FACTOR);
                return true;
            }
        });
    }

    /**
     * Scales button drawables by recalculating and resetting bounds on them
     *
     * @param button    button with the drawable, whose size should be scaled (usually reduced)
     * @param fitFactor the factor by which the button drawable is to be scaled
     */
    private void scaleButtonDrawables(Button button, double fitFactor) {
        Drawable[] drawables = button.getCompoundDrawables();

        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                int imgWidth = drawables[i].getIntrinsicWidth();
                int imgHeight = drawables[i].getIntrinsicHeight();
                if ((imgHeight > 0) && (imgWidth > 0)) {    // might be -1
                    float scale;
                    if ((i == 0) || (i == 2)) { // left or right -> scale height
                        scale = (float) (button.getHeight() * fitFactor) / imgHeight;
                    } else { // top or bottom -> scale width
                        scale = (float) (button.getWidth() * fitFactor) / imgWidth;
                    }
                    if (scale < 1.0) {
                        Rect rect = drawables[i].getBounds();
                        int newWidth = (int) (imgWidth * scale);
                        int newHeight = (int) (imgHeight * scale);
                        rect.left = rect.left + (int) (0.5 * (imgWidth - newWidth));
                        rect.top = rect.top + (int) (0.5 * (imgHeight - newHeight));
                        rect.right = rect.left + newWidth;
                        rect.bottom = rect.top + newHeight;
                        drawables[i].setBounds(rect);
                    }
                }
            }
        }
    }

}
