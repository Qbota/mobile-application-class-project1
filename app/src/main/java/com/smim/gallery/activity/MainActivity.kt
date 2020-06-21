package com.smim.gallery.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.smim.gallery.R
import com.smim.gallery.adapter.GalleryImageAdapter
import com.smim.gallery.adapter.GalleryImageClickListener
import com.smim.gallery.adapter.Image
import com.smim.gallery.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), GalleryImageClickListener {

    private val SPAN_COUNT = 4

    private val PERMISSIONS: Array<String> = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE"
    )

    private val imageList = ArrayList<Image>()
    private lateinit var galleryAdapter: GalleryImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askForPermissionsIfNeeded()

        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this

        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

    }


    private fun askForPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS,200)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 200){
            loadImages()
        }
    }

    private fun loadImages() {
        File("/storage/emulated/0").walk().forEach {
            if(
                it.absolutePath.contains("jpg") ||
                it.absolutePath.contains("gif") ||
                it.absolutePath.contains("png") ||
                it.absolutePath.contains("jpeg")
            ){
                imageList.add(Image(it.absolutePath,it.name))
            }
        }
        imageList.reverse()
        galleryAdapter.notifyDataSetChanged()
    }

    override fun onClick(position: Int) {
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", position)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "gallery")
    }
}
