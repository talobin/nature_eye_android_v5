package dji.sampleV5.modulecommon.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import dji.sampleV5.modulecommon.R
import dji.sampleV5.modulecommon.data.MEDIA_FILE_DETAILS_STR
import dji.sampleV5.modulecommon.models.MediaVM
import dji.sampleV5.modulecommon.util.ToastUtils
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.datacenter.media.DJIList
import dji.v5.manager.datacenter.media.MediaFile
import dji.v5.manager.datacenter.media.MediaFileListState
import dji.v5.manager.datacenter.media.MediaManager
import dji.v5.utils.common.LogUtils
import kotlinx.android.synthetic.main.frag_media_page.*
import java.util.ArrayList

/**
 * @author feel.feng
 * @time 2022/04/19 5:04 下午
 * @description:  回放下载操作界面
 */
class MediaFragment : DJIFragment(){

    private val TAG = LogUtils.getTag(this)
    private val mediaVM: MediaVM by activityViewModels()
    var adapter:MediaListAdapter?= null

    private var isload = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_media_page, container, false);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }


    private fun initData() {
        MediaDataCenter.getInstance().mediaManager.enable(  object :CommonCallbacks.CompletionCallback{
            override fun onSuccess() {
                if (!isload) {
                    mediaVM.init()
                    isload = true
                }
                adapter = MediaListAdapter(mediaVM.mediaFileListData.value?.data!! , context , ::onItemClick )
                media_recycle_list.adapter = adapter
                mediaVM.mediaFileListData.observe(viewLifecycleOwner){
                    adapter!!.notifyDataSetChanged()
                    tv_list_count.text = "Count:${it.data.size}"
                }
            }
            override fun onFailure(error: IDJIError) {
                LogUtils.e(TAG , "enter failed" + error.description());
            }
        })


        mediaVM.fileListState.observe(viewLifecycleOwner) {
            if (it == MediaFileListState.UPDATING) {
                fetch_progress?.visibility = View.VISIBLE
            } else{
                fetch_progress?.visibility = View.GONE
            }

            tv_get_list_state?.setText("State:\n ${it.name}")
        }

    }


    private fun initView() {
        media_recycle_list.layoutManager = GridLayoutManager(context , 3)
        btn_delete.setOnClickListener(){
            var mediafiles = ArrayList<MediaFile>()
            if (adapter?.getSelectedItems()?.size != 0) {
                mediafiles.addAll(adapter?.getSelectedItems()!!)
                MediaDataCenter.getInstance().mediaManager.deleteMediaFiles(mediafiles , object :CommonCallbacks.CompletionCallback {
                    override fun onSuccess() {
                        clearSelectFiles()
                        ToastUtils.showToast("delete success ");
                    }

                    override fun onFailure(error: IDJIError) {
                        ToastUtils.showToast("delete failed  " + error.description());
                    }

                })
            } else {
                ToastUtils.showToast("please select files ");
            }

        }
        btn_refresh_file_list.setOnClickListener(){
           mediaVM.pullMediaFileListFromCamera()
        }

        btn_select.setOnClickListener() {

            adapter?.selectionMode = !adapter?.selectionMode!!
            clearSelectFiles()
            btn_select.setText(
                if (adapter?.selectionMode!!) {
                    R.string.unselect_files
                } else {
                    R.string.select_files
                }
            )
            updateDeleteBtn(adapter?.selectionMode!!)
        }


        sp_choose_component.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                mediaVM.setComponentIndex(ComponentIndexType.find(index))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //do nothing
            }
        }

    }
    private fun updateDeleteBtn(enable: Boolean) {
        btn_delete.isEnabled = enable
    }

    private fun clearSelectFiles() {
        adapter?.mSelectedItems?.clear()
        adapter?.notifyDataSetChanged()
    }

    private fun onItemClick(mediaFile: MediaFile , view: View){

        var imageView =  view.findViewById(R.id.iv_thumbnail) as ImageView
        ViewCompat.setTransitionName(view, mediaFile.fileName );

        val extra = FragmentNavigatorExtras(
            view to "tansitionImage"
        )

        Navigation.findNavController(view).navigate( R.id.media_details_page , bundleOf(
            MEDIA_FILE_DETAILS_STR to mediaFile   ) , null , extra)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        MediaDataCenter.getInstance().mediaManager.stopPullMediaFileListFromCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaDataCenter.getInstance().mediaManager.disable( object :CommonCallbacks.CompletionCallback{
            override fun onSuccess() {
                LogUtils.d(TAG , "exit success");
            }
            override fun onFailure(error: IDJIError) {
                LogUtils.e(TAG , "exit failed " + error.description());
            }

        })
        mediaVM.destroy()
    }


}