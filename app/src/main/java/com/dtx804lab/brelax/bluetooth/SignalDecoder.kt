package com.dtx804lab.brelax.bluetooth

import android.util.Log
import java.util.LinkedList
class SignalDecoder {

    companion object {
        private val HEADER = arrayOf('$', 'G', 'T', 'F')
        private const val TAG = "Signal Decoder"
    }

    enum class DecodeStage {
        MATCHING_HEADER, MATCHING_START, MATCHING_CHECKSUM
    }

    private val buffer = LinkedList<Byte>()
    private var startTime: Int? = null
    private var isPos = false
    private var loopTimes = 0

    fun init() {
        buffer.clear()
        startTime = null
        isPos = false
        loopTimes = 0
    }

    fun signalDecode(data: ByteArray): List<Signal> {
        buffer.addAll(data.asList())
        var stage = DecodeStage.MATCHING_HEADER
        var mark = -1
        var count = 0
        val content = mutableListOf<Byte>()
        val checksum = arrayOf<Byte>(0, 0)
        val result = mutableListOf<Signal>()
        buffer.forEachIndexed { index, byte ->
            when (stage) {
                DecodeStage.MATCHING_HEADER -> {
                    if (byte != HEADER[count].code.toByte()) {
                        count = 0
                        content.clear()
                        return@forEachIndexed
                    }
                    count++
                    if (count > 1) content.add(byte)
                    if (count == 1) mark = index - 1
                    if (count == 4) {
                        count = 0
                        stage = DecodeStage.MATCHING_START
                    }
                }
                DecodeStage.MATCHING_START -> {
                    if (byte == '*'.code.toByte()) stage = DecodeStage.MATCHING_CHECKSUM
                    else content.add(byte)
                }
                DecodeStage.MATCHING_CHECKSUM -> {
                    checksum[count] = byte
                    count++
                    if (count == 2) {
                        if (content.toTypedArray().checksum() == checksum.decodeHexByte()) {
                            result.add(getSignal(content.toTypedArray()))
                        }
                        mark = index
                        count = 0
                        content.clear()
                        checksum[0] = 0
                        checksum[1] = 0
                        stage = DecodeStage.MATCHING_HEADER
                    }
                }
            }
        }
        for (i in 0..mark) buffer.removeFirst()
        Log.d(TAG, result.joinToString(separator = ",") {
            "Signal: ${it.tickTime}-${it.battery}-${it.s0}"
        })
        return result
    }

    private fun Array<Byte>.checksum(): Byte {
        var result = 0
        this.forEach {
            result = result xor it.toInt()
        }
        return result.toByte()
    }

    private fun Byte.numberFromAscii(): Int {
        if (this in 48..57) return this - 48
        if (this in 65..70) return this - 55
        return -1
    }

    private fun Array<Byte>.decodeHexByte(): Byte? {
        if (this.size < 2) return null
        val high = (this[0].numberFromAscii() shl 4) and 0xF0
        val low = this[1].numberFromAscii() and 0xF
        return (high or low).toByte()
    }

    private fun Array<Byte>.decodeHexShort(): Short? {
        if (this.size < 4) return null
        val b1 = (this[0].numberFromAscii() shl 12) and 0xF000
        val b2 = (this[1].numberFromAscii() shl 8) and 0xF00
        val b3 = (this[2].numberFromAscii() shl 4) and 0xF0
        val b4 = this[3].numberFromAscii() and 0xF
        return (b1 or b2 or b3 or b4).toShort()
    }

    private fun Array<Byte>.decodeInt(): Int {
        var result = 0
        this.forEach {
            result = result * 10 + it.numberFromAscii()
        }
        return result
    }

    private fun splitContent(content: Array<Byte>): List<Array<Byte>> {
        val result = mutableListOf<Array<Byte>>()
        val temp = mutableListOf<Byte>()
        content.forEach {
            if (it == ','.code.toByte()) {
                result.add(temp.toTypedArray())
                temp.clear()
            } else temp.add(it)
        }
        result.add(temp.toTypedArray())
        return result.toList()
    }

    private fun getSignal(content: Array<Byte>): Signal {
        with(splitContent(content)) {
            val time = this[1].decodeHexShort()!!.toInt()
            if (startTime == null) startTime = time
            if (isPos && time < 0) loopTimes++
            isPos = time > 0
            return Signal(
                tickTime = time - startTime!! + loopTimes * 65536,
                battery = this[2].decodeInt(),
                s0 = this[3].decodeInt(),
                s1 = this[4].decodeInt(),
                s2 = this[5].decodeInt(),
                s3 = this[6].decodeInt(),
                s4 = this[7].decodeInt(),
                s5 = this[8].decodeInt(),
                s6 = this[9].decodeInt(),
                s7 = this[10].decodeInt()
            )
        }
    }

}

