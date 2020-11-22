package org.xapps.apps.weatherx.services.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "places")
data class Place (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = CURRENT_PLACE_ID,

    @ColumnInfo(name = "country")
    var country: String,

    @ColumnInfo(name = "city")
    var city: String,

    @ColumnInfo(name = "latitude")
    var latitude: Double,

    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @ColumnInfo(name = "code")
    var code: String

) {

    companion object {
        const val CURRENT_PLACE_ID = 1L
    }

}