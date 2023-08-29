package daily.dayo.presentation.common

import android.text.Editable

object ReplaceUnicode {
    fun replaceBlankText(originalText: String): String {
        var editedText = originalText
        editedText = editedText.replace(" ", "")
        editedText = editedText.replace("\u00A0", "")
        editedText = editedText.replace("\u2000", "")
        editedText = editedText.replace("\u2001", "")
        editedText = editedText.replace("\u2002", "")
        editedText = editedText.replace("\u2003", "")
        editedText = editedText.replace("\u2004", "")
        editedText = editedText.replace("\u2005", "")
        editedText = editedText.replace("\u2006", "")
        editedText = editedText.replace("\u200b", "")
        return editedText
    }

    fun trimBlankText(originalText: String): String {
        var editedText = originalText
        editedText = editedText.trim()
        editedText = editedText.trim(' ')
        editedText = editedText.trim('\u00A0')
        editedText = editedText.trim('\u2000')
        editedText = editedText.trim('\u2001')
        editedText = editedText.trim('\u2002')
        editedText = editedText.trim('\u2003')
        editedText = editedText.trim('\u2004')
        editedText = editedText.trim('\u2005')
        editedText = editedText.trim('\u2006')
        editedText = editedText.trim('\u200b')
        return editedText
    }

    fun trimBlankText(originalText: Editable?): String {
        var editedText = (originalText ?: "").toString()
        editedText = editedText.trim()
        editedText = editedText.trim(' ')
        editedText = editedText.trim('\u00A0')
        editedText = editedText.trim('\u2000')
        editedText = editedText.trim('\u2001')
        editedText = editedText.trim('\u2002')
        editedText = editedText.trim('\u2003')
        editedText = editedText.trim('\u2004')
        editedText = editedText.trim('\u2005')
        editedText = editedText.trim('\u2006')
        editedText = editedText.trim('\u200b')
        return editedText
    }
}