package com.vfite.football.data.db

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format

/** Stores a [LocalDate] as ISO `yyyy-MM-dd` text, so `WHERE date = :date` matches the API's date strings. */
class LocalDateConverter {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.format(LocalDate.Formats.ISO)

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it, LocalDate.Formats.ISO) }
}
