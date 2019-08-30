package com.example.doctorapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_analysis.*

class Analysis : AppCompatActivity() {

    lateinit var adapter_m:CustomAdaptorForAnalysis
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)
        supportActionBar?.hide()

        var listview= findViewById<ListView>(R.id.lv_analysis)
        database = FirebaseDatabase.getInstance().reference
        var intent: Intent =getIntent()
        var patient_id=intent.getStringExtra("patient_id")
        var arr_test_list:ArrayList<AnalysisDate> = ArrayList()
        var test_image:ArrayList<String> = ArrayList()



        btn_blood_res.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View?) {

                if(arr_test_list.isNotEmpty()){
                    adapter_m.updateList(arr_test_list)
                }
                test_image.clear()

                var get_blood_test_results = database.child("analysis_results").child("blood_analysis_result")
                get_blood_test_results.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for(postSnapshot in dataSnapshot.children){
                            var key=postSnapshot.key.toString()
                            var test_date=postSnapshot.child("analysisDate").value.toString()
                            var test_result=postSnapshot.child("test").value.toString()
                            test_image.add(test_result)
                            Log.e("testdate",test_date)
                            if(key==patient_id){
                                arr_test_list.add(AnalysisDate(test_date))
                                //Toast.makeText(applicationContext,"tarih "+test_date,Toast.LENGTH_SHORT).show()
                            }
                        }
                        adapter_m=CustomAdaptorForAnalysis(this@Analysis!!.applicationContext,arr_test_list)
                        listview.adapter=adapter_m

                        listview.setOnItemClickListener{
                                parent: AdapterView<*>?, view:View?, position:Int?, id:Long ->

                            val intent = Intent(this@Analysis, TestResult::class.java)
                            intent.putExtra("test",test_image[position!!])
                            startActivity(intent)

                        }
                    }

                })


            }


        })


        btn_urine_test.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View?) {
                //Toast.makeText(applicationContext,"btn_urine_test",Toast.LENGTH_SHORT).show()
                if(arr_test_list.isNotEmpty()){
                    adapter_m.updateList(arr_test_list)

                }
                test_image.clear()

                var get_urine_test_results = database.child("analysis_results").child("urine_test_result")
                get_urine_test_results.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for(postSnapshot in dataSnapshot.children){
                            var key=postSnapshot.key.toString()
                            var test_date=postSnapshot.child("analysisDate").value.toString()
                            var test_result=postSnapshot.child("test").value.toString()
                            test_image.add(test_result)
                            Log.e("testdate",test_date)
                            if(key==patient_id){
                                arr_test_list.add(AnalysisDate(test_date))
                                //Toast.makeText(applicationContext,"tarih "+test_date,Toast.LENGTH_SHORT).show()
                            }
                        }
                        adapter_m=CustomAdaptorForAnalysis(this@Analysis!!.applicationContext,arr_test_list)
                        listview.adapter=adapter_m

                        listview.setOnItemClickListener{
                                parent: AdapterView<*>?, view:View?, position:Int?, id:Long ->

                            val intent = Intent(this@Analysis, TestResult::class.java)
                            intent.putExtra("test",test_image[position!!])
                            startActivity(intent)

                        }
                    }

                })

            }


        })

        btn_x_ray_test.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View?) {

                if(arr_test_list.isNotEmpty()){
                    adapter_m.updateList(arr_test_list)

                }
                test_image.clear()
                //Toast.makeText(applicationContext,"btn_x_ray",Toast.LENGTH_SHORT).show()
                var get_x_ray_results = database.child("analysis_results").child("x_ray_analysis_result")
                get_x_ray_results.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for(postSnapshot in dataSnapshot.children){
                            var key=postSnapshot.key.toString()
                            var test_date=postSnapshot.child("analysisDate").value.toString()
                            var test_result=postSnapshot.child("test").value.toString()
                            test_image.add(test_result)
                            Log.e("testdate",test_date)
                            if(key==patient_id){
                                arr_test_list.add(AnalysisDate(test_date))
                                //Toast.makeText(applicationContext,"tarih "+test_date,Toast.LENGTH_SHORT).show()
                                Log.e("Gerçek Tarih",test_date)
                            }
                        }
                        adapter_m=CustomAdaptorForAnalysis(this@Analysis!!.applicationContext,arr_test_list)
                        listview.adapter=adapter_m

                        listview.setOnItemClickListener{
                                parent: AdapterView<*>?, view:View?, position:Int?, id:Long ->

                            val intent = Intent(this@Analysis, TestResult::class.java)
                            intent.putExtra("test",test_image[position!!])
                            startActivity(intent)

                        }
                    }

                })


            }

        })

    }

}
