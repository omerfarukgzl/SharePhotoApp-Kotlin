package com.example.sharephoto

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharephoto.databinding.ActivitySharePhotoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class SharePhotoActivity : AppCompatActivity() {
    var secilenGorsel : Uri ?=null
    var secilenBitmap: Bitmap ?=null


    // firebase nesnesleri oluşturduk
    private lateinit var storage :FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var recyclerAdapter: PhotoRecyclerAdapter

    private lateinit var binding: ActivitySharePhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_photo)

        // firabase nesnelerini tanımladık
        storage= FirebaseStorage.getInstance()
        auth= FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()

        // view binding işlemi / ekrandakş elemanlara ulaştık
        binding  = ActivitySharePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    // Gorsel Secme İşlemi
    fun imageChooseClick(view: View)
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            // izin zaten yoksa izin al
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1) // request kolarla istek kodları atadık ve izinler sonucunda ne olacak bolumunde kontrol edilecek
        }else
        {
            // izin zaten varsa galeriye git ==> Intent
            val galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2) // request kolarla istek kodları atadık ve izinler sonucunda ne olacak bolumunde kontrol edilecek
        }
    }
    // istenilen izinlerin sonucunda ne olacak
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode==1)// izin kodları kontrolü ==> galeri izni verildiyse
        {
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)// grandResult==> sonuçlar dizisi ( bana bu izin verildimi)
            {
                // izin verilince yapılacaklar
                val galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2) // request kolarla istek kodları atadık ve izinler sonucunda ne olacak bolumunde kontrol edilecek
            }
            else
            {
               //==> izin verilmemişse
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)    // izin almaya git
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // gidilenActivity sonucu ne olacak
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // galeri activty e giderse olacaklar

        if(requestCode==2 && resultCode==Activity.RESULT_OK && data != null ) // istek kodu 2 (yukarıdaki startActivityResult) ,resultcode geriye birşey döndümü ( kullanıcı birşeye basmadan geri tuşunada basabilir), data!=null (geriye sonuc olarak data null donmediyse)
        {
            secilenGorsel=data.data// galeriden secilen gorsel data ile donen data (veriye) eşitledik
            if(secilenGorsel!=null) // secilen gorsel null degilse
            {
                if(Build.VERSION.SDK_INT>28)//get Bitmap 28 SDK altı için çalışmıyor
                {
                    val source= ImageDecoder.createSource(this.contentResolver,secilenGorsel!!) // Image Decoder layıp bitmap haline getirdik
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    binding.imageChoose.setImageBitmap(secilenBitmap)
                }else{
                    secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    binding.imageChoose.setImageBitmap(secilenBitmap)
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }


    // Gorsel Paylasma İşlemi
    @SuppressLint("SuspiciousIndentation")
    fun shareButtonClick(view: View)
    {
        val reference = storage.reference // referans oluşturduk --> gorsel nereye kayedeilecek bu referans ile belirlenir

        // storage.reference depomuzun kendisi
        // storage.reference.child("gorseller") depomuzun içindeki gorseller klasörü
        // storage.reference.child("gorseller").child("gorsel.jpg") depomuzun içindeki gorseller klasörü içindeki gorsel.jpg dosyası


        val gorselIsmi= "${UUID.randomUUID()}.jpg" // rastgele bir isim oluşturduk ---> sürekli aynı isimde oluşturup üzerine kaydetmesin diye random isim oluşturduk
        val gorselReference = reference.child("images").child(gorselIsmi) // gorsel referansı oluşturduk
        if(secilenGorsel!=null)
        gorselReference.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapshot -> // gorsel referansına secilen gorseli yükledik
            // yuklenen birseyin yerini öğrenebilmek için yeniden referans oluşturmamız gerekiyor. taskSnapshot ile yuklenen gorselin referansını alıyoruz.
            val yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)

            yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri -> // yuklenen gorselin referansına downloadUrl ile indirme linkini alıyoruz
                val downloadUrl = uri.toString() // indirme linkini stringe çevirdik

                // veritabanına kayıt işlemi
                val postHashMap = hashMapOf<String,Any>() // veritabanına kayıt yaparken kullanacağımız hashmap
                postHashMap.put("userEmail",auth.currentUser!!.email.toString()) // kullanıcının emailini hashmap e ekledik
                postHashMap.put("downloadUrl",downloadUrl) // indirme linkini hashmap e ekledik
                postHashMap.put("comment",binding.commentText.text.toString()) // kullanıcının yorumunu hashmap e ekledik
                postHashMap.put("date", FieldValue.serverTimestamp()) // kullanıcının yorum tarihini hashmap e ekledik
                database.collection("Posts").add(postHashMap).addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        // gorsel paylasma basarılı
                        Toast.makeText(this,"Yükleme İşlemi Başarılı",Toast.LENGTH_LONG).show()
                        finish()
                    }
                }.addOnFailureListener { exception ->
                    // gorsel paylasma basarısız
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                // indirme linki alınamadı
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }


}