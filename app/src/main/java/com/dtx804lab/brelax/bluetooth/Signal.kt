package com.dtx804lab.brelax.bluetooth

data class Signal(
    val tickTime: Int, val battery: Int,
    val s0: Int, val s1: Int, val s2: Int, val s3: Int,
    val s4: Int, val s5: Int, val s6: Int, val s7: Int,
)
