package com.daily.dayo.domain.usecase.notice

import com.daily.dayo.domain.repository.NoticeRepository
import javax.inject.Inject

class RequestDetailNoticeUseCase @Inject constructor(
    private val noticeRepository: NoticeRepository
) {
    suspend operator fun invoke(noticeId: Int) =
        noticeRepository.requestDetailNotice(noticeId)
}