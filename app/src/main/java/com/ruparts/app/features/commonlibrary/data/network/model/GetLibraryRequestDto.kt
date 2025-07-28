package com.ruparts.app.features.commonlibrary.data.network.model

import com.ruparts.app.core.data.network.EndpointRequestDto

class GetLibraryRequestDto : EndpointRequestDto<Any?>(
    action = "app.common.library",
    data = null,
)
