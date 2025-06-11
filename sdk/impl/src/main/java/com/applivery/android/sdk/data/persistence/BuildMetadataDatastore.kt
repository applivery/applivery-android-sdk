package com.applivery.android.sdk.data.persistence

import android.content.Context
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.google.gson.annotations.SerializedName

internal class BuildMetadataDatastore(
    context: Context
) : DynamicDataStoreDataSource<BuildMetadataDs, String> by dynamicDataStoreDataSource(context)

@DataStoreEntity("launcher")
internal data class BuildMetadataDs(
    @SerializedName("id") val id: String,
    @SerializedName("version") val version: Int,
    @SerializedName("filePath") val filePath: String,
)

internal fun BuildMetadata.toDs(): BuildMetadataDs = BuildMetadataDs(
    id = id,
    version = version,
    filePath = filePath,
)
