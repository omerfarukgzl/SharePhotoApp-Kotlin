package com.example.sharephoto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.sharephoto.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance() // singelton gibi çalışıyor

        binding  = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun girisButtonClick(view: View)
    {

    }
    fun kayitButtonClick(view: View)
    {
        var email = binding.editTextTextEmailAddress.text.toString()
        var password = binding.editTextNumberPassword.text.toString()

        // email ve password kullanarak kullanıcı oluştur
        // asenkron listener özelliği mevcuttur . örneğin tamamlandığında hata aldığında dinle başarılı olduğunda vs.
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener{exception->
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show() // if bloğu hata olursa else yerine oddOnFailureListener ile kontrol ettik
            }

    }
}