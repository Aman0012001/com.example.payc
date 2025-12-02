package com.example.payc.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

object ThemeUtils {
    tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
