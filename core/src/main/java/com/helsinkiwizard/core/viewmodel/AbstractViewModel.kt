package com.helsinkiwizard.core.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class AbstractViewModel(defaultState: UiState = UiState.Loading) : ViewModel() {

    protected val mutableUiStateFlow = MutableStateFlow(defaultState)
    val uiState: StateFlow<UiState> = mutableUiStateFlow

    protected val mutableDialogStateFlow = MutableStateFlow<DialogState>(DialogState.Gone)
    val dialogState: StateFlow<DialogState> = mutableDialogStateFlow

    protected open fun onError(e: Exception, errorType: BaseErrorType? = null) {
        mutableUiStateFlow.value = UiState.Error(e, errorType)
        Log.e(javaClass.simpleName, e.message, e)
    }

    /**
     * Wrap API calls with this method and catch network related exceptions.
     *
     * @param context use if the call needs to be handled on a different thread than the default
     * @param progressIndicator defaults to a full page progress indicator. If you want to use a different progress
     * indicator you can use this block to set that indicator, or pass in null and handle elsewhere if needed.
     * @param typeOfError the error type to use if this call results in an error.
     * @param onError a function that gets called in the case of an error. If not set, the default error or passed in
     * typeOfError will be shown. If more handling needs to be done than just setting the typeOfError, you can use this
     * block.
     * @param call the code to execute
     */
    protected fun CoroutineScope.safeLaunch(
        context: CoroutineContext = EmptyCoroutineContext,
        progressIndicator: (() -> Unit)? = ::progressIndicator,
        typeOfError: BaseErrorType? = null,
        onError: (Exception, BaseErrorType?) -> Unit = ::onError,
        cleanup: (() -> Unit)? = null,
        call: suspend () -> Unit
    ): Job {
        progressIndicator?.invoke()
        return launch(context) {
            try {
                call()
            } catch (e: CancellationException) {
                // no-op, just ignore the coroutine CancellationException
                Log.d(javaClass.simpleName, "CancellationException is caught ")
            } catch (e: Exception) {
                onError(e, typeOfError)
            } finally {
                cleanup?.invoke()
            }
        }
    }

    fun resetDialogState() {
        mutableDialogStateFlow.value = DialogState.Gone
    }

    private fun progressIndicator() {
        mutableUiStateFlow.value = UiState.Loading
    }
}
