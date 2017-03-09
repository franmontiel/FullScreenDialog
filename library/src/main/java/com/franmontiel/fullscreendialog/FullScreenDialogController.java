package com.franmontiel.fullscreendialog;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by fj on 8/03/17.
 */

public interface FullScreenDialogController {

    void setConfirmButtonEnabled(boolean enabled);

    void confirm(@Nullable Bundle result);

    void discard();


}
