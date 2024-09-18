package com.example.todo.Fragment

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.Adapter.CalendarTodoAdapter
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val calendarTodoAdapter = CalendarTodoAdapter()
    private var todoList: List<TodoEntity> = listOf()
    private var selectedDate: LocalDate? = null
    private lateinit var db: TodoDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        db = TodoDatabase.getInstance(requireContext())!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh()

        // 클릭하면 현재 월로 이동
        binding.monthText.setOnClickListener{
            refresh()
        }

        binding.nextMonth.setOnClickListener {
            binding.calendar.findFirstVisibleMonth()?.let {
                binding.calendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.previousMonth.setOnClickListener {
            binding.calendar.findFirstVisibleMonth()?.let {
                binding.calendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(){
        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(daysOfWeek)

        binding.calendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)

        selectedDate = LocalDate.now()
        updateAdapter(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = calendarTodoAdapter
        }
        calendarTodoAdapter.notifyDataSetChanged()

        binding.calendar.monthScrollListener = { month ->
            binding.monthText.text = month.yearMonth.toString()

            selectedDate?.let {
                selectedDate = LocalDate.now()
                binding.calendar.notifyDateChanged(it)
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    private fun updateAdapter(date: String){
        calendarTodoAdapter.clearList()
        CoroutineScope(Dispatchers.IO).launch {
            todoList = db.todoDAO().getTodo()
            activity?.runOnUiThread{
                val dateFormat = SimpleDateFormat("yyyy.MM.dd")
                for(todo in todoList){
                    try {
                        // 문자열을 Date 객체로 변환
                        val today: Date = dateFormat.parse(date)!!
                        val date1: Date = dateFormat.parse(todo.startDate)!!
                        val date2: Date = dateFormat.parse(todo.endDate)!!

                        if((today.after(date1) && today.before(date2)) || today == date1 || today == date2){
                            calendarTodoAdapter.addListItem(todo)
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
                calendarTodoAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTodos(date: String): Int {
        var result = 0
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")

        for(todo in todoList){
            try {
                // 문자열을 Date 객체로 변환
                val today: Date = dateFormat.parse(date)!!
                val date1: Date = dateFormat.parse(todo.startDate)!!
                val date2: Date = dateFormat.parse(todo.endDate)!!

                if((today.after(date1) && today.before(date2)) || today == date1 || today == date2){
                    result++
                    if(result == 3){
                        break
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        return result
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
                            updateAdapter(day.date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
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

                when (getTodos(data.date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))) {
                    0 -> {
                        container.binding.dayTodo1.visibility = View.GONE
                        container.binding.dayTodo2.visibility = View.GONE
                        container.binding.dayTodo3.visibility = View.GONE
                    }
                    1 -> {
                        container.binding.dayTodo1.visibility = View.VISIBLE
                        container.binding.dayTodo2.visibility = View.GONE
                        container.binding.dayTodo3.visibility = View.GONE
                    }
                    2 -> {
                        container.binding.dayTodo1.visibility = View.VISIBLE
                        container.binding.dayTodo2.visibility = View.VISIBLE
                        container.binding.dayTodo3.visibility = View.GONE
                    }
                    3 -> {
                        container.binding.dayTodo1.visibility = View.VISIBLE
                        container.binding.dayTodo2.visibility = View.VISIBLE
                        container.binding.dayTodo3.visibility = View.VISIBLE
                    }
                }

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
