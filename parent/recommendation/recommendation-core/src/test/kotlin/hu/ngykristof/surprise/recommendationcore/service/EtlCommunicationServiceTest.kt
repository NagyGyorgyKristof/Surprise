package hu.ngykristof.surprise.recommendationcore.service

import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.web.reactive.function.client.WebClient

internal class EtlCommunicationServiceTest {

    private lateinit var webClientBuilder: WebClient.Builder
    private lateinit var etlCommunicationService: EtlCommunicationService

    @BeforeEach
    fun setUp() {
        this.webClientBuilder = mockk(relaxed = true)
        this.etlCommunicationService = EtlCommunicationService(webClientBuilder)
    }

    @Test
    fun executeRequest() {
        val url = "url"

        assertDoesNotThrow { etlCommunicationService.executeRequest(url) }
    }
}