package com.mishin.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for Mishin Cat Café & Shelter.
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation.
 */
@HiltAndroidApp
class MishinApplication : Application()
