package com.example.android.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.android.echo.R
import com.example.android.echo.R.id.songTitle
import com.example.android.echo.Songs
import com.example.android.echo.adapters.FavAdapter
import com.example.android.echo.databases.EchoDatabase
import kotlinx.android.synthetic.main.fragment_favourite.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FavouriteFragment : Fragment() {
    var myActivity:Activity?=null
    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int =0
    var favouriteContent :EchoDatabase?=null
    var refreshList:ArrayList<Songs>?=null
    var getListFromDatabase:ArrayList<Songs>?=null

    object Statified{
        var mediaPlayer : MediaPlayer?=null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_favourite, container, false)
        activity?.title= "Favourites"
        favouriteContent = EchoDatabase(myActivity)
        noFavorites = view?.findViewById(R.id.noFavourites)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view.findViewById(R.id.songTitle)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        recyclerView = view.findViewById(R.id.favouriteRecycler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity =context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        display_favourites_by_searching()
        bottomBarSetup()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun getSongsFromPhone(): ArrayList<Songs>? {

        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)

                if(currentArtist!=null)
                  {  arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate)) }
            }

        }else{
            return null
        }
        return arrayList

    }

    fun bottomBarSetup(){
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle )
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle )
                SongPlayingFragment.Staticated.onSongComplete()
            })
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                nowPlayingBottomBar?.visibility = View.VISIBLE
            }else{
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }

        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler(){
          Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
          nowPlayingBottomBar?.setOnClickListener({
              Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
              val songPlayingFragment = SongPlayingFragment()
              var args = Bundle()
              args.putString("songArtist",SongPlayingFragment.Statified.currentSongHelper?.songArtist)
              args.putString("path",SongPlayingFragment.Statified.currentSongHelper?.songPath )
              args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
              args.putInt("songId", SongPlayingFragment.Statified.currentSongHelper?.songId as Int)
              args.putInt("songPosition",SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
              args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
              args.putString("favBottomBar","success")
              songPlayingFragment.arguments = args

              fragmentManager?.beginTransaction()
                      ?.replace(R.id.details_fragment,songPlayingFragment)
                      ?.addToBackStack("SongPlayingFragment")
                      ?.commit()
          })

        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)

            }
        })
    }

    fun display_favourites_by_searching(){
        if (favouriteContent?.checkSize() as Int > 0){
          refreshList= ArrayList<Songs>()
            getListFromDatabase = favouriteContent?.queryDblist()
            var fetchListFromDevice = getSongsFromPhone()
            if (fetchListFromDevice !=null){
                for (i in 0..fetchListFromDevice?.size  - 1){
                    for (j in 0..getListFromDatabase?.size as Int  - 1){
                        if (getListFromDatabase?.get(j)?.songId === fetchListFromDevice?.get(i)?.songId) {
                                     refreshList?.add((getListFromDatabase as ArrayList<Songs>)[j])
                                 }
                    }

                }
            }else{

            }

            if (refreshList==null){
                recyclerView?.visibility =View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            }else{
                var favAdapter = FavAdapter(refreshList as ArrayList<Songs> ,myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favAdapter
                recyclerView?.setHasFixedSize(true)
            }
        }else{
            recyclerView?.visibility =View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }
    }
}
