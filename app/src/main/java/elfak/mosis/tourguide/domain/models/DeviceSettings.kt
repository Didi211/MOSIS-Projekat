package elfak.mosis.tourguide.domain.models

data class DeviceSettings(
    var gpsEnabled: Boolean = false,
    var locationPermissionAllowed: Boolean = false,
)