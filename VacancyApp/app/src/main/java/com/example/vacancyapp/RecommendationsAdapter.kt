package com.example.vacancyapp


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.vacancyapp.databinding.ItemRecommendationBinding


class RecommendationsAdapter(
    private var recommendations: List<Recommendation>
) : RecyclerView.Adapter<RecommendationsAdapter.RecommendationViewHolder>() {

    fun updateData(newRecommendations: List<Recommendation>) {
        recommendations = newRecommendations
        notifyDataSetChanged()
    }

    inner class RecommendationViewHolder(val binding: ItemRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val binding = ItemRecommendationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecommendationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val recommendation = recommendations[position]

        with(holder.binding) {
            title.text = recommendation.title

            // Устанавливаем иконку
            val iconResId = getIconResource(recommendation.id ?: "default_id")
            icon.setImageResource(iconResId)


            if (recommendation.button != null) {
                subtitle.text = recommendation.button.text
                subtitle.visibility = View.VISIBLE
            } else {
                subtitle.visibility = View.GONE
            }

            cardView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recommendation.link))
                root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return recommendations.size
    }

    private fun getIconResource(recommendationId: String): Int {
        return when (recommendationId) {
            "near_vacancies" -> R.drawable.ic_near_vacancies
            "level_up_resume" -> R.drawable.ic_level_up_resume
            "temporary_job" -> R.drawable.ic_temporary_job
            else -> R.drawable.ic_questions
        }
    }
}
//    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
//        val recommendation = recommendations[position]
//        with(holder.binding) {
//            title.text = recommendation.title
//
//            // Устанавливаем текст кнопки, если он есть
//            if (recommendation.button != null) {
//                subtitle.text = recommendation.button.text
//                subtitle.visibility = View.VISIBLE
//            } else {
//                subtitle.visibility = View.GONE
//            }
//
//            // Обрабатываем нажатие на карточку
//            cardView.setOnClickListener {
//                val context = root.context
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recommendation.link))
//                if (intent.resolveActivity(context.packageManager) != null) {
//                    context.startActivity(intent)
//                } else {
//                    Toast.makeText(context, "Невозможно открыть ссылку", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

