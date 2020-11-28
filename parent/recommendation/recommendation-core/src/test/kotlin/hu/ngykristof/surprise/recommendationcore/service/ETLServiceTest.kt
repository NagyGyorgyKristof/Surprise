package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationcore.service.ETLConstants.Companion.START_UP
import hu.ngykristof.surprise.recommendationcore.service.ETLConstants.Companion.UPDATE_MOVIES
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ETLServiceTest {

    private lateinit var communicationService: CommunicationService
    private lateinit var etlService: ETLService

    companion object {
        const val ETL_BASE_URL = "ETL_BASE_URL"
    }

    @BeforeEach
    fun setUp() {
        this.communicationService = mockk(relaxed = true)
        this.etlService = ETLService(communicationService, ETL_BASE_URL)
    }

    @Test
    fun runSetupETLFlow() {
        val urlSlot = slot<String>()

        etlService.runSetupETLFlow()

        verify { communicationService.executeRequest(capture(urlSlot)) }

        assertEquals("$ETL_BASE_URL/$START_UP", urlSlot.captured)
    }

    @Test
    fun runUpdateMoviesETLFlow() {
        val urlSlot = slot<String>()

        etlService.runUpdateMoviesETLFlow()

        verify { communicationService.executeRequest(capture(urlSlot)) }

        assertEquals("$ETL_BASE_URL/$UPDATE_MOVIES", urlSlot.captured)
    }
}