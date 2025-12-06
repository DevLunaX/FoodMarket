package mx.edu.utng.foodmarket.data.model

data class Review(
    val id: String = "",
    val businessId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val response: String? = null,
    val responseTimestamp: Long? = null
)