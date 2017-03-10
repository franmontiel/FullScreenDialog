package com.franmontiel.fullscreendialog;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Interface to control the dialog
 */
public interface FullScreenDialogController {

    /**
     * Enable or disable the confirm button.
     *
     * @param enabled true to enable the button, false to disable it
     */
    void setConfirmButtonEnabled(boolean enabled);

    /**
     * Closes the dialog with a confirm action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnConfirmListener} will be called.
     *
     * @param result optional bundle with result that will be passed to the
     *               {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnConfirmListener} callback
     */
    void confirm(@Nullable Bundle result);

    /**
     * Closes the dialog with a discard action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardListener} will be called.
     */
    void discard();


}
