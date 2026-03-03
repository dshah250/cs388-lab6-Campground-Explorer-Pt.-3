package com.codepath.lab6

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import kotlin.let

private const val TAG = "CampgroundFragment"
private const val API_KEY = BuildConfig.API_KEY
private const val CAMPGROUND_URL =
    "https://developer.nps.gov/api/v1/campgrounds?api_key=${API_KEY}"

class CampgroundFragment : Fragment() {

    private val campgrounds = mutableListOf<Campground>()
    private lateinit var campgroundsRecyclerView: RecyclerView
    private lateinit var campgroundAdapter: CampgroundAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_campground, container, false)

        val layoutManager = LinearLayoutManager(context)
        campgroundsRecyclerView = view.findViewById(R.id.campground_recycler_view)
        campgroundsRecyclerView.layoutManager = layoutManager
        campgroundsRecyclerView.setHasFixedSize(true)
        campgroundAdapter = CampgroundAdapter(view.context, campgrounds)
        campgroundsRecyclerView.adapter = campgroundAdapter

        fetchCampgrounds()

        return view
    }

    companion object {
        fun newInstance(): CampgroundFragment {
            return CampgroundFragment()
        }
    }

    private fun fetchCampgrounds() {
        val client = AsyncHttpClient()
        client.get(CAMPGROUND_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch campgrounds: $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "Successfully fetched campgrounds: $json")
                try {
                    val parsedJson = createJson().decodeFromString(
                        CampgroundResponse.serializer(),
                        json.jsonObject.toString()
                    )
                    parsedJson.data?.let { list ->
                        val startPos = campgrounds.size
                        campgrounds.addAll(list)
                        campgroundAdapter.notifyItemRangeInserted(startPos, list.size)
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Exception: $e")
                }
            }
        })
    }
}