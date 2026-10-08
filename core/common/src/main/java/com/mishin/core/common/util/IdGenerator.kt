package com.mishin.core.common.util

import com.benasher44.uuid.uuid4

/**
 * Generates a new UUID v4 string for entity IDs.
 * All IDs are generated client-side to support offline-first sync
 * without conflicts across devices/locations.
 */
fun generateId(): String = uuid4().toString()
