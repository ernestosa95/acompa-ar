package com.example.acompanar.ManagementModule.StastisticsManagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.acompanar.databinding.ViewholderItemHorizontalChartBinding

class HorizontalBarChartAdapter(
        private val pointListHombres : ArrayList<Int>,
        private val pointListMujeres : ArrayList<Int>,
        private val pointListEdades : ArrayList<String>,
        private val maxValueOnXAxis : Int
        ) : RecyclerView.Adapter<HorizontalBarChartAdapter.HorizontalBarChartViewHolder>() {

    private var viewWidth_mujeres : Int = 0
    private var viewWidth_hombres : Int = 0
    private var parentWidth: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalBarChartViewHolder {
        val binding = ViewholderItemHorizontalChartBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)

        parentWidth = getParentWidth(parent)
        return HorizontalBarChartViewHolder(binding)
    }

    private fun getParentWidth(parent: ViewGroup): Int {
        return (parent.context.resources.displayMetrics.widthPixels)
    }

    override fun getItemCount() : Int = pointListHombres.size

    override fun onBindViewHolder(holder: HorizontalBarChartViewHolder, position: Int) {
        viewWidth_mujeres = calculateViewWidth(pointListMujeres[position])
        viewWidth_hombres = calculateViewWidth(pointListHombres[position])
        holder.binding.apply {
            valueText.text = pointListMujeres[position].toString()
            valueText2.text = pointListHombres[position].toString()
            categoria.text = pointListEdades[position].toString()
            view.layoutParams.width = viewWidth_mujeres
            view3.layoutParams.width = viewWidth_hombres
        }
    }

    private fun calculateViewWidth(value : Int) : Int = value * parentWidth / maxValueOnXAxis

    class HorizontalBarChartViewHolder(val binding: ViewholderItemHorizontalChartBinding) : RecyclerView.ViewHolder(binding.root)

}
