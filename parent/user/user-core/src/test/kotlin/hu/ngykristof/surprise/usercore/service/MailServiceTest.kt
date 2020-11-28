package hu.ngykristof.surprise.usercore.service

import hu.ngykristof.surprise.usercore.test.createMockUserEntity
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.util.*
import javax.mail.internet.MimeMessage

internal class MailServiceTest {

    lateinit var javaMailService: JavaMailSender
    lateinit var messageSource: MessageSource
    lateinit var templateEngine: SpringTemplateEngine
    lateinit var mailService: MailService

    companion object {
        private const val BASE_URL = "baseUrl"
    }

    @BeforeEach
    fun setUp() {
        javaMailService = mockk(relaxed = true)
        messageSource = mockk(relaxed = true)
        templateEngine = mockk(relaxed = true)
        mailService = MailService(
                javaMailSender = javaMailService,
                messageSource = messageSource,
                templateEngine = templateEngine,
                baseUrl = BASE_URL
        )
    }

    @Test
    fun sendActivationEmail() {
        val mockUserEntity = createMockUserEntity()
        val activationEmailTemplate = "mail/activationEmail"
        val activationTitle = "email.activation.title"
        val mockMimeMessage: MimeMessage = mockk(relaxed = true)

        mailService.sendActivationEmail(mockUserEntity)

        coEvery { javaMailService.createMimeMessage() } returns mockMimeMessage

        coVerifyAll {
            templateEngine.process(activationEmailTemplate, ofType(Context::class))
            messageSource.getMessage(activationTitle, null, ofType(Locale::class))
        }
    }
}