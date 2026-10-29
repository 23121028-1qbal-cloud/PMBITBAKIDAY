package com.example.pmbitbakiday.data.remote

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiConfigTest {

    @After
    fun tearDown() {
        ApiConfig.resetToDefault()
    }

    @Test
    fun testDefaultBaseUrlHasTrailingSlash() {
        val baseUrl = ApiConfig.getBaseUrl()
        assertTrue("Base URL harus diakhiri dengan /", baseUrl.endsWith("/"))
    }

    @Test
    fun testCustomBaseUrlOverridesDefaultAndNormalizesSlash() {
        ApiConfig.setCustomBaseUrl("http://192.168.1.100:8000/api/v1")
        assertEquals("http://192.168.1.100:8000/api/v1/", ApiConfig.getBaseUrl())

        ApiConfig.resetToDefault()
        assertFalse(ApiConfig.getBaseUrl().contains("192.168.1.100"))
    }

    @Test
    fun testIsTrustedHostVerification() {
        ApiConfig.setCustomBaseUrl("http://10.0.2.2:8000/api/v1/")

        val trustedUrl = "http://10.0.2.2:8000/api/v1/applications".toHttpUrl()
        assertTrue(ApiConfig.isTrustedHost(trustedUrl))

        val untrustedUrl = "https://external-malicious-site.com/api/steal".toHttpUrl()
        assertFalse(ApiConfig.isTrustedHost(untrustedUrl))
    }
}
