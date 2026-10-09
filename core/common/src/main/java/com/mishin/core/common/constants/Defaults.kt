package com.mishin.core.common.constants

/**
 * Default configuration constants.
 * These can be overridden per-item or in settings.
 */
object Defaults {
    /** Default alert threshold percentage (stock/max). */
    const val ALERT_THRESHOLD_PERCENT = 25

    /** Default buffer between reservations in minutes. */
    const val RESERVATION_BUFFER_MINUTES = 15

    /** Default location ID for the single-location MVP. */
    const val DEFAULT_LOCATION_ID = "loc_mishin_main"

    /** Default currency code (Bolivianos, displayed as `Bs`). */
    const val CURRENCY_CODE = "BOB"
}
