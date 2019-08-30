package com.example.doctorapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_patient_page.*
import android.R.id.edit
import android.content.SharedPreferences
import android.R.id.edit
import android.net.Uri


class PatientPage : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    val KEY_USER_NAME="UserName"
    val IS_DOCTOR="IsDoctor"
    val KEY_USER_PROFILE="UserProfile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_page)
        setSupportActionBar(toolbar_for_patientpage)
        supportActionBar!!.title="Doktor Listesi"

        val prefences = this!!.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String
        val isDoctor=prefences.getString(IS_DOCTOR,"Sonuç Bulunamadı") as String

        var listview= this?.findViewById(R.id.lv_for_doctor_list) as ListView
        var arr_doctor_list:ArrayList<PatientInfo> = ArrayList()
        var doctor_id:ArrayList<String> = ArrayList()

        var doctor_list= ArrayList<String>()
        database = FirebaseDatabase.getInstance().reference
        var chatMembers = database.child("members")
        chatMembers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {



            }

            override fun onDataChange(p0: DataSnapshot) {
                for (p0 in p0.children) {
                    var doctor = p0.child("doctor").value.toString()
                    var patient = p0.child("patient").value.toString()
                    var doctor_name = p0.child("doctor_name").value.toString()
                    var doctor_image = p0.child("doctor_picture").value.toString()
                    var chat_key_for_chat_page=p0.key.toString()
                    doctor_id.add(doctor)

                    if(tc_kimlik==patient){
                        if(doctor_image==""){
                            arr_doctor_list.add(PatientInfo(doctor_name,"https://firebasestorage.googleapis.com/v0/b/doctorapp-acbca.appspot.com/o/images%2Fuser.png?alt=media&token=6194e048-11ee-4edb-9766-1badcf865e97",chat_key_for_chat_page))
                        }
                        else{
                            arr_doctor_list.add(PatientInfo(doctor_name,doctor_image,chat_key_for_chat_page))
                        }
                    }
                }

                listview.adapter=CustomAdaptorPatientList(this@PatientPage!!.applicationContext,arr_doctor_list)

                listview.setOnItemClickListener{
                        parent: AdapterView<*>?, view: View?, position:Int?, id:Long ->

                    val intent = Intent(this@PatientPage, ChatPage::class.java)
                    intent.putExtra("doctor_id",doctor_id[position!!])
                    intent.putExtra("doctor_name",arr_doctor_list.get(position).patient_name)
                    intent.putExtra("doctor_photo",arr_doctor_list.get(position).image)
                    intent.putExtra("chat_key",arr_doctor_list.get(position).chat_key)
                    startActivity(intent)

                }
            }


        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_profil -> {

            val intent = Intent(this, Profile::class.java)
            // start your next activity
            startActivity(intent)
            true
        }
        R.id.item_logout -> {

            val editor = this!!.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE).edit()
            editor.clear().commit()

            val intent = Intent(this, MainActivity::class.java)
            // start your next activity
            startActivity(intent)
            true
        }

        R.id.item_go_mhrs -> {

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse( "https://www.mhrs.gov.tr/Vatandas/" ))
            // start your next activity
            startActivity(intent)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
