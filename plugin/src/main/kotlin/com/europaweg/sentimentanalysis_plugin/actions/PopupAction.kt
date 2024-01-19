package com.europaweg.sentimentanalysis_plugin.actions

import ai.onnxruntime.OrtException
import com.europaweg.sentimentanalysis_plugin.commentDetection.Detector
import com.europaweg.sentimentanalysis_plugin.data.DataCenter
import com.europaweg.sentimentanalysis_plugin.inference.RobertaInference
import com.europaweg.sentimentanalysis_plugin.inference.postProcessing
import com.europaweg.sentimentanalysis_plugin.settings.PluginState
import com.europaweg.sentimentanalysis_plugin.tokenize.TokenizerRunner
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project
import io.kinference.ort.ORTEngine
import io.kinference.ort.model.ORTModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.NonNls


class PopupAction : AnAction() {

    private val dataCenter = DataCenter.getInstance()

    override fun actionPerformed(e: AnActionEvent) {

        // check if it is kotlin file
        val fileName = e.getRequiredData(CommonDataKeys.PSI_FILE).viewProvider.virtualFile.name

        if (isKotlinFile(fileName.toString())) {
            // get the selected contents; if nothing selected, get whole file
            val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
            val selectedText = getSelectedStringContent(editor)
            if (selectedText.isNotEmpty()) {

                val state = PluginState()

                // extract comment
                val extractedComments: ArrayList<String>? = Detector.extractCommentsFromKotlinCode(selectedText, false)
                val actualComments = Detector.extractCommentsFromKotlinCode(selectedText, true)
                if (extractedComments.isNullOrEmpty()) {
                    return
                }

                // ask user to provide tokenizer
                val tokenizerRunner = getTokenizer(state)

                if (tokenizerRunner != null) {
                    // tokenizing + preprocessing
                    val tokenizedSentences = tokenizerRunner.tokenizeParagraph(extractedComments)
                    val paddedSentences = tokenizerRunner.padSequences(tokenizedSentences)
                    val inputShape = TokenizerRunner.getPaddedShape(paddedSentences)
                    val inputArray = TokenizerRunner.flatten(paddedSentences)

                    // loading model + running inference
                    val robertaModel = getRobertaModel(state)
                    if (robertaModel != null) {
                        runInferenceWithProgress(e.project, dataCenter, robertaModel, fileName, actualComments, inputArray, inputShape)
                    }
                }
            }
        } else {
            // If the file is not kotlin
            Messages.showMessageDialog(
                "Current file is not kotlin, cannot run sentiment analysis.",
                "Information",
                Messages.getInformationIcon()
            )
        }
    }


