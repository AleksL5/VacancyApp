package com.example.vacancyapp

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vacancyapp.databinding.FragmentFavoritesBinding
import com.example.vacancyapp.databinding.FragmentVacancyBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModel by activityViewModels()

    private lateinit var vacanciesAdapter: VacanciesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем адаптер для RecyclerView
        vacanciesAdapter = VacanciesAdapter(
            emptyList(),
            onFavoriteClickListener = { vacancy ->
                Log.d("FavoritesFragment", "Favorite clicked: ${vacancy.title}")
                viewModel.toggleFavorite(vacancy) // Удалить или добавить в избранное
            },
            onItemClickListener = { vacancy ->
                Log.d("FavoritesFragment", "Vacancy clicked: ${vacancy.title}")
                val action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToVacancyFragment(vacancy.id)
                findNavController().navigate(action)
            }
        )

        // Настраиваем RecyclerView
        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vacanciesAdapter
        }

        // Подписываемся на изменения списка избранных
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            Log.d("FavoritesFragment", "Favorites updated: ${favorites.size} items")
            updateVacanciesList(favorites) // Обновляем данные в RecyclerView
            updateFavoritesCount(favorites.size) // Обновляем заголовок
            (activity as? MenuStateUpdater)?.updateFavoritesMenuState(false, favorites.size)
        }
    }

    // Обновление списка вакансий в RecyclerView
    private fun updateVacanciesList(vacancies: List<Vacancy>) {
        Log.d("FavoritesFragment", "Updating RecyclerView with ${vacancies.size} vacancies")
        vacanciesAdapter.updateData(vacancies) // Передаем только избранные вакансии в адаптер
    }

    // Обновление заголовка с количеством избранных
    private fun updateFavoritesCount(count: Int) {
        val countText = when (count) {
            0 -> "Нет избранных вакансий"
            1 -> "1 избранная вакансия"
            else -> "$count избранных вакансий"
        }
        binding.tvFavoritesCount.text = countText
        Log.d("FavoritesFragment", "Updated favorites count: $count")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val favoritesCount = viewModel.favorites.value?.size ?: 0
        (requireActivity() as? MenuStateUpdater)?.updateFavoritesMenuState(
            isFragmentActive = true,
            favoritesCount = favoritesCount
        )
    }

    override fun onPause() {
        super.onPause()
        val favoritesCount = viewModel.favorites.value?.size ?: 0
        (requireActivity() as? MenuStateUpdater)?.updateFavoritesMenuState(
            isFragmentActive = false,
            favoritesCount = favoritesCount
        )
    }
}
