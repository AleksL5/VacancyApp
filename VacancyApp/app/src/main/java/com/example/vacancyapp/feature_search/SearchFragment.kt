package com.example.vacancyapp.feature_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vacancyapp.feature_vacancies.RecommendationsAdapter
import com.example.vacancyapp.feature_vacancies.VacanciesAdapter
import com.example.vacancyapp.core_data.ViewModel
import com.example.vacancyapp.app.MenuStateUpdater
import com.example.vacancyapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: ViewModel by activityViewModels()
    private lateinit var vacanciesAdapter: VacanciesAdapter
    private lateinit var recommendationsAdapter: RecommendationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Настраиваем адаптер для рекомендаций
        recommendationsAdapter = RecommendationsAdapter(emptyList())
        binding.recommendationsList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendationsAdapter
        }

        // Настраиваем адаптер для вакансий
        vacanciesAdapter = VacanciesAdapter(
            emptyList(),
            onFavoriteClickListener = { vacancy ->
                viewModel.toggleFavorite(vacancy) // Уведомляем ViewModel о клике на "избранное"
            },
            onItemClickListener = { vacancy ->
                val action =
                    SearchFragmentDirections.actionSearchFragmentToVacancyFragment(vacancy.id)
                findNavController().navigate(action)
            }
        )

        binding.vacanciesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vacanciesAdapter
        }

        // Подписываемся на изменения в рекомендациях
        viewModel.recommendations.observe(viewLifecycleOwner) { recommendations ->
            recommendationsAdapter.updateData(recommendations)
        }

        // Подписываемся на изменения в вакансиях
        viewModel.vacancies.observe(viewLifecycleOwner) { vacancies ->
            val limitedVacancies = vacancies.take(3)
            vacanciesAdapter.updateData(limitedVacancies)

            binding.showMoreButton.text = if (vacancies.size > 3) {
                "Ещё ${vacancies.size - 3} вакансии"
            } else {
                "Все вакансии отображены"
            }
            // Изменяем список вакансий(размер, количество) при нажатии кнопки
            binding.showMoreButton.setOnClickListener {
                showExpandedState()
            }

            binding.btnBack.setOnClickListener {
                showInitialState()
            }
        }

            viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            (activity as? MenuStateUpdater)?.updateFavoritesMenuState(false, favorites.size)
        }
    }

    private fun showExpandedState() {
        val allVacancies = viewModel.vacancies.value ?: emptyList()
        vacanciesAdapter.updateData(allVacancies)

        // Скрытие и показ элементов
        binding.searchIcon.visibility = View.GONE
        binding.btnBack.visibility = View.VISIBLE
        binding.jobsForYouTitle.visibility = View.GONE
        binding.recommendationsList.visibility = View.GONE
        binding.totalVacanciesText.visibility = View.VISIBLE
        binding.totalVacanciesText.text = getWatchingMessage(viewModel.vacancies.value?.size ?: 0)
        binding.sortVacanciesText.visibility = View.VISIBLE
        binding.showMoreButton.visibility = View.GONE
        binding.vacanciesList.setPadding(
            binding.vacanciesList.paddingLeft,
            80,
            binding.vacanciesList.paddingRight,
            230
        )
    }

    private fun showInitialState() {
        val limitedVacancies = viewModel.vacancies.value?.take(3) ?: emptyList()
        vacanciesAdapter.updateData(limitedVacancies)

        // Скрытие и показ элементов
        binding.searchIcon.visibility = View.VISIBLE
        binding.btnBack.visibility = View.GONE
        binding.jobsForYouTitle.visibility = View.VISIBLE
        binding.recommendationsList.visibility = View.VISIBLE
        binding.totalVacanciesText.visibility = View.GONE
        binding.sortVacanciesText.visibility = View.GONE
        binding.showMoreButton.visibility = View.VISIBLE
        binding.vacanciesList.setPadding(
            binding.vacanciesList.paddingLeft,
            1,
            binding.vacanciesList.paddingRight,
            1
        )
    }
    fun getWatchingMessage(count: Int): String {
        val suffix = when {
            count % 100 in 11..19 -> "вакансий"
            count % 10 == 1 -> "вакансия"
            count % 10 in 2..4 -> "вакансии"
            else -> "вакансий"
        }
        return "$count $suffix"
    }

}

