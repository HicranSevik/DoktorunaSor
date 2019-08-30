package com.example.doctorapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class CustomAdaptorPatientListForToday(var context: Context, var patient:ArrayList<Patients>) : BaseAdapter() {

    private class ViewHolder(row: View?){
        var patient_name: TextView
        var patient_image: ImageView
        init {
            this.patient_name=row?.findViewById(R.id.tv_name_surname) as TextView
            this.patient_image=row?.findViewById(R.id.img_profil) as ImageView

        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var tek_satir_view: View?
        var viewHolder:ViewHolder

        if(convertView==null){
            val layouinflator= LayoutInflater.from(context)
            tek_satir_view=layouinflator.inflate(R.layout.patient_list,parent,false)
            viewHolder=ViewHolder(tek_satir_view)
            tek_satir_view.tag=viewHolder
        }
        else{
            tek_satir_view=convertView
            viewHolder=tek_satir_view.tag as ViewHolder
        }


        var current_patient=getItem(position) as Patients
        viewHolder.patient_name.text=current_patient.patient_name
        //viewHolder.patient_image.setImageResource(current_patient.image)
        Picasso.get().load(current_patient.image).into(viewHolder.patient_image)

        return  tek_satir_view as View
    }

    override fun getItem(position: Int): Any {
        return patient.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return patient.count()
    }
}