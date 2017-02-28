package com.franmontiel.fullscreendialog;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by fj on 27/02/17.
 */

public interface OnDialogActionListener {

    void onConfirmDialog(DialogConfirmActionCallback callback);

    void onDiscardDialog(DialogDiscardActionCallback callback);

    interface DialogConfirmActionCallback {
        void onActionConfirmed(@Nullable Bundle result);
    }

    interface DialogDiscardActionCallback {
        void onActionConfirmed();
    }

}
