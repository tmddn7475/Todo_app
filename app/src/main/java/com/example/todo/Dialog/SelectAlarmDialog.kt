package com.example.todo.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.todo.Interface.SelectAlarmInterface
import com.example.todo.R
import com.example.todo.databinding.DialogSelectAlarmBinding

class SelectAlarmDialog(private var textView: TextView, private var selectAlarmInterface: SelectAlarmInterface): DialogFragment() {
    private var _binding: DialogSelectAlarmBinding? = null
    private val binding get() = _binding!!
    private var alarm: String = "알림 없음"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectAlarmBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.rgNone.isChecked = true
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.rg_none -> alarm = "알림 없음"
                R.id.rg_5_min -> alarm = "5분 전"
                R.id.rg_10_min -> alarm = "10분 전"
                R.id.rg_1_hour -> alarm = "1시간 전"
                R.id.rg_1_day -> alarm = "1일 전"
            }
        }

        binding.btnNo.setOnClickListener{
            dismiss()
        }
        binding.btnYes.setOnClickListener{
            selectAlarmInterface.selectedAlarm(textView, alarm)
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}