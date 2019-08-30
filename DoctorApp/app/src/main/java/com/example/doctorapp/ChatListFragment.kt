package com.example.doctorapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.google.firebase.database.*

class ChatListFragment: Fragment() {
    private lateinit var database: DatabaseReference
    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    val KEY_USER_NAME="UserName"
    val IS_DOCTOR="IsDoctor"
    val KEY_USER_PROFILE="UserProfile"
    lateinit var listener: FragmentActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefences = context!!.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String
        val isDoctor=prefences.getString(IS_DOCTOR,"Sonuç Bulunamadı") as String
        //Toast.makeText(context,"Doctor : "+isDoctor,Toast.LENGTH_SHORT).show()

        if(isDoctor=="true"){
            var listview= view?.findViewById(R.id.lv_chat_members) as ListView
            var arr_patient_list:ArrayList<PatientInfo> = ArrayList()
            var patient_id:ArrayList<String> = ArrayList()

            var patient_list= ArrayList<String>()
            database = FirebaseDatabase.getInstance().reference
            var chatMembers = database.child("members")

            chatMembers.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (postSnapshot in dataSnapshot.children) {
                        var doctor=postSnapshot.child("doctor").value.toString()
                        var patient=postSnapshot.child("patient").value.toString()
                        var patient_name=postSnapshot.child("patient_name").value.toString()
                        var patient_image=postSnapshot.child("patient_image").value.toString()
                        var chat_visibility=postSnapshot.child("chat_visibility").value.toString()
                        var chat_key_for_chat_page=postSnapshot.key.toString()


                        if((tc_kimlik==doctor) &&  (chat_visibility.toInt()==1)){
                            patient_id.add(patient)
                            if(patient_image==""){
                                arr_patient_list.add(PatientInfo(patient_name,"https://firebasestorage.googleapis.com/v0/b/doctorapp-acbca.appspot.com/o/images%2Fuser.png?alt=media&token=6194e048-11ee-4edb-9766-1badcf865e97",chat_key_for_chat_page))
                            }
                            else{
                                arr_patient_list.add(PatientInfo(patient_name,patient_image,chat_key_for_chat_page))
                            }
                        }
                    }
                    listview.adapter=CustomAdaptorPatientList(activity!!.applicationContext,arr_patient_list)

                    listview.setOnItemClickListener{
                            parent: AdapterView<*>?, view:View?, position:Int?, id:Long ->

                        val intent = Intent(activity, ChatPage::class.java)
                        intent.putExtra("patient_id",patient_id[position!!])
                        intent.putExtra("patient_name",arr_patient_list.get(position).patient_name)
                        intent.putExtra("patient_photo",arr_patient_list.get(position).image)
                        intent.putExtra("chat_key",arr_patient_list.get(position).chat_key)
                        startActivity(intent)

                    }

                }
            })
        }
        else{

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }
}