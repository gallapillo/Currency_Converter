package com.gallapillo.currencyconverter

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gallapillo.currencyconverter.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var mArrayList = arrayListOf<String>("")
    private var mConvertFromValue : String = "USD"
    private var mConvertToValue : String = "EUR"
    private var mConversionValue : String = ""
    private var mCountry = arrayOf("USD", "EUR", "ALL","DZD","AZN", "XCD", "AOA","AUD","BZD", "XOF", "BYN", "BTN", "BSD", "PHP")
    private lateinit var fromDialog: Dialog
    private lateinit var toDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        for (country in mCountry) {
            mArrayList.add(country)
        }
        initFromDialog()
        initToDialog()

        mBinding.button.setOnClickListener {
            try {
                val amountToConvert = mBinding.edFrom.text.toString().toDouble()
                getConversationRate(mConvertFromValue, mConvertToValue, amountToConvert)
            } catch (e: Exception) {

            }
        }
    }

    private fun getConversationRate(ConvertFrom: String, ConvertTo: String, amountToConvert: Double) : String {
        val queue = Volley.newRequestQueue(this)
        val url  = "https://free.currconv.com/api/v7/convert?q=" + ConvertFrom + "_" + ConvertTo + "&compact=ultra&apiKey=ebbd0601460a86769319"
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                var jsonObject : JSONObject? = null
                try {
                    jsonObject = JSONObject(response)
                    val conversionRateValue = round(jsonObject.get(ConvertFrom + "_" + ConvertTo).toString().toDouble(), 2)
                    mConversionValue = "" + round(conversionRateValue * amountToConvert, 2)
                    mBinding.edTo.text = mConversionValue
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, {

        })
        queue.add(stringRequest)
        return mConversionValue
    }

    fun round(value: Double, places: Int) : Double {
        if (places < 0) throw IllegalArgumentException()
        var bd = BigDecimal.valueOf(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    private fun initFromDialog() {
        mBinding.tvFromChangeCurrency.setOnClickListener {
            fromDialog = Dialog(this)
            fromDialog.setContentView(R.layout.from_spinner)
            fromDialog.window?.setLayout(650, 800)
            fromDialog.show()

            val editText = fromDialog.findViewById<EditText>(R.id.edit_text)
            val listView = fromDialog.findViewById<ListView>(R.id.list_view)

            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList)
            listView.adapter = adapter

            editText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            listView.setOnItemClickListener { parent, view, position, id ->
                mBinding.tvFromChangeCurrency.text = adapter.getItem(position)
                fromDialog.dismiss()
                mConvertFromValue = adapter.getItem(position).toString()
            }
        }
    }

    private fun initToDialog() {
        mBinding.tvToChangeCurrency.setOnClickListener {
            toDialog = Dialog(this)
            toDialog.setContentView(R.layout.to_spinner)
            toDialog.window?.setLayout(650, 800)
            toDialog.show()

            val editText = toDialog.findViewById<EditText>(R.id.edit_text)
            val listView = toDialog.findViewById<ListView>(R.id.list_view)

            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList)
            listView.adapter = adapter

            editText.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
            listView.setOnItemClickListener { parent, view, position, id ->
                mBinding.tvToChangeCurrency.text = adapter.getItem(position)
                toDialog.dismiss()
                mConvertToValue = adapter.getItem(position).toString()
            }
        }
    }
}