package hu.ngykristof.surprise.commonscore.util

import org.apache.commons.lang3.RandomStringUtils
import java.security.SecureRandom

class RandomUtil {

    companion object {

        private val SECURE_RANDOM: SecureRandom by lazy {
            SecureRandom().apply {
                nextBytes(ByteArray(64))
            }
        }

        private fun generateRandomAlphanumericString(defCount: Int): String {
            return RandomStringUtils.random(defCount, 0, 0, true, true, null, SECURE_RANDOM)
        }

        fun generateActivationKey(): String {
            return generateRandomAlphanumericString(defCount = 20)
        }


        fun generateRefreshToken(): String {
            return generateRandomAlphanumericString(defCount = 100)
        }
    }
}
