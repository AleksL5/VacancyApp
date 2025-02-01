package com.example.vacancyapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _vacancies = MutableLiveData<List<Vacancy>>()
    val vacancies: LiveData<List<Vacancy>> get() = _vacancies

    private val _recommendations = MutableLiveData<List<Recommendation>>()
    val recommendations: LiveData<List<Recommendation>> get() = _recommendations

    private val _favorites = MutableLiveData<List<Vacancy>>(
        savedStateHandle["favorites"] ?: emptyList()
    )
    val favorites: LiveData<List<Vacancy>> get() = _favorites

    init {
        loadRecommendations()
        loadVacancies()
    }
    private inline fun <reified T> loadDataFromJson(fileName: String, jsonArrayName: String): List<T> {
        val context = getApplication<Application>().applicationContext
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray(jsonArrayName)
            val type = object : TypeToken<List<T>>() {}.type
            Gson().fromJson(jsonArray.toString(), type)
        } catch (e: JSONException) {
            Log.e("SearchViewModel", "JSON parsing error", e)
            emptyList()
        } catch (e: IOException) {
            Log.e("SearchViewModel", "Error reading file", e)
            emptyList()
        }
    }

    private fun loadRecommendations() {
        _recommendations.value = loadDataFromJson("data.json", "offers")
    }

    private fun loadVacancies() {
        _vacancies.value = loadDataFromJson("data.json", "vacancies")
    }

    fun toggleFavorite(vacancy: Vacancy) {
        Log.d("ViewModel", "Toggling favorite for: ${vacancy.title}")
        vacancy.isFavorite = !vacancy.isFavorite

        val currentFavorites = _favorites.value?.toMutableList() ?: mutableListOf()
        if (vacancy.isFavorite) {
            Log.d("ViewModel", "Adding to favorites: ${vacancy.title}")
            currentFavorites.add(vacancy)
        } else {
            Log.d("ViewModel", "Removing from favorites: ${vacancy.title}")
            currentFavorites.remove(vacancy)
        }

        _favorites.value = currentFavorites
        Log.d("ViewModel", "Updated favorites size: ${currentFavorites.size}")
        savedStateHandle["favorites"] = currentFavorites
    }
}
