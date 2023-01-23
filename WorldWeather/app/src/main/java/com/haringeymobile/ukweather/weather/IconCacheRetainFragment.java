package com.haringeymobile.ukweather.weather;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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