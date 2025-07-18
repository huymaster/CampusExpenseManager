package com.github.huymaster.campusexpensemanager.core;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.snackbar.Snackbar;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ViewFunctions {
    private ViewFunctions() {
    }

    public static String getTextOrEmpty(TextView textView) {
        CharSequence text = textView.getText();
        return (text == null || text.length() == 0) ? "" : text.toString();
    }


    public static void showSnackbar(@NonNull ViewBinding binding, @StringRes int message) {
        showSnackbar(binding.getRoot(), message, Snackbar.LENGTH_SHORT, null);
    }

    public static void showSnackbar(@NonNull ViewBinding binding, @NonNull CharSequence message) {
        showSnackbar(binding.getRoot(), message, Snackbar.LENGTH_SHORT, null);
    }

    public static void showSnackbar(@NonNull View view, @StringRes int message) {
        showSnackbar(view, message, Snackbar.LENGTH_SHORT, null);
    }

    public static void showSnackbar(@NonNull View view, @NonNull CharSequence message) {
        showSnackbar(view, message, Snackbar.LENGTH_SHORT, null);
    }

    public static void showSnackbar(@NonNull ViewBinding binding, @StringRes int message, int duration) {
        showSnackbar(binding.getRoot(), message, duration, null);
    }

    public static void showSnackbar(@NonNull ViewBinding binding, @NonNull CharSequence message, int duration) {
        showSnackbar(binding.getRoot(), message, duration, null);
    }

    public static void showSnackbar(@NonNull View view, @StringRes int message, int duration) {
        Resources resources = view.getResources();
        showSnackbar(view, resources.getString(message), duration, null);
    }

    public static void showSnackbar(@NonNull View view, @NonNull CharSequence message, int duration) {
        showSnackbar(view, message, duration, null);
    }

    public static void showSnackbar(@NonNull ViewBinding binding, @StringRes int message, int duration, @Nullable Consumer<Snackbar> builder) {
        showSnackbar(binding.getRoot(), message, duration, builder);
    }

    public static void showSnackbar(@NonNull ViewBinding binding, @NonNull CharSequence message, int duration, @Nullable Consumer<Snackbar> builder) {
        showSnackbar(binding.getRoot(), message, duration, builder);
    }

    public static void showSnackbar(@NonNull View view, @StringRes int message, int duration, @Nullable Consumer<Snackbar> builder) {
        Resources resources = view.getResources();
        showSnackbar(view, ResourceFunctions.getString(message), duration, builder);
    }

    public static void showSnackbar(@NonNull View view, @NonNull CharSequence message, int duration, Consumer<Snackbar> builder) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        if (builder != null) builder.accept(snackbar);
        snackbar.show();
    }
}
