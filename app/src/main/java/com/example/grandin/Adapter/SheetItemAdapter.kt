package com.example.grandin.Adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.grandin.ApiData
import com.example.grandin.Business
import com.example.grandin.R
import com.example.grandin.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText

import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.io.path.fileVisitor

class SheetItemAdapter(var context: Context,val sheetData: List<Business> // Add this as a parameter to the adapter
 ) : RecyclerView.Adapter<SheetItemAdapter.SheetItemViewHolder>() {



    lateinit var selectedStatus : String
    val REQUEST_CALL_PERMISSION_CODE = 100


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_business_info, parent, false) // Replace with your actual layout file name
        return SheetItemViewHolder(itemView)
    }

        override fun onBindViewHolder(holder: SheetItemViewHolder, position: Int) {
            val currentItem = sheetData[position]

            Log.e("currentItem", "onBindViewHold"+currentItem.name+currentItem.contactNumber+currentItem.emailId+currentItem.company+currentItem.website+currentItem.businessType)
            holder.textViewName.text = currentItem.name
            holder.textViewContactNo.text = currentItem.contactNumber

            holder.textViewContactNo.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_auto_diler, null)
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()

                // Access views in the bottom sheet layout
                val buttonStart = view.findViewById<Button>(R.id.buttonStandardMode)
