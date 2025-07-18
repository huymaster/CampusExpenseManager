package com.github.huymaster.campusexpensemanager.core;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import com.github.huymaster.campusexpensemanager.MainApplication;

public class ResourceFunctions {
    private ResourceFunctions() {
    }

    private static Resources getResources() {
        return MainApplication.INSTANCE.getResources();
    }

    private static Resources.Theme getTheme() {
        return MainApplication.INSTANCE.getTheme();
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return ResourcesCompat.getDrawable(getResources(), id, getTheme());
    }

    public static int getAttr(@AttrRes int id) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(id, typedValue, true);
        return typedValue.data;
    }

    public static int getColor(@ColorRes int id) {
        return ResourcesCompat.getColor(getResources(), id, getTheme());
    }

    public static String getString(@StringRes int id) {
        return getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }
}
