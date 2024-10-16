package com.ithink.dailylist.Dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ithink.dailylist.Interface.SelectTimeInterface
import com.ithink.dailylist.databinding.DialogSelectTimeBinding

class SelectTimeDialog(private var textView: TextView, private var selectTimeInterface: SelectTimeInterface): DialogFragment() {
    private var _binding: DialogSelectTimeBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("DiscouragedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectTimeBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val getTime = textView.text.toString().split(":")

        binding.timePicker.setIs24HourView(true)
        binding.timePicker.hour = getTime[0].toInt()
        binding.timePicker.minute = getTime[1].toInt() / 5

        // minute 부분 5분 단위로 설정
        binding.run {
            timePicker.run {
                // 분 단위 스피너 찾기
                val minutePicker = findViewById<NumberPicker>(resources.getIdentifier("minute", "id", "android"))
                // 5분 간격의 배열을 생성해 분 단위 스피너에 적용하기
                val minuteValues = Array(12) { (it * 5).toString().padStart(2, '0') }
                minutePicker.minValue = 0
                minutePicker.maxValue = 11
                minutePicker.displayedValues = minuteValues
            }
        }

        binding.timeBtnYes.setOnClickListener{
            val time = if(binding.timePicker.minute < 2){
                "${binding.timePicker.hour}:0${binding.timePicker.minute * 5}"
            } else {
                "${binding.timePicker.hour}:${binding.timePicker.minute * 5}"
            }
            selectTimeInterface.selected(textView, time)
            dismiss()
        }
        binding.timeBtnNo.setOnClickListener{
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}