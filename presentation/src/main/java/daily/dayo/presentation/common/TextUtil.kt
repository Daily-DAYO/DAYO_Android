package daily.dayo.presentation.common

import java.util.regex.Pattern

object TextLimitUtil {

    // 이모지를 한 글자로 여겨 글자 수를 제한
    fun trimToMaxLength(text: String, maxLength: Int): String {
        val pattern = Pattern.compile("(\\p{M}|.){1,$maxLength}")
        val matcher = pattern.matcher(text)
        var trimmedText = ""

        if (matcher.find()) {
            trimmedText = matcher.group(0) ?: ""
        }

        return trimmedText
    }
}