package daily.dayo.data.datasource.remote.retrofit.factory

import daily.dayo.domain.model.NetworkResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResponseAdapter<T> (
    private val successType: Type,
) : CallAdapter<T, Call<NetworkResponse<T>>> { // 여기서 <앞, 뒤>에 넣어준 것에 따라

    override fun responseType(): Type = successType
    override fun adapt(call: Call<T>): Call<NetworkResponse<T>> { // in(Call<앞>) out(Call<뒤>)의 타입이 정해짐
        return NetworkResponseCall(call) // 얘는 Custom으로 구현한 클래스 아래에서 작성해줄것
    }
}