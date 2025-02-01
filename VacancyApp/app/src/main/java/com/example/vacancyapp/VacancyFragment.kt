package com.example.vacancyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.vacancyapp.databinding.FragmentVacancyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class VacancyFragment : Fragment() {

    private lateinit var binding: FragmentVacancyBinding
    private val viewModel: ViewModel by activityViewModels()
    private lateinit var vacancy: Vacancy
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVacancyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем переданный ID вакансии
        val vacancyId = arguments?.getString("vacancyId") ?: return

        // Ищем вакансию в ViewModel
        vacancy = viewModel.vacancies.value?.find { it.id == vacancyId }
            ?: throw IllegalArgumentException("Vacancy not found")

        setInfoVacancy()
        setupMap()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivFavorite.setOnClickListener {
            toggleFavorite()
        }
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            (activity as? MenuStateUpdater)?.updateFavoritesMenuState(false, favorites.size)
        }
    }

    private fun setInfoVacancy() {
        with(binding) {
            tvJobTitle.text = vacancy.title
            tvCompany.text = vacancy.company
            tvLocation.text = "${vacancy.address.town}, ${vacancy.address.street}, ${vacancy.address.house}"
            tvExperience.text = vacancy.experience.text
            tvSalary.text = vacancy.salary.full
            tvDescription.text = vacancy.description
            tvResponsibilities.text = "Ваши задачи:\n" + vacancy.responsibilities
            ivFavorite.setImageResource(
                if (vacancy.isFavorite) R.drawable.ic_favorites_active else R.drawable.ic_favorites_inactive
            )
        }
    }

    private fun setupMap() {
        mapView = binding.mapView
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(0))

        // Настройка карты
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val fullAddress = "${vacancy.address.town}, ${vacancy.address.street}, ${vacancy.address.house}"

        // Запускаем геокодирование в фоновом потоке
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val coordinates = geocodeAddress(fullAddress)
            withContext(Dispatchers.Main) {  // Возвращаемся в UI-поток
                if (coordinates != null) {
                    val (latitude, longitude) = coordinates
                    Log.d("Geocode", "Координаты: lat=$latitude, lon=$longitude")

                    val startPoint = GeoPoint(latitude, longitude)
                    mapView.controller.setZoom(15.0)
                    mapView.controller.setCenter(startPoint)

                    // Добавление маркера вакансии
                    val marker = Marker(mapView)
                    marker.position = startPoint
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = vacancy.company
                    marker.snippet = fullAddress
                    mapView.overlays.add(marker)

                } else {
                    Log.e("Geocode", "Не удалось получить координаты")
                }
            }
        }
    }

    private fun toggleFavorite() {
        viewModel.toggleFavorite(vacancy)
        binding.ivFavorite.setImageResource(
            if (vacancy.isFavorite) R.drawable.ic_favorites_active else R.drawable.ic_favorites_inactive
        )
    }

    private fun geocodeAddress(address: String): Pair<Double, Double>? {
        return try {
            val client = OkHttpClient()
            val url = "https://nominatim.openstreetmap.org/search?format=json&q=${address.replace(" ", "+")}"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "YourAppName") // Указываем User-Agent
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null

                val jsonArray = JSONArray(response.body?.string() ?: return null)
                if (jsonArray.length() == 0) return null

                val location = jsonArray.getJSONObject(0)
                val lat = location.getDouble("lat")
                val lon = location.getDouble("lon")

                Pair(lat, lon)
            }
        } catch (e: Exception) {
            Log.e("Geocode", "Ошибка геокодирования: ${e.message}")
            null
        }
    }
}