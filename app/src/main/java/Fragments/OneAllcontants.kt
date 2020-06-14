package Fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.android.gms.common.api.Batch
import com.google.android.gms.internal.zzahn.runOnUiThread
import com.google.android.gms.plus.PlusOneDummyView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_one_allcontants.*
import kotlinx.android.synthetic.main.fragment_one_allcontants.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import smartherd.kenyamessagesolution.Activities.MainActivity.Companion.TAG
import smartherd.kenyamessagesolution.Activities.OneContactList
import smartherd.kenyamessagesolution.Activities.OtherFiles
import smartherd.kenyamessagesolution.Adapteres.bring
import smartherd.kenyamessagesolution.Extensions.goToActivity
import smartherd.kenyamessagesolution.Extensions.makeLongToast
import smartherd.kenyamessagesolution.Extensions.showAlertDialog
import smartherd.kenyamessagesolution.R
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat

class OneAllcontants : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewy: View
    private var FILE_SELECT_CODE = 1500
    private lateinit var db: FirebaseFirestore
    private lateinit var databaseHome: DocumentReference
    private lateinit var secondDatabaseHome: DocumentReference
    private lateinit var progressBar: ProgressBar
    var uploadData: ArrayList<String>? = ArrayList()
    var doubleBounce: Sprite = DoubleBounce()
    private var batches: ArrayList<Batch> = ArrayList()
    private lateinit var onethemobilespinner: String
    private lateinit var onethelocationspinner: String
    private lateinit var onethedistrictspinner: String
    private lateinit var onethesublocationspinner: String
    private lateinit var onethevillagespinner: String
    private lateinit var onethecountyspinner: String
    private lateinit var onethesubcountyspinner: String
    private lateinit var onetheemailspinner: String
    private lateinit var onethegenderspinner: String
    private lateinit var onethenamespinner: String
    private lateinit var mHandler: Handler
    private var returnedArray: ArrayList<bring> = ArrayList<bring>()
    private val note: MutableMap<String, Any> = HashMap()
    private var name = "Unassigned"
    private var mobile = 0.0
    private var location = "Unsassigned"
    private var district = "Unassigned"
    private var sublocation = "Unassigned"
    private var village = "Unassigned"
    private var county = "Unassigned"
    private var subcounty = "Unassigned"
    private var email = "Unassigned"
    private var gender = "Unassigned"
    private var theCounter = 0
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewy = inflater.inflate(R.layout.fragment_one_allcontants, container, false)

        initall(viewy)

        return viewy
    }

    private fun initall(viewy: View) {

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        databaseHome = db.collection(auth.currentUser!!.email.toString()).document("Information")
        secondDatabaseHome = db.collection(auth.currentUser!!.email.toString()).document("Keys")
        progressBar = viewy.progress
        progressBar.indeterminateDrawable = doubleBounce

        //IO, Main and Default
        CoroutineScope(Dispatchers.IO).launch {
            initiateTheSpinners(viewy);
        }

        viewy.otherFiles.setOnClickListener {
            val intent = Intent(activity!!, OtherFiles::class.java)
            startActivity(intent)
        }

        viewy.upload.setOnClickListener {
            uploadfileToFirebaes()
        }

        viewy.allcontacts.setOnClickListener {
            activity?.goToActivity(activity!!, OneContactList::class.java)
        }
    }

    private suspend fun initiateTheSpinners(viewy: View) {

        onethemobilespinner = "0"
        onethelocationspinner = "Nil"
        onethedistrictspinner = "Nil"
        onethesublocationspinner = "Nil"
        onethevillagespinner = "Nil"
        onethecountyspinner = "Nil"
        onethesubcountyspinner = "Nil"
        onetheemailspinner = "Nil"
        onethegenderspinner = "Nil"
        onethenamespinner = "Nil"

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            activity?.applicationContext!!,
            R.array.numbers,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        viewy.themobilespinner.setAdapter(adapter)
        viewy.themobilespinner.setOnItemSelectedListener(this);
        viewy.themobilespinner.setAdapter(adapter)
        viewy.themobilespinner.setOnItemSelectedListener(this);
        viewy.thelocationspinner.setAdapter(adapter)
        viewy.thelocationspinner.setOnItemSelectedListener(this);
        viewy.thedistrictspinner.setAdapter(adapter)
        viewy.thedistrictspinner.setOnItemSelectedListener(this);
        viewy.thesublocationspinner.setAdapter(adapter)
        viewy.thesubcountyspinner.setOnItemSelectedListener(this);
        viewy.thevillagespinner.setAdapter(adapter)
        viewy.thevillagespinner.setOnItemSelectedListener(this);
        viewy.thecountyspinner.setAdapter(adapter)
        viewy.thecountyspinner.setOnItemSelectedListener(this);
        viewy.thesubcountyspinner.setAdapter(adapter)
        viewy.thesubcountyspinner.setOnItemSelectedListener(this);
        viewy.theemailspinner.setAdapter(adapter)
        viewy.theemailspinner.setOnItemSelectedListener(this);
        viewy.thegenderspinner.setAdapter(adapter)
        viewy.thegenderspinner.setOnItemSelectedListener(this);
        viewy.thenamespinner.setOnItemSelectedListener(this);
        viewy.thenamespinner.setAdapter(adapter)
        viewy.thenamespinner.setOnItemSelectedListener(this);

    }

    private fun uploadfileToFirebaes() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {

            progress.setVisibility(View.VISIBLE)
            val pickedFile: Uri = data?.data!!
            result.setVisibility(VISIBLE)
            result.text = "Processing Data..."

            //IO, Main and Default
            CoroutineScope(Dispatchers.IO).launch {
                readExcelData(pickedFile)
            }
        }
    }

    private suspend fun readExcelData(filePath: Uri) {
        //decarle input file
        try {
            val inputStream: InputStream =
                getActivity()?.getContentResolver()?.openInputStream(filePath)!!
            try {
                val workbook: XSSFWorkbook = XSSFWorkbook(inputStream)
                val sheet: XSSFSheet = workbook.getSheetAt(0)
                val rowsCount = sheet.getPhysicalNumberOfRows()
                val formulaEvaluator: FormulaEvaluator =
                    workbook.getCreationHelper().createFormulaEvaluator();
                val sb: StringBuilder = StringBuilder();

                //outter loop, loops through rows
                for (r in 1 until rowsCount) {
                    val row: Row = sheet.getRow(r)
                    val cellsCount = row.physicalNumberOfCells
                    //inner loop, loops through columns
                    for (c in 0 until cellsCount) {
                        //handles if there are to many columns on the excel sheet.
                        val value: String = getCellAsString(row, c, formulaEvaluator)
                        val cellInfo = "r:$r; c:$c; v:$value"
                        Log.d(TAG, "readExcelData: Data from row: $cellInfo")
                        sb.append("$value, ")
                    }
                    sb.append(":")
                }
                parseStringBuilder(sb)
                Log.d(TAG, "readExcelData: STRINGBUILDER: " + sb.toString());
                //parseStringBuilder(sb);
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "readExcelData: FileNotFoundException. " + e.message.toString());
                runOnUiThread {
                    activity?.makeLongToast("File not found :" + e.message.toString())
                }
            } catch (e: IOException) {
                runOnUiThread {
                    activity?.makeLongToast("Error reading file : " + e.message.toString())
                }
                Log.e(TAG, "readExcelData: Error reading inputstream. " + e.message.toString());
            }
        } catch (e: Exception) {
            runOnUiThread {
                activity?.makeLongToast("Error reading file : " + e.message.toString())
            }

        }
    }


    /**
     * Returns the cell as a string from the excel file
     * @param row
     * @param c
     * @param formulaEvaluator
     * @return
     */
    private fun getCellAsString(
        row: Row,
        c: Int,
        formulaEvaluator: FormulaEvaluator
    ): String {
        var value = ""
        try {
            val cell = row.getCell(c)
            val cellValue = formulaEvaluator.evaluate(cell)
            when (cellValue?.cellType) {
                Cell.CELL_TYPE_BOOLEAN -> value =
                    "" + cellValue.booleanValue
                Cell.CELL_TYPE_NUMERIC -> {
                    val numericValue = cellValue.numberValue
                    value = if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        val date = cellValue.numberValue
                        val formatter = SimpleDateFormat("MM/dd/yy")
                        formatter.format(HSSFDateUtil.getJavaDate(date))
                    } else {
                        "" + numericValue
                    }
                }
                Cell.CELL_TYPE_STRING -> value =
                    "" + cellValue.stringValue
                else -> {
                }
            }
        } catch (e: NullPointerException) {
            Log.e(TAG, "getCellAsString: NullPointerException: " + e.message)
        }
        return value
    }

    /**
     * Method for parsing imported data and storing in ArrayList<XYValue>
    </XYValue> */
    private fun parseStringBuilder(mStringBuilder: java.lang.StringBuilder) {
        Log.d(PlusOneDummyView.TAG, "parseStringBuilder: Started parsing.")

        // splits the sb into rows.
        val rows = mStringBuilder.toString().split(":").toTypedArray()


        //IO, Main and Default
        CoroutineScope(Dispatchers.IO).launch {
            heavyComputation(rows)
        }

        runOnUiThread(Runnable {
            // Stuff that updates the UI
            progressBar.setVisibility(View.GONE)
            result.setVisibility(GONE)
            activity?.makeLongToast("Completed uploading contacts")
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun heavyComputation(rows: Array<String>) {
        //Add to the ArrayList<XYValue> row by row
        for (index in rows.indices) {

            if (rows[index].split(",").toTypedArray().contains("")) {
                continue
            } else {
                println("\nIndex---$index Element---${rows[index]}")
                //Split the columns of the rows
                val columns = rows[index].split(",").toTypedArray()

                //use try catch to make sure there are no "" that try to parse into doubles.
                try {

                    checkForAddedFiles(columns)
                    heaveyComputation(columns)
                    addToDatabase(index)

                } catch (e: NumberFormatException) {
                    Log.e(
                        PlusOneDummyView.TAG,
                        "parseStringBuilder: NumberFormatException: " + e.message
                    )
                    if (theCounter <= 1) {
                        runOnUiThread {
                            activity?.showAlertDialog("Not all items were added! Error below occured because you either specified a wrong raw number or a wrong type of the data (E.g  numbers or words)\n\n" + e.message.toString())
                        }
                        theCounter++
                    }
                }
            }
        }

    }

    private fun heaveyComputation(columns: Array<String>) {
        if (!onethemobilespinner.equals("0") && !onethemobilespinner.equals("Nil") && !columns[onethemobilespinner.toInt() - 1].isNullOrEmpty()) {
            mobile = columns[onethemobilespinner.toInt() - 1].toDouble()
            val intPart = mobile.toLong()
            note["mobile"] = intPart
        }

        if (!onethelocationspinner.equals("Nil") && !onethelocationspinner.equals("0") && !columns[onethelocationspinner.toInt() - 1].isNullOrEmpty()) {
            location = columns[onethelocationspinner.toInt() - 1]
            note["location"] = location
        }

        if (!onethedistrictspinner.equals("Nil") && !onethedistrictspinner.equals("0") && !columns[onethedistrictspinner.toInt() - 1].isNullOrEmpty()) {
            district = columns[onethedistrictspinner.toInt() - 1]
            note["district"] = district
        }

        if (!onethesublocationspinner.equals("Nil") && !onethesublocationspinner.equals("0") && !columns[onethesublocationspinner.toInt() - 1].isNullOrEmpty()) {
            sublocation = columns[onethesublocationspinner.toInt() - 1]
            note["sublocation"] = sublocation
        }

        if (!onethevillagespinner.equals("Nil") && !onethevillagespinner.equals("0") && !columns[onethevillagespinner.toInt() - 1].isNullOrEmpty()) {
            village = columns[onethevillagespinner.toInt() - 1]
            note["village"] = village
        }

        if (!onethecountyspinner.equals("Nil") && !onethecountyspinner.equals("0") && !columns[onethecountyspinner.toInt() - 1].isNullOrEmpty()) {
            county = columns[onethecountyspinner.toInt() - 1]
            note["county"] = county
        }

        if (!onethesubcountyspinner.equals("Nil") && !onethesubcountyspinner.equals("0") && !columns[onethesubcountyspinner.toInt() - 1].isNullOrEmpty()) {
            subcounty = columns[onethesubcountyspinner.toInt() - 1]
            note["subcounty"] = subcounty
        }
        if (!onetheemailspinner.equals("Nil") && !onetheemailspinner.equals("0") && !columns[onetheemailspinner.toInt() - 1].isNullOrEmpty()) {
            email = columns[onetheemailspinner.toInt() - 1]
            note["email"] = email
        }

        if (!onethegenderspinner.equals("Nil") && !onethegenderspinner.equals("0") && !columns[onethegenderspinner.toInt() - 1].isNullOrEmpty()) {
            gender = columns[onethegenderspinner.toInt() - 1]
            note["gender"] = gender
        }

        if (!onethenamespinner.equals("Nil") && !onethenamespinner.equals("0") && !columns[onethenamespinner.toInt() - 1].isNullOrEmpty()) {
            name = columns[onethenamespinner.toInt() - 1]
            note["name"] = name
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addToDatabase(index: Int) {
        Log.d("Maneno", note.toString())
        databaseHome.collection("People").document(index.toString()).set(note)
            .addOnSuccessListener {
                //activity?.makeLongToast("Successful")
            }.addOnFailureListener {
                //makeLongToast("Error saving company credentials, you will need to update these later.")
            }


        val docData: MutableMap<String, Any> = HashMap()
        val deviceNameList = arrayListOf<String>()

        //Loop through each item in the note and add the note
        for (item in note) {
            deviceNameList.add(item.key.toString())
        }

        docData.put("allkeys", deviceNameList);

        //Now add the valus to the keys
        secondDatabaseHome.set(docData).addOnSuccessListener {
            //activity?.makeLongToast("Successful"
        }.addOnFailureListener {
            //makeLongToast("Error saving company credentials, you will need to update these later.")
        }
    }

    private fun checkForAddedFiles(columns: Array<String>) {

        loadIntentBundles()

        if (!returnedArray.isEmpty()) {
            returnedArray.forEach {
                var nameofarray = it.rowname
                var rownumber = it.rownumber
                var datatype = it.numbersOrletters

                try {
                    if (datatype.toString().equals("Numbers")) {
                        //Data = Double
                        note[nameofarray] = columns[rownumber.toInt() - 1].toDouble()
                    } else if (datatype.toString().equals("Letters")) {
                        //Data  = String
                        note[nameofarray] = columns[rownumber.toInt() - 1]
                    }
                } catch (e: NumberFormatException) {
                    Log.e(TAG, "readExcelData: FileNotFoundException. " + e.message.toString());
                    runOnUiThread {
                        activity?.showAlertDialog("Incomplete! Error below occured because you either specified a wrong raw number or a wrong type of the data (E.g  numbers or words)\n\n" + e.message.toString())
                    }
                } catch (e: NullPointerException) {
                    Log.e(TAG, "readExcelData: FileNotFoundException. " + e.message.toString());
                    runOnUiThread {
                        activity?.showAlertDialog("Incomplete! Error below occured because you either specified a wrong raw number or a wrong type of the data (E.g  numbers or words)\n\n" + e.message.toString())
                    }
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text = parent!!.getItemAtPosition(position).toString()
        if (parent.getId() == R.id.themobilespinner) {
            onethemobilespinner = text
        } else if (parent.getId() == R.id.thelocationspinner) {
            onethelocationspinner = text
        } else if (parent.getId() == R.id.thedistrictspinner) {
            onethedistrictspinner = text
        } else if (parent.getId() == R.id.thesublocationspinner) {
            onethesublocationspinner = text
        } else if (parent.getId() == R.id.thevillagespinner) {
            onethevillagespinner = text
        } else if (parent.getId() == R.id.thecountyspinner) {
            onethecountyspinner = text
        } else if (parent.getId() == R.id.thesubcountyspinner) {
            onethesubcountyspinner = text
        } else if (parent.getId() == R.id.theemailspinner) {
            onetheemailspinner = text
        } else if (parent.getId() == R.id.thegenderspinner) {
            onethegenderspinner = text
        } else if (parent.getId() == R.id.thenamespinner) {
            onethenamespinner = text
        }
    }

    private fun loadIntentBundles() {
        //get the bundle
        if (activity?.getIntent()?.extras != null) {
            val b = activity?.getIntent()?.extras
            @Suppress("UNCHECKED_CAST")
            returnedArray = (b!!.getSerializable("questions") as ArrayList<bring>?)!!
        } else {
            runOnUiThread {
                //activity?.makeLongToast("An internal error occured")
            }
        }
    }
}