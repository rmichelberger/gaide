package games.thinkin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform