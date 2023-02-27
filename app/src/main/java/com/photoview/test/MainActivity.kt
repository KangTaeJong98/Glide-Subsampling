package com.photoview.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.photoview.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter = ImageAdapter()
    private val snapHelper = PagerSnapHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initAdapter()
        initRecyclerView()
    }

    private fun initAdapter() {
        adapter.submitList(
            listOf(
                "https://t1.daumcdn.net/cfile/tistory/22165B3F55BA279E22",
                "https://img.freepik.com/premium-photo/day-sky-with-clouds-vertical-photo_182793-602.jpg",
                "https://p4.wallpaperbetter.com/wallpaper/798/215/712/vertical-portrait-display-wallpaper-preview.jpg",
                "https://www.theguru.co.kr/data/photos/20210937/art_16316071303022_bf8378.jpg",
                "https://news.nateimg.co.kr/orgImg/pt/2022/06/28/202206281630779508_62bab09b47ba0.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/0/0f/IU_posing_for_Marie_Claire_Korea_March_2022_issue_03.jpg",
                "http://image.genie.co.kr/Y/IMAGE/IMG_ARTIST/067/872/918/67872918_1588900211036_19_600x600.JPG",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/IU_at_Broker_stage_greetings%2C_19_June_2022_01.jpg/1200px-IU_at_Broker_stage_greetings%2C_19_June_2022_01.jpg",
                "https://cdn.ppomppu.co.kr/zboard/data3/2018/0209/20180209101054_wacmmdzw.png"
            )
        )
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = adapter
        snapHelper.attachToRecyclerView(binding.recyclerView)
    }
}