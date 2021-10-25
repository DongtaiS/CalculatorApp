package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.calculator.databinding.ActivityMainBinding
import java.text.DecimalFormat
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    var lastNum: Double = 0.0
    var mainNum: Double = 0.0
    var numButtons = ArrayList<Button>()

    var isDecimal = false
    var decimalPlaces = 1

    enum class Operator { MULTIPLY, DIVIDE, ADD, SUBTRACT, NONE }

    var readyForNewNum = true
    var firstNum = true
    var operationComplete = false

    var currentOperator = Operator.NONE
    var operatorString = ""

    val df = DecimalFormat("#.###########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with (binding) {
            numButtons.addAll(arrayOf(btn0, btn1, btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9))
            for (i in 0 until numButtons.size) {
                numButtons[i].setOnClickListener { addNum(i) }
            }
            btnAdd.setOnClickListener{ setOperator(Operator.ADD) }
            btnSubtract.setOnClickListener{ setOperator(Operator.SUBTRACT) }
            btnMultiply.setOnClickListener{ setOperator(Operator.MULTIPLY) }
            btnDivide.setOnClickListener{ setOperator(Operator.DIVIDE) }
            btnEquals.setOnClickListener { equals() }
            btnPosNeg.setOnClickListener{
                mainNum *= -1
                updateMainText()
            }
            btnClear.setOnClickListener{ clear() }
            btnDelete.setOnClickListener{ delete() }
            btnDecimal.setOnClickListener{ decimal() }
        }
    }

    private fun decimal() {
        if (!readyForNewNum)
        {
            isDecimal = true
            updateMainText()
        }
    }

    private fun delete() {
        if (isDecimal) {
            decimalPlaces--
            mainNum -= (mainNum * 10.0.pow(decimalPlaces) % 10) * 10.0.pow(-decimalPlaces)
            if (decimalPlaces == 1) {
                isDecimal = false
            }
        } else {
            mainNum -= mainNum%10
            mainNum /= 10
        }
        updateMainText()
    }

    private fun clear() {
        mainNum = 0.0
        lastNum = 0.0
        firstNum = true
        readyForNewNum = true
        operationComplete = false
        isDecimal = false
        decimalPlaces = 1
        setOperator(Operator.NONE)
        binding.mainText.text = ""
        binding.subText.text = ""
    }

    private fun equals() {
        if (currentOperator != Operator.NONE) {
            val subText = binding.subText.text.toString() + " ${formatNum(mainNum)}"
            binding.subText.text = subText
        }
        when (currentOperator) {
            Operator.ADD -> mainNum += lastNum
            Operator.SUBTRACT -> mainNum = lastNum - mainNum
            Operator.MULTIPLY -> mainNum *= lastNum
            Operator.DIVIDE -> mainNum = lastNum / mainNum
        }
        operationComplete = true
        setOperator(Operator.NONE)
        updateMainText()
    }

    private fun setOperator(operator: Operator) {
        if (!operationComplete && !readyForNewNum && !firstNum) {
            equals()
            operationComplete = true
        }

        currentOperator = operator
        operatorString = when(operator) {
            Operator.ADD -> "+"
            Operator.DIVIDE -> "÷"
            Operator.MULTIPLY -> "x"
            Operator.SUBTRACT -> "-"
            Operator.NONE -> ""
        }
        if (!readyForNewNum) {
            readyForNewNum = true
            firstNum = false
        }
        if (operator != Operator.NONE){
            operationComplete = false
        }
        updateMainText()
    }

    private fun addNum(num: Int) {
        if (readyForNewNum) {
            lastNum = mainNum
            if (!firstNum && currentOperator != Operator.NONE)
            {
                val subText = formatNum(lastNum) + " $operatorString"
                binding.subText.text = subText
            } else if (currentOperator == Operator.NONE) {
                clear()
            }
            operatorString = ""
            mainNum = num.toDouble()
            isDecimal = false
            decimalPlaces = 1
            readyForNewNum = false
        } else {
            if (!isDecimal) {
                mainNum *= 10
                mainNum += num
            } else {
                mainNum += num * 10.0.pow(-decimalPlaces)
                decimalPlaces++
            }

        }
        updateMainText()
    }

    private fun updateMainText() {
        var str = formatNum(mainNum) + " $operatorString"
        binding.mainText.text = str
    }

    private fun formatNum(num: Double): String {
        if (num % 1.0 == 0.0) {
            if (isDecimal) {
                return num.toInt().toString() + "."
            } else {
                return num.toInt().toString()
            }
        } else {
            return String.format("%.11s", df.format(num).toString())
        }
    }
}