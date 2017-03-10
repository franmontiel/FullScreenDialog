/*
 * Copyright (C) 2017 Francisco José Montiel Navarro.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.franmontiel.fullscreendialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * A DialogFragment that implements the Full-screen dialog pattern defined in
 * <a href="https://material.io/guidelines/components/dialogs.html#dialogs-full-screen-dialogs">the Material Design guidelines</a>
 */
public class FullScreenDialogFragment extends DialogFragment {
    /**
     * Callback that will be called when the dialog is closed due to a confirm button click.
     */
    public interface OnConfirmListener {
        void onConfirm(@Nullable Bundle result);
    }

    /**
     * Callback that will be called when the dialog is closed due a discard button click.
     */
    public interface OnDiscardListener {
        void onDiscard();
    }

    private static final String BUILDER_TITLE = "BUILDER_TITLE";
    private static final String BUILDER_POSITIVE_BUTTON = "BUILDER_POSITIVE_BUTTON";
    private static final String BUILDER_FULL_SCREEN = "BUILDER_FULL_SCREEN";

    private static FullScreenDialogFragment newInstance(Builder builder) {
        FullScreenDialogFragment f = new FullScreenDialogFragment();
        f.setArguments(mapBuilderToArguments(builder));
        f.setContent(Fragment.instantiate(builder.context, builder.contentClass.getName(), builder.contentArguments));
        f.setOnConfirmListener(builder.onConfirmListener);
        f.setOnDiscardListener(builder.onDiscardListener);
        return f;
    }

    private static Bundle mapBuilderToArguments(Builder builder) {
        Bundle builderData = new Bundle();
        builderData.putString(BUILDER_TITLE, builder.title);
        builderData.putString(BUILDER_POSITIVE_BUTTON, builder.confirmButton);
        builderData.putBoolean(BUILDER_FULL_SCREEN, builder.fullScreen);

        return builderData;
    }

    private String title;
    private String positiveButton;
    private boolean fullScreen;
    private Fragment content;

    private MenuItem itemConfirmButton;

    private OnConfirmListener onConfirmListener;
    private OnDiscardListener onDiscardListener;

    private FullScreenDialogController dialogController;

    private void setContent(Fragment content) {
        this.content = content;
    }

    /**
     * Get the content {@link Fragment} to be able to interact directly with it.
     *
     * @return The content @{@link Fragment} of the dialog
     */
    public Fragment getContent() {
        return content;
    }

    /**
     * Sets the callback that will be called when the dialog is closed due to a confirm button click.
     *
     * @param onConfirmListener
     */
    public void setOnConfirmListener(@Nullable OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    /**
     * Sets the callback that will be called when the dialog is closed due a discard button click.
     *
     * @param onDiscardListener
     */
    public void setOnDiscardListener(@Nullable OnDiscardListener onDiscardListener) {
        this.onDiscardListener = onDiscardListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            content = getChildFragmentManager().findFragmentById(R.id.content);

        dialogController = new FullScreenDialogController() {
            @Override
            public void setConfirmButtonEnabled(boolean enabled) {
                FullScreenDialogFragment.this.itemConfirmButton.setEnabled(enabled);
            }

            @Override
            public void confirm(@Nullable Bundle result) {
                FullScreenDialogFragment.this.confirm(result);
            }

            @Override
            public void discard() {
                FullScreenDialogFragment.this.discard();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBuilderArguments();

        if (fullScreen)
            hideActivityActionBar(savedInstanceState == null);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.full_screen_dialog, container, false);

        initToolbar(view);

        if (fullScreen)
            setThemeBackground(view);

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        return view;
    }

    private void setThemeBackground(View view) {
        TypedValue a = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            view.setBackgroundColor(a.data);
        } else {
            try {
                Drawable d = ResourcesCompat.getDrawable(getActivity().getResources(), a.resourceId, getActivity().getTheme());
                ViewCompat.setBackground(view, d);
            } catch (Resources.NotFoundException ignore) {
            }
        }
    }

    private void initToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        Drawable closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_close);
        tintToolbarHomeButton(toolbar, closeDrawable);

