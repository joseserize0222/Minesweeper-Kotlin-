package minesweeper
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException
import kotlin.random.Random


class MineField(private val rows : Int, private val columns : Int, numberOfMines : Int){
    private val numberOfMines : Int
    private val table : MutableList<MutableList<Char>>
    private val mark : MutableList<MutableList<Boolean>>
    private val danger : MutableList<MutableList<Int>>
    private var markedOnes : Int = 0
    private var selectedOnes : Int = 0
    private var exploredCells : Int = 0

    init{
        this.numberOfMines = numberOfMines
        table = MutableList(rows) {MutableList (columns) {'.'} }
        mark = MutableList(rows) {MutableList (columns) {false} }
        danger = MutableList(rows) {MutableList (columns) {0} }
        var count = 0
        while(count < numberOfMines){
            val x = Random.nextInt(0,rows)
            val y = Random.nextInt(0, columns)
            if(!mark[x][y]){
                mark[x][y] = true
                count++
            }
        }
        for(i in 0 until rows){
            for(j in 0 until columns){
                if(!mark[i][j]){
                    danger[i][j] = calculateDanger(i,j)
                }
            }
        }
    }

    fun displayAux(){
        println()
        println(" |123456789|")
        println("-|---------|")
        for(i in 0 until rows){
            print("${(i+1)}|")
            for(j in 0 until columns){
                print(if(mark[i][j]) 'X' else if(danger[i][j] > 0) danger[i][j] else table[i][j])
            }
            print("|")
            println()
        }
        println("-|---------|")
    }

    fun updateAfterInit(xPos : Int, yPos : Int){
        if(mark[xPos][yPos]){
            var x = 0
            var y = 0
            while(true){
                x = Random.nextInt(0, rows)
                y = Random.nextInt(0,columns)
                if(!mark[x][y]){
                    if(table[x][y] == '*') markedOnes++
                    if(table[xPos][yPos] == '*') markedOnes--
                    mark[x][y] = true
                    mark[xPos][yPos] = false
                    danger[xPos][yPos] = calculateDanger(xPos, yPos)
                    break
                }
            }
            val movx = listOf(-1,-1,-1,0,0,1,1,1)
            val movy = listOf(-1,0,1,-1,1,-1,0,1)
            for(i in 0 until 8){
                if(x + movx[i] in 0 until rows && y + movy[i] in 0 until columns && !mark[x+movx[i]][y+movy[i]]){
                    danger[x + movx[i]][y + movy[i]] = calculateDanger(x+movx[i], y+movy[i])
                }
            }
            for(i in 0 until 8){
                if(xPos + movx[i] in 0 until rows && yPos + movy[i] in 0 until columns && !mark[xPos+movx[i]][yPos+movy[i]]){
                    danger[xPos + movx[i]][yPos + movy[i]] = calculateDanger(xPos+movx[i], yPos+movy[i])
                }
            }
        }
    }

    private fun calculateDanger(xPos : Int, yPos : Int) : Int{
        val movx = listOf(-1,-1,-1,0,0,1,1,1)
        val movy = listOf(-1,0,1,-1,1,-1,0,1)
        var result = 0
        for(i in 0 until 8){
            if((xPos + movx[i] in 0 until rows) && (yPos + movy[i] in 0 until columns)  && mark[xPos+movx[i]][yPos+movy[i]]){
                result++
            }
        }
        return result
    }

    fun markMine(xPos : Int, yPos : Int) : Boolean{
        when(table[xPos][yPos]){
            '.' ->{
                table[xPos][yPos] = '*'
                if(mark[xPos][yPos]) {
                    markedOnes++
                }
                selectedOnes++
                displayTable()
            }
            in ('1' .. '8') -> {
                println("There is a number here!")
                return false
            }
            '/'->{
                println("This is an explored cell!")
                return false
            }
            '*' -> {
                table[xPos][yPos] = '.'
                if(mark[xPos][yPos]){
                    markedOnes--
                }
                selectedOnes--
                displayTable()
            }
        }
        return (markedOnes == numberOfMines && selectedOnes == numberOfMines)
    }

    private fun expandSearch(xPos : Int, yPos : Int) : Boolean{
        if(table[xPos][yPos] == '/' || table[xPos][yPos] in ('0' .. '8') || mark[xPos][yPos]) return false
        exploredCells++
        if(table[xPos][yPos] == '*'){
            selectedOnes--
        }
        val movx = listOf(-1,-1,-1,0,0,1,1,1)
        val movy = listOf(-1,0,1,-1,1,-1,0,1)
        if(danger[xPos][yPos] == 0){
            table[xPos][yPos] = '/'
            for(i in 0 until 8){
                if(xPos + movx[i] in 0 until rows && yPos + movy[i] in 0 until columns){
                    if(mark[xPos + movx[i]][yPos + movy[i]] == false) {
                        expandSearch(xPos + movx[i], yPos + movy[i])
                    }
                }
            }
        }
        else{
            table[xPos][yPos] = danger[xPos][yPos].toString().first()
        }
        return (exploredCells == rows*columns - numberOfMines)
    }

    fun exploreCell(xPos : Int, yPos : Int) : Int{
        if(exploredCells == 0){
            updateAfterInit(xPos, yPos)
        }
        if(mark[xPos][yPos]){
            return -1
        }
        else{
            if(expandSearch(xPos,yPos)){
                return 1
            }
        }
        return 0
    }

    fun displayTable(){
        println()
        println(" |123456789|")
        println("-|---------|")
        for(i in 0 until rows){
            print("${(i+1)}|")
            for(j in 0 until columns){
                print(table[i][j])
            }
            print("|")
            println()
        }
        println("-|---------|")
    }
}

fun main() {
    println("How many mines do you want on the field?")

    val numberOfMines : Int = try{
        readln().toInt()
    }
    catch (e : NumberFormatException){
        0
    }

    val mineField = MineField(9,9, numberOfMines)
    mineField.displayTable()
    while(true){
        println("Set/unset mines marks or claim a cell as free:")
        val (x , y, op) = readln().split(" ")
        try{
            when(op){
                "mine"->{
                    if(mineField.markMine(y.toInt()-1,x.toInt()-1)){
                        println("Congratulations! You found all the mines!")
                        break
                    }
                }
                "free"->{
                    when(mineField.exploreCell(y.toInt()-1, x.toInt()-1)){
                        1 -> {
                            mineField.displayTable()
                            println("Congratulations! You found all the mines!")
                            break
                        }
                        -1->{
                            mineField.displayAux()
                            println("You stepped on a mine and failed")
                            break
                        }
                        0->{
                            mineField.displayTable()
                        }
                    }
                }
            }

        }
        catch(e  : IndexOutOfBoundsException){
            println(e.message)
        }
        catch(e : NumberFormatException){
            println(e.message)
        }
    }
}
