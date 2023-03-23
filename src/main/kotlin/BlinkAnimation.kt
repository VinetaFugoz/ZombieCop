class BlinkAnimation(private var frameInterval: Int) {

    private var blinkFrames = 0
    fun animate(): Boolean {
        blinkFrames++
        return if (blinkFrames == frameInterval) {
            blinkFrames = 0
            false
        } else true
    }
}