        toolbar.setNavigationIcon(closeDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDiscardButtonClick();
            }
        });

        toolbar.setTitle(title);

        Menu menu = toolbar.getMenu();

        final int menuItemTitleId = 1;
        itemConfirmButton = menu.add(0, menuItemTitleId, 0, this.positiveButton);
        itemConfirmButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        itemConfirmButton.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == menuItemTitleId) {
                            onConfirmButtonClick();
                            return true;
                        } else
                            return false;
                    }
                });
    }

    private void tintToolbarHomeButton(Toolbar toolbar, Drawable homeButtonDrawable) {
        int[] colorAttrs = new int[]{R.attr.colorControlNormal};
        TypedArray a = toolbar.getContext().obtainStyledAttributes(colorAttrs);
        int color = a.getColor(0, -1);
        a.recycle();

        DrawableCompat.setTint(DrawableCompat.wrap(homeButtonDrawable), color);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((FullScreenDialogContent) getContent()).onDialogCreated(dialogController);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initBuilderArguments();

        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                onDiscardButtonClick();
            }
        };
        if (!fullScreen)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null)
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.none, 0, 0, R.anim.none)
                    .add(R.id.content, content)
                    .commitNow();
    }

    private void onConfirmButtonClick() {
        boolean eventConsumed = ((FullScreenDialogContent) content).onConfirmClick(dialogController);
        if (!eventConsumed)
            dialogController.confirm(null);
    }

    private void onDiscardButtonClick() {
        boolean eventConsumed = ((FullScreenDialogContent) content).onDiscardClick(dialogController);
        if (!eventConsumed)
            dialogController.discard();
    }

    private void confirm(Bundle result) {
        if (onConfirmListener != null) {
            onConfirmListener.onConfirm(result);
        }
        dismiss();
    }

    private void discard() {
        if (onDiscardListener != null) {
            onDiscardListener.onDiscard();
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        if (fullScreen)
            getFragmentManager().popBackStackImmediate();
        else
            super.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fullScreen)
            showActivityActionBar();
    }

    private void showActivityActionBar() {
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null && actionBar instanceof WindowDecorActionBar) {
                actionBar.setShowHideAnimationEnabled(true);
                actionBar.show();
            }
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    }

    private void hideActivityActionBar(boolean animate) {
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null && actionBar instanceof WindowDecorActionBar) {
                actionBar.setShowHideAnimationEnabled(animate);
                actionBar.hide();
            }
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    @Override
    @SuppressLint("CommitTransaction")
    public void show(FragmentManager manager, String tag) {
        show(manager.beginTransaction(), tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        initBuilderArguments();

        if (fullScreen) {
            transaction.setCustomAnimations(R.anim.slide_in_bottom, 0, 0, R.anim.slide_out_bottom);
            return transaction.add(android.R.id.content, this, tag).addToBackStack(null).commit();
        } else {
            return super.show(transaction, tag);
        }
    }

    private void initBuilderArguments() {
        Bundle builderData = getArguments();
        title = builderData.getString(BUILDER_TITLE);
        positiveButton = builderData.getString(BUILDER_POSITIVE_BUTTON);
        fullScreen = builderData.getBoolean(BUILDER_FULL_SCREEN, true);
    }

    /**
     * Method intented to be called from the {@link Activity#onBackPressed()} the back button press will be processed as a discard button click.
     */
    public void onBackPressed() {
        if (isAdded())
            onDiscardButtonClick();
    }

    public static class Builder {
        private Context context;
        private String title;
        private String confirmButton;
        private boolean fullScreen;
        private Class<? extends Fragment> contentClass;
        private Bundle contentArguments;
        private OnConfirmListener onConfirmListener;
        private OnDiscardListener onDiscardListener;

        /**
         * Builder to construct a {@link FullScreenDialogFragment}.
         *
         * @param context
         */
        public Builder(@NonNull Context context) {
            this.context = context;
            this.fullScreen = true;
        }

        /**
         * Creates a {@link FullScreenDialogFragment} with the provided parameters.
         *
         * @return the created instance of {@link FullScreenDialogFragment}
         */
        public FullScreenDialogFragment build() {
            return FullScreenDialogFragment.newInstance(this);
        }

        public Builder setTitle(@NonNull String text) {
            this.title = text;
            return this;
        }

        public Builder setTitle(@StringRes int textResId) {
            this.title = context.getString(textResId);
            return this;
        }

        public Builder setConfirmButton(@NonNull String text) {
            this.confirmButton = text;
            return this;
        }

        public Builder setConfirmButton(@StringRes int textResId) {
            return setConfirmButton(context.getString(textResId));
        }

        /**
         * Sets the callback that will be called when the dialog is closed due to a confirm button click.
         *
         * @param onConfirmListener
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnConfirmListener(@Nullable OnConfirmListener onConfirmListener) {
            this.onConfirmListener = onConfirmListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is closed due a discard button click.
         *
         * @param onDiscardListener
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDiscardListener(@Nullable OnDiscardListener onDiscardListener) {
            this.onDiscardListener = onDiscardListener;
            return this;
        }

        /**
         * Sets the {@link Fragment} that will be added as the content of the dialog. This fragment must implements {@link FullScreenDialogContent}.
         *
         * @param contentClass     the content class that will be instantiated
         * @param contentArguments the arguments to add to the content
         * @return This Builder object to allow for chaining of calls to set methods
         * @throws IllegalArgumentException if the contentClass does not implement the {@link FullScreenDialogContent} interface
         */
        public Builder setContent(Class<? extends Fragment> contentClass, @Nullable Bundle contentArguments) {
            if (!FullScreenDialogContent.class.isAssignableFrom(contentClass)) {
                throw new IllegalArgumentException("The setContent Fragment must implement FullScreenDialogContent interface");
            }
            this.contentClass = contentClass;
            this.contentArguments = contentArguments;
            return this;
        }

        /**
         * Sets if the dialog will be shown in full-screen (the default value if this method is not invoked) or using the default dialog window.
         *
         * @param fullScreen
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setFullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
            return this;
        }
    }
}
