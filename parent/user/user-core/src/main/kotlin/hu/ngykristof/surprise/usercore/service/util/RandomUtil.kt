package hu.ngykristof.surprise.usercore.service.util

import org.apache.commons.lang.RandomStringUtils
import java.security.SecureRandom

class RandomUtil {

    companion object {
        private const val DEF_COUNT = 20

        private val SECURE_RANDOM: SecureRandom by lazy {
            SecureRandom().apply {
                nextBytes(ByteArray(64))
            }
        }

        private fun generateRandomAlphanumericString(): String {
            return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM)
        }

        fun generateActivationKey(): String {
            return generateRandomAlphanumericString()
        }
    }
}