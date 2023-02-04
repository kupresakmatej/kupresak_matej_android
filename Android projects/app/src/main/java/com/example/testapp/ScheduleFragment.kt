package com.example.testapp

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.format
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.util.TimeFormatException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Time
import java.text.DateFormat
import java.util.*


class ScheduleFragment : Fragment(), TrainingRecyclerAdapter.ContentListener {
    var datePickerDialog: DatePickerDialog? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var trainingAdapter: TrainingRecyclerAdapter
    var timeForNotificationHour: Int? = null
    var timeForNotificationMinute: Int? = null
    var dateForNotificationYear: Int? = null
    var dateForNotificationMonth: Int? = null
    var dateForNotificationDay: Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        var hour: Int? = null
        var minute: Int? = null

        val dateButton = view.findViewById<Button>(R.id.dateButton)
        val timeButton = view.findViewById<Button>(R.id.timeButton)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        navigationControler(view)

        getDateForTraining(view, dateButton)

        timeButton.text = Calendar.HOUR_OF_DAY.toString() + ":" + Calendar.MINUTE.toString()

        timeButton.setOnClickListener {
            openTimePicker(timeButton)
        }

        displayTrainings(view)

        handleNotifications(view, dateButton, timeButton, database)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNotifications(view: View, dateButton: Button, timeButton: Button, database: DatabaseReference) {
        val scheduleButton = view.findViewById<Button>(R.id.submitTrainingButton)

        createNotificationChannel()

        scheduleButton.setOnClickListener {
            scheduleNotification()
            scheduleTraining(view, dateButton, timeButton, database)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification() {
        val intent = Intent(this.context, Notification::class.java)
        val title = "You scheduled a workout."
        val message = "It's time to work out."
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            this.context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val alarmManager = this.context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(this.context)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(this.context)

        AlertDialog.Builder(this.context)
            .setTitle("Notification scheduled")
            .setMessage(
                title + "\n" + message + "\n" + "At: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(): Long {
        val minute = timeForNotificationMinute
        val hour = timeForNotificationHour

        val day = dateForNotificationDay
        val month = dateForNotificationMonth
        val year = dateForNotificationYear

        val calendar = Calendar.getInstance()
        calendar.set(year!!, month!!, day!!, hour!!, minute!!)

        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notification channel"
        val desc = "Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun displayTrainings(view: View) {
        val trainingRecyclerView = view.findViewById<RecyclerView>(R.id.trainingRecyclerView)

        trainingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TrainingRecyclerAdapter(ArrayList(), this@ScheduleFragment)
        }

        database.child("users/" + auth.currentUser?.uid).child("scheduledTrainings").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: ArrayList<ScheduledTraining> = ArrayList()
                for(snapshot in snapshot.children)
                {
                    var training = ScheduledTraining(snapshot.child("date").getValue().toString(), snapshot.child("time").getValue().toString())

                    list.add(training)
                }

                trainingAdapter = TrainingRecyclerAdapter(list, this@ScheduleFragment)
                trainingRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = trainingAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun scheduleTraining(view: View, dateButton: Button, timeButton: Button, database: DatabaseReference) {
        val trainingToAdd = ScheduledTraining(dateButton.text.toString(), timeButton.text.toString())
        database.child("users/" + auth.currentUser?.uid).child("scheduledTrainings").child(timeButton.text.toString()).setValue(trainingToAdd)
    }

    private fun openTimePicker(button: Button) {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(Calendar.HOUR_OF_DAY)
            .setMinute(Calendar.MINUTE)
            .setTitleText("Set the time for training.")
            .build()
        picker.show(childFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            button.text = picker.hour.toString() + ":" + picker.minute.toString()

            timeForNotificationHour = picker.hour
            timeForNotificationMinute = picker.minute
        }
    }

    private fun getDateForTraining(view: View, dateButton: Button) {
        initDatePicker(dateButton)

        dateButton.setOnClickListener {
            datePickerDialog?.show()
        }

        dateButton.text = getTodaysDate()
    }

    private fun getTodaysDate(): String? {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        var month: Int = cal.get(Calendar.MONTH)
        month = month + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        return makeDateString(day, month, year)
    }

    private fun initDatePicker(dateButton: Button) {
        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                dateButton?.setText(date)
            }
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_DARK
        datePickerDialog =
            this.context?.let { DatePickerDialog(it, style, dateSetListener, year, month, day) }

        dateForNotificationYear = year
        dateForNotificationMonth = month
        dateForNotificationDay = day
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String? {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "JAN"
        if (month == 2) return "FEB"
        if (month == 3) return "MAR"
        if (month == 4) return "APR"
        if (month == 5) return "MAY"
        if (month == 6) return "JUN"
        if (month == 7) return "JUL"
        if (month == 8) return "AUG"
        if (month == 9) return "SEP"
        if (month == 10) return "OCT"
        if (month == 11) return "NOV"
        return if (month == 12) "DEC"
        else "JAN"
    }

    private fun navigationControler(view: View) {
        val homeNavButton = view.findViewById<ImageView>(R.id.homeNavButton)
        val videoNavButton = view.findViewById<ImageView>(R.id.videoNavButton)
        val progressNavButton = view.findViewById<ImageView>(R.id.progressNavButton)
        val infoNavButton = view.findViewById<ImageView>(R.id.infoNavButton)

        homeNavButton.setOnClickListener {
            val homeFragment = HomeFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, homeFragment)?.commit()
        }

        videoNavButton.setOnClickListener {
            val videoFragment = VideoFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, videoFragment)?.commit()
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

    override fun onItemButtonClick(index: Int, training: ScheduledTraining, clickType: ItemClickType) {
        if (clickType == ItemClickType.REMOVE) {
            trainingAdapter.removeItem(index)
            training.date?.let {
                database.child("users/" + auth.currentUser?.uid).child("scheduledTrainings").child(training.time).removeValue()
            }
        }
    }
}