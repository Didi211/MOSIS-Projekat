package elfak.mosis.tourguide.business.something

interface ILocationState {
    var context: LocationStateContext
    fun enableGps()
    fun disableGps()
    fun locateDevice()
    fun touchMap()
}

class LocationStateContext {
    private var currentState: ILocationState? = null

    init {
        currentState = LocationOffState()
    }

    fun setState(locationMode: LocationMode) {
        when(locationMode) {
            LocationMode.LOCATION_OFF -> this.currentState = LocationOffState()
            LocationMode.LOCATION_ON -> this.currentState = LocationOnState()
            LocationMode.LOCATED -> this.currentState = LocatedState()
        }
    }

    fun enableGps() {
        this.currentState?.enableGps()
    }

    fun disableGps() {
        this.currentState?.disableGps()
    }

    fun locateDevice() {
        this.currentState?.locateDevice()
    }

    fun touchMap() {
        this.currentState?.touchMap()
    }
}
class LocationOffState : ILocationState {
    override var context: LocationStateContext
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun enableGps() {
        this.findDeviceLocation()
        this.context.setState(LocationMode.LOCATED)
    }

    override fun disableGps() {
    }

    override fun locateDevice() {
        this.findDeviceLocation()
        this.context.setState(LocationMode.LOCATED)

    }

    override fun touchMap() {
    }

    private fun findDeviceLocation() {

    }
}

class LocationOnState : ILocationState {
    override var context: LocationStateContext
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun enableGps() {

    }

    override fun disableGps() {
        this.context.setState(LocationMode.LOCATION_OFF)
    }

    override fun locateDevice() {

    }

    override fun touchMap() {
        // stop location tracking
        this.context.setState(LocationMode.LOCATION_ON)

    }
}

class LocatedState : ILocationState {
    override var context: LocationStateContext
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun enableGps() {
        TODO("Not yet implemented")
    }

    override fun disableGps() {
        TODO("Not yet implemented")
    }

    override fun locateDevice() {
        TODO("Not yet implemented")
    }

    override fun touchMap() {
        TODO("Not yet implemented")
    }
}

enum class LocationMode {
    LOCATION_OFF,
    LOCATION_ON,
    LOCATED
}