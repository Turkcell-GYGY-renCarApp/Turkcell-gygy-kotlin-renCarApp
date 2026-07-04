package com.turkcell.rencarapp.data.license

import retrofit2.Response

interface LicenseRepository {

    suspend fun uploadLicense(
        frontBytes: ByteArray,
        frontFileName: String,
        frontMimeType: String,
        backBytes: ByteArray,
        backFileName: String,
        backMimeType: String
    ): Response<LicenseResponse>

    suspend fun getLicenseStatus(): Response<LicenseStatusResponse>
}
