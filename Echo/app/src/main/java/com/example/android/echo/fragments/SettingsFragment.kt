package com.example.android.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import com.example.android.echo.R
import kotlinx.android.synthetic.main.fragment_settings2.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : Fragment() {

     var myActivity:Activity?=null
    var shakeSwitch: Switch?=null

    object Statified{
        var MY_PREFS_NAME = "Shakefeature"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings2, container, false)
        shakeSwitch = view?.findViewById(R.id.switchShake)
        activity?.title = "Settings"

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME ,Context.MODE_PRIVATE)
        val isAllowed = prefs?.getBoolean("feature",false)
        if (isAllowed as Boolean){
            switchShake?.isChecked = true
        }else{
            switchShake?.isChecked = false
        }

        switchShake?.setOnCheckedChangeListener({compoundButton, b->
            if (b){
                val editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME ,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.apply()
            }else{
                val editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME ,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",false)
                editor?.apply()
            }
        })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }


}
