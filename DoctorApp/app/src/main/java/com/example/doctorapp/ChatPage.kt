package com.example.doctorapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_chat_page.*
import android.view.View
import android.widget.ListView
import com.google.firebase.database.*
import android.widget.BaseAdapter








class ChatPage : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    val KEY_USER_NAME="UserName"
    val KEY_USER_PROFILE="UserProfile"
    val IS_DOCTOR="IsDoctor"
    var chat_key:String=""
    var check_doctor=""
    var user_tc_kimlik=""
    var patient_id=""
    var phone_number=""
    var doctor_id=""
    lateinit var adaptor_m:MessageAdapter

    companion object {
        var REQUEST_CALL=1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_page)


        val prefences = this!!.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        user_tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String
        val user_name=prefences.getString(KEY_USER_NAME,"İsim Bulunamadı") as String
        val user_image=prefences.getString(KEY_USER_PROFILE,"İsim Bulunamadı") as String
        val isDoctor=prefences.getString(IS_DOCTOR,"Sonuç Bulunamadı") as String
        var listview= findViewById<ListView>(R.id.rv_for_messages) as ListView
        //var item=menu.findItem(R.id.item_recete_yaz)
        check_doctor=isDoctor
        var arr_message_list:ArrayList<Messages> = ArrayList()

        database = FirebaseDatabase.getInstance().reference

        if(isDoctor=="true"){

            var intent: Intent =getIntent()
            patient_id=intent.getStringExtra("patient_id")
            var patient_name=intent.getStringExtra("patient_name")
            var patient_image=intent.getStringExtra("patient_photo")
            chat_key=intent.getStringExtra("chat_key")
            //Toast.makeText(this,chat_key,Toast.LENGTH_SHORT).show()

            Log.e("patient_id:",patient_id)
            Log.e("patient_name:",patient_name)
            Log.e("patient_image:",patient_image)
            Log.e("doctor_tc_kimlik:",user_tc_kimlik)
            Log.e("doctor_tc_name:",user_name)

            setSupportActionBar(toolbar_for_chat)
            tv_for_toolbar.setText(patient_name)
            supportActionBar!!.title=null
            Picasso.get().load(patient_image).into(img_for_toolbar)

            var patient_number = database.child("patients")
            patient_number.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (postSnapshot in dataSnapshot.children) {
                        var key=postSnapshot.key.toString()
                        var num = postSnapshot.child("phone_number").value.toString()
                        if(key==patient_id){
                            phone_number=num
                            //Toast.makeText(applicationContext,"patient_num"+patient_num,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })




            btn_send.setOnClickListener(object:View.OnClickListener{
                override fun onClick(v: View?) {
                    var add_new_messages = database.child("chats").child(chat_key).child("messages")
                    add_new_messages.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var mes=et_message_send.text.toString()
                            if(mes!=""){
                                var message_id= add_new_messages.push().key!!
                                var new_message=SendMessage(user_tc_kimlik,mes)
                                add_new_messages.child(message_id).setValue(new_message)
                            }
                            et_message_send.setText("")

                        }

                    })
                    adaptor_m.updateList(arr_message_list)
                }

            })



            var add_new_messages = database.child("chats").child(chat_key).child("messages")
            add_new_messages.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (postSnapshot in p0.children) {
                        var message=postSnapshot.child("message").value.toString()
                        var sender=postSnapshot.child("sender").value.toString()
                        arr_message_list.add(Messages(message,sender))

                    }
                    adaptor_m=MessageAdapter(this@ChatPage.applicationContext, arr_message_list)
                    listview.adapter=adaptor_m
                    //adaptor_m.updateList(arr_message_list)

                }
            })

            btn_call.setOnClickListener(object:View.OnClickListener{
                override fun onClick(v: View?) {

                    checkPermission()
                }
            })




        }
        else{
            //Toast.makeText(this,"Yhasta kısmındasın",Toast.LENGTH_SHORT).show()
            var intent: Intent =getIntent()
            doctor_id=intent.getStringExtra("doctor_id")
            var doctor_name=intent.getStringExtra("doctor_name")
            var doctor_photo=intent.getStringExtra("doctor_photo")
            chat_key=intent.getStringExtra("chat_key")

            Toast.makeText(this,"Doktor_id"+doctor_id,Toast.LENGTH_SHORT).show()

            setSupportActionBar(toolbar_for_chat)
            tv_for_toolbar.setText(doctor_name)
            supportActionBar!!.title=null
            Picasso.get().load(doctor_photo).into(img_for_toolbar)

            var doctor_number = database.child("doctors")
            doctor_number.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (postSnapshot in dataSnapshot.children) {
                        var key=postSnapshot.key.toString()
                        var num = postSnapshot.child("phone_number").value.toString()
                        //Toast.makeText(applicationContext,"key"+doctor_id,Toast.LENGTH_SHORT).show()
                        Log.e("Doktor id",doctor_id)
                        Log.e("Key",key)
                        if(key==doctor_id){
                            phone_number=num
                            //Log.e("Doktorun tel No",doctor_num)
                        }
                    }
                }
            })

            btn_call.setOnClickListener(object:View.OnClickListener{
                override fun onClick(v: View?) {
                    checkPermission()
                }
            })


            btn_send.setOnClickListener(object:View.OnClickListener{
                override fun onClick(v: View?) {

                    var add_new_messages = database.child("chats").child(chat_key).child("messages")
                    add_new_messages.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var mes=et_message_send.text.toString()
                            if(mes!=""){
                                var message_id= add_new_messages.push().key!!
                                var new_message=SendMessage(user_tc_kimlik,mes)
                                add_new_messages.child(message_id).setValue(new_message)
                            }
                            et_message_send.setText("")

                        }

                    })
                    adaptor_m.updateList(arr_message_list)
                }

            })

            var arr_message_list:ArrayList<Messages> = ArrayList()
            var add_new_messages = database.child("chats").child(chat_key).child("messages")
            add_new_messages.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    arr_message_list.clear()
                    for (postSnapshot in p0.children) {
                        var message=postSnapshot.child("message").value.toString()
                        var sender=postSnapshot.child("sender").value.toString()

                        arr_message_list.add(Messages(message,sender))

                    }
                    adaptor_m=MessageAdapter(this@ChatPage.applicationContext, arr_message_list)
                    listview.adapter=adaptor_m


                }
            })
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat,menu)
        if(check_doctor=="false"){
            menu!!.findItem(R.id.item_results).setVisible(false)
            menu!!.findItem(R.id.item_recete_yaz).setVisible(false)
            menu!!.findItem(R.id.item_remove_chat).setVisible(false)
        }
        else{

            menu!!.findItem(R.id.item_receteyi_gor).setVisible(false)
        }
        return true
    }

    /*override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        /*if(check_doctor=="false"){
            return false
        }
        else{
            return true
        }*/
    }*/

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_remove_chat -> {

            //val intent = Intent(this, DoctorPage::class.java)
            // start your next activity
            //startActivity(intent)

            var chatMembers = database.child("members")

            chatMembers.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        var doctor=postSnapshot.child("doctor").value.toString()
                        var patient=postSnapshot.child("patient").value.toString()
                        var chat_vis=postSnapshot.child("chat_visibility").value.toString()
                        var key=postSnapshot.key.toString()
                        Log.e("Doktorlar ",doctor)
                        Log.e("Hastalar ",patient)
                        if(doctor==user_tc_kimlik && patient==patient_id && chat_vis.toInt()==1){
                           // Log.e("PATIENT ",patient)
                            database.child("members").child(key).child("chat_visibility").setValue(0)
                            break
                        }
                    }
                }
            })
            val intent = Intent(this, DoctorPage::class.java)
            startActivity(intent)

            true
        }

        R.id.item_results -> {

            val intent = Intent(this, Analysis::class.java)
            intent.putExtra("patient_id",patient_id)
            startActivity(intent)
            true
        }
        R.id.item_recete_yaz -> {

            val intent = Intent(this, WritePrescription::class.java)
            intent.putExtra("patient_id",patient_id)
            startActivity(intent)

            true
        }

        R.id.item_receteyi_gor -> {
            val intent = Intent(this, SeePresscriptionCode::class.java)
            intent.putExtra("doctor_id",doctor_id)
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CALL_PHONE),REQUEST_CALL)

        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            callPhone()
        } else {
            //Toast.makeTest(applicationContext,)
        }
    }

    fun callPhone(){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number))
        startActivity(intent)
    }


}
