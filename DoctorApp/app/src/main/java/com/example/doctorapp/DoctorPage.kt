package com.example.doctorapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_doctor_page.*
import java.text.SimpleDateFormat
import java.util.*

class DoctorPage : AppCompatActivity()  {

    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"


    private val mOnNavigationItemSelectedListener=BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.navigation_patient_list -> {
                addFragmentPatients()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_chat-> {
                addFragmentChat()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private  val TAG = "HICRAN"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_page)
        setSupportActionBar(toolbar_for_doctorpage)

        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String

        //Toast.makeText(applicationContext, tc_kimlik, Toast.LENGTH_SHORT).show()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        var calendar= Calendar.getInstance().time
        var currentTime= SimpleDateFormat("yyyy-MM-dd")
        var formattedDate=currentTime.format(calendar)
        //Toast.makeText(applicationContext, currentTime.toString(), Toast.LENGTH_LONG).show();
        Log.e( TAG,"ZAMAN=" + formattedDate);

        addFragmentChat()
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
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }



    private fun addFragmentChat(){
        val fragmentTransaction=supportFragmentManager.beginTransaction()
        val fragment=ChatListFragment()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun addFragmentPatients(){
        val fragmentTransaction=supportFragmentManager.beginTransaction()
        val fragment=PatientListFragment()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
