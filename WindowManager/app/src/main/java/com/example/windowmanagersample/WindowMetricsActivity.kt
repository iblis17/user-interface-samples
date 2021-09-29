/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.windowmanagersample

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import androidx.window.layout.WindowMetricsCalculator
import com.example.windowmanagersample.databinding.ActivityWindowMetricsBinding
import com.example.windowmanagersample.infolog.InfoLogAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WindowMetricsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWindowMetricsBinding

    private val adapter = InfoLogAdapter()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowMetricsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView.adapter = adapter
        adapter.append("onCreate", "triggered")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                windowInfoRepository().currentWindowMetrics.collect { windowMetrics ->
                    val width = windowMetrics.bounds.width()
                    val height = windowMetrics.bounds.height()
                    adapter.append("AndroidX Flow", "width: $width, height: $height")
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val windowMetrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
        val width = windowMetrics.bounds.width()
        val height = windowMetrics.bounds.height()
        adapter.append("Config.Change", "width: $width, height: $height")
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }
}