//                val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
                val editTextBrakeBetweenCalls = view.findViewById<EditText>(R.id.editTextCallDelay)

                buttonStart.setOnClickListener {
                    val timeInput = editTextBrakeBetweenCalls.text.toString()
                    if (timeInput.isNotEmpty())
                    {
                        val timeInSeconds = timeInput.toLong() * 1000 // Convert seconds to milliseconds
                        bottomSheetDialog.dismiss()

                        // Show countdown timer layout
//                        val countdownDialog = Dialog(holder.itemView.context)
//                        val countdownDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
//                        val countdownView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_countdown_timer, null)
//                        countdownDialog.setContentView(countdownView)
//                        countdownDialog.setCancelable(false)
//                        countdownDialog.show()


                        // Create a BottomSheetDialog instance with the custom style
                        val countdownDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
                        val bottomSheetDialog  = LayoutInflater.from(context).inflate(R.layout.layout_countdown_timer, null)
                        countdownDialog.setContentView(bottomSheetDialog)
                        countdownDialog.setCancelable(false)
                        countdownDialog.show()

                        val textViewCountdown = countdownDialog.findViewById<TextView>(R.id.textViewCountdown)

                        // Start the countdown
                        object : CountDownTimer(timeInSeconds, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val secondsRemaining = millisUntilFinished / 1000
                                textViewCountdown!!.text = "$secondsRemaining"
                            }

                            override fun onFinish() {

                                countdownDialog.dismiss() // Dismiss the countdown view after the countdown completes
    //
                                // Attempt to initiate the call
                                if (ActivityCompat.checkSelfPermission(holder.itemView.context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                                        data = Uri.parse("tel:${currentItem.contactNumber}")
                                    }

                                    // Show Update Caller layout as a bottom sheet dialog
                                    val updateCallerDialog = Dialog(holder.itemView.context)
                                    val updateView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_update_caller, null)
                                    updateCallerDialog.setContentView(updateView)
                                    updateCallerDialog.show()

                                     updateView.findViewById<ImageView>(R.id.imgClose).setOnClickListener {
                                       updateCallerDialog.dismiss()
                                    }


                                    // List of phone numbers
                                    val phoneNumbers = listOf("1234567890", "1234567891", "1234567892","1234567893","1234567894","1234567895","1234567896","1234567897","1234567898","1234567899") // Add all numbers here
                                    var currentIndex = 0 // Index to track the current phone number


                                    updateCallerDialog.findViewById<ImageView>(R.id.imgClose).setOnClickListener {
                                        updateCallerDialog.dismiss()
                                    }

                                    updateCallerDialog.findViewById<ImageView>(R.id.imgNext).setOnClickListener {
                                        // Increment the current index to move to the next number
                                        currentIndex = (currentIndex + 1) % phoneNumbers.size

                                        // Get the next phone number
                                        val nextPhoneNumber = phoneNumbers[currentIndex]

                                        updateCallerDialog.findViewById<TextView>(R.id.textViewPhoneNumber).text = nextPhoneNumber
                                        // Call the next number
                                        makePhoneCall(context, nextPhoneNumber)
                                    }


                                    // Access views in the Update Caller layout (optional)
//                                    var spinnerStatus = updateView.findViewById<Spinner>(R.id.spinnerStatus)
//                                    val editTextFollowupDate = updateView.findViewById<EditText>(R.id.editTextFollowupDate)
//                                    val editTextComment = updateView.findViewById<EditText>(R.id.editTextComment)
//                                    val spinnerNotInterestedReason = updateView.findViewById<Spinner>(R.id.spinnerNotInterestedReason)


                                    //Spinner
                                    // Define the list of statuses
//                                    val statusList = arrayOf("Assigned", "Follow-Up Scheduled", "Interested", "Not Interested")
//
//                                    // Create an ArrayAdapter using the string array and a default spinner layout
//                                    val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, statusList)
//
//                                    // Specify the layout to use when the list of choices appears
//                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                                    // Apply the adapter to the spinner
//                                    spinnerStatus.adapter = adapter


//                                    holder.textStatus.visibility = View.GONE
//                                    spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                                        @SuppressLint("SetTextI18n")
//                                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//
//                                            selectedStatus = statusList[position]
//                                            // Show or hide the EditText based on the selected status
//                                            if (selectedStatus == "Follow-Up Scheduled")
//                                            {
//                                                editTextFollowupDate.visibility = View.VISIBLE // Show the EditText
//                                            }
//                                            else
//                                            {
//                                                editTextFollowupDate.visibility = View.GONE // Hide the EditText
//                                            }
//                                            holder.textStatus.text = selectedStatus
//                                            holder.textStatus.visibility = View.VISIBLE
//                                        }
//
//                                        override fun onNothingSelected(parent: AdapterView<*>)
//                                        {
//                                            // Optionally handle case when nothing is selected
//                                        }
//                                    }

                                    //Date and Time
//                                    var dateandTime = editTextFollowupDate.setOnClickListener {
//                                        // Variable to hold the selected date and time in one
//                                         var selectedDateTime: String? = null
//
//// Date and Time Picker
//                                        var dateandTime = editTextFollowupDate.setOnClickListener {
//                                            val c = Calendar.getInstance()
//
//                                            // Get the current day, month, and year
//                                            val year = c.get(Calendar.YEAR)
//                                            val month = c.get(Calendar.MONTH)
//                                            val day = c.get(Calendar.DAY_OF_MONTH)
//
//                                            // Create the DatePickerDialog
//                                            val datePickerDialog = DatePickerDialog(
//                                                holder.itemView.context,
//                                                { view, year, monthOfYear, dayOfMonth ->
//                                                    // Set the selected date
//                                                    val selectedDate = Calendar.getInstance()
//                                                    selectedDate.set(year, monthOfYear, dayOfMonth)
//
//                                                    // Now show the TimePickerDialog after the date is picked
//                                                    val hour = c.get(Calendar.HOUR_OF_DAY)
//                                                    val minute = c.get(Calendar.MINUTE)
//
//                                                    val timePickerDialog = TimePickerDialog(
//                                                        holder.itemView.context,
//                                                        { _, selectedHour, selectedMinute ->
//                                                            // Combine the selected date and time
//                                                            selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour)
//                                                            selectedDate.set(Calendar.MINUTE, selectedMinute)
//
//                                                            // Format the date and time to a readable format
//                                                            selectedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(selectedDate.time)
//
//                                                            // Set the formatted date and time to the EditText
//                                                            editTextFollowupDate.setText(selectedDateTime)
//                                                        },
//                                                        hour,
//                                                        minute,
//                                                        true // true for 24-hour format, false for AM/PM format
//                                                    )
//                                                    // Show the TimePickerDialog
//                                                    timePickerDialog.show()
//                                                },
//                                                year,
//                                                month,
//                                                day
//                                            )
//                                            // Show the DatePickerDialog
//                                            datePickerDialog.show()
//                                        }
//
//                                    }

//                                    Log.e("DATEANDTIME", "onFinish: "+dateandTime)
//
//                                    val buttonUpdate = updateView.findViewById<Button>(R.id.buttonUpdate)
//                                    val buttonCancelUpdate = updateView.findViewById<Button>(R.id.buttonCancel)

//                                    buttonUpdate.setOnClickListener {
//
//                                        val followUpDate = editTextFollowupDate.text.toString()
//                                        val comment = editTextComment.text.toString()
//                                        val status = spinnerStatus.selectedItem.toString()
//                                        val notInterestedReason = spinnerNotInterestedReason.selectedItem?.toString()
//
//                                        // Call your function to make the API call here
////                                        updateCallerData(currentItem.slNo, followUpDate, comment, status, notInterestedReason)
//
//                                        val name = currentItem.name
//                                        val dateAndTime = dateandTime
//                                        val assigned = selectedStatus
//                                        val scheduled = selectedStatus
//                                        val interested = selectedStatus
//                                        val notInterested = selectedStatus
//
//                                        val apiData = ApiData(name!!, dateAndTime.toString(), comment, assigned, scheduled, interested, notInterested)
//                                        updateApiData(apiData)
//
//
//                                        Log.e("PutData", "onFinish: "+apiData)
//
//                                        updateCallerDialog.dismiss()
//                                    }


//                                    buttonCancelUpdate.setOnClickListener {
//                                        updateCallerDialog.dismiss()
//                                    }
                                    holder.itemView.context.startActivity(callIntent)
                                } else {
                                    ActivityCompat.requestPermissions(
                                        (holder.itemView.context as Activity),
                                        arrayOf(Manifest.permission.CALL_PHONE),
                                        CALL_PERMISSION_REQUEST_CODE
                                    )
                                }
                            }
                        }.start()
                    } else {
                        Toast.makeText(holder.itemView.context, "Please enter a time", Toast.LENGTH_SHORT).show()
                    }
                }

