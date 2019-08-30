package com.example.doctorapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_see_presscription_code.*
import kotlinx.android.synthetic.main.activity_write_prescription.*

class SeePresscriptionCode : AppCompatActivity() {

    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    var patient_id=""
    lateinit var database:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_presscription_code)


        var intent: Intent =getIntent()
        var doctor_id=intent.getStringExtra("doctor_id")
        val prefences = this!!.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        patient_id=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String
        Log.e("patient_id:",patient_id)
        Log.e("doctor_id:",doctor_id)
        database = FirebaseDatabase.getInstance().reference

        var see_prescription_code = database.child("prescription").limitToLast(1)
        see_prescription_code.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(postSnapShot in dataSnapshot.children){
                    var doctor_id_in_database=postSnapShot.child("doctor_id").value.toString()
                    var patient_id_in_database=postSnapShot.child("patient_id").value.toString()
                    var code=postSnapShot.child("phrase").value.toString()
                    Log.e("code:",code)
                    if(doctor_id_in_database==doctor_id && patient_id_in_database==patient_id){
                        tv_see_medicines.setText(code)
                        // Toast.makeText(applicationContext,medicines_in_database,Toast.LENGTH_SHORT).show()
                        Log.e("MEDICINES:",code)
                    }
                }

            }
        })
    }
}
