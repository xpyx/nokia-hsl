package com.xpyx.nokiahslvisualisation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test

class MainActivityTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

}