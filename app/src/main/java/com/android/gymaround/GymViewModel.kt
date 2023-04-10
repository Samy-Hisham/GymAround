package com.android.gymaround

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GymViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var state by mutableStateOf(emptyList<Gym>())

    private var apiService: GymsApiService

    private var gymDao = GymsDatabase.getDeoInstance(App.getApplicationContext())

//    val errorHandle = CoroutineExceptionHandler { _, throwable ->
//        throwable.printStackTrace()
//    }

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

    private suspend fun getGymsFromRemoteDB() = withContext(Dispatchers.IO) {
        try {

            val gyms = apiService.getGym()
            gymDao.addAll(gyms)
            return@withContext gyms

        } catch (e: Exception) {
            gymDao.getAll()
        }
    }

    fun toggleFavoriteState(gymId: Int) {
        val gyms = state.toMutableList()
        val itemIndex = gyms.indexOfFirst { it.id == gymId }
        gyms[itemIndex] = gyms[itemIndex].copy(isFavourite = !gyms[itemIndex].isFavourite)
        storeSelectedGym(gyms[itemIndex])
        state = gyms
        viewModelScope.launch { toggleFavouriteGym(gymId, gyms[itemIndex].isFavourite) }
    }

    private suspend fun toggleFavouriteGym(gymId: Int, newFavoriteState: Boolean) {
        withContext(Dispatchers.IO) {
            gymDao.update(GymFavoriteState(
                id = gymId,
                isFavourite = newFavoriteState))
        }
    }

    private fun storeSelectedGym(gym: Gym) {

        val savedHandelList = savedStateHandle.get<List<Int>?>(FAV_IDS).orEmpty().toMutableList()

        if (gym.isFavourite) savedHandelList.add(gym.id)
        else savedHandelList.remove(gym.id)

        savedStateHandle[FAV_IDS] = savedHandelList
    }

    private fun List<Gym>.restoreSelectedGym(): List<Gym> {
        savedStateHandle.get<List<Int>?>(FAV_IDS)?.let { savedIds ->
            val gymMap = this.associateBy { it.id }.toMutableMap()
            savedIds.forEach { gymId ->
                val gym = gymMap[gymId] ?: return@forEach
                gymMap[gymId] = gym.copy(isFavourite = true)
            }
            return gymMap.values.toList()
        }
        return this
    }

    companion object {
        const val FAV_IDS = "favoriteGymsId"
    }
}