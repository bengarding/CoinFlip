package com.helsinkiwizard.core.viewmodel

import com.helsinkiwizard.core.viewmodel.UiState.ShowContent

/**
 * Interface outlines three basic ViewModel states that are getting observed in a Fragment.
 * The [ShowContent] class support different content types to be shown by the Fragment based
 * on the interactions with an [AbstractViewModel] instance.
 */
sealed interface UiState {
    data object Loading : UiState
    data class Error(
        val error: Exception? = null,
        val type: BaseErrorType? = null
    ) : UiState
    data class ShowContent(val type: BaseType) : UiState
}

/**
 * An interface that generalizes different content types and to be implemented by classes/interfaces that
 * provide UI states for individual ViewModel instances.
 * */
interface BaseType

/**
 * An interface for dialog content types. It is recommended to use a separate sealed interface, see
 * WalletEnrollmentViewModel. Many instances inherit from both BaseType and BaseDialogType in the same sealed interface
 * for simplicity when this was introduced, can be separated as needed.
 */
interface BaseDialogType

/**
 * An interface for error content types. It is recommended to use a separate sealed interface. See
 * EmailSettingsViewModel. Keep in mind that the general error case will be null, so this type should always be nullable
 * and handle the null case.
 */
interface BaseErrorType

sealed interface DialogState {
    data object Gone : DialogState
    data class Error(val error: Exception? = null) : DialogState
    data class ShowContent(val type: BaseDialogType) : DialogState
}
