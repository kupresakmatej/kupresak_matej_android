package com.example.testapp

import android.content.Context
import android.icu.text.IDNA.Info
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.*
import java.io.IOException
import kotlin.math.pow
import kotlin.random.Random


class HomeFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var httpClient: OkHttpClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        httpClient = OkHttpClient()

        getRandomQuote(view)

        setPersonalInformation(view, database, auth)

        setCalculationsForUser(view, database, auth)

        displayLocalTemperature(view, httpClient)

        navigationControler(view)

        logout(view, auth)

        return view;
    }

    private fun logout(view: View, auth: FirebaseAuth) {
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            auth.signOut()

            val introFragment = IntroFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, introFragment)?.commit()
        }
    }

    private fun displayLocalTemperature(view: View, httpClient: OkHttpClient) {
        val localTemperatureTextView = view.findViewById<TextView>(R.id.temperatureLocalTextView)

        val request = Request.Builder()
            .url("https://weather-by-api-ninjas.p.rapidapi.com/v1/weather?city=Zagreb") //pokazuje vrijeme za zagreb trenutno, kasnije se igrat sa lokacijom
            .get()
            .addHeader("X-RapidAPI-Key", "49a9412f2amsha2f611d3d3e5744p1f1acbjsn8c029a017b00")
            .addHeader("X-RapidAPI-Host", "weather-by-api-ninjas.p.rapidapi.com")
            .build()

        httpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    var apiResponse = response.body?.string()

                    val splitResponse = apiResponse?.split(",")?.toTypedArray()
                    val temperatureSplit = splitResponse?.get(1)?.split(":")?.toTypedArray()
                    var temperature = temperatureSplit?.get(1)

                    activity?.runOnUiThread {
                        localTemperatureTextView.text = "Current temperature is: " + temperature.toString()
                    }
                }
            }
        })
    }

    private fun navigationControler(view: View) {
        val videoNavButton = view.findViewById<ImageView>(R.id.videoNavButton)
        val scheduleNavButton = view.findViewById<ImageView>(R.id.calendarNavButton)
        val progressNavButton = view.findViewById<ImageView>(R.id.progressNavButton)
        val infoNavButton = view.findViewById<ImageView>(R.id.infoNavButton)

        videoNavButton.setOnClickListener {
            val videoFragment = VideoFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, videoFragment)?.commit()
        }

        scheduleNavButton.setOnClickListener {
            val scheduleFragment = ScheduleFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, scheduleFragment)?.commit()
        }

        progressNavButton.setOnClickListener {
            val progressFragment = ProgressFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, progressFragment)?.commit()
        }

        infoNavButton.setOnClickListener {
            val infoFragment = InformationFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, infoFragment)?.commit()
        }
    }

    private fun setCalculationsForUser(view: View, database: DatabaseReference, auth: FirebaseAuth) {
        var calculationsText = view.findViewById<TextView>(R.id.calculationsTextView)

        var userID = auth.currentUser?.uid.toString()

        database.child("users").child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val height = snapshot.child("height").getValue(String::class.java).toString()
                val weight = snapshot.child("weight").getValue(String::class.java).toString()

                var heightInt = Integer.parseInt(height)
                var weightInt = Integer.parseInt(weight)

                var heightForBMI = heightInt/100.0

                var bmi = weightInt.toDouble()/(heightForBMI.toDouble().pow(2.0))

                var bmiMessage: String? = null
                if(bmi < 18.5){
                    bmiMessage = "Underweight"
                }
                else if(bmi > 18.5 && bmi < 24.9){
                    bmiMessage = "Normal weight"
                }
                else if(bmi > 24.9 && bmi < 29.9){
                    bmiMessage = "Overweight"
                }
                else if(bmi > 29.9 && bmi < 34.9){
                    bmiMessage = "Obesity"
                }
                else if(bmi > 34.9 && bmi < 40){
                    bmiMessage = "Extreme obesity"
                }

                calculationsText.text = bmiMessage + "\n" + "Your BMI is: " + bmi.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to get data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setPersonalInformation(view: View, database: DatabaseReference, auth: FirebaseAuth) {
        var informationText = view.findViewById<TextView>(R.id.informationTextView)

        var userID = auth.currentUser?.uid.toString()

        database.child("users").child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("fullName").getValue(String::class.java).toString()
                val height = snapshot.child("height").getValue(String::class.java).toString()
                val weight = snapshot.child("weight").getValue(String::class.java).toString()

                informationText.text = "Name: " + name + "\n" + "Height: " + height + "\n" + "Weight: " + weight
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to get data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getRandomQuote(view: View) {
        var quotes = arrayOf("U vježbanju kao i u životu, ne postoji ništa i nitko tko će raditi za vas.",
            "Naše tijelo ovisi isključivo o nama, nikome drugom nije toliko stalo do vašeg uspjeha kao vama samima!",
            "Ne smiješ se vezati za ishod. Umjesto toga uživaj u procesu, rezultat ćeš brže ostvariti što si manje usredotočen na njega.",
            "Jedine granice tvojega života su one koje si sam odrediš.",
            "Trenutak u kojem bi najradije odustao trenutak je u kojem moraš smoći snage za nastavak.",
            "Maleni dnevni pomaci, ako su ustaljeni, vode zapanjujućim rezultatima.",
            "Kako bi dostigao rezultate najboljih 5%, moraš činiti ono što 95% ljudi ne želi.",
            "Svaki dan postaje dramatično bolji ako sadrži tjelovježbu.",
            "Mrzio sam svaku minutu treninga, ali rekao sam sebi:“Nemoj odustati. Pati sada i ostatak života živi kao šampion. Muhammad Ali",
            "Budi promjena koju želiš vidjeti u drugima.",
            "Najvrijednija stvar u životu osim samog života je zdravlje. Ali ljudi uništavaju svoje zdravlje brinući se zbog svih mogućih besmislica.",
            "Ako je tijelo slabo, ni um neće biti snažan."
        )

        val rand = Random(System.currentTimeMillis())
        val n = rand.nextInt(quotes.count() - 1)

        var quoteText = view.findViewById<TextView>(R.id.quoteTextView)

        quoteText.text = quotes.get(n)
    }
}