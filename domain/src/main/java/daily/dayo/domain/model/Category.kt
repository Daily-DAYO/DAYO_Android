package daily.dayo.domain.model

enum class Category {
    ALL, ETC, GOOD_NOTE, POCKET_BOOK, SCHEDULER, SIX_DIARY, STUDY_PLANNER
}

fun categoryKR(category: Category): String {
    return when(category){
        Category.SCHEDULER -> "스케줄러"
        Category.STUDY_PLANNER -> "스터디 플래너"
        Category.GOOD_NOTE -> "굿노트"
        Category.POCKET_BOOK -> "포켓북"
        Category.SIX_DIARY -> "6공 다이어리"
        Category.ETC -> "기타"
        else -> ""
    }
}
