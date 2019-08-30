package com.example.doctorapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdaptorForAnalysis (var context: Context, var date:ArrayList<AnalysisDate>) : BaseAdapter(){

    private class ViewHolder(row: View?){
        var test_date: TextView

        init {
            this.test_date=row?.findViewById(R.id.tv_test_date) as TextView
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var tek_satir_view: View?
        var viewHolder:ViewHolder

        if(convertView==null){
            val layouinflator= LayoutInflater.from(context)
            tek_satir_view=layouinflator.inflate(R.layout.analysis_list,parent,false)
            viewHolder=ViewHolder(tek_satir_view)
            tek_satir_view.tag=viewHolder
        }
        else{
            tek_satir_view=convertView
            viewHolder=tek_satir_view.tag as ViewHolder
        }


        var current_result=getItem(position) as AnalysisDate
        viewHolder.test_date.text=current_result.date

        return  tek_satir_view as View
    }

    override fun getItem(position: Int): Any {
        return date.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return date.count()
    }
    fun updateList(list: ArrayList<AnalysisDate>){
        date.clear()
        list.addAll(list)
        notifyDataSetChanged()

    }
}