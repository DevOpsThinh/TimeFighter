package com.forever.bee.listtasksmaker.models

import android.os.Parcel
import android.os.Parcelable

class TaskList(val name: String, val tasks: ArrayList<String> = ArrayList()): Parcelable {

    constructor(p0: Parcel) : this(
        p0.readString()!!,
        p0.createStringArrayList()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(name)
        p0.writeStringList(tasks)
    }

    companion object CREATOR: Parcelable.Creator<TaskList> {
        override fun createFromParcel(p0: Parcel): TaskList = TaskList(p0)
        override fun newArray(p0: Int): Array<TaskList?> = arrayOfNulls(p0)
    }

}