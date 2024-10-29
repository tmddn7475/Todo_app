package com.ithink.dailytodo.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ithink.dailytodo.Object.Command
import com.ithink.dailytodo.Interface.SelectDateInterface
import com.ithink.dailytodo.databinding.DialogSelectDateBinding

class SelectDateDialog(private var textView: TextView, private var selectTimeInterface: SelectDateInterface): DialogFragment() {
    private var _binding: DialogSelectDateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectDateBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.textView.text = Command.getToday()

        binding.calendarView.setOnDateChangeListener{
            _, year, month, dayOfMonth ->
            val selectedDate = "$year.${month+1}.$dayOfMonth"
            binding.textView.text = selectedDate
        }

        binding.btnNo.setOnClickListener{
            dismiss()
        }
        binding.btnYes.setOnClickListener{
            selectTimeInterface.selectedDate(textView, binding.textView.text.toString())
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}