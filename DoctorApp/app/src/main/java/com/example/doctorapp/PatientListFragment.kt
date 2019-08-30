package com.example.doctorapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.doctorapp.R.id.lv_current_patients
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_doctor_page.*
import kotlinx.android.synthetic.main.fragment_patients.*
import kotlinx.android.synthetic.main.fragment_patients.view.*
import kotlinx.android.synthetic.main.patient_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.support.v7.widget.LinearLayoutManager
import com.squareup.picasso.Picasso


class PatientListFragment: Fragment() {

    private lateinit var database: DatabaseReference
    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    val KEY_USER_NAME="UserName"
    val KEY_USER_PROFILE="UserProfile"
    lateinit var listener: FragmentActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefences = context!!.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String
        val doctor_name=prefences.getString(KEY_USER_NAME,"İsim Bulunamadı") as String
        val doctor_image=prefences.getString(KEY_USER_PROFILE,"İsim Bulunamadı") as String

        //Toast.makeText(context,"DOKTOR TC:"+tc_kimlik+"Doktor adı:"+doctor_name,Toast.LENGTH_SHORT).show()

        var listview= view?.findViewById(R.id.lv_current_patients) as ListView
        var arr_patient_list:ArrayList<Patients> = ArrayList()
        var patient_id:ArrayList<String> = ArrayList()
        //var inflater = LayoutInflater.from(context)
        //val rootView = inflater.inflate(R.layout.fragment_patients, container, false)

        database = FirebaseDatabase.getInstance().reference
        var createChatMembers = database.child("members")
        var checkChatMembers = database.child("members")
        var getCurrentPatients = database.child("doctors").child(tc_kimlik).child("patientsOfDoctor")
        getCurrentPatients.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    var patient_name=postSnapshot.child("name").value.toString()
                    var visit_date=postSnapshot.child("examineDate").value.toString()
                    var photo=postSnapshot.child("profilPhoto").value.toString()
                    var key=postSnapshot.key.toString()

                    if(visit_date==getDate()){
                        Log.e("Patient Name:",patient_name)
                        Log.e("Doktor Muayenesi:",visit_date)
                        Log.e("Key:",key)
                        patient_id.add(key)

                        //var inflater = LayoutInflater.from(context)
                        //var newpatients = inflater.inflate(R.layout.patient_list, null)
                        //newpatients.tv_name_surname.setText(patient_name)

                        if(photo==""){
                            arr_patient_list.add(Patients(patient_name,"https://firebasestorage.googleapis.com/v0/b/doctorapp-acbca.appspot.com/o/images%2Fuser.png?alt=media&token=6194e048-11ee-4edb-9766-1badcf865e97"))
                        }
                        else{
                            arr_patient_list.add(Patients(patient_name,photo))
                        }

                    }
                }

                listview.adapter=CustomAdaptorPatientListForToday(activity!!.applicationContext,arr_patient_list)

                listview.setOnItemClickListener{
                    parent:AdapterView<*>?,view:View?, position:Int?,id:Long ->


                    checkChatMembers.addValueEventListener(object : ValueEventListener {


                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var isMemberExist=false
                            var chat_key_for_chat_page:String=""
                            for (postSnapshot in dataSnapshot.children) {
                                var doctor_id_in_database=postSnapshot.child("doctor").value.toString()
                                var patient_id_in_database=postSnapshot.child("patient").value.toString()

                                if(tc_kimlik==doctor_id_in_database && patient_id[position!!]==patient_id_in_database){
                                    chat_key_for_chat_page=postSnapshot.key.toString()
                                    isMemberExist=true
                                    break
                                }
                            }
                            var new_id:String
                            if(isMemberExist==false){
                                new_id= createChatMembers.push().key!!
                                var members:ChatMember=ChatMember(tc_kimlik,doctor_name,doctor_image,patient_id[position!!],arr_patient_list.get(position).patient_name,arr_patient_list.get(position).image,1)
                                createChatMembers.child(new_id).setValue(members)

                                val intent = Intent(activity, ChatPage::class.java)
                                intent.putExtra("patient_id",patient_id[position!!])
                                intent.putExtra("patient_name",arr_patient_list.get(position).patient_name)
                                intent.putExtra("patient_photo",arr_patient_list.get(position).image)
                                intent.putExtra("chat_key",new_id)
                                startActivity(intent)

                            }
                            else{
                                val intent = Intent(activity, ChatPage::class.java)
                                intent.putExtra("patient_id",patient_id[position!!])
                                intent.putExtra("patient_name",arr_patient_list.get(position).patient_name)
                                intent.putExtra("patient_photo",arr_patient_list.get(position).image)
                                intent.putExtra("chat_key",chat_key_for_chat_page)
                                startActivity(intent)

                                //Toast.makeText(context,"Hellow"+chat_key_for_chat_page,Toast.LENGTH_SHORT).show()
                            }
                        }
                    })



                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })


        //rootView.lv_current_patients.adapter=adapter_for_patients
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v= inflater.inflate(R.layout.fragment_patients, container, false)
        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if(context is Activity){
            this.listener=context as FragmentActivity
        }
    }

    fun getDate():String{
        var calendar= Calendar.getInstance().time
        var currentTime= SimpleDateFormat("yyyy-MM-dd")
        var formattedDate=currentTime.format(calendar)
        return formattedDate.toString()
    }
}