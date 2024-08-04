package com.example.journal

import android.content.Context
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TagUtils {

    private val defaultTags = listOf("Do", "φ", "“…“", "Book", "Grace")

    fun initializeTagButtons(
        context: Context,
        tagLayout: LinearLayout,
        tags: List<String>,
        onClick: (String, Button) -> Unit
    ) {
        tagLayout.removeAllViews()

        for (tag in tags) {
            val tagButton = Button(context).apply {
                text = tag
                setBackgroundColor(Color.parseColor("#AFAFAF"))
                setTextColor(Color.WHITE)
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                setPadding(0, 0, 0, 0)
                minHeight = 0
                height = LinearLayout.LayoutParams.WRAP_CONTENT
                setOnClickListener { onClick(tag, this) }
            }
            tagLayout.addView(tagButton)
        }
    }

    fun updateTagButtons(tagLayout: LinearLayout, tags: List<String>?) {
        for (i in 0 until tagLayout.childCount) {
            val tagButton = tagLayout.getChildAt(i) as Button
            if (tags != null && tags.contains(tagButton.text.toString())) {
                tagButton.setBackgroundColor(Color.parseColor("#999999"))
            } else {
                tagButton.setBackgroundColor(Color.parseColor("#AFAFAF"))
            }
        }
    }

    fun toggleTag(
        entries: MutableList<EntryEditorActivity.EntryData>,
        currentEntryId: String?,
        tag: String,
        button: Button,
        updateEntriesJson: () -> Unit
    ) {
        val entryData = entries.find { it.created == currentEntryId }
        entryData?.let {
            if (it.tags.contains(tag)) {
                it.tags.remove(tag)
                button.setBackgroundColor(Color.parseColor("#AFAFAF"))
            } else {
                it.tags.add(tag)
                button.setBackgroundColor(Color.parseColor("#999999"))

                if (it.content.isEmpty() && it.modified == null) {
                    EntryDataUtils.updateModifiedTime(it)
                }
            }
            updateEntriesJson()
        }
    }

    fun loadTags(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences("com.example.journal", Context.MODE_PRIVATE)
        val tagsJson = sharedPreferences.getString("tags", null)

        return if (tagsJson.isNullOrEmpty()) {
            defaultTags // Load default tags if no tags are saved
        } else {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(tagsJson, type)
        }
    }

    fun saveTags(context: Context, tags: List<String>) {
        val sharedPreferences = context.getSharedPreferences("com.example.journal", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val tagsJson = Gson().toJson(tags)
        editor.putString("tags", tagsJson)
        editor.apply()
    }
}
