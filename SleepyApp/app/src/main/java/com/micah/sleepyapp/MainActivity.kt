package com.micah.sleepyapp

import android.media.MediaPlayer
import android.os.Bundle
import android.app.TimePickerDialog
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var BedtimeResults: TextView
    private lateinit var TimeSelected: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private var selectedHour = -1
    private var selectedMinute = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val timePickerBttn = findViewById<Button>(R.id.timePickerBttn)
        val calculatorBttn = findViewById<Button>(R.id.calculatorBttn)
        val infoBttn = findViewById<Button>(R.id.infoBttn)
        TimeSelected = findViewById(R.id.TimeSelected)
        BedtimeResults = findViewById(R.id.BedtimeResults)

        mediaPlayer = MediaPlayer.create(this, R.raw.sleep)
        mediaPlayer.isLooping = true
        mediaPlayer.start()


        timePickerBttn.setOnClickListener {
            showTimePicker()
        }

        calculatorBttn.setOnClickListener {
            if (selectedHour == -1) {
                Toast.makeText(this, "Select a wake-up time first.", Toast.LENGTH_SHORT).show()
            } else {
                showIdealBedtimes(selectedHour, selectedMinute)
            }
        }

        infoBttn.setOnClickListener {
            showInfoDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(this, { _, selectedH, selectedM ->
            selectedHour = selectedH
            selectedMinute = selectedM
            val timeFormatted = String.format("%02d:%02d", selectedH, selectedM)
            TimeSelected.text = timeFormatted
        }, hour, minute, false)

        timePicker.show()
    }

    private fun showIdealBedtimes(wakeHour: Int, wakeMinute: Int) {
        val results = StringBuilder("Ideal Bedtimes:\n\n")

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, wakeHour)
        calendar.set(Calendar.MINUTE, wakeMinute)

        for(i in 6 downTo 3) {
            val Bedtime = calendar.clone() as Calendar
            Bedtime.add(Calendar.MINUTE, -90 * i)

            val hour = Bedtime.get(Calendar.HOUR)
            val minute = Bedtime.get(Calendar.MINUTE)
            val AmPm = if (Bedtime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

            results.append(String.format("%02d:%02d %s\n", hour, minute, AmPm))
        }

        BedtimeResults.text = results.toString()
    }

    private fun showInfoDialog() {
        val dialogview = layoutInflater.inflate(R.layout.infodialog, null)

        val buildDialog = AlertDialog.Builder(this)
            .setView(dialogview)
            .setCancelable(true)

        val dialog = buildDialog.create()
        dialog.show()

        val closeBttn = dialogview.findViewById<Button>(R.id.closeBttn)
        closeBttn.setOnClickListener {
            dialog.dismiss()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}