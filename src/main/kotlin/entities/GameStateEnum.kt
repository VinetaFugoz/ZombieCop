package entities

enum class GameStateEnum {
    NORMAL {
        override fun getText(): String = "NORMAL"
    },
    CONGRATULATIONS {
        override fun getText(): String = "CONGRATULATIONS"
    },
    GAME_OVER {
        override fun getText(): String = "GAME OVER"
    };

    abstract fun getText(): String
}