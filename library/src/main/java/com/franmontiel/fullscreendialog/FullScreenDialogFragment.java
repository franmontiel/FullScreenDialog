package com.franmontiel.fullscreendialog;

import android.annotation.SuppressLint;
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
 * Created by fj on 24/02/17.
 */
public class FullScreenDialogFragment extends DialogFragment {

    private static final String BUILDER_TITLE = "BUILDER_TITLE";
    private static final String BUILDER_POSITIVE_BUTTON = "BUILDER_POSITIVE_BUTTON";
    private static final String BUILDER_FULL_SCREEN = "BUILDER_FULL_SCREEN";

    private static FullScreenDialogFragment newInstance(Builder builder) {
        FullScreenDialogFragment f = new FullScreenDialogFragment();
        f.setArguments(mapBuilderToArguments(builder));
        f.setContent(Fragment.instantiate(builder.context, builder.contentClass.getName(), builder.contentArguments));
        f.setOnConfirmListener(builder.onConfirmListener);
        return f;
    }

    private static Bundle mapBuilderToArguments(Builder builder) {
        Bundle builderData = new Bundle();
        builderData.putString(BUILDER_TITLE, builder.title);
        builderData.putString(BUILDER_POSITIVE_BUTTON, builder.positiveButton);
        builderData.putBoolean(BUILDER_FULL_SCREEN, builder.fullScreen);

        return builderData;
    }

    private String title;
    private String positiveButton;
    private boolean fullScreen;
    private Fragment content;
    private MenuItem itemPositiveButton;
    private OnConfirmListener onConfirmListener;

    private void setContent(Fragment content) {
        this.content = content;
    }

    public Fragment getContent() {
        return content;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            content = getChildFragmentManager().findFragmentById(R.id.content);

        if (content instanceof DialogConfirmationButtonController)
            ((DialogConfirmationButtonController) content).setDialogConfirmationButtonStatusController(
                    new DialogConfirmationButtonController.DialogConfirmationButtonStatusController() {
                        @Override
                        public void setEnabled(boolean enabled) {
                            itemPositiveButton.setEnabled(enabled);
                        }
                    });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBuilderArguments();

        if (fullScreen)
            hideActivityActionBar(savedInstanceState == null);

        View view = inflater.inflate(R.layout.full_screen_dialog, container, false);

        initToolbar(view);

        if (fullScreen)
            setThemeBackground(view);

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
                onNegativeButtonClick();
            }
        });

        toolbar.setTitle(title);

        Menu menu = toolbar.getMenu();

        final int menuItemTitleId = 1;
        itemPositiveButton = menu.add(0, menuItemTitleId, 0, this.positiveButton);
        itemPositiveButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        itemPositiveButton.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == menuItemTitleId) {
                            onPositiveButtonClick();
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initBuilderArguments();

        Dialog dialog = super.onCreateDialog(savedInstanceState);
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

    private void onPositiveButtonClick() {
        ((OnDialogActionListener) content).onConfirmDialog(new OnDialogActionListener.DialogConfirmActionCallback() {
            @Override
            public void onActionConfirmed(@Nullable Bundle result) {
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm(result);
                }
                dismiss();
            }
        });
    }

    private void onNegativeButtonClick() {
        ((OnDialogActionListener) content).onDiscardDialog(new OnDialogActionListener.DialogDiscardActionCallback() {
            @Override
            public void onActionConfirmed() {
                dismiss();
            }
        });
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

    public static class Builder {
        private Context context;
        private String title;
        private String positiveButton;
        private boolean fullScreen;
        private Class<? extends Fragment> contentClass;
        private Bundle contentArguments;
        private OnConfirmListener onConfirmListener;

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

        public Builder positiveButton(@NonNull String text, @Nullable OnConfirmListener onConfirmListener) {
            this.positiveButton = text;
            this.onConfirmListener = onConfirmListener;
            return this;
        }

        public Builder positiveButton(@StringRes int textResId, @Nullable OnConfirmListener onConfirmListener) {
            return positiveButton(context.getString(textResId), onConfirmListener);
        }

        public Builder content(Class<? extends Fragment> contentClass, @Nullable Bundle contentArguments) {
            if (!OnDialogActionListener.class.isAssignableFrom(contentClass)) {
                throw new IllegalArgumentException("The content Fragment must implement OnDialogActionListener interface");
            }
            this.contentClass = contentClass;
            this.contentArguments = contentArguments;
            return this;
        }

        public Builder fullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
            return this;
        }
    }

    public interface OnConfirmListener {
        void onConfirm(@Nullable Bundle result);
    }
}
