package smartherd.githubissuetracker.Activities

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import smartherd.githubissuetracker.Adapteres.date_Converter as dateConverter

public class GraphqlActivityTest_Unit{

    @Test
    fun test_dateConverter_Returns_A_String() {
        assertDoesNotThrow {
            assertNotNull(dateConverter())
            println("Formatted Date Is Generated Successfully")
        }
    }

}