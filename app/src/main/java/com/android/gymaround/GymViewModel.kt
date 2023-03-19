package com.android.gymaround

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GymViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var state by mutableStateOf(emptyList<Gym>())

    private var apiService: GymsApiService

    val errorHandle = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .baseUrl("https://cairo-gyms-a59ef-default-rtdb.europe-west1.firebasedatabase.app/")
            .build()

        apiService = retrofit.create(GymsApiService::class.java)

        getGyms()
    }

    private fun getGyms() {

        viewModelScope.launch {

            try {
                // io thread
                val gyms = getGymsFromRemoteDB()

                // main thread
                state = gyms.restoreSelectedGym()

            } catch (ex: Exception) {

                ex.printStackTrace()
            }
        }
    }

    private suspend fun getGymsFromRemoteDB() = withContext(Dispatchers.IO) { apiService.getGym() }

    fun toggleFavoriteState(gymId: Int) {
        val gyms = state.toMutableList()
        val itemIndex = gyms.indexOfFirst { it.id == gymId }
        gyms[itemIndex] = gyms[itemIndex].copy(isFavourite = !gyms[itemIndex].isFavourite)
        storeSelectedGym(gyms[itemIndex])
        state = gyms

    }

    private fun storeSelectedGym(gym: Gym) {

        val savedHandelList = savedStateHandle.get<List<Int>?>(FAV_IDS).orEmpty().toMutableList()

        if (gym.isFavourite) savedHandelList.add(gym.id)
        else savedHandelList.remove(gym.id)

        savedStateHandle[FAV_IDS] = savedHandelList
    }

    private fun List<Gym>.restoreSelectedGym(): List<Gym> {
        val gyms = this
        savedStateHandle.get<List<Int>?>(FAV_IDS)?.let {
            it.forEach { gymId ->
                gyms.find { it.id == gymId }?.isFavourite = true
            }
        }

        return gyms
    }

    companion object {
        const val FAV_IDS = "favoriteGymsId"
    }
}