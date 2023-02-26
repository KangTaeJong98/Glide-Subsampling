package com.photoview.test.glide

import android.os.FileUtils
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader.LoadData
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.nio.file.Files

class StringFileFetcher(
    private val loadData: LoadData<InputStream>
) : DataFetcher<File> {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in File>) {
        loadData.fetcher.loadData(priority, object : DataFetcher.DataCallback<InputStream> {
            override fun onDataReady(data: InputStream?) {
                callback.onDataReady(
                    data?.let { inputStream ->
                        inputStream.use {
                            File("Hello").also { Files.copy(inputStream, it.toPath()) }
                        }
                    }
                )
            }

            override fun onLoadFailed(e: Exception) {
                callback.onLoadFailed(e)
            }
        })
    }

    override fun cleanup() = loadData.fetcher.cleanup()

    override fun cancel() = loadData.fetcher.cancel()

    override fun getDataClass() = File::class.java

    override fun getDataSource() = DataSource.LOCAL
}