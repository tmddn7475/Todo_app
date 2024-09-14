package com.example.todo.Fragment1

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.todo.R
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.CalendarDayBinding
import com.example.todo.databinding.CalendarHeaderBinding
import com.example.todo.databinding.FragmentCalendarBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: LocalDate? = null
    private lateinit var db: TodoDatabase
    private lateinit var todo: List<TodoEntity>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        db = TodoDatabase.getInstance(requireContext())!!

        CoroutineScope(Dispatchers.IO).launch {
            todo = db.todoDAO().getTodo()
            activity?.runOnUiThread{
                for(item in todo){
                    Log.i("todo", item.startDate)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(daysOfWeek)
        binding.calendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)

        selectedDate = LocalDate.now()

        Log.i("ewadwee", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))

        binding.calendar.monthScrollListener = { month ->
            binding.exFiveMonthYearText.text = month.yearMonth.toString()

            selectedDate?.let {
                selectedDate = LocalDate.now()
                binding.calendar.notifyDateChanged(it)
            }
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            binding.calendar.findFirstVisibleMonth()?.let {
                binding.calendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.calendar.findFirstVisibleMonth()?.let {
                binding.calendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>){
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            val binding = this@CalendarFragment.binding
                            binding.calendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.calendar.notifyDateChanged(it) }
                            //updateAdapterForDate(day.date)
                            Log.i("dsad", day.date.toString())
                        }
                    }
                }
            }
        }
        binding.calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val view = container.binding.root
                val textView = container.binding.dayText
                val layout = container.binding.dayLayout
                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.binding.dayText.setTextColor(ContextCompat.getColor(view.context, R.color.text))
                    view.setBackgroundColor(if (selectedDate == data.date) ContextCompat.getColor(view.context, R.color.gray) else 0)
                } else {
                    container.binding.dayText.setTextColor(ContextCompat.getColor(view.context, R.color.gray2))
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        val typeFace = Typeface.create("sans-serif-light", Typeface.NORMAL)
        binding.calendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = true
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                var str = daysOfWeek[index].toString().uppercase()
                                str = str.substring(0, 3)
                                tv.text = str
                                tv.setTextColor(ContextCompat.getColor(binding.calendar.context, R.color.text))
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                                tv.typeface = typeFace
                            }
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
