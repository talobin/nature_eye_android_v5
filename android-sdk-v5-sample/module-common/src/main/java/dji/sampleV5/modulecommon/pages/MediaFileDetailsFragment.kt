package dji.sampleV5.modulecommon.pages

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import dji.sampleV5.modulecommon.R
import dji.sampleV5.modulecommon.data.MEDIA_FILE_DETAILS_STR
import dji.sampleV5.modulecommon.models.MediaDetailsVM
import dji.sampleV5.modulecommon.models.MediaVM
import dji.sampleV5.modulecommon.util.ToastUtils
import dji.sampleV5.modulecommon.util.Util
import dji.sdk.keyvalue.value.camera.MediaFileType
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.media.MediaFile
import dji.v5.manager.datacenter.media.MediaFileDownloadListener
import dji.v5.utils.common.ContextUtil
import dji.v5.utils.common.DiskUtil
import dji.v5.utils.common.LogUtils
import dji.v5.utils.common.StringUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_mediafile_details.*
import kotlinx.android.synthetic.main.layout_media_play_download_progress.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author feel.feng
 * @time 2022/04/25 9:18 下午
 * @description:
 */
class MediaFileDetailsFragment : DJIFragment() ,View.OnClickListener {
    private val mediaDetailsVM: MediaDetailsVM by activityViewModels()
    var mediaFile: MediaFile? = null
    var mediaFileDir  = "/mediafile"
    lateinit var image: ImageView
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.frag_mediafile_details, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTransition()
        initView(view)

    }

    private fun initView(view: View) {
        image = view.findViewById(R.id.image) as ImageView
        mediaFile = arguments?.getSerializable(MEDIA_FILE_DETAILS_STR) as MediaFile
        image.setImageBitmap(mediaFile?.thumbNail)

        image.setOnClickListener(this)
        preview_file.setOnClickListener(this)
        download_file.setOnClickListener(this)
        cancel_download.setOnClickListener(this)
    }


    private fun initTransition(){
        sharedElementEnterTransition = DetailsTransition()
        enterTransition = Fade()
        sharedElementReturnTransition = DetailsTransition()
        exitTransition = Fade()

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaFile?.release()
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.preview_file -> {
              fetchPreview()
            }
            R.id.download_file -> {
                downloadFile()
            }
            R.id.cancel_download -> {
                cancleDownload()
            }

            R.id.image -> {
                if (mediaFile?.fileType == MediaFileType.MP4) {
                    enterVideoPage()
                }
            }

        }
    }

    private fun enterVideoPage() {
        Navigation.findNavController(image).navigate( R.id.video_play_page , bundleOf(
            MEDIA_FILE_DETAILS_STR to mediaFile   ) , )
    }


    fun fetchPreview(){
        mediaFile?.pullPreviewFromCamera(object :CommonCallbacks.CompletionCallbackWithParam<Bitmap>{
            override fun onSuccess(t: Bitmap?) {

                AndroidSchedulers.mainThread().scheduleDirect {
                    //  image.setImageBitmap(t)
                    Glide.with(ContextUtil.getContext()).load(t).into(image)
                }
            }
            override fun onFailure(error: IDJIError) {
                LogUtils.e("MediaFile" , "fetch preview failed$error" )
            }

        })
    }

    fun downloadFile(){
        val dirs: File = File(DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(), mediaFileDir))
        if (!dirs.exists()) {
            dirs.mkdirs()
        }

        val filepath = DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(), mediaFileDir + "/" + mediaFile?.fileName)
        val file: File = File(filepath)
        if (file.exists()) {
            file.delete()
        }
        val outputStream = FileOutputStream(file)
        val bos = BufferedOutputStream(outputStream)
        var beginTime = System.currentTimeMillis()
        mediaFile?.pullOriginalMediaFileFromCamera(0 , object :MediaFileDownloadListener {
            override fun onStart() {
                showProgress()
            }

            override fun onProgress(total: Long, current: Long) {
                updateProgress(current , total)
            }

            override fun onRealtimeDataUpdate(data: ByteArray, position: Long) {
                try {
                    bos.write(data, 0, data.size)
                    bos.flush()
                } catch (e: IOException) {
                    LogUtils.e("MediaFile" , "write error" + e.message)
                }
            }

            override fun onFinish() {
                var spendTime = (System.currentTimeMillis() - beginTime)
                var speedBytePerMill : Float? = mediaFile?.fileSize?.div(spendTime.toFloat())
                var divs = 1000.div(1024 * 1024.toFloat());
                var speedKbPerSecond : Float?= speedBytePerMill?.times(divs)

                ToastUtils.showToast(getString(R.string.msg_download_compelete_tips) + "${speedKbPerSecond }Mbps"
                        + getString(R.string.msg_download_save_tips) + "${filepath}"  )
                hideProgress()
                try {
                    outputStream.close()
                    bos.close()
                } catch (error: IOException) {
                    LogUtils.e("MediaFile" , "close error$error" )
                }
            }

            override fun onFailure(error: IDJIError?) {
                LogUtils.e("MediaFile" , "download error$error" )
            }

        })
    }

    fun cancleDownload(){
        mediaFile?.stopPullOriginalMediaFileFromCamera(object:CommonCallbacks.CompletionCallback{
            override fun onSuccess() {
                hideProgress()
            }

            override fun onFailure(error: IDJIError) {
                hideProgress()

            }

        })
    }


    fun showProgress(){
        progressContainer.setVisibility(View.VISIBLE)

    }

    fun updateProgress(currentsize:Long , total:Long){

        progressBar.setMax(total.toInt())
        progressBar.setProgress(currentsize.toInt())
        val data: Double = StringUtils.formatDouble((currentsize.toDouble() / total.toDouble()))
        val result: String = StringUtils.formatDouble(data * 100, "#0").toString() + "%"
        progressInfo.setText(result)
    }

    fun hideProgress(){
        updateProgress(0 ,0)
        progressContainer.setVisibility(View.GONE)
    }

}