package com.example.android.echo.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.android.echo.R
import com.example.android.echo.activities.MainActivity
import com.example.android.echo.fragments.SongPlayingFragment

class CaptureBroadcast : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            MainActivity.Statified.notificationManager?.cancel(1978)
           try {
               if( SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                   SongPlayingFragment.Statified.mediaPlayer?.pause()
                   SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
           }

           }catch (e :Exception){
               e.printStackTrace()
           }
        }else{
            val tm :TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    MainActivity.Statified.notificationManager?.cancel(1978)
                    try {
                        if( SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                            SongPlayingFragment.Statified.mediaPlayer?.pause()
                            SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }

                    }catch (e :Exception){
                        e.printStackTrace()
                    }
                }

                else ->{

                }
            }

        }
    }

}