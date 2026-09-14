package com.clickbus.challenge.indoor

import android.content.Context
import org.json.JSONObject

object IndoorMapAssetLoader {
    fun loadTieteMap(context: Context): IndoorMapData {
        val json = context.assets.open("tiete-map.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val pointArray = root.getJSONArray("points")
        val connectionArray = root.getJSONArray("connections")

        val points = buildList {
            repeat(pointArray.length()) { index ->
                val point = pointArray.getJSONObject(index)
                add(
                    IndoorMapPoint(
                        id = point.getString("id"),
                        label = point.getString("label"),
                        floor = point.getString("floor"),
                        type = point.getString("type"),
                        xPercent = point.getDouble("x").toFloat(),
                        yPercent = point.getDouble("y").toFloat(),
                    ),
                )
            }
        }

        val connections = buildList {
            repeat(connectionArray.length()) { index ->
                val connection = connectionArray.getJSONObject(index)
                add(
                    IndoorMapConnection(
                        fromId = connection.getString("from"),
                        toId = connection.getString("to"),
                    ),
                )
            }
        }

        return IndoorMapData(points = points, connections = connections)
    }
}
