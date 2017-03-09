package com.franmontiel.fullscreendialog;

/**
 * Created by fj on 9/03/17.
 */

public interface FullScreenDialogContent {
    /**
     * Called after the dialog has been initialized
     *
     * @param dialogController
     */
    void onDialogCreated(FullScreenDialogController dialogController);

    /**
     * Called when the confirm button is clicked
     *
     * @param dialogController
     * @return true if the event has been consumed
     */
    boolean onConfirmClick(FullScreenDialogController dialogController);

    /**
     * Called when the discard button or the back button is clicked
     *
     * @param dialogController
     * @return true if the event has been consumed
     */
    boolean onDiscardClick(FullScreenDialogController dialogController);
}

