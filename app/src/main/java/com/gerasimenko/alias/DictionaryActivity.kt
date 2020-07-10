//package com.gerasimenko.alias
//
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.ImageButton
//import androidx.appcompat.app.AppCompatActivity
//import com.google.gson.Gson
//import kotlinx.android.synthetic.main.activity_dictionary.*
//
//class DictionaryActivity : AppCompatActivity() {
//    private var sharedPreferences: SharedPreferences? = null
//    private var btnEnglish: Button? = null
//    private var btnRussian: Button? = null
//    private var btnEducation: Button? = null
//    private var btnSport: Button? = null
//    private var btnIt: Button? = null
//    private var btnScience: Button? = null
//    private var btnLaw: Button? = null
//    private var btnEconomy: Button? = null
//    private var btnArt: Button? = null
//    private var btnService: Button? = null
//    private var btnMedicine: Button? = null
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.setting_dictionary_layout)
//        btnArt = findViewById(R.id.btn_art)
//        btnSport = findViewById(R.id.btn_sport)
//        btnEducation = findViewById(R.id.btn_education)
//        btnEconomy = findViewById(R.id.btn_economy)
//        btnIt = findViewById(R.id.btn_it)
//        btnScience = findViewById(R.id.btn_science)
//        btnService = findViewById(R.id.btn_service)
//        btnMedicine = findViewById(R.id.btn_medicine)
//        btnLaw = findViewById(R.id.btn_law)
//
//
//    }
//
//    fun onClickEducation(view: View) {
//        val selectedDictionary: String = btnEducation?.text as String
//        val dictionaryName:String = selectedDictionary.replace(" ", "")
//        val editor = sharedPreferences!!.edit()
//        val gson = Gson()
//        editor.apply()
//    }
//    fun saveData(){
//
//
//
//        val editor = sharedPreferences!!.edit()
//        val gson = Gson()
//
//        editor.apply()
//    }
//    fun categoriesDictionary(){
//        when(R.array.categories){
//            btnMedicine -> R.array.MedicineDictionary
//        }
//    }
//}