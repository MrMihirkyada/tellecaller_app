package com.example.grandin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grandin.Adapter.SheetItemAdapter
import com.example.grandin.databinding.ActivityDashboardDesign2Binding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DashboardDesign2_Activity : AppCompatActivity() {
    lateinit var binding : ActivityDashboardDesign2Binding

    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE = 1
    }


    lateinit var adapter : SheetItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardDesign2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        initview()
    }

    private fun initview()
    {

        binding.call.setOnClickListener {
            showCallingModeDialog()
        }

//        // Sample data (this should match the data from the table)
//        val businessList = listOf(
//            Business("Rakesh", "1234567890", "user1@gmail.com", "Apollo", "www.apollo.com", "Hospital"),
//            Business("Rajesh", "1234567891", "user2@gmail.com", "Minerva", "www.minerva.com", "Hotel"),
//            Business("Sam", "1234567892", "user3@gmail.com", "Star Hospital", "www.starhospital.com", "Hospital"),
//            Business("John", "1234567893", "user4@gmail.com", "Taj Hotel", "www.tajhotel.com", "Hotel"),
//            Business("Lokesh", "1234567894", "user5@gmail.com", "Fitness Hub", "www.fitnesshub.com", "Gym"),
//            Business("Ajay", "1234567895", "user6@gmail.com", "Rainbow School", "www.rainbowschool.com", "School"),
//            Business("Vijay", "1234567896", "user7@gmail.com", "Fortune Grand", "www.fortunegrand.com", "Hotel"),
//            Business("Prakash", "1234567897", "user8@gmail.com", "Capital Way", "www.capitalway.com", "Hotel"),
//            Business("Rahul", "1234567898", "user9@gmail.com", "The Gateway", "www.thegateway.com", "Hotel"),
//            Business("Kiran", "1234567899", "user10@gmail.com", "Tulip Manohar", "www.tulipmanohar.com", "Hotel")
//        )
//
//        // Setting up the adapter with the data
//        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerview.adapter = SheetItemAdapter(requireContext(),businessList)
    }

    private fun showCallingModeDialog()
    {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_auto_diler, null) // Replace with your dialog layout ID
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        val buttonStart = view.findViewById<Button>(R.id.buttonStandardMode)
        val editTextBrakeBetweenCalls = view.findViewById<EditText>(R.id.editTextCallDelay)

        buttonStart.setOnClickListener {
            val timeInput = editTextBrakeBetweenCalls.text.toString()
            if (timeInput.isNotEmpty()) {
                val timeInSeconds = timeInput.toLong() * 1000 // Convert to milliseconds
                bottomSheetDialog.dismiss()

                showCountdownDialog(timeInSeconds)
            } else {
                Toast.makeText(this, "Please enter a time", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCountdownDialog(timeInMillis: Long)
    {
        val countdownDialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
        val countdownView = layoutInflater.inflate(R.layout.layout_countdown_timer, null) // Replace with your countdown layout ID
        countdownDialog.setContentView(countdownView)
        countdownDialog.setCancelable(false)
        countdownDialog.show()

        val textViewCountdown = countdownView.findViewById<TextView>(R.id.textViewCountdown)

        object : CountDownTimer(timeInMillis, 1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                textViewCountdown.text = "$secondsRemaining"
            }

            override fun onFinish()
            {
                countdownDialog.dismiss()
                makePhoneCall("1234567890") // Replace with the actual phone number
            }
        }.start()
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(callIntent)
            showUpdateCallerDialog()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showUpdateCallerDialog() {
        val updateCallerDialog = Dialog(this)
        val updateView = layoutInflater.inflate(R.layout.dialog_update_caller, null) // Replace with your update dialog layout ID
        updateCallerDialog.setContentView(updateView)
        updateCallerDialog.show()

        val phoneNumbers = listOf("1234567890", "1234567891", "1234567892") // Replace with your list of phone numbers
        var currentIndex = 0

        updateView.findViewById<ImageView>(R.id.imgClose).setOnClickListener {
            updateCallerDialog.dismiss()
        }

        updateView.findViewById<ImageView>(R.id.imgNext).setOnClickListener {
            currentIndex = (currentIndex + 1) % phoneNumbers.size
            val nextPhoneNumber = phoneNumbers[currentIndex]
            updateView.findViewById<TextView>(R.id.textViewPhoneNumber).text = nextPhoneNumber
            makePhoneCall(nextPhoneNumber)
        }

        // Optional: Additional code for handling spinners, date pickers, and other fields in updateView
    }

    // Handling permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                makePhoneCall("1234567890") // Replace with actual number or logic to determine the number
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}