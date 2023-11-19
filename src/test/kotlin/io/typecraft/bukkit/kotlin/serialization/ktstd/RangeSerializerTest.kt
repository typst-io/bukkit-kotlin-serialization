package io.typecraft.bukkit.kotlin.serialization.ktstd


import io.typecraft.bukkit.kotlin.serialization.decodeFromString
import io.typecraft.bukkit.kotlin.serialization.encodeToString
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RangeSerializerTest {
    @Test
    fun `int range serializer`(): Unit = IntRangeSerializer.run {
        // encode
        assertEquals("[${Int.MIN_VALUE}, ${Int.MAX_VALUE}]", encodeToString(Int.MIN_VALUE..Int.MAX_VALUE))
        // decode
        assertEquals(Int.MIN_VALUE..Int.MAX_VALUE, decodeFromString("[${Int.MIN_VALUE},${Int.MAX_VALUE}]"))
        assertEquals(IntRange.EMPTY, decodeFromString(""))
        assertEquals(IntRange.EMPTY, decodeFromString("[a"))
        assertEquals(IntRange.EMPTY, decodeFromString("[1"))
        assertEquals(IntRange.EMPTY, decodeFromString("[1,"))
        assertEquals(IntRange.EMPTY, decodeFromString("[1,a"))
        assertEquals(IntRange.EMPTY, decodeFromString("[1,2"))
    }

    @Test
    fun `long range serializer`(): Unit = LongRangeSerializer.run {
        // encode
        assertEquals("[${Long.MIN_VALUE}, ${Long.MAX_VALUE}]", encodeToString(Long.MIN_VALUE..Long.MAX_VALUE))
        // decode
        assertEquals(Long.MIN_VALUE..Long.MAX_VALUE, decodeFromString("[${Long.MIN_VALUE},${Long.MAX_VALUE}]"))
        assertEquals(LongRange.EMPTY, decodeFromString(""))
        assertEquals(LongRange.EMPTY, decodeFromString("[a"))
        assertEquals(LongRange.EMPTY, decodeFromString("[1"))
        assertEquals(LongRange.EMPTY, decodeFromString("[1,"))
        assertEquals(LongRange.EMPTY, decodeFromString("[1,a"))
        assertEquals(LongRange.EMPTY, decodeFromString("[1,2"))
    }
}