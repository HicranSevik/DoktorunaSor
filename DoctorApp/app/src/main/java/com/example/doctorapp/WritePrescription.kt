package com.example.doctorapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_write_prescription.*
import java.text.SimpleDateFormat
import java.util.*

class WritePrescription : AppCompatActivity() {

    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    var doctor_tc_kimlik=""
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_prescription)

        var intent: Intent =getIntent()
        var patient_id=intent.getStringExtra("patient_id")
        val prefences = this!!.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        doctor_tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String
        database = FirebaseDatabase.getInstance().reference



        btn_write_prescription.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                var phrase=producePrescriptionPhrase()
                Log.e("phrase: ",phrase)

                // Toast.makeText(applicationContext,"Phrase: "+ phrase,Toast.LENGTH_SHORT).show()

                var write_prescription = database.child("prescription")
                write_prescription.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(et_write_prescription.text.trim().length>0) {
                            var prescripton_id = write_prescription.push().key!!
                            write_prescription.child(prescripton_id).child("doctor_id").setValue(doctor_tc_kimlik)
                            write_prescription.child(prescripton_id).child("phrase").setValue(phrase)
                            write_prescription.child(prescripton_id).child("patient_id").setValue(patient_id)
                            write_prescription.child(prescripton_id).child("medicine").setValue(et_write_prescription.text.toString())
                            write_prescription.child(prescripton_id).child("date").setValue(getDate())
                            et_write_prescription.setText("")
                        }


                    }
                })
            }

        })

        var show_prescription = database.child("prescription")
        show_prescription.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(postSnapShot in dataSnapshot.children){
                    var doctor_id_in_database=postSnapShot.child("doctor_id").value.toString()
                    var patient_id_in_database=postSnapShot.child("patient_id").value.toString()
                    var medicines_in_database=postSnapShot.child("medicine").value.toString()
                    if(doctor_id_in_database==doctor_tc_kimlik && patient_id_in_database==patient_id){
                        tv_medicines.setText(medicines_in_database)
                        // Toast.makeText(applicationContext,medicines_in_database,Toast.LENGTH_SHORT).show()
                        Log.e("MEDICINES:",medicines_in_database)
                    }
                }

            }
        })


    }

    private fun producePrescriptionPhrase(): String {
        var phrase=""
        val sb = StringBuilder()
        var alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        for(i in 0 until 6){
            val random = Math.random() * 2

            if(random>1){
                var rand=Math.random() * 26
                Log.e("random: ",rand.toString())
                var character=alphabet.get(rand.toInt())
                Log.e("character: ",character.toString())
                sb.append(character)
                phrase=sb.toString()
            }
            else
            {
                var rand=Math.random() * 9
                sb.append(rand.toInt().toString())
                phrase=sb.toString()
            }

        }

        return phrase
    }

    fun getDate():String{
        var calendar= Calendar.getInstance().time
        var currentTime= SimpleDateFormat("yyyy-MM-dd")
        var formattedDate=currentTime.format(calendar)
        return formattedDate.toString()
    }
}
