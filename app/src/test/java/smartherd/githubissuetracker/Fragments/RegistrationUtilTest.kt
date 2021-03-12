package smartherd.githubissuetracker.Fragments

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest {

    @Test
    fun `empty username returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            username = "",
            password = "123",
            confirmedpassword = "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `valid username and correctly repeated password returns true`() {
        val result = RegistrationUtil.validateRegistrationInput(
            username = "Phillip",
            password = "123",
            confirmedpassword = "123"
        )
        assertThat(result).isTrue()
    }


    @Test
    fun `username already exists returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            username = "Carl",
            password = "123",
            confirmedpassword = "123"
        )
        assertThat(result).isTrue()
    }


}