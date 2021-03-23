package com.xpyx.nokiahslvisualisation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.xpyx.nokiahslvisualisation.GetAlertsQuery
import com.xpyx.nokiahslvisualisation.R
import com.xpyx.nokiahslvisualisation.apolloClient.ApolloClient

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val btn = view.findViewById<Button>(R.id.button)
        val text = view.findViewById<TextView>(R.id.textView)
        val apollo = ApolloClient()

        btn.setOnClickListener{
            apollo.client.query(
                GetAlertsQuery.builder().build()
            ).enqueue(object : ApolloCall.Callback<GetAlertsQuery.Data>() {

                override fun onFailure(e: ApolloException) {
                    Log.d("DBG, on failure", e.localizedMessage ?: "Error")
                }

                override fun onResponse(response: Response<GetAlertsQuery.Data>) {
                    Log.d("DBG, on response", response.data.toString())
                    text.text = response.data.toString()
                }
            })
        }
        return view
    }

}