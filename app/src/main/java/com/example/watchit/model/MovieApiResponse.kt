package com.example.watchit.model

data class MovieApiResponse(
    val results: Array<Movie>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieApiResponse

        return results.contentEquals(other.results)
    }

    override fun hashCode(): Int {
        return results.contentHashCode()
    }
}