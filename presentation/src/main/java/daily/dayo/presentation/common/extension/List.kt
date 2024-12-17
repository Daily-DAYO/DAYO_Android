package daily.dayo.presentation.common.extension

fun <T> List<T>.limitTo(limit: Int): List<T> = this.take(limit)