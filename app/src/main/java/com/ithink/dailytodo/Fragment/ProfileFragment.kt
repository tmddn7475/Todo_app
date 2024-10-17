package com.ithink.dailytodo.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ithink.dailytodo.Activity.DonateActivity
import com.ithink.dailytodo.Activity.FaqActivity
import com.ithink.dailytodo.Activity.PriorityActivity
import com.ithink.dailytodo.Activity.setting.SettingActivity
import com.ithink.dailytodo.RoomDB.TodoDatabase
import com.ithink.dailytodo.RoomDB.TodoEntity
import com.ithink.dailytodo.databinding.FragmentProfileBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.ithink.dailytodo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

@SuppressLint("SimpleDateFormat")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: TodoDatabase
    private var currentDate = LocalDate.now()
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        db = TodoDatabase.getInstance(requireContext())!!

        // 차트 설정
        initBarChart(binding.chart)
        updateWeekText(binding.chartDate)

        // 차트 일정 변경
        binding.chartDateImg.setOnClickListener {
            currentDate = currentDate.minusWeeks(1) // 한 주 전으로 이동
            updateWeekText(binding.chartDate)
        }
        binding.chartDateImg2.setOnClickListener {
            currentDate = currentDate.plusWeeks(1) // 한 주 후로 이동
            updateWeekText(binding.chartDate)
        }

        // 일정 추가
        binding.profileAddTodo.setOnClickListener{
            val intent = Intent(context, com.ithink.dailytodo.Activity.AddTodoActivity::class.java)
            startActivity(intent)
        }

        binding.profilePriority.setOnClickListener{
            val intent = Intent(context, PriorityActivity::class.java)
            startActivity(intent)
        }

        binding.profileDonate.setOnClickListener{
            val intent = Intent(context, DonateActivity::class.java)
            startActivity(intent)
        }

        binding.profileFaq.setOnClickListener{
            val intent = Intent(context, FaqActivity::class.java)
            startActivity(intent)
        }

        binding.profileShare.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"

            val text = getString(R.string.app_share_text) + "\n\nhttps://github.com/tmddn7475"
            intent.putExtra(Intent.EXTRA_TEXT, text)

            val chooserTitle = ""
            startActivity(Intent.createChooser(intent, chooserTitle))
        }

        binding.profileSetting.setOnClickListener{
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }

        return binding.root
    }

    // 차트 설정
    private fun initBarChart(barChart: BarChart) {
        // 차트 회색 배경 설정 (default = false)
        barChart.setDrawGridBackground(false)
        // 막대 그림자 설정 (default = false)
        barChart.setDrawBarShadow(false)
        // 차트 테두리 설정 (default = false)
        barChart.setDrawBorders(false)
        barChart.legend.isEnabled = false
        barChart.setMaxVisibleValueCount(7)

        val description = Description()
        // 오른쪽 하단 모서리 설명 레이블 텍스트 표시 (default = false)
        description.isEnabled = false
        barChart.description = description

        // X, Y 바의 애니메이션 효과
        barChart.animateY(0)
        barChart.animateX(0)

        // 바텀 좌표 값
        val xAxis: XAxis = barChart.xAxis
        // x축 위치 설정
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // 그리드 선 수평 거리 설정
        xAxis.granularity = 1f
        // x축 텍스트 컬러 설정
        xAxis.textSize = 12f
        xAxis.textColor = Color.GRAY
        // x축 선 설정 (default = true)
        xAxis.setDrawAxisLine(false)
        // 격자선 설정 (default = true)
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = MyXAxisFormatter()

        val leftAxis: YAxis = barChart.axisLeft
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawLabels(true)
        leftAxis.axisMaximum = 0f
        leftAxis.axisMaximum = 10f
        leftAxis.granularity = 2f
        leftAxis.textColor = Color.GRAY

        val rightAxis: YAxis = barChart.axisRight
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
    }

    // 차트 값 가져옴
    private fun setData(barChart: BarChart, startOfWeek: String, endOfWeek: String) {
        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false)

        val valueList = ArrayList<BarEntry>()
        val title = ""

        // 데이터 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            val todoData = db.todoDAO().getTodo2() as ArrayList<TodoEntity>
            activity?.runOnUiThread {
                val arr = getWeekRange(startOfWeek, endOfWeek, todoData)
                for (i in arr.indices) {
                    valueList.add(BarEntry(i.toFloat(), arr[i].toFloat()))
                }

                val barDataSet = BarDataSet(valueList, title)
                // 바 색상 설정 (ColorTemplate.LIBERTY_COLORS)
                barDataSet.setColors(Color.rgb(0, 179, 239))
                barDataSet.valueFormatter = MyValueFormatter()

                val data = BarData(barDataSet)
                data.barWidth = 0.5f

                barChart.data = data
                barChart.invalidate()
                barChart.visibility = View.VISIBLE
            }
        }
    }

    private fun getWeekRange(startOfWeek: String, endOfWeek: String, todo: ArrayList<TodoEntity>): Array<Int> {
        val arr = arrayOf(0,0,0,0,0,0,0)

        for(item in todo){
            val start: Date = dateFormat.parse(startOfWeek)!!
            val end: Date = dateFormat.parse(endOfWeek)!!
            val dataDate: Date = dateFormat.parse(item.doneDate)!!

            Log.i("doneDate", item.doneDate)

            if((end.after(dataDate) && start.before(dataDate)) || dataDate == start || dataDate == end){
                val calendar = Calendar.getInstance()
                calendar.time = dataDate
                val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)
                when (dayOfWeek) {
                    1 -> arr[0]++
                    2 -> arr[1]++
                    3 -> arr[2]++
                    4 -> arr[3]++
                    5 -> arr[4]++
                    6 -> arr[5]++
                    7 -> arr[6]++
                }
            }
        }

        return arr
    }

    // 주의 시작일과 종료일을 가져오는 함수
    private fun getCurrentWeek(): Pair<String, String> {
        val startOfWeek = if(currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
            currentDate.with(DayOfWeek.SUNDAY)
        } else {
            currentDate.minusWeeks(1).with(DayOfWeek.SUNDAY)
        }

        val endOfWeek = if(currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
            currentDate.plusWeeks(1).with(DayOfWeek.SATURDAY)
        } else {
            currentDate.with(DayOfWeek.SATURDAY)
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return Pair(startOfWeek.format(formatter), endOfWeek.format(formatter))
    }

    @SuppressLint("SetTextI18n")
    private fun updateWeekText(weekTextView: TextView) {
        val (startOfWeek, endOfWeek) = getCurrentWeek()
        weekTextView.text = "$startOfWeek ~ $endOfWeek"
        setData(binding.chart, startOfWeek, endOfWeek)
    }

    // 차트 x값
    inner class MyXAxisFormatter: ValueFormatter() {
        private val days = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    // 차트 y값 소수점 안 나오게 변경
    inner class MyValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