    /**
     * Runs Roberta inference with progress tracking.
     */
    fun runInferenceWithProgress(project: Project?,
                                 dataCenter: DataCenter,
                                 robertaModel: ORTModel,
                                 fileName: String,
                                 actualComments: ArrayList<String>,
                                 inputArray: LongArray,
                                 inputShape: LongArray) {

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Running Roberta Inference", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = false
                indicator.fraction = 0.0
                runBlocking {
                    // Actual function call
                    val roberaInference = RobertaInference(robertaModel)
                    val outputValues = roberaInference.predict(inputArray, inputShape)
                    val predictions = postProcessing(outputValues)
                    if (predictions != null) {
                        dataCenter.addToMap(fileName, actualComments, predictions)
                        dataCenter.addToTable(fileName, actualComments, predictions)
                    }
                }
                // Update progress
                indicator.fraction = 1.0
            }
        })

        return
    }

    /**
     * Retrieves the content of the selected text in the given [editor].
     * If no text is selected, the entire content of the editor is returned.
     */
    private fun getSelectedStringContent(editor: Editor): String {
        val selectionModel: SelectionModel = editor.selectionModel
        val selectedText: String? = selectionModel.selectedText

        if (selectedText == null) {
            val document = editor.document
            val text = document.text
            val startOffset = 0
            val endOffset = text.length
            val selectedString = text.substring(startOffset, endOffset)

            return selectedString

        } else {return selectedText}
    }

    /**
     * Retrieves a [TokenizerRunner] based on the provided [state].
     * The function attempts to create a [TokenizerRunner] with the path obtained from [getTokenizerPath].
     * If unsuccessful due to incorrect or missing resources, the user is prompted to re-select the directory.
     * If the user chooses to re-select, the [TokenizerRunner] is set to null, and the [state] is updated accordingly.
     * If the user chooses not to re-select or closes the dialog, a message is displayed, and the function returns null.
     */
    private fun getTokenizer(state: PluginState?): TokenizerRunner? {
        var tokenizerRunner: TokenizerRunner?
        var isSuccess: Boolean
        do {
            try {
                val vocabularyBaseDirPath = getTokenizerPath(state)
                tokenizerRunner = TokenizerRunner(vocabularyBaseDirPath)
                isSuccess = true
            } catch (e: Exception) {
                val userChoice = Messages.showYesNoDialog(
                    "The  tokenizer resources is either incorrect, missing, " +
                            "or not provided. Would you like to re-select the directory " +
                            "to enable the Sentiment Analysis plugin?",
                    "Confirmation",
                    Messages.getQuestionIcon()
                )
                if (userChoice == Messages.YES) {
                    // User clicked "Yes"
                    tokenizerRunner = null
                    if (state != null) {
                        state.tokenizerPath = "empty"
                    }
                    isSuccess = false
                } else {
                    // User clicked "No" or closed the dialog, handle accordingly
                    Messages.showMessageDialog(
                        "The Sentiment Analysis plugin is disabled due to " +
                                "missing or inaccessible tokenizer resources. To fix," +
                                " close the dialog, right-click, and reload resources, " +
                                "ensuring the folder contains base_vocabulary.json, " +
                                "vocabulary.json, and merges.txt. Download the resources from:" +
                                "\n\n https://github.com/purecloudlabs/roberta-toke" +
                                "nizer/tree/main/src/test/resources/test-vocabularies",
                        "Information",
                        Messages.getInformationIcon()
                    )
                    tokenizerRunner = null
                    isSuccess = true
                }
            }
        } while (!isSuccess)
        return tokenizerRunner
    }

    private fun getTokenizerPath(state: PluginState?): String? {
        var tokenizerPath: String?
        if (state == null || state.tokenizerPath == "empty") {
            tokenizerPath = askUserForTokenizerPath()
            if (tokenizerPath != null && state != null) {
                state.tokenizerPath = tokenizerPath
            }
        } else {
            tokenizerPath = state.tokenizerPath
        }
        return tokenizerPath
    }

    private fun askUserForTokenizerPath(): @NonNls String? {
        val fileChooserDescriptor = FileChooserDescriptor(
            false,
            true,
            false,
            false,
            false,
            false
        )
        fileChooserDescriptor.title = "Tokenizer Chooser (Sentiment Analysis Plugin)"
        fileChooserDescriptor.description = "Please choose the Tokenizer folder."
        val selectedFile = FileChooser.chooseFile(fileChooserDescriptor, null, null)

        return selectedFile?.path
    }

    /**
     * Retrieves the Roberta model based on the provided [state].
     * The function attempts to create a [ortModel] instance.
     * If unsuccessful due to incorrect or missing model files, the user is prompted to re-select the directory.
     * If the user chooses to re-select, the [ortModel] are set to null, and the [state] is updated accordingly.
     * If the user chooses not to re-select or closes the dialog, a message is displayed, and the function returns null.
     */
    private fun getRobertaModel(state: PluginState?): ORTModel? {
        var ortModel: ORTModel? = null
        var isSuccess: Boolean

        do {
            try {
                val modelPath = getModelPath(state)
                if (modelPath != null && modelPath != "empty") {
                    runBlocking { ortModel = ORTEngine.loadModel(modelPath) }
                    isSuccess = true
                } else {
                    if (state != null) {
                        state.modelPath = "empty"
                    }
                    isSuccess = true
                }
            } catch (e: OrtException) {
                isSuccess = false
                val userChoice = Messages.showYesNoDialog(
                        "The Roberta model file is either incorrect, missing, " +
                                "or not provided. Would you like to re-select the directory " +
                                "to enable the Sentiment Analysis plugin?",
                        "Confirmation",
                    Messages.getQuestionIcon()
                )
                if (userChoice == Messages.YES) {
                    // User clicked "Yes"
                    if (state != null) {
                        state.modelPath = "empty"
                    }
                    isSuccess = false
                } else {
                    // User clicked "No" or closed the dialog, handle accordingly
                    Messages.showMessageDialog(
                        "The Sentiment Analysis plugin is currently disabled due " +
                                "to missing or inaccessible Roberta model directories. To " +
                                "resolve, close the dialog, right-click, and reload resources. " +
                                "Download the required model from: \n\n https://git" +
                                "hub.com/onnx/models/tree/main/validated/text/machine_comprehen" +
                                "sion/roberta",
                            "Information",
                        Messages.getInformationIcon()
                    )
                    isSuccess = true
                }
            }
        } while (!isSuccess)
        return ortModel
    }

    private fun getModelPath(state: PluginState?): String? {
        var modelPath: String?
        if (state == null || state.modelPath == "empty") {
            modelPath = askUserForModelPath()
            if (modelPath != null && state != null) {
                state.modelPath = modelPath
            }
        } else {
            modelPath = state.modelPath
        }
        return modelPath
    }

    private fun askUserForModelPath(): @NonNls String? {
        val fileChooserDescriptor = FileChooserDescriptor(
            true,
            false,
            false,
            false,
            false,
            false
        )
        fileChooserDescriptor.title = "Model Chooser (Sentiment Analysis Plugin)"
        fileChooserDescriptor.description = "Please choose the Sentiment Analysis " +
                "model ONNX file."
        val selectedFile = FileChooser.chooseFile(fileChooserDescriptor, null, null)

        return selectedFile?.path
    }
}