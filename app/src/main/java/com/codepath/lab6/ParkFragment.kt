package com.codepath.lab6

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "ParkFragment"
private val PARKS_URL = "https://developer.nps.gov/api/v1/parks?api_key=${BuildConfig.API_KEY}"

class ParkFragment : Fragment() {

    private val parks = mutableListOf<Park>()
    private lateinit var parksRecyclerView: RecyclerView
    private lateinit var parksAdapter: ParksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_park, container, false)

        parksRecyclerView = view.findViewById(R.id.park_recycler_view)
        val layoutManager = LinearLayoutManager(context)
        parksRecyclerView.layoutManager = layoutManager
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        parksRecyclerView.addItemDecoration(divider)
        parksRecyclerView.setHasFixedSize(true)
        parksAdapter = ParksAdapter(requireContext(), parks)
        parksRecyclerView.adapter = parksAdapter

        fetchParks()

        return view
    }

    private fun fetchParks() {
        val client = AsyncHttpClient()
        client.get(PARKS_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch parks: $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "Successfully fetched parks: $json")
                try {
                    val parsedJson = createJson().decodeFromString(
                        ParksResponse.serializer(),
                        json.jsonObject.toString()
                    )
                    parsedJson.data?.let { list ->
                        val startPos = parks.size
                        parks.addAll(list)
                        parksAdapter.notifyItemRangeInserted(startPos, list.size)
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Exception: $e")
                }
            }
        })
    }

    companion object {
        fun newInstance(): ParkFragment = ParkFragment()
    }
}


