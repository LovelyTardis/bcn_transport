package com.example.wearosbarcelona.ui.modifier

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import kotlinx.coroutines.launch

/**
 * Vincula los eventos de la corona física (wheel) de Wear OS al contenedor desplazable.
 */
fun Modifier.rotaryScroll(
    scrollableState: ScrollableState,
    focusRequester: FocusRequester
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    this
        .onRotaryScrollEvent {
            coroutineScope.launch {
                scrollableState.scrollBy(it.verticalScrollPixels)
            }
            true
        }
        .focusRequester(focusRequester)
        .focusable()
}
