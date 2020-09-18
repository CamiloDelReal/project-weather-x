package org.xapps.apps.weatherx.views.fragments

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentHomeBinding
import org.xapps.apps.weatherx.services.models.Location
import org.xapps.apps.weatherx.services.settings.SettingsService
import org.xapps.apps.weatherx.viewmodels.HomeViewModel
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var settings: SettingsService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedElementEnterTransition =
            TransitionInflater.from(this.context).inflateTransition(R.transition.change_bounds)
        sharedElementReturnTransition =
            TransitionInflater.from(this.context).inflateTransition(R.transition.change_bounds)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        ViewCompat.setTranslationZ(binding.root, 1f)
        binding.location = Location("Serbia", "Stari", 0.0, 0.0, 10240, true, 28, "Clear")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.i("Checking settings var =  ${settings}")

        // background color
        chart.setBackgroundColor(Color.TRANSPARENT)

        // disable description text
        chart.getDescription().setEnabled(false)

        // enable touch gestures
        chart.setTouchEnabled(true)

        // set listeners
//            chart.setOnChartValueSelectedListener(this)
        chart.setDrawGridBackground(false)

        // create marker to display box when values are selected
//            val mv = MyMarkerView(this, R.layout.custom_marker_view)

        // Set the marker to the chart
//            mv.setChartView(chart)
//            chart.setMarker(mv)

        // enable scaling and dragging
        chart.setDragEnabled(true)
        chart.setScaleEnabled(true)
        chart.isHighlightPerTapEnabled = false
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
//            chart.setPinchZoom(true)


        var xAxis: XAxis
        xAxis = chart.getXAxis()
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
//        xAxis.isGranularityEnabled = false
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                Log.i("AppLogger", "getAxisLabel $value")
                return when {
                    value == -1f -> "Now"
                    value <= 12 -> "${value.toInt()} am"
                    else -> "${value.toInt() % 12} pm"
                }
            }

            override fun getPointLabel(entry: Entry?): String {
                Log.i("AppLogger-PointLabel", "${entry?.x}, ${entry?.y}")
                return super.getPointLabel(entry)
            }
        }

        // vertical grid lines
//            xAxis.enableGridDashedLine(10f, 10f, 0f)

        var yAxis: YAxis
        yAxis = chart.getAxisLeft()

//        yAxis.valueFormatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                return "Cam ${value.toString()}"
//            }
//        }

//        yAxis.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false

        // disable dual axis (only use LEFT axis)
//            chart.getAxisRight().setEnabled(true)

        // horizontal grid lines
//        yAxis.enableGridDashedLine(10f, 10f, 0f)

        yAxis.axisMaximum = 38f
        yAxis.axisMinimum = 11f

        val values: ArrayList<Entry> = ArrayList()
        values.add(Entry(-1f, 28f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
        values.add(Entry(13f, 26f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
        values.add(Entry(14f, 25f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
        values.add(Entry(15f, 25f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
        values.add(Entry(16f, 23f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
        values.add(Entry(17f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(6f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(7f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(8f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(9f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(10f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(11f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(12f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        values.add(Entry(13f, 21f, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
        val set = LineDataSet(values, "")
        set.setDrawIcons(false)
        set.color = Color.WHITE
        set.setCircleColor(Color.WHITE)
        set.valueTextColor = Color.WHITE
        set.lineWidth = 1f
        set.circleRadius = 3f
        set.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                Log.i("AppLogger-Set", "Value = $value")
                return "F ${value.toString()}"
            }
        }
//        set.isDashedLineEnabled = false
//        set.formLineWidth = 1f
//        set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
//        set.formSize = 15f
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set) // add the data sets
        val data = LineData(dataSets)

//        val l =  chart.getLegend()
//        l.setForm(Legend.LegendForm.LINE)

        chart.data = data

    }

    private fun setData(count: Int, range: Float) {


//        for (i in 0 until count) {
//            val `val` = (Math.random() * range).toFloat() - 30
//            values.add(Entry(i.toFloat(), `val`, ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)))
//        }
//
//        if (chart.getData() != null &&
//            chart.getData().getDataSetCount() > 0
//        ) {
//            set1 = chart.getData().getDataSetByIndex(0) as LineDataSet
//            set1.setValues(values)
//            set1.notifyDataSetChanged()
//            chart.getData().notifyDataChanged()
//            chart.notifyDataSetChanged()
//        } else {
//            // create a dataset and give it a type
//            set1 = LineDataSet(values, "DataSet 1")
//            set1.setDrawIcons(false)
//
//            // draw dashed line
////            set1.enableDashedLine(10f, 5f, 0f)
//
//            // black lines and points

//
//            // line thickness and point size

//
//            // draw points as solid circles
//            set1.setDrawCircleHole(false)
//
//            // customize legend entry

//
//            // text size of values
//            set1.valueTextSize = 9f
//
//            // draw selection line as dashed
////            set1.enableDashedHighlightLine(10f, 5f, 0f)
//
//            // set the filled area
//            set1.setDrawFilled(true)
//            set1.fillFormatter =
//                IFillFormatter { dataSet, dataProvider -> chart.getAxisLeft().getAxisMinimum() }
//
//            // set color of filled area
//                // drawables only supported on api level 18 and above
////                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
////                set1.fillDrawable = drawable
//
//
//            // create a data object with the data sets
//
//        }
    }
}