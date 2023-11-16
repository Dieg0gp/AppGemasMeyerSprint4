package com.example.gemasmeyerapp_ver2.Models

import android.os.Parcel
import android.os.Parcelable

data class ProductoCarrito (val idProducto: Int,val nombre: String?,val cantidad: Int,val precio: Int): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idProducto)
        parcel.writeString(nombre)
        parcel.writeInt(cantidad)
        parcel.writeInt(precio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductoCarrito> {
        override fun createFromParcel(parcel: Parcel): ProductoCarrito {
            return ProductoCarrito(parcel)
        }

        override fun newArray(size: Int): Array<ProductoCarrito?> {
            return arrayOfNulls(size)
        }
    }
}