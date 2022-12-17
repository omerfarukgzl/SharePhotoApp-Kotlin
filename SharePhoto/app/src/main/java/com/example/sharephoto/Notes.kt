package com.example.sharephoto

class Notes {

    /*


    -----------------------------    FireBase  ---------------------------------------

    1) FireBase sitesinden bir proje oluşturulur ve bu projeyle android proje birbirine bağlanır.
    2) Uygulama packet name girilir ve indirilen download-service.json doysası uygulamada app klasoru içine koyulur.( gorunumu proje gorunumune gec)
    3) build gradle larda ilgiili bağımlılıklar eklenir


    -- (<project>/build.gradle)
    buildscript {
        dependencies {
            classpath 'com.google.gms:google-services:4.3.14'
        }
    }
    plugins {
        id 'com.android.application' version '7.3.1' apply false
        id 'com.android.library' version '7.3.1' apply false
        id 'org.jetbrains.kotlin.android' version '1.7.20' apply false

    }

    -- (<project>/<app-module>/build.gradle):
    plugins {
        id 'com.android.application'
        id 'org.jetbrains.kotlin.android'
        id 'com.google.gms.google-services'
    }

    dependencies {

        implementation 'androidx.core:core-ktx:1.7.0'
        implementation 'androidx.appcompat:appcompat:1.5.1'
        implementation 'com.google.android.material:material:1.7.0'
        implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
        implementation 'com.google.firebase:firebase-database-ktx:20.1.0'
        implementation 'com.google.firebase:firebase-auth-ktx:21.1.0'
        testImplementation 'junit:junit:4.13.2'
        androidTestImplementation 'androidx.test.ext:junit:1.1.4'
        androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.0'

    }

    Kullanacağımız kütühaneler

    -- Kullanıcı İşleri             ==>Authentication
    -- Veri Tabanı                  ==>Cloud FireStore
    -- Deoplama (Gorsel Video vs)   ==> Cloud Storage


    -- Öncelikle Authentication kullanımı yapalım giriş kayıt

    1)Giriş Activity hazırlayalım.
    Daha sonra firebase de Authentication aktifleştirip ve kullanıcı oluşturuyoruz

    Email ve şifre ile Authentication yapılacak.(Enable Hale getir) manuel kullanıcı ekleyebilirz fakatr kodla yapacağız

    Daha sonra implementationlar yapılır.
    dependencies {
// Import the BoM for the Firebase platform
        implementation platform('com.google.firebase:firebase-bom:31.1.0')

// Add the dependency for the Firebase Authentication library
// When using the BoM, you don't specify versions in Firebase library dependencies
        implementation 'com.google.firebase:firebase-auth-ktx'
    }


    private lateinit var auth: FirebaseAuth ==>

// Initialize Firebase Auth
    auth = Firebase.auth


    public override fun onStart() {
        super.onStart()
// Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }


    ---Yeni Kullanıcılar kaydetme
    Bir e-posta adresini ve parolayı alan, bunları doğrulayan ve ardından createUserWithEmailAndPassword yöntemiyle
    yeni bir kullanıcı oluşturan yeni bir createAccount yöntemi oluşturun.

    auth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "createUserWithEmail:success")
            val user = auth.currentUser
            updateUI(user)
        } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "createUserWithEmail:failure", task.exception)
            Toast.makeText(baseContext, "Authentication failed.",
                Toast.LENGTH_SHORT).show()
            updateUI(null)
        }
    }



    ---Mevcut kullanıcılarda oturum açın
    Bir e-posta adresini ve parolayı alan, bunları doğrulayan ve ardından signIn
    yöntemiyle bir kullanıcının oturumunu açan yeni bir oturum signInWithEmailAndPassword yöntemi oluşturun.


    fun kayitButtonClick(view:View)
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
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show() // if bloğu hata olursa else yerine oddOnFailureListener ile kontrol ettik
            }

    }





    --- Kullanıcı bilgilerine erişin
    Bir kullanıcı başarılı bir şekilde oturum açtıysa, getCurrentUser yöntemiyle hesap verilerini istediğiniz zaman alabilirsiniz.

    val user = Firebase.auth.currentUser
    user?.let {
        // Name, email address, and profile photo Url
        val name = user.displayName
        val email = user.email
        val photoUrl = user.photoUrl

        // Check if user's email is verified
        val emailVerified = user.isEmailVerified

        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getToken() instead.
        val uid = user.uid
    }




    ---- Uygulamada yazdığımız giriş ve kayıtol fonksiyonu

    fun girisButtonClick(view: View)
    {
        var email = binding.editTextTextEmailAddress.text.toString()
        var password = binding.editTextTextPassword.text.toString()

        if(! email.equals("") && !password.equals("") )
        {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                     val girisYapanKullanici = auth.currentUser!!
                        val girisYapanKullaniciMail = girisYapanKullanici.email.toString()
                        Toast.makeText(this,"Merhaba ${girisYapanKullaniciMail}",Toast.LENGTH_LONG).show()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener{exception->
                    Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show() // if bloğu hata olursa else yerine oddOnFailureListener ile kontrol ettik
                }

        }


    }
    fun kayitButtonClick(view: View)
    {
        var email = binding.editTextTextEmailAddress.text.toString()
        var password = binding.editTextTextPassword.text.toString()

        // email ve password kullanarak kullanıcı oluştur
        // asenkron listener özelliği mevcuttur . örneğin tamamlandığında hata aldığında dinle başarılı olduğunda vs.
        if(! email.equals("") && ! password.equals("") )
        {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this,MainActivity::class.java)
                        binding.editTextTextEmailAddress.text=null
                        binding.editTextTextPassword.text = null
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener{exception->
                    Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show() // if bloğu hata olursa else yerine oddOnFailureListener ile kontrol ettik
                }

        }

    }


--- Options Menu

    Uygulamada login sayfasıyla authenticaiton gerçekleştirdik. Kullanıcı kaydettik ve giriş yaptırdık. Giriş yapan kullanıcı yı main activty e yonlendırdık
    Daha sonra bu yonelendirdiğimiz activityde bir menu ve bu menude cıkıs işlemi yapmak istedğimiz item ekliyoruz.
    Bu menu res klasoru altında sağ tıklayarak menu adında yeni klasor ( Directory) oluşturuyoruz
    Daha sonra bu menu ye sağ tıklayarak menu resources file ekle diyoruz
    Gelen xml gorunumunun kodlarında xml kısmına gelerek aşağıdaki kodları yazıyoruz.


    <menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/fotograf_paylas"
        android:title="Fotoğraf Paylaş"
    />
    <item
        android:id="@+id/cikis_yap"
        android:title="Çıkış"
        />
    </menu>

--- Menu ve Activity Bağlama

    Daha sonra bu menuyu activity e bağlamak için activityde iki fonksiyon override edilmeli.

    - onCreateOptionsMenu == > Menu bağlar
    - onOptionsItemSelected ==> Hangi item seçilince ne yapsın

    -- onCreateOptionsMenu

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.secenekler_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    menuInflater ile bağladık


   -- onOptionsItemSelected

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.fotograf_paylas) // secilen item In id si fotograf_paylas 'ın id si ise
        {

            val intent = Intent(this,SharePhotoActivity::class.java)
            startActivity(intent)


        }
        if(item.itemId== R.id.cikis_yap)// secilen item In id si cikis_yap id si ise
        {
            auth.signOut()
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    ==> auth.signOut() ile firebase üzerinden çıkış yaptırdık


Not:// izin kontrolu ====> ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
 ==: Daha önce izin verilmediyse

Not:// izin isteme ====>

-- Share Photo -- Permission
    Daha sonra gorsle paylaşma uygulaması tasarımı yapılır . Bir image view ve tıklanma resmi koyulup imageView e clickable true ve on click functon atanır
    button ve resim tıklanma olayları kontrol edilir
    görsel seçmek için ise izinler kontrol edilmeli ve gorsel verilen izinlere gore asagıdaki kodlarla alınır.( Dangeres Permission)

1) Manifeste istenilen izin yazılır
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>

2)Kullanıcıya sor izin al aşağıdaki kodlar

önce butona tıklandığında daha önce izin verildimi kontrolu yap
   |-- verilmediyse izin aldır
   |
   |  |-- verildiyse direk galeriye gönder
   |  |
   |----->  izin alma sonucu izin verildiyse galeriye ilet
     |         izin alma sonucu izin verilmediyse tekrar izin aldır
     |
     |
     |-----> galeriye gitme sonucu geri dönen data varsa geriye bir ok donduyse
                    secilen gorseli al --> decoderla --> Bitmape çevir --> Bitmapı imageView e ata(setBitmap)





     ------- Gorsel Secimi ------

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


        ------- Gorsel Paylasimi -------

4) secilen gorsel null değilse firebase gonder
    öncelikle firebase de Database ve storage etkin hale getirilir
     storage etkin hale getirilirken depolanacak bulut bolge olarak turkiye için eurp seçilebilir.
        Daha sonra image depolayacğımız için verileri sınıflandırmak adına image klasoru acılır.

    Database olarak fireStore Database kullanılacak. Realtime dan daha gelişiş bir teknolojidir.
    Daha sonra rules kısmından  allow read, write: if request.auth!=null; yazılır.
        Bu kod ile sadece giriş yapmış kullanıcıların veri okuyup yazmasına izin verir.


    Öcelikle fotograf paylaşmak için storage ( depo ) işlemiyle başlanır.
        Storage, Database Ve Authentication işlemleri için nesneler tanımlanır.

    Daha sonra gorsel nereye kayedeilecek bu referans ile belirlenir
    Daha sonra gorsel referansı oluşturulur ve secilen gorsel kaydedilir.
    Resimler Storage içinde image klasörü altında kaydedildi ve bu kaydedilen resimlerin url lerini alıp database e kaydedildi.
    Bu url yanında database'e kaydedilen kullanıcı bilgileri ve yorum bilgisi kaydedildi.


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
                postHashMap.put("downloadUrl",downloadUrl) // indirme linkini hashmap e ekledik
                postHashMap.put("userEmail",auth.currentUser!!.email.toString()) // kullanıcının emailini hashmap e ekledik
                postHashMap.put("comment",binding.editTextComment.text.toString()) // kullanıcının yorumunu hashmap e ekledik
                postHashMap.put("date", FieldValue.serverTimestamp()) // kullanıcının yorumunu hashmap e ekledik

                database.collection("Posts").add(postHashMap).addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        // gorsel paylasma basarılı
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



            Toast.makeText(applicationContext,"Yüklendi",Toast.LENGTH_LONG).show()
        }

    }




   ------ Verileri Listeleme ------

5) Veriler firebase storage dan read ile çekilir.
    Bu read işlemi iki şekilde yapılabilir
            1) Realtime Database --> Verileri sürekli dinler ve değişiklik olduğunda günceller.
            2) FireStore Database --> Verileri tek seferde çeker ve değişiklik olduğunda güncellemek için refresh yapılması gerekir.


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
 // Not:  verileri belli bir filtreleme işlemi yaparak almak için  database.collection("Posts").whereEqualTo() kullanılır

 // Not:  verileri sıralmak için orderBy kullanılır


Daha sonra recycler view row oluşturulur ve bu row içinde veriler gösterilir.
    Layout--> New Layout Resource File --> Layout Resource File --> RecyclerView Row Layout



Not: // resim indirme işlemi için glide kütüphanesi veya picasso kütüphanesi kullanılabilir.
implementation 'com.github.bumptech.glide:glide:4.11.0'
    // glide kütüphanesi ile resim indirme işlemi
    Glide.with(this).load(kullaniciGorselUrl).into(binding.imageView)

    implementation 'com.squareup.picasso:picasso:2.71828'
    // picasso kütüphanesi ile resim indirme işlemi
    Picasso.get().load(kullaniciGorselUrl).into(binding.imageView)



















    -----------------------------    FireBase  ---------------------------------------








    000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000









*/






}


