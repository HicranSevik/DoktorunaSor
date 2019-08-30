package com.example.doctorapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class Profile : AppCompatActivity() {

    val PREFS_FILENAME = "com.example.doctorapp"
    val KEY_USER_ID = "TcKimlikNo"
    lateinit var tc_kimlik:String
    val IS_DOCTOR="IsDoctor"
    var profile_url=""

    lateinit var storage:FirebaseStorage
    lateinit var storageReference:StorageReference
    private lateinit var filePath: Uri

    private lateinit var database: DatabaseReference
    companion object {
        const val PICK_IMAGE_REQUEST = 71
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        tc_kimlik=prefences.getString(KEY_USER_ID,"Tc Kimlik No Bulunamadı") as String
        val isDoctor=prefences.getString(IS_DOCTOR,"Sonuç Bulunamadı") as String


        //Toast.makeText(applicationContext, tc_kimlik, Toast.LENGTH_SHORT).show()
        Log.e( "HEYYYYYY","Profil Tc Kimlik=" + tc_kimlik)

        if(isDoctor=="true"){
            Toast.makeText(applicationContext, "Profile Url"+profile_url, Toast.LENGTH_SHORT).show();
            storage= FirebaseStorage.getInstance()
            storageReference=storage.reference


            database = FirebaseDatabase.getInstance().reference
            var name = database.child("doctors").child(tc_kimlik).child("name")

            name.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var user_name=dataSnapshot.value.toString()
                        tv_name_surname.setText(user_name)

                    } else {
                        Toast.makeText(applicationContext, "Kullanıcı bilgileri görüntülenemiyor. ", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })


            var get_profile_image=database.child("doctors").child(tc_kimlik).child("profileImg")
            get_profile_image.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        var user_image=dataSnapshot.value.toString()

                        if(user_image!=""){
                            Picasso.get().load(user_image).into(img_profil)
                        }
                        else{
                            img_profil.setImageResource(R.drawable.user_profile)
                        }


                    } else {
                        // Toast.makeText(applicationContext, "Kullanıcı bilgileri görüntülenemiyor. ", Toast.LENGTH_SHORT).show();

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            btn_profil_choose.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    chooseImage()
                }


            })

            btn_profil_upload.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    uploadImage()
                }


            })
        }
        else{

            Toast.makeText(applicationContext,"URL"+profile_url,Toast.LENGTH_SHORT).show()
            storage= FirebaseStorage.getInstance()
            storageReference=storage.reference


            database = FirebaseDatabase.getInstance().reference
            var name = database.child("patients").child(tc_kimlik).child("name")

            name.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var user_name=dataSnapshot.value.toString()
                        tv_name_surname.setText(user_name)

                    } else {
                        Toast.makeText(applicationContext, "Kullanıcı bilgileri görüntülenemiyor. ", Toast.LENGTH_SHORT).show();

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })


            var get_profile_image=database.child("patients").child(tc_kimlik).child("profileImg")
            get_profile_image.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        var user_image=dataSnapshot.value.toString()

                        if(user_image!=""){
                            Picasso.get().load(user_image).into(img_profil)
                        }
                        else{
                            img_profil.setImageResource(R.drawable.user_profile)
                        }


                    } else {
                        // Toast.makeText(applicationContext, "Kullanıcı bilgileri görüntülenemiyor. ", Toast.LENGTH_SHORT).show();

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            btn_profil_choose.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    chooseImage()
                }


            })

            btn_profil_upload.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    uploadImageForPatient()
                }


            })
        }


    }

    private fun uploadImageForPatient() {
        if(filePath!=null){
            var progressDialog=ProgressDialog(this) as ProgressDialog
            progressDialog.setTitle("Yükleniyor...")
            progressDialog.show()

            var ref: StorageReference? = storageReference.child("images/"+ UUID.randomUUID().toString())
            ref?.putFile(filePath)
                ?.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                        progressDialog.dismiss()
                        Toast.makeText(this@Profile,"Yüklendi",Toast.LENGTH_SHORT).show()
                    }

                })
                ?.addOnFailureListener(object:OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        progressDialog.dismiss()
                        Toast.makeText(this@Profile,"Yüklenemedi",Toast.LENGTH_SHORT).show()
                    }

                })
                ?.addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot>{
                    override fun onProgress(p0: UploadTask.TaskSnapshot?) {
                        var progress=(100.0*p0!!.bytesTransferred/p0.totalByteCount)
                        progressDialog.setMessage("Yüklendi"+progress.toInt()+"%")
                    }
                })


            val urlTask = ref?.putFile(filePath)!!.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    var mUni=downloadUri.toString()

                    profile_url=mUni

                    var save_img_to_database=database.child("patients").child(tc_kimlik)
                    var map:HashMap<String,Any> = HashMap<String,Any>()
                    map.put("profileImg",mUni)
                    save_img_to_database.updateChildren(map)

                    var save_to_member=database.child("members")
                    save_to_member.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (postSnapshot in dataSnapshot.children) {
                                var patient=postSnapshot.child("patient").value.toString()
                                var key=postSnapshot.key.toString()
                                if(patient==tc_kimlik){
                                    //Log.e("işlem", "başarılı")
                                    database.child("members").child(key).child("patient_image").setValue(mUni)
                                }
                            }
                        }
                    })

                } else {
                    // Handle failures
                    // ...
                }
            }


        }    }

    private fun uploadImage() {
        if(filePath!=null){
            var progressDialog=ProgressDialog(this) as ProgressDialog
            progressDialog.setTitle("Yükleniyor...")
            progressDialog.show()

            var ref: StorageReference? = storageReference.child("images/"+ UUID.randomUUID().toString())
            ref?.putFile(filePath)
                ?.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                        progressDialog.dismiss()
                        Toast.makeText(this@Profile,"Yüklendi",Toast.LENGTH_SHORT).show()
                    }

                })
                ?.addOnFailureListener(object:OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        progressDialog.dismiss()
                        Toast.makeText(this@Profile,"Yüklenemedi",Toast.LENGTH_SHORT).show()
                    }

                })
                ?.addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot>{
                    override fun onProgress(p0: UploadTask.TaskSnapshot?) {
                        var progress=(100.0*p0!!.bytesTransferred/p0.totalByteCount)
                        progressDialog.setMessage("Yüklendi"+progress.toInt()+"%")
                    }
                })


            val urlTask = ref?.putFile(filePath)!!.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    var mUni=downloadUri.toString()
                    profile_url=mUni
                    var save_img_to_database=database.child("doctors").child(tc_kimlik)
                    var map:HashMap<String,Any> = HashMap<String,Any>()
                    map.put("profileImg",mUni)
                    save_img_to_database.updateChildren(map)

                    var save_to_member=database.child("members")
                    save_to_member.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (postSnapshot in dataSnapshot.children) {
                                var doctor=postSnapshot.child("doctor").value.toString()
                                var key=postSnapshot.key.toString()
                                if(doctor==tc_kimlik){
                                    //Log.e("işlem", "başarılı")
                                    database.child("members").child(key).child("doctor_picture").setValue(mUni)
                                }
                            }
                        }
                    })

                } else {
                    // Handle failures
                    // ...
                }
            }


        }

    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, Companion.PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== Companion.PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK && data !=null && data.data!=null){
            filePath=data.data
            try{
                var bitmap=MediaStore.Images.Media.getBitmap(contentResolver,filePath) as Bitmap
                img_profil.setImageBitmap(bitmap)
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }


}
