package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationcore.repository.GeneralGraphRepository
import org.springframework.stereotype.Service

@Service
class GeneralGraphService(
        private val generalGraphRepository: GeneralGraphRepository
) {

    fun isGraphEmpty(): Boolean {
        return generalGraphRepository.getNodeCount() == 0
    }
}