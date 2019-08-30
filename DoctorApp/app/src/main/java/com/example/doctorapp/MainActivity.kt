package com.example.doctorapp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseError
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    val KEY_USER_NAME="UserName"
    val KEY_USER_PROFILE="UserProfile"
    var DOCTOR_IS_CHECKED="IsDoctor"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor = prefences.edit()

        supportActionBar?.hide()

        val btnLogin = findViewById<Button>(R.id.btn_login)
        var etIdentifier = findViewById<EditText>(R.id.et_tckimlik)
        var etPassword = findViewById<EditText>(R.id.et_sifre)
        val isDoctor = findViewById<CheckBox>(R.id.cb_doktorum)

        isDoctor.isChecked = false

        btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(etIdentifier.text) || TextUtils.isEmpty(etPassword.text)) {
                Toast.makeText(applicationContext, "Alanları boş bırakmayınız", Toast.LENGTH_SHORT).show();
            } else if (etIdentifier.text.trim().length != 11) {
                Toast.makeText(applicationContext, "Tc Kimlik Numaranız 11 Hane Olmalıdır.", Toast.LENGTH_SHORT).show();
            } else {
                var identifier=etIdentifier.text.toString().trim()
                var password=etPassword.text.toString().trim()
                if (isDoctor.isChecked == true) {

                    database = FirebaseDatabase.getInstance().reference
                    var isExist=false
                    var checkUserExist = database.child("doctors")
                    checkUserExist.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (postSnapshot in dataSnapshot.children) {
                                var key=postSnapshot.key.toString()
                                var doctor_name=postSnapshot.child("name").value.toString()
                                var doctor_password=postSnapshot.child("password").value.toString()
                                var doctor_image=postSnapshot.child("profileImg").value.toString()
                                //Toast.makeText(applicationContext,isDoctor.isChecked.toString(),Toast.LENGTH_SHORT).show()
                                if((password==doctor_password) && (identifier==key)){
                                    Log.e("KEY",identifier)
                                    Log.e("NAME",doctor_name)
                                    Log.e("DOCTOR PROFILE",doctor_image)
                                    isExist=true

                                    editor.putString(KEY_USER_ID,identifier)
                                    editor.putString(KEY_USER_NAME,doctor_name)
                                    editor.putString(KEY_USER_PROFILE,doctor_image)
                                    editor.putString(this@MainActivity.DOCTOR_IS_CHECKED, isDoctor.isChecked.toString())
                                    editor.commit()

                                    //Toast.makeText(applicationContext,isDoctor.isChecked.toString(),Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@MainActivity, DoctorPage::class.java)
                                    startActivity(intent)
                                    break
                                }
                            }
                            if(isExist==false){
                                Toast.makeText(applicationContext,"Tekrar Deneyin",Toast.LENGTH_SHORT).show()
                            }

                        }
                    })

                } else {

                    database = FirebaseDatabase.getInstance().reference
                    var isExist=false
                    var checkPatientExist = database.child("patients")

                    checkPatientExist.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (postSnapshot in dataSnapshot.children) {
                                var key=postSnapshot.key.toString()
                                var patient_name=postSnapshot.child("name").value.toString()
                                var patient_password=postSnapshot.child("password").value.toString()
                                Log.e("KEY",key)
                                Log.e("NAME",patient_name)

                                if((password==patient_password) && (identifier==key)){
                                    Log.e("KEY",identifier)
                                    Log.e("NAME",patient_name)
                                    isExist=true

                                    editor.putString(KEY_USER_ID,identifier)
                                    editor.putString(KEY_USER_NAME,patient_name)
                                    editor.putString(DOCTOR_IS_CHECKED, isDoctor.isChecked.toString())
                                    editor.commit()
                                    //Toast.makeText(applicationContext,DOCTOR_IS_CHECKED,Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@MainActivity, PatientPage::class.java)
                                    startActivity(intent)
                                    break
                                }
                            }
                            if(isExist==false){
                                Toast.makeText(applicationContext,"Tekrar Deneyin",Toast.LENGTH_SHORT).show()
                            }

                        }
                    })

                }
            }

        }

    }

}
