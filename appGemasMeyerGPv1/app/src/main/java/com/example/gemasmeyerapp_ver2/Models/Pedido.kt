package com.example.gemasmeyerapp_ver2.Models

import java.util.Date

data class Pedido (val idPedido: Int? = null,val idUsuario: String,val idProducto: Int,val estado: Int,val cantidad: Int,val fecha: String)