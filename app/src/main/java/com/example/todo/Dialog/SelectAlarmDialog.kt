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

class SelectAlarmDialog(private var textView: TextView, private var selectAlarmInterface: SelectAlarmInterface, private val boolean: Boolean): DialogFragment() {
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
        
        // 하루종일이 체크 되어있는 경우 당일 8시로 고정
        if(boolean){
            binding.rgOnTime.visibility = View.GONE
            binding.rg5Min.visibility = View.GONE
            binding.rg10Min.visibility = View.GONE
            binding.rg1Hour.visibility = View.GONE
            binding.rg1Day.visibility = View.GONE
            binding.rgAllDay.visibility = View.VISIBLE
        } else {
            binding.rgOnTime.visibility = View.VISIBLE
            binding.rg5Min.visibility = View.VISIBLE
            binding.rg10Min.visibility = View.VISIBLE
            binding.rg1Hour.visibility = View.VISIBLE
            binding.rg1Day.visibility = View.VISIBLE
            binding.rgAllDay.visibility = View.GONE
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.rg_none -> alarm = "알림 없음"
                R.id.rg_on_time -> alarm = "정각"
                R.id.rg_5_min -> alarm = "5분 전"
                R.id.rg_10_min -> alarm = "10분 전"
                R.id.rg_1_hour -> alarm = "1시간 전"
                R.id.rg_1_day -> alarm = "1일 전"
                R.id.rg_all_day -> alarm = "당일 8:00"
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