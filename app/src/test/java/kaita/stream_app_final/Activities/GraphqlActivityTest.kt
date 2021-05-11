package kaita.stream_app_final.Activities

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kaita.stream_app_final.Adapteres.date_Converter as dateConverter

public class MainActivityTest_Unit{

    @Test
    fun test_dateConverter_Returns_A_String() {
        assertDoesNotThrow {
            assertNotNull(dateConverter())
            println("Formatted Date Is Generated Successfully")
        }
    }

}