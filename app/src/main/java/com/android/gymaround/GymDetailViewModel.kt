package com.android.gymaround

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GymDetailViewModel (stateHandle: SavedStateHandle): ViewModel() {

    val state = mutableStateOf<Gym?>(null)

    private var apiService: GymsApiService

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://cairo-gyms-a59ef-default-rtdb.europe-west1.firebasedatabase.app/")
            .build()

        apiService = retrofit.create(GymsApiService::class.java)

        val gymId = stateHandle.get<Int>("gym_id") ?: 0
        getDetail(gymId)
    }

    private fun getDetail(id: Int) {

        viewModelScope.launch {

            val gymDetail = getGymsFromRemoteDB(id)
            state.value = gymDetail
        }
    }

    private suspend fun getGymsFromRemoteDB(id: Int) =
        withContext(Dispatchers.IO) {
            apiService.getGymDetail(id).values.first()
        }
}