package com.franmontiel.fullscreendialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

/**
 * Created by fj on 24/02/17.
 */
public class FullScreenDialogFragment extends DialogFragment {

    private static final String BUILDER_TITLE = "BUILDER_TITLE";
    private static final String BUILDER_POSITIVE_BUTTON = "BUILDER_POSITIVE_BUTTON";
    private static final String BUILDER_NEGATIVE_BUTTON = "BUILDER_NEGATIVE_BUTTON";
    private static final String BUILDER_CONTENT = "BUILDER_CONTENT";
    private static final String BUILDER_FULL_SCREEN = "BUILDER_FULL_SCREEN";
    private static final String BUILDER_DATA = "BUILDER_DATA";

    private static final int MENU_ITEM_TITLE_ID = 1;

    private static FullScreenDialogFragment newInstance(Builder builder) {
        FullScreenDialogFragment f = new FullScreenDialogFragment();
        f.setArguments(mapBuilderToArguments(builder));
        return f;
    }

    private static Bundle mapBuilderToArguments(Builder builder) {
        Bundle builderData = new Bundle();
        builderData.putString(BUILDER_TITLE, builder.title);
        builderData.putString(BUILDER_POSITIVE_BUTTON, builder.positiveButton);
        builderData.putString(BUILDER_NEGATIVE_BUTTON, builder.negativeButton);
        builderData.putInt(BUILDER_CONTENT, builder.content);
        builderData.putBoolean(BUILDER_FULL_SCREEN, builder.fullScreen);

        Bundle arguments = builder.extras != null ? builder.extras : new Bundle();
        arguments.putBundle(BUILDER_DATA, builderData);

        return arguments;
    }

    private String title;
    private String positiveButton;
    private String negativeButton;
    private int content;
    private boolean fullScreen;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBuilderArguments();

        return fullScreen ? initView(inflater, container) : null;
    }

    private View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        view = inflater.inflate(R.layout.full_screen_dialog, container, false);
        initToolbar(view);
        initContent(view);

        return view;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    private void initToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (fullScreen) {
            toolbar.setVisibility(View.VISIBLE);

            Drawable closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_close);
            tintToolbarHomeButton(toolbar, closeDrawable);

            toolbar.setNavigationIcon(closeDrawable);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNegativeButtonClick();
                }
            });

            toolbar.setTitle(title);

            Menu menu = toolbar.getMenu();
            MenuItem itemPositiveButton = menu.add(0, MENU_ITEM_TITLE_ID, 0, this.positiveButton);
            itemPositiveButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            itemPositiveButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == MENU_ITEM_TITLE_ID) {
                        onPositiveButtonClick();
                        return true;
                    } else
                        return false;
                }
            });
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    private void tintToolbarHomeButton(Toolbar toolbar, Drawable homeButtonDrawable) {
        int[] colorAttrs = new int[]{R.attr.colorControlNormal};
        TypedArray a = toolbar.getContext().obtainStyledAttributes(colorAttrs);
        int color = a.getColor(0, -1);
        a.recycle();

        DrawableCompat.setTint(DrawableCompat.wrap(homeButtonDrawable), color);
    }

    private void initContent(View view) {
        ViewStub contentStub = (ViewStub) view.findViewById(R.id.contentStub);
        contentStub.setLayoutResource(content);
        contentStub.inflate();
    }

    protected void onPositiveButtonClick() {
        dismiss();
    }

    protected void onNegativeButtonClick() {
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
    public void onStart() {
        super.onStart();
        if (fullScreen)
            hideActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fullScreen)
            showActionBar();
    }

    boolean actionBarHidden;

    private void showActionBar() {
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setShowHideAnimationEnabled(false);
                actionBar.show();
            }
        }
        android.app.ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    private void hideActionBar() {
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setShowHideAnimationEnabled(false);
                actionBar.hide();
                actionBarHidden = true;
            }
        }
        android.app.ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.hide();
            actionBarHidden = true;
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
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            return transaction.add(android.R.id.content, this, tag)
                    .addToBackStack(null).commit();
        } else {
            return super.show(transaction, tag);
        }
    }

    private void initBuilderArguments() {
        Bundle builderData = getArguments().getBundle(BUILDER_DATA);
        title = builderData.getString(BUILDER_TITLE);
        positiveButton = builderData.getString(BUILDER_POSITIVE_BUTTON);
        negativeButton = builderData.getString(BUILDER_NEGATIVE_BUTTON);
        content = builderData.getInt(BUILDER_CONTENT);
        fullScreen = builderData.getBoolean(BUILDER_FULL_SCREEN, true);
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initBuilderArguments();

        Dialog dialog;
        if (!fullScreen) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(title);

            if (content != 0)
                builder.setView(initView(LayoutInflater.from(getContext()), null));

            if (positiveButton != null)
                builder.setPositiveButton(positiveButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onPositiveButtonClick();
                            }
                        }
                );
            if (negativeButton != null)
                builder.setNegativeButton(negativeButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onNegativeButtonClick();
                            }
                        }
                );
            dialog = builder.create();

        } else {
            dialog = super.onCreateDialog(savedInstanceState);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }


    public static class Builder {
        private Context context;

        private String title;
        private String positiveButton;
        private String negativeButton;
        private int content;
        private boolean fullScreen;
        private Bundle extras;

        public Builder(@NonNull Context context) {
            this.context = context;
            this.fullScreen = true;
        }

        public FullScreenDialogFragment build() {
            return FullScreenDialogFragment.newInstance(this);
        }

        public Builder title(@NonNull String title) {
            this.title = title;
            return this;
        }

        public Builder title(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public Builder positiveButton(@NonNull String positiveButton) {
            this.positiveButton = positiveButton;
            return this;
        }

        public Builder positiveButton(@StringRes int positiveButton) {
            this.positiveButton = context.getString(positiveButton);
            return this;
        }

        public Builder negativeButton(@NonNull String negativeButton) {
            this.negativeButton = negativeButton;
            return this;
        }

        public Builder negativeButton(@StringRes int negativeButton) {
            this.negativeButton = context.getString(negativeButton);
            return this;
        }

        public Builder content(@LayoutRes int contentLayout) {
            this.content = contentLayout;
            return this;
        }

        public Builder fullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
            return this;
        }

        public Builder extras(Bundle extras) {
            this.extras = extras;
            return this;
        }
    }
}
