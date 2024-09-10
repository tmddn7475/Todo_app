package com.example.todo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.todo.Interface.SelectTimeInterface
import com.example.todo.databinding.DialogSelectTimeBinding

class SelectTimeDialog(var textView: TextView, var selectTimeInterface: SelectTimeInterface): DialogFragment() {
    private var _binding: DialogSelectTimeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectTimeBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.timePicker.setIs24HourView(true)

        binding.timeBtnNo.setOnClickListener{
            dismiss()
        }
        binding.timeBtnYes.setOnClickListener{
            val time = if(binding.timePicker.minute < 10){
                "${binding.timePicker.hour}:0${binding.timePicker.minute}"
            } else {
                "${binding.timePicker.hour}:${binding.timePicker.minute}"
            }
            selectTimeInterface.selected(textView, time)
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}