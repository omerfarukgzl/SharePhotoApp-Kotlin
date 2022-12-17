package com.example.sharephoto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharephoto.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var recyclerAdapter: PhotoRecyclerAdapter


    var postListesi = ArrayList<Post>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        // view binding işlemi / ekrandakş elemanlara ulaştık
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        verileriAl()
        var layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        recyclerAdapter = PhotoRecyclerAdapter(postListesi)
        binding.recyclerView.adapter = recyclerAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.secenekler_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.fotograf_paylas) // secilen item In id si fotograf_paylas 'ın id si ise
        {
            val intent = Intent(this, SharePhotoActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.cikis_yap)// secilen item In id si cikis_yap id si ise
        {
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun verileriAl() {
        // verileri  real time listeleme işlemi
        database.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener{ snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            } else {
                if (snapshot != null && snapshot.isEmpty() == false) {

                    val documents = snapshot.documents
                    for (document in documents) {
                        val kullaniciEmail = document.get("userEmail") as String
                        val kullaniciYorum = document.get("comment") as String
                        val kullaniciGorselUrl = document.get("downloadUrl") as String
                        val kullaniciTarih = document.get("date") as Timestamp

                        val indirilen = Post(kullaniciEmail, kullaniciYorum, kullaniciGorselUrl, kullaniciTarih)
                        postListesi.add(indirilen)
                    }
                    recyclerAdapter.notifyDataSetChanged()

                }
                else{
                        Toast.makeText(applicationContext, "Veri Yok", Toast.LENGTH_LONG)
                        .show()
                }
            }

        }

    }


}



