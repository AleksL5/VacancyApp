package com.example.vacancyapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.vacancyapp.databinding.FragmentPasswordBinding
import com.google.android.material.button.MaterialButton

class PasswordFragment : Fragment(R.layout.fragment_password) {

    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var codeInputs: List<EditText>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPasswordBinding.bind(view)

        binding.btnVerify.setOnClickListener {
            findNavController().navigate(R.id.action_passwordFragment_to_searchFragment)
        }

        codeInputs = listOf(
            binding.etCode1,
            binding.etCode2,
            binding.etCode3,
            binding.etCode4
        )

        setupCodeInputWatcher()
        updateButtonState() // Проверка состояния кнопки при запуске
    }
    private fun setupCodeInputWatcher() {
        codeInputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && s.length == 1) {
                        editText.postDelayed({
                            editText.removeTextChangedListener(this)
                            editText.setText("•")
                            editText.setSelection(1)
                            editText.addTextChangedListener(this)


                            if (index < codeInputs.size - 1) {
                                codeInputs[index + 1].requestFocus()
                            }
                        }, 250)
                    }
                    updateButtonState()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

//    private fun setupCodeInputWatcher() {
//        codeInputs.forEachIndexed { index, editText ->
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    updateButtonState() // Переключение на следующее поле, если введен символ
//                    if (s?.length == 1 && index < codeInputs.size - 1) {
//                        codeInputs[index + 1].requestFocus()
//                    } // Если поле очищается, перейти к предыдущему
//                    else if (s?.isEmpty() == true && index > 0) {
//                        codeInputs[index - 1].requestFocus()
//                    }
//                }
//
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            }) // Обработка нажатия "Удалить" для возврата на предыдущее поле
//            editText.setOnKeyListener { _, keyCode, event ->
//                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
//                    if (editText.text.isEmpty() && index > 0) {
//                        codeInputs[index - 1].requestFocus()
//                    }
//                    true
//                } else {
//                    false
//                }
//            }
//        }
//    }

    private fun updateButtonState() {
        val isAllFieldsFilled = codeInputs.all { it.text.toString().trim().isNotEmpty() }
        binding.btnVerify.isEnabled = isAllFieldsFilled
        binding.btnVerify.alpha = if (isAllFieldsFilled) 1.0f else 0.7f
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}