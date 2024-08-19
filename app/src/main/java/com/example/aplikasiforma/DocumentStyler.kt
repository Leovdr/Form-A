package com.example.aplikasiforma

import org.apache.poi.xwpf.usermodel.LineSpacingRule
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.apache.poi.xwpf.usermodel.XWPFRun
import org.apache.poi.xwpf.usermodel.XWPFNumbering
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat
import java.math.BigInteger

class DocumentStyler(private val document: XWPFDocument) {

    // Method to create and style title paragraphs
    fun createTitleParagraph(text: String): XWPFParagraph {
        val paragraph = document.createParagraph()
        paragraph.setSpacingBetween(1.0)
        paragraph.alignment = ParagraphAlignment.CENTER
        paragraph.spacingLineRule = LineSpacingRule.AUTO
        val run = paragraph.createRun()
        run.isBold = true
        run.fontSize = 12
        run.fontFamily = "Arial"  // Set font family
        run.setText(text)
        return paragraph
    }

    // Method to create and style general paragraphs
    fun createStyledParagraph(
        text: String,
        isBold: Boolean = false,
        fontSize: Int = 12,
        fontFamily: String = "Arial",
        alignment: ParagraphAlignment = ParagraphAlignment.LEFT
    ): XWPFParagraph {
        val paragraph = document.createParagraph()
        paragraph.spacingAfter = 200
        paragraph.alignment = alignment
        val run = paragraph.createRun()
        run.isBold = isBold
        run.fontSize = fontSize
        run.fontFamily = fontFamily
        run.setText(text)
        return paragraph
    }

    // Method to create numbering style
    fun createNumbering(numbering: XWPFNumbering, isBullet: Boolean = false, isAlphabetic: Boolean = false): BigInteger {
        val abstractNum = CTAbstractNum.Factory.newInstance()
        abstractNum.abstractNumId = BigInteger.ONE

        val ctLvl = abstractNum.addNewLvl()
        ctLvl.ilvl = BigInteger.ZERO

        when {
            isBullet -> {
                ctLvl.addNewNumFmt().`val` = STNumberFormat.BULLET
                ctLvl.addNewLvlText().`val` = "•"
            }
            isAlphabetic -> {
                ctLvl.addNewNumFmt().`val` = STNumberFormat.LOWER_LETTER // Huruf kecil
                ctLvl.addNewLvlText().`val` = "%1."
            }
            else -> {
                ctLvl.addNewNumFmt().`val` = STNumberFormat.DECIMAL
                ctLvl.addNewLvlText().`val` = "%1."
            }
        }

        ctLvl.addNewStart().`val` = BigInteger.ONE

        val abstractNumId = numbering.addAbstractNum(XWPFAbstractNum(abstractNum))
        return numbering.addNum(abstractNumId)
    }

    // Method to create numbered paragraph with indent
    fun createNumberedParagraphWithIndent(text: String, numId: BigInteger): XWPFParagraph {
        val paragraph = document.createParagraph()
        paragraph.setNumID(numId)
        paragraph.indentationLeft = 720  // Atur indentasi kiri (misalnya, 720 twips = 0.5 inch)
        paragraph.indentationHanging = 360  // Atur indentasi untuk baris kedua dan seterusnya

        val run = paragraph.createRun()
        run.setText(text)
        return paragraph
    }

    // Method to style and justify text in a paragraph (can be used in table cells too)
    fun styleParagraph(
        paragraph: XWPFParagraph,
        text: String,
        isBold: Boolean = false,
        fontSize: Int = 12,
        fontFamily: String = "Arial",
        alignment: ParagraphAlignment = ParagraphAlignment.LEFT
    ) {
        paragraph.alignment = alignment
        val run = paragraph.createRun()
        run.isBold = isBold
        run.fontSize = fontSize
        run.fontFamily = fontFamily
        run.setText(text)
    }

    // Method to style table cell
    fun styleTableCell(
        cell: XWPFTableCell,
        text: String,
        isBold: Boolean = false,
        fontSize: Int = 12,
        fontFamily: String = "Arial",
        alignment: ParagraphAlignment = ParagraphAlignment.LEFT
    ) {
        val paragraph = cell.paragraphs[0] // Use the first paragraph in the cell
        styleParagraph(paragraph, text, isBold, fontSize, fontFamily, alignment)
    }
}
