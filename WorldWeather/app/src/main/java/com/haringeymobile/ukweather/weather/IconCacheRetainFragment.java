package com.haringeymobile.ukweather.weather;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;

/**
 * A fragment to store weather icon cache during orientation changes.
 */
public class IconCacheRetainFragment extends Fragment {

    private static final String TAG = "Cache fragment";
    public LruCache<String, Bitmap> iconCache;

    public static IconCacheRetainFragment findOrCreateRetainFragment(
            FragmentManager fragmentManager) {
        IconCacheRetainFragment fragment = (IconCacheRetainFragment) fragmentManager
                .findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new IconCacheRetainFragment();
            fragmentManager.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

}