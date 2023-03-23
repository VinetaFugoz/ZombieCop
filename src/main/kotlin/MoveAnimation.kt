
class MoveAnimation(speed: Int, size: Int) {
    private var currentFrame: Int = 0
    private var maxFrame: Int = (5 / speed)
    var currentAnimationIndex: Int = 0
    private var animationSize: Int = size

    fun animate() {
        currentFrame++
        if (currentFrame >= maxFrame) {
            currentFrame = 0
            currentAnimationIndex++
            if (currentAnimationIndex >= animationSize) {
                currentAnimationIndex = 0
            }
        }
    }
}
