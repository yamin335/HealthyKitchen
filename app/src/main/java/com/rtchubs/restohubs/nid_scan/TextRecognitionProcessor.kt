package com.rtchubs.restohubs.nid_scan

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.rtchubs.restohubs.models.NIDBackModel
import com.rtchubs.restohubs.models.NIDFrontModel

/** Processor for the text detector demo.  */

interface TextRecognitionResultPublisher {
    fun onFrontNID(result: NIDFrontModel)
    fun onBackNID(result: NIDBackModel)
}
class TextRecognitionProcessor(context: Context, publisher: TextRecognitionResultPublisher, imageType: ImageType) : VisionProcessorBase<Text>(context) {
    private val textRecognizer: TextRecognizer = TextRecognition.getClient()
    private val nidFrontData = NIDFrontModel()
    private val nidBackData = NIDBackModel()
    private val resultPublisher = publisher
    private var nidSide = imageType

    override fun stop() {
        super.stop()
        textRecognizer.close()
    }

    override fun detectInImage(image: InputImage): Task<Text> {
        return textRecognizer.process(image)
    }

    override fun onSuccess(text: Text, graphicOverlay: GraphicOverlay) {
        logExtrasForTesting(text)
        graphicOverlay.add(TextGraphic(graphicOverlay, text))
    }

    override fun onFailure(e: Exception) {
        Log.w("TAG", "Text detection failed.$e")
    }

    private fun logExtrasForTesting(text: Text?) {
        if (text != null && text.textBlocks.size > 0) {
            val blocks = text.textBlocks
            if (nidSide == ImageType.NID_FRONT) {
                for (i in blocks.indices) {
                    val textValue = blocks[i].text.trim()
                    if (nidFrontData.nidNo.isBlank() && textValue.length == 12 && "[0-9 ]+".toRegex().matches(textValue)) {
                        nidFrontData.isValidNID = true
                        nidFrontData.nidNo = textValue
                    }
//                    if (!nidFrontData.isValidNID && blocks[i].text.contains("D Card") || blocks[i].text.contains("O Card") || blocks[i].text.contains("National ID Card")) {
//                        nidFrontData.isValidNID = true
//                    }
//
//                    if (nidFrontData.nidNo.isBlank()) {
//                        if ((blocks[i].text.trim() == "NID No." || blocks[i].text.trim() == "NID No" || blocks[i].text.trim() == "NIO No." || blocks[i].text.trim() == "NIO No") && blocks.size > i + 1 && blocks[i + 1].text.trim().length == 12) {
//                            nidFrontData.nidNo = blocks[i + 1].text.trim()
//                        } else if (blocks[i].text.contains("NID No.") || blocks[i].text.contains("NIO No.") || blocks[i].text.contains("NID No") || blocks[i].text.contains("NIO No.")) {
//                            val textArray = blocks[i].text.split(".")
//                            if (textArray.size == 2 && textArray[1].trim().length == 12) {
//                                nidFrontData.nidNo = textArray[1].trim()
//                            }
//                        }
//                    }

                    if (nidFrontData.isValidNID && nidFrontData.nidNo.isNotBlank()) {
                        nidSide = ImageType.NID_BACK
                        resultPublisher.onFrontNID(nidFrontData)
                        break
                    }

//                    if (nidFrontData.name.isBlank() && blocks[i].text.trim() == "Name" && blocks.size > i + 1) {
//                        nidFrontData.name = blocks[i + 1].text.trim()
//                    }
//
//                    if (nidFrontData.birthDate.isBlank() && blocks[i].text.contains("Birth")) {
//                        val textArray = blocks[i].text.split("Birth")
//                        if (textArray.size == 2 && textArray[1].trim().length >= 11) {
//                            nidFrontData.birthDate = textArray[1].trim()
//                        }
//                    }
                }
            } else if (nidSide == ImageType.NID_BACK) {
                for (i in blocks.indices) {
                    val textValue = blocks[i].text.trim()
//                    if (nidBackData.bloodGroup.isBlank() && textValue.contains("Group")) {
//                        val textArray = textValue.split("Group")
//                        if (textArray.size == 2) {
//                            nidBackData.bloodGroup = textArray[1].removePrefix(":").trim()
//                        }
//                    }

                    if (nidBackData.birthPlace.isBlank() && textValue.contains("Birth")) {
                        val textArray = textValue.split("ce of Birth")
                        if (textArray.size == 2) {
                            nidBackData.birthPlace = textArray[1].removePrefix(":").trim()
                        }
                    }

//                    if (nidBackData.nidIssueDate.isBlank() && textValue.contains("Date")) {
//                        val textArray = textValue.split("Date")
//                        if (textArray.size == 2) {
//                            nidBackData.nidIssueDate = textArray[1].removePrefix(":").trim()
//                        }
//                    }

                    if (nidBackData.birthPlace.isNotBlank()){
                        nidBackData.isValidNID = true
                        nidSide = ImageType.NONE
                        resultPublisher.onBackNID(nidBackData)
                        break
                    }
                }
            }
        }
    }
}
