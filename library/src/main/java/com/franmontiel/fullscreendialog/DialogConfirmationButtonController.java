package com.franmontiel.fullscreendialog;

/**
 * Created by fj on 27/02/17.
 */

public interface DialogConfirmationButtonController {

    void setDialogConfirmationButtonStatusController(DialogConfirmationButtonStatusController controller);

    interface DialogConfirmationButtonStatusController {
        void setEnabled(boolean enabled);
    }
}
