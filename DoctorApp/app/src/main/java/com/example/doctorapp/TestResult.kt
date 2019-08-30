package com.example.doctorapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_test_result.*

class TestResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result)

        var intent: Intent =getIntent()
        var test_result=intent.getStringExtra("test")
        if(test_result!=""){
            Picasso.get().load(test_result).into(img_test)
        }
        else{
            Toast.makeText(applicationContext,"Sonuçlar yüklenirken bir hata oluştu",Toast.LENGTH_SHORT).show()
        }
        //Picasso.get().load(test_result).into(img_profil)
        //Toast.makeText(applicationContext,"resim:"+test_result,Toast.LENGTH_SHORT).show()
    }
}
