package daily.dayo.presentation.common.extension

fun String.limitTo(limit: Int): String {
    return if (this.length > limit) this.substring(0, limit) else this
}