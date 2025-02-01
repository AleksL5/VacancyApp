package com.example.vacancyapp.feature_vacancies

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vacancyapp.R
import com.example.vacancyapp.core_data.Vacancy
import com.example.vacancyapp.databinding.ItemVacancyBinding

class VacanciesAdapter(
    private var vacancies: List<Vacancy>,
    private val onFavoriteClickListener: (Vacancy) -> Unit,
    private val onItemClickListener: (Vacancy) -> Unit
) : RecyclerView.Adapter<VacanciesAdapter.VacancyViewHolder>() {

    fun updateData(newVacancies: List<Vacancy>) {
        Log.d("VacanciesAdapter", "Updating adapter with ${newVacancies.size} items")
        vacancies = newVacancies
        notifyDataSetChanged()
    }

    inner class VacancyViewHolder(val binding: ItemVacancyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val binding = ItemVacancyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VacancyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        val vacancy = vacancies[position]

        with(holder.binding) {
            tvJobTitle.text = vacancy.title
            tvLocation.text = vacancy.address.town
            tvCompany.text = vacancy.company
            tvExperience.text = vacancy.experience.previewText
            tvPublishedDate.text = vacancy.publishedDate
            tvViewingNow.text = getWatchingMessage(vacancy.lookingNumber)

            // Установка состояния "Избранное"
            ivFavorite.setImageResource(
                if (vacancy.isFavorite) R.drawable.ic_favorites_active
                else R.drawable.ic_favorites_inactive
            )

            // Обновление статуса при клике
            ivFavorite.setOnClickListener {
                onFavoriteClickListener(vacancy) // Уведомляем ViewModel
                notifyItemChanged(position) // Обновляем элемент
            }
            root.setOnClickListener {
                onItemClickListener(vacancy)
            }
        }
    }
    fun getWatchingMessage(count: Int): String {
        val suffix = when {
            count % 100 in 11..19 -> "человек"
            count % 10 == 1 -> "человек"
            count % 10 in 2..4 -> "человека"
            else -> "человек"
        }
        return "Сейчас просматривает $count $suffix"
    }

    override fun getItemCount(): Int = vacancies.size
}