package com.xpyx.nokiahslvisualisation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.apolloClient.ApolloClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.button)
        val text = findViewById<TextView>(R.id.textView)

        val apolloClient = ApolloClient()

        btn.setOnClickListener{
            apolloClient.apolloClient.query(
                GetAlertsQuery.builder().build()
            ).enqueue(object : ApolloCall.Callback<GetAlertsQuery.Data>() {

                override fun onFailure(e: ApolloException) {
                    Log.d("DBG, on failure", e.localizedMessage ?: "Error")
                }

                override fun onResponse(response: Response<GetAlertsQuery.Data>) {
                    Log.d("DBG, on response", response.data().toString())
                    text.text = response.data().toString()
                }
            })
        }
    }
}