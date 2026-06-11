package com.example.gms

import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

class DateUtilTest {

    @Test
    fun getCurrentData_shouldReturnFormattedDate(){
        val result =getCurrentData()
        val regex = Regex("\\d{2} [A-Za-z]{3} \\d{4} \\d{2}:\\d{2} (AM|PM)")
        assertTrue(regex.matches(result))
    }


    fun getCurrentData() : String {
        return java.text.SimpleDateFormat(
            "dd MMM yyyy hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
    }

}