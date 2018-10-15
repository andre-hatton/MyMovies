package com.yoshizuka.mymovies.models.entities

data class Response(val page: Int, val total_results: Int, val total_page: Int, val results: List<Movie>) {
}