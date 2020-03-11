package hu.ngykristof.surprise.usercore.service

import hu.ngykristof.surprise.usercore.domain.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class MailService(
        private val javaMailSender: JavaMailSender,
        private val messageSource: MessageSource,
        private val templateEngine: SpringTemplateEngine,
        @Value("\${surprise.mail.base-url}")
        private val baseUrl: String
) {

    companion object {
        private val log = LoggerFactory.getLogger(MailService::class.java)

        private const val USER = "user"

        private const val BASE_URL = "baseUrl"
    }

    private fun sendEmail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content)
        // Prepare message using a Spring helper
        val mimeMessage = javaMailSender.createMimeMessage()
        try {
            val message = MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name())
            message.setTo(to)
            //TODO to property!
            message.setFrom("surprise@localhost")
            message.setSubject(subject)
            message.setText(content, isHtml)
            javaMailSender.send(mimeMessage)
            log.debug("Sent email to User '{}'", to)
        } catch (e: Exception) {
            if (log.isDebugEnabled) {
                log.warn("Email could not be sent to user '{}'", to, e)
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.message)
            }
        }
    }

    private fun sendEmailFromTemplate(user: UserEntity, templateName: String, titleKey: String) {
        val locale =  LocaleContextHolder.getLocale()
        val context = Context(locale)
        context.setVariable(USER, user)
        context.setVariable(BASE_URL, baseUrl)
        val content = templateEngine.process(templateName, context)
        val subject = messageSource.getMessage(titleKey, null, locale)
        sendEmail(user.email, subject, content, isMultipart = false, isHtml = true)
    }

    fun sendActivationEmail(user: UserEntity) {
        log.debug("Sending activation email to '{}'", user.email)
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title")
    }
}