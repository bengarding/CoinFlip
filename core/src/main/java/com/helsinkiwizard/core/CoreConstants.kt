package com.helsinkiwizard.core

object CoreConstants {
    const val PACKAGE_NAME = "com.helsinkiwizard.cointoss"
    const val VALUE_UNDEFINED = -1
    const val SPACE_STRING = " "
    const val EMPTY_STRING = ""

    // Device communication
    const val WEAR_CAPABILITY = "coin_toss_send_custom_coin"
    const val PREPARE_FOR_COIN_TRANSFER = "/prepare-for-coin-transfer"
    const val READY_FOR_COIN_TRANSFER = "/ready-for-coin-transfer"
    const val TRANSFER_COMPLETE = "/transfer-complete"
    const val IMAGE_PATH = "/image"
    const val BYTE_BUFFER_CAPACITY = 4
    const val NODE_ID = "node-id"

    const val SPEED_MIN = 0.5f
    const val SPEED_MAX = 6f
    const val SPEED_DEFAULT = 3f
    const val SPEED_STEPS = 10

    // Analytics
    const val COIN_SELECTED = "coin_selected"
}
