package com.example.vikvakt

object WindDirection {
    fun windStartPositions(angle: Double): List<Int> {
        val posList = edgePositions()
        return getEdgeValuesAroundAngle(angle, posList)
    }

    fun edgePositions(): List<Int> {
        val size = 256
        val posList: MutableList<Int> = ArrayList()
        for (i in 0 until size) {
            posList.add(i)
        }
        run {
            var i = size
            while (i < size * (size - 1)) {
                posList.add(i + size - 1)
                i += size
            }
        }
        for (i in size * (size - 1) + size - 1 downTo size * (size - 1)) {
            posList.add(i)
        }
        var i = size * (size - 1)
        while (i >= 0) {
            posList.add(i)
            i -= size
        }
        return posList
    }

    fun getEdgeValuesAroundAngle(angle: Double, posList: List<Int>): List<Int> {
        val totalPositions = posList.size // Totalt antal kantvärden, t.ex. 1020
        val centerIndex = Math.floor(angle / 360.0 * totalPositions).toInt()

        // Skapa en lista för att lagra de 255 värdena
        val result: MutableList<Int> = ArrayList()

        // Lägg till 127 föregående värden
        for (i in -127..127) {
            // Wrapa runt med modulo så att index alltid ligger mellan 0 och totalPositions-1
            val wrappedIndex = (centerIndex + i + totalPositions) % totalPositions
            result.add(posList[wrappedIndex])
        }
        return result
    }
}