//                buttonCancel.setOnClickListener {
//                    bottomSheetDialog.dismiss()
//                }
            }

            holder.textViewEmailId.text = currentItem.emailId
            holder.textViewCompanyName.text = currentItem.company
            holder.textViewWebsite.text = currentItem.website
            holder.textViewBusinessType.text = currentItem.businessType
        }


    private fun updateApiData(apiData: ApiData) {
//        val call = RetrofitClient.instance.getupdateData(apiData)
//        call.enqueue(object : retrofit2.Callback<ApiData> {
//            override fun onResponse(call: Call<ApiData>, response: retrofit2.Response<ApiData>) {
//                if (response.isSuccessful) {
                    Toast.makeText(context, "Data updated successfully!", Toast.LENGTH_SHORT).show()
//                } else {
//                    val errorBody = response.errorBody()!!.string()
//                    Log.e("Update failed:", "Code: ${response.code()} - Message: $errorBody")
//                    Toast.makeText(context, "Update failed: $errorBody", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//
//            override fun onFailure(call: Call<ApiData>, t: Throwable) {
//                // Handle failure
//                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    // Function to initiate the phone call
    private fun makePhoneCall(context: Context, phoneNumber: String) {


        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(callIntent)
        } else {
            // Request CALL_PHONE permission if not granted
            if (context is Activity) {
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION_CODE)
            } else {
                Toast.makeText(context, "Call permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return sheetData.size
    }

    class SheetItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        val textViewContactNo: TextView = itemView.findViewById(R.id.textViewContactNo)
        val textViewEmailId: TextView = itemView.findViewById(R.id.textViewEmailId)
        val textViewCompanyName: TextView = itemView.findViewById(R.id.textViewCompanyName)
        val textViewWebsite: TextView = itemView.findViewById(R.id.textViewWebsite)
        val textViewBusinessType: TextView = itemView.findViewById(R.id.textViewBusinessType)
    }


    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE = 100
    }

}