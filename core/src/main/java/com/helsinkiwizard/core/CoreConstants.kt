package com.helsinkiwizard.core

object CoreConstants {
    const val PACKAGE_NAME = "com.helsinkiwizard.cointoss"
    const val VALUE_UNDEFINED = -1
    const val SPACE_STRING = " "
    const val EMPTY_STRING = ""

    // Deep links
    const val PLAY_STORE_DEEPLINK = "market://details?id=$PACKAGE_NAME"
    const val CREATE_COIN_DEEPLINK = "cointoss://create-coin"

    // Device communication
    const val WEAR_SEND_COIN_CAPABILITY = "coin_toss_send_custom_coin"
    const val PHONE_NAV_TO_CREATE_COIN_CAPABILITY = "coin_toss_nav_to_create_coin"
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
