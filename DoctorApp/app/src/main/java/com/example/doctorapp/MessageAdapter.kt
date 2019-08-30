package com.example.doctorapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MessageAdapter (var context: Context, var message:ArrayList<Messages>) : BaseAdapter() {

    private lateinit var database: DatabaseReference
    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    val prefences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    val user_tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String

    companion object {
        const val MSG_TYPE_LEFT=0;
        const val MSG_TYPE_RIGHT=1;
    }

    private class ViewHolder(row: View?){
        var show_message: TextView
        init {
            this.show_message=row?.findViewById(R.id.tv_for_messages) as TextView
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var tek_satir_view: View?=null
        var viewHolder:ViewHolder
        var view_type=getItemViewType(position)

        if(convertView==null){
            val layouinflator= LayoutInflater.from(context)


            if(view_type==1){
                tek_satir_view=layouinflator.inflate(R.layout.chat_item_right,parent,false)
            }
            else{
                tek_satir_view = layouinflator.inflate(R.layout.chat_item_left, parent, false)
            }
            viewHolder=ViewHolder(tek_satir_view)
            tek_satir_view!!.tag=viewHolder
        }
        else{
            tek_satir_view=convertView
            viewHolder=tek_satir_view.tag as ViewHolder
        }


        var current_message=getItem(position) as Messages
        viewHolder.show_message.text=current_message.message

        return  tek_satir_view as View
    }

    override fun getItem(position: Int): Any {
        return message.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return message.count()
    }

    override fun getItemViewType(position: Int): Int {
        if(message.get(position).sender.equals(user_tc_kimlik)){
            return  MSG_TYPE_RIGHT
        }
        else{
            return MSG_TYPE_LEFT
        }
    }

    fun updateList(messageList:ArrayList<Messages>){
        message.clear()
        messageList.addAll(messageList)
        notifyDataSetChanged()

    }



}