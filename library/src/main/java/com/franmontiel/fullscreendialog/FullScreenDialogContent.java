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

/**
 * Interface that the content {@link android.support.v4.app.Fragment} must implement. It allows to control over the container dialog.
 */
public interface FullScreenDialogContent {
    /**
     * Called after the dialog has been initialized. It is invoked before the content onCreateView.
     *
     * @param dialogController that allows to control the container dialog
     */
    void onDialogCreated(FullScreenDialogController dialogController);

    /**
     * Called when the confirm button is clicked.
     *
     * @param dialogController that allows to control the container dialog
     * @return true if the event has been consumed, false otherwise
     */
    boolean onConfirmClick(FullScreenDialogController dialogController);

    /**
     * Called when the discard button is clicked.
     *
     * @param dialogController that allows to control the container dialog
     * @return true if the event has been consumed, false otherwise
     */
    boolean onDiscardClick(FullScreenDialogController dialogController);
}

