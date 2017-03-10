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
